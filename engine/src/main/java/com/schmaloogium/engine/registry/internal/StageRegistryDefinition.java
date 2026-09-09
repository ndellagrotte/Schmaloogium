// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassIndex;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramSlotKind;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Validates and compiles a stage-registry definition (PHASE_4_DOC §4.1). All nine
 * {@code StageId} identities exist exactly once per schedule (GBUFFERS twice); array
 * populations use 0…99 without duplicate index; descriptor stages/bands match their
 * containing occurrence; FINAL is singleton and last; SETUP sits outside the per-frame
 * schedule under LOAD_OR_RESIZE; sparse virtual-prelude membership obeys §2.2/D-P4-40;
 * compute slots are descriptors on non-gbuffers passes only. Invalid definitions reject
 * during construction, before any publication.
 */
public final class StageRegistryDefinition {

    /** Compute-slot admission: v0.1 keeps them dormant as descriptor sets only. */
    public enum ComputePolicy {
        /** Descriptors may carry compute slot sets; no semantics exist yet (G8/S2). */
        DORMANT_SLOTS_ONLY
    }

    private StageRegistryDefinition() {
    }

    /** Compile and validate one definition; returns the immutable registry. */
    public static StageRegistry compile(
            List<ProgramSlotDescriptor> slots,
            List<StageStep> schedule,
            ComputePolicy computePolicy,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor,
            Function<ProgramSlotId, Set<BufferRef>> mipmappedBeforeReadFor) {
        java.util.Objects.requireNonNull(computePolicy, "computePolicy");
        Catalog catalog = validateCatalog(slots);
        validateScheduleShape(schedule);
        Map<StageStep, StepData> steps = new LinkedHashMap<>();
        for (StageStep step : schedule) {
            steps.put(step, buildStep(step, catalog, computePolicy,
                explicitFlipsFor, mipmappedBeforeReadFor));
        }
        return new StageRegistryImpl(List.copyOf(schedule), Map.copyOf(steps));
    }

    // ------------------------------------------------------------------ catalog

    private static Catalog validateCatalog(List<ProgramSlotDescriptor> slots) {
        Map<ProgramSlotId, ProgramSlotDescriptor> byId = new LinkedHashMap<>();
        Set<String> stems = new LinkedHashSet<>();
        for (ProgramSlotDescriptor slot : slots) {
            if (byId.putIfAbsent(slot.id(), slot) != null) {
                throw new IllegalArgumentException("duplicate catalog slot: " + slot.id());
            }
            if (slot.kind() == ProgramSlotKind.RASTER) {
                if (slot.sourceStem().isEmpty()) {
                    throw new IllegalArgumentException("raster slot without source stem: "
                        + slot.id());
                }
                if (!stems.add(slot.sourceStem().get())) {
                    throw new IllegalArgumentException("duplicate source stem: "
                        + slot.sourceStem().get());
                }
                if (!slot.sourceStem().get().equals(slot.id().packName())) {
                    throw new IllegalArgumentException("source stem must equal the slot name: "
                        + slot.id());
                }
            } else {
                if (slot.sourceStem().isPresent() || slot.fallback().isPresent()) {
                    throw new IllegalArgumentException("non-raster slot with stem/fallback: "
                        + slot.id());
                }
            }
        }
        for (ProgramSlotDescriptor slot : slots) {
            slot.fallback().ifPresent(parent -> {
                if (!byId.containsKey(parent)) {
                    throw new IllegalArgumentException("unknown fallback parent for "
                        + slot.id() + ": " + parent);
                }
                if (parent.equals(slot.id())) {
                    throw new IllegalArgumentException("self fallback: " + slot.id());
                }
            });
        }
        // A cycle is a catalog construction error, never a pack error (§4.6).
        for (ProgramSlotDescriptor slot : slots) {
            Set<ProgramSlotId> seen = new LinkedHashSet<>();
            ProgramSlotId cursor = slot.id();
            while (cursor != null) {
                if (!seen.add(cursor)) {
                    throw new IllegalArgumentException("fallback cycle at: " + cursor);
                }
                ProgramSlotDescriptor current = byId.get(cursor);
                cursor = current == null ? null : current.fallback().orElse(null);
            }
        }
        return new Catalog(byId);
    }

    // ----------------------------------------------------------------- schedule

    private static void validateScheduleShape(List<StageStep> schedule) {
        Map<StageId, Integer> occurrences = new LinkedHashMap<>();
        for (StageStep step : schedule) {
            occurrences.merge(step.stage(), 1, Integer::sum);
            if (step.stage() == StageId.SETUP && step.band() != StageBand.LOAD_OR_RESIZE) {
                throw new IllegalArgumentException("SETUP must use LOAD_OR_RESIZE");
            }
            if (step.stage() != StageId.SETUP && step.band() == StageBand.LOAD_OR_RESIZE) {
                throw new IllegalArgumentException("LOAD_OR_RESIZE is SETUP-only: " + step.stage());
            }
        }
        boolean fullShape = occurrences.containsKey(StageId.SETUP);
        for (StageId id : StageId.values()) {
            int count = occurrences.getOrDefault(id, 0);
            if (fullShape) {
                // All nine identities exactly once; GBUFFERS is the one permitted twice.
                if (count != 1 && !(id == StageId.GBUFFERS && count == 2)) {
                    throw new IllegalArgumentException(
                        "stage identity " + id + " occurs " + count + " times");
                }
            } else if (id == StageId.GBUFFERS) {
                if (count != 0 && count != 2) {
                    throw new IllegalArgumentException(
                        "classic GBUFFERS occurs " + count + " times");
                }
            } else if (count > 1) {
                throw new IllegalArgumentException(
                    "stage identity " + id + " occurs " + count + " times");
            }
        }
        if (!schedule.isEmpty()) {
            StageStep first = schedule.get(0);
            if (fullShape && first.stage() != StageId.SETUP) {
                throw new IllegalArgumentException("SETUP must precede the frame schedule");
            }
            StageStep last = schedule.get(schedule.size() - 1);
            if (last.stage() != StageId.FINAL) {
                throw new IllegalArgumentException("FINAL must be last");
            }
            if (last.population() instanceof PassPopulation.Singleton) {
                // legal
            } else {
                throw new IllegalArgumentException("FINAL must be a Singleton step");
            }
        }
        for (StageStep step : schedule) {
            StageBand expected = switch (step.stage()) {
                case SETUP -> StageBand.LOAD_OR_RESIZE;
                case BEGIN -> StageBand.FRAME_BEGIN;
                case SHADOW -> StageBand.SHADOW;
                case SHADOWCOMP -> StageBand.AFTER_SHADOW;
                case PREPARE -> StageBand.BEFORE_GBUFFERS;
                case GBUFFERS -> step.band() == StageBand.GBUFFERS_OPAQUE
                    || step.band() == StageBand.GBUFFERS_TRANSLUCENT ? step.band() : null;
                case DEFERRED -> StageBand.BETWEEN_GBUFFERS;
                case COMPOSITE -> StageBand.FRAME_END;
                case FINAL -> StageBand.SCREEN;
            };
            if (expected != step.band()) {
                throw new IllegalArgumentException(
                    "wrong band " + step.band() + " for stage " + step.stage());
            }
        }
    }

    // -------------------------------------------------------------------- steps

    private static StepData buildStep(StageStep step, Catalog catalog,
            ComputePolicy computePolicy,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor,
            Function<ProgramSlotId, Set<BufferRef>> mipmappedBeforeReadFor) {
        if (step.population() instanceof PassPopulation.Singleton population) {
            return singletonStep(step, catalog, explicitFlipsFor, mipmappedBeforeReadFor);
        }
        if (step.population() instanceof PassPopulation.NamedPrograms population) {
            return namedStep(step, catalog, population, explicitFlipsFor,
                mipmappedBeforeReadFor);
        }
        return sparseStep(step, catalog, (PassPopulation.SparseArray) step.population(),
            explicitFlipsFor, mipmappedBeforeReadFor);
    }

    private static StepData singletonStep(StageStep step, Catalog catalog,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor,
            Function<ProgramSlotId, Set<BufferRef>> mipmappedBeforeReadFor) {
        if (step.stage() != StageId.FINAL) {
            throw new IllegalArgumentException("Singleton population is FINAL-only");
        }
        List<PassDescriptor> passes = new ArrayList<>();
        // Exactly one descriptor: `final`. Derived from the catalog rows of this stage.
        List<ProgramSlotDescriptor> stageRows = catalog.rowsOf(step.stage());
        if (stageRows.size() != 1) {
            throw new IllegalArgumentException("FINAL requires exactly one catalog slot");
        }
        ProgramSlotDescriptor slot = stageRows.get(0);
        requireRasterForStep(slot, step);
        passes.add(descriptor(step, slot, Optional.empty(), catalog, explicitFlipsFor,
            mipmappedBeforeReadFor));
        return new StepData(step, List.copyOf(passes), namedIndex(passes),
            Map.of(), null);
    }

    private static StepData namedStep(StageStep step, Catalog catalog,
            PassPopulation.NamedPrograms population,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor,
            Function<ProgramSlotId, Set<BufferRef>> mipmappedBeforeReadFor) {
        List<ProgramSlotDescriptor> stageRows = catalog.rowsOf(step.stage());
        List<ProgramSlotId> declared = stageRows.stream()
            .filter(s -> s.kind() == ProgramSlotKind.RASTER)
            .filter(s -> s.permittedBands().contains(step.band()))
            .map(ProgramSlotDescriptor::id)
            .toList();
        if (!declared.equals(population.slots())) {
            throw new IllegalArgumentException(
                "named population must list this stage's catalog-order slots for band "
                    + step.band());
        }
        List<PassDescriptor> passes = new ArrayList<>();
        for (ProgramSlotDescriptor slot : stageRows) {
            if (slot.kind() != ProgramSlotKind.RASTER
                    || !slot.permittedBands().contains(step.band())) {
                continue;
            }
            requireRasterForStep(slot, step);
            passes.add(descriptor(step, slot, Optional.empty(), catalog, explicitFlipsFor,
                mipmappedBeforeReadFor));
        }
        return new StepData(step, List.copyOf(passes), namedIndex(passes), Map.of(), null);
    }

    private static StepData sparseStep(StageStep step, Catalog catalog,
            PassPopulation.SparseArray population,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor,
            Function<ProgramSlotId, Set<BufferRef>> mipmappedBeforeReadFor) {
        ProgramSlotId expectedPrelude = expectedPrelude(step);
        boolean preludeLegal = expectedPrelude != null;
        Optional<ProgramSlotId> declaredPrelude = population.virtualPrelude();
        if (declaredPrelude.isPresent()) {
            if (!preludeLegal || !declaredPrelude.get().equals(expectedPrelude)) {
                throw new IllegalArgumentException("wrong prelude for step " + step);
            }
        } else if (preludeLegal && catalog.requiresPrelude(step.stage())) {
            throw new IllegalArgumentException("missing required prelude for " + step.stage());
        }
        List<PassDescriptor> passes = new ArrayList<>();
        Map<Integer, PassDescriptor> indexed = new HashMap<>();
        PassDescriptor prelude = null;
        if (declaredPrelude.isPresent()) {
            ProgramSlotDescriptor slot = catalog.byIndex(declaredPrelude.get());
            if (slot == null) {
                throw new IllegalArgumentException(
                    "prelude slot missing from catalog: " + declaredPrelude.get());
            }
            if (slot.kind() != ProgramSlotKind.VIRTUAL_FLIP_CONTROL
                    || !slot.stage().equals(step.stage())
                    || slot.sourceStem().isPresent() || slot.fallback().isPresent()) {
                throw new IllegalArgumentException("illegal prelude slot: " + slot.id());
            }
            prelude = virtualDescriptor(step, slot, explicitFlipsFor);
            passes.add(prelude);
        }
        int maxIndex = -1;
        for (ProgramSlotDescriptor slot : catalog.rowsOf(step.stage())) {
            if (slot.kind() == ProgramSlotKind.VIRTUAL_FLIP_CONTROL) {
                if (declaredPrelude.isEmpty() || !slot.id().equals(declaredPrelude.get())) {
                    throw new IllegalArgumentException(
                        "virtual slot outside the prelude position: " + slot.id());
                }
                continue;
            }
            if (slot.kind() != ProgramSlotKind.RASTER) {
                throw new IllegalArgumentException("non-raster indexed slot: " + slot.id());
            }
            PassIndex index = familyIndex(step, slot);
            if (index.value() > population.highestLegalIndex()) {
                throw new IllegalArgumentException("index above legal bound: " + slot.id());
            }
            if (indexed.putIfAbsent(index.value(), null) != null) {
                throw new IllegalArgumentException("duplicate sparse index: " + index.value());
            }
            maxIndex = Math.max(maxIndex, index.value());
            PassDescriptor descriptor = descriptor(step, slot, Optional.of(index), catalog,
                explicitFlipsFor, mipmappedBeforeReadFor);
            indexed.put(index.value(), descriptor);
        }
        if (maxIndex != population.highestPopulatedIndex()) {
            throw new IllegalArgumentException("inconsistent highestPopulatedIndex for step "
                + step + ": declared " + population.highestPopulatedIndex()
                + ", actual " + maxIndex);
        }
        List<PassDescriptor> ordered = new ArrayList<>();
        if (prelude != null) {
            ordered.add(prelude);
        }
        List<Integer> present = new ArrayList<>(indexed.keySet());
        java.util.Collections.sort(present);
        for (int i : present) {
            ordered.add(indexed.get(i));
        }
        return new StepData(step, List.copyOf(ordered), namedIndexForPrelude(prelude),
            Map.copyOf(indexed), prelude);
    }

    private static void requireRasterForStep(ProgramSlotDescriptor slot, StageStep step) {
        if (slot.kind() != ProgramSlotKind.RASTER) {
            throw new IllegalArgumentException("non-raster slot in " + step);
        }
        if (!slot.stage().equals(step.stage())) {
            throw new IllegalArgumentException("descriptor stage " + slot.stage()
                + " does not match containing identity " + step.stage());
        }
        if (!slot.permittedBands().contains(step.band())) {
            throw new IllegalArgumentException("slot " + slot.id()
                + " not permitted in band " + step.band());
        }
    }

    private static PassDescriptor descriptor(StageStep step, ProgramSlotDescriptor slot,
            Optional<PassIndex> index, Catalog catalog,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor,
            Function<ProgramSlotId, Set<BufferRef>> mipmappedBeforeReadFor) {
        Set<BufferRef> readable;
        Set<BufferRef> writes;
        if (step.stage() == StageId.DEFERRED || step.stage() == StageId.COMPOSITE) {
            readable = colortexRange();
            writes = index.map(i -> Set.of(new BufferRef(BufferDomain.COLORTEX, i.value())))
                .orElse(Set.of());
        } else if (step.stage() == StageId.FINAL) {
            readable = colortexRange();
            writes = Set.of();
        } else {
            readable = Set.of();
            writes = Set.of();
        }
        return new PassDescriptor(
            step,
            slot.id(),
            index,
            new PassResourceAccess(readable, writes,
                Map.copyOf(explicitFlipsFor.apply(slot.id())),
                Set.copyOf(mipmappedBeforeReadFor.apply(slot.id()))),
            Set.of());
    }

    private static PassDescriptor virtualDescriptor(StageStep step,
            ProgramSlotDescriptor slot,
            Function<ProgramSlotId, Map<BufferRef, Boolean>> explicitFlipsFor) {
        return new PassDescriptor(
            step,
            slot.id(),
            Optional.empty(),
            new PassResourceAccess(Set.of(), Set.of(),
                Map.copyOf(explicitFlipsFor.apply(slot.id())), Set.of()),
            Set.of());
    }

    private static Set<BufferRef> colortexRange() {
        Set<BufferRef> refs = new LinkedHashSet<>();
        for (int i = 0; i <= 15; i++) {
            refs.add(new BufferRef(BufferDomain.COLORTEX, i));
        }
        return refs;
    }

    /** The stage-specific exact prelude key, or null when this sparse stage has none. */
    public static ProgramSlotId expectedPrelude(StageStep step) {
        if (step.stage() == StageId.DEFERRED && step.band() == StageBand.BETWEEN_GBUFFERS) {
            return ClassicProgramCatalog.DEFERRED_PRE;
        }
        if (step.stage() == StageId.COMPOSITE && step.band() == StageBand.FRAME_END) {
            return ClassicProgramCatalog.COMPOSITE_PRE;
        }
        return null;
    }

    private static PassIndex familyIndex(StageStep step, ProgramSlotDescriptor slot) {
        int index = familyIndexOf(step.stage(), slot.id().packName());
        if (index < 0) {
            throw new IllegalArgumentException("slot outside the family naming scheme: "
                + slot.id());
        }
        return new PassIndex(index);
    }

    /** The §4.3 naming function's inverse for one sparse family; -1 when foreign. */
    public static int familyIndexOf(StageId stage, String packName) {
        String stem = switch (stage) {
            case SETUP -> "setup";
            case BEGIN -> "begin";
            case SHADOWCOMP -> "shadowcomp";
            case PREPARE -> "prepare";
            case DEFERRED -> "deferred";
            case COMPOSITE -> "composite";
            default -> null;
        };
        if (stem == null) {
            return -1;
        }
        if (packName.equals(stem)) {
            return 0;
        }
        if (!packName.startsWith(stem)) {
            return -1;
        }
        String suffix = packName.substring(stem.length());
        if (suffix.isEmpty() || !suffix.chars().allMatch(c -> c >= '0' && c <= '9')) {
            return -1;
        }
        if (suffix.length() > 1 && suffix.charAt(0) == '0') {
            return -1;
        }
        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Map<ProgramSlotId, PassDescriptor> namedIndex(List<PassDescriptor> passes) {
        Map<ProgramSlotId, PassDescriptor> byName = new LinkedHashMap<>();
        for (PassDescriptor pass : passes) {
            byName.put(pass.slot(), pass);
        }
        return Map.copyOf(byName);
    }

    private static Map<ProgramSlotId, PassDescriptor> namedIndexForPrelude(
            PassDescriptor prelude) {
        return prelude == null ? Map.of() : Map.of(prelude.slot(), prelude);
    }

    private record Catalog(Map<ProgramSlotId, ProgramSlotDescriptor> byId) {

        List<ProgramSlotDescriptor> rowsOf(StageId stage) {
            List<ProgramSlotDescriptor> rows = new ArrayList<>();
            for (ProgramSlotDescriptor slot : byId.values()) {
                if (slot.stage() == stage) {
                    rows.add(slot);
                }
            }
            return List.copyOf(rows);
        }

        ProgramSlotDescriptor byIndex(ProgramSlotId id) {
            return byId.get(id);
        }

        boolean requiresPrelude(StageId stage) {
            return stage == StageId.DEFERRED || stage == StageId.COMPOSITE;
        }
    }

    static final class StepData {
        final StageStep step;
        final List<PassDescriptor> passes;
        final Map<ProgramSlotId, PassDescriptor> named;
        final Map<Integer, PassDescriptor> indexed;
        final PassDescriptor prelude;

        StepData(StageStep step, List<PassDescriptor> passes,
                Map<ProgramSlotId, PassDescriptor> named,
                Map<Integer, PassDescriptor> indexed, PassDescriptor prelude) {
            this.step = step;
            this.passes = passes;
            this.named = named;
            this.indexed = indexed;
            this.prelude = prelude;
        }
    }
}
