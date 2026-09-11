// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.pack.PackFrontEnds;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.mod.core.pipeline.PipelineFixtures.CollectingDiagnostics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/** The durable-string decode against a real discovery: off, internal, malformed, missing, present. */
class SelectionResolverTest {

    @TempDir
    Path gameDir;

    private SelectionResolver resolver(Path shaderpacks, String durable, CollectingDiagnostics diag) {
        return new SelectionResolver(PackFrontEnds.create().frontEnd(), shaderpacks, () -> durable, diag);
    }

    @Test
    void off_and_blank_resolveOffSilently() throws Exception {
        Path shaderpacks = PipelineFixtures.writeMinimalPack(gameDir);
        CollectingDiagnostics diag = new CollectingDiagnostics();
        assertInstanceOf(PackSelection.Off.class, resolver(shaderpacks, "off", diag).get());
        assertInstanceOf(PackSelection.Off.class, resolver(shaderpacks, "", diag).get());
        assertTrue(diag.reports.isEmpty());
    }

    @Test
    void internal_isOffWithOneWarning() throws Exception {
        Path shaderpacks = PipelineFixtures.writeMinimalPack(gameDir);
        CollectingDiagnostics diag = new CollectingDiagnostics();
        assertInstanceOf(PackSelection.Off.class, resolver(shaderpacks, "(internal)", diag).get());
        assertEquals(1, diag.reports.size());
        assertEquals("schmaloogium.warn.pipeline.internalUnavailable", diag.reports.get(0).messageKey());
    }

    @Test
    void malformed_isKeptOff() throws Exception {
        Path shaderpacks = PipelineFixtures.writeMinimalPack(gameDir);
        CollectingDiagnostics diag = new CollectingDiagnostics();
        assertInstanceOf(PackSelection.Off.class, resolver(shaderpacks, "x:nope", diag).get());
        assertEquals("schmaloogium.gui.restoreKeptOff", diag.reports.get(0).messageKey());
    }

    @Test
    void missing_isKeptOff() throws Exception {
        Path shaderpacks = PipelineFixtures.writeMinimalPack(gameDir);
        CollectingDiagnostics diag = new CollectingDiagnostics();
        assertInstanceOf(PackSelection.Off.class,
                resolver(shaderpacks, "d:absent", diag).get());
        assertTrue(diag.reports.stream()
                .anyMatch(d -> d.messageKey().equals("schmaloogium.gui.restoreKeptOff")));
    }

    @Test
    void present_resolvesToFilesystemSelection() throws Exception {
        Path shaderpacks = PipelineFixtures.writeMinimalPack(gameDir);
        CollectingDiagnostics diag = new CollectingDiagnostics();
        String durable = PipelineFixtures.durableFor(PackFrontEnds.create(), shaderpacks,
                PipelineFixtures.PACK_NAME);
        PackSelection selection = resolver(shaderpacks, durable, diag).get();
        assertInstanceOf(PackSelection.Filesystem.class, selection);
    }
}
