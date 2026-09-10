// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.util.List;
import java.util.Optional;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiSnapshot;
import com.schmaloogium.mod.gui.model.EngineSettingsModel;
import com.schmaloogium.mod.gui.model.OptionEditSession;
import com.schmaloogium.mod.gui.model.OptionPresentationModel;
import com.schmaloogium.mod.gui.model.PackSelectionActions;
import com.schmaloogium.mod.gui.model.PackSelectionModel;
import com.schmaloogium.mod.gui.model.ScreenId;

/**
 * The entire surface a view must implement (PHASE_12_DOC §4.10.1). A view receives
 * fully resolved models — labels localized, tooltips split and severity-tagged, columns
 * resolved, {@code *} already expanded — and reports intents back. A view makes no
 * contract decision: it never infers gates from dirty/count, never reconstructs
 * prepared text, never validates or persists or submits reloads from widget events.
 *
 * <p>Both implementations are client-thread only; every argument is a deeply immutable
 * snapshot. {@code expressionDiagnostics} carries the exact P11 presentation record —
 * not a copy, not a channel conversion; adapters must not inspect its runtime
 * attribution. Nothing above this seam exposes a Minecraft/Forge/LWJGL type.
 */
public interface OptionScreenView {

    /** Shows the pack-selection screen with all seven engine-settings entries. */
    void showPackSelection(PackSelectionModel model, EngineSettingsModel settings,
                           PackSelectionActions actions);

    /** Shows the options screen tree at the given navigation position. */
    void showOptions(OptionPresentationModel model, ScreenId screen, OptionEditSession session);

    /**
     * Delivers the two producer-separated diagnostic inputs: Phase 1's SHADER_GUI list
     * and, optionally, the unchanged P11 final-attempt snapshot. Absence clears only
     * the displayed expression section, never the P1 producer store.
     */
    void showErrors(List<EngineDiagnostic> shaderGuiDiagnostics,
                    Optional<ExpressionDiagnosticGuiSnapshot> expressionDiagnostics);

    /** Closes whatever this view is showing. */
    void close();
}
