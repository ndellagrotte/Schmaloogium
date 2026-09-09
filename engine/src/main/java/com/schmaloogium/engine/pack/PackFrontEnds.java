// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Dependency-free public acquisition of the Phase 3 service bundle. */
public final class PackFrontEnds {

    private PackFrontEnds() {
    }

    public static PackFrontEndServices create() {
        return PackFrontEndServicesProvider.create();
    }
}
