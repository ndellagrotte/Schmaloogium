// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Deterministic bounded discovery over one real shaderpacks directory:
 * two sentinels, then direct-child folder/archive candidates in
 * case-insensitive natural tie-break order, each validated by a bounded
 * recursive search for at least one shaders directory.
 */
final class DiscoveryIndex {

    record Entry(String displayName, FilesystemCandidateReference reference,
        Path hostPath, boolean directory, boolean valid,
        List<EngineDiagnostic> diagnostics, Optional<DiscoveryRoot> root) {
    }

    /** The selected pack root: normalized shaders-dir prefix inside the candidate. */
    record DiscoveryRoot(String normalizedRoot, boolean directory) {
    }

    /** One retained generation: reference-keyed ids plus entry details. */
    record Snapshot(Map<String, PackCandidateIdToken> byReference, List<Entry> entries) {

        Snapshot {
            byReference = Map.copyOf(byReference);
            entries = List.copyOf(entries);
        }

        Entry entry(String displayName) {
            for (Entry e : entries) {
                if (e.displayName().equals(displayName)) {
                    return e;
                }
            }
            return null;
        }
    }

    private static final int MAX_SEARCH_ENTRIES = 4096;
    private static final int MAX_SEARCH_DEPTH = 16;

    private DiscoveryIndex() {
    }

    static List<Entry> scan(Path shaderpacksDirectory, List<EngineDiagnostic> out) {
        List<Path> children = new ArrayList<>();
        try (var stream = Files.newDirectoryStream(shaderpacksDirectory)) {
            stream.forEach(children::add);
        } catch (IOException e) {
            out.add(diag("schmaloogium.error.pack.discovery_scan", e.toString()));
            return List.of();
        }
        children.sort(Comparator.comparing(DiscoveryIndex::displayNameOf,
            String.CASE_INSENSITIVE_ORDER));
        List<Entry> entries = new ArrayList<>();
        for (Path child : children) {
            String name = child.getFileName().toString();
            if (Files.isSymbolicLink(child)) {
                continue; // symlinks are not followed during discovery
            }
            boolean directory = Files.isDirectory(child);
            FilesystemCandidateReference reference = directory
                ? FilesystemCandidateReferences.ofDirectory(name)
                : FilesystemCandidateReferences.ofArchive(name);
            List<EngineDiagnostic> diags = new ArrayList<>();
            Optional<DiscoveryRoot> root = findRoot(child, directory, diags);
            entries.add(new Entry(name, reference, child, directory, root.isPresent(), diags, root));
        }
        return entries;
    }

    static String displayNameOf(Path child) {
        return child.getFileName().toString();
    }

    private static Optional<DiscoveryRoot> findRoot(Path candidate, boolean directory,
            List<EngineDiagnostic> diags) {
        if (directory) {
            Path shaders = candidate.resolve("shaders");
            if (Files.isDirectory(shaders) && !Files.isSymbolicLink(shaders)) {
                return Optional.of(new DiscoveryRoot("shaders", true));
            }
            return Optional.empty();
        }
        int[] count = {0};
        String[] shallowest = {null};
        try (ZipFile zip = new ZipFile(candidate.toFile())) {
            var entries = zip.stream().map(ZipEntry::getName).sorted().toList();
            for (String name : entries) {
                if (++count[0] > MAX_SEARCH_ENTRIES) {
                    break;
                }
                int depth = 0;
                boolean deep = false;
                for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) == '/') {
                        if (++depth > MAX_SEARCH_DEPTH) {
                            deep = true;
                            break;
                        }
                    }
                }
                if (deep) {
                    continue;
                }
                int idx = name.indexOf("shaders/");
                if (idx >= 0 && (idx == 0 || name.charAt(idx - 1) == '/')) {
                    String prefix = name.substring(0, idx + "shaders".length());
                    if (shallowest[0] == null || prefix.length() < shallowest[0].length()
                            || (prefix.length() == shallowest[0].length()
                                && prefix.compareTo(shallowest[0]) < 0)) {
                        if (shallowest[0] != null && !shallowest[0].equals(prefix)) {
                            diags.add(diag("schmaloogium.warn.pack.multiple_roots", prefix));
                        }
                        shallowest[0] = prefix;
                    }
                }
            }
        } catch (IOException e) {
            diags.add(diag("schmaloogium.error.pack.archive_unreadable", e.toString()));
            return Optional.empty();
        }
        if (shallowest[0] == null) {
            return Optional.empty();
        }
        return Optional.of(new DiscoveryRoot(shallowest[0], false));
    }

    private static EngineDiagnostic diag(String key, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY, key,
            List.of(), detail, "schmaloogium.pack");
    }

    /** NFC display normalization used only for comparison, never for paths. */
    static String nfc(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFC);
    }
}
