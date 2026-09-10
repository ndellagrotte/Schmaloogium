// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The Phase-7-supplied replay-error observer (PHASE_6_DOC §2.2/§4.11, D-P6-27). The void
 * method returns normally only after accepting the entire report: silent rejection,
 * filtering, overflow and no-op sinks are forbidden. Delivery occurs on the render thread
 * under the outer operation's active-callback guard, before the enclosing operation
 * returns; the callback performs no GL, provider calls, lifecycle work or reentrant
 * uniform entry.
 */
public interface UniformReplayErrorSink {

    void accept(UniformReplayReport report);
}
