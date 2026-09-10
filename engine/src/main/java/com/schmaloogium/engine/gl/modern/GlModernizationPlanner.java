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

import java.util.ArrayList;
import java.util.List;

/**
 * The engagement half of {@link GlModernizationPlan}: {@link #derive} is the PURE
 * selection function of PHASE_14_DOC §2.2 Idea 1 — one immutable plan from the live
 * {@link GLCapabilityProfile} + user {@link GlModernizationPolicy}, before the first
 * frame, never mutated for the life of the GL context. The plan value itself lives in
 * {@code engine.gl} (P1 D-P1-54 grant); this planner sits beside it in the Phase 14
 * engagement package because the owning package's surfaces are frozen and the
 * derivation was assigned to this phase (PHASE_14_DOC §12 item 3).
 *
 * <p>Master rule D-P14-17: every row is individually switchable at runtime (the plan
 * reference is replaced wholesale), and {@code AUTO} resolves to the
 * reference-faithful path until that row's ledger entry closes. Policy may NOT
 * overrule capability — and it may not overrule an open evidence gate either: the
 * per-row gate constants below flip only when their owner's evidence or grant lands
 * (PHASE_14_DOC §9.1 "enabled-by-default vs conditional-disabled").
 */
public final class GlModernizationPlanner {

    // ------------------------------------------------------------------ evidence gates
    // Each constant is the ONE engagement-side switch of its row. It flips only when the
    // named evidence exists (ledger closure or owner grant) — never from user policy.

    /** §7.5 L-2 (GL 3.3 samplers remove native state churn) — open; no measured win yet. */
    static final boolean L2_SAMPLER_LEDGER_CLOSED = false;
    /** §7.5 L-6 (DSA availability and behavioral invisibility) — open; needs the §8.2 layers. */
    static final boolean L6_DSA_LEDGER_CLOSED = false;
    /** §4.3.6 imperceptibility criteria C1–C4 over the PBO/fence ring (closes L-3). */
    static final boolean L3_READBACK_C1_TO_C4_PASS = false;
    /** R-P14→P5-2 (MULTI_BIND batch boundary) — pending/ungranted (D-P14-19 summary). */
    static final boolean P5_2_MULTI_BIND_GRANT = false;
    /** R-P14→P6-1 (center-depth sample age) — pending/ungranted; without it a stale value
     *  would be hidden, which the design refuses ("shipping a silently-stale value is
     *  refused, not risked"). */
    static final boolean P6_1_SAMPLE_AGE_GRANT = false;
    /** R-P14→P4-1 + R-P14→P7-1 (compiler owner change + transaction resumability) —
     *  pending/ungranted (D-P14-22); until adopted the mechanism is the current
     *  synchronous {@code ProgramRegistryCompiler.compile}. */
    static final boolean P4_P7_COMPILER_ADOPTION = false;

    private static final String ARB_SAMPLER_OBJECTS = "GL_ARB_sampler_objects";
    private static final String ARB_MULTI_BIND = "GL_ARB_multi_bind";
    private static final String ARB_DIRECT_STATE_ACCESS = "GL_ARB_direct_state_access";
    private static final String KHR_DEBUG = "GL_KHR_debug";
    private static final String ARB_SYNC = "GL_ARB_sync";

    /** One row's selected tier plus the deciding rationale entry (§8.1 row 1). */
    private record Decision<T>(T tier, String why) {
    }

    private GlModernizationPlanner() {
    }

    /** Derives the plan from the profile + policy, reading the
     *  {@code schmaloogium.debug.glLabels} flag (DebugFlags.GL_LABELS) as the debug
     *  activity gate's second half (D-P14-14). Call once, at bring-up, after
     *  {@code mod.core} has bootstrapped. */
    public static GlModernizationPlan derive(GLCapabilityProfile profile,
                                             GlModernizationPolicy policy) {
        return derive(profile, policy, Boolean.getBoolean(DebugFlags.GL_LABELS));
    }

    /** Flag-injected derivation — the pure core the tests drive. */
    static GlModernizationPlan derive(GLCapabilityProfile profile,
                                      GlModernizationPolicy policy,
                                      boolean glLabelsEnabled) {
        List<String> rationale = new ArrayList<>(5);

        Decision<SamplerTier> sampler = deriveSampler(profile, policy.sampler());
        Decision<DsaTier> dsa = deriveDsa(profile, policy.dsa());
        Decision<DebugTier> debug = deriveDebug(profile, policy.debug(), glLabelsEnabled);
        Decision<AsyncCompileTier> compile = deriveCompile(profile, policy.compile());
        Decision<AsyncReadbackTier> readback = deriveReadback(profile, policy.readback());

        rationale.add(sampler.why());
        rationale.add(dsa.why());
        rationale.add(debug.why());
        rationale.add(compile.why());
        rationale.add(readback.why());
        return new GlModernizationPlan(sampler.tier(), dsa.tier(), debug.tier(),
                compile.tier(), readback.tier(), rationale);
    }

    // ------------------------------------------------------------------ rows

    private static Decision<SamplerTier> deriveSampler(GLCapabilityProfile p,
                                                       GlModernizationPolicy.Row row) {
        return switch (row) {
            case FORCE_OFF -> new Decision<>(SamplerTier.NONE,
                    "sampler: FORCE_OFF selects the reference-faithful per-texture path (D-P14-17)");
            case AUTO -> L2_SAMPLER_LEDGER_CLOSED
                    ? new Decision<>(capableSamplerLadder(p),
                            "sampler: L-2 closed; highest tier the profile admits")
                    : new Decision<>(SamplerTier.NONE,
                            "sampler: AUTO resolves reference-faithful until L-2 closes (D-P14-17); "
                                    + "ledger open, no measured native-churn win yet");
            case FORCE_ON -> new Decision<>(capableSamplerLadder(p),
                    "sampler: FORCE_ON over policy; capability ladder decides "
                            + "(policy never overrules capability)");
        };
    }

    /** MULTI_BIND → PER_UNIT → NONE (D-P14-2), each rung gated on real capability and
     *  its outstanding grant. */
    private static SamplerTier capableSamplerLadder(GLCapabilityProfile p) {
        boolean multiBindCapable = p.atLeast(4, 5) || p.hasExtension(ARB_MULTI_BIND);
        if (multiBindCapable && P5_2_MULTI_BIND_GRANT) {
            return SamplerTier.MULTI_BIND;
        }
        // Sampler objects are core from GL 3.3; below that the ARB extension string is
        // the evidence the matrix accepts (a plain "3.3" line stays reference-faithful).
        if (p.atLeast(4, 3) || (p.atLeast(3, 3) && p.hasExtension(ARB_SAMPLER_OBJECTS))) {
            return SamplerTier.PER_UNIT;
        }
        return SamplerTier.NONE;
    }

    private static Decision<DsaTier> deriveDsa(GLCapabilityProfile p,
                                               GlModernizationPolicy.Row row) {
        return switch (row) {
            case FORCE_OFF -> new Decision<>(DsaTier.BIND_TO_EDIT,
                    "dsa: FORCE_OFF selects the reference-faithful bind-to-edit path (D-P14-17)");
            case AUTO -> L6_DSA_LEDGER_CLOSED
                    ? new Decision<>(capableDsaLadder(p),
                            "dsa: L-6 closed; highest tier the profile admits")
                    : new Decision<>(DsaTier.BIND_TO_EDIT,
                            "dsa: AUTO resolves reference-faithful until L-6 closes (D-P14-17); "
                                    + "binding-neutrality capture outstanding");
            case FORCE_ON -> new Decision<>(capableDsaLadder(p),
                    "dsa: FORCE_ON over policy; capability ladder decides "
                            + "(policy never overrules capability)");
        };
    }

    /** CORE_45 → ARB → BIND_TO_EDIT; entirely internal to the backend (D-P14-5). */
    private static DsaTier capableDsaLadder(GLCapabilityProfile p) {
        if (p.atLeast(4, 5)) {
            return DsaTier.CORE_45;
        }
        if (p.hasExtension(ARB_DIRECT_STATE_ACCESS)) {
            return DsaTier.ARB;
        }
        return DsaTier.BIND_TO_EDIT;
    }
    private static Decision<DebugTier> deriveDebug(GLCapabilityProfile p,
                                                   GlModernizationPolicy.Row row,
                                                   boolean glLabelsEnabled) {
        boolean khrCapable = p.atLeast(4, 3) || p.hasExtension(KHR_DEBUG);
        return switch (row) {
            case FORCE_OFF -> new Decision<>(DebugTier.NONE,
                    "debug: FORCE_OFF selects the silent reference path (D-P14-17)");
            case AUTO, FORCE_ON -> {
                // D-P14-14: gate = (KHR_debug or GL 4.3) && -Dschmaloogium.debug.glLabels.
                // A debug context is never a precondition; the reference itself labels on
                // non-debug contexts, so the flag-gated KHR tier IS reference-faithful.
                boolean active = khrCapable && glLabelsEnabled;
                yield active
                        ? new Decision<>(DebugTier.KHR,
                                "debug: GL 4.3/KHR_debug capable and schmaloogium.debug.glLabels set (D-P14-14)")
                        : new Decision<>(DebugTier.NONE,
                                "debug: inactive — every shipping configuration; the tier needs "
                                        + (khrCapable ? "the glLabels flag" : "GL 4.3 or GL_KHR_debug"));
            }
        };
    }

    private static Decision<AsyncCompileTier> deriveCompile(GLCapabilityProfile p,
                                                            GlModernizationPolicy.Row row) {
        if (row == GlModernizationPolicy.Row.FORCE_OFF) {
            return new Decision<>(AsyncCompileTier.INLINE,
                    "compile: FORCE_OFF selects the current synchronous compiler (D-P14-17)");
        }
        if (!P4_P7_COMPILER_ADOPTION) {
            return new Decision<>(AsyncCompileTier.INLINE,
                    "compile: current synchronous compiler; R-P14→P4-1 + R-P14→P7-1 ungranted "
                            + "(D-P14-22 — a policy proposal must not mint compiler ownership)");
        }
        // Executor adopted: the driver table decides (row-16 semantics).
        return new Decision<>(AsyncCompileDriverPolicy.SHIPPED.resolve(p, row),
                "compile: executor adopted; OQ-15 verdict table (D-P14-12) decides this family");
    }

    private static Decision<AsyncReadbackTier> deriveReadback(GLCapabilityProfile p,
                                                              GlModernizationPolicy.Row row) {
        return switch (row) {
            case FORCE_OFF -> new Decision<>(AsyncReadbackTier.SYNCHRONOUS,
                    "readback: FORCE_OFF selects Phase 6's synchronous readDepthPixel path (D-P14-17)");
            case AUTO -> new Decision<>(AsyncReadbackTier.SYNCHRONOUS,
                    "readback: AUTO resolves synchronous until R-P14→P6-1 lands AND C1–C4 pass "
                            + "(a silently-stale value is refused, never risked)");
            case FORCE_ON -> {
                if (!P6_1_SAMPLE_AGE_GRANT) {
                    yield new Decision<>(AsyncReadbackTier.SYNCHRONOUS,
                            "readback: FORCE_ON cannot enable the ring while R-P14→P6-1 is ungranted "
                                    + "(D-P14-8: the latency must be contracted, never hidden)");
                }
                boolean fenceCapable = p.atLeast(3, 2) || p.hasExtension(ARB_SYNC);
                if (!fenceCapable) {
                    yield new Decision<>(AsyncReadbackTier.SYNCHRONOUS,
                            "readback: fence sync needs GL 3.2 or GL_ARB_sync; profile lacks both — "
                                    + "capability wins");
                }
                if (!L3_READBACK_C1_TO_C4_PASS) {
                    yield new Decision<>(AsyncReadbackTier.SYNCHRONOUS,
                            "readback: fence-capable profile, but §4.3.6 C1–C4 are unproven (L-3 open)");
                }
                yield new Decision<>(AsyncReadbackTier.PBO_FENCE,
                        "readback: R-P14→P6-1 granted and C1–C4 pass on a GL 3.2+ profile");
            }
        };
    }
}
