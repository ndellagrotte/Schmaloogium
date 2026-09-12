// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.conformance.Manifests;
import com.schmaloogium.conformance.scene.CapturePlan;
import com.schmaloogium.conformance.scene.CapturePlanReader;
import com.schmaloogium.conformance.scene.SceneParser;
import com.schmaloogium.conformance.scene.SceneSpec;
import com.schmaloogium.conformance.tier.T0Evaluator;
import com.schmaloogium.conformance.tier.TierOutcome;
import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** RunManifestFailureTest + ManifestValidatorTest + ClientLaunchSpecTest + inventory/world-descriptor tests. */
class RunnerPiecesTest {

    static final Path PLAN_FIXTURE = Path.of("src", "test", "resources", "wire", "capture-plan-v4.plan");

    static CapturePlan plan() throws IOException {
        return CapturePlanReader.parse(Files.readString(PLAN_FIXTURE, StandardCharsets.UTF_8));
    }

    @Test
    void runnerSynthesizedFailureManifestIsSchemaValidAndFailsT0() throws IOException {
        CapturePlan plan = plan();
        RunManifest failed = FailureManifests.fromPlan(plan, "FAILED", "client timed out", "", 20_000L, true,
            "linux", "25", "0.6.10-alpha");
        RunManifest reparsed = RunManifestReader.parse(RunManifestWriter.render(failed));
        assertEquals(RunManifestWriter.render(failed), RunManifestWriter.render(reparsed));
        assertEquals(TierOutcome.FAIL, T0Evaluator.evaluate(reparsed).outcome());
        assertEquals(plan.planHash(), reparsed.token("run.planHash"));
        assertEquals(plan.document().token("pack.archiveSha512"), reparsed.token("pack.archiveSha512"));
        assertFalse(reparsed.bool("resources.available"));
        assertFalse(reparsed.bool("hooks.available"));
        assertFalse(reparsed.bool("timing.available"));
        assertEquals(0, reparsed.familyCount("frames"));
        assertEquals("NOT_REACHED", reparsed.token("run.compatVerdict"));
        RunManifest skipped = FailureManifests.fromPlan(plan, "SKIPPED", "fixture-absent: place x", "", 0, false,
            "linux", "25", "v");
        assertEquals("SKIPPED", RunManifestReader.parse(RunManifestWriter.render(skipped)).token("run.exitStatus"));
        assertThrows(IllegalArgumentException.class, () -> FailureManifests.fromPlan(plan, "COMPLETE", "x", "", 0,
            false, "l", "j", "v"));
        // the plan's provenance is authoritative: a manifest claiming another archive hash is rejected
        RunManifest tampered = Manifests.with(failed, b -> b.set("pack.archiveSha512",
            new RunManifest.Value.Token("0".repeat(128))));
        List<String> problems = ManifestValidator.validate(tampered, plan, Files.createTempDirectory("run"));
        assertTrue(problems.stream().anyMatch(p -> p.startsWith("pack.archiveSha512")), problems.toString());
    }

    @Test
    void validatorChecksImagesAgainstDisk(@TempDir Path run) throws IOException {
        CapturePlan plan = plan();
        RunManifest failed = FailureManifests.fromPlan(plan, "FAILED", "x", "", 1, false, "l", "j", "v");
        RunManifest withImage = Manifests.with(failed, b -> {
            java.util.SortedMap<String, RunManifest.Value> row = new TreeMap<>();
            row.put("captureKind", new RunManifest.Value.Token("SHOT"));
            row.put("captureId", new RunManifest.Value.Text("main"));
            row.put("sampleOrdinal", new RunManifest.Value.Int(0));
            row.put("path", new RunManifest.Value.Text("images/main-0.png"));
            row.put("width", new RunManifest.Value.Int(2));
            row.put("height", new RunManifest.Value.Int(2));
            row.put("pixelSha256", new RunManifest.Value.Token("0".repeat(64)));
            b.row("images", 0, row);
            return b;
        });
        List<String> absent = ManifestValidator.validate(withImage, plan, run);
        assertTrue(absent.stream().anyMatch(p -> p.contains("file absent")), absent.toString());
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Files.createDirectories(run.resolve("images"));
        javax.imageio.ImageIO.write(img, "png", run.resolve("images/main-0.png").toFile());
        List<String> wrongHash = ManifestValidator.validate(withImage, plan, run);
        assertTrue(wrongHash.stream().anyMatch(p -> p.contains("pixelSha256")), wrongHash.toString());
        String actual = com.schmaloogium.conformance.diff.PngRaster.read(run.resolve("images/main-0.png")).pixelSha256();
        RunManifest right = Manifests.with(withImage, b -> b.set("images.0.pixelSha256", new RunManifest.Value.Token(actual)));
        assertFalse(ManifestValidator.validate(right, plan, run).stream().anyMatch(p -> p.startsWith("images")));
    }

    @Test
    void launchSpecParsesAndBuildsTheCommand() {
        String text = ClientLaunchSpec.SCHEMA_LINE + "\n"
            + "args.0 = \"--username\"\nargs.1 = \"Developer\"\nargs.count = 2\n"
            + "classpath.0 = \"/a.jar\"\nclasspath.1 = \"/b.jar\"\nclasspath.count = 2\n"
            + "env.0.name = \"MC_VERSION\"\nenv.0.value = \"1.12.2\"\nenv.count = 1\n"
            + "java = \"/usr/bin/java\"\njvmArgs.0 = \"-Xmx2G\"\njvmArgs.count = 1\n"
            + "mainClass = \"com.cleanroommc.boot.MainClient\"\n"
            + "subject.0 = \"/mod/build/classes/java/main\"\nsubject.count = 1\n"
            + "workingDir = \"/mod/run/client\"\n";
        ClientLaunchSpec spec = ClientLaunchSpec.parse(text);
        List<String> cmd = spec.command(List.of("-Dx=1"));
        assertEquals(List.of("/usr/bin/java", "-Xmx2G", "-Dx=1", "-cp", "/a.jar:/b.jar",
            "com.cleanroommc.boot.MainClient", "--username", "Developer"), cmd);
        assertEquals("1.12.2", spec.environment().get("MC_VERSION"));
    }

    @Test
    void inventoryIsRehashedFromDiskAndAnchoredToTheSubject(@TempDir Path tmp) throws IOException {
        Path subject = tmp.resolve("classes");
        Files.createDirectories(subject.resolve("com"));
        Files.writeString(subject.resolve("com/A.class"), "A");
        Path lib = tmp.resolve("lib.jar");
        Files.writeString(lib, "jarbytes");
        ClientLaunchSpec launch = new ClientLaunchSpec("java", "Main", tmp, List.of(), List.of(), List.of(),
            Map.of(), List.of(subject));
        String subjectHash = LaunchInventory.subjectHash(List.of(subject));
        String libHash = Hashes.sha256HexOfFile(lib);
        String inventory = LaunchInventory.SCHEMA_LINE + "\n"
            + "mods.0.id = \"forge\"\nmods.0.sha256 = " + libHash + "\nmods.0.source = \"" + lib + "\"\n"
            + "mods.1.id = \"schmaloogium\"\nmods.1.sha256 = " + subjectHash + "\nmods.1.source = \"\"\n"
            + "mods.count = 2\n";
        var env = LaunchInventory.authenticate(inventory, launch, "1.12.2");
        assertEquals(subjectHash, env.subjectJarSha256());
        assertEquals(2, env.modJarHashes().size());
        assertNotEquals(env.modSetSha256(), env.externalModSetSha256());
        assertEquals(LaunchInventory.modSetHash(Map.of("forge", libHash), false), env.externalModSetSha256());
        String stale = inventory.replace(libHash, "0".repeat(64));
        assertThrows(IOException.class, () -> LaunchInventory.authenticate(stale, launch, "1.12.2"));
        // the subject's record is anchored to the launch spec's directories, never to the client's
        // claim: a rebuilt subject yields a new subject hash without re-running the inventory
        Files.writeString(subject.resolve("com/A.class"), "B");
        var rebuilt = LaunchInventory.authenticate(inventory, launch, "1.12.2");
        assertNotEquals(env.subjectJarSha256(), rebuilt.subjectJarSha256());
        assertEquals(env.externalModSetSha256(), rebuilt.externalModSetSha256());
    }

    @Test
    void worldDescriptorIsCanonicalAndDistinguishesGenerationInputs() throws IOException {
        SceneSpec scene = SceneParser.parse(com.schmaloogium.conformance.scene.SceneSpecTest.terrainDay());
        String external = "0".repeat(64);
        WorldCache.Descriptor a = WorldCache.descriptor(scene.world(), "1.12.2", external);
        assertTrue(a.document().render().startsWith(WorldCache.DESCRIPTOR_SCHEMA_LINE + "\n"));
        assertEquals(List.of("externalModSetSha256", "generateStructures", "minecraftVersion", "seed", "worldType"),
            List.copyOf(a.document().entries().keySet()));
        SceneSpec.World flat = new SceneSpec.World(scene.world().seed(), "flat", scene.world().generateStructures(),
            0, 0, "clear", 1, "peaceful", "creative", scene.world().gamerules(), List.of(), 0);
        assertNotEquals(a.sha256(), WorldCache.descriptor(flat, "1.12.2", external).sha256());
        // post-load state (time, weather, prepTicks) is not a generation input
        SceneSpec.World night = new SceneSpec.World(scene.world().seed(), "default", true, 0, 18000, "rain", 5,
            "peaceful", "creative", scene.world().gamerules(), List.of(), 40);
        assertEquals(a.sha256(), WorldCache.descriptor(night, "1.12.2", external).sha256());
    }

    @Test
    void framedTreeHashRejectsLinksAndIsOrderIndependent(@TempDir Path tmp) throws IOException {
        Path root = tmp.resolve("save");
        Files.createDirectories(root.resolve("region"));
        Files.writeString(root.resolve("level.dat"), "lvl");
        Files.writeString(root.resolve("region/r.0.0.mca"), "chunk");
        String first = Hashes.framedTreeSha256(root);
        Files.writeString(root.resolve("region/r.0.0.mca"), "chunk"); // same content, new mtime
        assertEquals(first, Hashes.framedTreeSha256(root));
        Files.writeString(root.resolve("level.dat"), "lvl2");
        assertNotEquals(first, Hashes.framedTreeSha256(root));
        try {
            Files.createSymbolicLink(root.resolve("link"), root.resolve("level.dat"));
        } catch (UnsupportedOperationException | IOException noLinks) {
            return;
        }
        assertThrows(IOException.class, () -> Hashes.framedTreeSha256(root));
    }

    @Test
    void resourcesGrammarRejectsWrongVariants() {
        RunManifest valid = Manifests.valid();
        RunManifestReader.parse(RunManifestWriter.render(valid));
        // PLANNED forbids allocation keys
        RunManifest planned = Manifests.with(valid, b -> b.set("resources.evidence_stage",
            new RunManifest.Value.Token("PLANNED")));
        assertThrows(IllegalArgumentException.class, () -> RunManifestReader.parse(RunManifestWriter.render(planned)));
        // CONSTANT needs all four colour components
        RunManifest missingColour = Manifests.with(valid, b -> b.unset("resources.colorBuffers.1.clear_color_a"));
        assertThrows(IllegalArgumentException.class, () -> RunManifestReader.parse(RunManifestWriter.render(missingColour)));
        // old aliases reject
        RunManifest alias = Manifests.with(valid, b -> b.set("resources.colorBuffers.0.format",
            new RunManifest.Value.Text("RGBA")));
        assertThrows(IllegalArgumentException.class, () -> RunManifestReader.parse(RunManifestWriter.render(alias)));
        FlatDocument d = FlatDocument.parse("schema = x/1\na = 1\nb = \"t\"\n", "schema = x/1");
        assertEquals(1, d.integer("a"));
        assertEquals("t", d.text("b"));
    }
}
