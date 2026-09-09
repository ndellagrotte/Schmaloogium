// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Package-private generation token bound to one real-directory key. */
final class DiscoveryGenerationToken implements DiscoveryGeneration {

    final Object domain;
    final String directoryKey;   // real-path identity, or null for an invalid generation
    final long completionSequence;

    DiscoveryGenerationToken(Object domain, String directoryKey, long completionSequence) {
        this.domain = domain;
        this.directoryKey = directoryKey;
        this.completionSequence = completionSequence;
    }

    boolean invalid() {
        return directoryKey == null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DiscoveryGenerationToken other
            && this.domain == other.domain
            && this.completionSequence == other.completionSequence;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(System.identityHashCode(domain), completionSequence);
    }

    @Override
    public String toString() {
        return "DiscoveryGeneration[" + completionSequence + "]";
    }
}
