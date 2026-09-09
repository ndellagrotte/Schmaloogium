// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.ConstScanner;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.IdMappingParserImpl;
import com.schmaloogium.engine.config.IdMappingParseRequest;
import com.schmaloogium.engine.config.LogicalProperties;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.MacroDefinition;
import com.schmaloogium.engine.config.InternalOptionSnapshot;
import com.schmaloogium.engine.config.MacroIdentityPolicy;
import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionCatalogBuilder;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionDefinition;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.config.ProfileModel;
import com.schmaloogium.engine.config.ProfileScreenParser;
import com.schmaloogium.engine.config.ResourceRequirementsBuilder;
import com.schmaloogium.engine.config.ScreenModel;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.config.ShaderPropertiesParser;
import com.schmaloogium.engine.config.SliderSet;
import com.schmaloogium.engine.config.SourceAttribution;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.preprocess.IncludeEdge;
import com.schmaloogium.engine.preprocess.MacroEnvironmentBuilder;
import com.schmaloogium.engine.preprocess.PropertiesPreprocessor;
import com.schmaloogium.engine.preprocess.SourceCatalog;
import com.schmaloogium.engine.preprocess.SourceDocument;
import com.schmaloogium.engine.preprocess.SourceId;
import com.schmaloogium.engine.preprocess.SourceIndex;
import com.schmaloogium.engine.preprocess.SourceKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;

/**
 * The one front end implementation: deterministic discovery, safe input snapshots,
 * and the full load pipeline from selected location to one validated PackConfiguration.
 */
final class PackFrontEndImpl implements PackFrontEnd {

    private final PackFrontEndDomain domain;
    private final IdMappingParserImpl idMappingParser = new IdMappingParserImpl();

    PackFrontEndImpl(PackFrontEndDomain domain) {
        this.domain = domain;
    }

    @Override
    public DiscoveryLimits discoveryLimits() {
        return domain.limits;
    }

    // ------------------------------------------------------------------ discovery

    @Override
    public PackDiscoveryResult discover(PackDiscoveryRequest request) {
        java.util.Objects.requireNonNull(request, "request");
        long sequence = domain.nextCompletionSequence();
        String directoryKey = realKeyOrNull(request.shaderpacksDirectory());
        DiscoveryGenerationToken token = new DiscoveryGenerationToken(domain, directoryKey, sequence);
        List<EngineDiagnostic> diags = new ArrayList<>();
        List<PackCandidate> candidates = new ArrayList<>();
        candidates.add(new PackCandidate(new PackCandidateIdToken(domain, token, "Off"),
            Optional.empty(), PackCandidateKind.OFF, "Off", PackCandidateStatus.AVAILABLE,
            List.of()));
        candidates.add(new PackCandidate(new PackCandidateIdToken(domain, token, "(internal)"),
            Optional.empty(), PackCandidateKind.INTERNAL, "(internal)",
            PackCandidateStatus.AVAILABLE, List.of()));
        if (directoryKey != null && Files.isDirectory(request.shaderpacksDirectory())) {
            List<DiscoveryIndex.Entry> entries = DiscoveryIndex.scan(
                request.shaderpacksDirectory(), diags);
            Map<String, PackCandidateIdToken> byReference = new LinkedHashMap<>();
            for (DiscoveryIndex.Entry entry : entries) {
                PackCandidateKind kind = entry.directory()
                    ? PackCandidateKind.DIRECTORY : PackCandidateKind.ARCHIVE;
                PackCandidateStatus status = entry.valid()
                    ? PackCandidateStatus.AVAILABLE : PackCandidateStatus.UNREADABLE;
                PackCandidateIdToken id = new PackCandidateIdToken(domain, token, entry.displayName());
                byReference.put(entry.reference().canonicalValue(), id);
                candidates.add(new PackCandidate(id, Optional.of(entry.reference()), kind,
                    entry.displayName(), status, entry.diagnostics()));
            }
            domain.retainIndex(token, byReference, entries);
        } else {
            diags.add(diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.discovery_scan",
                "shaderpacks directory is not a readable directory"));
        }
        domain.retain(token);
        return new PackDiscoveryResult(token, candidates, diags);
    }

    private static String realKeyOrNull(Path directory) {
        try {
            Path real = directory.toRealPath();
            return Files.isDirectory(real) ? real.toString() : null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public FilesystemCandidateResolution resolveFilesystemCandidate(
            FilesystemCandidateReference reference, PackDiscoveryResult current) {
        java.util.Objects.requireNonNull(reference, "reference");
        java.util.Objects.requireNonNull(current, "current");
        if (!(current.generation() instanceof DiscoveryGenerationToken token)
                || token.domain != domain) {
            return new FilesystemCandidateResolution.InvalidSnapshot();
        }
        DiscoveryIndex.Snapshot snapshot = domain.index(token);
        if (snapshot == null) {
            return new FilesystemCandidateResolution.InvalidSnapshot();
        }
        PackCandidateIdToken id = snapshot.byReference().get(reference.canonicalValue());
        if (id == null) {
            return new FilesystemCandidateResolution.Missing();
        }
        DiscoveryIndex.Entry entry = snapshot.entry(id.debugName);
        if (entry == null) {
            return new FilesystemCandidateResolution.Missing();
        }
        Path host = entry.hostPath();
        boolean directoryNow = Files.isDirectory(host);
        if (!directoryNow && !Files.isRegularFile(host)) {
            return new FilesystemCandidateResolution.Missing();
        }
        if (directoryNow != entry.directory()) {
            return new FilesystemCandidateResolution.KindChanged(directoryNow
                ? PackCandidateKind.DIRECTORY : PackCandidateKind.ARCHIVE);
        }
        return new FilesystemCandidateResolution.Resolved(id);
    }

    @Override
    public PackOptionsTargetAcquisition packOptionsTarget(PackCandidateId candidate) {
        java.util.Objects.requireNonNull(candidate, "candidate");
        if (!(candidate instanceof PackCandidateIdToken token) || token.domain != domain) {
            return new PackOptionsTargetAcquisition.Rejected(
                PackOptionsTargetRejection.FOREIGN_DOMAIN);
        }
        DiscoveryIndex.Snapshot snapshot = token.generation
            instanceof DiscoveryGenerationToken generation ? domain.index(generation) : null;
        if (snapshot == null) {
            return new PackOptionsTargetAcquisition.Rejected(
                PackOptionsTargetRejection.SUPERSEDED_GENERATION);
        }
        DiscoveryIndex.Entry entry = snapshot.entry(token.debugName);
        if (entry == null || !entry.directory() && !Files.isRegularFile(entry.hostPath())) {
            return new PackOptionsTargetAcquisition.Rejected(
                PackOptionsTargetRejection.UNAVAILABLE);
        }
        return new PackOptionsTargetAcquisition.Acquired(new PackOptionsTargetValue(
            entry.reference(), entry.displayName(), domain, token));
    }

    // ------------------------------------------------------------------ load

    @Override
    public PackLoadResult load(PackLoadRequest request) {
        LoadOutcome outcome = runLoad(request);
        return switch (outcome) {
            case LoadOutcome.Off o -> new PackLoadResult.Off();
            case LoadOutcome.Failed f -> new PackLoadResult.Failed(f.failure());
            case LoadOutcome.Done d -> new PackLoadResult.Loaded(d.configuration());
        };
    }

    @Override
    public PackInspectionResult inspect(PackLoadRequest request) {
        // section 5.1.1: the ordered sanitized projection of this call's emitted events
        List<com.schmaloogium.engine.diag.EngineDiagnostic> events = new ArrayList<>();
        DiagnosticReporter reporter = diagnostic -> {
            events.add(diagnostic);
            request.diagnostics().report(diagnostic);
        };
        LoadOutcome outcome = runLoad(withDiagnostics(request, reporter));
        List<DecisionDiagnostic> projected = projectDiagnostics(events);
        return switch (outcome) {
            case LoadOutcome.Off o -> new PackInspectionResult.Off();
            case LoadOutcome.Failed f -> new PackInspectionResult.Failed(f.failure(),
                projected);
            case LoadOutcome.Done d -> new PackInspectionResult.Inspected(d.configuration(),
                decisionSnapshot(d.configuration(), projected), Optional.empty());
        };
    }

    private static PackLoadRequest withDiagnostics(PackLoadRequest request,
            DiagnosticReporter reporter) {
        return new PackLoadRequest(request.shaderpacksDirectory(), request.selection(),
            request.runtimeIdentity(), request.capabilities(), request.engineOptions(),
            request.companionOptionMacros(), request.rendererFeatures(),
            request.persistenceFiles(), request.internalPackSource(),
            request.internalOptions(), reporter);
    }

    private static List<DecisionDiagnostic> projectDiagnostics(
            List<com.schmaloogium.engine.diag.EngineDiagnostic> events) {
        return events.stream()
            .map(event -> new DecisionDiagnostic(event.messageKey(), event.severity(),
                event.channel(), Optional.empty()))
            .toList();
    }

    private sealed interface LoadOutcome {
        record Off() implements LoadOutcome {}

        record Failed(PackLoadFailure failure) implements LoadOutcome {}

        record Done(PackConfiguration configuration) implements LoadOutcome {}
    }

    private LoadOutcome runLoad(PackLoadRequest request) {
        if (request == null || request.selection() == null || request.runtimeIdentity() == null
                || request.capabilities() == null || request.engineOptions() == null
                || request.companionOptionMacros() == null) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INVALID_REQUEST,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.invalid_request",
                    "load request is incomplete")));
        }
        if (request.selection() instanceof PackSelection.Off) {
            return new LoadOutcome.Off();
        }
        if (request.selection() instanceof PackSelection.Internal) {
            return loadInternal(request);
        }
        PackSelection.Filesystem filesystem = (PackSelection.Filesystem) request.selection();
        return loadFilesystem(request, filesystem.candidate());
    }

    // ------------------------------------------------------- filesystem pipeline

    private LoadOutcome loadFilesystem(PackLoadRequest request, PackCandidateId candidate) {
        if (!(candidate instanceof PackCandidateIdToken token) || token.domain != domain) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INVALID_SELECTION,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.invalid_selection",
                    "candidate id was not issued by this front end")));
        }
        if (!(token.generation instanceof DiscoveryGenerationToken generation)) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INVALID_SELECTION,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.invalid_selection",
                    "candidate carries no discovery generation")));
        }
        DiscoveryIndex.Snapshot snapshot = domain.index(generation);
        if (snapshot == null) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INVALID_SELECTION,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.stale_generation",
                    "discovery generation is no longer retained")));
        }
        DiscoveryIndex.Entry entry = snapshot.entry(token.debugName);
        if (entry == null || !entry.valid() || entry.root().isEmpty()) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INPUT_UNREADABLE,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.candidate_unreadable",
                    token.debugName)));
        }
        PackInputLimits limits = new PackInputLimits(65536, 512L * 1024L * 1024L, 4096, 16);
        PackInputSnapshot input;
        try {
            input = entry.directory()
                ? PackInputSnapshot.ofDirectory(entry.hostPath().resolve("shaders"), limits,
                    entry.root().get().normalizedRoot())
                : PackInputSnapshot.ofArchive(entry.hostPath(), entry.root().get().normalizedRoot(),
                    limits);
        } catch (IOException e) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INPUT_UNREADABLE,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.snapshot_read",
                    e.toString())));
        } catch (RuntimeException bounds) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INPUT_LIMIT_EXCEEDED,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.snapshot_limit",
                    bounds.toString())));
        }
        return finishLoad(request, input.identity(), input.files, input, false,
            Optional.empty());
    }

    // ------------------------------------------------------- internal pipeline

    private LoadOutcome loadInternal(PackLoadRequest request) {
        if (request.internalPackSource() == null) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INTERNAL_SOURCE_INVALID,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.internal_missing",
                    "no internal pack source supplied")));
        }
        PackInputLimits limits = new PackInputLimits(65536, 512L * 1024L * 1024L, 4096, 16);
        Map<NormalizedPackPath, byte[]> files = new TreeMap<>(NormalizedPackPath.ORDER);
        InternalPackSnapshot snapshot;
        try {
            snapshot = request.internalPackSource().snapshot(limits);
        } catch (InternalPackReadException e) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INTERNAL_SOURCE_INVALID,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.internal_read",
                    e.toString())));
        }
        for (InternalPackEntry e : snapshot.entries()) {
            if (e instanceof InternalPackEntry.File f) {
                files.put(f.path(), f.bytes().copy());
            }
        }
        if (files.isEmpty()) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.INTERNAL_SOURCE_INVALID,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.internal_empty",
                    "internal pack source produced no files")));
        }
        return finishLoad(request, request.internalPackSource().identity(), files, null, true,
            request.internalOptions());
    }

    // ------------------------------------------------------- shared pipeline

    private LoadOutcome finishLoad(PackLoadRequest request, PackIdentity identity,
            Map<NormalizedPackPath, byte[]> files, PackInputSnapshot input, boolean internal,
            Optional<InternalOptionSnapshot> internalOptions) {
        DiagnosticReporter diags = request.diagnostics();

        // 1. source index: decode, roots, includes, executable programs
        List<EngineDiagnostic> indexDiags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, indexDiags);
        indexDiags.forEach(diags::report);
        if (index.roots().isEmpty()) {
            return new LoadOutcome.Failed(new PackLoadFailure(PackLoadFailureCode.STRUCTURALLY_UNUSABLE,
                diag(DiagnosticSeverity.ERROR, "schmaloogium.error.pack.no_roots",
                    "no executable program sources found")));
        }

        // 2. properties: preprocess shaders.properties and decode the model
        NormalizedPackPath propertiesPath = new NormalizedPackPath("shaders.properties");
        byte[] propertiesBytes = files.get(propertiesPath);
        List<ProfileScreenParser.Line> rawProperties = new ArrayList<>();
        ShaderPropertiesModel properties;
        if (propertiesBytes != null) {
            Map<String, String> macroEnv = MacroEnvironmentBuilder.standardMacros(request);
            String text = PropertiesPreprocessor.preprocess(
                new String(propertiesBytes, StandardCharsets.UTF_8), macroEnv);
            String[] lines = text.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].stripLeading();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                rawProperties.add(new ProfileScreenParser.Line(line.substring(0, eq).trim(),
                    line.substring(eq + 1), i + 1));
            }
            properties = ShaderPropertiesParser.parse(toLogicalEntries(rawProperties),
                propertiesPath);
        } else {
            properties = ShaderPropertiesParser.parse(List.of(), propertiesPath);
        }

        // 3. options: const/switch discovery over root documents, catalog + state
        Map<String, List<SourceAttribution>> switchOcc = new LinkedHashMap<>();
        Map<String, String> switchDefaults = new LinkedHashMap<>();
        Map<String, List<String>> switchValues = new LinkedHashMap<>();
        Map<String, String> tooltips = new LinkedHashMap<>();
        Map<String, List<SourceAttribution>> constOcc = new LinkedHashMap<>();
        Map<String, OptionCatalogBuilder.Raw.FindingValues> constValues = new LinkedHashMap<>();
        Set<String> confirmed = confirmedSwitchNames(rawProperties);
        Map<String, ConstScanner.Finding> consts = new LinkedHashMap<>();
        for (SourceKey root : index.roots()) {
            SourceDocument doc = index.rootDocument(root).orElse(null);
            if (doc == null) {
                continue;
            }
            SourceAttribution attribution = new SourceAttribution(doc.id().path(), 1, 1);
            String docText = String.join("\n", doc.originalLogicalLines());
            OptionCatalogBuilder.scanSwitches(docText, attribution, switchOcc, switchDefaults,
                switchValues, tooltips, confirmed);
            OptionCatalogBuilder.scanConsts(docText, attribution, constOcc, constValues);
            ConstScanner.scan(docText).forEach(consts::putIfAbsent);
        }
        List<OptionDefinition> definitions = OptionCatalogBuilder.build(
            new OptionCatalogBuilder.Raw(switchOcc, switchDefaults, switchValues,
                constOcc, constValues, tooltips));
        Object packKey = new Object();
        OptionCatalog catalog = OptionCatalogs.create(definitions, packKey, internal);
        OptionState state = catalog.defaultState();

        // 4. macro configuration: companions default closed, and the option-macro
        // projection is derived from them so the catalog invariant holds by construction
        CompanionOptionMacros companions = request.companionOptionMacros() != null
            ? request.companionOptionMacros() : new CompanionOptionMacros(false, false);
        List<MacroDefinition> companionDefines = new ArrayList<>();
        if (companions.normalMap()) {
            companionDefines.add(new MacroDefinition("MC_NORMAL_MAP", ""));
        }
        if (companions.specularMap()) {
            companionDefines.add(new MacroDefinition("MC_SPECULAR_MAP", ""));
        }
        MacroConfiguration macros = new MacroConfiguration(
            MacroIdentityPolicy.OPTION_1, List.of(), companionDefines, companions,
            List.of(), List.of(), Map.of(), List.of());

        // 5. profiles / screens / sliders from the retained stream
        List<ProfileModel> profiles = new ArrayList<>();
        Map<String, List<String>> profileTokens = new LinkedHashMap<>();
        for (ProfileScreenParser.Line line : rawProperties) {
            if (line.key().startsWith("profile.") && !line.key().endsWith(".comment")) {
                profileTokens.put(line.key().substring("profile.".length()),
                    List.of(line.value().trim().split("\\s+")));
            }
        }
        profileTokens.forEach((name, tokens) ->
            profiles.add(ProfileScreenParser.parseProfile(name, tokens)));
        ScreenModel mainScreen = new ScreenModel(OptionalInt.empty(),
            rawProperties.stream().filter(l -> l.key().equals("screen"))
                .flatMap(l -> ProfileScreenParser.parseScreen(l.value()).stream()).toList());
        Map<String, ScreenModel> namedScreens = new LinkedHashMap<>();
        for (ProfileScreenParser.Line line : rawProperties) {
            if (line.key().startsWith("screen.") && !line.key().endsWith(".columns")) {
                namedScreens.put(line.key().substring("screen.".length()),
                    new ScreenModel(OptionalInt.empty(),
                        ProfileScreenParser.parseScreen(line.value())));
            }
        }
        List<String> sliders = rawProperties.stream()
            .filter(l -> l.key().equals("sliders"))
            .findFirst()
            .map(l -> ProfileScreenParser.parseSliders(l.value()))
            .orElse(List.of());
        OptionConfiguration options = new OptionConfiguration(catalog, state, profiles,
            mainScreen, namedScreens, new SliderSet(sliders), Map.of());

        // 6. source catalog view + dimension configurations
        Map<String, String> macroEnv =
            MacroEnvironmentBuilder.shaderMacros(request, macros);
        SourceCatalogView catalogView = new SourceCatalogView(index, macroEnv,
            effectiveGlslVersion(request.capabilities().glslVersion()));
        Map<DimensionKey, DimensionConfiguration> dimensions = dimensionsOf(index);

        // 7. id mapping inputs
        IdMappingMacroEnvironment idEnv = new IdMappingMacroEnvironment(
            mcVersion(request.runtimeIdentity()),
            List.of(new MacroDefinition("MC_VERSION",
                Integer.toString(mcVersion(request.runtimeIdentity())))));
        IdMappingFileInput blocks = parseIdMapping(files, identity, "block.properties",
            MappingKind.BLOCK, idEnv, diags);
        IdMappingFileInput items = parseIdMapping(files, identity, "item.properties",
            MappingKind.ITEM, idEnv, diags);
        IdMappingFileInput entities = parseIdMapping(files, identity, "entity.properties",
            MappingKind.ENTITY, idEnv, diags);
        IdMappingFileInput layers = parseIdMapping(files, identity, "block.properties",
            MappingKind.LAYER, idEnv, diags);
        IdMappingInput idMappings = new IdMappingInput(PackFrontEnd.CURRENT_SCHEMA_VERSION,
            idEnv, blocks, items, entities, layers);

        // 8. resource requirements
        var requirements = ResourceRequirementsBuilder.build(properties, consts);

        // 9. compatibility + fingerprint + configuration
        String fingerprintValue = Sha256.hex((identity.contentHashes().toString()
            + "|" + properties.unknownProperties().size() + "|" + definitions.size()
            + "|" + PackFrontEnd.CURRENT_SCHEMA_VERSION).getBytes(StandardCharsets.UTF_8));
        PackConfiguration configuration = new PackConfiguration(PackFrontEnd.CURRENT_SCHEMA_VERSION,
            identity, CompatibilityStatus.COMPATIBLE, dimensions, catalogView,
            input != null ? input.assets() : emptyAssets(identity), options, macros,
            properties, requirements, idMappings, List.of(),
            new ConfigurationFingerprint(fingerprintValue));
        return new LoadOutcome.Done(configuration);
    }

    private static PackDecisionSnapshot decisionSnapshot(PackConfiguration configuration,
            List<DecisionDiagnostic> decisionDiagnostics) {
        // section 5.1.1: allowlisted source projection from the load's identity hashes
        List<DecisionSource> sources = new ArrayList<>();
        configuration.pack().contentHashes().entrySet().stream()
            .sorted(Map.Entry.comparingByKey(NormalizedPackPath.ORDER))
            .forEach(entry -> {
                int logicalLines = configuration.sources().source(
                    new com.schmaloogium.engine.preprocess.SourceId(entry.getKey()))
                    .map(document -> document.originalLogicalLines().size())
                    .orElse(0);
                sources.add(new DecisionSource(entry.getKey(), logicalLines, entry.getValue()));
            });
        // one evaluation pass feeds both the programStates section and its diagnostics
        List<com.schmaloogium.engine.diag.EngineDiagnostic> evaluation = new ArrayList<>();
        DiagnosticReporter evaluationReporter = evaluation::add;
        Map<String, DecisionValue> sections =
            DecisionValueProjector.sections(configuration, evaluationReporter);
        List<DecisionDiagnostic> projected = new ArrayList<>(decisionDiagnostics);
        evaluation.forEach(diagnostic -> projected.add(new DecisionDiagnostic(
            diagnostic.messageKey(), diagnostic.severity(), diagnostic.channel(),
            Optional.empty())));
        return new PackDecisionSnapshot(1, configuration.schemaVersion(),
            configuration.fingerprint(), sources, sections, projected);
    }

    private PackAssetSnapshot emptyAssets(PackIdentity identity) {
        return new PackAssetSnapshotImpl(identity, Map.of());
    }

    private static List<LogicalProperties.Entry> toLogicalEntries(
            List<ProfileScreenParser.Line> lines) {
        List<LogicalProperties.Entry> out = new ArrayList<>();
        for (ProfileScreenParser.Line line : lines) {
            out.add(new LogicalProperties.Entry(line.key(), line.value(), line.line()));
        }
        return out;
    }

    /** Leading integer of the GL_SHADING_LANGUAGE_VERSION string; 120 when unparseable. */
    private static int effectiveGlslVersion(String glslVersion) {
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("([0-9]+)").matcher(glslVersion == null ? "" : glslVersion);
        return m.find() ? Integer.parseInt(m.group(1)) : 120;
    }

    /** Screen/slider/profile mentions confirm switch candidates (documented App F.3). */
    private static Set<String> confirmedSwitchNames(List<ProfileScreenParser.Line> lines) {
        Set<String> confirmed = new LinkedHashSet<>();
        for (ProfileScreenParser.Line line : lines) {
            if (line.key().equals("screen") || line.key().startsWith("screen.")
                    || line.key().startsWith("profile.")) {
                for (String token : line.value().split("[\\s,]+")) {
                    String t = token.trim();
                    if (t.isEmpty() || t.equals("<empty>") || t.startsWith("profile.")
                            || t.startsWith("program.") || t.startsWith("!")) {
                        continue;
                    }
                    int colon = t.indexOf(':');
                    if (colon > 0) {
                        t = t.substring(0, colon);
                    }
                    if (t.startsWith("[")) {
                        t = t.substring(1, t.length() - 1);
                    }
                    if (t.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                        confirmed.add(t);
                    }
                }
            }
        }
        return confirmed;
    }

    private IdMappingFileInput parseIdMapping(Map<NormalizedPackPath, byte[]> files,
            PackIdentity identity, String path, MappingKind kind,
            IdMappingMacroEnvironment env, DiagnosticReporter diags) {
        byte[] bytes = files.get(new NormalizedPackPath(path));
        IdMappingParseRequest parseRequest = new IdMappingParseRequest(kind,
            bytes == null ? Optional.empty() : Optional.of(ImmutableBytesImpl.of(bytes)),
            new com.schmaloogium.engine.config.PackMappingOrigin(identity,
                new NormalizedPackPath(path)), env, diags);
        return idMappingParser.parse(parseRequest);
    }

    private static Map<DimensionKey, DimensionConfiguration> dimensionsOf(SourceIndex index) {
        Map<DimensionKey, List<SourceKey>> byDimension = new TreeMap<>(DimensionKey::compareTo);
        for (SourceKey key : index.roots()) {
            byDimension.computeIfAbsent(key.dimension(), k -> new ArrayList<>()).add(key);
        }
        Map<DimensionKey, DimensionConfiguration> out = new TreeMap<>(DimensionKey::compareTo);
        byDimension.forEach((dimension, roots) -> out.put(dimension,
            new DimensionConfiguration(dimension,
                dimension.equals(DimensionKey.BASE) ? DimensionMode.BASE : DimensionMode.OVERRIDE,
                dimension.equals(DimensionKey.BASE) ? Optional.empty()
                    : Optional.of(DimensionKey.BASE),
                List.copyOf(roots))));
        if (out.isEmpty()) {
            out.put(DimensionKey.BASE, new DimensionConfiguration(DimensionKey.BASE,
                DimensionMode.BASE, Optional.empty(), List.of()));
        }
        return out;
    }

    private static int mcVersion(RuntimeIdentityData identity) {
        return identity.mcMajor() * 10000 + identity.mcMinor() * 100 + identity.mcPatch();
    }

    private static EngineDiagnostic diag(DiagnosticSeverity severity, String key, String detail) {
        return new EngineDiagnostic(severity, UserChannel.LOG_ONLY, key,
            List.of(detail), "", "schmaloogium.pack");
    }

    /** Read-only view over the built index implementing the public catalog. */
    private static final class SourceCatalogView implements SourceCatalog {
        private final SourceIndex index;
        private final Map<String, String> macros;
        private final int effectiveVersion;

        SourceCatalogView(SourceIndex index, Map<String, String> macros, int effectiveVersion) {
            this.index = index;
            this.macros = macros;
            this.effectiveVersion = effectiveVersion;
        }

        @Override
        public List<SourceDocument> sources() {
            return index.sources();
        }

        @Override
        public List<SourceKey> roots() {
            return index.roots();
        }

        @Override
        public Set<com.schmaloogium.engine.config.ProgramKey> executablePrograms() {
            return index.executablePrograms();
        }

        @Override
        public Optional<SourceDocument> source(SourceId id) {
            return index.document(id);
        }

        @Override
        public List<IncludeEdge> includeEdges() {
            return index.includeEdges();
        }

        @Override
        public com.schmaloogium.engine.preprocess.SourceMaterializer materializer() {
            return new com.schmaloogium.engine.preprocess.MaterializerImpl(
                index, macros, effectiveVersion);
        }
    }
}
