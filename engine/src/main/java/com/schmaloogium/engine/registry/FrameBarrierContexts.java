// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The per-frame context issuer (PHASE_4_DOC §4.10). {@code activation} accepts only a
 * {@link StageStep} from the current published {@link StageRegistry} and requires
 * {@code shadowPass == (step.stage() == SHADOW && step.band() == SHADOW)}. {@code release}
 * issues the frame's canonical release-kind context, copying the most recently issued
 * activation stage/band or using FINAL/SCREEN when none was issued; its shadowPass is always
 * false, including when the copied pair is SHADOW/SHADOW.
 */
public interface FrameBarrierContexts {

    BarrierContext activation(StageStep step, boolean shadowPass);

    BarrierContext release();
}
