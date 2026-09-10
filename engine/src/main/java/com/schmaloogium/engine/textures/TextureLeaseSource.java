// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.registry.ProgramBindingSelection;

/**
 * The restricted lease source (§4.5.2). Validates, in order, publication availability,
 * expected id, registry fingerprint, selection validity, base-binding validity and base
 * currentness — every rejection before incrementing the lease count. The old two-argument
 * overload does not exist.
 */
public interface TextureLeaseSource {

    TextureLeaseResult lease(TextureOverlayPublicationId expected,
                              ProgramBindingSelection selection,
                              AtlasBindingEvidence baseBinding);
}
