// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ShadowMipmapPolicy;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.shadow.internal.ShadowPlanFactoryImpl;
import com.schmaloogium.engine.uniforms.Double3;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.OptionalFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** §4.1/§4.3/§4.4/§4.7 headless cases: health gates, plan law, traversal admission. */
class ShadowPlanTraversalTest {

    private static ShadowPolicy policy() {
        return new ShadowPolicy(new OptionalFloat.Absent(), 160f, 0.85f, 4f, 0f, true, true,
                new ShadowMipmapPolicy(List.of()),
                new ShadowPcfPolicy(Set.of(
                        new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0)))));
    }

    private static ShadowHookHealth healthy() {
        return ShadowHookHealth.of(ShadowHookHealth.catalogue().stream()
                .map(id -> new ShadowHookRow(id, 1, 1, HookDisposition.HEALTHY)).toList());
    }

    private static ShadowPlanResult plan(ShadowPolicy policy, ShadowHookHealth health,
            boolean requested) {
        return new ShadowPlanFactoryImpl().plan(new ShadowPlanInput(policy, health, requested));
    }

    private static ShadowPlan readyPlan() {
        return assertInstanceOf(ShadowPlanResult.Ready.class,
                plan(policy(), healthy(), true)).plan();
    }

    @Test
    void catalogueIsCanonicalAndStable() {
        List<String> ids = ShadowHookHealth.catalogue();
        assertEquals(66, ids.size());
        List<String> sorted = new ArrayList<>(ids);
        java.util.Collections.sort(sorted);
        assertEquals(sorted, ids);
        assertEquals(ids, ShadowHookHealth.catalogue());
        assertFalse(ShadowHookHealth.catalogue().isEmpty());
    }

    @Test
    void healthOfVerifiesFingerprints() {
        ShadowHookHealth health = healthy();
        assertTrue(health.shadowEnabled());
        assertDoesNotThrow(() -> ShadowHookHealth.of(health.rows()));
        // Tampered fingerprint row must be rejected structurally.
        List<ShadowHookRow> tampered = healthy().rows().stream()
                .map(row -> new ShadowHookRow(row.hookId(), row.expected(), row.actual() + 1,
                        row.disposition()))
                .toList();
        assertThrows(IllegalArgumentException.class, () -> ShadowHookHealth.of(tampered));
        // Duplicate and reordered rows are rejected.
        List<ShadowHookRow> rows = healthy().rows();
        List<ShadowHookRow> duplicated = new ArrayList<>(rows);
        duplicated.add(rows.get(0));
        assertThrows(IllegalArgumentException.class, () -> ShadowHookHealth.of(duplicated));
        List<ShadowHookRow> reordered = new ArrayList<>(rows);
        java.util.Collections.swap(reordered, 0, 1);
        assertThrows(IllegalArgumentException.class, () -> ShadowHookHealth.of(reordered));
    }

    @Test
    void gateRowsDisableShadowButCloudsStayOptional() {
        // Any disabled non-cloud gate row disables the whole shadow subsystem.
        String gateHook = ShadowHookHealth.catalogue().stream()
                .filter(ShadowHookHealth::gatesShadow)
                .findFirst().orElseThrow();
        List<ShadowHookRow> gateDisabled = healthy().rows().stream()
                .map(row -> row.hookId().equals(gateHook)
                        ? new ShadowHookRow(row.hookId(), 1, 0, HookDisposition.FEATURE_DISABLED)
                        : row)
                .toList();
        ShadowHookHealth health = ShadowHookHealth.of(gateDisabled);
        assertFalse(health.shadowEnabled());
        ShadowPlanResult result = plan(policy(), health, true);
        ShadowPlanResult.Disabled disabled = assertInstanceOf(ShadowPlanResult.Disabled.class,
                result);
        assertInstanceOf(ShadowDisableReason.HookUnavailable.class, disabled.reason());
        // A disabled cloud hook keeps shadows enabled.
        List<ShadowHookRow> cloudDisabled = healthy().rows().stream()
                .map(row -> row.hookId().equals("H8-CLOUD-01-RESOLVE")
                        ? new ShadowHookRow(row.hookId(), 1, 0, HookDisposition.FEATURE_DISABLED)
                        : row)
                .toList();
        assertTrue(ShadowHookHealth.of(cloudDisabled).shadowEnabled());
    }

    @Test
    void notRequestedWinsAndInvalidPoliciesAreRejected() {
        assertInstanceOf(ShadowPlanResult.NotRequested.class, plan(policy(), healthy(), false));
        assertEquals("shadowDistanceRenderMul",
                policyField(new ShadowPolicy(new OptionalFloat.Absent(), 160f, 1.5f, 4f, 0f,
                        true, true, new ShadowMipmapPolicy(List.of()),
                        new ShadowPcfPolicy(Set.of()))));
        assertEquals("shadowMapFov",
                policyField(new ShadowPolicy(new OptionalFloat.Present(200f), 160f, 0.85f, 4f,
                        0f, true, true, new ShadowMipmapPolicy(List.of()),
                        new ShadowPcfPolicy(Set.of()))));
        // A shadowcolor member is outside the compare-depth family.
        assertEquals("pcf",
                policyField(new ShadowPolicy(new OptionalFloat.Absent(), 160f, 0.85f, 4f, 0f,
                        true, true, new ShadowMipmapPolicy(List.of()),
                        new ShadowPcfPolicy(Set.of(new LogicalBuffer(BufferDomain.SHADOWCOLOR,
                                new BufferIndex(0)))))));
    }

    private static String policyField(ShadowPolicy policy) {
        ShadowPlanResult.Disabled disabled = assertInstanceOf(ShadowPlanResult.Disabled.class,
                plan(policy, healthy(), true));
        return assertInstanceOf(ShadowDisableReason.InvalidPolicy.class, disabled.reason())
                .field();
    }

    @Test
    void readyPlanFingerprintsAreContentBound() {
        ShadowPlan a = readyPlan();
        ShadowPlan b = readyPlan();
        assertEquals(a.fingerprint(), b.fingerprint());
        ShadowPolicy otherDistance = new ShadowPolicy(new OptionalFloat.Absent(), 128f, 0.85f,
                4f, 0f, true, true, new ShadowMipmapPolicy(List.of()), new ShadowPcfPolicy(
                        Set.of(new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0)))));
        ShadowPlan c = assertInstanceOf(ShadowPlanResult.Ready.class,
                plan(otherDistance, healthy(), true)).plan();
        assertNotEquals(a.fingerprint(), c.fingerprint());
        // The plan's celestial policy delegates to CelestialMath.angles.
        assertEquals(CelestialMath.angles(0.25f), a.celestialPolicy().sample(0.25f));
        assertEquals(CelestialMath.angles(0.75f), a.celestialPolicy().sample(0.75f));
    }

    // ---- traversal planner (§4.4) ----

    @Test
    void fullLoadedViewWhenRenderMulZeroOrDistanceCoversView() {
        assertEquals(ShadowTraversalPlan.FullLoadedView.class,
                planFor(0f, 8, LIGHT_X).getClass());
        // d = ceil(160*0.5/16) = 5: full view at V<=5, prism beyond.
        assertEquals(ShadowTraversalPlan.FullLoadedView.class,
                planFor(0.5f, 5, LIGHT_X).getClass());
        assertEquals(ShadowTraversalPlan.SunAlignedPrism.class,
                planFor(0.5f, 6, LIGHT_X).getClass());
        assertEquals(ShadowTraversalPlan.SunAlignedPrism.class,
                planFor(0.5f, 9, LIGHT_X).getClass());
    }

    @Test
    void prismRadiusFromDistanceAndRenderMul() {
        ShadowTraversalPlan.SunAlignedPrism prism =
                assertInstanceOf(ShadowTraversalPlan.SunAlignedPrism.class, planFor(0.5f, 8, LIGHT_X));
        // 160 * 0.85 = 136 blocks / 16 = 8.5 -> ceil 9 (d < V = 8? no: 9 >= 8 would be
        // full view, so viewDistance 20 keeps the prism).
        assertEquals(ShadowTraversalPlan.FullLoadedView.class, planFor(0.85f, 8, LIGHT_X).getClass());
        ShadowTraversalPlan.SunAlignedPrism tight =
                assertInstanceOf(ShadowTraversalPlan.SunAlignedPrism.class,
                        ShadowTraversalPlanner.standard().plan(ShadowFrustum.disabledCulling(),
                                policyOf(0.5f, 160f), LIGHT_X, 20));
        assertEquals(5, tight.shadowRadiusChunks());
        assertEquals(20, tight.viewRadiusChunks());
        assertEquals(LIGHT_X, tight.towardLight());
    }

    @Test
    void cursorEmitsCameraRingLeadingAndSectionsInnermost() {
        // d=1, V=2, camera chunk origin, sections 0..1.
        ShadowTraversalPlan.SunAlignedPrism prism = new ShadowTraversalPlan.SunAlignedPrism(
                ShadowFrustum.disabledCulling(), 1, 2, LIGHT_X);
        ShadowTraversalChunkCursor cursor =
                ShadowTraversalChunkCursor.create(prism, new Float3(0f, 0f, 0f), 0, 1);
        // Longitudinal order [0, +1, -1, +2]; 3x3 expanding perp square; 2 sections.
        assertEquals(4 * 9 * 2, cursor.remaining());
        int[] out = new int[3];
        List<int[]> seen = new ArrayList<>();
        while (cursor.advance(out)) {
            seen.add(new int[] {out[0], out[1], out[2]});
        }
        assertEquals(0, cursor.remaining());
        // First: camera column, perp center, section 0.
        assertArrayEq(new int[] {0, 0, 0}, seen.get(0));
        // Sections innermost.
        assertArrayEq(new int[] {0, 0, 0}, seen.get(1));
        // Ring d=1 begins at (perpA, perpB) = (-1,-1) with perpA = Y.
        assertArrayEq(new int[] {0, -1, -1}, seen.get(2));
        // Next longitudinal ring is toward the view (+X).
        assertArrayEq(new int[] {1, 0, 0}, seen.get(18));
        // The toward-light trailing ring (+X is toward light for LIGHT_X) follows.
        assertArrayEq(new int[] {1, 0, 0}, seen.get(18));
        assertArrayEq(new int[] {-1, 0, 0}, seen.get(36));
        // The far view ring is last.
        assertArrayEq(new int[] {2, 0, 0}, seen.get(54));
    }

    @Test
    void nearVerticalLightUsesYAxisWithXMajorPerp() {
        Float3 up = new Float3(0.01f, 25.0f, 0f);
        float len = (float) Math.sqrt(0.01d * 0.01d + 25d * 25d);
        up = new Float3(up.x() / len, up.y() / len, up.z() / len);
        ShadowTraversalPlan.SunAlignedPrism prism = new ShadowTraversalPlan.SunAlignedPrism(
                ShadowFrustum.disabledCulling(), 1, 2, up);
        ShadowTraversalChunkCursor cursor =
                ShadowTraversalChunkCursor.create(prism, new Float3(0f, 0f, 0f), 0, 0);
        int[] out = new int[3];
        assertTrue(cursor.advance(out));
        // Longitudinal axis Y; perp A = X (major), perp B = Z. The camera column comes
        // first: (0, 0, 0) with the single section 0.
        assertArrayEq(new int[] {0, 0, 0}, out);
        assertTrue(cursor.advance(out));
        // Perp rings drain first on the camera ring: d=1 starts at (da, db) = (-1, -1).
        assertArrayEq(new int[] {-1, 0, -1}, out);
    }

    private static final Float3 LIGHT_X = new Float3(1f, 0f, 0f);

    private static ShadowPolicy policyOf(float renderMul, float distance) {
        return new ShadowPolicy(new OptionalFloat.Absent(), distance, renderMul, 4f, 0f,
                true, true, new ShadowMipmapPolicy(List.of()),
                new ShadowPcfPolicy(Set.of(
                        new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0)))));
    }

    private static ShadowTraversalPlan planFor(float renderMul, int viewDistance, Float3 light) {
        return ShadowTraversalPlanner.standard().plan(ShadowFrustum.disabledCulling(),
                policyOf(renderMul, 160f), light, viewDistance);
    }

    private static void assertArrayEq(int[] expected, int[] actual) {
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, actual);
    }

}
