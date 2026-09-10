// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.schmaloogium.engine.config.LangDecorations;
import com.schmaloogium.engine.config.OptionDefinition;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.config.ValueDecorationKey;

/**
 * The frozen-locale label/value/tooltip resolver (PHASE_12_DOC §4.3.5). One instance is
 * frozen per model build over the owner's complete locale catalog. Resolution is per
 * individual key: requested-locale catalog → {@code en_us} catalog (once, when
 * different) → entry-specific fallback. A present-empty value suppresses later
 * fallback; a key missing from every catalog never selects the first available
 * translation.
 */
public final class GuiText {

    /** The owner's complete deep-immutable locale catalog (the sole locale authority). */
    private final Map<String, LangDecorations> decorations;
    /** GUI-owned message catalogs, resolved by the same chain, per locale. */
    private final Map<String, Map<String, String>> guiMessages;
    private final String locale;

    private static final Pattern LOCALE =
            Pattern.compile("[A-Za-z]{2,8}(?:[_-][A-Za-z0-9]{1,8})*");

    public GuiText(Map<String, LangDecorations> decorations,
                   Map<String, Map<String, String>> guiMessages, String locale) {
        this.decorations = Map.copyOf(decorations);
        Map<String, Map<String, String>> gui = new java.util.LinkedHashMap<>();
        guiMessages.forEach((k, v) -> gui.put(k, Map.copyOf(v)));
        this.guiMessages = Map.copyOf(gui);
        this.locale = normalizeLocale(locale);
    }

    /** §4.3.5 normalization: ASCII lowercase, {@code -}→{@code _}, else absent sentinel. */
    public static String normalizeLocale(String requested) {
        if (requested == null || !LOCALE.matcher(requested).matches()) {
            return "";
        }
        return requested.toLowerCase(Locale.ROOT).replace('-', '_');
    }

    /** The frozen normalized locale (empty = the absent-locale sentinel). */
    public String locale() {
        return locale;
    }

    /** Decorations catalog chain: requested locale, then en_us once when different. */
    private List<LangDecorations> catalogChain() {
        List<LangDecorations> chain = new ArrayList<>(2);
        LangDecorations requested = decorations.get(locale);
        chain.add(requested == null ? LangDecorations.empty() : requested);
        if (!locale.equals("en_us")) {
            LangDecorations en = decorations.get("en_us");
            chain.add(en == null ? LangDecorations.empty() : en);
        }
        return chain;
    }

    /** Bare-key lookup across the catalog chain; present-empty stops the chain. */
    private <K> Optional<String> lookup(
            java.util.function.Function<LangDecorations, Map<K, String>> map, K key) {
        for (LangDecorations catalog : catalogChain()) {
            Map<K, String> m = map.apply(catalog);
            if (m.containsKey(key)) {
                return Optional.ofNullable(m.get(key));
            }
        }
        return Optional.empty();
    }

    /** GUI message lookup across the GUI catalog chain, bare keys. */
    private Optional<String> lookupGui(String key) {
        for (String localeKey : guiLocaleChain()) {
            Map<String, String> m = guiMessages.get(localeKey);
            if (m != null && m.containsKey(key)) {
                return Optional.ofNullable(m.get(key));
            }
        }
        return Optional.empty();
    }

    private List<String> guiLocaleChain() {
        List<String> chain = new ArrayList<>(2);
        chain.add(locale);
        if (!locale.equals("en_us")) {
            chain.add("en_us");
        }
        return chain;
    }

    /** Option label: option label key → owner tooltip text → prettified name. */
    public String optionLabel(OptionDefinition definition) {
        return lookup(d -> d.optionLabels(), definition.name())
                .orElseGet(() -> definition.tooltip()
                        .filter(t -> !t.isEmpty())
                        .orElseGet(() -> prettify(definition.name())));
    }

    /** Value label: {@code value.<NAME>.<VAL>} → raw value verbatim. */
    public String valueLabel(String optionName, String rawValue) {
        return lookupValue(new ValueDecorationKey(optionName, rawValue)).orElse(rawValue);
    }

    private Optional<String> lookupValue(ValueDecorationKey key) {
        for (LangDecorations catalog : catalogChain()) {
            if (catalog.valueLabels().containsKey(key)) {
                return Optional.ofNullable(catalog.valueLabels().get(key));
            }
        }
        return Optional.empty();
    }

    /** Profile label: {@code profile.<NAME>} → prettified. */
    public String profileLabel(ProfileName name) {
        return lookup(d -> d.profileLabels(), name)
                .orElseGet(() -> prettify(name.value()));
    }

    /** The Custom outcome label: {@code profile.Custom} then literal {@code Custom}. */
    public String customProfileLabel() {
        return lookup(d -> d.profileLabels(), new ProfileName("Custom")).orElse("Custom");
    }

    /** Screen/link label: {@code screen.<NAME>} (root uses bare {@code screen}). */
    public String screenLabel(String screenName, boolean main) {
        String key = main ? "screen" : "screen." + screenName;
        return lookup(d -> d.screenLabels(), key)
                .orElseGet(() -> main ? "Shader Pack Options" : prettify(screenName));
    }

    /** A GUI-owned message by bare key. */
    public String gui(String key) {
        return lookupGui(key).orElseGet(() -> Defaults.INSTANCE.messages.getOrDefault(key, key));
    }

    /** A GUI-owned message with one {@code %s} argument substitution. */
    public String gui(String key, Object arg) {
        return gui(key).replace("%s", String.valueOf(arg));
    }

    /** Display value = prefix + value label + suffix, affixes omitted when absent. */
    public String decorate(String optionName, String rawValue) {
        String label = valueLabel(optionName, rawValue);
        String prefix = lookup(d -> d.prefixes(), "prefix." + optionName).orElse("");
        String suffix = lookup(d -> d.suffixes(), "suffix." + optionName).orElse("");
        return prefix + label + suffix;
    }

    /** Option tooltip: comment keys, else the owner definition text; else none. */
    public Tooltip optionTooltip(OptionDefinition definition) {
        Optional<String> comment = lookup(d -> d.optionComments(), definition.name())
                .or(definition::tooltip);
        return comment.map(GuiText::splitTooltip).orElse(Tooltip.EMPTY);
    }

    /** Profile tooltip: {@code profileComments} only. */
    public Tooltip profileTooltip(ProfileName name) {
        return lookup(d -> d.profileComments(), name)
                .map(GuiText::splitTooltip).orElse(Tooltip.EMPTY);
    }

    /** Screen tooltip: {@code screenComments} only. */
    public Tooltip screenTooltip(String screenName, boolean main) {
        String key = main ? "screen" : "screen." + screenName;
        return lookup(d -> d.screenComments(), key)
                .map(GuiText::splitTooltip).orElse(Tooltip.EMPTY);
    }

    /**
     * Splits owner text on the literal {@code ". "}; a line ending in {@code !} is
     * marked WARNING and its final marker is removed for display (the view renders it
     * red); whitespace and remaining punctuation are preserved.
     */
    public static Tooltip splitTooltip(String text) {
        List<TooltipLine> lines = new ArrayList<>();
        for (String raw : text.split("\\. ", -1)) {
            if (raw.isEmpty()) {
                continue;
            }
            String line = raw.endsWith("!") ? raw.substring(0, raw.length() - 1) : raw;
            lines.add(new TooltipLine(line,
                    raw.endsWith("!") ? TooltipSeverity.WARNING : TooltipSeverity.INFO));
        }
        return Tooltip.ofLines(lines);
    }

    /** Last-resort prettification: {@code _} {@code .} {@code -} → space, Title Case. */
    public static String prettify(String name) {
        String[] words = name.toLowerCase(Locale.ROOT).split("[_.\\-]");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word, 1, word.length());
        }
        return sb.toString();
    }

    /** Built-in English GUI messages (the terminal fallback of the GUI chain). */
    private static final class Defaults {
        private static final Defaults INSTANCE = new Defaults();

        private final Map<String, String> messages = Map.ofEntries(
                Map.entry("schmaloogium.gui.screen.main", "Shader Pack Options"),
                Map.entry("schmaloogium.gui.noProfiles", "No profiles available"),
                Map.entry("schmaloogium.gui.internalUnavailable",
                        "Internal option editing unavailable"),
                Map.entry("schmaloogium.gui.persistenceUnavailable",
                        "Option persistence unavailable for this pack"),
                Map.entry("schmaloogium.gui.noPendingChanges", "No pending changes"),
                Map.entry("schmaloogium.gui.unsavedCount", "Unsaved changes: %s"),
                Map.entry("schmaloogium.gui.pendingProfile", "Pending profile selection: %s"),
                Map.entry("schmaloogium.gui.pendingProfileCleared",
                        "Pending profile selection: cleared"),
                Map.entry("schmaloogium.gui.profiles", "Profile"),
                Map.entry("schmaloogium.gui.custom", "Custom"),
                Map.entry("schmaloogium.gui.issues", "Shader issues: %s"),
                Map.entry("schmaloogium.gui.outOfList", "Current value is not in the list"),
                Map.entry("schmaloogium.gui.done", "Done"),
                Map.entry("schmaloogium.gui.apply", "Apply"),
                Map.entry("schmaloogium.gui.reset", "Reset"),
                Map.entry("schmaloogium.gui.back", "Back"),
                Map.entry("schmaloogium.gui.off", "(off)"),
                Map.entry("schmaloogium.gui.internal", "(internal)"),
                Map.entry("schmaloogium.gui.openFolder", "Open folder"),
                Map.entry("schmaloogium.gui.refresh", "Refresh"),
                Map.entry("schmaloogium.gui.settings", "Shader pack settings…"),
                Map.entry("schmaloogium.gui.settingsTitle", "Shader Pack Settings"));

        private Defaults() {
        }
    }
}
