// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureHandle;

/**
 * An engine-OWNED texture of the LWJGL3 backend (PHASE_1_DOC §4.7.3, D-P1-71): logical
 * creation yields no native name; the first admitted allocation materializes the exact
 * target and applies the retained label. Deleting an unmaterialized handle retires it
 * without a native delete. Extent/format metadata backs the depth-copy and clear
 * admission checks.
 */
final class Lwjgl3OwnedTexture implements TextureHandle, Lwjgl3Handle {

    private final Lwjgl3GLDevice device;
    private String subjectLabel;
    private boolean deleted;

    /** -1 until materialized by the first admitted allocation. */
    private int glName = -1;

    /** The target fixed by the first successful allocation; null before it. */
    TextureAllocationTarget target;

    ColorInternalFormat colorFormat;
    DepthAttachmentFormat depthFormat;
    int allocatedWidth;
    int allocatedHeight;
    int allocatedDepth;
    int mipLevels;

    /** Set when level zero has been defined to the source depth format by the first
     *  depth initialization (for copyDepthToTexture's steady-copy admission). */
    boolean levelZeroDepthDefined;
    boolean levelZeroDepthStencil;

    Lwjgl3OwnedTexture(Lwjgl3GLDevice owner, String debugLabel) {
        this.device = owner;
        this.subjectLabel = debugLabel;
    }

    @Override
    public Lwjgl3GLDevice owner() {
        return device;
    }

    @Override
    public boolean deleted() {
        return deleted;
    }

    @Override
    public String subjectLabel() {
        return subjectLabel;
    }

    boolean materialized() {
        return glName >= 0;
    }

    int glName() {
        return glName;
    }

    void materialize(int glName) {
        this.glName = glName;
    }

    void relabel(String label) {
        this.subjectLabel = label;
    }

    void markDeleted() {
        this.deleted = true;
        this.glName = -1;
    }

    /** True when this is a depth or packed depth/stencil storage. */
    boolean isDepth() {
        return depthFormat != null;
    }

    /** Level-zero extent along one axis at the given mip level. */
    static int levelDimension(int base, int level) {
        return Math.max(1, base >> level);
    }
}
