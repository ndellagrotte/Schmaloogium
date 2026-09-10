// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The typed always-present event sink (PHASE_6_DOC §2.2/§4.12, D-P6-8). Constructed with
 * the runtime, usable across non-terminal resets, and every method is safe when no
 * program declares the consumer. Hook owners call these typed methods; they never assign
 * callbacks into Phase 6. After retirement every method throws
 * {@code IllegalStateException} before cell mutation, provider access or upload.
 *
 * <p>Every publication lands in the §4.12 notifier-to-producer audit table; signal events
 * are synchronous — a hook observes a value and writes it before the draw activation that
 * consumes it.
 */
public interface UniformEventSink {

    /** Phase 7's post-camera setup hook, exactly once per frame (D-P6-19). */
    void captureGbufferMatrices(long frameId, Matrix4Value modelView, Matrix4Value projection);

    /** Celestial rotation (Phase 8 values at v0.2); immediate upload if a token is current. */
    void updateCelestial(CelestialSample sample);

    /** Shadow-camera signal (Phase 8, v0.2); cells only, no {@code previous*} contract. */
    void updateShadowMatrices(ShadowMatrixSample sample);

    /** Fog mutation sites plus frame fallback; immediate upload if active. */
    void updateFog(FogSample sample);

    /** Effective blend observation (D-P6-32); immediate upload if active. */
    void updateBlend(BlendSample sample);

    /** P7-owned hurt/flash tint; immediate upload, neutral outside scope. */
    void updateEntityColor(Float4 value);

    /** Phase 9 scoped entity ID (v0.3); immediate upload, 0 restoration. */
    void updateEntityId(int value);

    /** Phase 9 scoped block-entity ID (v0.3); immediate upload, 0 restoration. */
    void updateBlockEntityId(int value);

    /** Phase 7 draw-loop ID 0…N−1 (v0.5); immediate upload before each prepared draw. */
    void updateInstanceId(int value);

    /** Authenticated atlas bind through the D-P6-20 adapter (v0.5); immediate upload. */
    void updateAtlasSize(Int2 value);

    /** Phase 9 held-item values (v0.3); replaces the value for its logical tick. */
    void updateHeldItems(HeldItemSample value);
}
