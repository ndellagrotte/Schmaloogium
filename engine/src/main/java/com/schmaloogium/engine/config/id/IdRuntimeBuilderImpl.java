// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.IdMappingParseRequest;
import com.schmaloogium.engine.config.IdMappingParser;
import com.schmaloogium.engine.config.IdRule;
import com.schmaloogium.engine.config.IntegerRange;
import com.schmaloogium.engine.config.LayerRule;
import com.schmaloogium.engine.config.MappingEra;
import com.schmaloogium.engine.config.MappingFileState;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.MappingOrigin;
import com.schmaloogium.engine.config.MappingRule;
import com.schmaloogium.engine.config.MetadataConstraint;
import com.schmaloogium.engine.config.ModMappingOrigin;
import com.schmaloogium.engine.config.PropertyPredicate;
import com.schmaloogium.engine.config.PropertyValueConstraint;
import com.schmaloogium.engine.config.PackMappingOrigin;
import com.schmaloogium.engine.config.SelectorKind;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.pack.PackFrontEnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The §4.4 merge/precedence resolver (PHASE_9_DOC §§4.3–4.9). One build parses the
 * bounded mod sources through the exact Phase 3 parser environment, freezes the tag
 * shim, applies the BLOCK/ENTITY 11300 selection per contribution, resolves selectors
 * with exact-before-expansion and the MODERN ambiguous-token exception, fills tier 4
 * only when the pack block file is absent, and never mutates Phase 3 data or a prior
 * table. Diagnostics are buffered and flushed in deterministic source order.
 */
final class IdRuntimeBuilderImpl implements IdRuntimeBuilder {

    /** The representable 16-bit mc_Entity alias domain, read by low-bit pattern. */
    static final int MIN_REPRESENTABLE_ALIAS = -32768;
    static final int MAX_REPRESENTABLE_ALIAS = 65535;

    private final IdMappingParser parser;
    private final Set<String> emittedWarnKeys = new HashSet<>();
    private String warnScope = null;

    IdRuntimeBuilderImpl(IdMappingParser parser) {
        this.parser = java.util.Objects.requireNonNull(parser, "parser");
    }

    @Override
    public synchronized IdBuildResult build(IdBuildRequest request) {
        List<EngineDiagnostic> unconditional = new ArrayList<>();
        try {
            return buildInternal(request, unconditional);
        } catch (RuntimeException e) {
            IdBuildFailure failure =
                    new IdBuildFailure(IdBuildFailure.Kind.INTERNAL, String.valueOf(e));
            unconditional.add(failureDiagnostic(failure));
            return failed(request, failure, unconditional);
        }
    }

    private IdBuildResult buildInternal(IdBuildRequest request,
            List<EngineDiagnostic> unconditional) {
        // Schema gate: reject every non-current version before any derivation (D-P9-22).
        if (request.mappings().schemaVersion() != PackFrontEnd.CURRENT_SCHEMA_VERSION) {
            IdBuildFailure failure = new IdBuildFailure(IdBuildFailure.Kind.SCHEMA_MISMATCH,
                    "mapping input schema " + request.mappings().schemaVersion()
                            + " is not the current schema " + PackFrontEnd.CURRENT_SCHEMA_VERSION);
            unconditional.add(failureDiagnostic(failure));
            return failed(request, failure, unconditional);
        }
        IdIndexes indexes;
        try {
            indexes = new IdIndexes(request.registries());
        } catch (RuntimeException e) {
            IdBuildFailure failure = new IdBuildFailure(
                    IdBuildFailure.Kind.SNAPSHOT_INCONSISTENT, String.valueOf(e.getMessage()));
            unconditional.add(failureDiagnostic(failure));
            return failed(request, failure, unconditional);
        }

        // Parse the bounded mod sources with the exact published parser environment.
        ParsedMods mods = parseModSources(request, unconditional);

        CompatibilityAliasCatalog catalog = request.aliases();
        // Selection is input-determined, so identity and warn scope compute up front.
        IdSourceFingerprint sourceFingerprint = sourceFingerprint(request, catalog, mods);
        String scopeKey = Fingerprints.sha256Hex(sourceFingerprint.value() + ":"
                + request.registries().fingerprint().value() + ":"
                + request.registries().registryGeneration());
        if (!scopeKey.equals(warnScope)) {
            emittedWarnKeys.clear();
            warnScope = scopeKey;
        }
        WarnOnce warnOnce = new WarnOnce(emittedWarnKeys, scopeKey);
        for (EngineDiagnostic diagnostic : unconditional) {
            warnOnce.unconditional(diagnostic);
        }

        TagMembershipSnapshot tags = freezeTags(request.tags(), request.registries(), warnOnce);

        IdResolutionTables tables = new IdResolutionTables(
                indexes.stateCount(), request.registries().items().size(),
                request.registries().entities().size());
        for (int i = 0; i < indexes.stateCount(); i++) {
            tables.renderType[i] = indexes.state(i).renderType();
            tables.legacyMetadata[i] = indexes.state(i).legacyMetadata();
        }
        Resolver resolver = new Resolver(request, mods, indexes, catalog, tags, tables, warnOnce);
        try {
            resolver.run();
        } catch (RuntimeException e) {
            warnOnce.unconditional(failureDiagnostic(
                    new IdBuildFailure(IdBuildFailure.Kind.INTERNAL, String.valueOf(e))));
            flush(request, warnOnce);
            return new IdBuildResult.Failed(
                    new IdBuildFailure(IdBuildFailure.Kind.INTERNAL, String.valueOf(e)));
        }

        IdRuntimeFingerprint runtimeFingerprint = runtimeFingerprint(request, sourceFingerprint);
        List<EngineDiagnostic> diagnostics = warnOnce.drain();
        for (EngineDiagnostic diagnostic : diagnostics) {
            request.diagnostics().report(diagnostic);
        }
        IdRuntimeCandidateImpl candidate = new IdRuntimeCandidateImpl(
                tables, runtimeFingerprint, sourceFingerprint,
                request.registries().registryGeneration(),
                request.registries().fingerprint(), diagnostics);
        return new IdBuildResult.Built(candidate);
    }

    private IdBuildResult failed(IdBuildRequest request, IdBuildFailure failure,
            List<EngineDiagnostic> unconditional) {
        for (EngineDiagnostic diagnostic : unconditional) {
            request.diagnostics().report(diagnostic);
        }
        return new IdBuildResult.Failed(failure);
    }

    private static void flush(IdBuildRequest request, WarnOnce warnOnce) {
        for (EngineDiagnostic diagnostic : warnOnce.drain()) {
            request.diagnostics().report(diagnostic);
        }
    }

    private static EngineDiagnostic failureDiagnostic(IdBuildFailure failure) {
        String key = switch (failure.kind()) {
            case SCHEMA_MISMATCH -> "schmaloogium.error.ids.schema_mismatch";
            case SNAPSHOT_INCONSISTENT -> "schmaloogium.error.ids.snapshot_inconsistent";
            case INVALID_REQUEST -> "schmaloogium.error.ids.invalid_request";
            case INTERNAL -> "schmaloogium.error.ids.build_failed";
        };
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY, key,
                List.of(failure.kind()), failure.detail(), LogChannels.CONFIG);
    }

    // ------------------------------------------------------------------ mod sources

    /** Parsed per-mod mapping inputs in deterministic mod order (§4.3, D-P9-9). */
    private record ModInput(String modId, int contributionOrdinal,
            Map<MappingKind, IdMappingFileInput> files) {
    }

    private record ParsedMods(List<ModInput> mods) {

        static final ParsedMods NONE = new ParsedMods(List.of());
    }

    private ParsedMods parseModSources(IdBuildRequest request,
            List<EngineDiagnostic> unconditional) {
        if (request.modSources().sources().isEmpty()) {
            return ParsedMods.NONE;
        }
        IdMappingMacroEnvironment environment = request.mappings().parserEnvironment();
        Map<String, Map<MappingKind, IdMappingFileInput>> byMod = new TreeMap<>();
        Map<String, Integer> ordinals = new HashMap<>();
        for (ModSourceEntry entry : request.modSources().sources()) {
            int ordinal = ordinals.computeIfAbsent(entry.modId(), modId -> byMod.size());
            ModMappingOrigin origin =
                    new ModMappingOrigin(entry.modId(), ordinal, entry.sourceName());
            try {
                IdMappingFileInput parsed = parser.parse(new IdMappingParseRequest(entry.kind(),
                        Optional.of(entry.bytes()), origin, environment, request.diagnostics()));
                byMod.computeIfAbsent(entry.modId(), modId -> new HashMap<>())
                        .put(entry.kind(), parsed);
            } catch (RuntimeException e) {
                // One unreadable/invalid mod file warns once, contributes no rules, and
                // sibling mods and mapping kinds continue (§4.3).
                unconditional.add(new EngineDiagnostic(DiagnosticSeverity.WARN,
                        UserChannel.LOG_ONLY, "schmaloogium.warn.ids.mod_source_unreadable",
                        List.of(entry.modId(), entry.kind(), entry.sourceName()),
                        String.valueOf(e), LogChannels.CONFIG));
            }
        }
        List<ModInput> mods = new ArrayList<>();
        for (Map.Entry<String, Map<MappingKind, IdMappingFileInput>> entry : byMod.entrySet()) {
            mods.add(new ModInput(entry.getKey(), ordinals.get(entry.getKey()), entry.getValue()));
        }
        return new ParsedMods(List.copyOf(mods));
    }

    // ------------------------------------------------------------------ tag freeze

    private TagMembershipSnapshot freezeTags(LegacyTagCatalog catalog,
            IdRegistrySnapshot registries, WarnOnce warnOnce) {
        if (catalog.bindings().isEmpty()) {
            return TagMembershipSnapshot.empty();
        }
        Map<String, List<RegistryName>> frozen = new TreeMap<>();
        for (Map.Entry<String, List<LegacyTagCatalog.Binding>> entry
                : catalog.bindings().entrySet()) {
            Set<RegistryName> names = new TreeSet<>();
            for (LegacyTagCatalog.Binding binding : entry.getValue()) {
                try {
                    for (RegistryName name : binding.provider().members(entry.getKey(), registries)) {
                        // Providers return only names of the same snapshot; defend here.
                        if (name != null && nameExists(registries, name)) {
                            names.add(name);
                        }
                    }
                } catch (RuntimeException e) {
                    warnOnce.warn("schmaloogium.warn.ids.tag_provider_failed",
                            List.of(entry.getKey(), binding.providerName()),
                            String.valueOf(e), LogChannels.CONFIG,
                            "tag-provider|" + entry.getKey() + "|" + binding.providerName());
                }
            }
            frozen.put(entry.getKey(), List.copyOf(names));
        }
        return new TagMembershipSnapshot(frozen);
    }

    private static boolean nameExists(IdRegistrySnapshot registries, RegistryName name) {
        for (BlockTypeRecord block : registries.blocks()) {
            if (block.name().equals(name)) {
                return true;
            }
        }
        for (ItemTypeRecord item : registries.items()) {
            if (item.name().equals(name)) {
                return true;
            }
        }
        for (EntityTypeRecord entity : registries.entities()) {
            if (entity.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ identity

    private IdSourceFingerprint sourceFingerprint(IdBuildRequest request,
            CompatibilityAliasCatalog catalog, ParsedMods mods) {
        StringBuilder canonical = new StringBuilder();
        canonical.append("schema=").append(request.mappings().schemaVersion()).append('\n');
        appendFileIdentity(canonical, "pack-block", request.mappings().blocks(), true);
        appendFileIdentity(canonical, "pack-item", request.mappings().items(), false);
        appendFileIdentity(canonical, "pack-entity", request.mappings().entities(), true);
        appendFileIdentity(canonical, "pack-layer", request.mappings().layers(), false);
        IdMappingMacroEnvironment env = request.mappings().parserEnvironment();
        canonical.append("env=").append(env.mcVersion());
        for (var macro : env.standardMacros()) {
            canonical.append('|').append(macro.name()).append('=').append(macro.replacement());
        }
        canonical.append('\n');
        for (ModInput mod : mods.mods()) {
            for (MappingKind kind : List.of(MappingKind.BLOCK, MappingKind.ITEM,
                    MappingKind.ENTITY)) {
                IdMappingFileInput file = mod.files().get(kind);
                canonical.append("mod:").append(mod.modId()).append(':').append(kind)
                        .append('=').append(file == null ? "absent" : discriminator(file, true))
                        .append(':').append(file == null ? "-" : file.fingerprint().value())
                        .append('\n');
            }
        }
        canonical.append("alias=").append(catalog.version()).append(':')
                .append(catalog.fingerprint()).append('\n');
        canonical.append("tags=").append(request.tags().version()).append('\n');
        return new IdSourceFingerprint(Fingerprints.sha256Hex(canonical.toString()));
    }

    private void appendFileIdentity(StringBuilder canonical, String label,
            IdMappingFileInput file, boolean allowForced) {
        canonical.append(label).append('=').append(discriminator(file, allowForced))
                .append(':').append(file.fingerprint().value()).append('\n');
    }

    /** The §4.7 selected-list discriminator of one contribution file. */
    private static String discriminator(IdMappingFileInput file, boolean allowForced) {
        if (!allowForced) {
            return file.state() == MappingFileState.ABSENT ? "absent" : "ordinary";
        }
        return switch (file.state()) {
            case ABSENT -> "absent";
            case PRESENT_RULES -> "ordinary";
            case PRESENT_EMPTY -> file.forced11300Rules().isEmpty()
                    ? "present-empty" : "forced11300";
        };
    }

    private IdRuntimeFingerprint runtimeFingerprint(IdBuildRequest request,
            IdSourceFingerprint sourceFingerprint) {
        HandLightPolicy policy = request.handLightPolicy();
        String canonical = "source=" + sourceFingerprint.value()
                + "\nregistry=" + request.registries().registryGeneration()
                + ":" + request.registries().fingerprint().value()
                + "\npolicy=old:" + policy.resolvedOldHandLight()
                + ",dynamic:" + policy.dynamicHandLightActive() + "\n";
        return new IdRuntimeFingerprint(Fingerprints.sha256Hex(canonical));
    }

    // ------------------------------------------------------------------ resolution

    /** One build's resolution pass over fixed inputs. */
    private static final class Resolver {

        private final IdBuildRequest request;
        private final ParsedMods mods;
        private final IdIndexes indexes;
        private final CompatibilityAliasCatalog catalog;
        private final TagMembershipSnapshot tags;
        private final IdResolutionTables tables;
        private final WarnOnce warnOnce;
        private final Set<Object> conflictsReported = new HashSet<>();

        Resolver(IdBuildRequest request, ParsedMods mods, IdIndexes indexes,
                CompatibilityAliasCatalog catalog, TagMembershipSnapshot tags,
                IdResolutionTables tables, WarnOnce warnOnce) {
            this.request = request;
            this.mods = mods;
            this.indexes = indexes;
            this.catalog = catalog;
            this.tags = tags;
            this.tables = tables;
            this.warnOnce = warnOnce;
        }

        void run() {
            resolveKind(MappingKind.BLOCK, request.mappings().blocks());
            resolveKind(MappingKind.ITEM, request.mappings().items());
            resolveKind(MappingKind.ENTITY, request.mappings().entities());
            resolveLayerFile(request.mappings().layers());
            // §4.8: tier 4 activates exactly when the pack block file state is ABSENT.
            if (request.mappings().blocks().state() == MappingFileState.ABSENT) {
                applyLegacyFallback();
            }
            reportUnrepresentableAliases();
        }

        /** Tiers 1–3: pack entries, pack tags, then each mod's entries then tags. */
        private void resolveKind(MappingKind kind, IdMappingFileInput packFile) {
            resolveContribution(packFile, kind);
            for (ModInput mod : mods.mods()) {
                resolveContribution(mod.files().get(kind), kind);
            }
        }

        private void resolveContribution(IdMappingFileInput file, MappingKind kind) {
            if (file == null) {
                return;
            }
            List<MappingRule> selected = selectedRules(file, kind);
            for (MappingRule rule : selected) {
                if (rule.selectorKind() == SelectorKind.ENTRY) {
                    resolveRule(rule, kind);
                }
            }
            for (MappingRule rule : selected) {
                if (rule.selectorKind() == SelectorKind.TAG) {
                    resolveRule(rule, kind);
                }
            }
        }

        private void resolveLayerFile(IdMappingFileInput file) {
            if (file == null) {
                return;
            }
            List<MappingRule> selected = selectedRules(file, MappingKind.LAYER);
            for (MappingRule rule : selected) {
                if (rule.selectorKind() == SelectorKind.ENTRY) {
                    resolveRule(rule, MappingKind.LAYER);
                }
            }
            for (MappingRule rule : selected) {
                if (rule.selectorKind() == SelectorKind.TAG) {
                    resolveRule(rule, MappingKind.LAYER);
                }
            }
        }

        /** §4.7 BLOCK/ENTITY selection; ITEM/LAYER contribute ordinary rules only. */
        private static List<MappingRule> selectedRules(IdMappingFileInput file, MappingKind kind) {
            if (kind != MappingKind.BLOCK && kind != MappingKind.ENTITY) {
                return file.ordinaryRules();
            }
            return switch (file.state()) {
                case ABSENT -> List.of();
                case PRESENT_RULES -> file.ordinaryRules();
                case PRESENT_EMPTY -> file.forced11300Rules().isEmpty()
                        ? List.of() : file.forced11300Rules();
            };
        }

        private void resolveRule(MappingRule rule, MappingKind kind) {
            switch (kind) {
                case BLOCK -> {
                    List<Integer> states = rule.selectorKind() == SelectorKind.TAG
                            ? matchTagStates(rule) : matchBlockStates(rule);
                    assignBlocks((IdRule) rule, states);
                }
                case ITEM -> {
                    IdRule idRule = (IdRule) rule;
                    List<Integer> items = rule.selectorKind() == SelectorKind.TAG
                            ? matchTagged(idRule, "item") : matchNamed(idRule, true);
                    assignItems(idRule, items);
                }
                case ENTITY -> {
                    IdRule idRule = (IdRule) rule;
                    List<Integer> entities = rule.selectorKind() == SelectorKind.TAG
                            ? matchTagged(idRule, "entity") : matchNamed(idRule, false);
                    assignEntities(idRule, entities);
                }
                case LAYER -> {
                    LayerRule layerRule = (LayerRule) rule;
                    List<Integer> states = rule.selectorKind() == SelectorKind.TAG
                            ? matchTagStates(rule) : matchBlockStates(rule);
                    assignLayer(layerRule, states);
                }
            }
        }

        // ------------------------------------------------------------ block matching

        private List<Integer> matchBlockStates(MappingRule rule) {
            String token = rule.selectorToken();
            if (isNumeric(token)) {
                int numeric = parseNumeric(token);
                BlockTypeRecord block = numeric >= 0 ? indexes.blockByNumericId(numeric) : null;
                if (block == null) {
                    warnUnknown(rule, token);
                    return List.of();
                }
                return statesFor(rule, block);
            }
            RegistryName name;
            try {
                name = RegistryName.parse(token);
            } catch (RuntimeException e) {
                warnUnknown(rule, token);
                return List.of();
            }
            // The sole era exception: a MODERN-provenance rule for an ambiguous token
            // replaces exact lookup with the alias expansion (§4.5/§4.6).
            if (rule.era() == MappingEra.MODERN) {
                Optional<RegistryName> ambiguous = catalog.ambiguousModernTarget(name);
                if (ambiguous.isPresent()) {
                    BlockTypeRecord target = indexes.block(ambiguous.get());
                    if (target == null) {
                        warnMissingTarget(rule, name, ambiguous.get());
                        return List.of();
                    }
                    return statesFor(rule, target);
                }
            }
            BlockTypeRecord block = indexes.block(name);
            if (block != null) {
                // Still/flowing groups add the live counterpart after the written token;
                // §4.4 assign-if-absent still controls (§4.6).
                List<Integer> matched = new ArrayList<>(statesFor(rule, block));
                Optional<RegistryName> counterpart = catalog.fluidCounterpart(name);
                if (counterpart.isPresent()) {
                    BlockTypeRecord other = indexes.block(counterpart.get());
                    if (other != null) {
                        matched.addAll(statesFor(rule, other));
                    } else {
                        warnMissingTarget(rule, name, counterpart.get());
                    }
                }
                return matched;
            }
            Optional<RegistryName> modern = catalog.modernNameTarget(name);
            if (modern.isPresent()) {
                BlockTypeRecord target = indexes.block(modern.get());
                if (target == null) {
                    warnMissingTarget(rule, name, modern.get());
                    return List.of();
                }
                return new ArrayList<>(statesFor(rule, target));
            }
            Optional<RegistryName> counterpart = catalog.fluidCounterpart(name);
            if (counterpart.isPresent()) {
                BlockTypeRecord other = indexes.block(counterpart.get());
                if (other == null) {
                    warnMissingTarget(rule, name, counterpart.get());
                    return List.of();
                }
                return new ArrayList<>(statesFor(rule, other));
            }
            warnUnknown(rule, token);
            return List.of();
        }

        private List<Integer> matchTagStates(MappingRule rule) {
            Optional<List<RegistryName>> members = tags.members(rule.selectorToken());
            if (members.isEmpty() || members.get().isEmpty()) {
                warnTagUnresolved(rule);
                return List.of();
            }
            List<Integer> matched = new ArrayList<>();
            for (RegistryName name : members.get()) {
                BlockTypeRecord block = indexes.block(name);
                if (block != null) {
                    matched.addAll(statesFor(rule, block));
                }
            }
            if (matched.isEmpty()) {
                warnNoMatch(rule);
            }
            return matched;
        }

        /**
         * Predicates validate against the block's finite live domain first: a missing
         * property, unknown literal or disjoint interval assigns nothing for the whole
         * selector (§4.5, D-P9-23).
         */
        private List<Integer> statesFor(MappingRule rule, BlockTypeRecord block) {
            List<PropertyPredicate> predicates = predicatesOf(rule);
            for (PropertyPredicate predicate : predicates) {
                IdIndexes.PropertyDomain domain =
                        indexes.propertyDomain(block, predicate.propertyName());
                if (domain == null || !predicateMatchesDomain(domain, predicate)) {
                    warnInvalidPredicate(rule, predicate);
                    return List.of();
                }
            }
            Optional<MetadataConstraint> metadata = metadataOf(rule);
            List<Integer> matched = new ArrayList<>();
            for (int stateOrdinal : block.stateOrdinals()) {
                if (stateMatches(metadata, predicates, indexes.state(stateOrdinal))) {
                    matched.add(stateOrdinal);
                }
            }
            if (matched.isEmpty()) {
                warnNoMatch(rule);
            }
            return matched;
        }

        private boolean predicateMatchesDomain(IdIndexes.PropertyDomain domain,
                PropertyPredicate predicate) {
            for (PropertyValueConstraint alternative : predicate.acceptedValues()) {
                if (alternative instanceof PropertyValueConstraint.Literal literal) {
                    if (!domain.contains(literal.value())) {
                        return false;
                    }
                } else {
                    IntegerRange range =
                            ((PropertyValueConstraint.IntegerInterval) alternative).range();
                    if (!domain.intersects(range.lowerInclusive(), range.upperInclusive())) {
                        return false;
                    }
                }
            }
            return true;
        }

        /** OR alternatives within each property; AND metadata and all predicates. */
        private boolean stateMatches(Optional<MetadataConstraint> metadata,
                List<PropertyPredicate> predicates, BlockStateRecord state) {
            if (metadata.isPresent()
                    && !metadataMatches(metadata.get(), state.legacyMetadata())) {
                return false;
            }
            for (PropertyPredicate predicate : predicates) {
                String value = state.properties().get(predicate.propertyName());
                if (value == null || !propertyValueMatches(predicate, value)) {
                    return false;
                }
            }
            return true;
        }

        private boolean propertyValueMatches(PropertyPredicate predicate, String value) {
            boolean any = false;
            for (PropertyValueConstraint alternative : predicate.acceptedValues()) {
                if (alternative instanceof PropertyValueConstraint.Literal literal) {
                    any = literal.value().equals(value);
                } else {
                    Integer parsed = IdIndexes.canonicalDecimal(value);
                    IntegerRange range =
                            ((PropertyValueConstraint.IntegerInterval) alternative).range();
                    any = parsed != null && parsed >= range.lowerInclusive()
                            && parsed <= range.upperInclusive();
                }
                if (any) {
                    break;
                }
            }
            return any;
        }

        private boolean metadataMatches(MetadataConstraint constraint, int legacyMetadata) {
            for (IntegerRange range : constraint.alternatives()) {
                if (legacyMetadata >= range.lowerInclusive()
                        && legacyMetadata <= range.upperInclusive()) {
                    return true;
                }
            }
            return false;
        }

        /** The typed selector constraints shared by entry and layer rules. */
        private static Optional<MetadataConstraint> metadataOf(MappingRule rule) {
            if (rule instanceof IdRule id) {
                return id.legacyMetadata();
            }
            if (rule instanceof LayerRule layer) {
                return layer.legacyMetadata();
            }
            return Optional.empty();
        }

        private static List<PropertyPredicate> predicatesOf(MappingRule rule) {
            if (rule instanceof IdRule id) {
                return id.propertyPredicates();
            }
            if (rule instanceof LayerRule layer) {
                return layer.propertyPredicates();
            }
            return List.of();
        }

        // ------------------------------------------------------------ item/entity matching

        /** Exact names only; a predicate-bearing rule warns and matches nothing. */
        private List<Integer> matchNamed(IdRule rule, boolean item) {
            if (!rule.propertyPredicates().isEmpty() || rule.legacyMetadata().isPresent()) {
                warnPredicateUnsupported(rule);
                return List.of();
            }
            String token = rule.selectorToken();
            if (isNumeric(token)) {
                warnUnknown(rule, token);
                return List.of();
            }
            RegistryName name;
            try {
                name = RegistryName.parse(token);
            } catch (RuntimeException e) {
                warnUnknown(rule, token);
                return List.of();
            }
            if (item) {
                ItemTypeRecord found = indexes.item(name);
                if (found != null) {
                    return List.of(found.itemOrdinal());
                }
                Optional<RegistryName> modern = catalog.modernNameTarget(name);
                if (modern.isEmpty()) {
                    warnUnknown(rule, token);
                    return List.of();
                }
                ItemTypeRecord target = indexes.item(modern.get());
                if (target == null) {
                    warnMissingTarget(rule, name, modern.get());
                    return List.of();
                }
                return List.of(target.itemOrdinal());
            }
            EntityTypeRecord found = indexes.entity(name);
            return found == null ? List.of() : List.of(found.entityTypeOrdinal());
        }

        private List<Integer> matchTagged(IdRule rule, String universe) {
            if (!rule.propertyPredicates().isEmpty() || rule.legacyMetadata().isPresent()) {
                warnPredicateUnsupported(rule);
                return List.of();
            }
            Optional<List<RegistryName>> members = tags.members(rule.selectorToken());
            if (members.isEmpty() || members.get().isEmpty()) {
                warnTagUnresolved(rule);
                return List.of();
            }
            List<Integer> matched = new ArrayList<>();
            for (RegistryName name : members.get()) {
                if ("item".equals(universe)) {
                    ItemTypeRecord item = indexes.item(name);
                    if (item != null) {
                        matched.add(item.itemOrdinal());
                    }
                } else {
                    EntityTypeRecord entity = indexes.entity(name);
                    if (entity != null) {
                        matched.add(entity.entityTypeOrdinal());
                    }
                }
            }
            if (matched.isEmpty()) {
                warnNoMatch(rule);
            }
            return matched;
        }

        // ------------------------------------------------------------ assignment

        private void assignBlocks(IdRule rule, List<Integer> states) {
            for (int stateOrdinal : states) {
                if (!tables.blockAssigned[stateOrdinal]) {
                    tables.blockAssigned[stateOrdinal] = true;
                    tables.blockIds[stateOrdinal] = rule.shaderId();
                    tables.winnerByState[stateOrdinal] = rule;
                    tables.blockAssignments++;
                } else if (tables.winnerByState[stateOrdinal] != rule) {
                    reportConflict(rule, (MappingRule) tables.winnerByState[stateOrdinal]);
                }
            }
        }
        private void assignItems(IdRule rule, List<Integer> items) {
            for (int itemOrdinal : items) {
                if (!tables.itemAssigned[itemOrdinal]) {
                    tables.itemAssigned[itemOrdinal] = true;
                    tables.itemIds[itemOrdinal] = rule.shaderId();
                    tables.winnerByItem[itemOrdinal] = rule;
                    tables.itemAssignments++;
                } else if (tables.winnerByItem[itemOrdinal] != rule) {
                    reportConflict(rule, (MappingRule) tables.winnerByItem[itemOrdinal]);
                }
            }
        }

        private void assignEntities(IdRule rule, List<Integer> entities) {
            for (int entityOrdinal : entities) {
                if (!tables.entityAssigned[entityOrdinal]) {
                    tables.entityAssigned[entityOrdinal] = true;
                    tables.entityIds[entityOrdinal] = rule.shaderId();
                    tables.winnerByEntity[entityOrdinal] = rule;
                    tables.entityAssignments++;
                } else if (tables.winnerByEntity[entityOrdinal] != rule) {
                    reportConflict(rule, (MappingRule) tables.winnerByEntity[entityOrdinal]);
                }
            }
        }
        private void assignLayer(LayerRule rule, List<Integer> states) {
            ResolvedRenderLayer layer = ResolvedRenderLayer.of(rule.layer());
            boolean excluded = false;
            for (int stateOrdinal : states) {
                if (indexes.state(stateOrdinal).solidOpaqueCube()) {
                    excluded = true;
                    continue;
                }
                if (tables.layers[stateOrdinal] == null) {
                    tables.layers[stateOrdinal] = layer;
                    tables.layerAssignments++;
                }
            }
            if (excluded) {
                warnOnce.warn("schmaloogium.warn.ids.layer_opaque_excluded",
                        List.of(rule.selectorToken(), rule.layer()), originText(rule),
                        LogChannels.CONFIG, onceKey(rule, "opaque-excluded"));
            }
        }

        /** §4.8: live vanilla numeric fill after explicit mod rules, absent-only. */
        private void applyLegacyFallback() {
            for (int stateOrdinal = 0; stateOrdinal < tables.stateCount; stateOrdinal++) {
                if (tables.blockAssigned[stateOrdinal]) {
                    continue;
                }
                BlockTypeRecord block = indexes.owningBlock(stateOrdinal);
                if (block == null || !"minecraft".equals(block.name().namespace())) {
                    continue;
                }
                tables.blockAssigned[stateOrdinal] = true;
                tables.blockIds[stateOrdinal] = block.liveLegacyNumericId();
                tables.winnerByState[stateOrdinal] = IdResolutionTables.FALLBACK;
                tables.blockAssignments++;
            }
        }

        /** §4.10: a full alias outside the domain reports once per rule, never truncates. */
        private void reportUnrepresentableAliases() {
            for (int stateOrdinal = 0; stateOrdinal < tables.stateCount; stateOrdinal++) {
                if (!tables.blockAssigned[stateOrdinal]) {
                    continue;
                }
                int id = tables.blockIds[stateOrdinal];
                if (id < MIN_REPRESENTABLE_ALIAS || id > MAX_REPRESENTABLE_ALIAS) {
                    Object winner = tables.winnerByState[stateOrdinal];
                    if (winner instanceof MappingRule rule) {
                        warnOnce.warn("schmaloogium.warn.ids.unrepresentable_alias",
                                List.of(rule.selectorToken(), id), originText(rule),
                                LogChannels.CONFIG, onceKey(rule, "unrepresentable"));
                    } else if (winner == IdResolutionTables.FALLBACK) {
                        warnOnce.warn("schmaloogium.warn.ids.unrepresentable_alias",
                                List.of("fallback", id), "legacy-numeric-fallback",
                                LogChannels.CONFIG, "fallback|unrepresentable");
                    }
                }
            }
        }

        private void reportConflict(MappingRule loser, MappingRule winner) {
            if (!conflictsReported.add(loser)) {
                return;
            }
            warnOnce.warn("schmaloogium.warn.ids.conflict",
                    List.of(ruleIdentity(winner), ruleIdentity(loser)),
                    "first-writer-wins", LogChannels.CONFIG,
                    onceKey(loser, "conflict|" + ruleIdentity(winner)));
        }

        // ------------------------------------------------------------ diagnostics

        private void warnUnknown(MappingRule rule, String token) {
            warnOnce.warn("schmaloogium.warn.ids.unknown_name",
                    List.of(token), originText(rule), LogChannels.CONFIG,
                    onceKey(rule, "unknown"));
        }

        private void warnMissingTarget(MappingRule rule, RegistryName source,
                RegistryName target) {
            warnOnce.warn("schmaloogium.warn.ids.missing_alias_target",
                    List.of(source.canonical(), target.canonical()), originText(rule),
                    LogChannels.CONFIG, onceKey(rule, "missing|" + target.canonical()));
        }

        private void warnInvalidPredicate(MappingRule rule, PropertyPredicate predicate) {
            warnOnce.warn("schmaloogium.warn.ids.invalid_predicate",
                    List.of(rule.selectorToken(), predicate.propertyName()), originText(rule),
                    LogChannels.CONFIG, onceKey(rule, "predicate|" + predicate.propertyName()));
        }

        private void warnNoMatch(MappingRule rule) {
            warnOnce.warn("schmaloogium.warn.ids.selector_matched_nothing",
                    List.of(rule.selectorToken()), originText(rule), LogChannels.CONFIG,
                    onceKey(rule, "no-match"));
        }

        private void warnTagUnresolved(MappingRule rule) {
            warnOnce.warn("schmaloogium.warn.ids.tag_unresolved",
                    List.of(rule.selectorToken()), originText(rule), LogChannels.CONFIG,
                    onceKey(rule, "tag-unresolved"));
        }

        private void warnPredicateUnsupported(MappingRule rule) {
            warnOnce.warn("schmaloogium.warn.ids.predicate_unsupported",
                    List.of(rule.selectorToken()), originText(rule), LogChannels.CONFIG,
                    onceKey(rule, "predicate-unsupported"));
        }

        private String onceKey(MappingRule rule, String reason) {
            return (rule instanceof LayerRule ? "LAYER" : "ID") + "|"
                    + originText(rule) + "|" + rule.sourceLine() + "|"
                    + rule.selectorToken() + "|" + reason;
        }

        private String originText(MappingRule rule) {
            MappingOrigin origin = rule.origin();
            if (origin instanceof PackMappingOrigin pack) {
                return "pack:" + pack.source().canonicalString();
            }
            if (origin instanceof ModMappingOrigin mod) {
                return "mod:" + mod.modId() + "#" + mod.contributionOrdinal()
                        + ":" + mod.sourceName();
            }
            return origin.toString();
        }

        private String ruleIdentity(MappingRule rule) {
            return originText(rule) + "@" + rule.sourceLine() + ":" + rule.selectorToken();
        }

        private static boolean isNumeric(String token) {
            return !token.isEmpty() && token.matches("[0-9]+");
        }

        private static int parseNumeric(String token) {
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }
}
