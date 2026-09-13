// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans a fragment source for the {@code /* DRAWBUFFERS:0123 *&#47;} directive (RESEARCH App
 * A.2 / PHASE_3_DOC §4.5): each character is one positional slot, digits {@code 0-9} address
 * colortex0-9 in order (duplicates and holes preserved), {@code N} is a no-output slot. The
 * first directive in the file wins; a directive with no legal character is ignored (the
 * program then writes every buffer it uses, {@link DrawRouting.AllUsed}).
 */
public final class DrawBuffersScanner {

    private static final Pattern DIRECTIVE = Pattern.compile(
        "/\\*\\s*DRAWBUFFERS\\s*:\\s*([0-9A-Za-z]*)\\s*\\*/");

    private DrawBuffersScanner() {
    }

    /** The explicit routing the text declares, or empty when it declares none. */
    public static Optional<DrawRouting> scan(String text) {
        Matcher m = DIRECTIVE.matcher(text);
        while (m.find()) {
            List<DrawSlot> slots = new ArrayList<>();
            boolean legal = true;
            for (char c : m.group(1).toCharArray()) {
                if (c >= '0' && c <= '9') {
                    slots.add(new DrawSlot.Attachment(new ColorAttachmentKey(c - '0')));
                } else if (c == 'N') {
                    slots.add(new DrawSlot.None());
                } else {
                    legal = false;
                    break;
                }
            }
            if (legal && !slots.isEmpty()) {
                return Optional.of(new DrawRouting.Explicit(slots));
            }
        }
        return Optional.empty();
    }
}
