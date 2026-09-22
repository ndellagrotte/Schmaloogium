// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.config.ColorAttachmentKey;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.ProgramRequirementKey;
import com.schmaloogium.engine.config.ProgramRequirements;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.preprocess.GeometrySourceRequest;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.preprocess.MaterializationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Section 4.7 attachment directives end to end, in the shape a real classic pack ships
 * them (PHASE_3_DOC §3.3 :1827-1830). SEUS Renewed declares only the legacy buffer names
 * and nothing read them: every attachment fell through to the unsized RGBA fallback, and
 * every {@code gauxNMipmapEnabled} request was dropped, so a composite chain's
 * {@code texture2DLod} auto-exposure silently resolved to level zero.
 */
class LegacyBufferDirectivePackTest {

    private static final DiagnosticReporter NOOP = d -> { };

    @TempDir
    Path root;

    private static GLCapabilityProfile capabilities() {
        return new GLCapabilityProfile(4, 6, "4.60 NVIDIA", "NVIDIA Corporation",
            "NVIDIA GeForce RTX 3070/PCIe/SSE2", 8, 8, 32, 16, 32768, 2048, 32768,
            Set.of("GL_ARB_texture_rectangle"));
    }

    /** The zeroed token consts a classic pack declares to keep the compiler happy. */
    private static final String TOKENS = """
        const int RGB8 = 0;
        const int RGBA8 = 0;
        const int RGBA16 = 0;
        """;

    private static final String BODY =
        "uniform vec4 color;\nvoid main() { gl_FragColor = color; }\n";

    private PackConfiguration load() {
        return assertInstanceOf(PackLoadResult.Loaded.class, loadResult()).configuration();
    }

    private PackLoadResult loadResult() {
        PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
        PackCandidate candidate = frontEnd.discover(new PackDiscoveryRequest(root, null))
            .candidates().stream().filter(c -> c.displayName().equals("legacyPack"))
            .findFirst().orElseThrow();
        PersistenceFileAccess access = switch (PackFrontEnds.create().persistenceFiles(
            new PersistenceRootConfiguration(root, root))) {
            case PersistenceFileAccessAcquisition.Acquired a -> a.files();
            case PersistenceFileAccessAcquisition.InvalidRoots f -> throw new AssertionError(f.failure());
        };
        return frontEnd.load(new PackLoadRequest(
            root, new PackSelection.Filesystem(candidate.id()),
            new RuntimeIdentityData(1, 12, 2, "classic", "1.0.0", OsFamily.LINUX, Map.of()),
            capabilities(), new EngineOptionData(Map.of()),
            new CompanionOptionMacros(false, false), new RendererFeatureData(false, false),
            access, null, Optional.empty(), NOOP));
    }

    private void writePack() throws IOException {
        Path shaders = root.resolve("legacyPack/shaders");
        Files.createDirectories(shaders);
        Files.writeString(shaders.resolve("shaders.properties"), "clouds=off\n");
        // A composite source carrying the legacy format, mipmap and clear directives.
        Files.writeString(shaders.resolve("composite2.fsh"), "#version 120\n" + TOKENS + """
            const int gcolorFormat = RGB8;
            const int gaux3Format = RGBA16;
            const int gaux4Format = RGBA16;
            const bool gaux3MipmapEnabled = true;
            const bool gaux4Clear = false;
            """ + BODY);
        // A gbuffers source: its format still counts, but its clear directive must not —
        // the clear family filter admits deferred and composite only.
        Files.writeString(shaders.resolve("gbuffers_terrain.fsh"), "#version 120\n" + TOKENS + """
            const int gnormalFormat = RGBA8;
            const bool gcolorClear = false;
            """ + BODY);
        Files.writeString(shaders.resolve("final.fsh"), "#version 120\n" + BODY);
    }

    @Test
    void legacyNamesReachTheEstateAndThePerProgramMipmapRequest() throws IOException {
        writePack();
        PackConfiguration cfg = load();
        var attachments = cfg.resources().colorAttachments();

        // Formats: read from either family, through the shared normalizer.
        assertEquals("RGB8", assertInstanceOf(ColorAttachmentFormat.Explicit.class,
            attachments.get(new ColorAttachmentKey(0)).format()).format().name());
        assertEquals("RGBA16", assertInstanceOf(ColorAttachmentFormat.Explicit.class,
            attachments.get(new ColorAttachmentKey(6)).format()).format().name());
        assertEquals("RGBA8", assertInstanceOf(ColorAttachmentFormat.Explicit.class,
            attachments.get(new ColorAttachmentKey(2)).format()).format().name());

        // gaux4Clear=false came from a composite source: honoured.
        assertFalse(attachments.get(new ColorAttachmentKey(7)).clear(),
            "a composite source's clear directive is honoured");
        // gcolorClear=false came from gbuffers: filtered out, so the default clear stands.
        assertTrue(attachments.get(new ColorAttachmentKey(0)).clear(),
            "a gbuffers source's clear directive is not estate-wide");
    }

    @Test
    void mipmapRequestReachesEveryFullscreenReader() throws IOException {
        writePack();
        PackConfiguration cfg = load();
        Map<ProgramRequirementKey, ProgramRequirements> programs = cfg.resources().programs();

        ProgramRequirements composite2 = programs.entrySet().stream()
            .filter(e -> e.getKey().programName().equals("composite2"))
            .map(Map.Entry::getValue).findFirst().orElseThrow();
        assertEquals(Set.of(new ColorAttachmentKey(6)), composite2.mipmappedAfterPass(),
            "gaux3MipmapEnabled=true is colortex6, and the set must survive construction");

        // D-P3-FIXUP: the request reaches every fullscreen reader, not only the declaring
        // program. A classic pack declares the directive in one composite and then writes
        // that buffer from a later one before final samples its mip chain, so a strictly
        // per-program request could never resolve above level zero.
        ProgramRequirements finalProgram = programs.entrySet().stream()
            .filter(e -> e.getKey().programName().equals("final"))
            .map(Map.Entry::getValue).findFirst().orElseThrow();
        assertEquals(Set.of(new ColorAttachmentKey(6)), finalProgram.mipmappedAfterPass(),
            "final declares no directive of its own but still reads the mipmapped buffer");

        // gbuffers_terrain is outside the family: it never acquires the request.
        programs.entrySet().stream()
            .filter(e -> e.getKey().programName().equals("gbuffers_terrain"))
            .map(Map.Entry::getValue).findFirst()
            .ifPresent(r -> assertTrue(r.mipmappedAfterPass().isEmpty()));
    }

    @Test
    void connectedIncludesPreserveResourceRequirementsWithoutScanningOrphans() throws IOException {
        writePack();
        Path shaders = root.resolve("legacyPack/shaders");
        Files.writeString(shaders.resolve("gbuffers_terrain.vsh"), """
            #version 120
            attribute vec4 mc_Entity;
            attribute vec4 at_tangent;
            void main() { gl_Position = gl_Vertex; }
            """);
        Files.writeString(shaders.resolve("composite2.fsh"), """
            const int gcolorFormat = RGB8;
            const bool gaux3MipmapEnabled = true;
            const int countInstances = 3;
            /* DRAWBUFFERS:06 */
            """ + BODY);
        var direct = load().resources();
        assertEquals(Set.of(com.schmaloogium.engine.config.VertexAttribute.MC_ENTITY,
                com.schmaloogium.engine.config.VertexAttribute.AT_TANGENT),
            direct.programs().entrySet().stream()
                .filter(e -> e.getKey().programName().equals("gbuffers_terrain"))
                .map(e -> e.getValue().vertices().attributes()).findFirst().orElseThrow());

        Path library = Files.createDirectories(shaders.resolve("lib/nested"));
        for (String name : java.util.List.of("gbuffers_terrain.vsh", "gbuffers_terrain.fsh",
                "composite2.fsh", "final.fsh")) {
            Files.move(shaders.resolve(name), library.resolve(name + ".glsl"));
            Files.writeString(shaders.resolve("lib").resolve(name + ".glsl"),
                "#include \"nested/" + name + ".glsl\"\n");
            Files.writeString(shaders.resolve(name), "#include \"/lib/" + name + ".glsl\"\n");
        }
        Files.writeString(shaders.resolve("lib/orphan.glsl"), """
            const int colortex0Format = RGBA32F;
            const int countInstances = 19;
            attribute vec4 mc_midTexCoord;
            /* DRAWBUFFERS:7 */
            """);
        assertEquals(direct, load().resources(),
            "moving the same stage source into nested includes preserves its resource contract");
    }

    @Test
    void nestedConditionalsExposeOnlyActiveResourceRequirements() throws IOException {
        writePack();
        Path shaders = root.resolve("legacyPack/shaders");
        String active = """
            const int gcolorFormat = RGB8;
            const bool gaux3MipmapEnabled = true;
            const bool gaux4Clear = false;
            const int countInstances = 3;
            const int shadowMapResolution = 2048;
            /* DRAWBUFFERS:06 */
            """;
        String vertex = "attribute vec4 at_tangent;\n";
        Files.writeString(shaders.resolve("composite2.fsh"), "#version 120\n" + active + BODY);
        Files.writeString(shaders.resolve("gbuffers_terrain.vsh"), "#version 120\n" + vertex
            + "void main() { gl_Position = gl_Vertex; }\n");
        var expected = load().resources();
        assertEquals(2048, expected.shadow().resolution());
        var composite = expected.programs().entrySet().stream()
            .filter(e -> e.getKey().programName().equals("composite2"))
            .map(Map.Entry::getValue).findFirst().orElseThrow();
        assertEquals(3, composite.instanceCount());
        assertEquals(new com.schmaloogium.engine.config.DrawRouting.Explicit(java.util.List.of(
                new com.schmaloogium.engine.config.DrawSlot.Attachment(new ColorAttachmentKey(0)),
                new com.schmaloogium.engine.config.DrawSlot.Attachment(new ColorAttachmentKey(6)))),
            composite.routing(), "active DRAWBUFFERS comments must survive preprocessing");

        Path nested = Files.createDirectories(shaders.resolve("lib/nested"));
        Files.writeString(shaders.resolve("lib/resources.glsl"),
            "#include \"nested/resources.glsl\"\n");
        Files.writeString(nested.resolve("resources.glsl"), """
            #if MC_VERSION < 11202
            const int gcolorFormat = RGBA32F;
            const bool gaux3MipmapEnabled = false;
            const bool gaux4Clear = true;
            const int countInstances = 19;
            const int shadowMapResolution = 4096;
            /* DRAWBUFFERS:7 */
            #else
            #if __VERSION__ == 120 && defined(MC_GL_VENDOR_NVIDIA) && !defined(MC_NORMAL_MAP)
            """ + active + """
            #else
            const int gcolorFormat = RGBA16F;
            const int countInstances = 11;
            const int shadowMapResolution = 8192;
            /* DRAWBUFFERS:3 */
            #endif
            #endif
            """);
        Files.writeString(shaders.resolve("composite2.fsh"),
            "#version 120\n#include \"/lib/resources.glsl\"\n" + BODY);
        Files.writeString(nested.resolve("attributes.glsl"), """
            #if defined(MC_NORMAL_MAP)
            attribute vec4 mc_Entity;
            #else
            #if defined(__VERSION__) && __VERSION__ > 0
            """ + vertex + """
            #else
            attribute vec4 mc_midTexCoord;
            #endif
            #endif
            """);
        Files.writeString(shaders.resolve("gbuffers_terrain.vsh"),
            "#include \"/lib/nested/attributes.glsl\"\n"
                + "void main() { gl_Position = gl_Vertex; }\n");
        assertEquals(expected, load().resources(),
            "inactive directives must not size, route or declare resources; active comments survive");
    }

    @Test
    void preprocessingErrorCannotPublishPartialResourceRequirements() throws IOException {
        writePack();
        Path shaders = root.resolve("legacyPack/shaders");
        Files.writeString(shaders.resolve("composite2.fsh"), "#version 120\n" + BODY);
        var expected = load().resources();
        Path library = Files.createDirectories(shaders.resolve("lib"));
        Files.writeString(shaders.resolve("composite2.fsh"),
            "#version 120\n#include \"/lib/broken.glsl\"\n" + BODY);
        Files.writeString(library.resolve("broken.glsl"), """
            const int shadowMapResolution = 4096;
            /* DRAWBUFFERS:7 */
            #error resource preprocessing must fail
            const int countInstances = 19;
            """);
        PackConfiguration configuration = load();
        assertEquals(expected, configuration.resources(),
            "an errored root contributes neither its prefix nor its suffix directives");
        assertCompositeUnavailableAndFinalAvailable(configuration,
            "schmaloogium.error.preprocess.failed");
    }

    @Test
    void resourceExpansionDepthFailureCannotProduceDefaultRequirements() throws IOException {
        writePack();
        Path shaders = root.resolve("legacyPack/shaders");
        Files.writeString(shaders.resolve("composite2.fsh"), "#version 120\n" + BODY);
        var expected = load().resources();
        Path library = Files.createDirectories(shaders.resolve("lib"));
        Files.writeString(shaders.resolve("composite2.fsh"), "#include \"/lib/depth0.glsl\"\n");
        for (int depth = 0; depth < 11; depth++) {
            Files.writeString(library.resolve("depth" + depth + ".glsl"),
                "#include \"depth" + (depth + 1) + ".glsl\"\n");
        }
        Files.writeString(library.resolve("depth11.glsl"),
            "const int colortex0Format = RGBA32F;\n" + BODY);
        PackConfiguration configuration = load();
        assertEquals(expected, configuration.resources(),
            "an unexpanded root contributes no directives while unrelated roots remain usable");
        assertCompositeUnavailableAndFinalAvailable(configuration,
            "schmaloogium.error.source.include_depth");
    }

    private static void assertCompositeUnavailableAndFinalAvailable(
            PackConfiguration configuration, String diagnosticKey) {
        var sources = configuration.sources();
        var composite = sources.roots().stream()
            .filter(root -> root.programName().equals("composite2"))
            .findFirst().orElseThrow();
        var unavailable = assertInstanceOf(MaterializationResult.Unavailable.class,
            sources.materializer().materialize(composite, new MacroContribution.Empty(),
                new GeometrySourceRequest.None()));
        assertTrue(unavailable.diagnostics().stream()
            .anyMatch(d -> d.messageKey().equals(diagnosticKey)));
        var finalRoot = sources.roots().stream()
            .filter(root -> root.programName().equals("final"))
            .findFirst().orElseThrow();
        assertInstanceOf(MaterializationResult.Available.class,
            sources.materializer().materialize(finalRoot, new MacroContribution.Empty(),
                new GeometrySourceRequest.None()));
    }

    @Test
    void programRequirementsRetainsItsMipmapSetInAscendingOrder() {
        ProgramRequirements requirements = new ProgramRequirements(
            new com.schmaloogium.engine.config.DrawRouting.AllUsed(),
            Set.of(new ColorAttachmentKey(6), new ColorAttachmentKey(1)),
            new com.schmaloogium.engine.config.VertexRequirements(Set.of()),
            1, Optional.empty());
        assertEquals(java.util.List.of(new ColorAttachmentKey(1), new ColorAttachmentKey(6)),
            java.util.List.copyOf(requirements.mipmappedAfterPass()),
            "the compact constructor orders by attachment index and keeps every member");
    }
}
