// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
// world1 (overworld) override: distinct terrain fragment colour.

#version 120

void main() {
    gl_FragData[0] = vec4(0.5, 0.5, 0.0, 1.0);
}
