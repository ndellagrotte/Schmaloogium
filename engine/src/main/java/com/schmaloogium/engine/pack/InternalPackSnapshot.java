// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.util.List;

public record InternalPackSnapshot(List<InternalPackEntry> entries) {

    public InternalPackSnapshot {
        entries = List.copyOf(java.util.Objects.requireNonNull(entries, "entries"));
    }
}
