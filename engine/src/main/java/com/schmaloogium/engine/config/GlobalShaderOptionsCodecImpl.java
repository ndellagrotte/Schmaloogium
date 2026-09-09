// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.pack.GlobalOptionsTarget;
import com.schmaloogium.engine.pack.GlobalShaderOptionsCodec;
import com.schmaloogium.engine.pack.GlobalShaderOptionsReadRequest;
import com.schmaloogium.engine.pack.GlobalShaderOptionsReadResult;
import com.schmaloogium.engine.pack.GlobalShaderOptionsWriteRequest;
import com.schmaloogium.engine.pack.GlobalShaderOptionsWriteResult;
import com.schmaloogium.engine.pack.ImmutableBytes;
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
import java.util.TreeMap;

/**
 * The closed global optionsshaders.txt codec: overlay read and full-canonical write,
 * separate from pack options and never mixed with them.
 */
public final class GlobalShaderOptionsCodecImpl implements GlobalShaderOptionsCodec {

    @Override
    public GlobalShaderOptionsReadResult read(GlobalShaderOptionsReadRequest request) {
        if (request == null || request.files() == null || request.target() == null
                || request.baseline() == null) {
            return failedRead(baselineOrEmpty(request),
                new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST, "null request field"));
        }
        PersistenceReadSource source = request.files().read(request.target());
        if (source instanceof PersistenceReadSource.Failed f) {
            return failedRead(request.baseline(), f.failure());
        }
        if (source instanceof PersistenceReadSource.Absent) {
            return new GlobalShaderOptionsReadResult(request.baseline(),
                PersistenceReadStatus.ABSENT, Optional.empty(), List.of());
        }
        byte[] bytes = ((PersistenceReadSource.Present) source).bytes().copy();
        Map<String, String> overlay = new TreeMap<>(EngineOptionData::compareUnsignedUtf8);
        request.baseline().values().forEach(overlay::put);
        List<EngineDiagnostic> diags = new ArrayList<>();
        for (LogicalProperties.Entry line : LogicalProperties.decode(bytes,
                StandardCharsets.ISO_8859_1)) {
            String key = line.key();
            String value = line.value();
            if (!EngineOptionData.isKnownKey(key)) {
                // unknown-safe occurrence: warn and overlay
                overlay.put(key, value);
                continue;
            }
            if (!EngineOptionData.isKnownValueValid(key, value)) {
                continue; // typed-invalid known occurrence omitted
            }
            overlay.put(key, value);
        }
        return new GlobalShaderOptionsReadResult(new EngineOptionData(overlay),
            PersistenceReadStatus.APPLIED, Optional.empty(), List.copyOf(diags));
    }

    @Override
    public GlobalShaderOptionsWriteResult write(GlobalShaderOptionsWriteRequest request) {
        if (request == null || request.files() == null || request.target() == null
                || request.values() == null) {
            return new GlobalShaderOptionsWriteResult(PersistenceWriteStatus.FAILED,
                Optional.of(new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST,
                    "null request field")), List.of());
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : request.values().values().entrySet()) {
            sb.append(PropertiesEscaping.escapeKey(e.getKey()))
                .append('=')
                .append(PropertiesEscaping.escapeValue(e.getValue()))
                .append('\n');
        }
        ImmutableBytes payload = ImmutableBytes.of(
            sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        var receipt = request.files().writeAtomically(request.target(), payload);
        if (receipt.status() == PersistenceWriteStatus.COMMITTED) {
            return new GlobalShaderOptionsWriteResult(PersistenceWriteStatus.COMMITTED,
                Optional.empty(), List.of());
        }
        return new GlobalShaderOptionsWriteResult(PersistenceWriteStatus.FAILED,
            receipt.failure(), List.of());
    }

    private static EngineOptionData baselineOrEmpty(GlobalShaderOptionsReadRequest request) {
        if (request == null || request.baseline() == null) {
            return EngineOptionData.empty();
        }
        return request.baseline();
    }

    private static GlobalShaderOptionsReadResult failedRead(EngineOptionData baseline,
            PersistenceFailure failure) {
        return new GlobalShaderOptionsReadResult(baseline,
            PersistenceReadStatus.FAILED, Optional.of(failure), List.of());
    }
}
