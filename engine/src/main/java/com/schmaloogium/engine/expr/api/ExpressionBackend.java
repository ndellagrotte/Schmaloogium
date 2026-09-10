// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** Published language and evaluator semantic identities (§4.11). The canonical v0.4
 * semantic ID is the only accepted backend ID; any other deterministically fails before
 * parsing with one source-less {@code UNSUPPORTED_BACKEND} diagnostic. */
public final class ExpressionBackend {

    /** Expression language semantic version; part of every plan fingerprint. */
    public static final String LANGUAGE_VERSION = "schmaloogium-expression-language-v1";

    /** Evaluator semantic version; part of every plan fingerprint. */
    public static final String EVALUATOR_SEMANTIC_VERSION = "typed-ast-interpreter-v1";

    /** The only accepted backend semantic ID in v0.4. */
    public static final String TYPED_AST_INTERPRETER_V1 = "schmaloogium:typed-ast-interpreter-v1";

    private ExpressionBackend() {}
}
