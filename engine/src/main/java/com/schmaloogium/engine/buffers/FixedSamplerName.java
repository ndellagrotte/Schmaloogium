// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** The closed fixed-sampler-name domain (Phase 5 Doc §2.4) with exact pack-facing spellings. */
public enum FixedSamplerName {
    TEXTURE("texture"), TEX("tex"), LIGHTMAP("lightmap"), NORMALS("normals"),
    SPECULAR("specular"), SHADOWTEX0("shadowtex0"), WATERSHADOW("watershadow"),
    SHADOW("shadow"), SHADOWTEX1("shadowtex1"), DEPTHTEX0("depthtex0"),
    GDEPTHTEX("gdepthtex"), GAUX1("gaux1"), GAUX2("gaux2"), GAUX3("gaux3"),
    GAUX4("gaux4"), DEPTHTEX1("depthtex1"), DEPTHTEX2("depthtex2"),
    SHADOWCOLOR0("shadowcolor0"), SHADOWCOLOR("shadowcolor"), SHADOWCOLOR1("shadowcolor1"),
    NOISETEX("noisetex"), COLORTEX0("colortex0"), COLORTEX1("colortex1"),
    COLORTEX2("colortex2"), COLORTEX3("colortex3"), COLORTEX4("colortex4"),
    COLORTEX5("colortex5"), COLORTEX6("colortex6"), COLORTEX7("colortex7"),
    GCOLOR("gcolor"), GDEPTH("gdepth"), GNORMAL("gnormal"), COMPOSITE("composite");

    private final String exactName;

    FixedSamplerName(String exactName) {
        this.exactName = exactName;
    }

    public String exactName() {
        return exactName;
    }
}
