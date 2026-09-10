// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.registry.ExtendedAttribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Attribute binding plans for the classic layout (PHASE_10_DOC §4.1, §4.6, §5.1): the
 * fixed extended-attribute table is consumed exactly as Phase 4 publishes it
 * ({@code mc_Entity}=10, {@code mc_midTexCoord}=11, {@code at_tangent}=12) — never
 * rebound or renumbered. A live plan enables exactly the intersection of declared
 * names and physically present supported fields; participation is the authenticated
 * source mask from {@link VertexProducer}, never inferred.
 *
 * <p>Capability gate (§4.6): the highest used location must sit below
 * {@code maxVertexAttribs} — location 10 needs 11, 11 needs 12, 12 needs 13. An unused
 * location 12 never rejects a simpler program.
 */
public final class ClassicInputPlans {

    private ClassicInputPlans() {
    }

    /**
     * The generic pointers for the declared extended names that the layout physically
     * carries, in ascending location order. Native mappings stay inside the P1 facade;
     * these descriptors are the plan's vocabulary.
     */
    public static List<AttributePointer> pointers(Collection<ExtendedAttribute> declared,
                                                  VertexLayout layout) {
        List<ExtendedAttribute> ordered = new ArrayList<>(declared);
        ordered.sort(Comparator.comparingInt(ExtendedAttribute::location));
        List<AttributePointer> pointers = new ArrayList<>(ordered.size());
        for (ExtendedAttribute attribute : ordered) {
            VertexField field = Classic56Layout.field(layout, declaredName(attribute));
            if (field == null) {
                continue; // declared but not physically present: not enabled, not forged
            }
            pointers.add(new AttributePointer(field.name(), attribute.location(),
                    field.byteOffset(), field.components(), field.storage(),
                    attribute == ExtendedAttribute.AT_TANGENT));
        }
        return List.copyOf(pointers);
    }

    /**
     * Builds the complete classic plan: layout fingerprint, ordered pointers, the
     * authenticated conventional participation in enum order and the required
     * expected geometry input.
     */
    public static VertexInputPlan plan(Set<ConventionalInput> participation,
                                       Set<ExtendedAttribute> declared,
                                       VertexGeometryInput geometryInput) {
        Set<ConventionalInput> ordered = participation.isEmpty()
                ? EnumSet.noneOf(ConventionalInput.class)
                : EnumSet.copyOf(participation);
        return new VertexInputPlan(Classic56Layout.layout().fingerprint(),
                pointers(declared, Classic56Layout.layout()),
                ordered, geometryInput);
    }

    /**
     * The fixed classic correspondence between Phase 4's published table row and the
     * declared GLSL name of the same attribute (PHASE_10_DOC §4.1, §4.6).
     */
    public static String declaredName(ExtendedAttribute attribute) {
        return switch (attribute) {
            case MC_ENTITY -> "mc_Entity";
            case MC_MID_TEX_COORD -> "mc_midTexCoord";
            case AT_TANGENT -> "at_tangent";
        };
    }

    /**
     * The minimum {@code maxVertexAttribs} this plan requires: one above its highest
     * used location, zero when the plan carries no generic pointers.
     */
    public static int requiredMaxVertexAttribs(VertexInputPlan plan) {
        int highest = -1;
        for (AttributePointer pointer : plan.pointers()) {
            highest = Math.max(highest, pointer.location());
        }
        return highest + 1;
    }

    /** True when the plan fits the capability: highest used location below the limit. */
    public static boolean fitsCapability(VertexInputPlan plan, int maxVertexAttribs) {
        return requiredMaxVertexAttribs(plan) <= maxVertexAttribs;
    }
}
