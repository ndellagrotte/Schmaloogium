// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.modern;

import com.schmaloogium.engine.gl.AsyncCompileTier;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GlModernizationPolicy;

import java.util.List;

/**
 * D-P14-12's shipped driver-eligibility data for the shared-context compile tier
 * (PHASE_14_DOC §4.4): a small data table keyed on {@link GLCapabilityProfile#vendor()}
 * and {@link GLCapabilityProfile#renderer()} substrings. Eligibility defaults to DENY —
 * no driver family is eligible for {@link AsyncCompileTier#SHARED_CONTEXT} until the
 * OQ-15 spike (PHASE_14_DOC §10.1) positively records a pass for that family, so the
 * shipped instance's allowlist is EMPTY and enabling a family is a data change, never a
 * code change. The recorded per-family verdict table of
 * {@code docs/decisions/OQ-15_ASYNC_COMPILE.md} is the source of this table's contents.
 *
 * <p>Row resolution (PHASE_14_DOC §8.1 row 16, DriverPolicyTest): an unknown
 * vendor/renderer resolves to {@link AsyncCompileTier#INLINE} (default deny); an
 * allowlisted family resolves to {@link AsyncCompileTier#SHARED_CONTEXT};
 * {@link GlModernizationPolicy.Row#FORCE_OFF} overrides the allowlist; the denylist
 * overrides {@link GlModernizationPolicy.Row#FORCE_ON}.
 *
 * <p>Pure: no GL, no LWJGL, no Minecraft. Render-thread confinement and worker-context
 * mechanics are the backend's (post-v0.5, checklist items 30–32); this value only
 * decides the tier.
 */
public record AsyncCompileDriverPolicy(List<Family> allowlist, List<Family> denylist) {

    /** The shipped data: deny-by-default, no OQ-15 pass recorded for any family yet. */
    public static final AsyncCompileDriverPolicy SHIPPED =
            new AsyncCompileDriverPolicy(List.of(), List.of());

    public AsyncCompileDriverPolicy {
        allowlist = List.copyOf(allowlist);
        denylist = List.copyOf(denylist);
    }

    /** One driver-family rule. A blank component matches any value of that string. */
    public record Family(String vendorSubstring, String rendererSubstring) {

        public Family {
            vendorSubstring = normalized(vendorSubstring);
            rendererSubstring = normalized(rendererSubstring);
        }

        private static String normalized(String value) {
            return value == null ? "" : value.strip().toLowerCase(java.util.Locale.ROOT);
        }

        /** Case-insensitive substring match; a blank component is a wildcard. */
        public boolean matches(GLCapabilityProfile profile) {
            return contains(vendorSubstring, profile.vendor())
                    && contains(rendererSubstring, profile.renderer());
        }

        private static boolean contains(String substring, String actual) {
            return substring.isEmpty()
                    || actual.toLowerCase(java.util.Locale.ROOT).contains(substring);
        }
    }

    /** True when any allowlist family matches the profile. */
    public boolean allowlisted(GLCapabilityProfile profile) {
        return anyMatches(allowlist, profile);
    }

    /** True when any denylist family matches the profile; the denylist always wins. */
    public boolean denylisted(GLCapabilityProfile profile) {
        return anyMatches(denylist, profile);
    }

    /** Resolves the compile row for one profile under this table (the row-16 semantics). */
    public AsyncCompileTier resolve(GLCapabilityProfile profile, GlModernizationPolicy.Row row) {
        return switch (row) {
            case FORCE_OFF -> AsyncCompileTier.INLINE;
            // D-P14-12: enabled only by a recorded OQ-15 pass, as shipped data.
            case AUTO -> allowlisted(profile) ? AsyncCompileTier.SHARED_CONTEXT : AsyncCompileTier.INLINE;
            // A per-user FORCE_ON overrides the allowlist; the denylist overrides FORCE_ON.
            case FORCE_ON -> denylisted(profile) ? AsyncCompileTier.INLINE : AsyncCompileTier.SHARED_CONTEXT;
        };
    }

    private static boolean anyMatches(List<Family> families, GLCapabilityProfile profile) {
        for (Family family : families) {
            if (family.matches(profile)) {
                return true;
            }
        }
        return false;
    }
}
