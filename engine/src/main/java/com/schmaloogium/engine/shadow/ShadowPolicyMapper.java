// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ShadowMipmapPolicy;
import com.schmaloogium.engine.config.CloudMode;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.ShadowDepthKey;
import com.schmaloogium.engine.config.ShadowRequirements;
import com.schmaloogium.engine.config.ShadowTextureKey;
import com.schmaloogium.engine.config.TriState;
import com.schmaloogium.engine.config.WorldRenderConstants;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.uniforms.OptionalFloat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The Phase 3 → Phase 8 projection (PHASE_8_DOC §2.2 "resolved Phase-3 value, frozen before
 * provider construction"): the pack's {@code const} shadow requirements, its
 * {@code sunPathRotation} and the shaders.properties flags become one {@link ShadowPolicy}.
 *
 * <p>Two values are normalized, never invented: the parser's {@code shadowDistanceRenderMul}
 * default is {@code -1} ("optimization off"), which the plan factory would reject as negative,
 * so any negative multiplier maps to {@code 0} (= {@code FullLoadedView}); every tri-state
 * flag reads {@code DEFAULT} as its documented default ({@code shadowTranslucent} and the four
 * content switches true; clouds cast unless {@code clouds=off}). Everything else is carried
 * verbatim and validated by {@code ShadowPlanFactory}.
 */
public final class ShadowPolicyMapper {

    private ShadowPolicyMapper() {
    }

    public static ShadowPolicy map(ShadowRequirements shadow, WorldRenderConstants world,
            EngineFlags flags) {
        Objects.requireNonNull(shadow, "shadow");
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(flags, "flags");
        OptionalFloat fov = shadow.fov().<OptionalFloat>map(OptionalFloat.Present::new)
                .orElseGet(OptionalFloat.Absent::new);
        float renderMul = shadow.distanceRenderMultiplier();
        if (Float.isFinite(renderMul) && renderMul < 0f) {
            renderMul = 0f;
        }
        return new ShadowPolicy(
                fov,
                shadow.distance(),
                renderMul,
                shadow.intervalSize(),
                world.sunPathRotation(),
                enabled(flags.shadowTranslucent()),
                flags.clouds() != CloudMode.OFF,
                mipmaps(shadow.mipmapped()),
                pcf(shadow.hardwarePcf()),
                new ShadowPolicy.ShadowContent(
                        enabled(flags.shadowTerrain()),
                        enabled(flags.shadowEntities()),
                        enabled(flags.shadowBlockEntities()),
                        enabled(flags.shadowPlayer())));
    }

    /** {@code DEFAULT} and {@code TRUE} enable; only an explicit {@code false} disables. */
    static boolean enabled(TriState state) {
        return state != TriState.FALSE;
    }

    static ShadowMipmapPolicy mipmaps(Set<ShadowTextureKey> mipmapped) {
        List<LogicalBuffer> buffers = new ArrayList<>();
        for (ShadowTextureKey key : mipmapped) {
            buffers.add(switch (key) {
                case DEPTH_0 -> shadowtex(0);
                case DEPTH_1 -> shadowtex(1);
                case COLOR_0 -> shadowcolor(0);
                case COLOR_1 -> shadowcolor(1);
            });
        }
        return new ShadowMipmapPolicy(buffers);
    }

    static ShadowPcfPolicy pcf(Set<ShadowDepthKey> hardwarePcf) {
        Set<LogicalBuffer> members = new LinkedHashSet<>();
        for (ShadowDepthKey key : hardwarePcf) {
            members.add(shadowtex(key == ShadowDepthKey.DEPTH_0 ? 0 : 1));
        }
        return new ShadowPcfPolicy(members);
    }

    private static LogicalBuffer shadowtex(int index) {
        return new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(index));
    }

    private static LogicalBuffer shadowcolor(int index) {
        return new LogicalBuffer(BufferDomain.SHADOWCOLOR, new BufferIndex(index));
    }
}
