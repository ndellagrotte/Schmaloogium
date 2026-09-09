// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.config.ProgramKey;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * One immutable source index over a pack snapshot: documents, include graph with
 * cycle/depth analysis, stage roots, and the executable program universe.
 */
public final class SourceIndex {

    private final List<SourceDocument> sources;
    private final Map<SourceId, Integer> fileNumbers;
    private final List<SourceKey> roots;
    private final Set<ProgramKey> executablePrograms;
    private final List<IncludeEdge> includeEdges;
    private final List<EngineDiagnostic> diagnostics;
    private final Map<SourceKey, SourceDocument> rootDocuments;

    private SourceIndex(List<SourceDocument> sources, Map<SourceId, Integer> fileNumbers,
            List<SourceKey> roots, Set<ProgramKey> executablePrograms,
            List<IncludeEdge> includeEdges, List<EngineDiagnostic> diagnostics,
            Map<SourceKey, SourceDocument> rootDocuments) {
        this.sources = List.copyOf(sources);
        this.fileNumbers = Map.copyOf(fileNumbers);
        this.roots = List.copyOf(roots);
        this.executablePrograms = Set.copyOf(executablePrograms);
        this.includeEdges = List.copyOf(includeEdges);
        this.diagnostics = List.copyOf(diagnostics);
        this.rootDocuments = Map.copyOf(rootDocuments);
    }

    public static SourceIndex build(Map<NormalizedPackPath, byte[]> files,
            List<EngineDiagnostic> diagnostics) {
        // 1. decode every file (UTF-8 with optional BOM); collect documents in path order
        List<SourceDocument> docs = new ArrayList<>();
        Map<SourceId, Integer> fileNumbers = new LinkedHashMap<>();
        Map<NormalizedPackPath, SourceDocument> byPath = new TreeMap<>(NormalizedPackPath.ORDER);
        for (Map.Entry<com.schmaloogium.engine.pack.NormalizedPackPath, byte[]> e : files.entrySet()) {
            NormalizedPackPath path = e.getKey();
            byte[] bytes = e.getValue();
            String text = decodeUtf8(bytes);
            List<String> lines = List.of(text.split("\n", -1));
            SourceDocument doc = new SourceDocument(new SourceId(path), lines);
            docs.add(doc);
            fileNumbers.put(doc.id(), fileNumbers.size() + 1);
            byPath.put(path, doc);
        }
        List<EngineDiagnostic> diags = new ArrayList<>(diagnostics);

        // 2. scan includes (line-oriented, comment-unaware) and build edges
        List<IncludeEdge> edges = new ArrayList<>();
        Map<SourceId, Set<SourceId>> adjacency = new LinkedHashMap<>();
        for (SourceDocument doc : docs) {
            adjacency.computeIfAbsent(doc.id(), k -> new LinkedHashSet<>());
            String dir = doc.id().path().canonicalString();
            int slash = dir.lastIndexOf('/');
            String rootPrefix = rootPrefixOf(dir);
            String parentFull = slash >= 0 ? dir.substring(0, slash) : "";
            String parentWithinRoot = parentFull.length() >= rootPrefix.length()
                ? parentFull.substring(rootPrefix.length()) : "";
            List<String> lines = doc.originalLogicalLines();
            for (int i = 0; i < lines.size(); i++) {
                Optional<String> target = includeTarget(lines.get(i));
                if (target.isEmpty()) {
                    continue;
                }
                String requestedRaw = target.get();
                String withinRoot = requestedRaw.startsWith("/")
                    ? requestedRaw.substring(1)
                    : (parentWithinRoot.isEmpty()
                        ? requestedRaw : parentWithinRoot + "/" + requestedRaw);
                String resolved = normalize(rootPrefix + withinRoot);
                if (resolved == null) {
                    diags.add(diagWarn("schmaloogium.warn.include.unsafe", requestedRaw));
                    edges.add(new IncludeEdge(doc.id(), new NormalizedPackPath(sanitizeIncluded(requestedRaw)),
                        Optional.empty(), i + 1));
                    continue;
                }
                SourceDocument included = byPath.get(new NormalizedPackPath(resolved));
                if (included == null) {
                    diags.add(diagWarn("schmaloogium.warn.include.missing", resolved));
                }
                edges.add(new IncludeEdge(doc.id(), new NormalizedPackPath(resolved),
                    included == null ? Optional.empty() : Optional.of(included.id()), i + 1));
                if (included != null) {
                    adjacency.computeIfAbsent(doc.id(), k -> new LinkedHashSet<>()).add(included.id());
                    adjacency.computeIfAbsent(included.id(), k -> new LinkedHashSet<>()).add(doc.id());
                }
            }
        }

        // 3. roots: base <program>.vsh/.fsh/.gsh and world<id> <program>.vsh/.fsh
        List<SourceKey> roots = new ArrayList<>();
        Map<SourceKey, SourceDocument> rootDocuments = new LinkedHashMap<>();
        Set<ProgramKey> executables = new LinkedHashSet<>();
        for (SourceDocument doc : docs) {
            String path = doc.id().path().canonicalString();
            DimensionKey dimension = dimensionOf(path);
            if (dimension == null) {
                continue;
            }
            StageName stage = rootStage(path);
            if (stage == null) {
                continue;
            }
            SourceKey key = new SourceKey(dimension, stage.programName, stage.stage, doc.id());
            roots.add(key);
            rootDocuments.put(key, doc);
            executables.add(new ProgramKey(dimension, stage.programName));
        }
        roots.sort(Comparator
            .comparing((SourceKey k) -> k.dimension(), Comparator.naturalOrder())
            .thenComparing(k -> k.source().path().canonicalString())
            .thenComparing(SourceKey::programName)
            .thenComparing(k -> k.stage().name()));

        // 4. cycle detection over directed include edges (DFS with color marks)
        detectCycles(docs, edges, diags);
        return new SourceIndex(docs, fileNumbers, roots, executables, edges, diags, rootDocuments);
    }

    private static final int BASE = 0;

    private static DimensionKey dimensionOf(String path) {
        if (path.startsWith("shaders/world")) {
            String rest = path.substring("shaders/world".length());
            int slash = rest.indexOf('/');
            if (slash > 0) {
                try {
                    int id = Integer.parseInt(rest.substring(0, slash));
                    if (id >= -128 && id <= 128) {
                        return DimensionKey.world(id);
                    }
                } catch (NumberFormatException ignored) {
                    // fall through: ordinary path
                }
            }
        }
        if (path.startsWith("shaders/")) {
            return DimensionKey.BASE;
        }
        return null;
    }

    /**
     * The dimension root prefix a pack path lives under: {@code shaders/world<id>/}
     * for a valid legacy dimension subtree, else {@code shaders/}. Includes resolve
     * within their own root's namespace.
     */
    static String rootPrefixOf(String path) {
        if (path.startsWith("shaders/world")) {
            String rest = path.substring("shaders/world".length());
            int slash = rest.indexOf('/');
            if (slash > 0) {
                try {
                    int id = Integer.parseInt(rest.substring(0, slash));
                    if (id >= -128 && id <= 128) {
                        return "shaders/world" + rest.substring(0, slash) + "/";
                    }
                } catch (NumberFormatException ignored) {
                    // fall through: base root
                }
            }
        }
        return "shaders/";
    }

    record StageName(String programName, ShaderSourceStage stage) {
    }

    private static StageName rootStage(String path) {
        int slash = path.lastIndexOf('/');
        String name = slash >= 0 ? path.substring(slash + 1) : path;
        if (name.endsWith(".vsh")) {
            return new StageName(name.substring(0, name.length() - 4), ShaderSourceStage.VERTEX);
        }
        if (name.endsWith(".fsh")) {
            return new StageName(name.substring(0, name.length() - 4), ShaderSourceStage.FRAGMENT);
        }
        if (name.endsWith(".gsh")) {
            return new StageName(name.substring(0, name.length() - 4), ShaderSourceStage.GEOMETRY);
        }
        return null;
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

    /** Reuses the discovery escape guard: rejects absolute, '..', NUL, backslash paths. */
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
                return null; // escapes the pack root
            }
            out.add(segment);
        }
        return String.join("/", out);
    }

    private static String sanitizeIncluded(String raw) {
        return normalize(raw) == null ? raw.replace('\\', '_') : normalize(raw);
    }

    private static void detectCycles(List<SourceDocument> docs, List<IncludeEdge> edges,
            List<EngineDiagnostic> diags) {
        Map<SourceId, List<SourceId>> directed = new LinkedHashMap<>();
        for (IncludeEdge e : edges) {
            if (e.included().isPresent()) {
                directed.computeIfAbsent(e.including(), k -> new ArrayList<>()).add(e.included().get());
            }
        }
        Map<SourceId, Integer> state = new LinkedHashMap<>();
        List<SourceId> stack = new ArrayList<>();
        for (SourceDocument doc : docs) {
            visit(doc.id(), directed, state, stack, diags);
        }
    }

    private static final int VISITING = 1;
    private static final int DONE = 2;

    private static void visit(SourceId id, Map<SourceId, List<SourceId>> directed,
            Map<SourceId, Integer> state, List<SourceId> stack, List<EngineDiagnostic> diags) {
        int mark = state.getOrDefault(id, 0);
        if (mark == DONE) {
            return;
        }
        if (mark == VISITING) {
            diags.add(diagWarn("schmaloogium.warn.include.cycle", id.path().canonicalString()));
            return;
        }
        state.put(id, VISITING);
        stack.add(id);
        for (SourceId next : directed.getOrDefault(id, List.of())) {
            visit(next, directed, state, stack, diags);
        }
        stack.remove(stack.size() - 1);
        state.put(id, DONE);
    }

    private static String decodeUtf8(byte[] bytes) {
        int start = bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF
            && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF ? 3 : 0;
        return new String(bytes, start, bytes.length - start, StandardCharsets.UTF_8);
    }

    private static EngineDiagnostic diagWarn(String key, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY, key,
            List.of(), detail, "schmaloogium.preprocess");
    }

    public List<SourceDocument> sources() {
        return sources;
    }

    public Map<SourceId, Integer> fileNumbers() {
        return fileNumbers;
    }

    public List<SourceKey> roots() {
        return roots;
    }

    public Set<ProgramKey> executablePrograms() {
        return executablePrograms;
    }

    public List<IncludeEdge> includeEdges() {
        return includeEdges;
    }

    public List<EngineDiagnostic> diagnostics() {
        return diagnostics;
    }

    public Optional<SourceDocument> document(SourceId id) {
        for (SourceDocument doc : sources) {
            if (doc.id().equals(id)) {
                return Optional.of(doc);
            }
        }
        return Optional.empty();
    }

    public Optional<SourceDocument> rootDocument(SourceKey key) {
        return Optional.ofNullable(rootDocuments.get(key));
    }

    /** Undirected adjacency for option weak-component analysis. */
    public Map<SourceId, Set<SourceId>> undirectedAdjacency() {
        Map<SourceId, Set<SourceId>> adjacency = new LinkedHashMap<>();
        for (IncludeEdge edge : includeEdges) {
            if (edge.included().isPresent()) {
                adjacency.computeIfAbsent(edge.including(), k -> new LinkedHashSet<>())
                    .add(edge.included().get());
                adjacency.computeIfAbsent(edge.included().get(), k -> new LinkedHashSet<>())
                    .add(edge.including());
            }
        }
        return adjacency;
    }
}
