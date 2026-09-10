// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.pack.OptionPersistenceCodec;
import com.schmaloogium.engine.pack.GlobalOptionsTarget;
import com.schmaloogium.engine.pack.GlobalShaderOptionsCodec;
import com.schmaloogium.engine.pack.GlobalShaderOptionsReadRequest;
import com.schmaloogium.engine.pack.GlobalShaderOptionsWriteRequest;
import com.schmaloogium.engine.pack.OptionPersistenceWriteRequest;
import com.schmaloogium.engine.pack.PackFrontEndServices;
import com.schmaloogium.engine.pack.PackOptionsTarget;
import com.schmaloogium.engine.pack.PersistenceFileAccess;
import com.schmaloogium.engine.pack.PersistenceFileAccessAcquisition;
import com.schmaloogium.engine.pack.PersistenceRootConfiguration;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.mod.gui.model.EngineSettingsController;
import com.schmaloogium.mod.gui.model.OptionEditSessionImpl;

/**
 * The real-bundle persistence ports (PHASE_12_DOC §4.7.2): the mod-side glue between
 * the session/settings controllers and Phase 3's codecs over the same acquisition.
 * The codecs own formats, escaping and atomic writes; this class only decides
 * <em>when</em> they are invoked and reports failures.
 */
public final class BundleIo {

    private BundleIo() {
    }

    /** The acquired persistence context over one bundle and root pair. */
    public static Optional<Access> acquire(PackFrontEndServices services,
                                           PersistenceRootConfiguration roots,
                                           DiagnosticReporter diagnostics) {
        PersistenceFileAccessAcquisition acquisition = services.persistenceFiles(roots);
        if (acquisition instanceof PersistenceFileAccessAcquisition.InvalidRoots invalid) {
            if (diagnostics != null) {
                diagnostics.report(new com.schmaloogium.engine.diag.EngineDiagnostic(
                        com.schmaloogium.engine.diag.DiagnosticSeverity.WARN,
                        com.schmaloogium.engine.diag.UserChannel.LOG_ONLY,
                        "schmaloogium.warn.gui.rootsInvalid", java.util.List.of(),
                        invalid.failure().code() + ": " + invalid.failure().detail(),
                        "schmaloogium.config"));
            }
            return Optional.empty();
        }
        return Optional.of(new Access(services,
                ((PersistenceFileAccessAcquisition.Acquired) acquisition).files(), roots));
    }

    /** One authenticated acquisition; every port validates through the bundle. */
    public static final class Access {

        private final PackFrontEndServices services;
        private final PersistenceFileAccess files;
        private final PersistenceRootConfiguration roots;

        Access(PackFrontEndServices services, PersistenceFileAccess files,
               PersistenceRootConfiguration roots) {
            this.services = Objects.requireNonNull(services);
            this.files = Objects.requireNonNull(files);
            this.roots = Objects.requireNonNull(roots);
        }

        public PersistenceFileAccess files() {
            return files;
        }

        /** The global-file port for {@link EngineSettingsController}. */
        public EngineSettingsController.GlobalsIo globalIo(EngineOptionData baseline,
                                                           DiagnosticReporter diagnostics) {
            GlobalShaderOptionsCodec codec = services.globalOptions();
            GlobalOptionsTarget target = new GlobalOptionsTarget();
            return new EngineSettingsController.GlobalsIo() {
                @Override
                public EngineOptionData read() {
                    return codec.read(new GlobalShaderOptionsReadRequest(
                            files, target, baseline, diagnostics)).values();
                }

                @Override
                public boolean writeCommitted(EngineOptionData values) {
                    return codec.write(new GlobalShaderOptionsWriteRequest(
                            files, target, values, diagnostics))
                            .status() == com.schmaloogium.engine.pack.PersistenceWriteStatus.COMMITTED;
                }
            };
        }

        /** The per-pack changed-only port; the target must be bundle-acquired. */
        public OptionEditSessionImpl.PackOptionsIo packIo(PackOptionsTarget target,
                                                          com.schmaloogium.engine.config.OptionCatalog catalog,
                                                          DiagnosticReporter diagnostics) {
            Objects.requireNonNull(target, "target");
            Objects.requireNonNull(catalog, "catalog");
            OptionPersistenceCodec codec = services.optionPersistence();
            return state -> codec.write(new OptionPersistenceWriteRequest(
                    files, target, catalog, state, diagnostics))
                    .status() == com.schmaloogium.engine.pack.PersistenceWriteStatus.COMMITTED;
        }

        public Path shaderpacksDirectory() {
            return roots.shaderpacksDirectory();
        }
    }
}
