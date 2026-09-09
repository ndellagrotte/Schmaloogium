// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The immutable per-pass context view (PHASE_4_DOC §4.10). Ordinary public interface, but
 * only Phase 4 mints implementations a barrier or publisher accepts; no public operation
 * accepts a caller-supplied frame number, source identity, epoch, or kind.
 */
public interface BarrierContext {

    boolean shadowPass();

    StageId stage();

    StageBand band();
}
