// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import com.schmaloogium.conformance.wire.SectionedText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Per-capture ignore rectangles (PHASE_2_DOC §4.6.4): a committed sidecar of
 * {@code [rect <name>]} blocks with {@code x, y, width, height} in pixel coordinates and a
 * mandatory {@code reason}. The report prints the masked fraction — an ignore mask is a
 * small admission of defeat and should be visible as one.
 */
public record IgnoreMask(List<Rect> rects) {

    public record Rect(int x, int y, int width, int height, String reason) {
        public Rect {
            if (x < 0 || y < 0 || width <= 0 || height <= 0) {
                throw new IllegalArgumentException("ignore rectangle must have non-negative origin"
                    + " and positive size");
            }
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("ignore rectangle needs a reason");
            }
        }

        boolean contains(int px, int py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
    }

    public static final IgnoreMask NONE = new IgnoreMask(List.of());

    public IgnoreMask {
        rects = List.copyOf(rects);
    }

    public static IgnoreMask parse(String text) {
        List<Rect> rects = new ArrayList<>();
        for (SectionedText.Section s : SectionedText.parse(text)) {
            if (!s.kind().equals("rect")) {
                throw new IllegalArgumentException("line " + s.line() + ": expected [rect <name>]");
            }
            Map<String, String> e = s.entries();
            for (String key : e.keySet()) {
                if (!List.of("x", "y", "width", "height", "reason").contains(key)) {
                    throw new IllegalArgumentException("[rect " + s.name() + "]: unknown key " + key);
                }
            }
            if (!e.containsKey("reason")) {
                throw new IllegalArgumentException("[rect " + s.name() + "]: reason is mandatory");
            }
            try {
                rects.add(new Rect(Integer.parseInt(e.get("x")), Integer.parseInt(e.get("y")),
                    Integer.parseInt(e.get("width")), Integer.parseInt(e.get("height")),
                    e.get("reason")));
            } catch (NumberFormatException | NullPointerException bad) {
                throw new IllegalArgumentException("[rect " + s.name() + "]: x, y, width, height"
                    + " must all be integers");
            }
        }
        return new IgnoreMask(rects);
    }

    /** True when the pixel is excluded from the comparison. */
    public boolean masked(int x, int y) {
        for (Rect r : rects) {
            if (r.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return rects.isEmpty();
    }
}
