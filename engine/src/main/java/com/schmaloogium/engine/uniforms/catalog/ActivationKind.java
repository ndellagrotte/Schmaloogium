// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

/**
 * Activation policy of one built-in (PHASE_6_DOC §4.3): how the precomputed
 * {@code ProgramUploadPlan} entry behaves at every successful shader activation.
 */
public enum ActivationKind {
    /** Exact canonical comparison; equal values skip the GL upload. */
    SKIP_EQUAL,
    /** Matrices: uploaded on every activation, equality deliberately ignored. */
    ALWAYS_UPLOAD,
    /** Signal cell that additionally uploads immediately while a Phase 4 activity token
     *  is current (celestial, fog, blend, entity color/IDs, instance, atlas size). */
    IMMEDIATE_IF_ACTIVE
}
