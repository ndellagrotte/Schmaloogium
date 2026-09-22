// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.CustomTextureSpec;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Declared {@code texture.*} sources must reach Phase 13 as paths its same-load
 * {@link PackAssetSnapshot} can actually acquire. The declaration value is relative to
 * the shaders root, while snapshot keys carry that root as a prefix: before the fix every
 * custom texture and the {@code texture.noise} override resolved to an undeclared path,
 * so preparation failed and the publication carried no custom or noise source at all.
 */
class PackTextureDeclarationTest {

    private static final DiagnosticReporter NOOP = d -> { };

    @TempDir
    Path root;

    private static final String COMPOSITE =
        "#version 120\nconst int noiseTextureResolution = 128;\n"
            + "uniform vec4 color;\nvoid main() { gl_FragColor = color; }\n";
    private static final String PROPERTIES = """
        texture.noise=lib/textures/noise.png
        texture.deferred.colortex3=lib/textures/cloud-water.png
        """;
    private static final String MCMETA =
        "{\"texture\":{\"blur\":true,\"clamp\":false}}\n";

    @Test
    void declaredTexturesResolveUnderTheShadersRootAndKeepTheirSidecars()
            throws IOException {
        PackConfiguration configuration = load();

        CustomTextureSpec.PackPath custom = assertInstanceOf(CustomTextureSpec.PackPath.class,
            configuration.properties().textures().getFirst());
        assertEquals("shaders/lib/textures/cloud-water.png",
            custom.image().canonicalString());
        assertEquals("shaders/lib/textures/cloud-water.png.mcmeta",
            custom.sidecar().orElseThrow().path().canonicalString());
        // The acquisition Phase 13 performs, on the exact published path.
        assertInstanceOf(PackAssetAcquisition.Acquired.class,
            configuration.assets().acquire(custom.image()));
        assertInstanceOf(PackAssetAcquisition.Acquired.class,
            configuration.assets().acquire(custom.sidecar().orElseThrow().path()));
    }

    @Test
    void noiseOverrideAndItsDirectiveDriveTheNoiseRequirement() throws IOException {
        PackConfiguration configuration = load();

        NoiseTextureSpec.Override override = assertInstanceOf(
            NoiseTextureSpec.Override.class, configuration.properties().noise());
        assertEquals("shaders/lib/textures/noise.png", override.image().canonicalString());
        assertInstanceOf(PackAssetAcquisition.Acquired.class,
            configuration.assets().acquire(override.image()));
        // The declared const supplies both the enabled flag and the resolution.
        assertTrue(configuration.resources().noise().enabled());
        assertEquals(128, configuration.resources().noise().resolution());
    }

    @Test
    void anUndeclaredSidecarIsNeverReferenced() throws IOException {
        PackConfiguration configuration = load();

        // noise.png ships no .mcmeta here: Phase 13 acquires sidecar references
        // unconditionally, so an absent one must not be referenced at all.
        NoiseTextureSpec.Override override = assertInstanceOf(
            NoiseTextureSpec.Override.class, configuration.properties().noise());
        assertTrue(override.sidecar().isEmpty());
    }

    private PackConfiguration load() throws IOException {
        Path shaders = root.resolve("texPack/shaders");
        Files.createDirectories(shaders.resolve("lib/textures"));
        Files.writeString(shaders.resolve("composite.fsh"), COMPOSITE);
        Files.writeString(shaders.resolve("shaders.properties"), PROPERTIES);
        Files.write(shaders.resolve("lib/textures/noise.png"), new byte[] {1, 2, 3, 4});
        Files.write(shaders.resolve("lib/textures/cloud-water.png"),
            new byte[] {5, 6, 7, 8});
        Files.writeString(shaders.resolve("lib/textures/cloud-water.png.mcmeta"), MCMETA);

        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackCandidate candidate = frontEnd.discover(new PackDiscoveryRequest(root, null))
            .candidates().stream().filter(c -> c.displayName().equals("texPack"))
            .findFirst().orElseThrow();
        PersistenceFileAccess access = switch (PackFrontEnds.create().persistenceFiles(
                new PersistenceRootConfiguration(root, root))) {
            case PersistenceFileAccessAcquisition.Acquired a -> a.files();
            case PersistenceFileAccessAcquisition.InvalidRoots f ->
                throw new AssertionError(f.failure());
        };
        return assertInstanceOf(PackLoadResult.Loaded.class,
            frontEnd.load(new PackLoadRequest(root,
                new PackSelection.Filesystem(candidate.id()),
                new RuntimeIdentityData(1, 12, 2, "classic", "1.0.0", OsFamily.LINUX,
                    Map.of()),
                capabilities(), new EngineOptionData(Map.of()),
                new CompanionOptionMacros(false, false),
                new RendererFeatureData(false, false), access, null, Optional.empty(),
                NOOP))).configuration();
    }

    private static GLCapabilityProfile capabilities() {
        return new GLCapabilityProfile(4, 6, "4.60 NVIDIA", "NVIDIA Corporation",
            "NVIDIA GeForce RTX 3070/PCIe/SSE2", 8, 8, 32, 16, 32768, 2048, 32768,
            Set.of("GL_ARB_texture_rectangle"));
    }
}
