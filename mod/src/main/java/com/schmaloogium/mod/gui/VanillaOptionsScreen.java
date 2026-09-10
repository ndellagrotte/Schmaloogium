// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiSnapshot;
import com.schmaloogium.mod.core.SchmaloogiumMod;
import com.schmaloogium.mod.gui.model.ApplyOutcome;
import com.schmaloogium.mod.gui.model.OptionActionAvailability;
import com.schmaloogium.mod.gui.model.OptionApplyStatus;
import com.schmaloogium.mod.gui.model.OptionEditSession;
import com.schmaloogium.mod.gui.model.OptionId;
import com.schmaloogium.mod.gui.model.OptionPresentationModel;
import com.schmaloogium.mod.gui.model.OptionSessionAvailability;
import com.schmaloogium.mod.gui.model.PresentationEntry;
import com.schmaloogium.mod.gui.model.PresentationScreen;
import com.schmaloogium.mod.gui.model.ScreenId;
import com.schmaloogium.mod.gui.model.Tooltip;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

/**
 * The vanilla options screen (PHASE_12_DOC §4.10.2): a grid of {@code
 * resolvedColumns} buttons per row with a scroll window computed from the screen
 * height, Back/Reset/Apply/Done along the bottom bound by the supplied session
 * availability, the pending-profile summary line beside the unsaved count, and vanilla
 * hover tooltips with WARNING lines in red. Navigation below the root is view-internal;
 * every edit intent goes through the injected {@link OptionEditSession} and the widgets
 * are then refreshed from {@code session.present(currentScreen)}. A view-layer
 * exception is logged and closes to the parent (§6 rung 5).
 */
@SideOnly(Side.CLIENT)
final class VanillaOptionsScreen extends GuiScreen {

    private static final int ROW_HEIGHT = 22;
    private static final int ID_BACK = 1;
    private static final int ID_RESET = 2;
    private static final int ID_APPLY = 3;
    private static final int ID_DONE = 4;
    private static final int ID_ISSUES = 5;
    private static final int ID_CELL_BASE = 100;

    private final VanillaOptionScreens owner;
    private OptionPresentationModel model;
    private OptionEditSession session;
    private ScreenId currentScreen;
    private final Deque<ScreenId> navStack = new ArrayDeque<>();

    private int scrollOffset;
    private int columns;
    private int cellW;
    private int gridX;
    private int listTop;
    private int listBottom;
    private int visibleRows;
    private int gridRows;
    private int bottomY;
    private int bottomX;
    private int buttonW;
    private int statusTop;

    VanillaOptionsScreen(VanillaOptionScreens owner, OptionPresentationModel model,
                         ScreenId screen, OptionEditSession session) {
        this.owner = owner;
        this.model = model;
        this.currentScreen = screen;
        this.session = session;
    }

    /** Producer delivery: stores the latest snapshots and re-renders at that position. */
    void replace(OptionPresentationModel model, ScreenId screen, OptionEditSession session) {
        this.model = model;
        this.session = session;
        if (!screen.equals(currentScreen)) {
            currentScreen = screen;
            navStack.clear();
            scrollOffset = 0;
        }
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

        List<PresentationEntry> entries = screen().entries();
        int firstCell = scrollOffset * columns;
        int lastCell = Math.min(entries.size(), (scrollOffset + visibleRows) * columns);
        for (int cell = firstCell; cell < lastCell; cell++) {
            PresentationEntry entry = entries.get(cell);
            if (entry instanceof PresentationEntry.Blank) {
                continue;
            }
            int col = cell % columns;
            int row = cell / columns - scrollOffset;
            GuiButton cellButton = new GuiButton(ID_CELL_BASE + cell,
                    gridX + col * (cellW + 4), listTop + row * ROW_HEIGHT, cellW, 20,
                    cellLabel(entry));
            cellButton.enabled = cellEnabled(entry);
            addButton(cellButton);
        }

        addButton(new GuiButton(ID_BACK, bottomX, bottomY, buttonW, 20, "Back"));
        GuiButton reset = new GuiButton(ID_RESET, bottomX + (buttonW + 4), bottomY, buttonW, 20,
                "Reset");
        GuiButton apply = new GuiButton(ID_APPLY, bottomX + 2 * (buttonW + 4), bottomY, buttonW,
                20, "Apply");
        GuiButton done = new GuiButton(ID_DONE, bottomX + 3 * (buttonW + 4), bottomY, buttonW, 20,
                "Done");
        OptionSessionAvailability availability = model.availability();
        reset.enabled = availability.reset().enabled();
        apply.enabled = availability.apply().enabled();
        done.enabled = availability.done().enabled();
        addButton(reset);
        addButton(apply);
        addButton(done);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == ID_BACK) {
            back();
        } else if (button.id == ID_RESET) {
            guard("reset", () -> {
                session.resetToPackDefaults();
                refreshFromSession();
            });
        } else if (button.id == ID_APPLY) {
            guard("apply", () -> {
                session.apply();
                refreshFromSession();
            });
        } else if (button.id == ID_DONE) {
            done();
        } else if (button.id == ID_ISSUES) {
            owner.openDiagnostics(this);
        } else if (button.id >= ID_CELL_BASE
                && button.id - ID_CELL_BASE < screen().entries().size()) {
            actOnCell(screen().entries().get(button.id - ID_CELL_BASE));
        }
    }

    private void actOnCell(PresentationEntry entry) {
        if (entry instanceof PresentationEntry.SwitchOption option) {
            if (!option.interactive()) {
                return;
            }
            guard("toggle option", () -> {
                session.toggle(option.id());
                refreshFromSession();
            });
        } else if (entry instanceof PresentationEntry.ValueOption option) {
            cycleValue(option.id(), option.interactive(), isShiftKeyDown() ? -1 : 1);
        } else if (entry instanceof PresentationEntry.SliderOption option) {
            cycleValue(option.id(), option.interactive(), isShiftKeyDown() ? -1 : 1);
        } else if (entry instanceof PresentationEntry.ProfileCycle option) {
            if (!option.applicable() || !option.availability().enabled()) {
                return;
            }
            guard("cycle profile", () -> {
                session.cycleProfile();
                refreshFromSession();
            });
        } else if (entry instanceof PresentationEntry.SubScreenLink option) {
            if (!option.resolved()) {
                return;
            }
            navStack.push(currentScreen);
            currentScreen = option.target();
            scrollOffset = 0;
            guard("open subscreen", this::refreshFromSession);
        }
    }

    private void cycleValue(OptionId id, boolean interactive, int step) {
        if (!interactive) {
            return;
        }
        guard("cycle option", () -> {
            session.cycle(id, step);
            refreshFromSession();
        });
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 1) {
            return;
        }
        for (GuiButton button : buttonList) {
            if (button.id < ID_CELL_BASE || !button.visible) {
                continue;
            }
            if (VanillaOptionScreens.hovered(mouseX, mouseY, button.x, button.y, button.width,
                    button.height)) {
                cycleBackwards(screen().entries().get(button.id - ID_CELL_BASE));
                return;
            }
        }
    }

    private void cycleBackwards(PresentationEntry entry) {
        if (entry instanceof PresentationEntry.ValueOption option) {
            cycleValue(option.id(), option.interactive(), -1);
        } else if (entry instanceof PresentationEntry.SliderOption option) {
            cycleValue(option.id(), option.interactive(), -1);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            back();
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
            scrollOffset += wheel > 0 ? -1 : 1;
            rebuild();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, screen().title(), width / 2, 8, 0xFFFFFF);
        int y = statusTop;
        drawCenteredString(fontRenderer, "Unsaved changes: " + session.pendingChangeCount(),
                width / 2, y, 0xE0E0E0);
        y += 10;
        if (model.pendingProfileSummary().isPresent()) {
            drawCenteredString(fontRenderer, model.pendingProfileSummary().get().displayText(),
                    width / 2, y, 0xFFFF55);
            y += 10;
        }
        OptionActionAvailability mutation = model.availability().mutation();
        if (!mutation.enabled() && mutation.disabledReason().isPresent()) {
            drawCenteredString(fontRenderer, mutation.disabledReason().get(), width / 2, y,
                    0xFF8080);
        }
        drawHoverTooltips(mouseX, mouseY);
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        List<PresentationEntry> entries = screen().entries();
        for (GuiButton button : buttonList) {
            if (button.id < ID_CELL_BASE || !button.visible) {
                continue;
            }
            if (VanillaOptionScreens.hovered(mouseX, mouseY, button.x, button.y, button.width,
                    button.height)) {
                drawCellTooltip(entries.get(button.id - ID_CELL_BASE), mouseX, mouseY);
                return;
            }
        }
        OptionSessionAvailability availability = model.availability();
        if (hoverReason(ID_RESET, availability.reset(), mouseX, mouseY)
                || hoverReason(ID_APPLY, availability.apply(), mouseX, mouseY)) {
            return;
        }
        hoverReason(ID_DONE, availability.done(), mouseX, mouseY);
    }

    private boolean hoverReason(int buttonId, OptionActionAvailability availability,
                                int mouseX, int mouseY) {
        if (availability.enabled() || availability.disabledReason().isEmpty()) {
            return false;
        }
        for (GuiButton button : buttonList) {
            if (button.id == buttonId && VanillaOptionScreens.hovered(mouseX, mouseY, button.x,
                    button.y, button.width, button.height)) {
                drawHoveringText(List.of(availability.disabledReason().get()), mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    private void drawCellTooltip(PresentationEntry entry, int mouseX, int mouseY) {
        List<String> texts = new ArrayList<>(VanillaOptionScreens.tooltipTexts(tooltipOf(entry)));
        if (entry instanceof PresentationEntry.ProfileCycle option
                && !cellEnabled(entry)
                && option.availability().disabledReason().isPresent()) {
            texts.add(TextFormatting.RED + option.availability().disabledReason().get());
        }
        if (!texts.isEmpty()) {
            drawHoveringText(texts, mouseX, mouseY);
        }
    }

    private static Tooltip tooltipOf(PresentationEntry entry) {
        if (entry instanceof PresentationEntry.SwitchOption option) {
            return option.tooltip();
        }
        if (entry instanceof PresentationEntry.ValueOption option) {
            return option.tooltip();
        }
        if (entry instanceof PresentationEntry.SliderOption option) {
            return option.tooltip();
        }
        if (entry instanceof PresentationEntry.ProfileCycle option) {
            return option.tooltip();
        }
        if (entry instanceof PresentationEntry.SubScreenLink option) {
            return option.tooltip();
        }
        return Tooltip.EMPTY;
    }

    private static String cellLabel(PresentationEntry entry) {
        if (entry instanceof PresentationEntry.SwitchOption option) {
            return option.label() + ": " + (option.value() ? "On" : "Off");
        }
        if (entry instanceof PresentationEntry.ValueOption option) {
            return option.label() + ": " + option.displayValue();
        }
        if (entry instanceof PresentationEntry.SliderOption option) {
            return option.label() + ": " + option.displayValue();
        }
        if (entry instanceof PresentationEntry.ProfileCycle option) {
            return option.label();
        }
        if (entry instanceof PresentationEntry.SubScreenLink option) {
            return option.label();
        }
        return "";
    }

    private static boolean cellEnabled(PresentationEntry entry) {
        if (entry instanceof PresentationEntry.SwitchOption option) {
            return option.interactive();
        }
        if (entry instanceof PresentationEntry.ValueOption option) {
            return option.interactive();
        }
        if (entry instanceof PresentationEntry.SliderOption option) {
            return option.interactive();
        }
        if (entry instanceof PresentationEntry.ProfileCycle option) {
            return option.applicable() && option.availability().enabled();
        }
        if (entry instanceof PresentationEntry.SubScreenLink option) {
            return option.resolved();
        }
        return false;
    }

    /** The screen at the view's current navigation position. */
    private PresentationScreen screen() {
        if (currentScreen.isMain()) {
            return model.mainScreen();
        }
        PresentationScreen sub = model.subScreens().get(currentScreen);
        return sub != null ? sub : model.mainScreen();
    }

    /** Escape and Back: pop a subscreen; at the root discard, then close. */
    private void back() {
        if (!navStack.isEmpty()) {
            currentScreen = navStack.pop();
            scrollOffset = 0;
            guard("back", this::refreshFromSession);
        } else {
            guard("discard", () -> {
                session.discard();
                owner.close();
            });
        }
    }

    private void done() {
        try {
            if (session.isDirty()) {
                ApplyOutcome outcome = session.apply();
                OptionApplyStatus status = outcome.status();
                if (status == OptionApplyStatus.UNCHANGED
                        || status == OptionApplyStatus.PERSISTED
                        || status == OptionApplyStatus.SESSION_ACCEPTED) {
                    owner.close();
                    return;
                }
                refreshFromSession();
            } else {
                owner.close();
            }
        } catch (Exception e) {
            SchmaloogiumMod.LOGGER.error("Shader GUI: done failed; closing the screen", e);
            owner.close();
        }
    }

    /** §4.10.1: after every intent, refresh from the session's freshly built model. */
    private void refreshFromSession() {
        model = session.present(currentScreen);
        rebuild();
    }

    private void computeLayout() {
        columns = Math.max(1, screen().resolvedColumns());
        cellW = Math.min(200, Math.max(60, (width - 40) / columns - 4));
        gridX = (width - (columns * (cellW + 4) - 4)) / 2;
        bottomY = height - 28;
        buttonW = (width - 20 - 12) / 4;
        bottomX = (width - (4 * buttonW + 12)) / 2;
        statusTop = bottomY - 34;
        listTop = 26;
        listBottom = statusTop - 6;
        visibleRows = Math.max(1, (listBottom - listTop) / ROW_HEIGHT);
        gridRows = (screen().entries().size() + columns - 1) / columns;
        clampScroll();
    }

    private void clampScroll() {
        int max = Math.max(0, gridRows - visibleRows);
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
            SchmaloogiumMod.LOGGER.error("Shader GUI: options screen {} failed; closing", what, e);
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
