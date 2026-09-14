// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.VertexBindMode;
import com.schmaloogium.engine.gl.VertexBindResult;
import com.schmaloogium.engine.gl.VertexBinding;
import com.schmaloogium.engine.gl.VertexSource;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.registry.ExtendedAttribute;
import com.schmaloogium.engine.vertex.Classic56Layout;
import com.schmaloogium.engine.vertex.ClassicInputPlans;
import com.schmaloogium.engine.vertex.VertexGeometryInput;
import com.schmaloogium.engine.vertex.VertexInputPlan;
import com.schmaloogium.engine.vertex.VertexProducer;
import com.schmaloogium.mod.glue.VertexSources;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * The draw-side glue (PHASE_10_DOC §4.6): per chunk layer, the first extended VBO binds
 * the classic layout with the activated program's declared attributes through the P1
 * facade (capturing the predecessor once), every further VBO of the layer re-points
 * ({@code rebind}), and the layer end restores. An extended VBO is always drawn through
 * the facade (vanilla's 28-byte pointers cannot address a 56-byte record); a vanilla VBO
 * under an extended program gets the neutral current values. Display-list capture issues
 * the same pointer set as a live draw (client range); replay restores the neutral values.
 */
public final class ChunkDrawBridge {

    private static final Log LOG = Logs.channel(LogChannels.FRAME);
    private static final boolean PROBE = Boolean.getBoolean("schmaloogium.debug.probeBuffers");
    private static volatile GLDevice device;

    // Per-layer state (render thread only).
    private static VertexBinding layerBinding;
    private static VertexSource layerSource;
    private static boolean neutralApplied;
    private static final java.util.Set<String> vboLogged = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static boolean firstListLogged;
    private static boolean rejectionLogged;
    private static boolean staleLogged;

    /** The draw bridge's own draw call, supplied by the mixin (the redirected original). */
    @FunctionalInterface
    public interface Draw {
        void run();
    }

    private ChunkDrawBridge() {
    }

    public static void install(GLDevice glDevice) {
        device = glDevice;
    }

    /** Re-arms the one-shot evidence lines for a new publication. */
    public static void noteInstalled() {
        vboLogged.clear();
        firstListLogged = false;
        neutralLogged = false;
        rejectionLogged = false;
        staleLogged = false;
    }

    private static VertexInputPlan planFor(Set<ExtendedAttribute> declared, VertexGeometryInput geometry) {
        return ClassicInputPlans.plan(VertexProducer.BLOCK.participation(), declared, geometry);
    }

    private static VertexProgramInputTracker.Declared declared() {
        VertexProgramInputTracker.Declared d = VertexProgramInputTracker.current();
        return d != null ? d : new VertexProgramInputTracker.Declared(Set.of(), VertexGeometryInput.NONE, "fixed");
    }

    /** H10-VBO-LAYER / H10-VBO-DRAW: around one chunk VBO's {@code drawArrays}. */
    public static void drawVbo(VertexBufferSidecar vbo, String layer, Draw original) {
        GLDevice dev = device;
        if (dev == null || !vbo.schmaloogium$extended()) {
            applyNeutralIfDeclared(dev);
            original.run();
            return;
        }
        VertexProgramInputTracker.Declared declared = declared();
        VertexSource source = VertexSources.borrowedVbo(dev,
                (long) vbo.schmaloogium$count() * Classic56Layout.STRIDE_BYTES, vbo.schmaloogium$count());
        VertexBindResult result;
        if (layerBinding == null) {
            result = dev.vertexInputs().bind(source, Classic56Layout.layout(),
                    planFor(declared.attributes(), declared.geometry()), VertexBindMode.LIVE_DRAW);
            if (result instanceof VertexBindResult.Bound bound) {
                layerBinding = bound.binding();
            }
        } else {
            result = dev.vertexInputs().rebind(layerBinding, source);
            if (!(result instanceof VertexBindResult.Bound)) {
                layerBinding = null; // a failed rebind already restored the predecessor
            }
        }
        if (layerSource != null) {
            VertexSources.retire(layerSource);
        }
        layerSource = source;
        if (!(result instanceof VertexBindResult.Bound)) {
            if (!rejectionLogged) {
                rejectionLogged = true;
                LOG.warn("H10-VBO-01 extended draw refused by the facade ({} program {}): the layer is skipped",
                        result, declared.program());
            }
            return; // never draw a 56-byte record with vanilla's 28-byte pointers
        }
        VertexEpochs.Publication current = VertexEpochs.current();
        if ((current == null || current.epoch().serial() != vbo.schmaloogium$serial()) && !staleLogged) {
            staleLogged = true;
            LOG.info("H10-VBO-02 a product of serial {} is drawn under {} (own stride; renderers reload replaces it)",
                    vbo.schmaloogium$serial(), current == null ? "vanilla" : "serial " + current.epoch().serial());
        }
        // One line per distinct declaration set: the shadow program and each gbuffers
        // program declare their own subset, and each must bind exactly that subset.
        if (vboLogged.add(declared.attributes().toString())) {
            LOG.info("H10-VBO-01 first extended draw for {}: layer {} stride {} vertices {} program {}",
                    declared.attributes(), layer, Classic56Layout.STRIDE_BYTES, vbo.schmaloogium$count(),
                    declared.program());
            if (PROBE) {
                VertexGlProbe.probeExtendedDraw(vbo.schmaloogium$count(), declared.attributes());
            }
        }
        original.run();
    }

    /** H10-VBO-LAYER end: one restore per layer. */
    public static void endLayer() {
        GLDevice dev = device;
        VertexBinding binding = layerBinding;
        layerBinding = null;
        neutralApplied = false;
        if (layerSource != null) {
            VertexSources.retire(layerSource);
            layerSource = null;
        }
        if (binding != null && dev != null) {
            try {
                dev.vertexInputs().restore(binding);
            } catch (RuntimeException e) {
                LOG.warn("H10-VBO-LAYER restore failed: {}", e.toString());
            }
        }
    }

    /**
     * Once per accepted frame, before any draw: every classic attribute the installed
     * pipeline declares reads its documented neutral current value with its array
     * disabled (PHASE_10_DOC §4.6). Chunk draws re-enable and re-point their own arrays;
     * everything else — entities, block entities, the hand, particles, the sky — is a
     * non-extended draw that must not read a stale or default tangent.
     */
    public static void applyFrameNeutrals() {
        GLDevice dev = device;
        Set<ExtendedAttribute> declared = VertexEpochs.declaredUnion();
        if (dev == null || declared.isEmpty()) {
            return;
        }
        try {
            dev.vertexInputs().setNeutralCurrentValues(planFor(declared, VertexGeometryInput.NONE));
            if (!neutralLogged) {
                neutralLogged = true;
                LOG.info("H10-NEUTRAL-01 non-chunk draws read the neutral current values for {}", declared);
            }
        } catch (RuntimeException e) {
            LOG.warn("H10 frame neutral current values failed: {}", e.toString());
        }
    }

    private static boolean neutralLogged;

    private static void applyNeutralIfDeclared(GLDevice dev) {
        if (dev == null || neutralApplied) {
            return;
        }
        VertexProgramInputTracker.Declared declared = VertexProgramInputTracker.current();
        if (declared == null || declared.attributes().isEmpty()) {
            return;
        }
        neutralApplied = true;
        try {
            dev.vertexInputs().setNeutralCurrentValues(planFor(declared.attributes(), declared.geometry()));
        } catch (RuntimeException e) {
            LOG.warn("H10 neutral current values failed: {}", e.toString());
        }
    }

    /**
     * H10-CLIENT: around the display-list compile's {@code glDrawArrays} of an extended
     * builder (LIST_CAPTURE with the union of attributes the installed pipeline declares,
     * so the list carries every attribute a later main or shadow draw reads).
     */
    public static void drawClient(ByteBuffer bytes, int vertexCount, Draw original) {
        GLDevice dev = device;
        if (dev == null) {
            original.run();
            return;
        }
        VertexSource source = VertexSources.clientRange(dev, bytes, 0L,
                (long) vertexCount * Classic56Layout.STRIDE_BYTES, vertexCount);
        VertexBindResult result = dev.vertexInputs().bind(source, Classic56Layout.layout(),
                planFor(VertexEpochs.declaredUnion(), VertexGeometryInput.NONE), VertexBindMode.LIST_CAPTURE);
        if (!(result instanceof VertexBindResult.Bound bound)) {
            if (!rejectionLogged) {
                rejectionLogged = true;
                LOG.warn("H10-LIST-01 list capture refused by the facade ({}): compiled without attributes", result);
            }
            VertexSources.retire(source);
            original.run();
            return;
        }
        try {
            if (!firstListLogged) {
                firstListLogged = true;
                LOG.info("H10-LIST-01 first list capture: stride {} vertices {} attributes {}",
                        Classic56Layout.STRIDE_BYTES, vertexCount, VertexEpochs.declaredUnion());
            }
            original.run();
        } finally {
            try {
                dev.vertexInputs().restore(bound.binding());
            } catch (RuntimeException e) {
                LOG.warn("H10-LIST-01 restore failed: {}", e.toString());
            }
            VertexSources.retire(source);
        }
    }

    /** H10-LIST-REPLAY end: the compiled attribute values persist as current values; neutralize. */
    public static void endListReplay() {
        neutralApplied = false;
        applyNeutralIfDeclared(device);
        neutralApplied = false;
    }
}
