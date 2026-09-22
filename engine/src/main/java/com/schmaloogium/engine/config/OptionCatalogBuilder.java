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
 * Builds catalog definitions from original-source switch and numeric variable
 * declarations, property-confirmed candidates, and const whitelist options.
 */
public final class OptionCatalogBuilder {

    private OptionCatalogBuilder() {
    }

    private static final Pattern DEFINE_LINE = Pattern.compile(
        "^\\s*(//\\s*)?#\\s*define\\s+([A-Za-z_][A-Za-z0-9_]*)(?:\\s+(.*?))?\\s*(?://(.*))?$");
    private static final Pattern IFDEF_LINE = Pattern.compile("^\\s*#\\s*(ifdef|ifndef)\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*$");
    private static final Pattern NUMBER = Pattern.compile(
        "[+-]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?[fF]?");
    private static final Pattern BRACKET_VALUES = Pattern.compile("\\[([^\\[\\]]+)\\]");

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
            boolean variable = !defaultValue.equals("true") && !defaultValue.equals("false");
            List<OptionValue> values = new ArrayList<>();
            for (String v : raw.switchValues().getOrDefault(name, List.of())) {
                values.add(variable ? new TextOptionValue(v) : new BooleanOptionValue(Boolean.parseBoolean(v)));
            }
            if (variable && !values.contains(new TextOptionValue(defaultValue))) {
                values.addFirst(new TextOptionValue(defaultValue));
            } else if (!variable && values.isEmpty()) {
                values.add(new BooleanOptionValue(true));
                values.add(new BooleanOptionValue(false));
            }
            out.put(name, new OptionDefinition(name, variable ? OptionKind.VARIABLE : OptionKind.SWITCH,
                variable ? new TextOptionValue(defaultValue) : new BooleanOptionValue(Boolean.parseBoolean(defaultValue)),
                values, OptionAvailability.AVAILABLE, Optional.ofNullable(raw.tooltips().get(name)),
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

    /** Scans original text for switches and numeric variables; the legacy map names carry both. */
    public static void scanSwitches(String text, SourceAttribution attribution,
            Map<String, List<SourceAttribution>> switchOccurrences,
            Map<String, String> switchDefaults,
            Map<String, List<String>> switchValues,
            Map<String, String> tooltips,
            java.util.Set<String> confirmedNames) {
        String[] lines = text.split("\\n", -1);
        java.util.Set<String> localSwitchNames = new java.util.HashSet<>();
        for (String line : lines) {
            Matcher ifdef = IFDEF_LINE.matcher(ConstScanner.stripComments(line));
            if (ifdef.matches()) {
                localSwitchNames.add(ifdef.group(2));
            }
        }
        for (String line : lines) {
            Matcher directive = DEFINE_LINE.matcher(line);
            if (!directive.matches()) {
                continue;
            }
            boolean commented = directive.group(1) != null;
            String name = directive.group(2);
            String value = directive.group(3) == null ? "" : directive.group(3).trim();
            String comment = directive.group(4);
            boolean numeric = NUMBER.matcher(value).matches();
            List<String> allowed = new ArrayList<>();
            if (numeric && comment != null) {
                Matcher bracket = BRACKET_VALUES.matcher(comment);
                if (bracket.find()) {
                    for (String token : bracket.group(1).trim().split("\\s+")) {
                        if (!NUMBER.matcher(token).matches()) {
                            allowed.clear();
                            break;
                        }
                        if (!allowed.contains(token)) {
                            allowed.add(token);
                        }
                    }
                }
            }
            if (numeric) {
                if (commented || (!confirmedNames.contains(name) && allowed.isEmpty())) {
                    continue;
                }
                if (!allowed.contains(value)) {
                    allowed.addFirst(value);
                }
            } else {
                if ((!value.isEmpty() && !value.equals("true") && !value.equals("false"))
                        || (!confirmedNames.contains(name) && !localSwitchNames.contains(name))) {
                    continue;
                }
                value = commented ? "false" : value.isEmpty() ? "true" : value;
            }
            switchOccurrences.computeIfAbsent(name, k -> new ArrayList<>()).add(attribution);
            if (switchDefaults.putIfAbsent(name, value) == null && numeric) {
                switchValues.put(name, List.copyOf(allowed));
            }
            if (comment != null) {
                tooltips.putIfAbsent(name, comment);
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
