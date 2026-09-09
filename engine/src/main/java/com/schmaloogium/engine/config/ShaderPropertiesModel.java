// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** Complete immutable Appendix F shaders.properties model. */
public record ShaderPropertiesModel(
        EngineFlags engineFlags,
        List<MinimumEditionRule> minimumEditionRules,
        List<CustomTextureSpec> textures,
        List<TexturePropertyDecl> textureDeclarations,
        NoiseTextureSpec noise,
        List<CustomExpressionDecl> customExpressions,
        ProgramStateModel programStates,
        List<UnknownProperty> unknownProperties) {

    public ShaderPropertiesModel {
        java.util.Objects.requireNonNull(engineFlags, "engineFlags");
        minimumEditionRules = List.copyOf(minimumEditionRules);
        textures = List.copyOf(textures);
        textureDeclarations = List.copyOf(textureDeclarations);
        java.util.Objects.requireNonNull(noise, "noise");
        customExpressions = List.copyOf(customExpressions);
        java.util.Objects.requireNonNull(programStates, "programStates");
        unknownProperties = List.copyOf(unknownProperties);
    }
}
