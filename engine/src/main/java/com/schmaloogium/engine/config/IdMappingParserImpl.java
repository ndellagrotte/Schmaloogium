// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;
import java.util.Set;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.pack.ImmutableBytes;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.preprocess.PropertiesPreprocessor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The one pure pack/mod ID-mapping parse operation: properties-safe preprocessing
 * under the supplied A-G environment, then D-P3-73 selector grammar, with the
 * dual-run BLOCK/ENTITY forced-11300 parse from one defensive byte copy.
 */
public final class IdMappingParserImpl implements IdMappingParser {

    private static final int MAX_LINES = 65536;
    private static final int MAX_RULES = 65536;

    @Override
    public IdMappingFileInput parse(IdMappingParseRequest request) {
        if (request == null || request.kind() == null || request.origin() == null
                || request.environment() == null) {
            return emptyResult(request == null ? null : request.kind(),
                MappingFileState.ABSENT, List.of());
        }
        if (request.source().isEmpty()) {
            return new IdMappingFileInput(request.kind(), MappingFileState.ABSENT,
                List.of(), List.of(), new IdMappingFileFingerprint(absentFingerprint(request)));
        }
        byte[] bytes = request.source().get().copy(); // defensive owned copy
        Map<String, String> ordinaryEnv = environmentMap(request.environment());
        List<MappingRule> ordinary = runParse(bytes, ordinaryEnv, request, false);
        MappingFileState state = ordinary.isEmpty() ? MappingFileState.PRESENT_EMPTY
            : MappingFileState.PRESENT_RULES;
        List<MappingRule> forced = List.of();
        if ((request.kind() == MappingKind.BLOCK || request.kind() == MappingKind.ENTITY)) {
            Map<String, String> modernEnv = new LinkedHashMap<>(ordinaryEnv);
            modernEnv.put("MC_VERSION", "11300");
            forced = runParse(bytes, modernEnv, request, true);
        }
        return new IdMappingFileInput(request.kind(), state, List.copyOf(ordinary),
            List.copyOf(forced), new IdMappingFileFingerprint(fingerprint(request, state, ordinary, forced)));
    }

    private Map<String, String> environmentMap(IdMappingMacroEnvironment environment) {
        Map<String, String> env = new LinkedHashMap<>();
        env.put("MC_VERSION", Integer.toString(environment.mcVersion()));
        for (MacroDefinition def : environment.standardMacros()) {
            env.put(def.name(), def.replacement());
        }
        return env;
    }

    /** One bounded run: decode as ISO-8859-1, preprocess, parse selectors. */
    private List<MappingRule> runParse(byte[] bytes, Map<String, String> env,
            IdMappingParseRequest request, boolean alternate) {
        List<MappingRule> out = new ArrayList<>();
        MappingEra era = alternate ? MappingEra.MODERN : MappingEra.CLASSIC;
        String text = PropertiesPreprocessor.preprocess(
            new String(bytes, StandardCharsets.ISO_8859_1), env);
        String[] lines = text.split("\n", -1);
        int count = Math.min(lines.length, MAX_LINES);
        for (int i = 0; i < count; i++) {
            final int line = i;
            String raw = lines[line];
            String trimmed = raw.stripLeading();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
                continue;
            }
            int eq = raw.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = raw.substring(0, eq).trim();
            String value = raw.substring(eq + 1);
            if (request.kind() == MappingKind.LAYER) {
                if (!key.startsWith("layer.")) {
                    continue;
                }
                RequestedRenderLayer layer = layerOf(key.substring("layer.".length()));
                if (layer == null) {
                    warn(request, "schmaloogium.warn.idmap.invalid_layer", key, line + 1);
                    continue;
                }
                parseSelectors(value, line + 1, request, era, out, selector -> new LayerRule(layer,
                    selector.kind(), selector.token(), selector.metadata(),
                    selector.predicates(), era, request.origin(), line + 1, selector.ordinal()));
            } else {
                int shaderId;
                try {
                    shaderId = Integer.parseInt(key.trim());
                } catch (NumberFormatException e) {
                    continue; // not a shader-id line; other keys are pack metadata
                }
                final int id = shaderId;
                parseSelectors(value, line + 1, request, era, out, selector -> new IdRule(id,
                    selector.kind(), selector.token(), selector.metadata(),
                    selector.predicates(), era, request.origin(), line + 1, selector.ordinal()));
            }
            if (out.size() >= MAX_RULES) {
                warn(request, "schmaloogium.warn.idmap.rule_limit", request.kind().name(), line + 1);
                break;
            }
        }
        return out;
    }

    private RequestedRenderLayer layerOf(String name) {
        return switch (name) {
            case "solid" -> RequestedRenderLayer.SOLID;
            case "cutout" -> RequestedRenderLayer.CUTOUT;
            case "cutout_mipped" -> RequestedRenderLayer.CUTOUT_MIPPED;
            case "translucent" -> RequestedRenderLayer.TRANSLUCENT;
            default -> null;
        };
    }

    private interface RuleFactory {
        MappingRule create(Selector selector);
    }

    /** Accepted D-P3-73 selector with its typed constraints and provenance ordinal. */
    record Selector(SelectorKind kind, String token, Optional<MetadataConstraint> metadata,
        List<PropertyPredicate> predicates, int ordinal) {
    }

    private void parseSelectors(String value, int line, IdMappingParseRequest request,
            MappingEra era, List<MappingRule> out, RuleFactory factory) {
        int ordinal = 0;
        for (String rawSelector : value.split("[ \t]+")) {
            if (rawSelector.isEmpty()) {
                continue;
            }
            Selector selector = parseSelector(rawSelector, ordinal);
            if (selector == null) {
                warn(request, "schmaloogium.warn.idmap.invalid_selector", rawSelector, line);
                ordinal++;
                continue;
            }
            out.add(factory.create(selector));
            ordinal++;
        }
    }

    /** Parses one ENTRY or TAG selector; null on rejection. */
    private Selector parseSelector(String raw, int ordinal) {
        if (raw.startsWith("%")) {
            String tagId = raw.substring(1);
            if (isPureNumber(tagId)) {
                return null; // %123 rejected
            }
            String canonical = canonicalTag(tagId);
            if (canonical == null) {
                return null;
            }
            return new Selector(SelectorKind.TAG, canonical, Optional.empty(), List.of(), ordinal);
        }
        // ENTRY: identity [":" metadata] (":" property "=" alternatives)*
        String[] parts = raw.split(":", -1);
        if (parts.length < 1 || parts[0].isEmpty()) {
            return null;
        }
        String identity;
        int nextPart;
        boolean numericIdentity = isPureNumber(parts[0]);
        if (parts.length >= 2 && !parts[1].contains("=") && !isNumericListCandidate(parts[1])
                && !isPureNumber(parts[0])) {
            identity = parts[0] + ":" + parts[1];
            nextPart = 2;
        } else if (numericIdentity) {
            identity = parts[0];
            nextPart = 1;
        } else {
            identity = "minecraft:" + parts[0];
            nextPart = 1;
        }
        if (!isPureNumber(identity) && !validIdentity(identity)) {
            return null;
        }
        Optional<MetadataConstraint> metadata = Optional.empty();
        List<PropertyPredicate> predicates = new ArrayList<>();
        int i = nextPart;
        if (i < parts.length && !parts[i].contains("=")) {
            MetadataConstraint constraint = parseMetadata(parts[i]);
            if (constraint == null) {
                return null;
            }
            metadata = Optional.of(constraint);
            i++;
        }
        Set<String> propertyNames = new java.util.HashSet<>();
        for (; i < parts.length; i++) {
            int eq = parts[i].indexOf('=');
            if (eq <= 0) {
                return null;
            }
            String property = parts[i].substring(0, eq);
            if (!property.matches("[a-z0-9_]+") || !propertyNames.add(property)) {
                return null;
            }
            List<PropertyValueConstraint> accepted = new ArrayList<>();
            java.util.LinkedHashSet<String> normalized = new java.util.LinkedHashSet<>();
            for (String alternative : parts[i].substring(eq + 1).split(",", -1)) {
                PropertyValueConstraint constraint = parseAlternative(alternative);
                if (constraint == null || !normalized.add(canonicalAlternative(constraint))) {
                    return null;
                }
                accepted.add(constraint);
            }
            if (accepted.isEmpty()) {
                return null;
            }
            predicates.add(new PropertyPredicate(property, List.copyOf(accepted)));
        }
        return new Selector(SelectorKind.ENTRY, identity, metadata,
            List.copyOf(predicates), ordinal);
    }

    /** Entry identities are lowercase namespaced paths or legacy numeric IDs. */
    private static boolean validIdentity(String identity) {
        int colon = identity.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : identity.substring(0, colon);
        String path = colon < 0 ? identity : identity.substring(colon + 1);
        if (identity.indexOf(':', colon + 1) >= 0 || namespace.isEmpty()
                || !namespace.matches("[a-z0-9][a-z0-9._-]*")) {
            return false;
        }
        for (String segment : path.split("/", -1)) {
            if (segment.isEmpty() || segment.equals(".") || segment.equals("..")
                    || !segment.matches("[a-z0-9._-]+")) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumericListCandidate(String s) {
        return !s.isEmpty() && (Character.isDigit(s.charAt(0)) || s.charAt(0) == '+'
            || s.charAt(0) == '-' || s.charAt(0) == ',');
    }

    private static boolean isPureNumber(String s) {
        return s.matches("[0-9]+");
    }

    private String canonicalTag(String tagId) {
        int colon = tagId.indexOf(':');
        String namespace;
        String path;
        if (colon < 0) {
            namespace = "minecraft";
            path = tagId;
        } else {
            if (tagId.indexOf(':', colon + 1) >= 0) {
                return null;
            }
            namespace = tagId.substring(0, colon);
            path = tagId.substring(colon + 1);
        }
        if (namespace.isEmpty() || !namespace.matches("[a-z0-9][a-z0-9._-]*")) {
            return null;
        }
        for (String segment : path.split("/", -1)) {
            if (segment.isEmpty() || segment.equals(".") || segment.equals("..")
                    || !segment.matches("[a-z0-9._-]+")) {
                return null;
            }
        }
        return namespace + ":" + path;
    }

    private MetadataConstraint parseMetadata(String raw) {
        List<IntegerRange> ranges = new ArrayList<>();
        java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<>();
        for (String atom : raw.split(",", -1)) {
            IntegerRange range = parseRange(atom, 15);
            if (range == null || !seen.add(canonicalRange(range))) {
                return null;
            }
            ranges.add(range);
        }
        if (ranges.isEmpty()) {
            return null;
        }
        return new MetadataConstraint(List.copyOf(ranges));
    }

    private IntegerRange parseRange(String atom, int max) {
        int hyphen = atom.indexOf('-');
        try {
            if (hyphen < 0) {
                int n = Integer.parseUnsignedInt(atom);
                if (n > max) {
                    return null;
                }
                return new IntegerRange(n, n);
            }
            int lo = Integer.parseUnsignedInt(atom.substring(0, hyphen));
            int hi = Integer.parseUnsignedInt(atom.substring(hyphen + 1));
            if (lo > hi || hi > max) {
                return null;
            }
            return new IntegerRange(lo, hi);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String canonicalRange(IntegerRange range) {
        return range.lowerInclusive() + "-" + range.upperInclusive();
    }

    private PropertyValueConstraint parseAlternative(String atom) {
        if (atom.isEmpty()) {
            return null;
        }
        int hyphen = atom.indexOf('-');
        if (hyphen > 0 && isDigits(atom.substring(0, hyphen)) && isDigits(atom.substring(hyphen + 1))) {
            IntegerRange range = parseRange(atom, Integer.MAX_VALUE);
            return range == null ? null : new PropertyValueConstraint.IntegerInterval(range);
        }
        if (!atom.matches("[a-z0-9_]+")) {
            return null;
        }
        String literal = atom;
        if (isDigits(atom)) {
            try {
                int v = Integer.parseUnsignedInt(atom);
                literal = Integer.toString(v);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return new PropertyValueConstraint.Literal(literal);
    }

    private static boolean isDigits(String s) {
        return !s.isEmpty() && s.chars().allMatch(Character::isDigit);
    }

    private String canonicalAlternative(PropertyValueConstraint constraint) {
        if (constraint instanceof PropertyValueConstraint.Literal l) {
            return "lit:" + l.value();
        }
        return canonicalRange(((PropertyValueConstraint.IntegerInterval) constraint).range());
    }

    private void warn(IdMappingParseRequest request, String key, String arg, int line) {
        DiagnosticReporter reporter = request.diagnostics();
        if (reporter != null) {
            reporter.report(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY,
                key, List.of(arg, line), "", "schmaloogium.config"));
        }
    }

    private String absentFingerprint(IdMappingParseRequest request) {
        return "absent:" + request.kind() + ":" + request.environment().mcVersion();
    }

    private String fingerprint(IdMappingParseRequest request, MappingFileState state,
            List<MappingRule> ordinary, List<MappingRule> forced) {
        return "idmap:" + request.kind() + ":" + state + ":" + ordinary.size()
            + ":" + forced.size() + ":" + request.environment().mcVersion();
    }

    private IdMappingFileInput emptyResult(MappingKind kind, MappingFileState state,
            List<EngineDiagnostic> diagnostics) {
        return new IdMappingFileInput(kind == null ? MappingKind.BLOCK : kind, state,
            List.of(), List.of(), new IdMappingFileFingerprint("invalid"));
    }
}
