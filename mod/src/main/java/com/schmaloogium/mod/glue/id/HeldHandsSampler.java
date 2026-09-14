// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.HeldHandsValue;
import com.schmaloogium.engine.config.id.HeldStackValue;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Samples both hands of the view player into a {@link HeldHandsValue} (PHASE_9_DOC §4.11:
 * the item ordinal and the placed block's static light; the resolver applies the
 * old/normal hand-light policy). Unknown items are empty hands.
 */
public final class HeldHandsSampler {

    private HeldHandsSampler() {
    }

    public static HeldHandsValue sample(IdIdentityMaps maps, EntityPlayer player,
                                        long worldEpoch, long logicalTick) {
        if (player == null) {
            return new HeldHandsValue(worldEpoch, logicalTick,
                    HeldStackValue.emptyHand(), HeldStackValue.emptyHand());
        }
        return new HeldHandsValue(worldEpoch, logicalTick,
                stack(maps, player.getHeldItemMainhand()), stack(maps, player.getHeldItemOffhand()));
    }

    static HeldStackValue stack(IdIdentityMaps maps, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return HeldStackValue.emptyHand();
        }
        Item item = stack.getItem();
        int ordinal = maps.itemOrdinal(item);
        if (ordinal < 0) {
            return HeldStackValue.emptyHand();
        }
        int light = 0;
        if (item instanceof ItemBlock itemBlock) {
            light = Math.max(0, Math.min(15, itemBlock.getBlock().getDefaultState().getLightValue()));
        }
        return new HeldStackValue(false, ordinal, light);
    }
}
