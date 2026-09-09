// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Catalog slot kind (PHASE_4_DOC §2.2). {@link #VIRTUAL_FLIP_CONTROL} marks the typed
 * {@code deferred_pre}/{@code composite_pre} transitions; {@link #FIXED_FUNCTION_SENTINEL}
 * marks the external {@code <none>} menu sentinel. A real slot that terminates in a fixed
 * terminal (for example {@code shadow}) is still {@link #RASTER}.
 */
public enum ProgramSlotKind {
    RASTER,
    VIRTUAL_FLIP_CONTROL,
    FIXED_FUNCTION_SENTINEL
}
