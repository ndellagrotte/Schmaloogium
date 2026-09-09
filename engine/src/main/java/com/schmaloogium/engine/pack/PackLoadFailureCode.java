// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public enum PackLoadFailureCode {
    INVALID_REQUEST, INVALID_SELECTION, INPUT_UNREADABLE, INPUT_UNSAFE,
    INPUT_LIMIT_EXCEEDED, INTERNAL_SOURCE_INVALID, STRUCTURALLY_UNUSABLE,
    UNEXPECTED_INTERNAL
}
