// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.registry.ExtendedAttribute;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-EPOCH pure kernel (PHASE_10_DOC §4.5, §8): epoch equality is necessary, never
 * sufficient — identical alias fingerprints with unequal generations reject, a world
 * change with repeated coordinates rejects, and a plan change with equal epochs
 * rejects. Admission never reinterprets stale bytes.
 */
class MeshAdmissionTest {

    private static final VertexInputPlan PLAN = ClassicInputPlans.plan(
            VertexProducer.BLOCK.participation(),
            EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES);

    private static VertexEpoch epoch(long serial, long world, long generation,
                                     String fingerprint) {
        return new VertexEpoch(serial, world, generation, fingerprint);
    }

    @Test
    void matchingEpochAndPlanAdmits() {
        VertexEpoch current = epoch(7, 1, 3, PLAN.layoutFingerprint());
        assertEquals(MeshAdmission.Admission.Admitted.class,
                MeshAdmission.admit(current, current, PLAN, PLAN).getClass());
    }

    @Test
    void queuedUploadUnderAnOldSerialNeverAdmits() {
        // Queue under epoch A, activate B, run A's queued upload → rejected.
        VertexEpoch queued = epoch(7, 1, 3, PLAN.layoutFingerprint());
        VertexEpoch activated = epoch(8, 1, 3, PLAN.layoutFingerprint());
        assertRejected(queued, activated, PLAN, PLAN, MeshAdmission.Reason.STALE_SERIAL);
    }

    @Test
    void identicalFingerprintsWithUnequalGenerationsReject() {
        VertexEpoch product = epoch(7, 1, 3, PLAN.layoutFingerprint());
        VertexEpoch republished = epoch(7, 1, 4, PLAN.layoutFingerprint());
        assertRejected(product, republished, PLAN, PLAN,
                MeshAdmission.Reason.ID_GENERATION_MISMATCH);
    }

    @Test
    void worldChangeWithRepeatedCoordinatesRejects() {
        VertexEpoch product = epoch(7, 1, 3, PLAN.layoutFingerprint());
        VertexEpoch reloaded = epoch(7, 2, 3, PLAN.layoutFingerprint());
        assertRejected(product, reloaded, PLAN, PLAN,
                MeshAdmission.Reason.WORLD_EPOCH_MISMATCH);
    }

    @Test
    void layoutFingerprintChangeRejectsEvenWithEqualSerials() {
        VertexInputPlan grownPlan = ClassicInputPlans.plan(
                VertexProducer.BLOCK.participation(),
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES);
        VertexEpoch product = epoch(7, 1, 3, PLAN.layoutFingerprint());
        VertexEpoch current = epoch(7, 1, 3, "different-layout-fingerprint");
        assertRejected(product, current, PLAN, grownPlan, MeshAdmission.Reason.LAYOUT_CHANGED);
    }

    @Test
    void planChangeWithEqualEpochsRejects() {
        // Same layout fingerprint and stride is insufficient: a participation change
        // invalidates old products.
        VertexEpoch current = epoch(7, 1, 3, PLAN.layoutFingerprint());
        VertexInputPlan oldmodelPlan = ClassicInputPlans.plan(
                VertexProducer.OLDMODEL.participation(),
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES);
        assertRejected(current, current, oldmodelPlan, PLAN, MeshAdmission.Reason.PLAN_CHANGED);

        VertexInputPlan noTangentPlan = ClassicInputPlans.plan(
                VertexProducer.BLOCK.participation(),
                EnumSet.of(ExtendedAttribute.MC_ENTITY), VertexGeometryInput.TRIANGLES);
        assertRejected(current, current, noTangentPlan, PLAN, MeshAdmission.Reason.PLAN_CHANGED);
    }

    @Test
    void missingProductIdentityNeverAdmits() {
        VertexEpoch current = epoch(7, 1, 3, PLAN.layoutFingerprint());
        assertEquals(MeshAdmission.Admission.Rejected.class,
                MeshAdmission.admit(null, current, PLAN, PLAN).getClass());
    }

    private static void assertRejected(VertexEpoch product, VertexEpoch current,
                                       VertexInputPlan productPlan, VertexInputPlan currentPlan,
                                       MeshAdmission.Reason reason) {
        var admission = MeshAdmission.admit(product, current, productPlan, currentPlan);
        assertTrue(admission instanceof MeshAdmission.Admission.Rejected);
        assertEquals(reason, ((MeshAdmission.Admission.Rejected) admission).reason());
    }
}
