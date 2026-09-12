// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.fixture.FixtureCache;
import com.schmaloogium.conformance.scene.SceneSpec;
import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * [D-P2-48] the digest-addressed world cache (§4.5.5). The descriptor
 * ({@code schmaloogium.world-generation/1}) holds exactly {@code externalModSetSha256},
 * {@code generateStructures}, {@code minecraftVersion}, {@code seed}, {@code worldType}; its
 * exact-byte SHA-256 is {@code environment.worldGenerationSha256}. The sole entry is
 * {@code <cache>/worlds/<sha>/} with {@code generation.worldgen}, {@code receipt.world} and the
 * immutable {@code save/}. Generation happens in a private temporary entry and is published
 * atomically after no-follow tree hashing; a hit requires byte-equal descriptor and matching
 * receipt/save hashes; anything malformed fails closed.
 */
public final class WorldCache {

    public static final String DESCRIPTOR_SCHEMA_LINE = "schema = schmaloogium.world-generation/1";
    public static final String RECEIPT_SCHEMA_LINE = "schema = schmaloogium.world-receipt/1";

    public record Descriptor(FlatDocument document) {
        public String sha256() {
            return document.sha256();
        }
    }

    public record Entry(Descriptor descriptor, Path dir, String worldSha256) {
        public Path save() {
            return dir.resolve("save");
        }
    }

    private final FixtureCache cache;

    public WorldCache(FixtureCache cache) {
        this.cache = cache;
    }

    public static Descriptor descriptor(SceneSpec.World world, String minecraftVersion,
            String externalModSetSha256) {
        FlatDocument d = FlatDocument.builder(DESCRIPTOR_SCHEMA_LINE)
            .token("externalModSetSha256", externalModSetSha256)
            .bool("generateStructures", world.generateStructures())
            .token("minecraftVersion", minecraftVersion)
            .integer("seed", world.seed())
            .token("worldType", world.worldType())
            .build();
        return new Descriptor(d);
    }

    public Path entryDir(Descriptor descriptor) {
        return cache.worlds().resolve(descriptor.sha256());
    }

    /** A verified hit, or empty on a genuine miss; a malformed entry throws (fail closed). */
    public Optional<Entry> lookup(Descriptor descriptor) throws IOException {
        Path dir = entryDir(descriptor);
        if (!Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
            return Optional.empty();
        }
        Path gen = dir.resolve("generation.worldgen");
        Path receipt = dir.resolve("receipt.world");
        Path save = dir.resolve("save");
        if (!Files.isRegularFile(gen, LinkOption.NOFOLLOW_LINKS)
                || !Files.isRegularFile(receipt, LinkOption.NOFOLLOW_LINKS)
                || !Files.isDirectory(save, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("world cache entry " + dir + " is incomplete (fails closed; remove"
                + " it to regenerate)");
        }
        String storedDescriptor = Files.readString(gen, StandardCharsets.UTF_8);
        if (!storedDescriptor.equals(descriptor.document().render())) {
            throw new IOException("world cache entry " + dir + " descriptor bytes differ from the request");
        }
        FlatDocument r = FlatDocument.parse(Files.readString(receipt, StandardCharsets.UTF_8),
            RECEIPT_SCHEMA_LINE);
        if (!r.token("worldGenerationSha256").equals(descriptor.sha256())) {
            throw new IOException("world cache receipt names a different descriptor digest");
        }
        String actual = Hashes.framedTreeSha256(save);
        if (!actual.equals(r.token("worldSha256"))) {
            throw new IOException("world cache save/ hash " + actual + " differs from its receipt "
                + r.token("worldSha256") + " (the immutable save was modified)");
        }
        return Optional.of(new Entry(descriptor, dir, actual));
    }

    /** Publishes a freshly generated save directory as the entry (atomic rename). */
    public Entry publish(Descriptor descriptor, Path generatedSave) throws IOException {
        Path dir = entryDir(descriptor);
        Path temp = cache.worlds().resolve("." + descriptor.sha256() + ".part");
        if (Files.exists(temp)) {
            deleteTree(temp);
        }
        Files.createDirectories(temp);
        copyTree(generatedSave, temp.resolve("save"));
        String worldSha = Hashes.framedTreeSha256(temp.resolve("save"));
        Files.writeString(temp.resolve("generation.worldgen"), descriptor.document().render(),
            StandardCharsets.UTF_8);
        FlatDocument receipt = FlatDocument.builder(RECEIPT_SCHEMA_LINE)
            .token("worldGenerationSha256", descriptor.sha256())
            .token("worldSha256", worldSha)
            .build();
        Files.writeString(temp.resolve("receipt.world"), receipt.render(), StandardCharsets.UTF_8);
        Files.move(temp, dir, StandardCopyOption.ATOMIC_MOVE);
        return new Entry(descriptor, dir, worldSha);
    }

    /** No-follow copy of a save tree; links and special entries are containment failures. */
    public static void copyTree(Path from, Path to) throws IOException {
        for (String rel : Hashes.regularFilePaths(from)) {
            Path target = to.resolve(rel);
            Files.createDirectories(target.getParent());
            Files.copy(from.resolve(rel), target, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    public static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            });
        }
    }
}
