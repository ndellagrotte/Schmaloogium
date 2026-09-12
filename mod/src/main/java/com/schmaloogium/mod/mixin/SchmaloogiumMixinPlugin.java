// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin;

import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.mod.compat.BailRegistry;
import com.schmaloogium.mod.compat.CompatVerdict;
import com.schmaloogium.mod.compat.EarlyCompatContext;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * The MOD-phase config's plugin (PHASE_1_DOC §4.5.2, D-P1-53): consults the bail
 * registry in {@code shouldApplyMixin} so a detected incompatible chunk-renderer
 * replacement can veto vertex-pipeline mixins <em>before</em> they apply. Not a
 * returns-true skeleton: it evaluates the registered early subset
 * ({@link BailRegistry#evaluateEarly}) with class-presence probes only — no game/GL
 * initialization — and retains the whole-family terminal veto for the session.
 *
 * <p>Phase 1 registers zero checks, so at v0.1 this admits everything; Phase 10 supplies
 * the real class-only detection policy through D-P1-50.
 */
public final class SchmaloogiumMixinPlugin implements IMixinConfigPlugin {

    /** The resource/metadata probe context: class presence only, never initialization. */
    static final EarlyCompatContext EARLY_CONTEXT = binaryName -> {
        // getResourceAsStream probes without linking or initializing the class.
        return SchmaloogiumMixinPlugin.class.getClassLoader().getResourceAsStream(
                binaryName.replace('.', '/') + ".class") != null;
    };

    private volatile Boolean vetoed = null;

    @Override
    public void onLoad(String mixinPackage) {
        // Nothing to prepare; the early subset registers itself before first evaluation.
    }

    @Override
    public String getRefMapperConfig() {
        return null; // Refmap handling is left to Unimined (D-P1-13).
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Boolean v = vetoed;
        if (v == null) {
            v = BailRegistry.evaluateEarly(EARLY_CONTEXT).shouldBail();
            if (v) {
                Logs.sink().emit(LogChannels.COMPAT,
                        com.schmaloogium.engine.log.LogLevel.ERROR,
                        "schmaloogium.compat.mixinVeto", new Object[]{}, null);
            }
            vetoed = v;
        }
        return !v;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // No target sharing with other configs.
    }

    @Override
    public List<String> getMixins() {
        // No class-scan discovery: the drift insurance is the config/package agreement
        // test (§4.5.2a rejected the scan), and v0.1 has no mixins anyway.
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                         IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                          IMixinInfo mixinInfo) {
        // The structural hook-application evidence (R18): which mixin applied to which target,
        // handed to the game-loader side through a JVM-global property (this package is
        // classloader-excluded, so a direct reference is not loadable from game code).
        System.setProperty("schmaloogium.hooks.applied." + mixinClassName, targetClassName);
    }
}
