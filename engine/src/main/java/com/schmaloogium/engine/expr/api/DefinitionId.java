// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Opaque stable per-definition identity assigned at plan build (§2.3). */
public record DefinitionId(long value) {}
