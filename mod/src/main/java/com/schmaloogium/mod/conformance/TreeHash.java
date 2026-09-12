// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * The client-side copy of §4.5.4's framed tree hash (sorted relative slash paths,
 * {@code path\0length\0bytes}, no-follow, links and special entries are hard failures) so the
 * agent can authenticate the copied save against the plan before loading it (§5.1.1).
 */
final class TreeHash {

    private TreeHash() {
    }

    static String sha256Hex(byte[] bytes) {
        return hex(digest().digest(bytes));
    }

    static String fileSha256(Path file) throws IOException {
        MessageDigest md = digest();
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[1 << 16];
            int n;
            while ((n = in.read(buffer)) > 0) {
                md.update(buffer, 0, n);
            }
        }
        return hex(md.digest());
    }

    static String framedTreeSha256(Path root) throws IOException {
        List<Path> files = new ArrayList<>();
        collect(root, root, files);
        TreeMap<String, Path> byPath = new TreeMap<>();
        for (Path file : files) {
            byPath.put(relative(root, file), file);
        }
        MessageDigest md = digest();
        for (var e : byPath.entrySet()) {
            md.update(e.getKey().getBytes(StandardCharsets.UTF_8));
            md.update((byte) 0);
            md.update(Long.toString(Files.size(e.getValue())).getBytes(StandardCharsets.US_ASCII));
            md.update((byte) 0);
            try (InputStream in = Files.newInputStream(e.getValue())) {
                byte[] buffer = new byte[1 << 16];
                int n;
                while ((n = in.read(buffer)) > 0) {
                    md.update(buffer, 0, n);
                }
            }
        }
        return hex(md.digest());
    }

    private static void collect(Path root, Path dir, List<Path> out) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                BasicFileAttributes a = Files.readAttributes(entry, BasicFileAttributes.class,
                        LinkOption.NOFOLLOW_LINKS);
                if (a.isSymbolicLink() || a.isOther()) {
                    throw new IOException("containment failure under " + root + ": " + entry);
                }
                if (a.isDirectory()) {
                    collect(root, entry, out);
                } else if (a.isRegularFile()) {
                    out.add(entry);
                }
            }
        }
    }

    private static String relative(Path root, Path file) {
        StringBuilder sb = new StringBuilder();
        for (Path part : root.relativize(file)) {
            if (sb.length() > 0) {
                sb.append('/');
            }
            sb.append(part);
        }
        return sb.toString();
    }

    private static MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
