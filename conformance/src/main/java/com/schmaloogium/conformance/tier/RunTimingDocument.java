// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.tier;

import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.capture.RunManifestWriter;
import com.schmaloogium.conformance.wire.FlatDocument;

import java.util.Map;

/**
 * {@code schmaloogium.run-timing/1} (§4.5.4): a canonical flat document holding exactly
 * {@code run.id}, {@code run.planHash}, the three {@code clock.*} keys and every
 * {@code timing.*} key copied from a manifest with the same sorted-key/scalar rules. It has
 * no self-hash; its complete text is the {@code candidateTiming}/{@code referenceTiming}
 * payload inside the hashed comparability evidence, regenerated from the authenticated
 * manifest by publisher and reader alike.
 */
public final class RunTimingDocument {

    public static final String SCHEMA_LINE = "schema = schmaloogium.run-timing/1";

    private RunTimingDocument() {
    }

    public static String render(RunManifest manifest) {
        FlatDocument.Builder b = FlatDocument.builder(SCHEMA_LINE);
        for (Map.Entry<String, RunManifest.Value> e : manifest.entries().entrySet()) {
            String key = e.getKey();
            if (key.equals("run.id") || key.equals("run.planHash") || key.startsWith("clock.")
                    || key.startsWith("timing.")) {
                b.raw(key, RunManifestWriter.render(e.getValue()));
            }
        }
        return b.build().render();
    }
}
