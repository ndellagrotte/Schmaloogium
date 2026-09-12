// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.wire;

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
import java.util.Map;
import java.util.TreeMap;

/**
 * Lowercase-hex digests and the §4.5.4 framed tree hash: for every regular file, sorted by
 * its {@code /}-separated relative path in UTF-8 byte order, hash {@code path\0length\0bytes}.
 * The walk never follows links; a link, socket, device or FIFO anywhere is a hard failure.
 */
public final class Hashes {

    private Hashes() {
    }

    public static String sha256Hex(byte[] bytes) {
        return hex(digest("SHA-256", bytes));
    }

    public static String sha512Hex(byte[] bytes) {
        return hex(digest("SHA-512", bytes));
    }

    public static String sha256HexOf(String text) {
        return sha256Hex(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha512HexOfFile(Path file) throws IOException {
        MessageDigest md = newDigest("SHA-512");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[1 << 16];
            int n;
            while ((n = in.read(buffer)) > 0) {
                md.update(buffer, 0, n);
            }
        }
        return hex(md.digest());
    }

    public static String sha256HexOfFile(Path file) throws IOException {
        MessageDigest md = newDigest("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[1 << 16];
            int n;
            while ((n = in.read(buffer)) > 0) {
                md.update(buffer, 0, n);
            }
        }
        return hex(md.digest());
    }

    /** The framed digest over {@code (path, content)} records already sorted by path. */
    public static String framedSha256(Map<String, byte[]> sortedRecords) {
        MessageDigest md = newDigest("SHA-256");
        for (Map.Entry<String, byte[]> e : new TreeMap<>(sortedRecords).entrySet()) {
            frame(md, e.getKey(), e.getValue().length);
            md.update(e.getValue());
        }
        return hex(md.digest());
    }

    /** Framed tree hash of every regular file beneath {@code root} (no-follow). */
    public static String framedTreeSha256(Path root) throws IOException {
        List<Path> files = new ArrayList<>();
        collectRegularFiles(root, root, files);
        TreeMap<String, Path> byPath = new TreeMap<>();
        for (Path file : files) {
            byPath.put(relativeSlashPath(root, file), file);
        }
        MessageDigest md = newDigest("SHA-256");
        for (Map.Entry<String, Path> e : byPath.entrySet()) {
            long size = Files.size(e.getValue());
            frame(md, e.getKey(), size);
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

    /** Lists the relative slash paths of every regular file beneath {@code root} (no-follow). */
    public static List<String> regularFilePaths(Path root) throws IOException {
        List<Path> files = new ArrayList<>();
        collectRegularFiles(root, root, files);
        List<String> paths = new ArrayList<>(files.size());
        for (Path file : files) {
            paths.add(relativeSlashPath(root, file));
        }
        paths.sort(CanonicalText.ORDER);
        return paths;
    }

    private static void collectRegularFiles(Path root, Path dir, List<Path> out) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(dir, BasicFileAttributes.class,
            LinkOption.NOFOLLOW_LINKS);
        if (!attrs.isDirectory()) {
            throw new IOException("not a directory (links are not followed): " + dir);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                BasicFileAttributes a = Files.readAttributes(entry, BasicFileAttributes.class,
                    LinkOption.NOFOLLOW_LINKS);
                if (a.isSymbolicLink() || a.isOther()) {
                    throw new IOException("containment failure: link or special entry under "
                        + root + ": " + entry);
                }
                if (a.isDirectory()) {
                    collectRegularFiles(root, entry, out);
                } else if (a.isRegularFile()) {
                    out.add(entry);
                }
            }
        }
    }

    private static String relativeSlashPath(Path root, Path file) {
        Path rel = root.relativize(file);
        StringBuilder sb = new StringBuilder();
        for (Path part : rel) {
            if (sb.length() > 0) {
                sb.append('/');
            }
            sb.append(part.toString());
        }
        return sb.toString();
    }

    private static void frame(MessageDigest md, String path, long length) {
        md.update(path.getBytes(StandardCharsets.UTF_8));
        md.update((byte) 0);
        md.update(Long.toString(length).getBytes(StandardCharsets.US_ASCII));
        md.update((byte) 0);
    }

    private static byte[] digest(String algorithm, byte[] bytes) {
        return newDigest(algorithm).digest(bytes);
    }

    private static MessageDigest newDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    public static boolean isHex(String s, int digits) {
        if (s == null || s.length() != digits) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) {
                return false;
            }
        }
        return true;
    }
}
