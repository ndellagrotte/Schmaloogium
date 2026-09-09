// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.List;

/**
 * Framebuffer objects, attachments, draw routes and the depth/pixel-transfer verbs
 * (PHASE_1_DOC §4.7.4, §4.7.4b). Every framebuffer argument must be a live handle created
 * by the receiving device; texture arguments are classified before GL ([D-P1-40]): an
 * owned texture of this device, an ordinary foreign value (legal at NONE of these verbs),
 * or an authenticated same-device {@link BorrowedDepthAttachmentHandle} — legal ONLY at
 * {@link #attachDepth} and {@link #attachDepthStencil}. Wrong-device, wrong-context,
 * forged, deleted and otherwise unknown values are rejected with
 * {@code IllegalArgumentException} before any binding or attachment mutation.
 */
public interface FramebufferService {

    FramebufferHandle create(String debugLabel);

    /** Wraps a live, same-device ordinary foreign platform texture into an opaque,
     *  non-owned borrowed-depth handle. Rejection is pre-GL. */
    BorrowedDepthAttachmentHandle borrowDepthAttachment(TextureHandle platformTexture);

    void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t);

    /** Replaces f's depth attachment and leaves f with no stencil attachment; restores
     *  prior read/draw bindings before return ([D-P1-40]). */
    void attachDepth(FramebufferHandle f, TextureHandle t);

    /** Replaces both depth and stencil attachments with the SAME DEPTH24_STENCIL8
     *  texture; the borrowed form additionally requires authenticated metadata proving
     *  the combined format. Restores prior read/draw bindings before return. */
    void attachDepthStencil(FramebufferHandle f, TextureHandle t);

    /** Positional output locations; None consumes a location but names no attachment.
     *  Empty means no writes, never preserve-current; no raw sentinel is accepted.
     *  Draw-route validation happens before GL ([D-P1-57] route rules). */
    void drawBuffers(FramebufferHandle f, List<FramebufferDrawSlot> slots);

    FramebufferStatus check(FramebufferHandle f);

    void bind(FramebufferTarget target, FramebufferHandle f);

    /** Binds framebuffer NAME 0, the GL default framebuffer, on {@code target}. NOT
     *  "whatever the platform regards as its default target": Minecraft renders the world
     *  into a framebuffer object of its own, and no verb here binds that one (§4.7.4,
     *  §4.12). */
    void bindDefault(FramebufferTarget target);

    /** Framebuffer-to-framebuffer copy (Phase 5, the one owner). {@link BlitSpec} carries
     *  the rectangles, attachment mask and filter (depth requires NEAREST, enforced at
     *  construction). Restores the caller's prior draw and read bindings before
     *  returning. */
    void blit(FramebufferHandle src, FramebufferHandle dst, BlitSpec spec);

    /** First copy after destination creation/reallocation: redefines owned dst level zero
     *  to the region's extent using src's exact depth or packed depth/stencil internal
     *  format, then copies the region (glCopyTexImage2D semantics). Never legal for any
     *  foreign destination. Restores prior read/draw framebuffer and texture bindings. */
    void initializeDepthTextureFromFramebuffer(
            FramebufferHandle src, TextureHandle dst, TextureRegion region);

    /** STEADY COPY ONLY: dst must be an owned, live texture whose level zero is already
     *  defined with the same format and region extent; changes contents only, never
     *  storage. This — not blit — is the verb the depthtex1/depthtex2 copies need.
     *  Restores prior read/draw framebuffer and texture bindings. */
    void copyDepthToTexture(FramebufferHandle src, TextureHandle dst, TextureRegion region);

    /** Synchronous single-pixel depth readback — returns immediately usable data and
     *  therefore stalls the pipeline, faithful to reference behavior. WHICH pixel, at
     *  which moment, and any halflife smoothing are Phase 6 policy; this is only the
     *  verb. */
    float readDepthPixel(FramebufferHandle f, int x, int y);

    /** Mandatory typed color clear (D-P1-67/69): exact owned color attachment selected
     *  through its POSITIONAL draw-route index; clears its full extent and restores all
     *  private temporary state (§4.7.4b). */
    void clearColorAttachment(FramebufferHandle f, int drawBufferIndex, ColorClearValue value);

    void delete(FramebufferHandle f);
}
