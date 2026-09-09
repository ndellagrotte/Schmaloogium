// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Publication failure causes (PHASE_4_DOC §4.12). Every kind through
 * {@code BOOTSTRAP_CANDIDATE} is an exhaustive pre-release cause and produces
 * {@code Rejected}; the remaining kinds are exhaustive failures after old-barrier release
 * begins and produce {@code RecoveredOff}.
 */
public enum PublicationFailureKind {
    NULL_PUBLICATION,
    WRONG_RENDER_THREAD,
    CONTEXT_MISSING,
    CONTEXT_SOURCE,
    CONTEXT_EPOCH,
    CONTEXT_KIND,
    COMPILER_ORIGIN,
    REGISTRY_STATE,
    BARRIER_PROVENANCE,
    COMPOSITION_PROVENANCE,
    PRODUCT_IDENTITY,
    REGISTRY_IDENTITY,
    BARRIER_STATE,
    OWNERSHIP,
    BOOTSTRAP_CANDIDATE,
    RELEASE_SHADERS_OFF,
    RELEASE_FAILED_SAFE,
    RELEASE_PROTOCOL_INVALID,
    RELEASE_EXCEPTION,
    POST_RELEASE_UNEXPECTED_BACKEND
}
