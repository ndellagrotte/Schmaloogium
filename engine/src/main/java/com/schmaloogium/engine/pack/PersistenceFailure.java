// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public record PersistenceFailure(PersistenceFailureCode code, String detail) {

    public PersistenceFailure {
        java.util.Objects.requireNonNull(code, "code");
        java.util.Objects.requireNonNull(detail, "detail");
    }
}
