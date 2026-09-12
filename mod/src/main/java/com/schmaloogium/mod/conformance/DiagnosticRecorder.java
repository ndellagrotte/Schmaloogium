// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records every engine diagnostic for the manifest's {@code diagnostics} block while
 * forwarding it to the normal client router (chat/GUI/log delivery is unchanged).
 */
final class DiagnosticRecorder implements DiagnosticReporter {

    private final DiagnosticReporter delegate;
    private final List<EngineDiagnostic> recorded = Collections.synchronizedList(new ArrayList<>());

    DiagnosticRecorder(DiagnosticReporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void report(EngineDiagnostic d) {
        recorded.add(d);
        delegate.report(d);
    }

    List<EngineDiagnostic> snapshot() {
        synchronized (recorded) {
            return List.copyOf(recorded);
        }
    }
}
