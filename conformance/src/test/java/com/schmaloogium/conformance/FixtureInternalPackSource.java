// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import com.schmaloogium.engine.pack.ImmutableBytes;
import com.schmaloogium.engine.pack.InternalPackEntry;
import com.schmaloogium.engine.pack.InternalPackReadException;
import com.schmaloogium.engine.pack.InternalPackSnapshot;
import com.schmaloogium.engine.pack.InternalPackSource;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackIdentity;
import com.schmaloogium.engine.pack.PackInputLimits;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Bounded {@link InternalPackSource} over a committed fixture directory — the harness's
 * stand-in for Phase 7's internal provider, exercising the internal selection path of
 * {@code frontEnd.inspect} headlessly. The snapshot mirrors {@code PackInputSnapshot}'s
 * rules: files under {@code shaders/}, canonical normalized paths, per-file SHA-256.
 */
public final class FixtureInternalPackSource implements InternalPackSource {

    private final Path shadersDir;
    private final String rootName;

    public FixtureInternalPackSource(Path fixturePackDir, String rootName) {
        this.shadersDir = fixturePackDir.resolve("shaders");
        this.rootName = rootName;
        if (!Files.isDirectory(shadersDir)) {
            throw new IllegalArgumentException("fixture has no shaders/ root: " + fixturePackDir);
        }
    }

    @Override
    public PackIdentity identity() {
        return new PackIdentity(new NormalizedPackPath(rootName), contentHashes());
    }

    @Override
    public InternalPackSnapshot snapshot(PackInputLimits limits) throws InternalPackReadException {
        List<InternalPackEntry> entries = new ArrayList<>();
        long[] total = {0};
        int[] count = {0};
        try (Stream<Path> stream = Files.walk(shadersDir)) {
            List<Path> files = stream.filter(Files::isRegularFile).sorted().toList();
            for (Path file : files) {
                String relative = "shaders/" + shadersDir.relativize(file).toString().replace('\\', '/');
                if (relative.length() > limits.maxPathLength()) {
                    throw new InternalPackReadException("path exceeds maxPathLength: " + relative);
                }
                if (++count[0] > limits.maxEntries()) {
                    throw new InternalPackReadException("entry count exceeds maxEntries");
                }
                long size = Files.size(file);
                if ((total[0] += size) > limits.maxTotalBytes()) {
                    throw new InternalPackReadException("total bytes exceed maxTotalBytes");
                }
                entries.add(new InternalPackEntry.File(new NormalizedPackPath(relative),
                    ImmutableBytes.of(Files.readAllBytes(file))));
            }
        } catch (IOException e) {
            throw new InternalPackReadException("fixture unreadable: " + e);
        }
        if (entries.isEmpty()) {
            throw new InternalPackReadException("internal fixture produced no files");
        }
        return new InternalPackSnapshot(entries);
    }

    private Map<NormalizedPackPath, String> contentHashes() {
        Map<NormalizedPackPath, String> hashes = new TreeMap<>();
        try (Stream<Path> stream = Files.walk(shadersDir)) {
            stream.filter(Files::isRegularFile).sorted().forEach(file -> {
                try {
                    hashes.put(new NormalizedPackPath("shaders/"
                        + shadersDir.relativize(file).toString().replace('\\', '/')),
                        sha256Hex(Files.readAllBytes(file)));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return hashes;
    }

    static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder out = new StringBuilder();
            for (byte b : digest.digest(bytes)) {
                out.append(Character.forDigit((b >> 4) & 0xF, 16));
                out.append(Character.forDigit(b & 0xF, 16));
            }
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
