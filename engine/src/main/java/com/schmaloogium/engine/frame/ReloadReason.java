// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

public enum ReloadReason {
    PACK_SELECTION,
    OPTION_CHANGE,
    RESOURCE_RELOAD,
    DIMENSION_CHANGE,
    RESOLUTION_MULTIPLIER,
    REGISTRY_REMAP,
    MOD_ID_SOURCE_CHANGE,
    TAG_OR_ALIAS_CATALOG_CHANGE,
    HAND_LIGHT_POLICY_CHANGE
}
