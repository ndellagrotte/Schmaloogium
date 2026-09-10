// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.List;

/** Closed per-cell candidate-table entry: present candidates or a classified absence. */
public sealed interface TextureCandidateEntry {
    record Candidates(List<TextureBindingCandidate> candidates) implements TextureCandidateEntry {

        public Candidates {
            java.util.Objects.requireNonNull(candidates, "candidates");
            candidates.forEach(java.util.Objects::requireNonNull);
            candidates = List.copyOf(candidates);
        }
    }

    record Absent(TextureOverlayAbsence reason) implements TextureCandidateEntry {
    }
}
