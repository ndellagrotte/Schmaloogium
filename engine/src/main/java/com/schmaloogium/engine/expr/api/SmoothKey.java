// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Opaque plan-wide smooth cell identity: explicit ids map directly; automatic keys derive
 * from plan language version, declaration ordinal, and AST preorder site — never a runtime
 * object hash (§4.7, D-P11-12). */
public record SmoothKey(long value) {}
