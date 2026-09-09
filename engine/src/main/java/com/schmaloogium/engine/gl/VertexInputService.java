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
}
