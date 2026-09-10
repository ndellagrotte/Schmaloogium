// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.RegistryFingerprint;

/**
 * Outcome of a buffer estate publication attempt (PHASE_5_DOC §2.2, §4.11).
 */
public sealed interface BufferPublicationResult {

    record Published(PublishedBufferEstate publication)
            implements BufferPublicationResult {
    }

    record ProvenanceRejected(RegistryFingerprint candidateRegistry,
                              RegistryFingerprint acceptedRegistry)
            implements BufferPublicationResult {
    }

    record ConsumerFailed(long failedGeneration, PublishedBufferEstate offPublication,
                          String consumerId, int deliveredCount)
            implements BufferPublicationResult {
    }
}
