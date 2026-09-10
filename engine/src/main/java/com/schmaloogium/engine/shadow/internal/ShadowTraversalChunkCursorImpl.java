// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.shadow.ShadowTraversalChunkCursor;
import com.schmaloogium.engine.shadow.ShadowTraversalPlan;
import com.schmaloogium.engine.uniforms.Float3;

/**
 * The sole cursor implementation. Deterministic, allocation-free per step: the
 * longitudinal near-to-far order and the perpendicular square order are precomputed as
 * primitive arrays at creation; {@code advance} is pure index arithmetic over them plus
 * the ascending section range.
 */
public final class ShadowTraversalChunkCursorImpl implements ShadowTraversalChunkCursor {

    private static final float BLOCKS_PER_CHUNK = 16.0f;

    private final int longitudinalAxis; // 0=x, 1=y, 2=z
    private final int perpAxisA;
    private final int perpAxisB;
    private final int camLong;
    private final int camPerpA;
    private final int camPerpB;
    private final int minSection;
    private final int maxSection;
    private final int[] longitudinalOrder;
    private final int[] perpendicularA;
    private final int[] perpendicularB;

    private int longitudinalIndex;
    private int perpendicularIndex;
    private int section;

    public ShadowTraversalChunkCursorImpl(ShadowTraversalPlan.SunAlignedPrism plan,
            Float3 cameraPosition, int minSection, int maxSection) {
        Float3 light = plan.towardLight();
        double ax = Math.abs(light.x());
        double ay = Math.abs(light.y());
        double az = Math.abs(light.z());
        if (ax >= ay && ax >= az) {
            this.longitudinalAxis = 0;
            this.perpAxisA = 1;
            this.perpAxisB = 2;
        } else if (ay >= az) {
            this.longitudinalAxis = 1;
            // Near-vertical light: deterministic X-major horizontal square, never a
            // division by the near-zero horizontal projection.
            this.perpAxisA = 0;
            this.perpAxisB = 2;
        } else {
            this.longitudinalAxis = 2;
            this.perpAxisA = 0;
            this.perpAxisB = 1;
        }
        this.camLong = chunk(component(cameraPosition, longitudinalAxis));
        this.camPerpA = chunk(component(cameraPosition, perpAxisA));
        this.camPerpB = chunk(component(cameraPosition, perpAxisB));
        this.minSection = minSection;
        this.maxSection = maxSection;
        this.longitudinalOrder = buildLongitudinalOrder(camLong,
                camLong - plan.shadowRadiusChunks(), camLong + plan.viewRadiusChunks());
        int[] perpA = new int[0];
        int[] perpB = new int[0];
        int radius = plan.shadowRadiusChunks();
        int capacity = (2 * radius + 1) * (2 * radius + 1);
        perpA = new int[capacity];
        perpB = new int[capacity];
        int count = 0;
        for (int d = 0; d <= radius; d++) {
            for (int da = -d; da <= d; da++) {
                for (int db = -d; db <= d; db++) {
                    if (Math.abs(da) == d || Math.abs(db) == d) {
                        perpA[count] = da;
                        perpB[count] = db;
                        count++;
                    }
                }
            }
        }
        this.perpendicularA = java.util.Arrays.copyOf(perpA, count);
        this.perpendicularB = java.util.Arrays.copyOf(perpB, count);
        this.section = minSection;
    }

    private static float component(Float3 vector, int axis) {
        return axis == 0 ? vector.x() : axis == 1 ? vector.y() : vector.z();
    }

    private static int chunk(float blockCoordinate) {
        return (int) Math.floor(blockCoordinate / BLOCKS_PER_CHUNK);
    }

    /**
     * Near-to-far ring over longitudinal chunk coordinates: distance 0, +1, -1, +2, -2 …
     * clipped to the prism range; monotonic in distance from the shadow camera.
     */
    private static int[] buildLongitudinalOrder(int cam, int min, int max) {
        int span = max - min + 1;
        if (span <= 0) {
            return new int[0];
        }
        int[] order = new int[span];
        int count = 0;
        order[count++] = cam;
        for (int distance = 1; count < span; distance++) {
            int towardView = cam + distance;
            if (towardView <= max) {
                order[count++] = towardView;
            }
            int towardLight = cam - distance;
            if (towardLight >= min && count < span) {
                order[count++] = towardLight;
            }
        }
        return java.util.Arrays.copyOf(order, count);
    }

    @Override
    public boolean advance(int[] chunkOut) {
        if (chunkOut == null || chunkOut.length < 3) {
            throw new IllegalArgumentException("chunkOut must carry three slots");
        }
        if (longitudinalIndex >= longitudinalOrder.length) {
            return false;
        }
        chunkOut[longitudinalAxis] = longitudinalOrder[longitudinalIndex];
        chunkOut[perpAxisA] = camPerpA + perpendicularA[perpendicularIndex];
        chunkOut[perpAxisB] = camPerpB + perpendicularB[perpendicularIndex];
        step();
        return true;
    }

    private void step() {
        section++;
        if (section <= maxSection) {
            return;
        }
        section = minSection;
        perpendicularIndex++;
        if (perpendicularIndex < perpendicularA.length) {
            return;
        }
        perpendicularIndex = 0;
        longitudinalIndex++;
    }

    @Override
    public int remaining() {
        if (longitudinalIndex >= longitudinalOrder.length) {
            return 0;
        }
        int sectionTotal = maxSection - minSection + 1;
        int perpTotal = perpendicularA.length;
        int consumedInSection = section - minSection;
        int fullLongitudinal = longitudinalOrder.length - longitudinalIndex - 1;
        int currentPerpRemainder = (perpTotal - perpendicularIndex) * sectionTotal
                - consumedInSection;
        return fullLongitudinal * perpTotal * sectionTotal + currentPerpRemainder;
    }
}
