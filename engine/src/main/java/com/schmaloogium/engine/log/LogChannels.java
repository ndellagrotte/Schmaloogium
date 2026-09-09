// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.log;

/**
 * The fixed channel list (PHASE_1_DOC §4.9.2, D-P1-20): per-subsystem strings a user's
 * {@code log4j2.xml} filter can be meaningful against. Channels are fixed constants,
 * never composed at runtime; every log line goes to exactly one channel. The list covers
 * all fourteen phases. A later phase that genuinely needs a channel adds it here via a
 * fix-up to the Phase 1 document, not at runtime.
 */
public final class LogChannels {

    /** mod lifecycle, engine bootstrap, capability probe (Phases 1/7). */
    public static final String BOOT = "schmaloogium.boot";
    /** Pack discovery, file model, dimension folders (Phase 3). */
    public static final String PACK = "schmaloogium.pack";
    /** {@code #include} resolution, macro header, preprocessor (Phase 3). */
    public static final String PREPROCESS = "schmaloogium.preprocess";
    /** {@code shaders.properties}, options, profiles, screens, ID files (Phase 3). */
    public static final String CONFIG = "schmaloogium.config";
    /** Shader compile / link / validate (Phase 4). */
    public static final String COMPILE = "schmaloogium.compile";
    /** Stage registry, program slots, backup chains (Phase 4). */
    public static final String REGISTRY = "schmaloogium.registry";
    /** Framebuffers, colortex, ping-pong, clears, sizing (Phase 5). */
    public static final String BUFFERS = "schmaloogium.buffers";
    /** Built-in uniforms, cadences, samplers, unit map (Phase 6). */
    public static final String UNIFORMS = "schmaloogium.uniforms";
    /** Entity/block-entity/item id aliasing, per-draw dynamics, unknown-id warnings (Phase 9). */
    public static final String IDS = "schmaloogium.ids";
    /** Texture systems: noise texture, {@code _n}/{@code _s} companion atlases, custom textures (Phase 13). */
    public static final String TEXTURES = "schmaloogium.textures";
    /** Custom-uniform expression engine (Phase 11). */
    public static final String EXPR = "schmaloogium.expr";
    /** Per-frame orchestration, pass dispatch (Phase 7). */
    public static final String FRAME = "schmaloogium.frame";
    /** Shadow pass (Phase 8). */
    public static final String SHADOW = "schmaloogium.shadow";
    /** Facade-level GL events, capability gates, GL errors (Phases 1/14). */
    public static final String GL = "schmaloogium.gl";
    /** Coexistence detection, bail verdicts (Phases 1/10). */
    public static final String COMPAT = "schmaloogium.compat";
    /** Options and pack-selection screens (Phase 12). */
    public static final String GUI = "schmaloogium.gui";
    /** Harness-side output (Phase 2). */
    public static final String CONFORMANCE = "schmaloogium.conformance";

    private LogChannels() {
    }
}
