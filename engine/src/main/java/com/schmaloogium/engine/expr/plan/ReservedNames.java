// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import com.schmaloogium.engine.expr.api.ExpressionContextSchema;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;
import com.schmaloogium.engine.expr.type.FunctionSymbol;
import java.util.Set;

/** Reserved and excluded name domains (§4.1/§4.4). Collisions are diagnosed
 * {@code DUPLICATE_NAME}; never shadowed. The excluded built-in/per-draw set is the
 * seven-name union of D-P11-9: the five D.4 dynamics plus fogMode and fogColor. */
public final class ReservedNames {

    /** Appendix D.4 per-draw dynamics plus fogMode/fogColor — absent from the schema. */
    public static final Set<String> EXCLUDED = Set.of(
            "entityColor", "entityId", "blockEntityId", "blendFunc", "instanceId",
            "fogMode", "fogColor");

    private static final Set<String> CONTEXT_SCALARS = Set.of("biome", "temperature", "rainfall");

    private static final Set<String> CONTEXT_FLAGS = Set.of(
            "is_alive", "is_burning", "is_child", "is_glowing", "is_hurt", "is_in_lava",
            "is_in_water", "is_invisible", "is_on_ground", "is_ridden", "is_riding",
            "is_sneaking", "is_sprinting", "is_wet");

    private static final Set<String> LITERALS = Set.of("pi", "true", "false");

    private ReservedNames() {}

    /** True when a declaration with this name is rejected as a collision. */
    public static boolean isReserved(String name, FixedExpressionInputSchema fixed,
                                     ExpressionContextSchema context) {
        return LITERALS.contains(name)
                || FunctionSymbol.BY_NAME.containsKey(name)
                || CONTEXT_SCALARS.contains(name)
                || CONTEXT_FLAGS.contains(name)
                || EXCLUDED.contains(name)
                || context.biomeConstants().containsKey(name)
                || fixed.inputs().containsKey(name);
    }
}
