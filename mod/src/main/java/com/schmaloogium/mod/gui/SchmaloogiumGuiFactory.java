// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import com.schmaloogium.mod.core.SchmaloogiumMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Collections;
import java.util.Set;

/**
 * The Forge Mods-menu {@code Config} entry point (PHASE_12_DOC §4.8 access surfaces).
 * The button is enabled by declaring this factory in the {@code @Mod} annotation's
 * {@code guiFactory} attribute (Cleanroom's FML reads only that path; the mcmod.info
 * key is inert); selecting it opens the same vanilla pack-selection screen the
 * {@code P} convenience keybind reaches. When the GUI assembly is degraded (missing
 * {@code shaderpacks/} bindings — the same condition {@link ShaderGui#showPackSelection}
 * guards), a minimal notice screen returns instead so the Forge contract (never null)
 * holds without crashing the Mods menu.
 */
public final class SchmaloogiumGuiFactory implements IModGuiFactory {


    @Override
    public void initialize(Minecraft minecraftInstance) {
        // No cached state: the screens are built per open through ShaderGui.
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        SchmaloogiumMod.LOGGER.info("Mods-menu Config invoked; opening pack selection");
        GuiScreen before = Minecraft.getMinecraft().currentScreen;
        ShaderGui.showPackSelection();
        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        // showPackSelection displayed one of our screens iff currentScreen moved off the
        // Mods list (the parent). Otherwise the assembly is degraded — show the notice.
        if (current != null && current != before && current != parentScreen) {
            return current;
        }
        return new DegradedNoticeScreen(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Collections.emptySet();
    }

    /** Minimal notice shown when the pack GUI cannot assemble (degraded bindings). */
    private static final class DegradedNoticeScreen extends GuiScreen {
        private final GuiScreen parent;
        private GuiButton done;

        DegradedNoticeScreen(GuiScreen parent) {
            this.parent = parent;
        }

        @Override
        public void initGui() {
            done = addButton(new GuiButton(0, width / 2 - 100, height / 4 + 120,
                    "Back"));
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button == done) {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            drawCenteredString(fontRenderer, "Schmaloogium", width / 2, height / 4, 0xFFFFFF);
            drawCenteredString(fontRenderer,
                    "Shader pack screen unavailable: shaderpacks directory degraded.",
                    width / 2, height / 4 + 20, 0xFF5555);
            drawCenteredString(fontRenderer,
                    "Create the shaderpacks folder (or check the log) and reopen.",
                    width / 2, height / 4 + 32, 0xAAAAAA);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
