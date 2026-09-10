// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainDepthSource;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The depthtex0 bridge (PHASE_5_DOC main-depth seam; §4.9 borrowed destination): the
 * vanilla main framebuffer's depth attachment is lent to the engine as {@code depthtex0}.
 *
 * <p>{@code Framebuffer} mixins (H-FBO-01/02) feed {@link #observe} with the live
 * depth-attachment GL identity and extent; the P5 estate consumes {@link #prepare} at the
 * frame-begin safe point and {@link #current} on the render thread. The borrowed handle
 * is minted only by the installed P1 device's framebuffer service (the sole authenticated
 * issuer); until the renderer installs it, the bridge answers the truthful pending or
 * unavailable outcomes rather than forging a handle ([D-P1-40]).
 */
public final class DepthTex0Bridge implements MainDepthSource {

    private static final DepthTex0Bridge INSTANCE = new DepthTex0Bridge();

    /** Render-thread supplier of the installed device's framebuffer service. */
    private static volatile Supplier<Optional<BorrowedDepthAttachmentHandle>> borrowedSource =
            Optional::empty;

    private static volatile long observedTextureIdentity;
    private static volatile int observedWidth;
    private static volatile int observedHeight;
    private static volatile long versionSource;
    private static volatile Optional<BorrowedDepthAttachmentHandle> lastBorrowed = Optional.empty();

    private DepthTex0Bridge() {
    }

    public static DepthTex0Bridge get() {
        return INSTANCE;
    }

    /**
     * Installs the device-bound minting source. Called once by the renderer when the P1
     * device exists; the supplier must be render-thread confined and device-authentic.
     */
    public static void installBorrowedSource(
            Supplier<Optional<BorrowedDepthAttachmentHandle>> source) {
        borrowedSource = source == null ? Optional::empty : source;
    }

    /** H-FBO-01/02 observation: the vanilla depth attachment changed identity or extent. */
    public static void observe(long textureIdentity, int width, int height) {
        if (textureIdentity != observedTextureIdentity) {
            versionSource++;
            lastBorrowed = Optional.empty(); // the old borrowed handle is stale by identity
        } else if (width != observedWidth || height != observedHeight) {
            versionSource++;
        }
        observedTextureIdentity = textureIdentity;
        observedWidth = width;
        observedHeight = height;
    }

    @Override
    public MainDepthPreparation prepare(Extent2i requiredExtent) {
        MainDepthSnapshot.Available available = mintedSnapshot();
        if (available == null) {
            // Never observed, or no device minted a borrowed handle yet: P5 treats this
            // as a pending depth input rather than a hard failure.
            return new MainDepthPreparation.Pending(versionSource);
        }
        return new MainDepthPreparation.Ready(available);
    }

    @Override
    public MainDepthSnapshot current() {
        MainDepthSnapshot.Available available = mintedSnapshot();
        if (available == null) {
            return new MainDepthSnapshot.Unavailable(
                    versionSource, "schmaloogium.frame.depthtex0.unborrowed");
        }
        return available;
    }

    /** The snapshot only when observation AND device minting both succeeded. */
    private static MainDepthSnapshot.Available mintedSnapshot() {
        if (observedTextureIdentity == 0L || observedWidth <= 0 || observedHeight <= 0) {
            return null;
        }
        if (lastBorrowed.isEmpty()) {
            lastBorrowed = mintBorrowed();
        }
        return lastBorrowed.map(handle -> new MainDepthSnapshot.Available(
                versionSource, handle,
                DepthAttachmentFormat.DEPTH_COMPONENT,
                new Extent2i(observedWidth, observedHeight)))
                .orElse(null);
    }

    private static Optional<BorrowedDepthAttachmentHandle> mintBorrowed() {
        try {
            return borrowedSource.get();
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
