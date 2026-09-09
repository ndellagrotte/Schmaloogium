// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.config.AlphaTestSpec;
import com.schmaloogium.engine.config.BlendSpec;
import com.schmaloogium.engine.preprocess.LegacyGeometryConfig;
import com.schmaloogium.engine.config.ViewportScale;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The complete immutable per-program state bundle (PHASE_4_DOC §2.2/§3.3). Alpha/blend/scale
 * are the Phase 3 evaluated values passed through losslessly; absent means "do not lock that
 * aspect" and an explicit OFF differs from absence. {@code explicitFlips} holds only the
 * Phase 3 tri-state override entries. Required non-null {@code geometryInput} is appended
 * after {@code legacyGeometry}. Virtual slots have no bundle. Immutable.
 */
public record ProgramStateBundle(
        DrawRouting drawRouting,
        Set<BufferRef> compositeMipmaps,
        int instanceCount,
        Set<ExtendedAttribute> attributes,
        Optional<AlphaTestSpec> alphaTest,
        Optional<BlendSpec> blend,
        Optional<ViewportScale> viewportScale,
        Map<BufferRef, Boolean> explicitFlips,
        Optional<LegacyGeometryConfig> legacyGeometry,
        GeometryInputRequirement geometryInput) {

    public ProgramStateBundle {
        java.util.Objects.requireNonNull(drawRouting, "drawRouting");
        compositeMipmaps = Set.copyOf(compositeMipmaps);
        if (instanceCount < 1) {
            throw new IllegalArgumentException("instanceCount must be positive: " + instanceCount);
        }
        attributes = Set.copyOf(attributes);
        alphaTest = alphaTest == null ? Optional.empty() : alphaTest;
        blend = blend == null ? Optional.empty() : blend;
        viewportScale = viewportScale == null ? Optional.empty() : viewportScale;
        explicitFlips = Map.copyOf(explicitFlips);
        legacyGeometry = legacyGeometry == null ? Optional.empty() : legacyGeometry;
        java.util.Objects.requireNonNull(geometryInput, "geometryInput");
    }
}
