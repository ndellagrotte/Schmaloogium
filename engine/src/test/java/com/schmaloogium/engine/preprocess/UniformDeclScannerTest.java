// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class UniformDeclScannerTest {
    @Test
    void multipleDeclaratorsAndStatementsRetainEveryNameAndItsOwnShape() {
        var sampler2d = new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D2,
            false, false, false);
        var sampler3d = new DeclaredGlslType.Sampler(SampledKind.FLOAT, TextureDimension.D3,
            false, false, false);
        assertEquals(List.of(
            new UniformDeclScanner.Finding("first", sampler2d, 1),
            new UniformDeclScanner.Finding("layers", new DeclaredGlslType.Array(sampler2d,
                List.of(new ArrayExtent.Sized(2), new ArrayExtent.Sized(3))), 1),
            new UniformDeclScanner.Finding("volume", sampler3d, 1)),
            UniformDeclScanner.scan("uniform highp sampler2D first, layers[2][3]; uniform sampler3D volume;"));
    }

    @Test
    void commentsAndNestedInitializerCommasDoNotInventOrLoseDeclarations() {
        String source = """
            /* uniform sampler2D ignored;
               still a comment */
            uniform vec2 origin = vec2(0.0, 1.0),
                offset = vec2(2.0, 3.0); // uniform float hidden;
            uniform sampler2D surface;
            """;
        var vector = new DeclaredGlslType.Vector(ScalarKind.FLOAT, 2);
        assertEquals(List.of(
            new UniformDeclScanner.Finding("origin", vector, 3),
            new UniformDeclScanner.Finding("offset", vector, 4),
            new UniformDeclScanner.Finding("surface", new DeclaredGlslType.Sampler(
                SampledKind.FLOAT, TextureDimension.D2, false, false, false), 5)),
            UniformDeclScanner.scan(source));
    }
}
