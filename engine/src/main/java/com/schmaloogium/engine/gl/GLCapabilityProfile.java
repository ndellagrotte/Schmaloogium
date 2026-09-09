// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Immutable value object: the whole GL-side capability picture (PHASE_1_DOC §4.7.2). The
 * single most-consumed type this phase produces — Phase 2 replays recorded profiles,
 * Phases 4/5/6 gate on it, and Phase 3 derives {@code MC_<GL_extension>} macros from it.
 * It is the whole GL-side input to Phase 3's standard macro header.
 *
 * <p>{@code extensions} is defensively copied and exposed unmodifiable — a record's
 * component accessor would otherwise hand out a mutable set.
 *
 * <p>Target limits ([D-P1-66], D-P1-66): the native probe admits 3D on
 * {@link #atLeast}(1,2) and rectangle on {@link #atLeast}(3,1) or any of
 * {@code GL_ARB_texture_rectangle} / {@code GL_EXT_texture_rectangle} /
 * {@code GL_NV_texture_rectangle}. These are target-support gates, not inferred maxima; a
 * false gate produces exactly zero. No maximum is guessed from another limit, a version or
 * an extension string.
 *
 * <p>Serialization ([D-P1-16], D-P1-66): a stable, human-readable, diff-friendly text
 * form — a sorted {@code key = value} properties document with {@code extensions} as a
 * sorted newline-delimited block. {@link #write(Writer)} produces the canonical form
 * (scalar keys lexicographically sorted, exactly {@code key = value} with LF endings, no
 * padding, then the sorted {@code extensions =} block);
 * {@link #parse(Reader)} accepts it and rejects missing, duplicate, malformed, negative,
 * overflowing or gate-inconsistent values — never substituting defaults. Round-trip is
 * byte-identical.
 */
public record GLCapabilityProfile(
        int glVersionMajor,
        int glVersionMinor,
        String glslVersion,          // as reported by GL_SHADING_LANGUAGE_VERSION
        String vendor,               // GL_VENDOR
        String renderer,             // GL_RENDERER
        int maxDrawBuffers,          // GL_MAX_DRAW_BUFFERS
        int maxColorAttachments,     // GL_MAX_COLOR_ATTACHMENTS
        int maxTextureImageUnits,    // GL_MAX_TEXTURE_IMAGE_UNITS
        int maxVertexAttribs,        // GL_MAX_VERTEX_ATTRIBS
        int maxTextureSize,          // GL_MAX_TEXTURE_SIZE
        int max3DTextureSize,        // GL_MAX_3D_TEXTURE_SIZE; zero only unsupported
        int maxRectangleTextureSize, // GL_MAX_RECTANGLE_TEXTURE_SIZE; zero only unsupported
        Set<String> extensions) {

    public GLCapabilityProfile {
        extensions = Set.copyOf(extensions);
    }

    public boolean atLeast(int major, int minor) {
        return glVersionMajor > major || (glVersionMajor == major && glVersionMinor >= minor);
    }

    public boolean hasExtension(String name) {
        return extensions.contains(name);
    }

    /** RESEARCH.md §4.1: "mipmap gen requires GL 3.0". */
    public boolean supportsMipmapGeneration() {
        return atLeast(3, 0);
    }

    /** True when the native probe may query/use 3D texture targets ([D-P1-66]). */
    public boolean supports3DTextures() {
        return atLeast(1, 2);
    }

    /** True when the native probe may query/use rectangle texture targets ([D-P1-66]). */
    public boolean supportsRectangleTextures() {
        return atLeast(3, 1)
                || hasExtension("GL_ARB_texture_rectangle")
                || hasExtension("GL_EXT_texture_rectangle")
                || hasExtension("GL_NV_texture_rectangle");
    }

    // ------------------------------------------------------------------
    // Serialization (PHASE_1_DOC §4.7.2, D-P1-66)
    // ------------------------------------------------------------------

    private static final String KEY_GL_VERSION = "gl.version";
    private static final String KEY_GLSL_VERSION = "glsl.version";
    private static final String KEY_VENDOR = "vendor";
    private static final String KEY_RENDERER = "renderer";
    private static final String KEY_MAX_DRAW_BUFFERS = "max.drawBuffers";
    private static final String KEY_MAX_COLOR_ATTACHMENTS = "max.colorAttachments";
    private static final String KEY_MAX_TEXTURE_IMAGE_UNITS = "max.textureImageUnits";
    private static final String KEY_MAX_VERTEX_ATTRIBS = "max.vertexAttribs";
    private static final String KEY_MAX_TEXTURE_SIZE = "max.textureSize";
    private static final String KEY_MAX_3D_TEXTURE_SIZE = "max.3DTextureSize";
    private static final String KEY_MAX_RECTANGLE_TEXTURE_SIZE = "max.rectangleTextureSize";
    private static final String KEY_EXTENSIONS = "extensions";

    private static final Set<String> KNOWN_KEYS = Set.of(
            KEY_GL_VERSION,
            KEY_GLSL_VERSION,
            KEY_VENDOR,
            KEY_RENDERER,
            KEY_MAX_DRAW_BUFFERS,
            KEY_MAX_COLOR_ATTACHMENTS,
            KEY_MAX_TEXTURE_IMAGE_UNITS,
            KEY_MAX_VERTEX_ATTRIBS,
            KEY_MAX_TEXTURE_SIZE,
            KEY_MAX_3D_TEXTURE_SIZE,
            KEY_MAX_RECTANGLE_TEXTURE_SIZE);

    private static final Pattern UNSIGNED_INT = Pattern.compile("[0-9]+");
    private static final Pattern GL_VERSION = Pattern.compile("([0-9]+)\\.([0-9]+)");

    /** Writes the canonical form ([D-P1-66]): scalar keys sorted lexicographically, exactly
     *  {@code key = value} with LF endings, no padding, then the sorted, two-space-indented
     *  {@code extensions =} block. Round-trips through {@link #parse(Reader)}
     *  byte-identically. Does not close {@code out}. */
    public void write(Writer out) throws IOException {
        Map<String, String> scalars = new TreeMap<>();
        scalars.put(KEY_GL_VERSION, glVersionMajor + "." + glVersionMinor);
        scalars.put(KEY_GLSL_VERSION, glslVersion);
        scalars.put(KEY_MAX_COLOR_ATTACHMENTS, Integer.toString(maxColorAttachments));
        scalars.put(KEY_MAX_DRAW_BUFFERS, Integer.toString(maxDrawBuffers));
        scalars.put(KEY_MAX_RECTANGLE_TEXTURE_SIZE, Integer.toString(maxRectangleTextureSize));
        scalars.put(KEY_MAX_TEXTURE_IMAGE_UNITS, Integer.toString(maxTextureImageUnits));
        scalars.put(KEY_MAX_TEXTURE_SIZE, Integer.toString(maxTextureSize));
        scalars.put(KEY_MAX_VERTEX_ATTRIBS, Integer.toString(maxVertexAttribs));
        scalars.put(KEY_MAX_3D_TEXTURE_SIZE, Integer.toString(max3DTextureSize));
        scalars.put(KEY_RENDERER, renderer);
        scalars.put(KEY_VENDOR, vendor);

        StringBuilder sb = new StringBuilder(512);
        for (Map.Entry<String, String> entry : scalars.entrySet()) {
            sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
        }
        sb.append(KEY_EXTENSIONS).append(" =").append('\n');
        for (String extension : sortedExtensions()) {
            sb.append("  ").append(extension).append('\n');
        }
        out.write(sb.toString());
        out.flush();
    }

    /** Parses the canonical form, rejecting missing, duplicate, malformed, negative,
     *  overflowing or gate-inconsistent values ([D-P1-66]) — never substituting defaults.
     *  The mandatory exact, case-sensitive keys include {@code max.3DTextureSize} and
     *  {@code max.rectangleTextureSize}; zero is legal iff the corresponding target gate
     *  is false. Throws {@link IllegalArgumentException} on any violation. Does not close
     *  {@code in}. */
    public static GLCapabilityProfile parse(Reader in) throws IOException {
        BufferedReader reader = (in instanceof BufferedReader buffered) ? buffered : new BufferedReader(in);

        Map<String, String> scalars = new TreeMap<>();
        List<String> extensions = null;
        String previousExtension = null;

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                throw malformed("blank line");
            }
            if (extensions != null) {
                if (!line.startsWith("  ") || line.length() == 2 || line.charAt(2) == ' ') {
                    throw malformed("extension lines are exactly two spaces plus a name: \"" + line + "\"");
                }
                String extension = line.substring(2);
                if (previousExtension != null && extension.compareTo(previousExtension) <= 0) {
                    throw malformed("extensions must be unique and sorted; got \"" + extension + "\" after \""
                            + previousExtension + "\"");
                }
                extensions.add(extension);
                previousExtension = extension;
                continue;
            }
            if (line.equals(KEY_EXTENSIONS + " =")) {
                if (extensions != null) {
                    throw malformed("duplicate extensions block");
                }
                extensions = new ArrayList<>();
                continue;
            }
            int separator = line.indexOf(" = ");
            if (separator <= 0) {
                throw malformed("expected \"key = value\": \"" + line + "\"");
            }
            String key = line.substring(0, separator);
            String value = line.substring(separator + 3);
            if (key.equals(KEY_EXTENSIONS)) {
                throw malformed("the extensions header carries no value: \"" + line + "\"");
            }
            if (!KNOWN_KEYS.contains(key)) {
                throw malformed("unknown key \"" + key + "\"");
            }
            if (scalars.put(key, value) != null) {
                throw malformed("duplicate key \"" + key + "\"");
            }
        }

        if (extensions == null) {
            throw malformed("missing extensions block");
        }
        if (scalars.size() != KNOWN_KEYS.size()) {
            List<String> missing = new ArrayList<>(KNOWN_KEYS);
            missing.removeAll(scalars.keySet());
            throw malformed("missing keys " + missing);
        }

        java.util.regex.Matcher version = GL_VERSION.matcher(require(scalars, KEY_GL_VERSION));
        if (!version.matches()) {
            throw malformed(KEY_GL_VERSION + " must be major.minor: \"" + scalars.get(KEY_GL_VERSION) + "\"");
        }
        int major = parseNonNegativeInt(KEY_GL_VERSION, version.group(1));
        int minor = parseNonNegativeInt(KEY_GL_VERSION, version.group(2));

        int max3D = parseNonNegativeInt(KEY_MAX_3D_TEXTURE_SIZE, require(scalars, KEY_MAX_3D_TEXTURE_SIZE));
        int maxRectangle = parseNonNegativeInt(KEY_MAX_RECTANGLE_TEXTURE_SIZE,
                require(scalars, KEY_MAX_RECTANGLE_TEXTURE_SIZE));
        int maxTextureSize = parseNonNegativeInt(KEY_MAX_TEXTURE_SIZE, require(scalars, KEY_MAX_TEXTURE_SIZE));
        if (maxTextureSize == 0) {
            throw malformed(KEY_MAX_TEXTURE_SIZE + " must stay positive");
        }

        GLCapabilityProfile candidate = new GLCapabilityProfile(
                major,
                minor,
                requireNonEmpty(scalars, KEY_GLSL_VERSION),
                requireNonEmpty(scalars, KEY_VENDOR),
                requireNonEmpty(scalars, KEY_RENDERER),
                parseNonNegativeInt(KEY_MAX_DRAW_BUFFERS, require(scalars, KEY_MAX_DRAW_BUFFERS)),
                parseNonNegativeInt(KEY_MAX_COLOR_ATTACHMENTS, require(scalars, KEY_MAX_COLOR_ATTACHMENTS)),
                parseNonNegativeInt(KEY_MAX_TEXTURE_IMAGE_UNITS, require(scalars, KEY_MAX_TEXTURE_IMAGE_UNITS)),
                parseNonNegativeInt(KEY_MAX_VERTEX_ATTRIBS, require(scalars, KEY_MAX_VERTEX_ATTRIBS)),
                maxTextureSize,
                max3D,
                maxRectangle,
                Set.copyOf(extensions));

        // Gate consistency ([D-P1-66]): zero legal iff the gate is false.
        if ((candidate.max3DTextureSize() > 0) != candidate.supports3DTextures()) {
            throw malformed(KEY_MAX_3D_TEXTURE_SIZE + " = " + max3D + " contradicts the 3D gate of "
                    + candidate.glVersionMajor() + "." + candidate.glVersionMinor());
        }
        if ((candidate.maxRectangleTextureSize() > 0) != candidate.supportsRectangleTextures()) {
            throw malformed(KEY_MAX_RECTANGLE_TEXTURE_SIZE + " = " + maxRectangle
                    + " contradicts the rectangle gate of " + candidate.glVersionMajor() + "."
                    + candidate.glVersionMinor() + " and its extension set");
        }
        return candidate;
    }

    private List<String> sortedExtensions() {
        return List.copyOf(new TreeSet<>(extensions()));
    }

    private static String require(Map<String, String> scalars, String key) {
        String value = scalars.get(key);
        if (value == null) {
            throw malformed("missing key \"" + key + "\"");
        }
        return value;
    }

    private static String requireNonEmpty(Map<String, String> scalars, String key) {
        String value = require(scalars, key);
        if (value.isEmpty()) {
            throw malformed(key + " must not be empty");
        }
        return value;
    }

    private static int parseNonNegativeInt(String key, String value) {
        if (!UNSIGNED_INT.matcher(value).matches()) {
            throw malformed(key + " must be a base-10 nonnegative integer: \"" + value + "\"");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw malformed(key + " overflows the int range: \"" + value + "\"");
        }
    }

    private static IllegalArgumentException malformed(String why) {
        return new IllegalArgumentException("GLCapabilityProfile text form is not canonical: " + why);
    }
}
