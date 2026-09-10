// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;
import com.schmaloogium.mod.gui.model.ReloadLifecycle;
import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ReloadTrigger;

/**
 * Headless vectors for the reload trigger classification, merge algebra and the inert
 * coordinator (§4.8): a mod-side ReloadRequest is always FULL/true/false with its
 * trigger's cause; burst coalescing runs through the real coordinator.
 */
class ReloadClassificationTest {

    @Test
    void reload_classificationTriggerMatrix() {
        ReloadRequest f3r = ReloadTrigger.F3R_KEYBIND.request();
        assertEquals(ReloadLifecycle.FULL, f3r.lifecycle());
        assertTrue(f3r.worldRendererReload());
        assertFalse(f3r.resourceReacquire());
        assertEquals(ReloadCause.KEYBIND, f3r.cause());

        ReloadRequest command = ReloadTrigger.RELOAD_SHADERS_COMMAND.request();
        assertEquals(ReloadLifecycle.FULL, command.lifecycle());
        assertTrue(command.worldRendererReload());
        assertFalse(command.resourceReacquire());
        assertEquals(ReloadCause.COMMAND, command.cause());

        ReloadRequest resource = ReloadTrigger.RESOURCE_MANAGER_RELOAD.request();
        assertEquals(ReloadLifecycle.NONE, resource.lifecycle());
        assertFalse(resource.worldRendererReload());
        assertTrue(resource.resourceReacquire());
        assertEquals(ReloadCause.RESOURCE_RELOAD, resource.cause());
    }

    @Test
    void reload_seamDeliversEveryClassifiedRequestInOrder() {
        var seen = new CopyOnWriteArrayList<ReloadRequest>();
        var diagnostics = new CopyOnWriteArrayList<EngineDiagnostic>();
        ReloadCoordinator.install(seen::add);
        try {
            ReloadCoordinator.submitOrReportInert(
                    ReloadTrigger.RESOURCE_MANAGER_RELOAD.request(), diagnostics::add);
            ReloadCoordinator.submitOrReportInert(
                    ReloadTrigger.RELOAD_SHADERS_COMMAND.request(), diagnostics::add);
            ReloadCoordinator.submitOrReportInert(
                    ReloadTrigger.F3R_KEYBIND.request(), diagnostics::add);
            // Mod-side ownership ends at the classified request: every trigger is
            // delivered, in submit order, with its classification intact; the Phase 7
            // drain owns coalescing.
            assertEquals(3, seen.size());
            assertEquals(ReloadCause.RESOURCE_RELOAD, seen.get(0).cause());
            assertEquals(ReloadCause.COMMAND, seen.get(1).cause());
            assertEquals(ReloadCause.KEYBIND, seen.get(2).cause());
            assertEquals(0, diagnostics.size());
        } finally {
            ReloadCoordinator.clear();
        }
    }

    @Test
    void reload_absentCoordinatorInertAndLoggedOnce() {
        var diagnostics = new CopyOnWriteArrayList<EngineDiagnostic>();
        ReloadCoordinator.clear();
        try {
            ReloadCoordinator.submitOrReportInert(
                    ReloadTrigger.RELOAD_SHADERS_COMMAND.request(), diagnostics::add);
            ReloadCoordinator.submitOrReportInert(
                    ReloadTrigger.F3R_KEYBIND.request(), diagnostics::add);
            assertEquals(1, diagnostics.size());
            assertEquals(DiagnosticSeverity.WARN, diagnostics.get(0).severity());
            assertEquals(UserChannel.LOG_ONLY, diagnostics.get(0).channel());
        } finally {
            ReloadCoordinator.clear();
        }
    }


    @Test
    void chordActivation_gateSuppressesUntilEngineArms() {
        // engine inactive: never
        assertFalse(ReloadTrigger.chordActivated(false, false, true, true, false));
        // screen open: never
        assertFalse(ReloadTrigger.chordActivated(true, true, true, true, false));
        // F3 held but R not freshly pressed
        assertFalse(ReloadTrigger.chordActivated(true, false, true, true, true));
        assertFalse(ReloadTrigger.chordActivated(true, false, false, true, false));
        // the actual chord: F3 down + R freshly pressed, no screen open, engine active
        assertTrue(ReloadTrigger.chordActivated(true, false, true, true, false));
    }
}
