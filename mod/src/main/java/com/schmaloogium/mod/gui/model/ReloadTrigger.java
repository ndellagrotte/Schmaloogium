// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * The trigger × lifecycle matrix of PHASE_12_DOC §4.7.3 as data. Every trigger this
 * phase owns is an enum constant carrying exactly one classification, so an
 * unclassified trigger is a compile failure, not a silent NONE. The dimension switch is
 * deliberately absent — it is Phase 7's trigger, not ours.
 *
 * <p>Bake-set-dependent {@code worldRendererReload} rows (option/profile/reset apply,
 * old-lighting) classify {@code REPUBLISH} with the flag false here: Phase 7 computes
 * the post-load bake predicate [D-P12-11] and ORs it in at drain.
 */
public enum ReloadTrigger {
    F3R_KEYBIND(ReloadLifecycle.FULL, true, false, ReloadCause.KEYBIND),
    RELOAD_SHADERS_COMMAND(ReloadLifecycle.FULL, true, false, ReloadCause.COMMAND),
    RESOURCE_MANAGER_RELOAD(ReloadLifecycle.NONE, false, true, ReloadCause.RESOURCE_RELOAD),
    PACK_SELECTION_CHANGED(ReloadLifecycle.FULL, true, false, ReloadCause.PACK_SELECTION),
    OPTION_APPLY_OR_DONE_DIRTY(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.OPTION_APPLY),
    PROFILE_APPLIED(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.PROFILE_APPLY),
    RESET_TO_PACK_DEFAULTS(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.OPTION_RESET),
    ENGINE_NORMAL_OR_SPECULAR_MAP(ReloadLifecycle.REPUBLISH, false, true, ReloadCause.ENGINE_SETTING),
    ENGINE_RENDER_OR_SHADOW_RES(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.ENGINE_SETTING),
    ENGINE_HAND_DEPTH(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.ENGINE_SETTING),
    ENGINE_OLD_HAND_LIGHT(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.ENGINE_SETTING),
    ENGINE_OLD_LIGHTING(ReloadLifecycle.REPUBLISH, false, false, ReloadCause.ENGINE_SETTING),
    NAVIGATION_SCROLL_HOVER_DISCARD(ReloadLifecycle.NONE, false, false, null);

    private static final Map<ReloadCause, ReloadTrigger> BY_CAUSE =
            new EnumMap<>(ReloadCause.class);

    private final ReloadLifecycle lifecycle;
    private final boolean worldRendererReload;
    private final boolean resourceReacquire;
    private final ReloadCause cause;

    ReloadTrigger(ReloadLifecycle lifecycle, boolean worldRendererReload,
                  boolean resourceReacquire, ReloadCause cause) {
        this.lifecycle = lifecycle;
        this.worldRendererReload = worldRendererReload;
        this.resourceReacquire = resourceReacquire;
        this.cause = cause;
    }

    public ReloadRequest request() {
        return new ReloadRequest(lifecycle, worldRendererReload, resourceReacquire, cause);
    }

    /** The cause constants classify one-to-one onto triggers that submit requests. */
    static {
        for (ReloadTrigger t : values()) {
            if (t.cause != null) {
                BY_CAUSE.put(t.cause, t);
            }
        }
    }

    /** The trigger owning the given cause, for classification keyed by cause. */
    public static ReloadTrigger byCause(ReloadCause cause) {
        return BY_CAUSE.get(cause);
    }

    /**
     * The {@code /reloadshaders} reply classification (§4.8.2), MC-free so tests can
     * pin it: done naming lifecycle + cause when a coordinator is installed, the
     * inert notice otherwise.
     */
    public static String replyKey(boolean coordinatorInstalled) {
        return coordinatorInstalled ? "schmaloogium.command.reloadshaders.done"
                : "schmaloogium.command.reloadshaders.inert";
    }

    /**
     * The F3+R chord gate (§4.8.1, D-P12-13): engine active, no screen open, F3 held
     * and R freshly transitioned to pressed. Pure function; the client binding only
     * feeds it live keyboard state.
     */
    public static boolean chordActivated(boolean engineActive, boolean anyScreenOpen,
                                         boolean f3Down, boolean rDown, boolean rWasDown) {
        return engineActive && !anyScreenOpen && f3Down && rDown && !rWasDown;
    }
}
