// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.modern;

import com.schmaloogium.engine.gl.AsyncCompileTier;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GlModernizationPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8.1 row {@code DriverPolicyTest} (PHASE_14_DOC §8.1 row 16, D-P14-12): the shipped
 * driver-eligibility data defaults to DENY — an unknown vendor/renderer resolves
 * INLINE; an allowlisted family resolves SHARED_CONTEXT; FORCE_OFF overrides the
 * allowlist; the denylist overrides FORCE_ON. Deterministic; no GL anywhere.
 */
class DriverPolicyTest {

    private static GLCapabilityProfile profile(String vendor, String renderer) {
        return new GLCapabilityProfile(4, 5, "4.50", vendor, renderer,
                8, 8, 16, 16, 4096, 256, 0, Set.of());
    }

    /** Allowlist: NVIDIA by vendor, or anything with "rtx" in the renderer. */
    private static final AsyncCompileDriverPolicy POLICY = new AsyncCompileDriverPolicy(
            List.of(new AsyncCompileDriverPolicy.Family("nvidia", ""),
                    new AsyncCompileDriverPolicy.Family("", "rtx")),
            List.of(new AsyncCompileDriverPolicy.Family("amd", "broken")));

    @Test
    void unknownVendorDefaultsToDeny() {
        assertEquals(AsyncCompileTier.INLINE,
                POLICY.resolve(profile("Mesa", "Software Rasterizer"),
                        GlModernizationPolicy.Row.AUTO),
                "default deny: an unrecorded family never goes async (D-P14-12)");
        assertFalse(POLICY.allowlisted(profile("Mesa", "Software Rasterizer")));
    }

    @Test
    void allowlistedFamilyEnablesUnderAuto() {
        GLCapabilityProfile nvidia = profile("NVIDIA Corporation", "GeForce RTX 3080");
        assertTrue(POLICY.allowlisted(nvidia));
        assertEquals(AsyncCompileTier.SHARED_CONTEXT,
                POLICY.resolve(nvidia, GlModernizationPolicy.Row.AUTO));
    }

    @Test
    void matchingIsCaseInsensitiveOnBothComponents() {
        assertTrue(POLICY.allowlisted(profile("nvidia corporation", "geforce rtx 3080")));
        assertTrue(POLICY.allowlisted(profile("ANY", "NVIDIA RTX A4000")));
    }

    @Test
    void forceOffOverridesTheAllowlist() {
        assertEquals(AsyncCompileTier.INLINE,
                POLICY.resolve(profile("NVIDIA Corporation", "GeForce RTX 3080"),
                        GlModernizationPolicy.Row.FORCE_OFF));
    }

    @Test
    void forceOnOverridesAllowlistAbsenceButNotTheDenylist() {
        GLCapabilityProfile unknown = profile("Mesa", "Software Rasterizer");
        assertEquals(AsyncCompileTier.SHARED_CONTEXT,
                POLICY.resolve(unknown, GlModernizationPolicy.Row.FORCE_ON),
                "a per-user FORCE_ON accepts the risk on an unlisted family");
        GLCapabilityProfile denylisted = profile("AMD", "Radeon (broken-radeonsi)");
        assertTrue(POLICY.denylisted(denylisted));
        assertEquals(AsyncCompileTier.INLINE,
                POLICY.resolve(denylisted, GlModernizationPolicy.Row.FORCE_ON),
                "the denylist overrides FORCE_ON");
    }

    @Test
    void blankFamilyComponentIsAWildcard() {
        AsyncCompileDriverPolicy catchAll = new AsyncCompileDriverPolicy(
                List.of(new AsyncCompileDriverPolicy.Family(" ", null)), List.of());
        assertTrue(catchAll.allowlisted(profile("Anything", "any renderer")));
    }

    @Test
    void shippedTableDeniesEveryFamily() {
        // The OQ-15 verdict table is empty until the spike records a pass.
        assertTrue(AsyncCompileDriverPolicy.SHIPPED.allowlist().isEmpty());
        for (GLCapabilityProfile p : List.of(
                profile("NVIDIA Corporation", "GeForce RTX 3080"),
                profile("AMD", "Radeon RX 6800 (radeonsi)"),
                profile("Intel", "Mesa Iris"),
                profile("Unknown", "Unknown"))) {
            for (GlModernizationPolicy.Row row : GlModernizationPolicy.Row.values()) {
                if (row == GlModernizationPolicy.Row.FORCE_ON) {
                    continue; // FORCE_ON intentionally bypasses the allowlist
                }
                assertEquals(AsyncCompileTier.INLINE,
                        AsyncCompileDriverPolicy.SHIPPED.resolve(p, row),
                        "the shipped table deny-defaults " + p.vendor() + " under " + row);
            }
        }
    }
}
