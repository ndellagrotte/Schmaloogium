// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * GPU resource evidence for one plan (PHASE_5_DOC §2.2); lists are canonically ordered and duplicate-free.
 */
public record BufferResourceProjection(
        ResourceEvidenceStage evidenceStage,
        List<ColorBufferResource> colorBuffers,
        int depthTextures,
        ShadowResourceProjection shadow,
        boolean centerDepthSmoothEnabled,
        int noiseResolution,
        List<VertexAttributeResource> vertexAttributes,
        List<InstanceResource> instances,
        CapabilityGate capabilityGate,
        List<CapabilityShortfall> capabilityShortfalls) {

    public BufferResourceProjection {
        evidenceStage = Objects.requireNonNull(evidenceStage, "evidenceStage");
        shadow = Objects.requireNonNull(shadow, "shadow");
        capabilityGate = Objects.requireNonNull(capabilityGate, "capabilityGate");
        colorBuffers = List.copyOf(colorBuffers);

        List<VertexAttributeResource> attributes =
                new ArrayList<>(Objects.requireNonNull(vertexAttributes, "vertexAttributes"));
        for (VertexAttributeResource attribute : attributes) {
            Objects.requireNonNull(attribute, "vertexAttribute");
        }
        attributes.sort(Comparator.comparing(VertexAttributeResource::program)
                .thenComparing(VertexAttributeResource::name));
        for (int i = 1; i < attributes.size(); i++) {
            if (attributes.get(i).equals(attributes.get(i - 1))) {
                throw new IllegalArgumentException("duplicate vertex attribute: " + attributes.get(i));
            }
        }
        vertexAttributes = List.copyOf(attributes);

        List<InstanceResource> instanceList = new ArrayList<>(Objects.requireNonNull(instances, "instances"));
        for (InstanceResource instance : instanceList) {
            Objects.requireNonNull(instance, "instance");
        }
        instanceList.sort(Comparator.comparing(InstanceResource::program)
                .thenComparingInt(InstanceResource::count));
        for (int i = 1; i < instanceList.size(); i++) {
            if (instanceList.get(i).equals(instanceList.get(i - 1))) {
                throw new IllegalArgumentException("duplicate instance resource: " + instanceList.get(i));
            }
        }
        instances = List.copyOf(instanceList);

        List<CapabilityShortfall> shortfalls =
                new ArrayList<>(Objects.requireNonNull(capabilityShortfalls, "capabilityShortfalls"));
        for (CapabilityShortfall shortfall : shortfalls) {
            Objects.requireNonNull(shortfall, "capabilityShortfall");
        }
        shortfalls.sort(Comparator.comparingInt(shortfall -> shortfall.limit().ordinal()));
        for (int i = 1; i < shortfalls.size(); i++) {
            if (shortfalls.get(i).limit() == shortfalls.get(i - 1).limit()) {
                throw new IllegalArgumentException("duplicate capability shortfall: " + shortfalls.get(i).limit());
            }
        }
        capabilityShortfalls = List.copyOf(shortfalls);
    }
}
