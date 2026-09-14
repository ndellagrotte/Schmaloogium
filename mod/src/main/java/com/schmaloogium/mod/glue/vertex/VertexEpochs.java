// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.config.id.AliasLookup;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.vertex.VertexEpoch;
import com.schmaloogium.mod.glue.id.IdIdentityMaps;

import java.util.Objects;
import java.util.Optional;

/**
 * The render-thread publication of the current vertex epoch and the id lookup chunk
 * workers borrow while building (PHASE_10_DOC §4.4: "keep old borrowed alias tables and
 * ordinal maps alive until their last task ends" — the tuple is immutable, a task captures
 * it once at start). A change between vanilla and extended, or of the serial, requests
 * one world-renderer reload which the bootstrap performs off-frame (§4.8).
 */
public final class VertexEpochs {

    /** The immutable tuple a chunk task captures at its start. */
    public record Publication(VertexEpoch epoch, AliasLookup aliases, IdIdentityMaps maps) {
        public Publication {
            Objects.requireNonNull(epoch, "epoch");
            Objects.requireNonNull(maps, "maps");
        }
    }

    private static final Log LOG = Logs.channel(LogChannels.FRAME);
    private static volatile Publication current;
    private static volatile java.util.Set<com.schmaloogium.engine.registry.ExtendedAttribute> declaredUnion =
            java.util.Set.of();
    private static volatile boolean reloadPending;
    private static volatile String lastLogged = "vanilla";

    private VertexEpochs() {
    }

    /** Publishes the install's epoch (empty = vanilla formats). */
    public static void publish(Optional<VertexEpoch> epoch, AliasLookup aliases, IdIdentityMaps maps) {
        Publication previous = current;
        Publication next = epoch.map(e -> new Publication(e, aliases, maps)).orElse(null);
        boolean changed = (previous == null) != (next == null)
                || (previous != null && !previous.epoch().equals(next.epoch()));
        current = next;
        if (changed) {
            reloadPending = true;
            String now = next == null ? "vanilla" : "serial " + next.epoch().serial()
                    + " idGeneration " + next.epoch().idGeneration();
            LOG.info("H10-EPOCH-01 vertex epoch {} -> {} (world renderer reload requested)", lastLogged, now);
            lastLogged = now;
        }
    }

    /** The union of classic attributes any program of the installed pipeline declares (list capture). */
    public static void publishDeclaredUnion(java.util.Set<com.schmaloogium.engine.registry.ExtendedAttribute> union) {
        declaredUnion = java.util.Set.copyOf(union);
    }

    public static java.util.Set<com.schmaloogium.engine.registry.ExtendedAttribute> declaredUnion() {
        return declaredUnion;
    }

    /** The current extended publication, or null under vanilla formats. */
    public static Publication current() {
        return current;
    }

    /** True once per requested reload; the caller performs {@code loadRenderers()}. */
    public static boolean consumeReloadRequest() {
        if (!reloadPending) {
            return false;
        }
        reloadPending = false;
        return true;
    }
}
