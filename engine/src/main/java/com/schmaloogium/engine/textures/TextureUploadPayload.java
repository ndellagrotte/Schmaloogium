// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.gl.TextureData;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * One owned source's immutable decoded pixels/raw transfer bytes: initial uploads in
 * ascending mip order, animation rows ascending iconName UTF-8 bytes then
 * sourceFrameIndex, each row ascending mip. No duplicates, gaps in required initialized
 * coverage or out-of-bounds regions. P1 borrows each TextureData cursor synchronously,
 * without mutation or retention; byte views stay stable read-only owned preparation data.
 */
public record TextureUploadPayload(List<TextureData> initialUploads,
                                   List<TextureFramePayload> animationFrames) {

    private static final Comparator<TextureFramePayload> ROW_ORDER = (a, b) -> {
        int byIcon = AtlasDescriptor.unsignedUtf8Compare(a.iconName(), b.iconName());
        return byIcon != 0 ? byIcon : Integer.compare(a.sourceFrameIndex(), b.sourceFrameIndex());
    };

    public TextureUploadPayload {
        Objects.requireNonNull(initialUploads, "initialUploads");
        initialUploads.forEach(Objects::requireNonNull);
        initialUploads = List.copyOf(initialUploads);
        for (int i = 1; i < initialUploads.size(); i++) {
            int prev = initialUploads.get(i - 1).mipLevel();
            int cur = initialUploads.get(i).mipLevel();
            if (cur <= prev) {
                throw new IllegalArgumentException("initial uploads must ascend by mip level");
            }
        }
        Objects.requireNonNull(animationFrames, "animationFrames");
        animationFrames.forEach(Objects::requireNonNull);
        List<TextureFramePayload> rows = List.copyOf(animationFrames);
        for (int i = 1; i < rows.size(); i++) {
            if (ROW_ORDER.compare(rows.get(i - 1), rows.get(i)) >= 0) {
                throw new IllegalArgumentException(
                    "animation rows must ascend by iconName UTF-8 bytes then sourceFrameIndex");
            }
        }
        animationFrames = rows;
    }

    public static TextureUploadPayload ofInitial(List<TextureData> initialUploads) {
        return new TextureUploadPayload(initialUploads, List.of());
    }
}
