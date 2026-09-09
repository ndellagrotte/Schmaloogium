// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.log.LogLevel;
import com.schmaloogium.engine.log.Logs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The CHAT leaf of the §4.9.4 fan-out, kept in its own {@code @SideOnly(CLIENT)} class
 * so the router itself stays linkable (and headless-testable) without Minecraft types.
 * CHAT becomes a translated chat message via the client player; without a player it
 * downgrades to a log line - never buffered indefinitely, never dropped silently
 * (PHASE_1_DOC §6 failure-mode table).
 */
@SideOnly(Side.CLIENT)
public final class ChatDelivery {

    private ChatDelivery() {
    }

    static void deliver(EngineDiagnostic d) {
        Minecraft client = Minecraft.getMinecraft();
        if (client == null || client.player == null) {
            Logs.sink().emit(d.logChannel(), LogLevel.WARN,
                    "schmaloogium.error.noPlayer", new Object[]{d.messageKey()}, null);
            return;
        }
        ITextComponent line = new TextComponentTranslation(d.messageKey(), d.args().toArray());
        client.player.sendMessage(line);
    }
}
