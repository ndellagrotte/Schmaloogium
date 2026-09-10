// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.Optional;

/**
 * Optional custom render layer per state ordinal after the opaque-solid exclusion
 * (PHASE_9_DOC §4.9). The lookup is a decision, never a renderer mutation: Phase 7 asks
 * it during terrain dispatch and otherwise preserves vanilla's layer. A
 * publication-generation change invalidates the decision alongside the aliases.
 */
public interface RenderLayerLookup {

    /** The resolved custom layer for one state ordinal, or empty when vanilla applies. */
    Optional<ResolvedRenderLayer> layer(int blockStateOrdinal);
}
