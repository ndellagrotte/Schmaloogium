// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Closed expression result domain (PHASE_11_DOC §2.3). {@code INT} is a declaration/storage
 * type only: all numeric evaluation happens in finite binary32 {@code FLOAT}. */
public enum ExpressionType { BOOL, INT, FLOAT, VEC2, VEC3, VEC4 }
