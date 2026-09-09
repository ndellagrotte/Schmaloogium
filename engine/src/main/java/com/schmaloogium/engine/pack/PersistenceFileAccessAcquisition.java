// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public sealed interface PersistenceFileAccessAcquisition {
    record Acquired(PersistenceFileAccess files) implements PersistenceFileAccessAcquisition {}
    record InvalidRoots(PersistenceFailure failure) implements PersistenceFileAccessAcquisition {}
}
