// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.GlobalShaderOptionsCodecImpl;
import com.schmaloogium.engine.config.IdMappingParserImpl;
import com.schmaloogium.engine.config.OptionPersistenceCodecImpl;

/** Builds the one bundle of Phase 3 services over shared live domain state. */
final class PackFrontEndServicesProvider {

    private PackFrontEndServicesProvider() {
    }

    static PackFrontEndServices create() {
        PackFrontEndDomain domain = new PackFrontEndDomain();
        PackFrontEnd frontEnd = new PackFrontEndImpl(domain);
        return new PackFrontEndServices(
            frontEnd,
            new OptionPersistenceCodecImpl(domain),
            new GlobalShaderOptionsCodecImpl(),
            new IdMappingParserImpl());
    }
}
