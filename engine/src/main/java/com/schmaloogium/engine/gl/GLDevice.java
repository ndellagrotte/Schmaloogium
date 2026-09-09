// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.List;

/**
 * The GL facade root (PHASE_1_DOC §4.7.4, [D-P1-15]): a small set of role-oriented
 * service interfaces addressing GL objects through opaque handle types — not a thin 1:1
 * mirror of GL verbs. Seven baseline services plus the bounded vertex-input service of
 * §4.7.6, each a role rather than a GL module.
 */
public interface GLDevice {

    GLCapabilityProfile capabilities();

    ShaderService shaders();

    UniformService uniforms();

    TextureService textures();

    FramebufferService framebuffers();

    StateService state();

    DrawService draw();

    DebugService debug();

    VertexInputService vertexInputs();

    /**
     * Errors observed since the last drain; empty when clean. Draining clears ([D-P1-30]).
     *
     * <p>NOT a query of GL's state on demand, in either direction: the drain elides
     * entirely when no mutating FACADE call has occurred since the previous one, so an
     * empty return means "nothing of ours mutated, or nothing errored" — it does not mean
     * the per-context error flag is clear. And a non-empty return may carry an error no
     * facade call caused.
     *
     * <p>ORDER: under the per-call debug cadence the list is in call order. Under the
     * default cadence it is the order the driver's error flags come back in, which GL
     * does not define — a drain that returns several elements is reporting several FLAGS,
     * not a sequence ([D-P1-30]).
     *
     * <p>This is the signal §G2.4's rung 2 acts on, at the attribution granularity
     * [D-P1-32] states — see {@link GLError} and {@link ReplayAwareGLError}.
     */
    List<GLError> drainErrors();
}
