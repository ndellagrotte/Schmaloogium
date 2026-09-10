// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Per-frame clear inputs: frame id, fog RGB, and full-clear flag (PHASE_5_DOC §2.2). */
public record ClearRequest(long frameId, float fogRed, float fogGreen, float fogBlue, boolean fullClear) {
}
