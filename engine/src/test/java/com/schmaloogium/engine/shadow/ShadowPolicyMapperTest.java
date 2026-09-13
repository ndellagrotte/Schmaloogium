// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.config.CloudMode;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.ShadowDepthKey;
import com.schmaloogium.engine.config.ShadowRequirements;
import com.schmaloogium.engine.config.ShadowTextureKey;
import com.schmaloogium.engine.config.TriState;
import com.schmaloogium.engine.config.WorldRenderConstants;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.uniforms.OptionalFloat;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ShadowPolicyMapperTest {

    private static ShadowRequirements requirements(Optional<Float> fov, float renderMul,
            Set<ShadowTextureKey> mipmapped, Set<ShadowDepthKey> pcf) {
        return new ShadowRequirements(2048, fov, 120f, renderMul, 2f, mipmapped,
                EnumSet.noneOf(ShadowTextureKey.class), pcf);
    }

    @Test
    void parserDefaultsMapToTheDocumentedPolicy() {
        ShadowPolicy policy = ShadowPolicyMapper.map(
                requirements(Optional.empty(), -1f, EnumSet.noneOf(ShadowTextureKey.class),
                        EnumSet.noneOf(ShadowDepthKey.class)),
                new WorldRenderConstants(-40f, 1f), EngineFlags.allDefault());
        assertInstanceOf(OptionalFloat.Absent.class, policy.shadowMapFov());
        assertEquals(120f, policy.shadowDistance());
        assertEquals(0f, policy.shadowDistanceRenderMul(), "-1 (optimization off) is FullLoadedView");
        assertEquals(2f, policy.shadowIntervalSize());
        assertEquals(-40f, policy.sunPathRotationDegrees());
        assertTrue(policy.shadowTranslucent());
        assertTrue(policy.cloudsInShadow());
        assertEquals(ShadowPolicy.ShadowContent.ALL, policy.content());
        assertTrue(policy.mipmaps().buffers().isEmpty());
        assertTrue(policy.pcf().compareDepthBuffers().isEmpty());
        // The mapped policy is plan-valid (the -1 default no longer disables shadows).
        assertInstanceOf(ShadowPlanResult.Ready.class, new com.schmaloogium.engine.shadow.internal.ShadowPlanFactoryImpl()
                .plan(new ShadowPlanInput(policy, healthy(), true)));
    }

    @Test
    void explicitValuesAreCarriedAndOnlyFalseDisables() {
        EngineFlags flags = new EngineFlags(CloudMode.OFF, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.FALSE, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT,
                TriState.TRUE, TriState.FALSE, TriState.FALSE, TriState.DEFAULT);
        ShadowPolicy policy = ShadowPolicyMapper.map(
                requirements(Optional.of(90f), 0.5f,
                        EnumSet.of(ShadowTextureKey.COLOR_1, ShadowTextureKey.DEPTH_0),
                        EnumSet.of(ShadowDepthKey.DEPTH_1)),
                new WorldRenderConstants(0f, 1f), flags);
        assertEquals(new OptionalFloat.Present(90f), policy.shadowMapFov());
        assertEquals(0.5f, policy.shadowDistanceRenderMul());
        assertFalse(policy.shadowTranslucent());
        assertFalse(policy.cloudsInShadow(), "clouds=off never casts");
        assertEquals(new ShadowPolicy.ShadowContent(true, false, false, true), policy.content());
        assertEquals(List.of(
                new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(1)),
                new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0))),
                policy.mipmaps().buffers(), "canonical domain order");
        assertEquals(Set.of(new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(1))),
                policy.pcf().compareDepthBuffers());
    }

    @Test
    void contentSwitchesAreInThePlanFingerprint() {
        ShadowRequirements req = requirements(Optional.empty(), 0f,
                EnumSet.noneOf(ShadowTextureKey.class), EnumSet.noneOf(ShadowDepthKey.class));
        ShadowPolicy all = ShadowPolicyMapper.map(req, new WorldRenderConstants(0f, 1f), EngineFlags.allDefault());
        ShadowPolicy noPlayer = new ShadowPolicy(all.shadowMapFov(), all.shadowDistance(),
                all.shadowDistanceRenderMul(), all.shadowIntervalSize(), all.sunPathRotationDegrees(),
                all.shadowTranslucent(), all.cloudsInShadow(), all.mipmaps(), all.pcf(),
                new ShadowPolicy.ShadowContent(true, true, true, false));
        var factory = new com.schmaloogium.engine.shadow.internal.ShadowPlanFactoryImpl();
        var a = (ShadowPlanResult.Ready) factory.plan(new ShadowPlanInput(all, healthy(), true));
        var b = (ShadowPlanResult.Ready) factory.plan(new ShadowPlanInput(noPlayer, healthy(), true));
        assertFalse(a.plan().fingerprint().equals(b.plan().fingerprint()));
    }

    private static ShadowHookHealth healthy() {
        return ShadowHookHealth.of(ShadowHookHealth.catalogue().stream()
                .map(id -> new ShadowHookRow(id, 1, 1, HookDisposition.HEALTHY)).toList());
    }
}
