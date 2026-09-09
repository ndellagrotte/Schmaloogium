// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Sealed screen entry model (Appendix F.4). */
public sealed interface ScreenEntry
        permits ScreenOptionEntry, ScreenSubscreenEntry, ScreenProfileEntry,
                ScreenEmptyEntry, ScreenAllOptionsEntry {
}
