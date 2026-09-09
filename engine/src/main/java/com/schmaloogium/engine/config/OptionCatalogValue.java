// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionState;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

final class OptionCatalogValue implements OptionCatalog {

    private final List<OptionDefinition> definitions;
    private final java.util.Map<String, OptionDefinition> byName;
    private final Object packKey;          // (bundle domain, candidate/identity credential)
    private final boolean internalOrigin;
    private final OptionStateValue defaultState;

    OptionCatalogValue(List<OptionDefinition> definitions, Object packKey, boolean internalOrigin) {
        this.definitions = List.copyOf(definitions);
        java.util.Map<String, OptionDefinition> byName = new java.util.LinkedHashMap<>();
        for (OptionDefinition d : this.definitions) {
            byName.put(d.name(), d);
        }
        this.byName = java.util.Map.copyOf(byName);
        this.packKey = java.util.Objects.requireNonNull(packKey, "packKey");
        this.internalOrigin = internalOrigin;
        java.util.Map<String, OptionValue> defaults = new java.util.LinkedHashMap<>();
        for (OptionDefinition d : this.definitions) {
            defaults.put(d.name(), d.defaultValue());
        }
        this.defaultState = new OptionStateValue(this, defaults);
    }

    List<OptionDefinition> definitionsList() {
        return definitions;
    }

    boolean isInternalOrigin() {
        return internalOrigin;
    }

    boolean owns(OptionState state) {
        return state instanceof OptionStateValue v && v.catalog == this;
    }

    OptionDefinition definition(String name) {
        return byName.get(name);
    }

    /** The hidden pack key shared with the issuing bundle. */
    Object packKey() {
        return packKey;
    }

    /** Hidden domain check for the persistence codecs. */
    public boolean matchesDomain(Object domain) {
        return packKey == domain;
    }

    @Override
    public List<OptionDefinition> definitions() {
        return definitions;
    }

    @Override
    public Optional<OptionDefinition> find(String name) {
        java.util.Objects.requireNonNull(name, "name");
        return Optional.ofNullable(byName.get(name));
    }

    @Override
    public OptionState defaultState() {
        return defaultState;
    }

    @Override
    public OptionStateResult constructState(
            Map<String, OptionValue> values, DiagnosticReporter diagnostics) {
        OptionStateFailure f = null;
        if (values == null || diagnostics == null) {
            f = OptionStateFailure.NULL_INPUT;
        } else {
            // first unknown in unsigned-UTF-8 order
            java.util.List<String> unknown = values.keySet().stream()
                .filter(n -> !byName.containsKey(n))
                .sorted(EngineOptionData::compareUnsignedUtf8)
                .toList();
            if (!unknown.isEmpty()) {
                f = OptionStateFailure.UNKNOWN_OPTION;
            }
        }
        if (f == null) {
            for (OptionDefinition d : definitions) {
                if (!values.containsKey(d.name())) {
                    f = OptionStateFailure.MISSING_OPTION;
                    break;
                }
            }
        }
        if (f == null) {
            for (OptionDefinition d : definitions) {
                if (values.get(d.name()) == null) {
                    f = OptionStateFailure.NULL_VALUE;
                    break;
                }
            }
        }
        if (f == null) {
            for (OptionDefinition d : definitions) {
                if (!kindMatches(d, values.get(d.name()))) {
                    f = OptionStateFailure.KIND_MISMATCH;
                    break;
                }
            }
        }
        if (f == null) {
            for (OptionDefinition d : definitions) {
                if (d.availability() == OptionAvailability.DISABLED_AMBIGUOUS
                        && !values.get(d.name()).equals(d.defaultValue())) {
                    f = OptionStateFailure.DISABLED_AMBIGUOUS;
                    break;
                }
            }
        }
        if (f == null) {
            for (OptionDefinition d : definitions) {
                if (values.get(d.name()) instanceof TextOptionValue t && !isSafeText(t.value())) {
                    f = OptionStateFailure.UNSAFE_TEXT;
                    break;
                }
            }
        }
        if (f != null) {
            return new OptionStateResult.Invalid(f);
        }
        // out-of-list values remain valid with one warning each (D-P3-14)
        for (OptionDefinition d : definitions) {
            OptionValue v = values.get(d.name());
            if (!d.allowedValues().isEmpty() && !d.allowedValues().contains(v) && diagnostics != null) {
                diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                    UserChannel.LOG_ONLY, "schmaloogium.warn.option.value_outside_list",
                    List.of(d.name()), "", "schmaloogium.config"));
            }
        }
        return new OptionStateResult.Valid(new OptionStateValue(this, values));
    }

    @Override
    public OptionStateResult updateState(
            OptionState baseline, String name, OptionValue value, DiagnosticReporter diagnostics) {
        if (baseline == null || name == null || value == null || diagnostics == null) {
            return new OptionStateResult.Invalid(OptionStateFailure.NULL_INPUT);
        }
        if (!(baseline instanceof OptionStateValue v) || v.catalog != this) {
            return new OptionStateResult.Invalid(OptionStateFailure.FOREIGN_CATALOG);
        }
        OptionDefinition d = byName.get(name);
        if (d == null) {
            return new OptionStateResult.Invalid(OptionStateFailure.UNKNOWN_OPTION);
        }
        if (!kindMatches(d, value)) {
            return new OptionStateResult.Invalid(OptionStateFailure.KIND_MISMATCH);
        }
        if (d.availability() == OptionAvailability.DISABLED_AMBIGUOUS) {
            return new OptionStateResult.Invalid(OptionStateFailure.DISABLED_AMBIGUOUS);
        }
        if (value instanceof TextOptionValue t && !isSafeText(t.value())) {
            return new OptionStateResult.Invalid(OptionStateFailure.UNSAFE_TEXT);
        }
        if (!d.allowedValues().isEmpty() && !d.allowedValues().contains(value)) {
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                UserChannel.LOG_ONLY, "schmaloogium.warn.option.value_outside_list",
                List.of(name), "", "schmaloogium.config"));
        }
        java.util.Map<String, OptionValue> updated = new java.util.LinkedHashMap<>(v.values());
        updated.put(name, value);
        return new OptionStateResult.Valid(new OptionStateValue(this, updated));
    }

    @Override
    public OptionStateValidation validate(OptionState candidate) {
        if (candidate == null) {
            return new OptionStateValidation(Optional.of(OptionStateFailure.NULL_INPUT));
        }
        if (!(candidate instanceof OptionStateValue v) || v.catalog != this) {
            return new OptionStateValidation(Optional.of(OptionStateFailure.FOREIGN_CATALOG));
        }
        for (OptionDefinition d : definitions) {
            OptionValue value = v.values.get(d.name());
            if (value == null) {
                return new OptionStateValidation(Optional.of(OptionStateFailure.MISSING_OPTION));
            }
            if (!kindMatches(d, value)) {
                return new OptionStateValidation(Optional.of(OptionStateFailure.KIND_MISMATCH));
            }
            if (d.availability() == OptionAvailability.DISABLED_AMBIGUOUS
                    && !value.equals(d.defaultValue())) {
                return new OptionStateValidation(Optional.of(OptionStateFailure.DISABLED_AMBIGUOUS));
            }
            if (value instanceof TextOptionValue t && !isSafeText(t.value())) {
                return new OptionStateValidation(Optional.of(OptionStateFailure.UNSAFE_TEXT));
            }
        }
        for (String name : v.values.keySet()) {
            if (!byName.containsKey(name)) {
                return new OptionStateValidation(Optional.of(OptionStateFailure.UNKNOWN_OPTION));
            }
        }
        return new OptionStateValidation(Optional.empty());
    }

    @Override
    public InternalOptionCaptureResult captureInternalOptions(
            OptionState state, DiagnosticReporter diagnostics) {
        if (state == null || diagnostics == null) {
            return new InternalOptionCaptureResult.Invalid(InternalOptionFailure.NULL_INPUT);
        }
        if (!(state instanceof OptionStateValue v) || v.catalog != this) {
            return new InternalOptionCaptureResult.Invalid(InternalOptionFailure.FOREIGN_CATALOG);
        }
        if (!internalOrigin) {
            return new InternalOptionCaptureResult.Invalid(InternalOptionFailure.NOT_INTERNAL);
        }
        if (!validate(state).valid()) {
            return new InternalOptionCaptureResult.Invalid(InternalOptionFailure.INVALID_STATE);
        }
        return new InternalOptionCaptureResult.Captured(new InternalOptionSnapshotValue(
            packKey, java.util.Map.copyOf(v.values())));
    }

    private static boolean kindMatches(OptionDefinition d, OptionValue v) {
        if (v == null) {
            return false;
        }
        return switch (d.kind()) {
            case SWITCH -> v instanceof BooleanOptionValue;
            case VARIABLE, CONSTANT -> v instanceof TextOptionValue;
        };
    }

    static boolean isSafeText(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\n' || c == '\r' || c == '\u0000') {
                return false;
            }
        }
        // no comment or preprocessing-directive token
        String trimmed = value.stripLeading();
        if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("#")) {
            return false;
        }
        return !trimmed.startsWith("#");
    }
}
