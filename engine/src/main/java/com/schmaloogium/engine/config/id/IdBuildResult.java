// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/** Closed build outcome with built/failed ownership (PHASE_9_DOC §2.2). */
public sealed interface IdBuildResult {

    /** The candidate is caller-owned until a successful publication transfers it once. */
    record Built(IdRuntimeCandidate candidate) implements IdBuildResult {
    }

    /** No candidate exists; the old publication is unchanged and the reporter was fed. */
    record Failed(IdBuildFailure failure) implements IdBuildResult {
    }
}
