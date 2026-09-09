// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Closed pack/mod ID-mapping rule algebra. */
public sealed interface MappingRule permits IdRule, LayerRule {

    SelectorKind selectorKind();

    String selectorToken();

    MappingEra era();

    MappingOrigin origin();

    int sourceLine();

    int selectorOrdinal();
}
