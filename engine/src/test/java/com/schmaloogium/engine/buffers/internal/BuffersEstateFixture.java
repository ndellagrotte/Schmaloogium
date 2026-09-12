// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.CapabilityGate;
import com.schmaloogium.engine.buffers.ColorAllocationOrigin;
import com.schmaloogium.engine.buffers.ColorBufferResource;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainDepthSource;
import com.schmaloogium.engine.buffers.PhysicalSide;
import com.schmaloogium.engine.buffers.RealizedColorAllocation;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.ResourceEvidenceStage;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
/**
 * Assembled Phase-5 estate fixture for headless buffers tests: a real {@link EstateCore}
 * over a {@link RecordingGLDevice} with planned color rows, realized evidence and a
 * borrowed main depth. No GL context, no sleeps; everything the estate operators touch is
 * backed by the recorder. The shadow variant realizes the §4.10 estate through the real
 * {@link CandidateBuilder#allocateShadow} path and wires {@link ShadowEstateImpl}.
 */
final class BuffersEstateFixture {

    static final Extent2i EXTENT = new Extent2i(32, 32);

    final RecordingGLDevice device;
    EstateCore core;
    final List<EngineDiagnostic> diagnostics = new ArrayList<>();

    private BuffersEstateFixture(RecordingGLDevice device, EstateCore core) {
        this.device = device;
        this.core = core;
    }

    /**
     * Builds an estate with one planned colortex row per given format. Each row gets a
     * double-sided ColorPair allocated at that exact internal format through the real
     * {@link FormatTable} transfer layout, so typed clears are validated by the recorder.
     */
    static BuffersEstateFixture create(ColorInternalFormat[] formats,
            ResourceClearPolicy[] policies, boolean[] clear, int depthTextureCount) {
        return assemble(formats, policies, clear, depthTextureCount,
            new ShadowResourceProjection(0, 0, 0, List.of(), List.of()), 0,
            new ScriptedResponses(), Map.of());
    }

    /**
     * Builds an estate whose plan carries the given pass routes (so {@code snapshot}
     * resolves them); pass FBOs are still the test's to create and seed into
     * {@code core.passFbos}/{@code core.attachedColor}.
     */
    static BuffersEstateFixture createWithRoutes(ColorInternalFormat[] formats,
            ResourceClearPolicy[] policies, boolean[] clear, int depthTextureCount,
            Map<com.schmaloogium.engine.registry.ProgramSlotId, PlanningArtifacts.PlannedRoute> routes,
            ScriptedResponses responses) {
        return assemble(formats, policies, clear, depthTextureCount,
            new ShadowResourceProjection(0, 0, 0, List.of(), List.of()), 0,
            responses, routes);
    }

    /**
     * Builds an estate with a planned §4.10 shadow estate: the projection and shadow extent
     * enter the plan, and the estate is realized through the real
     * {@link CandidateBuilder#allocateShadow} (neutral cache first, then depths, color
     * pairs and the sfb) with {@link ShadowEstateImpl} wired when operable.
     */
    static BuffersEstateFixture createWithShadow(ColorInternalFormat[] formats,
            ResourceClearPolicy[] policies, boolean[] clear, int depthTextureCount,
            ShadowResourceProjection shadow, int shadowSize, ScriptedResponses responses) {
        return assemble(formats, policies, clear, depthTextureCount, shadow, shadowSize,
            responses, Map.of());
    }

    private static BuffersEstateFixture assemble(ColorInternalFormat[] formats,
            ResourceClearPolicy[] policies, boolean[] clear, int depthTextureCount,
            ShadowResourceProjection shadow, int shadowSize, ScriptedResponses responses,
            Map<com.schmaloogium.engine.registry.ProgramSlotId, PlanningArtifacts.PlannedRoute> routes) {
        GLCapabilityProfile profile = new GLCapabilityProfile(3, 3, "3.30 test profile",
            "vendor", "renderer", 8, 8, 16, 16, 4096, 256, 0, Set.of());
        RecordingGLDevice device = new RecordingGLDevice(profile, responses);

        List<PlanningArtifacts.ColorPlan> colorPlans = new ArrayList<>();
        List<ColorBufferResource> colorResources = new ArrayList<>();
        for (int row = 0; row < formats.length; row++) {
            LogicalBuffer logical = colorRow(row);
            colorPlans.add(new PlanningArtifacts.ColorPlan(logical,
                new ColorAttachmentFormat.Explicit(formats[row]), clear[row], policies[row]));
            colorResources.add(new ColorBufferResource(
                new ColorAttachmentFormat.Explicit(formats[row]),
                Optional.of(new RealizedColorAllocation(formats[row].name(),
                    ColorAllocationOrigin.REQUESTED)),
                clear[row], policies[row]));
        }
        boolean shadowPlanned = shadow.depthTextures() > 0 || shadow.colorTextures() > 0;
        BufferResourceProjection projection = new BufferResourceProjection(
            ResourceEvidenceStage.REALIZED, colorResources, depthTextureCount,
            shadow, false, 0,
            List.of(), List.of(), CapabilityGate.OK, List.of());
        PlanningArtifacts plan = new PlanningArtifacts(
            new BufferSizing(EXTENT, shadowPlanned
                ? Optional.of(new Extent2i(shadowSize, shadowSize))
                : Optional.empty()),
            new BufferInventory(List.of()),
            depthTextureCount, colorPlans, routes, List.of(), List.of(), List.of(),
            projection);

        // The recorder mints borrowed handles only around ordinary FOREIGN platform
        // textures (§4.7.3); an engine-owned texture is explicitly rejected.
        TextureHandle platformDepth = new TextureHandle() {
        };
        BorrowedDepthAttachmentHandle borrowed = device.framebuffers()
            .borrowDepthAttachment(platformDepth);
        MainDepthSnapshot.Available depth = new MainDepthSnapshot.Available(1L, borrowed,
            DepthAttachmentFormat.DEPTH_COMPONENT, EXTENT);
        MainDepthSource depthSource = new MainDepthSource() {
            @Override
            public MainDepthPreparation prepare(Extent2i requiredExtent) {
                return new MainDepthPreparation.Ready(depth);
            }

            @Override
            public MainDepthSnapshot current() {
                return depth;
            }
        };

        BuffersEstateFixture fixture = new BuffersEstateFixture(device, null);
        DiagnosticReporter reporter = fixture.diagnostics::add;
        EstateCore core = new EstateCore(device, reporter,
            new RegistryFingerprint("fp-buffers-test"), plan,
            new BufferResourceSnapshot.Available(projection), depthSource, depth);
        core.generation = 1;
        fixture.core = core;

        for (int row = 0; row < formats.length; row++) {
            core.colorPairs.add(new EstateCore.ColorPair(colorRow(row),
                allocatedColorTexture(device, "colortex" + row + "a", formats[row]),
                allocatedColorTexture(device, "colortex" + row + "b", formats[row]),
                PhysicalSide.A, false, 1, TextureMinFilter.NEAREST));
        }

        if (shadowPlanned) {
            // The real §4.10 allocation path: neutral cache, shadowtex0/1, shadowcolor
            // pairs, sfb — through the same code the candidate builder runs.
            CandidateBuilder.ShadowEstate estate =
                CandidateBuilder.allocateShadow(device, plan, depth,
                    new CandidateBuilder.Ledger(device));
            core.shadowNeutral = estate.neutrals();
            if (estate.operable()) {
                core.shadowDepths.addAll(estate.depths());
                for (int index = 0; index < estate.colorSidesA().size(); index++) {
                    core.shadowColorPairs.add(new EstateCore.ShadowColorPair(
                        CandidateBuilder.shadowColorLogical(index),
                        estate.colorSidesA().get(index), estate.colorSidesB().get(index)));
                }
                core.shadowFbo = estate.sfb();
                core.shadowView = new ShadowEstateImpl(core);
            } else {
                core.shadowFailure = estate.failure();
            }
        }
        return fixture;
    }

    static LogicalBuffer colorRow(int index) {
        return new LogicalBuffer(BufferDomain.COLORTEX, new BufferIndex(index));
    }

    private static TextureHandle allocatedColorTexture(RecordingGLDevice device, String label,
            ColorInternalFormat format) {
        TextureHandle texture = device.textures().create(label);
        com.schmaloogium.engine.gl.ColorInternalFormat glFormat =
            com.schmaloogium.engine.gl.ColorInternalFormat.valueOf(format.name());
        device.textures().allocate(texture, new TextureSpec.ColorTextureSpec(
            TextureAllocationTarget.TEXTURE_2D, glFormat,
            FormatTable.row(glFormat).allocationLayout(),
            new TextureExtent(EXTENT.width(), EXTENT.height(), 1), 1));
        return texture;
    }

    /** Count of calls with exactly this operation name (no prefix capture of restores). */
    long exactOpCount(String op) {
        return device.log().calls().stream().filter(call -> call.op().equals(op)).count();
    }
}
