// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.registry.ExtendedAttribute;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Attribute binding plans versus capability gates (PHASE_10_DOC §4.6, §5.1): the
 * published 10/11/12 rows are consumed exactly, the plan enables the declared∩present
 * intersection, and the capability gate is highest-used-location — an unused location
 * 12 never rejects a simpler program.
 */
class ClassicInputPlansTest {

    private static final Set<ConventionalInput> BLOCK = VertexProducer.BLOCK.participation();

    @Test
    void pointersMatchThePublishedFixedTable() {
        var pointers = ClassicInputPlans.pointers(
                EnumSet.allOf(ExtendedAttribute.class), Classic56Layout.layout());
        assertEquals(3, pointers.size());
        assertPointer(pointers.get(0), "mc_Entity", 10, 48, 3, StorageType.INT16, false);
        assertPointer(pointers.get(1), "mc_midTexCoord", 11, 32, 2, StorageType.FLOAT32, false);
        assertPointer(pointers.get(2), "at_tangent", 12, 40, 4, StorageType.INT16, true);
    }

    @Test
    void capabilityGateIsTheHighestUsedLocation() {
        VertexInputPlan allThree = ClassicInputPlans.plan(BLOCK,
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES);
        assertEquals(13, ClassicInputPlans.requiredMaxVertexAttribs(allThree));
        assertFalse(ClassicInputPlans.fitsCapability(allThree, 12));
        assertTrue(ClassicInputPlans.fitsCapability(allThree, 13));
        assertTrue(ClassicInputPlans.fitsCapability(allThree, 16));

        VertexInputPlan identityOnly = ClassicInputPlans.plan(BLOCK,
                EnumSet.of(ExtendedAttribute.MC_ENTITY), VertexGeometryInput.TRIANGLES);
        // Location 10 needs 11 — and never rejects for the unused 11/12.
        assertEquals(11, ClassicInputPlans.requiredMaxVertexAttribs(identityOnly));
        assertTrue(ClassicInputPlans.fitsCapability(identityOnly, 11));
        assertFalse(ClassicInputPlans.fitsCapability(identityOnly, 10));

        VertexInputPlan midpointAndIdentity = ClassicInputPlans.plan(BLOCK,
                EnumSet.of(ExtendedAttribute.MC_ENTITY, ExtendedAttribute.MC_MID_TEX_COORD),
                VertexGeometryInput.TRIANGLES);
        assertEquals(12, ClassicInputPlans.requiredMaxVertexAttribs(midpointAndIdentity));
        assertTrue(ClassicInputPlans.fitsCapability(midpointAndIdentity, 12));

        VertexInputPlan noExtended = ClassicInputPlans.plan(BLOCK,
                EnumSet.noneOf(ExtendedAttribute.class), VertexGeometryInput.NONE);
        assertEquals(0, ClassicInputPlans.requiredMaxVertexAttribs(noExtended));
        assertTrue(ClassicInputPlans.fitsCapability(noExtended, 1));
    }

    @Test
    void planIdentityCoversFingerprintPointersParticipationAndGeometry() {
        VertexInputPlan plan = ClassicInputPlans.plan(BLOCK,
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES);
        assertEquals(Classic56Layout.layout().fingerprint(), plan.layoutFingerprint());
        assertEquals(BLOCK, plan.conventionalInputs());
        assertEquals(VertexGeometryInput.TRIANGLES, plan.expectedGeometryInput());
        assertEquals(plan, ClassicInputPlans.plan(BLOCK,
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES));

        assertNotEquals(plan, ClassicInputPlans.plan(VertexProducer.OLDMODEL.participation(),
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES));
        assertNotEquals(plan, ClassicInputPlans.plan(BLOCK,
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.NONE));
        assertNotEquals(plan, ClassicInputPlans.plan(BLOCK,
                EnumSet.of(ExtendedAttribute.MC_ENTITY), VertexGeometryInput.TRIANGLES));
    }

    @Test
    void declaredButPhysicallyAbsentFieldsAreNotForged() {
        // A reduced layout without at_tangent: the declared name finds no field and no
        // location-12 pointer is emitted.
        VertexLayout reduced = VertexLayoutBuilder
                .seeded(Classic56Layout.fields().subList(0, 6),
                        Classic56Layout.fields().get(5).byteOffset() + 1)
                .append("mc_Entity", 3, StorageType.INT16, Delivery.FLOAT_VALUE)
                .build("REDUCED");
        var pointers = ClassicInputPlans.pointers(
                EnumSet.allOf(ExtendedAttribute.class), reduced);
        assertEquals(1, pointers.size());
        assertEquals(10, pointers.get(0).location());
    }

    @Test
    void planPointersAreDefensivelyCopied() {
        VertexInputPlan plan = ClassicInputPlans.plan(BLOCK,
                EnumSet.allOf(ExtendedAttribute.class), VertexGeometryInput.TRIANGLES);
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> plan.pointers().add(plan.pointers().get(0)));
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> plan.conventionalInputs().add(ConventionalInput.UV1));
    }

    private static void assertPointer(AttributePointer pointer, String name, int location,
                                      int offset, int components, StorageType storage,
                                      boolean normalized) {
        assertEquals(name, pointer.name());
        assertEquals(location, pointer.location());
        assertEquals(offset, pointer.byteOffset());
        assertEquals(components, pointer.components());
        assertEquals(storage, pointer.storage());
        assertEquals(normalized, pointer.normalized());
    }
}
