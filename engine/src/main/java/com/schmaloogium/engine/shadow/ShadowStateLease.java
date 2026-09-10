// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * The reversible §4.4 state lease (PHASE_8_DOC): opened after all pre-GL mutation is
 * complete, restored exactly once, never reopened. Restoration covers forced-third-person
 * state, shadow viewport/camera installation and Phase-8 platform shadow-state scopes in
 * reverse acquisition order.
 */
public interface ShadowStateLease {

    /** Restores every captured scope independently; idempotent; never throws past report. */
    void restore();
}
