// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * The sole prepublication owner (PHASE_9_DOC §2.2/§4.1). A candidate holds the resolved
 * primitive tables, conflict/diagnostic summaries and complete identity. Publication
 * transfers it exactly once; failed or rejected publications leave it caller-owned.
 * Closing before publication discards it; closing after transfer is a no-op because the
 * publisher owns retirement.
 */
public interface IdRuntimeCandidate extends AutoCloseable {

    /** Operation-free inspection of the resolved tables and identity. */
    IdRuntimeView view();

    @Override
    void close();
}
