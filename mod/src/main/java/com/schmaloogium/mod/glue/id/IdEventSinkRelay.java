// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.uniforms.BlendSample;
import com.schmaloogium.engine.uniforms.CelestialSample;
import com.schmaloogium.engine.uniforms.FogSample;
import com.schmaloogium.engine.uniforms.HeldItemSample;
import com.schmaloogium.engine.uniforms.Float4;
import com.schmaloogium.engine.uniforms.Int2;
import com.schmaloogium.engine.uniforms.Matrix4Value;
import com.schmaloogium.engine.uniforms.ShadowMatrixSample;
import com.schmaloogium.engine.uniforms.UniformEventSink;

/**
 * A {@link UniformEventSink} whose target is the currently installed P6 runtime's sink
 * (or nothing while none is installed). The P9 publisher and held-item resolver bind one
 * sink at construction (PHASE_9_DOC §5.3), while the P6 runtime is rebuilt on every
 * pipeline install; the relay lets one publisher span installs so id generations stay
 * monotonic.
 */
public final class IdEventSinkRelay implements UniformEventSink {

    private volatile UniformEventSink target;

    public void retarget(UniformEventSink sink) {
        this.target = sink;
    }

    public void clear() {
        this.target = null;
    }

    private UniformEventSink t() {
        return target;
    }

    @Override
    public void captureGbufferMatrices(long frameId, Matrix4Value modelView, Matrix4Value projection) {
        UniformEventSink s = t();
        if (s != null) {
            s.captureGbufferMatrices(frameId, modelView, projection);
        }
    }

    @Override
    public void updateCelestial(CelestialSample sample) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateCelestial(sample);
        }
    }

    @Override
    public void updateShadowMatrices(ShadowMatrixSample sample) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateShadowMatrices(sample);
        }
    }

    @Override
    public void updateFog(FogSample sample) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateFog(sample);
        }
    }

    @Override
    public void updateBlend(BlendSample sample) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateBlend(sample);
        }
    }

    @Override
    public void updateEntityColor(Float4 value) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateEntityColor(value);
        }
    }

    @Override
    public void updateEntityId(int value) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateEntityId(value);
        }
    }

    @Override
    public void updateBlockEntityId(int value) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateBlockEntityId(value);
        }
    }

    @Override
    public void updateInstanceId(int value) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateInstanceId(value);
        }
    }

    @Override
    public void updateAtlasSize(Int2 value) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateAtlasSize(value);
        }
    }

    @Override
    public void updateHeldItems(HeldItemSample value) {
        UniformEventSink s = t();
        if (s != null) {
            s.updateHeldItems(value);
        }
    }
}
