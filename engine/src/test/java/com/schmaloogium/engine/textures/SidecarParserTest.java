// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.textures.internal.SidecarOutcome;
import com.schmaloogium.engine.textures.internal.SidecarParser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The §4.3.5 bounded sidecar parser contract: strict single-document UTF-8 JSON object,
 * duplicate rejection, field shape, limit handling and byte-evidence/digest law.
 * JSON escape sequences are built via {@link #ESC} because a literal backslash-u in Java
 * source would be consumed by the unicode preprocessor.
 */
class SidecarParserTest {

    /** One backslash character, free of unicode-escape preprocessing hazards. */
    private static final String ESC = "\\";

    private static SidecarOutcome parse(String json) {
        return SidecarParser.parse(json.getBytes(StandardCharsets.UTF_8), Optional.empty());
    }

    private static SidecarOutcome parse(byte[] bytes) {
        return SidecarParser.parse(bytes, Optional.empty());
    }

    private static byte[] utf8(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** MALFORMED shape law: DISCARDED presences, no effective fields. */
    private static void assertMalformed(SidecarOutcome outcome, SidecarOutcome.FailureClass clazz) {
        assertEquals(SidecarOutcome.Outcome.MALFORMED, outcome.outcome());
        assertEquals(clazz, outcome.failureClass());
        assertEquals(SidecarOutcome.Presence.DISCARDED, outcome.blurPresence());
        assertEquals(SidecarOutcome.Presence.DISCARDED, outcome.clampPresence());
        assertTrue(outcome.effectiveBlur().isEmpty());
        assertTrue(outcome.effectiveClamp().isEmpty());
    }

    /** Non-limit malformed outcomes read the complete bounded input. */
    private static void assertCompleteEvidence(SidecarOutcome outcome, byte[] input) {
        assertEquals(SidecarOutcome.ByteEvidence.COMPLETE, outcome.byteEvidence());
        assertEquals(sha256Hex(input), outcome.byteSha256().orElseThrow());
    }

    private static void assertJsonFailure(String json) {
        byte[] bytes = utf8(json);
        SidecarOutcome outcome = parse(bytes);
        assertMalformed(outcome, SidecarOutcome.FailureClass.JSON);
        assertCompleteEvidence(outcome, bytes);
    }

    private static void assertEncodingFailure(byte[] bytes) {
        SidecarOutcome outcome = parse(bytes);
        assertMalformed(outcome, SidecarOutcome.FailureClass.ENCODING);
        assertCompleteEvidence(outcome, bytes);
    }

    @Test
    void validMinimalObject() {
        SidecarOutcome outcome = parse("{}");
        assertEquals(SidecarOutcome.Outcome.VALID, outcome.outcome());
        assertEquals(SidecarOutcome.FailureClass.NONE, outcome.failureClass());
        assertEquals(SidecarOutcome.Presence.OMITTED, outcome.blurPresence());
        assertEquals(SidecarOutcome.Presence.OMITTED, outcome.clampPresence());
        assertTrue(outcome.effectiveBlur().isEmpty());
        assertTrue(outcome.effectiveClamp().isEmpty());
        assertCompleteEvidence(outcome, utf8("{}"));
    }

    @Test
    void bomAndJsonWhitespaceAccepted() {
        byte[] input = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = utf8("  {\n\t\"texture\" : {} }  ");
        byte[] all = new byte[input.length + body.length];
        System.arraycopy(input, 0, all, 0, input.length);
        System.arraycopy(body, 0, all, input.length, body.length);
        SidecarOutcome outcome = parse(all);
        assertEquals(SidecarOutcome.Outcome.VALID, outcome.outcome());
        assertCompleteEvidence(outcome, all);
        // a mid-document or trailing BOM is not JSON whitespace
        assertJsonFailure("{} " + '\uFEFF');
    }

    @Test
    void textureCombinations() {
        // texture absent: both fields omitted
        SidecarOutcome absent = parse("{}");
        assertEquals(SidecarOutcome.Presence.OMITTED, absent.blurPresence());
        assertEquals(SidecarOutcome.Presence.OMITTED, absent.clampPresence());
        // texture present but empty
        SidecarOutcome empty = parse("{\"texture\":{}}");
        assertEquals(SidecarOutcome.Outcome.VALID, empty.outcome());
        assertEquals(SidecarOutcome.Presence.OMITTED, empty.blurPresence());
        assertEquals(SidecarOutcome.Presence.OMITTED, empty.clampPresence());
        // blur true / false, clamp omitted
        SidecarOutcome blurTrue = parse("{\"texture\":{\"blur\":true}}");
        assertEquals(SidecarOutcome.Presence.TRUE, blurTrue.blurPresence());
        assertEquals(Optional.of(Boolean.TRUE), blurTrue.effectiveBlur());
        assertEquals(SidecarOutcome.Presence.OMITTED, blurTrue.clampPresence());
        assertTrue(blurTrue.effectiveClamp().isEmpty());
        SidecarOutcome blurFalse = parse("{\"texture\":{\"blur\":false}}");
        assertEquals(SidecarOutcome.Presence.FALSE, blurFalse.blurPresence());
        assertEquals(Optional.of(Boolean.FALSE), blurFalse.effectiveBlur());
        // clamp true / false, blur omitted
        SidecarOutcome clampTrue = parse("{\"texture\":{\"clamp\":true}}");
        assertEquals(SidecarOutcome.Presence.TRUE, clampTrue.clampPresence());
        assertEquals(Optional.of(Boolean.TRUE), clampTrue.effectiveClamp());
        assertEquals(SidecarOutcome.Presence.OMITTED, clampTrue.blurPresence());
        SidecarOutcome clampFalse = parse("{\"texture\":{\"clamp\":false}}");
        assertEquals(SidecarOutcome.Presence.FALSE, clampFalse.clampPresence());
        assertEquals(Optional.of(Boolean.FALSE), clampFalse.effectiveClamp());
        // both explicit, independent
        SidecarOutcome both = parse("{\"texture\":{\"blur\":true,\"clamp\":false}}");
        assertEquals(SidecarOutcome.Presence.TRUE, both.blurPresence());
        assertEquals(SidecarOutcome.Presence.FALSE, both.clampPresence());
        assertEquals(Optional.of(Boolean.TRUE), both.effectiveBlur());
        assertEquals(Optional.of(Boolean.FALSE), both.effectiveClamp());
    }

    @Test
    void strictSyntaxViolationsAreJson() {
        assertJsonFailure("{\"a\":1,}"); // trailing comma in root
        assertJsonFailure("{\"texture\":{\"blur\":true,}}"); // trailing comma in texture
        assertJsonFailure("/* c */ {\"texture\":{}}"); // comment before
        assertJsonFailure("{\"texture\":{}} // trailing comment"); // comment after
        assertJsonFailure("{\"a\":1}{\"b\":2}"); // two documents
        assertJsonFailure(""); // empty input
        assertJsonFailure("   "); // whitespace only
        assertJsonFailure(String.valueOf('\uFEFF')); // BOM only
        assertJsonFailure("{\"a\":NaN}"); // nonfinite tokens
        assertJsonFailure("{\"a\":Infinity}");
        assertJsonFailure("{\"a\":-Infinity}");
        assertJsonFailure("{\"a\":01}"); // leading zero
        assertJsonFailure("{\"a\":+1}"); // bare plus
        assertJsonFailure("{\"a\":.5}"); // no integer part
        assertJsonFailure("{\"a\":5.}"); // no fraction digits
        assertJsonFailure("{\"a\":1e}"); // empty exponent
        assertJsonFailure("{\"texture\":"); // unterminated object
        assertJsonFailure("{\"texture\":\"oops}"); // unterminated string
        assertJsonFailure("{\"texture\":{}}x"); // trailing garbage after document
        assertJsonFailure("{\"a\" \"b\"}"); // missing colon
        assertJsonFailure("{\"a\":1 \"b\":2}"); // missing comma
        assertJsonFailure("[1,2]"); // root must be an object
        assertJsonFailure("\"text\""); // root must be an object
        assertJsonFailure("{\"a\":'b'}"); // single quotes are not JSON
    }

    @Test
    void badEscapesAndRawControlCharactersAreJson() {
        assertJsonFailure("{\"a\":\"" + ESC + "x\"}"); // unknown escape
        assertJsonFailure("{\"a\":\"" + ESC + "\"}"); // bare backslash before quote
        assertJsonFailure("{\"a\":\"" + ESC + "u12\"}"); // short unicode escape
        assertJsonFailure("{\"a\":\"" + ESC + "uGHIJ\"}"); // non-hex unicode escape
    }

    @Test
    void loneSurrogateEscapesAreEncoding() {
        assertEncodingFailure(utf8("{\"a\":\"" + ESC + "ud800\"}")); // lone high surrogate
        assertEncodingFailure(utf8("{\"a\":\"" + ESC + "udc00\"}")); // lone low surrogate
        assertEncodingFailure(utf8("{\"texture\":{\"blur\":\"" + ESC + "ud83d\"}}"));
        // high surrogate must be completed immediately by a low-surrogate escape
        assertEncodingFailure(utf8("{\"a\":\"" + ESC + "ud800A\"}"));
        assertEncodingFailure(utf8("{\"a\":\"" + ESC + "ud800" + ESC + "u0041\"}"));
    }

    @Test
    void invalidUtf8BytesAreEncoding() {
        assertEncodingFailure(new byte[]{'{', '"', 'a', '"', ':', '"', (byte) 0xFF, '"', '}'});
        assertEncodingFailure(new byte[]{'{', '"', (byte) 0xC3, '"', '}'}); // truncated 2-byte
        assertEncodingFailure(new byte[]{'{', '"', 'a', '"', ':', '"', (byte) 0xED, (byte) 0xA0,
            (byte) 0x80, '"', '}'}); // UTF-8-encoded surrogate
        assertEncodingFailure(new byte[]{'{', '"', 'a', '"', ':', (byte) 0x80, '}'});
    }

    @Test
    void duplicateMembersAreMalformedAnywhere() {
        byte[] root = utf8("{\"a\":1,\"a\":2}");
        SidecarOutcome rootOutcome = parse(root);
        assertMalformed(rootOutcome, SidecarOutcome.FailureClass.DUPLICATE_MEMBER);
        assertCompleteEvidence(rootOutcome, root);

        byte[] texture = utf8("{\"texture\":{\"blur\":true,\"blur\":false}}");
        SidecarOutcome textureOutcome = parse(texture);
        assertMalformed(textureOutcome, SidecarOutcome.FailureClass.DUPLICATE_MEMBER);
        assertCompleteEvidence(textureOutcome, texture);

        byte[] ignored = utf8("{\"x\":{\"a\":[1,{\"b\":2,\"b\":3}]}}");
        SidecarOutcome ignoredOutcome = parse(ignored);
        assertMalformed(ignoredOutcome, SidecarOutcome.FailureClass.DUPLICATE_MEMBER);
        assertCompleteEvidence(ignoredOutcome, ignored);

        // duplicates compare decoded names, not raw escape text
        byte[] decoded = utf8("{\"a\":1,\"" + ESC + "u0061\":2}");
        assertMalformed(parse(decoded), SidecarOutcome.FailureClass.DUPLICATE_MEMBER);
    }

    @Test
    void nonBooleanFieldsAreShape() {
        for (String field : new String[]{"blur", "clamp"}) {
            byte[] nullValue = utf8("{\"texture\":{\"" + field + "\":null}}");
            SidecarOutcome nullOutcome = parse(nullValue);
            assertMalformed(nullOutcome, SidecarOutcome.FailureClass.SHAPE);
            assertCompleteEvidence(nullOutcome, nullValue);
            assertMalformed(parse("{\"texture\":{\"" + field + "\":\"true\"}}"),
                SidecarOutcome.FailureClass.SHAPE);
            assertMalformed(parse("{\"texture\":{\"" + field + "\":1}}"),
                SidecarOutcome.FailureClass.SHAPE);
            assertMalformed(parse("{\"texture\":{\"" + field + "\":[]}}"),
                SidecarOutcome.FailureClass.SHAPE);
            assertMalformed(parse("{\"texture\":{\"" + field + "\":{}}}"),
                SidecarOutcome.FailureClass.SHAPE);
        }
    }

    @Test
    void textureMustBeAnObject() {
        assertMalformed(parse("{\"texture\":1}"), SidecarOutcome.FailureClass.SHAPE);
        assertMalformed(parse("{\"texture\":null}"), SidecarOutcome.FailureClass.SHAPE);
        assertMalformed(parse("{\"texture\":[]}"), SidecarOutcome.FailureClass.SHAPE);
        assertMalformed(parse("{\"texture\":\"x\"}"), SidecarOutcome.FailureClass.SHAPE);
        assertMalformed(parse("{\"texture\":true}"), SidecarOutcome.FailureClass.SHAPE);
        // texture checked before blur: the failing member is texture, no field overrides
        SidecarOutcome outcome = parse("{\"texture\":1,\"blur\":\"x\"}");
        assertMalformed(outcome, SidecarOutcome.FailureClass.SHAPE);
        assertEquals(Optional.empty(), outcome.effectiveBlur());
        assertEquals(Optional.empty(), outcome.effectiveClamp());
    }

    @Test
    void unknownMembersValidatedButIgnored() {
        // invalid unknown value: syntax error
        assertJsonFailure("{\"unknown\":{\"a\":[1,2}}");
        assertJsonFailure("{\"unknown\":nope}");
        // valid unknown members at root and inside texture: ignored, outcome VALID
        SidecarOutcome outcome = parse(
            "{\"unknown\":{\"a\":[1,2,{\"deep\":null}],\"s\":\"" + ESC + "u00e9\"},"
                + "\"texture\":{\"unknown2\":{\"x\":[true,false]},\"blur\":false}}");
        assertEquals(SidecarOutcome.Outcome.VALID, outcome.outcome());
        assertEquals(SidecarOutcome.Presence.FALSE, outcome.blurPresence());
        assertEquals(SidecarOutcome.Presence.OMITTED, outcome.clampPresence());
        // root-level blur/clamp are unknown members, never field overrides
        SidecarOutcome rootLevel = parse("{\"blur\":true,\"clamp\":false}");
        assertEquals(SidecarOutcome.Outcome.VALID, rootLevel.outcome());
        assertEquals(SidecarOutcome.Presence.OMITTED, rootLevel.blurPresence());
        assertEquals(SidecarOutcome.Presence.OMITTED, rootLevel.clampPresence());
    }

    @Test
    void numberAndEscapeFormsAccepted() {
        SidecarOutcome outcome = parse(
            "{\"n1\":0,\"n2\":-0.5,\"n3\":1e10,\"n4\":-2.5E-3,\"n5\":123456789,"
                + "\"esc\":\"" + ESC + "u0041" + ESC + "uD83D" + ESC + "uDE00"
                + ESC + "t" + ESC + "n" + ESC + ESC + ESC + "\"" + ESC + "/\","
                + "\"texture\":{\"blur\":true}}");
        assertEquals(SidecarOutcome.Outcome.VALID, outcome.outcome());
        assertEquals(SidecarOutcome.Presence.TRUE, outcome.blurPresence());
    }

    @Test
    void byteLimitStopsWithNoEvidence() {
        byte[] over = new byte[SidecarParser.MAX_BYTES + 1];
        over[0] = '{';
        SidecarOutcome outcome = parse(over);
        assertMalformed(outcome, SidecarOutcome.FailureClass.BYTE_LIMIT);
        assertEquals(SidecarOutcome.ByteEvidence.NONE, outcome.byteEvidence());
        assertTrue(outcome.byteSha256().isEmpty());
        // exactly the limit parses: pad with an ignored string member
        byte[] prefixBytes = utf8("{\"pad\":\"");
        byte[] exact = new byte[SidecarParser.MAX_BYTES];
        System.arraycopy(prefixBytes, 0, exact, 0, prefixBytes.length);
        java.util.Arrays.fill(exact, prefixBytes.length, exact.length - 2, (byte) 'a');
        exact[exact.length - 2] = '"';
        exact[exact.length - 1] = '}';
        SidecarOutcome exactOutcome = parse(exact);
        assertEquals(SidecarOutcome.Outcome.VALID, exactOutcome.outcome());
        assertCompleteEvidence(exactOutcome, exact);
    }

    @Test
    void depthLimitStopsWithNoEvidence() {
        // root object = depth 1; 64 nested arrays in an ignored member reach depth 65
        String over = "{\"u\":" + "[".repeat(64) + "]".repeat(64) + "}";
        SidecarOutcome outcome = parse(utf8(over));
        assertMalformed(outcome, SidecarOutcome.FailureClass.DEPTH_LIMIT);
        assertEquals(SidecarOutcome.ByteEvidence.NONE, outcome.byteEvidence());
        assertTrue(outcome.byteSha256().isEmpty());
        // boundary: 63 nested arrays reach depth 64 and are legal
        String boundary = "{\"u\":" + "[".repeat(63) + "]".repeat(63) + "}";
        assertEquals(SidecarOutcome.Outcome.VALID, parse(utf8(boundary)).outcome());
        // depth accumulates through mixed containers: root 1 + texture 2 + 63 arrays = 65
        String mixed = "{\"texture\":{\"t\":" + "[".repeat(63) + "]".repeat(63) + "}}";
        assertMalformed(parse(utf8(mixed)), SidecarOutcome.FailureClass.DEPTH_LIMIT);
        String mixedBoundary = "{\"texture\":{\"t\":" + "[".repeat(62) + "]".repeat(62) + "}}";
        assertEquals(SidecarOutcome.Outcome.VALID, parse(utf8(mixedBoundary)).outcome());
    }

    @Test
    void completeMalformedInputKeepsFullByteHash() {
        byte[] input = utf8("{\"texture\":{\"blur\":\"yes\"}}");
        SidecarOutcome outcome = parse(input);
        assertMalformed(outcome, SidecarOutcome.FailureClass.SHAPE);
        assertCompleteEvidence(outcome, input);
    }

    @Test
    void digestEqualityAndSensitivity() {
        byte[] input = utf8("{\"unknown\":1,\"texture\":{\"blur\":true}}");
        SidecarOutcome first = SidecarParser.parse(input, Optional.of("pack.png.mcmeta"));
        SidecarOutcome second = SidecarParser.parse(input, Optional.of("pack.png.mcmeta"));
        assertEquals(first.digest(), second.digest());
        assertEquals(first, second);
        // path participates in the digest
        assertNotEquals(first.digest(),
            SidecarParser.parse(input, Optional.of("other.png.mcmeta")).digest());
        assertNotEquals(first.digest(), SidecarParser.parse(input, Optional.empty()).digest());
        // one-byte change in an ignored member value changes the digest
        byte[] changed = utf8("{\"unknown\":2,\"texture\":{\"blur\":true}}");
        assertNotEquals(first.digest(),
            SidecarParser.parse(changed, Optional.of("pack.png.mcmeta")).digest());
        // the recorded byte hash is the exact SHA-256 of all bounded bytes
        assertEquals(sha256Hex(input), first.byteSha256().orElseThrow());
        assertFalse(first.normalizedPath().isEmpty());
    }
}
