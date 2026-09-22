// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.schmaloogium.engine.config.BooleanOptionValue;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.ProgramStateEvaluationResult;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProgramEnablementTest {
    @TempDir Path root;

    @Test
    void literalsAndFinalizedSwitchValuesControlEnablement() throws Exception {
        var configuration = load("program.composite.enabled=true\n"
            + "program.composite1.enabled=false\n"
            + "program.composite2.enabled=OPTION_OFF\n"
            + "program.composite3.enabled=OPTION_ON && !OPTION_OFF\n");
        assertEquals(new BooleanOptionValue(false), configuration.options().state().values().get("OPTION_OFF"));
        assertEquals(new BooleanOptionValue(true), configuration.options().state().values().get("OPTION_ON"));
        var diagnostics = new ArrayList<EngineDiagnostic>();
        assertEquals(Map.of("composite", true, "composite1", false, "composite2", false, "composite3", true),
            evaluate(configuration, diagnostics));
        assertEquals(java.util.List.of(), diagnostics);
    }

    @Test
    void unknownAndNumericNamesDoNotBecomeBooleanTruthValues() throws Exception {
        var configuration = load("program.composite.enabled=!MISSING\n"
            + "program.composite1.enabled=OPTION_ON || MISSING\n"
            + "program.composite2.enabled=NUMERIC_LEVEL\n"
            + "program.composite3.enabled=!NUMERIC_LEVEL\n");
        assertEquals(new TextOptionValue("3"), configuration.options().state().values().get("NUMERIC_LEVEL"));
        var diagnostics = new ArrayList<EngineDiagnostic>();
        assertEquals(Map.of("composite", false, "composite1", false, "composite2", false, "composite3", false),
            evaluate(configuration, diagnostics));
        assertEquals(4, diagnostics.size());
        assertEquals(Set.of("schmaloogium.warn.program.enabled_unknown_switch"),
            diagnostics.stream().map(EngineDiagnostic::messageKey).collect(java.util.stream.Collectors.toSet()));
    }

    private Map<String, Boolean> evaluate(PackConfiguration configuration, ArrayList<EngineDiagnostic> diagnostics) {
        var evaluated = assertInstanceOf(ProgramStateEvaluationResult.Evaluated.class,
            configuration.evaluateProgramStates(Optional.empty(), diagnostics::add));
        var states = new TreeMap<String, Boolean>();
        evaluated.states().programs().forEach(state -> states.put(state.key().programName(), state.finalEnabled()));
        return states;
    }

    private PackConfiguration load(String properties) throws Exception {
        Path shaders = root.resolve("fixture/shaders");
        Files.createDirectories(shaders);
        for (String name : java.util.List.of("composite", "composite1", "composite2", "composite3")) {
            Files.writeString(shaders.resolve(name + ".fsh"), "#version 120\n"
                + "#define OPTION_ON\n//#define OPTION_OFF\n#define NUMERIC_LEVEL 3 // [1 3 5]\n"
                + "#ifdef OPTION_ON\n#endif\n#ifdef OPTION_OFF\n#endif\nvoid main() {}\n");
        }
        Files.writeString(shaders.resolve("shaders.properties"), properties);
        var frontEnd = PackFrontEnds.create().frontEnd();
        var candidate = frontEnd.discover(new PackDiscoveryRequest(root, null)).candidates().stream()
            .filter(c -> c.displayName().equals("fixture")).findFirst().orElseThrow();
        var capabilities = new GLCapabilityProfile(4, 6, "4.60 NVIDIA", "NVIDIA Corporation",
            "NVIDIA GeForce RTX 3070/PCIe/SSE2", 8, 8, 32, 16, 32768, 2048, 32768, Set.of());
        var request = new PackLoadRequest(root, new PackSelection.Filesystem(candidate.id()),
            new RuntimeIdentityData(1, 12, 2, "classic", "1.0.0", OsFamily.LINUX, Map.of()),
            capabilities, new EngineOptionData(Map.of()), new CompanionOptionMacros(false, false),
            new RendererFeatureData(false, false), null, null, Optional.empty(), d -> {});
        var loaded = frontEnd.load(request);
        return assertInstanceOf(PackLoadResult.Loaded.class, loaded, loaded.toString()).configuration();
    }
}
