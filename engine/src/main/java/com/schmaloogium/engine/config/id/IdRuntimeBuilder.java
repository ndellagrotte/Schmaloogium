// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingParser;

/**
 * The immutable merge/resolution entry point (PHASE_9_DOC §2.2). {@code build} performs
 * no publication, mutates no prior table, and never throws: every failure arrives as
 * {@code IdBuildResult.Failed}. The builder holds the fingerprint-scoped warn-once state,
 * so repeated builds over unchanged inputs do not repeat warnings.
 */
public interface IdRuntimeBuilder {

    /** Resolves one immutable candidate from the request inputs. */
    IdBuildResult build(IdBuildRequest request);

    /**
     * Creates a builder over the exact published Phase 3 parser. The parser is the sole
     * decoder of pack/mod mapping bytes; Phase 9 never reopens a pack or parses a
     * property line independently.
     */
    static IdRuntimeBuilder create(IdMappingParser parser) {
        return new IdRuntimeBuilderImpl(parser);
    }
}
