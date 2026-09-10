// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.Objects;

/**
 * Phase-13-owned plan-level source identity (§2.3). Owned uploads hash immutable content and
 * configuration identity; foreign live sources hash the logical resource/reload/object epochs,
 * never mutable pixels or GL names.
 */
public sealed interface TextureSourceIdentity {

    record OwnedUpload(OwnedTextureSourceKind sourceKind, String logicalSource,
                       String contentDigest, String configurationIdentity)
            implements TextureSourceIdentity {
        public OwnedUpload {
            Objects.requireNonNull(sourceKind, "sourceKind");
            Objects.requireNonNull(logicalSource, "logicalSource");
            Objects.requireNonNull(contentDigest, "contentDigest");
            Objects.requireNonNull(configurationIdentity, "configurationIdentity");
        }
    }

    record ForeignLive(String exactResourceIdentity, long resourceReloadEpoch, long objectEpoch)
            implements TextureSourceIdentity {
        public ForeignLive {
            Objects.requireNonNull(exactResourceIdentity, "exactResourceIdentity");
        }
    }
}
