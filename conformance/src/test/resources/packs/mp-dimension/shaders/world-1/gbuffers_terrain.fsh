// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
// world-1 (nether) override: distinct terrain fragment colour.

#version 120

void main() {
    gl_FragData[0] = vec4(0.6, 0.0, 0.0, 1.0);
}
