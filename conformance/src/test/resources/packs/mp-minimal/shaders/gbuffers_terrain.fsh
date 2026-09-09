// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

#version 120

uniform sampler2D texture;

void main() {
    gl_FragData[0] = texture2D(texture, gl_TexCoord[0].st);
}
