// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The opaque activity epoch (PHASE_4_DOC §4.10). Only this token may be retained to
 * authorize Phase 6's cached immediate uploads between activations. {@code isCurrent()} is a
 * pure thread-safe epoch comparison with no GL query or side effect; a stale token never
 * becomes current again and confers no operation.
 */
public interface BoundProgramActivityToken {

    boolean isCurrent();
}
