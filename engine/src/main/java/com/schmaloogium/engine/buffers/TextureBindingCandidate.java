// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.registry.StageId;

/**
 * One expandable texture binding candidate with its provenance, identity, backing and
 * parameterization.
 */
public record TextureBindingCandidate(
    CandidateOrigin origin, StageId expandedStage, String exactSamplerName,
    FixedSamplerName name, DeclaredGlslType.Sampler shape, TextureTarget target,
    TextureHandleRef handle, TextureSourceIdentity source, TextureParameterSpec parameters,
    TextureParameterFingerprint parameterizationFingerprint, int candidateOrdinal) {

    public TextureBindingCandidate {
        java.util.Objects.requireNonNull(origin, "origin");
        java.util.Objects.requireNonNull(expandedStage, "expandedStage");
        java.util.Objects.requireNonNull(exactSamplerName, "exactSamplerName");
        java.util.Objects.requireNonNull(name, "name");
        java.util.Objects.requireNonNull(shape, "shape");
        java.util.Objects.requireNonNull(target, "target");
        java.util.Objects.requireNonNull(handle, "handle");
        java.util.Objects.requireNonNull(source, "source");
        java.util.Objects.requireNonNull(parameters, "parameters");
        java.util.Objects.requireNonNull(parameterizationFingerprint, "parameterizationFingerprint");
        if (!exactSamplerName.equals(name.exactName())) {
            throw new IllegalArgumentException(
                "exactSamplerName must equal name.exactName(): " + exactSamplerName
                    + " != " + name.exactName());
        }
        if (candidateOrdinal < 0) {
            throw new IllegalArgumentException("candidateOrdinal must be nonnegative: "
                + candidateOrdinal);
        }
    }
}
