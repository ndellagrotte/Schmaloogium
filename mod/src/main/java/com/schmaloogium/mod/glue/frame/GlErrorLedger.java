// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.ReplayAwareGLError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Every GL error the glue drains outside the P6 replay protocol, retained for the run
 * manifest's {@code gl_errors} block (R4A/R17): the port's per-draw drains are recorded with
 * {@code attributed = false} because no replay isolates them to one facade call — attribution
 * is never guessed from {@code op}. Bounded; the count keeps growing past the capacity.
 */
public final class GlErrorLedger {

    private static final int CAPACITY = 1024;
    private static final List<ReplayAwareGLError> LEDGER = new ArrayList<>();
    private static long recorded;

    private GlErrorLedger() {
    }

    public static synchronized void record(List<GLError> errors, boolean attributed) {
        for (GLError error : errors) {
            recorded++;
            if (LEDGER.size() < CAPACITY) {
                LEDGER.add(new ReplayAwareGLError(error, attributed));
            }
        }
    }

    public static synchronized List<ReplayAwareGLError> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(LEDGER));
    }

    public static synchronized long recordedCount() {
        return recorded;
    }

    public static synchronized void reset() {
        LEDGER.clear();
        recorded = 0;
    }
}
