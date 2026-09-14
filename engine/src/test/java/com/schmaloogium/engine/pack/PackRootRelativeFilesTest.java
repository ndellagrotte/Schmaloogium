// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.MappingFileState;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Task F regression: snapshot keys carry the selected shaders root as a prefix, so the
 * root-relative files ({@code shaders.properties}, {@code block.properties}, …) must be
 * resolved under it. Before the fix every filesystem pack read them as absent, which
 * silently dropped the pack's properties and forced the legacy numeric id fallback.
 */
class PackRootRelativeFilesTest {

    private static final DiagnosticReporter NOOP = d -> { };

    @TempDir
    Path root;

    private static GLCapabilityProfile capabilities() {
        return new GLCapabilityProfile(4, 6, "4.60 NVIDIA", "NVIDIA Corporation",
            "NVIDIA GeForce RTX 3070/PCIe/SSE2", 8, 8, 32, 16, 32768, 2048, 32768,
            Set.of("GL_ARB_texture_rectangle"));
    }

    private static final String COMPOSITE =
        "#version 120\nuniform vec4 color;\nvoid main() { gl_FragColor = color; }\n";
    private static final String PROPERTIES = "shadowMapResolution=4096\n";
    private static final String BLOCKS = "block.42=minecraft:stone\n43=minecraft:glass\n";
    private static final String ITEMS = "item.7=minecraft:stick\n";
    private static final String ENTITIES = "entity.9=minecraft:cow\n";

    private PackLoadResult.Loaded load(String displayName) {
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackCandidate candidate = frontEnd.discover(new PackDiscoveryRequest(root, null))
            .candidates().stream().filter(c -> c.displayName().equals(displayName))
            .findFirst().orElseThrow();
        PersistenceFileAccess access = switch (PackFrontEnds.create().persistenceFiles(
            new PersistenceRootConfiguration(root, root))) {
            case PersistenceFileAccessAcquisition.Acquired a -> a.files();
            case PersistenceFileAccessAcquisition.InvalidRoots f -> throw new AssertionError(f.failure());
        };
        return assertInstanceOf(PackLoadResult.Loaded.class, frontEnd.load(new PackLoadRequest(
            root, new PackSelection.Filesystem(candidate.id()),
            new RuntimeIdentityData(1, 12, 2, "classic", "1.0.0", OsFamily.LINUX, Map.of()),
            capabilities(), new EngineOptionData(Map.of()),
            new CompanionOptionMacros(false, false), new RendererFeatureData(false, false),
            access, null, Optional.empty(), NOOP)));
    }

    private void assertRootRelativeFilesWereRead(PackLoadResult.Loaded loaded) {
        PackConfiguration cfg = loaded.configuration();
        assertEquals(MappingFileState.PRESENT_RULES, cfg.idMappings().blocks().state());
        assertEquals(2, cfg.idMappings().blocks().ordinaryRules().size(),
            "both the block.<id> and the bare <id> spellings are accepted");
        assertEquals(MappingFileState.PRESENT_RULES, cfg.idMappings().items().state());
        assertEquals(MappingFileState.PRESENT_RULES, cfg.idMappings().entities().state());
        // block.properties is parsed twice: once as BLOCK, once as LAYER; this pack
        // declares no layer.* rules, so the LAYER view is present-but-empty.
        assertEquals(MappingFileState.PRESENT_EMPTY, cfg.idMappings().layers().state());
        assertTrue(cfg.properties().engineFlags() != null, "the pack properties were decoded");
    }

    @Test
    void directoryPackReadsFilesUnderItsShadersRoot() throws IOException {
        Path shaders = root.resolve("dirPack/shaders");
        Files.createDirectories(shaders);
        Files.writeString(shaders.resolve("composite.fsh"), COMPOSITE);
        Files.writeString(shaders.resolve("shaders.properties"), PROPERTIES);
        Files.writeString(shaders.resolve("block.properties"), BLOCKS);
        Files.writeString(shaders.resolve("item.properties"), ITEMS);
        Files.writeString(shaders.resolve("entity.properties"), ENTITIES);

        assertRootRelativeFilesWereRead(load("dirPack"));
    }

    @Test
    void zipPackReadsFilesUnderItsShadersRoot() throws IOException {
        Path zip = root.resolve("zipPack.zip");
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip))) {
            put(out, "shaders/composite.fsh", COMPOSITE);
            put(out, "shaders/shaders.properties", PROPERTIES);
            put(out, "shaders/block.properties", BLOCKS);
            put(out, "shaders/item.properties", ITEMS);
            put(out, "shaders/entity.properties", ENTITIES);
        }

        assertRootRelativeFilesWereRead(load("zipPack.zip"));
    }

    private static void put(ZipOutputStream out, String name, String text) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(text.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
    }
}
