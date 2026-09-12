// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.fixture;

import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resolves a registry row to a verified archive in the cache (§4.10.2, §4.10.5).
 * <ul>
 *   <li>{@code MANUAL}: never fetches. Present + SHA-512 match → resolved; absent →
 *       skipped with the remedy (URL, filename, hash, target path); mismatch → hard failure.</li>
 *   <li>{@code MODRINTH}: the transport is a named deferral of Task C — a warm cache is
 *       accepted after hash verification; a cold cache is skipped with the same remedy and
 *       the note that the Modrinth transport is not implemented; an unpinned row is a hard
 *       failure ([D-P2-20]).</li>
 * </ul>
 * A {@code SOURCE.txt} is written next to every resolved archive so a developer who finds
 * the file later knows what it is.
 */
public final class FixtureResolver {

    public sealed interface Outcome permits Resolved, Skipped {
    }

    public record Resolved(PackFixture fixture, Path archive, String sha512) implements Outcome {
    }

    public record Skipped(PackFixture fixture, String reason, String remedy) implements Outcome {
    }

    private final FixtureCache cache;

    public FixtureResolver(FixtureCache cache) {
        this.cache = cache;
    }

    public Path archivePath(PackFixture fixture) {
        return cache.packs().resolve(fixture.id()).resolve(fixture.version()).resolve(fixture.archiveName());
    }

    public Outcome resolve(PackFixture fixture) throws IOException {
        if (!fixture.pinned()) {
            throw new IllegalStateException("[D-P2-20] registry row " + fixture.id()
                + " has no pin (sha512" + (fixture.mode() == AcquisitionMode.MODRINTH
                ? "/modrinthVersion" : "") + "); refusing to resolve 'latest'");
        }
        Path archive = archivePath(fixture);
        if (!Files.isRegularFile(archive)) {
            String reason = fixture.mode() == AcquisitionMode.MANUAL ? "fixture-absent" : "fixture-unavailable";
            String remedy = "place " + fixture.archiveName() + " (SHA-512 " + fixture.sha512() + ") at "
                + archive + ", from " + fixture.sourceUrl()
                + (fixture.mode() == AcquisitionMode.MODRINTH
                    ? " (the Modrinth transport is not implemented; a warm cache is accepted)" : "");
            return new Skipped(fixture, reason, remedy);
        }
        String actual = Hashes.sha512HexOfFile(archive);
        if (!actual.equals(fixture.sha512())) {
            Files.delete(archive);
            throw new IllegalStateException("fixture " + fixture.id() + " archive SHA-512 mismatch:"
                + " expected " + fixture.sha512() + ", got " + actual + "; the file was deleted"
                + " (a changed artifact under a fixed pin is what integrity checking exists for)");
        }
        Path source = archive.resolveSibling("SOURCE.txt");
        if (!Files.exists(source)) {
            Files.writeString(source, "pack: " + fixture.key() + "\nlicence: " + fixture.licence()
                + "\nsource: " + fixture.sourceUrl() + "\nrule: never redistribute or commit this"
                + " archive (RESEARCH.md App G, PHASE_2_DOC §4.10.4)\n", StandardCharsets.UTF_8);
        }
        return new Resolved(fixture, archive, actual);
    }
}
