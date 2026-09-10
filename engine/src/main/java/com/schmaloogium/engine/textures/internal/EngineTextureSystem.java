// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.buffers.TextureCandidateTable;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.buffers.TextureOverlayFingerprint;
import com.schmaloogium.engine.buffers.TextureParameterFingerprint;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.gl.TextureService;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.textures.AtlasBindingObservation;
import com.schmaloogium.engine.textures.AtlasBindingObserver;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.AtlasSizeResult;
import com.schmaloogium.engine.textures.CompanionAtlasPlan;
import com.schmaloogium.engine.textures.NoisePlan;
import com.schmaloogium.engine.textures.TextureBuildRequest;
import com.schmaloogium.engine.textures.TextureBuildResult;
import com.schmaloogium.engine.textures.TextureBuildSources;
import com.schmaloogium.engine.textures.TextureCaptureSink;
import com.schmaloogium.engine.textures.TextureFailure;
import com.schmaloogium.engine.textures.TextureFailureCode;
import com.schmaloogium.engine.textures.TextureLeaseRejection;
import com.schmaloogium.engine.textures.TextureLeaseResult;
import com.schmaloogium.engine.textures.TexturePlan;
import com.schmaloogium.engine.textures.TexturePlanRequest;
import com.schmaloogium.engine.textures.TexturePlanResult;
import com.schmaloogium.engine.textures.TexturePublication;
import com.schmaloogium.engine.textures.TexturePreparedSource;
import com.schmaloogium.engine.textures.TextureSystem;
import com.schmaloogium.engine.textures.TextureSystemCreationResult;
import com.schmaloogium.engine.textures.TextureUploadPayload;
import com.schmaloogium.engine.textures.TextureUploadSpec;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The one texture owner (§4.5-§4.7): pure planning, single-shot build with all-or-nothing
 * allocation, exact-order lease preflight, the size-value source, the capture acceptor and
 * the companion animation application. The factory creates an INACTIVE owner; Phase 7 alone
 * retains build/close authority. close() retires immediately, deletes owned objects in
 * reverse creation order only at lease count zero, and never blocks the render thread.
 */
public final class EngineTextureSystem implements TextureSystem, TextureCaptureSink {

    private final GLDevice device;
    private final DiagnosticReporter diagnostics;
    private final AtlasBindingObserver bindingObserver;

    // Lifecycle: guarded by this.
    private boolean retiring;
    private boolean closed;
    private int leaseCount;

    // Publication: non-null exactly after a successful build (single-shot).
    private TexturePublication publication;
    private OwnerState ownerState;

    // Capture state (§4.4): accepted stitch captures at the current epoch.
    private long capturedEpoch;
    private final Map<AtlasId, AtlasDescriptor> capturedAtlases = new HashMap<>();
    private AtlasId designatedAtlas;
    private final Map<AtlasId, BaseAssociation> baseAssociations = new HashMap<>();
    private final List<LeasedOverlay> liveLeases = new ArrayList<>();

    private record BaseAssociation(BaseAtlasContext context, TextureHandle base,
                                   long epoch) {
    }

    private EngineTextureSystem(GLDevice device, DiagnosticReporter diagnostics,
                                AtlasBindingObserver bindingObserver) {
        this.device = Objects.requireNonNull(device, "device");
        this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
        this.bindingObserver = Objects.requireNonNull(bindingObserver, "bindingObserver");
    }

    /** The canonical factory: an inactive owner, no GL allocation, atlasSize Unknown. */
    public static TextureSystemCreationResult create(GLDevice device,
                                                     DiagnosticReporter diagnostics) {
        Objects.requireNonNull(device, "device");
        Objects.requireNonNull(diagnostics, "diagnostics");
        return new TextureSystemCreationResult.Created(
            new EngineTextureSystem(device, diagnostics, com.schmaloogium.engine.textures.AtlasBindingObservers.active()));
    }

    @Override
    public TexturePlanResult plan(TexturePlanRequest request) {
        Objects.requireNonNull(request, "request");
        try {
            return new TexturePlanResult.Planned(TexturePlanner.plan(request));
        } catch (TexturePlanner.SchemaGateException gate) {
            return new TexturePlanResult.Invalid(TextureFailure.of(
                TextureFailureCode.IDENTITY_MISMATCH, TextureFailure.SYSTEM_IDENTITY));
        } catch (IllegalArgumentException invalid) {
            return new TexturePlanResult.Invalid(TextureFailure.of(
                TextureFailureCode.INVALID_REQUEST, TextureFailure.SYSTEM_IDENTITY));
        }
    }

    @Override
    public TextureBuildResult build(TextureBuildRequest request) {
        Objects.requireNonNull(request, "request");
        synchronized (this) {
            if (retiring || closed) {
                return failed(TextureFailureCode.OWNER_UNAVAILABLE);
            }
            if (publication != null) {
                throw new IllegalStateException(
                    "single-shot build: this owner already published; retire it first");
            }
        }
        var planned = plan(request.planRequest());
        if (planned instanceof TexturePlanResult.Invalid invalid) {
            return new TextureBuildResult.Failed(invalid.failure());
        }
        var plan = ((TexturePlanResult.Planned) planned).plan();
        var state = new OwnerState(plan);
        try {
            state.validatePairing(request.sources());
            state.allocate(device.textures());
        } catch (TexturePlanner.SchemaGateException gate) {
            state.destroyAll(device.textures());
            return failed(TextureFailureCode.IDENTITY_MISMATCH);
        } catch (RuntimeException failure) {
            state.destroyAll(device.textures());
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                UserChannel.LOG_ONLY, "schmaloogium.error.texture.backend_failure",
                List.of(failure.toString()),
                "texture allocation failed; publication discarded",
                LogChannels.TEXTURES));
            return failed(TextureFailureCode.BACKEND_FAILURE);
        }
        var id = new TextureOverlayPublicationId(
            request.planRequest().estateGeneration(),
            new TextureOverlayFingerprint(TextureDigests.publicationFingerprint(
                publicationAtoms(plan),
                request.planRequest().registryFingerprint().value(),
                plan.inputs().configuration().fingerprint().value(),
                com.schmaloogium.engine.buffers.FixedSamplerPolicies.appB3Fingerprint()
                    .value(),
                request.planRequest().estateGeneration(),
                request.planRequest().registryGeneration(),
                request.planRequest().resourceReloadEpoch())));
        var publication = new TexturePublication(id,
            request.planRequest().registryFingerprint(),
            request.planRequest().registryGeneration(),
            request.planRequest().resourceReloadEpoch(), plan, state.candidateTable());
        synchronized (this) {
            this.publication = publication;
            this.ownerState = state;
        }
        return new TextureBuildResult.Ready(publication);
    }

    private static TextureBuildResult.Failed failed(TextureFailureCode code) {
        return new TextureBuildResult.Failed(
            TextureFailure.of(code, TextureFailure.SYSTEM_IDENTITY));
    }

    private static List<String> publicationAtoms(TexturePlan plan) {
        List<String> atoms = new ArrayList<>();
        atoms.add(Long.toString(plan.inputs().estateGeneration()));
        atoms.add(Long.toString(plan.inputs().resourceReloadEpoch()));
        atoms.add(plan.inputs().registryFingerprint().value());
        for (var companion : plan.companions()) {
            atoms.add(companion.base().value());
            atoms.add(companion.kind().name());
        }
        atoms.add(switch (plan.noise()) {
            case NoisePlan.Generated g -> "generated:" + g.resolution();
            case NoisePlan.FromPack f -> "pack:" + f.image().canonicalString();
            case NoisePlan.Disabled d -> "disabled";
        });
        for (var entry : plan.customTextures()) {
            atoms.add(Integer.toString(entry.phase3Ordinal()));
            atoms.add(entry.key().sampler());
        }
        return atoms;
    }

    @Override
    public TextureLeaseResult lease(TextureOverlayPublicationId expected,
                                    ProgramBindingSelection selection,
                                    AtlasBindingEvidence baseBinding) {
        Objects.requireNonNull(expected, "expected");
        Objects.requireNonNull(selection, "selection");
        synchronized (this) {
            if (retiring || closed || publication == null) {
                return new TextureLeaseResult.Rejected(
                    TextureLeaseRejection.PUBLICATION_UNAVAILABLE);
            }
            if (!publication.id().equals(expected)) {
                return new TextureLeaseResult.Rejected(
                    TextureLeaseRejection.PUBLICATION_ID_MISMATCH);
            }
            if (!publication.registryFingerprint().equals(selection.registryFingerprint())) {
                return new TextureLeaseResult.Rejected(
                    TextureLeaseRejection.REGISTRY_FINGERPRINT_MISMATCH);
            }
            if (selection.registryGeneration() != publication.registryGeneration()) {
                return new TextureLeaseResult.Rejected(
                    TextureLeaseRejection.STALE_SELECTION);
            }
        }
        // Base binding outside the owner lock: the observer owns its own synchronization.
        AtlasBindingObservation observation =
            bindingObserver.authenticate(Objects.requireNonNull(baseBinding, "baseBinding"));
        if (!(observation instanceof AtlasBindingObservation.Authenticated authenticated)) {
            return new TextureLeaseResult.Rejected(
                TextureLeaseRejection.INVALID_BASE_BINDING);
        }
        if (!bindingObserver.isLatest(authenticated.currentnessToken())) {
            return new TextureLeaseResult.Rejected(TextureLeaseRejection.STALE_BASE_BINDING);
        }
        synchronized (this) {
            if (retiring || closed || publication == null) {
                return new TextureLeaseResult.Rejected(
                    TextureLeaseRejection.PUBLICATION_UNAVAILABLE);
            }
            leaseCount++;
            var lease = new LeasedOverlay(publication, authenticated, this);
            liveLeases.add(lease);
            return new TextureLeaseResult.Acquired(lease);
        }
    }

    @Override
    public AtlasSizeResult atlasSize(AtlasId atlas) {
        Objects.requireNonNull(atlas, "atlas");
        synchronized (this) {
            var descriptor = capturedAtlases.get(atlas);
            if (descriptor == null || retiring || closed) {
                return AtlasSizeResult.Unknown.INSTANCE;
            }
            return new AtlasSizeResult.Known(descriptor.width(), descriptor.height());
        }
    }

    @Override
    public AtlasSizeResult atlasSize() {
        synchronized (this) {
            if (designatedAtlas == null || retiring || closed) {
                return AtlasSizeResult.Unknown.INSTANCE;
            }
            var descriptor = capturedAtlases.get(designatedAtlas);
            return descriptor == null ? AtlasSizeResult.Unknown.INSTANCE
                : new AtlasSizeResult.Known(descriptor.width(), descriptor.height());
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            retiring = true;
            invalidateStitch(Long.MAX_VALUE);
            if (leaseCount == 0) {
                destroyNow();
            }
        }
    }

    private void destroyNow() {
        if (ownerState != null) {
            ownerState.destroyAll(device.textures());
            ownerState = null;
        }
        publication = null;
        closed = true;
        retiring = false;
    }

    private void leaseClosed(LeasedOverlay lease) {
        synchronized (this) {
            liveLeases.remove(lease);
            if (leaseCount > 0) {
                leaseCount--;
            }
            if (retiring && !closed && leaseCount == 0) {
                destroyNow();
            }
        }
    }

    private boolean leaseUsable(LeasedOverlay lease) {
        synchronized (this) {
            return !retiring && !closed && !lease.closed && publication != null;
        }
    }

    // ------------------------------------------------------------------
    // TextureCaptureSink (§4.4)
    // ------------------------------------------------------------------

    @Override
    public void onStitchAccepted(AtlasDescriptor descriptor, boolean designatedBlockItemAtlas) {
        Objects.requireNonNull(descriptor, "descriptor");
        synchronized (this) {
            capturedAtlases.put(descriptor.id(), descriptor);
            if (designatedBlockItemAtlas) {
                designatedAtlas = descriptor.id();
            }
        }
    }

    @Override
    public void invalidateStitch(long newResourceReloadEpoch) {
        synchronized (this) {
            capturedEpoch = newResourceReloadEpoch;
            capturedAtlases.clear();
            baseAssociations.clear();
            designatedAtlas = null;
        }
    }

    @Override
    public void associateBase(TextureHandle base, BaseAtlasContext association,
                              long resourceReloadEpoch) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(association, "association");
        synchronized (this) {
            if (association instanceof BaseAtlasContext.Atlas atlas) {
                baseAssociations.put(atlas.atlas(),
                    new BaseAssociation(association, base, resourceReloadEpoch));
            }
        }
    }

    @Override
    public void applyAnimationSnapshot(
            com.schmaloogium.engine.textures.AtlasAnimationSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        List<OwnerState.PendingUpload> pending;
        synchronized (this) {
            if (retiring || closed || ownerState == null) {
                return;
            }
            pending = ownerState.resolveAnimation(snapshot, capturedEpoch);
        }
        for (var upload : pending) {
            try {
                device.textures().upload(upload.handle(), upload.data());
            } catch (RuntimeException failure) {
                reportFrame0Fallback(snapshot, upload.iconName());
                return;
            }
        }
    }

    @Override
    public AtlasId designatedBlockItemAtlas() {
        synchronized (this) {
            return designatedAtlas;
        }
    }

    private void reportFrame0Fallback(
            com.schmaloogium.engine.textures.AtlasAnimationSnapshot snapshot, String icon) {
        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
            UserChannel.LOG_ONLY, "schmaloogium.error.texture.animation_frame0_fallback",
            List.of(icon), "companion frame upload failed; frame0 fallback",
            LogChannels.TEXTURES));
    }

    /** Per-publication owned-object graph with pairing, allocation and animation logic. */
    private final class OwnerState {
        private final TexturePlan plan;
        private final List<TextureHandle> creationOrder = new ArrayList<>();
        private final Map<CompanionAtlasPlan, TextureHandle> companionHandles =
            new LinkedHashMap<>();
        private final Map<CompanionKind, TextureHandle> defaultHandles =
            new EnumMap<>(CompanionKind.class);
        private final Map<NoisePlan, TextureHandle> noiseHandles = new LinkedHashMap<>();
        private final Map<com.schmaloogium.engine.textures.CustomTexturePlanEntry,
            TextureHandle> customHandles = new LinkedHashMap<>();
        private final Map<CompanionAtlasPlan, TextureUploadPayload> companionPayloads =
            new LinkedHashMap<>();
        private TextureCandidateTable candidateTable;

        private OwnerState(TexturePlan plan) {
            this.plan = Objects.requireNonNull(plan, "plan");
        }

        /** logicalSource → payload for paired owned prepared sources (§2.3). */
        private final Map<String, TextureUploadPayload> retainedPayloads = new HashMap<>();

        private void validatePairing(TextureBuildSources sources) {
            if (sources.resourceReloadEpoch() != plan.inputs().resourceReloadEpoch()) {
                throw new TexturePlanner.SchemaGateException(
                    "prepared-source epoch does not pair with the plan");
            }
            retainedPayloads.clear();
            for (var prepared : sources.sources()) {
                switch (prepared) {
                    case TexturePreparedSource.Owned owned -> {
                        if (!(owned.asset().identity()
                            instanceof com.schmaloogium.engine.textures.TextureSourceIdentity.OwnedUpload identity)) {
                            throw new TexturePlanner.SchemaGateException(
                                "owned payload paired with a foreign identity");
                        }
                        retainedPayloads.put(identity.logicalSource(), owned.payload());
                    }
                    case TexturePreparedSource.Foreign foreign -> {
                        if (!(foreign.asset().identity()
                            instanceof com.schmaloogium.engine.textures.TextureSourceIdentity.ForeignLive)) {
                            throw new TexturePlanner.SchemaGateException(
                                "foreign handle paired with an owned identity");
                        }
                    }
                }
            }
        }

        private void allocate(TextureService textures) {
            String configurationIdentity = plan.inputs().configuration().fingerprint().value();
            allocateCompanions(textures, configurationIdentity);
            allocateDefaults(textures);
            allocateNoise(textures, configurationIdentity, retainedPayloads);
            allocateCustoms(textures, retainedPayloads);
            candidateTable = MapTextureCandidateTable.build(plan, companionHandles,
                defaultHandles, noiseHandles, noiseParameters(),
                noiseFingerprint(), customHandles, configurationIdentity);
        }


        private com.schmaloogium.engine.buffers.TextureParameterSpec noiseParameters() {
            return ParameterPolicy.generatedNoisePolicy();
        }

        private String noiseFingerprint() {
            return SidecarPolicy.fixedRoleFingerprint(
                com.schmaloogium.engine.textures.OwnedTextureSourceKind.GENERATED_NOISE,
                SidecarPolicy.Role.GENERATED_NOISE, ParameterPolicy.generatedNoisePolicy());
        }

        private void allocateCompanions(TextureService textures, String configurationIdentity) {
            for (var companion : plan.companions()) {
                int levels = companion.mipmapLevels() + 1;
                var spec = new TextureSpec.ColorTextureSpec(
                    TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGBA8,
                    new PixelLayout.Color(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE),
                    new TextureExtent(companion.width(), companion.height(), 1), levels);
                var parameters = ParameterPolicy.facadeParameters(
                    TextureAllocationTarget.TEXTURE_2D,
                    ParameterPolicy.companionAtlasPolicy(companion.mipmapLevels()), levels);
                var payload = retainedPayloads.get(companionAtlasKey(companion));
                List<TextureData> uploads = payload == null
                    ? defaultFillUploads(companion)
                    : payload.initialUploads();
                var handle = uploadOwned(textures, spec, parameters,
                    "companion/" + companion.base().value() + "/" + companion.kind().name(),
                    uploads);
                companionHandles.put(companion, handle);
                companionPayloads.put(companion, payload);
            }
        }

        private String companionAtlasKey(CompanionAtlasPlan companion) {
            return "companionAtlas:" + companion.base().value() + ":"
                + companion.kind().name();
        }

        private List<TextureData> defaultFillUploads(CompanionAtlasPlan companion) {
            int packed = companion.defaultFill();
            byte r = (byte) (packed >>> 24);
            byte g = (byte) (packed >>> 16 & 0xFF);
            byte b = (byte) (packed >>> 8 & 0xFF);
            byte a = (byte) (packed & 0xFF);
            List<TextureData> uploads = new ArrayList<>();
            var layout = new PixelLayout.Color(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE);
            for (int level = 0; level <= companion.mipmapLevels(); level++) {
                int w = Math.max(1, companion.width() >> level);
                int h = Math.max(1, companion.height() >> level);
                var bytes = new byte[w * h * 4];
                for (int i = 0; i < w * h; i++) {
                    bytes[i * 4] = r;
                    bytes[i * 4 + 1] = g;
                    bytes[i * 4 + 2] = b;
                    bytes[i * 4 + 3] = a;
                }
                uploads.add(new TextureData(TextureAllocationTarget.TEXTURE_2D,
                    new TextureRegion(0, 0, 0, w, h, 1), level, layout,
                    ByteBuffer.wrap(bytes)));
            }
            return uploads;
        }

        private void allocateDefaults(TextureService textures) {
            for (var kind : CompanionKind.values()) {
                if (!kindEnabled(kind)) {
                    continue;
                }
                var spec = new TextureSpec.ColorTextureSpec(
                    TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGBA8,
                    new PixelLayout.Color(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE),
                    new TextureExtent(1, 1, 1), 1);
                var parameters = ParameterPolicy.facadeParameters(
                    TextureAllocationTarget.TEXTURE_2D,
                    ParameterPolicy.standaloneDefaultPolicy(), 1);
                var handle = uploadOwned(textures, spec, parameters,
                    "default/" + kind.name(),
                    List.of(defaultFillUploads(kindFillPlan(kind)).getFirst()));
                defaultHandles.put(kind, handle);
            }
        }

        private CompanionAtlasPlan kindFillPlan(CompanionKind kind) {
            int fill = kind == CompanionKind.NORMALS ? 0xFF7F7FFF : 0x00000000;
            return new CompanionAtlasPlan(new AtlasId("default"), kind, 1, 1, 0,
                List.of(), fill);
        }

        private boolean kindEnabled(CompanionKind kind) {
            var state = plan.macroState();
            return kind == CompanionKind.NORMALS ? state.normalMap() : state.specularMap();
        }

        private void allocateNoise(TextureService textures, String configurationIdentity,
                                   Map<String, TextureUploadPayload> payloads) {
            switch (plan.noise()) {
                case NoisePlan.Disabled disabled -> {
                }
                case NoisePlan.Generated generated -> {
                    int resolution = generated.resolution();
                    var bytes = NoiseGenerator.generateRgb(resolution);
                    var spec = new TextureSpec.ColorTextureSpec(
                        TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGB8,
                        new PixelLayout.Color(PixelFormat.RGB, PixelType.UNSIGNED_BYTE),
                        new TextureExtent(resolution, resolution, 1), 1);
                    var parameters = ParameterPolicy.facadeParameters(
                        TextureAllocationTarget.TEXTURE_2D,
                        ParameterPolicy.generatedNoisePolicy(), 1);
                    var layout = new PixelLayout.Color(PixelFormat.RGB,
                        PixelType.UNSIGNED_BYTE);
                    var handle = uploadOwned(textures, spec, parameters, "noise/generated",
                        List.of(new TextureData(TextureAllocationTarget.TEXTURE_2D,
                            new TextureRegion(0, 0, 0, resolution, resolution, 1), 0,
                            layout, ByteBuffer.wrap(bytes))));
                    noiseHandles.put(generated, handle);
                }
                case NoisePlan.FromPack fromPack -> {
                    var payload = payloads.get(noiseOverrideKey(fromPack));
                    var spec = new TextureSpec.ColorTextureSpec(
                        TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGBA8,
                        new PixelLayout.Color(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE),
                        extentOf(payload), mipCountOf(payload));
                    var parameters = ParameterPolicy.facadeParameters(
                        TextureAllocationTarget.TEXTURE_2D,
                        ParameterPolicy.generatedNoisePolicy(), mipCountOf(payload));
                    var handle = uploadOwned(textures, spec, parameters,
                        "noise/pack", payload.initialUploads());
                    noiseHandles.put(fromPack, handle);
                }
            }
        }

        private String noiseOverrideKey(NoisePlan.FromPack fromPack) {
            return "noise:" + fromPack.image().canonicalString();
        }

        private TextureExtent extentOf(TextureUploadPayload payload) {
            var first = payload.initialUploads().getFirst();
            return new TextureExtent(first.region().width(), first.region().height(),
                first.region().depth());
        }

        private int mipCountOf(TextureUploadPayload payload) {
            int max = 0;
            for (var data : payload.initialUploads()) {
                max = Math.max(max, data.mipLevel());
            }
            return max + 1;
        }

        private static String logicalSourceOf(
                com.schmaloogium.engine.textures.TextureSourceIdentity identity) {
            return switch (identity) {
                case com.schmaloogium.engine.textures.TextureSourceIdentity.OwnedUpload owned ->
                    owned.logicalSource();
                case com.schmaloogium.engine.textures.TextureSourceIdentity.ForeignLive foreign ->
                    foreign.exactResourceIdentity();
            };
        }
        private void allocateCustoms(TextureService textures,
                                     Map<String, TextureUploadPayload> payloads) {
            for (var entry : plan.customTextures()) {
                var spec = colorSpec(entry);
                var parameters = ParameterPolicy.facadeParameters(entry.target(),
                    entry.parameters(), spec.mipLevels());
                var handle = textures.create("custom/" + entry.key().sampler() + "/"
                    + entry.phase3Ordinal());
                creationOrder.add(handle);
                textures.allocate(handle, spec);
                textures.setParameters(handle, parameters);
                var payload = payloads.get(logicalSourceOf(entry.source()));
                if (payload != null) {
                    for (var data : payload.initialUploads()) {
                        textures.upload(handle, data);
                    }
                }
                customHandles.put(entry, handle);
            }
        }

        private TextureSpec.ColorTextureSpec colorSpec(
                com.schmaloogium.engine.textures.CustomTexturePlanEntry entry) {
            var format = switch (entry.upload()) {
                case TextureUploadSpec.OneD s -> s.internalFormat();
                case TextureUploadSpec.TwoD s -> s.internalFormat();
                case TextureUploadSpec.ThreeD s -> s.internalFormat();
                case TextureUploadSpec.Rectangle s -> s.internalFormat();
            };
            var extent = switch (entry.upload()) {
                case TextureUploadSpec.OneD s -> new TextureExtent(s.width(), 1, 1);
                case TextureUploadSpec.TwoD s -> new TextureExtent(s.width(), s.height(), 1);
                case TextureUploadSpec.ThreeD s -> new TextureExtent(s.width(), s.height(),
                    s.depth());
                case TextureUploadSpec.Rectangle s -> new TextureExtent(s.width(),
                    s.height(), 1);
            };
            return new TextureSpec.ColorTextureSpec(entry.target(), format,
                new PixelLayout.Color(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE), extent, 1);
        }

        private TextureHandle uploadOwned(TextureService textures, TextureSpec spec,
                                          TextureParameters parameters, String label,
                                          List<TextureData> uploads) {
            var handle = textures.create(label);
            creationOrder.add(handle);
            textures.allocate(handle, spec);
            textures.setParameters(handle, parameters);
            for (var data : uploads) {
                textures.upload(handle, data);
            }
            return handle;
        }

        private void destroyAll(TextureService textures) {
            for (int i = creationOrder.size() - 1; i >= 0; i--) {
                try {
                    textures.delete(creationOrder.get(i));
                } catch (RuntimeException ignored) {
                    // Best-effort teardown on the failure path; never masks the original.
                }
            }
            creationOrder.clear();
            companionHandles.clear();
            defaultHandles.clear();
            noiseHandles.clear();
            customHandles.clear();
        }

        private TextureCandidateTable candidateTable() {
            return Objects.requireNonNull(candidateTable, "candidateTable");
        }

        private List<PendingUpload> resolveAnimation(
            com.schmaloogium.engine.textures.AtlasAnimationSnapshot snapshot,
            long currentEpoch) {
            List<PendingUpload> pending = new ArrayList<>();
            var atlasDescriptor = capturedAtlases.get(snapshot.atlas());
            if (atlasDescriptor == null
                || snapshot.resourceReloadEpoch() != currentEpoch) {
                return pending;
            }
            for (var companion : plan.companions()) {
                if (!companion.base().equals(snapshot.atlas())) {
                    continue;
                }
                var handle = companionHandles.get(companion);
                if (handle == null) {
                    continue;
                }
                for (var state : snapshot.sprites()) {
                    var datas = companionFrames(companion, state);
                    for (var data : datas) {
                        pending.add(new PendingUpload(handle, data, state.iconName()));
                    }
                }
            }
            return pending;
        }

        private List<TextureData> companionFrames(CompanionAtlasPlan companion,
                com.schmaloogium.engine.textures.SpriteAnimationState state) {
            var payload = companionPayloads.get(companion);
            if (payload == null) {
                return List.of();
            }
            for (var row : payload.animationFrames()) {
                if (row.iconName().equals(state.iconName())
                    && row.sourceFrameIndex() == state.nextSourceFrameIndex()) {
                    return row.uploads();
                }
            }
            return List.of();
        }

        private record PendingUpload(TextureHandle handle, TextureData data,
                                     String iconName) {
        }
    }

    /** The leased immutable overlay view handed to Phase 5 (§8.2). */
    private static final class LeasedOverlay implements TextureOverlayLease {
        private final TexturePublication publication;
        private final AtlasBindingObservation.Authenticated observation;
        private final EngineTextureSystem owner;
        private volatile boolean closed;

        private LeasedOverlay(TexturePublication publication,
                              AtlasBindingObservation.Authenticated observation,
                              EngineTextureSystem owner) {
            this.publication = publication;
            this.observation = observation;
            this.owner = owner;
        }

        @Override
        public TextureOverlayPublicationId id() {
            return publication.id();
        }

        @Override
        public RegistryFingerprint registryFingerprint() {
            return publication.registryFingerprint();
        }

        @Override
        public long registryGeneration() {
            return publication.registryGeneration();
        }

        @Override
        public long resourceReloadEpoch() {
            return publication.resourceReloadEpoch();
        }

        @Override
        public ConfigurationFingerprint configurationFingerprint() {
            return publication.plan().inputs().configuration().fingerprint();
        }

        @Override
        public FixedSamplerPolicyFingerprint policyFingerprint() {
            return com.schmaloogium.engine.buffers.FixedSamplerPolicies.appB3Fingerprint();
        }

        @Override
        public TextureCandidateTable candidates() {
            return publication.candidates();
        }

        @Override
        public BaseAtlasContext baseAtlasContext() {
            return observation.association();
        }

        @Override
        public Optional<TextureHandleRef> baseTexture() {
            return observation.base();
        }

        @Override
        public BaseAtlasContext atlasContext(TextureHandleRef base) {
            Objects.requireNonNull(base, "base");
            return baseAtlasContext();
        }

        @Override
        public boolean isCurrent() {
            return !closed && owner.leaseUsable(this);
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                owner.leaseClosed(this);
            }
        }
    }
}
