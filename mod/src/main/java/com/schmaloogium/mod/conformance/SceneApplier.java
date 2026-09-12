// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

import java.util.List;
import java.util.Map;

/**
 * Applies {@code [world]} and {@code [client]} state (PHASE_2_DOC §4.3.2, §4.4): video
 * settings on the client, gamerules/time/weather/difficulty/gamemode/entities on the
 * integrated server's thread, exact poses on the client player (previous = current at sample
 * 0), held items on both sides. It never regenerates terrain and never echoes a pose back as
 * an observation — the grabber reads the actual pose at the hook.
 */
final class SceneApplier {

    private SceneApplier() {
    }

    static void applyClientSettings(Minecraft mc, CapturePlanReader plan) {
        var gs = mc.gameSettings;
        gs.fovSetting = (float) plan.decimal("client.fov");
        gs.gammaSetting = (float) plan.decimal("client.gamma");
        gs.renderDistanceChunks = (int) plan.integer("client.renderDistance");
        gs.guiScale = (int) plan.integer("client.guiScale");
        gs.mipmapLevels = (int) plan.integer("client.mipmapLevels");
        gs.particleSetting = (int) plan.integer("client.particles");
        gs.fancyGraphics = plan.bool("client.fancyGraphics");
        gs.clouds = (int) plan.integer("client.clouds");
        gs.ambientOcclusion = (int) plan.integer("client.ao");
        gs.hideGUI = plan.bool("client.hideGui");
        gs.viewBobbing = plan.bool("client.viewBobbing");
        gs.entityShadows = plan.bool("client.entityShadows");
        gs.smoothCamera = plan.bool("client.smoothCamera");
        gs.anaglyph = plan.bool("client.anaglyph");
        gs.enableVsync = plan.bool("client.vsync");
        gs.pauseOnLostFocus = plan.bool("client.pauseOnLostFocus");
        gs.showDebugInfo = false;
        gs.limitFramerate = 260; // "unlimited": no Display.sync throttling under the controlled clock
    }

    /** Runs on the server thread: post-load world state, never generation inputs. */
    static void applyWorldState(IntegratedServer server, CapturePlanReader plan, CapturePlanReader.Pose pose) {
        int dimension = (int) plan.integer("world.dimension");
        WorldServer world = server.getWorld(dimension);
        WorldInfo info = world.getWorldInfo();
        Map<String, String> rules = plan.namedMap("world.gamerules");
        for (Map.Entry<String, String> rule : rules.entrySet()) {
            info.getGameRulesInstance().setOrCreateGameRule(rule.getKey(), rule.getValue());
        }
        long time = plan.integer("world.time");
        // absolute day time pins time-of-day and moon phase; total time stays the save's own
        info.setWorldTime(time);
        String weather = plan.token("world.weather");
        int weatherTicks = (int) plan.integer("world.weatherTicks");
        info.setCleanWeatherTime(0);
        info.setRainTime(weatherTicks);
        info.setThunderTime(weatherTicks);
        info.setRaining(!weather.equals("clear"));
        info.setThundering(weather.equals("thunder"));
        server.setDifficultyForAllWorlds(difficulty(plan.token("world.difficulty")));
        GameType type = GameType.getByName(plan.token("world.gamemode"));
        server.getPlayerList().setGameType(type);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            player.setGameType(type);
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
            // the authoritative move: the server tracks chunks around its own player position
            player.connection.setPlayerLocation(pose.x(), pose.y(), pose.z(), (float) pose.yaw(), (float) pose.pitch());
        }
        // §4.4: entities that move are entities that break diffs. Pre-existing mobs (worldgen
        // passive spawns wander under their own AI) are removed before the scene's own NoAI
        // entities are summoned; the manifest still records the per-frame entity count.
        for (Entity existing : new java.util.ArrayList<>(world.loadedEntityList)) {
            if (existing instanceof net.minecraft.entity.EntityLiving) {
                existing.setDead();
            }
        }
        int entities = plan.count("world.entities");
        for (int i = 0; i < entities; i++) {
            String p = "world.entities." + i + ".";
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(plan.text(p + "type")), world);
            if (entity == null) {
                throw new IllegalStateException("unknown entity type " + plan.text(p + "type"));
            }
            String[] pos = plan.text(p + "pos").trim().split("\\s+");
            String nbt = plan.text(p + "nbt");
            if (!nbt.isEmpty()) {
                try {
                    NBTTagCompound full = entity.writeToNBT(new NBTTagCompound());
                    full.merge(JsonToNBT.getTagFromJson(nbt));
                    entity.readFromNBT(full);
                } catch (net.minecraft.nbt.NBTException e) {
                    throw new IllegalStateException("entity " + i + " nbt: " + e.getMessage());
                }
            }
            entity.setLocationAndAngles(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]),
                    Double.parseDouble(pos[2]), 0f, 0f);
            world.spawnEntity(entity);
        }
    }

    static EnumDifficulty difficulty(String name) {
        return switch (name) {
            case "peaceful" -> EnumDifficulty.PEACEFUL;
            case "easy" -> EnumDifficulty.EASY;
            case "normal" -> EnumDifficulty.NORMAL;
            case "hard" -> EnumDifficulty.HARD;
            default -> throw new IllegalArgumentException("difficulty " + name);
        };
    }

    static WorldSettings generationSettings(long seed, String worldType, boolean structures) {
        WorldType type = switch (worldType) {
            case "default" -> WorldType.DEFAULT;
            case "flat" -> WorldType.FLAT;
            case "largeBiomes" -> WorldType.LARGE_BIOMES;
            case "amplified" -> WorldType.AMPLIFIED;
            default -> throw new IllegalArgumentException("worldType " + worldType);
        };
        return new WorldSettings(seed, GameType.CREATIVE, structures, false, type).enableCommands();
    }

    /** Client thread: the exact current pose with the exact previous pose (§4.3.4). */
    static void installPose(EntityPlayerSP player, CapturePlanReader.Pose current, CapturePlanReader.Pose previous) {
        player.capabilities.allowFlying = true;
        player.capabilities.isFlying = true;
        player.motionX = 0;
        player.motionY = 0;
        player.motionZ = 0;
        player.setPositionAndRotation(current.x(), current.y(), current.z(), (float) current.yaw(),
                (float) current.pitch());
        player.prevPosX = previous.x();
        player.prevPosY = previous.y();
        player.prevPosZ = previous.z();
        player.lastTickPosX = previous.x();
        player.lastTickPosY = previous.y();
        player.lastTickPosZ = previous.z();
        player.prevRotationYaw = (float) previous.yaw();
        player.prevRotationPitch = (float) previous.pitch();
        player.prevRotationYawHead = (float) previous.yaw();
        player.rotationYawHead = (float) current.yaw();
    }

    /** Sets held items on the client player and on the matching server player. */
    static void installHeldItems(Minecraft mc, IntegratedServer server, String heldMain, String heldOff) {
        ItemStack main = stack(heldMain);
        ItemStack off = stack(heldOff);
        EntityPlayerSP player = mc.player;
        player.inventory.setInventorySlotContents(player.inventory.currentItem, main.copy());
        player.inventory.offHandInventory.set(0, off.copy());
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        server.addScheduledTask(() -> {
            for (EntityPlayerMP mp : players) {
                mp.inventory.setInventorySlotContents(mp.inventory.currentItem, main.copy());
                mp.inventory.offHandInventory.set(0, off.copy());
                mp.inventoryContainer.detectAndSendChanges();
            }
        });
    }

    private static ItemStack stack(String spec) {
        if (spec == null || spec.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = Item.getByNameOrId(spec);
        if (item == null) {
            throw new IllegalStateException("unknown item " + spec);
        }
        return new ItemStack(item);
    }
}
