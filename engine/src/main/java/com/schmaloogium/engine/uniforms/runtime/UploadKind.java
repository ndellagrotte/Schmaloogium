// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

/**
 * Closed upload-operation domain of one {@link UploadCommand} (PHASE_6_DOC §4.11):
 * exactly the facade verbs the built-in catalog and the six custom command variants
 * need. {@code Bool1} commands are encoded by Phase 6 to {@code INT} 0/1 before the
 * batch is built — Phase 11 never performs GL encoding (D-P6-17).
 */
public enum UploadKind {
    INT,
    FLOAT,
    INT2,
    INT4,
    FLOAT2,
    FLOAT3,
    FLOAT4,
    MAT4
}
