// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.AlphaTestSpec;
import com.schmaloogium.engine.config.BlendAlphaFactors;
import com.schmaloogium.engine.config.BlendSpec;
import com.schmaloogium.engine.config.BooleanOptionValue;
import com.schmaloogium.engine.config.BufferMinima;
import com.schmaloogium.engine.config.CenterDepthRequirements;
import com.schmaloogium.engine.config.CloudMode;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.config.ColorAttachmentKey;
import com.schmaloogium.engine.config.ColorAttachmentRequirement;
import com.schmaloogium.engine.config.DrawRouting;
import com.schmaloogium.engine.config.DrawSlot;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.EvaluatedProgramState;
import com.schmaloogium.engine.config.EvaluatedProgramStates;
import com.schmaloogium.engine.config.CustomExpressionDecl;
import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdRule;
import com.schmaloogium.engine.config.LangDecorations;
import com.schmaloogium.engine.config.LayerRule;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.MacroDefinition;
import com.schmaloogium.engine.config.MacroIdentityPolicy;
import com.schmaloogium.engine.config.MacroOverride;
import com.schmaloogium.engine.config.MappingRule;
import com.schmaloogium.engine.config.MetadataConstraint;
import com.schmaloogium.engine.config.MinimumEditionRule;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionDefinition;
import com.schmaloogium.engine.config.OptionValue;
import com.schmaloogium.engine.config.ProfileConstraint;
import com.schmaloogium.engine.config.ProfileModel;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.config.ProgramDisable;
import com.schmaloogium.engine.config.ProgramKey;
import com.schmaloogium.engine.config.ProgramRequirements;
import com.schmaloogium.engine.config.ProgramState;
import com.schmaloogium.engine.config.ProgramStateEvaluationResult;
import com.schmaloogium.engine.config.ProgramStateEvaluator;
import com.schmaloogium.engine.config.PropertyPredicate;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ScreenModel;
import com.schmaloogium.engine.config.ScreenOptionEntry;
import com.schmaloogium.engine.config.ScreenSubscreenEntry;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.config.ShadowRequirements;
import com.schmaloogium.engine.config.SliderSet;
import com.schmaloogium.engine.config.SourceAttribution;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.config.TexturePropertyDecl;
import com.schmaloogium.engine.config.TriState;
import com.schmaloogium.engine.config.UnknownProperty;
import com.schmaloogium.engine.config.ValueDecorationKey;
import com.schmaloogium.engine.config.ViewportScale;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.preprocess.LegacyGeometryConfig;
import com.schmaloogium.engine.preprocess.LegacyGeometrySite;
import com.schmaloogium.engine.preprocess.SourceKey;
import com.schmaloogium.engine.preprocess.SourceSpan;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;

/**
 * Hand-written section 5.1.1 source-free decision projection: one typed recursive
 * allowlist per published configuration graph, no reflection and no toString.
 * String leaves use TextHash except the documented schema-owned allowlist
 * (enum tokens, validated identifiers, canonical paths).
 */
final class DecisionValueProjector {

    /** Local lowercase-hex SHA-256 helper for string leaves. */
    private static final class Sha256Hex {
        private Sha256Hex() {
        }

        static String of(byte[] bytes) {
            try {
                byte[] digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(bytes);
                StringBuilder out = new StringBuilder(digest.length * 2);
                for (byte b : digest) {
                    out.append(String.format(java.util.Locale.ROOT, "%02x", b));
                }
                return out.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private DecisionValueProjector() {
    }

    static DecisionValue textHash(String value) {
        return new DecisionValue.TextHash(
            Sha256Hex.of(value.getBytes(StandardCharsets.UTF_8)));
    }

    private static DecisionValue token(String value) {
        return new DecisionValue.Token(value);
    }

    private static DecisionValue integer(long value) {
        return new DecisionValue.IntegerValue(value);
    }

    private static DecisionValue bool(boolean value) {
        return new DecisionValue.Bool(value);
    }

    private static DecisionValue absent() {
        return new DecisionValue.Absent();
    }

    private static DecisionValue present(Optional<?> value, DecisionValue projected) {
        return value == null || value.isEmpty() ? absent() : projected;
    }

    private static DecisionValue presentInt(OptionalInt value) {
        return value == null || value.isEmpty() ? absent() : integer(value.getAsInt());
    }

    private static DecisionValue fields(Object... nameValuePairs) {
        Map<String, DecisionValue> out = new LinkedHashMap<>();
        for (int i = 0; i < nameValuePairs.length; i += 2) {
            out.put((String) nameValuePairs[i], (DecisionValue) nameValuePairs[i + 1]);
        }
        return new DecisionValue.Fields(out);
    }

    private static DecisionValue sequence(List<DecisionValue> values) {
        return new DecisionValue.Sequence(List.copyOf(values));
    }

    private static DecisionValue floatValue(float value) {
        return new DecisionValue.FloatBits(Float.floatToRawIntBits(value));
    }

    private static DecisionValue optionalFloat(Optional<Float> value) {
        return value == null || value.isEmpty() ? absent() : floatValue(value.get());
    }

    /** The nine section keys in the documented order. */
    static Map<String, DecisionValue> sections(PackConfiguration configuration,
            DiagnosticReporter evaluationReporter) {
        Map<String, DecisionValue> sections = new LinkedHashMap<>();
        sections.put("pack", pack(configuration));
        sections.put("dimensions", dimensions(configuration));
        sections.put("options", options(configuration.options()));
        sections.put("properties", properties(configuration.properties()));
        sections.put("programStates", programStates(configuration, evaluationReporter));
        sections.put("resources", resources(configuration.resources()));
        sections.put("macros", macros(configuration.macros()));
        sections.put("idMappings", idMappings(configuration.idMappings()));
        sections.put("assets", assets(configuration.assets()));
        return sections;
    }

    private static DecisionValue pack(PackConfiguration configuration) {
        PackIdentity identity = configuration.pack();
        List<DecisionValue> hashes = new ArrayList<>();
        new TreeMap<NormalizedPackPath, String>(NormalizedPackPath.ORDER)
            .putAll(identity.contentHashes());
        identity.contentHashes().entrySet().stream()
            .sorted(Map.Entry.comparingByKey(NormalizedPackPath.ORDER))
            .forEach(entry -> hashes.add(fields(
                "key", token(entry.getKey().canonicalString()),
                "value", textHash(entry.getValue()))));
        return fields("schemaVersion", integer(configuration.schemaVersion()),
            "compatibility", token(configuration.compatibility().name()),
            "identity", fields(
                "selectedRoot", token(identity.selectedRoot().canonicalString()),
                "contentHashes", sequence(hashes)));
    }

    private static DecisionValue dimensions(PackConfiguration configuration) {
        List<DecisionValue> out = new ArrayList<>();
        configuration.dimensions().forEach((key, dimension) -> out.add(fields(
            "key", dimensionKey(key),
            "value", fields("$type", token("DimensionConfiguration"),
                "key", dimensionKey(key),
                "mode", token(dimension.mode().name()),
                "baseDimension", dimension.baseDimension().isPresent()
                    ? dimensionKey(dimension.baseDimension().get())
                    : absent(),
                "sourceRoots", sequence(dimension.sourceRoots().stream()
                    .map(DecisionValueProjector::sourceKey).toList())))));
        return sequence(out);
    }

    private static DecisionValue dimensionKey(DimensionKey key) {
        return fields("$type", token("DimensionKey"),
            "legacyId", presentInt(key.legacyId()));
    }

    private static DecisionValue sourceKey(SourceKey key) {
        return fields("$type", token("SourceKey"),
            "dimension", dimensionKey(key.dimension()),
            "programName", token(key.programName()),
            "stage", token(key.stage().name()),
            "source", token(key.source().path().canonicalString()));
    }

    private static DecisionValue optionValue(OptionValue value) {
        if (value instanceof BooleanOptionValue bool) {
            return fields("$type", token("BooleanOptionValue"), "value", bool(bool.value()));
        }
        if (value instanceof TextOptionValue text) {
            return fields("$type", token("TextOptionValue"), "value", textHash(text.value()));
        }
        throw new IllegalStateException("unprojected OptionValue variant " + value.getClass());
    }

    private static DecisionValue options(OptionConfiguration options) {
        List<DecisionValue> definitions = new ArrayList<>();
        for (OptionDefinition definition : options.catalog().definitions()) {
            definitions.add(fields("$type", token("OptionDefinition"),
                "name", token(definition.name()),
                "kind", token(definition.kind().name()),
                "defaultValue", optionValue(definition.defaultValue()),
                "allowedValues", sequence(definition.allowedValues().stream()
                    .map(DecisionValueProjector::optionValue).toList()),
                "availability", token(definition.availability().name()),
                // evaluated lazily: an absent tooltip must not reach orElseThrow (real packs
                // declare options without tooltips; inspection threw for SEUS/projectLUMA)
                "tooltip", present(definition.tooltip(),
                    definition.tooltip().map(DecisionValueProjector::textHash).orElse(null)),
                "occurrences", sequence(definition.occurrences().stream()
                    .map(DecisionValueProjector::attribution).toList())));
        }
        List<DecisionValue> values = new ArrayList<>();
        options.state().values().forEach((name, value) -> values.add(fields(
            "key", token(name), "value", optionValue(value))));
        List<DecisionValue> profiles = new ArrayList<>();
        for (ProfileModel profile : options.profiles()) {
            profiles.add(fields("$type", token("ProfileModel"),
                "name", token(profile.name().value()),
                "constraints", sequence(profile.constraints().stream()
                    .map(DecisionValueProjector::profileConstraint)
                    .toList()),
                "disabledPrograms", sequence(profile.disabledPrograms().stream()
                    .map(DecisionValueProjector::programDisable).toList())));
        }
        List<DecisionValue> locales = new ArrayList<>();
        options.localizedDecorations().forEach((locale, decorations) -> locales.add(fields(
            "key", textHash(locale), "value", langDecorations(decorations))));
        List<DecisionValue> namedScreens = new ArrayList<>();
        options.namedScreens().forEach((name, screen) -> namedScreens.add(fields(
            "key", token(name), "value", screen(screen))));
        return fields(
            "catalog", fields("definitions", sequence(definitions)),
            "state", fields("values", sequence(values)),
            "profiles", sequence(profiles),
            "mainScreen", screen(options.mainScreen()),
            "namedScreens", sequence(namedScreens),
            "sliders", fields("optionNames", sequence(options.sliders().optionNames().stream()
                .map(DecisionValueProjector::token).toList())),
            "localizedDecorations", sequence(locales));
    }

    private static DecisionValue profileConstraint(ProfileConstraint constraint) {
        return fields("$type", token("ProfileConstraint"),
            "optionName", token(constraint.optionName()),
            "requiredValue", optionValue(constraint.requiredValue()));
    }

    private static DecisionValue langDecorations(LangDecorations decorations) {
        return fields("$type", token("LangDecorations"),
            "optionLabels", stringMap(decorations.optionLabels()),
            "optionComments", stringMap(decorations.optionComments()),
            "valueLabels", valueDecorationMap(decorations.valueLabels()),
            "prefixes", stringMap(decorations.prefixes()),
            "suffixes", stringMap(decorations.suffixes()),
            "profileLabels", profileMap(decorations.profileLabels()),
            "profileComments", profileMap(decorations.profileComments()),
            "screenLabels", stringMap(decorations.screenLabels()),
            "screenComments", stringMap(decorations.screenComments()));
    }

    private static DecisionValue stringMap(Map<String, String> map) {
        List<DecisionValue> entries = new ArrayList<>();
        map.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getKey, EngineOptionData::compareUnsignedUtf8))
            .forEach(entry -> entries.add(fields(
                "key", textHash(entry.getKey()), "value", textHash(entry.getValue()))));
        return sequence(entries);
    }

    private static DecisionValue valueDecorationMap(Map<ValueDecorationKey, String> map) {
        List<DecisionValue> entries = new ArrayList<>();
        map.entrySet().stream()
            .sorted(Comparator
                .comparing((Map.Entry<ValueDecorationKey, String> e) -> e.getKey().optionName(),
                    EngineOptionData::compareUnsignedUtf8)
                .thenComparing(e -> e.getKey().value(),
                    EngineOptionData::compareUnsignedUtf8))
            .forEach(entry -> entries.add(fields(
                "key", fields("optionName", textHash(entry.getKey().optionName()),
                    "value", textHash(entry.getKey().value())),
                "value", textHash(entry.getValue()))));
        return sequence(entries);
    }

    private static DecisionValue profileMap(Map<ProfileName, String> map) {
        List<DecisionValue> entries = new ArrayList<>();
        map.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().value(),
                EngineOptionData::compareUnsignedUtf8))
            .forEach(entry -> entries.add(fields(
                "key", textHash(entry.getKey().value()),
                "value", textHash(entry.getValue()))));
        return sequence(entries);
    }

    private static DecisionValue programDisable(ProgramDisable disable) {
        return fields("$type", token("ProgramDisable"),
            "dimension", disable.dimension().isPresent()
                ? dimensionKey(disable.dimension().get())
                : absent(),
            "programName", token(disable.programName()));
    }

    private static DecisionValue screen(ScreenModel screen) {
        return fields("$type", token("ScreenModel"),
            "explicitColumns", presentInt(screen.explicitColumns()),
            "entries", sequence(screen.entries().stream()
                .map(entry -> {
                    String typeName = entry.getClass().getSimpleName();
                    if (entry instanceof ScreenOptionEntry option) {
                        return fields("$type", token(typeName),
                            "optionName", token(option.optionName()));
                    }
                    if (entry instanceof ScreenSubscreenEntry sub) {
                        return fields("$type", token(typeName),
                            "screenName", token(sub.screenName()));
                    }
                    return fields("$type", token(typeName));
                })
                .toList()));
    }

    private static DecisionValue properties(ShaderPropertiesModel properties) {
        EngineFlags flags = properties.engineFlags();
        Map<String, DecisionValue> flagFields = new LinkedHashMap<>();
        flagFields.put("clouds", token(flags.clouds().name()));
        putTriState(flagFields, "oldHandLight", flags.oldHandLight());
        putTriState(flagFields, "dynamicHandLight", flags.dynamicHandLight());
        putTriState(flagFields, "oldLighting", flags.oldLighting());
        putTriState(flagFields, "shadowTranslucent", flags.shadowTranslucent());
        putTriState(flagFields, "underwaterOverlay", flags.underwaterOverlay());
        putTriState(flagFields, "sun", flags.sun());
        putTriState(flagFields, "moon", flags.moon());
        putTriState(flagFields, "vignette", flags.vignette());
        putTriState(flagFields, "backFaceSolid", flags.backFaceSolid());
        putTriState(flagFields, "backFaceCutout", flags.backFaceCutout());
        putTriState(flagFields, "backFaceCutoutMipped", flags.backFaceCutoutMipped());
        putTriState(flagFields, "backFaceTranslucent", flags.backFaceTranslucent());
        putTriState(flagFields, "rainDepth", flags.rainDepth());
        putTriState(flagFields, "beaconBeamDepth", flags.beaconBeamDepth());
        putTriState(flagFields, "separateAo", flags.separateAo());
        putTriState(flagFields, "frustumCulling", flags.frustumCulling());
        List<DecisionValue> editions = new ArrayList<>();
        for (MinimumEditionRule rule : properties.minimumEditionRules()) {
            editions.add(fields("$type", token("MinimumEditionRule"),
                "minecraftVersion", textHash(rule.minecraftVersion()),
                "minimumEdition", token(rule.minimumEdition())));
        }
        List<DecisionValue> declarations = new ArrayList<>();
        for (TexturePropertyDecl decl : properties.textureDeclarations()) {
            declarations.add(fields("$type", token("TexturePropertyDecl"),
                "key", textHash(decl.key()),
                "value", textHash(decl.value()),
                "sourceOrdinal", integer(decl.sourceOrdinal()),
                "attribution", attribution(decl.attribution()),
                "disposition", token(decl.disposition().name())));
        }
        List<DecisionValue> expressions = new ArrayList<>();
        for (CustomExpressionDecl decl : properties.customExpressions()) {
            expressions.add(fields("$type", token("CustomExpressionDecl"),
                "kind", token(decl.kind().name()),
                "type", token(decl.type().name()),
                "name", token(decl.name()),
                "rawExpression", textHash(decl.rawExpression()),
                "sourceOrdinal", integer(decl.sourceOrdinal()),
                "attribution", attribution(decl.attribution())));
        }
        List<DecisionValue> rawPrograms = new ArrayList<>();
        properties.programStates().programs().forEach((key, state) -> rawPrograms.add(fields(
            "key", programKey(key), "value", programState(state))));
        List<DecisionValue> unknown = new ArrayList<>();
        for (UnknownProperty property : properties.unknownProperties()) {
            unknown.add(fields("$type", token("UnknownProperty"),
                "key", textHash(property.key()),
                "value", textHash(property.value()),
                "attribution", attribution(property.attribution())));
        }
        return fields("engineFlags", new DecisionValue.Fields(flagFields),
            "minimumEditionRules", sequence(editions),
            "textures", sequence(List.of()),
            "textureDeclarations", sequence(declarations),
            "noise", absent(),
            "customExpressions", sequence(expressions),
            "programStates", fields("programs", sequence(rawPrograms)),
            "unknownProperties", sequence(unknown));
    }

    private static void putTriState(Map<String, DecisionValue> out, String name, TriState state) {
        out.put(name, token(state.name()));
    }

    private static DecisionValue programState(ProgramState state) {
        List<DecisionValue> flips = new ArrayList<>();
        state.flips().entrySet().stream()
            .sorted(Comparator.comparingInt(e -> e.getKey().attachment().colortexIndex()))
            .forEach(e -> flips.add(fields(
                "key", fields("attachment",
                    fields("colortexIndex", integer(e.getKey().attachment().colortexIndex()))),
                "value", token(e.getValue().name()))));
        return fields("$type", token("ProgramState"),
            "alphaTest", alphaTest(state.alphaTest()),
            "blend", blend(state.blend()),
            "scale", scale(state.scale()),
            "flips", sequence(flips),
            "enabledExpression", present(state.enabledExpression(),
                state.enabledExpression().isPresent()
                    ? textHash(new String(
                        state.enabledExpression().get().capturedExpressionBytes(),
                        StandardCharsets.UTF_8))
                    : absent()));
    }

    private static DecisionValue alphaTest(Optional<AlphaTestSpec> spec) {
        if (spec.isEmpty()) {
            return absent();
        }
        if (spec.get() instanceof AlphaTestSpec.Off) {
            return fields("$type", token("Off"));
        }
        AlphaTestSpec.Enabled enabled = (AlphaTestSpec.Enabled) spec.get();
        return fields("$type", token("Enabled"),
            "function", token(enabled.function().name()),
            "reference", floatValue(enabled.reference()));
    }

    private static DecisionValue blend(Optional<BlendSpec> spec) {
        if (spec.isEmpty()) {
            return absent();
        }
        if (spec.get() instanceof BlendSpec.Off) {
            return fields("$type", token("Off"));
        }
        BlendSpec.Enabled enabled = (BlendSpec.Enabled) spec.get();
        return fields("$type", token("Enabled"),
            "sourceColor", token(enabled.sourceColor().name()),
            "destinationColor", token(enabled.destinationColor().name()),
            "alpha", enabled.alpha().isPresent() ? blendAlpha(enabled.alpha().get()) : absent());
    }

    private static DecisionValue blendAlpha(BlendAlphaFactors alpha) {
        return fields("$type", token("BlendAlphaFactors"),
            "source", token(alpha.source().name()),
            "destination", token(alpha.destination().name()));
    }

    private static DecisionValue scale(Optional<ViewportScale> scale) {
        return scale.isEmpty() ? absent() : fields("$type", token("ViewportScale"),
            "scale", floatValue(scale.get().scale()),
            "offsetX", floatValue(scale.get().offsetX()),
            "offsetY", floatValue(scale.get().offsetY()));
    }

    private static DecisionValue attribution(SourceAttribution attribution) {
        return fields("$type", token("SourceAttribution"),
            "source", token(attribution.source().canonicalString()),
            "physicalLine", integer(attribution.physicalLine()),
            "physicalColumn", integer(attribution.physicalColumn()));
    }

    private static DecisionValue programKey(ProgramKey key) {
        return fields("$type", token("ProgramKey"),
            "dimension", dimensionKey(key.dimension()),
            "programName", token(key.programName()));
    }

    private static DecisionValue programStates(PackConfiguration configuration,
            DiagnosticReporter reporter) {
        ProgramStateEvaluationResult result = ProgramStateEvaluator.evaluate(
            configuration, configuration.options().state(), Optional.empty(), reporter);
        if (result instanceof ProgramStateEvaluationResult.InvalidState invalid) {
            return fields("$type", token("InvalidState"),
                "failure", token(invalid.failure().name()));
        }
        EvaluatedProgramStates states =
            ((ProgramStateEvaluationResult.Evaluated) result).states();
        List<DecisionValue> programs = new ArrayList<>();
        for (EvaluatedProgramState program : states.programs()) {
            programs.add(fields("$type", token("EvaluatedProgramState"),
                "key", programKey(program.key()),
                "alphaTest", alphaTest(program.alphaTest()),
                "blend", blend(program.blend()),
                "scale", scale(program.scale()),
                "propertyEnabled", bool(program.propertyEnabled()),
                "profileDisabled", bool(program.profileDisabled()),
                "finalEnabled", bool(program.finalEnabled())));
        }
        List<DecisionValue> flips = new ArrayList<>();
        states.explicitFlips().forEach((key, bufferFlips) -> {
            List<DecisionValue> entries = new ArrayList<>();
            bufferFlips.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().attachment().colortexIndex()))
                .forEach(e -> entries.add(fields(
                    "key", fields("attachment",
                        fields("colortexIndex", integer(e.getKey().attachment().colortexIndex()))),
                    "value", token(e.getValue().name()))));
            flips.add(fields("key", programKey(key), "value", sequence(entries)));
        });
        return fields("$type", token("Evaluated"),
            "programs", sequence(programs), "explicitFlips", sequence(flips));
    }

    private static DecisionValue resources(ResourceRequirements requirements) {
        BufferMinima minima = requirements.minima();
        List<DecisionValue> colorAttachments = new ArrayList<>();
        requirements.colorAttachments().forEach((key, requirement) -> colorAttachments.add(
            fields("key", fields("colortexIndex", integer(key.colortexIndex())),
                "value", colorAttachment(requirement))));
        ShadowRequirements shadow = requirements.shadow();
        List<DecisionValue> programs = new ArrayList<>();
        requirements.programs().forEach((key, program) -> programs.add(fields(
            "key", fields("dimension", dimensionKey(key.dimension()),
                "programName", token(key.programName())),
            "value", programRequirements(program))));
        return fields("minima", fields("$type", token("BufferMinima"),
                "colorBuffers", integer(minima.colorBuffers()),
                "mainDepthTextures", integer(minima.mainDepthTextures()),
                "shadowDepthBuffers", integer(minima.shadowDepthBuffers()),
                "shadowColorBuffers", integer(minima.shadowColorBuffers())),
            "colorAttachments", sequence(colorAttachments),
            "shadow", fields("$type", token("ShadowRequirements"),
                "resolution", integer(shadow.resolution()),
                "fov", optionalFloat(shadow.fov()),
                "distance", floatValue(shadow.distance()),
                "distanceRenderMultiplier", floatValue(shadow.distanceRenderMultiplier()),
                "intervalSize", floatValue(shadow.intervalSize()),
                "mipmapped", sequence(shadow.mipmapped().stream()
                    .map(k -> token(k.name())).toList()),
                "nearest", sequence(shadow.nearest().stream()
                    .map(k -> token(k.name())).toList()),
                "hardwarePcf", sequence(shadow.hardwarePcf().stream()
                    .map(k -> token(k.name())).toList())),
            "centerDepth", fields("$type", token("CenterDepthRequirements"),
                "required", bool(((CenterDepthRequirements) requirements.centerDepth()).required())),
            "programs", sequence(programs),
            "smoothing", fields("$type", token("SmoothingConstants"),
                "wetnessHalfLifeTicks",
                    floatValue(requirements.smoothing().wetnessHalfLifeTicks()),
                "drynessHalfLifeTicks",
                    floatValue(requirements.smoothing().drynessHalfLifeTicks()),
                "eyeBrightnessHalfLifeTicks",
                    floatValue(requirements.smoothing().eyeBrightnessHalfLifeTicks()),
                "centerDepthHalfLifeTicks",
                    floatValue(requirements.smoothing().centerDepthHalfLifeTicks())),
            "world", fields("$type", token("WorldRenderConstants"),
                "sunPathRotation", floatValue(requirements.world().sunPathRotation()),
                "ambientOcclusionLevel",
                    floatValue(requirements.world().ambientOcclusionLevel())),
            "noise", fields("$type", token("NoiseRequirement"),
                "enabled", bool(requirements.noise().enabled()),
                "resolution", integer(requirements.noise().resolution())));
    }

    private static DecisionValue colorAttachment(ColorAttachmentRequirement requirement) {
        ColorAttachmentFormat format = requirement.format();
        DecisionValue formatValue;
        if (format instanceof ColorAttachmentFormat.Explicit explicit) {
            formatValue = fields("$type", token("Explicit"),
                "format", token(explicit.format().name()));
        } else {
            formatValue = fields("$type", token("DefaultRgba"));
        }
        return fields("$type", token("ColorAttachmentRequirement"),
            "format", formatValue,
            "clear", bool(requirement.clear()),
            "clearColorOverride", requirement.clearColorOverride().isPresent()
                ? vec4(requirement.clearColorOverride().get())
                : absent());
    }

    private static DecisionValue vec4(com.schmaloogium.engine.config.Vec4f color) {
        return fields("$type", token("Vec4f"),
            "red", floatValue(color.red()),
            "green", floatValue(color.green()),
            "blue", floatValue(color.blue()),
            "alpha", floatValue(color.alpha()));
    }

    private static DecisionValue programRequirements(ProgramRequirements program) {
        DrawRouting routing = program.routing();
        DecisionValue routingValue;
        if (routing instanceof DrawRouting.AllUsed) {
            routingValue = fields("$type", token("AllUsed"));
        } else {
            List<DecisionValue> slots = new ArrayList<>();
            for (DrawSlot slot : ((DrawRouting.Explicit) routing).slots()) {
                slots.add(slot instanceof DrawSlot.Attachment attachment
                    ? fields("$type", token("Attachment"),
                        "target", dimensionless(attachment.target()))
                    : fields("$type", token("None")));
            }
            routingValue = fields("$type", token("Explicit"), "slots", sequence(slots));
        }
        DecisionValue legacyGeometry = program.legacyGeometry().isPresent()
            ? legacyGeometry(program.legacyGeometry().get())
            : absent();
        return fields("$type", token("ProgramRequirements"),
            "routing", routingValue,
            "mipmappedAfterPass", sequence(program.mipmappedAfterPass().stream()
                .map(DecisionValueProjector::dimensionless).toList()),
            "vertices", fields("$type", token("VertexRequirements"),
                "attributes", sequence(program.vertices().attributes().stream()
                    .map(a -> token(a.name())).toList())),
            "instanceCount", integer(program.instanceCount()),
            "legacyGeometry", legacyGeometry);
    }

    private static DecisionValue dimensionless(ColorAttachmentKey key) {
        return fields("$type", token("ColorAttachmentKey"),
            "colortexIndex", integer(key.colortexIndex()));
    }

    private static DecisionValue legacyGeometry(LegacyGeometryConfig config) {
        return fields("$type", token("LegacyGeometryConfig"),
            "root", sourceKey(config.root()),
            "extension", token(config.extension().name()),
            "maxVertices", integer(config.maxVertices()),
            "site", legacySite(config.site()));
    }

    private static DecisionValue legacySite(LegacyGeometrySite site) {
        return fields("$type", token("LegacyGeometrySite"),
            "root", sourceKey(site.root()),
            "extensionSpan", span(site.extensionSpan()),
            "maxVerticesSpan", span(site.maxVerticesSpan()));
    }

    private static DecisionValue span(SourceSpan span) {
        return fields("$type", token("SourceSpan"),
            "source", token(span.source().path().canonicalString()),
            "startOffset", integer(span.startOffset()),
            "endOffset", integer(span.endOffset()),
            "startLine", integer(span.startLine()),
            "startColumn", integer(span.startColumn()));
    }

    private static DecisionValue macros(MacroConfiguration macros) {
        return fields("identityPolicy", token(macros.identityPolicy().name()),
            "baseCompatibilityMacros", macroList(macros.baseCompatibilityMacros()),
            "optionMacros", macroList(macros.optionMacros()),
            "companionOptionMacros", fields("$type", token("CompanionOptionMacros"),
                "normalMap", bool(macros.companionOptionMacros().normalMap()),
                "specularMap", bool(macros.companionOptionMacros().specularMap())),
            "capabilityFeatureMacros", macroList(macros.capabilityFeatureMacros()),
            "engineIdentityMacros", macroList(macros.engineIdentityMacros()),
            "perPackOverrides", sequence(macros.perPackOverrides().entrySet().stream()
                .map(e -> fields("key", token(e.getKey()), "value", macroOverride(e.getValue())))
                .toList()),
            "reservedContributors", sequence(macros.reservedContributors().stream()
                .map(DecisionValueProjector::token).toList()));
    }

    private static DecisionValue macroList(List<MacroDefinition> definitions) {
        return sequence(definitions.stream()
            .map(d -> fields("$type", token("MacroDefinition"),
                "name", token(d.name()), "replacement", textHash(d.replacement())))
            .toList());
    }

    private static DecisionValue macroOverride(MacroOverride override) {
        return fields("$type", token("MacroOverride"),
            "action", token(override.action().name()),
            "replacement", present(override.replacement(),
                textHash(override.replacement().orElseThrow())));
    }

    private static DecisionValue idMappings(IdMappingInput input) {
        return fields("schemaVersion", integer(input.schemaVersion()),
            "parserEnvironment", fields("$type", token("IdMappingMacroEnvironment"),
                "mcVersion", integer(input.parserEnvironment().mcVersion()),
                "standardMacros", macroList(input.parserEnvironment().standardMacros())),
            "blocks", idFile(input.blocks()),
            "items", idFile(input.items()),
            "entities", idFile(input.entities()),
            "layers", idFile(input.layers()));
    }

    private static DecisionValue idFile(IdMappingFileInput file) {
        return fields("$type", token("IdMappingFileInput"),
            "kind", token(file.kind().name()),
            "state", token(file.state().name()),
            "ordinaryRules", sequence(file.ordinaryRules().stream()
                .map(DecisionValueProjector::mappingRule).toList()),
            "forced11300Rules", sequence(file.forced11300Rules().stream()
                .map(DecisionValueProjector::mappingRule).toList()),
            "fingerprint", textHash(file.fingerprint().value()));
    }

    private static DecisionValue mappingRule(MappingRule rule) {
        if (rule instanceof LayerRule layer) {
            return commonRule("LayerRule", layer.selectorKind().name(), layer.selectorToken(),
                layer.legacyMetadata(), layer.propertyPredicates(), layer.era().name(),
                layer.origin(), layer.sourceLine(), layer.selectorOrdinal(),
                fields("$type", token("RequestedRenderLayer"),
                    "name", token(layer.layer().name())));
        }
        IdRule id = (IdRule) rule;
        return commonRule("IdRule", id.selectorKind().name(), id.selectorToken(),
            id.legacyMetadata(), id.propertyPredicates(), id.era().name(), id.origin(),
            id.sourceLine(), id.selectorOrdinal(), null);
    }

    private static DecisionValue commonRule(String typeName, String selectorKind,
            String selectorToken, Optional<MetadataConstraint> legacyMetadata,
            List<PropertyPredicate> predicates, String era,
            com.schmaloogium.engine.config.MappingOrigin origin, int sourceLine,
            int selectorOrdinal, DecisionValue extra) {
        DecisionValue metadata = legacyMetadata.isEmpty() ? absent()
            : fields("$type", token("MetadataConstraint"),
                "alternatives", sequence(legacyMetadata.get().alternatives().stream()
                    .map(r -> fields("$type", token("IntegerRange"),
                        "lowerInclusive", integer(r.lowerInclusive()),
                        "upperInclusive", integer(r.upperInclusive())))
                    .toList()));
        List<DecisionValue> projectedPredicates = new ArrayList<>();
        for (PropertyPredicate predicate : predicates) {
            projectedPredicates.add(fields("$type", token("PropertyPredicate"),
                "propertyName", textHash(predicate.propertyName()),
                "acceptedValues", sequence(predicate.acceptedValues().stream()
                    .map(v -> fields("$type", token(v.getClass().getSimpleName())))
                    .toList())));
        }
        Map<String, DecisionValue> out = new LinkedHashMap<>();
        out.put("$type", token(typeName));
        if (extra != null) {
            out.put("requestedRenderLayer", extra);
        }
        out.put("selectorKind", token(selectorKind));
        out.put("selectorToken", textHash(selectorToken));
        out.put("legacyMetadata", metadata);
        out.put("propertyPredicates", sequence(projectedPredicates));
        out.put("era", token(era));
        out.put("origin", token(origin.getClass().getSimpleName()));
        out.put("sourceLine", integer(sourceLine));
        out.put("selectorOrdinal", integer(selectorOrdinal));
        return new DecisionValue.Fields(out);
    }

    private static DecisionValue assets(PackAssetSnapshot assets) {
        List<DecisionValue> manifest = new ArrayList<>();
        for (PackAssetMetadata metadata : assets.manifest()) {
            manifest.add(fields("$type", token("PackAssetMetadata"),
                "path", token(metadata.path().canonicalString()),
                "availability", token(metadata.availability().name()),
                "byteCount", metadata.byteCount().isPresent()
                    ? integer(metadata.byteCount().getAsInt()) : absent(),
                "sha256", metadata.sha256().isPresent()
                    ? textHash(metadata.sha256().get()) : absent()));
        }
        return sequence(manifest);
    }
}
