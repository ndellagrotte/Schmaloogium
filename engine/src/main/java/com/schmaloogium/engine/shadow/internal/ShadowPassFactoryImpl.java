// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.shadow.ShadowCameraMath;
import com.schmaloogium.engine.shadow.ShadowPassBuildInput;
import com.schmaloogium.engine.shadow.ShadowPassBuildResult;
import com.schmaloogium.engine.shadow.ShadowPassFactory;
import com.schmaloogium.engine.shadow.ShadowTraversalPlanner;

/**
 * The sole pass factory implementation (PHASE_8_DOC §2.2): pure wiring of the ready
 * plan with its borrowed collaborators into one invocation slot. The returned slot is
 * born PLANNED and is armed by the same publication close that Phase 7 performs.
 */
public final class ShadowPassFactoryImpl implements ShadowPassFactory {

    @Override
    public ShadowPassBuildResult create(ShadowPassBuildInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input");
        }
        ShadowInvocationSlotImpl slot = new ShadowInvocationSlotImpl(
                input.plan(),
                input.registryFingerprint(),
                input.uniforms(),
                input.world(),
                input.bindings(),
                input.renderThread(),
                input.reporter(),
                cameraMath(),
                traversalPlanner());
        return new ShadowPassBuildResult.Ready(slot, slot.epoch());
    }

    ShadowCameraMath cameraMath() {
        return ShadowCameraMath.shared();
    }

    ShadowTraversalPlanner traversalPlanner() {
        return ShadowTraversalPlanner.standard();
    }
}
