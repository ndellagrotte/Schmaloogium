// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads the {@code schmaloogium.hooks.anchor.<id>} counts the mixin plugin's audit
 * publishes for the vertex family ({@code HookAnchorAudit}) and answers one verdict.
 * Every catalogued id expects exactly one applied anchor in its transformed target.
 */
public final class McVertexHookHealth {

    /** The audited Task F vertex rows (one anchor each). */
    public static final List<String> CATALOGUE = List.of(
            "H10-TASK", "H10-TASK-EXIT",
            "H10-BUILD", "H10-BUILD-EXIT",
            "H10-BLOCK", "H10-BLOCK-EXIT",
            "H10-MODEL-ARRAY-FLAT", "H10-MODEL-ARRAY-SMOOTH",
            "H10-BEGIN", "H10-BEGIN-INIT", "H10-END", "H10-ARRAY", "H10-ARRAY-EXIT", "H10-BULK",
            "H10-WRITER-POS", "H10-WRITER-COLOR-INT", "H10-WRITER-TEX", "H10-WRITER-LIGHTMAP",
            "H10-WRITER-NORMAL", "H10-RESET",
            "H10-UPLOAD-DESCRIPTOR", "H10-UPLOAD-DESCRIPTOR-EXIT",
            "H10-VBO-UPLOAD", "H10-VBO-DELETE",
            "H10-VBO-LAYER", "H10-VBO-LAYER-END",
            "H10-CLIENT",
            "H10-LIST-REPLAY-END");

    static final String PROPERTY_PREFIX = "schmaloogium.hooks.anchor.";

    private McVertexHookHealth() {
    }

    /** The raw published counts for the given ids (evidence). */
    public static String anchors(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            String value = System.getProperty(PROPERTY_PREFIX + id);
            sb.append(id).append('=').append(value == null ? "absent" : value).append(' ');
        }
        return sb.toString().trim();
    }

    public static VertexHookHealth current() {
        List<String> disabled = new ArrayList<>();
        for (String id : CATALOGUE) {
            String value = System.getProperty(PROPERTY_PREFIX + id);
            int count;
            try {
                count = value == null ? 0 : Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                count = 0;
            }
            // An exit row is a RETURN injection: one call site per return instruction of
            // the one wrapped method (the transformed cardinality), never zero.
            boolean exitRow = id.endsWith("-EXIT") || id.endsWith("-END");
            boolean healthy = exitRow ? count >= 1 : count == 1;
            if (!healthy) {
                disabled.add(id + "=" + (value == null ? "absent" : value));
            }
        }
        return new VertexHookHealth(disabled.isEmpty(), disabled);
    }
}
