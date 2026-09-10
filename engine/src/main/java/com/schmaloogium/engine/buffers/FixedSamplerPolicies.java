// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.buffers.internal.AppB3Policy;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

/**
 * The Phase-5-owned fixed sampler policy (PHASE_5_DOC §4.12.1, D-P5-35): pure, schema- and
 * layout-independent, available before any runtime or GL object. Phase 4's
 * {@code INVALID_SAMPLER_POLICY} boundary consumes {@link #appB3()} before compile; the
 * static {@link #lookup(String)} and {@link #resolve(FixedSamplerName, ProgramSamplerLayout,
 * StageId, StageId, StageBand) resolve} entries serve planning and binding without a layout
 * object.
 */
public final class FixedSamplerPolicies {

    private static final AppB3Policy POLICY = AppB3Policy.create();

    private FixedSamplerPolicies() {
    }

    /** The App B.3 policy singleton (fingerprint-stable for the engine's lifetime). */
    public static FixedSamplerLayoutPolicy appB3() {
        return POLICY;
    }

    /** The policy's stable identity, shared with {@link #appB3()}. */
    public static FixedSamplerPolicyFingerprint appB3Fingerprint() {
        return AppB3Policy.staticFingerprint();
    }

    /**
     * Pure resolver over the App B.3 policy: {@code resolve(layout, stage, band)} returns
     * Ready for a valid same-policy layout (fixed-function and virtual layouts yield an empty
     * Ready), Invalid with typed evidence otherwise.
     */
    public static FixedSamplerResolver resolver() {
        return POLICY.resolver();
    }

    /**
     * Case-sensitive exact-name lookup over the 33 legal names; never synthesizes
     * colortex8-15 or any other sentinel.
     */
    public static FixedSamplerLookup lookup(String exactName) {
        return AppB3Policy.lookup(exactName);
    }

    /**
     * Pure fixed-unit resolution for one name in one stage domain. The layout parameter is
     * the effective provider's complete layout; it decides the conditional {@code shadow}
     * rule. Unknown stage domains and names without a fixed unit in the stage's column return
     * {@link FixedSamplerResolution.UnsupportedDomain}.
     */
    public static FixedSamplerResolution resolve(FixedSamplerName name,
            ProgramSamplerLayout layout, StageId stage, StageBand band) {
        return AppB3Policy.resolve(name, layout, stage, band);
    }
}
