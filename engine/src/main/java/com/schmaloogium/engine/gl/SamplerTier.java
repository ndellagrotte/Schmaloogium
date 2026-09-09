// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Sampler-strategy tier (R-P14→P1-2, PHASE_1_DOC §4.7.8; PHASE_14_DOC §2.1):
 * MULTI_BIND | PER_UNIT | NONE. Selected once at bring-up into a
 * {@link GlModernizationPlan} and never mutated for the life of the GL context; NONE is
 * the reference-faithful shipping default until the sampler ledger entry closes
 * (D-P14-17).
 */
public enum SamplerTier {
    MULTI_BIND,
    PER_UNIT,
    NONE
}
