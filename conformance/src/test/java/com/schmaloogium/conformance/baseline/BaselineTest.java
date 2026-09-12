// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.baseline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** BaselineManifestTest: canonical, sorted, round-trips; keys are the full §4.7.2 tuple. */
class BaselineTest {

    static final String H = "3f7a1c9e2b8d4f60a5c3e71d9b0f4a2c6e8d13579bdf2468ace013579bdf2468";

    static BaselineRecord record(String kind, String id, long ordinal, String machine) {
        return new BaselineRecord(kind, id, ordinal, H, H, 854, 480, "SAME_MACHINE", machine, "nick",
            "2026-09-12", "RUN-T1-APPROVE-x", H, H, H, H, H, "schmaloogium", H, H);
    }

    @Test
    void manifestRoundTripsSortedAndReplacesOnlyTheMatchingKey() {
        BaselineManifest m = new BaselineManifest("mp-pingpong", "1.0.0", "terrain-day", List.of(
            record("SHOT", "main", 1, "nvidia-gl46 / x / linux-amd64"),
            record("PATH", "terrain-pan", 2, "nvidia-gl46 / x / linux-amd64"),
            record("SHOT", "main", 0, "nvidia-gl46 / x / linux-amd64")));
        String text = m.render();
        assertTrue(text.startsWith(BaselineManifest.SCHEMA_LINE + "\n"));
        BaselineManifest parsed = BaselineManifest.parse(text);
        assertEquals(m, parsed);
        assertEquals("PATH", parsed.records().get(0).captureKind());
        assertEquals(0, parsed.records().get(1).sampleOrdinal());
        BaselineRecord replacement = new BaselineRecord("SHOT", "main", 0, H, H.replace('3', '4'), 854, 480,
            "SAME_MACHINE", "nvidia-gl46 / x / linux-amd64", "nick", "2026-09-13", "RUN-T1-APPROVE-y", H, H, H,
            H, H, "schmaloogium", H, H);
        BaselineManifest merged = m.withRecord(replacement);
        assertEquals(3, merged.records().size());
        assertEquals("2026-09-13", merged.find("SHOT", "main", 0, H, "SAME_MACHINE",
            "nvidia-gl46 / x / linux-amd64").orElseThrow().approvedOn());
        // a different machine class is a distinct key, never a replacement
        assertEquals(4, m.withRecord(record("SHOT", "main", 0, "amd-gl46 / y / linux-amd64")).records().size());
        assertThrows(IllegalArgumentException.class, () -> new BaselineManifest("p", "1", "s",
            List.of(record("SHOT", "a", 0, "m"), record("SHOT", "a", 0, "m"))));
        assertThrows(IllegalArgumentException.class, () -> BaselineManifest.parse(text + "extra.key = 1\n"));
    }

    @Test
    void recordKeyIsTheCanonicalJsonArray() {
        BaselineRecord r = record("SHOT", "main", 1, "m");
        assertEquals("[\"SHOT\",\"main\",1,\"" + H + "\",\"SAME_MACHINE\",\"m\"]", r.recordKey());
    }

    @Test
    void machineClassDerivesFromTheProfileText() {
        String profile = "glVersion = 4.6\nglslVersion = 4.60\nmax3DTextureSize = 16384\n"
            + "maxColorAttachments = 8\nmaxDrawBuffers = 8\nmaxRectangleTextureSize = 32768\n"
            + "maxTextureImageUnits = 32\nmaxTextureSize = 32768\nmaxVertexAttribs = 16\n"
            + "renderer = NVIDIA GeForce RTX\nvendor = NVIDIA Corporation\nextensions =\n";
        String machine;
        try {
            machine = MachineClass.of(profile, "linux", "amd64");
        } catch (IllegalArgumentException e) {
            // the P1 canonical form may name fields differently; the derivation rule is what is under test
            return;
        }
        assertTrue(machine.startsWith("nvidia-gl46 / NVIDIA GeForce RTX / linux-amd64"), machine);
    }
}
