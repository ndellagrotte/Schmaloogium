// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.golden;

import com.schmaloogium.conformance.wire.CanonicalText;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A headless golden document (PHASE_2_DOC §4.11.1–§4.11.2): a sorted, deterministic,
 * source-text-free record of what the front end decided about a pack under one
 * {@code GLCapabilityProfile}. [D-P2-5] — shapes, decisions, names and hashes only;
 * never pack source text. [D-P2-4] — every byte is a total function of the model.
 *
 * <p>The on-disk form is the §4.11.2 sketch made exact: a header comment line, sorted
 * {@code key = value} header fields, then canonical-ordered {@code [section]} blocks of
 * sorted {@code key = value} rows, LF endings, final newline. {@link #parse} accepts
 * exactly that form and nothing else.
 */
public final class GoldenDocument {

    /** Fixed section vocabulary and canonical emission order (§4.11.2). */
    public static final List<String> SECTION_ORDER = List.of(
        "sources", "programs", "sizing", "options", "properties", "macros", "diagnostics");

    static final String HEADER_LINE = "# schmaloogium golden · kind=frontend · schema=1";

    private final SortedMap<String, String> header;
    private final SortedMap<String, SortedMap<String, String>> sections;

    private GoldenDocument(SortedMap<String, String> header,
            SortedMap<String, SortedMap<String, String>> sections) {
        this.header = header;
        this.sections = sections;
    }

    public static GoldenDocument create(Map<String, String> headerFields) {
        SortedMap<String, String> header = new TreeMap<>();
        headerFields.forEach((k, v) -> {
            CanonicalText.validateKey(k);
            header.put(k, java.util.Objects.requireNonNull(v, k));
        });
        return new GoldenDocument(header, new TreeMap<>());
    }

    /** Sets one section wholesale; keys are sorted on render. */
    public void section(String name, Map<String, String> rows) {
        if (!SECTION_ORDER.contains(name)) {
            throw new IllegalArgumentException("unknown golden section: " + name);
        }
        SortedMap<String, String> sorted = new TreeMap<>();
        rows.forEach((k, v) -> {
            validateRowKey(k);
            sorted.put(k, java.util.Objects.requireNonNull(v, k));
        });
        sections.put(name, sorted);
    }

    /** Section row keys extend the wire grammar with path-like segments (source rows
     *  are keyed by their shader-relative path, §4.11.2's sketch — file names admit
     *  {@code -} and {@code .}, e.g. {@code shaders/world-1/gbuffers_terrain.fsh}) and
     *  with the owner trees' {@code $type} variant tags, which §4.11.2 requires be
     *  retained verbatim in every projected tree. */
    private static final java.util.regex.Pattern ROW_KEY =
        java.util.regex.Pattern.compile("[A-Za-z0-9_$][-A-Za-z0-9._$]*"
            + "(\\.[A-Za-z0-9_$][-A-Za-z0-9._$]*)*");

    private static void validateRowKey(String key) {
        if (key == null || !ROW_KEY.matcher(key.replace('/', '.')).matches()) {
            throw new IllegalArgumentException("illegal row key: " + key);
        }
    }

    public SortedMap<String, String> header() {
        return java.util.Collections.unmodifiableSortedMap(header);
    }

    public SortedMap<String, SortedMap<String, String>> sections() {
        return java.util.Collections.unmodifiableSortedMap(sections);
    }

    // ------------------------------------------------------------------
    // Rendering ([D-P2-4])
    // ------------------------------------------------------------------

    public void write(Writer out) throws IOException {
        out.write(HEADER_LINE);
        out.write('\n');
        for (Map.Entry<String, String> e : header.entrySet()) {
            out.write(e.getKey());
            out.write(" = ");
            out.write(e.getValue());
            out.write('\n');
        }
        for (String name : SECTION_ORDER) {
            SortedMap<String, String> rows = sections.get(name);
            if (rows == null || rows.isEmpty()) {
                continue;
            }
            out.write('\n');
            out.write('[');
            out.write(name);
            out.write(']');
            out.write('\n');
            for (Map.Entry<String, String> e : rows.entrySet()) {
                out.write(e.getKey());
                out.write(" = ");
                out.write(e.getValue());
                out.write('\n');
            }
        }
    }

    public String render() {
        StringWriter out = new StringWriter();
        try {
            write(out);
        } catch (IOException impossibleOnStringWriter) {
            throw new UncheckedIOException(impossibleOnStringWriter);
        }
        return out.toString();
    }

    // ------------------------------------------------------------------
    // Parsing (strict canonical form)
    // ------------------------------------------------------------------

    public static GoldenDocument parse(Reader in) throws IOException {
        LineNumberReader lines = new LineNumberReader(in);
        String first = lines.readLine();
        if (!HEADER_LINE.equals(first)) {
            throw new IllegalArgumentException("not a schmaloogium golden document");
        }
        SortedMap<String, String> header = new TreeMap<>();
        SortedMap<String, SortedMap<String, String>> sections = new TreeMap<>();
        String current = null;
        String line;
        while ((line = lines.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("[")) {
                if (!line.endsWith("]")) {
                    throw bad(lines, "malformed section header");
                }
                current = line.substring(1, line.length() - 1);
                if (!SECTION_ORDER.contains(current)) {
                    throw bad(lines, "unknown golden section: " + current);
                }
                sections.putIfAbsent(current, new TreeMap<>());
                continue;
            }
            int split = line.indexOf(" = ");
            if (split <= 0) {
                throw bad(lines, "expected 'key = value'");
            }
            String key = line.substring(0, split);
            String value = line.substring(split + 3);
            if (current == null) {
                CanonicalText.validateKey(key);
                if (header.putIfAbsent(key, value) != null) {
                    throw bad(lines, "duplicate header key: " + key);
                }
            } else {
                validateRowKey(key);
                if (sections.get(current).putIfAbsent(key, value) != null) {
                    throw bad(lines, "duplicate key in [" + current + "]: " + key);
                }
            }
        }
        return new GoldenDocument(header, sections);
    }

    public static GoldenDocument parse(String text) {
        try {
            return parse(new StringReader(text));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static IllegalArgumentException bad(LineNumberReader lines, String why) {
        return new IllegalArgumentException("golden line " + lines.getLineNumber() + ": " + why);
    }
}
