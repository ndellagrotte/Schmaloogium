// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.record;

import java.util.ArrayList;
import java.util.List;

/**
 * One recorded mutating (or explicitly query-shaped, per PHASE_1_DOC §4.7.5) facade
 * call: a stable operation name and its argument list. Arguments are the values the
 * facade received — handles, engine enums, pure records, numbers and strings — never
 * bulk bytes, native names or addresses (§4.7.5: "bulk data is logged by summary, never
 * by content"). The rendered form is deterministic: the renderer in {@link GLCallLog}
 * resolves handles to their stable sequence-number names, so {@code render()} carries no
 * timestamps and no identity hash codes (§4.7.5).
 *
 * @param op   the stable facade verb, e.g. {@code "shaders.use"},
 *             {@code "framebuffers.attachDepth"}
 * @param args the call's arguments, defensively copied immutable
 */
public record GLCall(String op, List<Object> args) {

    public GLCall {
        if (op == null) {
            throw new IllegalArgumentException("op must not be null");
        }
        // Null-tolerant defensive copy: restored-identity arguments are legitimately
        // absent (§4.7.4 depth verbs, §4.7.4a blit) and renderArg renders them "absent".
        args = java.util.Collections.unmodifiableList(new ArrayList<>(args));
    }

    /** Renders this one call as one deterministic line (no trailing newline). */
    String render(GLCallLog log) {
        StringBuilder line = new StringBuilder(op).append('(');
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                line.append(", ");
            }
            line.append(log.renderArg(args.get(i)));
        }
        return line.append(')').toString();
    }
}
