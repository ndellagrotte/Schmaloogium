// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Closed fixed-input kind domain distinguishing scalar, vector width/component kind, and
 * mat4 (§4.4). Integer-vector members promote to float when read (§4.3). */
public enum FixedInputKind { FLOAT, INT, VEC2, VEC3, VEC4, IVEC2, IVEC3, IVEC4, MAT4 }
