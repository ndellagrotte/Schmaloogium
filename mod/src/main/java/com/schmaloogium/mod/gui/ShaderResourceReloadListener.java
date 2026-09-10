// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import com.schmaloogium.mod.core.SchmaloogiumMod;
import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ReloadTrigger;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Forwards resource-manager reloads into the reload model (PHASE_12_DOC §4.8.3):
 * a resource-pack change or F3+T emits the {@link ReloadTrigger#RESOURCE_MANAGER_RELOAD}
 * request — {@code NONE} with {@code resourceReacquire} only — so the engine can react
 * without reloading the shader pack. This is a post-replacement notification, never a
 * pre-destructive gate ([D-P12-12]): it never gates on the predicate and never throws —
 * a failing submitter is swallowed and logged so the notification still completes.
 */
@SideOnly(Side.CLIENT)
public final class ShaderResourceReloadListener implements ISelectiveResourceReloadListener {

    private final Consumer<ReloadRequest> submitter;

    /**
     * @param submitter receives the classified {@link ReloadTrigger#RESOURCE_MANAGER_RELOAD} request
     */
    public ShaderResourceReloadListener(Consumer<ReloadRequest> submitter) {
        this.submitter = submitter;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager,
                                        Predicate<IResourceType> resourcePredicate) {
        try {
            submitter.accept(ReloadTrigger.RESOURCE_MANAGER_RELOAD.request());
        } catch (RuntimeException e) {
            SchmaloogiumMod.LOGGER.error(
                    "schmaloogium.error.resource_reload.notify_failed", e);
        }
    }
}
