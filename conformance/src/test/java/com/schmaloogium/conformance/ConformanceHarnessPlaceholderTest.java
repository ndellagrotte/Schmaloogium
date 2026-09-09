// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the {@code :conformance} module builds and runs JUnit (PHASE_1_DOC §8.1): the
 * module exists, its {@code :engine} dependency resolves, and the harness slot is wired.
 * The harness content is Phase 2's.
 */
class ConformanceHarnessPlaceholderTest {

    @Test
    void moduleIsWired() {
        assertTrue(com.schmaloogium.engine.log.LogChannels.BOOT.startsWith("schmaloogium."),
                ":conformance must see :engine's classes");
    }
}
