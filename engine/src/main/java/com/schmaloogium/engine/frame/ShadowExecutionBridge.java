// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The driver-owned one-credential shadow execution bridge (PHASE_7_DOC §5.1). Phase 7 is
 * the sole issuer and owner; exactly one non-nestable dynamic-extent view per installed
 * slot invocation; the caller closes in {@code finally} on every post-open exit and before
 * the main clear.
 */
public interface ShadowExecutionBridge {

    ShadowExecutionOpenResult open(
            ShadowExecutionIdentity activeExecutionIdentity, ShadowSlotEpoch slotEpoch);

    ShadowExecutionValidationResult validate(
            ShadowExecutionView view,
            ShadowExecutionIdentity activeExecutionIdentity,
            ShadowSlotEpoch slotEpoch);

    ShadowExecutionCloseResult close(ShadowExecutionView view);
}
