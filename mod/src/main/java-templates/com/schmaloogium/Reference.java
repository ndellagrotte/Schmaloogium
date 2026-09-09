// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package {{ package }};

/**
 * Generated identity constants (Blossom). The {@code package} blossom property is
 * overridden to {@code root_package} alone — {@code com.schmaloogium} — so this class
 * lives at the package root (PHASE_1_DOC §2.3, §4.2.4).
 */
public final class Reference {

    private Reference() {
    }

    public static final String MOD_ID = "{{ mod_id }}";
    public static final String MOD_NAME = "{{ mod_name }}";
    public static final String VERSION = "{{ mod_version }}";
}
