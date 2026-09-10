// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable acquisition record of one open shadow pass; sides are frozen at beginPass.
 */
public record ShadowPassSnapshot(
        long estateGeneration,
        long depthAttachmentEpoch,
        long frameId,
        PassDescriptor pass,
        ProgramBindingSelection selection,
        FramebufferHandle framebuffer,
        List<ColorAttachment> colorAttachments,
        Map<LogicalBuffer, TextureHandle> readableTextures,
        Set<LogicalBuffer> flipAfterPass) {

    public ShadowPassSnapshot {
        java.util.Objects.requireNonNull(pass, "pass");
        java.util.Objects.requireNonNull(selection, "selection");
        java.util.Objects.requireNonNull(framebuffer, "framebuffer");
        colorAttachments = List.copyOf(java.util.Objects.requireNonNull(colorAttachments, "colorAttachments"));
        readableTextures = Map.copyOf(java.util.Objects.requireNonNull(readableTextures, "readableTextures"));
        flipAfterPass = Set.copyOf(java.util.Objects.requireNonNull(flipAfterPass, "flipAfterPass"));
    }
}
