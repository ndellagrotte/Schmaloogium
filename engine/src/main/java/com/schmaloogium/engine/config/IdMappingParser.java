// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.IdMappingParser;

/** The one pure pack/mod ID-mapping parse operation. */
public interface IdMappingParser {

    IdMappingFileInput parse(IdMappingParseRequest request);
}
