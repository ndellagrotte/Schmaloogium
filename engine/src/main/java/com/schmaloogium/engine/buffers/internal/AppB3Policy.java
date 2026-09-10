// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.FixedSamplerLookup;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.FixedSamplerPlanResult;
import com.schmaloogium.engine.buffers.FixedSamplerResolution;
import com.schmaloogium.engine.buffers.FixedSamplerResolver;
import com.schmaloogium.engine.buffers.ResolvedSamplerBinding;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.FixedUnitSamplerConflict;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.SamplerLayoutIssue;
import com.schmaloogium.engine.registry.SamplerLayoutIssueCode;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * The App B.3 fixed sampler policy: the single 16-row unit table for the gbuffers/shadow and
 * fullscreen stage families, the conditional {@code shadow} rule, typed validation and the
 * pure resolver (PHASE_5_DOC §4.12.1, D-P5-35). No GL object, query or dependency exists
 * here; this is the real policy Phase 4's {@code INVALID_SAMPLER_POLICY} boundary consumes
 * before any runtime exists.
 */
public final class AppB3Policy implements FixedSamplerLayoutPolicy {

    /**
     * Stage/band windows served by the policy columns (§4.12.1, RESEARCH B.3): the full
     * gbuffers column applies to gbuffers draw bands (both gbuffers bands plus the shadow
     * band); the shadow window (after-shadow / before-gbuffers) admits only the shadow
     * machinery; deferred/composite/final share the fullscreen column; everything else is
     * unwired and every declaration is UNSUPPORTED_STAGE_DOMAIN.
     */
    enum Domain {
        GBUFFERS,
        SHADOW_WINDOW,
        FULLSCREEN
    }

    private static final String WATERSHADOW = "watershadow";

    /** The App B.3 columns: fixed name -> fixed unit, per stage family. */
    private static final Map<FixedSamplerName, Integer> GBUFFERS_COLUMN = buildGbuffersColumn();
    private static final Map<FixedSamplerName, Integer> FULLSCREEN_COLUMN = buildFullscreenColumn();
    private static final Map<FixedSamplerName, Integer> SHADOW_WINDOW_COLUMN =
        buildShadowWindowColumn();

    private final FixedSamplerPolicyFingerprint fingerprint;

    private AppB3Policy(FixedSamplerPolicyFingerprint fingerprint) {
        this.fingerprint = fingerprint;
    }

    public static AppB3Policy create() {
        return new AppB3Policy(new FixedSamplerPolicyFingerprint(digest()));
    }

    public static FixedSamplerPolicyFingerprint staticFingerprint() {
        return new FixedSamplerPolicyFingerprint(digest());
    }

    /**
     * Case-sensitive exact-name lookup over the 33 legal names (PHASE_5_DOC §4.12.1). Never
     * synthesizes colortex8-15 or any other sentinel.
     */
    public static FixedSamplerLookup lookup(String exactName) {
        Objects.requireNonNull(exactName, "exactName");
        for (FixedSamplerName name : FixedSamplerName.values()) {
            if (name.exactName().equals(exactName)) {
                return new FixedSamplerLookup.Known(name);
            }
        }
        return new FixedSamplerLookup.Unknown(exactName);
    }

    /**
     * Pure fixed-unit resolution for one name in one stage domain (§4.12.1). When
     * {@code name} is the conditional {@code shadow}, the layout's declarations decide the
     * unit: a direct sampler-compatible {@code watershadow} declaration moves {@code shadow}
     * from unit 4 to unit 5.
     */
    public static FixedSamplerResolution resolve(FixedSamplerName name, ProgramSamplerLayout layout,
            StageId stage, StageBand band) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(band, "band");
        Domain domain = domainOf(stage, band);
        if (domain == null) {
            return new FixedSamplerResolution.UnsupportedDomain(name, stage, band);
        }
        boolean watershadow = layout instanceof ProgramSamplerLayout.Shader shader
            && hasDirectWatershadow(shader.declarations());
        Integer unit = unitIn(name, domain, stage, watershadow);
        if (unit == null) {
            return new FixedSamplerResolution.UnsupportedDomain(name, stage, band);
        }
        return new FixedSamplerResolution.Resolved(name, unit);
    }

    /** {@code null} when the name has no fixed unit in this window. */
    private static Integer unitIn(FixedSamplerName name, Domain domain, StageId stage,
            boolean watershadow) {
        if (name == FixedSamplerName.SHADOW) {
            return watershadow ? 5 : 4;
        }
        if (domain == Domain.GBUFFERS && name == FixedSamplerName.TEX
                && stage != StageId.SHADOW) {
            // RESEARCH B.3: tex is the shadow-stage alias of texture.
            return null;
        }
        return columnOf(domain).get(name);
    }

    /**
     * The conditional {@code shadow} rule (§4.12.1): unit 5 with a direct sampler-compatible
     * {@code watershadow} declaration in the same projection, unit 4 otherwise.
     */
    private static boolean hasDirectWatershadow(List<ProgramSamplerDeclaration> declarations) {
        for (ProgramSamplerDeclaration declaration : declarations) {
            if (WATERSHADOW.equals(declaration.exactName())
                    && declaration.type() instanceof DeclaredGlslType.Sampler) {
                return true;
            }
        }
        return false;
    }

    private static Map<FixedSamplerName, Integer> columnOf(Domain domain) {
        return switch (domain) {
            case GBUFFERS -> GBUFFERS_COLUMN;
            case SHADOW_WINDOW -> SHADOW_WINDOW_COLUMN;
            case FULLSCREEN -> FULLSCREEN_COLUMN;
        };
    }

    /**
     * Stage family classification (§4.12.1): both gbuffers bands and the shadow stage share
     * the gbuffers column; deferred/composite/final share the fullscreen column; compute and
     * unwired domains (setup, begin, shadowcomp, prepare) are unsupported, never treated as
     * composite. {@code null} marks the unsupported family.
     */
    private static Domain domainOf(StageId stage, StageBand band) {
        return switch (stage) {
            case GBUFFERS, SHADOW -> switch (band) {
                case BEFORE_GBUFFERS, AFTER_SHADOW -> Domain.SHADOW_WINDOW;
                case GBUFFERS_OPAQUE, GBUFFERS_TRANSLUCENT, SHADOW -> Domain.GBUFFERS;
                default -> null;
            };
            case DEFERRED, COMPOSITE, FINAL -> Domain.FULLSCREEN;
            case SETUP, BEGIN, SHADOWCOMP, PREPARE -> null;
        };
    }

    /**
     * The shadow window (after-shadow / before-gbuffers bands of the gbuffers/shadow
     * stages): only the shadow-estate machinery has fixed units here.
     */
    private static Map<FixedSamplerName, Integer> buildShadowWindowColumn() {
        Map<FixedSamplerName, Integer> column = new EnumMap<>(FixedSamplerName.class);
        column.put(FixedSamplerName.SHADOWTEX0, 4);
        column.put(FixedSamplerName.WATERSHADOW, 4);
        column.put(FixedSamplerName.SHADOWTEX1, 5);
        column.put(FixedSamplerName.SHADOWCOLOR0, 13);
        column.put(FixedSamplerName.SHADOWCOLOR, 13);
        column.put(FixedSamplerName.SHADOWCOLOR1, 14);
        column.put(FixedSamplerName.NOISETEX, 15);
        return Map.copyOf(column);
    }

    private static Map<FixedSamplerName, Integer> buildGbuffersColumn() {
        Map<FixedSamplerName, Integer> column = new EnumMap<>(FixedSamplerName.class);
        column.put(FixedSamplerName.TEXTURE, 0);
        column.put(FixedSamplerName.TEX, 0);
        column.put(FixedSamplerName.LIGHTMAP, 1);
        column.put(FixedSamplerName.NORMALS, 2);
        column.put(FixedSamplerName.SPECULAR, 3);
        column.put(FixedSamplerName.SHADOWTEX0, 4);
        column.put(FixedSamplerName.WATERSHADOW, 4);
        column.put(FixedSamplerName.SHADOWTEX1, 5);
        column.put(FixedSamplerName.DEPTHTEX0, 6);
        column.put(FixedSamplerName.GAUX1, 7);
        column.put(FixedSamplerName.GAUX2, 8);
        column.put(FixedSamplerName.GAUX3, 9);
        column.put(FixedSamplerName.GAUX4, 10);
        column.put(FixedSamplerName.DEPTHTEX1, 11);
        column.put(FixedSamplerName.SHADOWCOLOR0, 13);
        column.put(FixedSamplerName.SHADOWCOLOR, 13);
        column.put(FixedSamplerName.SHADOWCOLOR1, 14);
        column.put(FixedSamplerName.NOISETEX, 15);
        return Map.copyOf(column);
    }

    private static Map<FixedSamplerName, Integer> buildFullscreenColumn() {
        Map<FixedSamplerName, Integer> column = new EnumMap<>(FixedSamplerName.class);
        column.put(FixedSamplerName.COLORTEX0, 0);
        column.put(FixedSamplerName.GCOLOR, 0);
        column.put(FixedSamplerName.COLORTEX1, 1);
        column.put(FixedSamplerName.GDEPTH, 1);
        column.put(FixedSamplerName.COLORTEX2, 2);
        column.put(FixedSamplerName.GNORMAL, 2);
        column.put(FixedSamplerName.COLORTEX3, 3);
        column.put(FixedSamplerName.COMPOSITE, 3);
        column.put(FixedSamplerName.SHADOWTEX0, 4);
        column.put(FixedSamplerName.WATERSHADOW, 4);
        column.put(FixedSamplerName.SHADOWTEX1, 5);
        column.put(FixedSamplerName.DEPTHTEX0, 6);
        column.put(FixedSamplerName.GDEPTHTEX, 6);
        column.put(FixedSamplerName.COLORTEX4, 7);
        column.put(FixedSamplerName.GAUX1, 7);
        column.put(FixedSamplerName.COLORTEX5, 8);
        column.put(FixedSamplerName.GAUX2, 8);
        column.put(FixedSamplerName.COLORTEX6, 9);
        column.put(FixedSamplerName.GAUX3, 9);
        column.put(FixedSamplerName.COLORTEX7, 10);
        column.put(FixedSamplerName.GAUX4, 10);
        column.put(FixedSamplerName.DEPTHTEX1, 11);
        column.put(FixedSamplerName.DEPTHTEX2, 12);
        column.put(FixedSamplerName.SHADOWCOLOR0, 13);
        column.put(FixedSamplerName.SHADOWCOLOR, 13);
        column.put(FixedSamplerName.SHADOWCOLOR1, 14);
        column.put(FixedSamplerName.NOISETEX, 15);
        return Map.copyOf(column);
    }

    /**
     * SHA-256 over the complete policy: domain tag, the conditional rule description, every
     * exact-name-to-unit mapping of both columns (including the gbuffers DEPTHTEX2 absence),
     * the validation issue ordering and the resolver row ordering (§4.12.1: canonical framing
     * over Phase 3 §4.10, producer canonicalization).
     */
    private static String digest() {
        return CanonicalDigest.sha256Hex(
            CanonicalDigest.seq(
                CanonicalDigest.atom(CanonicalDigest.SAMPLER_POLICY_DOMAIN),
                CanonicalDigest.seqOfStrings(
                    "conditional-shadow", "direct-watershadow-declaration-then-unit-5-else-unit-4",
                    "band-windows", "gbuffers-draw-bands-plus-shadow-band-gbuffers-column",
                    "after-shadow-and-before-gbuffers-shadow-machinery-only",
                    "tex-shadow-stage-alias-of-texture",
                    "deferred-composite-final-any-band-fullscreen-column"),
                CanonicalDigest.seq(
                    columnDigest(Domain.GBUFFERS),
                    columnDigest(Domain.FULLSCREEN)),
                CanonicalDigest.seqOfStrings(
                    "validation-order", "band-declaration-order", "declaration-order",
                    "issue-code-declaration-order",
                    "issue-codes", "UNSUPPORTED_NAME", "UNSUPPORTED_STAGE_DOMAIN",
                    "UNSUPPORTED_SHAPE",
                    "conflicts", "unit-ascending-witnesses-declaration-order",
                    "conflict-plus-unsupported", "ConflictingTypes"),
                CanonicalDigest.seqOfStrings(
                    "resolver", "row-order-unit-ascending-then-fixed-name-declaration-order",
                    "fixedfunction-and-virtual", "ready-empty",
                    "initialization", "exact-name-utf8-sorted-every-declared-sampler-once")));
    }

    private static byte[] columnDigest(Domain domain) {
        List<byte[]> rows = new ArrayList<>();
        for (int unit = 0; unit <= 15; unit++) {
            List<String> names = new ArrayList<>();
            for (FixedSamplerName name : FixedSamplerName.values()) {
                if (name == FixedSamplerName.SHADOW) {
                    continue; // conditional: framed by rule, not by column
                }
                Integer mapped = columnOf(domain).get(name);
                if (mapped != null && mapped == unit) {
                    names.add(name.exactName());
                }
            }
            rows.add(CanonicalDigest.seq(
                CanonicalDigest.atom(unit),
                CanonicalDigest.seqOfStrings(names.toArray(String[]::new))));
        }
        return CanonicalDigest.seq(
            CanonicalDigest.atom(domain == Domain.GBUFFERS ? "GBUFFERS_FAMILY" : "FULLSCREEN"),
            CanonicalDigest.seq(rows.toArray(byte[][]::new)));
    }

    @Override
    public FixedSamplerPolicyFingerprint fingerprint() {
        return fingerprint;
    }

    @Override
    public SamplerLayoutValidation validate(StageId effectiveStage, StageBand effectiveBand,
            List<ProgramSamplerDeclaration> declarations) {
        Objects.requireNonNull(effectiveStage, "effectiveStage");
        Objects.requireNonNull(effectiveBand, "effectiveBand");
        Objects.requireNonNull(declarations, "declarations");

        Domain domain = domainOf(effectiveStage, effectiveBand);
        List<SamplerLayoutIssue> issues = new ArrayList<>();
        List<ResolvedDeclaration> resolvedDeclarations = new ArrayList<>();

        if (domain == null) {
            for (ProgramSamplerDeclaration declaration : declarations) {
                issues.add(new SamplerLayoutIssue(SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN,
                    effectiveBand, declaration));
            }
            return finishValidation(declarations, issues, resolvedDeclarations);
        }

        for (ProgramSamplerDeclaration declaration : declarations) {
            DeclaredGlslType type = declaration.type();
            if (!(type instanceof DeclaredGlslType.Sampler sampler) || sampler.multisample()) {
                issues.add(new SamplerLayoutIssue(SamplerLayoutIssueCode.UNSUPPORTED_SHAPE,
                    effectiveBand, declaration));
                continue;
            }
            FixedSamplerLookup lookup = lookup(declaration.exactName());
            switch (lookup) {
                case FixedSamplerLookup.Unknown unknown -> issues.add(new SamplerLayoutIssue(
                    SamplerLayoutIssueCode.UNSUPPORTED_NAME, effectiveBand, declaration));
                case FixedSamplerLookup.Known known -> {
                    Integer unit = unitIn(known.name(), domain, effectiveStage,
                        hasDirectWatershadow(declarations));
                    if (unit == null) {
                        issues.add(new SamplerLayoutIssue(
                            SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN, effectiveBand,
                            declaration));
                    } else {
                        resolvedDeclarations.add(new ResolvedDeclaration(known.name(), sampler,
                            declaration, unit));
                    }
                }
            }
        }
        return finishValidation(declarations, issues, resolvedDeclarations);
    }

    /**
     * Typed outcome assembly (§4.12.1): conflicts win and carry coexisting unsupported
     * evidence; issues are ordered by declaration order then issue-code declaration order;
     * same-unit incompatible shapes conflict with witnesses in declaration order while
     * identical shapes coexist.
     */
    private static SamplerLayoutValidation finishValidation(
            List<ProgramSamplerDeclaration> declarations, List<SamplerLayoutIssue> issues,
            List<ResolvedDeclaration> resolvedDeclarations) {

        Map<Integer, List<ResolvedDeclaration>> byUnit = new TreeMap<>();
        for (ResolvedDeclaration resolved : resolvedDeclarations) {
            byUnit.computeIfAbsent(resolved.unit(), key -> new ArrayList<>()).add(resolved);
        }
        List<FixedUnitSamplerConflict> conflicts = new ArrayList<>();
        for (Map.Entry<Integer, List<ResolvedDeclaration>> entry : byUnit.entrySet()) {
            List<ResolvedDeclaration> witnesses = entry.getValue();
            if (witnesses.size() < 2) {
                continue;
            }
            boolean incompatible = false;
            for (int i = 1; i < witnesses.size() && !incompatible; i++) {
                incompatible = !witnesses.get(0).shape().equals(witnesses.get(i).shape());
            }
            if (incompatible) {
                conflicts.add(new FixedUnitSamplerConflict(entry.getKey(),
                    witnesses.stream().map(ResolvedDeclaration::declaration).toList()));
            }
        }

        Comparator<SamplerLayoutIssue> issueOrder = Comparator
            .comparingInt((SamplerLayoutIssue issue) -> issue.declaration().declarationOrder())
            .thenComparing(issue -> issue.code());
        issues.sort(issueOrder);

        if (!conflicts.isEmpty()) {
            return new SamplerLayoutValidation.ConflictingTypes(conflicts, List.copyOf(issues));
        }
        if (!issues.isEmpty()) {
            return new SamplerLayoutValidation.Unsupported(List.copyOf(issues));
        }
        return new SamplerLayoutValidation.Valid();
    }

    @Override
    public List<SamplerUnitAssignment> initializationAssignments(
            ProgramSamplerLayout.Shader layout) {
        Objects.requireNonNull(layout, "layout");
        requireSamePolicy(layout);
        if (!(layout.validation() instanceof SamplerLayoutValidation.Valid)) {
            throw new IllegalStateException(
                "initializationAssignments requires a Valid sampler layout");
        }
        List<StageBand> bands = new ArrayList<>(layout.validatedBands());
        bands.sort(Comparator.comparingInt(StageBand::ordinal));
        if (bands.isEmpty()) {
            throw new IllegalStateException("validated shader layout carries no bands");
        }
        Map<String, Integer> assignments = null;
        for (StageBand band : bands) {
            FixedSamplerPlanResult planResult = RESOLVER.resolve(layout, layout.effectiveStage(),
                band);
            if (!(planResult instanceof FixedSamplerPlanResult.Ready ready)) {
                throw new IllegalStateException("validated band resolved to " + planResult);
            }
            Map<String, Integer> bandAssignments = new LinkedHashMap<>();
            for (ResolvedSamplerBinding binding : ready.bindings()) {
                bandAssignments.put(binding.exactName(), binding.unit());
            }
            if (assignments == null) {
                assignments = bandAssignments;
                continue;
            }
            if (!assignments.equals(bandAssignments)) {
                throw new IllegalStateException("validated bands disagree on exact-name/unit pairs");
            }
        }
        Map<String, Integer> frozen = assignments;
        List<SamplerUnitAssignment> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : new TreeMap<>(frozen).entrySet()) {
            result.add(new SamplerUnitAssignment(entry.getKey(), entry.getValue()));
        }
        return List.copyOf(result);
    }

    private void requireSamePolicy(ProgramSamplerLayout layout) {
        if (!layout.policyFingerprint().equals(fingerprint)) {
            throw new IllegalStateException(
                "layout policy fingerprint mismatch: layout was validated by another policy");
        }
    }

    /** One declaration resolved to its fixed unit within one validated band. */
    private record ResolvedDeclaration(FixedSamplerName name, DeclaredGlslType.Sampler shape,
            ProgramSamplerDeclaration declaration, int unit) {
    }

    /**
     * The pure resolver over this policy (§4.12.1): fixed-function and virtual layouts are
     * Ready with no rows; shader layouts validate first and then resolve every declaration to
     * a binding row ordered by ascending unit, then fixed-name declaration order. Unsupported
     * shader domains return Invalid with the typed evidence, never an implicit map.
     */
    final FixedSamplerResolver RESOLVER = new FixedSamplerResolver() {

        @Override
        public FixedSamplerPlanResult resolve(ProgramSamplerLayout layout, StageId stage,
                StageBand band) {
            Objects.requireNonNull(layout, "layout");
            Objects.requireNonNull(stage, "stage");
            Objects.requireNonNull(band, "band");
            if (layout instanceof ProgramSamplerLayout.FixedFunctionEmpty
                    || layout instanceof ProgramSamplerLayout.VirtualNotApplicable) {
                requireSamePolicy(layout);
                return new FixedSamplerPlanResult.Ready(List.of(), fingerprint);
            }
            if (!(layout instanceof ProgramSamplerLayout.Shader shader)) {
                throw new IllegalArgumentException("unknown sampler layout variant: " + layout);
            }
            requireSamePolicy(shader);
            SamplerLayoutValidation validation = validate(shader.effectiveStage(), band,
                shader.declarations());
            if (!(validation instanceof SamplerLayoutValidation.Valid)) {
                return new FixedSamplerPlanResult.Invalid(validation);
            }
            List<ResolvedSamplerBinding> bindings = new ArrayList<>();
            Domain domain = domainOf(shader.effectiveStage(), band);
            for (ProgramSamplerDeclaration declaration : shader.declarations()) {
                if (!(declaration.type() instanceof DeclaredGlslType.Sampler shape)
                        || shape.arrayed() || shape.multisample()) {
                    continue;
                }
                FixedSamplerLookup lookup = lookup(declaration.exactName());
                if (!(lookup instanceof FixedSamplerLookup.Known known)) {
                    continue;
                }
                Integer unit = unitIn(known.name(), domain, shader.effectiveStage(),
                    hasDirectWatershadow(shader.declarations()));
                if (unit == null) {
                    continue;
                }
                bindings.add(new ResolvedSamplerBinding(declaration.exactName(), shape, unit));
            }
            // Ascending unit; within a unit the layout's declaration order (stable sort).
            bindings.sort(Comparator.comparingInt(ResolvedSamplerBinding::unit));
            return new FixedSamplerPlanResult.Ready(List.copyOf(bindings), fingerprint);
        }
    };

    /** The resolver instance handed out by the public factory. */
    public FixedSamplerResolver resolver() {
        return RESOLVER;
    }

    /** The App B.3 unit map rows shared with planning and binding (unit -> names). */
    static List<List<String>> columnRows(Domain domain) {
        Map<Integer, List<String>> rows = new LinkedHashMap<>();
        for (int unit = 0; unit <= 15; unit++) {
            rows.put(unit, new ArrayList<>());
        }
        for (Map.Entry<FixedSamplerName, Integer> entry : columnOf(domain).entrySet()) {
            rows.get(entry.getValue()).add(entry.getKey().exactName());
        }
        for (List<String> names : rows.values()) {
            names.sort(Comparator.comparingInt(AppB3Policy::declarationOrdinal));
        }
        return List.copyOf(rows.values());
    }

    private static int declarationOrdinal(String exactName) {
        for (FixedSamplerName name : FixedSamplerName.values()) {
            if (name.exactName().equals(exactName)) {
                return name.ordinal();
            }
        }
        return Integer.MAX_VALUE;
    }

    static Set<FixedSamplerName> supportedNames(Domain domain) {
        return columnOf(domain).keySet();
    }
}
