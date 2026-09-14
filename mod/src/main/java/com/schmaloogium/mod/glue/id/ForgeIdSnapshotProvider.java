// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.TagMembershipSnapshot;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.schmaloogium.engine.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The live Forge registry adapter (PHASE_9_DOC §4.2, D-6): reads the block/item/entity
 * registries into {@link RegistryProjection} inputs and keeps the canonical objects in
 * the identity maps. The registry generation is a process-local counter bumped by every
 * {@code FMLModIdMappingEvent} (see {@code PipelineBootstrap}); the fingerprint is the
 * projection's own content digest, so a remap that changes nothing keeps the fingerprint.
 */
public final class ForgeIdSnapshotProvider {

    private static final Log LOG = Logs.channel(LogChannels.IDS);
    private static final AtomicLong GENERATION = new AtomicLong(1);

    private ForgeIdSnapshotProvider() {
    }

    /** Bumped on every id remap; the snapshot reports the value current at its capture. */
    public static long noteRegistryRemapped() {
        return GENERATION.incrementAndGet();
    }

    public static long registryGeneration() {
        return GENERATION.get();
    }

    /** The current live projection, or empty (logged once per cause) when it cannot be validated. */
    public static Optional<RegistryProjection.Projection> snapshot() {
        try {
            return Optional.of(RegistryProjection.project(GENERATION.get(),
                    blocks(), items(), entities(), TagMembershipSnapshot.empty()));
        } catch (RuntimeException failure) {
            LOG.warn("H9-IDS-00 registry snapshot rejected: {}", failure.toString());
            return Optional.empty();
        }
    }

    /** The registered entity type's ordinal for a live entity, or -1 (unknown types keep the prior id). */
    public static int entityOrdinal(IdIdentityMaps maps, Entity entity) {
        if (entity == null) {
            return -1;
        }
        int direct = maps.entityOrdinal(entity.getClass());
        if (direct >= 0) {
            return direct;
        }
        EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
        return entry == null ? -1 : maps.entityOrdinal(entry.getEntityClass());
    }

    private static List<RegistryProjection.BlockInput> blocks() {
        List<RegistryProjection.BlockInput> out = new ArrayList<>();
        for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            ResourceLocation name = block.getRegistryName();
            if (name == null) {
                continue;
            }
            List<RegistryProjection.StateInput> states = new ArrayList<>();
            for (IBlockState state : block.getBlockState().getValidStates()) {
                states.add(new RegistryProjection.StateInput(state,
                        block.getMetaFromState(state) & 0xF, properties(state),
                        state.getRenderType().ordinal(),
                        state.isFullCube() && state.isOpaqueCube(),
                        Math.max(0, Math.min(15, state.getLightValue()))));
            }
            out.add(new RegistryProjection.BlockInput(name.getNamespace(), name.getPath(),
                    Block.getIdFromBlock(block), states));
        }
        return out;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static SortedMap<String, String> properties(IBlockState state) {
        SortedMap<String, String> out = new TreeMap<>();
        for (Map.Entry<IProperty<?>, Comparable<?>> e : state.getProperties().entrySet()) {
            IProperty property = e.getKey();
            out.put(property.getName(), property.getName(e.getValue()));
        }
        return out;
    }

    private static List<RegistryProjection.ItemInput> items() {
        List<RegistryProjection.ItemInput> out = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            ResourceLocation name = item.getRegistryName();
            if (name == null) {
                continue;
            }
            Optional<Object> placed = item instanceof ItemBlock itemBlock
                    ? Optional.of(itemBlock.getBlock().getDefaultState()) : Optional.empty();
            out.add(new RegistryProjection.ItemInput(item, name.getNamespace(), name.getPath(), placed));
        }
        return out;
    }

    private static List<RegistryProjection.EntityInput> entities() {
        List<RegistryProjection.EntityInput> out = new ArrayList<>();
        for (EntityEntry entry : ForgeRegistries.ENTITIES.getValuesCollection()) {
            ResourceLocation name = entry.getRegistryName();
            if (name == null || entry.getEntityClass() == null) {
                continue;
            }
            out.add(new RegistryProjection.EntityInput(entry.getEntityClass(),
                    name.getNamespace(), name.getPath()));
        }
        return out;
    }
}
