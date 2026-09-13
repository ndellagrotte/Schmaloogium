// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The sole hook-facing engine surface (PHASE_7_DOC §5.1): the vanilla Mixin/event adapter
 * (and any future Kirino producer) calls exactly these nine methods, on the render thread,
 * in the doc's exact order. All results are closed algebras; nothing here leaks engine
 * internals or Minecraft types.
 */
public interface FrameHookSink {

    FrameOpenResult open(FrameBeginSignal signal);

    FrameStepResult beforeFirstClear(FrameToken token);

    FrameStepResult afterFirstClear(FrameToken token,
            com.schmaloogium.engine.buffers.MainDepthPreparation depth);

    FrameStepResult captureMainCamera(FrameToken token, CameraSnapshot camera);

    FrameStepResult afterTerrainSetup(FrameToken token);

    /**
     * H-FRAME-05 with the shadow frame inputs (camera position, sky and sun angle; the
     * frame id is re-stamped by the driver): when the composition carries a shadow slot the
     * driver invokes it here (PHASE_7_DOC D-P7-76: after the main clear, before any terrain
     * draw). The one-argument form is the v0.1 shape and skips the slot.
     */
    default FrameStepResult afterTerrainSetup(FrameToken token, ShadowFrameView shadowFrame) {
        return afterTerrainSetup(token);
    }

    ScopeOpenResult enter(FrameToken token,
            com.schmaloogium.engine.frame.dispatch.RenderSection section);

    ScopeCloseResult exit(FrameToken token, ScopeToken scope);

    FrameFinishResult finish(FrameToken token, FrameExitKind exitKind);

    FrameAbortResult abort(FrameToken token, FrameAbortReason reason);
}
