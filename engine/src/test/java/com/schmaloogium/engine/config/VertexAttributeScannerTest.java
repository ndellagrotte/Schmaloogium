// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

/** The classic attribute declarations: attribute/in forms, CRLF, comments ignored. */
class VertexAttributeScannerTest {

    @Test
    void attributeDeclarationsAreFoundInEnumOrder() {
        Set<VertexAttribute> found = VertexAttributeScanner.scan(
            "#version 120\r\nattribute vec4 at_tangent;\r\nattribute vec4 mc_Entity;\r\n"
                + "attribute vec4 mc_midTexCoord;\r\nvoid main(){}\r\n");
        assertEquals(Set.of(VertexAttribute.MC_ENTITY, VertexAttribute.MC_MID_TEX_COORD,
            VertexAttribute.AT_TANGENT), found);
        assertEquals(VertexAttribute.MC_ENTITY, found.iterator().next());
    }

    @Test
    void inFormAndPrecisionQualifiersAreAccepted() {
        assertEquals(Set.of(VertexAttribute.MC_ENTITY),
            VertexAttributeScanner.scan("#version 130\nin highp vec3 mc_Entity;\n"));
    }

    @Test
    void commentsAndUsesWithoutDeclarationsAreIgnored() {
        assertEquals(Set.of(), VertexAttributeScanner.scan(
            "// attribute vec4 at_tangent;\nvoid main(){ vec4 t = at_tangent; }\n"));
        assertEquals(Set.of(), VertexAttributeScanner.scan("uniform vec4 mc_Entity;\n"));
    }
}
