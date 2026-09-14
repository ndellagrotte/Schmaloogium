// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/**
 * Program-name family tests for the section 4.7 directive family filters (PHASE_3_DOC
 * §3.3): {@code colortexNClear} / {@code colortexNClearColor} are honoured only for the
 * deferred and composite families, and {@code colortexNMipmapEnabled} only for deferred,
 * composite and final. The registry's {@code StageId} is a Phase 4 concept and is not
 * available while the front end is still scanning sources, so the test is by name here.
 */
public final class ProgramFamilies {

    private ProgramFamilies() {
    }

    /** {@code deferred}, {@code deferred1}-{@code deferred99}, and the virtual prelude. */
    public static boolean isDeferred(String programName) {
        return isIndexedFamily(programName, "deferred");
    }

    /** {@code composite}, {@code composite1}-{@code composite99}, and the virtual prelude. */
    public static boolean isComposite(String programName) {
        return isIndexedFamily(programName, "composite");
    }

    public static boolean isFinal(String programName) {
        return "final".equals(programName);
    }

    /** The clear / clear-colour family filter. */
    public static boolean isDeferredOrComposite(String programName) {
        return isDeferred(programName) || isComposite(programName);
    }

    /** The mipmap family filter: deferred, composite and final. */
    public static boolean isFullscreen(String programName) {
        return isDeferredOrComposite(programName) || isFinal(programName);
    }

    private static boolean isIndexedFamily(String programName, String base) {
        if (programName == null || !programName.startsWith(base)) {
            return false;
        }
        String rest = programName.substring(base.length());
        if (rest.isEmpty() || "_pre".equals(rest)) {
            return true;
        }
        if (rest.length() > 2) {
            return false;
        }
        for (int i = 0; i < rest.length(); i++) {
            if (rest.charAt(i) < '0' || rest.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
}
