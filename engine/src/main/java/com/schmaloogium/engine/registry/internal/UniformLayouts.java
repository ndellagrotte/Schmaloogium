// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramUniformDeclaration;
import com.schmaloogium.engine.registry.ProgramUniformDeclarationSite;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.ProgramUniformLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.SamplerLayoutIssue;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;
import com.schmaloogium.engine.registry.FixedUnitSamplerConflict;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.DeclaredUniform;
import com.schmaloogium.engine.preprocess.DeclaredUniformCatalog;
import com.schmaloogium.engine.preprocess.ShaderSourceStage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;

/**
 * The pure uniform merge and sampler projection (PHASE_4_DOC §4.7). Visits stages VERTEX,
 * GEOMETRY, FRAGMENT, COMPUTE with declarations in each catalog's published token order;
 * merges equal-name/equal-type repetitions retaining every attributed site; a same-name
 * unequal-type repetition is the source-attributed {@code UNIFORM_TYPE_CONFLICT}. The
 * sampler projection excludes nonsamplers, retains direct sampler values and keeps
 * sampler-bearing arrays/structs as unsupported shapes.
 */
final class UniformLayouts {

    record Merged(
            ProgramUniformLayout layout,
            List<ProgramSamplerDeclaration> samplers,
            String typeConflictName) {

        boolean hasTypeConflict() {
            return typeConflictName != null;
        }
    }

    private UniformLayouts() {
    }

    /** Merge stage catalogs in linked order; stage catalogs must arrive in
     *  VERTEX, GEOMETRY, FRAGMENT, COMPUTE order with token order inside. */
    static Merged merge(List<DeclaredUniformCatalog> stageCatalogs) {
        Map<String, ProgramUniformDeclaration> byName = new LinkedHashMap<>();
        String conflictName = null;
        for (DeclaredUniformCatalog catalog : stageCatalogs) {
            for (DeclaredUniform declared : catalog.declarations()) {
                ProgramUniformDeclarationSite site = new ProgramUniformDeclarationSite(
                    declared.declaringStage(),
                    declared.location(),
                    catalog.materialization());
                ProgramUniformDeclaration existing = byName.get(declared.exactName());
                if (existing == null) {
                    byName.put(declared.exactName(), new ProgramUniformDeclaration(
                        declared.exactName(), declared.type(), List.of(site)));
                } else if (existing.type().equals(declared.type())) {
                    byName.put(declared.exactName(), new ProgramUniformDeclaration(
                        declared.exactName(), declared.type(),
                        append(existing.sites(), site)));
                } else if (conflictName == null) {
                    conflictName = declared.exactName();
                }
            }
        }
        ProgramUniformLayoutFingerprint fingerprint = new ProgramUniformLayoutFingerprint(
            fingerprint(byName));
        TreeMap<String, ProgramUniformDeclaration> ordered = new TreeMap<>(byName);
        ProgramUniformLayout layout = new ProgramUniformLayout(fingerprint, ordered);
        return new Merged(layout, projectSamplers(ordered), conflictName);
    }

    private static List<ProgramUniformDeclarationSite> append(
            List<ProgramUniformDeclarationSite> sites, ProgramUniformDeclarationSite site) {
        List<ProgramUniformDeclarationSite> merged = new ArrayList<>(sites.size() + 1);
        merged.addAll(sites);
        merged.add(site);
        return List.copyOf(merged);
    }

    /** The ordered sampler projection; declarationOrder is the zero-based first-occurrence
     *  ordinal in this projection. */
    static List<ProgramSamplerDeclaration> projectSamplers(
            TreeMap<String, ProgramUniformDeclaration> orderedDeclarations) {
        List<ProgramSamplerDeclaration> out = new ArrayList<>();
        int order = 0;
        for (ProgramUniformDeclaration declaration : orderedDeclarations.values()) {
            if (containsSampler(declaration.type())) {
                out.add(new ProgramSamplerDeclaration(
                    declaration.exactName(), declaration.type(), order, declaration.sites()));
                order++;
            }
        }
        return List.copyOf(out);
    }

    private static boolean containsSampler(DeclaredGlslType type) {
        if (type instanceof DeclaredGlslType.Sampler) {
            return true;
        }
        if (type instanceof DeclaredGlslType.Array array) {
            return containsSampler(array.element());
        }
        if (type instanceof DeclaredGlslType.Struct struct) {
            return struct.fields().stream().anyMatch(f -> containsSampler(f.type()));
        }
        return false;
    }

    /** Canonical uniform-layout digest: schema tag, exact-name order, types, sites. */
    static String fingerprint(Map<String, ProgramUniformDeclaration> byName) {
        List<byte[]> parts = new ArrayList<>();
        parts.add(CanonicalFraming.atom(CanonicalFraming.UNIFORM_LAYOUT_DOMAIN));
        List<String> names = new java.util.ArrayList<>(new TreeMap<>(byName).keySet());
        parts.add(CanonicalFraming.seqOfStrings(names));
        for (String name : names) {
            ProgramUniformDeclaration d = byName.get(name);
            parts.add(CanonicalFraming.atom(name));
            parts.add(CanonicalFraming.atom(structuralType(d.type())));
            for (ProgramUniformDeclarationSite site : d.sites()) {
                parts.add(CanonicalFraming.atom(site.declaringStage().name()));
                parts.add(CanonicalFraming.atom(site.location().source().path().canonicalString()));
                parts.add(CanonicalFraming.atom(site.location().logicalLine()));
                parts.add(CanonicalFraming.atom(site.location().column()));
                parts.add(CanonicalFraming.atom(site.materialization().value()));
            }
        }
        return CanonicalFraming.sha256Hex(parts.toArray(new byte[0][]));
    }

    /** Structural rendering of a Phase 3 type for hashing; aggregates retain every field. */
    static String structuralType(DeclaredGlslType type) {
        return switch (type) {
            case DeclaredGlslType.Scalar s -> "Scalar:" + s.kind().name();
            case DeclaredGlslType.Vector v -> "Vector:" + v.component().name() + ":" + v.width();
            case DeclaredGlslType.Matrix m -> "Matrix:" + m.component().name() + ":"
                + m.columns() + ":" + m.rows();
            case DeclaredGlslType.Sampler s -> "Sampler:" + s.sample().name() + ":"
                + s.dimension().name() + ":" + s.arrayed() + ":" + s.shadow() + ":"
                + s.multisample();
            case DeclaredGlslType.Image i -> "Image:" + i.sample().name() + ":"
                + i.dimension().name() + ":" + i.arrayed() + ":" + i.multisample();
            case DeclaredGlslType.AtomicCounter c -> "AtomicCounter";
            case DeclaredGlslType.Array a -> "Array:" + structuralType(a.element()) + ":"
                + a.extents().size();
            case DeclaredGlslType.Struct s -> "Struct:" + s.declaredName().orElse("") + ":"
                + s.shape().value();
        };
    }

    /** Run the policy over every provider-permitted band in canonical StageBand order,
     *  unioning issues and conflicts without losing witnesses (D-P4-36). */
    static SamplerLayoutValidation validateAcrossBands(
            FixedSamplerLayoutPolicy policy,
            StageId effectiveStage,
            List<StageBand> permittedBands,
            List<ProgramSamplerDeclaration> declarations) {
        List<StageBand> canonical = new ArrayList<>(permittedBands);
        canonical.sort(Comparator.comparingInt(b -> b.ordinal()));
        List<FixedUnitSamplerConflict> conflicts = new ArrayList<>();
        Map<String, FixedUnitSamplerConflict> conflictByKey = new LinkedHashMap<>();
        List<SamplerLayoutIssue> issues = new ArrayList<>();
        boolean anyConflict = false;
        boolean anyUnsupported = false;
        for (StageBand band : canonical) {
            SamplerLayoutValidation result =
                policy.validate(effectiveStage, band, declarations);
            switch (result) {
                case SamplerLayoutValidation.Valid valid -> {
                }
                case SamplerLayoutValidation.ConflictingTypes conflicting -> {
                    anyConflict = true;
                    for (FixedUnitSamplerConflict conflict : conflicting.conflicts()) {
                        String key = conflict.unit() + ":" + conflict.witnesses().stream()
                            .map(w -> w.exactName() + "/" + structuralType(w.type()))
                            .reduce("", (a, b) -> a + ";" + b);
                        conflictByKey.putIfAbsent(key, conflict);
                    }
                    issues.addAll(conflicting.otherIssues());
                }
                case SamplerLayoutValidation.Unsupported unsupported -> {
                    anyUnsupported = true;
                    issues.addAll(unsupported.issues());
                }
            }
        }
        conflicts.addAll(conflictByKey.values());
        List<SamplerLayoutIssue> canonicalIssues = canonicalIssues(issues);
        if (anyConflict) {
            return new SamplerLayoutValidation.ConflictingTypes(
                List.copyOf(conflicts), canonicalIssues);
        }
        if (anyUnsupported) {
            return new SamplerLayoutValidation.Unsupported(canonicalIssues);
        }
        return new SamplerLayoutValidation.Valid();
    }

    /** Canonical issue order: StageBand declaration order, declarationOrder, issue code. */
    static List<SamplerLayoutIssue> canonicalIssues(List<SamplerLayoutIssue> issues) {
        List<SamplerLayoutIssue> sorted = new ArrayList<>(issues);
        sorted.sort(Comparator
            .comparingInt((SamplerLayoutIssue i) -> i.band().ordinal())
            .thenComparingInt(i -> i.declaration().declarationOrder())
            .thenComparingInt(i -> i.code().ordinal()));
        return List.copyOf(sorted);
    }

    /** The canonical sampler-layout digest over the full Shader payload. */
    static String shaderLayoutDigest(
            ProgramSamplerLayout.Shader layout, List<SamplerUnitAssignment> assignments) {
        List<byte[]> parts = new ArrayList<>();
        parts.add(CanonicalFraming.atom(CanonicalFraming.SAMPLER_LAYOUT_DOMAIN));
        parts.add(CanonicalFraming.atom(layout.effectiveStage().name()));
        List<StageBand> bands = new ArrayList<>(layout.validatedBands());
        bands.sort(Comparator.comparingInt(StageBand::ordinal));
        parts.add(CanonicalFraming.seqOfStrings(bands.stream().map(StageBand::name).toList()));
        for (ProgramSamplerDeclaration d : layout.declarations()) {
            parts.add(CanonicalFraming.atom(d.exactName()));
            parts.add(CanonicalFraming.atom(structuralType(d.type())));
            parts.add(CanonicalFraming.atom(d.declarationOrder()));
            for (var site : d.sites()) {
                parts.add(CanonicalFraming.atom(site.materialization().value()));
            }
        }
        parts.add(CanonicalFraming.atom(layout.validation() instanceof SamplerLayoutValidation.Valid
            ? "Valid"
            : layout.validation() instanceof SamplerLayoutValidation.ConflictingTypes
                ? "ConflictingTypes" : "Unsupported"));
        if (layout.validation() instanceof SamplerLayoutValidation.ConflictingTypes conflicting) {
            for (FixedUnitSamplerConflict conflict : conflicting.conflicts()) {
                parts.add(CanonicalFraming.atom(conflict.unit()));
            }
        }
        for (var assignment : assignments) {
            parts.add(CanonicalFraming.atom(assignment.exactName()));
            parts.add(CanonicalFraming.atom(assignment.unit()));
        }
        return CanonicalFraming.sha256Hex(parts.toArray(new byte[0][]));
    }

    /** The digest of a fixed/virtual (declaration-free) layout variant. */
    static String emptyLayoutDigest(String variantTag, FixedSamplerPolicyFingerprint policy) {
        return CanonicalFraming.sha256Hex(
            CanonicalFraming.atom(CanonicalFraming.SAMPLER_LAYOUT_DOMAIN),
            CanonicalFraming.atom(variantTag),
            CanonicalFraming.atom(policy.value()));
    }
}
