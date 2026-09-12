// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.fixture;

import com.schmaloogium.conformance.wire.Hashes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Builds a deterministic zip of a repo-owned micro-pack directory (the §4.11.6 corpus):
 * entries sorted by path, a fixed DOS timestamp, STORED method, no extra fields, so the
 * archive bytes — and therefore the registry's SHA-512 pin — depend only on the pack's
 * files. The zip is written to the cache's packs tree; nothing is committed.
 */
public final class MicroPackStager {

    /** 1980-01-01T00:00:00 in DOS time, the earliest representable zip timestamp. */
    private static final long FIXED_TIME = 315532800000L;

    private MicroPackStager() {
    }

    public static byte[] deterministicZip(Path packDir) throws IOException {
        List<String> paths = Hashes.regularFilePaths(packDir);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            zip.setMethod(ZipOutputStream.STORED);
            for (String rel : paths) {
                byte[] data = Files.readAllBytes(packDir.resolve(rel));
                ZipEntry entry = new ZipEntry(rel);
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(data.length);
                entry.setCompressedSize(data.length);
                CRC32 crc = new CRC32();
                crc.update(data);
                entry.setCrc(crc.getValue());
                entry.setTime(FIXED_TIME);
                zip.putNextEntry(entry);
                zip.write(data);
                zip.closeEntry();
            }
        }
        return bytes.toByteArray();
    }

    /** Stages {@code packDir} as {@code <cache>/packs/<id>/<version>/<archiveName>}; returns the SHA-512. */
    public static String stage(Path packDir, PackFixture fixture, FixtureCache cache) throws IOException {
        byte[] zip = deterministicZip(packDir);
        Path target = cache.packs().resolve(fixture.id()).resolve(fixture.version())
            .resolve(fixture.archiveName());
        Files.createDirectories(target.getParent());
        Path temp = target.resolveSibling(target.getFileName() + ".part");
        Files.write(temp, zip);
        Files.move(temp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
            java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        return Hashes.sha512Hex(zip);
    }
}
