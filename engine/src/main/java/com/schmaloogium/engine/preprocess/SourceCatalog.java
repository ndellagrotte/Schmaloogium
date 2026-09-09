// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.config.ProgramKey;

import java.util.List;
import java.util.Set;
import java.util.Optional;

/** Immutable source snapshot: documents, roots, include graph, and the materializer. */
public interface SourceCatalog {

    List<SourceDocument> sources();

    List<SourceKey> roots();

    Set<ProgramKey> executablePrograms();

    Optional<SourceDocument> source(SourceId id);

    List<IncludeEdge> includeEdges();

    SourceMaterializer materializer();
}
