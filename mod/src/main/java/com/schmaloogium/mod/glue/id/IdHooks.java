// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.HeldItemResolver;
import com.schmaloogium.engine.config.id.IdScopeAdmission;
import com.schmaloogium.engine.config.id.IdScopeResult;
import com.schmaloogium.engine.config.id.IdScopeToken;
import com.schmaloogium.engine.config.id.PerDrawDynamics;
import com.schmaloogium.engine.config.id.PublishedIdRuntime;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.uniforms.Float4;
import com.schmaloogium.engine.uniforms.UniformEventSink;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The render-thread glue behind the Phase 9 hooks (PHASE_9_DOC §4.11–4.13): held-item
 * sampling on the accepted frame boundary (H9-HELD-01), entity / block-entity id scopes
 * under a live admission (H9-ENTITY-ID-01, H9-BLOCK-ENTITY-ID-01), the frame drain, and
 * the observed entity colour (H9-COLOR-01/02). Every method is a no-op while no id
 * runtime is published, and never lets an engine exception reach vanilla.
 */
public final class IdHooks {

    private static final Log LOG = Logs.channel(LogChannels.IDS);
    private static final Object NO_SCOPE = new Object();

    private record Publication(PublishedIdRuntime runtime, IdIdentityMaps maps,
                               HeldItemResolver held, UniformEventSink sink) {
    }

    private static volatile Publication current;
    private static final Deque<Object> entityTokens = new ArrayDeque<>();
    private static final Deque<Object> blockEntityTokens = new ArrayDeque<>();
    private static volatile boolean entityLogged;
    private static volatile boolean entityMainLogged;
    private static volatile boolean blockEntityLogged;
    private static volatile boolean blockEntityMainLogged;
    // H9-DIAG counters (render thread), reported once per publication.
    private static long entityCalls;
    private static long entityNoAdmission;
    private static long entityEntered;
    private static long blockEntityCalls;
    private static long blockEntityNoAdmission;
    private static long blockEntityUnknown;
    private static long blockEntityEntered;
    private static volatile boolean heldLogged;
    private static volatile boolean colorLogged;
    private static float[] colorScope = new float[4];
    private static boolean colorActive;

    private IdHooks() {
    }

    /** Composition root: the install's publication (or {@link IdPublication#none()}). */
    public static void publish(IdPublication publication) {
        Publication next = null;
        if (publication.runtime().isPresent()) {
            PublishedIdRuntime runtime = publication.runtime().get();
            try {
                next = new Publication(runtime, publication.maps(),
                        new HeldItemResolver(runtime.aliases(), publication.handLight(), publication.sink()),
                        publication.sink());
            } catch (RuntimeException e) {
                LOG.warn("H9-IDS-00 id publication unusable: {}", e.toString());
            }
        }
        current = next;
        entityTokens.clear();
        blockEntityTokens.clear();
        entityLogged = false;
        entityMainLogged = false;
        blockEntityLogged = false;
        blockEntityMainLogged = false;
        entityCalls = entityNoAdmission = entityEntered = 0;
        blockEntityCalls = blockEntityNoAdmission = blockEntityUnknown = blockEntityEntered = 0;
        heldLogged = false;
        colorLogged = false;
        colorActive = false;
    }

    public static boolean active() {
        return current != null;
    }

    /** The published runtime generation, or 0 while off. */
    public static long generation() {
        Publication p = current;
        try {
            return p == null ? 0L : p.runtime().generation();
        } catch (RuntimeException closed) {
            return 0L;
        }
    }

    /** H9-HELD-01: immediately after the accepted frame begin, before any activation. */
    public static void onFrameAccepted(EntityPlayer player, long worldEpoch, long logicalTick) {
        Publication p = current;
        if (p == null) {
            return;
        }
        try {
            var hands = HeldHandsSampler.sample(p.maps(), player, worldEpoch, logicalTick);
            p.held().accept(hands);
            if (!heldLogged) {
                heldLogged = true;
                LOG.info("H9-HELD-01 first held sample: main ordinal {} light {} / off ordinal {} light {} -> {}",
                        hands.main().itemOrdinal(), hands.main().staticLight(),
                        hands.off().itemOrdinal(), hands.off().staticLight(), p.held().lastPublished());
            }
        } catch (RuntimeException e) {
            LOG.warn("H9-HELD-01 held sample failed: {}", e.toString());
        }
    }

    /** The frame's outer finally: both stacks cleared and zeros sent before any later draw. */
    public static void onFrameFinished() {
        IdAdmissionGate.closeAll();
        entityTokens.clear();
        blockEntityTokens.clear();
        colorActive = false;
        Publication p = current;
        if (p == null) {
            return;
        }
        try {
            p.runtime().perDraw().resetFrame();
        } catch (RuntimeException e) {
            // A retired runtime has nothing to drain; the next install re-arms.
        }
    }

    /** Main admission for an accepted ENTITIES / BLOCK_ENTITIES scope. */
    public static IdScopeAdmission openMainAdmission(long frameId) {
        return current == null ? null : IdAdmissionGate.openMain(generation(), frameId);
    }

    /** Shadow admission for the authenticated shadow entity pass. */
    public static IdScopeAdmission openShadowAdmission(long frameId) {
        return current == null ? null : IdAdmissionGate.openShadow(generation(), frameId);
    }

    public static void closeAdmission(IdScopeAdmission admission) {
        if (admission != null) {
            IdAdmissionGate.close(admission);
        }
    }

    /** H9-ENTITY-ID-01 HEAD: pushes the entity's scope (or a no-scope marker) for the paired RETURN. */
    /** H9-DIAG-01: the scope statistics since the publication (one line, on demand). */
    public static String diagnostics() {
        return "entity calls " + entityCalls + " noAdmission " + entityNoAdmission + " entered " + entityEntered
                + "; blockEntity calls " + blockEntityCalls + " noAdmission " + blockEntityNoAdmission
                + " unknownState " + blockEntityUnknown + " entered " + blockEntityEntered;
    }

    public static void enterEntity(Entity entity) {
        Publication p = current;
        IdScopeAdmission admission = IdAdmissionGate.current();
        Object token = NO_SCOPE;
        if (p != null) {
            entityCalls++;
            if (admission == null) {
                entityNoAdmission++;
            }
        }
        if (p != null && admission != null) {
            int ordinal = ForgeIdSnapshotProvider.entityOrdinal(p.maps(), entity);
            if (ordinal >= 0) {
                try {
                    IdScopeResult result = p.runtime().perDraw().enterEntity(admission, ordinal);
                    if (result instanceof IdScopeResult.Entered entered) {
                        token = entered.token();
                        entityEntered++;
                        boolean first = admission.shadow() ? !entityLogged : !entityMainLogged;
                        if (admission.shadow()) {
                            entityLogged = true;
                        } else {
                            entityMainLogged = true;
                        }
                        if (first) {
                            LOG.info("H9-ENTITY-ID-01 first entity id scope: {} ordinal {} -> entityId {}{}",
                                    entity.getClass().getSimpleName(), ordinal,
                                    p.runtime().aliases().entityId(ordinal).shaderId(),
                                    admission.shadow() ? " (shadow)" : "");
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.warn("H9-ENTITY-ID-01 enter failed: {}", e.toString());
                }
            }
        }
        entityTokens.push(token);
    }

    /** H9-ENTITY-ID-01 RETURN: restores the preceding id. */
    public static void leaveEntity() {
        Object token = entityTokens.poll();
        leave(token);
    }

    /** H9-BLOCK-ENTITY-ID-01: the block entity's own block state decides the id. */
    public static void enterBlockEntity(TileEntity tile) {
        Publication p = current;
        IdScopeAdmission admission = IdAdmissionGate.current();
        Object token = NO_SCOPE;
        if (p != null) {
            blockEntityCalls++;
            if (admission == null) {
                blockEntityNoAdmission++;
            }
        }
        if (p != null && admission != null && tile != null) {
            int ordinal = -1;
            try {
                if (tile.hasWorld() && tile.getPos() != null) {
                    ordinal = p.maps().stateOrdinal(tile.getWorld().getBlockState(tile.getPos()));
                }
            } catch (RuntimeException e) {
                ordinal = -1;
            }
            if (ordinal < 0) {
                blockEntityUnknown++;
            }
            if (ordinal >= 0) {
                try {
                    IdScopeResult result = p.runtime().perDraw().enterBlockEntity(admission, ordinal);
                    if (result instanceof IdScopeResult.Entered entered) {
                        token = entered.token();
                        blockEntityEntered++;
                        boolean first = admission.shadow() ? !blockEntityLogged : !blockEntityMainLogged;
                        if (admission.shadow()) {
                            blockEntityLogged = true;
                        } else {
                            blockEntityMainLogged = true;
                        }
                        if (first) {
                            LOG.info("H9-BLOCK-ENTITY-ID-01 first block entity id scope: {} state ordinal {} -> blockEntityId {}{}",
                                    tile.getClass().getSimpleName(), ordinal,
                                    p.runtime().aliases().blockId(ordinal).shaderId(),
                                    admission.shadow() ? " (shadow)" : "");
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.warn("H9-BLOCK-ENTITY-ID-01 enter failed: {}", e.toString());
                }
            }
        }
        blockEntityTokens.push(token);
    }

    public static void leaveBlockEntity() {
        leave(blockEntityTokens.poll());
    }

    private static void leave(Object token) {
        if (!(token instanceof IdScopeToken scope)) {
            return;
        }
        Publication p = current;
        if (p == null) {
            return;
        }
        try {
            PerDrawDynamics perDraw = p.runtime().perDraw();
            perDraw.leave(scope);
        } catch (RuntimeException e) {
            LOG.warn("id scope leave failed: {}", e.toString());
        }
    }

    /** H9-COLOR-01: vanilla's GL_TEXTURE_ENV_COLOR operands, observed; the original call runs unchanged. */
    public static void noteEntityColor(float r, float g, float b, float a) {
        Publication p = current;
        if (p == null || !(Float.isFinite(r) && Float.isFinite(g) && Float.isFinite(b) && Float.isFinite(a))) {
            return;
        }
        colorScope = new float[] {r, g, b, a};
        colorActive = true;
        p.sink().updateEntityColor(new Float4(r, g, b, a));
        if (!colorLogged) {
            colorLogged = true;
            LOG.info("H9-COLOR-01 first entity colour observed: ({}, {}, {}, {})", r, g, b, a);
        }
    }

    /** H9-COLOR-02: vanilla unset the effect; neutral outside every colour scope. */
    public static void clearEntityColor() {
        Publication p = current;
        if (p == null || !colorActive) {
            return;
        }
        colorActive = false;
        p.sink().updateEntityColor(new Float4(0f, 0f, 0f, 0f));
    }
}
