// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Opaque engine-issued Boolean switch expression; consumers never inspect it. */
public sealed interface ProgramEnabledExpression permits ProgramEnabledExpressionValue {

    /** Exact captured expression bytes for the inspection TextHash projection. */
    byte[] capturedExpressionBytes();
}
