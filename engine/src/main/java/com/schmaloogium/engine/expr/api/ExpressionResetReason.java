// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Closed reset reasons (§4.12). Every nonterminal reason permits later activation;
 * {@code CLOSE} is terminal. Smooth state and memo generations reset on every reason. */
public enum ExpressionResetReason {
    PACK_REPLACEMENT, SHADERS_OFF, WORLD_EPOCH, FRAMEBUFFER_RESIZE,
    GL_CONTEXT_LOSS, CLOSE
}
