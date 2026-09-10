// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;

/**
 * Live shadow-estate protocol view and sole issuer of open pass snapshots (§4.10).
 */
public interface ShadowEstateView {

    long estateGeneration();

    ShadowBeginResult beginPass(
            long frameId, PassDescriptor pass, ProgramBindingSelection selection);

    ShadowOperationResult bind(ShadowPassSnapshot snapshot);

    ShadowOperationResult clear(ShadowPassSnapshot snapshot, ClearRequest request);

    ShadowOperationResult copyDepth(ShadowPassSnapshot snapshot, ShadowDepthCopyPoint point);

    TextureBindingResult shadowBindings(
            long generation, long frameId, ShadowPassSnapshot snapshot,
            TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay);

    ShadowMipmapResult generateShadowMipmaps(
            long generation, long frameId, ShadowPassSnapshot snapshot,
            ShadowMipmapPolicy policy);

    ShadowNeutralizationResult degradeToNeutral(long generation, ShadowNeutralReason reason);

    ShadowCompletionResult completePass(ShadowPassSnapshot snapshot);

    ShadowAbortResult abortPass(ShadowPassSnapshot snapshot, String diagnosticId);
}
