// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import com.schmaloogium.mod.gui.model.ReloadCoordinator;
import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ReloadTrigger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The {@code /reloadshaders} client command (PHASE_12_DOC §4.8.2). The assembly
 * registers it once during client init against
 * {@code ClientCommandHandler.instance.registerCommand(ICommand)}, where client
 * commands take precedence over identically named server commands.
 *
 * <p>The reply is a local acknowledgement built from the request just classified:
 * one chat line naming the lifecycle and the cause, or the inert notice when no
 * coordinator is installed (§6). The reload's result is not a command return value —
 * a successful reload is silent (§4.8.2).
 */
@SideOnly(Side.CLIENT)
public final class ReloadShadersCommand extends CommandBase {

    /** The command name, identical semantics to F3+R by contract (RESEARCH §4.7). */
    static final String NAME = "reloadshaders";

    private final Consumer<ReloadRequest> submitter;
    private final BiConsumer<String, Object[]> chatReply;

    /**
     * @param submitter receives the classified {@link ReloadTrigger#RELOAD_SHADERS_COMMAND} request
     * @param chatReply receives the reply message key plus its translation args
     */
    public ReloadShadersCommand(Consumer<ReloadRequest> submitter,
                                BiConsumer<String, Object[]> chatReply) {
        this.submitter = submitter;
        this.chatReply = chatReply;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + NAME;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ReloadRequest request = ReloadTrigger.RELOAD_SHADERS_COMMAND.request();
        submitter.accept(request);
        chatReply.accept(replyKey(ReloadCoordinator.installed().isPresent()),
                new Object[]{request.lifecycle().name(), request.cause().name()});
    }

    /**
     * The reply decision lives MC-free in {@link ReloadTrigger#replyKey}; this
     * delegate only keeps the command's call sites short.
     */
    static String replyKey(boolean coordinatorInstalled) {
        return com.schmaloogium.mod.gui.model.ReloadTrigger
                .replyKey(coordinatorInstalled);
    }
}
