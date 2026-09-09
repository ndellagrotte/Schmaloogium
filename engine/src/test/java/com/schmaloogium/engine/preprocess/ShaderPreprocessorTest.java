// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** jcpp shader preprocessing vectors: option macros, MC_VERSION, identity macros. */
class ShaderPreprocessorTest {

    private static ShaderPreprocessor.Result run(String source, Map<String, String> macros,
            int effectiveVersion) {
        return ShaderPreprocessor.process(source, macros, effectiveVersion);
    }

    @Test
    void optionDefineControlsBranch() {
        String source = String.join("\n",
            "#ifdef DO_FOG",
            "vec3 applyFog(vec3 c) { return c; }",
            "#else",
            "vec3 applyFogHalf(vec3 c) { return c; }",
            "#endif",
            "");
        assertTrue(run(source, Map.of("DO_FOG", "1"), 120).text().contains("applyFog("));
        assertFalse(run(source, Map.of("DO_FOG", "1"), 120).text().contains("applyFogHalf"));
        assertTrue(run(source, Map.of(), 120).text().contains("applyFogHalf"));
    }

    @Test
    void mcVersionMacroComparesNumerically() {
        String source = String.join("\n",
            "#if MC_VERSION >= 11300",
            "const bool newPipeline = true;",
            "#else",
            "const bool oldPipeline = true;",
            "#endif",
            "");
        assertTrue(run(source, Map.of("MC_VERSION", "11300"), 120).text()
            .contains("newPipeline"));
        assertTrue(run(source, Map.of("MC_VERSION", "11202"), 120).text()
            .contains("oldPipeline"));
    }

    @Test
    void irisIdentityMacrosStayUndefined() {
        String source = String.join("\n",
            "#ifndef IS_IRIS",
            "const bool vanillaPath = true;",
            "#endif",
            "");
        // option-1 identity: no IS_IRIS macro is ever defined
        assertTrue(run(source, Map.of(), 120).text().contains("vanillaPath"));
    }

    @Test
    void codeSurvivesWithCommentRetention() {
        String source = "// leading\nint x = 1;\n";
        ShaderPreprocessor.Result result = run(source, Map.of(), 120);
        assertTrue(result.text().contains("int x = 1;"));
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    void malformedDirectiveDoesNotThrow() {
        String source = "int x = 1;\n#if((( broken\nint y = 2;\n";
        assertDoesNotThrow(() -> run(source, Map.of(), 120));
    }
}
