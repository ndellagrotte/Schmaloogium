// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.IdMappingParser;

public final class PackFrontEndServices {

    private final PackFrontEnd frontEnd;
    private final OptionPersistenceCodec optionPersistence;
    private final GlobalShaderOptionsCodec globalOptions;
    private final IdMappingParser idMappings;

    PackFrontEndServices(PackFrontEnd frontEnd, OptionPersistenceCodec optionPersistence,
        GlobalShaderOptionsCodec globalOptions, IdMappingParser idMappings) {
        this.frontEnd = java.util.Objects.requireNonNull(frontEnd, "frontEnd");
        this.optionPersistence = java.util.Objects.requireNonNull(optionPersistence, "optionPersistence");
        this.globalOptions = java.util.Objects.requireNonNull(globalOptions, "globalOptions");
        this.idMappings = java.util.Objects.requireNonNull(idMappings, "idMappings");
    }

    public PackFrontEnd frontEnd() {
        return frontEnd;
    }

    public OptionPersistenceCodec optionPersistence() {
        return optionPersistence;
    }

    public GlobalShaderOptionsCodec globalOptions() {
        return globalOptions;
    }

    public IdMappingParser idMappings() {
        return idMappings;
    }

    public PersistenceFileAccessAcquisition persistenceFiles(PersistenceRootConfiguration roots) {
        return PersistenceFileAccessProvider.acquire(this, roots);
    }
}
