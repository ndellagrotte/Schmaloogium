// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Typed upload sink: one submission per successful uniform in declaration order (§4.8). */
public interface CustomUniformUploadSink {
    CustomSubmitResult submit(CustomUploadCommand command);
}
