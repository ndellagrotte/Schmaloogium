// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.RegistryFingerprint;

/**
 * Non-owning accepted immutable estate identity; mutating operations are render-thread-only
 * and enforce it (PHASE_5_DOC §2.2).
 */
public interface BufferEstateView {

    long generation();

    RegistryFingerprint registryFingerprint();

    BufferSizing sizing();

    BufferInventory inventory();

    BufferResourceSnapshot.Available resources();

    MainDepthRefreshResult refreshMainDepth();

    FrameBeginResult beginFrame(long frameId);

    VirtualTransitionResult applyVirtualTransition(long frameId, PassDescriptor pass);

    DrawBuffersNoneOpenResult openDrawBuffersNone(long frameId);

    PassSnapshotResult snapshot(PassDescriptor pass, ProgramBindingSelection selection);

    MainMipmapResult generateMainMipmaps(PassBufferSnapshot snapshot);

    PassCompletionResult completePass(PassBufferSnapshot snapshot);

    PassDiscardResult discardPass(PassBufferSnapshot snapshot);

    ClearExecutionPlan clearPlan(ClearRequest request);

    ClearExecutionResult executeClear(ClearExecutionPlan plan);

    DepthCopyResult copyDepth(DepthCopyPoint point, long frameId);

    FrameEndResult commitFrame(long frameId);

    FrameEndResult abortFrame(long frameId, String diagnosticId);

    TextureBindingResult textureBindings(
            PassBufferSnapshot snapshot,
            TextureOverlayLease overlay,
            TextureOverlayPublicationId expectedOverlay);

    ShadowEstateResult shadow();
}
