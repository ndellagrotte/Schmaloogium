// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

#version 120

uniform sampler2D texture;

void main() {
    vec4 color = texture2D(texture, gl_TexCoord[0].st);
    gl_FragData[0] = vec4(color.rgb, 0.7);
}
