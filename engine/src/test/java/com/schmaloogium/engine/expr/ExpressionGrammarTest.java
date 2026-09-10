// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.SourceAttribution;
import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.expr.api.BuiltInLookup;
import com.schmaloogium.engine.expr.api.BuiltInValue;
import com.schmaloogium.engine.expr.api.CustomExpressionCompileRequest;
import com.schmaloogium.engine.expr.api.CustomExpressionController;
import com.schmaloogium.engine.expr.api.CustomExpressionPlan;
import com.schmaloogium.engine.expr.api.CustomExpressionSource;
import com.schmaloogium.engine.expr.api.CustomSubmitResult;
import com.schmaloogium.engine.expr.api.CustomUploadCommand;
import com.schmaloogium.engine.expr.api.DeclarationKind;
import com.schmaloogium.engine.expr.api.ExpressionContextProvider;
import com.schmaloogium.engine.expr.api.ExpressionContextRequest;
import com.schmaloogium.engine.expr.api.ExpressionContextResult;
import com.schmaloogium.engine.expr.api.ExpressionContextSchema;
import com.schmaloogium.engine.expr.api.ExpressionContextSnapshot;
import com.schmaloogium.engine.expr.api.ExpressionDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.expr.api.PlanActivationResult;
import com.schmaloogium.engine.expr.api.PlanBuildResult;
import com.schmaloogium.engine.expr.api.ViewEntityFlags;
import com.schmaloogium.engine.expr.eval.ControllerFactory;
import com.schmaloogium.engine.expr.plan.PlanCompiler;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Grammar (§4.2 EBNF) violation classes, precedence/associativity, and the
 * division/modulo edge rules of §4.6. */
class ExpressionGrammarTest {

    private static final String PACK = "pack-grammar";

    private static PlanBuildResult compile(String raw, ExpressionType type) {
        Map<String, FixedInputKind> inputs = Map.of(
                "frameCounter", FixedInputKind.INT,
                "frameTime", FixedInputKind.FLOAT);
        Map<String, FixedInputKind> ordered = new java.util.LinkedHashMap<>(inputs);
        return new PlanCompiler().compile(new CustomExpressionCompileRequest(PACK,
                List.of(new CustomExpressionSource(0, DeclarationKind.UNIFORM, type, "u", raw,
                        new SourceAttribution(new NormalizedPackPath("t.p"), 10, 3))),
                new FixedExpressionInputSchema("f1", Map.copyOf(ordered)),
                new ExpressionContextSchema("c1", Map.of()),
                com.schmaloogium.engine.expr.api.ExpressionBackend.TYPED_AST_INTERPRETER_V1));
    }

    private static ExpressionDiagnosticKind firstDiagnostic(PlanBuildResult result) {
        List<ExpressionDiagnostic> diags = result instanceof PlanBuildResult.Success s
                ? s.diagnostics()
                : result instanceof PlanBuildResult.Partial p ? p.diagnostics()
                        : ((PlanBuildResult.Failure) result).diagnostics();
        return diags.get(0).kind();
    }

    private static float evaluate(String raw) {
        PlanBuildResult result = compile(raw, ExpressionType.FLOAT);
        CustomExpressionPlan plan = result instanceof PlanBuildResult.Success s ? s.plan()
                : ((PlanBuildResult.Partial) result).plan();
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(
                metrics -> { }, diagnostic -> { });
        ViewEntityFlags flags = new ViewEntityFlags(true, false, false, false, false, false,
                false, false, true, false, false, false, false, false);
        ExpressionContextProvider provider = request -> new ExpressionContextResult.Available(
                new ExpressionContextSnapshot(1, 1.0f, 0.0f, flags));
        BuiltInExpressionView view = name -> {
            BuiltInValue value = "frameCounter".equals(name) ? new BuiltInValue.Int1(1)
                    : "frameTime".equals(name) ? new BuiltInValue.Float1(0) : null;
            return value == null ? new BuiltInLookup.Absent() : new BuiltInLookup.Present(value);
        };
        assertEquals(PlanActivationResult.Activated.class,
                controller.activate(plan, provider, () -> 0.0f).getClass());
        float[] out = { Float.NaN };
        controller.refresh(view, command -> {
            if (command instanceof CustomUploadCommand.Float1 f) {
                out[0] = f.value();
            }
            return new CustomSubmitResult.Accepted();
        });
        controller.close();
        return out[0];
    }

    // ---- each EBNF violation class ----

    @Test
    void lexErrorRejectsIllegalCharacter() {
        assertEquals(ExpressionDiagnosticKind.LEX,
                firstDiagnostic(compile("1 # 2", ExpressionType.FLOAT)));
    }

    @Test
    void parseErrorRejectsUnclosedGroup() {
        assertEquals(ExpressionDiagnosticKind.PARSE,
                firstDiagnostic(compile("(1+2", ExpressionType.FLOAT)));
    }

    @Test
    void parseErrorRejectsTrailingOperator() {
        assertEquals(ExpressionDiagnosticKind.PARSE,
                firstDiagnostic(compile("1+2*", ExpressionType.FLOAT)));
    }

    @Test
    void parseErrorRejectsEmptyExpression() {
        assertEquals(ExpressionDiagnosticKind.PARSE,
                firstDiagnostic(compile("", ExpressionType.FLOAT)));
    }

    @Test
    void parseErrorRejectsBadExponent() {
        assertEquals(ExpressionDiagnosticKind.PARSE,
                firstDiagnostic(compile("1e+", ExpressionType.FLOAT)));
    }

    @Test
    void parseErrorRejectsUnknownMember() {
        assertEquals(ExpressionDiagnosticKind.PARSE,
                firstDiagnostic(compile("gbufferModelView.9", ExpressionType.FLOAT)));
    }

    @Test
    void semanticErrorRejectsUnknownFunction() {
        assertEquals(ExpressionDiagnosticKind.UNKNOWN_NAME,
                firstDiagnostic(compile("nosuch(1)", ExpressionType.FLOAT)));
    }

    @Test
    void semanticErrorRejectsWrongArity() {
        assertEquals(ExpressionDiagnosticKind.ARITY,
                firstDiagnostic(compile("sin(1,2)", ExpressionType.FLOAT)));
    }

    @Test
    void semanticErrorRejectsUnknownName() {
        assertEquals(ExpressionDiagnosticKind.UNKNOWN_NAME,
                firstDiagnostic(compile("missingName", ExpressionType.FLOAT)));
    }

    @Test
    void semanticErrorRejectsTypeMismatch() {
        assertEquals(ExpressionDiagnosticKind.TYPE,
                firstDiagnostic(compile("vec2(1,2)+1", ExpressionType.FLOAT)));
    }

    @Test
    void limitErrorRejectsExcessiveNesting() {
        PlanBuildResult result = compile("1+".repeat(300) + "1", ExpressionType.FLOAT);
        PlanBuildResult.Partial partial = assertInstanceOf(PlanBuildResult.Partial.class, result);
        assertEquals(1, partial.diagnostics().size());
        assertEquals(ExpressionDiagnosticKind.LIMIT, partial.diagnostics().get(0).kind());
    }

    @Test
    void semanticErrorRejectsDeclaredTypeMismatch() {
        PlanBuildResult result = compile("vec2(1,2)", ExpressionType.FLOAT);
        PlanBuildResult.Partial partial = assertInstanceOf(PlanBuildResult.Partial.class, result);
        assertEquals(ExpressionDiagnosticKind.TYPE, partial.diagnostics().get(0).kind());
    }

    // ---- precedence and associativity ----

    @Test
    void multiplicationBindsTighterThanAddition() {
        assertEquals(7.0f, evaluate("1+2*3"), 0.0f);
        assertEquals(7.0f, evaluate("2*3+1"), 0.0f);
    }

    @Test
    void binaryOperatorsAreLeftAssociative() {
        assertEquals(-4.0f, evaluate("1-2-3"), 0.0f);
        assertEquals(1.5f, evaluate("9/3/2"), 0.0f);
    }

    @Test
    void comparisonIsLooserThanArithmetic() {
        assertEquals(1.0f, evaluate("if(1+2*2>4,1,0)"), 0.0f);
        assertEquals(1.0f, evaluate("if(1-2-3==-4,1,0)"), 0.0f);
    }

    @Test
    void unaryMinusBindsTighterThanMultiplication() {
        assertEquals(6.0f, evaluate("-2*-3"), 0.0f);
        assertEquals(-2.0f, evaluate("-2*3/3"), 0.0f);
    }

    @Test
    void parenthesesOverridePrecedence() {
        assertEquals(9.0f, evaluate("(1+2)*3"), 0.0f);
    }

    // ---- division and modulo edges (§4.6) ----

    @Test
    void truncatingRemainderKeepsTheDividendSign() {
        assertEquals(-5 % 3, evaluate("-5%3"), 0.0f);
        assertEquals(5 % -3, evaluate("5%-3"), 0.0f);
    }

    @Test
    void floorModuloIsDistinctForNegativeOperands() {
        assertEquals(-5.0f - (float) Math.floor(-5.0f / 3.0f) * 3.0f,
                evaluate("fmod(-5,3)"), 0.0f);
        assertTrue(evaluate("fmod(-5,3)") != evaluate("-5%3"));
    }

    @Test
    void truncatingDivision() {
        assertEquals(-5.0f / 3.0f, evaluate("-5/3"), 0.0f);
    }

    @Test
    void moduloRejectsZeroDivisor() {
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(
                metrics -> { }, diagnostic -> { });
        PlanBuildResult result = compile("1%temperature", ExpressionType.FLOAT);
        CustomExpressionPlan plan = result instanceof PlanBuildResult.Success s ? s.plan()
                : ((PlanBuildResult.Partial) result).plan();
        ViewEntityFlags flags = new ViewEntityFlags(true, false, false, false, false, false,
                false, false, true, false, false, false, false, false);
        ExpressionContextProvider provider = request -> new ExpressionContextResult.Available(
                new ExpressionContextSnapshot(1, 0.0f, 0.0f, flags));
        controller.activate(plan, provider, () -> 0.0f);
        float[] out = { Float.NaN };
        controller.refresh(name -> new BuiltInLookup.Absent(), command -> {
            if (command instanceof CustomUploadCommand.Float1 f) {
                out[0] = f.value();
            }
            return new CustomSubmitResult.Accepted();
        });
        controller.close();
        assertTrue(Float.isNaN(out[0]));
    }
}
