// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.ImmutableBytes;

public sealed interface PersistenceFileAccess permits PersistenceFileAccessValue {

    PersistenceReadSource read(PersistenceTarget target);

    PersistenceWriteReceipt writeAtomically(PersistenceTarget target, ImmutableBytes content);
}
