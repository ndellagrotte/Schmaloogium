// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackIdentity;

import java.util.List;

public sealed interface PackAssetSnapshot permits PackAssetSnapshotImpl {

    PackIdentity pack();

    List<PackAssetMetadata> manifest();

    PackAssetAcquisition acquire(NormalizedPackPath path);
}
