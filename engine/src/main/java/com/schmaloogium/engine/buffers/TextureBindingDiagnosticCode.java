// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Closed texture-binding diagnostic vocabulary (Phase 5 Doc §2.4). */
public enum TextureBindingDiagnosticCode {
    NO_CANDIDATE, INCOMPATIBLE_CANDIDATE, CONFLICTING_SAMPLER_TYPES, CONFLICTING_CANDIDATES,
    UNSUPPORTED_SAMPLER_NAME, UNSUPPORTED_STAGE_DOMAIN, UNSUPPORTED_SAMPLER_SHAPE,
    PUBLICATION_UNAVAILABLE, NOT_CONFIGURED, NOT_APPLICABLE_TO_STAGE, MISSING_BACKING
}
