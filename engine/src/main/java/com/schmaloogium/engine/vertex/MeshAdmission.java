// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * The mesh epoch-check policy for saved states, resorts, queued uploads and draws
 * (PHASE_10_DOC §4.5): a sealed product's attached epoch and complete plan identity are
 * validated against the current publication before the product may restore, upload or
 * draw. Epoch equality is necessary, never sufficient — a plan (participation or
 * pointer set) change invalidates old products even with equal epochs, and an equal
 * layout fingerprint with an unequal Phase 9 generation is stale.
 *
 * <p>Checking order is fixed so diagnostics are stable: world epoch, then ID
 * generation, then serial, then layout fingerprint, then complete plan identity.
 */
public final class MeshAdmission {

    /** Why a sealed product may not restore, upload or draw. */
    public enum Reason {
        WORLD_EPOCH_MISMATCH,
        ID_GENERATION_MISMATCH,
        STALE_SERIAL,
        LAYOUT_CHANGED,
        PLAN_CHANGED
    }

    /** The closed admission outcome. */
    public sealed interface Admission {

        /** The product may restore/upload/draw under the current publication. */
        record Admitted() implements Admission {
        }

        /** The product is stale; schedule a rebuild, never reinterpret its bytes. */
        record Rejected(Reason reason) implements Admission {
        }
    }

    private MeshAdmission() {
    }

    /**
     * Validates a sealed product's identity against the current epoch and plan.
     * Both epochs and both plans must be nonnull; a null product identity never
     * admits.
     */
    public static Admission admit(VertexEpoch product, VertexEpoch current,
                                  VertexInputPlan productPlan, VertexInputPlan currentPlan) {
        if (product == null || current == null || productPlan == null || currentPlan == null) {
            return new Admission.Rejected(Reason.PLAN_CHANGED);
        }
        if (product.worldEpoch() != current.worldEpoch()) {
            return new Admission.Rejected(Reason.WORLD_EPOCH_MISMATCH);
        }
        if (product.idGeneration() != current.idGeneration()) {
            return new Admission.Rejected(Reason.ID_GENERATION_MISMATCH);
        }
        if (product.serial() != current.serial()) {
            return new Admission.Rejected(Reason.STALE_SERIAL);
        }
        if (!product.layoutFingerprint().equals(current.layoutFingerprint())) {
            return new Admission.Rejected(Reason.LAYOUT_CHANGED);
        }
        if (!productPlan.equals(currentPlan)) {
            return new Admission.Rejected(Reason.PLAN_CHANGED);
        }
        return new Admission.Admitted();
    }
}
