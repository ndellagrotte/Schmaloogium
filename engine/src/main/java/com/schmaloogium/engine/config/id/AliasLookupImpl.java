// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * O(1) primitive-array alias reader over one publication (PHASE_9_DOC §4.10). Bounds
 * failures are protocol rejections that never index the arrays; use after runtime close
 * is likewise rejected.
 */
final class AliasLookupImpl implements AliasLookup {

    private final PublishedIdRuntimeImpl owner;
    private final long generation;
    private final int[] blockIds;
    private final boolean[] blockPresent;
    private final int[] itemIds;
    private final boolean[] itemPresent;
    private final int[] entityIds;
    private final boolean[] entityPresent;
    private final int[] renderType;
    private final int[] metadata;

    AliasLookupImpl(PublishedIdRuntimeImpl owner, long generation,
            IdResolutionTables tables) {
        this.owner = owner;
        this.generation = generation;
        this.blockIds = tables.blockIds;
        this.blockPresent = tables.blockAssigned;
        this.itemIds = tables.itemIds;
        this.itemPresent = tables.itemAssigned;
        this.entityIds = tables.entityIds;
        this.entityPresent = tables.entityAssigned;
        this.renderType = tables.renderType;
        this.metadata = tables.legacyMetadata;
    }

    @Override
    public long generation() {
        return generation;
    }

    @Override
    public AliasValue blockId(int blockStateOrdinal) {
        owner.checkOpen();
        requireStateOrdinal(blockStateOrdinal);
        return blockPresent[blockStateOrdinal]
                ? new AliasValue(true, blockIds[blockStateOrdinal])
                : AliasValue.absent();
    }

    @Override
    public AliasValue itemId(int itemOrdinal) {
        owner.checkOpen();
        requireIndex(itemOrdinal, itemIds.length, "item ordinal");
        return itemPresent[itemOrdinal]
                ? new AliasValue(true, itemIds[itemOrdinal])
                : AliasValue.absent();
    }

    @Override
    public AliasValue entityId(int entityTypeOrdinal) {
        owner.checkOpen();
        requireIndex(entityTypeOrdinal, entityIds.length, "entity ordinal");
        return entityPresent[entityTypeOrdinal]
                ? new AliasValue(true, entityIds[entityTypeOrdinal])
                : AliasValue.absent();
    }

    @Override
    public BlockStampResult mcEntity(int blockStateOrdinal) {
        owner.checkOpen();
        requireStateOrdinal(blockStateOrdinal);
        int highType = renderType[blockStateOrdinal] & 0xffff;
        int packedZeroLow = highType << 16;
        int meta = metadata[blockStateOrdinal] & 0xffff;
        if (!blockPresent[blockStateOrdinal]) {
            return new BlockStampResult.Absent(packedZeroLow, meta);
        }
        int id = blockIds[blockStateOrdinal];
        if (id < IdRuntimeBuilderImpl.MIN_REPRESENTABLE_ALIAS
                || id > IdRuntimeBuilderImpl.MAX_REPRESENTABLE_ALIAS) {
            return new BlockStampResult.Unrepresentable(packedZeroLow, meta);
        }
        return new BlockStampResult.Present(packedZeroLow | (id & 0xffff), meta);
    }

    private void requireStateOrdinal(int ordinal) {
        requireIndex(ordinal, blockIds.length, "state ordinal");
    }

    private static void requireIndex(int ordinal, int length, String label) {
        if (ordinal < 0 || ordinal >= length) {
            throw new IndexOutOfBoundsException(
                    "protocol rejection: " + label + " " + ordinal + " outside 0.." + length);
        }
    }
}
