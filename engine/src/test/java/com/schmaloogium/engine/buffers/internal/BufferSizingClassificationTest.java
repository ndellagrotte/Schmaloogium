// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.schmaloogium.engine.buffers.BufferArchitectures;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.BufferPlanRequest;
import com.schmaloogium.engine.buffers.BufferPlanResult;
import com.schmaloogium.engine.buffers.BufferRuntimeInputs;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.config.BufferMinima;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.config.ColorAttachmentKey;
import com.schmaloogium.engine.config.ColorAttachmentRequirement;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.IdMappingFileFingerprint;
import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.MappingFileState;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.MacroIdentityPolicy;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.ProgramStateModel;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.config.ShadowDepthKey;
import com.schmaloogium.engine.config.ShadowRequirements;
import com.schmaloogium.engine.config.ShadowTextureKey;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.CompatibilityStatus;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackAssetSnapshot;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackIdentity;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.StageRegistry;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The §4.11 sizing-change classification (§8.2, PHASE_5_DOC §4.11.1/§4.11.2), driven
 * through the pure planner: each of the eight rebuild-causing rows shows its
 * planner-level delta signature through {@code BufferArchitectures.create().plan()}
 * — which rows move main/shadow extents (positive changes) and which gates fail
 * closed (negative display sizes, non-finite or non-positive multipliers, and
 * maxTextureSize overruns) — while fingerprint-only pack and registry rotations and
 * the estate-side main-depth rows contribute no planning delta. Row → reason mapping:
 * display/render-quality → DISPLAY_EXTENT/RENDER_QUALITY, same-extent main-depth and
 * main-depth mismatch → MAIN_DEPTH_EXTENT (estate-side), shadow resolution/quality →
 * SHADOW_RESOLUTION/SHADOW_QUALITY, pack/configuration → PACK_CONFIGURATION,
 * registry/route/flip → REGISTRY_PLAN, color format/count → COLOR_INVENTORY_OR_FORMAT.
 */
class BufferSizingClassificationTest {

    private static final int MAX_TEXTURE_SIZE = 4096;
    private static final String APP_B3 = FixedSamplerPolicies.appB3Fingerprint().value();

    // ------------------------------------------------------------------ fixture


    private static BufferPlanResult plan(BufferRuntimeInputs runtime,
            ResourceRequirements resources) {
        return plan(runtime, resources, PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp",
            "registry-fp", "registry-fp", APP_B3);
    }

    private static BufferPlanResult plan(BufferRuntimeInputs runtime,
            ResourceRequirements resources, int schemaVersion, String configurationFingerprint,
            String registryFingerprint, String requestFingerprint,
            String samplerPolicyFingerprint) {
        return BufferArchitectures.create().plan(new BufferPlanRequest(
            configuration(resources, schemaVersion, configurationFingerprint),
            registry(registryFingerprint, samplerPolicyFingerprint),
            new RegistryFingerprint(requestFingerprint),
            new GLCapabilityProfile(3, 3, "3.30 test profile", "vendor", "renderer",
                8, 8, 16, 16, MAX_TEXTURE_SIZE, 256, 0, Set.of()),
            runtime));
    }

    private static BufferRuntimeInputs runtime(int width, int height, double renderQuality,
            double shadowQuality) {
        return new BufferRuntimeInputs(new Extent2i(width, height), renderQuality,
            shadowQuality);
    }

    private static ResourceRequirements requirements(int colorBuffers, int depthTextures,
            int shadowDepth, int shadowColor, int shadowResolution,
            Map<ColorAttachmentKey, ColorAttachmentRequirement> colorOverrides) {
        boolean shadowPlanned = shadowDepth > 0 || shadowColor > 0;
        ShadowRequirements shadow = shadowPlanned
            ? new ShadowRequirements(shadowResolution, Optional.empty(), 0f, 0f, 0f,
                EnumSet.noneOf(ShadowTextureKey.class),
                EnumSet.noneOf(ShadowTextureKey.class),
                EnumSet.noneOf(ShadowDepthKey.class))
            : null;
        return new ResourceRequirements(
            new BufferMinima(colorBuffers, depthTextures, shadowDepth, shadowColor),
            colorOverrides, shadow, null, Map.of(), null, null, null);
    }

    private static PlanningArtifacts valid(BufferPlanResult result) {
        return ((BufferPlanResult.Valid) assertInstanceOf(BufferPlanResult.Valid.class,
            result)).plan().artifacts();
    }

    private static BufferFailure invalid(BufferPlanResult result) {
        return assertInstanceOf(BufferPlanResult.Invalid.class, result).failure();
    }

    /**
     * Mints the empty pack-asset snapshot the PackConfiguration constructor requires.
     * PackAssetSnapshotImpl is package-private and P13-owned; reflection is contained
     * to this test until P13 ships a public empty-snapshot factory.
     */
    private static PackAssetSnapshot emptyAssets(PackIdentity pack) {
        try {
            Constructor<?> constructor = Class
                .forName("com.schmaloogium.engine.pack.PackAssetSnapshotImpl")
                .getDeclaredConstructor(PackIdentity.class, Map.class);
            constructor.setAccessible(true);
            return (PackAssetSnapshot) constructor.newInstance(pack, Map.of());
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot mint an empty pack-asset snapshot", failure);
        }
    }

    /**
     * An inert-minimal but genuinely validated PackConfiguration: the planner reads
     * only schemaVersion() and resources(), every other component is structurally
     * valid filler so the compact constructor's guards all pass.
     */
    private static PackConfiguration configuration(ResourceRequirements resources,
            int schemaVersion, String configurationFingerprint) {
        PackIdentity pack = new PackIdentity(new NormalizedPackPath("shaders"), Map.of());
        var catalog = OptionCatalogs.create(List.of(), new Object(), false);
        ShaderPropertiesModel properties = new ShaderPropertiesModel(
            EngineFlags.allDefault(), List.of(), List.of(), List.of(),
            new NoiseTextureSpec.Generated(), List.of(), new ProgramStateModel(Map.of()),
            List.of());
        IdMappingFileInput absentBlock = new IdMappingFileInput(MappingKind.BLOCK,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("block-fp"));
        IdMappingFileInput absentItem = new IdMappingFileInput(MappingKind.ITEM,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("item-fp"));
        IdMappingFileInput absentEntity = new IdMappingFileInput(MappingKind.ENTITY,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("entity-fp"));
        IdMappingFileInput absentLayer = new IdMappingFileInput(MappingKind.LAYER,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("layer-fp"));
        return new PackConfiguration(schemaVersion, pack, CompatibilityStatus.COMPATIBLE,
            Map.of(), new EmptySources(), emptyAssets(pack),
            new OptionConfiguration(catalog, catalog.defaultState(), List.of(), null,
                Map.of(), null, Map.of()),
            new MacroConfiguration(MacroIdentityPolicy.OPTION_1, List.of(), List.of(),
                new CompanionOptionMacros(false, false), List.of(), List.of(), Map.of(),
                List.of()),
            properties, resources,
            new IdMappingInput(schemaVersion, new IdMappingMacroEnvironment(11904, List.of()),
                absentBlock, absentItem, absentEntity, absentLayer),
            List.of(), new ConfigurationFingerprint(configurationFingerprint));
    }

    private static ProgramRegistryView registry(String fingerprint,
            String samplerPolicyFingerprint) {
        return new ProgramRegistryView() {
            @Override
            public StageRegistry stages() {
                return new StageRegistry() {
                    @Override
                    public List<com.schmaloogium.engine.registry.StageStep> schedule() {
                        return List.of();
                    }

                    @Override
                    public List<com.schmaloogium.engine.registry.PassDescriptor> passes(
                            com.schmaloogium.engine.registry.StageStep step) {
                        return List.of();
                    }

                    @Override
                    public Optional<com.schmaloogium.engine.registry.PassDescriptor> named(
                            com.schmaloogium.engine.registry.StageStep step,
                            com.schmaloogium.engine.registry.ProgramSlotId id) {
                        return Optional.empty();
                    }

                    @Override
                    public Optional<com.schmaloogium.engine.registry.PassDescriptor> indexed(
                            com.schmaloogium.engine.registry.StageStep step,
                            com.schmaloogium.engine.registry.PassIndex index) {
                        return Optional.empty();
                    }

                    @Override
                    public boolean stepExists(
                            com.schmaloogium.engine.registry.StageStep step) {
                        return false;
                    }
                };
            }

            @Override
            public Optional<com.schmaloogium.engine.registry.ResolvedProgramDescriptor> resolve(
                    com.schmaloogium.engine.registry.ProgramSlotId requested) {
                return Optional.empty();
            }

            @Override
            public List<com.schmaloogium.engine.registry.ProgramResolutionProjection>
                    resolutions() {
                return List.of();
            }

            @Override
            public RegistryFingerprint fingerprint() {
                return new RegistryFingerprint(fingerprint);
            }

            @Override
            public FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
                return new FixedSamplerPolicyFingerprint(samplerPolicyFingerprint);
            }
        };
    }

    /** Inert-minimal SourceCatalog: the planner never touches pack sources. */
    private static final class EmptySources
            implements com.schmaloogium.engine.preprocess.SourceCatalog {

        @Override
        public List<com.schmaloogium.engine.preprocess.SourceDocument> sources() {
            return List.of();
        }

        @Override
        public List<com.schmaloogium.engine.preprocess.SourceKey> roots() {
            return List.of();
        }

        @Override
        public Set<com.schmaloogium.engine.config.ProgramKey> executablePrograms() {
            return Set.of();
        }

        @Override
        public Optional<com.schmaloogium.engine.preprocess.SourceDocument> source(
                com.schmaloogium.engine.preprocess.SourceId id) {
            return Optional.empty();
        }

        @Override
        public List<com.schmaloogium.engine.preprocess.IncludeEdge> includeEdges() {
            return List.of();
        }

        @Override
        public com.schmaloogium.engine.preprocess.SourceMaterializer materializer() {
            return null;
        }
    }

    // ------------------------------------------------------------------ eight rows

    /** Row "display or render-quality change" → DISPLAY_EXTENT. */
    @Test
    void displayChangeReplansAtTheNewMainExtentAndRejectsNonPositiveDisplay() {
        PlanningArtifacts base = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        PlanningArtifacts changed = valid(plan(runtime(64, 48, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));

        assertEquals(new Extent2i(64, 48), changed.sizing().mainExtent(),
            "mainWidth/mainHeight follow the positive display change verbatim");
        assertEquals(base.sizing().shadowExtent(), changed.sizing().shadowExtent(),
            "a display change never moves the shadow extent");
        assertEquals(base.inventory(), changed.inventory());
        assertEquals(base.colors(), changed.colors());
        assertNotEquals(base, changed);

        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(0, 32, 1.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, -1, 1.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
    }

    /** Row "display or render-quality change" → RENDER_QUALITY. */
    @Test
    void renderQualityChangeScalesMainExtentAndValidatesTheMultiplier() {
        assertEquals(new Extent2i(32, 32),
            valid(plan(runtime(32, 32, 1.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).sizing().mainExtent());
        assertEquals(new Extent2i(48, 48),
            valid(plan(runtime(32, 32, 1.5, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).sizing().mainExtent());
        assertEquals(new Extent2i(17, 17),
            valid(plan(runtime(33, 33, 0.5, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).sizing().mainExtent(),
            "mainExtent rounds half-up before the max(1, ...) floor");

        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, 32, 0.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, 32, -2.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, 32, Double.NaN, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, 32, Double.POSITIVE_INFINITY, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
        assertEquals(BufferFailureCode.CAPABILITY_LIMIT,
            invalid(plan(runtime(32, 32, 0.0001, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code(),
            "a positive multiplier rounding below one extent fails the capability gate");
        assertEquals(BufferFailureCode.CAPABILITY_LIMIT,
            invalid(plan(runtime(32, 32, 256.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code(),
            "a main extent above maxTextureSize fails before allocation");
        assertEquals(new Extent2i(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE),
            valid(plan(runtime(MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE, 1.0, 1.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).sizing().mainExtent(),
            "the maxTextureSize boundary itself still plans");
    }

    /**
     * Rows "same-extent main-depth identity/format change" and "main-depth extent
     * differs" → MAIN_DEPTH_EXTENT: both compare the prepared depth snapshot against
     * the planned extent inside the candidate builder, so the pure planner exposes no
     * main-depth input and identical inputs must replan identically at the same
     * planned extent.
     */
    @Test
    void mainDepthRowsStayEstateSideAndUnchangedInputsReplanIdentically() {
        PlanningArtifacts first = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        PlanningArtifacts second = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        assertEquals(first, second, "the planner is a pure function of its inputs");
        assertEquals(new Extent2i(32, 32), first.sizing().mainExtent(),
            "the planned main extent is the estate-side mismatch comparison basis");
    }

    /** Row "shadow resolution/quality change" → SHADOW_RESOLUTION. */
    @Test
    void shadowResolutionChangeRebuildsOnlyTheShadowExtent() {
        PlanningArtifacts base = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        PlanningArtifacts changed = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 2048, Map.of())));

        assertEquals(new Extent2i(2048, 2048), changed.sizing().shadowExtent().orElseThrow(),
            "shadowSide follows the resolution change");
        assertEquals(base.sizing().mainExtent(), changed.sizing().mainExtent(),
            "a shadow resolution change never moves the main extent");
        assertEquals(base.inventory(), changed.inventory());
        assertNotEquals(base, changed);

        assertEquals(BufferFailureCode.CAPABILITY_LIMIT,
            invalid(plan(runtime(32, 32, 1.0, 1.0),
                requirements(4, 2, 1, 1, 8192, Map.of()))).code(),
            "a shadow side above maxTextureSize fails the capability gate");
    }

    /** Row "shadow resolution/quality change" → SHADOW_QUALITY. */
    @Test
    void shadowQualityChangeScalesOnlyTheShadowExtent() {
        PlanningArtifacts base = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        PlanningArtifacts halved = valid(plan(runtime(32, 32, 1.0, 0.5),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        assertEquals(new Extent2i(512, 512), halved.sizing().shadowExtent().orElseThrow());
        assertEquals(base.sizing().mainExtent(), halved.sizing().mainExtent());
        assertEquals(new Extent2i(513, 513),
            valid(plan(runtime(32, 32, 1.0, 0.5),
                requirements(4, 2, 1, 1, 1025, Map.of()))).sizing().shadowExtent()
                .orElseThrow(),
            "the shadow side rounds half-up like the main extent");

        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, 32, 1.0, 0.0),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
        assertEquals(BufferFailureCode.INVALID_INPUT,
            invalid(plan(runtime(32, 32, 1.0, Double.NaN),
                requirements(4, 2, 1, 1, 1024, Map.of()))).code());
    }

    /** Row "pack/configuration fingerprint change" → PACK_CONFIGURATION. */
    @Test
    void packConfigurationRotationReplansIdenticallyAndRejectsForeignSchema() {
        assertEquals(
            valid(plan(runtime(32, 32, 1.0, 1.0), requirements(4, 2, 1, 1, 1024, Map.of()),
                PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp-a", "registry-fp",
                "registry-fp", APP_B3)),
            valid(plan(runtime(32, 32, 1.0, 1.0), requirements(4, 2, 1, 1, 1024, Map.of()),
                PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp-b", "registry-fp",
                "registry-fp", APP_B3)),
            "a fingerprint-only pack rotation leaves the planning payload identical; the "
                + "rebuild decision is taken on the fingerprint upstream");

        BufferFailure foreignSchema = invalid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of()),
            PackFrontEnd.CURRENT_SCHEMA_VERSION - 1, "config-fp", "registry-fp",
            "registry-fp", APP_B3));
        assertEquals(BufferFailureCode.INVALID_INPUT, foreignSchema.code());
        assertEquals("schmaloogium.buffers.error.plan.schema", foreignSchema.messageKey());
    }

    /** Row "registry fingerprint/route/flip change" → REGISTRY_PLAN. */
    @Test
    void registryRotationReplansIdenticallyAndRejectsProvenanceMismatches() {
        assertEquals(
            valid(plan(runtime(32, 32, 1.0, 1.0), requirements(4, 2, 1, 1, 1024, Map.of()),
                PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp", "registry-fp-a",
                "registry-fp-a", APP_B3)),
            valid(plan(runtime(32, 32, 1.0, 1.0), requirements(4, 2, 1, 1, 1024, Map.of()),
                PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp", "registry-fp-b",
                "registry-fp-b", APP_B3)),
            "a fingerprint-only registry rotation leaves the planning payload identical");

        BufferFailure foreignRegistry = invalid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of()),
            PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp", "registry-fp-b",
            "registry-fp-a", APP_B3));
        assertEquals(BufferFailureCode.INVALID_INPUT, foreignRegistry.code());
        assertEquals("schmaloogium.buffers.error.plan.registry-fingerprint",
            foreignRegistry.messageKey(),
            "the request fingerprint must match the registry view's own fingerprint");

        BufferFailure foreignPolicy = invalid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of()),
            PackFrontEnd.CURRENT_SCHEMA_VERSION, "config-fp", "registry-fp",
            "registry-fp", "other-policy"));
        assertEquals(BufferFailureCode.INVALID_INPUT, foreignPolicy.code());
        assertEquals("schmaloogium.buffers.error.plan.sampler-policy",
            foreignPolicy.messageKey());
    }

    /** Row "color format/count change" → COLOR_INVENTORY_OR_FORMAT. */
    @Test
    void colorInventoryAndFormatChangesRebuildColorsWithoutResizing() {
        PlanningArtifacts base = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of())));
        PlanningArtifacts wider = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(6, 2, 1, 1, 1024, Map.of())));
        assertNotEquals(base, wider);
        assertNotEquals(base.inventory(), wider.inventory(),
            "the color count change grows the dense colortex inventory");
        assertEquals(base.teardownOrder().subList(0, 3), wider.teardownOrder().subList(0, 3),
            "the shadow and depth teardown steps are untouched by the color change");
        assertEquals(2, wider.teardownOrder().size() - base.teardownOrder().size(),
            "the wider inventory appends two extra dense color teardown steps");
        assertEquals(new LogicalBuffer(BufferDomain.COLORTEX, new BufferIndex(5)),
            wider.teardownOrder().get(3),
            "the color teardown walks the new dense scan top first");
        assertEquals(base.sizing(), wider.sizing(),
            "a color inventory change never resizes the estate");

        PlanningArtifacts explicitFormat = valid(plan(runtime(32, 32, 1.0, 1.0),
            requirements(4, 2, 1, 1, 1024, Map.of(new ColorAttachmentKey(0),
                new ColorAttachmentRequirement(
                    new ColorAttachmentFormat.Explicit(ColorInternalFormat.RGBA32F), true,
                    Optional.empty())))));
        assertEquals(base.inventory(), explicitFormat.inventory(),
            "a format change keeps the inventory identical");
        assertNotEquals(base.colors(), explicitFormat.colors());
        assertEquals(base.sizing(), explicitFormat.sizing());
    }

    /** §4.11.1: shadowExtent is present exactly when either shadow minimum is positive. */
    @Test
    void shadowExtentPresenceFollowsTheShadowMinima() {
        assertEquals(Optional.empty(),
            valid(plan(runtime(32, 32, 1.0, 1.0),
                requirements(4, 2, 0, 0, 1024, Map.of()))).sizing().shadowExtent(),
            "no shadow demand plans no sfb extent");
        assertEquals(new Extent2i(1024, 1024),
            valid(plan(runtime(32, 32, 1.0, 1.0),
                requirements(4, 2, 1, 0, 1024, Map.of()))).sizing().shadowExtent()
                .orElseThrow());
        assertEquals(new Extent2i(512, 512),
            valid(plan(runtime(32, 32, 1.0, 1.0),
                requirements(4, 2, 0, 1, 512, Map.of()))).sizing().shadowExtent()
                .orElseThrow(),
            "color-only shadow demand also plans the sfb extent");
    }
}
