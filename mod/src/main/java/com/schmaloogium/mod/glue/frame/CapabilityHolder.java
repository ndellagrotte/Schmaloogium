// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Publication slot for the stage-2 capability profile captured at H-BOOT-02. The mod's
 * compat context reads it so capability gates answer from the captured profile instead
 * of re-probing GL off the bootstrap path.
 */
public final class CapabilityHolder {

    private static final AtomicReference<Optional<GLCapabilityProfile>> PROFILE =
            new AtomicReference<>(Optional.empty());

    private CapabilityHolder() {
    }

    static void publish(GLCapabilityProfile profile) {
        PROFILE.set(Optional.ofNullable(profile));
    }

    public static Optional<GLCapabilityProfile> peek() {
        return PROFILE.get();
    }
}
