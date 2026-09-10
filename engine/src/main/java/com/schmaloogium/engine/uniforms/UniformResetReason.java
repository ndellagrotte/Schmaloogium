// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Closed reset-reason domain (PHASE_6_DOC §4.14.1). {@code adoptRegistryGeneration}
 *  accepts exactly {@code PACK_REPLACEMENT | SHADERS_OFF | GL_CONTEXT_LOSS}; direct
 *  {@code reset} accepts exactly {@code WORLD_EPOCH}. There is no {@code CLOSE}: terminal
 *  disposal is {@code retire(UniformRetirementReason)} (R7-11, D-P6-18). */
public enum UniformResetReason {
    PACK_REPLACEMENT,
    SHADERS_OFF,
    GL_CONTEXT_LOSS,
    WORLD_EPOCH
}
