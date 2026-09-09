// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed bind-result vocabulary (PHASE_1_DOC §4.7.6): a live opaque {@link Bound}
 * binding, a mutation-free {@link Rejected} verdict, or {@link Failed} after partial
 * mutation (diagnostic id carried; the backend has already restored the saved predecessor
 * and, on failed rollback, invalidated input-stack admission so Phase 7 takes
 * shaders-off containment).
 */
public sealed interface VertexBindResult {

    /** A live binding; the sole argument to {@link VertexInputService#restore}. */
    record Bound(VertexBinding binding) implements VertexBindResult {
    }

    /** Mutation-free pre-mutation rejection; no native call appended. */
    record Rejected(VertexBindRejection reason) implements VertexBindResult {
    }

    /** Setup failure after mutation; {@code diagnosticId} names it for the diagnostics. */
    record Failed(String diagnosticId) implements VertexBindResult {
    }
}
