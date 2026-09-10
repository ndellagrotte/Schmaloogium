// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import com.schmaloogium.engine.expr.api.ExpressionDiagnosticKind;
import com.schmaloogium.engine.expr.api.SourceSpan;

/** Deterministic stable diagnostic identities (§4.9). Prose is never hashed: declaration
 * diagnostics use kind + plan fingerprint + declaration ordinal + span; the pre-plan
 * backend rejection uses kind + pack fingerprint + requested backend ID; structural
 * active-plan failures use kind + active fingerprint + a stable reason code. */
public final class StableIds {

    private StableIds() {}

    public static String forDeclaration(ExpressionDiagnosticKind kind, String planFingerprint,
                                        int ordinal, SourceSpan span) {
        return "expr:" + kind + ":" + planFingerprint + ":" + ordinal
                + ":" + span.startOffset() + ":" + span.endOffset();
    }

    public static String forUnsupportedBackend(String packConfigurationFingerprint, String requestedId) {
        return "expr:UNSUPPORTED_BACKEND:" + packConfigurationFingerprint + ":" + requestedId;
    }

    public static String forStructural(ExpressionDiagnosticKind kind, String planFingerprint, String reason) {
        return "expr:" + kind + ":" + planFingerprint + ":reason:" + reason;
    }
}
