// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * The parameters of one framebuffer-to-framebuffer blit (PHASE_1_DOC §4.7.4): the source
 * and destination rectangles, the attachment mask (colour and/or depth) and the filter.
 * A depth blit must specify NEAREST — enforced HERE at construction rather than left to
 * the backend. Consumer: Phase 5, the one owner of framebuffer-to-framebuffer movement.
 */
public record BlitSpec(TextureRegion source, TextureRegion destination, Set<BlitMask> masks, BlitFilter filter) {

    public BlitSpec {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(masks, "masks");
        Objects.requireNonNull(filter, "filter");
        if (masks.isEmpty()) {
            throw new IllegalArgumentException("a blit must carry at least one attachment mask");
        }
        masks = EnumSet.copyOf(masks);
        if (masks.contains(BlitMask.DEPTH) && filter != BlitFilter.NEAREST) {
            throw new IllegalArgumentException("a depth blit must specify NEAREST (PHASE_1_DOC §4.7.4)");
        }
    }

    /** Attachment mask half: colour and/or depth. */
    public enum BlitMask {
        COLOR,
        DEPTH
    }

    /** Closed engine blit-filter vocabulary; the backend maps these to native values. */
    public enum BlitFilter {
        NEAREST,
        LINEAR
    }
}
