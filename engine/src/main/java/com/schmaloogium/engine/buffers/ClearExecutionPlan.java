// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.TextureHandle;

import java.util.List;
import java.util.Objects;

/**
 * The immutable typed-clear plan (PHASE_5_DOC §4.6): estate generation, attachment epoch,
 * frame token, and the ordered converted batches grouped by equal extent + converted typed
 * value + physical side. P5-owned; Phase 7 executes it verbatim via
 * {@link BufferEstateView#executeClear(ClearExecutionPlan)} on the render thread. It is an
 * opaque final value: equality covers all carried artifacts.
 */
public final class ClearExecutionPlan {

    /** One ordered batch: physical side textures cleared at dense route positions. */
    public static final class Batch {
        private final List<ColorClearValue> values;
        private final List<TextureHandle> textures;

        public Batch(List<ColorClearValue> values, List<TextureHandle> textures) {
            this.values = List.copyOf(values);
            this.textures = List.copyOf(textures);
        }

        /** One converted typed value per occupied route position, ascending. */
        public List<ColorClearValue> values() {
            return values;
        }

        public List<TextureHandle> textures() {
            return textures;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Batch other && values.equals(other.values)
                && textures.equals(other.textures);
        }

        @Override
        public int hashCode() {
            return values.hashCode() * 31 + textures.hashCode();
        }
    }

    private final long estateGeneration;
    private final long depthAttachmentEpoch;
    private final long frameId;
    private final List<Batch> batches;

    public ClearExecutionPlan(long estateGeneration, long depthAttachmentEpoch,
            long frameId, List<Batch> batches) {
        this.estateGeneration = estateGeneration;
        this.depthAttachmentEpoch = depthAttachmentEpoch;
        this.frameId = frameId;
        this.batches = List.copyOf(batches);
    }

    public long estateGeneration() {
        return estateGeneration;
    }

    public long depthAttachmentEpoch() {
        return depthAttachmentEpoch;
    }

    public long frameId() {
        return frameId;
    }

    /** The ordered clear batches; empty when nothing requires clearing. */
    public List<Batch> batches() {
        return batches;
    }

    /** True when the plan carries no clear work. */
    public boolean isEmpty() {
        return batches.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClearExecutionPlan other
            && estateGeneration == other.estateGeneration
            && depthAttachmentEpoch == other.depthAttachmentEpoch
            && frameId == other.frameId
            && batches.equals(other.batches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estateGeneration, depthAttachmentEpoch, frameId, batches);
    }

    @Override
    public String toString() {
        return "ClearExecutionPlan[generation=" + estateGeneration + ", epoch="
            + depthAttachmentEpoch + ", frame=" + frameId + ", batches=" + batches.size()
            + "]";
    }
}
