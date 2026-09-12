// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.CapabilityShortfall;
import com.schmaloogium.engine.buffers.ColorBufferResource;
import com.schmaloogium.engine.buffers.InstanceResource;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.buffers.ShadowTextureResource;
import com.schmaloogium.engine.buffers.VertexAttributeResource;
import com.schmaloogium.engine.config.ColorAttachmentFormat;
import com.schmaloogium.engine.config.OptionValue;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.frame.FinalizedFrame;
import com.schmaloogium.engine.frame.spi.FrameCompletionObserver;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.ReplayAwareGLError;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.pack.PackCandidate;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.engine.pack.PackCandidateStatus;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackDiscoveryResult;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.uniforms.UniformFrameTiming;
import com.schmaloogium.engine.uniforms.UniformReplayReport;
import com.schmaloogium.mod.core.ClientDiagnosticRouter;
import com.schmaloogium.mod.core.pipeline.ActivePipeline;
import com.schmaloogium.mod.core.pipeline.PipelineBootstrap;
import com.schmaloogium.mod.glue.frame.BootstrapHooks;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import com.schmaloogium.mod.glue.frame.FrameObservers;
import com.schmaloogium.mod.glue.frame.GlErrorLedger;
import com.schmaloogium.mod.gui.ShaderGui;
import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;
import com.schmaloogium.mod.gui.model.ReloadLifecycle;
import com.schmaloogium.mod.gui.model.ReloadRequest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The client-side capture agent (PHASE_2_DOC §4.5, §4.5.6; Phase 7 H-CAPTURE-01/02). Inert
 * unless one of the {@code -Dschmaloogium.conformance.*} properties is present. Three modes:
 * <ul>
 *   <li><b>inventory</b> — write the loader's active mod list and quit;</li>
 *   <li><b>generate</b> — create a world from a generation descriptor, settle, quit (the
 *       runner hashes and publishes the save);</li>
 *   <li><b>capture</b> — authenticate the copied save, load it, apply the scene, select the
 *       pack, establish checkpoint0 in the same tick the composition installs, then run every
 *       preparation / warm-up / sample step under the controlled clock, grabbing frames at
 *       the after-final hook, and commit {@code manifest.tmp} before a clean shutdown.</li>
 * </ul>
 * Every step is guarded: any failure disarms, logs on {@code schmaloogium.conformance},
 * writes a failure manifest and shuts the client down cleanly; nothing rethrows into vanilla.
 */
@SideOnly(Side.CLIENT)
public final class CaptureAgent implements FrameCompletionObserver {

    private static final Log LOG = Logs.channel(LogChannels.CONFORMANCE);
    private static final int SETTLE_TICKS = 100;
    private static final int GENERATE_SETTLE_TICKS = 400;
    private static final int APPLY_TICKS = 40;
    private static final int CHUNK_READY_TIMEOUT_TICKS = 20 * 120;
    private static volatile CaptureAgent instance;

    enum Mode { CAPTURE, GENERATE, INVENTORY }

    enum State { BOOT, LOADING, SETTLING, APPLYING, SELECTING, AWAITING_INSTALL, CONTROLLED, COMMITTING, DONE, FAILED }

    private record FrameRow(String kind, String id, long ordinal, boolean captured,
            CapturePlanReader.Pose plannedCurrent, CapturePlanReader.Pose plannedPrevious,
            CapturePlanReader.Pose actualCurrent, CapturePlanReader.Pose actualPrevious,
            TimingLedger.Step step, long entityCount, long durationMillis) {
    }

    private record ImageRow(String kind, String id, long ordinal, String path, int width, int height, String sha) {
    }

    private final Minecraft mc;
    private final Mode mode;
    private final Path outDir;
    private final CapturePlanReader plan;
    private final List<CapturePlanReader.Capture> captures;
    private final long prepSteps;
    private final long totalSteps;
    private final long hangCeilingMillis;
    private final String packArchive;
    private final TimingLedger ledger = new TimingLedger();
    private final List<FrameRow> frames = new ArrayList<>();
    private final List<ImageRow> images = new ArrayList<>();
    private final String startedAt = Instant.now().toString();
    private State state = State.BOOT;
    private int ticksInState;
    private long installEpochBefore;
    private long acceptedBefore;
    private long baseRendered;
    private long baseClientTicks;
    private long baseServerTicks;
    private long lastPresentNanos;
    private long[] warmupObserved;
    private long[] samplesObserved;
    private int heldItemsInstalledFor = -1;
    private int quietTicks;
    private DiagnosticRecorder diagnostics;
    private String failureReason = "";
    private String uncaught = "";

    private CaptureAgent(Minecraft mc, Mode mode, Path outDir, CapturePlanReader plan, long hangCeiling,
            String packArchive) {
        this.mc = mc;
        this.mode = mode;
        this.outDir = outDir;
        this.plan = plan;
        this.captures = plan == null ? List.of() : plan.captures();
        this.prepSteps = plan == null ? 0 : plan.integer("world.prepTicks") / plan.integer("clock.ticksPerFrame");
        long steps = prepSteps;
        for (CapturePlanReader.Capture c : captures) {
            steps += c.warmupFrames() + c.samples().size();
        }
        this.totalSteps = steps;
        this.hangCeilingMillis = hangCeiling;
        this.packArchive = packArchive;
        this.warmupObserved = new long[captures.size()];
        this.samplesObserved = new long[captures.size()];
    }

    /** Arms only when a conformance property is present; otherwise a no-op. */
    public static void install(Minecraft mc) {
        if (instance != null) {
            return;
        }
        String planProp = System.getProperty("schmaloogium.conformance.plan");
        String generateProp = System.getProperty("schmaloogium.conformance.generate");
        String inventoryProp = System.getProperty("schmaloogium.conformance.inventory");
        if (planProp == null && generateProp == null && inventoryProp == null) {
            return;
        }
        try {
            CaptureAgent agent;
            if (inventoryProp != null) {
                agent = new CaptureAgent(mc, Mode.INVENTORY, Path.of(inventoryProp).getParent(), null, 0, "");
            } else if (generateProp != null) {
                agent = new CaptureAgent(mc, Mode.GENERATE, Path.of(System.getProperty("schmaloogium.conformance.out")),
                        null, 0, "");
            } else {
                CapturePlanReader plan = CapturePlanReader.parse(Files.readString(Path.of(planProp), StandardCharsets.UTF_8));
                agent = new CaptureAgent(mc, Mode.CAPTURE, Path.of(System.getProperty("schmaloogium.conformance.out")),
                        plan, Long.getLong("schmaloogium.conformance.hangCeilingMillis", 20_000L),
                        System.getProperty("schmaloogium.conformance.packArchive", ""));
            }
            instance = agent;
            if (agent.mode == Mode.CAPTURE) {
                ControlledClock.holdAnimation(true); // H-CLOCK-04: no uncontrolled animation ticks
            }
            FMLCommonHandler.instance().bus().register(agent);
            LOG.info("H-CAPTURE-00 capture agent armed: mode {} out {}", agent.mode, agent.outDir);
        } catch (RuntimeException | IOException e) {
            LOG.error("capture agent could not arm: {}", e.toString());
        }
    }

    // ------------------------------------------------------------------
    // client tick driver
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || state == State.DONE || state == State.FAILED) {
            return;
        }
        try {
            step();
        } catch (Throwable t) {
            LOG.error("capture agent uncaught in state {}: {}", state, stack(t));
            uncaught = t.toString();
            fail("uncaught in state " + state + ": " + t);
        }
    }

    private void step() throws IOException {
        ticksInState++;
        switch (state) {
            case BOOT -> boot();
            case LOADING -> {
                if (mc.world != null && mc.player != null && mc.currentScreen == null
                        && mc.getIntegratedServer() != null) {
                    transition(State.SETTLING);
                }
            }
            case SETTLING -> settling();
            case APPLYING -> applying();
            case SELECTING -> selecting();
            case AWAITING_INSTALL -> awaitingInstall();
            case CONTROLLED -> controlled();
            case COMMITTING -> commit();
            default -> { }
        }
    }

    private void transition(State next) {
        LOG.info("capture agent {} -> {}", state, next);
        state = next;
        ticksInState = 0;
    }

    private void boot() throws IOException {
        if (!(mc.currentScreen instanceof GuiMainMenu) || mc.world != null) {
            return;
        }
        switch (mode) {
            case INVENTORY -> {
                writeInventory(Path.of(System.getProperty("schmaloogium.conformance.inventory")));
                shutdown();
            }
            case GENERATE -> {
                Path descriptor = Path.of(System.getProperty("schmaloogium.conformance.generate"));
                Map<String, String> d = flat(Files.readString(descriptor, StandardCharsets.UTF_8),
                        "schema = schmaloogium.world-generation/1");
                String folder = System.getProperty("schmaloogium.conformance.generateFolder");
                mc.launchIntegratedServer(folder, folder, SceneApplier.generationSettings(
                        Long.parseLong(d.get("seed")), d.get("worldType"),
                        CanonicalScalars.parseBoolean(d.get("generateStructures"))));
                transition(State.LOADING);
            }
            case CAPTURE -> {
                // Shaders off before the world exists: no composition may install (and accept a
                // frame) before checkpoint0 (§5.1.1).
                ShaderGui.settings().ifPresent(s -> s.setShaderPack("off"));
                String worldPath = plan.text("world.path");
                String folder = worldPath.substring(worldPath.lastIndexOf('/') + 1);
                Path save = mc.gameDir.toPath().resolve("saves").resolve(folder);
                String actual = TreeHash.framedTreeSha256(save);
                if (!actual.equals(plan.token("environment.worldSha256"))) {
                    fail("copied save hash " + actual + " != plan environment.worldSha256");
                    return;
                }
                Path gen = outDir.resolve("generation.worldgen");
                String genText = Files.readString(gen, StandardCharsets.UTF_8);
                if (!TreeHash.sha256Hex(genText.getBytes(StandardCharsets.UTF_8))
                        .equals(plan.token("environment.worldGenerationSha256"))) {
                    fail("retained generation descriptor hash != plan environment.worldGenerationSha256");
                    return;
                }
                Map<String, String> d = flat(genText, "schema = schmaloogium.world-generation/1");
                if (!d.get("seed").equals(plan.raw("world.seed")) || !d.get("worldType").equals(plan.raw("world.worldType"))
                        || !d.get("generateStructures").equals(plan.raw("world.generateStructures"))
                        || !d.get("minecraftVersion").equals(plan.raw("environment.minecraftVersion"))
                        || !d.get("externalModSetSha256").equals(plan.raw("environment.externalModSetSha256"))) {
                    fail("generation descriptor disagrees with the plan's world/environment facts");
                    return;
                }
                if (!inventoryMatchesPlan()) {
                    return;
                }
                mc.launchIntegratedServer(folder, folder, null);
                transition(State.LOADING);
            }
            default -> { }
        }
    }

    private void settling() throws IOException {
        if (mode == Mode.GENERATE) {
            if (ticksInState >= GENERATE_SETTLE_TICKS) {
                Files.writeString(outDir.resolve("generated.txt"), "folder = "
                        + System.getProperty("schmaloogium.conformance.generateFolder") + "\n", StandardCharsets.UTF_8);
                shutdown();
            }
            return;
        }
        if (ticksInState == 1) {
            SceneApplier.applyClientSettings(mc, plan);
            CapturePlanReader.Pose first = captures.get(0).samples().get(0);
            SceneApplier.installPose(mc.player, first, first);
        }
        if (ticksInState >= SETTLE_TICKS) {
            transition(State.APPLYING);
        }
    }

    private void applying() {
        IntegratedServer server = mc.getIntegratedServer();
        CapturePlanReader.Pose first = captures.get(0).samples().get(0);
        if (ticksInState == 1) {
            server.addScheduledTask(() -> {
                try {
                    SceneApplier.applyWorldState(server, plan, first);
                } catch (RuntimeException e) {
                    fail("world state: " + e);
                }
            });
            SceneApplier.installHeldItems(mc, server, captures.get(0).heldMain(), captures.get(0).heldOff());
            heldItemsInstalledFor = 0;
        }
        SceneApplier.installPose(mc.player, first, first);
        if (ticksInState < APPLY_TICKS) {
            return;
        }
        int width = (int) plan.integer("client.width");
        int height = (int) plan.integer("client.height");
        if (mc.displayWidth != width || mc.displayHeight != height) {
            fail("client window is " + mc.displayWidth + "x" + mc.displayHeight + ", plan needs " + width + "x" + height);
            return;
        }
        // Controlled preparation needs the terrain around the pose present on the client and the
        // chunk renderer quiet; §4.4's residual "stall during load" is otherwise a black frame.
        if (chunksReady(first) && mc.renderGlobal.hasNoChunkUpdates()) {
            quietTicks++;
        } else {
            quietTicks = 0;
        }
        if (quietTicks >= 20) {
            transition(State.SELECTING);
        } else if (ticksInState > CHUNK_READY_TIMEOUT_TICKS) {
            fail("terrain around the first pose did not load/render within " + CHUNK_READY_TIMEOUT_TICKS
                    + " ticks: loaded " + loadedChunks(first) + " of " + loadedChunksWanted() + " chunks, renderer quiet "
                    + mc.renderGlobal.hasNoChunkUpdates() + ", client player at " + mc.player.posX + "," + mc.player.posY
                    + "," + mc.player.posZ);
        }
    }

    private int loadedChunksWanted() {
        int radius = (int) plan.integer("client.renderDistance");
        return (2 * radius + 1) * (2 * radius + 1);
    }

    private int loadedChunks(CapturePlanReader.Pose pose) {
        int radius = (int) plan.integer("client.renderDistance");
        int cx = ((int) Math.floor(pose.x())) >> 4;
        int cz = ((int) Math.floor(pose.z())) >> 4;
        int loaded = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (mc.world.getChunkProvider().getLoadedChunk(cx + dx, cz + dz) != null) {
                    loaded++;
                }
            }
        }
        return loaded;
    }

    private boolean chunksReady(CapturePlanReader.Pose pose) {
        int radius = (int) plan.integer("client.renderDistance");
        int cx = ((int) Math.floor(pose.x())) >> 4;
        int cz = ((int) Math.floor(pose.z())) >> 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (mc.world.getChunkProvider().getLoadedChunk(cx + dx, cz + dz) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void selecting() {
        diagnostics = new DiagnosticRecorder(new ClientDiagnosticRouter());
        Diagnostics.install(diagnostics);
        GlErrorLedger.reset();
        FrameObservers.install(this);
        var services = ShaderGui.services();
        var access = ShaderGui.access();
        var settings = ShaderGui.settings();
        if (services.isEmpty() || access.isEmpty() || settings.isEmpty()) {
            fail("shader GUI services are not installed");
            return;
        }
        PackDiscoveryResult discovery = services.get().frontEnd().discover(
                new PackDiscoveryRequest(access.get().shaderpacksDirectory(), Diagnostics::report));
        String bare = packArchive.endsWith(".zip") ? packArchive.substring(0, packArchive.length() - 4) : packArchive;
        Optional<PackCandidate> candidate = discovery.candidates().stream()
                .filter(c -> c.kind() == PackCandidateKind.ARCHIVE || c.kind() == PackCandidateKind.DIRECTORY)
                .filter(c -> c.displayName().equals(packArchive) || c.displayName().equals(bare))
                .findFirst();
        if (candidate.isEmpty() || candidate.get().status() != PackCandidateStatus.AVAILABLE
                || candidate.get().filesystemReference().isEmpty()) {
            fail("pack archive " + packArchive + " was not discovered as AVAILABLE; candidates: "
                    + discovery.candidates().stream().map(PackCandidate::displayName).toList());
            return;
        }
        Map<String, String> engineOptions = plan.namedMap("pack.engineOptions");
        Map<String, String> committed = executableEngineOptions(settings.get().committed().values());
        if (!committed.equals(engineOptions)) {
            fail("engine option state " + committed + " != plan " + engineOptions
                    + " (v0.1 applies only the baseline; pinned engine options are not supported)");
            return;
        }
        if (!settings.get().setShaderPack(candidate.get().filesystemReference().get().canonicalValue())) {
            fail("durable selection could not be persisted");
            return;
        }
        installEpochBefore = FrameHooks.installEpoch();
        acceptedBefore = FrameHooks.acceptedFrames();
        // The clock runs from here: frames before the install are VanillaOnly, and the frame
        // in flight when the composition installs is already timed when checkpoint0 lands.
        int tpf = (int) plan.integer("clock.ticksPerFrame");
        if (tpf != 1) {
            fail("checkpoint0 accounting supports ticksPerFrame = 1 at v0.1 (plan asks " + tpf + ")");
            return;
        }
        float seconds = (float) (plan.integer("clock.frameTimeNanos") / 1_000_000_000.0);
        ControlledClock.arm(tpf, (float) plan.decimal("clock.partialTicks"), seconds, hangCeilingMillis);
        ControlledClock.gate();
        ReloadCoordinator.submitOrReportInert(new ReloadRequest(ReloadLifecycle.FULL, true, false,
                ReloadCause.PACK_SELECTION), Diagnostics::report);
        transition(State.AWAITING_INSTALL);
    }

    private void awaitingInstall() {
        Optional<ActivePipeline> active = active();
        if (active.isEmpty() || FrameHooks.installEpoch() == installEpochBefore) {
            if (ticksInState > 20 * 60) {
                fail("composition did not install within 60 s of selection");
            }
            return;
        }
        // checkpoint0 — same client tick as the install, before the next frame renders.
        long acceptedSince = FrameHooks.acceptedFrames() - acceptedBefore;
        if (acceptedSince != 0) {
            fail("an accepted shader frame preceded checkpoint0 (" + acceptedSince + ")");
            return;
        }
        if (ControlledClock.failure() != null) {
            fail(ControlledClock.failure());
            return;
        }
        ActivePipeline a = active.get();
        long registryGen = a.composition().registry().generation();
        boolean frameTimingAbsent = a.uniforms().frameTiming(registryGen, 0L).isEmpty();
        String checkpointId = "cp-" + Long.toHexString(System.nanoTime());
        // The frame in flight (its ticks already ran under the clock) is controlled step 1, so
        // the origin is one frame's worth of counters behind the values observed now.
        int tpf = ControlledClock.ticksPerFrame();
        baseRendered = ControlledClock.renderedFrames() - 1;
        baseClientTicks = ControlledClock.clientTicks() - tpf;
        baseServerTicks = ControlledClock.serverTicks() - tpf;
        ledger.admit(new TimingLedger.Origin(checkpointId, mc.world.getTotalWorldTime() - tpf, 0, 0,
                !FrameHooks.hasOpenFrame(), true, frameTimingAbsent, registryGen,
                a.composition().estate().generation(), a.composition().resourceReloadEpoch(),
                a.composition().identity().toString()));
        ledger.pending();
        lastPresentNanos = 0;
        LOG.info("H-CAPTURE-03 checkpoint0 {}: worldTick {} registry {} estate {} epoch {}; {} controlled steps",
                checkpointId, ledger.origin().worldTick(), registryGen, a.composition().estate().generation(),
                a.composition().resourceReloadEpoch(), totalSteps);
        installPoseForStep(1);
        transition(State.CONTROLLED);
    }

    private void controlled() {
        String clockFailure = ControlledClock.failure();
        if (clockFailure != null) {
            fail(clockFailure);
            return;
        }
        long next = ledger.clockStep() + 1;
        if (next > totalSteps) {
            transition(State.COMMITTING);
            return;
        }
        installPoseForStep(next);
    }

    /** Phase/capture/ordinal of a 1-based controlled step. */
    private int[] locate(long step) {
        if (step <= prepSteps) {
            return new int[] {0, -1, (int) (step - 1)}; // PREPARATION
        }
        long cursor = prepSteps;
        for (int i = 0; i < captures.size(); i++) {
            CapturePlanReader.Capture c = captures.get(i);
            if (step <= cursor + c.warmupFrames()) {
                return new int[] {1, i, (int) (step - cursor - 1)}; // WARMUP
            }
            cursor += c.warmupFrames();
            if (step <= cursor + c.samples().size()) {
                return new int[] {2, i, (int) (step - cursor - 1)}; // SAMPLE
            }
            cursor += c.samples().size();
        }
        throw new IllegalStateException("step " + step + " beyond the schedule");
    }

    private void installPoseForStep(long step) {
        int[] where = locate(step);
        int captureIndex = where[1] < 0 ? 0 : where[1];
        CapturePlanReader.Capture capture = captures.get(captureIndex);
        CapturePlanReader.Pose current;
        CapturePlanReader.Pose previous;
        if (where[0] == 2) {
            current = capture.samples().get(where[2]);
            previous = capture.samples().get(Math.max(0, where[2] - 1));
        } else {
            current = capture.samples().get(0);
            previous = current;
        }
        if (where[0] != 0 && heldItemsInstalledFor != captureIndex) {
            SceneApplier.installHeldItems(mc, mc.getIntegratedServer(), capture.heldMain(), capture.heldOff());
            heldItemsInstalledFor = captureIndex;
        }
        SceneApplier.installPose(mc.player, current, previous);
    }

    // ------------------------------------------------------------------
    // H-CAPTURE-01: render thread, after final, before presentation
    // ------------------------------------------------------------------

    @Override
    public void beforePresent(FinalizedFrame frame) {
        if (state != State.CONTROLLED) {
            return;
        }
        try {
            observe(frame);
        } catch (Throwable t) {
            LOG.error("capture agent uncaught at the capture hook: {}", stack(t));
            uncaught = t.toString();
            fail("uncaught at the capture hook: " + t);
        }
    }

    private void observe(FinalizedFrame frame) throws IOException {
        long step = ledger.clockStep() + 1;
        if (step > totalSteps) {
            fail("an extra shader frame was accepted after the last scheduled step");
            return;
        }
        int[] where = locate(step);
        Optional<ActivePipeline> active = active();
        if (active.isEmpty()) {
            fail("no active pipeline at the capture hook");
            return;
        }
        Optional<UniformFrameTiming> timing = active.get().uniforms()
                .frameTiming(frame.readiness().registryGeneration(), frame.frameId());
        if (timing.isEmpty()) {
            fail("P6 reported no frame timing for frame " + frame.frameId());
            return;
        }
        UniformFrameTiming t = timing.get();
        int tpf = ControlledClock.ticksPerFrame();
        long originWorld = ledger.origin().worldTick();
        long worldTick = mc.world.getTotalWorldTime();
        long clientTicks = ControlledClock.clientTicks() - baseClientTicks;
        long serverTicks = ControlledClock.serverTicks() - baseServerTicks;
        long rendered = ControlledClock.renderedFrames() - baseRendered;
        long accepted = FrameHooks.acceptedFrames() - acceptedBefore;
        long logicalRel = t.logicalTick() - originWorld;
        boolean valid = rendered == step
                && clientTicks == step * tpf && serverTicks == step * tpf
                && worldTick == originWorld + step * tpf && logicalRel == step * tpf
                && t.frameCounter() == step && accepted == step
                && frame.readiness().registryGeneration() == ledger.origin().registryGeneration();
        String phase = where[0] == 0 ? "PREPARATION" : where[0] == 1 ? "WARMUP" : "SAMPLE";
        TimingLedger.Step record = new TimingLedger.Step(phase, where[1], where[2], valid,
                t.registryGeneration(), t.frameId(), t.worldEpoch(), logicalRel, t.smoothingTimeTicks(),
                t.frameTimeSeconds(), t.frameCounter(), t.frameTimeCounter(), worldTick, clientTicks,
                mc.getRenderPartialTicks(), step, clientTicks, serverTicks, accepted, step);
        ledger.record(record);
        if (!valid) {
            fail("controlled step " + step + " (" + phase + ") observation mismatch: rendered "
                    + rendered + " client " + clientTicks + " server " + serverTicks
                    + " world " + worldTick + " (origin " + originWorld + ") logical " + logicalRel
                    + " frameCounter " + t.frameCounter() + " accepted " + accepted);
            return;
        }
        if (where[0] == 1) {
            warmupObserved[where[1]]++;
        }
        if (where[0] == 2) {
            CapturePlanReader.Capture capture = captures.get(where[1]);
            samplesObserved[where[1]]++;
            long now = System.nanoTime();
            long duration = lastPresentNanos == 0 ? 0 : Math.max(0, (now - lastPresentNanos) / 1_000_000L);
            int ordinal = where[2];
            boolean inWindow = ordinal >= capture.captureStartSample()
                    && ordinal < capture.captureStartSample() + capture.captureSampleCount();
            CapturePlanReader.Pose actualCurrent = new CapturePlanReader.Pose(mc.player.posX, mc.player.posY,
                    mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
            CapturePlanReader.Pose actualPrevious = new CapturePlanReader.Pose(mc.player.prevPosX, mc.player.prevPosY,
                    mc.player.prevPosZ, mc.player.prevRotationYaw, mc.player.prevRotationPitch);
            frames.add(new FrameRow(capture.kind(), capture.id(), ordinal, inWindow,
                    capture.samples().get(ordinal), capture.samples().get(Math.max(0, ordinal - 1)),
                    actualCurrent, actualPrevious, record, mc.world.loadedEntityList.size(), duration));
            if (inWindow) {
                String rel = "images/" + capture.kind() + "-" + capture.id() + "-" + ordinal + ".png";
                FrameGrabber.Grab grab = FrameGrabber.grab(mc, outDir.resolve(rel));
                images.add(new ImageRow(capture.kind(), capture.id(), ordinal, rel, grab.width(), grab.height(), grab.pixelSha256()));
                if (images.size() == 1) {
                    LOG.info("H-CAPTURE-01 first frame grabbed after final, before presentation: {} {}x{} {}",
                            rel, grab.width(), grab.height(), grab.pixelSha256());
                }
            }
        }
        lastPresentNanos = System.nanoTime();
    }

    // ------------------------------------------------------------------
    // commit / failure / shutdown
    // ------------------------------------------------------------------

    private void commit() throws IOException {
        ControlledClock.release();
        FrameObservers.clear();
        Optional<ActivePipeline> active = active();
        boolean same = active.isPresent()
                && active.get().composition().registry().generation() == ledger.origin().registryGeneration()
                && active.get().composition().estate().generation() == ledger.origin().estateGeneration()
                && active.get().composition().resourceReloadEpoch() == ledger.origin().resourceEpoch()
                && active.get().composition().identity().toString().equals(ledger.origin().pipelineIdentity());
        ledger.close(true, same);
        writeManifest("COMPLETE", "", "");
        LOG.info("H-CAPTURE-02 manifest committed ({} steps, {} frames, {} images); clean shutdown",
                ledger.clockStep(), frames.size(), images.size());
        transition(State.DONE);
        shutdown();
    }

    private void fail(String reason) {
        if (state == State.FAILED || state == State.DONE) {
            return;
        }
        LOG.error("capture agent FAILED: {}", reason);
        failureReason = reason;
        ledger.fail(reason);
        ControlledClock.release();
        FrameObservers.clear();
        if (ledger.available() && !ledger.restoration().equals("RESTORED")) {
            ledger.close(true, false);
        }
        State previous = state;
        state = State.FAILED;
        if (mode == Mode.CAPTURE && plan != null) {
            try {
                writeManifest("FAILED", reason, uncaught);
            } catch (Throwable e) {
                LOG.error("failure manifest could not be written: {}", stack(e));
            }
        }
        LOG.error("capture agent disarmed in state {}; shutting down cleanly", previous);
        shutdown();
    }

    private static String stack(Throwable t) {
        StringBuilder sb = new StringBuilder(t.toString());
        StackTraceElement[] frames = t.getStackTrace();
        for (int i = 0; i < Math.min(12, frames.length); i++) {
            sb.append("\n    at ").append(frames[i]);
        }
        if (t.getCause() != null && t.getCause() != t) {
            sb.append("\n  caused by ").append(t.getCause());
        }
        return sb.toString();
    }

    private void shutdown() {
        if (state != State.FAILED) {
            state = State.DONE;
        }
        mc.addScheduledTask(mc::shutdown);
    }

    private static Optional<ActivePipeline> active() {
        return PipelineBootstrap.coordinator().flatMap(c -> c.transaction().active());
    }

    // ------------------------------------------------------------------
    // inventory (§5.1.1)
    // ------------------------------------------------------------------

    private void writeInventory(Path out) throws IOException {
        TreeMap<String, String[]> mods = new TreeMap<>();
        for (ModContainer container : Loader.instance().getActiveModList()) {
            String id = container.getModId();
            if (id.equals("schmaloogium")) {
                mods.put(id, new String[] {"", ""});
                continue;
            }
            java.io.File source = container.getSource();
            String path = source == null ? "" : source.getAbsolutePath();
            String sha = "";
            if (source != null && source.isFile()) {
                sha = TreeHash.fileSha256(source.toPath());
            } else if (source != null && source.isDirectory()) {
                sha = TreeHash.framedTreeSha256(source.toPath());
            }
            mods.put(id, new String[] {path, sha});
        }
        StringBuilder sb = new StringBuilder("schema = schmaloogium.launch-inventory/1\n");
        TreeMap<String, String> lines = new TreeMap<>();
        int i = 0;
        for (Map.Entry<String, String[]> e : mods.entrySet()) {
            if (e.getValue()[1].isEmpty() && !e.getKey().equals("schmaloogium")) {
                continue; // containers without an artifact (e.g. the loader's synthetic mods)
            }
            lines.put("mods." + i + ".id", CanonicalScalars.encodeJson(e.getKey()));
            lines.put("mods." + i + ".source", CanonicalScalars.encodeJson(e.getValue()[0]));
            lines.put("mods." + i + ".sha256", e.getValue()[1].isEmpty() ? "subject" : e.getValue()[1]);
            i++;
        }
        lines.put("mods.count", Integer.toString(i));
        for (Map.Entry<String, String> e : lines.entrySet()) {
            sb.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
        }
        // the subject's hash is the runner's to compute (it anchors the launch spec); write its
        // record with the placeholder the runner replaces after verifying the directories.
        String text = sb.toString().replace("= subject\n", "= " + "0".repeat(64) + "\n");
        Files.createDirectories(out.getParent());
        Files.writeString(out, text, StandardCharsets.UTF_8);
        LOG.info("launch inventory written: {} mods -> {}", i, out);
    }

    /** The loaded mod ids must equal the plan's inventory ids (hashes are the runner's). */
    private boolean inventoryMatchesPlan() {
        java.util.Set<String> loaded = new java.util.TreeSet<>();
        for (ModContainer container : Loader.instance().getActiveModList()) {
            java.io.File source = container.getSource();
            if (container.getModId().equals("schmaloogium") || (source != null && (source.isFile() || source.isDirectory()))) {
                loaded.add(container.getModId());
            }
        }
        java.util.Set<String> planned = new java.util.TreeSet<>();
        int n = plan.count("environment.mods");
        for (int i = 0; i < n; i++) {
            planned.add(plan.text("environment.mods." + i + ".id"));
        }
        if (!loaded.equals(planned)) {
            fail("loaded mod inventory " + loaded + " != plan inventory " + planned);
            return false;
        }
        return true;
    }

    private static Map<String, String> flat(String text, String schemaLine) {
        String[] lines = text.split("\n", -1);
        if (lines.length == 0 || !lines[0].equals(schemaLine)) {
            throw new IllegalArgumentException("expected " + schemaLine);
        }
        Map<String, String> out = new TreeMap<>();
        for (int i = 1; i < lines.length; i++) {
            int split = lines[i].indexOf(" = ");
            if (split > 0) {
                out.put(lines[i].substring(0, split), lines[i].substring(split + 3));
            }
        }
        return out;
    }

    // ------------------------------------------------------------------
    // the run manifest (§4.5.4)
    // ------------------------------------------------------------------

    private void writeManifest(String exitStatus, String reason, String uncaughtText) throws IOException {
        ManifestEmitter m = new ManifestEmitter();
        Optional<ActivePipeline> active = active();
        boolean complete = ledger.complete(totalSteps);
        m.token("run.id", plan.token("run.id"))
                .token("run.sceneId", plan.token("scene.id"))
                .token("run.sceneHash", plan.token("scene.hash"))
                .token("run.planHash", plan.planHash())
                .token("run.exitStatus", exitStatus)
                .text("run.failureReason", reason)
                .text("run.uncaughtException", uncaughtText)
                .token("run.compatVerdict", active.isPresent() ? "Continue" : "NOT_REACHED")
                .bool("run.shadersActiveThroughout", exitStatus.equals("COMPLETE") && complete)
                .integer("run.hangCeilingMillis", hangCeilingMillis)
                .bool("run.timedOut", false)
                .text("run.startedAt", startedAt)
                .text("run.endedAt", Instant.now().toString());
        m.bool("frontEnd.completed", active.isPresent())
                .bool("frontEnd.packConfigurationProduced", active.isPresent());
        m.token("environment.os", System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT).replaceAll("\\s+", "-"))
                .token("environment.jvm", Runtime.version().toString())
                .token("environment.minecraftVersion", plan.token("environment.minecraftVersion"))
                .token("environment.cleanroomVersion", loaderVersion())
                .token("environment.worldSha256", plan.token("environment.worldSha256"))
                .token("environment.modSetSha256", plan.token("environment.modSetSha256"))
                .token("environment.subjectModId", plan.token("environment.subjectModId"))
                .token("environment.subjectJarSha256", plan.token("environment.subjectJarSha256"))
                .token("environment.externalModSetSha256", plan.token("environment.externalModSetSha256"))
                .token("environment.worldGenerationSha256", plan.token("environment.worldGenerationSha256"));
        int mods = plan.count("environment.mods");
        m.family("environment.mods");
        for (int i = 0; i < mods; i++) {
            String p = m.row("environment.mods");
            m.text(p + "id", plan.text("environment.mods." + i + ".id"));
            m.token(p + "sha256", plan.token("environment.mods." + i + ".sha256"));
        }
        m.family("environment.resourcePacks");
        m.integer("clock.frameTimeNanos", plan.integer("clock.frameTimeNanos"))
                .integer("clock.ticksPerFrame", plan.integer("clock.ticksPerFrame"))
                .decimal("clock.partialTicks", plan.decimal("clock.partialTicks"));
        Optional<GLCapabilityProfile> profile = BootstrapHooks.capturedProfile();
        m.bool("gl.available", profile.isPresent());
        if (profile.isPresent()) {
            StringWriter w = new StringWriter();
            profile.get().write(w);
            m.text("gl.profile_text", w.toString());
        }
        m.text("pack.id", plan.text("pack.id")).text("pack.version", plan.text("pack.version"))
                .token("pack.acquisitionMode", plan.token("pack.acquisitionMode"))
                .token("pack.archiveSha512", plan.token("pack.archiveSha512"))
                .text("pack.licence", plan.text("pack.licence"))
                .token("pack.optionStateSha256", plan.token("pack.optionStateSha256"));
        Map<String, String> packOptions = new TreeMap<>();
        if (active.isPresent()) {
            for (Map.Entry<String, OptionValue> e : active.get().configuration().options().state().values().entrySet()) {
                packOptions.put(e.getKey(), renderOption(e.getValue()));
            }
        } else {
            packOptions.putAll(plan.namedMap("pack.options"));
        }
        namedRows(m, "pack.options", packOptions);
        Map<String, String> engineOptions = ShaderGui.settings().map(s -> s.committed().values())
                .map(CaptureAgent::executableEngineOptions).orElse(plan.namedMap("pack.engineOptions"));
        namedRows(m, "pack.engineOptions", engineOptions);
        // programs
        m.family("programs");
        if (active.isPresent() && active.get().composition().registry().registry().isPresent()) {
            for (ProgramResolutionProjection r : active.get().composition().registry().registry().get().resolutions()) {
                String p = m.row("programs");
                m.token(p + "slot", r.slot().packName());
                m.token(p + "status", r.status().name());
                m.text(p + "from", r.from().map(com.schmaloogium.engine.registry.ProgramSlotId::packName).orElse(""));
                m.bool(p + "sourcePresent", r.sourcePresent());
                m.token(p + "ownBuild", r.ownBuild().name());
                m.text(p + "driverLog", r.driverLog());
            }
        }
        // resources (P5 REALIZED snapshot)
        BufferResourceSnapshot resources = active.map(a -> a.composition().estate().resources()).orElse(null);
        if (resources instanceof BufferResourceSnapshot.Available available) {
            m.bool("resources.available", true);
            resourcesBlock(m, available.projection());
        } else {
            m.bool("resources.available", false);
        }
        // hooks
        m.bool("hooks.available", true);
        m.family("hooks.rows");
        for (HookApplicationReport.Row row : HookApplicationReport.rows()) {
            String p = m.row("hooks.rows");
            m.text(p + "catalogId", row.catalogId());
            m.text(p + "target", row.target());
            m.integer(p + "expectedCount", row.expectedCount());
            m.integer(p + "actualCount", row.actualCount());
            m.integer(p + "classes.count", row.classes().size());
            for (int i = 0; i < row.classes().size(); i++) {
                m.token(p + "classes." + i, row.classes().get(i));
            }
            m.token(p + "fallback", row.fallback());
        }
        m.family("hooks.subreports");
        // captures
        m.family("captures");
        for (int i = 0; i < captures.size(); i++) {
            CapturePlanReader.Capture c = captures.get(i);
            String p = m.row("captures");
            m.token(p + "kind", c.kind());
            m.text(p + "id", c.id());
            m.integer(p + "plannedWarmupFrames", c.warmupFrames());
            m.integer(p + "actualWarmupFrames", warmupObserved[i]);
            m.integer(p + "plannedSamples", c.samples().size());
            m.integer(p + "actualSamples", samplesObserved[i]);
            m.integer(p + "captureStartSample", c.captureStartSample());
            m.integer(p + "captureSampleCount", c.captureSampleCount());
        }
        // frames
        m.family("frames");
        for (FrameRow f : frames) {
            String p = m.row("frames");
            m.token(p + "captureKind", f.kind());
            m.text(p + "captureId", f.id());
            m.integer(p + "sampleOrdinal", f.ordinal());
            m.bool(p + "captured", f.captured());
            pose(m, p + "plannedCurrent.", f.plannedCurrent());
            pose(m, p + "plannedPrevious.", f.plannedPrevious());
            pose(m, p + "actualCurrent.", f.actualCurrent());
            pose(m, p + "actualPrevious.", f.actualPrevious());
            TimingLedger.Step s = f.step();
            m.integer(p + "worldTick", s.worldTick());
            m.decimal(p + "partialTicks", s.partialTicks());
            m.integer(p + "frameCounter", s.frameCounter());
            m.integer(p + "logicalTick", s.logicalTick());
            m.integer(p + "animationTick", s.animationTick());
            m.decimal(p + "smoothingTimeTicks", s.smoothingTimeTicks());
            m.decimal(p + "frameTimeSeconds", s.frameTimeSeconds());
            m.decimal(p + "frameTimeCounter", s.frameTimeCounter());
            m.integer(p + "clockStep", s.clockStep());
            m.integer(p + "entityCount", f.entityCount());
            m.integer(p + "durationMillis", f.durationMillis());
        }
        // images
        m.family("images");
        for (ImageRow img : images) {
            String p = m.row("images");
            m.token(p + "captureKind", img.kind());
            m.text(p + "captureId", img.id());
            m.integer(p + "sampleOrdinal", img.ordinal());
            m.text(p + "path", img.path());
            m.integer(p + "width", img.width());
            m.integer(p + "height", img.height());
            m.token(p + "pixelSha256", img.sha());
        }
        // gl_errors: P6 replay-aware ledger + every glue drain
        m.family("gl_errors");
        List<ReplayAwareGLError> errors = new ArrayList<>();
        active.ifPresent(a -> {
            for (UniformReplayReport report : a.replay().ledger()) {
                errors.addAll(report.errors());
            }
        });
        errors.addAll(GlErrorLedger.snapshot());
        for (ReplayAwareGLError e : errors) {
            String p = m.row("gl_errors");
            m.token(p + "op", e.error().op().isEmpty() ? "unknown" : e.error().op().replaceAll("\\s+", "_"));
            m.text(p + "subject", e.error().subjectLabel() == null ? "" : e.error().subjectLabel());
            m.token(p + "kind", e.error().kind().name());
            m.text(p + "detail", e.error().detail() == null ? "" : e.error().detail());
            m.bool(p + "attributed", e.attributed());
        }
        // diagnostics
        m.family("diagnostics");
        if (diagnostics != null) {
            for (EngineDiagnostic d : diagnostics.snapshot()) {
                String p = m.row("diagnostics");
                m.text(p + "code", d.messageKey());
                m.token(p + "severity", d.severity().name());
                m.token(p + "channel", d.channel().name());
                m.text(p + "file", "");
                m.integer(p + "line", 0);
            }
        }
        // timing (D-P2-50)
        m.bool("timing.available", ledger.available())
                .bool("timing.complete", complete)
                .text("timing.failureReason", ledger.failureReason())
                .token("timing.restoration", ledger.restoration());
        if (ledger.available()) {
            TimingLedger.Origin o = ledger.origin();
            m.bool("timing.identitiesUnchanged", ledger.identitiesUnchanged());
            m.text("timing.origin.checkpointId", o.checkpointId())
                    .integer("timing.origin.clockStep", 0)
                    .integer("timing.origin.worldTick", o.worldTick())
                    .integer("timing.origin.logicalTick", 0)
                    .integer("timing.origin.animationTick", 0)
                    .integer("timing.origin.clientTicks", 0)
                    .integer("timing.origin.serverTicks", 0)
                    .integer("timing.origin.acceptedFrames", o.acceptedFrames())
                    .integer("timing.origin.finalizedFrames", o.finalizedFrames())
                    .integer("timing.origin.frameCounter", 0)
                    .decimal("timing.origin.frameTimeCounter", 0.0)
                    .bool("timing.origin.quiescent", o.quiescent())
                    .bool("timing.origin.freshRuntime", o.freshRuntime())
                    .bool("timing.origin.frameTimingAbsent", o.frameTimingAbsent());
        } else {
            m.bool("timing.identitiesUnchanged", false);
        }
        m.family("timing.steps");
        for (TimingLedger.Step s : ledger.steps()) {
            String p = m.row("timing.steps");
            m.token(p + "phase", s.phase());
            m.integer(p + "captureIndex", s.captureIndex());
            m.integer(p + "ordinal", s.ordinal());
            m.token(p + "validation", s.valid() ? "VALID" : "INVALID");
            m.integer(p + "registryGeneration", s.registryGeneration());
            m.integer(p + "frameId", s.frameId());
            m.integer(p + "worldEpoch", s.worldEpoch());
            m.integer(p + "logicalTick", s.logicalTick());
            m.decimal(p + "smoothingTimeTicks", s.smoothingTimeTicks());
            m.decimal(p + "frameTimeSeconds", s.frameTimeSeconds());
            m.integer(p + "frameCounter", s.frameCounter());
            m.decimal(p + "frameTimeCounter", s.frameTimeCounter());
            m.integer(p + "worldTick", s.worldTick());
            m.integer(p + "animationTick", s.animationTick());
            m.decimal(p + "partialTicks", s.partialTicks());
            m.integer(p + "clockStep", s.clockStep());
            m.integer(p + "clientTicks", s.clientTicks());
            m.integer(p + "serverTicks", s.serverTicks());
            m.integer(p + "acceptedFrames", s.acceptedFrames());
            m.integer(p + "finalizedFrames", s.finalizedFrames());
        }
        Path target = outDir.resolve("manifest.tmp");
        Path part = outDir.resolve("manifest.tmp.part");
        Files.createDirectories(outDir);
        Files.writeString(part, m.render(), StandardCharsets.UTF_8);
        Files.move(part, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                java.nio.file.StandardCopyOption.ATOMIC_MOVE);
    }

    private static void resourcesBlock(ManifestEmitter m, BufferResourceProjection r) {
        m.token("resources.evidence_stage", r.evidenceStage().name());
        m.family("resources.colorBuffers");
        for (ColorBufferResource c : r.colorBuffers()) {
            String p = m.row("resources.colorBuffers");
            m.text(p + "requested_format", c.requestedFormat() instanceof ColorAttachmentFormat.Explicit e
                    ? e.format().name() : "DEFAULT_RGBA");
            m.bool(p + "clear", c.clear());
            if (c.clearPolicy() instanceof ResourceClearPolicy.Constant k) {
                m.token(p + "clear_policy", "CONSTANT");
                m.decimal(p + "clear_color_r", k.r()).decimal(p + "clear_color_g", k.g())
                        .decimal(p + "clear_color_b", k.b()).decimal(p + "clear_color_a", k.a());
            } else {
                m.token(p + "clear_policy", "FOG_RGB_ALPHA_ONE");
            }
            c.allocation().ifPresent(a -> {
                m.text(p + "allocated_format", a.format());
                m.token(p + "allocation_origin", a.origin().name());
            });
        }
        m.integer("resources.depthTextures.count", r.depthTextures());
        m.integer("resources.shadow.depthTextures", r.shadow().depthTextures())
                .integer("resources.shadow.colorTextures", r.shadow().colorTextures())
                .integer("resources.shadow.resolution", r.shadow().resolution());
        m.family("resources.shadow.depth");
        for (ShadowTextureResource s : r.shadow().depth()) {
            String p = m.row("resources.shadow.depth");
            m.bool(p + "hardwareFiltering", s.hardwareFiltering()).bool(p + "mipmap", s.mipmap()).bool(p + "nearest", s.nearest());
        }
        m.family("resources.shadow.color");
        for (ShadowTextureResource s : r.shadow().color()) {
            String p = m.row("resources.shadow.color");
            m.bool(p + "hardwareFiltering", s.hardwareFiltering()).bool(p + "mipmap", s.mipmap()).bool(p + "nearest", s.nearest());
        }
        m.bool("resources.centerDepthSmooth.enabled", r.centerDepthSmoothEnabled());
        m.integer("resources.noise.resolution", r.noiseResolution());
        m.family("resources.vertexAttributes");
        for (VertexAttributeResource v : r.vertexAttributes()) {
            String p = m.row("resources.vertexAttributes");
            m.text(p + "program", v.program()).text(p + "name", v.name());
        }
        m.family("resources.instances");
        for (InstanceResource inst : r.instances()) {
            String p = m.row("resources.instances");
            m.text(p + "program", inst.program()).integer(p + "count", inst.count());
        }
        m.token("resources.capabilityGate", r.capabilityGate().name());
        m.family("resources.capabilityShortfalls");
        for (CapabilityShortfall s : r.capabilityShortfalls()) {
            String p = m.row("resources.capabilityShortfalls");
            m.token(p + "limit", s.limit().wireName()).integer(p + "required", s.required())
                    .integer(p + "available", s.available());
        }
    }

    private static void namedRows(ManifestEmitter m, String family, Map<String, String> map) {
        m.family(family);
        for (Map.Entry<String, String> e : new TreeMap<>(map).entrySet()) {
            String p = m.row(family);
            m.text(p + "name", e.getKey()).text(p + "value", e.getValue());
        }
    }

    private static void pose(ManifestEmitter m, String prefix, CapturePlanReader.Pose pose) {
        m.decimal(prefix + "posX", pose.x()).decimal(prefix + "posY", pose.y()).decimal(prefix + "posZ", pose.z())
                .decimal(prefix + "yaw", pose.yaw()).decimal(prefix + "pitch", pose.pitch());
    }

    private static String renderOption(OptionValue value) {
        if (value instanceof com.schmaloogium.engine.config.BooleanOptionValue b) {
            return b.value() ? "true" : "false";
        }
        if (value instanceof com.schmaloogium.engine.config.TextOptionValue t) {
            return t.value();
        }
        return value.toString();
    }

    /** The eight executable engine settings; the durable pack selection key is not one of them. */
    private static Map<String, String> executableEngineOptions(Map<String, String> committed) {
        Map<String, String> out = new TreeMap<>(committed);
        out.remove("shaderPack");
        return out;
    }

    private static String loaderVersion() {
        for (ModContainer c : Loader.instance().getActiveModList()) {
            if (c.getModId().equals("cleanroom") || c.getModId().equals("forge")) {
                return c.getVersion().replaceAll("\\s+", "_");
            }
        }
        return "unknown";
    }
}
