// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.wire;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The canonical-text layer's own contract (D-P2-4): strict escaping, strict number
 * formatting, strict key syntax — locale-independent and re-encoding-stable.
 */
class CanonicalTextTest {

    @Test
    void jsonRoundTripsThroughDangerousCharacters() {
        String original = "quote\" backslash\\ newline\n tab\t unicode ☂Emoji tr\u00e9s";
        String encoded = CanonicalText.encodeJson(original);
        assertEquals(original, CanonicalText.decodeJson(encoded));
        // Re-encoding the decode is byte-identical (canonical, not just stable).
        assertEquals(encoded, CanonicalText.encodeJson(CanonicalText.decodeJson(encoded)));
    }

    @Test
    void jsonRejectsRawControlCharacters() {
        assertThrows(IllegalArgumentException.class,
            () -> CanonicalText.requireCanonicalJson("\"a\nb\""));
        assertThrows(IllegalArgumentException.class,
            () -> CanonicalText.requireCanonicalJson("\"a\tb\""));
    }

    @Test
    void jsonRejectsNonCanonicalEscapes() {
        // The escape that JSON allows but canonical form forbids (we emit backslash-u).
        assertThrows(IllegalArgumentException.class,
            () -> CanonicalText.requireCanonicalJson("\"\\u0041\""));
        assertThrows(IllegalArgumentException.class,
            () -> CanonicalText.requireCanonicalJson("\"\\x41\""));
        assertThrows(IllegalArgumentException.class,
            () -> CanonicalText.requireCanonicalJson("\"unterminated"));
    }

    @Test
    void decimalFormattingIsLocaleIndependent() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ITALY); // comma decimal separator
            assertEquals("0.5", CanonicalText.formatDouble(0.5d));
            assertEquals("1.0", CanonicalText.formatDouble(1.0d));
            assertEquals("0.05", CanonicalText.formatDouble(0.05d));
            assertEquals("-12.25", CanonicalText.formatDouble(-12.25d));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void decimalFormatDoesNotRoundTripThroughNonCanonicalText() {
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.parseDouble("0,5"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.parseDouble("0.50"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.parseDouble("+1.0"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.parseDouble("1e3"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.parseDouble("NaN"));
    }

    @Test
    void keySyntaxIsStrict() {
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey("run .id"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey("run id"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey("run/id"));
        CanonicalText.validateKey("9run");
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey("a..b"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey(".run.id"));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey("run.id."));
        assertThrows(IllegalArgumentException.class, () -> CanonicalText.validateKey("run-id"));
        CanonicalText.validateKey("x.producer_name.any.thing_0");
    }

    @Test
    void textValuesSurviveAQuoteAndBackslashRoundTrip() {
        String nasty = "\"\\\"\\\\ end";
        String encoded = CanonicalText.encodeJson(nasty);
        assertNotEquals(nasty, encoded);
        assertEquals(nasty, CanonicalText.decodeJson(encoded));
    }
}
