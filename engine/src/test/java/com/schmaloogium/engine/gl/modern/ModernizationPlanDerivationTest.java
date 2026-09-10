// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.modern;

import com.schmaloogium.engine.gl.AsyncCompileTier;
import com.schmaloogium.engine.gl.AsyncReadbackTier;
import com.schmaloogium.engine.gl.DebugFlags;
import com.schmaloogium.engine.gl.DebugTier;
import com.schmaloogium.engine.gl.DsaTier;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GlModernizationPlan;
import com.schmaloogium.engine.gl.GlModernizationPolicy;
import com.schmaloogium.engine.gl.SamplerTier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * §8.1 row {@code ModernizationPlanDerivationTest} (PHASE_14_DOC §8.1 row 1): the plan
 * derivation over the documented profile matrix — each of the five tiers resolves per
 * its documented value; every {@code FORCE_OFF} yields the reference-faithful tier
 * regardless of capability; every {@code FORCE_ON} on an incapable profile still yields
 * the fallback (policy may not overrule capability — and, per D-P14-17, not an open
 * evidence gate either); the rationale is non-empty and names the deciding capability
 * per row. Deterministic; no GL anywhere.
 */
class ModernizationPlanDerivationTest {

    private static final String ARB_DIRECT_STATE_ACCESS = "GL_ARB_direct_state_access";
    private static final String ARB_SAMPLER_OBJECTS = "GL_ARB_sampler_objects";
    private static final String KHR_DEBUG = "GL_KHR_debug";

    private static GLCapabilityProfile profile(int major, int minor, String... extensions) {
        return new GLCapabilityProfile(major, minor, major + "." + minor + "0",
                "vendor", "renderer", 8, 8, 16, 16, 4096, 256, 0, Set.of(extensions));
    }

    private static final List<GLCapabilityProfile> MATRIX = List.of(
            profile(2, 1),
            profile(3, 0),
            profile(3, 2),
            profile(3, 3),
            profile(4, 3),
            profile(4, 5),
            profile(3, 3, ARB_DIRECT_STATE_ACCESS),
            profile(3, 3, ARB_SAMPLER_OBJECTS),
            profile(3, 3, ARB_SAMPLER_OBJECTS, ARB_DIRECT_STATE_ACCESS),
            profile(4, 3, KHR_DEBUG));

    private static GlModernizationPlan derive(GLCapabilityProfile p, GlModernizationPolicy policy,
                                              boolean glLabels) {
        return GlModernizationPlanner.derive(p, policy, glLabels);
    }

    // ------------------------------------------------------------------ AUTO (D-P14-17)

    @Test
    void autoResolvesReferenceFaithfulOnEveryProfile() {
        for (GLCapabilityProfile p : MATRIX) {
            GlModernizationPlan plan = derive(p, GlModernizationPolicy.allAuto(), false);
            assertAll(p.glVersionMajor() + "." + p.glVersionMinor() + p.extensions(),
                    () -> assertEquals(SamplerTier.NONE, plan.sampler(), "L-2 open"),
                    () -> assertEquals(DsaTier.BIND_TO_EDIT, plan.dsa(), "L-6 open"),
                    () -> assertEquals(DebugTier.NONE, plan.debug(), "shipping default"),
                    () -> assertEquals(AsyncCompileTier.INLINE, plan.compile(),
                            "R-P14→P4-1/P7-1 ungranted, OQ-15 without a recorded pass"),
                    () -> assertEquals(AsyncReadbackTier.SYNCHRONOUS, plan.readback(),
                            "R-P14→P6-1 ungranted"));
        }
    }

    // ------------------------------------------------------------------ FORCE_OFF

    @Test
    void everyForceOffYieldsReferenceFaithfulRegardlessOfCapability() {
        GlModernizationPolicy allForceOff = new GlModernizationPolicy(
                GlModernizationPolicy.Row.FORCE_OFF, GlModernizationPolicy.Row.FORCE_OFF,
                GlModernizationPolicy.Row.FORCE_OFF, GlModernizationPolicy.Row.FORCE_OFF,
                GlModernizationPolicy.Row.FORCE_OFF);
        for (GLCapabilityProfile p : MATRIX) {
            GlModernizationPlan plan = derive(p, allForceOff, false);
            assertAll(p + " all-FORCE_OFF",
                    () -> assertEquals(SamplerTier.NONE, plan.sampler()),
                    () -> assertEquals(DsaTier.BIND_TO_EDIT, plan.dsa()),
                    () -> assertEquals(DebugTier.NONE, plan.debug()),
                    () -> assertEquals(AsyncCompileTier.INLINE, plan.compile()),
                    () -> assertEquals(AsyncReadbackTier.SYNCHRONOUS, plan.readback()));
        }
    }

    // ------------------------------------------------------------------ FORCE_ON vs capability

    @Test
    void samplerForceOnClimbsOnlyToCapableRungs() {
        assertAll(
                () -> assertEquals(SamplerTier.NONE,
                        sampler(derive(profile(2, 1), forceOn(), false))),
                () -> assertEquals(SamplerTier.NONE,
                        sampler(derive(profile(3, 2), forceOn(), false)),
                        "3.2 without ARB_sampler_objects stays on the reference path"),
                () -> assertEquals(SamplerTier.NONE,
                        sampler(derive(profile(3, 3), forceOn(), false)),
                        "plain 3.3 without the extension string stays NONE (failure table)"),
                () -> assertEquals(SamplerTier.PER_UNIT,
                        sampler(derive(profile(3, 3, ARB_SAMPLER_OBJECTS), forceOn(), false))),
                () -> assertEquals(SamplerTier.PER_UNIT,
                        sampler(derive(profile(4, 5), forceOn(), false))),
                () -> assertNotEquals(SamplerTier.MULTI_BIND,
                        sampler(derive(profile(4, 5), forceOn(), false))));
    }


    private static SamplerTier sampler(GlModernizationPlan plan) {
        return plan.sampler();
    }

    private static GlModernizationPolicy forceOn() {
        return new GlModernizationPolicy(GlModernizationPolicy.Row.FORCE_ON,
                GlModernizationPolicy.Row.FORCE_ON, GlModernizationPolicy.Row.FORCE_ON,
                GlModernizationPolicy.Row.FORCE_ON, GlModernizationPolicy.Row.FORCE_ON);
    }

    @Test
    void multiBindStaysLockedBehindItsGrantEvenWhenForced() {
        // R-P14→P5-2 is pending: even a 4.5 FORCE_ON profile may not climb to MULTI_BIND.
        GlModernizationPlan plan = derive(profile(4, 5), forceOn(), false);
        assertEquals(SamplerTier.PER_UNIT, plan.sampler());
        assertTrue(plan.rationale().get(0).contains("capability ladder"));
    }

    @Test
    void dsaForceOnClimbsOnlyToCapableRungs() {
        assertAll(
                () -> assertEquals(DsaTier.CORE_45,
                        derive(profile(4, 5), forceOn(), false).dsa()),
                () -> assertEquals(DsaTier.ARB,
                        derive(profile(3, 3, ARB_DIRECT_STATE_ACCESS), forceOn(), false).dsa()),
                () -> assertEquals(DsaTier.BIND_TO_EDIT,
                        derive(profile(3, 3), forceOn(), false).dsa(),
                        "3.3 without ARB_direct_state_access keeps the reference path"),
                () -> assertEquals(DsaTier.BIND_TO_EDIT,
                        derive(profile(2, 1), forceOn(), false).dsa()));
    }

    @Test
    void debugGateIsCapabilityAndFlagTogether() {
        GLCapabilityProfile capable = profile(4, 3);
        GLCapabilityProfile capableByExtension = profile(3, 3, KHR_DEBUG);
        GLCapabilityProfile incapable = profile(3, 3);
        assertAll(
                () -> assertEquals(DebugTier.KHR, derive(capable, forceOn(), true).debug()),
                () -> assertEquals(DebugTier.KHR,
                        derive(capableByExtension, forceOn(), true).debug()),
                () -> assertEquals(DebugTier.NONE, derive(capable, forceOn(), false).debug(),
                        "the flag is half the D-P14-14 gate"),
                () -> assertEquals(DebugTier.NONE, derive(incapable, forceOn(), true).debug(),
                        "capability is the other half; policy never overrules it"),
                () -> assertEquals(DebugTier.KHR,
                        derive(capable, GlModernizationPolicy.allAuto(), true).debug(),
                        "AUTO + flag follows the same gate"));
    }

    @Test
    void compileStaysInlineUntilOwnersAdoptTheExecutor() {
        for (GLCapabilityProfile p : MATRIX) {
            assertEquals(AsyncCompileTier.INLINE, derive(p, forceOn(), false).compile(),
                    "D-P14-22: a policy proposal must not mint compiler ownership");
        }
    }

    @Test
    void readbackForceOnCannotHideLatencyWithoutTheGrant() {
        assertAll(
                () -> assertEquals(AsyncReadbackTier.SYNCHRONOUS,
                        derive(profile(4, 5), forceOn(), false).readback(),
                        "R-P14→P6-1 ungranted: FORCE_ON cannot enable the ring"),
                () -> assertEquals(AsyncReadbackTier.SYNCHRONOUS,
                        derive(profile(2, 1), forceOn(), false).readback(),
                        "no fence sync either"));
    }

    // ------------------------------------------------------------------ rationale

    @Test
    void rationaleHasOneNonBlankEntryPerRow() {
        GlModernizationPlan plan = derive(profile(4, 5), forceOn(), true);
        List<String> rationale = plan.rationale();
        assertEquals(5, rationale.size(), "one deciding entry per row");
        for (int i = 0; i < rationale.size(); i++) {
            String line = rationale.get(i);
            assertFalse(line.isBlank(), "rationale " + i + " must not be blank");
            assertTrue(line.length() > 20, "rationale " + i + " must name its decision");
        }
        assertAll(
                () -> assertTrue(rationale.get(0).startsWith("sampler:")),
                () -> assertTrue(rationale.get(1).startsWith("dsa:")),
                () -> assertTrue(rationale.get(2).startsWith("debug:")),
                () -> assertTrue(rationale.get(3).startsWith("compile:")),
                () -> assertTrue(rationale.get(4).startsWith("readback:")));
    }

    // ------------------------------------------------------------------ runtime switchability

    @Test
    void rowsAreIndividuallySwitchableInTheSamePlan() {
        GlModernizationPolicy mixed = new GlModernizationPolicy(
                GlModernizationPolicy.Row.FORCE_OFF,
                GlModernizationPolicy.Row.FORCE_ON,
                GlModernizationPolicy.Row.AUTO,
                GlModernizationPolicy.Row.AUTO,
                GlModernizationPolicy.Row.FORCE_OFF);
        GlModernizationPlan plan = derive(profile(4, 5), mixed, true);
        assertAll(
                () -> assertEquals(SamplerTier.NONE, plan.sampler()),
                () -> assertEquals(DsaTier.CORE_45, plan.dsa()),
                () -> assertEquals(DebugTier.KHR, plan.debug()),
                () -> assertEquals(AsyncReadbackTier.SYNCHRONOUS, plan.readback()));
    }

    // ------------------------------------------------------------------ public entry point

    @Test
    void publicDeriveReadsTheGlLabelsFlag() {
        GLCapabilityProfile capable = profile(4, 3);
        try {
            System.setProperty(DebugFlags.GL_LABELS, "true");
            assertEquals(DebugTier.KHR,
                    GlModernizationPlanner.derive(capable, GlModernizationPolicy.allAuto()).debug());
            System.clearProperty(DebugFlags.GL_LABELS);
            assertEquals(DebugTier.NONE,
                    GlModernizationPlanner.derive(capable, GlModernizationPolicy.allAuto()).debug());
        } finally {
            System.clearProperty(DebugFlags.GL_LABELS);
        }
    }
}
