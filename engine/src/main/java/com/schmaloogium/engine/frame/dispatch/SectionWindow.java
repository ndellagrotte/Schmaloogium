// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.dispatch;

/**
 * Which machine states admit a scope entry for a section (PHASE_7_DOC §4.2). Sky and the
 * below-terrain cloud scope are legal only from {@code ESTATE_CLEARED} and must close
 * before the shadow slot; every other gbuffers scope requires {@code SHADOW_DONE}. The
 * translucent trigger owns the DEFERRED_DONE transition; hand scopes require the
 * translucent window.
 */
public enum SectionWindow {
    /** Legal only between the main estate clear and the shadow slot. */
    ESTATE_CLEARED,
    /** Legal from SHADOW_DONE onward through the opaque gbuffers window. */
    GBUFFERS_OPAQUE,
    /** Legal only in the GBUFFERS_TRANS window (water and later). */
    GBUFFERS_TRANSLUCENT,
    /** Terrain-translucent entry: runs the PRE_TRANSLUCENT trigger, then water scope. */
    TRANSLUCENT_TRIGGER
}
