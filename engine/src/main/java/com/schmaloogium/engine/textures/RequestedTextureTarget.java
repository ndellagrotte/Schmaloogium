// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.FixedSamplerName;

/**
 * Closed requested-target sum (§4.3.6): genuinely unknown names keep their exact spelling;
 * known names out of a stage column keep their typed enum. No null, sentinel, unrelated enum
 * or invented unit is legal.
 */
public sealed interface RequestedTextureTarget {
    record UnknownSampler(String exactName) implements RequestedTextureTarget {
        public UnknownSampler {
            java.util.Objects.requireNonNull(exactName, "exactName");
        }
    }

    record KnownSampler(FixedSamplerName sampler) implements RequestedTextureTarget {
        public KnownSampler {
            java.util.Objects.requireNonNull(sampler, "sampler");
        }
    }
}
