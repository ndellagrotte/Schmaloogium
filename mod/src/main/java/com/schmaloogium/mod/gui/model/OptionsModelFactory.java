// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.schmaloogium.engine.config.OptionAvailability;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionDefinition;
import com.schmaloogium.engine.config.OptionKind;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.config.OptionValue;
import com.schmaloogium.engine.config.ProfileInferenceResult;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.config.ScreenAllOptionsEntry;
import com.schmaloogium.engine.config.ScreenEmptyEntry;
import com.schmaloogium.engine.config.ScreenEntry;
import com.schmaloogium.engine.config.ScreenModel;
import com.schmaloogium.engine.config.ScreenOptionEntry;
import com.schmaloogium.engine.config.ScreenProfileEntry;
import com.schmaloogium.engine.config.ScreenSubscreenEntry;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

/**
 * The options presentation-model builder (PHASE_12_DOC §4.2): a single pass over the
 * exact published configuration artifacts — no re-parsing, no mutation, deterministic
 * (I-1…I-4). After the schema gate (the caller's), malformed presentation blocks can
 * never fail the build: every anomaly becomes a warning and the other entries survive.
 */
public final class OptionsModelFactory {

    private OptionsModelFactory() {
    }

    /**
     * Builds one model. The caller freezes locale ({@code text}) and supplies the
     * preview state, the pending-profile summary and the presenter-owned availability.
     */
    public static OptionPresentationModel build(
            OptionConfiguration configuration,
            OptionState previewState,
            Optional<PendingProfileSummary> pendingSummary,
            GuiText text,
            OptionSessionAvailability availability,
            DiagnosticReporter diagnostics) {


        Collector out = new Collector(diagnostics);
        Set<String> placed = new LinkedHashSet<>();
        collectPlaced(configuration.mainScreen(), placed);
        for (ScreenModel screen : configuration.namedScreens().values()) {
            collectPlaced(screen, placed);
        }

        // 4) expansion set: unplaced, visible, unambiguous, in published source order.
        List<OptionDefinition> expansion = expansionSet(configuration, placed);

        // 5) materialize: main first, then subscreens; exactly one `*` wins across the
        // whole build order (D-P12-2), tracked by this one shared flag; an option's
        // first occurrence wins — later placements drop with a warning (§4.3.3).
        boolean[] starTaken = {false};
        Set<String> rendered = new LinkedHashSet<>();
        List<PresentationEntry> mainEntries = new ArrayList<>();
        materialize(configuration, configuration.mainScreen(), true, expansion, starTaken,
                rendered, mainEntries, text, out, availability, previewState);
        PresentationScreen main = new PresentationScreen(ScreenId.MAIN,
                text.screenLabel("", true),
                columns(mainEntries, configuration.mainScreen()), List.copyOf(mainEntries));

        Map<ScreenId, PresentationScreen> subScreens = new java.util.LinkedHashMap<>();
        configuration.namedScreens().forEach((name, screen) -> {
            List<PresentationEntry> entries = new ArrayList<>();
                materialize(configuration, screen, false, expansion, starTaken,
                        rendered, entries, text, out, availability, previewState);
            ScreenId id = ScreenId.declared(name);
            subScreens.put(id, new PresentationScreen(id, text.screenLabel(name, false),
                    columns(entries, screen), List.copyOf(entries)));
        });
        diagnoseOrphans(configuration, out);

        return new OptionPresentationModel(main, subScreens, out.diagnostics,
                pendingSummary, availability);
    }

    /** Declared screens no link reaches are diagnosed, never dropped (D-P12-1). */
    private static void diagnoseOrphans(OptionConfiguration configuration, Collector out) {
        Set<String> linked = new LinkedHashSet<>();
        collectLinks(configuration.mainScreen(), linked, configuration);
        configuration.namedScreens().forEach((name, screen) ->
                collectLinks(screen, linked, configuration));
        for (String name : configuration.namedScreens().keySet()) {
            if (!linked.contains(name)) {
                out.warn("schmaloogium.warn.gui.orphanedScreen", name);
            }
        }
    }

    private static void collectLinks(ScreenModel screen, Set<String> linked,
                                     OptionConfiguration configuration) {
        for (ScreenEntry entry : screen.entries()) {
            if (entry instanceof ScreenSubscreenEntry link
                    && configuration.namedScreens().containsKey(link.screenName())) {
                linked.add(link.screenName());
            }
        }
    }


    private static void collectPlaced(ScreenModel screen, Set<String> placed) {
        for (ScreenEntry entry : screen.entries()) {
            if (entry instanceof ScreenOptionEntry option) {
                placed.add(option.optionName());
            }
        }
    }

    /** Eligibility per §4.3.3: in catalog, visible, not ambiguous, not placed. */
    private static List<OptionDefinition> expansionSet(OptionConfiguration configuration,
                                                       Set<String> placed) {
        Set<String> sliderNames = new LinkedHashSet<>(configuration.sliders().optionNames());
        List<OptionDefinition> expansion = new ArrayList<>();
        for (OptionDefinition definition : configuration.catalog().definitions()) {
            if (placed.contains(definition.name())) {
                continue;
            }
            if (!visible(definition, sliderNames, configuration)) {
                continue;
            }
            expansion.add(definition);
        }
        return expansion;
    }

    private static boolean visible(OptionDefinition definition, Set<String> sliderNames,
                                   OptionConfiguration configuration) {
        if (definition.availability() == OptionAvailability.DISABLED_AMBIGUOUS) {
            return false;
        }
        return switch (definition.kind()) {
            case SWITCH, VARIABLE -> true;
            case CONSTANT -> !definition.allowedValues().isEmpty()
                    || sliderNames.contains(definition.name())
                    || referencedByProfile(definition.name(), configuration);
        };
    }

    private static boolean referencedByProfile(String name, OptionConfiguration configuration) {
        return configuration.profiles().stream()
                .anyMatch(profile -> profile.constraints().stream()
                        .anyMatch(constraint -> constraint.optionName().equals(name)));
    }

    /** Column resolution over the retained slots after expansion (§4.3.4, D-P12-20). */
    private static int columns(List<PresentationEntry> entries, ScreenModel screen) {
        return screen.resolvedColumns(entries.size());
    }

    private record Ctx(OptionConfiguration configuration, OptionState preview, GuiText text,
                       OptionSessionAvailability availability, Collector out) {
    }

    private static void materialize(OptionConfiguration configuration, ScreenModel screen,
                                    boolean main, List<OptionDefinition> expansion,
                                    boolean[] starTaken, Set<String> rendered,
                                    List<PresentationEntry> entries,
                                    GuiText text, Collector out,
                                    OptionSessionAvailability availability,
                                    OptionState previewState) {
        Ctx ctx = new Ctx(configuration, previewState, text, availability, out);
        boolean interactive = availability.mutation().enabled();
        for (ScreenEntry entry : screen.entries()) {
            switch (entry) {
                case ScreenEmptyEntry empty -> entries.add(new PresentationEntry.Blank());
                case ScreenAllOptionsEntry star -> {
                    if (starTaken[0]) {
                        out.warn("schmaloogium.warn.gui.surplusStar",
                                main ? "" : "screen");
                        continue; // expands to nothing; contributes no slot (D-P12-2)
                    }
                    starTaken[0] = true;
                    for (OptionDefinition definition : expansion) {
                        if (!rendered.add(definition.name())) {
                            continue;
                        }
                        entries.add(optionEntry(ctx, definition, interactive));
                    }
                    // An empty expansion set contributes no slot.
                }
                case ScreenProfileEntry profile ->
                        entries.add(profileEntry(ctx, interactive));
                case ScreenSubscreenEntry link -> {
                    boolean resolved = configuration.namedScreens()
                            .containsKey(link.screenName());
                    if (!resolved) {
                        out.warn("schmaloogium.warn.gui.unresolvedSubscreen",
                                link.screenName());
                    }
                    entries.add(new PresentationEntry.SubScreenLink(
                            ScreenId.declared(link.screenName()),
                            text.screenLabel(link.screenName(), false),
                            text.screenTooltip(link.screenName(), false), resolved));
                }
                case ScreenOptionEntry option -> {
                    Optional<OptionDefinition> definition =
                            configuration.catalog().find(option.optionName());
                    if (definition.isEmpty()) {
                        out.warn("schmaloogium.warn.gui.unknownOption", option.optionName());
                        continue; // dropped; cannot reappear via `*` (already placed anyway)
                    }
                    if (!rendered.add(definition.get().name())) {
                        // First occurrence wins; later placements drop (§4.3.3).
                        out.warn("schmaloogium.warn.gui.duplicatePlacement",
                                definition.get().name());
                        continue;
                    }
                    entries.add(optionEntry(ctx, definition.get(), interactive));
                }
            }
        }
    }

    private static PresentationEntry optionEntry(Ctx ctx, OptionDefinition definition,
                                                 boolean interactive) {
        boolean ambiguous = definition.availability() == OptionAvailability.DISABLED_AMBIGUOUS;
        boolean usable = interactive && !ambiguous;
        List<String> allowed = textValues(definition);
        String raw = rawValue(ctx, definition);
        int index = allowed.indexOf(raw);
        Tooltip tooltip = tooltipFor(ctx.text(), definition, index);
        String label = ctx.text().optionLabel(definition);
        String decorated = ctx.text().decorate(definition.name(), raw);
        if (definition.kind() == OptionKind.SWITCH) {
            boolean value = raw.equals("true");
            return new PresentationEntry.SwitchOption(new OptionId(definition.name()), label,
                    value, tooltip, usable);
        }
        if (ctx.configuration().sliders().optionNames().contains(definition.name())
                && definition.kind() == OptionKind.VARIABLE && !allowed.isEmpty()) {
            return new PresentationEntry.SliderOption(new OptionId(definition.name()), label,
                    raw, decorated, allowed, index, tooltip, usable);
        }
        return new PresentationEntry.ValueOption(new OptionId(definition.name()), label,
                raw, decorated, allowed, index, tooltip, usable);
    }

    private static Tooltip tooltipFor(GuiText text, OptionDefinition definition, int index) {
        Tooltip tooltip = text.optionTooltip(definition);
        if (index < 0) {
            List<TooltipLine> lines = new ArrayList<>(tooltip.lines());
            lines.add(new TooltipLine(text.gui("schmaloogium.gui.outOfList"),
                    TooltipSeverity.INFO));
            tooltip = Tooltip.ofLines(lines);
        }
        return tooltip;
    }

    private static List<String> textValues(OptionDefinition definition) {
        List<String> out = new ArrayList<>(definition.allowedValues().size());
        for (OptionValue value : definition.allowedValues()) {
            out.add(textOf(value));
        }
        return out;
    }

    private static String rawValue(Ctx ctx, OptionDefinition definition) {
        return ctx.preview().value(definition.name())
                .map(OptionsModelFactory::textOf)
                .orElseGet(() -> textOf(definition.defaultValue()));
    }

    private static String textOf(OptionValue value) {
        if (value instanceof com.schmaloogium.engine.config.BooleanOptionValue b) {
            return b.value() ? "true" : "false";
        }
        return ((com.schmaloogium.engine.config.TextOptionValue) value).value();
    }

    private static PresentationEntry.ProfileCycle profileEntry(Ctx ctx, boolean interactive) {
        var profiles = ctx.configuration().profiles();
        boolean applicable = !profiles.isEmpty();
        Optional<ProfileName> current = currentProfile(ctx);
        String label;
        if (!applicable) {
            label = ctx.text().gui("schmaloogium.gui.noProfiles");
        } else if (current.isPresent()) {
            label = ctx.text().profileLabel(current.get());
        } else {
            label = ctx.text().customProfileLabel();
        }
        OptionActionAvailability rowAvailability = applicable && interactive
                ? OptionActionAvailability.ENABLED
                : (applicable
                        ? OptionActionAvailability.disabled(
                                ctx.text().gui("schmaloogium.gui.internalUnavailable"))
                        : OptionActionAvailability.disabled(
                                ctx.text().gui("schmaloogium.gui.noProfiles")));
        return new PresentationEntry.ProfileCycle(label, current,
                Tooltip.EMPTY, applicable, rowAvailability);
    }

    /** Display selection: pending explicit when present, else inferred (§4.5.1). */
    private static Optional<ProfileName> currentProfile(Ctx ctx) {
        List<ProfileName> names = ctx.configuration().profiles().stream()
                .map(p -> p.name()).toList();
        return switch (ctx.configuration().inferProfile(ctx.preview())) {
            case ProfileInferenceResult.Inferred inferred ->
                    inferred.inference().selected().filter(names::contains);
            case ProfileInferenceResult.InvalidState invalid -> Optional.empty();
        };
    }

    /** Accumulates build warnings into the model and the reporter; never fails. */
    private static final class Collector {
        private final List<EngineDiagnostic> diagnostics = new ArrayList<>();
        private final DiagnosticReporter reporter;

        Collector(DiagnosticReporter reporter) {
            this.reporter = reporter;
        }

        void warn(String key, String arg) {
            EngineDiagnostic diagnostic = new EngineDiagnostic(DiagnosticSeverity.WARN,
                    UserChannel.LOG_ONLY, key,
                    arg.isEmpty() ? List.of() : List.of(arg), "", "schmaloogium.config");
            diagnostics.add(diagnostic);
            if (reporter != null) {
                reporter.report(diagnostic);
            }
        }
    }
}
