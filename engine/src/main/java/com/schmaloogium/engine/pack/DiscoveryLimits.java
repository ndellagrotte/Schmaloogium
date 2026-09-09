// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Published finite discovery-retention policy. */
public record DiscoveryLimits(
        int maxCandidatesPerSnapshot,
        long maxSnapshotBytes,
        int maxRetainedDirectorySnapshots) {

    public DiscoveryLimits {
        if (maxCandidatesPerSnapshot < 2) {
            throw new IllegalArgumentException("maxCandidatesPerSnapshot must admit both sentinels");
        }
        if (maxSnapshotBytes <= 0) {
            throw new IllegalArgumentException("maxSnapshotBytes must be positive");
        }
        if (maxRetainedDirectorySnapshots <= 0) {
            throw new IllegalArgumentException("maxRetainedDirectorySnapshots must be positive");
        }
        if (maxSnapshotBytes < OverflowSnapshot.ENCODED_SIZE) {
            throw new IllegalArgumentException(
                "maxSnapshotBytes must admit the fixed two-sentinel overflow snapshot");
        }
    }
}
