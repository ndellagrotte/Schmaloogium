// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import com.schmaloogium.engine.config.CustomExpressionDecl;
import java.util.List;

/** The Phase 11 compiler contract (§4.1). {@code compilePhase3} is the sole public adapter
 * from Phase 3's declaration algebra; both entry points validate backend selection before
 * adaptation/parsing, even for an empty declaration list. */
public interface CustomExpressionCompiler {

    PlanBuildResult compile(CustomExpressionCompileRequest request);

    PlanBuildResult compilePhase3(String packConfigurationFingerprint,
                                  List<CustomExpressionDecl> declarations,
                                  FixedExpressionInputSchema fixedInputs,
                                  ExpressionContextSchema context,
                                  String backendSemanticId);
}
