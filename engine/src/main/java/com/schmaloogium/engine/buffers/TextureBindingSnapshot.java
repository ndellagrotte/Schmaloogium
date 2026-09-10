// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import java.util.List;

/**
 * Immutable 16-row texture binding snapshot. {@link #outcome(int)} is total for units 0–15 and
 * rejects out-of-range input.
 */
public interface TextureBindingSnapshot extends AutoCloseable {
    long estateGeneration();

    long depthAttachmentEpoch();

    long frameId();

    PassDescriptor pass();

    ProgramBindingSelection selection();

    TextureOverlayPublicationId overlayPublication();

    BindingPurpose purpose();

    List<TextureBindingRow> rows();

    TextureBindingOutcome outcome(int unit);

    List<TextureBindingDiagnostic> diagnostics();

    boolean isCurrent();

    @Override
    void close();
}
