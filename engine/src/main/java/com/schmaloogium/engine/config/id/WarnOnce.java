// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fingerprint-scoped warn-once gate (PHASE_9_DOC §4.14). Keys are stable tuples of the
 * identity scope, mapping kind, origin, line, selector and reason. Diagnostics are
 * buffered and flushed to the reporter in deterministic source order at the end of the
 * build; a builder clears its emitted set only when the input fingerprints change.
 */
final class WarnOnce {

    private final Deque<EngineDiagnostic> buffer = new ArrayDeque<>();
    private final Set<String> emitted;
    private final String scopeFingerprint;

    WarnOnce(Set<String> emitted, String scopeFingerprint) {
        this.emitted = emitted;
        this.scopeFingerprint = scopeFingerprint;
    }

    String scopeFingerprint() {
        return scopeFingerprint;
    }

    boolean first(String onceKey) {
        return emitted.add(onceKey);
    }

    /** Buffers one WARN diagnostic unless its once-key was already emitted. */
    void warn(String messageKey, List<Object> args, String detail, String logChannel,
            String onceKey) {
        if (!first(onceKey)) {
            return;
        }
        buffer.add(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY,
                messageKey, args, detail, logChannel));
    }

    /** Buffers one diagnostic unconditionally (errors, lifecycle notices). */
    void unconditional(EngineDiagnostic diagnostic) {
        buffer.add(diagnostic);
    }

    /** All buffered diagnostics in deterministic order; empties the buffer. */
    List<EngineDiagnostic> drain() {
        List<EngineDiagnostic> out = new java.util.ArrayList<>(buffer);
        buffer.clear();
        return out;
    }

    /** Flushes buffered diagnostics to the reporter in order. */
    void flushTo(DiagnosticReporter reporter) {
        for (EngineDiagnostic diagnostic : drain()) {
            reporter.report(diagnostic);
        }
    }
}
