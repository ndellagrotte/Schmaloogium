// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed internal-session capture outcome. */
public sealed interface InternalOptionCaptureResult {
    record Captured(InternalOptionSnapshot snapshot) implements InternalOptionCaptureResult {}
    record Invalid(InternalOptionFailure failure) implements InternalOptionCaptureResult {}
}
