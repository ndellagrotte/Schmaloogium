// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionKind;
import com.schmaloogium.engine.config.ProfileModel;
import com.schmaloogium.engine.config.ScreenModel;
import com.schmaloogium.engine.config.SliderSet;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.mod.gui.model.GuiText;
import com.schmaloogium.mod.gui.model.IssuesIndicator;
import com.schmaloogium.mod.gui.model.TooltipSeverity;

/**
 * Headless vectors for the frozen-locale resolver (§4.3.5) and the shared issues
 * indicator (§4.9) — pure values, no Minecraft types.
 */
class GuiTextAndDiagnosticsTest {

    @Test
    void tooltip_splitsOnDotSpace() {
        var tooltip = GuiText.splitTooltip("First part. Second part. Third");
        assertEquals(3, tooltip.lines().size());
        assertEquals("First part", tooltip.lines().get(0).text());
        assertEquals("Second part", tooltip.lines().get(1).text());
        assertEquals(TooltipSeverity.INFO, tooltip.lines().get(2).severity());
    }

    @Test
    void tooltip_trailingBangIsWarningSeverity() {
        var tooltip = GuiText.splitTooltip("Safe line. Danger line!");
        assertEquals(TooltipSeverity.INFO, tooltip.lines().get(0).severity());
        assertEquals("Danger line", tooltip.lines().get(1).text());
        assertEquals(TooltipSeverity.WARNING, tooltip.lines().get(1).severity());
    }

    @Test
    void lang_prettifyUnderscoreDotDash() {
        assertEquals("Shadow Softness", GuiText.prettify("shadow_softness"));
        assertEquals("Sun Angle", GuiText.prettify("SUN_ANGLE"));
        assertEquals("Old Hand Light", GuiText.prettify("old-hand-light"));
        assertEquals("Water Tint", GuiText.prettify("water.tint"));
    }

    @Test
    void lang_perKeyLocaleThenEnUsThenFallback() {
        Map<String, com.schmaloogium.engine.config.LangDecorations> catalogs = Map.of(
                "de_de", Fixtures.deDecorations(), "en_us", Fixtures.enDecorations());
        GuiText de = new GuiText(catalogs, Map.of(), "de-DE");
        GuiText en = new GuiText(catalogs, Map.of(), "en_US");
        GuiText fr = new GuiText(catalogs, Map.of(), "fr_fr");

        // requested locale wins
        assertEquals("Schatten", de.optionLabel(Fixtures.shadowSwitch()));
        // absent requested key falls to en_us independently per key
        assertEquals("Sun angle", fr.optionLabel(Fixtures.sunAngle()));
        // absent in both: owner tooltip text
        assertEquals("Raise for tall clouds.", en.optionLabel(Fixtures.cloudHeight()));
        // absent everywhere: prettified name
        assertEquals("Water Tint", fr.optionLabel(Fixtures.waterTint()));
        // present-empty blocks the chain (stays empty, never picks en_us)
        assertEquals("", de.optionLabel(Fixtures.ambiguousClouds()));
        // locale normalization: - lower-cased to _
        assertEquals("Schatten", new GuiText(catalogs, Map.of(), "de-de")
                .optionLabel(Fixtures.shadowSwitch()));
        // invalid locale: absent sentinel, chain still reaches en_us
        assertEquals("Shadows", new GuiText(catalogs, Map.of(), "not a locale!")
                .optionLabel(Fixtures.shadowSwitch()));
    }

    @Test
    void lang_affixesAndValueLabels() {
        GuiText text = new GuiText(Map.of("en_us", Fixtures.enDecorations()), Map.of(),
                "en_us");
        // value label wins over raw value; affixes wrap it
        assertEquals("Angle: Morning degrees", text.decorate("SUN_ANGLE", "30.0"));
        // absent value label: raw value verbatim
        assertEquals("90.0", text.valueLabel("SUN_ANGLE", "90.0"));
        // absent affixes: label alone
        assertEquals("true", text.decorate("SHADOWS", "true"));
    }

    @Test
    void gui_messagesFallBackToBuiltins() {
        GuiText text = new GuiText(Map.of(), Map.of(), "en_us");
        assertEquals("No pending changes", text.gui("schmaloogium.gui.noPendingChanges"));
        assertEquals("Unsaved changes: 3", text.gui("schmaloogium.gui.unsavedCount", 3));
        // unknown key: verbatim key, never invented text
        assertEquals("schmaloogium.gui.unknown", text.gui("schmaloogium.gui.unknown"));
    }

    @Test
    void issuesIndicator_countsAndWorstSeverity() {
        assertEquals(Optional.empty(), IssuesIndicator.of(List.of()));
        Optional<IssuesIndicator> some = IssuesIndicator.of(List.of(warn(), error(), warn()));
        assertTrue(some.isPresent());
        assertEquals(3, some.get().count());
        assertSame(DiagnosticSeverity.ERROR, some.get().worst());
        assertSame(DiagnosticSeverity.FATAL,
                IssuesIndicator.of(List.of(warn(), fatal())).get().worst());
    }

    private static EngineDiagnostic warn() {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.SHADER_GUI,
                "schmaloogium.warn.test", List.of(), "", "schmaloogium.config");
    }

    private static EngineDiagnostic error() {
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.SHADER_GUI,
                "schmaloogium.error.test", List.of(), "", "schmaloogium.config");
    }

    private static EngineDiagnostic fatal() {
        return new EngineDiagnostic(DiagnosticSeverity.FATAL, UserChannel.SHADER_GUI,
                "schmaloogium.fatal.test", List.of(), "", "schmaloogium.config");
    }

    @Test
    void configuration_recordAcceptsSyntheticCatalog() {
        // The headless fixture path other tests rely on: a synthetic catalog issued
        // through the public factory binds its own states.
        OptionCatalog catalog = OptionCatalogs.create(Fixtures.definitions(),
                new Object(), false);
        OptionConfiguration configuration = new OptionConfiguration(catalog,
                catalog.defaultState(), List.<ProfileModel>of(),
                new ScreenModel(java.util.OptionalInt.empty(), List.of()),
                Map.of(), new SliderSet(List.of()), Map.of());
        assertTrue(catalog.find("SHADOWS").isPresent());
        assertEquals(OptionKind.SWITCH, catalog.find("SHADOWS").get().kind());
        assertFalse(configuration.inferProfile(configuration.state()).toString().isEmpty());
    }
}
