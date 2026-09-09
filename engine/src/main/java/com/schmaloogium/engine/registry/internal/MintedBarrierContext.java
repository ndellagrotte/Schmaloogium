// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

/**
 * The only BarrierContext implementation a barrier or publisher accepts (PHASE_4_DOC §4.10).
 * Carries the minting source identity, frame epoch, context kind, and the stage/band pair.
 * Value equality is identity equality: two views with the same minted identity are the same
 * context; public-record-like copies are never issued.
 */
record MintedBarrierContext(
        Credentials.SourceIdentity source,
        long epoch,
        ContextKind kind,
        StageId stage,
        StageBand band,
        boolean shadowPass) implements BarrierContext {

    MintedBarrierContext {
        java.util.Objects.requireNonNull(source, "source");
        java.util.Objects.requireNonNull(kind, "kind");
        java.util.Objects.requireNonNull(stage, "stage");
        java.util.Objects.requireNonNull(band, "band");
    }

    enum ContextKind {
        ACTIVATION,
        RELEASE
    }

    @Override
    public StageId stage() {
        return stage;
    }

    @Override
    public StageBand band() {
        return band;
    }

    @Override
    public boolean shadowPass() {
        return shadowPass;
    }

    @Override
    public String toString() {
        return "BarrierContext[" + kind + " " + stage + "/" + band
            + (shadowPass ? " shadow" : "") + "]";
    }
}
