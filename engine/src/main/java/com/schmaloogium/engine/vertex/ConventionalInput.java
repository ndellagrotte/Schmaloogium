// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * Closed conventional-input participation vocabulary (PHASE_10_DOC §2.2; PHASE_1_DOC
 * §4.7.6, D-P1-68): the mask derives only from authenticated completed-producer
 * semantics — COLOR/UV1 participate iff the producer supplies them, and a partial ITEM
 * descriptor or filler grants none.
 */
public enum ConventionalInput {
    POSITION,
    COLOR,
    UV0,
    UV1,
    NORMAL
}
