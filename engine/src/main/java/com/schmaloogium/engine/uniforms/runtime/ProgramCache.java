// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramUniformLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Per-activation uniform state, keyed by {@link ProgramUniformCacheKey} across
 * re-activations of the same effective provider (PHASE_6_DOC §4.3/§4.9): located
 * uniform locations (including absent markers), last-uploaded canonical values, disabled
 * scopes kept by name, consecutive-clean-unattributed-drain recurrence, and sampler
 * plans reused by {@link SamplerPlanKey}. Immutable snapshots in/out; render-thread
 * confined by the runtime. All contents are discarded on generation adoption and at
 * retirement; enabled/disabled and per-program counters reset per reset scope.
 */
public final class ProgramCache {

    private final ProgramUniformCacheKey key;
    private final ProgramUniformLayout layout;
    private final Map<String, UniformLocation> locations = new HashMap<>();
    private final Map<String, UniformValue> lastUploaded = new HashMap<>();
    private final Map<String, Boolean> disabled = new HashMap<>();
    private final Map<SamplerPlanKey, SamplerPlan> samplerPlans = new HashMap<>();
    private int consecutiveCleanUnattributed;

    ProgramCache(ProgramUniformCacheKey key, ProgramUniformLayout layout) {
        this.key = key;
        this.layout = layout;
    }

    public ProgramUniformCacheKey key() {
        return key;
    }

    public ProgramUniformLayout layout() {
        return layout;
    }

    /**
     * Memoized location lookup: exactly one facade {@code locate} call per exact name
     * for the lifetime of this cache (PHASE_6_DOC §4.3/§4.10); absent results are
     * cached as absent markers and never re-queried.
     */
    public UniformLocation location(String name, Function<String, UniformLocation> locate) {
        return locations.computeIfAbsent(name, locate);
    }

    /** The cached location without triggering a lookup, or null when never located. */
    public UniformLocation peekLocation(String name) {
        return locations.get(name);
    }

    public UniformValue lastUploaded(String name) {
        return lastUploaded.get(name);
    }

    public void noteUploaded(String name, UniformValue value) {
        lastUploaded.put(name, value);
    }

    public boolean isDisabled(String name) {
        return disabled.getOrDefault(name, Boolean.FALSE);
    }

    /** Disables one exact uniform name for this cache's remaining lifetime. */
    public void disable(String name) {
        disabled.put(name, Boolean.TRUE);
    }

    public int consecutiveCleanUnattributed() {
        return consecutiveCleanUnattributed;
    }

    public void resetRecurrence() {
        consecutiveCleanUnattributed = 0;
    }

    public void incrementRecurrence() {
        consecutiveCleanUnattributed++;
    }

    public SamplerPlan samplerPlan(SamplerPlanKey planKey) {
        return samplerPlans.get(planKey);
    }

    public void noteSamplerPlan(SamplerPlanKey planKey, SamplerPlan plan) {
        samplerPlans.putIfAbsent(planKey, plan);
    }

    /** True when this cache holds no located names, uploads or plans (verification). */
    public boolean isEmpty() {
        return locations.isEmpty() && lastUploaded.isEmpty() && samplerPlans.isEmpty();
    }

    /** The "same value already uploaded" test used to skip redundant commands. */
    public boolean lastUploadedEquals(String name, UniformValue value) {
        UniformValue last = lastUploaded.get(name);
        return last != null && last.sameAs(value);
    }

}
