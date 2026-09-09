// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

import java.util.List;

final class DiagnosticCodec {

    static final String DISCOVERY_LIMIT_KEY = "schmaloogium.error.pack.discovery_limit";
    static final String PACK_LOG_CHANNEL = "schmaloogium.pack";

    private DiagnosticCodec() {
    }

    /** §4.10 closed-argument diagnostic factory; rejects anything outside the algebra. */
    static EngineDiagnostic diagnostic(DiagnosticSeverity severity, UserChannel channel,
        String messageKey, List<Object> args, String detail, String logChannel) {
        java.util.Objects.requireNonNull(severity, "severity");
        java.util.Objects.requireNonNull(channel, "channel");
        java.util.Objects.requireNonNull(messageKey, "messageKey");
        java.util.Objects.requireNonNull(args, "args");
        java.util.Objects.requireNonNull(detail, "detail");
        java.util.Objects.requireNonNull(logChannel, "logChannel");
        List<Object> copy = List.copyOf(args);
        for (Object a : copy) {
            if (a == null || !(a instanceof String || a instanceof Boolean
                    || a instanceof Integer || a instanceof Long)) {
                throw new IllegalArgumentException(
                    "diagnostic argument outside the closed algebra: "
                    + (a == null ? "null" : a.getClass().getName()));
            }
        }
        return new EngineDiagnostic(severity, channel, messageKey, copy, detail, logChannel);
    }

    /** Canonical diagnostic encoding per §4.10. */
    static byte[] encode(EngineDiagnostic d) {
        return CanonicalBytes.seq(
            CanonicalBytes.atom("EngineDiagnostic"),
            CanonicalBytes.atom(d.severity().name()),
            CanonicalBytes.atom(d.channel().name()),
            CanonicalBytes.atom(d.messageKey()),
            encodeArgs(d.args()),
            CanonicalBytes.atom(d.detail()),
            CanonicalBytes.atom(d.logChannel()));
    }

    private static byte[] encodeArgs(List<Object> args) {
        byte[][] encoded = new byte[args.size() + 1][];
        encoded[0] = CanonicalBytes.atom("DiagnosticArgs");
        for (int i = 0; i < args.size(); i++) {
            Object a = args.get(i);
            encoded[i + 1] = CanonicalBytes.seq(
                CanonicalBytes.atom(a.getClass().getSimpleName()),
                CanonicalBytes.atom(scalarText(a)));
        }
        return CanonicalBytes.seq(encoded);
    }

    private static String scalarText(Object a) {
        if (a instanceof String s) {
            return s;
        }
        return String.valueOf(a);
    }

    /** The fixed overflow discovery-limit diagnostic. */
    static EngineDiagnostic discoveryLimitDiagnostic() {
        return diagnostic(DiagnosticSeverity.ERROR, UserChannel.CHAT, DISCOVERY_LIMIT_KEY,
            List.of(), "", PACK_LOG_CHANNEL);
    }

    static byte[] discoveryLimitDiagnosticBytes() {
        return encode(discoveryLimitDiagnostic());
    }

    /** Canonical encoding of one candidate for snapshot accounting. */
    static byte[] encodeCandidate(PackCandidate c) {
        byte[] ref = c.filesystemReference()
            .map(r -> CanonicalBytes.seq(CanonicalBytes.atom("Present"),
                CanonicalBytes.atom(r.canonicalValue())))
            .orElseGet(() -> CanonicalBytes.seq(CanonicalBytes.atom("Absent")));
        byte[][] diags = new byte[c.diagnostics().size()][];
        for (int i = 0; i < diags.length; i++) {
            diags[i] = encode(c.diagnostics().get(i));
        }
        return CanonicalBytes.seq(
            CanonicalBytes.atom("PackCandidate"),
            ref,
            CanonicalBytes.atom(c.kind().name()),
            CanonicalBytes.atom(c.displayName()),
            CanonicalBytes.atom(c.status().name()),
            CanonicalBytes.seq(diags));
    }
}
