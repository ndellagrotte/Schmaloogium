// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.preprocess.LegacyGeometryConfig;
import java.util.Optional;
import java.util.Set;

/** Per-program resource requirements. */
public record ProgramRequirements(DrawRouting routing,
    Set<ColorAttachmentKey> mipmappedAfterPass, VertexRequirements vertices,
    int instanceCount, Optional<LegacyGeometryConfig> legacyGeometry) {

    public ProgramRequirements {
        if (mipmappedAfterPass == null || mipmappedAfterPass.isEmpty()) {
            mipmappedAfterPass = Set.of();
        } else {
            // Ascending attachment order, and the incoming members actually copied in:
            // the set fed Phase 4's registry state and Phase 5's pass-mipmap requirements,
            // so dropping it left every composite mipmap request unasked for.
            Set<ColorAttachmentKey> ordered = new java.util.TreeSet<>(
                java.util.Comparator.comparingInt(ColorAttachmentKey::colortexIndex));
            ordered.addAll(mipmappedAfterPass);
            mipmappedAfterPass = java.util.Collections.unmodifiableSet(ordered);
        }
        legacyGeometry = legacyGeometry == null ? Optional.empty() : legacyGeometry;
    }
}
