// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

/**
 * What classified a reload request. Diagnostic only: the latest cause wins in a merge
 * and can never erase an effect field or weaken a lifecycle (§4.7.4).
 */
public enum ReloadCause {
    KEYBIND, COMMAND, RESOURCE_RELOAD, PACK_SELECTION,
    OPTION_APPLY, OPTION_RESET, ENGINE_SETTING, PROFILE_APPLY
}
