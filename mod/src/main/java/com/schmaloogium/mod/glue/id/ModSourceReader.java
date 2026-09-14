// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.id.ModIdSourceSnapshot;
import com.schmaloogium.engine.config.id.ModSourceEntry;
import com.schmaloogium.engine.pack.ImmutableBytes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Bounded per-mod source acquisition (PHASE_9_DOC §4.3): exactly the three paths
 * {@code assets/<modid>/shaders/{block,item,entity}.properties} from a mod's jar or
 * directory root. Symlinks leaving a directory root, non-regular files, over-size files
 * and an over-size total are rejected per mod (that mod contributes nothing; siblings
 * continue). Pure over {@link Path} so it is unit-testable.
 */
public final class ModSourceReader {

    /** One candidate mod root. */
    public record ModRoot(String modId, Path source) {
    }

    static final int MAX_TOTAL_BYTES = 32 * 1024 * 1024;

    private ModSourceReader() {
    }

    /** Reads every root; {@code warn} receives (modId, reason) once per rejected mod. */
    public static ModIdSourceSnapshot read(List<ModRoot> roots, BiConsumer<String, String> warn) {
        List<ModSourceEntry> entries = new ArrayList<>();
        long total = 0;
        for (ModRoot root : roots) {
            if (root.modId() == null || !root.modId().matches("[a-z0-9][a-z0-9_.-]{0,63}")) {
                continue; // an unrepresentable id can never own a rule
            }
            List<ModSourceEntry> mine = new ArrayList<>();
            try {
                for (MappingKind kind : List.of(MappingKind.BLOCK, MappingKind.ITEM, MappingKind.ENTITY)) {
                    String relative = "assets/" + root.modId() + "/shaders/" + fileName(kind);
                    byte[] bytes = readBounded(root.source(), relative);
                    if (bytes == null) {
                        continue;
                    }
                    mine.add(new ModSourceEntry(root.modId(), kind, relative,
                            ImmutableBytes.of(bytes), sha256(bytes)));
                }
            } catch (IOException | RuntimeException failure) {
                warn.accept(root.modId(), failure.toString());
                continue;
            }
            long mineBytes = mine.stream().mapToLong(e -> e.bytes().size()).sum();
            if (total + mineBytes > MAX_TOTAL_BYTES) {
                warn.accept(root.modId(), "total mod source bytes exceed the bound");
                continue;
            }
            total += mineBytes;
            entries.addAll(mine);
        }
        return new ModIdSourceSnapshot(entries);
    }

    static String fileName(MappingKind kind) {
        return switch (kind) {
            case BLOCK -> "block.properties";
            case ITEM -> "item.properties";
            case ENTITY -> "entity.properties";
            case LAYER -> throw new IllegalArgumentException("mods never contribute layer rules");
        };
    }

    /** Null when absent; throws when present but out of bounds or not a regular file. */
    static byte[] readBounded(Path source, String relative) throws IOException {
        if (source == null || !Files.exists(source)) {
            return null;
        }
        if (Files.isDirectory(source)) {
            Path root = source.toRealPath();
            Path candidate = root.resolve(relative).normalize();
            if (!candidate.startsWith(root) || !Files.exists(candidate, LinkOption.NOFOLLOW_LINKS)) {
                return null;
            }
            Path real = candidate.toRealPath();
            if (!real.startsWith(root)) {
                throw new IOException("symlink leaves the mod root: " + relative);
            }
            if (!Files.isRegularFile(real)) {
                throw new IOException("not a regular file: " + relative);
            }
            long size = Files.size(real);
            if (size > ModSourceEntry.MAX_ENTRY_BYTES) {
                throw new IOException("mod source exceeds the per-file bound: " + relative);
            }
            return Files.readAllBytes(real);
        }
        try (ZipFile zip = new ZipFile(source.toFile())) {
            ZipEntry entry = zip.getEntry(relative);
            if (entry == null) {
                return null;
            }
            if (entry.isDirectory()) {
                throw new IOException("not a regular entry: " + relative);
            }
            try (InputStream in = zip.getInputStream(entry)) {
                byte[] bytes = in.readNBytes(ModSourceEntry.MAX_ENTRY_BYTES + 1);
                if (bytes.length > ModSourceEntry.MAX_ENTRY_BYTES) {
                    throw new IOException("mod source exceeds the per-file bound: " + relative);
                }
                return bytes;
            }
        }
    }

    static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Test/diagnostic helper: the entry text as ISO-8859-1 (the parser's own decode). */
    static String text(ModSourceEntry entry) {
        return new String(entry.bytes().copy(), StandardCharsets.ISO_8859_1);
    }
}
