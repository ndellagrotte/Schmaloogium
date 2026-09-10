// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Closed conformance verdict (§5.6): {@code UNSUPPORTED} is never PASS. */
public enum ExpressionConformanceVerdict { PASS, FAIL, UNSUPPORTED }
