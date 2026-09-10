// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.RegistryFingerprint;

/**
 * Render-thread-only owner of accepted estate generations; {@link #publish} requires the exact
 * {@code RegistryFingerprint} used to build the candidate (PHASE_5_DOC §2.2, §4.11).
 */
public interface BufferEstatePublisher {

    PublishedBufferEstate current();

    BufferPublicationResult publish(
            BufferEstateCandidate candidate,
            RegistryFingerprint acceptedRegistry);

    BufferPublicationResult publishOff(BufferFailure cause);

    BufferResizeRegistrationResult addResizeConsumer(
            String consumerId,
            BufferResizeConsumer consumer,
            BufferSizing acknowledgedSizing,
            long acknowledgedGeneration);
}
