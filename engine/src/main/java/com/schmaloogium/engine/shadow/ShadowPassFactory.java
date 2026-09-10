// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * The Phase-8 pass factory (PHASE_8_DOC §5.1): builds at most one ready invocation slot
 * per publication, or a typed disable. Construction is pure wiring; GL work happens
 * only inside {@link ShadowInvocationSlot#invoke}.
 */
public interface ShadowPassFactory {

    ShadowPassBuildResult create(ShadowPassBuildInput input);

    /** The default implementation. */
    static ShadowPassFactory standard() {
        return Standard.INSTANCE;
    }

    /** Holder for the shared factory. */
    final class Standard {
        private Standard() {
        }

        private static final ShadowPassFactory INSTANCE =
                new com.schmaloogium.engine.shadow.internal.ShadowPassFactoryImpl();
    }
}
