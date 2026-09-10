// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.schmaloogium.engine.config.BooleanOptionValue;
import com.schmaloogium.engine.config.LangDecorations;
import com.schmaloogium.engine.config.OptionAvailability;
import com.schmaloogium.engine.config.OptionDefinition;
import com.schmaloogium.engine.config.OptionKind;
import com.schmaloogium.engine.config.OptionValue;
import com.schmaloogium.engine.config.ProfileConstraint;
import com.schmaloogium.engine.config.ProfileModel;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.config.ScreenAllOptionsEntry;
import com.schmaloogium.engine.config.ScreenEmptyEntry;
import com.schmaloogium.engine.config.ScreenEntry;
import com.schmaloogium.engine.config.ScreenModel;
import com.schmaloogium.engine.config.ScreenOptionEntry;
import com.schmaloogium.engine.config.ScreenProfileEntry;
import com.schmaloogium.engine.config.ScreenSubscreenEntry;
import com.schmaloogium.engine.config.SliderSet;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.config.ValueDecorationKey;

/**
 * Headless option fixtures (§8 vectors). Definitions, profiles, screens, sliders and
 * decorations built directly on the Phase 3 models — the same shapes
 * {@code OptionsModelFactory} consumes. No Minecraft types.
 */
final class Fixtures {

    private Fixtures() {
    }

    static OptionDefinition shadowSwitch() {
        return new OptionDefinition("SHADOWS", OptionKind.SWITCH,
                new BooleanOptionValue(true),
                List.of(new BooleanOptionValue(true), new BooleanOptionValue(false)),
                OptionAvailability.AVAILABLE,
                Optional.of("Soft shadows. High cost!"), List.of());
    }

    static OptionDefinition sunAngle() {
        return new OptionDefinition("SUN_ANGLE", OptionKind.VARIABLE,
                new TextOptionValue("30.0"),
                List.of(new TextOptionValue("0.0"), new TextOptionValue("30.0"),
                        new TextOptionValue("60.0"), new TextOptionValue("90.0")),
                OptionAvailability.AVAILABLE, Optional.empty(), List.of());
    }

    static OptionDefinition waterTint() {
        return new OptionDefinition("WATER_TINT", OptionKind.CONSTANT,
                new TextOptionValue("1.0,1.0,1.0"), List.of(),
                OptionAvailability.AVAILABLE, Optional.empty(), List.of());
    }

    static OptionDefinition ambiguousClouds() {
        return new OptionDefinition("BLOCKY_CLOUDS", OptionKind.SWITCH,
                new BooleanOptionValue(true), List.of(),
                OptionAvailability.DISABLED_AMBIGUOUS, Optional.empty(), List.of());
    }

    static OptionDefinition cloudHeight() {
        return new OptionDefinition("CLOUD_HEIGHT", OptionKind.VARIABLE,
                new TextOptionValue("128.0"),
                List.of(new TextOptionValue("64.0"), new TextOptionValue("128.0"),
                        new TextOptionValue("192.0"), new TextOptionValue("256.0")),
                OptionAvailability.AVAILABLE,
                Optional.of("Raise for tall clouds."), List.of());
    }

    static OptionDefinition eyeBrightness() {
        // Referenced by a profile but placed on no screen: star-eligible constant.
        return new OptionDefinition("EYE_BRIGHTNESS", OptionKind.CONSTANT,
                new TextOptionValue("0.5"), List.of(), OptionAvailability.AVAILABLE,
                Optional.empty(), List.of());
    }

    static List<OptionDefinition> definitions() {
        return List.of(shadowSwitch(), sunAngle(), waterTint(), ambiguousClouds(),
                cloudHeight(), eyeBrightness());
    }

    static ProfileName performanceProfile() {
        return new ProfileName("Performance");
    }

    static ProfileName fancyProfile() {
        return new ProfileName("Fancy");
    }

    static List<ProfileModel> profiles() {
        return List.of(
                new ProfileModel(performanceProfile(),
                        List.of(new ProfileConstraint("SHADOWS", new BooleanOptionValue(false)),
                                new ProfileConstraint("SUN_ANGLE", new TextOptionValue("0.0"))),
                        List.of()),
                new ProfileModel(fancyProfile(),
                        List.of(new ProfileConstraint("SHADOWS", new BooleanOptionValue(true)),
                                new ProfileConstraint("SUN_ANGLE", new TextOptionValue("60.0")),
                                new ProfileConstraint("EYE_BRIGHTNESS",
                                        new TextOptionValue("0.6"))),
                        List.of()));
    }

    static ScreenModel mainScreen() {
        return new ScreenModel(java.util.OptionalInt.empty(), List.of(
                new ScreenOptionEntry("SUN_ANGLE"),
                new ScreenSubscreenEntry("effects"),
                new ScreenEmptyEntry(),
                new ScreenProfileEntry(),
                new ScreenOptionEntry("CLOUD_HEIGHT")));
    }

    static ScreenModel effectsScreen() {
        return new ScreenModel(java.util.OptionalInt.empty(), List.of(
                new ScreenOptionEntry("SHADOWS"),
                new ScreenOptionEntry("BLOCKY_CLOUDS"),
                new ScreenOptionEntry("WATER_TINT")));
    }

    static ScreenModel starredScreen() {
        // <*> plus one placed option: the star expands to every unplaced option in
        // declaration order, first occurrence wins.
        return new ScreenModel(java.util.OptionalInt.empty(), List.of(
                new ScreenAllOptionsEntry(),
                new ScreenOptionEntry("SUN_ANGLE")));
    }

    static ScreenModel wideScreen(int slots) {
        List<ScreenEntry> entries = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            entries.add(new ScreenEmptyEntry());
        }
        return new ScreenModel(java.util.OptionalInt.empty(), List.copyOf(entries));
    }

    static Map<String, ScreenModel> screens() {
        return Map.of("effects", effectsScreen());
    }

    static SliderSet sliders() {
        return new SliderSet(List.of("SUN_ANGLE"));
    }
    static LangDecorations enDecorations() {
        return new LangDecorations(
                Map.of("SHADOWS", "Shadows",
                        "SUN_ANGLE", "Sun angle",
                        "BLOCKY_CLOUDS", "Blocky clouds"),
                Map.of(),
                Map.of(new ValueDecorationKey("SUN_ANGLE", "30.0"), "Morning"),
                Map.of("prefix.SUN_ANGLE", "Angle: "),
                Map.of("suffix.SUN_ANGLE", " degrees"),
                Map.of(performanceProfile(), "Performance"),
                Map.of(),
                Map.of("screen", "Shader Pack Options", "screen.effects", "Effects screen"),
                Map.of());
    }

    static LangDecorations deDecorations() {
        return new LangDecorations(
                Map.of("SHADOWS", "Schatten",
                        "BLOCKY_CLOUDS", ""),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(performanceProfile(), "Leistung"),
                Map.of(),
                Map.of(),
                Map.of());
    }
}
