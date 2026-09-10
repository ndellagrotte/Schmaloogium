// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import com.schmaloogium.engine.expr.api.CustomExpressionCompileRequest;
import com.schmaloogium.engine.expr.api.ExpressionBackend;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/** Deterministic plan fingerprints (§4.1): hashes the source fingerprint, exact schema
 * versions, ordered declaration metadata/text, biome constant entries, language version,
 * evaluator semantic version, and backend semantic ID. Never hashes object identity or
 * host paths; diagnostics never alter the fingerprint. */
public final class Fingerprints {

    private Fingerprints() {}

    public static String planFingerprint(CustomExpressionCompileRequest request) {
        StringBuilder canonical = new StringBuilder(256);
        canonical.append("lang=").append(ExpressionBackend.LANGUAGE_VERSION).append('\n');
        canonical.append("evaluator=").append(ExpressionBackend.EVALUATOR_SEMANTIC_VERSION).append('\n');
        canonical.append("backend=").append(request.backendSemanticId()).append('\n');
        canonical.append("pack=").append(request.packConfigurationFingerprint()).append('\n');
        canonical.append("fixedSchema=").append(request.fixedInputs().version()).append('\n');
        canonical.append("contextSchema=").append(request.context().version()).append('\n');
        for (Map.Entry<String, Integer> biome : request.context().biomeConstants().entrySet()) {
            canonical.append("biome=").append(biome.getKey()).append('=').append(biome.getValue()).append('\n');
        }
        request.fixedInputs().inputs().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> canonical.append("input=").append(entry.getKey())
                        .append('=').append(entry.getValue()).append('\n'));
        request.declarations().forEach(source -> canonical
                .append("decl=").append(source.sourceOrdinal())
                .append('=').append(source.kind())
                .append('=').append(source.declaredType())
                .append('=').append(source.name())
                .append('=').append(source.rawExpression().replace("\n", "\\n"))
                .append('\n'));
        return sha256Hex(canonical.toString());
    }

    /** Deterministic automatic smooth key from plan language version, declaration ordinal,
     * and AST preorder site — never a runtime object hash (§4.7, D-P11-12). */
    public static long smoothKey(int declarationOrdinal, int preorderIndex) {
        long h = 0xcbf29ce484222325L;
        byte[] version = ExpressionBackend.LANGUAGE_VERSION.getBytes(StandardCharsets.UTF_8);
        for (byte b : version) {
            h = (h ^ (b & 0xffL)) * 0x100000001b3L;
        }
        h = (h ^ declarationOrdinal) * 0x100000001b3L;
        h = (h ^ preorderIndex) * 0x100000001b3L;
        return h == 0L ? 1L : h;
    }

    public static String sha256Hex(String canonical) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xf, 16));
                hex.append(Character.forDigit(b & 0xf, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
