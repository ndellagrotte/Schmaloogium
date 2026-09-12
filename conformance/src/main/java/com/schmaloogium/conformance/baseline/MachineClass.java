// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.baseline;

import com.schmaloogium.engine.gl.GLCapabilityProfile;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

/**
 * The §4.7.2 {@code machineClass} string derived from a manifest's canonical
 * {@code gl.profile_text}: {@code <vendor-short>-gl<major><minor> / <renderer> / <os>-<arch>}.
 * A different string escalates the tolerance profile to {@code CROSS_DRIVER} (§4.7.4).
 */
public final class MachineClass {

    private MachineClass() {
    }

    public static String of(String profileText, String os, String arch) {
        GLCapabilityProfile profile;
        try {
            profile = GLCapabilityProfile.parse(new StringReader(profileText));
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException("gl.profile_text is not a canonical profile: " + e.getMessage());
        }
        String vendor = profile.vendor().toLowerCase(Locale.ROOT);
        String vendorShort = vendor.contains("nvidia") ? "nvidia"
            : vendor.contains("amd") || vendor.contains("ati") ? "amd"
            : vendor.contains("intel") ? "intel"
            : vendor.contains("mesa") ? "mesa"
            : vendor.replaceAll("[^a-z0-9]+", "-");
        return vendorShort + "-gl" + profile.glVersionMajor() + profile.glVersionMinor() + " / "
            + profile.renderer() + " / " + os.toLowerCase(Locale.ROOT) + "-" + arch.toLowerCase(Locale.ROOT);
    }
}
