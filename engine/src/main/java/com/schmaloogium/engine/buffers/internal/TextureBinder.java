// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BooleanSupplier;

import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.buffers.BindingOrigin;
import com.schmaloogium.engine.buffers.BindingOriginKind;
import com.schmaloogium.engine.buffers.BindingPurpose;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.BufferInventoryEntry;
import com.schmaloogium.engine.buffers.CandidateOrigin;
import com.schmaloogium.engine.buffers.ColorAllocationOrigin;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.FixedSamplerPlanResult;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.ResolvedBufferFormat;
import com.schmaloogium.engine.buffers.ResolvedSamplerBinding;
import com.schmaloogium.engine.buffers.TextureBindingAction;
import com.schmaloogium.engine.buffers.TextureBindingCandidate;
import com.schmaloogium.engine.buffers.TextureBindingDegradation;
import com.schmaloogium.engine.buffers.TextureBindingDiagnostic;
import com.schmaloogium.engine.buffers.TextureBindingDiagnosticCode;
import com.schmaloogium.engine.buffers.TextureBindingOutcome;
import com.schmaloogium.engine.buffers.TextureBindingRejection;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureBindingRow;
import com.schmaloogium.engine.buffers.TextureBindingSnapshot;
import com.schmaloogium.engine.buffers.TextureCandidateEntry;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.buffers.TextureOverlayAbsence;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramBindingSelections;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSelectionValidation;
import com.schmaloogium.engine.registry.StageId;

/** P5 §4.12: authenticate, resolve all fixed rows without GL, then transfer one lease. */
public final class TextureBinder {
    private static final DeclaredGlslType.Sampler FLOAT_2D = new DeclaredGlslType.Sampler(
        SampledKind.FLOAT, TextureDimension.D2, false, false, false);

    TextureBindingResult bind(EstateCore core, PassBufferSnapshot snapshot,
            TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay) {
        if (snapshot == null || overlay == null || expectedOverlay == null) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.INVALID_INPUT);
        }
        if (Thread.currentThread() != core.renderThread) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.WRONG_THREAD);
        }
        if (!core.usable() || snapshot.estateGeneration() != core.generation) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.STALE_ESTATE_GENERATION);
        }
        if (core.openFrameId == -1) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.NO_OPEN_FRAME);
        }
        if (core.openFrameId != snapshot.frameId()) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.WRONG_FRAME_ID);
        }
        if (core.openPass == null || core.openPass.snapshot != snapshot) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.INVALID_PASS_SNAPSHOT);
        }
        if (snapshot.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.STALE_DEPTH_ATTACHMENT_EPOCH);
        }
        return bindValidated(core, snapshot.pass(), snapshot.selection(),
            snapshot.depthAttachmentEpoch(), snapshot.frameId(), snapshot.readableTextures(),
            overlay, expectedOverlay,
            () -> core.openPass != null && core.openPass.snapshot == snapshot);
    }

    TextureBindingResult bindValidated(EstateCore core, PassDescriptor pass,
            ProgramBindingSelection selection, long depthEpoch, long frameId,
            Map<LogicalBuffer, TextureHandle> readable, TextureOverlayLease overlay,
            TextureOverlayPublicationId expectedOverlay, BooleanSupplier current) {
        TextureBindingRejection rejection = authenticate(core, pass, selection, overlay, expectedOverlay);
        if (rejection != null) {
            return new TextureBindingResult.Rejected(rejection);
        }
        ProgramSamplerLayout layout = selection.effectiveDescriptor().samplerLayout();
        BindingPurpose purpose = layout instanceof ProgramSamplerLayout.Shader ? BindingPurpose.SHADER
            : pass.step().stage() == StageId.FINAL ? BindingPurpose.FIXED_FUNCTION_PASSTHROUGH
            : BindingPurpose.NONE;
        Map<Integer, List<ResolvedSamplerBinding>> byUnit = new LinkedHashMap<>();
        List<TextureBindingDiagnostic> diagnostics = new ArrayList<>();
        if (purpose == BindingPurpose.SHADER) {
            FixedSamplerPlanResult plan = FixedSamplerPolicies.resolver().resolve(layout,
                selection.effectiveStage(), selection.actualBand());
            if (plan instanceof FixedSamplerPlanResult.Invalid) {
                diagnostics.add(new TextureBindingDiagnostic(
                    TextureBindingDiagnosticCode.CONFLICTING_SAMPLER_TYPES, "layout", OptionalInt.empty()));
                return new TextureBindingResult.Degraded(new TextureBindingDegradation(
                    selection, diagnostics, TextureBindingAction.SUPPRESS_DRAW));
            }
            for (ResolvedSamplerBinding binding : ((FixedSamplerPlanResult.Ready) plan).bindings()) {
                byUnit.computeIfAbsent(binding.unit(), ignored -> new ArrayList<>()).add(binding);
            }
        }
        AppB3Policy.Domain domain = AppB3Policy.domainOf(selection.effectiveStage(), selection.actualBand());
        boolean platform = domain == AppB3Policy.Domain.GBUFFERS || domain == AppB3Policy.Domain.SHADOW_WINDOW;
        List<TextureBindingRow> rows = new ArrayList<>(16);
        BaseAtlasContext base = overlay.baseAtlasContext();
        boolean degraded = false;
        int mask = 0;
        for (int unit = 0; unit < 16; unit++) {
            List<ResolvedSamplerBinding> names = byUnit.get(unit);
            if (names == null) {
                if (unit == 0 && purpose == BindingPurpose.FIXED_FUNCTION_PASSTHROUGH) {
                    TextureHandle backing = backingFor(core, readable, unit, domain);
                    if (backing == null || !compatibleBacking(core, unit, FLOAT_2D, domain)) {
                        degraded = true;
                        diagnostics.add(new TextureBindingDiagnostic(
                            TextureBindingDiagnosticCode.MISSING_BACKING, "colortex0", OptionalInt.of(unit)));
                        rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
                    } else {
                        rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.BoundObject(
                            new TextureHandleRef.Borrowed(backing), FLOAT_2D, List.of(),
                            new BindingOrigin(BindingOriginKind.ESTATE, List.of()))));
                        mask |= 1 << unit;
                    }
                } else {
                    rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
                }
                continue;
            }
            Choice winner = null;
            boolean missing = false;
            for (ResolvedSamplerBinding name : names) {
                Choice choice = resolve(core, readable, overlay, selection.effectiveStage(), domain,
                    name, base, diagnostics);
                if (choice == null) {
                    missing = true;
                } else if (winner == null) {
                    winner = choice;
                } else if (!winner.sameObject(choice)) {
                    diagnostics.add(new TextureBindingDiagnostic(
                        TextureBindingDiagnosticCode.CONFLICTING_CANDIDATES,
                        name.exactName(), OptionalInt.of(unit)));
                    missing = true;
                }
            }
            if (missing || winner == null) {
                degraded = true;
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
            } else if (winner.handle == null) {
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.ForeignRetained(names)));
            } else {
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.BoundObject(
                    winner.handle, names.getFirst().shape(), names,
                    new BindingOrigin(winner.origin, List.of()))));
                mask |= 1 << unit;
                if (platform && unit == 0) {
                    base = overlay.atlasContext(winner.handle);
                }
            }
        }
        diagnostics.sort(Comparator.comparingInt((TextureBindingDiagnostic d) -> d.unit().orElse(-1))
            .thenComparing(TextureBindingDiagnostic::exactName)
            .thenComparing(TextureBindingDiagnostic::code));
        if (degraded) {
            return new TextureBindingResult.Degraded(new TextureBindingDegradation(
                selection, diagnostics, TextureBindingAction.SUPPRESS_DRAW));
        }
        try {
            core.device.textures().prepareUnitBindings(mask);
            for (TextureBindingRow row : rows) {
                if (row.outcome() instanceof TextureBindingOutcome.BoundObject bound) {
                    core.device.textures().bindToUnit(row.unit(), handle(bound.handle()));
                }
            }
            var errors = core.device.drainErrors();
            if (!errors.isEmpty()) {
                throw new IllegalStateException(errors.getFirst().detail());
            }
        } catch (RuntimeException failure) {
            core.diagnostics.report(BufferDiagnostics.backendFailure(
                "schmaloogium.buffers.error.bindings.backend", String.valueOf(failure)));
            return new TextureBindingResult.BackendFailed(core.failure(
                BufferFailureCode.UNEXPECTED_BACKEND, "schmaloogium.buffers.error.bindings.backend"));
        }
        logBindingRows(core.generation, pass, rows);
        return new TextureBindingResult.Bound(new Snapshot(core, pass, selection, depthEpoch,
            frameId, overlay, purpose, List.copyOf(rows), List.copyOf(diagnostics), current));
    }

    private TextureBindingRejection authenticate(EstateCore core, PassDescriptor pass,
            ProgramBindingSelection selection, TextureOverlayLease overlay,
            TextureOverlayPublicationId expected) {
        if (selection == null || overlay == null || expected == null) {
            return TextureBindingRejection.INVALID_INPUT;
        }
        var validation = ProgramBindingSelections.validateSelection(selection, selection.originatingContext());
        if (validation instanceof ProgramSelectionValidation.Rejected rejected) {
            return switch (rejected.reason()) {
                case INVALID_ISSUER -> TextureBindingRejection.INVALID_PROGRAM_SELECTION;
                case STALE_GENERATION -> TextureBindingRejection.STALE_REGISTRY_GENERATION;
                case STALE_CONTEXT, WRONG_STAGE_BAND -> TextureBindingRejection.PROGRAM_SELECTION_MISMATCH;
                case PROVIDER_LAYOUT_MISMATCH -> TextureBindingRejection.SAMPLER_LAYOUT_MISMATCH;
            };
        }
        if (!pass.slot().equals(selection.requested()) || pass.step().stage() != selection.effectiveStage()
                || pass.step().band() != selection.actualBand()) {
            return TextureBindingRejection.PROGRAM_SELECTION_MISMATCH;
        }
        if (!overlay.isCurrent()) return TextureBindingRejection.CLOSED_OVERLAY_LEASE;
        if (!expected.equals(overlay.id())) return TextureBindingRejection.OVERLAY_PUBLICATION_ID_MISMATCH;
        if (!core.registryFingerprint.equals(overlay.registryFingerprint())
                || !selection.registryFingerprint().equals(overlay.registryFingerprint())) {
            return TextureBindingRejection.REGISTRY_FINGERPRINT_MISMATCH;
        }
        if (selection.registryGeneration() != overlay.registryGeneration()) {
            return TextureBindingRejection.STALE_REGISTRY_GENERATION;
        }
        if (overlay.id().generation() != core.generation
                || !Objects.equals(core.configurationFingerprint, overlay.configurationFingerprint())) {
            return TextureBindingRejection.CONFIGURATION_FINGERPRINT_MISMATCH;
        }
        if (!FixedSamplerPolicies.appB3Fingerprint().equals(overlay.policyFingerprint())
                || !overlay.policyFingerprint().equals(selection.effectiveDescriptor().samplerLayout().policyFingerprint())) {
            return TextureBindingRejection.SAMPLER_LAYOUT_MISMATCH;
        }
        return null;
    }

    private Choice resolve(EstateCore core, Map<LogicalBuffer, TextureHandle> readable,
            TextureOverlayLease overlay, StageId stage, AppB3Policy.Domain domain,
            ResolvedSamplerBinding binding, BaseAtlasContext base,
            List<TextureBindingDiagnostic> diagnostics) {
        FixedSamplerName name = FixedSamplerName.valueOf(binding.exactName().toUpperCase(java.util.Locale.ROOT));
        TextureCandidateEntry entry = overlay.candidates().entry(stage, name);
        List<TextureBindingCandidate> candidates = entry instanceof TextureCandidateEntry.Candidates present
            ? present.candidates() : List.of();
        TextureBindingCandidate custom = null;
        for (TextureBindingCandidate candidate : candidates) {
            if (candidate.origin() instanceof CandidateOrigin.Custom
                    && compatible(candidate, stage, binding)
                    && (custom == null || candidate.candidateOrdinal() > custom.candidateOrdinal())) {
                custom = candidate;
            }
        }
        if (custom != null) return new Choice(custom.handle(), BindingOriginKind.CUSTOM, custom);
        boolean platform = domain == AppB3Policy.Domain.GBUFFERS || domain == AppB3Policy.Domain.SHADOW_WINDOW;
        int unit = binding.unit();
        if (platform && (unit == 2 || unit == 3) || unit == 15) {
            CompanionKind kind = unit == 2 ? CompanionKind.NORMALS : CompanionKind.SPECULAR;
            TextureBindingCandidate selected = null;
            boolean matchedAtlas = false;
            if (unit != 15) {
                for (TextureBindingCandidate candidate : candidates) {
                    if (compatible(candidate, stage, binding)
                            && candidate.origin() instanceof CandidateOrigin.Companion c
                            && base instanceof BaseAtlasContext.Atlas a
                            && c.atlas().equals(a.atlas()) && c.kind() == kind) {
                        matchedAtlas = true;
                        break;
                    }
                }
            }
            for (TextureBindingCandidate candidate : candidates) {
                if (!compatible(candidate, stage, binding)) continue;
                boolean companion = candidate.origin() instanceof CandidateOrigin.Companion c
                    && base instanceof BaseAtlasContext.Atlas a && c.atlas().equals(a.atlas()) && c.kind() == kind;
                boolean fallback = candidate.origin() instanceof CandidateOrigin.DefaultFill f && f.kind() == kind;
                boolean noise = unit == 15 && candidate.origin() instanceof CandidateOrigin.Noise;
                if (unit == 15 ? !noise : matchedAtlas ? !companion : !fallback) continue;
                if (selected != null && !new Choice(selected.handle(), BindingOriginKind.NEUTRAL, selected)
                        .sameObject(new Choice(candidate.handle(), BindingOriginKind.NEUTRAL, candidate))) {
                    diagnostics.add(new TextureBindingDiagnostic(TextureBindingDiagnosticCode.CONFLICTING_CANDIDATES,
                        binding.exactName(), OptionalInt.of(unit)));
                    return null;
                }
                selected = candidate;
            }
            if (selected != null) return new Choice(selected.handle(), unit == 15 ? BindingOriginKind.NOISE
                : matchedAtlas ? BindingOriginKind.COMPANION : BindingOriginKind.NEUTRAL, selected);
            // Disabled companion kinds are still allowed their estate-owned neutral, never another atlas.
            if (!(entry instanceof TextureCandidateEntry.Absent absent
                    && absent.reason() == TextureOverlayAbsence.NOT_CONFIGURED && unit != 15)) {
                diagnostics.add(new TextureBindingDiagnostic(TextureBindingDiagnosticCode.MISSING_BACKING,
                    binding.exactName(), OptionalInt.of(unit)));
                return null;
            }
        }
        TextureBindingDiagnosticCode code = candidates.isEmpty()
            ? entry instanceof TextureCandidateEntry.Absent absent ? switch (absent.reason()) {
                case NOT_CONFIGURED -> TextureBindingDiagnosticCode.NOT_CONFIGURED;
                case NOT_APPLICABLE_TO_STAGE -> TextureBindingDiagnosticCode.NOT_APPLICABLE_TO_STAGE;
                case PUBLICATION_UNAVAILABLE -> TextureBindingDiagnosticCode.PUBLICATION_UNAVAILABLE;
            } : TextureBindingDiagnosticCode.NO_CANDIDATE
            : TextureBindingDiagnosticCode.INCOMPATIBLE_CANDIDATE;
        Choice fallback = fallback(core, readable, overlay, domain, binding);
        if (fallback == null) {
            diagnostics.add(new TextureBindingDiagnostic(code, binding.exactName(), OptionalInt.of(unit)));
        }
        return fallback;
    }

    /** P5 §4.12.2 last resort: the estate's own attachment, or the foreign platform texture. */
    private Choice fallback(EstateCore core, Map<LogicalBuffer, TextureHandle> readable,
            TextureOverlayLease overlay, AppB3Policy.Domain domain, ResolvedSamplerBinding binding) {
        boolean platform = domain == AppB3Policy.Domain.GBUFFERS || domain == AppB3Policy.Domain.SHADOW_WINDOW;
        int unit = binding.unit();
        if (!compatibleBacking(core, unit, binding.shape(), domain)) return null;
        if (platform && unit <= 1) {
            return new Choice(unit == 0 ? overlay.baseTexture().orElse(null) : null, BindingOriginKind.FOREIGN, null);
        }
        TextureHandle backing = backingFor(core, readable, unit, domain);
        return backing == null ? null : new Choice(new TextureHandleRef.Borrowed(backing),
            platform && unit <= 3 ? BindingOriginKind.NEUTRAL : BindingOriginKind.ESTATE, null);
    }

    private boolean compatible(TextureBindingCandidate candidate, StageId stage, ResolvedSamplerBinding binding) {
        if (candidate.expandedStage() != stage || !candidate.exactSamplerName().equals(binding.exactName())
                || !candidate.shape().equals(binding.shape())) return false;
        TextureTarget target = switch (binding.shape().dimension()) {
            case D1 -> TextureTarget.TEXTURE_1D;
            case D2 -> TextureTarget.TEXTURE_2D;
            case D3 -> TextureTarget.TEXTURE_3D;
            case RECTANGLE -> TextureTarget.RECTANGLE;
            default -> null;
        };
        return target != null && candidate.target() == target && !binding.shape().arrayed()
            && !binding.shape().multisample() && handle(candidate.handle()) != null;
    }

    private boolean compatibleBacking(EstateCore core, int unit, DeclaredGlslType.Sampler shape,
            AppB3Policy.Domain domain) {
        if (shape.dimension() != TextureDimension.D2 || shape.arrayed() || shape.multisample()) return false;
        boolean compare = (unit == 4 || unit == 5)
            && core.plan.plannedProjection().shadow().depth().size() > unit - 4
            && core.plan.plannedProjection().shadow().depth().get(unit - 4).hardwareFiltering();
        if (shape.shadow() != compare) return false;
        SampledKind sample = SampledKind.FLOAT;
        LogicalBuffer logical = logicalForUnit(unit, domain);
        if (logical != null) {
            for (BufferInventoryEntry entry : core.plan.inventory().entries()) {
                if (entry.buffer().equals(logical) && entry.format() instanceof ResolvedBufferFormat.Color color) {
                    sample = switch (FormatTable.row(color.value()).numericClass()) {
                        case SIGNED_INTEGER -> SampledKind.SIGNED_INT;
                        case UNSIGNED_INTEGER -> SampledKind.UNSIGNED_INT;
                        default -> SampledKind.FLOAT;
                    };
                    break;
                }
            }
            if (logical.domain() == BufferDomain.COLORTEX
                    && logical.index().value() < core.realized.projection().colorBuffers().size()
                    && core.realized.projection().colorBuffers().get(logical.index().value()).allocation()
                        .map(allocation -> allocation.origin() == ColorAllocationOrigin.RGBA_FALLBACK)
                        .orElse(false)) {
                sample = SampledKind.FLOAT;
            }
        }
        return shape.sample() == sample;
    }

    private TextureHandle backingFor(EstateCore core, Map<LogicalBuffer, TextureHandle> readable,
            int unit, AppB3Policy.Domain domain) {
        boolean platform = domain == AppB3Policy.Domain.GBUFFERS || domain == AppB3Policy.Domain.SHADOW_WINDOW;
        if (platform && unit == 2) return core.companionNormalsNeutral;
        if (platform && unit == 3) return core.companionSpecularNeutral;
        LogicalBuffer logical = logicalForUnit(unit, domain);
        return logical == null ? null : readable.get(logical);
    }

    private LogicalBuffer logicalForUnit(int unit, AppB3Policy.Domain domain) {
        boolean platform = domain == AppB3Policy.Domain.GBUFFERS || domain == AppB3Policy.Domain.SHADOW_WINDOW;
        return switch (unit) {
            case 0, 1, 2, 3 -> platform ? null : new LogicalBuffer(BufferDomain.COLORTEX, new BufferIndex(unit));
            case 4, 5 -> new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(unit - 4));
            case 6 -> new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(0));
            case 7, 8, 9, 10 -> new LogicalBuffer(BufferDomain.COLORTEX, new BufferIndex(unit - 3));
            case 11, 12 -> new LogicalBuffer(BufferDomain.DEPTH, new BufferIndex(unit - 10));
            case 13, 14 -> new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(unit - 13));
            default -> null;
        };
    }

    private static TextureHandle handle(TextureHandleRef reference) {
        return switch (reference) {
            case TextureHandleRef.Owned owned -> owned.handle();
            case TextureHandleRef.Borrowed borrowed -> borrowed.handle();
        };
    }

    private record Choice(TextureHandleRef handle, BindingOriginKind origin, TextureBindingCandidate candidate) {
        boolean sameObject(Choice other) {
            if (!Objects.equals(handle, other.handle)) return false;
            if (candidate == null || other.candidate == null) return candidate == other.candidate;
            return candidate.source().equals(other.candidate.source())
                && candidate.parameterizationFingerprint().equals(other.candidate.parameterizationFingerprint())
                && candidate.parameters().equals(other.candidate.parameters());
        }
    }

    private static final java.util.Set<String> BINDINGS_LOGGED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static void logBindingRows(long generation, PassDescriptor pass, List<TextureBindingRow> rows) {
        if (!BINDINGS_LOGGED.add(generation + ":" + pass.slot())) return;
        StringBuilder text = new StringBuilder();
        for (TextureBindingRow row : rows) {
            if (row.outcome() instanceof TextureBindingOutcome.BoundObject bound) {
                text.append(' ').append(row.unit()).append('=');
                for (ResolvedSamplerBinding name : bound.names()) text.append(name.exactName()).append('/');
                text.append("->").append(handle(bound.handle())).append('@').append(bound.origin().kind());
            }
        }
        Logs.channel(LogChannels.BUFFERS).info("H5-BIND {} (estate {}):{}", pass.slot(), generation, text);
    }

    private static final class Snapshot implements TextureBindingSnapshot {
        private final EstateCore core;
        private final PassDescriptor pass;
        private final ProgramBindingSelection selection;
        private final long generation;
        private final long depthEpoch;
        private final long frameId;
        private final TextureOverlayLease overlay;
        private final TextureOverlayPublicationId publication;
        private final BindingPurpose purpose;
        private final List<TextureBindingRow> rows;
        private final List<TextureBindingDiagnostic> diagnostics;
        private final BooleanSupplier current;
        private boolean closed;

        Snapshot(EstateCore core, PassDescriptor pass, ProgramBindingSelection selection,
                long depthEpoch, long frameId, TextureOverlayLease overlay, BindingPurpose purpose,
                List<TextureBindingRow> rows, List<TextureBindingDiagnostic> diagnostics, BooleanSupplier current) {
            this.core = core;
            this.pass = pass;
            this.selection = selection;
            this.generation = core.generation;
            this.depthEpoch = depthEpoch;
            this.frameId = frameId;
            this.overlay = overlay;
            this.publication = overlay.id();
            this.purpose = purpose;
            this.rows = rows;
            this.diagnostics = diagnostics;
            this.current = current;
        }

        @Override public long estateGeneration() { return generation; }
        @Override public long depthAttachmentEpoch() { return depthEpoch; }
        @Override public long frameId() { return frameId; }
        @Override public PassDescriptor pass() { return pass; }
        @Override public ProgramBindingSelection selection() { return selection; }
        @Override public TextureOverlayPublicationId overlayPublication() { return publication; }
        @Override public BindingPurpose purpose() { return purpose; }
        @Override public List<TextureBindingRow> rows() { return rows; }
        @Override public TextureBindingOutcome outcome(int unit) { return rows.get(unit).outcome(); }
        @Override public List<TextureBindingDiagnostic> diagnostics() { return diagnostics; }
        @Override public boolean isCurrent() {
            return !closed && core.usable() && core.generation == generation
                && core.depthAttachmentEpoch == depthEpoch && core.openFrameId == frameId
                && current.getAsBoolean() && overlay.isCurrent()
                && ProgramBindingSelections.validateSelection(selection, selection.originatingContext())
                    instanceof ProgramSelectionValidation.Valid;
        }
        @Override public void close() {
            if (!closed) {
                closed = true;
                overlay.close();
            }
        }
    }
}
