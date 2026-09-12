// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.scene.CapturePlanWriter;
import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * The authenticated launch inventory (§5.1.1): the loader's active mod list with each
 * container's source artifact. The client writes it once in inventory mode
 * ({@code -Dschmaloogium.conformance.inventory=<file>}, schema
 * {@code schmaloogium.launch-inventory/1}); the runner re-hashes every listed source from
 * disk (jar → SHA-256 of bytes, directory → framed tree hash), requires the subject record
 * to equal the launch spec's subject directories hash, and only then freezes the resulting
 * ids and hashes into the plan. A stale or tampered listing therefore cannot pass.
 */
public final class LaunchInventory {

    public static final String SCHEMA_LINE = "schema = schmaloogium.launch-inventory/1";
    public static final String SUBJECT_MOD_ID = "schmaloogium";

    private LaunchInventory() {
    }

    /** Hashes one source artifact: a jar/file by bytes, a directory by framed tree. */
    public static String hashSource(Path source) throws IOException {
        if (Files.isSymbolicLink(source)) {
            throw new IOException("mod source is a symbolic link: " + source);
        }
        if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
            return Hashes.framedTreeSha256(source);
        }
        if (Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
            return Hashes.sha256HexOfFile(source);
        }
        throw new IOException("mod source is neither a file nor a directory: " + source);
    }

    /** The subject identity: framed hash over the subject dirs' combined file tree. */
    public static String subjectHash(java.util.List<Path> subjectDirs) throws IOException {
        Map<String, byte[]> records = new TreeMap<>();
        for (Path dir : subjectDirs) {
            if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
                continue; // an absent resources dir is simply empty
            }
            for (String rel : Hashes.regularFilePaths(dir)) {
                String key = dir.getFileName() + "/" + rel;
                if (records.putIfAbsent(key, Files.readAllBytes(dir.resolve(rel))) != null) {
                    throw new IOException("duplicate subject path " + key);
                }
            }
        }
        return Hashes.framedSha256(records);
    }

    /** Re-verifies the client-written listing and derives the plan environment. */
    public static CapturePlanWriter.Environment authenticate(String inventoryText, ClientLaunchSpec launch,
            String minecraftVersion) throws IOException {
        FlatDocument d = FlatDocument.parse(inventoryText, SCHEMA_LINE);
        String subjectExpected = subjectHash(launch.subjectDirs());
        int count = d.count("mods");
        TreeMap<String, String> hashes = new TreeMap<>();
        boolean subjectSeen = false;
        for (int i = 0; i < count; i++) {
            String id = d.text("mods." + i + ".id");
            String source = d.text("mods." + i + ".source");
            String claimed = d.token("mods." + i + ".sha256");
            if (id.isEmpty()) {
                throw new IOException("inventory row " + i + " has an empty mod id");
            }
            String actual;
            if (id.equals(SUBJECT_MOD_ID)) {
                // the subject's identity is the runner's: hashed from the launch spec's directories,
                // never trusted from the client (which writes a placeholder for its own record)
                actual = subjectExpected;
                claimed = actual;
                subjectSeen = true;
            } else if (source.isEmpty()) {
                throw new IOException("inventory row " + id + " has no source artifact");
            } else {
                actual = hashSource(Path.of(source));
            }
            if (!actual.equals(claimed)) {
                throw new IOException("inventory hash for " + id + " does not match its source on disk:"
                    + " claimed " + claimed + ", actual " + actual);
            }
            if (hashes.putIfAbsent(id, actual) != null) {
                throw new IOException("duplicate mod id in inventory: " + id);
            }
        }
        if (!subjectSeen) {
            throw new IOException("inventory does not list the subject mod " + SUBJECT_MOD_ID);
        }
        return new CapturePlanWriter.Environment(SUBJECT_MOD_ID, subjectExpected, modSetHash(hashes, false),
            modSetHash(hashes, true), minecraftVersion, hashes);
    }

    /** §4.5.4 framing over {@code (id, sha256)} records; {@code external} excludes the subject. */
    public static String modSetHash(Map<String, String> idToHash, boolean external) {
        Map<String, byte[]> records = new TreeMap<>();
        for (Map.Entry<String, String> e : idToHash.entrySet()) {
            if (external && e.getKey().equals(SUBJECT_MOD_ID)) {
                continue;
            }
            records.put(e.getKey(), e.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        return Hashes.framedSha256(records);
    }
}
