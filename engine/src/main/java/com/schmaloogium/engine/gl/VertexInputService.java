// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import com.schmaloogium.engine.vertex.VertexLayout;
import com.schmaloogium.engine.vertex.VertexInputPlan;

/**
 * The bounded vertex input grant's service (PHASE_1_DOC §4.7.6, R10-1/D-P1-50): the exact
 * requested input-state boundary — not buffer ownership, a renderer extension or a new
 * draw API. The eighth service on {@link GLDevice}.
 *
 * <p>{@code bind} requires the render thread/current context, an authentic live source, a
 * complete supported layout, exact range/count/stride with overflow-safe bounds, valid
 * field offsets/storage/normalization/locations and complete plan/mode compatibility.
 * Rejected ({@link VertexBindResult.Rejected}) is mutation-free and appends no native
 * call; {@link VertexBindResult.Failed} after partial mutation carries a diagnostic id and
 * implies the backend already restored the saved predecessor. {@link VertexBinding} is an
 * opaque LIFO render-thread restoration obligation: {@code restore} accepts only the same
 * device's live top binding.
 */
public interface VertexInputService {

    VertexBindResult bind(VertexSource source, VertexLayout layout,
                          VertexInputPlan plan, VertexBindMode mode);

    void restore(VertexBinding binding);

    /**
     * Re-issues the live top binding's pointers for a new source of the same layout, plan
     * and mode (Task F, P1 amendment): the predecessor captured by {@code bind} is kept,
     * so a chunk layer pays one capture and one restore while each chunk VBO re-points.
     * Rejections are mutation-free; the returned binding is the same live top binding.
     */
    VertexBindResult rebind(VertexBinding binding, VertexSource source);

    /**
     * Sets the plan's generic locations to their floating neutral current values with
     * their arrays disabled (PHASE_10_DOC §4.6: identity {@code (0,0,0)}, midpoint
     * {@code (0,0)}, tangent {@code (0,0,0,1)}), for draws of products that carry no
     * extended fields while an extended program is active. Render thread only.
     */
    void setNeutralCurrentValues(VertexInputPlan plan);
}
