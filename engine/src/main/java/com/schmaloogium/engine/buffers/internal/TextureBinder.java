// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BindingOrigin;
import com.schmaloogium.engine.buffers.BindingOriginKind;
import com.schmaloogium.engine.buffers.FixedSamplerPlanResult;
import com.schmaloogium.engine.buffers.BindingPurpose;
import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.buffers.PassBufferSnapshot;
import com.schmaloogium.engine.buffers.ResolvedSamplerBinding;
import com.schmaloogium.engine.buffers.TextureBindingAction;
import com.schmaloogium.engine.buffers.TextureBindingDegradation;
import com.schmaloogium.engine.buffers.TextureBindingDiagnostic;
import com.schmaloogium.engine.buffers.TextureBindingDiagnosticCode;
import com.schmaloogium.engine.buffers.TextureBindingOutcome;
import com.schmaloogium.engine.buffers.TextureBindingRejection;
import com.schmaloogium.engine.buffers.TextureBindingResult;
import com.schmaloogium.engine.buffers.TextureBindingRow;
import com.schmaloogium.engine.buffers.TextureBindingSnapshot;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayFingerprint;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * The normative sixteen-row sampler resolution (PHASE_5_DOC §4.12.2 / App B.3 table): the
 * fixed sampler plan is resolved purely from the layout; each occupied unit receives one
 * backing object; unbacked fixed units degrade the program with SUPPRESS_DRAW. Execution
 * binds each BoundObject row once through {@code prepareUnitBindings(unitMask)}.
 */
public final class TextureBinder {

    TextureBindingResult bind(EstateCore core, PassBufferSnapshot snapshot,
            TextureOverlayLease overlay, TextureOverlayPublicationId expectedOverlay) {
        Objects.requireNonNull(snapshot);
        ProgramBindingSelection selection = snapshot.selection();
        if (!core.usable()) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.STALE_ESTATE_GENERATION);
        }
        if (core.openPass == null || !core.openPass.snapshot.equals(snapshot)) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.INVALID_PASS_SNAPSHOT);
        }
        if (core.openFrameId != snapshot.frameId()) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.WRONG_FRAME_ID);
        }
        if (snapshot.depthAttachmentEpoch() != core.depthAttachmentEpoch) {
            return new TextureBindingResult.Rejected(
                TextureBindingRejection.STALE_DEPTH_ATTACHMENT_EPOCH);
        }
        if (selection == null) {
            return new TextureBindingResult.Rejected(TextureBindingRejection.INVALID_INPUT);
        }

        ProgramSamplerLayout layout = selection.effectiveDescriptor().samplerLayout();
        BindingPurpose purpose;
        if (layout instanceof ProgramSamplerLayout.Shader shaderLayout) {
            purpose = BindingPurpose.SHADER;
        } else if (layout instanceof ProgramSamplerLayout.FixedFunctionEmpty) {
            purpose = BindingPurpose.FIXED_FUNCTION_PASSTHROUGH;
        } else {
            purpose = BindingPurpose.NONE;
        }
        List<TextureBindingRow> rows = new ArrayList<>();
        List<TextureBindingDiagnostic> diagnostics = new ArrayList<>();
        boolean degrade = false;
        Map<Integer, List<ResolvedSamplerBinding>> byUnit = new LinkedHashMap<>();
        if (purpose == BindingPurpose.SHADER) {
            ProgramSamplerLayout.Shader shaderLayout = (ProgramSamplerLayout.Shader) layout;
            FixedSamplerPlanResult plan = FixedSamplerPolicies.resolver().resolve(shaderLayout,
                snapshot.pass().step().stage(), snapshot.pass().step().band());
            if (plan instanceof FixedSamplerPlanResult.Invalid) {
                diagnostics.add(new TextureBindingDiagnostic(
                    TextureBindingDiagnosticCode.CONFLICTING_SAMPLER_TYPES, "layout",
                    OptionalInt.empty()));
                return new TextureBindingResult.Degraded(new TextureBindingDegradation(
                    selection, diagnostics, TextureBindingAction.SUPPRESS_DRAW));
            }
            for (ResolvedSamplerBinding binding
                    : ((FixedSamplerPlanResult.Ready) plan).bindings()) {
                byUnit.computeIfAbsent(binding.unit(), ignored -> new ArrayList<>())
                    .add(binding);
            }
        }
        for (int unit = 0; unit < 16; unit++) {
            List<ResolvedSamplerBinding> names = byUnit.get(unit);
            boolean passthroughRow = purpose == BindingPurpose.FIXED_FUNCTION_PASSTHROUGH
                && unit == 0;
            if (names == null && !passthroughRow) {
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
                continue;
            }
            List<ResolvedSamplerBinding> boundNames = names == null ? List.of() : names;
            TextureHandle backing = backingFor(core, unit);
            if (backing == null) {
                for (ResolvedSamplerBinding name : boundNames) {
                    diagnostics.add(new TextureBindingDiagnostic(
                        TextureBindingDiagnosticCode.PUBLICATION_UNAVAILABLE,
                        name.exactName(), OptionalInt.of(unit)));
                }
                degrade = true;
                rows.add(new TextureBindingRow(unit, new TextureBindingOutcome.Unused()));
                continue;
            }
            DeclaredGlslType.Sampler shape = boundNames.isEmpty()
                ? new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2,
                    false, false, false)
                : boundNames.get(0).shape();
            BindingOrigin origin = new BindingOrigin(BindingOriginKind.ESTATE, List.of());
            rows.add(new TextureBindingRow(unit,
                new TextureBindingOutcome.BoundObject(new TextureHandleRef.Borrowed(backing),
                    shape, boundNames, origin)));
        }
        // Execute: one prepareUnitBindings + one bind per BoundObject row.
        int mask = 0;
        for (TextureBindingRow row : rows) {
            if (row.outcome() instanceof TextureBindingOutcome.BoundObject bound) {
                mask |= 1 << row.unit();
            }
        }
        try {
            core.device.textures().prepareUnitBindings(mask);
            for (TextureBindingRow row : rows) {
                if (row.outcome() instanceof TextureBindingOutcome.BoundObject bound) {
                    core.device.textures().bindToUnit(row.unit(),
                        ((TextureHandleRef.Borrowed) bound.handle()).handle());
                }
            }
            List<com.schmaloogium.engine.gl.GLError> errors = core.device.drainErrors();
            if (!errors.isEmpty()) {
                core.diagnostics.report(BufferDiagnostics.backendFailure(
                    "schmaloogium.buffers.error.bindings.backend", errors.get(0).detail()));
                return new TextureBindingResult.BackendFailed(core.failure(
                    BufferFailureCode.UNEXPECTED_BACKEND,
                    "schmaloogium.buffers.error.bindings.backend"));
            }
        } catch (RuntimeException bindFailure) {
            core.diagnostics.report(BufferDiagnostics.backendFailure(
                "schmaloogium.buffers.error.bindings.backend", String.valueOf(bindFailure)));
            return new TextureBindingResult.BackendFailed(core.failure(
                BufferFailureCode.UNEXPECTED_BACKEND,
                "schmaloogium.buffers.error.bindings.backend"));
        }
        if (degrade) {
            return new TextureBindingResult.Degraded(new TextureBindingDegradation(
                selection, diagnostics, TextureBindingAction.SUPPRESS_DRAW));
        }
        return new TextureBindingResult.Bound(new Snapshot(core, snapshot, purpose,
            List.copyOf(rows), diagnostics));
    }

    /** Unit -> backing object per the sixteen-row table; null = unbacked at v0.1. Shadow
     *  units resolve to the Phase-5 neutral objects once the estate degraded or its real
     *  estate failed at build (§4.10 neutral shadow bindings for units 4/5/13/14). */
    TextureHandle backingFor(EstateCore core, int unit) {
        return switch (unit) {
            case 0 -> side(core, 0);
            case 1 -> side(core, 1);
            case 2 -> side(core, 2);
            case 3 -> side(core, 3);
            case 4 -> shadowDepthBacking(core, 0);
            case 5 -> core.shadowPlannedDepthCount() >= 2 ? shadowDepthBacking(core, 1) : null;
            case 6 -> core.cachedDepth.texture();
            case 7 -> side(core, 4);
            case 8 -> side(core, 5);
            case 9 -> side(core, 6);
            case 10 -> side(core, 7);
            case 11 -> core.plan.depthTextureCount() >= 2
                ? core.copyDestinations.get(0).boundTexture() : null;
            case 12 -> core.plan.depthTextureCount() >= 3
                ? core.copyDestinations.get(1).boundTexture() : null;
            case 13 -> shadowColorBacking(core, 0);
            case 14 -> core.shadowPlannedColorCount() >= 2 ? shadowColorBacking(core, 1) : null;
            case 15 -> null; // noisetex: P13 overlay at v0.1 (publication unavailable)
            default -> null;
        };
    }

    private TextureHandle shadowDepthBacking(EstateCore core, int index) {
        if (core.shadowNeutralBacked()) {
            return core.shadowNeutral == null ? null : core.shadowNeutral.depthByUnit(index);
        }
        return core.shadowTexture(index);
    }

    private TextureHandle shadowColorBacking(EstateCore core, int index) {
        if (core.shadowNeutralBacked()) {
            return core.shadowNeutral == null ? null : core.shadowNeutral.colorByUnit(index);
        }
        EstateCore.ShadowColorPair pair = core.shadowColorPair(index);
        return pair == null ? null : pair.readSide();
    }

    private TextureHandle side(EstateCore core, int row) {
        return row < core.colorPairs.size() ? core.colorPairs.get(row).readSide() : null;
    }

    private static final class Snapshot implements TextureBindingSnapshot {
        private final EstateCore core;
        private final PassBufferSnapshot snapshot;
        private final BindingPurpose purpose;
        private final List<TextureBindingRow> rows;
        private final List<TextureBindingDiagnostic> diagnostics;
        private boolean closed;

        Snapshot(EstateCore core, PassBufferSnapshot snapshot, BindingPurpose purpose,
            List<TextureBindingRow> rows, List<TextureBindingDiagnostic> diagnostics) {
            this.core = core;
            this.snapshot = snapshot;
            this.purpose = purpose;
            this.rows = rows;
            this.diagnostics = diagnostics;
        }

        @Override
        public long estateGeneration() {
            return core.generation;
        }

        @Override
        public long depthAttachmentEpoch() {
            return snapshot.depthAttachmentEpoch();
        }

        @Override
        public long frameId() {
            return snapshot.frameId();
        }

        @Override
        public PassDescriptor pass() {
            return snapshot.pass();
        }

        @Override
        public ProgramBindingSelection selection() {
            return snapshot.selection();
        }

        @Override
        public TextureOverlayPublicationId overlayPublication() {
            return new TextureOverlayPublicationId(core.generation,
                new TextureOverlayFingerprint("unpublished:v0.1"));
        }

        @Override
        public BindingPurpose purpose() {
            return purpose;
        }

        @Override
        public List<TextureBindingRow> rows() {
            return rows;
        }

        @Override
        public TextureBindingOutcome outcome(int unit) {
            return rows.get(unit).outcome();
        }

        @Override
        public List<TextureBindingDiagnostic> diagnostics() {
            return diagnostics;
        }

        @Override
        public boolean isCurrent() {
            return !closed && core.usable();
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
