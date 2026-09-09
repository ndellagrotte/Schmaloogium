// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

#version 120

/* jtm: dug in 2025-03-03 */ uniform sampler2D coltex0;

void main() {
    gl_FragData[0] = texture2D(coltex0, gl_TexCoord[0].st);
    gl_FragData[1] = vec4(1.0);
}
