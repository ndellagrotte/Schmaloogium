// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.frame.DriverReloadRequest;
import com.schmaloogium.engine.frame.ReloadIntent;
import com.schmaloogium.engine.frame.ReloadReason;
import com.schmaloogium.engine.frame.ReloadReasons;
import com.schmaloogium.engine.frame.ReloadResult;
import com.schmaloogium.engine.frame.ReloadStatus;
import com.schmaloogium.engine.frame.ShaderReloadController;
import com.schmaloogium.engine.frame.lifecycle.ReloadLifecycle;
import com.schmaloogium.engine.frame.lifecycle.ReloadQueue;
import com.schmaloogium.engine.frame.lifecycle.ReloadRequest;
import com.schmaloogium.engine.frame.lifecycle.ShaderReloadControllerImpl;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The Phase 7 {@link ReloadCoordinator} implementation: mod-side classified requests are
 * merged in the engine {@link ReloadQueue} (client thread), and {@link #drainOnce()} turns
 * the merged slot into exactly one {@link DriverReloadRequest} through the token-bookkeeping
 * {@link ShaderReloadControllerImpl}, whose drain is the {@link PipelineTransaction}. N rapid
 * submits produce one rebuild per tick by construction.
 */
public final class ShaderReloadCoordinator implements ReloadCoordinator {

    private static final Log LOG = Logs.channel(LogChannels.FRAME);

    private final PipelineTransaction transaction;
    private final ReloadQueue queue;
    private final ShaderReloadControllerImpl controller;
    private final Supplier<PackSelection> selection;
    private long intentSequence;

    public ShaderReloadCoordinator(PipelineTransaction transaction, BooleanSupplier renderThread,
                                   Supplier<PackSelection> selection) {
        this.transaction = Objects.requireNonNull(transaction, "transaction");
        Objects.requireNonNull(renderThread, "renderThread");
        this.selection = Objects.requireNonNull(selection, "selection");
        this.queue = new ReloadQueue(renderThread);
        this.controller = new ShaderReloadControllerImpl(renderThread, transaction);
    }

    @Override
    public void submit(com.schmaloogium.mod.gui.model.ReloadRequest request) {
        Objects.requireNonNull(request, "request");
        queue.submit(new ReloadRequest(lifecycle(request.lifecycle()),
                request.worldRendererReload(), request.resourceReacquire(), reason(request.cause())));
    }

    /** Submits an engine-side request directly (initial load, dimension change, retries). */
    public void submitEngine(ReloadRequest request) {
        queue.submit(Objects.requireNonNull(request, "request"));
    }

    /**
     * Drains one merged request on the render thread with no frame open; empty when nothing
     * was pending. NONE-lifecycle requests touch no engine state and answer the current status.
     */
    public Optional<ReloadStatus> drainOnce() {
        Optional<ReloadRequest> pending = queue.beginDrain();
        if (pending.isEmpty()) {
            return Optional.empty(); // an empty slot opens no drain
        }
        try {
            ReloadRequest merged = pending.get();
            if (merged.lifecycle() == ReloadLifecycle.NONE) {
                LOG.info("reload {} ({}) is resource-only at v0.1: no publication change",
                        merged.lifecycle(), merged.cause());
                return Optional.of(transaction.currentStatus());
            }
            DriverReloadRequest driverRequest = new DriverReloadRequest(
                    new ReloadIntent.Select(selection.get()),
                    new ReloadReasons(Set.of(merged.cause())), intentSequence++);
            ReloadResult result = controller.request(driverRequest);
            return Optional.of(switch (result) {
                case ReloadResult.Accepted accepted -> controller.status(accepted.token());
                case ReloadResult.Coalesced coalesced -> controller.status(coalesced.token());
                case ReloadResult.Rejected rejected -> {
                    LOG.warn("reload request rejected: {}", rejected.reason());
                    yield transaction.currentStatus();
                }
            });
        } finally {
            queue.endDrain();
        }
    }

    public ShaderReloadController controller() {
        return controller;
    }

    public PipelineTransaction transaction() {
        return transaction;
    }

    static ReloadLifecycle lifecycle(com.schmaloogium.mod.gui.model.ReloadLifecycle lifecycle) {
        return switch (lifecycle) {
            case NONE -> ReloadLifecycle.NONE;
            case REPUBLISH -> ReloadLifecycle.REPUBLISH;
            case FULL -> ReloadLifecycle.FULL;
        };
    }

    static ReloadReason reason(ReloadCause cause) {
        return switch (cause) {
            case KEYBIND, COMMAND, PACK_SELECTION -> ReloadReason.PACK_SELECTION;
            case OPTION_APPLY, OPTION_RESET, PROFILE_APPLY -> ReloadReason.OPTION_CHANGE;
            case ENGINE_SETTING -> ReloadReason.RESOLUTION_MULTIPLIER;
            case RESOURCE_RELOAD -> ReloadReason.RESOURCE_RELOAD;
        };
    }
}
