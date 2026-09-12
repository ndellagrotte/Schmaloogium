// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.baseline.BaselineManifest;
import com.schmaloogium.conformance.baseline.BaselinePromoter;
import com.schmaloogium.conformance.baseline.ContactSheet;
import com.schmaloogium.conformance.baseline.MachineClass;
import com.schmaloogium.conformance.diff.IgnoreMask;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.fixture.FixtureCache;
import com.schmaloogium.conformance.fixture.FixtureResolver;
import com.schmaloogium.conformance.fixture.PackFixture;
import com.schmaloogium.conformance.fixture.PackFixtureRegistry;
import com.schmaloogium.conformance.run.RunId;
import com.schmaloogium.conformance.scene.CapturePlan;
import com.schmaloogium.conformance.scene.CapturePlanWriter;
import com.schmaloogium.conformance.scene.ClockProfile;
import com.schmaloogium.conformance.scene.SceneParser;
import com.schmaloogium.conformance.scene.SceneSpec;
import com.schmaloogium.conformance.scene.SceneValidator;
import com.schmaloogium.conformance.tier.T0Evaluator;
import com.schmaloogium.conformance.tier.T1Evaluator;
import com.schmaloogium.conformance.tier.TierOutcome;
import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;
import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * The external-process driver of §4.5.1: resolve fixture → scene → inventory → world →
 * write the plan → launch the client → validate the temporary manifest against the plan →
 * publish atomically → evaluate → report. Every failure before publication yields a
 * runner-authored {@code FAILED}/{@code SKIPPED} manifest, never an absent run. One client
 * per machine, enforced by a lock file in the runs tree.
 */
public final class CaptureRunner {

    public static final String MINECRAFT_VERSION = "1.12.2";
    public static final long DEFAULT_HANG_CEILING_MILLIS = 20_000L;

    /** Everything a runner invocation shares. */
    public record Context(
            Path repoRoot,
            FixtureCache cache,
            PackFixtureRegistry registry,
            ClientLaunchSpec launch,
            String inventoryText,
            Map<String, ClockProfile> clocks,
            Map<String, TolerancePolicy> profiles,
            GLCapabilityProfile planningProfile,
            Duration timeout,
            String cleanroomVersion) {
    }

    public record Outcome(Path runDir, RunManifest manifest, String manifestSha256, TierOutcome t0,
            Optional<T1Evaluator.T1Result> t1, List<String> notes) {
    }

    private final Context ctx;

    public CaptureRunner(Context ctx) {
        this.ctx = ctx;
    }

    public Path scenesDir() {
        return ctx.repoRoot().resolve("conformance").resolve("scenes");
    }

    public SceneSpec loadScene(String sceneId) throws IOException {
        Path file = scenesDir().resolve(sceneId + ".scene");
        SceneSpec scene = SceneParser.parse(Files.readString(file, StandardCharsets.UTF_8));
        SceneValidator.validate(scene, sceneId);
        return scene;
    }

    public String sceneHash(String sceneId) throws IOException {
        return Hashes.sha256Hex(Files.readAllBytes(scenesDir().resolve(sceneId + ".scene")));
    }

    // ------------------------------------------------------------------
    // world generation (§4.5.5)
    // ------------------------------------------------------------------

    public WorldCache.Entry ensureWorld(SceneSpec scene, CapturePlanWriter.Environment env, Log log)
            throws IOException, InterruptedException {
        WorldCache worlds = new WorldCache(ctx.cache());
        WorldCache.Descriptor descriptor = WorldCache.descriptor(scene.world(), MINECRAFT_VERSION,
            env.externalModSetSha256());
        Optional<WorldCache.Entry> hit = worlds.lookup(descriptor);
        if (hit.isPresent()) {
            log.info("world cache hit " + descriptor.sha256());
            return hit.get();
        }
        log.info("world cache miss " + descriptor.sha256() + "; generating through the client");
        Path genDir = ctx.cache().runs().resolve("gen-" + descriptor.sha256().substring(0, 16));
        WorldCache.deleteTree(genDir);
        Files.createDirectories(genDir);
        Path descriptorFile = genDir.resolve("generation.worldgen");
        Files.writeString(descriptorFile, descriptor.document().render(), StandardCharsets.UTF_8);
        String folder = "conf-gen-" + descriptor.sha256().substring(0, 16);
        Path clientSave = ctx.launch().workingDir().resolve("saves").resolve(folder);
        WorldCache.deleteTree(clientSave);
        int exit = launchClient(genDir, List.of(
            "-Dschmaloogium.conformance.generate=" + descriptorFile.toAbsolutePath(),
            "-Dschmaloogium.conformance.generateFolder=" + folder,
            "-Dschmaloogium.conformance.out=" + genDir.toAbsolutePath()), log);
        Path done = genDir.resolve("generated.txt");
        if (exit != 0 || !Files.isRegularFile(done) || !Files.isDirectory(clientSave)) {
            throw new IOException("world generation failed (exit " + exit + "); see " + genDir);
        }
        WorldCache.Entry entry = worlds.publish(descriptor, clientSave);
        WorldCache.deleteTree(clientSave);
        log.info("world published " + entry.dir() + " save hash " + entry.worldSha256());
        return entry;
    }

    // ------------------------------------------------------------------
    // capture runs
    // ------------------------------------------------------------------

    public Outcome capture(RunId runKind, String packRef, String sceneId, String clockName,
            String profileName, boolean allowUncalibrated, Log log) throws IOException, InterruptedException {
        PackFixture fixture = ctx.registry().require(packRef);
        FixtureResolver resolver = new FixtureResolver(ctx.cache());
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss", Locale.ROOT));
        String runId = runKind.wire() + "-" + sceneId + "-" + stamp;
        Path runDir = ctx.cache().runs().resolve(runId);
        Files.createDirectories(runDir);
        List<String> notes = new ArrayList<>();
        SceneSpec scene = loadScene(sceneId);
        String sceneHash = sceneHash(sceneId);
        ClockProfile clock = ctx.clocks().get(clockName);
        if (clock == null) {
            throw new IllegalArgumentException("unknown clock profile " + clockName);
        }
        CapturePlanWriter.Environment env = LaunchInventory.authenticate(ctx.inventoryText(), ctx.launch(),
            MINECRAFT_VERSION);
        FixtureResolver.Outcome resolved = resolver.resolve(fixture);
        if (resolved instanceof FixtureResolver.Skipped skipped) {
            // Skipped(fixture-absent) with the remedy (§4.13 rule 2); a plan still records the attempt.
            CapturePlanWriter.FixtureFacts facts = new CapturePlanWriter.FixtureFacts(fixture.id(),
                fixture.version(), fixture.mode().name(), fixture.sha512(), fixture.licence());
            WorldCache.Descriptor descriptor = WorldCache.descriptor(scene.world(), MINECRAFT_VERSION,
                env.externalModSetSha256());
            CapturePlan plan = CapturePlanWriter.write(runId, scene, sceneHash, scene.captures(), facts,
                Map.of(), PackFacts.engineOptionDefaults(), clock, env,
                new CapturePlanWriter.WorldFacts("saves/" + runId, descriptor.sha256(), zero64()));
            Files.writeString(runDir.resolve("capture.plan"), plan.render(), StandardCharsets.UTF_8);
            RunManifest failure = FailureManifests.fromPlan(plan, "SKIPPED", skipped.reason() + ": "
                + skipped.remedy(), "", DEFAULT_HANG_CEILING_MILLIS, false, os(), jvm(), ctx.cleanroomVersion());
            String sha = publish(runDir, failure);
            log.warn("SKIPPED(" + skipped.reason() + ") — " + skipped.remedy());
            return new Outcome(runDir, failure, sha, TierOutcome.NOT_ATTEMPTED, Optional.empty(),
                List.of(skipped.remedy()));
        }
        FixtureResolver.Resolved archive = (FixtureResolver.Resolved) resolved;
        PackFacts packFacts = PackFacts.inspect(archive.archive(), ctx.planningProfile());
        int floor = SceneValidator.warmupFloor(packFacts.largestHalflifeTicks(), clock.ticksPerFrame());
        SceneValidator.validate(scene, sceneId, floor, null);
        WorldCache.Entry world = ensureWorld(scene, env, log);
        // §4.5.1 step 2: copy the immutable save into the client's saves tree and re-hash it.
        Path clientSave = ctx.launch().workingDir().resolve("saves").resolve(runId);
        WorldCache.deleteTree(clientSave);
        WorldCache.copyTree(world.save(), clientSave);
        String copiedHash = Hashes.framedTreeSha256(clientSave);
        if (!copiedHash.equals(world.worldSha256())) {
            throw new IOException("copied save hash " + copiedHash + " != receipt " + world.worldSha256());
        }
        Files.writeString(runDir.resolve("generation.worldgen"), world.descriptor().document().render(),
            StandardCharsets.UTF_8);
        Files.copy(world.dir().resolve("receipt.world"), runDir.resolve("world.receipt"),
            StandardCopyOption.REPLACE_EXISTING);
        CapturePlanWriter.FixtureFacts facts = new CapturePlanWriter.FixtureFacts(fixture.id(),
            fixture.version(), fixture.mode().name(), archive.sha512(), fixture.licence());
        CapturePlan plan = CapturePlanWriter.write(runId, scene, sceneHash, scene.captures(), facts,
            packFacts.packOptionDefaults(), PackFacts.engineOptionDefaults(), clock, env,
            new CapturePlanWriter.WorldFacts("saves/" + runId, world.descriptor().sha256(),
                world.worldSha256()));
        Path planFile = runDir.resolve("capture.plan");
        Files.writeString(planFile, plan.render(), StandardCharsets.UTF_8);
        // install the pack archive where the client discovers packs
        Path shaderpacks = ctx.launch().workingDir().resolve("shaderpacks");
        Files.createDirectories(shaderpacks);
        Path installed = shaderpacks.resolve(fixture.archiveName());
        Files.copy(archive.archive(), installed, StandardCopyOption.REPLACE_EXISTING);
        log.info("plan " + planFile + " hash " + plan.planHash() + " steps " + plan.totalSteps());
        RunManifest manifest;
        boolean timedOut = false;
        int exit;
        try {
            exit = launchClient(runDir, List.of(
                "-Dschmaloogium.conformance.plan=" + planFile.toAbsolutePath(),
                "-Dschmaloogium.conformance.out=" + runDir.toAbsolutePath(),
                "-Dschmaloogium.conformance.hangCeilingMillis=" + DEFAULT_HANG_CEILING_MILLIS,
                "-Dschmaloogium.conformance.packArchive=" + fixture.archiveName(),
                "-Dschmaloogium.debug.recordGL=true"), log);
        } catch (TimeoutException t) {
            timedOut = true;
            exit = -1;
        } finally {
            WorldCache.deleteTree(clientSave);
        }
        Path temp = runDir.resolve("manifest.tmp");
        String failure = null;
        if (timedOut) {
            failure = "client exceeded the hard wall-clock timeout of " + ctx.timeout();
        } else if (!Files.isRegularFile(temp)) {
            failure = "client exited " + exit + " without writing manifest.tmp";
        }
        if (failure == null) {
            try {
                manifest = RunManifestReader.parse(Files.readString(temp, StandardCharsets.UTF_8));
                List<String> problems = ManifestValidator.validate(manifest, plan, runDir);
                if (!problems.isEmpty()) {
                    failure = "agent manifest rejected: " + String.join("; ", problems);
                    manifest = null;
                }
            } catch (IllegalArgumentException malformed) {
                failure = "agent manifest malformed: " + malformed.getMessage();
                manifest = null;
            }
        } else {
            manifest = null;
        }
        if (manifest == null) {
            manifest = FailureManifests.fromPlan(plan, "FAILED", failure, "", DEFAULT_HANG_CEILING_MILLIS,
                timedOut, os(), jvm(), ctx.cleanroomVersion());
            notes.add(failure);
            log.warn("FAILED: " + failure);
        }
        String sha = publish(runDir, manifest);
        T0Evaluator.T0Result t0 = T0Evaluator.evaluate(manifest);
        log.info("T0 " + t0.outcome() + " " + t0.verdicts().stream().filter(v -> !v.passed())
            .map(v -> v.predicate() + " " + v.failures()).toList());
        if (runKind.writesContactSheet() && manifest.familyCount("images") > 0) {
            log.info("contact sheet " + ContactSheet.write(runDir, manifest));
        }
        Optional<T1Evaluator.T1Result> t1 = Optional.empty();
        if (runKind.evaluatesT1()) {
            t1 = Optional.of(evaluateT1(runDir, manifest, sha, profileName, allowUncalibrated, log));
        }
        writeReport(runDir, manifest, t0, t1, notes);
        return new Outcome(runDir, manifest, sha, t0.outcome(), t1, notes);
    }

    /** Re-validates an existing {@code manifest.tmp} against the retained plan and republishes
     *  (for runner-side fixes; the client evidence is untouched). */
    public Outcome republish(Path runDir, RunId runKind, String profileName, boolean allowUncalibrated, Log log)
            throws IOException {
        CapturePlan plan = com.schmaloogium.conformance.scene.CapturePlanReader.parse(
            Files.readString(runDir.resolve("capture.plan"), StandardCharsets.UTF_8));
        Path temp = runDir.resolve("manifest.tmp");
        List<String> notes = new ArrayList<>();
        RunManifest manifest = null;
        String failure = null;
        if (!Files.isRegularFile(temp)) {
            failure = "no manifest.tmp in " + runDir;
        } else {
            try {
                manifest = RunManifestReader.parse(Files.readString(temp, StandardCharsets.UTF_8));
                List<String> problems = ManifestValidator.validate(manifest, plan, runDir);
                if (!problems.isEmpty()) {
                    failure = "agent manifest rejected: " + String.join("; ", problems);
                    manifest = null;
                }
            } catch (IllegalArgumentException malformed) {
                failure = "agent manifest malformed: " + malformed.getMessage();
            }
        }
        if (manifest == null) {
            manifest = FailureManifests.fromPlan(plan, "FAILED", failure, "", DEFAULT_HANG_CEILING_MILLIS, false,
                os(), jvm(), ctx.cleanroomVersion());
            notes.add(failure);
            log.warn("FAILED: " + failure);
        }
        String sha = publish(runDir, manifest);
        T0Evaluator.T0Result t0 = T0Evaluator.evaluate(manifest);
        log.info("T0 " + t0.outcome() + " " + t0.verdicts().stream().filter(v -> !v.passed())
            .map(v -> v.predicate() + " " + v.failures()).toList());
        if (runKind.writesContactSheet() && manifest.familyCount("images") > 0) {
            log.info("contact sheet " + ContactSheet.write(runDir, manifest));
        }
        Optional<T1Evaluator.T1Result> t1 = Optional.empty();
        if (runKind.evaluatesT1()) {
            t1 = Optional.of(evaluateT1(runDir, manifest, sha, profileName, allowUncalibrated, log));
        }
        writeReport(runDir, manifest, t0, t1, notes);
        return new Outcome(runDir, manifest, sha, t0.outcome(), t1, notes);
    }

    public T1Evaluator.T1Result evaluateT1(Path runDir, RunManifest manifest, String manifestSha,
            String profileName, boolean allowUncalibrated, Log log) throws IOException {
        String packId = manifest.text("pack.id");
        String packVersion = manifest.text("pack.version");
        String sceneId = manifest.token("run.sceneId");
        Path baselineFile = BaselinePromoter.manifestPath(ctx.repoRoot(), packId, packVersion, sceneId);
        Optional<BaselineManifest> baseline = Optional.empty();
        String baselineSha = zero64();
        if (Files.isRegularFile(baselineFile)) {
            String text = Files.readString(baselineFile, StandardCharsets.UTF_8);
            baseline = Optional.of(BaselineManifest.parse(text));
            baselineSha = Hashes.sha256HexOf(text);
            Path inputs = runDir.resolve("inputs");
            Files.createDirectories(inputs);
            Files.writeString(inputs.resolve(baselineSha + ".baseline"), text, StandardCharsets.UTF_8);
        }
        String machine = manifest.bool("gl.available")
            ? MachineClass.of(manifest.text("gl.profile_text"), manifest.token("environment.os"),
                System.getProperty("os.arch"))
            : "unknown";
        final Optional<BaselineManifest> baselineRef = baseline;
        T1Evaluator.Inputs in = new T1Evaluator.Inputs(manifest, manifestSha, machine, baseline, baselineSha,
            ctx.profiles(), profileName, captureId -> mask(sceneId, captureId),
            rel -> {
                try {
                    return PngRaster.read(runDir.resolve(rel));
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            },
            record -> {
                try {
                    return PngRaster.read(BaselinePromoter.rasterPath(ctx.cache(), packId, packVersion,
                        sceneId, record));
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            }, allowUncalibrated);
        T1Evaluator.T1Result result = T1Evaluator.evaluate(in);
        Path comparisons = runDir.resolve("comparisons");
        Files.createDirectories(comparisons);
        for (T1Evaluator.SampleOutcome s : result.samples()) {
            String text = s.comparison().render();
            Files.writeString(comparisons.resolve(Hashes.sha256HexOf(text) + ".comparison"), text,
                StandardCharsets.UTF_8);
            log.info("T1 " + s.captureKind() + "/" + s.captureId() + "/" + s.sampleOrdinal() + " "
                + s.outcome() + ": " + s.reason());
        }
        log.info("T1 " + result.outcome() + (baselineRef.isEmpty() ? " (no baseline manifest)" : ""));
        return result;
    }

    public IgnoreMask mask(String sceneId, String captureId) {
        Path sidecar = scenesDir().resolve(sceneId + "." + captureId + ".mask");
        if (!Files.isRegularFile(sidecar)) {
            return IgnoreMask.NONE;
        }
        try {
            return IgnoreMask.parse(Files.readString(sidecar, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    public Path approve(Path runDir, String approver, String profileName, Log log) throws IOException {
        RunManifest manifest = RunManifestReader.parse(Files.readString(runDir.resolve("manifest.manifest"),
            StandardCharsets.UTF_8));
        String sha = Hashes.sha256HexOfFile(runDir.resolve("manifest.manifest"));
        String machine = MachineClass.of(manifest.text("gl.profile_text"), manifest.token("environment.os"),
            System.getProperty("os.arch"));
        T0Evaluator.T0Result t0 = T0Evaluator.evaluate(manifest);
        if (t0.outcome() != TierOutcome.PASS) {
            throw new IllegalStateException("refusing to approve a run that fails T0: "
                + t0.verdicts().stream().filter(v -> !v.passed()).map(v -> v.predicate() + " "
                    + v.failures()).toList());
        }
        Path written = BaselinePromoter.approve(ctx.cache(), ctx.repoRoot(), runDir, manifest, sha, machine,
            profileName, approver, LocalDate.now().toString());
        log.info("approved by " + approver + " → " + written + " (commit this manifest; rasters stay in "
            + ctx.cache().baselines() + ")");
        return written;
    }

    // ------------------------------------------------------------------

    private String publish(Path runDir, RunManifest manifest) throws IOException {
        String text = RunManifestWriter.render(manifest);
        Path temp = runDir.resolve("manifest.publish.part");
        Files.writeString(temp, text, StandardCharsets.UTF_8);
        Files.move(temp, runDir.resolve("manifest.manifest"), StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING);
        return Hashes.sha256HexOf(text);
    }

    private void writeReport(Path runDir, RunManifest m, T0Evaluator.T0Result t0,
            Optional<T1Evaluator.T1Result> t1, List<String> notes) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(m.token("run.id")).append("\n\n");
        sb.append("- pack: ").append(m.text("pack.id")).append('@').append(m.text("pack.version")).append('\n');
        sb.append("- scene: ").append(m.token("run.sceneId")).append('\n');
        sb.append("- exit: ").append(m.token("run.exitStatus"));
        if (!m.text("run.failureReason").isEmpty()) {
            sb.append(" — ").append(m.text("run.failureReason"));
        }
        sb.append('\n');
        sb.append("- T0: ").append(t0.outcome()).append('\n');
        for (T0Evaluator.Verdict v : t0.verdicts()) {
            sb.append("  - ").append(v.predicate()).append(": ").append(v.passed() ? "pass" : v.failures()).append('\n');
        }
        sb.append("- programs: ");
        Map<String, Integer> counts = new java.util.TreeMap<>();
        for (RunManifest.Row row : m.family("programs")) {
            counts.merge(row.token("status"), 1, Integer::sum);
        }
        sb.append(counts).append('\n');
        sb.append("- images: ").append(m.familyCount("images")).append('\n');
        if (t1.isPresent()) {
            sb.append("- T1: ").append(t1.get().outcome()).append('\n');
            for (T1Evaluator.SampleOutcome s : t1.get().samples()) {
                sb.append("  - ").append(s.captureKind()).append('/').append(s.captureId()).append('/')
                    .append(s.sampleOrdinal()).append(": ").append(s.outcome()).append(" — ")
                    .append(s.reason()).append('\n');
            }
        }
        for (String note : notes) {
            sb.append("- note: ").append(note).append('\n');
        }
        Files.writeString(runDir.resolve("report.md"), sb.toString(), StandardCharsets.UTF_8);
    }

    /** Launches the client; returns the exit code. Throws {@link TimeoutException} on the hard ceiling. */
    private int launchClient(Path runDir, List<String> extraJvmArgs, Log log)
            throws IOException, InterruptedException {
        Path lock = ctx.cache().runs().resolve(".client.lock");
        try (java.nio.channels.FileChannel channel = java.nio.channels.FileChannel.open(lock,
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.WRITE);
             java.nio.channels.FileLock held = channel.tryLock()) {
            if (held == null) {
                throw new IOException("another client capture holds " + lock + " (one client per machine)");
            }
            List<String> command = ctx.launch().command(extraJvmArgs);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(ctx.launch().workingDir().toFile());
            pb.environment().putAll(ctx.launch().environment());
            Path clientLog = runDir.resolve("client.log");
            pb.redirectErrorStream(true);
            pb.redirectOutput(clientLog.toFile());
            log.info("launching client (log " + clientLog + ")");
            Process process = pb.start();
            boolean finished = process.waitFor(ctx.timeout().toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                process.waitFor(30, TimeUnit.SECONDS);
                copyLatestLog(runDir);
                throw new TimeoutException();
            }
            copyLatestLog(runDir);
            log.info("client exited " + process.exitValue());
            return process.exitValue();
        }
    }

    private void copyLatestLog(Path runDir) {
        Path latest = ctx.launch().workingDir().resolve("logs").resolve("latest.log");
        try {
            if (Files.isRegularFile(latest)) {
                Files.copy(latest, runDir.resolve("latest.log"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
            // the client log is diagnostic context, never evidence
        }
    }

    static final class TimeoutException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static String os() {
        return System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT).replaceAll("\\s+", "-");
    }

    public static String jvm() {
        return Runtime.version().toString();
    }

    static String zero64() {
        return "0".repeat(64);
    }

    /** Minimal logging seam so the CLI and tests can capture output. */
    public interface Log {
        void info(String message);

        void warn(String message);
    }

    /** Parses a stored profile text for planning (the last captured profile, if any). */
    public static GLCapabilityProfile parseProfile(String text) throws IOException {
        return GLCapabilityProfile.parse(new StringReader(text));
    }

    public static FlatDocument readFlat(Path file, String schemaLine) throws IOException {
        return FlatDocument.parse(Files.readString(file, StandardCharsets.UTF_8), schemaLine);
    }
}
