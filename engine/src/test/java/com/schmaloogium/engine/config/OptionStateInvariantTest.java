// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import org.junit.jupiter.api.Test;
import com.schmaloogium.engine.pack.CompanionOptionMacros;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Catalog construction/update invariants: the closed failure taxonomy of section 4.3. */
class OptionStateInvariantTest {

    private static final com.schmaloogium.engine.pack.NormalizedPackPath PATH
        = new com.schmaloogium.engine.pack.NormalizedPackPath("shaders/composite.fsh");

    private static SourceAttribution at(int line) {
        return new SourceAttribution(PATH, line, 1);
    }

    private static final com.schmaloogium.engine.diag.DiagnosticReporter NOOP = d -> { };

    private static OptionCatalog catalog() {
        List<OptionDefinition> defs = List.of(
            new OptionDefinition("FANCY", OptionKind.SWITCH,
                new BooleanOptionValue(true),
                List.of(new BooleanOptionValue(true), new BooleanOptionValue(false)),
                OptionAvailability.AVAILABLE, Optional.of("Fancy graphics"),
                List.of(at(1))),
            new OptionDefinition("shadowMapResolution", OptionKind.CONSTANT,
                new TextOptionValue("2048"), List.of(), OptionAvailability.AVAILABLE,
                Optional.empty(), List.of(at(4))));
        return OptionCatalogs.create(defs, new Object(), false);
    }

    @Test
    void defaultStateCarriesDefinitionsInSourceOrder() {
        OptionCatalog catalog = catalog();
        assertEquals(List.of("FANCY", "shadowMapResolution"), catalog.definitions().stream()
            .map(OptionDefinition::name).toList());
        OptionState defaults = catalog.defaultState();
        assertEquals(new BooleanOptionValue(true), defaults.value("FANCY").orElseThrow());
    }

    @Test
    void unknownOptionIsRejectedFirstInUnsignedUtf8Order() {
        OptionStateResult result = catalog().constructState(Map.of("zzzUnknown",
            new BooleanOptionValue(true)), NOOP);
        assertInstanceOf(OptionStateResult.Invalid.class, result);
        assertEquals(OptionStateFailure.UNKNOWN_OPTION,
            ((OptionStateResult.Invalid) result).failure());
    }

    @Test
    void missingOptionIsRejected() {
        OptionStateResult result = catalog().constructState(Map.of(), NOOP);
        assertInstanceOf(OptionStateResult.Invalid.class, result);
        assertEquals(OptionStateFailure.MISSING_OPTION,
            ((OptionStateResult.Invalid) result).failure());
    }

    @Test
    void kindMismatchIsRejected() {
        OptionStateResult result = catalog().constructState(Map.of(
            "FANCY", new TextOptionValue("true"),
            "shadowMapResolution", new TextOptionValue("2048")), NOOP);
        assertInstanceOf(OptionStateResult.Invalid.class, result);
        assertEquals(OptionStateFailure.KIND_MISMATCH,
            ((OptionStateResult.Invalid) result).failure());
    }

    @Test
    void disabledAmbiguousRejectsAnyNonDefault() {
        List<OptionDefinition> defs = List.of(new OptionDefinition("CONFLICT", OptionKind.SWITCH,
            new BooleanOptionValue(false), List.of(), OptionAvailability.DISABLED_AMBIGUOUS,
            Optional.empty(), List.of(at(1), at(2))));
        OptionCatalog catalog = OptionCatalogs.create(defs, new Object(), false);
        OptionStateResult same = catalog.constructState(Map.of("CONFLICT",
            new BooleanOptionValue(false)), NOOP);
        assertInstanceOf(OptionStateResult.Valid.class, same);
        OptionStateResult changed = catalog.constructState(Map.of("CONFLICT",
            new BooleanOptionValue(true)), NOOP);
        assertEquals(OptionStateFailure.DISABLED_AMBIGUOUS,
            ((OptionStateResult.Invalid) changed).failure());
    }

    @Test
    void outOfListValueIsAcceptedWithWarning() {
        List<OptionDefinition> defs = List.of(new OptionDefinition("FANCY", OptionKind.SWITCH,
            new BooleanOptionValue(true), List.of(new BooleanOptionValue(true)),
            OptionAvailability.AVAILABLE, Optional.empty(), List.of(at(1))));
        OptionCatalog catalog = OptionCatalogs.create(defs, new Object(), false);
        OptionStateResult result = catalog.constructState(Map.of("FANCY",
            new BooleanOptionValue(false)), NOOP);
        assertInstanceOf(OptionStateResult.Valid.class, result);
    }

    @Test
    void foreignStateIsNotAcceptedForUpdate() {
        OptionCatalog other = catalog();
        OptionState foreign = other.defaultState();
        OptionStateResult result = catalog().updateState(foreign, "FANCY",
            new BooleanOptionValue(false), NOOP);
        assertInstanceOf(OptionStateResult.Invalid.class, result);
    }

    @Test
    void definitionTooltipIsRetained() {
        assertEquals("Fancy graphics", catalog().find("FANCY").orElseThrow()
            .tooltip().orElseThrow());
        assertTrue(catalog().find("absent").isEmpty());
    }
    @Test
    void macroConfigurationConstructionValidatesCompanionProjection() {
        // agreeing projection constructs (and never NPEs inside the compact ctor)
        new MacroConfiguration(MacroIdentityPolicy.OPTION_1, List.of(),
            List.of(new MacroDefinition("MC_NORMAL_MAP", "")),
            new CompanionOptionMacros(true, false), List.of(), List.of(), Map.of(), List.of());
        // disagreeing projection is rejected with the documented failure, not an NPE
        assertThrows(IllegalArgumentException.class, () -> new MacroConfiguration(
            MacroIdentityPolicy.OPTION_1, List.of(), List.of(),
            new CompanionOptionMacros(true, false), List.of(), List.of(), Map.of(), List.of()));
    }
}
