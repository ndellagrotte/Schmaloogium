// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.Locale;

/**
 * One lower-case validated {@code namespace:path} registry name (PHASE_9_DOC §2.3).
 * Names are the only string identity that crosses the D-6 seam; ordinals are dense and
 * snapshot-local. Comparison is code-point order by namespace, then path.
 */
public record RegistryName(String namespace, String path)
        implements Comparable<RegistryName> {

    public RegistryName {
        if (namespace == null || path == null) {
            throw new IllegalArgumentException("registry name parts must be non-null");
        }
        if (!namespace.matches("[a-z0-9][a-z0-9._-]*")) {
            throw new IllegalArgumentException("invalid registry namespace: " + namespace);
        }
        for (String segment : path.split("/", -1)) {
            if (segment.isEmpty() || segment.equals(".") || segment.equals("..")
                    || !segment.matches("[a-z0-9._-]+")) {
                throw new IllegalArgumentException("invalid registry path: " + path);
            }
        }
    }

    /** Parses and validates one {@code namespace:path} or short {@code path} token. */
    public static RegistryName parse(String token) {
        int colon = token.indexOf(':');
        if (colon < 0) {
            return new RegistryName("minecraft", token);
        }
        if (token.indexOf(':', colon + 1) >= 0) {
            throw new IllegalArgumentException("multiple colons in registry name: " + token);
        }
        return new RegistryName(token.substring(0, colon), token.substring(colon + 1));
    }

    /** The canonical {@code namespace:path} text. */
    public String canonical() {
        return namespace + ":" + path;
    }

    @Override
    public int compareTo(RegistryName o) {
        int byNamespace = namespace.compareTo(o.namespace);
        return byNamespace != 0 ? byNamespace : path.compareTo(o.path);
    }

    @Override
    public String toString() {
        return canonical().toLowerCase(Locale.ROOT);
    }
}
