// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.record;

import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.SamplerInitializationResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Canned answers for query-shaped calls, so tests can drive failure paths with no GL
 * context (PHASE_1_DOC §4.7.5). All methods are fluent and return {@code this}; every
 * script is keyed by the debug label of the subject object, and canned driver errors are
 * consumed by the next {@code GLDevice.drainErrors()} (§4.7.5, [D-P1-30]) — what makes
 * §G2.4 rung 2 ("disable that uniform only") testable headlessly.
 */
public final class ScriptedResponses {

    private final Map<String, String> compileFailures = new HashMap<>();
    private final Map<String, String> linkFailures = new HashMap<>();
    private final Map<String, String> validateFailures = new HashMap<>();
    private final Map<String, SamplerInitializationResult.Failed> samplerInitFailures = new HashMap<>();
    private final Map<String, LinkedGeometryInputPrimitive> linkedGeometry = new HashMap<>();
    private final Set<String> absentUniforms = new HashSet<>();
    private final Map<String, FramebufferStatus> framebufferStatuses = new HashMap<>();
    private final Map<String, Deque<ScriptedError>> glErrors = new HashMap<>();
    private final Map<String, Map<Long, Float>> depthPixels = new HashMap<>();

    private record ScriptedError(String op, String subjectLabel, GLErrorKind kind) {
    }

    public ScriptedResponses() {
    }

    /** {@code link} on a program with this label fails with the given driver log. */
    public ScriptedResponses linkFails(String programLabel, String driverLog) {
        linkFailures.put(programLabel, driverLog);
        return this;
    }

    /** {@code compile} on a shader with this label fails with the given driver log. */
    public ScriptedResponses compileFails(String shaderLabel, String driverLog) {
        compileFailures.put(shaderLabel, driverLog);
        return this;
    }

    /** {@code validate} on a program with this label fails with the given driver log. */
    public ScriptedResponses validateFails(String programLabel, String driverLog) {
        validateFailures.put(programLabel, driverLog);
        return this;
    }

    /** Sampler initialization on this program fails as scripted. */
    public ScriptedResponses samplerInitializationFails(
            String programLabel, String detail, boolean selectionRestored) {
        samplerInitFailures.put(programLabel,
                new SamplerInitializationResult.Failed(detail, selectionRestored));
        return this;
    }

    /**
     * The committed effective linked-geometry input for this program (§4.7.4a recorder
     * parity): the recorder commits it on successful link; core geometry without this
     * script fails metadata acquisition rather than guessing.
     */
    public ScriptedResponses linkedGeometryInput(
            String programLabel, LinkedGeometryInputPrimitive input) {
        linkedGeometry.put(programLabel, input);
        return this;
    }

    /** {@code locate} on this uniform name reports an absent location. */
    public ScriptedResponses uniformAbsent(String uniformName) {
        absentUniforms.add(uniformName);
        return this;
    }

    /** {@code framebuffers.check} on this framebuffer answers with this status. */
    public ScriptedResponses framebufferStatus(String fboLabel, FramebufferStatus status) {
        framebufferStatuses.put(fboLabel, status);
        return this;
    }

    /**
     * Canned driver error, returned by the next {@code drainErrors()}. When an executed
     * facade call carries this op, the error drives that call's scripted failure path
     * (setup failure, dispatch failure) and lands in the same drain window — what makes
     * §G2.4 rung 2 testable with no GL context.
     */
    public ScriptedResponses glError(String op, String subjectLabel, GLErrorKind kind) {
        glErrors.computeIfAbsent(op, key -> new ArrayDeque<>())
                .add(new ScriptedError(op, subjectLabel, kind));
        return this;
    }

    /** Canned answer for {@code FramebufferService.readDepthPixel} (Phase 6's verb). */
    public ScriptedResponses depthPixel(String fboLabel, int x, int y, float depth) {
        depthPixels.computeIfAbsent(fboLabel, key -> new HashMap<>())
                .put(pack(x, y), depth);
        return this;
    }

    // ---------------------------------------------------------------- package access

    String compileFailureFor(String shaderLabel) {
        return compileFailures.get(shaderLabel);
    }

    String linkFailureFor(String programLabel) {
        return linkFailures.get(programLabel);
    }

    String validateFailureFor(String programLabel) {
        return validateFailures.get(programLabel);
    }

    SamplerInitializationResult.Failed samplerInitFailureFor(String programLabel) {
        return samplerInitFailures.get(programLabel);
    }

    LinkedGeometryInputPrimitive linkedGeometryFor(String programLabel) {
        return linkedGeometry.get(programLabel);
    }

    boolean isUniformAbsent(String uniformName) {
        return absentUniforms.contains(uniformName);
    }

    FramebufferStatus statusFor(String fboLabel) {
        return framebufferStatuses.get(fboLabel);
    }

    /** Consumes the next canned error for this op, if any (op-keyed FIFO). */
    GLError pollGlError(String op) {
        Deque<ScriptedError> queue = glErrors.get(op);
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        ScriptedError scripted = queue.poll();
        return new GLError(scripted.op(), scripted.subjectLabel(), scripted.kind(), "scripted");
    }

    /** Leftover canned errors surface at the next drain (§4.7.5). */
    void pollAllLeftoverGlErrors(List<GLError> into) {
        for (Deque<ScriptedError> queue : glErrors.values()) {
            while (!queue.isEmpty()) {
                ScriptedError scripted = queue.poll();
                into.add(new GLError(scripted.op(), scripted.subjectLabel(), scripted.kind(), "scripted"));
            }
        }
    }

    Float depthPixelFor(String fboLabel, int x, int y) {
        Map<Long, Float> byCoordinate = depthPixels.get(fboLabel);
        return byCoordinate == null ? null : byCoordinate.get(pack(x, y));
    }

    private static long pack(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    /** True when no script at all has been installed (used by recorder defaults). */
    public boolean isEmpty() {
        return compileFailures.isEmpty() && linkFailures.isEmpty() && validateFailures.isEmpty()
                && samplerInitFailures.isEmpty() && linkedGeometry.isEmpty()
                && absentUniforms.isEmpty() && framebufferStatuses.isEmpty()
                && glErrors.isEmpty() && depthPixels.isEmpty();
    }
}
