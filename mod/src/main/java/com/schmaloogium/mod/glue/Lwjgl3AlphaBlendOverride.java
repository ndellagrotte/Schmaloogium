// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.AlphaBlendOverride;
import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;

import java.util.Optional;

/**
 * The alpha/blend lease ([D-P1-57]): non-null optionals only, one at a time, closing
 * restores the captured real values through GLSM with the two-step poke. The lease
 * never carries a facade device and cannot be interrogated by the engine; the
 * P7-registered mutation hooks consult the device's suppression gate, which is false
 * while the lease is being restored (bypass depth) and before any device is installed.
 */
final class Lwjgl3AlphaBlendOverride implements AlphaBlendOverride {

    private final Lwjgl3GLDevice device;
    private final boolean holdsAlpha;
    private final boolean holdsBlend;
    private final AlphaTestState savedAlpha;
    private final BlendState savedBlend;
    private boolean consumed;

    Lwjgl3AlphaBlendOverride(Lwjgl3GLDevice device,
                             Optional<AlphaTestState> alpha, Optional<BlendState> blend) {
        this.device = device;
        this.holdsAlpha = alpha.isPresent();
        this.holdsBlend = blend.isPresent();
        // Capture the real values of exactly the held aspects before any write.
        this.savedAlpha = holdsAlpha ? device.captureAlphaForLock() : null;
        this.savedBlend = holdsBlend ? device.captureBlendForLock() : null;
        if (holdsAlpha) {
            device.stateIssueAlphaTest(alpha.get());
        }
        if (holdsBlend) {
            device.stateIssueBlend(blend.get());
        }
    }

    boolean holdsAlpha() {
        return holdsAlpha;
    }

    boolean holdsBlend() {
        return holdsBlend;
    }

    @Override
    public void close() {
        if (consumed) {
            return; // idempotent closure ([D-P1-57])
        }
        consumed = true;
        device.enterBypass(); // our own restoration must not be suppressed
        try {
            if (holdsAlpha) {
                device.stateIssueAlphaTest(savedAlpha);
            }
            if (holdsBlend) {
                device.stateIssueBlend(savedBlend);
            }
        } finally {
            device.exitBypass();
            device.detachAlphaLease(this);
        }
    }
}
