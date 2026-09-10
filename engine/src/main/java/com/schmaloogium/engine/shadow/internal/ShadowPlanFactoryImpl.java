// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.shadow.CelestialMath;
import com.schmaloogium.engine.shadow.HookDisposition;
import com.schmaloogium.engine.shadow.ShadowCelestialAngles;
import com.schmaloogium.engine.shadow.ShadowCelestialPolicy;
import com.schmaloogium.engine.shadow.ShadowDisableReason;
import com.schmaloogium.engine.shadow.ShadowHookHealth;
import com.schmaloogium.engine.shadow.ShadowHookRow;
import com.schmaloogium.engine.shadow.ShadowPlan;
import com.schmaloogium.engine.shadow.ShadowPlanFactory;
import com.schmaloogium.engine.shadow.ShadowPlanFingerprint;
import com.schmaloogium.engine.shadow.ShadowPlanInput;
import com.schmaloogium.engine.shadow.ShadowPlanResult;
import com.schmaloogium.engine.shadow.ShadowPcfPolicy;
import com.schmaloogium.engine.shadow.ShadowPolicy;
import com.schmaloogium.engine.uniforms.OptionalFloat;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The sole plan implementation (PHASE_8_DOC §4.1). Pure: no registry identity, GL, world
 * or MC state; deterministic within a JVM; validates in the listed order — structural
 * input validation, absence, the policy checks, then the required hook rows — and
 * computes the canonical fingerprint from plan contents. Diagnostics use stable ids on
 * the {@code schmaloogium.shadow} channel; the reporting id pairs the disable cause.
 */
public final class ShadowPlanFactoryImpl implements ShadowPlanFactory {

    /** §4.1 default when shadowMapFov is absent. */
    public static final float DEFAULT_SHADOW_MAP_FOV = 110.0f;

    private static final String PLAN_PREIMAGE_HEADER = "ShadowPlan/v1\n";

    @Override
    public ShadowPlanResult plan(ShadowPlanInput input) {
        Objects.requireNonNull(input, "input");
        ShadowPolicy policy = input.policy();
        try {
            validateStructure(policy);
        } catch (IllegalArgumentException | NullPointerException e) {
            return disabled(new ShadowDisableReason.InvalidPolicy("structure"),
                    "schmaloogium.shadow.plan.disabled.policy.structure");
        }
        if (!input.requested()) {
            return new ShadowPlanResult.NotRequested();
        }
        String policyField = validatePolicy(policy);
        if (policyField != null) {
            return disabled(new ShadowDisableReason.InvalidPolicy(policyField),
                    "schmaloogium.shadow.plan.disabled.policy." + policyField);
        }
        for (ShadowHookRow row : input.hookHealth().rows()) {
            if (ShadowHookHealth.gatesShadow(row.hookId())
                    && row.disposition() != HookDisposition.HEALTHY) {
                return disabled(new ShadowDisableReason.HookUnavailable(row.hookId()),
                        "schmaloogium.shadow.plan.disabled.hook." + row.hookId());
            }
        }
        ShadowPlanFingerprint fingerprint =
                new ShadowPlanFingerprint(CanonicalSha256.of(fingerprintPreimage(input)));
        ShadowCelestialPolicy celestial =
                sunAngle -> CelestialMath.angles(sunAngle);
        return new ShadowPlanResult.Ready(
                new ShadowPlan(policy, celestial, input.hookHealth(), fingerprint));
    }

    private static ShadowPlanResult.Disabled disabled(
            ShadowDisableReason reason, String diagnosticId) {
        return new ShadowPlanResult.Disabled(reason, diagnosticId);
    }

    /** Null-availability of the whole policy object is malformed structure. */
    private static void validateStructure(ShadowPolicy policy) {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(policy.mipmaps(), "mipmaps");
        Objects.requireNonNull(policy.pcf(), "pcf");
    }

    /**
     * §4.1 listed policy checks; returns the offending field name, or {@code null} when
     * every check passes. Values are validated, never clamped into a different pack
     * contract.
     */
    private static String validatePolicy(ShadowPolicy policy) {
        if (policy.shadowMapFov().isPresent()) {
            float fov = policy.shadowMapFov().value();
            if (!Float.isFinite(fov) || fov <= 0f || fov >= 180f) {
                return "shadowMapFov";
            }
        }
        if (!Float.isFinite(policy.shadowDistance()) || policy.shadowDistance() <= 0f) {
            return "shadowDistance";
        }
        float renderMul = policy.shadowDistanceRenderMul();
        if (!Float.isFinite(renderMul) || renderMul < 0f || renderMul > 1f) {
            return "shadowDistanceRenderMul";
        }
        if (!Float.isFinite(policy.shadowIntervalSize()) || policy.shadowIntervalSize() <= 0f) {
            return "shadowIntervalSize";
        }
        if (!Float.isFinite(policy.sunPathRotationDegrees())) {
            return "sunPathRotationDegrees";
        }
        for (LogicalBuffer member : policy.pcf().compareDepthBuffers()) {
            if (member.domain() != BufferDomain.SHADOWTEX
                    || (member.index().value() != 0 && member.index().value() != 1)) {
                return "pcf";
            }
        }
        return null;
    }

    private static String fingerprintPreimage(ShadowPlanInput input) {
        ShadowPolicy policy = input.policy();
        StringBuilder preimage = new StringBuilder(PLAN_PREIMAGE_HEADER);
        if (policy.shadowMapFov().isPresent()) {
            preimage.append("shadowMapFov=present\n")
                    .append("shadowMapFovValue=")
                    .append(Float.toString(policy.shadowMapFov().value())).append('\n');
        } else {
            preimage.append("shadowMapFov=absent\n")
                    .append("shadowMapFovValue=")
                    .append(Float.toString(DEFAULT_SHADOW_MAP_FOV)).append('\n');
        }
        preimage.append("shadowDistance=").append(Float.toString(policy.shadowDistance())).append('\n')
                .append("shadowDistanceRenderMul=")
                .append(Float.toString(policy.shadowDistanceRenderMul())).append('\n')
                .append("shadowIntervalSize=")
                .append(Float.toString(policy.shadowIntervalSize())).append('\n')
                .append("sunPathRotationDegrees=")
                .append(Float.toString(policy.sunPathRotationDegrees())).append('\n')
                .append("shadowTranslucent=").append(policy.shadowTranslucent()).append('\n')
                .append("cloudsInShadow=").append(policy.cloudsInShadow()).append('\n')
                .append("mipmaps=").append(canonicalMipmaps(policy.mipmaps())).append('\n')
                .append("pcf=").append(canonicalPcf(policy.pcf())).append('\n')
                .append("hookHealthFingerprint=").append(input.hookHealth().fingerprint().canonicalSha256())
                .append('\n')
                .append("requested=true\n");
        return preimage.toString();
    }

    private static String canonicalMipmaps(com.schmaloogium.engine.buffers.ShadowMipmapPolicy mipmaps) {
        StringJoiner joiner = new StringJoiner(",");
        for (LogicalBuffer buffer : mipmaps.buffers()) {
            joiner.add(buffer.domain().name() + ":" + buffer.index().value());
        }
        return joiner.toString();
    }

    private static String canonicalPcf(ShadowPcfPolicy pcf) {
        StringJoiner joiner = new StringJoiner(",");
        for (LogicalBuffer member : pcf.canonicalMembers()) {
            joiner.add(member.domain().name() + ":" + member.index().value());
        }
        return joiner.toString();
    }
}
