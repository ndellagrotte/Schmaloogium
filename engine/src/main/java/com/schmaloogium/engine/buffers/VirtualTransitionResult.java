// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.ProgramSlotId;

import java.util.List;

/**
 * Result of applying a planned virtual transition descriptor (PHASE_5_DOC §2.2).
 */
public sealed interface VirtualTransitionResult {
    record Applied(long frameId, ProgramSlotId transition, List<LogicalBuffer> flipped)
            implements VirtualTransitionResult {
        public Applied {
            flipped = List.copyOf(flipped);
        }
    }

    record NoChange(long frameId, ProgramSlotId transition) implements VirtualTransitionResult {
    }

    record Rejected(FrameProtocolRejection reason) implements VirtualTransitionResult {
    }
}
