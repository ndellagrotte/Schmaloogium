// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;

/**
 * An authenticated BORROWED DEPTH handle (PHASE_1_DOC §4.7.3, [D-P1-40]): issued by this
 * backend from a live ordinary foreign texture it recognizes. Legal only at
 * {@code bindToUnit}, {@code DebugService.label}, {@code attachDepth} and
 * {@code attachDepthStencil}; the combined-form attachment additionally requires
 * authenticated metadata proving {@code DEPTH24_STENCIL8}. At v0.1 the issuer cannot
 * prove the packed format (the main-depth bridge is Phase 5's), so
 * {@code packedDepthStencil} is false for every handle this backend issues.
 */
final class Lwjgl3BorrowedDepth implements BorrowedDepthAttachmentHandle, Lwjgl3Handle {

    private final Lwjgl3GLDevice device;
    private final Lwjgl3ForeignTexture platform;
    private final boolean packedDepthStencil;

    Lwjgl3BorrowedDepth(Lwjgl3GLDevice owner, Lwjgl3ForeignTexture platform, boolean packedDepthStencil) {
        this.device = owner;
        this.platform = platform;
        this.packedDepthStencil = packedDepthStencil;
    }

    @Override
    public Lwjgl3GLDevice owner() {
        return device;
    }

    @Override
    public boolean deleted() {
        return false; // borrowed objects are never lifetime-managed by this backend
    }

    @Override
    public String subjectLabel() {
        return platform.subjectLabel();
    }

    boolean provesPackedDepthStencil() {
        return packedDepthStencil;
    }

    java.util.Optional<Integer> resolve() {
        return platform.resolve();
    }
}
