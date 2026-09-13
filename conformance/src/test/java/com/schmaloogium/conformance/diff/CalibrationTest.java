// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** §4.6.5: observed maxima × factor propose the profile; L1 stays; nothing is ever lowered. */
class CalibrationTest {

    private static final TolerancePolicy IDENTICAL = new TolerancePolicy("IDENTICAL", false, 0, 0.0, 0, 0.0, 0, 0, "");

    private static TolerancePolicy sameMachine() {
        return TolerancePolicy.require(ImageDifferTest.profiles(), "SAME_MACHINE");
    }

    /** A 100×100 solid raster with {@code n} isolated pixels raised by {@code delta} on green. */
    private static DiffResult observe(int n, int delta) {
        PngRaster a = ImageDifferTest.solid(100, 100, 0xFF404040);
        int[] px = a.argb().clone();
        for (int i = 0; i < n; i++) {
            int x = (i * 7) % 100;
            int y = (i * 13) % 100;
            px[y * 100 + x] = 0xFF400040 | (Math.min(255, 0x40 + delta) << 8);
        }
        return ImageDiffer.compare(a, new PngRaster(100, 100, px), IDENTICAL, IgnoreMask.NONE);
    }

    @Test
    void maximaTimesFactorRaiseOnlyTheMeasuredThresholds() {
        TolerancePolicy current = sameMachine();
        DiffResult five = observe(5, 54);
        DiffResult one = observe(1, 8);
        Calibration c = Calibration.of(current, List.of(
            new Calibration.Observation("SHOT/main/0", five),
            new Calibration.Observation("PATH/terrain-pan/1", one)), 1.5, "2026-09-13, GPU, driver");
        TolerancePolicy p = c.proposed();
        assertEquals(current.channelTolerance(), p.channelTolerance(), "L1 is not measured");
        assertEquals(81, p.maxDelta(), "ceil(54 × 1.5)");
        assertEquals(0.00075, p.maxDifferingFraction(), 1e-12, "5/10000 × 1.5 raises the fraction");
        assertTrue(p.maxRmse() >= current.maxRmse());
        assertEquals(current.maxClusterArea(), p.maxClusterArea(), "largest cluster 1 × 1.5 < 64");
        assertEquals(current.maxClusters(), p.maxClusters(), "5 clusters × 1.5 < 1024");
        assertEquals("2026-09-13, GPU, driver", p.calibratedOn());
        assertTrue(p.calibrated());
        assertTrue(c.raisedAnyThreshold());
        assertTrue(c.report().contains("maxDelta = 81"));
        assertTrue(c.report().contains("SHOT/main/0"));
    }

    @Test
    void zeroObservationKeepsStartingNumbersButStampsProvenance() {
        TolerancePolicy current = sameMachine();
        DiffResult none = observe(0, 0);
        Calibration c = Calibration.of(current, List.of(new Calibration.Observation("SHOT/main/0", none)),
            2.0, "2026-09-13, GPU, driver");
        TolerancePolicy p = c.proposed();
        assertEquals(current.maxDelta(), p.maxDelta());
        assertEquals(current.maxDifferingFraction(), p.maxDifferingFraction());
        assertEquals(current.maxRmse(), p.maxRmse());
        assertEquals(current.maxClusterArea(), p.maxClusterArea());
        assertEquals(current.maxClusters(), p.maxClusters());
        assertFalse(c.raisedAnyThreshold());
        assertTrue(p.calibrated());
    }

    @Test
    void fractionIsRaisedWhenObservedExceedsTheStartingNumber() {
        TolerancePolicy current = sameMachine();
        // 60 of 10 000 pixels = 0.006 > 0.0005; × 1.5 = 0.009
        Calibration c = Calibration.of(current, List.of(new Calibration.Observation("k", observe(60, 20))),
            1.5, "x");
        assertEquals(0.009, c.proposed().maxDifferingFraction(), 1e-9);
        assertEquals(30, c.proposed().maxDelta());
    }

    @Test
    void rejectsAdvisoryBadFactorAndMissingProvenance() {
        TolerancePolicy current = sameMachine();
        List<Calibration.Observation> obs = List.of(new Calibration.Observation("k", observe(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> Calibration.of(TolerancePolicy.advisory(""), obs, 1.5, "x"));
        assertThrows(IllegalArgumentException.class, () -> Calibration.of(current, obs, 0.5, "x"));
        assertThrows(IllegalArgumentException.class, () -> Calibration.of(current, obs, 1.5, ""));
    }

    @Test
    void writerRoundTripsThroughTheParser() {
        Map<String, TolerancePolicy> profiles = ImageDifferTest.profiles();
        String text = TolerancePolicy.formatFile(profiles);
        assertTrue(text.startsWith("# Tolerance profiles"));
        Map<String, TolerancePolicy> again = TolerancePolicy.parseFile(text);
        assertEquals(profiles.keySet(), again.keySet());
        for (String name : profiles.keySet()) {
            assertEquals(profiles.get(name), again.get(name), name);
        }
        assertEquals(text, TolerancePolicy.formatFile(again), "formatting is a fixed point");
        // ADVISORY carries calibratedOn only; a quoted provenance survives with its escapes
        TolerancePolicy stamped = new TolerancePolicy("SAME_MACHINE", false, 1, 0.0005, 81, 1.0, 64, 1024,
            "2026-09-13, NVIDIA \"RTX\", 610.57.04");
        Map<String, TolerancePolicy> one = Map.of("SAME_MACHINE", stamped);
        assertEquals(stamped, TolerancePolicy.parseFile(TolerancePolicy.formatFile(one)).get("SAME_MACHINE"));
        String advisory = TolerancePolicy.formatProfile(TolerancePolicy.advisory(""));
        assertEquals("[profile ADVISORY]\ncalibratedOn = \"\"\n", advisory);
    }
}
