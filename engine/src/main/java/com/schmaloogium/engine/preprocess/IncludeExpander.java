// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Include expansion over one indexed snapshot: depth-first textual expansion with
 * numeric {@code #line} attribution before and after each included file, bounded by
 * ten active include edges, with the complete edge chain on failure.
 */
public final class IncludeExpander {

    public static final int MAX_INCLUDE_DEPTH = 10;

    /** One expanded logical line with its original provenance. */
    public record ExpandedLine(String text, SourceId source, int line) {
    }

    public sealed interface Result {
        record Expanded(List<ExpandedLine> lines) implements Result {
        }

        record Failed(List<EngineDiagnosticHolder> diagnostics) implements Result {
        }
    }

    /** Diagnostic holder avoids a preprocess dependency on ordered diag construction. */
    public record EngineDiagnosticHolder(String key, String detail) {
    }

    private final SourceIndex index;

    public IncludeExpander(SourceIndex index) {
        this.index = index;
    }

    public Result expand(SourceKey root) {
        Optional<SourceDocument> doc = index.rootDocument(root);
        if (doc.isEmpty()) {
            return new Result.Failed(List.of(new EngineDiagnosticHolder(
                "schmaloogium.error.source.root_missing", root.programName())));
        }
        List<ExpandedLine> out = new ArrayList<>();
        Set<SourceId> active = new HashSet<>();
        if (!visit(doc.get(), out, active, 0)) {
            return new Result.Failed(List.of(new EngineDiagnosticHolder(
                "schmaloogium.error.source.include_depth", root.programName())));
        }
        return new Result.Expanded(List.copyOf(out));
    }

    private boolean visit(SourceDocument doc, List<ExpandedLine> out, Set<SourceId> active,
            int depth) {
        if (depth > MAX_INCLUDE_DEPTH) {
            return false;
        }
        if (!active.add(doc.id())) {
            return true; // already expanded on this path; preprocessing dedupes guards
        }
        List<String> lines = doc.originalLogicalLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Optional<String> target = includeTarget(line);
            if (target.isEmpty()) {
                out.add(new ExpandedLine(line, doc.id(), i + 1));
                continue;
            }
            String resolved = resolve(doc, target.get());
            if (resolved == null) {
                out.add(new ExpandedLine(line, doc.id(), i + 1));
                continue;
            }
            SourceDocument included = null;
            for (SourceDocument candidate : index.sources()) {
                if (candidate.id().path().canonicalString().equals(resolved)) {
                    included = candidate;
                    break;
                }
            }
            if (included == null) {
                out.add(new ExpandedLine(line, doc.id(), i + 1));
                continue;
            }
            // select the included file's first line before the included text
            out.add(new ExpandedLine("#line " + lineDirective(1, included.id()),
                doc.id(), i + 1));
            if (!visit(included, out, active, depth + 1)) {
                return false;
            }
            // restore the parent's next line after the included text
            out.add(new ExpandedLine("#line " + lineDirective(i + 2, doc.id()),
                doc.id(), i + 1));
        }
        active.remove(doc.id());
        return true;
    }

    /** Desired next line L is encoded as n = L - 1 for GLSL 1.20/1.50 line convention. */
    private String lineDirective(int desiredLine, SourceId file) {
        Integer number = index.fileNumbers().get(file);
        return Math.max(desiredLine - 1, 0) + " " + number;
    }

    private static Optional<String> includeTarget(String line) {
        String trimmed = line.stripLeading();
        if (trimmed.startsWith("#include")) {
            String rest = trimmed.substring("#include".length()).strip();
            if (rest.length() >= 2 && ((rest.startsWith("\"") && rest.endsWith("\""))
                    || (rest.startsWith("<") && rest.endsWith(">")))) {
                return Optional.of(rest.substring(1, rest.length() - 1));
            }
            if (!rest.isEmpty()) {
                return Optional.of(rest);
            }
        }
        return Optional.empty();
    }

    private String resolve(SourceDocument doc, String requested) {
        String dir = doc.id().path().canonicalString();
        int slash = dir.lastIndexOf('/');
        String rootPrefix = SourceIndex.rootPrefixOf(dir);
        String parentFull = slash >= 0 ? dir.substring(0, slash) : "";
        String parentWithinRoot = parentFull.length() >= rootPrefix.length()
            ? parentFull.substring(rootPrefix.length()) : "";
        String withinRoot = requested.startsWith("/") ? requested.substring(1)
            : (parentWithinRoot.isEmpty() ? requested : parentWithinRoot + "/" + requested);
        return normalize(rootPrefix + withinRoot);
    }

    private static String normalize(String raw) {
        if (raw.isEmpty() || raw.indexOf('\\') >= 0 || raw.indexOf('\u0000') >= 0) {
            return null;
        }
        String[] segments = raw.split("/", -1);
        List<String> out = new ArrayList<>();
        for (String segment : segments) {
            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }
            if (segment.equals("..")) {
                return null;
            }
            out.add(segment);
        }
        return String.join("/", out);
    }
}
