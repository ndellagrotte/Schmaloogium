// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed source vocabulary for the bounded vertex input grant (PHASE_1_DOC §4.7.6).
 * The nested categories are {@code non-sealed} so the vanilla-owning {@code mod.glue.vertex}
 * adapter can issue same-device/context authenticated implementations from outside the
 * package; this does not add a fifth {@link GLHandle} category or grant marker
 * implementations trust — the backend authenticates its private issuer token and source
 * incarnation, and accepting an arbitrary marker implementation is forbidden.
 *
 * <p>These marker interfaces expose no GL names, native addresses or mutable range
 * getters, and no delete/upload/allocation/lifecycle privilege belongs to the input
 * service. Deletion, reallocation, builder reset, task cancellation or epoch retirement
 * invalidates issuance; a reused object name or equal public marker cannot resurrect it.
 */
public sealed interface VertexSource {

    /** A live JDK ByteBuffer plus byte offset/count/limit and task/epoch identity,
     *  without copying or owning memory. */
    non-sealed interface ClientRange extends VertexSource {
    }

    /** A vanilla-owned object incarnation, byte range, upload generation and epoch —
     *  not a public {@code adopt(int)} capability. */
    non-sealed interface BorrowedVbo extends VertexSource {
    }

    /** The corresponding vanilla-owned list incarnation, captured layout/primitive
     *  category and replay-safe state metadata. */
    non-sealed interface DisplayListReplay extends VertexSource {
    }
}
