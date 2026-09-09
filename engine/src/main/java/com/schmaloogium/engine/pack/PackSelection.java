// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public sealed interface PackSelection
        permits PackSelection.Off, PackSelection.Internal, PackSelection.Filesystem {

    record Off() implements PackSelection {}

    record Internal() implements PackSelection {}

    record Filesystem(PackCandidateId candidate) implements PackSelection {}
}
