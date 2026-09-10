// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat.vertex;

import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.mod.compat.CompatContext;
import com.schmaloogium.mod.compat.CompatVerdict;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-COMPAT (PHASE_10_DOC §4.9, §8): the actual {@code celeritas} id alone bails, an
 * exact renderer class alone bails, the candidate ids stay in the table, a package
 * root is never passed to the class probe, no GL capability profile is constructed for
 * class probes, and a clean vanilla context passes. Detect-and-bail only — no
 * warn-and-continue mode.
 */
class ChunkRendererCompatCheckTest {

    /** Recording stub: capabilities() throws — the check must never construct a profile. */
    private static final class StubContext implements CompatContext {
        private final Set<String> mods;
        private final Set<String> classes;
        private final Set<String> probedClasses = new HashSet<>();

        private StubContext(Set<String> mods, Set<String> classes) {
            this.mods = mods;
            this.classes = classes;
        }

        @Override
        public boolean isModLoaded(String modId) {
            return mods.contains(modId);
        }

        @Override
        public boolean isClassPresent(String binaryName) {
            probedClasses.add(binaryName);
            return classes.contains(binaryName);
        }

        @Override
        public GLCapabilityProfile capabilities() {
            throw new AssertionError(
                    "a GL capability profile must not be constructed for class probes");
        }
    }

    private static final String CELERITAS_RENDERER =
            "org.taumc.celeritas.impl.render.terrain.CeleritasWorldRenderer";
    private static final String CELERITAS_VINTAGE =
            "org.taumc.celeritas.CeleritasVintage";

    private static StubContext context() {
        return new StubContext(Set.of(), Set.of());
    }

    @Test
    void celeritasModIdAloneBails() {
        StubContext ctx = new StubContext(Set.of("celeritas"), Set.of());
        CompatVerdict.Bail bail = assertInstanceOf(CompatVerdict.Bail.class,
                new ChunkRendererCompatCheck().check(ctx));
        assertEquals(ChunkRendererCompatCheck.REASON_KEY, bail.reasonKey());
        assertEquals("Celeritas", bail.args().get(0));
    }

    @Test
    void exactRendererClassAloneBails() {
        StubContext ctx = new StubContext(Set.of(), Set.of(CELERITAS_RENDERER));
        CompatVerdict.Bail bail = assertInstanceOf(CompatVerdict.Bail.class,
                new ChunkRendererCompatCheck().check(ctx));
        assertEquals("Celeritas", bail.args().get(0));
    }

    @Test
    void exactAlternateClassAloneBails() {
        StubContext ctx = new StubContext(Set.of(), Set.of(CELERITAS_VINTAGE));
        assertInstanceOf(CompatVerdict.Bail.class, new ChunkRendererCompatCheck().check(ctx));
    }

    @Test
    void candidateModIdsAreRetainedProbes() {
        // Absence of the candidate ids does not clear Pintonium, and their presence bails.
        for (String candidate : new String[] {"embeddium", "celeritas_shaders"}) {
            StubContext ctx = new StubContext(Set.of(candidate), Set.of());
            assertInstanceOf(CompatVerdict.Bail.class,
                    new ChunkRendererCompatCheck().check(ctx));
        }
        StubContext provisional = new StubContext(Set.of("nothirium"), Set.of());
        CompatVerdict.Bail bail = assertInstanceOf(CompatVerdict.Bail.class,
                new ChunkRendererCompatCheck().check(provisional));
        assertEquals("Nothirium", bail.args().get(0));
    }

    @Test
    void cleanVanillaContextPasses() {
        assertEquals(CompatVerdict.Ok.class,
                new ChunkRendererCompatCheck().check(context()).getClass());
    }

    @Test
    void packageStringsAreNeverPassedToTheClassProbe() {
        StubContext ctx = context();
        new ChunkRendererCompatCheck().check(ctx);
        for (String probed : ctx.probedClasses) {
            assertTrue(CELERITAS_RENDERER.equals(probed) || CELERITAS_VINTAGE.equals(probed),
                    "non-class probe string reached isClassPresent: " + probed);
        }
        // The investigation roots are never probed as classes.
        assertTrue(!ctx.probedClasses.contains("org.embeddedt.embeddium"));
        assertTrue(!ctx.probedClasses.contains("org.taumc.celeritas"));
    }

    @Test
    void checkIdentityIsFixed() {
        ChunkRendererCompatCheck check = new ChunkRendererCompatCheck();
        assertEquals("schmaloogium.chunk_renderer", check.id());
    }
}
