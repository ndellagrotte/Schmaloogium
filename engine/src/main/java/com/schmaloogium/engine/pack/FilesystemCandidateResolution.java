// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public sealed interface FilesystemCandidateResolution {
    record Resolved(PackCandidateId candidate) implements FilesystemCandidateResolution {}
    record Missing() implements FilesystemCandidateResolution {}
    record Ambiguous() implements FilesystemCandidateResolution {}
    record KindChanged(PackCandidateKind currentKind) implements FilesystemCandidateResolution {}
    record InvalidSnapshot() implements FilesystemCandidateResolution {}
}
