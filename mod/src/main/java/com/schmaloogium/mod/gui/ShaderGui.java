// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.expr.api.ExpressionDiagnosticGuiSnapshot;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackFrontEndServices;
import com.schmaloogium.engine.pack.PackFrontEnds;
import com.schmaloogium.engine.pack.PackOptionsTarget;
import com.schmaloogium.engine.pack.PackOptionsTargetAcquisition;
import com.schmaloogium.engine.pack.PersistenceRootConfiguration;
import com.schmaloogium.mod.core.ShaderErrorStore;
import com.schmaloogium.mod.gui.model.EngineSettingsController;
import com.schmaloogium.mod.gui.model.GuiText;
import com.schmaloogium.mod.gui.model.OptionEditSessionImpl;
import com.schmaloogium.mod.gui.model.PackSelectionController;
import com.schmaloogium.mod.gui.model.ReloadCause;
import com.schmaloogium.mod.gui.model.ReloadCoordinator;
import com.schmaloogium.mod.gui.model.ReloadLifecycle;
import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ScreenId;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The client composition root for the GUI phase (PHASE_12_DOC §2.1, mod side): builds
 * the Phase 3 bundle, the persistence access, the controllers and the vanilla view,
 * then installs the platform bindings. Client-thread only. The reload submitter routes
 * through {@link ReloadCoordinator#submitOrReportInert}: with no Phase 7 coordinator
 * installed every trigger is inert and logged once (§6), while the GUI still opens,
 * reads and persists.
 */
@SideOnly(Side.CLIENT)
public final class ShaderGui {

    private static volatile Instance instance;

    private ShaderGui() {
    }

    /** Installs the GUI surface once during client init (from the client proxy). */
    public static void install(Minecraft client) {
        if (instance != null) {
            return;
        }
        instance = new Instance(client);
        if (instance.bindings != null) {
            instance.bindings.register();
        }
        FMLCommonHandler.instance().bus().register(new Object() {
            @SubscribeEvent
            public void onClientTick(TickEvent.ClientTickEvent event) {
                if (event.phase != TickEvent.Phase.END
                        || client.currentScreen != null
                        || instance.selection == null
                        || instance.bindings.openScreenBinding() == null) {
                    return;
                }
                if (instance.bindings.openScreenBinding().isPressed()) {
                    showPackSelection();
                }
            }
        });
    }

    /** The Phase 3 services bundle, once installed (composition-root read seam). */
    public static Optional<PackFrontEndServices> services() {
        Instance current = instance;
        return current == null ? Optional.empty() : Optional.ofNullable(current.services);
    }

    /** The persistence access, once installed and not degraded. */
    public static Optional<BundleIo.Access> access() {
        Instance current = instance;
        return current == null ? Optional.empty() : Optional.ofNullable(current.access);
    }

    /** The one {@code optionsshaders.txt} owner, once installed and not degraded. */
    public static Optional<EngineSettingsController> settings() {
        Instance current = instance;
        return current == null ? Optional.empty() : Optional.ofNullable(current.settings);
    }

    /** Replaces the live-configuration source (the Phase 7 publication bridge). */
    public static void installConfigurationSource(
            Supplier<Optional<OptionConfiguration>> source) {
        Instance current = instance;
        if (current != null) {
            current.configurationSource = source;
        }
    }

    /** Replaces the P11 snapshot source (Phase 7 publishes; read on present). */
    public static void installExpressionDiagnostics(
            Supplier<Optional<ExpressionDiagnosticGuiSnapshot>> source) {
        Instance current = instance;
        if (current != null) {
            current.expressionDiagnostics = source;
        }
    }

    /** Opens the pack-selection screen (keybind or future entry points). */
    public static void showPackSelection() {
        Instance current = instance;
        if (current == null || current.selection == null || current.settings == null) {
            return;
        }
        current.view.showPackSelection(current.selection.model(), current.settings.model(),
                current.selection);
        current.view.showErrors(ShaderErrorStore.snapshot(),
                current.expressionDiagnostics.get());
    }

    private static void submit(ReloadRequest request) {
        ReloadCoordinator.submitOrReportInert(request, Diagnostics::report);
    }

    /** One chat line naming the classified lifecycle + cause, or the inert notice. */
    private static void acknowledgeReload() {
        Minecraft client = Minecraft.getMinecraft();
        if (client == null || client.player == null) {
            return;
        }
        boolean installed = ReloadCoordinator.installed().isPresent();
        client.player.sendMessage(new net.minecraft.util.text.TextComponentTranslation(
                installed ? "schmaloogium.command.reloadshaders.done"
                        : "schmaloogium.command.reloadshaders.inert",
                ReloadLifecycle.FULL.name(), ReloadCause.KEYBIND.name()));
    }

    /** The client-thread composition; built once at install. */
    private static final class Instance {

        private final Minecraft client;
        private final PackFrontEndServices services;
        private final BundleIo.Access access;
        private final GuiText text;
        private final EngineSettingsController settings;
        private final PackSelectionController selection;
        private final VanillaOptionScreens view;
        private final ShaderPackKeyBindings bindings;
        private volatile Supplier<Optional<OptionConfiguration>> configurationSource =
                Optional::empty;
        private volatile Supplier<Optional<ExpressionDiagnosticGuiSnapshot>>
                expressionDiagnostics = Optional::empty;

        Instance(Minecraft client) {
            this.client = client;
            Path gameDir = client.gameDir.toPath();
            Path shaderpacks = gameDir.resolve("shaderpacks");
            try {
                // First launch: the persistence root does not exist yet. The engine's
                // root acquisition validates existence only, so the composition root
                // creates it here (§G2.4: a missing folder must degrade, never crash).
                java.nio.file.Files.createDirectories(shaderpacks);
            } catch (Exception e) {
                Diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                        UserChannel.LOG_ONLY, "schmaloogium.warn.gui.rootCreateFailed",
                        List.of(), String.valueOf(e), "schmaloogium.config"));
            }
            this.services = PackFrontEnds.create();
            this.access = BundleIo.acquire(services, new PersistenceRootConfiguration(
                    shaderpacks, gameDir), Diagnostics::report).orElse(null);
            String languageCode =
                    client.getLanguageManager().getCurrentLanguage().getLanguageCode();
            this.text = new GuiText(Map.of(), GuiMessageAssets.load(languageCode),
                    languageCode);
            if (access == null) {
                // Degraded: no usable roots; screens stay closed rather than lying.
                this.settings = null;
                this.selection = null;
                this.view = null;
                this.bindings = null;
                return;
            }
            this.settings = new EngineSettingsController(
                    access.globalIo(EngineSettingsController.baseline(), Diagnostics::report),
                    EngineSettingsController.inertGates(), text, Diagnostics::report);
            this.selection = new PackSelectionController(services.frontEnd(),
                    access.shaderpacksDirectory(), settings, new SelectionHost(), text);
            this.view = new VanillaOptionScreens(client);
            // Intents mutate controller state; the screen holds a snapshot, so it asks
            // the producer for a fresh publish (selection marker, status line, rows).
            this.view.installPackSelectionRepublisher(ShaderGui::showPackSelection);
            // F3+R arms itself the moment Phase 7 installs its coordinator; before
            // that the chord is inert and the key is never shadowed (D-P12-13).
            this.bindings = new ShaderPackKeyBindings(
                    () -> ReloadCoordinator.installed().isPresent(),
                    ShaderGui::submit, ShaderGui::acknowledgeReload);
        }

        /** The host the selection controller drives into the view. */
        private final class SelectionHost implements PackSelectionController.Host {

            @Override
            public void openFolder(Path shaderpacksDirectory) {
                try {
                    java.io.File folder = shaderpacksDirectory.toFile();
                    if (folder.isDirectory() && java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().open(folder);
                    }
                } catch (Exception e) {
                    Diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                            UserChannel.LOG_ONLY, "schmaloogium.warn.gui.openFolderFailed",
                            List.of(), String.valueOf(e), "schmaloogium.config"));
                }
            }

            @Override
            public void showOptions() {
                Optional<OptionConfiguration> configuration = configurationSource.get();
                if (configuration.isEmpty()) {
                    // Degraded (§6): no active publication to edit yet; report honestly.
                    if (client.player != null) {
                        client.player.sendMessage(new net.minecraft.util.text.TextComponentTranslation(
                                "schmaloogium.gui.optionsUnavailable"));
                    }
                    return;
                }
                OptionConfiguration loaded = configuration.get();
                OptionEditSessionImpl session = OptionEditSessionImpl.filesystem(loaded,
                        text, access.packIo(packTarget(), loaded.catalog(),
                                Diagnostics::report),
                        Optional.empty(), Diagnostics::report);
                view.showOptions(session.present(ScreenId.MAIN), ScreenId.MAIN, session);
                view.showErrors(ShaderErrorStore.snapshot(), expressionDiagnostics.get());
            }

            @Override
            public void close() {
                view.close();
            }

            /** The bundle-issued safe target matching the persisted durable string. */
            private PackOptionsTarget packTarget() {
                PackFrontEnd frontEnd = services.frontEnd();
                String durable = settings.shaderPack();
                var resolution = frontEnd.discover(new PackDiscoveryRequest(
                        access.shaderpacksDirectory(), Diagnostics::report));
                return resolution.candidates().stream()
                        .filter(candidate -> candidate.filesystemReference()
                                .map(reference -> reference.canonicalValue().equals(durable))
                                .orElse(false))
                        .findFirst()
                        .flatMap(candidate -> switch (
                                frontEnd.packOptionsTarget(candidate.id())) {
                            case PackOptionsTargetAcquisition.Acquired a ->
                                    Optional.of(a.target());
                            case PackOptionsTargetAcquisition.Rejected rejected ->
                                    Optional.<PackOptionsTarget>empty();
                        })
                        .orElseThrow(() -> new IllegalStateException(
                                "options target unavailable for the active pack"));
            }
        }
    }
}
