// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.schmaloogium.mod.gui.model.GuiText;

/**
 * Loads the mod's own GUI message catalogs for {@link GuiText} (PHASE_12_DOC §4.3.5).
 *
 * <p>These are the GUI's own strings, not a pack's {@code lang/} decorations: they ship
 * inside this jar under {@code assets/schmaloogium/lang/} and are read straight off the
 * classpath, so resolution never depends on the resource-pack reload order or on a
 * language file having been picked up by the vanilla loader. Only the requested locale
 * and {@code en_us} are read - exactly the chain {@code GuiText} resolves.
 */
public final class GuiMessageAssets {

    private static final String BASE = "/assets/schmaloogium/lang/";

    private GuiMessageAssets() {
    }

    /** The {@code locale -> messages} catalog map for a requested language code. */
    public static Map<String, Map<String, String>> load(String languageCode) {
        Map<String, Map<String, String>> catalogs = new LinkedHashMap<>(2);
        put(catalogs, GuiText.normalizeLocale(languageCode));
        put(catalogs, "en_us");
        return catalogs;
    }

    private static void put(Map<String, Map<String, String>> catalogs, String locale) {
        if (locale.isEmpty() || catalogs.containsKey(locale)) {
            return;
        }
        for (String name : fileNames(locale)) {
            Map<String, String> parsed = read(BASE + name + ".lang");
            if (parsed != null) {
                catalogs.put(locale, parsed);
                return;
            }
        }
    }

    /**
     * {@code en_us} plus the historical {@code en_US} spelling: 1.12.2 language codes are
     * lowercase, while pre-1.11 asset files carry an uppercase region.
     */
    private static List<String> fileNames(String locale) {
        int split = locale.indexOf('_');
        if (split < 0) {
            return List.of(locale);
        }
        String upper = locale.substring(0, split)
                + '_' + locale.substring(split + 1).toUpperCase(Locale.ROOT);
        return List.of(locale, upper);
    }

    /** Parses one {@code key=value} catalog, or {@code null} when the file is absent. */
    private static Map<String, String> read(String resource) {
        try (InputStream in = GuiMessageAssets.class.getResourceAsStream(resource)) {
            if (in == null) {
                return null;
            }
            Map<String, String> messages = new LinkedHashMap<>();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq > 0) {
                    messages.put(trimmed.substring(0, eq).trim(), trimmed.substring(eq + 1));
                }
            }
            return Map.copyOf(messages);
        } catch (IOException unreadable) {
            return null;
        }
    }
}
