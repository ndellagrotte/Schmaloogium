// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.schmaloogium.engine.config.ConstScanner;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Classic packs ship CRLF sources (SEUS Renewed, projectLUMA). Every line-anchored scanner
 * must see the same declarations it sees for LF text: before Task D the trailing CR defeated
 * {@code ;.*$} under {@code matches()}, every sampler and const option vanished, and every
 * CRLF pack rendered black with default constants.
 */
class CrlfSourceScanTest {

    private static final String LF = "#version 120\n"
        + "uniform sampler2D texture;\n"
        + "uniform sampler2D noisetex;\n"
        + "const int shadowMapResolution = 2048;\n"
        + "const float sunPathRotation = -40.0; // comment\n";

    @Test
    void uniformScannerSeesCrlfDeclarationsExactlyAsLf() {
        List<UniformDeclScanner.Finding> lf = UniformDeclScanner.scan(LF);
        List<UniformDeclScanner.Finding> crlf = UniformDeclScanner.scan(LF.replace("\n", "\r\n"));
        assertEquals(2, lf.size());
        assertEquals(lf, crlf, "CR is whitespace to GLSL; the catalog must not depend on it");
        assertEquals(List.of(2, 3), crlf.stream().map(UniformDeclScanner.Finding::line).toList());
    }

    @Test
    void constScannerSeesCrlfOptionsExactlyAsLf() {
        Map<String, ConstScanner.Finding> lf = ConstScanner.scan(LF);
        Map<String, ConstScanner.Finding> crlf = ConstScanner.scan(LF.replace("\n", "\r\n"));
        assertEquals(2, lf.size());
        assertEquals(lf, crlf);
        assertEquals("2048", crlf.get("shadowMapResolution").value());
        assertEquals("-40.0", crlf.get("sunPathRotation").value());
    }

    @Test
    void decodeNormalizesCrlfAndLoneCrWithoutTouchingLfText() {
        assertSame(LF, SourceIndex.normalizeLineEndings(LF), "LF text is returned as-is");
        assertEquals(LF, SourceIndex.normalizeLineEndings(LF.replace("\n", "\r\n")));
        assertEquals(LF, SourceIndex.normalizeLineEndings(LF.replace("\n", "\r")));
        assertEquals(LF.split("\n", -1).length,
            SourceIndex.normalizeLineEndings(LF.replace("\n", "\r\n")).split("\n", -1).length,
            "line counts survive, so attribution stays aligned");
    }
}
