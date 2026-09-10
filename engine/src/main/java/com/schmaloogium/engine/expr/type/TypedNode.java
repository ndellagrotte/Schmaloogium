// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.type;

import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.SourceSpan;
import com.schmaloogium.engine.expr.parse.Ast;
import java.util.List;

/** Typed, fully resolved expression tree (§4.3/§4.4). Vectors exist only at definition
 * boundaries and constructor results; every member/matrix read resolves to one FLOAT node,
 * so the interpreter computes scalars and booleans only. */
public sealed interface TypedNode {

    /** Result type; {@code INT} appears only for direct reads of {@code INT}-declared
     * definitions or fixed integer scalars (promoted to float when consumed). */
    ExpressionType type();

    SourceSpan span();

    /** Usable fixed-input shapes; MAT4 never forms a bare reference. */
    enum InputShape {
        FLOAT, INT, FVEC2, FVEC3, FVEC4, IVEC2, IVEC3, IVEC4;

        ExpressionType type() {
            return switch (this) {
                case FLOAT -> ExpressionType.FLOAT;
                case INT -> ExpressionType.INT;
                case FVEC2, IVEC2 -> ExpressionType.VEC2;
                case FVEC3, IVEC3 -> ExpressionType.VEC3;
                case FVEC4, IVEC4 -> ExpressionType.VEC4;
            };
        }
    }

    /** Numeric literal, {@code pi}, or a {@code BIOME_*} constant folded at build time. */
    record Const(float value, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.FLOAT;
        }
    }

    record BoolConst(boolean value, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.BOOL;
        }
    }

    enum ContextScalar { BIOME, TEMPERATURE, RAINFALL }

    record ContextFloat(ContextScalar symbol, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.FLOAT;
        }
    }

    enum ContextFlag {
        IS_ALIVE, IS_BURNING, IS_CHILD, IS_GLOWING, IS_HURT, IS_IN_LAVA, IS_IN_WATER,
        IS_INVISIBLE, IS_ON_GROUND, IS_RIDDEN, IS_RIDING, IS_SNEAKING, IS_SPRINTING, IS_WET
    }

    record ContextBool(ContextFlag symbol, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.BOOL;
        }
    }

    /** Direct read of a registered custom definition (either kind) by memo slot. */
    record DefinitionRef(int slot, ExpressionType type, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return type;
        }
    }

    /** Direct read of a fixed input; MAT4 is reachable only via {@link MatrixCell}. */
    record InputRef(int inputOrdinal, InputShape shape, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return shape.type();
        }
    }

    /** One mat4 cell via two literal indices. */
    record MatrixCell(int inputOrdinal, int row, int col, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.FLOAT;
        }
    }

    /** One component of a vector-valued expression; integer-vector members promote. */
    record Component(TypedNode base, int component, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.FLOAT;
        }
    }

    record Unary(Ast.UnaryOp op, TypedNode operand, SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return op == Ast.UnaryOp.NOT ? ExpressionType.BOOL : ExpressionType.FLOAT;
        }
    }

    record Binary(Ast.BinaryOp op, TypedNode left, TypedNode right, SourceSpan span)
            implements TypedNode {
        @Override
        public ExpressionType type() {
            return switch (op) {
                case OR, AND, EQ, NE, GT, GE, LT, LE -> ExpressionType.BOOL;
                default -> ExpressionType.FLOAT;
            };
        }
    }

    record Call(FunctionSymbol function, List<TypedNode> args, SourceSpan span)
            implements TypedNode {
        @Override
        public ExpressionType type() {
            return switch (function) {
                case BETWEEN, EQUALS, IN -> ExpressionType.BOOL;
                case VEC2 -> ExpressionType.VEC2;
                case VEC3 -> ExpressionType.VEC3;
                case VEC4 -> ExpressionType.VEC4;
                default -> ExpressionType.FLOAT;
            };
        }
    }

    record IfBranch(TypedNode condition, TypedNode value) {}

    /** Lazy conditional chain; unselected branches skip all effects and errors. */
    record If(List<IfBranch> branches, TypedNode otherwise, ExpressionType type, SourceSpan span)
            implements TypedNode {
        @Override
        public ExpressionType type() {
            return type;
        }
    }

    /** Stateful smooth site; fades may be null for the documented defaults (§4.7). */
    record Smooth(int siteIndex, TypedNode target, TypedNode fadeIn, TypedNode fadeOut,
                  SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.FLOAT;
        }
    }

    record Random(SourceSpan span) implements TypedNode {
        @Override
        public ExpressionType type() {
            return ExpressionType.FLOAT;
        }
    }

}
