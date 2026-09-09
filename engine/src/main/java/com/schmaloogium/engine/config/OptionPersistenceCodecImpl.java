// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.pack.ImmutableBytes;
import com.schmaloogium.engine.pack.OptionPersistenceCodec;
import com.schmaloogium.engine.pack.OptionPersistenceReadRequest;
import com.schmaloogium.engine.pack.OptionPersistenceReadResult;
import com.schmaloogium.engine.pack.OptionPersistenceWriteRequest;
import com.schmaloogium.engine.pack.OptionPersistenceWriteResult;
import com.schmaloogium.engine.pack.PackOptionsTarget;
import com.schmaloogium.engine.pack.PersistenceFailure;
import com.schmaloogium.engine.pack.PersistenceFailureCode;
import com.schmaloogium.engine.pack.PersistenceReadSource;
import com.schmaloogium.engine.pack.PersistenceReadStatus;
import com.schmaloogium.engine.pack.PersistenceWriteStatus;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Closed per-pack option persistence codec. Validates every request field, target
 * authentication, catalog identity and baseline state before any I/O, then applies
 * each persisted entry through updateState.
 */
public final class OptionPersistenceCodecImpl implements OptionPersistenceCodec {

    private final Object domain;

    public OptionPersistenceCodecImpl(Object domain) {
        this.domain = domain;
    }

    @Override
    public OptionPersistenceReadResult read(OptionPersistenceReadRequest request) {
        PersistenceFailure invalid = validate(request == null ? null : request.files(),
            request == null ? null : request.target(),
            request == null ? null : request.catalog(),
            request == null ? null : request.baseline(),
            request == null ? null : request.diagnostics());
        if (invalid != null) {
            return new OptionPersistenceReadResult.InvalidRequest(invalid, List.of());
        }
        OptionCatalog catalog = request.catalog();
        DiagnosticReporter reporter = request.diagnostics();
        PersistenceReadSource source = request.files().read(request.target());
        if (source instanceof PersistenceReadSource.Failed f) {
            return new OptionPersistenceReadResult.Completed(request.baseline(),
                PersistenceReadStatus.FAILED, Optional.of(f.failure()), List.of());
        }
        if (source instanceof PersistenceReadSource.Absent) {
            return new OptionPersistenceReadResult.Completed(request.baseline(),
                PersistenceReadStatus.ABSENT, Optional.empty(), List.of());
        }
        byte[] bytes = ((PersistenceReadSource.Present) source).bytes().copy();
        List<EngineDiagnostic> diags = new ArrayList<>();
        OptionState current = request.baseline();
        for (LogicalProperties.Entry line : LogicalProperties.decode(bytes,
                StandardCharsets.ISO_8859_1)) {
            String name = line.key().trim();
            if (name.isEmpty()) {
                continue;
            }
            OptionDefinition definition = catalog.find(name).orElse(null);
            if (definition == null) {
                diags.add(warn("schmaloogium.warn.persistence.unknown_option", name));
                continue;
            }
            if (definition.availability() == OptionAvailability.DISABLED_AMBIGUOUS) {
                diags.add(warn("schmaloogium.warn.persistence.ambiguous_option", name));
                continue;
            }
            OptionValue parsed = parseValue(definition, line.value());
            if (parsed == null) {
                diags.add(warn("schmaloogium.warn.persistence.invalid_value", name));
                continue;
            }
            OptionStateResult result = catalog.updateState(current, name, parsed, reporter);
            if (result instanceof OptionStateResult.Valid valid) {
                current = valid.state();
            } else {
                diags.add(warn("schmaloogium.warn.persistence.rejected_option", name));
            }
        }
        return new OptionPersistenceReadResult.Completed(current,
            PersistenceReadStatus.APPLIED, Optional.empty(), List.copyOf(diags));
    }

    @Override
    public OptionPersistenceWriteResult write(OptionPersistenceWriteRequest request) {
        PersistenceFailure invalid = validate(request == null ? null : request.files(),
            request == null ? null : request.target(),
            request == null ? null : request.catalog(),
            request == null ? null : request.state(),
            request == null ? null : request.diagnostics());
        if (invalid != null) {
            return new OptionPersistenceWriteResult(PersistenceWriteStatus.FAILED,
                Optional.of(invalid), List.of());
        }
        OptionCatalog catalog = request.catalog();
        if (!(catalog instanceof OptionCatalogValue value)) {
            return failedRequest("catalog is not an issued catalog");
        }
        // Changed-only writer: compare against defaultState() in catalog order.
        OptionState defaults = catalog.defaultState();
        StringBuilder sb = new StringBuilder();
        for (OptionDefinition definition : value.definitionsList()) {
            OptionValue current = request.state().value(definition.name()).orElseThrow();
            OptionValue baseline = defaults.value(definition.name()).orElseThrow();
            if (!current.equals(baseline)) {
                sb.append(PropertiesEscaping.escapeKey(definition.name()))
                    .append('=')
                    .append(PropertiesEscaping.escapeValue(textOf(current)))
                    .append('\n');
            }
        }
        ImmutableBytes payload = ImmutableBytes.of(
            sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        var receipt = request.files().writeAtomically(request.target(), payload);
        if (receipt.status() == PersistenceWriteStatus.COMMITTED) {
            return new OptionPersistenceWriteResult(PersistenceWriteStatus.COMMITTED,
                Optional.empty(), List.of());
        }
        return new OptionPersistenceWriteResult(PersistenceWriteStatus.FAILED,
            receipt.failure(), List.of());
    }

    private static String textOf(OptionValue value) {
        if (value instanceof BooleanOptionValue b) {
            return b.value() ? "true" : "false";
        }
        return ((TextOptionValue) value).value();
    }

    private static OptionValue parseValue(OptionDefinition definition, String raw) {
        if (definition.kind() == OptionKind.SWITCH) {
            if (raw.equals("true")) {
                return new BooleanOptionValue(true);
            }
            if (raw.equals("false")) {
                return new BooleanOptionValue(false);
            }
            return null;
        }
        return OptionCatalogValue.isSafeText(raw) ? new TextOptionValue(raw) : null;
    }

    /** Full pre-I/O request validation: fields, domain, credential, catalog, state. */
    private PersistenceFailure validate(Object files, Object target, Object catalog,
            Object state, Object reporter) {
        if (files == null || target == null || catalog == null || state == null) {
            return new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST,
                "null request field");
        }
        if (!(target instanceof com.schmaloogium.engine.pack.PackOptionsTarget issued)
                || !issued.isIssuedBy(domain)) {
            return new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST,
                "target is not issued by this bundle domain");
        }
        if (!(catalog instanceof OptionCatalogValue cv) || !cv.matchesDomain(domain)) {
            return new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST,
                "catalog pack key does not match the target credential");
        }
        if (!(state instanceof OptionStateValue sv) || sv.catalog() != cv) {
            return new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST,
                "state not issued by that catalog");
        }
        return null;
    }

    private static OptionPersistenceWriteResult failedRequest(String detail) {
        return new OptionPersistenceWriteResult(PersistenceWriteStatus.FAILED,
            Optional.of(new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST, detail)),
            List.of());
    }

    private static EngineDiagnostic warn(String key, String name) {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY, key,
            List.of(name), "", "schmaloogium.config");
    }
}
