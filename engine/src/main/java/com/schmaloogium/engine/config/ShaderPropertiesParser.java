// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;

/**
 * Parses the complete preprocessed shaders.properties stream into the immutable
 * ShaderPropertiesModel, retaining declarations, unknown keys, and the exact
 * closed grammars of section 4.8. Last valid value wins everywhere.
 */
public final class ShaderPropertiesParser {

    private ShaderPropertiesParser() {
    }

    public static ShaderPropertiesModel parse(List<LogicalProperties.Entry> entries,
            NormalizedPackPath sourcePath) {
        EngineFlagsAccumulator flags = new EngineFlagsAccumulator();
        List<MinimumEditionRule> minimumEdition = new ArrayList<>();
        List<CustomTextureSpec> textures = new ArrayList<>();
        List<TexturePropertyDecl> textureDecls = new ArrayList<>();
        List<CustomExpressionDecl> expressions = new ArrayList<>();
        NoiseTextureSpec noise = new NoiseTextureSpec.Generated();
        Map<ProgramKey, ProgramStateBuilder> programStates = new LinkedHashMap<>();
        List<UnknownProperty> unknown = new ArrayList<>();

        int ordinal = 0;
        for (LogicalProperties.Entry entry : entries) {
            String key = entry.key();
            String value = entry.value();
            SourceAttribution attribution = new SourceAttribution(sourcePath,
                entry.physicalLine(), 1);
            try {
                if (key.equals("clouds")) {
                    flags.clouds = parseCloudMode(value);
                } else if (EngineFlagsAccumulator.TRI_STATES.containsKey(key)) {
                    flags.triStates.put(key, parseTriState(value));
                } else if (key.startsWith("profile.")) {
                    // profiles are decoded by the option layer from the same stream
                } else if (key.equals("screen") || key.startsWith("screen.")
                        || key.equals("sliders")) {
                    // consumed by ProfileScreenParser over the retained stream
                } else if (key.startsWith("texture.")) {
                    parseTexture(key, value, ordinal, attribution, textures, textureDecls);
                    ordinal++;
                } else if (key.startsWith("uniform.") || key.startsWith("variable.")) {
                    parseCustomExpression(key, value, ordinal, attribution, expressions);
                    ordinal++;
                } else if (key.startsWith("alphaTest.") || key.startsWith("blend.")
                        || key.startsWith("scale.") || key.startsWith("flip.")
                        || key.startsWith("program.")) {
                    ProgramStateBuilder.apply(programStates, key, value);
                } else if (key.startsWith("version.")) {
                    MinimumEditionRule rule = MinimumEditionRules.parse(key, value);
                    if (rule != null) {
                        minimumEdition.add(rule);
                    }
                } else if (key.equals("noiseTextureResolution")) {
                    Integer resolution = NoiseTextureResolutions.parse(value);
                    if (resolution != null) {
                        noise = new NoiseTextureSpec.Generated();
                    }
                } else {
                    unknown.add(new UnknownProperty(key, value, attribution));
                }
            } catch (RuntimeException malformed) {
                unknown.add(new UnknownProperty(key, value, attribution));
            }
        }

        Map<ProgramKey, ProgramState> states = new TreeMap<>((a, b) -> {
            int byDim = a.dimension().compareTo(b.dimension());
            return byDim != 0 ? byDim
                : EngineOptionData.compareUnsignedUtf8(a.programName(), b.programName());
        });
        programStates.forEach((k, v) -> states.put(k, v.build()));
        return new ShaderPropertiesModel(flags.build(), minimumEdition, textures, textureDecls,
            noise, expressions, new ProgramStateModel(states), unknown);
    }

    private static CloudMode parseCloudMode(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "fast" -> CloudMode.FAST;
            case "fancy" -> CloudMode.FANCY;
            case "off" -> CloudMode.OFF;
            default -> CloudMode.DEFAULT;
        };
    }

    private static TriState parseTriState(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true" -> TriState.TRUE;
            case "false" -> TriState.FALSE;
            default -> TriState.DEFAULT;
        };
    }

    private static void parseTexture(String key, String value, int ordinal,
            SourceAttribution attribution, List<CustomTextureSpec> textures,
            List<TexturePropertyDecl> decls) {
        String[] segments = key.split("\\.");
        TexturePropertyDecl invalid = new TexturePropertyDecl(key, value, ordinal, attribution,
            TexturePropertyDisposition.INVALID_VALUE);
        if (segments.length < 3 || segments.length > 4) {
            decls.add(invalid);
            return;
        }
        TexturePropertyStage stage = stageOf(segments[1]);
        if (stage == null) {
            decls.add(invalid);
            return;
        }
        OptionalInt duplicate = OptionalInt.empty();
        if (segments.length == 4) {
            try {
                int n = Integer.parseInt(segments[3]);
                if (n < 0 || n > 9) {
                    throw new NumberFormatException();
                }
                duplicate = OptionalInt.of(n);
            } catch (NumberFormatException e) {
                decls.add(invalid);
                return;
            }
        }
        TextureBindingKey bindingKey = new TextureBindingKey(stage, segments[2], duplicate);
        String[] tokens = value.trim().split("\\s+");
        CustomTextureSpec spec = TextureSpecReducer.reduce(bindingKey, tokens);
        decls.add(new TexturePropertyDecl(key, value, ordinal, attribution,
            spec == null ? TexturePropertyDisposition.INVALID_VALUE
                : TexturePropertyDisposition.CUSTOM_SOURCE));
        if (spec != null) {
            textures.add(spec);
        }
    }

    private static TexturePropertyStage stageOf(String token) {
        return switch (token.toLowerCase(Locale.ROOT)) {
            case "gbuffers" -> TexturePropertyStage.GBUFFERS;
            case "deferred" -> TexturePropertyStage.DEFERRED;
            case "composite" -> TexturePropertyStage.COMPOSITE;
            default -> null;
        };
    }

    private static void parseCustomExpression(String key, String value, int ordinal,
            SourceAttribution attribution, List<CustomExpressionDecl> expressions) {
        String[] segments = key.split("\\.");
        if (segments.length != 3) {
            return;
        }
        CustomExpressionKind kind = segments[0].equals("uniform")
            ? CustomExpressionKind.UNIFORM : CustomExpressionKind.VARIABLE;
        CustomExpressionType type = CustomExpressionTypes.of(segments[1]);
        if (type == null || segments[2].isEmpty()) {
            return;
        }
        expressions.add(new CustomExpressionDecl(kind, type, segments[2], value,
            ordinal, attribution));
    }
}
