// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.ImmutableBytes;

public sealed interface PersistenceReadSource
        permits PersistenceReadSource.Absent, PersistenceReadSource.Present,
                PersistenceReadSource.Failed {

    record Absent() implements PersistenceReadSource {}

    record Present(ImmutableBytes bytes) implements PersistenceReadSource {

        public Present {
            java.util.Objects.requireNonNull(bytes, "bytes");
        }
    }

    record Failed(PersistenceFailure failure) implements PersistenceReadSource {

        public Failed {
            java.util.Objects.requireNonNull(failure, "failure");
        }
    }
}
