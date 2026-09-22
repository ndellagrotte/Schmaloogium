// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
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
 * Section 4.8 persistence round trip through real bundle-issued credentials:
 * a directory pack and a zip pack, target naming, write, and read-back apply.
 */
class PackOptionsRoundTripTest {

    private static final DiagnosticReporter NOOP = d -> { };

    @TempDir
    Path root;

    private static GLCapabilityProfile capabilities() {
        return new GLCapabilityProfile(4, 6, "4.60 NVIDIA", "NVIDIA Corporation",
            "NVIDIA GeForce RTX 3070/PCIe/SSE2", 8, 8, 32, 16, 32768, 2048, 32768,
            Set.of("GL_ARB_texture_rectangle"));
    }

    private static RuntimeIdentityData identity() {
        return new RuntimeIdentityData(1, 12, 2, "classic", "1.0.0", OsFamily.LINUX, Map.of());
    }

    private static void writeShaderRoot(Path shaders) throws Exception {
        Files.createDirectories(shaders);
        Files.writeString(shaders.resolve("composite.fsh"),
            "#version 120\nuniform vec4 color;\nvoid main() { gl_FragColor = color; }\n");
    }

    private PackCandidate candidate(PackFrontEnd frontEnd, String displayName) {
        PackDiscoveryResult discovery = frontEnd.discover(new PackDiscoveryRequest(root, null));
        return discovery.candidates().stream()
            .filter(c -> c.displayName().equals(displayName))
            .findFirst()
            .orElseThrow();
    }

    private PackLoadResult.Loaded loadDirectoryPack(PackFrontEnd frontEnd,
            PersistenceFileAccess access, PackCandidate candidate) {
        PackLoadRequest request = new PackLoadRequest(root,
            new PackSelection.Filesystem(candidate.id()), identity(), capabilities(),
            new EngineOptionData(Map.of()), new CompanionOptionMacros(false, false),
            new RendererFeatureData(false, false), access, null, Optional.empty(), NOOP);
        return assertInstanceOf(PackLoadResult.Loaded.class, frontEnd.load(request));
    }

    @Test
    void sharedIncludeDefaultsAreDiscoveredOnceAcrossStagesAndDimensions() throws Exception {
        Path shaders = root.resolve("includedOptions/shaders");
        Files.createDirectories(shaders.resolve("lib"));
        Files.createDirectories(shaders.resolve("world1"));
        Files.writeString(shaders.resolve("composite.fsh"),
            "#version 120\n#include \"lib/settings.glsl\"\nvoid main() {}\n");
        Files.writeString(shaders.resolve("composite.vsh"),
            "#version 120\n#include \"lib/settings.glsl\"\nvoid main() {}\n");
        Files.writeString(shaders.resolve("world1/composite.fsh"),
            "#version 120\n#include \"/lib/settings.glsl\"\nvoid main() {}\n");
        Files.writeString(shaders.resolve("lib/settings.glsl"),
            "const float shadowDistance = 96.0;\n"
                + "#define QUALITY_LEVEL 2 // [1 2 3]\n"
                + "#define REFLECTION_MODE -1 // [-1 0 1]\n");

        PackFrontEndServices services = PackFrontEnds.create();
        PackFrontEnd frontEnd = services.frontEnd();
        PersistenceFileAccess access = assertInstanceOf(PersistenceFileAccessAcquisition.Acquired.class,
            services.persistenceFiles(new PersistenceRootConfiguration(root, root))).files();
        var options = loadDirectoryPack(frontEnd, access, candidate(frontEnd, "includedOptions"))
            .configuration().options();

        assertEquals(Map.of("shadowDistance", new TextOptionValue("96.0"),
            "QUALITY_LEVEL", new TextOptionValue("2"),
            "REFLECTION_MODE", new TextOptionValue("-1")), options.state().values());
        for (String name : options.state().values().keySet()) {
            var definition = options.catalog().find(name).orElseThrow();
            assertEquals(1, definition.occurrences().size(),
                "sharing one original across stages and dimensions must not duplicate rewrite locations");
            assertEquals("shaders/lib/settings.glsl",
                definition.occurrences().getFirst().source().canonicalString());
        }
    }

    @Test
    void optionDiscoveryUsesRootConnectedWeakComponentsOnly() throws Exception {
        Path shaders = root.resolve("optionGraph/shaders");
        Files.createDirectories(shaders.resolve("lib"));
        Files.writeString(shaders.resolve("composite.fsh"),
            "#version 120\n#include \"lib/shared.glsl\"\nvoid main() {}\n");
        Files.writeString(shaders.resolve("lib/shared.glsl"),
            "const float shadowDistance = 80.0;\n");
        // This captured source has no incoming include, but belongs to the same weak component.
        Files.writeString(shaders.resolve("lib/reverse.glsl"),
            "#include \"shared.glsl\"\nconst float sunPathRotation = 15.0;\n");
        Files.writeString(shaders.resolve("lib/disconnected.glsl"),
            "const float shadowDistance = 500.0;\nconst int noiseTextureResolution = 32;\n");

        PackFrontEndServices services = PackFrontEnds.create();
        PackFrontEnd frontEnd = services.frontEnd();
        PersistenceFileAccess access = assertInstanceOf(PersistenceFileAccessAcquisition.Acquired.class,
            services.persistenceFiles(new PersistenceRootConfiguration(root, root))).files();
        var options = loadDirectoryPack(frontEnd, access, candidate(frontEnd, "optionGraph"))
            .configuration().options();

        assertEquals(Map.of("shadowDistance", new TextOptionValue("80.0"),
            "sunPathRotation", new TextOptionValue("15.0")), options.state().values());
        assertEquals(1, options.catalog().find("shadowDistance").orElseThrow().occurrences().size());
        assertTrue(options.catalog().find("noiseTextureResolution").isEmpty(),
            "disconnected declarations must neither create options nor change connected defaults");
    }

    @Test
    void loadReportsIndexWarningsOnce() throws Exception {
        Path shaders = root.resolve("missingInclude/shaders");
        writeShaderRoot(shaders);
        Files.writeString(shaders.resolve("composite.fsh"),
            "#version 120\n#include \"missing.glsl\"\nvoid main() {}\n");
        PackFrontEndServices services = PackFrontEnds.create();
        PackFrontEnd frontEnd = services.frontEnd();
        PersistenceFileAccess access = assertInstanceOf(PersistenceFileAccessAcquisition.Acquired.class,
            services.persistenceFiles(new PersistenceRootConfiguration(root, root))).files();
        var diagnostics = new java.util.ArrayList<com.schmaloogium.engine.diag.EngineDiagnostic>();
        PackLoadRequest request = new PackLoadRequest(root,
            new PackSelection.Filesystem(candidate(frontEnd, "missingInclude").id()),
            identity(), capabilities(), new EngineOptionData(Map.of()),
            new CompanionOptionMacros(false, false), new RendererFeatureData(false, false),
            access, null, Optional.empty(), diagnostics::add);

        assertInstanceOf(PackLoadResult.Loaded.class, frontEnd.load(request));
        assertEquals(1L, diagnostics.stream()
            .filter(d -> d.messageKey().equals("schmaloogium.warn.include.missing")).count(),
            "index-owned warnings must reach the load reporter without duplicate emission");
    }

    @Test
    void directoryPackTargetRoundTripsThroughWriteAndRead() throws Exception {
        writeShaderRoot(root.resolve("realPack/shaders"));
        Files.writeString(root.resolve("realPack.txt"),
            "screen.<empty>\n", StandardCharsets.ISO_8859_1);

        PackFrontEndServices services = PackFrontEnds.create();
        PackFrontEnd frontEnd = services.frontEnd();
        PackCandidate candidate = candidate(frontEnd, "realPack");

        // target: exact direct-child host name plus .txt (section 4.8.1)
        PackOptionsTargetAcquisition acquisition = frontEnd.packOptionsTarget(candidate.id());
        PackOptionsTarget target = assertInstanceOf(
            PackOptionsTargetAcquisition.Acquired.class, acquisition).target();
        assertEquals("realPack.txt", target.fileName());
        PersistenceFileAccess access = switch (services.persistenceFiles(
            new PersistenceRootConfiguration(root, root))) {
            case PersistenceFileAccessAcquisition.Acquired a -> a.files();
            case PersistenceFileAccessAcquisition.InvalidRoots f -> throw new AssertionError(
                "roots must be valid directories: " + f.failure());
        };
        PackLoadResult.Loaded loaded = loadDirectoryPack(frontEnd, access, candidate);
        var catalog = loaded.configuration().options().catalog();
        var defaultState = loaded.configuration().options().state();

        // read first: persisted baseline applies through the codec
        OptionPersistenceCodec codec = services.optionPersistence();
        var read = assertInstanceOf(OptionPersistenceReadResult.Completed.class,
            codec.read(new OptionPersistenceReadRequest(access, target, catalog,
                defaultState, NOOP)));
        assertEquals(PersistenceReadStatus.APPLIED, read.status());

        // write: the bundle-issued catalog/state pair commits through the same credentials
        var write = assertInstanceOf(OptionPersistenceWriteResult.class,
            codec.write(new OptionPersistenceWriteRequest(access, target, catalog,
                read.state(), NOOP)));
        assertEquals(PersistenceWriteStatus.COMMITTED, write.status());
        assertTrue(Files.isRegularFile(root.resolve("realPack.txt")),
            "the persistence file must be a regular file beside the pack folder");

        // read-back still applies (idempotent baseline)
        var reread = assertInstanceOf(OptionPersistenceReadResult.Completed.class,
            codec.read(new OptionPersistenceReadRequest(access, target, catalog,
                defaultState, NOOP)));
        assertEquals(PersistenceReadStatus.APPLIED, reread.status());
    }

    @Test
    void zipPackTargetKeepsArchiveExtensionAndAppendsTxt() throws Exception {
        Path zip = root.resolve("zipPack.zip");
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip))) {
            out.putNextEntry(new ZipEntry("shaders/composite.fsh"));
            out.write("#version 120\nvoid main() {}\n".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
        PackFrontEndServices services = PackFrontEnds.create();
        PackFrontEnd frontEnd = services.frontEnd();
        PackCandidate candidate = candidate(frontEnd, "zipPack.zip");

        PackOptionsTarget target = assertInstanceOf(PackOptionsTargetAcquisition.Acquired.class,
            frontEnd.packOptionsTarget(candidate.id())).target();
        // archive extensions are preserved: exact host name plus .txt
        assertEquals("zipPack.zip.txt", target.fileName());

        PersistenceFileAccess access = switch (services.persistenceFiles(
            new PersistenceRootConfiguration(root, root))) {
            case PersistenceFileAccessAcquisition.Acquired a -> a.files();
            case PersistenceFileAccessAcquisition.InvalidRoots f -> throw new AssertionError(
                "roots must be valid directories: " + f.failure());
        };
        PackLoadResult.Loaded loaded = loadDirectoryPack(frontEnd, access, candidate);
        var catalog = loaded.configuration().options().catalog();
        var write = assertInstanceOf(OptionPersistenceWriteResult.class,
            services.optionPersistence().write(new OptionPersistenceWriteRequest(access,
                target, catalog, loaded.configuration().options().state(), NOOP)));
        assertEquals(PersistenceWriteStatus.COMMITTED, write.status());
        assertTrue(Files.isRegularFile(root.resolve("zipPack.zip.txt")),
            "the zip pack's options file must not collide with the archive itself");
    }
}
