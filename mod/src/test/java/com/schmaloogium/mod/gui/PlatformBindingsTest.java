// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadLifecycle;
import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ReloadTrigger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless contract of the platform bindings (PHASE_12_DOC §4.8): the F3+R chord
 * gate's truth table [D-P12-13], the command's reply decision (§4.8.2), and the
 * classification each trigger hands to the reload model. The mod source-set test
 * classpath carries no Minecraft/Forge types, so only the MC-free model statics are
 * exercised here; the client-typed classes are covered by the client smoke.
 */
class PlatformBindingsTest {

    // --- chordActivated truth table ---

    @Test
    void freshF3AndRPressWithActiveEngineAndNoScreenActivates() {
        assertTrue(ReloadTrigger.chordActivated(true, false, true, true, false));
    }

    @Test
    void inactiveEngineNeverActivates() {
        assertFalse(ReloadTrigger.chordActivated(false, false, true, true, false));
    }

    @Test
    void openScreenNeverActivates() {
        assertFalse(ReloadTrigger.chordActivated(true, true, true, true, false));
    }

    @Test
    void f3WithoutFreshRPressNeverActivates() {
        // R not held at all:
        assertFalse(ReloadTrigger.chordActivated(true, false, true, false, false));
        // R held but was already down when F3 was still up — no transition to pressed:
        assertFalse(ReloadTrigger.chordActivated(true, false, false, true, false));
    }

    @Test
    void rMerelyHeldNeverRetriggers() {
        assertFalse(ReloadTrigger.chordActivated(true, false, true, true, true));
    }

    // --- reply decision ---

    @Test
    void replyKeyNamesAcknowledgementOrInertNotice() {
        assertEquals("schmaloogium.command.reloadshaders.done",
                ReloadTrigger.replyKey(true));
        assertEquals("schmaloogium.command.reloadshaders.inert",
                ReloadTrigger.replyKey(false));
    }

    // --- classification handed to the reload model ---

    @Test
    void resourceTriggerClassifiesAsNoneWithReacquire() {
        // The listener body submits exactly this request; the listener itself is
        // MC-typed and covered by the client smoke, not this MC-free source set.
        ReloadRequest request = ReloadTrigger.RESOURCE_MANAGER_RELOAD.request();
        assertEquals(ReloadLifecycle.NONE, request.lifecycle());
        assertFalse(request.worldRendererReload());
        assertTrue(request.resourceReacquire());
        assertEquals(ReloadCause.RESOURCE_RELOAD, request.cause());
    }

    @Test
    void keybindAndCommandTriggersClassifyAsFullWithTheirCauses() {
        ReloadRequest keybind = ReloadTrigger.F3R_KEYBIND.request();
        assertEquals(ReloadLifecycle.FULL, keybind.lifecycle());
        assertTrue(keybind.worldRendererReload());
        assertFalse(keybind.resourceReacquire());
        assertEquals(ReloadCause.KEYBIND, keybind.cause());

        ReloadRequest command = ReloadTrigger.RELOAD_SHADERS_COMMAND.request();
        assertEquals(ReloadLifecycle.FULL, command.lifecycle());
        assertTrue(command.worldRendererReload());
        assertFalse(command.resourceReacquire());
        assertEquals(ReloadCause.COMMAND, command.cause());
    }
}
