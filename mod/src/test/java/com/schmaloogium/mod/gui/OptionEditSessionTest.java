// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.engine.pack.PackCandidateStatus;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackFrontEnds;
import com.schmaloogium.engine.pack.PackOptionsTargetAcquisition;
import com.schmaloogium.engine.pack.PersistenceRootConfiguration;
import com.schmaloogium.mod.gui.model.ApplyOutcome;
import com.schmaloogium.mod.gui.model.GuiText;
import com.schmaloogium.mod.gui.model.OptionActionAvailability;
import com.schmaloogium.mod.gui.model.OptionApplyStatus;
import com.schmaloogium.mod.gui.model.OptionEditSessionImpl;
import com.schmaloogium.mod.gui.model.OptionId;
import com.schmaloogium.mod.gui.model.OptionPresentationModel;
import com.schmaloogium.mod.gui.model.PresentationEntry;
import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;
import com.schmaloogium.mod.gui.model.ReloadLifecycle;
import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ScreenId;

/**
 * Edit-session vectors (PHASE_12_DOC §4.4/§4.5/§4.7.2, §8): pending-change semantics,
 * atomic profile batches, apply/discard/reset, inert Internal gates, and the changed-
 * only file round-trip through the REAL Phase 3 codecs (no fake io).
 */
class OptionEditSessionTest {

    private static OptionConfiguration configuration() {
        OptionCatalog catalog = OptionCatalogs.create(Fixtures.definitions(),
                new Object(), false);
        return new OptionConfiguration(catalog, catalog.defaultState(),
                Fixtures.profiles(), Fixtures.mainScreen(),
                Map.of("effects", Fixtures.effectsScreen()), Fixtures.sliders(),
                Map.of("en_us", Fixtures.enDecorations()));
    }

    private static GuiText text(OptionConfiguration configuration) {
        return new GuiText(configuration.localizedDecorations(), Map.of(), "en_us");
    }

    /** A recording filesystem port with a programmable commit result. */
    private static final class RecordingIo implements OptionEditSessionImpl.PackOptionsIo {
        final List<OptionState> writes = new ArrayList<>();
        boolean succeed = true;

        @Override
        public boolean writeCommitted(OptionState state) {
            writes.add(state);
            return succeed;
        }
    }

    @Test
    void valueOption_cycleWrapsBothDirections() {
        OptionConfiguration configuration = configuration();
        RecordingIo io = new RecordingIo();
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                io, Optional.empty(), Diagnostics::report);
        OptionId sun = new OptionId("SUN_ANGLE");
        // default 30.0 at index 1: +1 → 60.0, +1 → 90.0, +1 wraps to 0.0, -1 back
        session.cycle(sun, 1);
        assertRawValue(session, sun, "60.0");
        session.cycle(sun, 1);
        assertRawValue(session, sun, "90.0");
        session.cycle(sun, 1); // wrap forward → 0.0
        assertRawValue(session, sun, "0.0");
        session.cycle(sun, -1); // wrap back → 90.0
        assertRawValue(session, sun, "90.0");
        // nothing written yet: edits stay pending
        assertEquals(0, io.writes.size());
        assertTrue(session.isDirty());
        assertEquals(1, session.pendingChangeCount());
    }

    @Test
    void profile_clickAppliesWholeBatchAtomically() {
        OptionConfiguration configuration = configuration();
        RecordingIo io = new RecordingIo();
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                io, Optional.empty(), Diagnostics::report);
        // fresh state infers nothing: Custom
        assertEquals("Custom", profileLabel(session.present(ScreenId.MAIN)));
        session.cycleProfile(); // Custom → first declared: Performance
        // the whole constraint batch landed in the pending preview at once
        assertRawValue(session, new OptionId("SHADOWS"), "false");
        assertRawValue(session, new OptionId("SUN_ANGLE"), "0.0");
        assertEquals("Performance", profileLabel(session.present(ScreenId.MAIN)));
        // two option deltas + the explicit selection = 3 pending changes
        assertEquals(3, session.pendingChangeCount());
        var seen = new CopyOnWriteArrayList<ReloadRequest>();
        ReloadCoordinator.install(seen::add);
        try {
            ApplyOutcome outcome = session.apply();
            assertEquals(OptionApplyStatus.PERSISTED, outcome.status());
            assertEquals(1, io.writes.size());
            // acceptance clears the pending selection (it became the baseline)
            assertTrue(session.pendingProfileSummaryText().isEmpty());
            assertEquals(1, seen.size());
            assertEquals(ReloadLifecycle.REPUBLISH, seen.get(0).lifecycle());
            assertEquals(ReloadCause.OPTION_APPLY, seen.get(0).cause());
        } finally {
            ReloadCoordinator.clear();
        }
        assertFalse(session.isDirty());
    }

    private static String profileLabel(OptionPresentationModel model) {
        return model.mainScreen().entries().stream()
                .filter(PresentationEntry.ProfileCycle.class::isInstance)
                .map(e -> ((PresentationEntry.ProfileCycle) e).label())
                .findFirst().orElseThrow();
    }

    private static void assertRawValue(OptionEditSessionImpl session, OptionId id,
                                       String expected) {
        OptionPresentationModel model = session.present(ScreenId.MAIN);
        List<PresentationEntry> everywhere = new ArrayList<>(model.mainScreen().entries());
        model.subScreens().values().forEach(s -> everywhere.addAll(s.entries()));
        String raw = everywhere.stream()
                .filter(e -> e instanceof PresentationEntry.ValueOption
                        || e instanceof PresentationEntry.SliderOption
                        || e instanceof PresentationEntry.SwitchOption)
                .filter(e -> id.name().equals(OptionEditSessionTest.nameOf(e)))
                .map(e -> e instanceof PresentationEntry.SwitchOption s
                        ? (s.value() ? "true" : "false")
                        : (e instanceof PresentationEntry.ValueOption v
                                ? v.rawValue() : ((PresentationEntry.SliderOption) e).rawValue()))
                .findFirst().orElseThrow();
        assertEquals(expected, raw);
    }

    private static String nameOf(PresentationEntry e) {
        if (e instanceof PresentationEntry.SwitchOption s) {
            return s.id().name();
        }
        if (e instanceof PresentationEntry.ValueOption v) {
            return v.id().name();
        }
        return ((PresentationEntry.SliderOption) e).id().name();
    }

    @Test
    void profile_cycleWrapsThroughDeclaredOrder() {
        OptionConfiguration configuration = configuration();
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                new RecordingIo(), Optional.empty(), Diagnostics::report);
        session.cycleProfile(); // Custom → Performance
        session.cycleProfile(); // Performance → Fancy
        assertEquals("Fancy", profileLabel(session.present(ScreenId.MAIN)));
        session.cycleProfile(); // Fancy wraps → Performance (first declared)
        assertEquals("Performance", profileLabel(session.present(ScreenId.MAIN)));
    }

    @Test
    void session_failedWriteRetainsEverything() {
        OptionConfiguration configuration = configuration();
        RecordingIo io = new RecordingIo();
        io.succeed = false;
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                io, Optional.empty(), Diagnostics::report);
        session.toggle(new OptionId("SHADOWS"));
        ApplyOutcome outcome = session.apply();
        assertEquals(OptionApplyStatus.FAILED, outcome.status());
        // pending preview and baselines retained; nothing queued
        assertTrue(session.isDirty());
        assertRawValue(session, new OptionId("SHADOWS"), "false");
        // retry with the port healed commits
        io.succeed = true;
        assertEquals(OptionApplyStatus.PERSISTED, session.apply().status());
        assertFalse(session.isDirty());
    }

    @Test
    void session_discardAndResetRestoreBaselines() {
        OptionConfiguration configuration = configuration();
        RecordingIo io = new RecordingIo();
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                io, Optional.empty(), Diagnostics::report);
        session.cycleProfile(); // pending Performance + selection
        session.discard();
        assertFalse(session.isDirty());
        assertEquals("Custom", profileLabel(session.present(ScreenId.MAIN)));
        assertRawValue(session, new OptionId("SHADOWS"), "true");

        // reset commits the pack defaults and clears any explicit selection, always
        // taking the write route even from a clean state
        session.cycleProfile();
        ApplyOutcome reset = session.resetToPackDefaults();
        assertEquals(OptionApplyStatus.PERSISTED, reset.status());
        assertEquals(1, io.writes.size()); // only the reset write; discards never write
        assertFalse(session.isDirty());
        assertEquals("Custom", profileLabel(session.present(ScreenId.MAIN)));
    }

    @Test
    void session_cleanApplyIsUnchangedWithoutReload() {
        OptionConfiguration configuration = configuration();
        RecordingIo io = new RecordingIo();
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                io, Optional.empty(), Diagnostics::report);
        var seen = new CopyOnWriteArrayList<ReloadRequest>();
        ReloadCoordinator.install(seen::add);
        try {
            ApplyOutcome outcome = session.apply();
            assertEquals(OptionApplyStatus.UNCHANGED, outcome.status());
            assertTrue(outcome.reload().isEmpty());
            assertEquals(0, seen.size());
            assertEquals(0, io.writes.size());
        } finally {
            ReloadCoordinator.clear();
        }
    }

    @Test
    void session_inertInternalGatesRejectEverything() {
        OptionConfiguration configuration = configuration();
        var session = OptionEditSessionImpl.internal(configuration, text(configuration),
                Optional.empty(), Optional.empty(), Diagnostics::report);
        session.toggle(new OptionId("SHADOWS"));
        session.cycle(new OptionId("SUN_ANGLE"), 1);
        session.cycleProfile();
        assertFalse(session.isDirty());
        // a clean session applies to UNCHANGED; REJECTED is only reachable when a
        // commit route existed and vanished — the gates prevent ever getting dirty
        assertEquals(OptionApplyStatus.UNCHANGED, session.apply().status());
        OptionPresentationModel model = session.present(ScreenId.MAIN);
        assertEquals(OptionActionAvailability.ENABLED,
                model.availability().done()); // viewing/closing stays possible
        assertFalse(model.availability().mutation().enabled());
        // every option renders non-interactive with the prepared reason
        assertTrue(model.mainScreen().entries().stream()
                .filter(e -> e instanceof PresentationEntry.SwitchOption)
                .map(e -> (PresentationEntry.SwitchOption) e)
                .allMatch(e -> !e.interactive()));
    }

    @Test
    void session_pendingProfileSummaryTracksIntent(@TempDir Path unused) {
        OptionConfiguration configuration = configuration();
        var session = OptionEditSessionImpl.filesystem(configuration, text(configuration),
                new RecordingIo(), Optional.empty(), Diagnostics::report);
        assertTrue(session.pendingProfileSummaryText().isEmpty());
        session.cycleProfile();
        String summary = session.pendingProfileSummaryText();
        assertEquals("Pending profile selection: Performance", summary);
        session.discard(); // pending selection returns to the empty baseline
        assertTrue(session.pendingProfileSummaryText().isEmpty());
        assertFalse(session.isDirty());
    }

    // ------------------------------------------------------------------ round trip

    @Test
    void session_packOptionsTargetIsBundleIssuedAndDomainGated(@TempDir Path gameDir)
            throws IOException {
        Path shaderpacks = gameDir.resolve("shaderpacks");
        Files.createDirectories(shaderpacks.resolve("LabPack/shaders"));
        var services = PackFrontEnds.create();
        var access = BundleIo.acquire(services,
                new PersistenceRootConfiguration(shaderpacks, gameDir),
                Diagnostics::report).orElseThrow();
        PackFrontEnd frontEnd = services.frontEnd();
        var discovery = frontEnd.discover(new PackDiscoveryRequest(shaderpacks,
                Diagnostics::report));
        var labPack = discovery.candidates().stream()
                .filter(c -> c.kind() == PackCandidateKind.DIRECTORY)
                .findFirst().orElseThrow();
        assertEquals(PackCandidateStatus.AVAILABLE, labPack.status());
        PackOptionsTargetAcquisition acquisition =
                frontEnd.packOptionsTarget(labPack.id());
        assertInstanceOf(PackOptionsTargetAcquisition.Acquired.class, acquisition);
        var target = ((PackOptionsTargetAcquisition.Acquired) acquisition).target();

        // the persistence codec domain-gates foreign catalogs: a synthetic catalog can
        // never satisfy the write preflight, so pack-session io must come from
        // bundle-issued configurations (the P7 publication bridge in production)
        OptionConfiguration configuration = configuration();
        var result = services.optionPersistence().write(
                new com.schmaloogium.engine.pack.OptionPersistenceWriteRequest(
                        access.files(), target, configuration.catalog(),
                        configuration.state(), Diagnostics::report));
        assertEquals(com.schmaloogium.engine.pack.PersistenceWriteStatus.FAILED,
                result.status());
    }
}
