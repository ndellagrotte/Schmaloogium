// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.CompanionOptionMacros;

/** Typed companion option-macro pair; required non-null on every non-Off load. */
public record CompanionOptionMacros(boolean normalMap, boolean specularMap) {
}
