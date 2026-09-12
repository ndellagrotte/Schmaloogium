// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.fixture;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.conformance.wire.Hashes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** RegistryTest + FixtureCacheRootTest + FixtureResolverTest (MANUAL) + MicroPackStagerDeterminismTest. */
class FixtureTest {

    static final Path REGISTRY = Path.of("..", "conformance", "fixtures", "packs.registry");

    @Test
    void everyAppGRowIsPresentWithLicenceAndSource() throws IOException {
        PackFixtureRegistry registry = PackFixtureRegistry.parse(Files.readString(REGISTRY, StandardCharsets.UTF_8));
        for (String id : new String[] {"bsl", "complementary-reimagined", "complementary-unbound", "sildurs-vibrant",
            "seus-renewed", "chocapic13-v9", "projectluma"}) {
            PackFixture row = registry.find(id).orElseThrow();
            assertFalse(row.licence().isEmpty());
            assertFalse(row.sourceUrl().isEmpty());
            if (row.mode() == AcquisitionMode.MODRINTH) {
                assertFalse(row.modrinthProject().isEmpty());
            } else {
                assertTrue(row.modrinthProject().isEmpty());
            }
        }
        assertEquals("corpus", registry.find("mp-pingpong").orElseThrow().tier());
        assertThrows(IllegalArgumentException.class, () -> registry.require("bsl@9.9"));
        assertThrows(IllegalArgumentException.class, () -> PackFixtureRegistry.parse(
            "[pack x]\ndisplayName = X\ntier = classic\nmode = MANUAL\nmodrinthProject = p\nversion = 1\n"
            + "archiveName = x.zip\nlicence = l\nsourceUrl = u\n"));
    }

    @Test
    void cacheRootInsideAGitWorkTreeIsRefusedAndLeftAbsent(@TempDir Path tmp) throws IOException {
        Path tree = tmp.resolve("tree");
        Files.createDirectories(tree.resolve(".git"));
        Path requested = tree.resolve("a").resolve("b").resolve("cache");
        assertThrows(IOException.class, () -> FixtureCache.establish(tmp, requested));
        assertFalse(Files.exists(requested));
        assertFalse(Files.exists(tree.resolve("a")));
        Path outside = tmp.resolve("elsewhere").resolve("cache");
        FixtureCache cache = FixtureCache.establish(tmp, outside);
        assertTrue(Files.isDirectory(cache.runs()));
        assertTrue(Files.isDirectory(cache.packs()));
        // relative roots resolve against the working directory
        FixtureCache relative = FixtureCache.establish(tmp.resolve("elsewhere"), Path.of("rel"));
        assertEquals(tmp.resolve("elsewhere").resolve("rel").toAbsolutePath().normalize(), relative.root());
    }

    @Test
    void manualModeResolvesFromCacheOnlyAndDeletesOnMismatch(@TempDir Path tmp) throws IOException {
        FixtureCache cache = FixtureCache.establish(tmp, tmp.resolve("cache"));
        FixtureResolver resolver = new FixtureResolver(cache);
        byte[] bytes = "zipbytes".getBytes(StandardCharsets.UTF_8);
        String sha = Hashes.sha512Hex(bytes);
        PackFixture row = new PackFixture("p", "P", "classic", AcquisitionMode.MANUAL, "", "", "1", sha,
            "p.zip", "lic", "https://example");
        FixtureResolver.Outcome absent = resolver.resolve(row);
        assertTrue(absent instanceof FixtureResolver.Skipped s && s.reason().equals("fixture-absent")
            && s.remedy().contains("p.zip") && s.remedy().contains(sha));
        Path archive = resolver.archivePath(row);
        Files.createDirectories(archive.getParent());
        Files.write(archive, bytes);
        FixtureResolver.Outcome ok = resolver.resolve(row);
        assertTrue(ok instanceof FixtureResolver.Resolved r && r.sha512().equals(sha));
        assertTrue(Files.exists(archive.resolveSibling("SOURCE.txt")));
        Files.write(archive, "tampered".getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalStateException.class, () -> resolver.resolve(row));
        assertFalse(Files.exists(archive));
        PackFixture unpinned = new PackFixture("q", "Q", "classic", AcquisitionMode.MANUAL, "", "", "1", "",
            "q.zip", "lic", "u");
        assertThrows(IllegalStateException.class, () -> resolver.resolve(unpinned));
    }

    @Test
    void microPackZipIsDeterministic(@TempDir Path tmp) throws IOException {
        Path pack = tmp.resolve("pack");
        Files.createDirectories(pack.resolve("shaders"));
        Files.writeString(pack.resolve("shaders/final.fsh"), "void main(){}\n");
        Files.writeString(pack.resolve("shaders/final.vsh"), "void main(){}\n");
        byte[] first = MicroPackStager.deterministicZip(pack);
        Files.setLastModifiedTime(pack.resolve("shaders/final.fsh"), java.nio.file.attribute.FileTime.fromMillis(0));
        byte[] second = MicroPackStager.deterministicZip(pack);
        assertArrayEquals(first, second);
        Files.writeString(pack.resolve("shaders/final.fsh"), "void main(){ }\n");
        assertFalse(java.util.Arrays.equals(first, MicroPackStager.deterministicZip(pack)));
    }
}
