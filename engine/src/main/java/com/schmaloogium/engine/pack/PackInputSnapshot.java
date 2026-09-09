// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Bounded immutable in-memory pack snapshot: every indexed regular file under the
 * selected root, read once through one lease (folder walk or single zip), with
 * overflow-checked byte accounting before allocation.
 */
final class PackInputSnapshot {

    final NormalizedPackPath selectedRoot;
    final Map<NormalizedPackPath, byte[]> files;

    private PackInputSnapshot(NormalizedPackPath selectedRoot, Map<NormalizedPackPath, byte[]> files) {
        this.selectedRoot = selectedRoot;
        this.files = files;
    }

    PackIdentity identity() {
        Map<NormalizedPackPath, String> hashes = new TreeMap<>(NormalizedPackPath.ORDER);
        files.forEach((p, b) -> hashes.put(p, Sha256.hex(b)));
        return new PackIdentity(selectedRoot, hashes);
    }

    PackAssetSnapshotImpl assets() {
        Map<NormalizedPackPath, PackAssetRow> rows = new TreeMap<>(NormalizedPackPath.ORDER);
        files.forEach((p, b) -> rows.put(p, PackAssetRow.available(p, b)));
        return new PackAssetSnapshotImpl(identity(), rows);
    }

    /** Reads a folder root; the normalized root string is the shaders prefix inside it. */
    static PackInputSnapshot ofDirectory(Path shadersDir, PackInputLimits limits,
            String rootRelative) throws IOException {
        Map<NormalizedPackPath, byte[]> files = new TreeMap<>(NormalizedPackPath.ORDER);
        long[] total = {0};
        int[] count = {0};
        walk(shadersDir, shadersDir, files, limits, total, count);
        return new PackInputSnapshot(new NormalizedPackPath(rootRelative), files);
    }

    private static void walk(Path root, Path dir, Map<NormalizedPackPath, byte[]> files,
            PackInputLimits limits, long[] total, int[] count) throws IOException {
        try (var stream = Files.newDirectoryStream(dir)) {
            for (var child : stream) {
                if (Files.isSymbolicLink(child)) {
                    continue; // folder symlinks are not followed during discovery
                }
                if (Files.isDirectory(child)) {
                    walk(root, child, files, limits, total, count);
                } else if (Files.isRegularFile(child)) {
                    String relative = root.relativize(child).toString().replace('\\', '/');
                    byte[] bytes = readBounded(child, limits, total, count, relative);
                    files.put(new NormalizedPackPath(relative), bytes);
                }
            }
        }
    }

    private static byte[] readBounded(Path file, PackInputLimits limits, long[] total,
            int[] count, String relative) throws IOException {
        if (relative.length() > limits.maxPathLength()) {
            throw new IOException("path exceeds maxPathLength: " + relative);
        }
        if (++count[0] > limits.maxEntries()) {
            throw new IOException("entry count exceeds maxEntries");
        }
        long size = Files.size(file);
        if (total[0] + size > limits.maxTotalBytes() || total[0] + size < 0) {
            throw new IOException("total bytes exceed maxTotalBytes");
        }
        if (size > Integer.MAX_VALUE) {
            throw new IOException("file too large for buffer domain");
        }
        total[0] += size;
        return Files.readAllBytes(file);
    }

    /** Reads one archive subtree under the given shaders root prefix. */
    static PackInputSnapshot ofArchive(Path archive, String shadersRootPrefix,
            PackInputLimits limits) throws IOException {
        Map<NormalizedPackPath, byte[]> files = new TreeMap<>(NormalizedPackPath.ORDER);
        long total = 0;
        int count = 0;
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            var entries = zip.stream()
                .sorted(Comparator.comparing(ZipEntry::getName))
                .toList();
            for (ZipEntry entry : entries) {
                if (entry.isDirectory() || !entry.getName().startsWith(shadersRootPrefix)) {
                    continue;
                }
                String relative = entry.getName().substring(shadersRootPrefix.length())
                    .replace('\\', '/');
                if (relative.isEmpty() || relative.endsWith("/")) {
                    continue;
                }
                if (relative.length() > limits.maxPathLength()) {
                    throw new IOException("path exceeds maxPathLength: " + relative);
                }
                if (++count > limits.maxEntries()) {
                    throw new IOException("entry count exceeds maxEntries");
                }
                long size = entry.getSize();
                if (size < 0 || total + size > limits.maxTotalBytes() || total + size < 0) {
                    throw new IOException("total bytes exceed maxTotalBytes");
                }
                if (size > Integer.MAX_VALUE) {
                    throw new IOException("file too large for buffer domain");
                }
                total += size;
                byte[] bytes = readExactly(zip, entry, (int) size);
                files.put(new NormalizedPackPath(relative), bytes);
            }
        }
        String root = shadersRootPrefix.endsWith("/")
            ? shadersRootPrefix.substring(0, shadersRootPrefix.length() - 1)
            : shadersRootPrefix;
        return new PackInputSnapshot(new NormalizedPackPath(root), files);
    }

    private static byte[] readExactly(ZipFile zip, ZipEntry entry, int size) throws IOException {
        try (InputStream in = zip.getInputStream(entry)) {
            byte[] bytes = in.readNBytes(size);
            if (bytes.length != size || in.read() != -1) {
                throw new IOException("unstable archive entry size: " + entry.getName());
            }
            return bytes;
        }
    }
}
