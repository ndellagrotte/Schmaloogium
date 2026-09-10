// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

import java.util.List;
import java.util.Objects;

/**
 * The notifier-to-producer audit (PHASE_6_DOC §4.12): every signal row with the Phase 6
 * consumer cells, the required producer moment, and the owner/milestone. Fifteen rows,
 * closed; the table is the Phase 6 side of the future Phase 7 integration review. There
 * are no nullable global listener fields behind any row (D-P6-8) — the runtime owns the
 * typed {@code UniformEventSink} before any plan is built, so declaring {@code blendFunc}
 * cannot NPE even before Phase 7 wiring exists.
 */
public final class ProducerAudit {

    /** Closed owner domain of the audit table. */
    public enum Owner {
        PHASE_7,
        PHASE_8,
        PHASE_9,
        PHASE_13,
        CADENCE_ENGINE
    }

    /**
     * One audit row.
     *
     * @param signal            the signal/notifier name
     * @param consumerUniforms  Phase 6 cells the signal feeds
     * @param requiredMoment    the producer hook's required moment
     * @param owner             accountable producer phase
     * @param milestone         value milestone
     */
    public record Row(String signal, String consumerUniforms, String requiredMoment,
            Owner owner, Milestone milestone) {

        public Row {
            Objects.requireNonNull(signal, "signal");
            Objects.requireNonNull(consumerUniforms, "consumerUniforms");
            Objects.requireNonNull(requiredMoment, "requiredMoment");
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(milestone, "milestone");
        }
    }

    private static final List<Row> ROWS = List.of(
            new Row("frame begin",
                    "tick/frame cells, previous snapshots, all three smoothers",
                    "before any resize or clear in world-frame orchestration",
                    Owner.PHASE_7, Milestone.V0_1),
            new Row("gbuffer matrix capture",
                    "current/inverse gbuffer matrices (captureGbufferMatrices)",
                    "later post-camera setup hook, once per frame; the ordinal-zero clear "
                            + "precedes camera setup and is NOT capture",
                    Owner.PHASE_7, Milestone.V0_1),
            new Row("celestial rotation",
                    "sunPosition/moonPosition/shadowLightPosition/upPosition (updateCelestial)",
                    "inside sky rotation after FF transforms are established; immediate upload "
                            + "if a shader token is current",
                    Owner.PHASE_8, Milestone.V0_2),
            new Row("shadow camera",
                    "four shadow matrices (updateShadowMatrices)",
                    "after Phase 8 installs its shadow FF camera, before shadow draw activation",
                    Owner.PHASE_8, Milestone.V0_2),
            new Row("fog mode/start/end/density/color",
                    "fogMode, fogDensity, fogColor (updateFog)",
                    "GlStateManager-facing fog mutation sites plus frame fallback; immediate "
                            + "upload if active",
                    Owner.PHASE_7, Milestone.V0_1),
            new Row("blend enable/factors",
                    "blendFunc (updateBlend)",
                    "successful effective GlStateManager/backend changes only, including Phase 1 "
                            + "override acquisition/release; suppressed attempts emit no event; "
                            + "immediate upload if active",
                    Owner.PHASE_7, Milestone.V0_1),
            new Row("texture bind",
                    "atlasSize; sampler unit integers do NOT change",
                    "actual authenticated current base texture bind/restoration, never "
                            + "stitch-only availability (D-P6-20 adapter)",
                    Owner.PHASE_13, Milestone.V0_5),
            new Row("normal/specular texture change",
                    "no built-in value; future custom/texture bridge invalidation",
                    "companion-atlas bind/change",
                    Owner.PHASE_13, Milestone.V0_5),
            new Row("render phase change",
                    "no Appendix D renderStage; retained as custom-extension signal only",
                    "every Phase 4/7 stage transition",
                    Owner.PHASE_7, Milestone.V0_1),
            new Row("fallback/current entity",
                    "entityId (updateEntityId)",
                    "scoped entity render push/pop, immediate upload, 0 restoration",
                    Owner.PHASE_9, Milestone.V0_3),
            new Row("current block entity",
                    "blockEntityId (updateBlockEntityId)",
                    "scoped TE render push/pop, immediate upload, 0 restoration",
                    Owner.PHASE_9, Milestone.V0_3),
            new Row("entity color",
                    "entityColor (updateEntityColor)",
                    "P7-owned hurt/flash push/pop, immediate upload, exact nested prior-color "
                            + "restoration, neutral outside scope; independent of alias IDs",
                    Owner.PHASE_7, Milestone.V0_1),
            new Row("instance",
                    "instanceId (updateInstanceId)",
                    "immediately upload IDs 0...N-1 before prepared copies; restore saved "
                            + "predecessor, outer zero",
                    Owner.PHASE_7, Milestone.V0_5),
            new Row("held items",
                    "heldItemId, heldBlockLightValue, heldItemId2, heldBlockLightValue2 "
                            + "(updateHeldItems)",
                    "tick/inventory change after Phase 9 alias resolution",
                    Owner.PHASE_9, Milestone.V0_3),
            new Row("atlas size",
                    "atlasSize (updateAtlasSize)",
                    "authenticated bind -> Phase 13 Known dimensions or Unknown/non-atlas "
                            + "(0,0); immediate active upload and reload reset",
                    Owner.PHASE_13, Milestone.V0_5));

    private ProducerAudit() {
    }

    /** The fifteen audit rows, in §4.12 table order. */
    public static List<Row> rows() {
        return ROWS;
    }
}
