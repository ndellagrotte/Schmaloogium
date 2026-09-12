// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import com.schmaloogium.conformance.capture.CaptureRunner;
import com.schmaloogium.conformance.capture.ClientLaunchSpec;
import com.schmaloogium.conformance.capture.LaunchInventory;
import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.capture.RunManifestReader;
import com.schmaloogium.conformance.diff.ImageDiffer;
import com.schmaloogium.conformance.diff.PngRaster;
import com.schmaloogium.conformance.diff.TolerancePolicy;
import com.schmaloogium.conformance.fixture.FixtureCache;
import com.schmaloogium.conformance.fixture.MicroPackStager;
import com.schmaloogium.conformance.fixture.PackFixture;
import com.schmaloogium.conformance.fixture.PackFixtureRegistry;
import com.schmaloogium.conformance.run.RunId;
import com.schmaloogium.conformance.scene.ClockProfile;
import com.schmaloogium.conformance.tier.TierOutcome;
import com.schmaloogium.conformance.wire.Hashes;
import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The harness command line (run through {@code ./gradlew :conformance:harness --args="..."}).
 * <pre>
 *   stage-micropacks                              build deterministic zips of the corpus packs into the cache
 *   inventory                                     launch the client once to record the loader inventory
 *   world --scene ID                              ensure the scene's world cache entry exists
 *   capture --run RUN-T0|RUN-T1-APPROVE|RUN-T1-REGRESS --pack ID@VER --scene ID|all
 *           [--clock default] [--profile SAME_MACHINE] [--allow-uncalibrated]
 *   selfcheck --pack ID@VER --scene ID            RUN-SCENE-SELFCHECK: two runs, IDENTICAL same-ordinal images
 *   approve --run-dir DIR --approver NAME [--profile SAME_MACHINE]
 *   evaluate --run-dir DIR [--profile SAME_MACHINE] [--allow-uncalibrated]
 * </pre>
 */
public final class Harness {

    private static final CaptureRunner.Log LOG = new CaptureRunner.Log() {
        @Override
        public void info(String message) {
            System.out.println("[harness] " + message);
        }

        @Override
        public void warn(String message) {
            System.out.println("[harness] WARN " + message);
        }
    };

    private Harness() {
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length == 0) {
            usage();
            System.exit(2);
        }
        Map<String, String> opts = new LinkedHashMap<>();
        Set<String> flags = Set.of("--allow-uncalibrated");
        for (int i = 1; i < argv.length; i++) {
            String a = argv[i];
            if (!a.startsWith("--")) {
                throw new IllegalArgumentException("unexpected argument " + a);
            }
            if (flags.contains(a)) {
                opts.put(a, "true");
            } else {
                if (i + 1 >= argv.length) {
                    throw new IllegalArgumentException(a + " needs a value");
                }
                opts.put(a, argv[++i]);
            }
        }
        Path repoRoot = Path.of(System.getProperty("schmaloogium.conformance.repoRoot", ".")).toAbsolutePath()
            .normalize();
        FixtureCache cache = FixtureCache.establish(repoRoot);
        LOG.info("cache root " + cache.root());
        PackFixtureRegistry registry = PackFixtureRegistry.parse(Files.readString(
            repoRoot.resolve("conformance/fixtures/packs.registry"), StandardCharsets.UTF_8));
        switch (argv[0]) {
            case "stage-micropacks" -> stageMicroPacks(repoRoot, cache, registry);
            case "inventory" -> inventory(context(repoRoot, cache, registry, false, opts));
            case "world" -> {
                CaptureRunner runner = new CaptureRunner(context(repoRoot, cache, registry, true, opts));
                var scene = runner.loadScene(require(opts, "--scene"));
                var env = LaunchInventory.authenticate(runner == null ? "" : inventoryText(repoRoot),
                    launch(repoRoot), CaptureRunner.MINECRAFT_VERSION);
                runner.ensureWorld(scene, env, LOG);
            }
            case "capture" -> {
                CaptureRunner runner = new CaptureRunner(context(repoRoot, cache, registry, true, opts));
                RunId run = RunId.parse(require(opts, "--run"));
                List<String> scenes = scenes(repoRoot, require(opts, "--scene"));
                int failures = 0;
                for (String scene : scenes) {
                    CaptureRunner.Outcome outcome = runner.capture(run, require(opts, "--pack"), scene,
                        opts.getOrDefault("--clock", "default"),
                        opts.getOrDefault("--profile", "SAME_MACHINE"),
                        opts.containsKey("--allow-uncalibrated"), LOG);
                    LOG.info("run dir " + outcome.runDir() + " exit " + outcome.manifest().token("run.exitStatus")
                        + " T0 " + outcome.t0() + (outcome.t1().isPresent() ? " T1 " + outcome.t1().get().outcome() : ""));
                    boolean ok = outcome.t0() == TierOutcome.PASS
                        && (outcome.t1().isEmpty() || outcome.t1().get().outcome() == TierOutcome.PASS);
                    if (!ok) {
                        failures++;
                    }
                }
                if (failures > 0) {
                    LOG.warn(failures + " of " + scenes.size() + " scene runs did not pass");
                    System.exit(1);
                }
            }
            case "selfcheck" -> {
                CaptureRunner runner = new CaptureRunner(context(repoRoot, cache, registry, true, opts));
                String pack = require(opts, "--pack");
                String scene = require(opts, "--scene");
                String clock = opts.getOrDefault("--clock", "default");
                compareRuns(runner, scene,
                    runner.capture(RunId.RUN_SCENE_SELFCHECK, pack, scene, clock, "IDENTICAL", true, LOG),
                    runner.capture(RunId.RUN_SCENE_SELFCHECK, pack, scene, clock, "IDENTICAL", true, LOG));
            }
            case "publish" -> {
                CaptureRunner runner = new CaptureRunner(context(repoRoot, cache, registry, false, opts));
                CaptureRunner.Outcome o = runner.republish(Path.of(require(opts, "--run-dir")),
                    RunId.parse(opts.getOrDefault("--run", "RUN-T0")), opts.getOrDefault("--profile", "SAME_MACHINE"),
                    opts.containsKey("--allow-uncalibrated"), LOG);
                LOG.info("republished " + o.runDir() + " exit " + o.manifest().token("run.exitStatus") + " T0 " + o.t0());
            }
            case "selfcheck-compare" -> {
                CaptureRunner runner = new CaptureRunner(context(repoRoot, cache, registry, false, opts));
                CaptureRunner.Outcome a = runner.republish(Path.of(require(opts, "--run-a")), RunId.RUN_SCENE_SELFCHECK,
                    "IDENTICAL", true, LOG);
                CaptureRunner.Outcome b = runner.republish(Path.of(require(opts, "--run-b")), RunId.RUN_SCENE_SELFCHECK,
                    "IDENTICAL", true, LOG);
                compareRuns(runner, a.manifest().token("run.sceneId"), a, b);
            }
            case "approve" -> new CaptureRunner(context(repoRoot, cache, registry, false, opts))
                .approve(Path.of(require(opts, "--run-dir")), require(opts, "--approver"),
                    opts.getOrDefault("--profile", "SAME_MACHINE"), LOG);
            case "evaluate" -> {
                CaptureRunner runner = new CaptureRunner(context(repoRoot, cache, registry, false, opts));
                Path runDir = Path.of(require(opts, "--run-dir"));
                RunManifest manifest = RunManifestReader.parse(Files.readString(runDir.resolve("manifest.manifest"),
                    StandardCharsets.UTF_8));
                var result = runner.evaluateT1(runDir, manifest,
                    Hashes.sha256HexOfFile(runDir.resolve("manifest.manifest")),
                    opts.getOrDefault("--profile", "SAME_MACHINE"), opts.containsKey("--allow-uncalibrated"), LOG);
                if (result.outcome() != TierOutcome.PASS) {
                    System.exit(1);
                }
            }
            default -> {
                usage();
                System.exit(2);
            }
        }
    }

    private static void usage() {
        System.out.println("usage: harness <stage-micropacks|inventory|world|capture|selfcheck|approve|evaluate> [--opt value]...");
    }

    private static String require(Map<String, String> opts, String key) {
        String v = opts.get(key);
        if (v == null) {
            throw new IllegalArgumentException("missing " + key);
        }
        return v;
    }

    private static List<String> scenes(Path repoRoot, String selection) throws IOException {
        if (!selection.equals("all")) {
            return List.of(selection);
        }
        List<String> ids = new ArrayList<>();
        try (var stream = Files.list(repoRoot.resolve("conformance/scenes"))) {
            stream.filter(p -> p.getFileName().toString().endsWith(".scene"))
                .map(p -> p.getFileName().toString().replaceAll("\\.scene$", ""))
                .sorted().forEach(ids::add);
        }
        return ids;
    }

    private static ClientLaunchSpec launch(Path repoRoot) throws IOException {
        Path spec = repoRoot.resolve("mod/build/conformance/client.launch");
        if (!Files.isRegularFile(spec)) {
            throw new IllegalStateException("missing " + spec + "; run ./gradlew :mod:writeClientLaunchSpec");
        }
        return ClientLaunchSpec.parse(Files.readString(spec, StandardCharsets.UTF_8));
    }

    private static Path inventoryFile(Path repoRoot) {
        return repoRoot.resolve("mod/build/conformance/inventory.txt");
    }

    private static String inventoryText(Path repoRoot) throws IOException {
        Path file = inventoryFile(repoRoot);
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("missing " + file + "; run: harness inventory");
        }
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    private static CaptureRunner.Context context(Path repoRoot, FixtureCache cache, PackFixtureRegistry registry,
            boolean needsInventory, Map<String, String> opts) throws IOException {
        Map<String, ClockProfile> clocks = ClockProfile.parseFile(Files.readString(
            repoRoot.resolve("conformance/fixtures/clocks.profile"), StandardCharsets.UTF_8));
        Map<String, TolerancePolicy> profiles = TolerancePolicy.parseFile(Files.readString(
            repoRoot.resolve("conformance/fixtures/tolerances.profile"), StandardCharsets.UTF_8));
        String cleanroom = System.getProperty("schmaloogium.conformance.cleanroomVersion", "unknown");
        Duration timeout = Duration.ofMinutes(Long.parseLong(opts.getOrDefault("--timeout-minutes", "20")));
        GLCapabilityProfile planning = new GLCapabilityProfile(3, 3, "3.30 planning", "Schmaloogium",
            "conformance-planning", 8, 8, 16, 16, 16384, 0, 0, Set.of("GL_ARB_texture_rectangle"));
        return new CaptureRunner.Context(repoRoot, cache, registry, launch(repoRoot),
            needsInventory ? inventoryText(repoRoot) : "", clocks, profiles, planning, timeout, cleanroom);
    }

    private static void stageMicroPacks(Path repoRoot, FixtureCache cache, PackFixtureRegistry registry)
            throws IOException {
        Path corpus = repoRoot.resolve("conformance/src/test/resources/packs");
        for (PackFixture fixture : registry.rows().values()) {
            if (!fixture.tier().equals("corpus")) {
                continue;
            }
            Path dir = corpus.resolve(fixture.id());
            if (!Files.isDirectory(dir)) {
                LOG.warn("corpus pack " + fixture.id() + " has no directory at " + dir);
                continue;
            }
            String sha = MicroPackStager.stage(dir, fixture, cache);
            String state = fixture.sha512().isEmpty() ? "UNPINNED (put this sha512 in packs.registry)"
                : fixture.sha512().equals(sha) ? "matches the registry pin" : "DOES NOT MATCH the registry pin";
            LOG.info("staged " + fixture.key() + " sha512 " + sha + " — " + state);
        }
    }

    private static void inventory(CaptureRunner.Context ctx) throws IOException, InterruptedException {
        Path out = inventoryFile(ctx.repoRoot());
        Files.createDirectories(out.getParent());
        Files.deleteIfExists(out);
        List<String> command = ctx.launch().command(List.of(
            "-Dschmaloogium.conformance.inventory=" + out.toAbsolutePath()));
        ProcessBuilder pb = new ProcessBuilder(command).directory(ctx.launch().workingDir().toFile());
        pb.environment().putAll(ctx.launch().environment());
        pb.redirectErrorStream(true);
        pb.redirectOutput(out.resolveSibling("inventory-client.log").toFile());
        LOG.info("launching client in inventory mode");
        Process process = pb.start();
        if (!process.waitFor(ctx.timeout().toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)) {
            process.destroyForcibly();
            throw new IllegalStateException("inventory client timed out");
        }
        if (!Files.isRegularFile(out)) {
            throw new IllegalStateException("client exited " + process.exitValue() + " without writing " + out);
        }
        var env = LaunchInventory.authenticate(Files.readString(out, StandardCharsets.UTF_8), ctx.launch(),
            CaptureRunner.MINECRAFT_VERSION);
        LOG.info("inventory authenticated: " + env.modJarHashes().size() + " mods, subject "
            + env.subjectJarSha256().substring(0, 16) + "…, external set "
            + env.externalModSetSha256().substring(0, 16) + "…");
    }

    private static void compareRuns(CaptureRunner runner, String scene, CaptureRunner.Outcome first,
            CaptureRunner.Outcome second) throws IOException {
        List<String> problems = new ArrayList<>();
        if (first.t0() != TierOutcome.PASS || second.t0() != TierOutcome.PASS) {
            problems.add("both runs must pass T0 (first " + first.t0() + ", second " + second.t0() + ")");
        }
        RunManifest a = first.manifest();
        RunManifest b = second.manifest();
        List<RunManifest.Row> imagesA = a.family("images");
        List<RunManifest.Row> imagesB = b.family("images");
        if (imagesA.size() != imagesB.size()) {
            problems.add("image counts differ: " + imagesA.size() + " vs " + imagesB.size());
        }
        TolerancePolicy identical = new TolerancePolicy("IDENTICAL", false, 0, 0.0, 0, 0.0, 0, 0, "");
        for (int i = 0; i < Math.min(imagesA.size(), imagesB.size()); i++) {
            RunManifest.Row ia = imagesA.get(i);
            RunManifest.Row ib = imagesB.get(i);
            String key = ia.token("captureKind") + "/" + ia.text("captureId") + "/" + ia.integer("sampleOrdinal");
            if (!key.equals(ib.token("captureKind") + "/" + ib.text("captureId") + "/" + ib.integer("sampleOrdinal"))) {
                problems.add("image order differs at " + i);
                continue;
            }
            if (ia.token("pixelSha256").equals(ib.token("pixelSha256"))) {
                LOG.info(key + " IDENTICAL (hash)");
                continue;
            }
            var diff = ImageDiffer.compare(PngRaster.read(first.runDir().resolve(ia.text("path"))),
                PngRaster.read(second.runDir().resolve(ib.text("path"))), identical,
                runner.mask(scene, ia.text("captureId")));
            problems.add(key + " differs: " + diff.summary() + " — a §4.4 determinism leak, not a pack defect");
        }
        List<RunManifest.Row> framesA = a.family("frames");
        List<RunManifest.Row> framesB = b.family("frames");
        if (framesA.size() != framesB.size()) {
            problems.add("frame counts differ");
        }
        long originA = a.has("timing.origin.worldTick") ? a.integer("timing.origin.worldTick") : 0;
        long originB = b.has("timing.origin.worldTick") ? b.integer("timing.origin.worldTick") : 0;
        for (int i = 0; i < Math.min(framesA.size(), framesB.size()); i++) {
            for (String field : List.of("worldTick", "logicalTick", "animationTick", "clockStep", "frameCounter",
                "entityCount")) {
                // world ticks compare relative to each run's checkpoint0 (absolute time depends on load wall-clock)
                long va = framesA.get(i).integer(field) - (field.equals("worldTick") ? originA : 0);
                long vb = framesB.get(i).integer(field) - (field.equals("worldTick") ? originB : 0);
                if (va != vb) {
                    problems.add("frames." + i + "." + field + " differs: " + va + " vs " + vb);
                }
            }
            for (String role : List.of("actualCurrent", "actualPrevious")) {
                for (String axis : List.of("posX", "posY", "posZ", "yaw", "pitch")) {
                    if (framesA.get(i).decimal(role + "." + axis) != framesB.get(i).decimal(role + "." + axis)) {
                        problems.add("frames." + i + "." + role + "." + axis + " differs");
                    }
                }
            }
        }
        if (problems.isEmpty()) {
            LOG.info("RUN-SCENE-SELFCHECK PASS: " + imagesA.size() + " same-ordinal images IDENTICAL, timing/poses/counts equal");
        } else {
            LOG.warn("RUN-SCENE-SELFCHECK FAIL:\n  " + String.join("\n  ", problems));
            System.exit(1);
        }
    }

    static String join(String[] a) {
        return String.join(" ", Arrays.asList(a));
    }
}
