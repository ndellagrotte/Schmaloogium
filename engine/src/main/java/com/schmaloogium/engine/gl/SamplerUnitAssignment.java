// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * One fixed-unit sampler assignment (PHASE_1_DOC §4.7.4, D-P1-59): the immutable complete
 * list P5 supplies to {@link ShaderService#initializeSamplerUnits}. Names are exact
 * sampler names; units are 0–15.
 */
public record SamplerUnitAssignment(String exactName, int unit) {
}
