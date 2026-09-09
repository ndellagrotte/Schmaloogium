// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.util.Optional;

public record PersistenceWriteReceipt(
        PersistenceWriteStatus status,
        Optional<PersistenceFailure> failure,
        boolean atomicMoveUsed) {

    public PersistenceWriteReceipt {
        failure = failure == null ? Optional.empty() : failure;
    }
}
