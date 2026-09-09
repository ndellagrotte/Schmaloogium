// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Package-private sealed token; identity equality, nonserializable. */
final class PackCandidateIdToken implements PackCandidateId {

    final Object domain;
    final Object generation;
    final String debugName;

    PackCandidateIdToken(Object domain, Object generation, String debugName) {
        this.domain = domain;
        this.generation = generation;
        this.debugName = java.util.Objects.requireNonNull(debugName, "debugName");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PackCandidateIdToken other
            && this.domain == other.domain
            && this.generation == other.generation
            && this.debugName.equals(other.debugName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(System.identityHashCode(domain),
            System.identityHashCode(generation), debugName);
    }

    @Override
    public String toString() {
        return "PackCandidateId[" + debugName + "]";
    }
}
