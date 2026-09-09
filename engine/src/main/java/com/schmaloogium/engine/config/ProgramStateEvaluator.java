// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.OptionKind;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.config.ProgramKey;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionState;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.preprocess.SourceCatalog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Produces the exact evaluated program states and explicit flips for one configuration
 * under a validated state and selected profile.
 */
public final class ProgramStateEvaluator {

    static final String VIRTUAL_PRE_ALL = "all";
    static final String VIRTUAL_PRE_SHADOWCOMP = "shadowcomp";

    private ProgramStateEvaluator() {
    }

    public static ProgramStateEvaluationResult evaluate(
            PackConfiguration configuration,
            OptionState state,
            Optional<ProfileName> selectedProfile,
            DiagnosticReporter diagnostics) {
        if (configuration == null || state == null || selectedProfile == null) {
            return new ProgramStateEvaluationResult.InvalidState(OptionStateFailure.NULL_INPUT);
        }
        OptionCatalog catalog = configuration.options().catalog();
        if (!catalog.validate(state).valid()) {
            return new ProgramStateEvaluationResult.InvalidState(OptionStateFailure.FOREIGN_CATALOG);
        }
        Set<String> switches = switchAvailability(configuration.options(), state);
        Set<ProgramKey> executable = configuration.sources() == null
            ? Set.of()
            : configuration.sources().executablePrograms();

        // profile-disabled programs
        Set<ProgramDisable> disabled = new java.util.HashSet<>();
        if (selectedProfile.isPresent()) {
            for (ProfileModel profile : configuration.options().profiles()) {
                if (profile.name().equals(selectedProfile.get())) {
                    disabled.addAll(profile.disabledPrograms());
                }
            }
        }

        List<EvaluatedProgramState> out = new ArrayList<>();
        for (ProgramKey key : new TreeSet<>(executable)) {
            ProgramState raw = configuration.properties().programStates().programs().get(key);
            out.add(evaluateOne(key, raw, switches, disabled, diagnostics));
        }
        Map<ProgramKey, Map<FlipBufferKey, FlipOverride>> explicitFlips = new TreeMap<>();
        for (Map.Entry<ProgramKey, ProgramState> e
                : configuration.properties().programStates().programs().entrySet()) {
            if (!e.getValue().flips().isEmpty() && !isVirtualPre(e.getKey())) {
                explicitFlips.put(e.getKey(), Map.copyOf(e.getValue().flips()));
            }
        }
        return new ProgramStateEvaluationResult.Evaluated(
            new EvaluatedProgramStates(List.copyOf(out), explicitFlips));
    }

    private static boolean isVirtualPre(ProgramKey key) {
        return (key.programName().equals(VIRTUAL_PRE_ALL)
                || key.programName().equals(VIRTUAL_PRE_SHADOWCOMP))
            && key.dimension().equals(DimensionKey.BASE);
    }

    private static EvaluatedProgramState evaluateOne(
            ProgramKey key,
            ProgramState raw,
            Set<String> switches,
            Set<ProgramDisable> disabled,
            DiagnosticReporter diagnostics) {
        Optional<AlphaTestSpec> alphaTest = raw == null ? Optional.empty() : raw.alphaTest();
        Optional<BlendSpec> blend = raw == null ? Optional.empty() : raw.blend();
        Optional<ViewportScale> scale = raw == null ? Optional.empty() : raw.scale();
        boolean propertyEnabled = true;
        if (raw != null && raw.enabledExpression().isPresent()) {
            if (raw.enabledExpression().get() instanceof ProgramEnabledExpressionValue expr) {
                for (String name : expr.referencedSwitches()) {
                    if (!switches.contains(name) && diagnostics != null) {
                        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                            UserChannel.LOG_ONLY,
                            "schmaloogium.warn.program.enabled_unknown_switch",
                            List.of(key.programName(), name), "", "schmaloogium.config"));
                    }
                }
                propertyEnabled = expr.evaluate(switches::contains);
            }
        }
        boolean profileDisabled = false;
        for (ProgramDisable d : disabled) {
            if (d.programName().equals(key.programName())
                    && (d.dimension().isEmpty() || d.dimension().get().equals(key.dimension()))) {
                profileDisabled = true;
                break;
            }
        }
        boolean finalEnabled = propertyEnabled && !profileDisabled;
        return new EvaluatedProgramState(key, alphaTest, blend, scale,
            propertyEnabled, profileDisabled, finalEnabled);
    }

    /** A switch name is available iff it names a SWITCH definition of the catalog. */
    private static Set<String> switchAvailability(OptionConfiguration options, OptionState state) {
        Set<String> available = new TreeSet<>();
        for (OptionDefinition d : options.catalog().definitions()) {
            if (d.kind() == OptionKind.SWITCH) {
                available.add(d.name());
            }
        }
        return available;
    }
}
