// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.expr.api.BuiltInLookup;
import com.schmaloogium.engine.expr.api.BuiltInValue;
import com.schmaloogium.engine.expr.api.CustomExpressionController;
import com.schmaloogium.engine.expr.api.CustomExpressionPlan;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticSink;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.CustomRefreshResult;
import com.schmaloogium.engine.expr.api.CustomSubmitResult;
import com.schmaloogium.engine.expr.api.CustomUniformBridge;
import com.schmaloogium.engine.expr.api.CustomUniformUploadSink;
import com.schmaloogium.engine.expr.api.CustomUploadCommand;
import com.schmaloogium.engine.expr.api.DeclarationKind;
import com.schmaloogium.engine.expr.api.DiagnosticChannel;
import com.schmaloogium.engine.expr.api.DiagnosticSeverity;
import com.schmaloogium.engine.expr.api.ExpressionBackend;
import com.schmaloogium.engine.expr.api.ExpressionContextProvider;
import com.schmaloogium.engine.expr.api.ExpressionContextRequest;
import com.schmaloogium.engine.expr.api.ExpressionContextResult;
import com.schmaloogium.engine.expr.api.ExpressionContextSnapshot;
import com.schmaloogium.engine.expr.api.ExpressionDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticLocation;
import com.schmaloogium.engine.expr.api.ExpressionMetrics;
import com.schmaloogium.engine.expr.api.ExpressionMetricsSink;
import com.schmaloogium.engine.expr.api.ExpressionResetReason;
import com.schmaloogium.engine.expr.api.PlanActivationResult;
import com.schmaloogium.engine.expr.api.RandomSource;
import com.schmaloogium.engine.expr.api.SourceSpan;
import com.schmaloogium.engine.expr.plan.CompiledPlan;
import com.schmaloogium.engine.expr.plan.Program;
import com.schmaloogium.engine.expr.plan.StableIds;
import com.schmaloogium.engine.expr.state.ClockAdvance;
import com.schmaloogium.engine.expr.state.RefreshClock;
import com.schmaloogium.engine.expr.state.SmoothState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** The one custom-expression controller (§4.8/§4.12): render-thread confined; atomic plan
 * slot; one evaluation epoch per refresh; expression-local failures isolate, structural
 * failures abort with the committed prefix. */
public final class Controller implements CustomExpressionController {

    private final ExpressionMetricsSink metricsSink;
    private final ExpressionDiagnosticSink diagnosticSink;
    private final RefreshClock clock = new RefreshClock();

    private CompiledPlan active;
    private ExpressionContextProvider contexts;
    private RandomSource random;
    private MemoTable memo;
    private SmoothState smooth;
    private boolean[] disabled;
    private long epochCounter;
    private long refreshCount;
    private long uniformErrorsThisRefresh;
    private boolean closed;
    private boolean featureDisabled;
    private String featureDisabledId;
    private final Set<String> reportedIds = new HashSet<>();

    Controller(ExpressionMetricsSink metricsSink, ExpressionDiagnosticSink diagnosticSink) {
        this.metricsSink = metricsSink;
        this.diagnosticSink = diagnosticSink;
    }

    @Override
    public PlanActivationResult activate(CustomExpressionPlan plan,
                                         ExpressionContextProvider contexts,
                                         RandomSource random) {
        requireOpen();
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(contexts, "contexts");
        Objects.requireNonNull(random, "random");
        if (!ExpressionBackend.TYPED_AST_INTERPRETER_V1.equals(plan.backendSemanticId())) {
            return new PlanActivationResult.Rejected(StableIds.forUnsupportedBackend(
                    plan.fingerprint(), plan.backendSemanticId()));
        }
        CompiledPlan compiled = requireCompiled(plan);
        Program program = compiled.program;
        this.active = compiled;
        this.contexts = contexts;
        this.random = random;
        this.memo = new MemoTable(program.slotCount, program.inputNames.length);
        if (this.smooth == null || this.smooth.cellCount() != program.smoothCellCount()) {
            this.smooth = new SmoothState(program.smoothCellCount());
        }
        this.disabled = new boolean[program.slotCount];
        this.clock.reset();
        this.reportedIds.clear();
        this.epochCounter = 0;
        this.refreshCount = 0;
        this.featureDisabled = false;
        this.featureDisabledId = null;
        return new PlanActivationResult.Activated(compiled.fingerprint());
    }

    private static CompiledPlan requireCompiled(CustomExpressionPlan plan) {
        if (plan instanceof CompiledPlan compiled) {
            return compiled;
        }
        throw new IllegalStateException("plan was not produced by this engine's compiler");
    }

    @Override
    public CustomRefreshResult refresh(BuiltInExpressionView values, CustomUniformUploadSink uploads) {
        requireOpen();
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(uploads, "uploads");
        CompiledPlan plan = active;
        if (featureDisabled) {
            return new CustomRefreshResult.Aborted(featureDisabledId, 0, 0, 0);
        }
        if (plan == null || plan.program.uniformOrder.length == 0) {
            return new CustomRefreshResult.NoCustoms();
        }
        Program program = plan.program;
        long accepted = 0;
        long skipped = 0;
        long rejected = 0;
        uniformErrorsThisRefresh = 0;
        long start = System.nanoTime();
        refreshCount++;
        Frame frame = null;
        try {
            if (!ExpressionBackend.TYPED_AST_INTERPRETER_V1.equals(plan.backendSemanticId())) {
                throw new ProviderProtocolException("corrupt plan: backend identity mismatch",
                        ExpressionDiagnosticKind.BACKEND_INVARIANT,
                        StableIds.forStructural(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                                plan.fingerprint(), "backend-identity"));
            }
            int frameCounter = frameCounter(values);
            float frameTime = frameTime(values);
            ClockAdvance advance = clock.advance(frameCounter, frameTime);
            if (advance instanceof ClockAdvance.Rejected rejectedAdvance) {
                throw new ProviderProtocolException(rejectedAdvance.reason());
            }
            double now = ((ClockAdvance.Advanced) advance).controllerSeconds();
            int epoch = (int) ++epochCounter;
            ExpressionContextResult contextResult = contexts.snapshot(
                    new ExpressionContextRequest(plan.fingerprint(), epoch, frameCounter));
            if (contextResult instanceof ExpressionContextResult.Unavailable unavailable) {
                throw new ProviderProtocolException("context provider unavailable",
                        ExpressionDiagnosticKind.PROVIDER, unavailable.diagnosticId());
            }
            ExpressionContextSnapshot snapshot = ((ExpressionContextResult.Available) contextResult).snapshot();
            memo.beginEpoch(epoch);
            smooth.beginRefresh();
            frame = new Frame(program, memo, smooth, epoch, now, random, values, snapshot,
                    this::evaluateSlot);
            for (int slot : program.order) {
                if (disabled[slot]) {
                    continue;
                }
                if (memo.slotStatus[slot] == MemoTable.SLOT_VALUE && memo.slotEpoch[slot] == epoch) {
                    continue;
                }
                evaluateSlot(slot, frame);
            }
            for (int slot : program.uniformOrder) {
                if (disabled[slot]) {
                    continue;
                }
                CustomSubmitResult result = uploads.submit(commandFor(slot, program, memo));
                switch (result) {
                    case CustomSubmitResult.Accepted ignored -> accepted++;
                    case CustomSubmitResult.SkippedAbsent ignored -> skipped++;
                    case CustomSubmitResult.Rejected ignored -> rejected++;
                }
            }
            return new CustomRefreshResult.Completed(accepted, skipped, rejected);
        } catch (ProviderProtocolException e) {
            String id = e.diagnosticId() != null
                    ? e.diagnosticId()
                    : StableIds.forStructural(e.kind(), plan.fingerprint(), slug(e.getMessage()));
            reportStructural(e.kind(), id, e.getMessage());
            if (e.kind() == ExpressionDiagnosticKind.BACKEND_INVARIANT) {
                featureDisabled = true;
                featureDisabledId = id;
            }
            return new CustomRefreshResult.Aborted(id, accepted, skipped, rejected);
        } finally {
            long elapsed = System.nanoTime() - start;
            long nodes = frame == null ? 0 : frame.nodeEvaluations;
            long hits = frame == null ? 0 : frame.variableMemoHits;
            long misses = frame == null ? 0 : frame.variableMemoMisses;
            metricsSink.record(new ExpressionMetrics(plan.fingerprint(), refreshCount, nodes,
                    hits, misses, accepted, uniformErrorsThisRefresh, skipped, elapsed,
                    "refresh-" + refreshCount));
        }
    }

    /** One definition transaction (§4.5/§4.7): evaluate once, convert once, commit the
     * smooth overlay once; on expression failure disable the slot and its readers. */
    private void evaluateSlot(int slot, Frame frame) throws ProviderProtocolException {
        Program program = frame.program;
        MemoTable memoTable = frame.memo;
        Program.Site[] sites = program.smoothSites[slot];
        int[] cellIndices = siteCells(sites);
        int previousOwner = frame.ownerSlot;
        frame.ownerSlot = slot;
        memoTable.slotStatus[slot] = MemoTable.SLOT_EVALUATING;
        memoTable.slotEpoch[slot] = frame.epoch;
        try {
            int cell = Interpreter.eval(program.roots[slot], frame);
            storeConverted(slot, cell, program, frame);
            memoTable.slotStatus[slot] = MemoTable.SLOT_VALUE;
            smooth.commitCells(cellIndices);
        } catch (ExprEvalException e) {
            smooth.discardCells(cellIndices);
            memoTable.slotStatus[slot] = MemoTable.SLOT_ERROR;
            disableAndReport(slot, e.kind(), program);
        } catch (ProviderProtocolException e) {
            smooth.discardCells(cellIndices);
            memoTable.slotStatus[slot] = MemoTable.SLOT_ERROR;
            throw e;
        } catch (RuntimeException e) {
            smooth.discardCells(cellIndices);
            memoTable.slotStatus[slot] = MemoTable.SLOT_ERROR;
            throw new ProviderProtocolException("backend invariant: " + e,
                    ExpressionDiagnosticKind.BACKEND_INVARIANT,
                    StableIds.forStructural(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                            planFingerprint(program), "runtime-invariant"));
        } finally {
            frame.ownerSlot = previousOwner;
        }
    }

    private String planFingerprint(Program program) {
        return active == null ? "detached" : active.fingerprint();
    }

    private static int[] siteCells(Program.Site[] sites) {
        if (sites == null || sites.length == 0) {
            return new int[0];
        }
        int[] cells = new int[sites.length];
        for (int i = 0; i < sites.length; i++) {
            cells[i] = sites[i].cellIndex();
        }
        return cells;
    }

    private void storeConverted(int slot, int cell, Program program, Frame frame)
            throws ExprEvalException {
        ExpressionType type = program.types[slot];
        MemoTable memoTable = frame.memo;
        switch (type) {
            case FLOAT -> {
                float v = Interpreter.numericScalar(cell, frame);
                requireFinite(v, "declared float result");
                memoTable.slotF[4 * slot] = v;
            }
            case INT -> {
                float v = Interpreter.numericScalar(cell, frame);
                if (!Float.isFinite(v) || v >= 2147483648.0f || v < -2147483648.0f) {
                    throw new ExprEvalException(ExpressionDiagnosticKind.INT_RANGE,
                            "declared int result out of range");
                }
                memoTable.slotI[4 * slot] = (int) v;
            }
            case BOOL -> {
                if (frame.cells.kind(cell) != Cells.KBOOL) {
                    throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                            "declared bool result expected");
                }
                memoTable.slotB[slot] = frame.cells.boolAt(cell);
            }
            case VEC2, VEC3, VEC4 -> {
                int width = type == ExpressionType.VEC2 ? 2 : type == ExpressionType.VEC3 ? 3 : 4;
                if (frame.cells.vectorWidth(frame.cells.kind(cell)) != width) {
                    throw new ExprEvalException(ExpressionDiagnosticKind.BACKEND_INVARIANT,
                            "declared vector width mismatch");
                }
                for (int lane = 0; lane < width; lane++) {
                    float v = frame.cells.floatAt(cell, lane);
                    requireFinite(v, "declared vector component");
                    memoTable.slotF[4 * slot + lane] = v;
                }
            }
        }
    }

    private void requireFinite(float v, String what) throws ExprEvalException {
        if (!Float.isFinite(v)) {
            throw new ExprEvalException(ExpressionDiagnosticKind.NON_FINITE, what + " is not finite");
        }
    }

    private void disableAndReport(int slot, ExpressionDiagnosticKind kind, Program program) {
        disabled[slot] = true;
        if (program.kinds[slot] == DeclarationKind.UNIFORM) {
            uniformErrorsThisRefresh++;
        }
        report(program, kind, DiagnosticChannel.CHAT_AND_LOG, slot,
                kind + " in " + program.kinds[slot] + " '" + program.names[slot] + "'");
        for (int reader : program.readerClosure[slot]) {
            disabled[reader] = true;
            report(program, ExpressionDiagnosticKind.INVALID_DEPENDENCY,
                    program.kinds[reader] == DeclarationKind.UNIFORM
                            ? DiagnosticChannel.CHAT_AND_LOG
                            : DiagnosticChannel.LOG_ONLY,
                    reader, "depends on failed definition '" + program.names[slot] + "'");
        }
    }
    private void report(Program program, ExpressionDiagnosticKind kind, DiagnosticChannel channel,
                        int slot, String summary) {
        SourceSpan span = new SourceSpan(program.sourceOrdinals[slot], 0,
                program.raws[slot].length());
        ExpressionDiagnostic diagnostic = new ExpressionDiagnostic(
                StableIds.forDeclaration(kind, active.fingerprint(),
                        declarationOrdinal(program, slot), span),
                kind, DiagnosticSeverity.ERROR, channel,
                new ExpressionDiagnosticLocation.Declaration(program.names[slot],
                        program.attributions[slot], span),
                summary);
        if (reportedIds.add(diagnostic.stableId())) {
            diagnosticSink.report(diagnostic);
        }
    }

    private void reportStructural(ExpressionDiagnosticKind kind, String id, String summary) {
        ExpressionDiagnostic diagnostic = new ExpressionDiagnostic(id, kind,
                DiagnosticSeverity.ERROR, DiagnosticChannel.CHAT_AND_LOG,
                new ExpressionDiagnosticLocation.SourceLess(), summary);
        if (reportedIds.add(id)) {
            diagnosticSink.report(diagnostic);
        }
    }

    private static int declarationOrdinal(Program program, int slot) {
        return program.sourceOrdinals[slot];
    }

    private static String slug(String message) {
        String cleaned = message == null ? "failure" : message;
        StringBuilder slug = new StringBuilder();
        for (char c : cleaned.toCharArray()) {
            slug.append(Character.isLetterOrDigit(c) ? Character.toLowerCase(c) : '-');
        }
        return slug.toString();
    }

    private static int frameCounter(BuiltInExpressionView values) throws ProviderProtocolException {
        BuiltInLookup lookup = values.lookup("frameCounter");
        if (lookup instanceof BuiltInLookup.Present present
                && present.value() instanceof BuiltInValue.Int1 int1) {
            return int1.value();
        }
        throw new ProviderProtocolException("frameCounter is required as Int1 in the view");
    }

    private static float frameTime(BuiltInExpressionView values) throws ProviderProtocolException {
        BuiltInLookup lookup = values.lookup("frameTime");
        if (lookup instanceof BuiltInLookup.Present present
                && present.value() instanceof BuiltInValue.Float1 float1) {
            return float1.value();
        }
        throw new ProviderProtocolException("frameTime is required as Float1 in the view");
    }

    private static CustomUploadCommand commandFor(int slot, Program program, MemoTable memoTable) {
        String name = program.names[slot];
        return switch (program.types[slot]) {
            case FLOAT -> new CustomUploadCommand.Float1(name, memoTable.slotF[4 * slot]);
            case INT -> new CustomUploadCommand.Int1(name, memoTable.slotI[4 * slot]);
            case BOOL -> new CustomUploadCommand.Bool1(name, memoTable.slotB[slot]);
            case VEC2 -> new CustomUploadCommand.Float2(name, memoTable.slotF[4 * slot],
                    memoTable.slotF[4 * slot + 1]);
            case VEC3 -> new CustomUploadCommand.Float3(name, memoTable.slotF[4 * slot],
                    memoTable.slotF[4 * slot + 1], memoTable.slotF[4 * slot + 2]);
            case VEC4 -> new CustomUploadCommand.Float4(name, memoTable.slotF[4 * slot],
                    memoTable.slotF[4 * slot + 1], memoTable.slotF[4 * slot + 2],
                    memoTable.slotF[4 * slot + 3]);
        };
    }

    @Override
    public void reset(ExpressionResetReason reason) {
        requireOpen();
        Objects.requireNonNull(reason, "reason");
        // The installed plan survives every reset (§4.12); only transient evaluation
        // state is cleared. Smooth cells keep their identity but lose their values.
        if (active != null) {
            memo = new MemoTable(active.program.slotCount, active.program.inputNames.length);
            disabled = new boolean[active.program.slotCount];
        }
        if (smooth != null) {
            smooth.reset();
        }
        clock.reset();
        reportedIds.clear();
        epochCounter = 0;
        refreshCount = 0;
        featureDisabled = false;
        featureDisabledId = null;
    }

    @Override
    public void close() {
        closed = true;
        active = null;
        contexts = null;
        random = null;
        memo = null;
        disabled = null;
        if (smooth != null) {
            smooth.reset();
        }
        clock.reset();
        reportedIds.clear();
        featureDisabled = false;
        featureDisabledId = null;
    }

    private void requireOpen() {
        if (closed) {
            throw new IllegalStateException("controller is closed");
        }
    }
}
