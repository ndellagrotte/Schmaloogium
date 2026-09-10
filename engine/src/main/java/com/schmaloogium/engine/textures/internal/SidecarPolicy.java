// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.textures.OwnedTextureSourceKind;

import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureWrap;

import java.util.Objects;
import java.util.Optional;

/**
 * The D-P13-27 source-specific sidecar policy (§4.3.5): per-role omitted-field baselines,
 * explicit Boolean overrides, atomic recovery, and the policy-role tags the
 * {@code phase13.parameters/v2} digest consumes. Omission retains its own baseline, never
 * false. No mipmaps are requested by these two fields.
 */
public final class SidecarPolicy {

    /** Closed policy-role tags of the parameter fingerprint (§4.5.1). */
    public enum Role {
        CUSTOM_PNG, RAW, NOISE_OVERRIDE, GENERATED_NOISE, RESOURCE_OWNER, COMPANION,
        DEFAULT_FILL, FOREIGN_OWNER
    }

    /** One prepared parameter result: effective spec, baseline, role and sidecar outcome. */
    public record Effective(TextureParameterSpec baseline, TextureParameterSpec effective,
                            Role role, SidecarOutcome sidecarOutcome) {

        public Effective {
            Objects.requireNonNull(baseline, "baseline");
            Objects.requireNonNull(effective, "effective");
            Objects.requireNonNull(role, "role");
            Objects.requireNonNull(sidecarOutcome, "sidecarOutcome");
        }

        /** The canonical phase13.parameters/v2 fingerprint (§4.5.1). */
        public String fingerprint(OwnedTextureSourceKind sourceKind) {
            return TextureDigests.parameterFingerprint(
                sourceKind.name(), role.name(),
                baseline.minFilter().name(), baseline.magFilter().name(),
                baseline.wrap().name(), sidecarOutcome.digest(),
                effective.minFilter().name(), effective.magFilter().name(),
                effective.wrap().name());
        }
    }

    private SidecarPolicy() {
    }

    /** Role baselines (§4.3.5): PNG NEAREST/REPEAT, raw LINEAR/CLAMP, noise LINEAR/REPEAT. */
    public static TextureParameterSpec baseline(Role role) {
        return switch (role) {
            case CUSTOM_PNG -> new TextureParameterSpec(
                TextureMinFilter.NEAREST, TextureMagFilter.NEAREST, TextureWrap.REPEAT);
            case RAW -> new TextureParameterSpec(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.CLAMP_TO_EDGE);
            case NOISE_OVERRIDE, GENERATED_NOISE -> new TextureParameterSpec(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT);
            case COMPANION, DEFAULT_FILL, RESOURCE_OWNER, FOREIGN_OWNER -> throw new
                IllegalArgumentException("role has no sidecar baseline: " + role);
        };
    }

    /**
     * Applies one sidecar outcome to a role baseline. VALID keeps explicit Boolean overrides
     * and omission distinctions; MALFORMED/UNREADABLE atomically resets both fields to the
     * baseline (no valid earlier field survives a later failure). NOT_APPLICABLE/ABSENT use
     * the baseline without a diagnostic.
     */
    public static Effective apply(Role role, SidecarOutcome outcome) {
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(outcome, "outcome");
        TextureParameterSpec base = baseline(role);
        return switch (outcome.outcome()) {
            case NOT_APPLICABLE, ABSENT -> new Effective(base, base, role, outcome);
            case VALID -> new Effective(base, overridden(
                base, outcome.effectiveBlur(), outcome.effectiveClamp()), role, outcome);
            case MALFORMED, UNREADABLE -> new Effective(base, base, role, outcome);
        };
    }

    private static TextureParameterSpec overridden(TextureParameterSpec base,
                                                   Optional<Boolean> blur,
                                                   Optional<Boolean> clamp) {
        TextureMinFilter min = base.minFilter();
        TextureMagFilter mag = base.magFilter();
        if (blur.isPresent()) {
            min = blur.get() ? TextureMinFilter.LINEAR : TextureMinFilter.NEAREST;
            mag = blur.get() ? TextureMagFilter.LINEAR : TextureMagFilter.NEAREST;
        }
        TextureWrap wrap = clamp.isPresent()
            ? (clamp.get() ? TextureWrap.CLAMP_TO_EDGE : TextureWrap.REPEAT)
            : base.wrap();
        return new TextureParameterSpec(min, mag, wrap);
    }

    /** The fixed NOT_APPLICABLE outcome for roles without sidecars (§4.5.1). */
    public static SidecarOutcome notApplicable() {
        return SidecarOutcome.notApplicableOutcome();
    }

    /** Companion/DefaultFill/Generated parameter fingerprints use the NOT_APPLICABLE sidecar. */
    public static String fixedRoleFingerprint(OwnedTextureSourceKind sourceKind, Role role,
                                              TextureParameterSpec spec) {
        return TextureDigests.parameterFingerprint(
            sourceKind.name(), role.name(),
            spec.minFilter().name(), spec.magFilter().name(), spec.wrap().name(),
            notApplicable().digest(),
            spec.minFilter().name(), spec.magFilter().name(), spec.wrap().name());
    }
}
