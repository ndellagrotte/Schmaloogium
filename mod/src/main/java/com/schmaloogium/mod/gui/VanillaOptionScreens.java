// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiSnapshot;
import com.schmaloogium.mod.gui.model.EngineSettingsModel;
import com.schmaloogium.mod.gui.model.OptionEditSession;
import com.schmaloogium.mod.gui.model.OptionPresentationModel;
import com.schmaloogium.mod.gui.model.PackSelectionActions;
import com.schmaloogium.mod.gui.model.PackSelectionModel;
import com.schmaloogium.mod.gui.model.ScreenId;
import com.schmaloogium.mod.gui.model.Tooltip;
import com.schmaloogium.mod.gui.model.TooltipLine;
import com.schmaloogium.mod.gui.model.TooltipSeverity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The vanilla {@link GuiScreen} view — the OQ-9 fallback (PHASE_12_DOC §4.10.2) that
 * always ships. It owns the current screen, forwards every producer delivery into that
 * screen as an immutable snapshot replacement, and makes no contract decision (§4.10.1):
 * gates, reasons, labels and tooltips arrive resolved from the models and are rendered
 * verbatim.
 */
@SideOnly(Side.CLIENT)
public final class VanillaOptionScreens implements OptionScreenView {

    private final Minecraft client;

    private List<EngineDiagnostic> shaderGuiDiagnostics = List.of();
    private Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics = Optional.empty();

    private VanillaPackSelectionScreen packSelectionScreen;
    private VanillaOptionsScreen optionsScreen;
    private DiagnosticsPanelScreen diagnosticsScreen;
    private Runnable packSelectionRepublisher = () -> { };

    public VanillaOptionScreens(Minecraft client) {
        this.client = client;
    }

    /**
     * Installs the producer's re-publish trigger. The pack screen renders one immutable
     * snapshot, so after an intent mutates controller state it must be handed a fresh
     * model; only the producer (which owns the controllers) can build one.
     */
    public void installPackSelectionRepublisher(Runnable republisher) {
        this.packSelectionRepublisher = java.util.Objects.requireNonNull(republisher);
    }

    /** Re-publishes the pack-selection snapshots after an intent (screen-internal). */
    void refreshPackSelection() {
        packSelectionRepublisher.run();
    }

    @Override
    public void showPackSelection(PackSelectionModel model, EngineSettingsModel settings,
                                  PackSelectionActions actions) {
        if (packSelectionScreen != null) {
            packSelectionScreen.replace(model, settings, actions);
            if (client.currentScreen != packSelectionScreen
                    && client.currentScreen != diagnosticsScreen) {
                client.displayGuiScreen(packSelectionScreen);
            }
            return;
        }
        packSelectionScreen = new VanillaPackSelectionScreen(this, model, settings, actions);
        client.displayGuiScreen(packSelectionScreen);
    }

    @Override
    public void showOptions(OptionPresentationModel model, ScreenId screen,
                            OptionEditSession session) {
        if (optionsScreen != null) {
            optionsScreen.replace(model, screen, session);
            if (client.currentScreen != optionsScreen
                    && client.currentScreen != diagnosticsScreen) {
                client.displayGuiScreen(optionsScreen);
            }
            return;
        }
        optionsScreen = new VanillaOptionsScreen(this, model, screen, session);
        client.displayGuiScreen(optionsScreen);
    }

    @Override
    public void showErrors(List<EngineDiagnostic> shaderGuiDiagnostics,
                           Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics) {
        this.shaderGuiDiagnostics = List.copyOf(shaderGuiDiagnostics);
        this.expressionDiagnostics = expressionDiagnostics == null
                ? Optional.empty() : expressionDiagnostics;
        if (packSelectionScreen != null) {
            packSelectionScreen.replaceDiagnostics(shaderGuiDiagnostics, expressionDiagnostics);
        }
        if (optionsScreen != null) {
            optionsScreen.replaceDiagnostics(shaderGuiDiagnostics, expressionDiagnostics);
        }
        if (diagnosticsScreen != null) {
            diagnosticsScreen.replaceDiagnostics(shaderGuiDiagnostics, expressionDiagnostics);
        }
    }

    @Override
    public void close() {
        client.displayGuiScreen(null);
    }

    /** The stored P1 producer list; both screens and the panel render from it. */
    List<EngineDiagnostic> shaderGuiDiagnostics() {
        return shaderGuiDiagnostics;
    }

    /** The stored optional P11 final-attempt snapshot, carried verbatim. */
    Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics() {
        return expressionDiagnostics;
    }

    /** The shared issues indicator label: count plus worst P1 severity; empty hides it. */
    Optional<String> issuesIndicatorLabel() {
        if (shaderGuiDiagnostics.isEmpty()) {
            return Optional.empty();
        }
        DiagnosticSeverity worst = shaderGuiDiagnostics.get(0).severity();
        for (EngineDiagnostic diagnostic : shaderGuiDiagnostics) {
            if (diagnostic.severity().ordinal() > worst.ordinal()) {
                worst = diagnostic.severity();
            }
        }
        return Optional.of("Issues: " + shaderGuiDiagnostics.size() + " (" + worst + ")");
    }

    /** Opens the diagnostics panel over {@code parent}. */
    void openDiagnostics(GuiScreen parent) {
        diagnosticsScreen = new DiagnosticsPanelScreen(this, parent,
                shaderGuiDiagnostics, expressionDiagnostics);
        client.displayGuiScreen(diagnosticsScreen);
    }

    /** Called by every screen from {@code onGuiClosed}; drops the kept instance. */
    void release(GuiScreen screen) {
        if (packSelectionScreen == screen) {
            packSelectionScreen = null;
        }
        if (optionsScreen == screen) {
            optionsScreen = null;
        }
        if (diagnosticsScreen == screen) {
            diagnosticsScreen = null;
        }
    }

    /** Ellipsis-trims {@code text} to {@code maxWidth} pixels. */
    static String ellipsize(FontRenderer font, String text, int maxWidth) {
        if (font.getStringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        return font.trimStringToWidth(text, maxWidth - font.getStringWidth(ellipsis)) + ellipsis;
    }

    /** Tooltip as plain strings: WARNING lines go red, the text itself stays verbatim. */
    static List<String> tooltipTexts(Tooltip tooltip) {
        List<String> texts = new ArrayList<>(tooltip.lines().size());
        for (TooltipLine line : tooltip.lines()) {
            texts.add(line.severity() == TooltipSeverity.WARNING
                    ? TextFormatting.RED + line.text() : line.text());
        }
        return texts;
    }

    /** Point-in-rect hit test for hover tooltips (works over disabled buttons too). */
    static boolean hovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
