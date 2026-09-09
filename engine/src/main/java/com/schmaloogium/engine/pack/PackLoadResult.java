// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public sealed interface PackLoadResult {
    record Off() implements PackLoadResult {}
    record Loaded(PackConfiguration configuration) implements PackLoadResult {}
    record Failed(PackLoadFailure failure) implements PackLoadResult {}
}
