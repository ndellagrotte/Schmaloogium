// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Projection-eligible candidate-build failure stages (PHASE_4_DOC §4.12). {@code BARRIER}
 * is not a stage and never enters a projection row; runtime barrier/publication failures
 * stay in their closed results and diagnostics.
 */
public enum ProgramBuildStage {
    MATERIALIZE,
    CAPABILITY,
    SAMPLER_LAYOUT,
    COMPILE,
    ATTRIBUTE_BIND,
    LINK,
    VALIDATE,
    UNEXPECTED_BACKEND
}
