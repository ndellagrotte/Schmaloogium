// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * The render-thread-only guards the shadow mixins consult (PHASE_8_DOC §4.7 step 3/4, §4.8.2):
 *
 * <ul>
 *   <li>the <b>setup-only traversal guard</b>, armed by {@link McShadowWorldPort#setupTerrain}
 *       around exactly one {@code RenderGlobal.setupTerrain}: the H8-TRAVERSE-01 redirects
 *       replace vanilla's frame-index visited marks with a pass-local identity set, answer
 *       {@code renderChunksMany = false} and all six seed directions, and filter neighbours
 *       through the prism's allowed set; the H8-REBUILD-01 entry witness counts here;</li>
 *   <li>the <b>entity-call guard</b>, armed around each shadow {@code renderEntities} call:
 *       the outline predicate answers false, and the {@code shadowBlockEntities} /
 *       {@code shadowPlayer} content switches cancel their draws;</li>
 *   <li>the <b>publication-scoped blob gate</b> (H8-BLOB-01): vanilla blob shadows are
 *       suppressed for as long as a composition with a ready shadow plan is installed.</li>
 * </ul>
 *
 * Every guard is a plain static because the mixins reach it from vanilla's call stack; all
 * mutation happens on the render thread inside one shadow invocation.
 */
public final class ShadowTraversalGuard {

    private static RenderGlobal receiver;
    private static Set<RenderChunk> visited;
    private static Set<RenderChunk> allowed;
    private static int rebuildEntries;

    private static boolean entitiesActive;
    private static boolean blockEntitiesEnabled = true;
    private static boolean playerEnabled = true;

    private static volatile boolean blobShadowsSuppressed;

    private ShadowTraversalGuard() {
    }

    // ------------------------------------------------------------------ traversal (setup-only)

    /** Arms the guard for one setup call; {@code allowedChunks} null means FullLoadedView. */
    public static void enter(RenderGlobal renderGlobal, Set<RenderChunk> allowedChunks) {
        if (receiver != null) {
            throw new IllegalStateException("shadow traversal guard is not reentrant");
        }
        receiver = renderGlobal;
        visited = Collections.newSetFromMap(new IdentityHashMap<>());
        allowed = allowedChunks;
        rebuildEntries = 0;
    }

    public static void exit() {
        receiver = null;
        visited = null;
        allowed = null;
    }

    public static boolean active() {
        return receiver != null;
    }

    /** The count of rebuild-branch entries observed since {@link #enter}. */
    public static int rebuildEntries() {
        return rebuildEntries;
    }

    /** H8-REBUILD-01-ENTRY: vanilla entered the rebuild branch of the guarded setup. */
    public static void noteRebuildEntry(RenderGlobal renderGlobal) {
        if (receiver == renderGlobal) {
            rebuildEntries++;
        }
    }

    /** H8-TRAVERSE-01 visit: true exactly when the chunk was not yet visited by this pass. */
    public static boolean visit(RenderChunk chunk, int vanillaFrameIndex) {
        if (receiver == null) {
            return chunk.setFrameIndex(vanillaFrameIndex);
        }
        return visited.add(chunk);
    }

    /** H8-TRAVERSE-01 neighbour: vanilla's answer, filtered by the prism's allowed set. */
    public static RenderChunk filterNeighbor(RenderChunk vanillaAnswer) {
        if (receiver == null || allowed == null || vanillaAnswer == null) {
            return vanillaAnswer;
        }
        return allowed.contains(vanillaAnswer) ? vanillaAnswer : null;
    }

    // ------------------------------------------------------------------ entities

    /** Arms the entity-call guard for one shadow {@code renderEntities} dynamic extent. */
    public static void enterEntities(boolean blockEntities, boolean player) {
        entitiesActive = true;
        blockEntitiesEnabled = blockEntities;
        playerEnabled = player;
    }

    public static void exitEntities() {
        entitiesActive = false;
        blockEntitiesEnabled = true;
        playerEnabled = true;
    }

    public static boolean entitiesActive() {
        return entitiesActive;
    }

    /** H-ENTITY-03 under shadow: block entities are skipped when {@code shadowBlockEntities=false}. */
    public static boolean cancelBlockEntity() {
        return entitiesActive && !blockEntitiesEnabled;
    }

    /** The view entity is skipped under shadow when {@code shadowPlayer=false}. */
    public static boolean cancelPlayer() {
        return entitiesActive && !playerEnabled;
    }

    // ------------------------------------------------------------------ blob gate

    /** Composition root: a ready shadow plan replaces vanilla's blob shadows (H8-BLOB-01). */
    public static void setBlobShadowsSuppressed(boolean suppressed) {
        blobShadowsSuppressed = suppressed;
    }

    public static boolean blobShadowsSuppressed() {
        return blobShadowsSuppressed;
    }
}
