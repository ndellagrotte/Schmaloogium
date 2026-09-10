// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Declaration family (§2.3). Only {@code UNIFORM} designates an upload root; variables are
 * typed memoized intermediates that never submit (Appendix F.6, D-P11-27). */
public enum DeclarationKind { UNIFORM, VARIABLE }
