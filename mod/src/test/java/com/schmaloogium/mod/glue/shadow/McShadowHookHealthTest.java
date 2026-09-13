// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.shadow.ShadowHookHealth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class McShadowHookHealthTest {

    @AfterEach
    void clear() {
        for (String id : ShadowHookHealth.catalogue()) {
            System.clearProperty("schmaloogium.hooks.anchor." + id);
        }
    }

    /** Forge is on the classpath in the client, not in this headless suite. */
    private static boolean forgePresent() {
        try {
            Class.forName("net.minecraftforge.client.ForgeHooksClient");
            return true;
        } catch (ClassNotFoundException | LinkageError absent) {
            return false;
        }
    }

    @Test
    void missingPropertiesDisableEveryAuditedRowAndForgeRowsResolveByReflection() {
        ShadowHookHealth health = McShadowHookHealth.current();
        assertEquals(66, health.rows().size());
        assertFalse(health.shadowEnabled());
        java.util.List<String> disabled = McShadowHookHealth.disabledRows(health);
        int forgeRows = forgePresent() ? 0 : 2;
        assertEquals(64 + forgeRows, disabled.size(), "everything but the reflected Forge rows: " + disabled);
        assertEquals(forgeRows, disabled.stream().filter(r -> r.startsWith("H8-FORGE-01")).count());
    }

    @Test
    void publishedCountsBecomeActualsAndAllOnesEnableShadows() {
        for (String id : ShadowHookHealth.catalogue()) {
            System.setProperty("schmaloogium.hooks.anchor." + id, "1");
        }
        System.setProperty("schmaloogium.hooks.anchor.H8-CLOUD-01-RESOLVE", "0");
        ShadowHookHealth health = McShadowHookHealth.current();
        assertEquals(forgePresent(), health.shadowEnabled(), "the CLOUD row does not gate shadows");
        java.util.List<String> disabled = McShadowHookHealth.disabledRows(health);
        assertTrue(disabled.contains("H8-CLOUD-01-RESOLVE=0"));
        assertEquals(forgePresent() ? 1 : 3, disabled.size(), String.valueOf(disabled));

        System.setProperty("schmaloogium.hooks.anchor.H8-TRAVERSE-01-VISIT-SEED", "3");
        ShadowHookHealth overmatched = McShadowHookHealth.current();
        assertFalse(overmatched.shadowEnabled(), "overmatch is preserved and disables");
        assertTrue(McShadowHookHealth.disabledRows(overmatched).contains("H8-TRAVERSE-01-VISIT-SEED=3"));
    }
}
