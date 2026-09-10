// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.config.BooleanOptionValue;
import com.schmaloogium.engine.config.InternalOptionCaptureResult;
import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.OptionDefinition;
import com.schmaloogium.engine.config.OptionKind;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.config.OptionStateResult;
import com.schmaloogium.engine.config.OptionValue;
import com.schmaloogium.engine.config.ProfileInferenceResult;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

/**
 * The client-thread edit session (PHASE_12_DOC §4.4/§4.5/§4.7.2): a pending overlay and
 * an independent explicit-selection intent over a committed baseline, with the §4.7.2
 * write timing — per-pack changed-only persistence on apply/dirty-done/reset, never on
 * hover, navigation or discard. Internal packs accept through the injected committer;
 * without one, editing is disabled with a prepared reason (D-P12-38).
 */
public final class OptionEditSessionImpl implements OptionEditSession {

    /** Writes the complete desired state; the codec derives the changed-only set. */
    public interface PackOptionsIo {

        /** {@code true} when the write committed to the safe target. */
        boolean writeCommitted(OptionState state);
    }

    /** The Phase 7 Internal acceptance route (session-only receipt; no file). */
    @FunctionalInterface
    public interface InternalCommitter {

        /** {@code true} when the session snapshot was accepted for the active Internal. */
        boolean commit(OptionState preview);
    }

    private final OptionConfiguration configuration;
    private final GuiText text;
    private final Optional<PackOptionsIo> filesystem;
    private final Optional<InternalCommitter> committer;
    private final DiagnosticReporter diagnostics;

    private OptionState committedState;
    private OptionState pendingState;
    private Optional<ProfileName> committedSelection;
    private Optional<ProfileName> pendingSelection;

    private OptionEditSessionImpl(OptionConfiguration configuration, GuiText text,
                                  Optional<PackOptionsIo> filesystem,
                                  Optional<InternalCommitter> committer,
                                  Optional<ProfileName> committedSelection,
                                  DiagnosticReporter diagnostics) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.text = Objects.requireNonNull(text, "text");
        this.filesystem = Objects.requireNonNull(filesystem);
        this.committer = Objects.requireNonNull(committer);
        this.diagnostics = diagnostics;
        this.committedState = configuration.state();
        this.pendingState = configuration.state();
        this.committedSelection = Objects.requireNonNull(committedSelection);
        this.pendingSelection = Optional.empty();
    }

    /** Opens a filesystem-backed session over the loaded configuration. */
    public static OptionEditSessionImpl filesystem(OptionConfiguration configuration,
                                                   GuiText text, PackOptionsIo io,
                                                   Optional<ProfileName> committedSelection,
                                                   DiagnosticReporter diagnostics) {
        return new OptionEditSessionImpl(configuration, text, Optional.of(io),
                Optional.empty(), committedSelection, diagnostics);
    }

    /** Opens an Internal session; an empty committer disables all mutation (v0.4). */
    public static OptionEditSessionImpl internal(OptionConfiguration configuration,
                                                 GuiText text,
                                                 Optional<InternalCommitter> committer,
                                                 Optional<ProfileName> committedSelection,
                                                 DiagnosticReporter diagnostics) {
        return new OptionEditSessionImpl(configuration, text, Optional.empty(),
                committer, committedSelection, diagnostics);
    }

    // ------------------------------------------------------------------ presentation

    @Override
    public OptionPresentationModel present(ScreenId screen) {
        // One model carries every resolved screen; `screen` is the view's navigation
        // position and needs no separate derivation here.
        return OptionsModelFactory.build(configuration, pendingState, pendingSummary(),
                text, availability(), diagnostics);
    }


    private OptionSessionAvailability availability() {
        boolean route = acceptanceRoute();
        OptionActionAvailability mutation = route
                ? OptionActionAvailability.ENABLED
                : OptionActionAvailability.disabled(mutationDisabledReason());
        boolean dirty = isDirty();
        // Doc §4.4.4: apply = mutation AND dirty; done enabled when clean, else mutation.
        OptionActionAvailability apply = route && dirty
                ? OptionActionAvailability.ENABLED
                : OptionActionAvailability.disabled(route
                        ? text.gui("schmaloogium.gui.noPendingChanges")
                        : mutationDisabledReason());
        OptionActionAvailability done = !dirty || route
                ? OptionActionAvailability.ENABLED
                : OptionActionAvailability.disabled(mutationDisabledReason());
        return new OptionSessionAvailability(mutation, mutation, apply, done);
    }

    private String mutationDisabledReason() {
        return filesystem.isEmpty()
                ? text.gui("schmaloogium.gui.internalUnavailable")
                : text.gui("schmaloogium.gui.persistenceUnavailable");
    }

    private Optional<PendingProfileSummary> pendingSummary() {
        if (pendingSelection.equals(committedSelection)) {
            return Optional.empty();
        }
        String display = pendingSelection
                .<String>map(name -> text.gui("schmaloogium.gui.pendingProfile",
                        text.profileLabel(name)))
                .orElseGet(() -> text.gui("schmaloogium.gui.pendingProfileCleared"));
        return Optional.of(new PendingProfileSummary(pendingSelection, display));
    }

    // --------------------------------------------------------------------- mutations

    @Override
    public void toggle(OptionId id) {
        if (!acceptanceRoute()) {
            return;
        }
        OptionDefinition definition = configuration.catalog().find(id.name()).orElse(null);
        if (definition == null || definition.kind() != OptionKind.SWITCH) {
            return;
        }
        boolean current = boolValue(pendingState, id.name());
        update(id.name(), new BooleanOptionValue(!current));
    }

    @Override
    public void cycle(OptionId id, int step) {
        List<String> allowed = allowedTexts(id.name());
        if (allowed.isEmpty()) {
            return;
        }
        String raw = textValue(pendingState, id.name());
        int index = allowed.indexOf(raw);
        // Out-of-list value: displayed and retained until the first cycle → index 0.
        int next = index < 0 ? 0 : Math.floorMod(index + step, allowed.size());
        setValueIndex(id, next);
    }

    @Override
    public void setValueIndex(OptionId id, int index) {
        if (!acceptanceRoute()) {
            return;
        }
        List<String> allowed = allowedTexts(id.name());
        if (index < 0 || index >= allowed.size()) {
            return;
        }
        update(id.name(), new TextOptionValue(allowed.get(index)));
    }

    @Override
    public void cycleProfile() {
        List<ProfileName> declared = declaredProfiles();
        if (!acceptanceRoute() || declared.isEmpty()) {
            return;
        }
        int cursor = -1; // Custom or no display selection → first declared profile
        Optional<ProfileName> display = pendingSelection.or(() -> displaySelection());
        if (display.isPresent()) {
            for (int i = 0; i < declared.size(); i++) {
                if (declared.get(i).equals(display.get())) {
                    cursor = i;
                    break;
                }
            }
        }
        ProfileName target = declared.get(Math.floorMod(cursor + 1, declared.size()));
        applyProfileBatch(target);
    }

    /** Writes the whole expanded option constraint set; invalid batch rejects all. */
    private void applyProfileBatch(ProfileName target) {
        Map<String, OptionValue> batch = new LinkedHashMap<>(pendingState.values());
        configuration.profiles().stream()
                .filter(profile -> profile.name().equals(target))
                .findFirst()
                .ifPresent(profile -> profile.constraints()
                        .forEach(constraint -> batch.put(constraint.optionName(),
                                constraint.requiredValue())));
        OptionCatalog catalog = configuration.catalog();
        OptionStateResult result = catalog.constructState(batch, diagnostics);
        if (result instanceof OptionStateResult.Valid valid) {
            pendingState = valid.state();
            pendingSelection = Optional.of(target);
        }
        // Invalid batch: neither preview nor selection changes.
    }

    private void update(String name, OptionValue value) {
        OptionStateResult result = configuration.catalog().updateState(
                pendingState, name, value, diagnostics);
        if (result instanceof OptionStateResult.Valid valid) {
            pendingState = valid.state();
        }
        // Rejected edit (ambiguity, unsafe text): preview unchanged.
    }

    // --------------------------------------------------------------------- inspection

    @Override
    public boolean isDirty() {
        return !pendingState.values().equals(committedState.values())
                || !pendingSelection.equals(committedSelection);
    }

    @Override
    public int pendingChangeCount() {
        int differing = 0;
        for (Map.Entry<String, OptionValue> entry : pendingState.values().entrySet()) {
            if (!entry.getValue().equals(committedState.values().get(entry.getKey()))) {
                differing++;
            }
        }
        return differing + (!pendingSelection.equals(committedSelection) ? 1 : 0);
    }

    // ----------------------------------------------------------------------- commits

    @Override
    public ApplyOutcome apply() {
        if (!isDirty()) {
            return ApplyOutcome.unchanged();
        }
        return commit(pendingState, applyOrProfileCause(), pendingSelection);
    }

    @Override
    public ApplyOutcome resetToPackDefaults() {
        if (!acceptanceRoute()) {
            return rejected();
        }
        // Reset always takes the commit route, even at defaults (writes empty set).
        return commit(configuration.catalog().defaultState(), ReloadCause.OPTION_RESET,
                Optional.empty());
    }

    private ApplyOutcome commit(OptionState desired, ReloadCause cause,
                                Optional<ProfileName> selection) {
        if (filesystem.isPresent()) {
            boolean committed = filesystem.get().writeCommitted(desired);
            if (!committed) {
                // Failure: pending preview and baselines retained, nothing queued.
                return new ApplyOutcome(OptionApplyStatus.FAILED, Optional.empty(),
                        List.of());
            }
        } else if (committer.isPresent()) {
            if (!committer.get().commit(desired)) {
                return new ApplyOutcome(OptionApplyStatus.FAILED, Optional.empty(),
                        List.of());
            }
        } else {
            return rejected();
        }
        committedState = desired;
        committedSelection = selection;
        pendingState = desired;
        pendingSelection = selection;
        // Only UNCHANGED-less acceptance queues exactly one reload; the bake predicate
        // is Phase 7's (D-P12-11), so the request carries the flag false.
        ReloadRequest request = new ReloadRequest(ReloadLifecycle.REPUBLISH, false, false,
                cause);
        ReloadCoordinator.submitOrReportInert(request, diagnostics);
        return new ApplyOutcome(
                filesystem.isPresent() ? OptionApplyStatus.PERSISTED
                        : OptionApplyStatus.SESSION_ACCEPTED,
                Optional.of(request), List.of());
    }

    @Override
    public void discard() {
        pendingState = committedState;
        pendingSelection = committedSelection;
    }

    /** The prepared pending-profile summary text; empty when selection and baseline agree. */
    public String pendingProfileSummaryText() {
        return pendingSummary().map(PendingProfileSummary::displayText).orElse("");
    }

    // ----------------------------------------------------------------------- helpers

    private boolean acceptanceRoute() {
        return filesystem.isPresent() || committer.isPresent();
    }

    private ReloadCause applyOrProfileCause() {
        boolean optionDelta = !pendingState.values().equals(committedState.values());
        return optionDelta ? ReloadCause.OPTION_APPLY : ReloadCause.PROFILE_APPLY;
    }

    private ApplyOutcome rejected() {
        if (diagnostics != null) {
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                    UserChannel.LOG_ONLY, "schmaloogium.warn.gui.commitRejected", List.of(),
                    "", "schmaloogium.config"));
        }
        return new ApplyOutcome(OptionApplyStatus.REJECTED, Optional.empty(), List.of());
    }

    private List<ProfileName> declaredProfiles() {
        return configuration.profiles().stream().map(profile -> profile.name()).toList();
    }

    /** Inferred display selection over the preview state (never the pending intent). */
    private Optional<ProfileName> displaySelection() {
        List<ProfileName> declared = declaredProfiles();
        return switch (configuration.inferProfile(pendingState)) {
            case ProfileInferenceResult.Inferred inferred ->
                    inferred.inference().selected().filter(declared::contains);
            case ProfileInferenceResult.InvalidState ignored -> Optional.empty();
        };
    }

    private List<String> allowedTexts(String name) {
        return configuration.catalog().find(name)
                .map(OptionsText::textValues)
                .orElse(List.of());
    }

    private static boolean boolValue(OptionState state, String name) {
        return state.value(name)
                .filter(v -> v instanceof BooleanOptionValue)
                .map(v -> ((BooleanOptionValue) v).value())
                .orElse(false);
    }

    private static String textValue(OptionState state, String name) {
        return state.value(name).map(OptionsText::textOf).orElse("");
    }

    /** Text projections shared with the model factory. */
    private static final class OptionsText {
        static List<String> textValues(OptionDefinition definition) {
            List<String> out = new ArrayList<>(definition.allowedValues().size());
            for (OptionValue value : definition.allowedValues()) {
                out.add(textOf(value));
            }
            return out;
        }

        static String textOf(OptionValue value) {
            if (value instanceof BooleanOptionValue b) {
                return b.value() ? "true" : "false";
            }
            return ((TextOptionValue) value).value();
        }
    }
}
