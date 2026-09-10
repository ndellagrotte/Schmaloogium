// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Whether device capabilities satisfy the plan's limits (PHASE_5_DOC §2.2). */
public enum CapabilityGate {
    OK,
    SHORTFALL
}
