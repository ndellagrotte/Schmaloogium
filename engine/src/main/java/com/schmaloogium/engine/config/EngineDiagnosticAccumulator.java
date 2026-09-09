// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.ArrayList;
import java.util.List;

/** Collects parse warnings without depending on a reporter instance. */
public final class EngineDiagnosticAccumulator {

    /** Minimal warning record retained by parsers. */
    public record Holder(String key, String detail) {
    }

    private final List<Holder> warnings = new ArrayList<>();

    public void warn(String key, String detail) {
        warnings.add(new Holder(key, detail));
    }

    public List<Holder> warnings() {
        return List.copyOf(warnings);
    }
}
