// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.type;

import com.schmaloogium.engine.expr.parse.Ast;

/** Conservative constant folder. Folds only literal arithmetic that cannot error or
 * observe effects ({@code + - *}, unary sign, {@code pi}), so folding can never hide an
 * unknown name, a division error, or an effect from an unselected lazy branch (§4.5).
 * Used for smooth's constant-id detection (§4.7). */
public final class ConstantFolder {

    /** Documented decimal 3.1415926 as binary32, bits 0x40490fda (§3.1). */
    public static final float PI = Float.intBitsToFloat(0x40490fda);

    private ConstantFolder() {}

    /** @return the folded binary32 value, or null when the tree is not foldable. */
    public static Float fold(Ast node) {
        if (node instanceof Ast.Num num) {
            return Float.isFinite(num.value()) ? num.value() : null;
        }
        if (node instanceof Ast.Ident ident && ident.name().equals("pi")) {
            return PI;
        }
        if (node instanceof Ast.Unary unary) {
            Float value = fold(unary.operand());
            if (value == null) {
                return null;
            }
            return switch (unary.op()) {
                case POS -> value;
                case NEG -> -value;
                case NOT -> null;
            };
        }
        if (node instanceof Ast.Bin bin) {
            Float left = fold(bin.left());
            Float right = fold(bin.right());
            if (left == null || right == null) {
                return null;
            }
            return switch (bin.op()) {
                case ADD -> left + right;
                case SUB -> left - right;
                case MUL -> left * right;
                default -> null; // division/remainder and booleans never fold
            };
        }
        return null;
    }

    /** True when {@code value} is a whole number representable as a signed 32-bit int. */
    public static boolean integral(Float value) {
        return value != null && Float.isFinite(value)
                && value == Math.rint(value)
                && value >= -2147483648.0f && value < 2147483648.0f;
    }
}
