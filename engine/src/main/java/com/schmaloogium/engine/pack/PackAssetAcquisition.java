// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;

public sealed interface PackAssetAcquisition {
    record Acquired(PackAssetBytes bytes) implements PackAssetAcquisition {}
    record Unreadable(NormalizedPackPath path) implements PackAssetAcquisition {}
    record Missing(NormalizedPackPath path) implements PackAssetAcquisition {}
    record InvalidReference(PackAssetReferenceFailure reason) implements PackAssetAcquisition {}
}
