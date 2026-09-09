// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;
import java.util.Optional;

/** Exact canonical encoded size of the fixed two-sentinel overflow result. */
final class OverflowSnapshot {

    static final long ENCODED_SIZE = overflowBytes();

    private OverflowSnapshot() {
    }

    private static long overflowBytes() {
        EngineDiagnostic limit = DiagnosticCodec.discoveryLimitDiagnostic();
        long total = CanonicalBytes.sizeOf(DiagnosticCodec.encode(limit));
        total += CanonicalBytes.sizeOf(DiagnosticCodec.encodeCandidate(
            sentinels().get(0)));
        total += CanonicalBytes.sizeOf(DiagnosticCodec.encodeCandidate(
            sentinels().get(1)));
        return total;
    }

    /** The two sentinel candidates as the overflow result publishes them. */
    static List<PackCandidate> sentinels() {
        return List.of(
            new PackCandidate(null, Optional.empty(), PackCandidateKind.OFF, "OFF",
                PackCandidateStatus.AVAILABLE, List.of()),
            new PackCandidate(null, Optional.empty(), PackCandidateKind.INTERNAL, "(internal)",
                PackCandidateStatus.AVAILABLE, List.of()));
    }
}
