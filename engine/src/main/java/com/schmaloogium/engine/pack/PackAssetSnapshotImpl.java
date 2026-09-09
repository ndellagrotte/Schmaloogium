// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackIdentity;

import java.util.List;

/** Same-load declared-domain asset snapshot (D-P3-69). */
final class PackAssetSnapshotImpl implements PackAssetSnapshot {

    private final PackIdentity pack;
    private final java.util.Map<NormalizedPackPath, PackAssetRow> rows;

    PackAssetSnapshotImpl(PackIdentity pack, java.util.Map<NormalizedPackPath, PackAssetRow> rows) {
        this.pack = java.util.Objects.requireNonNull(pack, "pack");
        this.rows = java.util.Map.copyOf(rows);
    }

    java.util.Map<NormalizedPackPath, PackAssetRow> rows() {
        return rows;
    }

    @Override
    public PackIdentity pack() {
        return pack;
    }

    @Override
    public List<PackAssetMetadata> manifest() {
        return rows.values().stream().map(PackAssetRow::metadata)
            .sorted(java.util.Comparator.comparing(PackAssetMetadata::path,
                NormalizedPackPath.ORDER))
            .toList();
    }

    @Override
    public PackAssetAcquisition acquire(NormalizedPackPath path) {
        if (path == null) {
            return new PackAssetAcquisition.InvalidReference(PackAssetReferenceFailure.NULL_PATH);
        }
        PackAssetRow row = rows.get(path);
        if (row == null) {
            return new PackAssetAcquisition.InvalidReference(PackAssetReferenceFailure.NOT_DECLARED);
        }
        return row.acquire(path);
    }
}
