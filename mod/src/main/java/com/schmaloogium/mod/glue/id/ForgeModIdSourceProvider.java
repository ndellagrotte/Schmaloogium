// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.ModIdSourceSnapshot;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.schmaloogium.engine.log.Log;

import java.util.ArrayList;
import java.util.List;

/** The Forge-facing side of {@link ModSourceReader}: active mod containers → roots. */
public final class ForgeModIdSourceProvider {

    private static final Log LOG = Logs.channel(LogChannels.IDS);

    private ForgeModIdSourceProvider() {
    }

    public static ModIdSourceSnapshot snapshot() {
        List<ModSourceReader.ModRoot> roots = new ArrayList<>();
        try {
            for (ModContainer container : Loader.instance().getActiveModList()) {
                if (container.getSource() == null) {
                    continue;
                }
                roots.add(new ModSourceReader.ModRoot(container.getModId(),
                        container.getSource().toPath()));
            }
        } catch (RuntimeException failure) {
            LOG.warn("H9-IDS-00 mod source enumeration failed: {}", failure.toString());
            return ModIdSourceSnapshot.empty();
        }
        try {
            return ModSourceReader.read(roots,
                    (modId, reason) -> LOG.warn("H9-IDS-00 mod {} contributes no id rules: {}", modId, reason));
        } catch (RuntimeException failure) {
            LOG.warn("H9-IDS-00 mod source snapshot rejected: {}", failure.toString());
            return ModIdSourceSnapshot.empty();
        }
    }
}
