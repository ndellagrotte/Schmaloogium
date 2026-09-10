// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.FixedUnitSamplerConflict;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.SamplerLayoutIssue;
import com.schmaloogium.engine.registry.SamplerLayoutIssueCode;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * App B.3 fixed sampler policy contract tests (PHASE_5_DOC §4.12.1, §8.3): the exact
 * 33-name/16-unit map per stage family, the conditional shadow rule, typed validation
 * evidence and ordering, the deterministic policy digest, and resolver/initialization
 * behavior.
 */
class FixedSamplerPoliciesTest {

    private static final FixedSamplerPolicyFingerprint FINGERPRINT =
        FixedSamplerPolicies.appB3Fingerprint();
    private static final ProgramSamplerLayoutFingerprint LAYOUT_FP =
        new ProgramSamplerLayoutFingerprint("layout-fp");

    private static DeclaredGlslType.Sampler sampler2d() {
        return new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2, false,
            false, false);
    }

    private static DeclaredGlslType.Sampler sampler3d() {
        return new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D3, false,
            false, false);
    }

    private static DeclaredGlslType.Sampler sampler2dArray() {
        return new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2, true,
            false, false);
    }

    private static DeclaredGlslType.Sampler sampler2dMs() {
        return new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2, false,
            false, true);
    }

    private static DeclaredGlslType samplerArray() {
        return new DeclaredGlslType.Array(sampler2d(), List.of());
    }

    private static ProgramSamplerDeclaration decl(String name, DeclaredGlslType type,
            int order) {
        return new ProgramSamplerDeclaration(name, type, order, List.of());
    }

    private static ProgramSamplerLayout.Shader shaderLayout(StageId stage,
            Set<StageBand> bands, List<ProgramSamplerDeclaration> declarations,
            SamplerLayoutValidation validation) {
        return new ProgramSamplerLayout.Shader(LAYOUT_FP, FINGERPRINT, stage, bands,
            declarations, validation);
    }

    private static Map<String, Integer> columnUnits(List<StageId> stages,
            StageBand band) {
        Map<String, Integer> units = new HashMap<>();
        for (FixedSamplerName name : FixedSamplerName.values()) {
            if (name == FixedSamplerName.SHADOW) {
                continue; // conditional: framed by the watershadow rule, not a column cell
            }
            for (StageId stage : stages) {
                FixedSamplerResolution resolution = FixedSamplerPolicies.resolve(name, null,
                    stage, band);
                if (resolution instanceof FixedSamplerResolution.Resolved resolved) {
                    Integer previous = units.putIfAbsent(name.exactName(), resolved.unit());
                    if (previous != null) {
                        assertEquals(previous, resolved.unit(),
                            name.exactName() + " must resolve to one unit within a column");
                    }
                }
            }
        }
        return units;
    }

    @Test
    void policyIsStableSingletonWithDigestFingerprint() {
        assertSame(FixedSamplerPolicies.appB3(), FixedSamplerPolicies.appB3());
        assertEquals(FixedSamplerPolicies.appB3().fingerprint(), FINGERPRINT);
        assertTrue(FINGERPRINT.value().matches("[0-9a-f]{64}"),
            "fingerprint must be a lowercase sha-256 hex digest");
        assertNotSame(FixedSamplerPolicies.appB3Fingerprint(),
            new FixedSamplerPolicyFingerprint("other"));
    }

    @Test
    void gbuffersAndShadowShareTheGbuffersColumn() {
        Map<String, Integer> units = columnUnits(List.of(StageId.GBUFFERS, StageId.SHADOW),
            StageBand.GBUFFERS_OPAQUE);
        Map<String, Integer> expected = new HashMap<>();
        expected.put("texture", 0);
        expected.put("tex", 0);
        expected.put("lightmap", 1);
        expected.put("normals", 2);
        expected.put("specular", 3);
        expected.put("shadowtex0", 4);
        expected.put("watershadow", 4);
        expected.put("shadowtex1", 5);
        expected.put("depthtex0", 6);
        expected.put("gaux1", 7);
        expected.put("gaux2", 8);
        expected.put("gaux3", 9);
        expected.put("gaux4", 10);
        expected.put("depthtex1", 11);
        expected.put("shadowcolor0", 13);
        expected.put("shadowcolor", 13);
        expected.put("shadowcolor1", 14);
        expected.put("noisetex", 15);
        assertEquals(expected, units);
        assertFalse(units.containsKey("depthtex2"), "depthtex2 has no gbuffers column");
        assertFalse(units.containsKey("colortex4"), "colortex aliases are fullscreen-only");
        assertFalse(units.containsKey("gcolor"), "gcolor is fullscreen-only");
    }

    @Test
    void deferredCompositeAndFinalShareTheFullscreenColumn() {
        Map<String, Integer> units = columnUnits(List.of(StageId.DEFERRED, StageId.COMPOSITE,
            StageId.FINAL), StageBand.FRAME_END);
        Map<String, Integer> expected = new HashMap<>();
        expected.put("colortex0", 0);
        expected.put("gcolor", 0);
        expected.put("colortex1", 1);
        expected.put("gdepth", 1);
        expected.put("colortex2", 2);
        expected.put("gnormal", 2);
        expected.put("colortex3", 3);
        expected.put("composite", 3);
        expected.put("shadowtex0", 4);
        expected.put("watershadow", 4);
        expected.put("shadowtex1", 5);
        expected.put("depthtex0", 6);
        expected.put("gdepthtex", 6);
        expected.put("colortex4", 7);
        expected.put("gaux1", 7);
        expected.put("colortex5", 8);
        expected.put("gaux2", 8);
        expected.put("colortex6", 9);
        expected.put("gaux3", 9);
        expected.put("colortex7", 10);
        expected.put("gaux4", 10);
        expected.put("depthtex1", 11);
        expected.put("depthtex2", 12);
        expected.put("shadowcolor0", 13);
        expected.put("shadowcolor", 13);
        expected.put("shadowcolor1", 14);
        expected.put("noisetex", 15);
        assertEquals(expected, units);
        assertFalse(units.containsKey("texture"), "texture is gbuffers-only");
        assertFalse(units.containsKey("tex"), "tex is gbuffers-only");
        assertFalse(units.containsKey("lightmap"), "lightmap is gbuffers-only");
        assertFalse(units.containsKey("normals"), "normals is gbuffers-only");
        assertFalse(units.containsKey("specular"), "specular is gbuffers-only");
    }

    @Test
    void bothColumnsCoverThirtyTwoDistinctNamesBesidesConditionalShadow() {
        Map<String, Integer> gbuffers = columnUnits(List.of(StageId.GBUFFERS, StageId.SHADOW),
            StageBand.GBUFFERS_OPAQUE);
        Map<String, Integer> fullscreen = columnUnits(List.of(StageId.FINAL),
            StageBand.SCREEN);
        Set<String> union = new java.util.HashSet<>(gbuffers.keySet());
        union.addAll(fullscreen.keySet());
        assertEquals(32, union.size());
    }

    @Test
    void unwiredStageDomainsSupportNothing() {
        for (StageId stage : List.of(StageId.SETUP, StageId.BEGIN, StageId.SHADOWCOMP,
                StageId.PREPARE)) {
            FixedSamplerResolution resolution = FixedSamplerPolicies.resolve(
                FixedSamplerName.TEXTURE, null, stage, StageBand.LOAD_OR_RESIZE);
            assertInstanceOf(FixedSamplerResolution.UnsupportedDomain.class, resolution,
                stage + " must support no fixed name");
        }
    }

    @Test
    void lookupIsExactCaseSensitiveOverThirtyThreeNames() {
        assertInstanceOf(FixedSamplerLookup.Known.class,
            FixedSamplerPolicies.lookup("colortex0"));
        assertInstanceOf(FixedSamplerLookup.Known.class, FixedSamplerPolicies.lookup("tex"));
        assertInstanceOf(FixedSamplerLookup.Unknown.class,
            FixedSamplerPolicies.lookup("ColorTex0"));
        assertInstanceOf(FixedSamplerLookup.Unknown.class,
            FixedSamplerPolicies.lookup("colortex8"));
        for (int index = 8; index <= 15; index++) {
            assertInstanceOf(FixedSamplerLookup.Unknown.class,
                FixedSamplerPolicies.lookup("colortex" + index),
                "no colortex" + index + " sentinel may exist");
        }
        assertThrows(NullPointerException.class, () -> FixedSamplerPolicies.lookup(null));
    }

    @Test
    void conditionalShadowUsesUnitFiveOnlyWithDirectWatershadow() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        List<ProgramSamplerDeclaration> withWatershadow = List.of(
            decl("watershadow", sampler2d(), 0),
            decl("shadow", sampler2d(), 1));
        SamplerLayoutValidation valid = policy.validate(StageId.GBUFFERS,
            StageBand.BEFORE_GBUFFERS, withWatershadow);
        assertInstanceOf(SamplerLayoutValidation.Valid.class, valid);

        List<ProgramSamplerDeclaration> withArrayedWatershadow = List.of(
            decl("watershadow", sampler2dArray(), 0),
            decl("shadow", sampler2d(), 1));
        assertInstanceOf(SamplerLayoutValidation.Valid.class,
            policy.validate(StageId.GBUFFERS, StageBand.BEFORE_GBUFFERS,
                withArrayedWatershadow));

        List<ProgramSamplerDeclaration> withoutWatershadow = List.of(
            decl("shadow", sampler2d(), 0));
        assertInstanceOf(SamplerLayoutValidation.Valid.class,
            policy.validate(StageId.GBUFFERS, StageBand.BEFORE_GBUFFERS, withoutWatershadow));

        // The resolver rows expose the conditional unit decision.
        FixedSamplerPlanResult with = FixedSamplerPolicies.resolver().resolve(
            shaderLayout(StageId.GBUFFERS, Set.of(StageBand.BEFORE_GBUFFERS), withWatershadow,
                valid),
            StageId.GBUFFERS, StageBand.BEFORE_GBUFFERS);
        FixedSamplerPlanResult.Ready readyWith = assertInstanceOf(
            FixedSamplerPlanResult.Ready.class, with);
        assertEquals(5, readyWith.bindings().stream()
            .filter(binding -> binding.exactName().equals("shadow")).findFirst().orElseThrow()
            .unit());

        FixedSamplerPlanResult without = FixedSamplerPolicies.resolver().resolve(
            shaderLayout(StageId.GBUFFERS, Set.of(StageBand.BEFORE_GBUFFERS),
                withoutWatershadow, valid),
            StageId.GBUFFERS, StageBand.BEFORE_GBUFFERS);
        FixedSamplerPlanResult.Ready readyWithout = assertInstanceOf(
            FixedSamplerPlanResult.Ready.class, without);
        assertEquals(4, readyWithout.bindings().stream()
            .filter(binding -> binding.exactName().equals("shadow")).findFirst().orElseThrow()
            .unit());
    }

    @Test
    void validationAcceptsAliasesAndCoexistingIdenticalShapes() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        SamplerLayoutValidation valid = policy.validate(StageId.DEFERRED,
            StageBand.BETWEEN_GBUFFERS, List.of(
                decl("colortex0", sampler2d(), 0),
                decl("gcolor", sampler2d(), 1),
                decl("depthtex1", sampler2d(), 2)));
        assertInstanceOf(SamplerLayoutValidation.Valid.class, valid);
    }

    @Test
    void validationReportsUnknownNameWrongDomainAndBadShapesInOrder() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        List<ProgramSamplerDeclaration> declarations = List.of(
            decl("custom_sampler", sampler2d(), 0), // UNSUPPORTED_NAME
            decl("texture", sampler2d(), 1), // fullscreen declaration order 3: UNSUPPORTED_STAGE_DOMAIN
            decl("depthtex2", sampler2d(), 2), // gbuffers: UNSUPPORTED_STAGE_DOMAIN
            decl("array_sampler", samplerArray(), 3), // UNSUPPORTED_SHAPE
            decl("ms_shadowtex", sampler2dMs(), 4)); // UNSUPPORTED_SHAPE

        List<SamplerLayoutIssue> fullscreenIssues = collectIssues(policy.validate(
            StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS, declarations));
        assertEquals(List.of(SamplerLayoutIssueCode.UNSUPPORTED_NAME,
            SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN,
            SamplerLayoutIssueCode.UNSUPPORTED_SHAPE,
            SamplerLayoutIssueCode.UNSUPPORTED_SHAPE), codes(fullscreenIssues));
        assertEquals("custom_sampler", fullscreenIssues.get(0).declaration().exactName());

        List<SamplerLayoutIssue> gbuffersIssues = collectIssues(policy.validate(
            StageId.GBUFFERS, StageBand.BEFORE_GBUFFERS, declarations));
        assertEquals(List.of(SamplerLayoutIssueCode.UNSUPPORTED_NAME,
            SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN,
            SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN,
            SamplerLayoutIssueCode.UNSUPPORTED_SHAPE,
            SamplerLayoutIssueCode.UNSUPPORTED_SHAPE), codes(gbuffersIssues));
    }

    @Test
    void unwiredDomainsReportEveryDeclarationAsUnsupportedStageDomain() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        SamplerLayoutValidation validation = policy.validate(StageId.SETUP,
            StageBand.LOAD_OR_RESIZE, List.of(decl("texture", sampler2d(), 0),
                decl("colortex0", sampler2d(), 1)));
        SamplerLayoutValidation.Unsupported unsupported = assertInstanceOf(
            SamplerLayoutValidation.Unsupported.class, validation);
        assertEquals(List.of(SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN,
            SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN), codes(unsupported.issues()));
    }

    @Test
    void conflictingShapesOnOneUnitYieldConflictingTypesWithOrderedWitnesses() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        List<ProgramSamplerDeclaration> declarations = List.of(
            decl("colortex0", sampler3d(), 0),
            decl("gcolor", sampler2d(), 1));
        SamplerLayoutValidation validation = policy.validate(StageId.DEFERRED,
            StageBand.BETWEEN_GBUFFERS, declarations);
        SamplerLayoutValidation.ConflictingTypes conflicting = assertInstanceOf(
            SamplerLayoutValidation.ConflictingTypes.class, validation);
        assertEquals(1, conflicting.conflicts().size());
        FixedUnitSamplerConflict conflict = conflicting.conflicts().get(0);
        assertEquals(0, conflict.unit());
        assertEquals(List.of("colortex0", "gcolor"),
            conflict.witnesses().stream().map(ProgramSamplerDeclaration::exactName).toList());
    }

    @Test
    void identicalShapesOnOneUnitNeverConflict() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        SamplerLayoutValidation validation = policy.validate(StageId.FINAL,
            StageBand.SCREEN, List.of(
                decl("colortex0", sampler2d(), 0),
                decl("gcolor", sampler2d(), 1)));
        assertInstanceOf(SamplerLayoutValidation.Valid.class, validation);
    }

    @Test
    void resolverReturnsEmptyReadyForFixedFunctionAndVirtualLayouts() {
        FixedSamplerPlanResult fixed = FixedSamplerPolicies.resolver().resolve(
            new ProgramSamplerLayout.FixedFunctionEmpty(LAYOUT_FP, FINGERPRINT),
            StageId.GBUFFERS, StageBand.BEFORE_GBUFFERS);
        FixedSamplerPlanResult.Ready fixedReady = assertInstanceOf(
            FixedSamplerPlanResult.Ready.class, fixed);
        assertTrue(fixedReady.bindings().isEmpty());
        assertEquals(FINGERPRINT, fixedReady.policy());

        FixedSamplerPlanResult virtual = FixedSamplerPolicies.resolver().resolve(
            new ProgramSamplerLayout.VirtualNotApplicable(LAYOUT_FP, FINGERPRINT),
            StageId.DEFERRED, StageBand.BETWEEN_GBUFFERS);
        FixedSamplerPlanResult.Ready virtualReady = assertInstanceOf(
            FixedSamplerPlanResult.Ready.class, virtual);
        assertTrue(virtualReady.bindings().isEmpty());
    }

    @Test
    void resolverOrdersRowsByUnitThenFixedNameDeclarationOrder() {
        List<ProgramSamplerDeclaration> declarations = List.of(
            decl("depthtex1", sampler2d(), 0),
            decl("gcolor", sampler2d(), 1),
            decl("colortex0", sampler2d(), 2));
        SamplerLayoutValidation valid = FixedSamplerPolicies.appB3().validate(StageId.FINAL,
            StageBand.SCREEN, declarations);
        assertInstanceOf(SamplerLayoutValidation.Valid.class, valid);
        FixedSamplerPlanResult result = FixedSamplerPolicies.resolver().resolve(
            shaderLayout(StageId.FINAL, Set.of(StageBand.SCREEN), declarations, valid),
            StageId.FINAL, StageBand.SCREEN);
        FixedSamplerPlanResult.Ready ready = assertInstanceOf(FixedSamplerPlanResult.Ready.class,
            result);
        assertEquals(List.of("gcolor", "colortex0", "depthtex1"),
            ready.bindings().stream().map(ResolvedSamplerBinding::exactName).toList());
        assertEquals(List.of(0, 0, 11),
            ready.bindings().stream().map(ResolvedSamplerBinding::unit).toList());
    }

    @Test
    void resolverRejectsForeignPolicyAndUnsupportedDomains() {
        ProgramSamplerLayout.Shader foreign = new ProgramSamplerLayout.Shader(LAYOUT_FP,
            new FixedSamplerPolicyFingerprint("not-appb3"), StageId.FINAL,
            Set.of(StageBand.SCREEN), List.of(decl("colortex0", sampler2d(), 0)),
            new SamplerLayoutValidation.Valid());
        assertThrows(IllegalStateException.class,
            () -> FixedSamplerPolicies.resolver().resolve(foreign, StageId.FINAL,
                StageBand.SCREEN));

        List<ProgramSamplerDeclaration> declarations = List.of(
            decl("colortex0", sampler2d(), 0));
        SamplerLayoutValidation valid = FixedSamplerPolicies.appB3().validate(StageId.FINAL,
            StageBand.SCREEN, declarations);
        assertInstanceOf(SamplerLayoutValidation.Valid.class, valid);
        FixedSamplerPlanResult rejected = FixedSamplerPolicies.resolver().resolve(
            shaderLayout(StageId.SETUP, Set.of(StageBand.LOAD_OR_RESIZE), declarations, valid),
            StageId.SETUP, StageBand.LOAD_OR_RESIZE);
        SamplerLayoutValidation.Unsupported unsupported = assertInstanceOf(
            SamplerLayoutValidation.Unsupported.class,
            assertInstanceOf(FixedSamplerPlanResult.Invalid.class, rejected).reason());
        assertEquals(SamplerLayoutIssueCode.UNSUPPORTED_STAGE_DOMAIN,
            unsupported.issues().get(0).code());
    }

    @Test
    void initializationAssignmentsSortExactNamesAndShareAliasUnits() {
        List<ProgramSamplerDeclaration> declarations = List.of(
            decl("colortex0", sampler2d(), 0),
            decl("gcolor", sampler2d(), 1),
            decl("depthtex1", sampler2d(), 2));
        SamplerLayoutValidation valid = FixedSamplerPolicies.appB3().validate(StageId.FINAL,
            StageBand.SCREEN, declarations);
        assertInstanceOf(SamplerLayoutValidation.Valid.class, valid);
        List<SamplerUnitAssignment> assignments = FixedSamplerPolicies.appB3()
            .initializationAssignments(shaderLayout(StageId.FINAL,
                Set.of(StageBand.BETWEEN_GBUFFERS, StageBand.FRAME_END), declarations, valid));
        assertEquals(List.of("colortex0", "depthtex1", "gcolor"),
            assignments.stream().map(SamplerUnitAssignment::exactName).toList());
        assertEquals(List.of(0, 11, 0),
            assignments.stream().map(SamplerUnitAssignment::unit).toList());
    }

    @Test
    void initializationAssignmentsRejectInvalidAndForeignLayouts() {
        FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();
        List<ProgramSamplerDeclaration> declarations = List.of(
            decl("colortex0", sampler3d(), 0), decl("gcolor", sampler2d(), 1));
        SamplerLayoutValidation conflicting = policy.validate(StageId.FINAL,
            StageBand.SCREEN, declarations);
        assertInstanceOf(SamplerLayoutValidation.ConflictingTypes.class, conflicting);
        assertThrows(IllegalStateException.class, () -> policy.initializationAssignments(
            shaderLayout(StageId.FINAL, Set.of(StageBand.SCREEN), declarations, conflicting)));

        ProgramSamplerLayout.Shader foreign = new ProgramSamplerLayout.Shader(LAYOUT_FP,
            new FixedSamplerPolicyFingerprint("not-appb3"), StageId.FINAL,
            Set.of(StageBand.SCREEN), List.of(decl("colortex0", sampler2d(), 0)),
            new SamplerLayoutValidation.Valid());
        assertThrows(IllegalStateException.class,
            () -> policy.initializationAssignments(foreign));
    }

    private static List<SamplerLayoutIssueCode> codes(List<SamplerLayoutIssue> issues) {
        return issues.stream().map(SamplerLayoutIssue::code).toList();
    }

    private static List<SamplerLayoutIssue> collectIssues(SamplerLayoutValidation validation) {
        if (validation instanceof SamplerLayoutValidation.Unsupported unsupported) {
            return unsupported.issues();
        }
        if (validation instanceof SamplerLayoutValidation.ConflictingTypes conflicting) {
            return conflicting.otherIssues();
        }
        throw new AssertionError("expected issue-bearing validation: " + validation);
    }
}
