// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * The section 4.7 attachment directives through the one shared buffer-name normalizer
 * (PHASE_3_DOC §3.3 :1827-1830): canonical and legacy spellings, the clear/clear-colour
 * family filter, and the per-program mipmap request.
 */
class ColorAttachmentDirectiveTest {

    private static final Set<String> SUFFIXES =
        Set.of("Format", "Clear", "ClearColor", "MipmapEnabled");

    private static ShaderPropertiesModel properties() {
        return new ShaderPropertiesModel(EngineFlags.allDefault(), List.of(), List.of(),
            List.of(), new NoiseTextureSpec.Generated(), List.of(),
            new ProgramStateModel(Map.of()), List.of());
    }

    private static Map<String, ConstScanner.Finding> consts(String source) {
        return ConstScanner.scan(source);
    }

    @Test
    void bufferNameNormalizerAcceptsAllParserFamilies() {
        assertEquals(0, ColorBufferNames.normalize("colortex0"));
        assertEquals(0, ColorBufferNames.normalize("gcolor"));
        assertEquals(1, ColorBufferNames.normalize("gdepth"));
        assertEquals(2, ColorBufferNames.normalize("gnormal"));
        assertEquals(3, ColorBufferNames.normalize("composite"));
        assertEquals(4, ColorBufferNames.normalize("gaux1"));
        assertEquals(7, ColorBufferNames.normalize("gaux4"));
        assertNull(ColorBufferNames.normalize("colortex8"));
        assertNull(ColorBufferNames.normalize("gaux5"));
    }

    @Test
    void scopedSplitsCanonicalAndLegacySpellingsAndRejectsUnknownSuffixes() {
        ColorBufferNames.ScopedDirective canonical =
            ColorBufferNames.scoped("colortex6Format", SUFFIXES);
        assertEquals(6, canonical.index());
        assertEquals("Format", canonical.suffix());
        assertTrue(canonical.canonical());

        ColorBufferNames.ScopedDirective legacy =
            ColorBufferNames.scoped("gaux3MipmapEnabled", SUFFIXES);
        assertEquals(6, legacy.index());
        assertEquals("MipmapEnabled", legacy.suffix());
        assertFalse(legacy.canonical());

        // A pack's own const that merely starts with a buffer name is not a directive.
        assertNull(ColorBufferNames.scoped("compositeStrength", SUFFIXES));
        assertNull(ColorBufferNames.scoped("composite", SUFFIXES));
        assertNull(ColorBufferNames.scoped("gaux3Format", Set.of("Clear")));
    }

    @Test
    void legacyFormatAliasesBecomeExplicitAttachmentFormats() {
        // Exactly the shape SEUS Renewed ships: the zeroed token consts it declares to
        // keep the GLSL compiler happy, then the legacy-name format directives.
        String source = """
            const int RGB8 = 0;
            const int RGBA16 = 0;
            const int gcolorFormat = RGB8;
            const int gaux3Format = RGBA16;
            """;
        ResourceRequirements requirements =
            ResourceRequirementsBuilder.build(properties(), consts(source));

        ColorAttachmentRequirement colortex0 =
            requirements.colorAttachments().get(new ColorAttachmentKey(0));
        assertEquals(ColorInternalFormat.RGB8,
            assertInstanceOf(ColorAttachmentFormat.Explicit.class, colortex0.format()).format());

        ColorAttachmentRequirement colortex6 =
            requirements.colorAttachments().get(new ColorAttachmentKey(6));
        assertEquals(ColorInternalFormat.RGBA16,
            assertInstanceOf(ColorAttachmentFormat.Explicit.class, colortex6.format()).format());
    }

    @Test
    void absentFormatStaysDistinctFromAnExplicitOne() {
        ResourceRequirements requirements = ResourceRequirementsBuilder.build(properties(),
            consts("const int gaux1Format = RGBA16;\n"));
        assertTrue(requirements.colorAttachments()
            .containsKey(new ColorAttachmentKey(4)));
        // No directive for colortex5: it must not acquire a requirement at all, so the
        // estate allocates its documented default rather than an invented explicit one.
        assertFalse(requirements.colorAttachments()
            .containsKey(new ColorAttachmentKey(5)));
    }

    @Test
    void canonicalSpellingWinsOverALegacyAliasForTheSameAttachment() {
        String source = """
            const int gaux3Format = RGBA16;
            const int colortex6Format = RGB8;
            """;
        ResourceRequirements requirements =
            ResourceRequirementsBuilder.build(properties(), consts(source));
        assertEquals(ColorInternalFormat.RGB8,
            assertInstanceOf(ColorAttachmentFormat.Explicit.class,
                requirements.colorAttachments().get(new ColorAttachmentKey(6)).format())
                .format());
    }

    @Test
    void clearFamilyFilterHonoursOnlyDeferredAndCompositeSources() {
        Map<String, ConstScanner.Finding> all = consts("""
            const int gaux4Format = RGBA16;
            const bool gaux4Clear = false;
            """);
        // Declared by a composite source: honoured.
        ResourceRequirements honoured =
            ResourceRequirementsBuilder.build(properties(), all, Map.of(), all);
        assertFalse(honoured.colorAttachments().get(new ColorAttachmentKey(7)).clear());

        // The same declaration seen only in a gbuffers source: the format still applies,
        // the clear directive does not.
        ResourceRequirements filtered =
            ResourceRequirementsBuilder.build(properties(), all, Map.of(), Map.of());
        assertTrue(filtered.colorAttachments().get(new ColorAttachmentKey(7)).clear());
    }

    @Test
    void clearColourOverrideSurvivesTheNormalizer() {
        Map<String, ConstScanner.Finding> all = consts("""
            const int colortex2Format = RGBA16;
            const vec4 gnormalClearColor = vec4(0.25, 0.5, 0.75, 1.0);
            """);
        ResourceRequirements requirements =
            ResourceRequirementsBuilder.build(properties(), all, Map.of(), all);
        Vec4f override = requirements.colorAttachments()
            .get(new ColorAttachmentKey(2)).clearColorOverride().orElseThrow();
        assertEquals(0.25f, override.red());
        assertEquals(1.0f, override.alpha());
    }

    @Test
    void programFamiliesGateClearAndMipmapDirectives() {
        assertTrue(ProgramFamilies.isDeferredOrComposite("composite"));
        assertTrue(ProgramFamilies.isDeferredOrComposite("composite15"));
        assertTrue(ProgramFamilies.isDeferredOrComposite("deferred"));
        assertTrue(ProgramFamilies.isDeferredOrComposite("deferred_pre"));
        assertFalse(ProgramFamilies.isDeferredOrComposite("final"));
        assertFalse(ProgramFamilies.isDeferredOrComposite("gbuffers_terrain"));
        assertFalse(ProgramFamilies.isDeferredOrComposite("compositeStrength"));

        assertTrue(ProgramFamilies.isFullscreen("final"));
        assertTrue(ProgramFamilies.isFullscreen("composite3"));
        assertFalse(ProgramFamilies.isFullscreen("shadow"));
    }
}
