// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

/** ImageDifferTest + ClusterAnalysisTest + TolerancePolicyTest + IgnoreMaskTest + raster hash (§8.1). */
public class ImageDifferTest {

    public static final String PROFILES = """
        [profile IDENTICAL]
        channelTolerance = 0
        maxDifferingFraction = 0.0
        maxDelta = 0
        maxRmse = 0.0
        maxClusterArea = 0
        maxClusters = 0
        calibratedOn = ""
        [profile SAME_MACHINE]
        channelTolerance = 1
        maxDifferingFraction = 0.0005
        maxDelta = 8
        maxRmse = 1.0
        maxClusterArea = 64
        maxClusters = 1024
        calibratedOn = ""
        [profile ADVISORY]
        calibratedOn = ""
        """;

    static PngRaster solid(int w, int h, int argb) {
        int[] px = new int[w * h];
        Arrays.fill(px, argb);
        return new PngRaster(w, h, px);
    }

    static Map<String, TolerancePolicy> profiles() {
        return TolerancePolicy.parseFile(PROFILES);
    }

    @Test
    void identicalImagesHaveNoDifferingPixels() {
        PngRaster a = solid(64, 64, 0xFF336699);
        DiffResult r = ImageDiffer.compare(a, a, profiles().get("IDENTICAL"), IgnoreMask.NONE);
        assertEquals(0, r.differingPixels());
        assertTrue(r.passed());
        assertEquals(0.0, r.rmse());
    }

    @Test
    void aSingleAlteredPixelIsFoundAtItsCoordinate() {
        PngRaster a = solid(32, 16, 0xFF808080);
        int[] px = a.argb().clone();
        px[5 * 32 + 7] = 0xFF8080FF;
        DiffResult r = ImageDiffer.compare(a, new PngRaster(32, 16, px), profiles().get("IDENTICAL"), IgnoreMask.NONE);
        assertEquals(1, r.differingPixels());
        assertEquals(1, r.clusterCount());
        assertEquals(127, r.maxChannelDelta());
        assertFalse(r.passed());
    }

    @Test
    void diffuseNoisePassesSameMachineAndFailsIdentical() {
        PngRaster a = solid(200, 100, 0xFF707070);
        int[] px = a.argb().clone();
        Random random = new Random(7);
        for (int i = 0; i < px.length; i++) {
            int d = random.nextInt(3) - 1; // ±1 on the blue channel
            px[i] = (px[i] & 0xFFFFFF00) | (0x70 + d);
        }
        PngRaster b = new PngRaster(200, 100, px);
        assertTrue(ImageDiffer.compare(a, b, profiles().get("SAME_MACHINE"), IgnoreMask.NONE).passed());
        assertFalse(ImageDiffer.compare(a, b, profiles().get("IDENTICAL"), IgnoreMask.NONE).passed());
    }

    @Test
    void aSolidBlockFailsSameMachineOnL3WhilePassingL2() {
        // a 20x20 block of wrong pixels is 0.02% of a 1920x1080 frame: under the aggregate
        // threshold, and exactly what a missing beacon beam looks like — [D-P2-7]'s reason to exist.
        int w = 1920;
        int h = 1080;
        PngRaster a = solid(w, h, 0xFF404040);
        int[] px = a.argb().clone();
        for (int y = 100; y < 120; y++) {
            for (int x = 200; x < 220; x++) {
                px[y * w + x] = 0xFF444444; // delta 4: within maxDelta, so L2 is quiet
            }
        }
        DiffResult r = ImageDiffer.compare(a, new PngRaster(w, h, px), profiles().get("SAME_MACHINE"), IgnoreMask.NONE);
        assertTrue(r.differingFraction() <= 0.0005);
        assertTrue(r.maxChannelDelta() <= 8);
        assertTrue(r.rmse() <= 1.0);
        assertEquals(400, r.largestClusterArea());
        assertFalse(r.passed());
        assertTrue(r.failedPredicates().stream().allMatch(p -> p.startsWith("L3")), r.failedPredicates().toString());
    }

    @Test
    void dimensionMismatchThrowsRatherThanScoring() {
        assertThrows(IllegalArgumentException.class, () -> ImageDiffer.compare(solid(10, 10, 0),
            solid(10, 11, 0), profiles().get("IDENTICAL"), IgnoreMask.NONE));
    }

    @Test
    void fourConnectivityDoesNotMergeDiagonals() {
        // diagonal chain: (0,0),(1,1),(2,2) are three clusters under 4-connectivity
        boolean[] mask = new boolean[9];
        mask[0] = true;
        mask[4] = true;
        mask[8] = true;
        ClusterAnalysis.Result r = ClusterAnalysis.analyse(mask, 3, 3);
        assertEquals(3, r.clusterCount());
        assertEquals(1, r.largestClusterArea());
        boolean[] line = new boolean[9];
        line[3] = line[4] = line[5] = true;
        assertEquals(1, ClusterAnalysis.analyse(line, 3, 3).clusterCount());
        assertEquals(3, ClusterAnalysis.analyse(line, 3, 3).largestClusterArea());
    }

    @Test
    void profilesLoadAndAdvisoryNeverYieldsAVerdict() {
        Map<String, TolerancePolicy> p = profiles();
        assertEquals(64, p.get("SAME_MACHINE").maxClusterArea());
        assertFalse(p.get("SAME_MACHINE").calibrated());
        assertThrows(IllegalArgumentException.class, () -> TolerancePolicy.require(p, "NOPE"));
        DiffResult advisory = ImageDiffer.compare(solid(4, 4, 0), solid(4, 4, 0xFFFFFFFF), p.get("ADVISORY"), IgnoreMask.NONE);
        assertTrue(advisory.advisory());
        assertFalse(advisory.passed());
        assertTrue(advisory.failedPredicates().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> TolerancePolicy.parseFile(
            "[profile X]\nchannelTolerance = 1\ncalibratedOn = \"\"\n"));
        assertThrows(IllegalArgumentException.class, () -> TolerancePolicy.parseFile(
            "[profile ADVISORY]\nmaxDelta = 1\ncalibratedOn = \"\"\n"));
    }

    @Test
    void ignoreMasksExcludeTheirPixelsAndDemandAReason() {
        IgnoreMask mask = IgnoreMask.parse("[rect hud]\nx = 0\ny = 0\nwidth = 2\nheight = 2\nreason = glint is wall-clock\n");
        assertTrue(mask.masked(1, 1));
        assertFalse(mask.masked(2, 2));
        PngRaster a = solid(4, 4, 0xFF000000);
        int[] px = a.argb().clone();
        px[0] = 0xFFFFFFFF; // inside the mask
        DiffResult r = ImageDiffer.compare(a, new PngRaster(4, 4, px), profiles().get("IDENTICAL"), mask);
        assertTrue(r.passed());
        assertEquals(0.25, r.maskedFraction());
        assertThrows(IllegalArgumentException.class, () -> IgnoreMask.parse("[rect hud]\nx = 0\ny = 0\nwidth = 2\nheight = 2\n"));
    }

    @Test
    void rasterHashIsOverPixelsInScanOrder() {
        PngRaster a = new PngRaster(2, 1, new int[] {0x01020304, 0x05060708});
        PngRaster b = new PngRaster(1, 2, new int[] {0x01020304, 0x05060708});
        assertEquals(a.pixelSha256(), b.pixelSha256()); // same byte stream, dimensions live in the manifest
        assertEquals(64, a.pixelSha256().length());
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(2, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, 0x01020304);
        img.setRGB(1, 0, 0x05060708);
        assertEquals(a.pixelSha256(), PngRaster.of(img).pixelSha256());
    }
}
