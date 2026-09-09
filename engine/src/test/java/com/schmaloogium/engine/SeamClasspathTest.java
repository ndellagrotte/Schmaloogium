// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Constraint C-1, classpath half (PHASE_1_DOC §4.3 layer 2): no forbidden coordinate on
 * {@code :engine}'s main compile or runtime classpath. The build injects both classpaths
 * through {@code -Dschmaloogium.test.compileClasspath} / {@code …runtimeClasspath}
 * (buildSrc {@code SeamClasspathArguments}); the failure message names the offending
 * entry and the configuration it came from, so the diagnosis is immediate.
 */
class SeamClasspathTest {

    private static final String[] FORBIDDEN_FILE_NAME_SUBSTRINGS = {
            "minecraft", "forge", "cleanroom", "unimined",
            "mixin", "spongepowered", "mixinextras",
            "lwjgl", "lwjglx", "fmlcore", "launchwrapper",
    };

    @Test
    void mainCompileClasspathHasNoForbiddenCoordinate() {
        assertClean(System.getProperty("schmaloogium.test.compileClasspath"), "main compileClasspath");
    }

    @Test
    void mainRuntimeClasspathHasNoForbiddenCoordinate() {
        assertClean(System.getProperty("schmaloogium.test.runtimeClasspath"), "main runtimeClasspath");
    }

    private static void assertClean(String classpathProperty, String configurationName) {
        assertTrue(classpathProperty != null && !classpathProperty.isBlank(),
                "the build did not inject schmaloogium.test.* classpath properties; "
                        + "SeamClasspathArguments is missing from this module's test jvmArgumentProviders");

        Map<String, List<String>> offenders = new LinkedHashMap<>();
        for (String entry : classpathProperty.split(File.pathSeparator)) {
            if (entry.isBlank()) {
                continue;
            }
            String fileName = new File(entry).getName().toLowerCase(Locale.ROOT);
            for (String forbidden : FORBIDDEN_FILE_NAME_SUBSTRINGS) {
                if (fileName.contains(forbidden)) {
                    offenders.computeIfAbsent(forbidden, k -> new ArrayList<>()).add(entry);
                }
            }
        }
        assertTrue(offenders.isEmpty(),
                "C-1 violation on " + configurationName + ": " + offenders);
    }
}
