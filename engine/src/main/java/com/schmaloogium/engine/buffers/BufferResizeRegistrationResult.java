// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Outcome of registering a resize consumer with the estate publisher (PHASE_5_DOC §2.2).
 */
public sealed interface BufferResizeRegistrationResult {

    record Registered(BufferResizeRegistration registration)
            implements BufferResizeRegistrationResult {
    }

    record Rejected(BufferResizeRegistrationRejection reason)
            implements BufferResizeRegistrationResult {
    }
}
