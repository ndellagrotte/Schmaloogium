// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.registry.ProgramSlotId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Buffer failure value (PHASE_5_DOC §2.2); never carries raw GL numbers — driver detail goes to GUI/log,
 * not chat.
 */
public record BufferFailure(BufferFailureCode code, String messageKey, String diagnosticId,
        List<LogicalBuffer> buffers, Optional<ProgramSlotId> pass, Optional<ColorInternalFormat> requestedFormat) {

    public BufferFailure {
        code = Objects.requireNonNull(code, "code");
        messageKey = Objects.requireNonNull(messageKey, "messageKey");
        diagnosticId = Objects.requireNonNull(diagnosticId, "diagnosticId");
        buffers = List.copyOf(Objects.requireNonNull(buffers, "buffers"));
        pass = pass == null ? Optional.empty() : pass;
        requestedFormat = requestedFormat == null ? Optional.empty() : requestedFormat;
    }
}
