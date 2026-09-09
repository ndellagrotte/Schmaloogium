// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.DimensionKey;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Last-valid-wins builder for alphaTest/blend/scale/flip/program.enabled property keys. */
public final class ProgramStateBuilder {

    private ProgramStateBuilder() {
    }

    public static void apply(Map<ProgramKey, ProgramStateBuilder> states, String key, String value) {
        if (key.startsWith("program.") && key.endsWith(".enabled")) {
            String program = key.substring("program.".length(), key.length() - ".enabled".length());
            builder(states, program).enabledExpression = BooleanExpression.parse(value).orElse(null);
            return;
        }
        int firstDot = key.indexOf('.');
        int secondDot = key.indexOf('.', firstDot + 1);
        if (secondDot < 0) {
            return;
        }
        String directive = key.substring(0, firstDot);
        String program = key.substring(firstDot + 1, secondDot);
        String operand = key.substring(secondDot + 1);
        ProgramStateBuilder builder = builder(states, program);
        switch (directive) {
            case "alphaTest" -> builder.alphaTest = parseAlphaTest(value);
            case "blend" -> builder.blend = parseBlend(value);
            case "scale" -> builder.scale = parseScale(value);
            case "flip" -> {
                FlipOverride override = parseFlip(value);
                Integer index = ColorBufferNames.normalize(operand);
                if (override != null && index != null) {
                    builder.flips.put(new FlipBufferKey(new ColorAttachmentKey(index)), override);
                }
            }
            default -> {
            }
        }
    }

    private static ProgramStateBuilder builder(Map<ProgramKey, ProgramStateBuilder> states,
            String program) {
        String name = program;
        DimensionKey dimension = DimensionKey.BASE;
        int slash = program.indexOf('/');
        if (slash > 0 && program.startsWith("world")) {
            try {
                dimension = DimensionKey.world(Integer.parseInt(program.substring(5, slash)));
                name = program.substring(slash + 1);
            } catch (NumberFormatException ignored) {
                dimension = DimensionKey.BASE;
                name = program;
            }
        }
        return states.computeIfAbsent(new ProgramKey(dimension, name),
            k -> new ProgramStateBuilder());
    }

    private static AlphaTestSpec parseAlphaTest(String value) {
        String[] tokens = value.trim().split("\\s+");
        if (tokens.length == 1 && tokens[0].equalsIgnoreCase("off")) {
            return new AlphaTestSpec.Off();
        }
        if (tokens.length == 2) {
            AlphaFunction function = alphaFunction(tokens[0]);
            if (function != null) {
                try {
                    return new AlphaTestSpec.Enabled(function, Float.parseFloat(tokens[1]));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private static AlphaFunction alphaFunction(String token) {
        return switch (token.toUpperCase()) {
            case "NEVER" -> AlphaFunction.NEVER;
            case "LESS" -> AlphaFunction.LESS;
            case "EQUAL" -> AlphaFunction.EQUAL;
            case "LEQUAL" -> AlphaFunction.LEQUAL;
            case "GREATER" -> AlphaFunction.GREATER;
            case "NOTEQUAL" -> AlphaFunction.NOTEQUAL;
            case "GEQUAL" -> AlphaFunction.GEQUAL;
            case "ALWAYS" -> AlphaFunction.ALWAYS;
            default -> null;
        };
    }

    private static BlendSpec parseBlend(String value) {
        String[] tokens = value.trim().split("\\s+");
        if (tokens.length == 1 && tokens[0].equalsIgnoreCase("off")) {
            return new BlendSpec.Off();
        }
        if (tokens.length < 2) {
            return null;
        }
        BlendFactor sourceColor = blendFactor(tokens[0]);
        BlendFactor destinationColor = blendFactor(tokens[1]);
        if (sourceColor == null || destinationColor == null) {
            return null;
        }
        if (tokens.length >= 4) {
            BlendFactor sourceAlpha = blendFactor(tokens[2]);
            BlendFactor destinationAlpha = blendFactor(tokens[3]);
            if (sourceAlpha == null || destinationAlpha == null) {
                return null;
            }
            return new BlendSpec.Enabled(sourceColor, destinationColor,
                Optional.of(new BlendAlphaFactors(sourceAlpha, destinationAlpha)));
        }
        return new BlendSpec.Enabled(sourceColor, destinationColor, Optional.empty());
    }

    private static BlendFactor blendFactor(String token) {
        return switch (token) {
            case "ZERO" -> BlendFactor.ZERO;
            case "ONE" -> BlendFactor.ONE;
            case "SRC_COLOR" -> BlendFactor.SRC_COLOR;
            case "ONE_MINUS_SRC_COLOR" -> BlendFactor.ONE_MINUS_SRC_COLOR;
            case "DST_COLOR" -> BlendFactor.DST_COLOR;
            case "ONE_MINUS_DST_COLOR" -> BlendFactor.ONE_MINUS_DST_COLOR;
            case "SRC_ALPHA" -> BlendFactor.SRC_ALPHA;
            case "ONE_MINUS_SRC_ALPHA" -> BlendFactor.ONE_MINUS_SRC_ALPHA;
            case "DST_ALPHA" -> BlendFactor.DST_ALPHA;
            case "ONE_MINUS_DST_ALPHA" -> BlendFactor.ONE_MINUS_DST_ALPHA;
            case "CONSTANT_COLOR" -> BlendFactor.CONSTANT_COLOR;
            case "ONE_MINUS_CONSTANT_COLOR" -> BlendFactor.ONE_MINUS_CONSTANT_COLOR;
            case "CONSTANT_ALPHA" -> BlendFactor.CONSTANT_ALPHA;
            case "ONE_MINUS_CONSTANT_ALPHA" -> BlendFactor.ONE_MINUS_CONSTANT_ALPHA;
            case "SRC_ALPHA_SATURATE" -> BlendFactor.SRC_ALPHA_SATURATE;
            default -> null;
        };
    }

    private static ViewportScale parseScale(String value) {
        String[] tokens = value.trim().split("\\s+");
        try {
            if (tokens.length == 1) {
                return new ViewportScale(Float.parseFloat(tokens[0]), 0.0f, 0.0f);
            }
            if (tokens.length == 3) {
                return new ViewportScale(Float.parseFloat(tokens[0]),
                    Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return null;
    }

    private static FlipOverride parseFlip(String value) {
        return switch (value.trim()) {
            case "true" -> FlipOverride.TRUE;
            case "false" -> FlipOverride.FALSE;
            default -> null;
        };
    }


    ProgramState build() {
        Map<FlipBufferKey, FlipOverride> flipsCopy = Map.copyOf(flips);
        return new ProgramState(
            Optional.ofNullable(alphaTest),
            Optional.ofNullable(blend),
            Optional.ofNullable(scale),
            flipsCopy,
            Optional.ofNullable(enabledExpression == null
                ? null : new ProgramEnabledExpressionValue(enabledExpression,
                    enabledExpression.render().getBytes(java.nio.charset.StandardCharsets.UTF_8))));
    }

    AlphaTestSpec alphaTest;
    BlendSpec blend;
    ViewportScale scale;
    final Map<FlipBufferKey, FlipOverride> flips = new LinkedHashMap<>();
    BooleanExpression enabledExpression;
}
