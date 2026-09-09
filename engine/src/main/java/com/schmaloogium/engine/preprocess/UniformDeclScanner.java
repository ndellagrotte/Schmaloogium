// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.pack.SourceId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans directive-active processed text for uniform declarations with their
 * structural types and attributed locations (section 4.5 declared-uniform catalog).
 */
public final class UniformDeclScanner {

    private static final Pattern UNIFORM = Pattern.compile(
        "^\\s*uniform\\s+([A-Za-z_][A-Za-z0-9_]*)\\s+([A-Za-z_][A-Za-z0-9_]*)"
            + "\\s*(\\[[0-9]+\\])?\\s*(?::\\s*[a-z]+)?\\s*;.*$");

    private UniformDeclScanner() {
    }

    /** One declaration finding: name, structural type, 1-based line of the text. */
    public record Finding(String name, DeclaredGlslType type, int line) {
    }

    public static List<Finding> scan(String text) {
        List<Finding> out = new ArrayList<>();
        String[] lines = text.split("\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            Matcher m = UNIFORM.matcher(ConstScanner.stripComments(lines[i]));
            if (!m.matches()) {
                continue;
            }
            DeclaredGlslType type = typeOf(m.group(1));
            if (type == null) {
                continue;
            }
            String brackets = m.group(3);
            if (brackets != null) {
                int length = Integer.parseInt(brackets.substring(1, brackets.length() - 1));
                if (length <= 0) {
                    continue;
                }
                type = new DeclaredGlslType.Array(type,
                    List.of(new ArrayExtent.Sized(length)));
            }
            out.add(new Finding(m.group(2), type, i + 1));
        }
        return out;
    }

    /** Closed GLSL declared-type parse; null for every name outside the algebra. */
    static DeclaredGlslType typeOf(String token) {
        return switch (token) {
            case "float" -> new DeclaredGlslType.Scalar(ScalarKind.FLOAT);
            case "double" -> new DeclaredGlslType.Scalar(ScalarKind.DOUBLE);
            case "int" -> new DeclaredGlslType.Scalar(ScalarKind.SIGNED_INT);
            case "uint" -> new DeclaredGlslType.Scalar(ScalarKind.UNSIGNED_INT);
            case "bool" -> new DeclaredGlslType.Scalar(ScalarKind.BOOL);
            case "atomic_uint" -> new DeclaredGlslType.AtomicCounter();
            case "sampler1D" -> sampler(SampledKind.FLOAT, TextureDimension.D1, false, false);
            case "sampler2D" -> sampler(SampledKind.FLOAT, TextureDimension.D2, false, false);
            case "sampler3D" -> sampler(SampledKind.FLOAT, TextureDimension.D3, false, false);
            case "samplerCube" -> sampler(SampledKind.FLOAT, TextureDimension.CUBE, false, false);
            case "sampler1DShadow" -> sampler(SampledKind.FLOAT, TextureDimension.D1, false, true);
            case "sampler2DShadow" -> sampler(SampledKind.FLOAT, TextureDimension.D2, false, true);
            case "samplerCubeShadow" -> sampler(SampledKind.FLOAT, TextureDimension.CUBE, false, true);
            case "sampler1DArray" -> sampler(SampledKind.FLOAT, TextureDimension.D1, true, false);
            case "sampler2DArray" -> sampler(SampledKind.FLOAT, TextureDimension.D2, true, false);
            case "sampler1DArrayShadow" -> sampler(SampledKind.FLOAT, TextureDimension.D1, true, true);
            case "sampler2DArrayShadow" -> sampler(SampledKind.FLOAT, TextureDimension.D2, true, true);
            case "sampler2DRect" -> sampler(SampledKind.FLOAT, TextureDimension.RECTANGLE, false, false);
            case "sampler2DRectShadow" -> sampler(SampledKind.FLOAT, TextureDimension.RECTANGLE, false, true);
            case "samplerBuffer" -> sampler(SampledKind.FLOAT, TextureDimension.BUFFER, false, false);
            case "sampler2DMS" -> sampler(SampledKind.FLOAT, TextureDimension.D2, false, false, true);
            case "sampler2DMSArray" -> sampler(SampledKind.FLOAT, TextureDimension.D2, true, false, true);
            case "isampler1D" -> sampler(SampledKind.SIGNED_INT, TextureDimension.D1, false, false);
            case "isampler2D" -> sampler(SampledKind.SIGNED_INT, TextureDimension.D2, false, false);
            case "isampler3D" -> sampler(SampledKind.SIGNED_INT, TextureDimension.D3, false, false);
            case "isamplerCube" -> sampler(SampledKind.SIGNED_INT, TextureDimension.CUBE, false, false);
            case "isampler2DRect" -> sampler(SampledKind.SIGNED_INT, TextureDimension.RECTANGLE, false, false);
            case "usampler1D" -> sampler(SampledKind.UNSIGNED_INT, TextureDimension.D1, false, false);
            case "usampler2D" -> sampler(SampledKind.UNSIGNED_INT, TextureDimension.D2, false, false);
            case "usampler3D" -> sampler(SampledKind.UNSIGNED_INT, TextureDimension.D3, false, false);
            case "usamplerCube" -> sampler(SampledKind.UNSIGNED_INT, TextureDimension.CUBE, false, false);
            case "usampler2DRect" -> sampler(SampledKind.UNSIGNED_INT, TextureDimension.RECTANGLE, false, false);
            case "image1D" -> image(SampledKind.FLOAT, TextureDimension.D1, false);
            case "image2D" -> image(SampledKind.FLOAT, TextureDimension.D2, false);
            case "image3D" -> image(SampledKind.FLOAT, TextureDimension.D3, false);
            case "image2DRect" -> image(SampledKind.FLOAT, TextureDimension.RECTANGLE, false);
            case "iimage2D" -> image(SampledKind.SIGNED_INT, TextureDimension.D2, false);
            case "uimage2D" -> image(SampledKind.UNSIGNED_INT, TextureDimension.D2, false);
            default -> vectorOrMatrix(token);
        };
    }

    private static DeclaredGlslType sampler(SampledKind kind, TextureDimension dimension,
            boolean arrayed, boolean shadow) {
        return new DeclaredGlslType.Sampler(kind, dimension, arrayed, shadow, false);
    }

    private static DeclaredGlslType sampler(SampledKind kind, TextureDimension dimension,
            boolean arrayed, boolean shadow, boolean multisample) {
        return new DeclaredGlslType.Sampler(kind, dimension, arrayed, shadow, multisample);
    }

    private static DeclaredGlslType image(SampledKind kind, TextureDimension dimension,
            boolean multisample) {
        return new DeclaredGlslType.Image(kind, dimension, false, multisample);
    }

    private static DeclaredGlslType vectorOrMatrix(String token) {
        ScalarKind component = scalarComponent(token);
        if (component == null) {
            return null;
        }
        if (token.startsWith("mat")) {
            String dims = token.substring(3);
            if (dims.isEmpty()) {
                return new DeclaredGlslType.Matrix(component, 2, 2);
            }
            int x = dims.indexOf('x');
            try {
                if (x > 0) {
                    return new DeclaredGlslType.Matrix(component,
                        Integer.parseInt(dims.substring(0, x)),
                        Integer.parseInt(dims.substring(x + 1)));
                }
                int n = Integer.parseInt(dims);
                return new DeclaredGlslType.Matrix(component, n, n);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        String prefix = vectorPrefix(token);
        if (prefix == null) {
            return null;
        }
        try {
            int width = Integer.parseInt(token.substring(prefix.length()));
            if (width < 2 || width > 4) {
                return null;
            }
            return new DeclaredGlslType.Vector(component, width);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static ScalarKind scalarComponent(String token) {
        if (token.startsWith("d")) {
            return ScalarKind.DOUBLE;
        }
        if (token.startsWith("i")) {
            return ScalarKind.SIGNED_INT;
        }
        if (token.startsWith("u") && (token.startsWith("uvec") || token.startsWith("umat"))) {
            return ScalarKind.UNSIGNED_INT;
        }
        if (token.startsWith("b")) {
            return ScalarKind.BOOL;
        }
        return ScalarKind.FLOAT;
    }

    private static String vectorPrefix(String token) {
        for (String p : new String[] {"dvec", "ivec", "uvec", "bvec", "vec"}) {
            if (token.startsWith(p)) {
                return p;
            }
        }
        return null;
    }
}
