// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.frame.ShadowFrameView;

/**
 * The headless world seam (PHASE_8_DOC §4.3): the implementation keeps no field and the
 * adapter exposes no extras. All rejections are typed; backend failures follow §6.
 */
public interface ShadowWorldPort {

    /** Reads the current world snapshot for the given shadow frame. */
    ShadowWorldSample sample(ShadowFrameView frame);

    /**
     * Opens the reversible §4.4 state lease: forced-third-person state, shadow
     * viewport/camera installation (the sfb extent is the viewport) and their
     * restoration credentials.
     */
    ShadowStateResult openState(ShadowCameraProjection camera, ShadowWorldSample sample,
            com.schmaloogium.engine.buffers.Extent2i shadowExtent);
    ShadowDrawResult setupTerrain(ShadowTraversalView traversal);

    /** §4.8.1 terrain band draw on the installed shadow camera. */
    ShadowDrawResult drawTerrain(ShadowTerrainBand band, ShadowTraversalView traversal);

    /** §4.8.2 configured-cloud draw; a failure disables clouds for the publication. */
    ShadowDrawResult drawClouds(ShadowTraversalView traversal);

    /** §4.8.2 entity draw in the given pass flavor. */
    ShadowDrawResult drawEntities(ShadowEntityPass pass, ShadowTraversalView traversal);

    /** Typed failure reasons. */
    enum Failure {
        STATE_CAPTURE, TERRAIN_SETUP, DRAW, REBUILD_ASSERTION
    }

    /** Entity draw flavors. */
    enum ShadowEntityPass {
        OPAQUE_ZERO, TRANSLUCENT_ONE
    }

    /** §4.8.1 terrain draw order. */
    enum ShadowTerrainBand {
        SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT
    }

    /** The traversal context for draws: the plan plus the synthesized frustum. */
    interface ShadowTraversalView {

        ShadowTraversalPlan plan();

        ShadowFrustum frustum();
    }

    /** Typed §4.4 open outcome. */
    sealed interface ShadowStateResult {

        record Opened(ShadowStateLease lease) implements ShadowStateResult {

            public Opened {
                if (lease == null) {
                    throw new IllegalArgumentException("lease");
                }
            }
        }

        record Rejected(Failure reason) implements ShadowStateResult {

            public Rejected {
                if (reason == null) {
                    throw new IllegalArgumentException("reason");
                }
            }
        }

        record Failed(Failure reason, String diagnosticId) implements ShadowStateResult {

            public Failed {
                if (reason == null) {
                    throw new IllegalArgumentException("reason");
                }
                if (diagnosticId == null) {
                    throw new IllegalArgumentException("diagnosticId");
                }
            }
        }
    }
}
