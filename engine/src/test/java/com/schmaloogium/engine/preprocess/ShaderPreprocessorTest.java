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
        assertFalse(result.text().contains("#version"), "implicit sources must not gain a guessed version");
    }

    @Test
    void preservesDriverVersionProfileAndOnlyActiveExtensionDirectives() {
        String source = """
            #version 150 compatibility
            #if __VERSION__ >= 150
            #extension GL_ARB_gpu_shader5 : enable
            #endif
            #if 0
            #extension GL_FAKE_inactive : require
            #endif
            uniform float brightness = 1.0;
            void main() { gl_Position = vec4(brightness); }
            """;
        var result = run(source, Map.of(), 150);
        var language = LanguageScanner.scan(result.text(),
            new SourceId(new com.schmaloogium.engine.pack.NormalizedPackPath("shaders/example.vsh")));
        assertTrue(language.explicitVersion());
        assertEquals(150, language.version());
        assertEquals(java.util.Optional.of(GlslProfile.COMPATIBILITY), language.explicitProfile());
        assertEquals(java.util.List.of("GL_ARB_gpu_shader5"),
            language.extensions().stream().map(ShaderExtensionDirective::name).toList());
        assertTrue(result.text().contains("void main()"));
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    void directiveLookingBlockCommentsCannotConsumeTheFollowingShader() {
        String comment = "/*\n#version 999 */";
        var result = run(comment + "\n#version 130\nvoid main() { gl_Position = vec4(1.0); }\n",
            Map.of(), 130);
        assertTrue(result.text().contains(comment));
        assertTrue(result.text().contains("#version 130"));
        assertTrue(result.text().contains("void main()"));
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    void inlineDirectiveCommentsPreserveTheirFollowingTokensAndNewlines() {
        String version = "#version /* language */ 150 compatibility";
        String extension = "#extension GL_ARB_gpu_shader5 /* explanation\ncontinued */ : enable";
        var result = run(version + "\n" + extension
            + "\nint sourceLine = __LINE__;\nvoid main() { gl_Position = vec4(1.0); }\n", Map.of(), 150);
        assertTrue(result.text().startsWith(version));
        assertTrue(result.text().contains(extension));
        assertTrue(result.text().contains("void main()"));
        assertTrue(result.text().contains("int sourceLine = 4;"));
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    void inactiveMultilineDirectivesPreserveSourceCoordinates() {
        String source = "#version 130\n#if 0\n"
            + "#extension GL_FAKE /* explanation\ncontinued */ : require\n"
            + "#endif\nint sourceLine = __LINE__;\n";
        var result = run(source, Map.of(), 130);
        assertTrue(result.text().contains("int sourceLine = 6;"));
        assertFalse(result.text().contains("GL_FAKE"));
        assertEquals(source.lines().count(), result.text().lines().count());
    }
    @Test
    void activePreprocessorErrorsReachTheResultButInactiveErrorsDoNot() {
        String source = "#if FAIL\n#error invalid active resource source\n#endif\n";
        var active = run(source, Map.of("FAIL", "1"), 120);
        assertTrue(active.diagnostics().stream().anyMatch(d ->
            d.severity() == com.schmaloogium.engine.diag.DiagnosticSeverity.ERROR));
        var inactive = run(source, Map.of("FAIL", "0"), 120);
        assertTrue(inactive.diagnostics().isEmpty());
    }
}
