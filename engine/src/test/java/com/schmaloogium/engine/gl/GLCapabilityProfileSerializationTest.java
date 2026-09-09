// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8.1 row {@code GLCapabilityProfileSerializationTest} (PHASE_1_DOC §4.7.2, D-P1-66):
 * round-trip {@code write} → {@code parse} is identity; output is sorted and
 * deterministic; a hand-written fixture parses to the expected values; and parse rejects
 * missing/duplicate/malformed/negative/overflowing/gate-inconsistent values.
 */
class GLCapabilityProfileSerializationTest {

    private static final String FIXTURE = "/profiles/handwritten-example.profile";

    private static GLCapabilityProfile sample() {
        return new GLCapabilityProfile(
                4, 6, "4.60 NVIDIA", "NVIDIA Corporation", "NVIDIA GeForce RTX 3070/PCIe/SSE2",
                8, 8, 32, 16, 32768, 2048, 32768,
                Set.of("GL_ARB_debug_output", "GL_ARB_sampler_objects", "GL_ARB_texture_rectangle"));
    }

    private static String write(GLCapabilityProfile profile) throws IOException {
        StringWriter out = new StringWriter();
        profile.write(out);
        return out.toString();
    }

    private static GLCapabilityProfile parse(String text) throws IOException {
        return GLCapabilityProfile.parse(new StringReader(text));
    }

    /** A GL 2.1 profile without GL_ARB_texture_rectangle: rectangle zero is legal, 3D is not. */
    private static String gl21Canonical() throws IOException {
        return write(new GLCapabilityProfile(
                2, 1, "2.1 Mesa 20.3.5", "Mesa/X.org", "llvmpipe (LLVM 11.0.0)",
                8, 8, 16, 16, 16384, 2048, 0,
                Set.of()));
    }

    @Test
    void roundTripIsIdentityAndByteIdentical() throws IOException {
        GLCapabilityProfile profile = sample();
        String once = write(profile);
        GLCapabilityProfile parsed = parse(once);
        assertEquals(profile, parsed, "parse(write(p)) must equal p");
        assertEquals(once, write(parsed), "second write must be byte-identical");
    }

    @Test
    void outputIsSortedAndDeterministic() throws IOException {
        String first = write(sample());
        String second = write(sample());
        assertEquals(first, second, "write must be deterministic");

        assertFalse(first.contains("\r"), "canonical form uses LF endings only");

        List<String> scalarKeys = new ArrayList<>();
        boolean extensionsSeen = false;
        for (String line : first.split("\n")) {
            if (line.equals("extensions =")) {
                extensionsSeen = true;
                continue;
            }
            if (extensionsSeen) {
                assertTrue(line.startsWith("  "), "extension lines are two-space indented: " + line);
                continue;
            }
            scalarKeys.add(line.substring(0, line.indexOf(" = ")));
        }
        List<String> sorted = new ArrayList<>(scalarKeys);
        java.util.Collections.sort(sorted);
        assertEquals(sorted, scalarKeys, "scalar keys must be lexicographically sorted");
        assertEquals(Set.of(
                "gl.version", "glsl.version", "vendor", "renderer",
                "max.drawBuffers", "max.colorAttachments", "max.textureImageUnits",
                "max.vertexAttribs", "max.textureSize", "max.3DTextureSize",
                "max.rectangleTextureSize"), Set.copyOf(scalarKeys), "mandatory exact keys");
        assertTrue(first.indexOf("extensions =") > first.indexOf("max.rectangleTextureSize ="),
                "the sorted extensions block comes after all scalars");
        assertTrue(first.endsWith("\n"), "the document closes with a newline");
        assertTrue(extensionsSeen, "the extensions header must be present");
        assertTrue(first.indexOf("extensions =") > first.indexOf("max.rectangleTextureSize ="),
                "the sorted extensions block comes after all scalars");
    }

    @Test
    void handWrittenFixtureParsesToExpectedValues() throws IOException {
        String fixtureText;
        try (InputStream in = GLCapabilityProfileSerializationTest.class.getResourceAsStream(FIXTURE)) {
            assertTrue(in != null, FIXTURE + " must be on the test classpath (testFixtures resources)");
            fixtureText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        GLCapabilityProfile profile = parse(fixtureText);

        assertAll(
                () -> assertEquals(4, profile.glVersionMajor()),
                () -> assertEquals(6, profile.glVersionMinor()),
                () -> assertEquals("4.60 NVIDIA", profile.glslVersion()),
                () -> assertEquals("NVIDIA Corporation", profile.vendor()),
                () -> assertEquals("NVIDIA GeForce RTX 3080/PCIe/SSE2", profile.renderer()),
                () -> assertEquals(8, profile.maxDrawBuffers()),
                () -> assertEquals(8, profile.maxColorAttachments()),
                () -> assertEquals(32, profile.maxTextureImageUnits()),
                () -> assertEquals(16, profile.maxVertexAttribs()),
                () -> assertEquals(32768, profile.maxTextureSize()),
                () -> assertEquals(2048, profile.max3DTextureSize()),
                () -> assertEquals(32768, profile.maxRectangleTextureSize()),
                () -> assertEquals(Set.of(
                        "GL_ARB_debug_output", "GL_ARB_sampler_objects",
                        "GL_ARB_texture_rectangle", "GL_KHR_debug"), profile.extensions()),
                () -> assertTrue(profile.supports3DTextures()),
                () -> assertTrue(profile.supportsRectangleTextures()));

        assertEquals(fixtureText, write(profile), "the fixture is canonical: round-trip is byte-identical");
    }

    @Test
    void rejectsMissingKey() throws IOException {
        String text = gl21Canonical().replace("max.vertexAttribs = 16\n", "");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> parse(text));
        assertTrue(e.getMessage().contains("max.vertexAttribs"));
    }

    @Test
    void rejectsDuplicateKey() throws IOException {
        String text = gl21Canonical().replace("vendor = Mesa/X.org\n",
                "vendor = Mesa/X.org\nvendor = Someone Else\n");
        assertThrows(IllegalArgumentException.class, () -> parse(text));
    }

    @Test
    void rejectsNegativeValue() throws IOException {
        String text = gl21Canonical().replace("max.drawBuffers = 8", "max.drawBuffers = -1");
        assertThrows(IllegalArgumentException.class, () -> parse(text));
    }

    @Test
    void rejectsOverflowingValue() throws IOException {
        String text = gl21Canonical().replace("max.textureSize = 16384", "max.textureSize = 2147483648");
        assertThrows(IllegalArgumentException.class, () -> parse(text));
    }

    @Test
    void rejectsMalformedLines() throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> parse(gl21Canonical().replace("max.drawBuffers = 8", "max.drawBuffers=8")),
                "no-padding form without spaces is malformed");
        assertThrows(IllegalArgumentException.class,
                () -> parse(gl21Canonical().replace("vendor = Mesa/X.org", "vendorX = Mesa/X.org")),
                "unknown keys are malformed");
        assertThrows(IllegalArgumentException.class,
                () -> parse(""),
                "empty document is malformed");
        assertThrows(IllegalArgumentException.class,
                () -> parse(gl21Canonical() + "\n"),
                "blank line is malformed");
    }

    @Test
    void rejectsGateInconsistent3D() throws IOException {
        String gateFalse = write(new GLCapabilityProfile(
                1, 1, "1.1 compat", "v", "r", 1, 1, 2, 2, 64, 0, 0, Set.of()));
        String positiveOnGateFalse =
                gateFalse.replace("max.3DTextureSize = 0", "max.3DTextureSize = 2048");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> parse(positiveOnGateFalse));
        assertTrue(e.getMessage().contains("3D"));

        String zeroOnGateTrue = write(sample()).replace("max.3DTextureSize = 2048", "max.3DTextureSize = 0");
        assertThrows(IllegalArgumentException.class, () -> parse(zeroOnGateTrue));
    }

    @Test
    void rejectsGateInconsistentRectangle() throws IOException {
        String positiveOnGateFalse = gl21Canonical()
                .replace("max.rectangleTextureSize = 0", "max.rectangleTextureSize = 16384");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> parse(positiveOnGateFalse));
        assertTrue(e.getMessage().contains("rectangle"));

        String zeroOnGateTrue = write(sample())
                .replace("max.rectangleTextureSize = 32768", "max.rectangleTextureSize = 0");
        assertThrows(IllegalArgumentException.class, () -> parse(zeroOnGateTrue));
    }

    @Test
    void rectangleGateOpensThroughTheExtensionAliases() throws IOException {
        // GL 2.1 + GL_ARB_texture_rectangle: rectangle positive, 3D positive (GL 1.2+).
        GLCapabilityProfile profile = new GLCapabilityProfile(
                2, 1, "2.1 NVIDIA-340.108", "NVIDIA Corporation", "GeForce 8800 GTS/PCIe/SSE2",
                8, 8, 16, 16, 8192, 8192, 8192,
                Set.of("GL_ARB_texture_rectangle"));
        assertEquals(profile, parse(write(profile)),
                "atLeast(3,1) or a texture_rectangle extension admits a positive rectangle maximum");
    }

    @Test
    void extensionsMustComeLastAndBeSortedAndUnique() throws IOException {
        String scalarAfterBlock = gl21Canonical().replace("vendor = Mesa/X.org\n", "")
                + "vendor = Mesa/X.org\n";
        assertThrows(IllegalArgumentException.class, () -> parse(scalarAfterBlock),
                "a scalar after the extensions block is malformed");

        String unsorted = gl21Canonical()
                .replace("extensions =\n", "extensions =\n  GL_ZZZ_extension\n  GL_AAA_extension\n");
        assertThrows(IllegalArgumentException.class, () -> parse(unsorted),
                "extensions must be sorted");

        String duplicated = gl21Canonical()
                .replace("extensions =\n", "extensions =\n  GL_EXT_abgr\n  GL_EXT_abgr\n");
        assertThrows(IllegalArgumentException.class, () -> parse(duplicated),
                "extensions must be unique");
    }

    @Test
    void emptyExtensionSetRoundTrips() throws IOException {
        GLCapabilityProfile profile = new GLCapabilityProfile(
                2, 1, "2.1", "v", "r", 1, 1, 8, 8, 64, 64, 0, Set.of());
        String text = write(profile);
        assertTrue(text.endsWith("extensions =\n"));
        assertEquals(profile, parse(text));
    }
}
