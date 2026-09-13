// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import com.schmaloogium.conformance.capture.CaptureRunner;
import com.schmaloogium.conformance.capture.ClientLaunchSpec;
import com.schmaloogium.conformance.capture.LaunchInventory;
import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.capture.RunManifestReader;
import com.schmaloogium.conformance.diff.Calibration;
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
 *   selfcheck-compare --run-a DIR --run-b DIR     the same comparison over two existing run directories
 *   publish --run-dir DIR [--run RUN-T0] [--profile SAME_MACHINE] [--allow-uncalibrated]
 *   calibrate --run-a DIR --run-b DIR | --runs DIR,DIR,... [--profile SAME_MACHINE] [--factor 1.5]
 *             [--gpu S] [--driver S] [--write]
 *   oracle-manifest --pack ID@VER --scene ID --run-dir DIR --of-build S --gpu S --driver S --operator S
 *             [--captured-on YYYY-MM-DD] --timing-evidence S --comparability ESTABLISHED|UNAVAILABLE
 *             --comparability-reason S            §4.8.2 step 6: hash the cache's oracle images, write the .oracle
 *                                                 §4.6.5: observed maxima × factor → the profile file
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
        Set<String> flags = Set.of("--allow-uncalibrated", "--write");
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
                        + " T0 " + outcome.t0() + (outcome.t1().isPresent() ? " T1 " + outcome.t1().get().outcome() : "")
                        + (outcome.t2().isPresent() ? " T2 " + outcome.t2().get().outcome() : ""));
                    boolean ok = outcome.t0() == TierOutcome.PASS
                        && (outcome.t1().isEmpty() || outcome.t1().get().outcome() == TierOutcome.PASS)
                        && (outcome.t2().isEmpty() || outcome.t2().get().outcome() == TierOutcome.PASS);
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
            case "calibrate" -> calibrate(repoRoot, new CaptureRunner(context(repoRoot, cache, registry, false, opts)),
                opts);
            case "oracle-manifest" -> oracleManifest(repoRoot, cache, opts);
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
        System.out.println("usage: harness <stage-micropacks|inventory|world|capture|selfcheck|selfcheck-compare"
            + "|publish|calibrate|oracle-manifest|approve|evaluate> [--opt value]...");
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

    /** One same-ordinal image pair of two runs; {@code sameHash} short-circuits the diff. */
    record ImagePair(String key, String captureId, Path a, Path b, boolean sameHash) {
    }

    /** Pairs the two manifests' image families by ordinal; a mismatch is reported, not paired. */
    static List<ImagePair> pairImages(Path dirA, RunManifest a, Path dirB, RunManifest b, List<String> problems) {
        List<RunManifest.Row> imagesA = a.family("images");
        List<RunManifest.Row> imagesB = b.family("images");
        if (imagesA.size() != imagesB.size()) {
            problems.add("image counts differ: " + imagesA.size() + " vs " + imagesB.size());
        }
        List<ImagePair> pairs = new ArrayList<>();
        for (int i = 0; i < Math.min(imagesA.size(), imagesB.size()); i++) {
            RunManifest.Row ia = imagesA.get(i);
            RunManifest.Row ib = imagesB.get(i);
            String key = ia.token("captureKind") + "/" + ia.text("captureId") + "/" + ia.integer("sampleOrdinal");
            if (!key.equals(ib.token("captureKind") + "/" + ib.text("captureId") + "/" + ib.integer("sampleOrdinal"))) {
                problems.add("image order differs at " + i);
                continue;
            }
            pairs.add(new ImagePair(key, ia.text("captureId"), dirA.resolve(ia.text("path")),
                dirB.resolve(ib.text("path")), ia.token("pixelSha256").equals(ib.token("pixelSha256"))));
        }
        return pairs;
    }

    private static final TolerancePolicy IDENTICAL_LOCAL = new TolerancePolicy("IDENTICAL", false, 0, 0.0, 0, 0.0, 0, 0, "");

    /**
     * §4.6.5 steps 1–4 over two existing run directories of one scene: every same-ordinal pair is
     * diffed at {@code IDENTICAL} (so all five measured metrics are observed, masks honoured), the
     * maxima × {@code --factor} (default 1.5) propose the profile, and {@code --write} rewrites
     * {@code conformance/fixtures/tolerances.profile} with {@code calibratedOn} stamped from the
     * run's Forge "GL info" line (override with {@code --gpu}/{@code --driver}). Without
     * {@code --write} the proposal is only printed.
     */
    private static void calibrate(Path repoRoot, CaptureRunner runner, Map<String, String> opts) throws IOException {
        List<Path> dirs = new ArrayList<>();
        if (opts.containsKey("--runs")) {
            for (String d : opts.get("--runs").split(",")) {
                dirs.add(Path.of(d.strip()).toAbsolutePath().normalize());
            }
        } else {
            dirs.add(Path.of(require(opts, "--run-a")).toAbsolutePath().normalize());
            dirs.add(Path.of(require(opts, "--run-b")).toAbsolutePath().normalize());
        }
        if (dirs.size() < 2) {
            throw new IllegalArgumentException("calibrate needs at least two run directories");
        }
        List<RunManifest> manifests = new ArrayList<>();
        for (Path dir : dirs) {
            RunManifest m = RunManifestReader.parse(Files.readString(dir.resolve("manifest.manifest"), StandardCharsets.UTF_8));
            if (!m.token("run.exitStatus").equals("COMPLETE")) {
                throw new IllegalArgumentException(dir.getFileName() + " is not COMPLETE: " + m.token("run.exitStatus"));
            }
            if (!manifests.isEmpty() && !m.token("run.sceneId").equals(manifests.get(0).token("run.sceneId"))) {
                throw new IllegalArgumentException("runs are of different scenes: " + dir.getFileName());
            }
            manifests.add(m);
        }
        String scene = manifests.get(0).token("run.sceneId");
        Path dirA = dirs.get(0);
        Path profileFile = repoRoot.resolve("conformance/fixtures/tolerances.profile");
        Map<String, TolerancePolicy> profiles = TolerancePolicy.parseFile(Files.readString(profileFile, StandardCharsets.UTF_8));
        String name = opts.getOrDefault("--profile", "SAME_MACHINE");
        TolerancePolicy current = TolerancePolicy.require(profiles, name);
        double factor = Double.parseDouble(opts.getOrDefault("--factor", "1.5"));
        List<String> problems = new ArrayList<>();
        List<Calibration.Observation> observations = new ArrayList<>();
        // Every pair of runs is one §4.6.5 observation set: the floor is the maximum seen.
        for (int i = 0; i < dirs.size(); i++) {
            for (int j = i + 1; j < dirs.size(); j++) {
                String pairName = dirs.get(i).getFileName() + " vs " + dirs.get(j).getFileName();
                for (ImagePair pair : pairImages(dirs.get(i), manifests.get(i), dirs.get(j), manifests.get(j), problems)) {
                    var mask = runner.mask(scene, pair.captureId());
                    var diff = ImageDiffer.compare(PngRaster.read(pair.a()), PngRaster.read(pair.b()), IDENTICAL_LOCAL, mask);
                    observations.add(new Calibration.Observation(pair.key() + " [" + pairName + "]", diff));
                }
            }
        }
        if (!problems.isEmpty()) {
            throw new IllegalArgumentException("runs are not pairable: " + String.join("; ", problems));
        }
        if (observations.isEmpty()) {
            throw new IllegalArgumentException("no same-ordinal images to observe");
        }
        GlInfo gl = GlInfo.fromRunLog(dirA.resolve("latest.log"));
        String gpu = opts.getOrDefault("--gpu", gl.renderer());
        String driver = opts.getOrDefault("--driver", gl.version());
        if (gpu.isEmpty() || driver.isEmpty()) {
            throw new IllegalArgumentException("GPU/driver provenance not found in " + dirA.resolve("latest.log")
                + "; pass --gpu and --driver");
        }
        StringBuilder runNames = new StringBuilder();
        for (Path dir : dirs) {
            runNames.append(runNames.length() == 0 ? "" : " + ").append(dir.getFileName());
        }
        String calibratedOn = java.time.LocalDate.now() + ", " + gpu + ", " + driver + ", runs " + runNames;
        Calibration calibration = Calibration.of(current, observations, factor, calibratedOn);
        LOG.info(calibration.report());
        if (!calibration.raisedAnyThreshold()) {
            LOG.info("no threshold raised: the observed maxima sit under the starting numbers (floor "
                + calibration.observedMaxima() + ")");
        }
        if (opts.containsKey("--write")) {
            Map<String, TolerancePolicy> updated = new LinkedHashMap<>(profiles);
            updated.put(name, calibration.proposed());
            String text = TolerancePolicy.formatFile(updated);
            TolerancePolicy.parseFile(text); // the writer must round-trip before it touches the file
            Files.writeString(profileFile, text, StandardCharsets.UTF_8);
            LOG.info("wrote " + profileFile + " ([profile " + name + "] calibratedOn = " + calibratedOn + ")");
        } else {
            LOG.info("dry run: pass --write to update " + profileFile);
        }
    }

    /** §4.8.2 step 6: the oracle-manifest tool over the cache's oracle image tree. */
    private static void oracleManifest(Path repoRoot, FixtureCache cache, Map<String, String> opts)
            throws IOException {
        String packRef = require(opts, "--pack");
        int at = packRef.lastIndexOf('@');
        if (at <= 0) {
            throw new IllegalArgumentException("--pack must be ID@VERSION");
        }
        String packId = packRef.substring(0, at);
        String packVersion = packRef.substring(at + 1);
        String scene = require(opts, "--scene");
        Path runDir = Path.of(require(opts, "--run-dir")).toAbsolutePath().normalize();
        RunManifest candidate = RunManifestReader.parse(Files.readString(runDir.resolve("manifest.manifest"),
            StandardCharsets.UTF_8));
        var provenance = new com.schmaloogium.conformance.oracle.OracleManifestTool.Provenance(
            require(opts, "--of-build"), require(opts, "--gpu"), require(opts, "--driver"),
            opts.getOrDefault("--captured-on", java.time.LocalDate.now().toString()), require(opts, "--operator"),
            require(opts, "--timing-evidence"), require(opts, "--comparability"),
            require(opts, "--comparability-reason"),
            candidate.token("environment.worldGenerationSha256"), candidate.token("environment.worldSha256"),
            candidate.token("environment.externalModSetSha256"));
        var manifest = com.schmaloogium.conformance.oracle.OracleManifestTool.build(cache.oracle(), packId,
            packVersion, scene, provenance);
        Path out = com.schmaloogium.conformance.oracle.OracleManifestTool.manifestPath(repoRoot, packId,
            packVersion, scene);
        Files.createDirectories(out.getParent());
        Files.writeString(out, manifest.render(), StandardCharsets.UTF_8);
        LOG.info("wrote " + out + " (" + manifest.records().size() + " oracle records, " + manifest.provenance() + ")");
    }

    /** The Forge "GL info" line of a run's client log: vendor, version, renderer. */
    record GlInfo(String vendor, String version, String renderer) {
        static final java.util.regex.Pattern LINE = java.util.regex.Pattern.compile(
            "GL info: ' Vendor: '([^']*)' Version: '([^']*)' Renderer: '([^']*)'");

        static GlInfo fromRunLog(Path log) throws IOException {
            if (!Files.isRegularFile(log)) {
                return new GlInfo("", "", "");
            }
            try (var lines = Files.lines(log, StandardCharsets.UTF_8)) {
                return lines.map(LINE::matcher).filter(java.util.regex.Matcher::find)
                    .map(m -> new GlInfo(m.group(1), m.group(2), m.group(3)))
                    .findFirst().orElse(new GlInfo("", "", ""));
            }
        }
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
        for (ImagePair pair : pairImages(first.runDir(), a, second.runDir(), b, problems)) {
            if (pair.sameHash()) {
                LOG.info(pair.key() + " IDENTICAL (hash)");
                continue;
            }
            var diff = ImageDiffer.compare(PngRaster.read(pair.a()), PngRaster.read(pair.b()), IDENTICAL_LOCAL,
                runner.mask(scene, pair.captureId()));
            problems.add(pair.key() + " differs: " + diff.summary() + " — a §4.4 determinism leak, not a pack defect");
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
