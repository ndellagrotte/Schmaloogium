// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import com.schmaloogium.engine.config.CustomExpressionDecl;
import com.schmaloogium.engine.config.CustomExpressionKind;
import com.schmaloogium.engine.config.CustomExpressionType;
import com.schmaloogium.engine.config.SourceAttribution;
import com.schmaloogium.engine.expr.api.CompiledUniform;
import com.schmaloogium.engine.expr.api.CustomExpressionCompileRequest;
import com.schmaloogium.engine.expr.api.CustomExpressionCompiler;
import com.schmaloogium.engine.expr.api.CustomExpressionSource;
import com.schmaloogium.engine.expr.api.DeclarationKind;
import com.schmaloogium.engine.expr.api.DiagnosticChannel;
import com.schmaloogium.engine.expr.api.DiagnosticSeverity;
import com.schmaloogium.engine.expr.api.ExpressionBackend;
import com.schmaloogium.engine.expr.api.ExpressionContextSchema;
import com.schmaloogium.engine.expr.api.ExpressionDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticLocation;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.expr.api.PlanBuildResult;
import com.schmaloogium.engine.expr.api.SourceSpan;
import com.schmaloogium.engine.expr.parse.Ast;
import com.schmaloogium.engine.expr.parse.ParseException;
import com.schmaloogium.engine.expr.parse.Parser;
import com.schmaloogium.engine.expr.type.SemanticException;
import com.schmaloogium.engine.expr.type.TypeChecker;
import com.schmaloogium.engine.expr.eval.InterpreterBackend;
import com.schmaloogium.engine.expr.type.TypedNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Section 2.2 compile flow: namespace registration, lex/parse/resolve/check, smooth-key
 * assignment, graph analysis, ordering, fingerprinting, and immutable plan construction
 * (§4.1-§4.5). Pure function of the request; useful partial plans carry every load
 * diagnostic. */
public final class PlanCompiler implements CustomExpressionCompiler {

    @Override
    public PlanBuildResult compile(CustomExpressionCompileRequest request) {
        String packFingerprint = request.packConfigurationFingerprint();
        String requestedBackend = request.backendSemanticId();
        if (!ExpressionBackend.TYPED_AST_INTERPRETER_V1.equals(requestedBackend)) {
            return new PlanBuildResult.Failure(List.of(new ExpressionDiagnostic(
                    StableIds.forUnsupportedBackend(packFingerprint, requestedBackend),
                    ExpressionDiagnosticKind.UNSUPPORTED_BACKEND, DiagnosticSeverity.ERROR,
                    DiagnosticChannel.CHAT_AND_LOG, new ExpressionDiagnosticLocation.SourceLess(),
                    "unsupported evaluator backend '" + requestedBackend + "' (expected '"
                            + ExpressionBackend.TYPED_AST_INTERPRETER_V1 + "')")));
        }

        String planFingerprint = Fingerprints.planFingerprint(request);
        List<CustomExpressionSource> sources = request.declarations();
        int declCount = sources.size();
        List<DiagAt> staged = new ArrayList<>();

        // ---- namespace registration (§4.1): first occurrence owns; collisions reject ----
        Map<String, Integer> owners = new LinkedHashMap<>();
        boolean[] declValid = new boolean[declCount];
        for (int i = 0; i < declCount; i++) {
            CustomExpressionSource source = sources.get(i);
            String name = source.name();
            if (ReservedNames.isReserved(name, request.fixedInputs(), request.context())) {
                staged.add(declDiagnostic(ExpressionDiagnosticKind.DUPLICATE_NAME,
                        planFingerprint, source,
                        "'" + name + "' collides with a reserved name and cannot be declared"));
                continue;
            }
            if (owners.containsKey(name)) {
                staged.add(declDiagnostic(ExpressionDiagnosticKind.DUPLICATE_NAME,
                        planFingerprint, source,
                        "'" + name + "' is already declared; the first declaration owns the name"));
                continue;
            }
            owners.put(name, i);
            declValid[i] = true;
        }
        int slotCount = owners.size();
        int[] slotOfDecl = new int[declCount];
        int[] declOfSlot = new int[slotCount];
        java.util.Arrays.fill(slotOfDecl, -1);
        int nextSlot = 0;
        for (int decl : owners.values()) {
            slotOfDecl[decl] = nextSlot;
            declOfSlot[nextSlot] = decl;
            nextSlot++;
        }

        // ---- input ordinals: schema entries ordered by name for determinism (§4.1) ----
        FixedExpressionInputSchema fixed = request.fixedInputs();
        TreeMap<String, FixedInputKind> sortedInputs = new TreeMap<>(fixed.inputs());
        String[] inputNames = sortedInputs.keySet().toArray(new String[0]);
        FixedInputKind[] inputKinds = sortedInputs.values().toArray(new FixedInputKind[0]);
        Map<String, Integer> inputIndex = new HashMap<>();
        for (int i = 0; i < inputNames.length; i++) {
            inputIndex.put(inputNames[i], i);
        }

        TypeChecker.Environment env = new RequestEnvironment(request, inputNames, inputIndex,
                owners, slotOfDecl, declOfSlot, sources);

        // ---- lex, parse, resolve, type-check, fold per owner (§4.2 step 3) ----
        TypedNode[] roots = new TypedNode[slotCount];
        List<TypeChecker.SmoothSite>[] siteLists = new List[slotCount];
        for (int slot = 0; slot < slotCount; slot++) {
            int decl = declOfSlot[slot];
            CustomExpressionSource source = sources.get(decl);
            try {
                Ast ast = Parser.parse(source.sourceOrdinal(), source.rawExpression());
                TypeChecker.Checked checked = TypeChecker.check(env, ast);
                requireConvertible(checked.root().type(), source.declaredType(), source);
                roots[slot] = checked.root();
                siteLists[slot] = checked.smoothSites();
            } catch (ParseException e) {
                declValid[decl] = false;
                staged.add(declDiagnostic(e.kind(), planFingerprint, e.span(), source, e.getMessage()));
            } catch (SemanticException e) {
                declValid[decl] = false;
                staged.add(declDiagnostic(e.kind(), planFingerprint, e.span(), source, e.getMessage()));
            }
        }

        // ---- smooth keys: explicit ids first-own; auto keys are deterministic (§4.7) ----
        Map<Integer, int[]> firstExplicitOwner = new HashMap<>();
        for (int slot = 0; slot < slotCount; slot++) {
            if (!declValid[declOfSlot[slot]] || siteLists[slot] == null) {
                continue;
            }
            int decl = declOfSlot[slot];
            for (int siteIndex = 0; siteIndex < siteLists[slot].size(); siteIndex++) {
                Integer explicitId = siteLists[slot].get(siteIndex).explicitId();
                if (explicitId == null) {
                    continue;
                }
                int[] prior = firstExplicitOwner.get(explicitId);
                if (prior != null) {
                    declValid[decl] = false;
                    staged.add(declDiagnostic(ExpressionDiagnosticKind.DUPLICATE_SMOOTH_ID,
                            planFingerprint, sources.get(decl),
                            "smooth id " + explicitId + " is already owned by declaration "
                                    + sources.get(prior[0]).name()));
                } else {
                    firstExplicitOwner.put(explicitId, new int[] {decl, siteIndex});
                }
            }
        }

        // ---- definition graph: exact SCC cycles, then transitive invalidity (§4.3) ----
        boolean[] slotValid = new boolean[slotCount];
        for (int slot = 0; slot < slotCount; slot++) {
            slotValid[slot] = declValid[declOfSlot[slot]];
        }
        int[][] deps = new int[slotCount][];
        for (int slot = 0; slot < slotCount; slot++) {
            deps[slot] = slotValid[slot] && roots[slot] != null
                    ? collectRefs(roots[slot])
                    : new int[0];
        }
        int[] component = DependencyAnalyzer.stronglyConnected(deps);
        for (int slot = 0; slot < slotCount; slot++) {
            if (slotValid[slot] && DependencyAnalyzer.onCycle(slot, component, deps)) {
                slotValid[slot] = false;
                staged.add(declDiagnostic(ExpressionDiagnosticKind.CYCLE, planFingerprint,
                        sources.get(declOfSlot[slot]),
                        "definition participates in a dependency cycle"));
            }
        }
        for (int slot : DependencyAnalyzer.propagateInvalidity(slotValid, deps)) {
            slotValid[slot] = false;
            CustomExpressionSource source = sources.get(declOfSlot[slot]);
            staged.add(declDiagnostic(ExpressionDiagnosticKind.INVALID_DEPENDENCY, planFingerprint,
                    channelFor(source.kind()), source,
                    source.kind() + " '" + source.name()
                            + "' depends on an invalid definition"));
        }
        for (int slot = 0; slot < slotCount; slot++) {
            if (!slotValid[slot]) {
                declValid[declOfSlot[slot]] = false;
            }
        }

        // ---- smooth cells: dense indices in declaration/site order (§4.7) ----
        Program.Site[][] smoothSites = new Program.Site[slotCount][];
        for (int slot = 0; slot < slotCount; slot++) {
            int decl = declOfSlot[slot];
            if (!declValid[decl] || siteLists[slot] == null) {
                continue;
            }
            Program.Site[] sites = new Program.Site[siteLists[slot].size()];
            for (int s = 0; s < sites.length; s++) {
                TypeChecker.SmoothSite site = siteLists[slot].get(s);
                long key = site.explicitId() != null
                        ? (long) site.explicitId() & 0xffffffffL
                        : Fingerprints.smoothKey(sourceOrdinal(sources.get(decl)), site.preorderIndex());
                sites[s] = new Program.Site(key, 0);
            }
            smoothSites[slot] = sites;
        }
        int cellCounter = 0;
        for (int slot = 0; slot < slotCount; slot++) {
            if (smoothSites[slot] == null) {
                continue;
            }
            Program.Site[] sites = smoothSites[slot];
            for (int s = 0; s < sites.length; s++) {
                smoothSites[slot][s] = new Program.Site(sites[s].key(), cellCounter++);
            }
        }

        // ---- retention: everything reachable from a valid uniform (§4.2 step 5) ----
        boolean[] retained = new boolean[slotCount];
        for (int slot = 0; slot < slotCount; slot++) {
            if (slotValid[slot] && sources.get(declOfSlot[slot]).kind() == DeclarationKind.UNIFORM) {
                markReachable(slot, deps, slotValid, retained);
            }
        }

        int[][] validDeps = new int[slotCount][];
        for (int slot = 0; slot < slotCount; slot++) {
            List<Integer> kept = new ArrayList<>();
            for (int dep : deps[slot]) {
                if (retained[dep]) {
                    kept.add(dep);
                }
            }
            validDeps[slot] = kept.stream().mapToInt(Integer::intValue).toArray();
        }
        int[] fullOrder = DependencyAnalyzer.orderValid(retained, validDeps);
        int[] order = java.util.Arrays.stream(fullOrder).filter(slot -> retained[slot]).toArray();
        int[][] readerClosure = DependencyAnalyzer.readerClosures(retained, validDeps);

        // submission/reporting order is the original declaration order (§2.2 step 7);
        // evaluation order above is prerequisite-first.
        int[] uniformOrder = java.util.Arrays.stream(order)
                .filter(slot -> sources.get(declOfSlot[slot]).kind() == DeclarationKind.UNIFORM)
                .boxed()
                .sorted(java.util.Comparator.comparingInt(
                        slot -> sources.get(declOfSlot[slot]).sourceOrdinal()))
                .mapToInt(Integer::intValue)
                .toArray();

        // ---- immutable program + published plan (§4.1/§4.2 step 8) ----
        String[] names = new String[slotCount];
        DeclarationKind[] kinds = new DeclarationKind[slotCount];
        ExpressionType[] types = new ExpressionType[slotCount];
        SourceAttribution[] attributions = new SourceAttribution[slotCount];
        String[] raws = new String[slotCount];
        for (int slot = 0; slot < slotCount; slot++) {
            CustomExpressionSource source = sources.get(declOfSlot[slot]);
            names[slot] = source.name();
            kinds[slot] = source.kind();
            types[slot] = source.declaredType();
            attributions[slot] = source.attribution();
            raws[slot] = source.rawExpression();
        }
        boolean[] usesRandom = new boolean[slotCount];
        for (int slot = 0; slot < slotCount; slot++) {
            usesRandom[slot] = roots[slot] != null && touchesRandom(roots[slot]);
        }
        int[] slotOrdinals = new int[slotCount];
        for (int slot = 0; slot < slotCount; slot++) {
            slotOrdinals[slot] = sources.get(declOfSlot[slot]).sourceOrdinal();
        }
        Program program = new Program(slotCount, names, kinds, types, attributions, raws,
                slotOrdinals, order, uniformOrder, roots, validDeps, readerClosure, smoothSites,
                usesRandom, inputNames, inputKinds, inputIndex);
        InterpreterBackend.build(program);

        List<CompiledUniform> uniforms = new ArrayList<>(uniformOrder.length);
        for (int slot : uniformOrder) {
            uniforms.add(new CompiledUniform(sources.get(declOfSlot[slot]).sourceOrdinal(),
                    types[slot], names[slot]));
        }
        staged.sort(java.util.Comparator.comparingInt(DiagAt::ordinal));
        List<ExpressionDiagnostic> diagnostics = staged.stream().map(DiagAt::diagnostic).toList();
        CompiledPlan plan = new CompiledPlan(planFingerprint, fixed.version(), request.context().version(),
                ExpressionBackend.TYPED_AST_INTERPRETER_V1, uniforms, diagnostics, program);
        return diagnostics.isEmpty()
                ? new PlanBuildResult.Success(plan, diagnostics)
                : new PlanBuildResult.Partial(plan, diagnostics);
    }

    @Override
    public PlanBuildResult compilePhase3(String packConfigurationFingerprint,
                                         List<CustomExpressionDecl> declarations,
                                         FixedExpressionInputSchema fixedInputs,
                                         ExpressionContextSchema context,
                                         String backendSemanticId) {
        List<CustomExpressionSource> sources = new ArrayList<>(declarations.size());
        try {
            for (CustomExpressionDecl decl : declarations) {
                sources.add(new CustomExpressionSource(decl.sourceOrdinal(),
                        mapKind(decl.kind(), packConfigurationFingerprint, decl.sourceOrdinal()),
                        mapType(decl.type(), packConfigurationFingerprint, decl.sourceOrdinal()),
                        decl.name(), decl.rawExpression(), decl.attribution()));
            }
        } catch (UnknownVariantException e) {
            return new PlanBuildResult.Failure(List.of(new ExpressionDiagnostic(
                    "expr:TYPE:" + e.packConfigurationFingerprint() + ":" + e.sourceOrdinal(),
                    ExpressionDiagnosticKind.TYPE, DiagnosticSeverity.ERROR,
                    DiagnosticChannel.CHAT_AND_LOG, new ExpressionDiagnosticLocation.SourceLess(),
                    e.getMessage())));
        }
        return compile(new CustomExpressionCompileRequest(packConfigurationFingerprint,
                List.copyOf(sources), fixedInputs, context, backendSemanticId));
    }

    private static DeclarationKind mapKind(CustomExpressionKind kind, String pack, int ordinal) {
        try {
            return DeclarationKind.valueOf(kind.name());
        } catch (IllegalArgumentException e) {
            throw new UnknownVariantException("unknown custom expression kind variant: " + kind, pack, ordinal);
        }
    }

    private static ExpressionType mapType(CustomExpressionType type, String pack, int ordinal) {
        try {
            return ExpressionType.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            throw new UnknownVariantException("unknown custom expression type variant: " + type, pack, ordinal);
        }
    }

    /** An unknown future P3 variant; converted to a Failure diagnostic in {@code compile}. */
    public static final class UnknownVariantException extends RuntimeException {
        private final String packConfigurationFingerprint;
        private final int sourceOrdinal;

        UnknownVariantException(String message, String packConfigurationFingerprint, int sourceOrdinal) {
            super(message);
            this.packConfigurationFingerprint = packConfigurationFingerprint;
            this.sourceOrdinal = sourceOrdinal;
        }

        public String packConfigurationFingerprint() {
            return packConfigurationFingerprint;
        }

        public int sourceOrdinal() {
            return sourceOrdinal;
        }
    }


    private static int sourceOrdinal(CustomExpressionSource source) {
        return source.sourceOrdinal();
    }

    private static DiagnosticChannel channelFor(DeclarationKind kind) {
        return kind == DeclarationKind.UNIFORM
                ? DiagnosticChannel.CHAT_AND_LOG
                : DiagnosticChannel.LOG_ONLY;
    }

    private static DiagAt declDiagnostic(ExpressionDiagnosticKind kind, String fingerprint,
                                        CustomExpressionSource source, String message) {
        return declDiagnostic(kind, fingerprint, wholeSpan(source), source, message);
    }

    /** Closed declaration-conversion table (§4.1): the expression type must convert
     * to the declared type; vectors require an exact match and booleans never
     * interconvert with numerics. */
    private static void requireConvertible(ExpressionType expressionType,
                                           ExpressionType declaredType,
                                           CustomExpressionSource source) throws SemanticException {
        boolean legal = switch (declaredType) {
            case FLOAT, INT -> expressionType == ExpressionType.FLOAT
                    || expressionType == ExpressionType.INT;
            case BOOL -> expressionType == ExpressionType.BOOL;
            case VEC2, VEC3, VEC4 -> expressionType == declaredType;
        };
        if (!legal) {
            throw new SemanticException(ExpressionDiagnosticKind.TYPE,
                    SourceSpan.whole(source.sourceOrdinal(), source.rawExpression()),
                    source.kind() + " '" + source.name() + "' declares " + declaredType
                            + " but the expression produces " + expressionType);
        }
    }

    private static DiagAt declDiagnostic(ExpressionDiagnosticKind kind, String fingerprint,
                                        DiagnosticChannel channel, CustomExpressionSource source,
                                        String message) {
        return declDiagnostic(kind, fingerprint, channel, wholeSpan(source), source, message);
    }

    private static DiagAt declDiagnostic(ExpressionDiagnosticKind kind, String fingerprint,
                                        SourceSpan span, CustomExpressionSource source,
                                        String message) {
        return declDiagnostic(kind, fingerprint, DiagnosticChannel.CHAT_AND_LOG, span, source, message);
    }

    private static DiagAt declDiagnostic(ExpressionDiagnosticKind kind, String fingerprint,
                                        DiagnosticChannel channel, SourceSpan span,
                                        CustomExpressionSource source, String message) {
        String id = StableIds.forDeclaration(kind, fingerprint, source.sourceOrdinal(), span);
        ExpressionDiagnostic diagnostic = new ExpressionDiagnostic(id, kind, DiagnosticSeverity.ERROR,
                channel, location(source), message);
        return new DiagAt(source.sourceOrdinal(), diagnostic);
    }

    /** Load diagnostics are ordered by declaration ordinal, then emission order. */
    private record DiagAt(int ordinal, ExpressionDiagnostic diagnostic) {}


    private static ExpressionDiagnosticLocation location(CustomExpressionSource source) {
        return new ExpressionDiagnosticLocation.Declaration(source.name(),
                source.attribution(), wholeSpan(source));
    }

    /** Whole-declaration span: offsets cover the raw text; parse spans refine inside. */
    private static SourceSpan wholeSpan(CustomExpressionSource source) {
        return new SourceSpan(source.sourceOrdinal(), 0, source.rawExpression().length());
    }
    private static void markReachable(int slot, int[][] deps, boolean[] valid, boolean[] retained) {
        if (retained[slot]) {
            return;
        }
        retained[slot] = true;
        for (int dep : deps[slot]) {
            if (valid[dep]) {
                markReachable(dep, deps, valid, retained);
            }
        }
    }

    private static int[] collectRefs(TypedNode node) {
        java.util.TreeSet<Integer> refs = new java.util.TreeSet<>();
        collectRefsInto(node, refs);
        return refs.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void collectRefsInto(TypedNode node, java.util.TreeSet<Integer> refs) {
        switch (node) {
            case TypedNode.DefinitionRef ref -> refs.add(ref.slot());
            case TypedNode.Component component -> collectRefsInto(component.base(), refs);
            case TypedNode.MatrixCell cell -> { }
            case TypedNode.Unary unary -> collectRefsInto(unary.operand(), refs);
            case TypedNode.Binary binary -> {
                collectRefsInto(binary.left(), refs);
                collectRefsInto(binary.right(), refs);
            }
            case TypedNode.Call call -> call.args().forEach(arg -> collectRefsInto(arg, refs));
            case TypedNode.If cond -> {
                for (TypedNode.IfBranch branch : cond.branches()) {
                    collectRefsInto(branch.condition(), refs);
                    collectRefsInto(branch.value(), refs);
                }
                if (cond.otherwise() != null) {
                    collectRefsInto(cond.otherwise(), refs);
                }
            }
            case TypedNode.Smooth smooth -> {
                collectRefsInto(smooth.target(), refs);
                if (smooth.fadeIn() != null) {
                    collectRefsInto(smooth.fadeIn(), refs);
                }
                if (smooth.fadeOut() != null) {
                    collectRefsInto(smooth.fadeOut(), refs);
                }
            }
            default -> { }
        }
    }

    private static boolean touchesRandom(TypedNode node) {
        switch (node) {
            case TypedNode.Random ignored -> {
                return true;
            }
            case TypedNode.Component component -> {
                return touchesRandom(component.base());
            }
            case TypedNode.Unary unary -> {
                return touchesRandom(unary.operand());
            }
            case TypedNode.Binary binary -> {
                return touchesRandom(binary.left()) || touchesRandom(binary.right());
            }
            case TypedNode.Call call -> {
                for (TypedNode arg : call.args()) {
                    if (touchesRandom(arg)) {
                        return true;
                    }
                }
                return false;
            }
            case TypedNode.If cond -> {
                for (TypedNode.IfBranch branch : cond.branches()) {
                    if (touchesRandom(branch.condition()) || touchesRandom(branch.value())) {
                        return true;
                    }
                }
                return cond.otherwise() != null && touchesRandom(cond.otherwise());
            }
            case TypedNode.Smooth smooth -> {
                return touchesRandom(smooth.target())
                        || (smooth.fadeIn() != null && touchesRandom(smooth.fadeIn()))
                        || (smooth.fadeOut() != null && touchesRandom(smooth.fadeOut()));
            }
            default -> {
                return false;
            }
        }
    }

    /** Resolution environment over the compile request (§4.4 tiers). */
    private record RequestEnvironment(CustomExpressionCompileRequest request,
                                      String[] inputNames,
                                      Map<String, Integer> inputIndex,
                                      Map<String, Integer> owners,
                                      int[] slotOfDecl,
                                      int[] declOfSlot,
                                      List<CustomExpressionSource> sources)
            implements TypeChecker.Environment {

        @Override
        public Integer biomeConstant(String name) {
            return request.context().biomeConstants().get(name);
        }

        @Override
        public TypeChecker.ContextName contextName(String name) {
            ExpressionContextSchema context = request.context();
            switch (name) {
                case "biome" -> {
                    return new TypeChecker.ContextName.Scalar(TypedNode.ContextScalar.BIOME);
                }
                case "temperature" -> {
                    return new TypeChecker.ContextName.Scalar(TypedNode.ContextScalar.TEMPERATURE);
                }
                case "rainfall" -> {
                    return new TypeChecker.ContextName.Scalar(TypedNode.ContextScalar.RAINFALL);
                }
                default -> {
                    try {
                        TypedNode.ContextFlag flag = TypedNode.ContextFlag.valueOf(name.toUpperCase(java.util.Locale.ROOT));
                        return new TypeChecker.ContextName.Flag(flag);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            }
        }

        @Override
        public Integer fixedInput(String name) {
            return inputIndex.get(name);
        }

        @Override
        public FixedInputKind fixedKind(int inputOrdinal) {
            return request.fixedInputs().inputs().get(inputNames[inputOrdinal]);
        }

        @Override
        public Integer definitionSlot(String name) {
            Integer decl = owners.get(name);
            return decl == null ? null : slotOfDecl[decl];
        }

        @Override
        public ExpressionType definitionType(int slot) {
            return sources.get(declOfSlot[slot]).declaredType();
        }
    }
}
