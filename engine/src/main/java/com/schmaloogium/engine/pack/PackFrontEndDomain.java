// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hidden shared domain of one bundle: discovery snapshots (completion-LRU), pack keys,
 * and per-candidate credentials. Everything the sealed tokens authenticate lives here.
 */
final class PackFrontEndDomain {

    /** Completion-LRU of retained directory snapshots. */
    final Deque<DiscoveryGenerationToken> retained = new ArrayDeque<>();
    final DiscoveryLimits limits = new DiscoveryLimits(64, 64L * 1024L * 1024L, 8);
    private final Map<DiscoveryGenerationToken, DiscoveryIndex.Snapshot> indices
        = new LinkedHashMap<>();
    private long completionSequence;

    synchronized long nextCompletionSequence() {
        return ++completionSequence;
    }

    synchronized void retain(DiscoveryGenerationToken token) {
        retained.remove(token);
        retained.addLast(token);
        while (retained.size() > limits.maxRetainedDirectorySnapshots()) {
            DiscoveryGenerationToken evicted = retained.removeFirst();
            indices.remove(evicted);
        }
    }

    synchronized void retainIndex(DiscoveryGenerationToken token,
            Map<String, PackCandidateIdToken> byReference, java.util.List<DiscoveryIndex.Entry> entries) {
        indices.put(token, new DiscoveryIndex.Snapshot(byReference, entries));
        // refresh LRU position
        retained.remove(token);
        retained.addLast(token);
    }

    synchronized DiscoveryIndex.Snapshot index(DiscoveryGenerationToken token) {
        return indices.get(token);
    }
}
