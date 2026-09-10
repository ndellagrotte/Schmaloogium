// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.List;
import java.util.Optional;

/**
 * The immutable, versioned, fingerprinted modern-name/fluid compatibility catalog
 * (PHASE_9_DOC §4.6). Each symbolic pair is validated against the live snapshot at
 * resolution; a missing target disables only that pair with one diagnostic. Extension
 * requires a named pack fixture, a live-target proof, a version increment and a D-P9
 * addendum — runtime name similarity never manufactures an alias.
 *
 * <p>Three closed pair kinds implement the §4.6 semantics:
 * <ul>
 *   <li>{@link ModernName} — an unambiguous modern name with no 1.12 meaning, expanded
 *       when exact lookup fails;</li>
 *   <li>{@link AmbiguousModern} — a token that already has a 1.12 meaning
 *       ({@code minecraft:grass}, {@code minecraft:redstone_lamp}); its modern target is
 *       used only for rules carrying {@code MappingEra.MODERN} provenance (P3's isolated
 *       BLOCK alternate), replacing exact lookup for that selector;</li>
 *   <li>{@link FluidPair} — still/flowing fluid groups; both tokens expand to both live
 *       entries, written token first, counterpart second.</li>
 * </ul>
 */
public record CompatibilityAliasCatalog(int version, List<Entry> entries) {

    /** Catalog version 1: exactly the assignment's §4.6 seeds. */
    public static final int V0_3_VERSION = 1;

    public CompatibilityAliasCatalog {
        if (version < 1) {
            throw new IllegalArgumentException("catalog version must be >= 1");
        }
        entries = List.copyOf(java.util.Objects.requireNonNull(entries, "entries"));
    }

    /** The v0.3 catalog: the eight §4.6 seeds plus the two fluid groups. */
    public static CompatibilityAliasCatalog v0_3() {
        return new CompatibilityAliasCatalog(V0_3_VERSION, List.of(
                new ModernName(n("grass_block"), n("grass")),
                new ModernName(n("short_grass"), n("tallgrass")),
                new ModernName(n("tall_grass"), n("double_plant")),
                new ModernName(n("dead_bush"), n("deadbush")),
                new ModernName(n("sugar_cane"), n("reeds")),
                new ModernName(n("lily_pad"), n("waterlily")),
                new ModernName(n("cobweb"), n("web")),
                new AmbiguousModern(n("grass"), n("tallgrass")),
                new AmbiguousModern(n("redstone_lamp"), n("lit_redstone_lamp")),
                new FluidPair(n("water"), n("flowing_water")),
                new FluidPair(n("lava"), n("flowing_lava"))));
    }

    private static RegistryName n(String path) {
        return new RegistryName("minecraft", path);
    }

    /** Deterministic catalog fingerprint over version and entries. */
    public String fingerprint() {
        StringBuilder canonical = new StringBuilder("version=").append(version).append('\n');
        for (Entry entry : entries) {
            canonical.append(entry.canonicalText()).append('\n');
        }
        return Fingerprints.sha256Hex(canonical.toString());
    }

    /** The unambiguous modern-name target for one source token, if any. */
    public Optional<RegistryName> modernNameTarget(RegistryName source) {
        for (Entry entry : entries) {
            if (entry instanceof ModernName modern && modern.source().equals(source)) {
                return Optional.of(modern.target());
            }
        }
        return Optional.empty();
    }

    /** The modern target for an ambiguous token, used only with MODERN provenance. */
    public Optional<RegistryName> ambiguousModernTarget(RegistryName source) {
        for (Entry entry : entries) {
            if (entry instanceof AmbiguousModern ambiguous && ambiguous.source().equals(source)) {
                return Optional.of(ambiguous.modernTarget());
            }
        }
        return Optional.empty();
    }

    /** The live counterpart of one token of a still/flowing group, if any. */
    public Optional<RegistryName> fluidCounterpart(RegistryName source) {
        for (Entry entry : entries) {
            if (entry instanceof FluidPair pair) {
                if (pair.still().equals(source)) {
                    return Optional.of(pair.flowing());
                }
                if (pair.flowing().equals(source)) {
                    return Optional.of(pair.still());
                }
            }
        }
        return Optional.empty();
    }

    /** One catalog pair. */
    public sealed interface Entry permits ModernName, AmbiguousModern, FluidPair {

        /** Canonical identity text used by the catalog fingerprint. */
        String canonicalText();
    }

    /** Unambiguous modern source name with no 1.12 meaning, and its 1.12 target. */
    public record ModernName(RegistryName source, RegistryName target) implements Entry {

        public ModernName {
            java.util.Objects.requireNonNull(source, "source");
            java.util.Objects.requireNonNull(target, "target");
        }

        @Override
        public String canonicalText() {
            return "modern:" + source.canonical() + "->" + target.canonical();
        }
    }

    /** Ambiguous token keeping its exact 1.12 meaning unless MODERN provenance applies. */
    public record AmbiguousModern(RegistryName source, RegistryName modernTarget)
            implements Entry {

        public AmbiguousModern {
            java.util.Objects.requireNonNull(source, "source");
            java.util.Objects.requireNonNull(modernTarget, "modernTarget");
        }

        @Override
        public String canonicalText() {
            return "ambiguous:" + source.canonical() + "->" + modernTarget.canonical();
        }
    }

    /** Still/flowing fluid group; both sides expand to both live entries. */
    public record FluidPair(RegistryName still, RegistryName flowing) implements Entry {

        public FluidPair {
            java.util.Objects.requireNonNull(still, "still");
            java.util.Objects.requireNonNull(flowing, "flowing");
        }

        @Override
        public String canonicalText() {
            return "fluid:" + still.canonical() + "<->" + flowing.canonical();
        }
    }
}
