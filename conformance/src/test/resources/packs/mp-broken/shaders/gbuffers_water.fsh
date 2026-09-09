// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

#version 120

// Deliberately ill-formed GLSL: this is the ownBuild=FAILED vector for the Phase 4
// registry leg. The front-end source index does not compile GLSL, so the pack parses;
// T3 clause 2 (no silent fallback) is what eventually observes this slot's failure.
void main() {
    gl_FragData[0] = vec4(;
}
