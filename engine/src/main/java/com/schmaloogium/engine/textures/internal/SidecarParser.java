// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * The strict bounded {@code .mcmeta} sidecar parser (§4.3.5). Hand-rolled scanner, zero
 * dependencies: exactly one strict UTF-8 JSON object, optional leading UTF-8 BOM, JSON
 * whitespace only outside the object; no comments, trailing commas, second documents or
 * nonfinite number tokens ({@code NaN}/{@code Infinity}/bare signs are invalid). Duplicate
 * <em>decoded</em> member names in any object are malformed ({@code DUPLICATE_MEMBER}),
 * never last-wins. Unknown root/texture members are ignored semantically, but their values
 * must be valid bounded JSON and count toward container depth. {@code texture} is optional
 * (absent keeps both fields omitted, VALID); if present it must be an object, and its
 * {@code blur}/{@code clamp} members must be absent or JSON booleans — anything else is
 * malformed {@code SHAPE}, never coerced. Invalid UTF-8 and unpaired surrogate escapes are
 * malformed {@code ENCODING}.
 *
 * <p>Limits are local finite parser limits, not claimed author limits: more than
 * {@link #MAX_BYTES} bytes or container depth above {@link #MAX_DEPTH} (root object = depth
 * 1) is a MALFORMED outcome ({@code BYTE_LIMIT}/{@code DEPTH_LIMIT}) with no byte evidence;
 * the parse stops at the first excess without truncating into a valid document. Failure
 * precedence is first failure in byte, decode, parse order: byte size, then strict UTF-8
 * decode, then the linear scan (syntax or depth, whichever is met first), then duplicates,
 * then field semantics checked texture, then blur, then clamp — only after full
 * syntax/duplicate validation of the whole document ("decode all input before committing
 * either override"). Every MALFORMED outcome except the two limit classes reads the
 * complete bounded input and carries {@code COMPLETE} evidence plus the lowercase SHA-256
 * of all bounded bytes. Unreadable is not a parser outcome (callers map IO);
 * NOT_APPLICABLE/ABSENT are not parser outcomes either.
 */
public final class SidecarParser {

    /** Maximum accepted sidecar size in bytes (§4.3.5); the limit itself is legal. */
    public static final int MAX_BYTES = 1_048_576;

    /** Maximum JSON container depth; the root object is depth 1 (§4.3.5). */
    public static final int MAX_DEPTH = 64;

    private static final char BOM = '\uFEFF';

    /** Scan context: root object members. */
    private static final int CTX_ROOT = 0;
    /** Scan context: the {@code texture} object members ({@code blur}/{@code clamp}). */
    private static final int CTX_TEXTURE = 1;
    /** Scan context: syntactically validated, semantically ignored values. */
    private static final int CTX_IGNORED = 2;
    /** Scan context: the {@code texture.blur} value. */
    private static final int CTX_BLUR = 3;
    /** Scan context: the {@code texture.clamp} value. */
    private static final int CTX_CLAMP = 4;

    /** Captured field kinds: absent, or the first token kind of the value. */
    private static final int KIND_ABSENT = 0;
    private static final int KIND_TRUE = 1;
    private static final int KIND_FALSE = 2;
    private static final int KIND_OTHER = 3;

    private SidecarParser() {
    }

    /**
     * Parses bounded sidecar bytes into a VALID or MALFORMED outcome. Never returns null.
     * The SHA-256 evidence always covers all bounded bytes, never the decoded text.
     */
    public static SidecarOutcome parse(byte[] bounded, Optional<String> normalizedPath) {
        Objects.requireNonNull(bounded, "bounded");
        Objects.requireNonNull(normalizedPath, "normalizedPath");
        if (bounded.length > MAX_BYTES) {
            return malformed(normalizedPath, SidecarOutcome.FailureClass.BYTE_LIMIT, bounded);
        }
        String text;
        try {
            text = decodeStrictUtf8(bounded);
        } catch (CharacterCodingException e) {
            return malformed(normalizedPath, SidecarOutcome.FailureClass.ENCODING, bounded);
        }
        if (!text.isEmpty() && text.charAt(0) == BOM) {
            text = text.substring(1);
        }
        Scan scan = new Scan(text);
        SidecarOutcome.FailureClass fail = scan.document();
        if (fail != null) {
            return malformed(normalizedPath, fail, bounded);
        }
        // Full syntax/duplicate validation of the whole document succeeded; only now are
        // field semantics committed, texture first, then blur, then clamp.
        SidecarOutcome.FailureClass semantic = scan.fieldSemantics();
        if (semantic != null) {
            return malformed(normalizedPath, semantic, bounded);
        }
        return new SidecarOutcome(SidecarOutcome.Outcome.VALID, normalizedPath,
            SidecarOutcome.ByteEvidence.COMPLETE,
            Optional.of(CanonicalFraming.sha256Hex(bounded)),
            SidecarOutcome.FailureClass.NONE,
            presence(scan.blurKind), presence(scan.clampKind),
            effective(scan.blurKind), effective(scan.clampKind));
    }

    private static String decodeStrictUtf8(byte[] bytes) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes))
            .toString();
    }

    private static SidecarOutcome malformed(Optional<String> normalizedPath,
                                            SidecarOutcome.FailureClass failureClass,
                                            byte[] bounded) {
        boolean complete = failureClass != SidecarOutcome.FailureClass.BYTE_LIMIT
            && failureClass != SidecarOutcome.FailureClass.DEPTH_LIMIT;
        return new SidecarOutcome(SidecarOutcome.Outcome.MALFORMED, normalizedPath,
            complete ? SidecarOutcome.ByteEvidence.COMPLETE : SidecarOutcome.ByteEvidence.NONE,
            complete ? Optional.of(CanonicalFraming.sha256Hex(bounded)) : Optional.empty(),
            failureClass,
            SidecarOutcome.Presence.DISCARDED, SidecarOutcome.Presence.DISCARDED,
            Optional.empty(), Optional.empty());
    }

    private static SidecarOutcome.Presence presence(int kind) {
        return switch (kind) {
            case KIND_TRUE -> SidecarOutcome.Presence.TRUE;
            case KIND_FALSE -> SidecarOutcome.Presence.FALSE;
            default -> SidecarOutcome.Presence.OMITTED;
        };
    }

    private static Optional<Boolean> effective(int kind) {
        if (kind == KIND_TRUE) {
            return Optional.of(Boolean.TRUE);
        }
        if (kind == KIND_FALSE) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    /** One linear scan over the BOM-stripped decoded text; single use. */
    private static final class Scan {
        private final String s;
        private final int len;
        private int pos;
        /** Decoded member names of each open object, for duplicate detection. */
        private final ArrayDeque<HashSet<String>> openObjects = new ArrayDeque<>();
        /** Scratch buffer for the decoded member name / discarded string value. */
        private final StringBuilder decoded = new StringBuilder();

        private boolean texturePresent;
        private boolean textureIsObject;
        private int blurKind = KIND_ABSENT;
        private int clampKind = KIND_ABSENT;
        private boolean duplicate;

        Scan(String text) {
            this.s = text;
            this.len = text.length();
        }

        /** Scans one document; returns the first parse-stage failure class or null. */
        SidecarOutcome.FailureClass document() {
            skipWhitespace();
            if (peek() != '{') {
                return SidecarOutcome.FailureClass.JSON;
            }
            SidecarOutcome.FailureClass fail = scanObject(1, CTX_ROOT);
            if (fail != null) {
                return fail;
            }
            skipWhitespace();
            if (pos != len) {
                return SidecarOutcome.FailureClass.JSON;
            }
            return duplicate ? SidecarOutcome.FailureClass.DUPLICATE_MEMBER : null;
        }

        /** Field semantics after full syntax/duplicate validation: texture, blur, clamp. */
        SidecarOutcome.FailureClass fieldSemantics() {
            if (texturePresent && !textureIsObject) {
                return SidecarOutcome.FailureClass.SHAPE;
            }
            if (blurKind == KIND_OTHER) {
                return SidecarOutcome.FailureClass.SHAPE;
            }
            if (clampKind == KIND_OTHER) {
                return SidecarOutcome.FailureClass.SHAPE;
            }
            return null;
        }

        private void skipWhitespace() {
            while (pos < len) {
                char c = s.charAt(pos);
                if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                    return;
                }
                pos++;
            }
        }

        private char peek() {
            return pos < len ? s.charAt(pos) : '\0';
        }

        /** Consumes and returns the current character; '\0' at end of input. */
        private char next() {
            return pos < len ? s.charAt(pos++) : '\0';
        }

        private SidecarOutcome.FailureClass scanObject(int depth, int ctx) {
            if (depth > MAX_DEPTH) {
                return SidecarOutcome.FailureClass.DEPTH_LIMIT;
            }
            pos++; // opening '{'
            openObjects.push(new HashSet<>());
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                openObjects.pop();
                return null;
            }
            while (true) {
                skipWhitespace();
                if (peek() != '"') {
                    return SidecarOutcome.FailureClass.JSON;
                }
                SidecarOutcome.FailureClass fail = scanString();
                if (fail != null) {
                    return fail;
                }
                String name = decoded.toString();
                if (!openObjects.peek().add(name)) {
                    duplicate = true;
                }
                skipWhitespace();
                if (peek() != ':') {
                    return SidecarOutcome.FailureClass.JSON;
                }
                pos++;
                skipWhitespace();
                int childCtx = switch (ctx) {
                    case CTX_ROOT -> "texture".equals(name) ? CTX_TEXTURE : CTX_IGNORED;
                    case CTX_TEXTURE -> "blur".equals(name) ? CTX_BLUR
                        : "clamp".equals(name) ? CTX_CLAMP : CTX_IGNORED;
                    default -> CTX_IGNORED;
                };
                fail = scanValue(depth + 1, childCtx);
                if (fail != null) {
                    return fail;
                }
                skipWhitespace();
                char c = next();
                if (c == ',') {
                    continue;
                }
                if (c == '}') {
                    openObjects.pop();
                    return null;
                }
                return SidecarOutcome.FailureClass.JSON;
            }
        }

        private SidecarOutcome.FailureClass scanArray(int depth) {
            if (depth > MAX_DEPTH) {
                return SidecarOutcome.FailureClass.DEPTH_LIMIT;
            }
            pos++; // opening '['
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return null;
            }
            while (true) {
                skipWhitespace();
                SidecarOutcome.FailureClass fail = scanValue(depth + 1, CTX_IGNORED);
                if (fail != null) {
                    return fail;
                }
                skipWhitespace();
                char c = next();
                if (c == ',') {
                    continue;
                }
                if (c == ']') {
                    return null;
                }
                return SidecarOutcome.FailureClass.JSON;
            }
        }

        private SidecarOutcome.FailureClass scanValue(int depth, int ctx) {
            char c = peek();
            SidecarOutcome.FailureClass fail;
            int kind = KIND_OTHER;
            switch (c) {
                case '{' -> fail = scanObject(depth,
                    ctx == CTX_TEXTURE ? CTX_TEXTURE : CTX_IGNORED);
                case '[' -> fail = scanArray(depth);
                case '"' -> fail = scanString();
                case 't' -> fail = scanLiteral("true");
                case 'f' -> {
                    fail = scanLiteral("false");
                    kind = KIND_FALSE;
                }
                case 'n' -> fail = scanLiteral("null");
                case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                    fail = scanNumber();
                default -> {
                    return SidecarOutcome.FailureClass.JSON;
                }
            }
            if (fail != null) {
                return fail;
            }
            if (c == 't') {
                kind = KIND_TRUE;
            }
            if (ctx == CTX_TEXTURE) {
                texturePresent = true;
                textureIsObject = c == '{';
            } else if (ctx == CTX_BLUR) {
                blurKind = kind;
            } else if (ctx == CTX_CLAMP) {
                clampKind = kind;
            }
            return null;
        }

        /** Scans a string into {@link #decoded}; strict escapes, paired surrogates only. */
        private SidecarOutcome.FailureClass scanString() {
            decoded.setLength(0);
            pos++; // opening '"'
            while (true) {
                char c = peek();
                if (c == '"') {
                    pos++;
                    return null;
                }
                if (c == '\\') {
                    pos++;
                    SidecarOutcome.FailureClass fail = scanEscape();
                    if (fail != null) {
                        return fail;
                    }
                    continue;
                }
                if (c < 0x20) {
                    // unescaped control character, or end of input inside the string
                    return SidecarOutcome.FailureClass.JSON;
                }
                decoded.append(c);
                pos++;
            }
        }

        private SidecarOutcome.FailureClass scanEscape() {
            char e = next();
            switch (e) {
                case '"' -> decoded.append('"');
                case '\\' -> decoded.append('\\');
                case '/' -> decoded.append('/');
                case 'b' -> decoded.append('\b');
                case 'f' -> decoded.append('\f');
                case 'n' -> decoded.append('\n');
                case 'r' -> decoded.append('\r');
                case 't' -> decoded.append('\t');
                case 'u' -> {
                    return scanUnicodeEscape();
                }
                default -> {
                    return SidecarOutcome.FailureClass.JSON;
                }
            }
            return null;
        }

        private SidecarOutcome.FailureClass scanUnicodeEscape() {
            int unit = hex4();
            if (unit < 0) {
                return SidecarOutcome.FailureClass.JSON;
            }
            if (Character.isHighSurrogate((char) unit)) {
                // a high surrogate must be completed immediately by a low-surrogate escape
                if (pos + 1 < len && s.charAt(pos) == '\\' && s.charAt(pos + 1) == 'u') {
                    int save = pos;
                    pos += 2;
                    int low = hex4();
                    if (low >= 0 && Character.isLowSurrogate((char) low)) {
                        decoded.append((char) unit).append((char) low);
                        return null;
                    }
                    pos = save;
                }
                return SidecarOutcome.FailureClass.ENCODING;
            }
            if (Character.isLowSurrogate((char) unit)) {
                return SidecarOutcome.FailureClass.ENCODING;
            }
            decoded.append((char) unit);
            return null;
        }

        /** Reads four ASCII hex digits; -1 (position untouched semantics) on failure. */
        private int hex4() {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                if (pos >= len) {
                    return -1;
                }
                char c = s.charAt(pos);
                int digit;
                if (c >= '0' && c <= '9') {
                    digit = c - '0';
                } else if (c >= 'a' && c <= 'f') {
                    digit = c - 'a' + 10;
                } else if (c >= 'A' && c <= 'F') {
                    digit = c - 'A' + 10;
                } else {
                    return -1;
                }
                value = (value << 4) | digit;
                pos++;
            }
            return value;
        }

        private SidecarOutcome.FailureClass scanLiteral(String word) {
            if (pos + word.length() > len || !s.startsWith(word, pos)) {
                return SidecarOutcome.FailureClass.JSON;
            }
            pos += word.length();
            return null;
        }

        /**
         * Strict RFC 8259 number: no leading zeros, no bare sign, optional fraction and
         * exponent. The numeric value is never evaluated.
         */
        private SidecarOutcome.FailureClass scanNumber() {
            if (peek() == '-') {
                pos++;
            }
            char c = next();
            if (c == '0') {
                // leading zero only as the complete integer part
            } else if (c >= '1' && c <= '9') {
                while (pos < len && s.charAt(pos) >= '0' && s.charAt(pos) <= '9') {
                    pos++;
                }
            } else {
                return SidecarOutcome.FailureClass.JSON;
            }
            if (peek() == '.') {
                pos++;
                if (!digits(1)) {
                    return SidecarOutcome.FailureClass.JSON;
                }
            }
            char e = peek();
            if (e == 'e' || e == 'E') {
                pos++;
                char sign = peek();
                if (sign == '+' || sign == '-') {
                    pos++;
                }
                if (!digits(1)) {
                    return SidecarOutcome.FailureClass.JSON;
                }
            }
            return null;
        }

        /** Consumes at least {@code min} ASCII digits, then all following ones. */
        private boolean digits(int min) {
            int start = pos;
            while (pos < len && s.charAt(pos) >= '0' && s.charAt(pos) <= '9') {
                pos++;
            }
            return pos - start >= min;
        }
    }
}
