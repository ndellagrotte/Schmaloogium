// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One sparse-array pass index, legal exactly in 0…99 (PHASE_4_DOC §2.2). Index 0 uses the
 * unsuffixed family stem; positive indices append the decimal value without zero padding
 * (§4.3). Every array API and validation rule is expressed through this type and the
 * definition's limit — never through a fixed-size allocation.
 */
public record PassIndex(int value) {

    public PassIndex {
        if (value < 0 || value > 99) {
            throw new IllegalArgumentException("pass index outside 0..99: " + value);
        }
    }
}
