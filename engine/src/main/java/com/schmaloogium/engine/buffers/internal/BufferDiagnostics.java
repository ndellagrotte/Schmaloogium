// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;

import java.util.List;

/**
 * Diagnostic emission for the buffers estate (PHASE_5_DOC §4.13/§7): message keys and
 * sanitized args cross the seam, driver detail stays in {@code detail} (log/GUI only, never
 * chat), and every line lands on the {@code schmaloogium.buffers} or
 * {@code schmaloogium.shadow} channel.
 */
public final class BufferDiagnostics {

    private BufferDiagnostics() {
    }

    public static EngineDiagnostic rgbaFallbackWarning(String requestedFormats) {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.CHAT,
            "schmaloogium.buffers.warning.rgba-fallback",
            List.of(requestedFormats), "whole-estate RGBA_COMPAT fallback",
            LogChannels.BUFFERS);
    }

    public static EngineDiagnostic fallbackFailed(String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.CHAT,
            "schmaloogium.buffers.error.fallback-failed",
            List.of(), detail, LogChannels.BUFFERS);
    }

    public static EngineDiagnostic candidateReady(int colorCount, int depthCount,
            long mainExtentWidth, long mainExtentHeight) {
        return new EngineDiagnostic(DiagnosticSeverity.INFO, UserChannel.LOG_ONLY,
            "schmaloogium.buffers.info.candidate-ready",
            List.of(colorCount, depthCount, mainExtentWidth, mainExtentHeight),
            "candidate created", LogChannels.BUFFERS);
    }

    public static EngineDiagnostic backendFailure(String messageKey, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY,
            messageKey, List.of(), detail, LogChannels.BUFFERS);
    }

    public static EngineDiagnostic shadowBackendFailure(String messageKey, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY,
            messageKey, List.of(), detail, LogChannels.SHADOW);
    }

    public static EngineDiagnostic depthCopyDegraded(String point, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY,
            "schmaloogium.buffers.warning.depth-copy-degraded",
            List.of(point), detail, LogChannels.BUFFERS);
    }

    public static EngineDiagnostic duplicateIgnored(String what) {
        return new EngineDiagnostic(DiagnosticSeverity.INFO, UserChannel.LOG_ONLY,
            "schmaloogium.buffers.info.duplicate-ignored",
            List.of(what), "duplicate operation ignored", LogChannels.BUFFERS);
    }
}
