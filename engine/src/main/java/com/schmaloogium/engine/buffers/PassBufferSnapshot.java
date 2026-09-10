// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.TextureHandle;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable authenticated pass snapshot stamped by the view at acquisition
 * (PHASE_5_DOC §2.2).
 */
public record PassBufferSnapshot(
        long estateGeneration,
        long depthAttachmentEpoch,
        long frameId,
        PassDescriptor pass,
        ProgramBindingSelection selection,
        List<ColorAttachment> colorAttachments,
        Map<LogicalBuffer, TextureHandle> readableTextures,
        Set<LogicalBuffer> flipAfterPass,
        PassDrawTarget drawTarget) {

    public PassBufferSnapshot {
        pass = Objects.requireNonNull(pass, "pass");
        selection = Objects.requireNonNull(selection, "selection");
        colorAttachments = List.copyOf(Objects.requireNonNull(colorAttachments, "colorAttachments"));
        readableTextures = Map.copyOf(Objects.requireNonNull(readableTextures, "readableTextures"));
        flipAfterPass = Set.copyOf(Objects.requireNonNull(flipAfterPass, "flipAfterPass"));
        drawTarget = Objects.requireNonNull(drawTarget, "drawTarget");
    }
}
