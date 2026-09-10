// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiEntry;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiSnapshot;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

/**
 * The scrollable diagnostics panel (PHASE_12_DOC §4.9/§4.10.2) fed directly from the
 * two producer inputs stored by {@code showErrors}: a P1 section over the SHADER_GUI
 * list — severity, message key, joined args, detail wrapped and never truncated — and
 * a P11 section, present only when the optional snapshot is, carrying the producer
 * identity (pack fingerprint, attempt serial, outcome) and the source-free entries.
 * Escape returns to the parent screen.
 */
@SideOnly(Side.CLIENT)
final class DiagnosticsPanelScreen extends GuiScreen {

    private static final int LINE_HEIGHT = 10;
    private static final int TEXT = 0xE0E0E0;
    private static final int MUTED = 0xA0A0A0;
    private static final int HEADER = 0xFFFF55;

    private record PanelLine(String text, int color) {
    }

    private final VanillaOptionScreens owner;
    private final GuiScreen parent;
    private List<EngineDiagnostic> shaderGuiDiagnostics;
    private Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics;
    private final List<PanelLine> lines = new ArrayList<>();
    private int scrollOffset;

    DiagnosticsPanelScreen(VanillaOptionScreens owner, GuiScreen parent,
                           List<EngineDiagnostic> shaderGuiDiagnostics,
                           Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics) {
        this.owner = owner;
        this.parent = parent;
        this.shaderGuiDiagnostics = List.copyOf(shaderGuiDiagnostics);
        this.expressionDiagnostics = expressionDiagnostics == null
                ? Optional.empty() : expressionDiagnostics;
    }

    /** Producer delivery: rebuilds the panel from the fresh producer inputs. */
    void replaceDiagnostics(List<EngineDiagnostic> shaderGuiDiagnostics,
                            Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics) {
        this.shaderGuiDiagnostics = List.copyOf(shaderGuiDiagnostics);
        this.expressionDiagnostics = expressionDiagnostics == null
                ? Optional.empty() : expressionDiagnostics;
        scrollOffset = 0;
        rebuildLines();
    }

    @Override
    public void initGui() {
        rebuildLines();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Diagnostics", width / 2, 8, 0xFFFFFF);
        int top = 24;
        int bottom = height - 8;
        int visible = Math.max(1, (bottom - top) / LINE_HEIGHT);
        int maxScroll = Math.max(0, lines.size() - visible);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        for (int i = 0; i < visible && scrollOffset + i < lines.size(); i++) {
            PanelLine line = lines.get(scrollOffset + i);
            if (!line.text().isEmpty()) {
                fontRenderer.drawString(line.text(), 12, top + i * LINE_HEIGHT, line.color());
            }
        }
        if (lines.size() > visible) {
            int track = bottom - top;
            Gui.drawRect(width - 10, top, width - 6, bottom, 0x40000000);
            int thumbHeight = Math.max(10, visible * track / lines.size());
            int thumbY = top + scrollOffset * track / lines.size();
            Gui.drawRect(width - 10, thumbY, width - 6, thumbY + thumbHeight, 0xFFFFFFFF);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(parent);
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
        }
    }

    private void rebuildLines() {
        lines.clear();
        int wrapWidth = Math.max(80, width - 60);
        addLine("Shader GUI diagnostics (P1)", HEADER);
        if (shaderGuiDiagnostics.isEmpty()) {
            addLine("(none)", MUTED);
        }
        for (EngineDiagnostic diagnostic : shaderGuiDiagnostics) {
            addLine(diagnostic.severity() + " " + diagnostic.messageKey(),
                    severityColor(diagnostic.severity()));
            if (!diagnostic.args().isEmpty()) {
                addWrapped("args: " + joinArgs(diagnostic), MUTED, wrapWidth);
            }
            if (diagnostic.detail() != null && !diagnostic.detail().isEmpty()) {
                addWrapped("detail: " + diagnostic.detail(), MUTED, wrapWidth);
            }
            addLine("", MUTED);
        }
        if (expressionDiagnostics.isPresent()) {
            ExpressionDiagnosticGuiSnapshot snapshot = expressionDiagnostics.get();
            addLine("", MUTED);
            addLine("Expression diagnostics (P11)", HEADER);
            addLine("pack " + snapshot.packFingerprint() + ", attempt " + snapshot.attemptSerial()
                    + ", " + snapshot.outcome(), MUTED);
            for (ExpressionDiagnosticGuiEntry entry : snapshot.entries()) {
                addLine(entry.kind() + " " + entry.severity(), severityColor(entry.severity()));
                if (!entry.declarationName().isEmpty()) {
                    addLine("in " + entry.declarationName(), MUTED);
                }
                addWrapped(entry.summary(), TEXT, wrapWidth);
                addLine("", MUTED);
            }
        }
    }

    private static String joinArgs(EngineDiagnostic diagnostic) {
        StringBuilder joined = new StringBuilder();
        for (int i = 0; i < diagnostic.args().size(); i++) {
            if (i > 0) {
                joined.append(", ");
            }
            joined.append(diagnostic.args().get(i));
        }
        return joined.toString();
    }

    /** The P11 snapshot carries its own closed severity domain ({@link
     * com.schmaloogium.engine.expr.api.DiagnosticSeverity}), distinct from the P1 one. */
    private static int severityColor(
            com.schmaloogium.engine.expr.api.DiagnosticSeverity severity) {
        return switch (severity) {
            case WARNING -> 0xFFC040;
            case ERROR -> 0xFF6060;
        };
    }

    private void addLine(String text, int color) {
        lines.add(new PanelLine(text, color));
    }

    /** Wraps long lines; detail and summary are never truncated to one line. */
    private void addWrapped(String text, int color, int wrapWidth) {
        for (String piece : fontRenderer.listFormattedStringToWidth(text, wrapWidth)) {
            addLine(piece, color);
        }
    }

    private static int severityColor(DiagnosticSeverity severity) {
        return switch (severity) {
            case INFO -> TEXT;
            case WARN -> 0xFFC040;
            case ERROR -> 0xFF6060;
            case FATAL -> 0xFF2020;
        };
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
