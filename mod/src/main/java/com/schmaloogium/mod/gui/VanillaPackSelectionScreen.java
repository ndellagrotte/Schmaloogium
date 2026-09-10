// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiSnapshot;
import com.schmaloogium.engine.pack.CompatibilityStatus;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.mod.core.SchmaloogiumMod;
import com.schmaloogium.mod.gui.model.EngineSettingEntry;
import com.schmaloogium.mod.gui.model.EngineSettingsModel;
import com.schmaloogium.mod.gui.model.PackSelectionActions;
import com.schmaloogium.mod.gui.model.PackSelectionModel;
import com.schmaloogium.mod.gui.model.PackSelectionRow;
import com.schmaloogium.mod.gui.model.Tooltip;
import com.schmaloogium.mod.gui.model.TriStateValue;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

/**
 * The vanilla pack-selection screen (PHASE_12_DOC §4.10.2): a fixed-width centred list
 * in Phase 3 discovery order — never re-sorted — with kind badges and the selection
 * marker, a toggled engine-settings pane rendering all seven supplied entries, and the
 * four bottom actions. Every intent goes through the injected {@link
 * PackSelectionActions}; availability, labels and tooltips come from the models
 * verbatim. §6 rung 5: a view-layer exception is logged and closes to the parent.
 */
@SideOnly(Side.CLIENT)
final class VanillaPackSelectionScreen extends GuiScreen {

    private static final int ROW_HEIGHT = 22;
    private static final int ID_OPEN_FOLDER = 1;
    private static final int ID_REFRESH = 2;
    private static final int ID_SETTINGS = 3;
    private static final int ID_DONE = 4;
    private static final int ID_ISSUES = 5;
    private static final int ID_ROW_BASE = 100;
    private static final int ID_SETTING_BASE = 10000;

    private final VanillaOptionScreens owner;
    private PackSelectionModel model;
    private EngineSettingsModel settings;
    private PackSelectionActions actions;

    private boolean settingsOpen;
    private int scrollOffset;

    private int listTop;
    private int listBottom;
    private int listX;
    private int listWidth;
    private int visibleRows;
    private int bottomY;
    private int bottomX;
    private int buttonW;
    private int statusY;
    private int paneTop;
    private int paneX;
    private int paneWidth;

    VanillaPackSelectionScreen(VanillaOptionScreens owner, PackSelectionModel model,
                               EngineSettingsModel settings, PackSelectionActions actions) {
        this.owner = owner;
        this.model = model;
        this.settings = settings;
        this.actions = actions;
    }

    /** Producer delivery: stores the latest snapshots and re-renders from them. */
    void replace(PackSelectionModel model, EngineSettingsModel settings,
                 PackSelectionActions actions) {
        this.model = model;
        this.settings = settings;
        this.actions = actions;
        rebuild();
    }

    /** Producer delivery of the two diagnostic inputs; only the indicator can change. */
    void replaceDiagnostics(List<EngineDiagnostic> shaderGuiDiagnostics,
                            Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics) {
        rebuild();
    }

    @Override
    public void initGui() {
        computeLayout();

        owner.issuesIndicatorLabel().ifPresent(label ->
                addButton(new GuiButton(ID_ISSUES, width - 126, 6, 120, 20, label)));

        List<PackSelectionRow> rows = model.rows();
        for (int slot = 0; slot < visibleRows; slot++) {
            int index = scrollOffset + slot;
            if (index >= rows.size()) {
                break;
            }
            PackSelectionRow row = rows.get(index);
            GuiButton rowButton = new GuiButton(ID_ROW_BASE + index, listX,
                    listTop + slot * ROW_HEIGHT, listWidth, ROW_HEIGHT - 2, rowLabel(row));
            rowButton.enabled = row.interactive();
            addButton(rowButton);
        }

        addButton(new GuiButton(ID_OPEN_FOLDER, bottomX, bottomY, buttonW, 20, "Open folder"));
        addButton(new GuiButton(ID_REFRESH, bottomX + (buttonW + 4), bottomY, buttonW, 20,
                "Refresh"));
        addButton(new GuiButton(ID_SETTINGS, bottomX + 2 * (buttonW + 4), bottomY, buttonW, 20,
                "Shader pack settings..."));
        addButton(new GuiButton(ID_DONE, bottomX + 3 * (buttonW + 4), bottomY, buttonW, 20,
                "Done"));

        if (settingsOpen) {
            List<EngineSettingEntry> entries = settings.entries();
            int entryY = paneTop + 14;
            for (int i = 0; i < entries.size(); i++) {
                GuiButton entryButton = new GuiButton(ID_SETTING_BASE + i, paneX, entryY,
                        paneWidth, 20, settingLabel(entries.get(i)));
                entryButton.enabled = entries.get(i).interactive();
                addButton(entryButton);
                entryY += ROW_HEIGHT;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == ID_DONE) {
            guard("done", actions::close);
        } else if (button.id == ID_OPEN_FOLDER) {
            guard("open folder", actions::openPackFolder);
        } else if (button.id == ID_REFRESH) {
            guard("refresh", actions::refresh);
        } else if (button.id == ID_SETTINGS) {
            settingsOpen = !settingsOpen;
            rebuild();
        } else if (button.id == ID_ISSUES) {
            owner.openDiagnostics(this);
        } else if (button.id >= ID_ROW_BASE && button.id < ID_ROW_BASE + model.rows().size()) {
            PackSelectionRow row = model.rows().get(button.id - ID_ROW_BASE);
            guard("select pack", () -> actions.selectCandidate(row.id()));
        } else if (button.id >= ID_SETTING_BASE
                && button.id < ID_SETTING_BASE + settings.entries().size()) {
            fireSettingIntent(settings.entries().get(button.id - ID_SETTING_BASE));
        }
    }

    /** Sends the three exact-value engine intents through the supplied actions only. */
    private void fireSettingIntent(EngineSettingEntry entry) {
        if (!entry.interactive()) {
            return;
        }
        if (entry instanceof EngineSettingEntry.Toggle toggle) {
            boolean next = !toggle.value();
            guard("engine toggle", () -> {
                actions.setEngineToggle(toggle.key(), next);
                rebuild();
            });
        } else if (entry instanceof EngineSettingEntry.TriState triState) {
            TriStateValue[] ladder = TriStateValue.values();
            TriStateValue next = ladder[(triState.value().ordinal() + 1) % ladder.length];
            guard("engine tri-state", () -> {
                actions.setEngineTriState(triState.key(), next);
                rebuild();
            });
        } else if (entry instanceof EngineSettingEntry.Choice choice
                && !choice.allowedValues().isEmpty()) {
            int next = choice.valueIndex() < 0
                    ? 0 : (choice.valueIndex() + 1) % choice.allowedValues().size();
            String token = choice.allowedValues().get(next);
            guard("engine choice", () -> {
                actions.setEngineChoice(choice.key(), token);
                rebuild();
            });
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            guard("close", actions::close);
            return;
        }
        if (keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_DOWN) {
            scrollOffset += keyCode == Keyboard.KEY_UP ? -1 : 1;
            rebuild();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scrollOffset += wheel > 0 ? -2 : 2;
            rebuild();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, "Shader Pack Selection", width / 2, 8, 0xFFFFFF);
        model.lastActionSummary().ifPresent(summary ->
                drawCenteredString(fontRenderer, summary, width / 2, statusY, 0xE0E0E0));
        if (settingsOpen) {
            drawCenteredString(fontRenderer, "Engine Settings", width / 2, paneTop, 0xFFFF55);
        }
        drawHoverTooltips(mouseX, mouseY);
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        List<PackSelectionRow> rows = model.rows();
        for (int slot = 0; slot < visibleRows; slot++) {
            int index = scrollOffset + slot;
            if (index >= rows.size()) {
                break;
            }
            int y = listTop + slot * ROW_HEIGHT;
            if (VanillaOptionScreens.hovered(mouseX, mouseY, listX, y, listWidth, ROW_HEIGHT - 2)) {
                List<String> lines = rowTooltip(rows.get(index));
                if (!lines.isEmpty()) {
                    drawHoveringText(lines, mouseX, mouseY);
                }
                return;
            }
        }
        if (settingsOpen) {
            List<EngineSettingEntry> entries = settings.entries();
            for (int i = 0; i < entries.size(); i++) {
                int y = paneTop + 14 + i * ROW_HEIGHT;
                if (VanillaOptionScreens.hovered(mouseX, mouseY, paneX, y, paneWidth, 20)) {
                    List<String> lines =
                            VanillaOptionScreens.tooltipTexts(tooltipOf(entries.get(i)));
                    if (!lines.isEmpty()) {
                        drawHoveringText(lines, mouseX, mouseY);
                    }
                    return;
                }
            }
        }
    }

    private static Tooltip tooltipOf(EngineSettingEntry entry) {
        if (entry instanceof EngineSettingEntry.Toggle toggle) {
            return toggle.tooltip();
        }
        if (entry instanceof EngineSettingEntry.TriState triState) {
            return triState.tooltip();
        }
        if (entry instanceof EngineSettingEntry.Choice choice) {
            return choice.tooltip();
        }
        return Tooltip.EMPTY;
    }

    private String rowLabel(PackSelectionRow row) {
        String marker = row.id().equals(model.selected()) ? "> " : "   ";
        String label = marker + badge(row.kind()) + " " + row.displayName();
        return VanillaOptionScreens.ellipsize(fontRenderer, label, listWidth - 8);
    }

    private static String badge(PackCandidateKind kind) {
        return switch (kind) {
            case OFF -> "(off)";
            case INTERNAL -> "(internal)";
            case DIRECTORY -> "[D]";
            case ARCHIVE -> "[Z]";
        };
    }

    private static String settingLabel(EngineSettingEntry entry) {
        if (entry instanceof EngineSettingEntry.Toggle toggle) {
            return toggle.label() + ": " + (toggle.value() ? "On" : "Off");
        }
        if (entry instanceof EngineSettingEntry.TriState triState) {
            return triState.label() + ": " + switch (triState.value()) {
                case DEFAULT -> "Default";
                case ON -> "On";
                case OFF -> "Off";
            };
        }
        if (entry instanceof EngineSettingEntry.Choice choice) {
            return choice.label() + ": " + choice.rawValue();
        }
        return entry.key();
    }

    private static List<String> rowTooltip(PackSelectionRow row) {
        List<String> lines = new ArrayList<>();
        if (row.compatibility().isPresent()) {
            lines.add(row.compatibility().get() == CompatibilityStatus.COMPATIBLE
                    ? "Compatible" : "Requires a newer edition");
        }
        for (EngineDiagnostic diagnostic : row.diagnostics()) {
            lines.add(diagnostic.severity() + " " + diagnostic.messageKey());
        }
        return lines;
    }

    private void computeLayout() {
        bottomY = height - 28;
        statusY = bottomY - 12;
        buttonW = (width - 20 - 12) / 4;
        bottomX = (width - (4 * buttonW + 12)) / 2;
        listWidth = Math.min(320, width - 20);
        listX = (width - listWidth) / 2;
        listTop = 26;
        paneWidth = Math.min(280, width - 40);
        paneX = (width - paneWidth) / 2;
        paneTop = 26;
        int paneBottom = paneTop + 14 + settings.entries().size() * ROW_HEIGHT + 4;
        listBottom = settingsOpen ? paneBottom + 8 : statusY - 6;
        visibleRows = Math.max(1, (listBottom - listTop) / ROW_HEIGHT);
        clampScroll();
    }

    private void clampScroll() {
        int max = Math.max(0, model.rows().size() - visibleRows);
        scrollOffset = Math.max(0, Math.min(scrollOffset, max));
    }

    private void rebuild() {
        buttonList.clear();
        initGui();
    }

    private void guard(String what, Runnable intent) {
        try {
            intent.run();
        } catch (Exception e) {
            SchmaloogiumMod.LOGGER.error(
                    "Shader GUI: pack selection {} failed; closing the screen", what, e);
            owner.close();
        }
    }

    @Override
    public void onGuiClosed() {
        owner.release(this);
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
