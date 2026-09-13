// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import java.util.List;

/**
 * Closed per-unit binding outcome. Estate/neutral and foreign objects appear as
 * {@code Borrowed}, with the producer identified by {@code BindingOrigin}.
 */
public sealed interface TextureBindingOutcome {
    record BoundObject(TextureHandleRef handle, DeclaredGlslType.Sampler shape,
                       List<ResolvedSamplerBinding> names, BindingOrigin origin)
        implements TextureBindingOutcome {

        public BoundObject {
            java.util.Objects.requireNonNull(handle, "handle");
            java.util.Objects.requireNonNull(shape, "shape");
            java.util.Objects.requireNonNull(origin, "origin");
            names = List.copyOf(names);
        }
    }

    record Unused() implements TextureBindingOutcome {
    }

    /**
     * A declared fixed unit the platform owns during this pass (PHASE_5_DOC §4.12.2:
     * {@code texture}/{@code lightmap} at units 0/1 in the gbuffers and shadow families).
     * The estate records the demand and issues no bind: whatever vanilla bound stays.
     */
    record ForeignRetained(List<ResolvedSamplerBinding> names) implements TextureBindingOutcome {
        public ForeignRetained {
            names = List.copyOf(names);
        }
    }
}
