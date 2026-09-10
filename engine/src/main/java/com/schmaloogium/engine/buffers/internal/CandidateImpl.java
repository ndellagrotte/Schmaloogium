// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferEstateInspection;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.registry.RegistryFingerprint;

import java.util.Objects;

/** The concrete owned candidate: inspection metadata plus the owned-handle ledger. */
public final class CandidateImpl implements BufferEstateInspection {

    private final EstateCore core;
    private final CandidateBuilder.Ledger ledger;
    private EstateViewImpl view;

    public CandidateImpl(EstateCore core, CandidateBuilder.Ledger ledger) {
        this.core = Objects.requireNonNull(core, "core");
        this.ledger = Objects.requireNonNull(ledger, "ledger");
    }

    public EstateCore core() {
        return core;
    }

    /** The runtime operation surface; constructed lazily once per candidate. */
    public synchronized EstateViewImpl view() {
        if (view == null) {
            view = new EstateViewImpl(core, new ClearExecutor(), new TextureBinder(),
                new ShadowOperator());
        }
        return view;
    }

    public void closeOwned() {
        ledger.walkReverse();
    }

    @Override
    public RegistryFingerprint registryFingerprint() {
        return core.registryFingerprint;
    }

    @Override
    public BufferSizing sizing() {
        return core.plan.sizing();
    }

    @Override
    public BufferInventory inventory() {
        return core.plan.inventory();
    }

    @Override
    public BufferResourceSnapshot.Available resources() {
        return core.realized;
    }
}
