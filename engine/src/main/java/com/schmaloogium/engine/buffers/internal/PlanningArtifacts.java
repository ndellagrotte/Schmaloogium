// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.registry.DrawRoutingSlot;
import com.schmaloogium.engine.registry.ProgramSlotId;

import java.util.List;
import java.util.Map;

/**
 * The concrete immutable planning payload inside {@link com.schmaloogium.engine.buffers.BufferPlan}
 * (PHASE_5_DOC §4.1 step 10): resolved sizing and inventory, ordered per-pass resource
 * descriptors, clear policies, fixed texture-unit rows and teardown order. Value equality
 * covers all artifacts; no GL handle, generation or side state appears here.
 */
public record PlanningArtifacts(
        BufferSizing sizing,
        BufferInventory inventory,
        int depthTextureCount,
        List<ColorPlan> colors,
        Map<ProgramSlotId, PlannedRoute> routes,
        List<List<String>> fullscreenUnitRows,
        List<List<String>> gbuffersUnitRows,
        List<LogicalBuffer> teardownOrder,
        BufferResourceProjection plannedProjection) {

    /** Per-buffer declarative plan for one dense colortex row (§4.1.1). */
    record ColorPlan(LogicalBuffer buffer, ColorAttachmentFormat requestedFormat, boolean clear,
            ResourceClearPolicy clearPolicy) {
    }

    /** One resolved pass route: positional slots plus the resolved attachment buffers. */
    record PlannedRoute(ProgramSlotId slot, List<DrawRoutingSlot> positional,
            List<LogicalBuffer> writeBuffers) {
    }

    public PlanningArtifacts {
        sizing = java.util.Objects.requireNonNull(sizing, "sizing");
        inventory = java.util.Objects.requireNonNull(inventory, "inventory");
        colors = List.copyOf(colors);
        routes = Map.copyOf(routes);
        fullscreenUnitRows = frozenRows(fullscreenUnitRows);
        gbuffersUnitRows = frozenRows(gbuffersUnitRows);
        teardownOrder = List.copyOf(teardownOrder);
        plannedProjection = java.util.Objects.requireNonNull(plannedProjection,
            "plannedProjection");
    }

    private static List<List<String>> frozenRows(List<List<String>> rows) {
        java.util.Objects.requireNonNull(rows, "rows");
        return rows.stream().map(List::copyOf).toList();
    }

    /** Dense ascending colortex plans, mirroring the evidence projection's row order. */
    List<ColorPlan> colorPlans() {
        return colors;
    }
}
