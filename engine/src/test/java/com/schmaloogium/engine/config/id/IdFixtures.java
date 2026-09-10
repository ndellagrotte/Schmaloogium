// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.IdMappingParseRequest;
import com.schmaloogium.engine.config.IdMappingParserImpl;
import com.schmaloogium.engine.config.MacroDefinition;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.ModMappingOrigin;
import com.schmaloogium.engine.config.PackMappingOrigin;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.pack.ImmutableBytes;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackIdentity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;

/**
 * Scripted registry and parser fixtures for the headless Phase 9 suite
 * (PHASE_9_DOC §8.1). The builder mirrors the glue contract: canonical sort order, dense
 * snapshot-local ordinals, and real Phase 3 parser bytes for every pack/mod mapping file.
 */
final class IdFixtures {

    private IdFixtures() {
    }

    /** One scripted block state. */
    record StateSpec(int metadata, Map<String, String> properties, int renderType,
            boolean opaqueCube, int light) {

        static StateSpec cube(int metadata) {
            return new StateSpec(metadata, Map.of(), 0, true, 0);
        }

        static StateSpec cube(int metadata, Map<String, String> properties) {
            return new StateSpec(metadata, properties, 0, true, 0);
        }

        static StateSpec cubeLight(int metadata, int light) {
            return new StateSpec(metadata, Map.of(), 0, true, light);
        }

        static StateSpec plant(int metadata, Map<String, String> properties) {
            return new StateSpec(metadata, properties, 0, false, 0);
        }

        static StateSpec liquid(int metadata) {
            return new StateSpec(metadata, Map.of(), 1, false, 0);
        }
    }

    /** One scripted block: name path, live legacy numeric ID, and its states. */
    record BlockDef(String path, int numericId, List<StateSpec> states) {

        static BlockDef cube(String path, int numericId) {
            return new BlockDef(path, numericId, List.of(StateSpec.cube(0)));
        }
    }

    record ItemDef(String path, String placedBlockPath) {
    }

    /** Assembles the validated snapshot exactly as glue would: sorted, dense. */
    static IdRegistrySnapshot snapshot(long generation, String fingerprint,
            List<BlockDef> blocks, List<ItemDef> items, List<String> entityPaths,
            TagMembershipSnapshot tags) {
        return snapshot(generation, fingerprint, blocks, items, entityPaths, tags,
                path -> "minecraft");
    }

    /** Namespace-aware assembly: {@code namespaceOf} maps each block path. */
    static IdRegistrySnapshot snapshot(long generation, String fingerprint,
            List<BlockDef> blocks, List<ItemDef> items, List<String> entityPaths,
            TagMembershipSnapshot tags, java.util.function.UnaryOperator<String> namespaceOf) {
        List<BlockDef> sortedBlocks = new ArrayList<>(blocks);
        sortedBlocks.sort(Comparator.comparing(
                b -> new RegistryName(namespaceOf.apply(b.path()), b.path())));
        List<BlockTypeRecord> blockRecords = new ArrayList<>();
        List<BlockStateRecord> stateRecords = new ArrayList<>();
        Map<String, Integer> defaultStateByPath = new LinkedHashMap<>();
        int stateOrdinal = 0;
        for (int blockOrdinal = 0; blockOrdinal < sortedBlocks.size(); blockOrdinal++) {
            BlockDef def = sortedBlocks.get(blockOrdinal);
            RegistryName name = new RegistryName(namespaceOf.apply(def.path()), def.path());
            List<StateSpec> sortedStates = new ArrayList<>(def.states());
            sortedStates.sort(Comparator
                    .comparing((StateSpec s) -> new TreeMap<>(s.properties()).toString())
                    .thenComparingInt(StateSpec::metadata));
            List<Integer> ordinals = new ArrayList<>();
            for (StateSpec spec : sortedStates) {
                stateRecords.add(new BlockStateRecord(stateOrdinal, blockOrdinal, spec.metadata(),
                        new TreeMap<>(spec.properties()), spec.renderType(), spec.opaqueCube(),
                        spec.light()));
                ordinals.add(stateOrdinal);
                defaultStateByPath.putIfAbsent(def.path(), stateOrdinal);
                stateOrdinal++;
            }
            blockRecords.add(new BlockTypeRecord(blockOrdinal, name, def.numericId(), ordinals));
        }
        List<ItemTypeRecord> itemRecords = new ArrayList<>();
        List<ItemDef> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparing(i -> new RegistryName("minecraft", i.path())));
        for (int itemOrdinal = 0; itemOrdinal < sortedItems.size(); itemOrdinal++) {
            ItemDef def = sortedItems.get(itemOrdinal);
            OptionalInt placed = def.placedBlockPath() == null
                    ? OptionalInt.empty()
                    : OptionalInt.of(defaultStateByPath.get(def.placedBlockPath()));
            itemRecords.add(new ItemTypeRecord(itemOrdinal,
                    new RegistryName(namespaceOf.apply(def.path()), def.path()), placed));
        }
        List<String> sortedEntities = new ArrayList<>(entityPaths);
        sortedEntities.sort(Comparator.naturalOrder());
        List<EntityTypeRecord> entityRecords = new ArrayList<>();
        for (int i = 0; i < sortedEntities.size(); i++) {
            entityRecords.add(new EntityTypeRecord(i,
                    new RegistryName("minecraft", sortedEntities.get(i))));
        }
        return new IdRegistrySnapshot(generation, new IdRegistryFingerprint(fingerprint),
                blockRecords, stateRecords, itemRecords, entityRecords, tags);
    }

    /** The shared vanilla-shaped scripted registry used across the suite. */
    static IdRegistrySnapshot vanilla() {
        return vanilla(true);
    }

    /** The shared vanilla-shaped registry, optionally without the waterlily block. */
    static IdRegistrySnapshot vanilla(boolean withWaterlily) {
        List<BlockDef> blocks = List.of(
                new BlockDef("cake", 92, List.of(
                        StateSpec.plant(0, Map.of("bites", "0")),
                        StateSpec.plant(3, Map.of("bites", "3")))),
                new BlockDef("deadbush", 32, List.of(StateSpec.plant(0, Map.of()))),
                new BlockDef("double_plant", 175, List.of(
                        StateSpec.plant(0, Map.of("half", "lower")),
                        StateSpec.plant(1, Map.of("half", "upper")))),
                new BlockDef("flowing_lava", 12, List.of(StateSpec.liquid(0))),
                new BlockDef("flowing_water", 10, List.of(StateSpec.liquid(0))),
                new BlockDef("glass", 20, List.of(new StateSpec(0, Map.of(), 0, false, 0))),
                new BlockDef("glowstone", 89, List.of(StateSpec.cubeLight(0, 15))),
                new BlockDef("grass", 2, List.of(StateSpec.cube(0, Map.of("snowy", "false")))),
                new BlockDef("lit_redstone_lamp", 124, List.of(StateSpec.cube(0))),
                new BlockDef("lava", 11, List.of(StateSpec.liquid(0))),
                new BlockDef("oak_door", 64, oakDoorStates()),
                new BlockDef("redstone_lamp", 123, List.of(StateSpec.cube(0))),
                new BlockDef("reeds", 83, List.of(
                        StateSpec.plant(0, Map.of("age", "0")),
                        StateSpec.plant(1, Map.of("age", "1")),
                        StateSpec.plant(2, Map.of("age", "2")),
                        StateSpec.plant(3, Map.of("age", "3")))),
                new BlockDef("stone", 1, List.of(StateSpec.cube(0))),
                new BlockDef("tallgrass", 31, List.of(
                        StateSpec.plant(0, Map.of("type", "grass")),
                        StateSpec.plant(1, Map.of("type", "fern")),
                        StateSpec.plant(2, Map.of("type", "dead")))),
                new BlockDef("water", 9, List.of(StateSpec.liquid(0))),
                new BlockDef("web", 30, List.of(StateSpec.plant(0, Map.of()))),
                new BlockDef("waterlily", 111, List.of(StateSpec.plant(0, Map.of()))));
        List<BlockDef> filtered = new ArrayList<>(blocks);
        if (!withWaterlily) {
            filtered.removeIf(b -> b.path().equals("waterlily"));
        }
        blocks = filtered;
        List<ItemDef> items = List.of(
                new ItemDef("glowstone", "glowstone"),
                new ItemDef("stick", null),
                new ItemDef("tallgrass", null));
        List<String> entities = List.of("creeper", "sheep");
        return snapshot(7, "registry-fp-1", blocks, items, entities, TagMembershipSnapshot.empty());
    }

    private static List<StateSpec> oakDoorStates() {
        // Twelve states: metadata 0..11, half lower (0-5) / upper (6-11), facing cycles
        // north,south,east,west. Real 1.12 doors split differently; the fixture is the
        // deterministic domain these tests resolve against.
        String[] facing = {"north", "south", "east", "west"};
        List<StateSpec> states = new ArrayList<>();
        for (int metadata = 0; metadata < 12; metadata++) {
            states.add(StateSpec.plant(metadata, Map.of(
                    "half", metadata < 6 ? "lower" : "upper",
                    "facing", facing[metadata % 4])));
        }
        return states;
    }

    /** A one-mod vanilla-shaped registry with a mod-owned block. */
    static IdRegistrySnapshot vanillaWithModBlock() {
        List<BlockDef> blocks = List.of(
                new BlockDef("stone", 1, List.of(StateSpec.cube(0))),
                new BlockDef("tallgrass", 31, List.of(
                        StateSpec.plant(0, Map.of("type", "grass")),
                        StateSpec.plant(1, Map.of("type", "fern")),
                        StateSpec.plant(2, Map.of("type", "dead")))),
                new BlockDef("custom_ore", 500, List.of(StateSpec.cube(0))));
        List<ItemDef> items = List.of(new ItemDef("stick", null));
        return snapshot(7, "registry-fp-mod", blocks, items, List.of(),
                TagMembershipSnapshot.empty(),
                path -> path.equals("custom_ore") ? "modrand" : "minecraft");
    }

    // ------------------------------------------------------------------ mapping inputs

    private static final DiagnosticReporter NOOP = d -> { };

    /** Parses real pack bytes through the Phase 3 parser (no resolver-only fixtures). */
    static IdMappingFileInput parsePack(MappingKind kind, String content, PackMappingOrigin origin) {
        IdMappingMacroEnvironment env = new IdMappingMacroEnvironment(11202, List.of(
                new MacroDefinition("MC_VERSION", "11202")));
        return new IdMappingParserImpl().parse(new IdMappingParseRequest(kind,
                content == null ? Optional.empty()
                        : Optional.of(ImmutableBytes.of(content.getBytes(StandardCharsets.ISO_8859_1))),
                origin, env, NOOP));
    }

    /** Parses real bounded mod bytes through the Phase 3 parser. */
    static IdMappingFileInput parseMod(MappingKind kind, String content, String modId,
            int contributionOrdinal, String sourceName) {
        IdMappingMacroEnvironment env = new IdMappingMacroEnvironment(11202, List.of(
                new MacroDefinition("MC_VERSION", "11202")));
        return new IdMappingParserImpl().parse(new IdMappingParseRequest(kind,
                content == null ? Optional.empty()
                        : Optional.of(ImmutableBytes.of(content.getBytes(StandardCharsets.ISO_8859_1))),
                new ModMappingOrigin(modId, contributionOrdinal, sourceName), env, NOOP));
    }

    static IdMappingInput packMappings(String blocks, String items, String entities,
            String layers, PackMappingOrigin origin) {
        return new IdMappingInput(23,
                new IdMappingMacroEnvironment(11202, List.of(
                        new MacroDefinition("MC_VERSION", "11202"))),
                parsePack(MappingKind.BLOCK, blocks, origin),
                parsePack(MappingKind.ITEM, items, origin),
                parsePack(MappingKind.ENTITY, entities, origin),
                parsePack(MappingKind.LAYER, layers, origin));
    }
    static PackMappingOrigin packOrigin() {
        return new PackMappingOrigin(
                new PackIdentity(new NormalizedPackPath("shaders"), Map.of()),
                new NormalizedPackPath("shaders/block.properties"));
    }

    /** Recording diagnostic reporter. */
    static final class RecordingReporter implements DiagnosticReporter {
        final List<EngineDiagnostic> received = new ArrayList<>();

        @Override
        public void report(EngineDiagnostic d) {
            received.add(d);
        }

        List<EngineDiagnostic> withKey(String messageKey) {
            return received.stream().filter(d -> d.messageKey().equals(messageKey)).toList();
        }

        int countWithLogChannel(String logChannel) {
            return (int) received.stream().filter(d -> d.logChannel().equals(logChannel)).count();
        }
    }
}
