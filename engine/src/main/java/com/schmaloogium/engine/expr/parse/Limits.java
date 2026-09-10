// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.parse;

/** Parser limits (§4.2): hitting one is that declaration's load error, never a client
 * crash. 16 KiB UTF-8-equivalent text, 4,096 tokens, 128 nesting levels, 256 call
 * arguments, 2,048 AST nodes per declaration. */
public final class Limits {

    public static final int MAX_TEXT_BYTES = 16 * 1024;
    public static final int MAX_TOKENS = 4096;
    public static final int MAX_DEPTH = 128;
    public static final int MAX_CALL_ARGS = 256;
    public static final int MAX_AST_NODES = 2048;

    private Limits() {}
}
