// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.BlockStateRecord;
import com.schmaloogium.engine.config.id.BlockTypeRecord;
import com.schmaloogium.engine.config.id.EntityTypeRecord;
import com.schmaloogium.engine.config.id.IdRegistryFingerprint;
import com.schmaloogium.engine.config.id.IdRegistrySnapshot;
import com.schmaloogium.engine.config.id.ItemTypeRecord;
import com.schmaloogium.engine.config.id.RegistryName;
import com.schmaloogium.engine.config.id.TagMembershipSnapshot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The pure ordinal assignment behind {@link ForgeIdSnapshotProvider} (PHASE_9_DOC §2.3,
 * §4.2): blocks sorted by registry name, each block's states by canonical property tuple
 * then legacy metadata, items and entities by name; ordinals are dense and snapshot-local.
 * The identities carried on the inputs are the canonical Minecraft objects the identity
 * maps are keyed by; this class never inspects them. Kept free of Minecraft types so the
 * ordering is unit-testable against the engine's own fixture algorithm.
 */
public final class RegistryProjection {

    /** One live block state. */
    public record StateInput(Object identity, int legacyMetadata, SortedMap<String, String> properties,
                             int renderType, boolean solidOpaqueCube, int emittedLight) {
    }

    /** One registered block with its valid states. */
    public record BlockInput(String namespace, String path, int liveNumericId, List<StateInput> states) {
    }

    /** One registered item; {@code placedBlockDefaultState} is the ItemBlock relation. */
    public record ItemInput(Object identity, String namespace, String path,
                            Optional<Object> placedBlockDefaultState) {
    }

    /** One registered entity type keyed by its entity class. */
    public record EntityInput(Object identity, String namespace, String path) {
    }

    /** The projected snapshot with its identity maps. */
    public record Projection(IdRegistrySnapshot snapshot, IdIdentityMaps maps) {
    }

    private RegistryProjection() {
    }

    /**
     * Projects the live inputs; throws {@code IllegalArgumentException} when the engine's
     * snapshot validation rejects them (the caller turns that into "IDs off").
     */
    public static Projection project(long registryGeneration, List<BlockInput> blocks,
                                     List<ItemInput> items, List<EntityInput> entities,
                                     TagMembershipSnapshot tags) {
        List<BlockInput> sortedBlocks = new ArrayList<>(blocks);
        sortedBlocks.sort(Comparator.comparing(b -> new RegistryName(b.namespace(), b.path())));
        List<BlockTypeRecord> blockRecords = new ArrayList<>();
        List<BlockStateRecord> stateRecords = new ArrayList<>();
        IdentityHashMap<Object, Integer> stateMap = new IdentityHashMap<>();
        IdentityHashMap<Object, Integer> itemMap = new IdentityHashMap<>();
        IdentityHashMap<Object, Integer> entityMap = new IdentityHashMap<>();
        int stateOrdinal = 0;
        for (int blockOrdinal = 0; blockOrdinal < sortedBlocks.size(); blockOrdinal++) {
            BlockInput block = sortedBlocks.get(blockOrdinal);
            // Exactly the engine's key (IdRegistrySnapshot.sortAndValidate): the canonical
            // property tuple, then metadata for the (impossible) tie.
            List<StateInput> sortedStates = new ArrayList<>(block.states());
            sortedStates.sort(Comparator
                    .comparing((StateInput s) -> canonicalTuple(s.properties()))
                    .thenComparingInt(StateInput::legacyMetadata));
            List<Integer> ordinals = new ArrayList<>();
            for (StateInput state : sortedStates) {
                stateRecords.add(new BlockStateRecord(stateOrdinal, blockOrdinal,
                        state.legacyMetadata(), new TreeMap<>(state.properties()),
                        state.renderType(), state.solidOpaqueCube(), state.emittedLight()));
                if (state.identity() != null) {
                    stateMap.put(state.identity(), stateOrdinal);
                }
                ordinals.add(stateOrdinal);
                stateOrdinal++;
            }
            blockRecords.add(new BlockTypeRecord(blockOrdinal,
                    new RegistryName(block.namespace(), block.path()), block.liveNumericId(), ordinals));
        }
        List<ItemInput> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparing(i -> new RegistryName(i.namespace(), i.path())));
        List<ItemTypeRecord> itemRecords = new ArrayList<>();
        for (int itemOrdinal = 0; itemOrdinal < sortedItems.size(); itemOrdinal++) {
            ItemInput item = sortedItems.get(itemOrdinal);
            OptionalInt placed = OptionalInt.empty();
            if (item.placedBlockDefaultState().isPresent()) {
                Integer ordinal = stateMap.get(item.placedBlockDefaultState().get());
                if (ordinal != null) {
                    placed = OptionalInt.of(ordinal);
                }
            }
            itemRecords.add(new ItemTypeRecord(itemOrdinal,
                    new RegistryName(item.namespace(), item.path()), placed));
            if (item.identity() != null) {
                itemMap.put(item.identity(), itemOrdinal);
            }
        }
        List<EntityInput> sortedEntities = new ArrayList<>(entities);
        sortedEntities.sort(Comparator.comparing(e -> new RegistryName(e.namespace(), e.path())));
        List<EntityTypeRecord> entityRecords = new ArrayList<>();
        for (int i = 0; i < sortedEntities.size(); i++) {
            EntityInput entity = sortedEntities.get(i);
            entityRecords.add(new EntityTypeRecord(i, new RegistryName(entity.namespace(), entity.path())));
            if (entity.identity() != null) {
                entityMap.put(entity.identity(), i);
            }
        }
        IdRegistryFingerprint fingerprint = new IdRegistryFingerprint(
                fingerprint(blockRecords, stateRecords, itemRecords, entityRecords));
        IdRegistrySnapshot snapshot = new IdRegistrySnapshot(registryGeneration, fingerprint,
                blockRecords, stateRecords, itemRecords, entityRecords, tags);
        return new Projection(snapshot, new IdIdentityMaps(stateMap, itemMap, entityMap));
    }

    private static String canonicalTuple(SortedMap<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        for (var e : new TreeMap<>(properties).entrySet()) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        return sb.toString();
    }

    private static String fingerprint(List<BlockTypeRecord> blocks, List<BlockStateRecord> states,
                                      List<ItemTypeRecord> items, List<EntityTypeRecord> entities) {
        StringBuilder sb = new StringBuilder();
        for (BlockTypeRecord b : blocks) {
            sb.append("b:").append(b.name()).append(':').append(b.liveLegacyNumericId()).append('\n');
        }
        for (BlockStateRecord s : states) {
            sb.append("s:").append(s.blockOrdinal()).append(':').append(s.legacyMetadata()).append(':')
                    .append(s.canonicalPropertyTuple()).append(':').append(s.renderType()).append(':')
                    .append(s.solidOpaqueCube()).append(':').append(s.emittedLight()).append('\n');
        }
        for (ItemTypeRecord i : items) {
            sb.append("i:").append(i.name()).append(':')
                    .append(i.placedBlockDefaultStateOrdinal().orElse(-1)).append('\n');
        }
        for (EntityTypeRecord e : entities) {
            sb.append("e:").append(e.name()).append('\n');
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
