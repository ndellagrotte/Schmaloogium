// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

/**
 * The opaque atlas base-binding evidence (PHASE_7_DOC §5.1). Minted only by the mod
 * binding-observer after a successful <em>actual</em> base-texture bind/restoration; the
 * private implementation carries the actual opaque handle when known, the P13
 * Atlas/NonAtlas/Unavailable association, the current {@code PipelineVersion},
 * {@code resourceReloadEpoch} and a monotonically increasing {@code bindSerial}. Consumers
 * (Phase 13 leases, the shadow binding receiver) treat it as an unforgeable credential:
 * no accessor is public API, and identity is the whole contract.
 */
public interface AtlasBindingEvidence {
}
