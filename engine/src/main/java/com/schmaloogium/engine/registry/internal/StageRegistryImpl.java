// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassIndex;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The immutable compiled schedule (PHASE_4_DOC §4.1): every lookup returns the identical
 * contained descriptor; foreign steps, illegal keys and wrong lookup kinds reject at the
 * boundary instead of converting into absence.
 */
public final class StageRegistryImpl implements StageRegistry {

    private final List<StageStep> schedule;
    private final Map<StageStep, StageRegistryDefinition.StepData> steps;

    StageRegistryImpl(List<StageStep> schedule,
            Map<StageStep, StageRegistryDefinition.StepData> steps) {
        this.schedule = schedule;
        this.steps = steps;
    }

    @Override
    public List<StageStep> schedule() {
        return schedule;
    }

    @Override
    public List<PassDescriptor> passes(StageStep step) {
        return data(step).passes;
    }

    @Override
    public Optional<PassDescriptor> named(StageStep step, ProgramSlotId id) {
        java.util.Objects.requireNonNull(id, "id");
        StageRegistryDefinition.StepData data = data(step);
        if (data.step.population() instanceof com.schmaloogium.engine.registry.PassPopulation.SparseArray) {
            ProgramSlotId preludeKey = StageRegistryDefinition.expectedPrelude(step);
            if (preludeKey == null || !id.equals(preludeKey)) {
                throw new IllegalArgumentException(
                    "named lookup on a sparse step accepts only the exact prelude key: " + id);
            }
            return Optional.ofNullable(data.prelude);
        }
        if (data.step.population() instanceof com.schmaloogium.engine.registry.PassPopulation.Singleton) {
            throw new IllegalArgumentException("Singleton steps have no named lookup");
        }
        com.schmaloogium.engine.registry.PassDescriptor found = data.named.get(id);
        if (found == null) {
            throw new IllegalArgumentException("undeclared named slot for step: " + id);
        }
        return Optional.of(found);
    }

    @Override
    public Optional<PassDescriptor> indexed(StageStep step, PassIndex index) {
        java.util.Objects.requireNonNull(index, "index");
        StageRegistryDefinition.StepData data = data(step);
        if (!(data.step.population() instanceof com.schmaloogium.engine.registry.PassPopulation.SparseArray)) {
            throw new IllegalArgumentException("indexed lookup requires a SparseArray step");
        }
        return Optional.ofNullable(data.indexed.get(index.value()));
    }

    @Override
    public boolean stepExists(StageStep step) {
        return step != null && steps.containsKey(step);
    }

    private StageRegistryDefinition.StepData data(StageStep step) {
        java.util.Objects.requireNonNull(step, "step");
        StageRegistryDefinition.StepData data = steps.get(step);
        if (data == null) {
            throw new IllegalArgumentException("foreign schedule step");
        }
        return data;
    }
}
