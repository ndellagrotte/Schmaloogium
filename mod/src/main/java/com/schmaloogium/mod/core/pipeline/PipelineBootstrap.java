// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.Reference;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.frame.ReloadReason;
import com.schmaloogium.engine.frame.ReloadStatus;
import com.schmaloogium.engine.frame.lifecycle.ReloadLifecycle;
import com.schmaloogium.engine.frame.lifecycle.ReloadRequest;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.OsFamily;
import com.schmaloogium.engine.pack.RuntimeIdentityData;
import com.schmaloogium.mod.glue.Lwjgl3GLDevice;
import com.schmaloogium.mod.glue.VanillaMainDepthBorrow;
import com.schmaloogium.mod.glue.frame.BootstrapHooks;
import com.schmaloogium.mod.glue.frame.DepthTex0Bridge;
import com.schmaloogium.mod.glue.frame.DeviceHolder;
import com.schmaloogium.mod.glue.frame.DeviceRenderPort;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import com.schmaloogium.mod.glue.frame.FrameRuntime;
import com.schmaloogium.mod.glue.frame.McFrameState;
import com.schmaloogium.mod.glue.uniforms.McCenterDepthSource;
import com.schmaloogium.mod.glue.uniforms.McUniformPlatformProvider;
import com.schmaloogium.mod.gui.BundleIo;
import com.schmaloogium.mod.gui.ShaderGui;
import com.schmaloogium.mod.gui.model.EngineSettingsController;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * Client wiring for the composition root: builds the one device and the transaction the
 * first time GL and the GUI bundle exist, installs the Phase 7 coordinator, and drains one
 * merged reload per client tick — on the render thread, with no frame open, and only while
 * a world exists (a pipeline is never built at the main menu). The persisted selection is
 * submitted once at the first eligible tick; dimension changes rebuild; a P5 "awaiting main
 * depth" answer retries a bounded number of times as the bridge's version advances.
 */
@SideOnly(Side.CLIENT)
public final class PipelineBootstrap {

    private static final Log LOG = Logs.channel(LogChannels.FRAME);
    private static final int DEPTH_RETRY_TICKS = 20;
    private static final int DEPTH_RETRY_LIMIT = 3;

    private static volatile PipelineBootstrap instance;

    private final Minecraft client;
    private ShaderReloadCoordinator coordinator;
    private boolean initialSubmitted;
    private DimensionKey lastDimension = DimensionKey.BASE;
    private int depthRetries;
    private int depthRetryCountdown;

    private PipelineBootstrap(Minecraft client) {
        this.client = client;
    }

    /** Installs once from the client proxy after the GUI bundle exists. */
    public static void install(Minecraft client) {
        if (instance != null) {
            return;
        }
        instance = new PipelineBootstrap(client);
        FMLCommonHandler.instance().bus().register(instance);
    }

    public static Optional<ShaderReloadCoordinator> coordinator() {
        PipelineBootstrap current = instance;
        return current == null ? Optional.empty() : Optional.ofNullable(current.coordinator);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (coordinator == null && !wire()) {
            return;
        }
        if (client.world == null || FrameHooks.hasOpenFrame()) {
            return;
        }
        if (!initialSubmitted) {
            initialSubmitted = true;
            lastDimension = McFrameState.dimension();
            coordinator.submitEngine(new ReloadRequest(ReloadLifecycle.FULL, true, false,
                    ReloadReason.PACK_SELECTION));
        }
        watchDimension();
        retryAwaitingDepth();
        Optional<ReloadStatus> status = coordinator.drainOnce();
        status.ifPresent(s -> LOG.info("reload drained: {}", s));
    }

    private boolean wire() {
        if (!BootstrapHooks.isGlReady() || !client.isCallingFromMinecraftThread()) {
            return false;
        }
        Optional<Lwjgl3GLDevice> device = DeviceHolder.acquire();
        Optional<BundleIo.Access> access = ShaderGui.access();
        Optional<EngineSettingsController> settings = ShaderGui.settings();
        if (device.isEmpty() || access.isEmpty() || settings.isEmpty()
                || ShaderGui.services().isEmpty()) {
            return false;
        }
        BooleanSupplier renderThread = client::isCallingFromMinecraftThread;
        FrameRuntime.installRenderThreadPredicate(renderThread);
        DepthTex0Bridge.installBorrowedSource(VanillaMainDepthBorrow.source(device.get()));
        EngineSettingsController controller = settings.get();
        SelectionResolver selection = new SelectionResolver(ShaderGui.services().get().frontEnd(),
                access.get().shaderpacksDirectory(), controller::shaderPack, Diagnostics::report);
        EnginePipelineStages stages = new EnginePipelineStages(
                ShaderGui.services().get().frontEnd(),
                access.get().shaderpacksDirectory(),
                access.get().files(),
                new RuntimeIdentityData(1, 12, 2, Reference.MOD_ID, Reference.VERSION, osFamily(),
                        Map.of()),
                device.get().capabilities(),
                device.get(),
                new McUniformPlatformProvider(),
                new McCenterDepthSource(device.get()),
                DepthTex0Bridge.get(),
                Diagnostics::report);
        PipelineTransaction transaction = new PipelineTransaction(new PipelineTransaction.Services(
                stages,
                selection,
                controller::committed,
                McFrameState::dimension,
                McFrameState::targetView,
                DepthTex0Bridge::version,
                new DeviceRenderPort(device.get(), McFrameState::targetView),
                composition -> {
                    FrameRuntime.installComposition(composition);
                    if (composition.isPresent()) {
                        FrameHooks.noteCompositionInstalled();
                    }
                },
                Diagnostics::report));
        coordinator = new ShaderReloadCoordinator(transaction, renderThread, selection);
        ReloadCoordinator.install(coordinator);
        ShaderGui.installConfigurationSource(
                () -> transaction.active().map(active -> active.configuration().options()));
        LOG.info("composition root installed: coordinator armed, device ready");
        return true;
    }

    private void watchDimension() {
        DimensionKey live = McFrameState.dimension();
        if (live.equals(lastDimension)) {
            return;
        }
        lastDimension = live;
        Optional<ActivePipeline> active = coordinator.transaction().active();
        if (active.isEmpty()) {
            return;
        }
        boolean packDeclaresLive = active.get().configuration().dimensions().containsKey(live);
        boolean activeIsSpecific = !active.get().composition().identity().dimension()
                .equals(DimensionKey.BASE);
        if (packDeclaresLive || activeIsSpecific) {
            coordinator.submitEngine(new ReloadRequest(ReloadLifecycle.REPUBLISH, false, false,
                    ReloadReason.DIMENSION_CHANGE));
        }
    }

    private void retryAwaitingDepth() {
        long awaiting = coordinator.transaction().awaitingMainDepthVersion();
        if (awaiting < 0L) {
            depthRetries = 0;
            depthRetryCountdown = 0;
            return;
        }
        if (depthRetries >= DEPTH_RETRY_LIMIT) {
            return;
        }
        if (depthRetryCountdown > 0) {
            depthRetryCountdown--;
            return;
        }
        if (DepthTex0Bridge.get().current() instanceof MainDepthSnapshot.Available) {
            depthRetries++;
            depthRetryCountdown = DEPTH_RETRY_TICKS;
            coordinator.submitEngine(new ReloadRequest(ReloadLifecycle.REPUBLISH, false, false,
                    ReloadReason.PACK_SELECTION));
        }
    }

    private static OsFamily osFamily() {
        String name = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (name.contains("win")) {
            return OsFamily.WINDOWS;
        }
        if (name.contains("mac")) {
            return OsFamily.MACOS;
        }
        if (name.contains("linux")) {
            return OsFamily.LINUX;
        }
        return OsFamily.OTHER;
    }
}
