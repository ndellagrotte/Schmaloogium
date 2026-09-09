// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds the sealed catalog definitions from raw discovery: switch candidates from
 * preprocessed properties (screen/sliders/profile mentions confirmed by directive
 * scan), const whitelist options, and exact commented/uncommented line shapes.
 */
public final class OptionCatalogBuilder {

    private OptionCatalogBuilder() {
    }

    private static final Pattern DEFINE_LINE = Pattern.compile(
        "^\\s*#define\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(.*?)\\s*(?://.*)?$");
    private static final Pattern IFDEF_LINE = Pattern.compile("^\\s*#(ifdef|ifndef)\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*$");

    /** The App F.3 exact const-option whitelist. */
    private static final java.util.Set<String> CONST_WHITELIST = java.util.Set.of(
        "shadowMapResolution", "shadowMapFov", "shadowDistance", "shadowDistanceRenderMul",
        "shadowIntervalSize", "generateShadowMipmap", "generateShadowColorMipmap",
        "shadowHardwareFiltering", "shadowHardwareFiltering0", "shadowHardwareFiltering1",
        "shadowtex0Mipmap", "shadowtex1Mipmap", "shadowcolor0Mipmap", "shadowcolor1Mipmap",
        "shadowtex0Nearest", "shadowtex1Nearest", "shadowcolor0Nearest", "shadowcolor1Nearest",
        "wetnessHalflife", "drynessHalflife", "eyeBrightnessHalflife",
        "centerDepthHalflife", "sunPathRotation", "ambientOcclusionLevel",
        "superSamplingLevel", "noiseTextureResolution");

    /** Raw discovery input collected by the load pipeline. */
    public record Raw(Map<String, List<SourceAttribution>> switchOccurrences,
        Map<String, String> switchDefaults,
        Map<String, List<String>> switchValues,
        Map<String, List<SourceAttribution>> constOccurrences,
        Map<String, FindingValues> constValues,
        Map<String, String> tooltips) {

        public record FindingValues(String type, String value) {
        }
    }

    public static List<OptionDefinition> build(Raw raw) {
        Map<String, OptionDefinition> out = new LinkedHashMap<>();
        raw.switchOccurrences().forEach((name, occurrences) -> {
            if (occurrences.isEmpty()) {
                return;
            }
            String defaultValue = raw.switchDefaults().getOrDefault(name, "false");
            List<OptionValue> values = new ArrayList<>();
            for (String v : raw.switchValues().getOrDefault(name, List.of())) {
                values.add(new BooleanOptionValue(Boolean.parseBoolean(v)));
            }
            if (values.isEmpty()) {
                values.add(new BooleanOptionValue(true));
                values.add(new BooleanOptionValue(false));
            }
            out.put(name, new OptionDefinition(name, OptionKind.SWITCH,
                new BooleanOptionValue(Boolean.parseBoolean(defaultValue)), values,
                OptionAvailability.AVAILABLE, Optional.ofNullable(raw.tooltips().get(name)),
                occurrences));
        });
        raw.constOccurrences().forEach((name, occurrences) -> {
            if (occurrences.isEmpty() || !CONST_WHITELIST.contains(name)) {
                return;
            }
            Raw.FindingValues fv = raw.constValues().get(name);
            String value = fv == null ? "0" : fv.value();
            out.putIfAbsent(name, new OptionDefinition(name, OptionKind.CONSTANT,
                new TextOptionValue(value), List.of(), OptionAvailability.AVAILABLE,
                Optional.ofNullable(raw.tooltips().get(name)), occurrences));
        });
        return List.copyOf(out.values());
    }

    /** Scans active text for the switch candidate shapes (documented strict grammar). */
    public static void scanSwitches(String text, SourceAttribution attribution,
            Map<String, List<SourceAttribution>> switchOccurrences,
            Map<String, String> switchDefaults,
            Map<String, List<String>> switchValues,
            Map<String, String> tooltips,
            java.util.Set<String> confirmedNames) {
        String[] lines = text.split("\\n", -1);
        for (String line : lines) {
            String stripped = ConstScanner.stripComments(line);
            Matcher directive = DEFINE_LINE.matcher(line);
            if (directive.matches()) {
                String name = directive.group(1);
                String value = directive.group(2).trim();
                if (!confirmedNames.contains(name)) {
                    continue; // unconfirmed candidates never define options
                }
                switchOccurrences.computeIfAbsent(name, k -> new ArrayList<>()).add(attribution);
                if (value.isEmpty()) {
                    switchDefaults.putIfAbsent(name, "true");
                } else if (value.equals("true") || value.equals("false")) {
                    switchDefaults.putIfAbsent(name, value);
                }
                continue;
            }
            Matcher ifdef = IFDEF_LINE.matcher(stripped);
            if (ifdef.matches()) {
                String name = ifdef.group(2);
                if (confirmedNames.contains(name)) {
                    switchOccurrences.computeIfAbsent(name, k -> new ArrayList<>()).add(attribution);
                }
            }
        }
    }

    /** Scans const options with their exact initializer values and attribution. */
    public static void scanConsts(String text, SourceAttribution attribution,
            Map<String, List<SourceAttribution>> constOccurrences,
            Map<String, Raw.FindingValues> constValues) {
        ConstScanner.scan(text).forEach((name, finding) -> {
            if (!CONST_WHITELIST.contains(name)) {
                return;
            }
            constOccurrences.computeIfAbsent(name, k -> new ArrayList<>()).add(attribution);
            constValues.putIfAbsent(name, new Raw.FindingValues(finding.type(), finding.value()));
        });
    }
}
