// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Display width/height pair (PHASE_5_DOC §2.2). Deliberately performs NO constructor validation:
 * positive display dimensions remain a sizing precondition enforced where extents are consumed.
 */
public record Extent2i(int width, int height) {
}
