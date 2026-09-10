// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Closed preparation outcome for a main-depth safe-point prepare call. */
public sealed interface MainDepthPreparation {
    record Ready(MainDepthSnapshot.Available snapshot) implements MainDepthPreparation {
    }

    record Pending(long expectedVersion) implements MainDepthPreparation {
    }

    record Failed(String diagnosticId) implements MainDepthPreparation {
    }
}
