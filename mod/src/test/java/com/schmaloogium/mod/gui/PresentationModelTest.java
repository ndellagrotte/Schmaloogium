// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.config.OptionStateResult;
import com.schmaloogium.engine.config.ProfileModel;
import com.schmaloogium.engine.config.ScreenModel;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.mod.gui.model.GuiText;
import com.schmaloogium.mod.gui.model.OptionActionAvailability;
import com.schmaloogium.mod.gui.model.OptionPresentationModel;
import com.schmaloogium.mod.gui.model.OptionSessionAvailability;
import com.schmaloogium.mod.gui.model.OptionsModelFactory;
import com.schmaloogium.mod.gui.model.PendingProfileSummary;
import com.schmaloogium.mod.gui.model.PresentationEntry;
import com.schmaloogium.mod.gui.model.ScreenId;

/**
 * Headless view-model vectors (PHASE_12_DOC §4.3, §8): star expansion and placement,
 * column resolution, entry kinds, frozen locale, and the presenter-owned availability
 * block. No Minecraft types.
 */
class PresentationModelTest {

    private static final OptionSessionAvailability EDITABLE =
            new OptionSessionAvailability(OptionActionAvailability.ENABLED,
                    OptionActionAvailability.ENABLED, OptionActionAvailability.ENABLED,
                    OptionActionAvailability.ENABLED);

    private static OptionConfiguration configuration(ScreenModel main,
                                                     Map<String, ScreenModel> named) {
        OptionCatalog catalog = OptionCatalogs.create(Fixtures.definitions(),
                new Object(), false);
        return new OptionConfiguration(catalog, catalog.defaultState(),
                Fixtures.profiles(), main, named, Fixtures.sliders(),
                Map.of("en_us", Fixtures.enDecorations(), "de_de", Fixtures.deDecorations()));
    }

    private static OptionPresentationModel build(OptionConfiguration configuration) {
        return OptionsModelFactory.build(configuration, configuration.state(),
                Optional.empty(),
                new GuiText(configuration.localizedDecorations(), Map.of(), "en_us"), EDITABLE, null);
    }

    @Test
    void optionStar_expandsOnlyUnplacedOptionsInDeclarationOrder() {
        OptionConfiguration configuration = configuration(Fixtures.starredScreen(),
                Map.of("effects", Fixtures.effectsScreen()));
        OptionPresentationModel model = build(configuration);
        List<PresentationEntry> entries = model.mainScreen().entries();
        // CLOUD_HEIGHT and EYE_BRIGHTNESS are the unplaced, visible options, in
        // declaration order; then the explicit SUN_ANGLE entry.
        assertEquals("CLOUD_HEIGHT", optionName(entries.get(0)));
        assertEquals("EYE_BRIGHTNESS", optionName(entries.get(1)));
        assertEquals("SUN_ANGLE", optionName(entries.get(2)));
        // Ambiguous BLOCKY_CLOUDS never expands; const WATER_TINT (no allowed list,
        // unreferenced, unslid) is invisible to the star.
        assertTrue(namesOf(entries).noneMatch("BLOCKY_CLOUDS"::equals));
        assertTrue(namesOf(entries).noneMatch("WATER_TINT"::equals));
        // The star fired exactly once across the whole build: the effects screen kept
        // only its own three explicit placements.
        assertEquals(3, model.subScreens().get(ScreenId.declared("effects"))
                .entries().size());
    }

    private static java.util.stream.Stream<String> namesOf(List<PresentationEntry> entries) {
        return entries.stream().map(PresentationModelTest::optionName)
                .filter(name -> name != null);
    }

    /** Resolved option name, or null for non-option entries. */
    private static String optionName(PresentationEntry entry) {
        if (entry instanceof PresentationEntry.SwitchOption s) {
            return s.id().name();
        }
        if (entry instanceof PresentationEntry.ValueOption v) {
            return v.id().name();
        }
        if (entry instanceof PresentationEntry.SliderOption s) {
            return s.id().name();
        }
        return null;
    }

    @Test
    void optionFirstOccurrenceWins_acrossScreens() {
        // SUN_ANGLE is placed on main; the effects screen also places it: the second
        // placement drops with a warning, the first stays.
        ScreenModel mainWithSun = new ScreenModel(java.util.OptionalInt.empty(),
                List.of(new com.schmaloogium.engine.config.ScreenOptionEntry("SUN_ANGLE")));
        ScreenModel effectsWithSun = new ScreenModel(java.util.OptionalInt.empty(),
                List.of(new com.schmaloogium.engine.config.ScreenOptionEntry("SHADOWS"),
                        new com.schmaloogium.engine.config.ScreenOptionEntry("SUN_ANGLE")));
        OptionConfiguration configuration = configuration(mainWithSun,
                Map.of("effects", effectsWithSun));
        OptionPresentationModel model = build(configuration);
        assertEquals(1, namesOf(model.mainScreen().entries())
                .filter("SUN_ANGLE"::equals).count());
        assertEquals(0, namesOf(model.subScreens().get(ScreenId.declared("effects"))
                .entries()).filter("SUN_ANGLE"::equals).count());
        assertTrue(model.diagnostics().stream().anyMatch(d ->
                d.messageKey().equals("schmaloogium.warn.gui.duplicatePlacement")
                        && d.severity() == DiagnosticSeverity.WARN));
    }

    @Test
    void screenEntries_allFiveKindsResolvedInOrder() {
        OptionConfiguration configuration = configuration(Fixtures.mainScreen(),
                Map.of("effects", Fixtures.effectsScreen()));
        OptionPresentationModel model = build(configuration);
        List<PresentationEntry> entries = model.mainScreen().entries();
        assertInstanceOf(PresentationEntry.SliderOption.class, entries.get(0)); // SUN_ANGLE
        assertInstanceOf(PresentationEntry.SubScreenLink.class, entries.get(1));
        assertInstanceOf(PresentationEntry.Blank.class, entries.get(2));
        assertInstanceOf(PresentationEntry.ProfileCycle.class, entries.get(3));
        assertInstanceOf(PresentationEntry.ValueOption.class, entries.get(4)); // CLOUD_HEIGHT
        assertTrue(((PresentationEntry.SubScreenLink) entries.get(1)).resolved());
        // labels resolve through the frozen locale
        assertEquals("Sun angle",
                ((PresentationEntry.SliderOption) entries.get(0)).label());
        assertEquals("Effects screen",
                ((PresentationEntry.SubScreenLink) entries.get(1)).label());
    }

    @Test
    void screenEntries_unknownOptionDropped_unresolvedLinkDisabled() {
        ScreenModel main = new ScreenModel(java.util.OptionalInt.empty(), List.of(
                new com.schmaloogium.engine.config.ScreenOptionEntry("NOT_AN_OPTION"),
                new com.schmaloogium.engine.config.ScreenSubscreenEntry("missing")));
        OptionPresentationModel model = build(configuration(main, Map.of()));
        // unknown drops; unresolved link stays as a disabled link with a warning
        assertEquals(1, model.mainScreen().entries().size());
        PresentationEntry.SubScreenLink link =
                (PresentationEntry.SubScreenLink) model.mainScreen().entries().get(0);
        assertFalse(link.resolved());
        assertTrue(model.diagnostics().stream().anyMatch(d -> d.messageKey()
                .equals("schmaloogium.warn.gui.unknownOption")));
        assertTrue(model.diagnostics().stream().anyMatch(d -> d.messageKey()
                .equals("schmaloogium.warn.gui.unresolvedSubscreen")));
    }

    @Test
    void screenColumnCounts_boundariesAndExplicitPin() {
        // D-P3-55: unconfigured floors at 2, then one column per 9 slots beyond.
        assertEquals(2, Fixtures.wideScreen(18).resolvedColumns(18));
        assertEquals(3, Fixtures.wideScreen(19).resolvedColumns(19));
        assertEquals(3, Fixtures.wideScreen(27).resolvedColumns(27));
        assertEquals(4, Fixtures.wideScreen(28).resolvedColumns(28));
        // explicit columns pin at least, never below the slot floor
        assertEquals(4, new ScreenModel(java.util.OptionalInt.of(4),
                List.of()).resolvedColumns(5));
        assertEquals(3, new ScreenModel(java.util.OptionalInt.of(1),
                List.of()).resolvedColumns(19));
        // the factory projects the same resolution onto the presentation screen
        OptionConfiguration configuration = configuration(Fixtures.mainScreen(),
                Map.of());
        OptionPresentationModel model = build(configuration);
        assertEquals(Fixtures.mainScreen().resolvedColumns(5),
                model.mainScreen().resolvedColumns());
    }

    @Test
    void model_availabilityBlockFlowsThroughVerbatim() {
        OptionConfiguration configuration = configuration(Fixtures.mainScreen(),
                Map.of());
        OptionActionAvailability blocked = OptionActionAvailability.disabled("locked");
        OptionSessionAvailability availability = new OptionSessionAvailability(blocked,
                blocked, blocked, blocked);
        OptionPresentationModel model = OptionsModelFactory.build(configuration,
                configuration.state(), Optional.empty(),
                new GuiText(configuration.localizedDecorations(), Map.of(), "en_us"),
                availability, null);
        // entries render non-interactive; the block itself is the supplied object
        assertFalse(((PresentationEntry.SliderOption) model.mainScreen().entries().get(0))
                .interactive());
        assertFalse(((PresentationEntry.ProfileCycle) model.mainScreen().entries().get(3))
                .availability().enabled());
        assertEquals("locked", model.availability().mutation().disabledReason().get());
        // ambiguous option stays non-interactive even in an editable session
        OptionConfiguration withAmbiguous = configuration(Fixtures.effectsScreen(),
                Map.of());
        OptionPresentationModel editable = build(withAmbiguous);
        assertFalse(((PresentationEntry.SwitchOption) editable.mainScreen().entries()
                .get(1)).interactive()); // BLOCKY_CLOUDS, DISABLED_AMBIGUOUS
    }

    @Test
    void model_pendingProfileSummaryCarriedVerbatim() {
        OptionConfiguration configuration = configuration(Fixtures.mainScreen(),
                Map.of());
        OptionPresentationModel model = OptionsModelFactory.build(configuration,
                configuration.state(), Optional.empty(),
                new GuiText(configuration.localizedDecorations(), Map.of(), "en_us"), EDITABLE, null);
        assertTrue(model.pendingProfileSummary().isEmpty());
        PendingProfileSummary summary = new PendingProfileSummary(
                Optional.of(Fixtures.performanceProfile()),
                "Pending profile selection: Performance");
        OptionPresentationModel withSummary = OptionsModelFactory.build(configuration,
                configuration.state(), Optional.of(summary),
                new GuiText(configuration.localizedDecorations(), Map.of(), "en_us"), EDITABLE, null);
        assertEquals("Pending profile selection: Performance",
                withSummary.pendingProfileSummary().get().displayText());
        assertEquals(Optional.of(Fixtures.performanceProfile()),
                withSummary.pendingProfileSummary().get().selection());
    }

    @Test
    void model_labelsFreezeAtBuildTime() {
        OptionConfiguration configuration = configuration(Fixtures.effectsScreen(),
                Map.of());
        // present-empty BLOCKY_CLOUDS label in the German catalog blocks the chain:
        // the German model renders "" while the English model resolves the label —
        // both frozen as strings inside their models.
        OptionPresentationModel german = OptionsModelFactory.build(configuration,
                configuration.state(), Optional.empty(),
                new GuiText(configuration.localizedDecorations(), Map.of(), "de_de"), EDITABLE, null);
        OptionPresentationModel english = build(configuration);
        assertEquals("", ((PresentationEntry.SwitchOption) german.mainScreen().entries()
                .get(1)).label());
        assertEquals("Blocky clouds",
                ((PresentationEntry.SwitchOption) english.mainScreen().entries().get(1))
                        .label());
    }

    @Test
    void model_valueDecorationUsesPrefixValueLabelSuffix() {
        OptionConfiguration configuration = configuration(Fixtures.mainScreen(),
                Map.of());
        OptionPresentationModel model = build(configuration);
        PresentationEntry.SliderOption sun =
                (PresentationEntry.SliderOption) model.mainScreen().entries().get(0);
        assertEquals("Angle: Morning degrees", sun.displayValue());
        assertEquals("30.0", sun.rawValue());
        assertEquals(List.of("0.0", "30.0", "60.0", "90.0"), sun.allowedValues());
        assertEquals(1, sun.valueIndex());
        // tooltip: comment absent, owner text absent for SUN_ANGLE → empty, no
        // out-of-list line while the value is in-list
        assertTrue(sun.tooltip().lines().isEmpty());
    }

    @Test
    void model_outOfListValueRetainedWithTooltipLine() {
        OptionCatalog catalog = OptionCatalogs.create(Fixtures.definitions(),
                new Object(), false);
        // constructState mints only full, kind-safe, safe-text maps — membership in
        // the allowed list is NOT checked, so a loaded out-of-list baseline is legal.
        Map<String, com.schmaloogium.engine.config.OptionValue> full =
                new java.util.LinkedHashMap<>();
        Fixtures.definitions().forEach(definition -> full.put(definition.name(),
                definition.defaultValue()));
        full.put("SUN_ANGLE", new TextOptionValue("45.0"));
        OptionStateResult result = catalog.constructState(full, Diagnostics::report);
        assertInstanceOf(OptionStateResult.Valid.class, result);
        OptionState state = ((OptionStateResult.Valid) result).state();
        OptionConfiguration configuration = new OptionConfiguration(catalog, state,
                List.<ProfileModel>of(), Fixtures.mainScreen(), Map.of(),
                Fixtures.sliders(), Map.of("en_us", Fixtures.enDecorations()));
        OptionPresentationModel model = OptionsModelFactory.build(configuration, state,
                Optional.empty(),
                new GuiText(configuration.localizedDecorations(), Map.of(), "en_us"), EDITABLE, null);
        PresentationEntry.SliderOption sun =
                (PresentationEntry.SliderOption) model.mainScreen().entries().get(0);
        assertEquals("45.0", sun.rawValue());
        assertEquals(-1, sun.valueIndex());
        assertTrue(sun.tooltip().lines().stream().anyMatch(line ->
                line.text().equals("Current value is not in the list")));
    }
}
