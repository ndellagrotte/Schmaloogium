// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.ImmutableBytes;
import com.schmaloogium.engine.pack.NormalizedPackPath;

public sealed interface InternalPackEntry {

    NormalizedPackPath path();

    record File(NormalizedPackPath path, ImmutableBytes bytes) implements InternalPackEntry {

        public File {
            java.util.Objects.requireNonNull(path, "path");
            java.util.Objects.requireNonNull(bytes, "bytes");
        }
    }

    record Directory(NormalizedPackPath path) implements InternalPackEntry {

        public Directory {
            java.util.Objects.requireNonNull(path, "path");
        }
    }
}
