// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.shadow.HookDisposition;
import com.schmaloogium.engine.shadow.ShadowHookHealth;
import com.schmaloogium.engine.shadow.ShadowHookRow;

import java.util.ArrayList;
import java.util.List;

/**
 * The game-side reader of the shadow hook-anchor audit (PHASE_8_DOC §4.13.1): every one of
 * the sixty-six catalogue rows in catalogue order, {@code expected = 1}, {@code actual} from
 * the {@code schmaloogium.hooks.anchor.<id>} property the coremod-side audit published
 * (missing = 0), except the two Forge resolution rows, which are resolved here by reflection
 * (Forge classes are never obfuscated). The disposition and fingerprint are computed by the
 * engine record, never here.
 */
public final class McShadowHookHealth {

    private McShadowHookHealth() {
    }

    public static ShadowHookHealth current() {
        List<ShadowHookRow> rows = new ArrayList<>();
        for (String id : ShadowHookHealth.catalogue()) {
            int actual = switch (id) {
                case "H8-FORGE-01-GET-RESOLVE" -> resolves(
                        "net.minecraftforge.client.MinecraftForgeClient", "getRenderPass");
                case "H8-FORGE-01-SET-RESOLVE" -> resolves(
                        "net.minecraftforge.client.ForgeHooksClient", "setRenderPass", int.class);
                default -> published(id);
            };
            rows.add(new ShadowHookRow(id, 1, actual,
                    actual == 1 ? HookDisposition.HEALTHY : HookDisposition.FEATURE_DISABLED));
        }
        return ShadowHookHealth.of(rows);
    }

    /** The rows that are not HEALTHY, for the H8-HEALTH-01 evidence line. */
    public static List<String> disabledRows(ShadowHookHealth health) {
        List<String> out = new ArrayList<>();
        for (ShadowHookRow row : health.rows()) {
            if (row.disposition() != HookDisposition.HEALTHY) {
                out.add(row.hookId() + "=" + row.actual());
            }
        }
        return out;
    }

    private static int published(String id) {
        String raw = System.getProperty("schmaloogium.hooks.anchor." + id);
        if (raw == null) {
            return 0;
        }
        try {
            return Math.max(0, Integer.parseInt(raw.strip()));
        } catch (NumberFormatException bad) {
            return 0;
        }
    }

    private static int resolves(String className, String method, Class<?>... parameters) {
        try {
            Class.forName(className).getMethod(method, parameters);
            return 1;
        } catch (ReflectiveOperationException | LinkageError absent) {
            return 0;
        }
    }
}
