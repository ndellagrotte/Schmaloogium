// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.type;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.expr.api.SourceSpan;
import com.schmaloogium.engine.expr.parse.Ast;
import java.util.ArrayList;
import java.util.List;

/** Static typing, overload selection, and smooth-shape analysis over the parsed tree
 * (§4.3, §4.6, §4.7). Names resolve in the fixed order of §4.4; every syntactic reference
 * resolves here — including lazy branches — so folding/graph work can never hide one. */
public final class TypeChecker {

    /** Resolution environment supplied by the plan compiler (§4.4). */
    public interface Environment {
        /** @return the exact float-representable id, or null. */
        Integer biomeConstant(String name);

        /** @return the context scalar/flag symbol, or null. */
        ContextName contextName(String name);

        /** @return the fixed-input ordinal, or null. */
        Integer fixedInput(String name);

        FixedInputKind fixedKind(int inputOrdinal);

        /** @return the definition memo slot, or null. */
        Integer definitionSlot(String name);

        ExpressionType definitionType(int slot);
    }

    /** Resolved context name (§4.4 tier 3). */
    public sealed interface ContextName {
        record Scalar(TypedNode.ContextScalar scalar) implements ContextName {}
        record Flag(TypedNode.ContextFlag flag) implements ContextName {}
    }

    /** Result of checking one declaration. */
    public record Checked(TypedNode root, List<SmoothSite> smoothSites) {}

    /** One smooth call site: explicit id or automatic key derivation inputs. */
    public record SmoothSite(int preorderIndex, Integer explicitId) {}

    private final Environment env;
    private final List<SmoothSite> sites = new ArrayList<>();
    private int siteCounter;
    private int preorder;

    private TypeChecker(Environment env) {
        this.env = env;
    }

    public static Checked check(Environment env, Ast root) throws SemanticException {
        TypeChecker checker = new TypeChecker(env);
        TypedNode typed = checker.check(root);
        return new Checked(typed, List.copyOf(checker.sites));
    }

    // dispatch ----------------------------------------------------------------

    private TypedNode check(Ast node) throws SemanticException {
        preorder++;
        if (node instanceof Ast.Num num) {
            if (!Float.isFinite(num.value())) {
                throw fail(ExpressionDiagnosticKind.NON_FINITE, num.span(), "numeric literal is not finite");
            }
            return new TypedNode.Const(num.value(), num.span());
        }
        if (node instanceof Ast.Ident ident) {
            return resolveIdent(ident);
        }
        if (node instanceof Ast.Unary unary) {
            return checkUnary(unary);
        }
        if (node instanceof Ast.Bin bin) {
            return checkBinary(bin);
        }
        if (node instanceof Ast.Member member) {
            return checkMember(member);
        }
        return checkCall((Ast.Call) node);
    }

    private TypedNode resolveIdent(Ast.Ident ident) throws SemanticException {
        String name = ident.name();
        switch (name) {
            case "pi" -> {
                return new TypedNode.Const(ConstantFolder.PI, ident.span());
            }
            case "true" -> {
                return new TypedNode.BoolConst(true, ident.span());
            }
            case "false" -> {
                return new TypedNode.BoolConst(false, ident.span());
            }
            default -> { }
        }
        Integer biome = env.biomeConstant(name);
        if (biome != null) {
            return new TypedNode.Const((float) biome, ident.span());
        }
        ContextName context = env.contextName(name);
        if (context instanceof ContextName.Scalar scalar) {
            return new TypedNode.ContextFloat(scalar.scalar(), ident.span());
        }
        if (context instanceof ContextName.Flag flag) {
            return new TypedNode.ContextBool(flag.flag(), ident.span());
        }
        Integer fixed = env.fixedInput(name);
        if (fixed != null) {
            return inputRef(fixed, ident.span());
        }
        Integer slot = env.definitionSlot(name);
        if (slot != null) {
            ExpressionType type = env.definitionType(slot);
            return new TypedNode.DefinitionRef(slot, type, ident.span());
        }
        throw fail(ExpressionDiagnosticKind.UNKNOWN_NAME, ident.span(), "unknown name: " + name);
    }

    private TypedNode inputRef(int ordinal, SourceSpan span) throws SemanticException {
        FixedInputKind kind = env.fixedKind(ordinal);
        // MAT4 is only reachable through the two-index member pattern; containers are
        // usable only through member access, so a bare reference is a type error.
        return switch (kind) {
            case FLOAT -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.FLOAT, span);
            case INT -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.INT, span);
            case VEC2 -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.FVEC2, span);
            case VEC3 -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.FVEC3, span);
            case VEC4 -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.FVEC4, span);
            case IVEC2 -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.IVEC2, span);
            case IVEC3 -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.IVEC3, span);
            case IVEC4 -> new TypedNode.InputRef(ordinal, TypedNode.InputShape.IVEC4, span);
            case MAT4 -> throw fail(ExpressionDiagnosticKind.TYPE, span,
                    "matrix access requires exactly two numeric indices");
        };
    }

    private TypedNode checkUnary(Ast.Unary unary) throws SemanticException {
        Ast.UnaryOp op = unary.op();
        if (op == Ast.UnaryOp.NOT) {
            TypedNode operand = check(unary.operand());
            requireBool(operand, "'!' requires a boolean operand");
            return new TypedNode.Unary(op, operand, unary.span());
        }
        TypedNode operand = check(unary.operand());
        requireNumericScalar(operand, "unary '" + (op == Ast.UnaryOp.NEG ? "-" : "+") + "' requires a numeric scalar");
        return new TypedNode.Unary(op, operand, unary.span());
    }

    private TypedNode checkBinary(Ast.Bin bin) throws SemanticException {
        Ast.BinaryOp op = bin.op();
        TypedNode left = check(bin.left());
        TypedNode right = check(bin.right());
        switch (op) {
            case OR, AND -> {
                requireBool(left, "'&&'/'||' requires boolean operands");
                requireBool(right, "'&&'/'||' requires boolean operands");
            }
            case EQ, NE -> {
                if (left.type() == ExpressionType.BOOL || right.type() == ExpressionType.BOOL) {
                    requireBool(left, "'=='/'!=' compares bool with bool or numeric with numeric");
                    requireBool(right, "'=='/'!=' compares bool with bool or numeric with numeric");
                } else {
                    requireNumericScalar(left, "'=='/'!=' compares bool with bool or numeric with numeric");
                    requireNumericScalar(right, "'=='/'!=' compares bool with bool or numeric with numeric");
                }
            }
            default -> {
                requireNumericScalar(left, "'" + op + "' requires numeric scalar operands");
                requireNumericScalar(right, "'" + op + "' requires numeric scalar operands");
            }
        }
        return new TypedNode.Binary(op, left, right, bin.span());
    }

    private TypedNode checkMember(Ast.Member member) throws SemanticException {
        Ast base = member.base();
        // matrix: two numeric member tokens over a MAT4 fixed input
        if (base instanceof Ast.Member inner && inner.base() instanceof Ast.Ident id) {
            Integer fixed = env.fixedInput(id.name());
            if (fixed != null && env.fixedKind(fixed) == FixedInputKind.MAT4) {
                if (inner.named() || member.named()
                        || !indexInRange(inner.index()) || !indexInRange(member.index())) {
                    throw fail(ExpressionDiagnosticKind.TYPE, member.span(),
                            "matrix access requires exactly two numeric indices 0..3");
                }
                return new TypedNode.MatrixCell(fixed, inner.index(), member.index(), member.span());
            }
        }
        if (base instanceof Ast.Ident id) {
            Integer fixed = env.fixedInput(id.name());
            if (fixed != null) {
                FixedInputKind kind = env.fixedKind(fixed);
                if (kind == FixedInputKind.MAT4) {
                    throw fail(ExpressionDiagnosticKind.TYPE, member.span(),
                            "matrix access requires exactly two numeric indices");
                }
                int width = vectorWidth(kind);
                int component = selector(member, width);
                boolean integer = kind == FixedInputKind.IVEC2 || kind == FixedInputKind.IVEC3
                        || kind == FixedInputKind.IVEC4;
                return new TypedNode.Component(
                        new TypedNode.InputRef(fixed, inputShape(kind), id.span()), component, member.span());
            }
            Integer slot = env.definitionSlot(id.name());
            if (slot != null) {
                ExpressionType type = env.definitionType(slot);
                int width = vectorWidth(type);
                if (width < 0) {
                    throw fail(ExpressionDiagnosticKind.TYPE, member.span(),
                            "member access requires a vector");
                }
                return new TypedNode.Component(
                        new TypedNode.DefinitionRef(slot, type, id.span()), selector(member, width),
                        member.span());
            }
        }
        TypedNode typedBase = check(base);
        int width = vectorWidth(typedBase.type());
        if (width < 0) {
            throw fail(ExpressionDiagnosticKind.TYPE, member.span(),
                    "member access requires a vector");
        }
        return new TypedNode.Component(typedBase, selector(member, width), member.span());
    }

    private int selector(Ast.Member member, int width) throws SemanticException {
        int component = member.index();
        if (component >= width) {
            throw fail(ExpressionDiagnosticKind.TYPE, member.span(),
                    "member index " + component + " out of bounds for width " + width);
        }
        return component;
    }

    private TypedNode checkCall(Ast.Call call) throws SemanticException {
        FunctionSymbol function = FunctionSymbol.BY_NAME.get(call.name());
        if (function == null) {
            // a resolvable non-function name is a type error; anything else is unknown
            if (resolves(call.name())) {
                throw fail(ExpressionDiagnosticKind.TYPE, call.nameSpan(),
                        "'" + call.name() + "' is not a function");
            }
            throw fail(ExpressionDiagnosticKind.UNKNOWN_NAME, call.nameSpan(),
                    "unknown name: " + call.name());
        }
        if (!function.accepts(call.args().size())) {
            throw fail(ExpressionDiagnosticKind.ARITY, call.nameSpan(),
                    function.spelling() + " does not accept " + call.args().size() + " arguments");
        }
        return switch (function) {
            case IF -> checkIf(call);
            case SMOOTH -> checkSmooth(call);
            default -> checkOrdinary(function, call);
        };
    }

    private TypedNode checkOrdinary(FunctionSymbol function, Ast.Call call) throws SemanticException {
        List<TypedNode> args = new ArrayList<>(call.args().size());
        for (Ast arg : call.args()) {
            TypedNode typed = check(arg);
            requireNumericScalar(typed, function.spelling() + " requires numeric scalar arguments");
            args.add(typed);
        }
        return new TypedNode.Call(function, List.copyOf(args), call.span());
    }

    private TypedNode checkIf(Ast.Call call) throws SemanticException {
        List<Ast> args = call.args();
        List<TypedNode.IfBranch> branches = new ArrayList<>((args.size() - 1) / 2);
        List<ExpressionType> valueTypes = new ArrayList<>();
        for (int i = 0; i + 1 < args.size(); i += 2) {
            TypedNode condition = check(args.get(i));
            requireBool(condition, "'if' conditions must be boolean");
            TypedNode value = check(args.get(i + 1));
            valueTypes.add(value.type());
            branches.add(new TypedNode.IfBranch(condition, value));
        }
        TypedNode otherwise = check(args.get(args.size() - 1));
        valueTypes.add(otherwise.type());
        ExpressionType unified = unify(valueTypes, call.span());
        return new TypedNode.If(List.copyOf(branches), otherwise, unified, call.span());
    }

    private ExpressionType unify(List<ExpressionType> types, SourceSpan span) throws SemanticException {
        boolean allNumeric = true;
        for (ExpressionType type : types) {
            if (type != ExpressionType.INT && type != ExpressionType.FLOAT) {
                allNumeric = false;
                break;
            }
        }
        if (allNumeric) {
            return ExpressionType.FLOAT;
        }
        ExpressionType first = types.get(0);
        for (ExpressionType type : types) {
            if (type != first) {
                throw fail(ExpressionDiagnosticKind.TYPE, span,
                        "'if' branches must share one type (numeric branches may mix int/float)");
            }
        }
        return first;
    }

    private TypedNode checkSmooth(Ast.Call call) throws SemanticException {
        List<Ast> args = call.args();
        int siteIndex = siteCounter++;
        TypedNode target;
        TypedNode fadeIn = null;
        TypedNode fadeOut = null;
        Integer explicitId = null;
        switch (args.size()) {
            case 1 -> {
                target = checkNumeric(args.get(0), "smooth value");
            }
            case 2 -> {
                Float id = ConstantFolder.fold(args.get(0));
                if (ConstantFolder.integral(id)) {
                    explicitId = (int) (float) id;
                    target = checkNumeric(args.get(1), "smooth value");
                } else {
                    target = checkNumeric(args.get(0), "smooth value");
                    fadeIn = checkNumeric(args.get(1), "smooth fade");
                }
            }
            case 3 -> {
                Float id = ConstantFolder.fold(args.get(0));
                if (ConstantFolder.integral(id)) {
                    explicitId = (int) (float) id;
                    target = checkNumeric(args.get(1), "smooth value");
                    fadeIn = checkNumeric(args.get(2), "smooth fade");
                } else {
                    target = checkNumeric(args.get(0), "smooth value");
                    fadeIn = checkNumeric(args.get(1), "smooth fade");
                    fadeOut = checkNumeric(args.get(2), "smooth fade");
                }
            }
            default -> {
                Float id = ConstantFolder.fold(args.get(0));
                if (!ConstantFolder.integral(id)) {
                    throw fail(ExpressionDiagnosticKind.TYPE, args.get(0).span(),
                            "four-argument smooth requires a constant integral id");
                }
                explicitId = (int) (float) id;
                target = checkNumeric(args.get(1), "smooth value");
                fadeIn = checkNumeric(args.get(2), "smooth fade");
                fadeOut = checkNumeric(args.get(3), "smooth fade");
            }
        }
        sites.add(new SmoothSite(preorder - 1, explicitId));
        return new TypedNode.Smooth(siteIndex, target, fadeIn, fadeOut, call.span());
    }

    private TypedNode checkNumeric(Ast node, String what) throws SemanticException {
        TypedNode typed = check(node);
        requireNumericScalar(typed, what + " must be a numeric scalar");
        return typed;
    }

    // helpers -----------------------------------------------------------------

    private boolean resolves(String name) {
        switch (name) {
            case "pi", "true", "false" -> {
                return true;
            }
            default -> { }
        }
        return env.biomeConstant(name) != null
                || env.contextName(name) != null
                || env.fixedInput(name) != null
                || env.definitionSlot(name) != null;
    }

    private static void requireBool(TypedNode node, String message) throws SemanticException {
        if (node.type() != ExpressionType.BOOL) {
            throw fail(ExpressionDiagnosticKind.TYPE, node.span(), message);
        }
    }

    private static void requireNumericScalar(TypedNode node, String message) throws SemanticException {
        if (node.type() != ExpressionType.FLOAT && node.type() != ExpressionType.INT) {
            throw fail(ExpressionDiagnosticKind.TYPE, node.span(), message);
        }
    }

    private static boolean indexInRange(int index) {
        return index >= 0 && index <= 3;
    }

    private static int vectorWidth(FixedInputKind kind) {
        return switch (kind) {
            case VEC2, IVEC2 -> 2;
            case VEC3, IVEC3 -> 3;
            case VEC4, IVEC4 -> 4;
            default -> -1;
        };
    }

    private static int vectorWidth(ExpressionType type) {
        return switch (type) {
            case VEC2 -> 2;
            case VEC3 -> 3;
            case VEC4 -> 4;
            default -> -1;
        };
    }

    private static TypedNode.InputShape inputShape(FixedInputKind kind) {
        return switch (kind) {
            case FLOAT -> TypedNode.InputShape.FLOAT;
            case INT -> TypedNode.InputShape.INT;
            case VEC2 -> TypedNode.InputShape.FVEC2;
            case VEC3 -> TypedNode.InputShape.FVEC3;
            case VEC4 -> TypedNode.InputShape.FVEC4;
            case IVEC2 -> TypedNode.InputShape.IVEC2;
            case IVEC3 -> TypedNode.InputShape.IVEC3;
            case IVEC4 -> TypedNode.InputShape.IVEC4;
            case MAT4 -> throw new IllegalArgumentException("mat4 has no member shape");
        };
    }

    private static SemanticException fail(ExpressionDiagnosticKind kind, SourceSpan span, String message)
            throws SemanticException {
        throw new SemanticException(kind, span, message);
    }
}
