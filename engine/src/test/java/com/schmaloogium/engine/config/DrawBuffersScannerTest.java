// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/** The DRAWBUFFERS directive: positional slots, holes, duplicates, first-wins, CRLF-safe. */
class DrawBuffersScannerTest {

    @Test
    void digitsBecomeOrderedAttachmentSlots() {
        DrawRouting.Explicit routing = assertInstanceOf(DrawRouting.Explicit.class,
            DrawBuffersScanner.scan("#version 120\n/* DRAWBUFFERS:46 */\nvoid main(){}\n").orElseThrow());
        assertEquals(List.of(new DrawSlot.Attachment(new ColorAttachmentKey(4)),
            new DrawSlot.Attachment(new ColorAttachmentKey(6))), routing.slots());
    }

    @Test
    void holesDuplicatesAndFirstDirectiveArePreserved() {
        DrawRouting.Explicit routing = assertInstanceOf(DrawRouting.Explicit.class,
            DrawBuffersScanner.scan("/* DRAWBUFFERS:0N0 */\r\n/* DRAWBUFFERS:7 */\r\n").orElseThrow());
        assertEquals(List.of(new DrawSlot.Attachment(new ColorAttachmentKey(0)),
            new DrawSlot.None(), new DrawSlot.Attachment(new ColorAttachmentKey(0))),
            routing.slots());
    }

    @Test
    void absentOrIllegalDirectivesMeanAllUsed() {
        assertEquals(Optional.empty(), DrawBuffersScanner.scan("void main(){}"));
        assertEquals(Optional.empty(), DrawBuffersScanner.scan("/* DRAWBUFFERS: */"));
        assertTrue(DrawBuffersScanner.scan("/* DRAWBUFFERS:0x */").isEmpty(),
            "an illegal character voids the directive rather than truncating it");
    }
}
