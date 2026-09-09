// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Owned-texture lifecycle and data (PHASE_1_DOC §4.7.4, §4.7.7, §4.7.7a). Since D-P1-71,
 * {@link #create} materializes only the authenticated logical handle and retains its
 * label; the first admitted {@link #allocate} materializes the exact target and applies
 * the label — deleting an unmaterialized handle retires it without a native delete.
 *
 * <p>Texture-accepting verbs require an owned, live, same-device/context texture on the
 * render thread. Ordinary foreign values ({@link ForeignTextureProvider}) and
 * authenticated {@link BorrowedDepthAttachmentHandle}s are legal ONLY at
 * {@link #bindToUnit} and {@link DebugService#label} — every other verb rejects them
 * before GL ([D-P1-40]).
 */
public interface TextureService {

    /** Logical creation: retains the label, chooses no target, creates no native texture
     *  (D-P1-71). */
    TextureHandle create(String debugLabel);

    /** First admitted call materializes the exact {@code spec.target()} after preflight,
     *  then applies the retained label. Defines storage, not contents (D-P1-63). */
    void allocate(TextureHandle t, TextureSpec spec);

    /** Applies the complete parameter value (D-P1-52); object baseline on every success
     *  (D-P1-64). */
    void setParameters(TextureHandle t, TextureParameters p);

    /** Upload texels into an allocated texture: the whole image or a sub-region, one mip
     *  level. A JDK {@link java.nio.ByteBuffer} of texels — a raw LWJGL buffer type is
     *  forbidden in {@code :engine} (C-1). What gets uploaded, in what format, and to
     *  which unit is Phase 13/5 policy. */
    void upload(TextureHandle t, TextureData data);

    /** Sampler normalization (D-P1-60): accepts exactly a 16-bit occupied-unit mask (bit
     *  n denotes fixed unit n) and clears native sampler bindings on unoccupied units
     *  0-15. Binds no textures and changes no active unit. */
    void prepareUnitBindings(int occupiedUnitMask);

    void bindToUnit(int unit, TextureHandle t);

    /** Caller checks {@link GLCapabilityProfile#supportsMipmapGeneration()} (RESEARCH.md
     *  §4.1: "mipmap gen requires GL 3.0"). */
    void generateMipmap(TextureHandle t);

    void delete(TextureHandle t);
}
