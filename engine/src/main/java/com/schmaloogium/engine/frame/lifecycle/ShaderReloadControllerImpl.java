// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import com.schmaloogium.engine.frame.DriverReloadRequest;
import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.PipelineIdentity;
import com.schmaloogium.engine.frame.PipelineVersion;
import com.schmaloogium.engine.frame.ReloadRejection;
import com.schmaloogium.engine.frame.ReloadResult;
import com.schmaloogium.engine.frame.ReloadStatus;
import com.schmaloogium.engine.frame.ReloadToken;
import com.schmaloogium.engine.frame.ShaderReloadController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

/**
 * The token-bookkeeping implementation of {@link ShaderReloadController} (PHASE_7_DOC §5.1).
 * Requests are validated everywhere, queued behind the render-thread drain seam, and polled
 * by exactly the issued token. The drain seam (the §4.1 pipeline transaction) is supplied by
 * the composition root; until a coordinator is installed every accepted token stays Queued.
 */
public final class ShaderReloadControllerImpl implements ShaderReloadController {

    /** One render-thread drain step for an accepted request; receives the merged request. */
    @FunctionalInterface
    public interface Drain {

        /** Advances one accepted request; called on the render thread only. */
        ReloadStatus drain(DriverReloadRequest request);
    }

    private static final class Entry {
        volatile ReloadStatus status = new ReloadStatus.Queued();
    }

    private final BooleanSupplier renderThread;
    private final Drain drain;
    private final Map<ReloadToken, Entry> issued = new HashMap<>();
    private final AtomicLong tokenSource = new AtomicLong();
    private volatile boolean shuttingDown;

    public ShaderReloadControllerImpl(BooleanSupplier renderThread, Drain drain) {
        this.renderThread = Objects.requireNonNull(renderThread, "renderThread");
        this.drain = Objects.requireNonNull(drain, "drain");
    }

    @Override
    public ReloadResult request(DriverReloadRequest request) {
        Objects.requireNonNull(request, "request");
        if (shuttingDown) {
            return new ReloadResult.Rejected(ReloadRejection.SHUTTING_DOWN);
        }
        if (!renderThread.getAsBoolean()) {
            return new ReloadResult.Rejected(ReloadRejection.WRONG_THREAD);
        }
        ReloadToken token = new ReloadToken(tokenSource.getAndIncrement());
        issued.put(token, new Entry());
        // The drain runs synchronously here at v0.1: one accepted request → one outcome,
        // which keeps token status honest without an async coordinator.
        entry(token).status = drain.drain(request);
        ReloadStatus status = entry(token).status;
        if (status instanceof ReloadStatus.Active active) {
            return new ReloadResult.Accepted(token);
        }
        if (status instanceof ReloadStatus.Off || status instanceof ReloadStatus.Failed) {
            return new ReloadResult.Accepted(token);
        }
        return new ReloadResult.Coalesced(token);
    }

    @Override
    public ReloadStatus status(ReloadToken token) {
        Objects.requireNonNull(token, "token");
        Entry entry = issued.get(token);
        return entry == null ? new ReloadStatus.Unknown() : entry.status;
    }

    /** Publishes the drained outcome for a token (render thread; the drain seam may defer). */
    public void publish(ReloadToken token, ReloadStatus status) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(status, "status");
        Entry entry = issued.get(token);
        if (entry == null) {
            throw new IllegalArgumentException("unknown reload token: " + token);
        }
        entry.status = status;
    }

    /** Terminal: further requests reject with SHUTTING_DOWN; issued tokens stay queryable. */
    public void shutdown() {
        shuttingDown = true;
    }

    /** The count of tokens this controller has issued (diagnostics). */
    public int issuedCount() {
        return issued.size();
    }

    private Entry entry(ReloadToken token) {
        Entry entry = issued.get(token);
        if (entry == null) {
            throw new IllegalStateException("token vanished: " + token);
        }
        return entry;
    }

    /** Constant off status helper for the composition root. */
    public static ReloadStatus offStatus(PipelineVersion version) {
        return new ReloadStatus.Off(version);
    }

    /** Constant failure status helper for the composition root. */
    public static ReloadStatus failedStatus(FailureId failure) {
        return new ReloadStatus.Failed(failure);
    }

    /** Constant active status helper for the composition root. */
    public static ReloadStatus activeStatus(PipelineIdentity identity, PipelineVersion version) {
        return new ReloadStatus.Active(identity, version);
    }
}
