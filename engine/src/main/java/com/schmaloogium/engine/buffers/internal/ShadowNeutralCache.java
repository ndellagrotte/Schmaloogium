// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.DepthTransferLayout;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.gl.TextureSpec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The bounded neutral shadow-object cache (PHASE_5_DOC §4.10): at most one 1&times;1
 * fully-far depth texture per distinct required shadowtex0/1 sampling policy (at most two)
 * and analogously at most two opaque-white color objects for shadowcolor0/1. The key is the
 * target/format class plus the complete effective {@link TextureParameters} — comparison
 * NONE and REF_TO_TEXTURE can never share, and different remaining parameters cannot share
 * either. Extent-dependent final-mip values are normalized to zero for 1&times;1 storage by
 * the caller. The cache is allocated once before real sfb construction, retained through
 * creation failure and runtime neutralization, never allocated per frame, and deleted once
 * with the owning ledger or estate teardown.
 */
public final class ShadowNeutralCache {

    /** Cache key: the full compatible sampling policy (target/format class + parameters). */
    private record Key(boolean depth, String formatClass, TextureParameters parameters) {
    }

    private static final TextureExtent NEUTRAL_EXTENT = new TextureExtent(1, 1, 1);

    private final GLDevice device;
    private final Consumer<Object> ownership;
    private final Map<Key, TextureHandle> objects = new LinkedHashMap<>();
    private final List<TextureHandle> depthUnits = new ArrayList<>();
    private final List<TextureHandle> colorUnits = new ArrayList<>();

    public ShadowNeutralCache(GLDevice device, Consumer<Object> ownership) {
        this.device = device;
        this.ownership = ownership;
    }

    /** Ensures the unit-order depth neutral exists for this policy; equal policies share. */
    public void requireDepthUnit(DepthAttachmentFormat format, DepthTransferLayout transfer,
            TextureParameters effectiveParameters) {
        depthUnits.add(objects.computeIfAbsent(
            new Key(true, format.name(), effectiveParameters),
            key -> allocateDepth(format, transfer, effectiveParameters)));
    }

    /** Ensures the unit-order color neutral exists for this policy; equal policies share. */
    public void requireColorUnit(TextureParameters effectiveParameters) {
        colorUnits.add(objects.computeIfAbsent(
            new Key(false, FormatTable.fallbackFormat().name(), effectiveParameters),
            key -> allocateColor(effectiveParameters)));
    }

    /** The neutral object backing shadowtex unit {@code index} (tex0, tex1). */
    public TextureHandle depthByUnit(int index) {
        return depthUnits.get(index);
    }

    /** The neutral object backing shadowcolor unit {@code index} (color0, color1). */
    public TextureHandle colorByUnit(int index) {
        return colorUnits.get(index);
    }

    /** Every owned neutral object, first-creation order (teardown deletes in reverse). */
    public List<TextureHandle> ownedObjects() {
        return List.copyOf(objects.values());
    }

    private TextureHandle allocateDepth(DepthAttachmentFormat format,
            DepthTransferLayout transfer, TextureParameters parameters) {
        TextureHandle texture = device.textures().create(
            "schmaloogium.buffers/shadow-neutral-depth");
        ownership.accept(texture);
        device.textures().allocate(texture, new TextureSpec.DepthTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, format, new PixelLayout.Depth(transfer),
            NEUTRAL_EXTENT, 1));
        // The complete admitted chain is level zero here: fully far (depth 1.0, stencil 0).
        ByteBuffer texels = ByteBuffer.allocate(4);
        if (transfer == DepthTransferLayout.DEPTH_STENCIL_UNSIGNED_INT_24_8) {
            texels.putInt(0xFFFFFF00);
        } else {
            texels.putFloat(1.0f);
        }
        texels.rewind();
        device.textures().upload(texture, new TextureData(TextureAllocationTarget.TEXTURE_2D,
            new TextureRegion(0, 0, 0, 1, 1, 1), 0, new PixelLayout.Depth(transfer), texels));
        device.textures().setParameters(texture, parameters);
        return texture;
    }

    private TextureHandle allocateColor(TextureParameters parameters) {
        TextureHandle texture = device.textures().create(
            "schmaloogium.buffers/shadow-neutral-color");
        ownership.accept(texture);
        FormatTable.FormatRow row = FormatTable.row(FormatTable.fallbackFormat());
        device.textures().allocate(texture, new TextureSpec.ColorTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, FormatTable.fallbackFormat(),
            row.allocationLayout(), NEUTRAL_EXTENT, 1));
        // Opaque white through the table's normalized transfer layout (one 1x1 texel).
        ByteBuffer texels = ByteBuffer.allocate(4);
        for (int channel = 0; channel < 4; channel++) {
            texels.put((byte) 0xFF);
        }
        texels.rewind();
        device.textures().upload(texture, new TextureData(TextureAllocationTarget.TEXTURE_2D,
            new TextureRegion(0, 0, 0, 1, 1, 1), 0, row.allocationLayout(), texels));
        device.textures().setParameters(texture, parameters);
        return texture;
    }
}
