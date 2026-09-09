// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

#version 120

#include "/include/depth01.glsl"

void main() {
    gl_Position = ftransform();
}
