// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.Optional;

/**
 * Handles for GL textures Minecraft owns (PHASE_1_DOC §4.7.3, [D-P1-36]). Implemented in
 * {@code mod.glue} — the only place a GL name may exist (C-1) — so no §4.7.4 verb changes
 * and no {@code adopt(int)} appears anywhere. The handle is the SAME type as an
 * engine-created one ({@link TextureHandle}) and carries a NARROWER contract: bind-only
 * (plus {@link DebugService#label}), and outside the owned-handle lifetime rule.
 *
 * <p>{@code key} is a pack-facing texture identifier used verbatim, in one of two disjoint
 * vocabularies: (a) App B.3's sampler names, bare — "texture", "lightmap", … — the fixed
 * unit map (Phase 5's); (b) {@code minecraft:}-namespaced resource locations naming a
 * texture Minecraft owns and keeps LIVE, e.g. "minecraft:dynamic/lightmap_1" (Phase 13's).
 * Phase 1 enumerates neither.
 *
 * <p>Resolution is lazy per call. Empty when the platform has no such texture yet, or none
 * under that key — the caller degrades rather than assuming presence. Never exposes the
 * GL name.
 */
public interface ForeignTextureProvider {

    /**
     * Resolves one pack-facing texture identifier (see class javadoc for the two
     * vocabularies). Empty when the platform has no such texture yet, or none under that
     * key. The returned handle resolves the underlying object at each use, so validity
     * across a vanilla reload is the backend's problem, not the caller's.
     */
    Optional<TextureHandle> handleFor(String key);
}
