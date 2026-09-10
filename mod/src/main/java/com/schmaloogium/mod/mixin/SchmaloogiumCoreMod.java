// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin;

import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

/**
 * The dev-environment early registration point for the mixin configs (§4.5 deviation
 * note). Production jars register the three configs through the {@code MixinConfigs}
 * manifest attribute; the dev run loads this mod from classes/resource directories,
 * which carry no manifest, so MixinBooter's early-loader scan is used instead. This
 * runs during MixinBooter's {@code injectData} — before any mod constructs and before
 * {@code OpenGlHelper} (the preinit config's target) class-loads.
 *
 * <p>This class is a coremod entry: it must stay free of references to mod/engine
 * classes so it loads untransformed through the {@code LaunchClassLoader} exclusions.
 * It transforms nothing; its only job is naming the three config resources.
 */
@IFMLLoadingPlugin.Name("schmaloogium-core")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1000)
public final class SchmaloogiumCoreMod implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return List.of(
            "schmaloogium.preinit.mixin.json",
            "schmaloogium.default.mixin.json",
            "schmaloogium.mod.mixin.json");
    }
}
