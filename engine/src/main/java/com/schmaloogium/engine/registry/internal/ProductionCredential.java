// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

/** Proof that a participant bundle was minted by the production assembler. */
public record ProductionCredential(Credentials.Production inner) {

    static final ProductionCredential MINTED =
        new ProductionCredential(new Credentials.Production());
}
