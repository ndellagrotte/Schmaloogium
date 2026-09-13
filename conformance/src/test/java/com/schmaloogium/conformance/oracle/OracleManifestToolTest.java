// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.conformance.diff.PngRaster;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** §4.8.2 step 6: the path grammar, the raster-hash check, the canonical committed manifest. */
class OracleManifestToolTest {

    private static final String OPTION = "a".repeat(64);
    private static final String WORLD = "b".repeat(64);

    private static PngRaster write(Path dir, String name, int fill) throws IOException {
        BufferedImage image = new BufferedImage(16, 8, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 16; x++) {
                image.setRGB(x, y, fill);
            }
        }
        PngRaster raster = PngRaster.of(image);
        Files.createDirectories(dir);
        javax.imageio.ImageIO.write(image, "png", dir.resolve(name == null ? raster.pixelSha256() + ".png" : name).toFile());
        return raster;
    }

    private static OracleManifestTool.Provenance provenance(String comparability) {
        return new OracleManifestTool.Provenance("HD_U_G6_pre1", "NVIDIA GeForce RTX 3080", "610.57.04",
            "2026-09-13", "nick", "two screenshots 10 s apart hashed identically", comparability,
            "static shot, pixels time-insensitive over the sampled range", WORLD, WORLD, WORLD);
    }

    @Test
    void hashesImagesIntoASortedCanonicalManifestThatRoundTrips(@TempDir Path cache) throws IOException {
        Path scene = OracleManifestTool.sceneRoot(cache, "seus-renewed", "1.0.1", "terrain-day");
        PngRaster a = write(scene.resolve(OPTION).resolve("SHOT").resolve("main").resolve("1"), null, 0xFF336699);
        PngRaster b = write(scene.resolve(OPTION).resolve("SHOT").resolve("main").resolve("0"), null, 0xFF993366);

        OracleManifest manifest = OracleManifestTool.build(cache, "seus-renewed", "1.0.1", "terrain-day",
            provenance("ESTABLISHED"));

        assertEquals(2, manifest.records().size());
        assertEquals(0, manifest.records().get(0).sampleOrdinal(), "sorted by record key");
        assertEquals(b.pixelSha256(), manifest.records().get(0).pixelSha256());
        assertEquals(a.pixelSha256(), manifest.records().get(1).pixelSha256());
        assertEquals(16, manifest.records().get(0).width());
        String text = manifest.render();
        assertTrue(text.startsWith(OracleManifest.SCHEMA_LINE));
        assertEquals(manifest, OracleManifest.parse(text));
        assertEquals(scene.resolve(OPTION).resolve("SHOT").resolve("main").resolve("0").resolve(b.pixelSha256() + ".png"),
            OracleManifestTool.imagePath(cache, "seus-renewed", "1.0.1", "terrain-day", manifest.records().get(0)));
        assertTrue(manifest.find("SHOT", "main", 1, OPTION).isPresent());
        assertTrue(manifest.find("PATH", "main", 1, OPTION).isEmpty());
    }

    @Test
    void misnamedOrMisfiledImagesAreRefused(@TempDir Path cache) throws IOException {
        Path scene = OracleManifestTool.sceneRoot(cache, "seus-renewed", "1.0.1", "terrain-day");
        write(scene.resolve(OPTION).resolve("SHOT").resolve("main").resolve("0"), "c".repeat(64) + ".png", 0xFF336699);
        IllegalArgumentException wrongName = assertThrows(IllegalArgumentException.class,
            () -> OracleManifestTool.build(cache, "seus-renewed", "1.0.1", "terrain-day", provenance("ESTABLISHED")));
        assertTrue(wrongName.getMessage().contains("hashes to"));

        Path other = OracleManifestTool.sceneRoot(cache, "seus-renewed", "1.0.1", "night-shadows");
        write(other.resolve(OPTION).resolve("SHOT").resolve("main"), null, 0xFF336699); // ordinal level missing
        assertThrows(IllegalArgumentException.class,
            () -> OracleManifestTool.build(cache, "seus-renewed", "1.0.1", "night-shadows", provenance("ESTABLISHED")));
        assertThrows(IllegalArgumentException.class,
            () -> OracleManifestTool.build(cache, "seus-renewed", "1.0.1", "absent", provenance("ESTABLISHED")));
    }

    @Test
    void recordsAreStaticShotsWithCompleteTimingEvidence() {
        assertThrows(IllegalArgumentException.class, () -> new OracleRecord("PATH", "pan", 1, OPTION, WORLD, 8, 8,
            "e", "ESTABLISHED", "r"));
        assertThrows(IllegalArgumentException.class, () -> new OracleRecord("SHOT", "main", 0, OPTION, WORLD, 8, 8,
            " ", "ESTABLISHED", "r"));
        assertThrows(IllegalArgumentException.class, () -> new OracleRecord("SHOT", "main", 0, OPTION, WORLD, 8, 8,
            "e", "MAYBE", "r"));
    }
}
