// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Discovery vectors: sentinel order, tie-break order, root validation, zip candidates. */
class PackDiscoveryOrderTest {

    @TempDir
    Path packs;

    private static PackDiscoveryRequest request(Path dir) {
        return new PackDiscoveryRequest(dir, null);
    }

    @Test
    void offThenInternalThenCandidatesInCaseInsensitiveOrder() throws Exception {
        Files.createDirectories(packs.resolve("betaPack/shaders"));
        Files.createDirectories(packs.resolve("AlphaPack/shaders"));
        Files.writeString(packs.resolve("zeta.zip"), "not a real zip");
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackDiscoveryResult result = frontEnd.discover(request(packs));
        List<String> names = new ArrayList<>();
        result.candidates().forEach(c -> names.add(c.displayName()));
        assertEquals("Off", names.get(0));
        assertEquals("(internal)", names.get(1));
        // case-insensitive natural tie-break order after the two sentinels
        assertEquals(5, names.size());
        assertEquals("AlphaPack", names.get(2));
        assertEquals("betaPack", names.get(3));
        assertEquals("zeta.zip", names.get(4));
        assertEquals(PackCandidateKind.OFF, result.candidates().get(0).kind());
        assertEquals(PackCandidateKind.INTERNAL, result.candidates().get(1).kind());
        assertEquals(PackCandidateKind.DIRECTORY, result.candidates().get(2).kind());
        assertEquals(PackCandidateKind.ARCHIVE, result.candidates().get(4).kind());
    }

    @Test
    void directoryWithoutShadersRootIsUnavailableNotMissing() throws Exception {
        Files.createDirectories(packs.resolve("plainFolder"));
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackDiscoveryResult result = frontEnd.discover(request(packs));
        PackCandidate plain = result.candidates().stream()
            .filter(c -> c.displayName().equals("plainFolder")).findFirst().orElseThrow();
        assertEquals(PackCandidateStatus.UNREADABLE, plain.status());
    }

    @Test
    void archiveWithNestedShadersRootIsValid() throws Exception {
        Path zip = packs.resolve("nested.zip");
        try (java.util.zip.ZipOutputStream out = new java.util.zip.ZipOutputStream(
                Files.newOutputStream(zip))) {
            out.putNextEntry(new java.util.zip.ZipEntry("some/prefix/shaders/composite.fsh"));
            out.write("void main() {}\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            out.closeEntry();
        }
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackDiscoveryResult result = frontEnd.discover(request(packs));
        PackCandidate nested = result.candidates().stream()
            .filter(c -> c.displayName().equals("nested.zip")).findFirst().orElseThrow();
        assertEquals(PackCandidateKind.ARCHIVE, nested.kind());
        assertEquals(PackCandidateStatus.AVAILABLE, nested.status());
    }

    @Test
    void missingDirectoryYieldsErrorDiagnosticAndOnlySentinels() {
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackDiscoveryResult result = frontEnd.discover(request(packs.resolve("absent")));
        assertEquals(2, result.candidates().size());
        assertTrue(result.diagnostics().stream()
            .anyMatch(d -> d.severity() == com.schmaloogium.engine.diag.DiagnosticSeverity.ERROR));
    }

    @Test
    void staleGenerationResolvesToInvalidSnapshot() throws Exception {
        Files.createDirectories(packs.resolve("packA/shaders"));
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackDiscoveryResult first = frontEnd.discover(request(packs));
        for (int i = 0; i < 200; i++) { // exceed the retention LRU bound
            frontEnd.discover(request(packs));
        }
        PackCandidate candidate = first.candidates().get(2);
        FilesystemCandidateReference reference = candidate.filesystemReference().orElseThrow();
        FilesystemCandidateResolution resolution
            = frontEnd.resolveFilesystemCandidate(reference, first);
        assertInstanceOf(FilesystemCandidateResolution.InvalidSnapshot.class, resolution);
    }

    @Test
    void foreignGenerationIsRejectedNotCrashed() throws Exception {
        Files.createDirectories(packs.resolve("packA/shaders"));
        PackFrontEnd other = PackFrontEnds.create().frontEnd();
        PackDiscoveryResult generation = PackFrontEnds.create().frontEnd()
            .discover(request(packs));
        PackCandidate candidate = generation.candidates().get(2);
        FilesystemCandidateResolution resolution
            = other.resolveFilesystemCandidate(candidate.filesystemReference().orElseThrow(),
                generation);
        assertInstanceOf(FilesystemCandidateResolution.InvalidSnapshot.class, resolution);
    }
}
