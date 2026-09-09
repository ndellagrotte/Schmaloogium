// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8.1 row {@code GLCapabilityProfileDerivationTest} (PHASE_1_DOC §4.7.2): {@code atLeast},
 * {@code hasExtension}, and {@code supportsMipmapGeneration()} — true at 3.0, false at 2.1,
 * the RESEARCH.md §4.1 gate.
 */
class GLCapabilityProfileDerivationTest {

    private static GLCapabilityProfile of(int major, int minor, String... extensions) {
        return new GLCapabilityProfile(
                major, minor, "x.y", "vendor", "renderer",
                8, 8, 16, 16, 4096,
                0, 0, // target gates are §4.7.2's; irrelevant to these derivations
                Set.of(extensions));
    }

    @Test
    void atLeastComparesTheVersionPair() {
        assertAll(
                // exact match on both components
                () -> assertTrue(of(4, 6).atLeast(4, 6)),
                // minor below
                () -> assertFalse(of(3, 0).atLeast(3, 1)),
                () -> assertTrue(of(3, 1).atLeast(3, 0)),
                // major dominates minor
                () -> assertTrue(of(4, 0).atLeast(3, 9)),
                () -> assertFalse(of(3, 9).atLeast(4, 0)),
                () -> assertFalse(of(4, 6).atLeast(5, 0)));
    }

    @Test
    void hasExtensionAnswersFromTheSet() {
        GLCapabilityProfile profile = of(4, 6, "GL_ARB_geometry_shader4", "GL_KHR_debug");
        assertTrue(profile.hasExtension("GL_ARB_geometry_shader4"));
        assertTrue(profile.hasExtension("GL_KHR_debug"));
        assertFalse(profile.hasExtension("GL_ARB_texture_rectangle"));
        assertEquals(2, profile.extensions().size(), "the set is exactly what was supplied");
    }

    @Test
    void mipmapGenerationRequiresThreePointZero() {
        // RESEARCH.md §4.1: "mipmap gen requires GL 3.0".
        assertFalse(of(2, 1).supportsMipmapGeneration(), "2.1 must not admit mipmap generation");
        assertFalse(of(2, 9).supportsMipmapGeneration());
        assertTrue(of(3, 0).supportsMipmapGeneration(), "the gate is inclusive at 3.0");
        assertTrue(of(4, 6).supportsMipmapGeneration());
    }
}
