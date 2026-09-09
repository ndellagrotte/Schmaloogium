// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.pack.NormalizedPackPath;

/** The sole public path projection: NFC, root-relative, slash-separated, validated. */
public record NormalizedPackPath(String canonicalString)
        implements Comparable<NormalizedPackPath> {

    /** Canonical order: unsigned lexicographic UTF-8 byte order of {@link #canonicalString()}. */
    public static final java.util.Comparator<NormalizedPackPath> ORDER =
        (a, b) -> EngineOptionData.compareUnsignedUtf8(a.canonicalString, b.canonicalString);

    public NormalizedPackPath {
        NormalizedPackPaths.validate(canonicalString);
    }

    @Override
    public int compareTo(NormalizedPackPath o) {
        return ORDER.compare(this, o);
    }
}
