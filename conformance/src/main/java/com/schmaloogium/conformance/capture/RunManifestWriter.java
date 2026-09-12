// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.capture.RunManifest.Value;
import com.schmaloogium.conformance.wire.CanonicalText;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

/**
 * Renders the model back to the canonical wire form (§4.5.2, §4.5.4): schema first
 * line, then strictly ascending {@code key = value} lines, JSON strings encoded by
 * {@link CanonicalText#encodeJson}, LF endings, final newline. Round-trips through
 * {@link RunManifestReader} byte-identically ([D-P2-4]).
 */
public final class RunManifestWriter {

    private RunManifestWriter() {
    }

    public static void write(RunManifest manifest, Writer out) throws IOException {
        out.write(RunManifest.SCHEMA_LINE);
        out.write('\n');
        for (Map.Entry<String, Value> entry : manifest.entries().entrySet()) {
            out.write(entry.getKey());
            out.write(" = ");
            out.write(render(entry.getValue()));
            out.write('\n');
        }
    }

    public static String render(RunManifest manifest) {
        StringWriter out = new StringWriter();
        try {
            write(manifest, out);
        } catch (IOException impossibleOnStringWriter) {
            throw new UncheckedIOException(impossibleOnStringWriter);
        }
        return out.toString();
    }

    public static String render(Value value) {
        if (value instanceof Value.Text text) {
            return CanonicalText.encodeJson(text.value());
        }
        if (value instanceof Value.Token token) {
            return token.value();
        }
        if (value instanceof Value.Bool bool) {
            return CanonicalText.formatBoolean(bool.value());
        }
        if (value instanceof Value.Int integer) {
            return CanonicalText.formatInt(integer.value());
        }
        if (value instanceof Value.Dec decimal) {
            return CanonicalText.formatDouble(decimal.value());
        }
        throw new IllegalStateException("unrenderable value: " + value.getClass());
    }
}
