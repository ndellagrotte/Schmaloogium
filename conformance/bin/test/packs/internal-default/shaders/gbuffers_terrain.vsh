// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
// The synthetic internal default pack fixture ([D-P2-11] corpus): the harness's
// always-available baseline pack for T0 legs. GPL-3.0-or-later, ours. The real
// engine-shipped default pack replaces this fixture when its provider lands.

#version 120

void main() {
    gl_Position = ftransform();
    gl_TexCoord[0] = gl_MultiTexCoord0;
}
