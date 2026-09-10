// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.SourceAttribution;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.expr.api.BuiltInLookup;
import com.schmaloogium.engine.expr.api.BuiltInValue;
import com.schmaloogium.engine.expr.api.CustomExpressionCompileRequest;
import com.schmaloogium.engine.expr.api.CustomExpressionController;
import com.schmaloogium.engine.expr.api.CustomExpressionPlan;
import com.schmaloogium.engine.expr.api.CustomExpressionSource;
import com.schmaloogium.engine.expr.api.CustomRefreshResult;
import com.schmaloogium.engine.expr.api.CustomSubmitResult;
import com.schmaloogium.engine.expr.api.CustomUniformUploadSink;
import com.schmaloogium.engine.expr.api.CustomUploadCommand;
import com.schmaloogium.engine.expr.api.DeclarationKind;
import com.schmaloogium.engine.expr.api.DiagnosticChannel;
import com.schmaloogium.engine.expr.api.DiagnosticSeverity;
import com.schmaloogium.engine.expr.api.ExpressionContextProvider;
import com.schmaloogium.engine.expr.api.ExpressionContextRequest;
import com.schmaloogium.engine.expr.api.ExpressionContextResult;
import com.schmaloogium.engine.expr.api.ExpressionContextSchema;
import com.schmaloogium.engine.expr.api.ExpressionContextSnapshot;
import com.schmaloogium.engine.expr.api.ExpressionDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticLocation;
import com.schmaloogium.engine.expr.api.ExpressionMetricsSink;
import com.schmaloogium.engine.expr.api.ExpressionResetReason;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.expr.api.PlanActivationResult;
import com.schmaloogium.engine.expr.api.PlanBuildResult;
import com.schmaloogium.engine.expr.api.RandomSource;
import com.schmaloogium.engine.expr.api.ViewEntityFlags;
import com.schmaloogium.engine.expr.eval.ControllerFactory;
import com.schmaloogium.engine.expr.plan.PlanCompiler;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;

/** The fifteen §5.6 receiving vectors, run through the real compile → activate →
 * refresh → reset → close path with scripted P6-conforming providers and sinks. */
class ExpressionConformanceVectorTest {

    private static final String PACK = "pack-vector";

    // ---------- scripted fixtures ----------

    private static final class ScriptedView implements BuiltInExpressionView {
        private final Map<String, BuiltInValue> values;

        ScriptedView(Map<String, BuiltInValue> values) {
            this.values = Map.copyOf(values);
        }

        @Override
        public BuiltInLookup lookup(String name) {
            BuiltInValue value = values.get(name);
            return value == null ? new BuiltInLookup.Absent() : new BuiltInLookup.Present(value);
        }
    }

    private static ScriptedView view(int counter, float time) {
        return new ScriptedView(Map.of("frameCounter", new BuiltInValue.Int1(counter),
                "frameTime", new BuiltInValue.Float1(time)));
    }

    private static final class ScriptedSink implements CustomUniformUploadSink {
        final Deque<CustomSubmitResult> script = new ArrayDeque<>();
        final List<CustomUploadCommand> commands = new ArrayList<>();

        ScriptedSink(CustomSubmitResult... results) {
            for (CustomSubmitResult result : results) {
                script.add(result);
            }
        }

        @Override
        public CustomSubmitResult submit(CustomUploadCommand command) {
            commands.add(command);
            return Objects.requireNonNull(script.poll(), "sink script exhausted");
        }
    }

    private static final class ScriptedContexts implements ExpressionContextProvider {
        final Deque<ExpressionContextResult> script;
        int samples;

        ScriptedContexts(List<ExpressionContextResult> results) {
            this.script = new ArrayDeque<>(results);
        }

        @Override
        public ExpressionContextResult snapshot(ExpressionContextRequest request) {
            samples++;
            return Objects.requireNonNull(script.poll(), "context script exhausted");
        }
    }

    private static ExpressionContextResult ctx(float temperature, boolean inWater) {
        return new ExpressionContextResult.Available(new ExpressionContextSnapshot(1,
                temperature, 0.0f, flags(inWater)));
    }

    private static ViewEntityFlags flags(boolean inWater) {
        return new ViewEntityFlags(true, false, false, false, false, false, inWater, false,
                true, false, false, false, false, false);
    }

    private static final class ScriptedRandom implements RandomSource {
        private final Deque<Float> script;
        int samples;

        ScriptedRandom(float... values) {
            this.script = new ArrayDeque<>();
            for (float v : values) {
                script.add(v);
            }
        }

        @Override
        public float nextFloat() {
            samples++;
            return Objects.requireNonNull(script.poll(), "random script exhausted");
        }
    }

    private static FixedExpressionInputSchema schema() {
        Map<String, FixedInputKind> inputs = new LinkedHashMap<>();
        inputs.put("frameCounter", FixedInputKind.INT);
        inputs.put("frameTime", FixedInputKind.FLOAT);
        inputs.put("gbufferModelView", FixedInputKind.MAT4);
        return new FixedExpressionInputSchema("fixed-v1", Map.copyOf(inputs));
    }

    private static ExpressionContextSchema contextSchema() {
        return new ExpressionContextSchema("context-v1", Map.of("BIOME_PLAINS", 1));
    }

    private static CustomExpressionSource decl(int ordinal, DeclarationKind kind,
                                               ExpressionType type, String name, String raw) {
        return new CustomExpressionSource(ordinal, kind, type, name, raw,
                new SourceAttribution(new NormalizedPackPath("test.pack"), 10 + ordinal, 3));
    }

    private static PlanBuildResult compile(CustomExpressionSource... sources) {
        return new PlanCompiler().compile(new CustomExpressionCompileRequest(PACK,
                List.of(sources), schema(), contextSchema(),
                com.schmaloogium.engine.expr.api.ExpressionBackend.TYPED_AST_INTERPRETER_V1));
    }

    private static CustomExpressionPlan planOf(PlanBuildResult result) {
        if (result instanceof PlanBuildResult.Success s) {
            return s.plan();
        }
        return ((PlanBuildResult.Partial) result).plan();
    }

    private record Running(CustomExpressionController controller, ScriptedContexts contexts,
                           ScriptedRandom random) {}

    private static List<Float> floats(ScriptedSink sink) {
        List<Float> values = new ArrayList<>();
        for (CustomUploadCommand command : sink.commands) {
            if (command instanceof CustomUploadCommand.Float1 f) {
                values.add(f.value());
            }
        }
        return values;
    }

    private static float refreshFloat(CustomExpressionController controller, ScriptedContexts contexts,
                                      int counter, float time) {
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted());
        controller.refresh(view(counter, time), sink);
        return floats(sink).isEmpty() ? Float.NaN : floats(sink).get(0);
    }

    // ---------- the fifteen vectors ----------

    @Test
    void remainderVsFloor() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "-5%3"),
                decl(1, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "b", "fmod(-5,3)"),
                decl(2, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "c", "frac(-1.25)"),
                decl(3, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "d", "round(-1.5)"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result),
                new ScriptedContexts(List.of(ctx(1, false))), new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted(), new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        assertEquals(new CustomRefreshResult.Completed(4, 0, 0),
                controller.refresh(view(1, 0), sink));
        assertEquals(-2.0f, floats(sink).get(0), 0.0f);
        assertEquals(1.0f, floats(sink).get(1), 0.0f);
        assertEquals(0.75f, floats(sink).get(2), 0.0f);
        assertEquals(-1.0f, floats(sink).get(3), 0.0f);
        controller.close();
    }

    @Test
    void lazyRandom() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "if(false,random(),0.25)"),
                decl(1, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "b", "random()"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        ScriptedRandom random = new ScriptedRandom(0.75f);
        controller.activate(planOf(result), new ScriptedContexts(List.of(ctx(1, false))), random);
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        controller.refresh(view(1, 0), sink);
        assertEquals(List.of(0.25f, 0.75f), floats(sink));
        assertEquals(1, random.samples);
        controller.close();
    }

    @Test
    void rowColumn() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "gbufferModelView.2.1"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        float[] matrix = new float[16];
        matrix[2 * 4 + 1] = 9.0f;
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result), new ScriptedContexts(List.of(ctx(1, false), ctx(1, false))),
                new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted());
        controller.refresh(new ScriptedView(Map.of("frameCounter", new BuiltInValue.Int1(1),
                "frameTime", new BuiltInValue.Float1(0),
                "gbufferModelView", new BuiltInValue.Mat4(matrix))), sink);
        assertEquals(List.of(9.0f), floats(sink));
        ScriptedSink sink2 = new ScriptedSink();
        controller.refresh(view(2, 1), sink2);
        assertEquals(0, sink2.commands.size());
        controller.close();
    }

    @Test
    void divide() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "1/temperature"),
                decl(1, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "b", "2"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result),
                new ScriptedContexts(List.of(ctx(0, false), ctx(1, false))), new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted());
        assertEquals(new CustomRefreshResult.Completed(1, 0, 0),
                controller.refresh(view(1, 0), sink));
        assertEquals(List.of(2.0f), floats(sink));
        ScriptedSink sink2 = new ScriptedSink(new CustomSubmitResult.Accepted());
        controller.refresh(view(2, 1), sink2);
        assertEquals(List.of(2.0f), floats(sink2));
        controller.close();
    }

    @Test
    void unsupportedEmpty() {
        PlanBuildResult result = new PlanCompiler().compile(new CustomExpressionCompileRequest(
                PACK, List.of(), schema(), contextSchema(), "schmaloogium:unsupported-test"));
        PlanBuildResult.Failure failure = assertInstanceOf(PlanBuildResult.Failure.class, result);
        assertEquals(1, failure.diagnostics().size());
        ExpressionDiagnostic diagnostic = failure.diagnostics().get(0);
        assertEquals(ExpressionDiagnosticKind.UNSUPPORTED_BACKEND, diagnostic.kind());
        assertEquals(DiagnosticSeverity.ERROR, diagnostic.severity());
        assertEquals(DiagnosticChannel.CHAT_AND_LOG, diagnostic.channel());
        assertInstanceOf(ExpressionDiagnosticLocation.SourceLess.class, diagnostic.location());
        assertTrue(diagnostic.stableId().contains(PACK));
        assertTrue(diagnostic.stableId().contains("schmaloogium:unsupported-test"));
        PlanBuildResult adapted = new PlanCompiler().compilePhase3(PACK, List.of(),
                schema(), contextSchema(), "schmaloogium:unsupported-test");
        assertInstanceOf(PlanBuildResult.Failure.class, adapted);
    }

    @Test
    void providerUnavailable() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "temperature"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        ScriptedContexts contexts = new ScriptedContexts(List.of(
                new ExpressionContextResult.Unavailable("ctx-unavailable-stable-id"), ctx(0.5f, false)));
        controller.activate(planOf(result), contexts, new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink();
        assertEquals(new CustomRefreshResult.Aborted("ctx-unavailable-stable-id", 0, 0, 0),
                controller.refresh(view(1, 0), sink));
        assertEquals(0, sink.commands.size());
        ScriptedSink sink2 = new ScriptedSink(new CustomSubmitResult.Accepted());
        assertEquals(new CustomRefreshResult.Completed(1, 0, 0),
                controller.refresh(view(2, 1), sink2));
        assertEquals(List.of(0.5f), floats(sink2));
        controller.close();
    }

    @Test
    void uniformReference() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.VEC3, "tint", "vec3(level,bridge,level)"),
                decl(1, DeclarationKind.VARIABLE, ExpressionType.FLOAT, "bridge", "level+0.25"),
                decl(2, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "level", "0.5"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result), new ScriptedContexts(List.of(ctx(1, false))),
                new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        controller.refresh(view(1, 0), sink);
        assertEquals(2, sink.commands.size());
        CustomUploadCommand tint = sink.commands.get(0);
        CustomUploadCommand level = sink.commands.get(1);
        assertInstanceOf(CustomUploadCommand.Float3.class, tint);
        CustomUploadCommand.Float3 tint3 = (CustomUploadCommand.Float3) tint;
        assertEquals(0.5f, tint3.x(), 0.0f);
        assertEquals(0.75f, tint3.y(), 0.0f);
        assertEquals(0.5f, tint3.z(), 0.0f);
        assertEquals(0.5f, ((CustomUploadCommand.Float1) level).value(), 0.0f);
        controller.close();
    }

    @Test
    void uniformConversionMemo() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "reader", "count+count+0.5"),
                decl(1, DeclarationKind.UNIFORM, ExpressionType.INT, "count", "2+random()"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        // random() is observable on every switch (§7.2), so each refresh re-evaluates
        // count; the vector pins the converted-value memoization within one evaluation.
        ScriptedRandom random = new ScriptedRandom(0.75f, 0.75f);
        controller.activate(planOf(result),
                new ScriptedContexts(List.of(ctx(1, false), ctx(1, false))), random);
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        controller.refresh(view(1, 0), sink);
        assertEquals(4.5f, ((CustomUploadCommand.Float1) sink.commands.get(0)).value(), 0.0f);
        assertEquals(2, ((CustomUploadCommand.Int1) sink.commands.get(1)).value());
        ScriptedSink sink2 = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        controller.refresh(view(2, 1), sink2);
        assertEquals(4.5f, ((CustomUploadCommand.Float1) sink2.commands.get(0)).value(), 0.0f);
        assertEquals(2, ((CustomUploadCommand.Int1) sink2.commands.get(1)).value());
        assertEquals(2, random.samples);
        controller.close();
    }

    @Test
    void definitionLazyEffects() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a",
                        "if(false,hidden,if(false,random(),0.25))"),
                decl(1, DeclarationKind.VARIABLE, ExpressionType.FLOAT, "hidden", "random()"),
                decl(2, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "b", "random()"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        ScriptedRandom random = new ScriptedRandom(0.5f, 0.75f);
        controller.activate(planOf(result), new ScriptedContexts(List.of(ctx(1, false))), random);
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        controller.refresh(view(1, 0), sink);
        assertEquals(List.of(0.25f, 0.75f), floats(sink));
        assertEquals(2, random.samples);
        controller.close();
    }

    @Test
    void uniformCycle() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "b+c"),
                decl(1, DeclarationKind.VARIABLE, ExpressionType.FLOAT, "b", "a"),
                decl(2, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "reader", "a"),
                decl(3, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "c", "temperature"),
                decl(4, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "good", "c+1"));
        PlanBuildResult.Partial partial = assertInstanceOf(PlanBuildResult.Partial.class, result);
        List<ExpressionDiagnosticKind> kinds = partial.diagnostics().stream()
                .map(ExpressionDiagnostic::kind).toList();
        assertEquals(3, kinds.size());
        assertEquals(2, kinds.stream().filter(ExpressionDiagnosticKind.CYCLE::equals).count());
        assertEquals(1, kinds.stream().filter(ExpressionDiagnosticKind.INVALID_DEPENDENCY::equals).count());
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result), new ScriptedContexts(List.of(ctx(0.5f, false))),
                new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        assertEquals(new CustomRefreshResult.Completed(2, 0, 0),
                controller.refresh(view(1, 0), sink));
        assertEquals(List.of(0.5f, 1.5f), floats(sink));
        controller.close();
    }

    @Test
    void uniformRuntime() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "reader", "bad+1"),
                decl(1, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "bad", "base/temperature"),
                decl(2, DeclarationKind.VARIABLE, ExpressionType.FLOAT, "base", "2"),
                decl(3, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "good", "base+1"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result),
                new ScriptedContexts(List.of(ctx(0, false), ctx(1, false))), new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted());
        controller.refresh(view(1, 0), sink);
        assertEquals(List.of(3.0f), floats(sink));
        ScriptedSink sink2 = new ScriptedSink(new CustomSubmitResult.Accepted());
        controller.refresh(view(2, 1), sink2);
        assertEquals(List.of(3.0f), floats(sink2));
        controller.close();
    }

    @Test
    void sharedNamespace() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.VARIABLE, ExpressionType.FLOAT, "shared", "0.5"),
                decl(1, DeclarationKind.UNIFORM, ExpressionType.INT, "shared", "2"),
                decl(2, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "reader", "shared+0.25"),
                decl(3, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "failed", "missingName"),
                decl(4, DeclarationKind.VARIABLE, ExpressionType.FLOAT, "failed", "1"),
                decl(5, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "dependent", "failed"),
                decl(6, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "good", "2"));
        PlanBuildResult.Partial partial = assertInstanceOf(PlanBuildResult.Partial.class, result);
        List<ExpressionDiagnosticKind> kinds = partial.diagnostics().stream()
                .map(ExpressionDiagnostic::kind).toList();
        assertEquals(2, kinds.stream().filter(ExpressionDiagnosticKind.DUPLICATE_NAME::equals).count());
        assertEquals(1, kinds.stream().filter(ExpressionDiagnosticKind.UNKNOWN_NAME::equals).count());
        assertEquals(1, kinds.stream().filter(ExpressionDiagnosticKind.INVALID_DEPENDENCY::equals).count());
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        controller.activate(planOf(result), new ScriptedContexts(List.of(ctx(1, false))),
                new ScriptedRandom());
        ScriptedSink sink = new ScriptedSink(new CustomSubmitResult.Accepted(),
                new CustomSubmitResult.Accepted());
        assertEquals(new CustomRefreshResult.Completed(2, 0, 0),
                controller.refresh(view(1, 0), sink));
        assertEquals(List.of(0.75f, 2.0f), floats(sink));
        controller.close();
    }

    @Test
    void sameFrameReset() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a", "smooth(7,temperature,1,2)"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        ScriptedContexts contexts = new ScriptedContexts(List.of(
                ctx(0, false), ctx(0, false), ctx(1, false), ctx(0.25f, false), ctx(0.25f, false)));
        controller.activate(planOf(result), contexts, new ScriptedRandom());
        assertEquals(0.0f, refreshFloat(controller, contexts, 1, 1), 0.0f);
        assertEquals(0.0f, refreshFloat(controller, contexts, 1, 1), 0.0f);
        assertEquals(1.0f, refreshFloat(controller, contexts, 2, 1), 0.0f);
        controller.reset(ExpressionResetReason.WORLD_EPOCH);
        assertEquals(0.25f, refreshFloat(controller, contexts, 1, 1), 0.0f);
        assertEquals(Float.floatToIntBits(0.25f),
                Float.floatToIntBits(refreshFloat(controller, contexts, 1, 1)));
        controller.close();
    }

    @Test
    void lazyLateUse() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a",
                        "if(is_in_water,smooth(7,temperature,1),0)"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        ScriptedContexts contexts = new ScriptedContexts(List.of(
                ctx(0, true), ctx(1, false), ctx(1, true), ctx(0, false), ctx(0, true)));
        controller.activate(planOf(result), contexts, new ScriptedRandom());
        assertEquals(0.0f, refreshFloat(controller, contexts, 1, 1), 0.0f);
        assertEquals(0.0f, refreshFloat(controller, contexts, 2, 1), 0.0f);
        assertEquals(1.0f, refreshFloat(controller, contexts, 2, 1), 0.0f);
        assertEquals(0.0f, refreshFloat(controller, contexts, 3, 1), 0.0f);
        assertEquals(0.0f, refreshFloat(controller, contexts, 3, 1), 0.0f);
        controller.close();
    }

    @Test
    void lateFirstUse() {
        PlanBuildResult result = compile(
                decl(0, DeclarationKind.UNIFORM, ExpressionType.FLOAT, "a",
                        "if(is_in_water,smooth(7,temperature,1),0)"));
        assertInstanceOf(PlanBuildResult.Success.class, result);
        CustomExpressionController controller = ControllerFactory.INSTANCE.create(metrics -> { }, diagnostic -> { });
        ScriptedContexts contexts = new ScriptedContexts(List.of(
                ctx(0, false), ctx(0.25f, false), ctx(0.25f, true), ctx(1, false), ctx(1, true)));
        controller.activate(planOf(result), contexts, new ScriptedRandom());
        assertEquals(0.0f, refreshFloat(controller, contexts, 1, 1), 0.0f);
        assertEquals(0.0f, refreshFloat(controller, contexts, 2, 1), 0.0f);
        assertEquals(Float.floatToIntBits(0.25f),
                Float.floatToIntBits(refreshFloat(controller, contexts, 2, 1)));
        assertEquals(0.0f, refreshFloat(controller, contexts, 3, 1), 0.0f);
        assertEquals(Float.floatToIntBits(1.0f),
                Float.floatToIntBits(refreshFloat(controller, contexts, 3, 1)));
        controller.close();
    }
}
