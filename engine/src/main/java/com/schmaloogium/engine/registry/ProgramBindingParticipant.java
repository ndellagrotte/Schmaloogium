// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One barrier position (PHASE_4_DOC §4.10). Invoked on every successful activation with the
 * handle-free descriptor and bound-uniform access; Phase 6 supplies the three production
 * implementations (sampler repoint, built-in refresh, custom refresh). A participant never
 * receives an operational handle and never invents fixed-function behavior.
 */
public interface ProgramBindingParticipant {

    BarrierParticipantResult afterBind(
            ResolvedProgramDescriptor binding,
            BarrierContext context,
            BoundProgramUniformAccess uniforms);
}
