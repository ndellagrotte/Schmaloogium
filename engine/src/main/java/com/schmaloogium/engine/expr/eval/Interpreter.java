// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.api.BuiltInLookup;
import com.schmaloogium.engine.expr.api.BuiltInValue;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.expr.parse.Ast;
import com.schmaloogium.engine.expr.plan.Program;
import com.schmaloogium.engine.expr.state.SmoothAdvance;
import com.schmaloogium.engine.expr.type.FunctionSymbol;
import com.schmaloogium.engine.expr.type.TypedNode;

/** The typed-AST interpreter backend (§4.6/§4.7). Strict binary32 at every AST operation:
 * each result is explicitly narrowed to {@code float} and checked with
 * {@link Float#isFinite}. Boolean {@code &&}, {@code ||}, and {@code if} are lazy and
 * skip unselected effects and errors; {@code min}/{@code max} visit every argument
 * exactly once; random consumption is left-to-right and never rewound. */
final class Interpreter {

    private static final float PI = (float) Math.PI;

    private Interpreter() {}

    /** Evaluates {@code node} into a fresh top cell and returns its index. */
    static int eval(TypedNode node, Frame fr) throws ExprEvalException, ProviderProtocolException {
        fr.nodeEvaluations++;
        if (node instanceof TypedNode.BoolConst b) {
            int cell = fr.cells.push(Cells.KBOOL);
            fr.cells.setBoolAt(cell, b.value());
            return cell;
        }
        if (node instanceof TypedNode.Const c) {
            int cell = fr.cells.push(Cells.KFLOAT);
            fr.cells.setFloatAt(cell, 0, c.value());
            return cell;
        }
        if (node instanceof TypedNode.ContextFloat cf) {
            float v = switch (cf.symbol()) {
                case BIOME -> (float) fr.context.biomeId();
                case TEMPERATURE -> fr.context.temperature();
                case RAINFALL -> fr.context.rainfall();
            };
            requireFinite(v, "context value");
            int cell = fr.cells.push(Cells.KFLOAT);
            fr.cells.setFloatAt(cell, 0, v);
            return cell;
        }
        if (node instanceof TypedNode.ContextBool cb) {
            int cell = fr.cells.push(Cells.KBOOL);
            fr.cells.setBoolAt(cell, flag(fr.context.viewEntityFlags(), cb.symbol()));
            return cell;
        }
        if (node instanceof TypedNode.InputRef ref) {
            resolveInput(ref.inputOrdinal(), fr);
            return pushInput(ref, fr);
        }
        if (node instanceof TypedNode.DefinitionRef ref) {
            return definition(ref, fr);
        }
        if (node instanceof TypedNode.Component comp) {
            int base = eval(comp.base(), fr);
            float v = laneOf(base, comp.component(), fr);
            fr.cells.pop();
            int cell = fr.cells.push(Cells.KFLOAT);
            fr.cells.setFloatAt(cell, 0, v);
            return cell;
        }
        if (node instanceof TypedNode.MatrixCell mc) {
            resolveInput(mc.inputOrdinal(), fr);
            requireFinite(fr.memo.inputM[16 * mc.inputOrdinal() + mc.row() * 4 + mc.col()], "matrix element");
            int cell = fr.cells.push(Cells.KFLOAT);
            fr.cells.setFloatAt(cell, 0, fr.memo.inputM[16 * mc.inputOrdinal() + mc.row() * 4 + mc.col()]);
            return cell;
        }
        if (node instanceof TypedNode.Unary unary) {
            return unary(unary, fr);
        }
        if (node instanceof TypedNode.Binary bin) {
            return binary(bin, fr);
        }
        if (node instanceof TypedNode.Call call) {
            return call(call, fr);
        }
        if (node instanceof TypedNode.If cond) {
            return ifExpression(cond, fr);
        }
        if (node instanceof TypedNode.Smooth smooth) {
            return smooth(smooth, fr);
        }
        if (node instanceof TypedNode.Random) {
            return random(fr);
        }
        throw new ProviderProtocolException("backend invariant: unknown node " + node.getClass());
    }

    private static boolean flag(com.schmaloogium.engine.expr.api.ViewEntityFlags flags,
                                TypedNode.ContextFlag symbol) {
        return switch (symbol) {
            case IS_ALIVE -> flags.isAlive();
            case IS_BURNING -> flags.isBurning();
            case IS_CHILD -> flags.isChild();
            case IS_GLOWING -> flags.isGlowing();
            case IS_HURT -> flags.isHurt();
            case IS_IN_LAVA -> flags.isInLava();
            case IS_IN_WATER -> flags.isInWater();
            case IS_INVISIBLE -> flags.isInvisible();
            case IS_ON_GROUND -> flags.isOnGround();
            case IS_RIDDEN -> flags.isRidden();
            case IS_RIDING -> flags.isRiding();
            case IS_SNEAKING -> flags.isSneaking();
            case IS_SPRINTING -> flags.isSprinting();
            case IS_WET -> flags.isWet();
        };
    }

    private static int pushInput(TypedNode.InputRef ref, Frame fr) {
        int ordinal = ref.inputOrdinal();
        MemoTable memo = fr.memo;
        return switch (ref.shape()) {
            case FLOAT -> {
                int cell = fr.cells.push(Cells.KFLOAT);
                fr.cells.setFloatAt(cell, 0, memo.inputF[4 * ordinal]);
                yield cell;
            }
            case INT -> {
                // fixed integer inputs promote to float when read (§4.1)
                int cell = fr.cells.push(Cells.KFLOAT);
                fr.cells.setFloatAt(cell, 0, (float) memo.inputI[4 * ordinal]);
                yield cell;
            }
            case FVEC2 -> pushVector(fr, memo.inputF, 4 * ordinal, Cells.KVEC2);
            case FVEC3 -> pushVector(fr, memo.inputF, 4 * ordinal, Cells.KVEC3);
            case FVEC4 -> pushVector(fr, memo.inputF, 4 * ordinal, Cells.KVEC4);
            case IVEC2 -> {
                float[] lanes = intLanes(memo, ordinal, 2);
                yield pushVector(fr, lanes, 0, Cells.KVEC2);
            }
            case IVEC3 -> {
                float[] lanes = intLanes(memo, ordinal, 3);
                yield pushVector(fr, lanes, 0, Cells.KVEC3);
            }
            case IVEC4 -> {
                float[] lanes = intLanes(memo, ordinal, 4);
                yield pushVector(fr, lanes, 0, Cells.KVEC4);
            }
        };
    }

    private static float[] intLanes(MemoTable memo, int ordinal, int width) {
        float[] lanes = new float[width];
        for (int i = 0; i < width; i++) {
            lanes[i] = (float) memo.inputI[4 * ordinal + i];
        }
        return lanes;
    }

    private static int pushVector(Frame fr, float[] lanes, int offset, byte kind) {
        int cell = fr.cells.push(kind);
        for (int i = 0; i < 4; i++) {
            fr.cells.setFloatAt(cell, i, i < widthOf(kind) ? lanes[offset + i] : 0.0f);
        }
        return cell;
    }

    private static int widthOf(byte kind) {
        return switch (kind) {
            case Cells.KVEC2 -> 2;
            case Cells.KVEC3 -> 3;
            case Cells.KVEC4 -> 4;
            default -> 1;
        };
    }

    private static void resolveInput(int ordinal, Frame fr) throws ExprEvalException, ProviderProtocolException {
        MemoTable memo = fr.memo;
        if (memo.inputEpoch[ordinal] == fr.epoch && memo.inputStatus[ordinal] != MemoTable.INPUT_UNKNOWN) {
            if (memo.inputStatus[ordinal] == MemoTable.INPUT_ABSENT) {
                throw new ExprEvalException(ExpressionDiagnosticKind.INPUT_ABSENT,
                        "fixed input '" + fr.program.inputNames[ordinal] + "' is absent");
            }
            return;
        }
        BuiltInLookup lookup = fr.view.lookup(fr.program.inputNames[ordinal]);
        if (lookup instanceof BuiltInLookup.Absent) {
            memo.inputStatus[ordinal] = MemoTable.INPUT_ABSENT;
            memo.inputEpoch[ordinal] = fr.epoch;
            throw new ExprEvalException(ExpressionDiagnosticKind.INPUT_ABSENT,
                    "fixed input '" + fr.program.inputNames[ordinal] + "' is absent");
        }
        BuiltInValue value = ((BuiltInLookup.Present) lookup).value();
        storeInput(ordinal, value, fr);
        memo.inputStatus[ordinal] = MemoTable.INPUT_PRESENT;
        memo.inputEpoch[ordinal] = fr.epoch;
    }

    /** Schema/value agreement (§4.4): a disagreement is an invariant diagnostic for
     * definitions reaching the slot. */
    private static void storeInput(int ordinal, BuiltInValue value, Frame fr) throws ExprEvalException {
        FixedInputKind kind = fr.program.inputKinds[ordinal];
        MemoTable memo = fr.memo;
        switch (kind) {
            case FLOAT -> {
                if (!(value instanceof BuiltInValue.Float1 f)) {
                    throw mismatch(kind, value);
                }
                memo.inputF[4 * ordinal] = f.value();
            }
            case INT -> {
                if (!(value instanceof BuiltInValue.Int1 i)) {
                    throw mismatch(kind, value);
                }
                memo.inputI[4 * ordinal] = i.value();
            }
            case VEC2 -> {
                if (!(value instanceof BuiltInValue.Float2 v)) {
                    throw mismatch(kind, value);
                }
                memo.inputF[4 * ordinal] = v.x();
                memo.inputF[4 * ordinal + 1] = v.y();
            }
            case VEC3 -> {
                if (!(value instanceof BuiltInValue.Float3 v)) {
                    throw mismatch(kind, value);
                }
                memo.inputF[4 * ordinal] = v.x();
                memo.inputF[4 * ordinal + 1] = v.y();
                memo.inputF[4 * ordinal + 2] = v.z();
            }
            case VEC4 -> {
                if (!(value instanceof BuiltInValue.Float4 v)) {
                    throw mismatch(kind, value);
                }
                memo.inputF[4 * ordinal] = v.x();
                memo.inputF[4 * ordinal + 1] = v.y();
                memo.inputF[4 * ordinal + 2] = v.z();
                memo.inputF[4 * ordinal + 3] = v.w();
            }
            case IVEC2 -> {
                if (!(value instanceof BuiltInValue.Int2 v)) {
                    throw mismatch(kind, value);
                }
                memo.inputI[4 * ordinal] = v.x();
                memo.inputI[4 * ordinal + 1] = v.y();
            }
            case IVEC3 -> {
                if (!(value instanceof BuiltInValue.Int3 v)) {
                    throw mismatch(kind, value);
                }
                memo.inputI[4 * ordinal] = v.x();
                memo.inputI[4 * ordinal + 1] = v.y();
                memo.inputI[4 * ordinal + 2] = v.z();
            }
            case IVEC4 -> {
                if (!(value instanceof BuiltInValue.Int4 v)) {
                    throw mismatch(kind, value);
                }
                memo.inputI[4 * ordinal] = v.x();
                memo.inputI[4 * ordinal + 1] = v.y();
                memo.inputI[4 * ordinal + 2] = v.z();
                memo.inputI[4 * ordinal + 3] = v.w();
            }
            case MAT4 -> {
                if (!(value instanceof BuiltInValue.Mat4 m)) {
                    throw mismatch(kind, value);
                }
                System.arraycopy(m.rowMajor(), 0, memo.inputM, 16 * ordinal, 16);
            }
        }
    }

    private static ExprEvalException mismatch(FixedInputKind kind, BuiltInValue value) {
        return new ExprEvalException(ExpressionDiagnosticKind.INPUT_SCHEMA_MISMATCH,
                "fixed input expects " + kind + " but the view supplied " + value.getClass().getSimpleName());
    }

    private static int definition(TypedNode.DefinitionRef ref, Frame fr)
            throws ExprEvalException, ProviderProtocolException {
        int slot = ref.slot();
        MemoTable memo = fr.memo;
        if (memo.slotEpoch[slot] == fr.epoch) {
            if (memo.slotStatus[slot] == MemoTable.SLOT_VALUE) {
                if (fr.program.kinds[slot] == com.schmaloogium.engine.expr.api.DeclarationKind.VARIABLE) {
                    fr.variableMemoHits++;
                }
                return pushDefinitionValue(slot, fr);
            }
            if (memo.slotStatus[slot] == MemoTable.SLOT_EVALUATING) {
                throw new ProviderProtocolException("backend invariant: dependency cycle escaped plan analysis");
            }
            if (memo.slotStatus[slot] == MemoTable.SLOT_ERROR) {
                throw new ProviderProtocolException("backend invariant: read of disabled slot");
            }
        }
        if (fr.program.kinds[slot] == com.schmaloogium.engine.expr.api.DeclarationKind.VARIABLE) {
            fr.variableMemoMisses++;
        }
        fr.evaluator.evaluate(slot, fr);
        return pushDefinitionValue(slot, fr);
    }

    private static int pushDefinitionValue(int slot, Frame fr) throws ExprEvalException {
        ExpressionType type = fr.program.types[slot];
        MemoTable memo = fr.memo;
        switch (type) {
            case FLOAT -> {
                int cell = fr.cells.push(Cells.KFLOAT);
                fr.cells.setFloatAt(cell, 0, memo.slotF[4 * slot]);
                return cell;
            }
            case INT -> {
                int cell = fr.cells.push(Cells.KFLOAT);
                fr.cells.setFloatAt(cell, 0, (float) memo.slotI[4 * slot]);
                return cell;
            }
            case BOOL -> {
                int cell = fr.cells.push(Cells.KBOOL);
                fr.cells.setBoolAt(cell, memo.slotB[slot]);
                return cell;
            }
            case VEC2 -> {
                return pushStoredVector(fr, slot, Cells.KVEC2);
            }
            case VEC3 -> {
                return pushStoredVector(fr, slot, Cells.KVEC3);
            }
            case VEC4 -> {
                return pushStoredVector(fr, slot, Cells.KVEC4);
            }
        }
        throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT, "unknown declared type");
    }

    private static int pushStoredVector(Frame fr, int slot, byte kind) {
        int cell = fr.cells.push(kind);
        for (int i = 0; i < 4; i++) {
            fr.cells.setFloatAt(cell, i, fr.memo.slotF[4 * slot + i]);
        }
        return cell;
    }

    private static int unary(TypedNode.Unary unary, Frame fr)
            throws ExprEvalException, ProviderProtocolException {
        if (unary.op() == Ast.UnaryOp.NOT) {
            int operand = eval(unary.operand(), fr);
            boolean v = !fr.cells.boolAt(operand);
            fr.cells.pop();
            int cell = fr.cells.push(Cells.KBOOL);
            fr.cells.setBoolAt(cell, v);
            return cell;
        }
        int operand = eval(unary.operand(), fr);
        float v = numericScalar(operand, fr);
        fr.cells.pop();
        if (unary.op() == Ast.UnaryOp.NEG) {
            v = -v;
        }
        requireFinite(v, "unary result");
        int cell = fr.cells.push(Cells.KFLOAT);
        fr.cells.setFloatAt(cell, 0, v);
        return cell;
    }

    private static int binary(TypedNode.Binary bin, Frame fr)
            throws ExprEvalException, ProviderProtocolException {
        Ast.BinaryOp op = bin.op();
        if (op == Ast.BinaryOp.OR) {
            boolean left = boolOf(eval(bin.left(), fr), fr);
            boolean result = left || boolOf(eval(bin.right(), fr), fr);
            fr.cells.pop();
            fr.cells.pop();
            int cell = fr.cells.push(Cells.KBOOL);
            fr.cells.setBoolAt(cell, result);
            return cell;
        }
        if (op == Ast.BinaryOp.AND) {
            boolean left = boolOf(eval(bin.left(), fr), fr);
            boolean result = left && boolOf(eval(bin.right(), fr), fr);
            fr.cells.pop();
            fr.cells.pop();
            int cell = fr.cells.push(Cells.KBOOL);
            fr.cells.setBoolAt(cell, result);
            return cell;
        }
        int left = eval(bin.left(), fr);
        int right = eval(bin.right(), fr);
        boolean boolResult = false;
        float result = 0.0f;
        switch (op) {
            case EQ, NE -> {
                boolean eq;
                if (fr.cells.kind(left) == Cells.KBOOL || fr.cells.kind(right) == Cells.KBOOL) {
                    eq = fr.cells.boolAt(left) == fr.cells.boolAt(right);
                } else {
                    eq = numericScalar(left, fr) == numericScalar(right, fr);
                }
                boolResult = op == Ast.BinaryOp.EQ ? eq : !eq;
            }
            case GT -> boolResult = numericScalar(left, fr) > numericScalar(right, fr);
            case GE -> boolResult = numericScalar(left, fr) >= numericScalar(right, fr);
            case LT -> boolResult = numericScalar(left, fr) < numericScalar(right, fr);
            case LE -> boolResult = numericScalar(left, fr) <= numericScalar(right, fr);
            case ADD -> result = checked(numericScalar(left, fr) + numericScalar(right, fr));
            case SUB -> result = checked(numericScalar(left, fr) - numericScalar(right, fr));
            case MUL -> result = checked(numericScalar(left, fr) * numericScalar(right, fr));
            case DIV -> {
                float divisor = numericScalar(right, fr);
                if (divisor == 0.0f) {
                    throw new ExprEvalException(ExpressionDiagnosticKind.DIVIDE_BY_ZERO, "division by zero");
                }
                result = checked(numericScalar(left, fr) / divisor);
            }
            case MOD -> {
                float divisor = numericScalar(right, fr);
                if (divisor == 0.0f) {
                    throw new ExprEvalException(ExpressionDiagnosticKind.DIVIDE_BY_ZERO, "modulo by zero");
                }
                float quotient = numericScalar(left, fr) / divisor;
                float truncated = (float) (quotient < 0.0f
                        ? Math.ceil((double) quotient)
                        : Math.floor((double) quotient));
                result = checked(numericScalar(left, fr) - truncated * divisor);
            }
            default -> throw new ProviderProtocolException("backend invariant: unhandled operator " + op);
        }
        fr.cells.pop();
        fr.cells.pop();
        boolean comparison = switch (op) {
            case EQ, NE, GT, GE, LT, LE -> true;
            default -> false;
        };
        if (comparison) {
            int cell = fr.cells.push(Cells.KBOOL);
            fr.cells.setBoolAt(cell, boolResult);
            return cell;
        }
        int cell = fr.cells.push(Cells.KFLOAT);
        fr.cells.setFloatAt(cell, 0, result);
        return cell;
    }

    private static int ifExpression(TypedNode.If cond, Frame fr)
            throws ExprEvalException, ProviderProtocolException {
        for (TypedNode.IfBranch branch : cond.branches()) {
            int condition = eval(branch.condition(), fr);
            boolean taken = fr.cells.boolAt(condition);
            fr.cells.pop();
            if (taken) {
                return eval(branch.value(), fr);
            }
        }
        if (cond.otherwise() != null) {
            return eval(cond.otherwise(), fr);
        }
        throw new ProviderProtocolException("backend invariant: if chain without terminal else");
    }

    private static int smooth(TypedNode.Smooth smooth, Frame fr)
            throws ExprEvalException, ProviderProtocolException {
        Program.Site[] sites = fr.program.smoothSites[fr.ownerSlot];
        if (sites == null || smooth.siteIndex() >= sites.length) {
            throw new ProviderProtocolException("backend invariant: smooth site missing from plan");
        }
        int cellIndex = sites[smooth.siteIndex()].cellIndex();
        float target = numericScalar(eval(smooth.target(), fr), fr);
        fr.cells.pop();
        float fadeIn = 1.0f;
        if (smooth.fadeIn() != null) {
            fadeIn = numericScalar(eval(smooth.fadeIn(), fr), fr);
            fr.cells.pop();
        }
        float fadeOut = smooth.fadeOut() != null
                ? numericScalar(eval(smooth.fadeOut(), fr), fr)
                : fadeIn;
        if (smooth.fadeOut() != null) {
            fr.cells.pop();
        }
        SmoothAdvance advance = fr.smooth.advance(cellIndex, target, fadeIn, fadeOut, fr.nowSeconds);
        if (advance.failed()) {
            throw new ExprEvalException(
                    "NON_FINITE".equals(advance.errorKind())
                            ? ExpressionDiagnosticKind.NON_FINITE
                            : ExpressionDiagnosticKind.DOMAIN,
                    advance.errorMessage());
        }
        int cell = fr.cells.push(Cells.KFLOAT);
        fr.cells.setFloatAt(cell, 0, advance.value());
        return cell;
    }

    private static int random(Frame fr) throws ProviderProtocolException {
        float value = fr.random.nextFloat();
        if (!Float.isFinite(value) || value < 0.0f || value >= 1.0f) {
            throw new ProviderProtocolException("random source returned a value outside [0,1)");
        }
        fr.randomSamples++;
        int cell = fr.cells.push(Cells.KFLOAT);
        fr.cells.setFloatAt(cell, 0, value);
        return cell;
    }

    private static int call(TypedNode.Call call, Frame fr)
            throws ExprEvalException, ProviderProtocolException {
        FunctionSymbol function = call.function();
        switch (function) {
            case IF, SMOOTH -> throw new ProviderProtocolException(
                    "backend invariant: special form reached the interpreter");
            case RANDOM -> {
                return random(fr);
            }
            default -> { }
        }
        int[] argCells = new int[call.args().size()];
        for (int i = 0; i < argCells.length; i++) {
            argCells[i] = eval(call.args().get(i), fr);
        }
        int cell;
        try {
            cell = apply(function, argCells, fr);
        } finally {
            for (int i = argCells.length - 1; i >= 0; i--) {
                fr.cells.pop();
            }
        }
        return cell;
    }

    private static int apply(FunctionSymbol function, int[] args, Frame fr)
            throws ExprEvalException {
        Cells cells = fr.cells;
        switch (function) {
            case SIN -> {
                return floatResult((float) Math.sin(scalar(args[0], fr)), fr);
            }
            case COS -> {
                return floatResult((float) Math.cos(scalar(args[0], fr)), fr);
            }
            case TAN -> {
                float v = (float) Math.tan(scalar(args[0], fr));
                requireFinite(v, "tan result");
                return floatResult(v, fr);
            }
            case ASIN -> {
                float x = scalar(args[0], fr);
                requireDomain(x >= -1.0f && x <= 1.0f, "asin domain");
                return floatResult((float) Math.asin(x), fr);
            }
            case ACOS -> {
                float x = scalar(args[0], fr);
                requireDomain(x >= -1.0f && x <= 1.0f, "acos domain");
                return floatResult((float) Math.acos(x), fr);
            }
            case ATAN -> {
                return floatResult((float) Math.atan(scalar(args[0], fr)), fr);
            }
            case ATAN2 -> {
                float v = (float) Math.atan2(scalar(args[0], fr), scalar(args[1], fr));
                requireFinite(v, "atan2 result");
                return floatResult(v, fr);
            }
            case TORAD -> {
                float x = scalar(args[0], fr);
                return floatResult(checked(x * PI / 180.0f), fr);
            }
            case TODEG -> {
                float x = scalar(args[0], fr);
                return floatResult(checked(x * 180.0f / PI), fr);
            }
            case MIN, MAX -> {
                boolean isMin = function == FunctionSymbol.MIN;
                float aggregate = scalar(args[0], fr);
                for (int i = 1; i < args.length; i++) {
                    float candidate = scalar(args[i], fr);
                    aggregate = isMin ? Math.min(aggregate, candidate) : Math.max(aggregate, candidate);
                }
                return floatResult(aggregate, fr);
            }
            case CLAMP -> {
                float x = scalar(args[0], fr);
                float low = scalar(args[1], fr);
                float high = scalar(args[2], fr);
                requireDomain(low <= high, "clamp bounds");
                return floatResult(Math.max(low, Math.min(x, high)), fr);
            }
            case ABS -> {
                float v = Math.abs(scalar(args[0], fr));
                requireFinite(v, "abs result");
                return floatResult(v, fr);
            }
            case FLOOR -> {
                return floatResult((float) Math.floor((double) scalar(args[0], fr)), fr);
            }
            case CEIL -> {
                return floatResult((float) Math.ceil((double) scalar(args[0], fr)), fr);
            }
            case EXP -> {
                float v = (float) Math.exp((double) scalar(args[0], fr));
                requireFinite(v, "exp result");
                return floatResult(v, fr);
            }
            case FRAC -> {
                float x = scalar(args[0], fr);
                return floatResult(checked(x - (float) Math.floor((double) x)), fr);
            }
            case LOG -> {
                float x = scalar(args[0], fr);
                requireDomain(x > 0.0f, "log domain");
                return floatResult((float) Math.log((double) x), fr);
            }
            case POW -> {
                float v = (float) Math.pow((double) scalar(args[0], fr), (double) scalar(args[1], fr));
                requireFinite(v, "pow result");
                return floatResult(v, fr);
            }
            case ROUND -> {
                float x = scalar(args[0], fr);
                return floatResult((float) Math.floor((double) (x + 0.5f)), fr);
            }
            case SIGNUM -> {
                return floatResult(Math.signum(scalar(args[0], fr)), fr);
            }
            case SQRT -> {
                float x = scalar(args[0], fr);
                requireDomain(x >= 0.0f, "sqrt domain");
                return floatResult((float) Math.sqrt((double) x), fr);
            }
            case FMOD -> {
                float x = scalar(args[0], fr);
                float y = scalar(args[1], fr);
                if (y == 0.0f) {
                    throw new ExprEvalException(ExpressionDiagnosticKind.DIVIDE_BY_ZERO, "fmod by zero");
                }
                return floatResult(checked(x - (float) Math.floor((double) (x / y)) * y), fr);
            }
            case BETWEEN -> {
                float x = scalar(args[0], fr);
                float low = scalar(args[1], fr);
                float high = scalar(args[2], fr);
                requireDomain(low <= high, "between bounds");
                return boolResult(x >= low && x <= high, fr);
            }
            case EQUALS -> {
                float x = scalar(args[0], fr);
                float y = scalar(args[1], fr);
                float eps = scalar(args[2], fr);
                requireDomain(Float.isFinite(eps) && eps >= 0.0f, "equals epsilon");
                return boolResult(Math.abs(x - y) <= eps, fr);
            }
            case IN -> {
                if (cells.kind(args[0]) == Cells.KBOOL) {
                    boolean x = cells.boolAt(args[0]);
                    for (int i = 1; i < args.length; i++) {
                        if (cells.kind(args[i]) != Cells.KBOOL) {
                            throw new ExprEvalException(ExpressionDiagnosticKind.TYPE,
                                    "in candidates must share the bool type");
                        }
                        if (cells.boolAt(args[i]) == x) {
                            return boolResult(true, fr);
                        }
                    }
                    return boolResult(false, fr);
                }
                float x = scalar(args[0], fr);
                for (int i = 1; i < args.length; i++) {
                    if (scalar(args[i], fr) == x) {
                        return boolResult(true, fr);
                    }
                }
                return boolResult(false, fr);
            }
            case VEC2, VEC3, VEC4 -> {
                int width = function == FunctionSymbol.VEC2 ? 2 : function == FunctionSymbol.VEC3 ? 3 : 4;
                float x = scalar(args[0], fr);
                float y = scalar(args[1], fr);
                float z = width > 2 ? scalar(args[2], fr) : 0.0f;
                float w = width > 3 ? scalar(args[3], fr) : 0.0f;
                requireFinite(x, "vector component");
                requireFinite(y, "vector component");
                if (width > 2) {
                    requireFinite(z, "vector component");
                }
                if (width > 3) {
                    requireFinite(w, "vector component");
                }
                byte kind = width == 2 ? Cells.KVEC2 : width == 3 ? Cells.KVEC3 : Cells.KVEC4;
                int cell = cells.push(kind);
                cells.setFloatAt(cell, 0, x);
                cells.setFloatAt(cell, 1, y);
                cells.setFloatAt(cell, 2, z);
                cells.setFloatAt(cell, 3, w);
                return cell;
            }
            default -> throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                    "unhandled function " + function);
        }
    }

    private static int floatResult(float value, Frame fr) {
        int cell = fr.cells.push(Cells.KFLOAT);
        fr.cells.setFloatAt(cell, 0, value);
        return cell;
    }

    private static int boolResult(boolean value, Frame fr) {
        int cell = fr.cells.push(Cells.KBOOL);
        fr.cells.setBoolAt(cell, value);
        return cell;
    }

    private static float scalar(int cell, Frame fr) throws ExprEvalException {
        if (fr.cells.kind(cell) != Cells.KFLOAT) {
            throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                    "numeric scalar expected");
        }
        return fr.cells.floatAt(cell, 0);
    }

    static float numericScalar(int cell, Frame fr) throws ExprEvalException {
        return scalar(cell, fr);
    }

    private static boolean boolOf(int cell, Frame fr) throws ExprEvalException {
        if (fr.cells.kind(cell) != Cells.KBOOL) {
            throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT, "boolean expected");
        }
        return fr.cells.boolAt(cell);
    }

    private static float laneOf(int base, int component, Frame fr) throws ExprEvalException {
        int width = fr.cells.vectorWidth(fr.cells.kind(base));
        if (width < 0 || component >= width) {
            throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                    "member base is not a vector of sufficient width");
        }
        return fr.cells.floatAt(base, component);
    }

    private static float checked(float value) throws ExprEvalException {
        requireFinite(value, "arithmetic result");
        return value;
    }

    private static void requireFinite(float value, String what) throws ExprEvalException {
        if (!Float.isFinite(value)) {
            throw new ExprEvalException(ExpressionDiagnosticKind.NON_FINITE, what + " is not finite");
        }
    }

    private static void requireDomain(boolean holds, String what) throws ExprEvalException {
        if (!holds) {
            throw new ExprEvalException(ExpressionDiagnosticKind.DOMAIN, what + " violated");
        }
    }
}
