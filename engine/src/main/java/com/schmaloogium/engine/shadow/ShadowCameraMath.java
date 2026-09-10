// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.ShadowFrameView;

/**
 * Pure §4.5 camera seam (PHASE_8_DOC §2.2): plan+policy+health derived, compute-only.
 * Nothing here is derived from viewport size, render distance or runtime GL; the sfb
 * extent influences nothing in the §4.5 math.
 */
public interface ShadowCameraMath {

    /**
     * §4.5.2: with snapping, via the delegating plan policy; celestial through
     * {@link CelestialMath#angles} (day boundary included); invalid/nonfinite input is
     * contained through the existing engine-failure path, never a synthesized camera.
     */
    ShadowCameraProjection compute(ShadowFrameView frame, CameraSnapshot camera,
            ShadowPlan plan, Extent2i shadowExtent);

    /** §4.5.3: MVP plane extraction, side-plane synthesis, silhouette capped at ten. */
    ShadowFrustum frustum(ShadowCameraProjection camera);

    /** The builder face over this math's {@link #frustum(ShadowCameraProjection)}. */
    ShadowFrustumBuilder frustumBuilder();

    /** The process-wide instance used by default wiring. */
    static ShadowCameraMath shared() {
        return Shared.INSTANCE;
    }

    /** Holder for the shared immutable instance. */
    final class Shared {
        private Shared() {
        }

        private static final ShadowCameraMath INSTANCE =
                new com.schmaloogium.engine.shadow.internal.ShadowCameraMathImpl();
    }
}
