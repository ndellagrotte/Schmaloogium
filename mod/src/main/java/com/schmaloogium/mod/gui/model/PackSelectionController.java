// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.pack.FilesystemCandidateReference;
import com.schmaloogium.engine.pack.FilesystemCandidateResolution;
import com.schmaloogium.engine.pack.PackCandidate;
import com.schmaloogium.engine.pack.PackCandidateId;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.engine.pack.PackCandidateStatus;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackDiscoveryResult;
import com.schmaloogium.engine.pack.PackFrontEnd;

/**
 * The pack-selection controller (PHASE_12_DOC §4.6.1): rows come straight from
 * {@code discover} in Phase 3's order; the {@code (off)} sentinel stays present,
 * enabled and selectable; every selection re-runs discovery first and carries the
 * retained filesystem reference through resolution (D-P12-8). Selections persist
 * through the durable {@code shaderPack} key and classify exactly one FULL request.
 */
public final class PackSelectionController implements PackSelectionActions {

    /** Platform effects the controller cannot perform headlessly. */
    public interface Host {

        /** Reveals the shaderpacks folder. */
        void openFolder(Path shaderpacksDirectory);

        /** Opens the options screen for the current selection, if one is viewable. */
        void showOptions();

        /** Closes the selection screen. */
        void close();
    }

    private final PackFrontEnd frontEnd;
    private final Path shaderpacksDirectory;
    private final EngineSettingsController settings;
    private final Host host;
    private final GuiText text;

    private PackDiscoveryResult discovery;
    private PackCandidateId selected;
    private Optional<String> lastActionSummary = Optional.empty();
    private boolean restored;

    public PackSelectionController(PackFrontEnd frontEnd, Path shaderpacksDirectory,
                                   EngineSettingsController settings, Host host, GuiText text) {
        this.frontEnd = Objects.requireNonNull(frontEnd, "frontEnd");
        this.shaderpacksDirectory = Objects.requireNonNull(shaderpacksDirectory, "roots");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.host = Objects.requireNonNull(host, "host");
        this.text = Objects.requireNonNull(text, "text");
        this.discovery = frontEnd.discover(new PackDiscoveryRequest(shaderpacksDirectory, null));
        this.selected = offRow();
    }

    // ------------------------------------------------------------------- view model

    public PackSelectionModel model() {
        restoreDurableSelectionOnce();
        List<PackSelectionRow> rows = new ArrayList<>(discovery.candidates().size());
        for (PackCandidate candidate : discovery.candidates()) {
            rows.add(new PackSelectionRow(candidate.id(), candidate.kind(),
                    candidate.displayName(), candidate.status(), Optional.empty(),
                    candidate.diagnostics(), candidate.status() == PackCandidateStatus.AVAILABLE));
        }
        return new PackSelectionModel(discovery.generation(), rows, selected,
                lastActionSummary);
    }

    /** Restores the persisted selection exactly once per controller lifetime. */
    private void restoreDurableSelectionOnce() {
        if (restored) {
            return;
        }
        restored = true;
        String durable = settings.shaderPack();
        if (durable.equals("off")) {
            selected = offRow();
            return;
        }
        if (durable.equals("(internal)")) {
            selectInternal();
            return;
        }
        try {
            FilesystemCandidateReference reference =
                    new FilesystemCandidateReference(durable);
            FilesystemCandidateResolution resolution =
                    frontEnd.resolveFilesystemCandidate(reference, discovery);
            if (resolution instanceof FilesystemCandidateResolution.Resolved resolved
                    && available(resolved.candidate())) {
                selected = resolved.candidate();
                return;
            }
            // Every other outcome keeps shaders off with its distinct reason.
            lastActionSummary = Optional.of(text.gui(
                    "schmaloogium.gui.restoreKeptOff", resolution.getClass().getSimpleName()));
            selected = offRow();
        } catch (RuntimeException malformed) {
            lastActionSummary = Optional.of(text.gui("schmaloogium.gui.restoreKeptOff",
                    "invalid"));
            selected = offRow();
        }
    }

    private void selectInternal() {
        discovery.candidates().stream()
                .filter(candidate -> candidate.kind() == PackCandidateKind.INTERNAL)
                .findFirst()
                .ifPresent(candidate -> selected = candidate.id());
    }

    private PackCandidateId offRow() {
        return discovery.candidates().stream()
                .filter(candidate -> candidate.kind() == PackCandidateKind.OFF)
                .findFirst()
                .map(PackCandidate::id)
                .orElseThrow(() -> new IllegalStateException("discovery lost the Off sentinel"));
    }

    private boolean available(PackCandidateId id) {
        return discovery.candidates().stream()
                .filter(candidate -> candidate.id().equals(id))
                .findFirst()
                .map(candidate -> candidate.status() == PackCandidateStatus.AVAILABLE)
                .orElse(false);
    }

    private Optional<PackCandidate> candidate(PackCandidateId id) {
        return discovery.candidates().stream()
                .filter(candidate -> candidate.id().equals(id))
                .findFirst();
    }

    private Optional<PackCandidate> selectedCandidate() {
        return candidate(selected);
    }

    // ---------------------------------------------------------------------- intents

    @Override
    public void selectCandidate(PackCandidateId candidate) {
        // Always re-run discovery first; never forward a stale id (D-P12-8).
        retainReferenceAndRefresh();
        Optional<PackCandidate> target = candidate(candidate);
        if (target.isEmpty() || target.get().status() != PackCandidateStatus.AVAILABLE) {
            lastActionSummary = Optional.of(text.gui("schmaloogium.gui.selectionUnavailable"));
            return;
        }
        selected = target.get().id();
        if (!persistDurableSelection(target.get())) {
            return; // write failure: summary already set; nothing queued
        }
        lastActionSummary = Optional.of(text.gui("schmaloogium.gui.selected",
                target.get().displayName()));
        submitPackSelection();
    }

    @Override
    public void refresh() {
        retainReferenceAndRefresh();
        lastActionSummary = Optional.of(text.gui("schmaloogium.gui.refreshed"));
    }

    /**
     * Re-runs discovery, carrying a filesystem selection's durable reference to the new
     * current id; the displayed stale id is never forwarded (D-P12-8).
     */
    private void retainReferenceAndRefresh() {
        Optional<FilesystemCandidateReference> retained = selectedCandidate()
                .flatMap(PackCandidate::filesystemReference);
        discovery = frontEnd.discover(new PackDiscoveryRequest(shaderpacksDirectory, null));
        selected = offRow();
        if (retained.isEmpty()) {
            return;
        }
        FilesystemCandidateResolution resolution =
                frontEnd.resolveFilesystemCandidate(retained.get(), discovery);
        if (resolution instanceof FilesystemCandidateResolution.Resolved resolved
                && available(resolved.candidate())) {
            selected = resolved.candidate();
        } else {
            lastActionSummary = Optional.of(text.gui("schmaloogium.gui.restoreKeptOff",
                    resolution.getClass().getSimpleName()));
        }
    }

    private boolean persistDurableSelection(PackCandidate target) {
        String durable = durableValue(target);
        if (durable == null || !settings.setShaderPack(durable)) {
            lastActionSummary = Optional.of(text.gui("schmaloogium.gui.persistFailed"));
            return false;
        }
        return true;
    }

    private String durableValue(PackCandidate target) {
        return switch (target.kind()) {
            case OFF -> "off";
            case INTERNAL -> "(internal)";
            case DIRECTORY, ARCHIVE -> target.filesystemReference()
                    .map(FilesystemCandidateReference::canonicalValue).orElse(null);
        };
    }

    private void submitPackSelection() {
        ReloadCoordinator.submitOrReportInert(
                ReloadTrigger.PACK_SELECTION_CHANGED.request(), null);
    }

    @Override
    public void openPackFolder() {
        host.openFolder(shaderpacksDirectory);
    }

    @Override
    public void openOptions() {
        boolean loadable = selectedCandidate()
                .map(candidate -> candidate.status() == PackCandidateStatus.AVAILABLE
                        && candidate.kind() != PackCandidateKind.OFF)
                .orElse(false);
        if (loadable) {
            host.showOptions();
        } else {
            lastActionSummary = Optional.of(text.gui("schmaloogium.gui.selectionUnavailable"));
        }
    }

    @Override
    public ApplyOutcome setEngineToggle(String key, boolean value) {
        return settings.setEngineToggle(key, value);
    }

    @Override
    public ApplyOutcome setEngineTriState(String key, TriStateValue value) {
        return settings.setEngineTriState(key, value);
    }

    @Override
    public ApplyOutcome setEngineChoice(String key, String rawValue) {
        return settings.setEngineChoice(key, rawValue);
    }

    @Override
    public void close() {
        host.close();
    }

    /** Exposed for the host to render status attribution without re-discovering. */
    public Map<String, List<String>> diagnosticSummaries() {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (PackCandidate candidate : discovery.candidates()) {
            candidate.diagnostics().forEach(diagnostic -> out
                    .computeIfAbsent(candidate.displayName(), key -> new ArrayList<>())
                    .add(diagnostic.messageKey()));
        }
        return out;
    }
}
