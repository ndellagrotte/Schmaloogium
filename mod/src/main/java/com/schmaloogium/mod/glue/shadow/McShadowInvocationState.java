// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import net.minecraft.client.renderer.RenderGlobal;

import java.util.List;

/**
 * One shadow invocation's borrowed main-traversal state (PHASE_8_DOC §4.7 step 2/6): the
 * exact main {@code renderInfos} reference captured before the forced shadow setup, put back
 * by the outer state lease's {@code restore()} on every terminal path. Created at
 * {@code openState}, filled by {@code setupTerrain}, consumed exactly once.
 */
final class McShadowInvocationState {

    private RenderGlobal receiver;
    private List<?> mainRenderInfos;
    private boolean restored;

    void captureMainList(RenderGlobal renderGlobal, List<?> renderInfos) {
        this.receiver = renderGlobal;
        this.mainRenderInfos = renderInfos;
    }

    boolean hasMainList() {
        return mainRenderInfos != null && !restored;
    }

    /** Puts the main list back; idempotent. */
    void restoreMainList() {
        if (restored || mainRenderInfos == null) {
            return;
        }
        restored = true;
        ((RenderGlobalShadowAccess) receiver).schmaloogium$setRenderInfos(mainRenderInfos);
    }
}
