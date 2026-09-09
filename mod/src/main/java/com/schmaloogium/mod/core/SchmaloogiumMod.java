// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core;

import com.schmaloogium.Reference;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.mod.compat.BailRegistry;
import com.schmaloogium.mod.compat.CompatContext;
import com.schmaloogium.mod.compat.CompatEvaluation;
import com.schmaloogium.mod.compat.CompatVerdict;
import com.schmaloogium.mod.core.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Mod entry point (PHASE_1_DOC §4.1, §4.9, §4.10). The bring-up sequence rides the FML
 * lifecycle, not a GameSettings mixin (§4.13 deviation 2, D-P1-37): the log sink
 * installs at {@code preInit} (stage 1) and bail point 1 evaluates post-
 * {@code FMLLoadCompleteEvent}, before engine bootstrap. Stage 2 — the capability probe
 * at {@code OpenGlHelper.initializeTextures} RETURN — is Phase 7's hook catalog entry.
 *
 * <p>This shared class never touches a client-only type; client work goes through the
 * side boundary ({@code IProxy}).
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public final class SchmaloogiumMod {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @SidedProxy(modId = Reference.MOD_ID,
            clientSide = "com.schmaloogium.mod.core.proxy.ClientProxy",
            serverSide = "com.schmaloogium.mod.core.proxy.CommonProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Logs.install(new Log4jLogSink());
        // Side services (the client diagnostic router) install through the proxy; the
        // common side leaves the engine's log-only fallback in place.
        proxy.installSideServices();
        LOGGER.info("Schmaloogium {} initializing", Reference.VERSION);
    }

    /**
     * Bail evaluation point 1 (PHASE_1_DOC §4.10): before engine bootstrap, after all
     * mods have loaded — the moment where another mod's presence can first be answered
     * for the whole session. A {@code Bail} verdict forces shaders off for the session
     * and keeps them off (a supported terminal state, §G2.4 rungs 4/5).
     */
    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        CompatEvaluation evaluation = BailRegistry.evaluate(new FmlCompatContext());
        if (evaluation.shouldBail()) {
            String reasonKey = evaluation.bails().get(0).reasonKey();
            List<Object> args = evaluation.bails().get(0).args();
            LOGGER.error("Compat check bailed: {}", reasonKey);
            Diagnostics.report(new EngineDiagnostic(
                    DiagnosticSeverity.ERROR, UserChannel.CHAT, reasonKey, args,
                    "bail point 1: " + evaluation.bails().size() + " bail verdict(s)",
                    LogChannels.COMPAT));
        } else if (!evaluation.degradations().isEmpty()) {
            LOGGER.warn("{} compat degradation(s) in effect", evaluation.degradations().size());
        }
    }

    /** FML-backed {@link CompatContext}: mod-list and class probes plus the GL profile slot. */
    static final class FmlCompatContext implements CompatContext {
        @Override
        public boolean isModLoaded(String modId) {
            return net.minecraftforge.fml.common.Loader.isModLoaded(modId);
        }

        @Override
        public boolean isClassPresent(String binaryName) {
            try {
                Class.forName(binaryName, false, SchmaloogiumMod.class.getClassLoader());
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        @Override
        public com.schmaloogium.engine.gl.GLCapabilityProfile capabilities() {
            // The capability profile exists only after stage 2 (display init); before
            // bootstrap there is nothing to gate on. Phase 7 populates this.
            throw new IllegalStateException("capability profile is not available before display init");
        }
    }
}
