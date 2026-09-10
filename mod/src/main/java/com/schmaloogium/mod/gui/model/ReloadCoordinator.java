// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Optional;
import java.util.List;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;

/**
 * The one-way submit seam Phase 7 installs (§5.1): {@code void} because the drain is
 * asynchronous and Phase 7 owns the drain point, thread and everything downstream.
 * Until an implementation is installed every trigger is inert (§6): requests are
 * dropped, one warning is logged for the session, and the GUI stays degraded but
 * functional — it still opens, reads and persists.
 */
@FunctionalInterface
public interface ReloadCoordinator {

    void submit(ReloadRequest request);

    /** The installed coordinator, or empty before Phase 7 installs one. */
    static Optional<ReloadCoordinator> installed() {
        return Holder.INSTANCE.optional();
    }

    /** Installs the Phase 7 implementation; later installs replace the earlier one. */
    static void install(ReloadCoordinator coordinator) {
        Holder.INSTANCE.set(coordinator);
    }

    /** Removes the installed coordinator and resets the inert warning (test seam). */
    static void clear() {
        Holder.INSTANCE.reset();
    }

    /** Submits to the installed coordinator, or warns once and drops the request. */
    static void submitOrReportInert(ReloadRequest request, DiagnosticReporter reporter) {
        Optional<ReloadCoordinator> current = installed();
        if (current.isPresent()) {
            current.get().submit(request);
            return;
        }
        if (reporter != null && !Holder.INSTANCE.inertWarned) {
            Holder.INSTANCE.inertWarned = true;
            reporter.report(new EngineDiagnostic(DiagnosticSeverity.WARN,
                    UserChannel.LOG_ONLY, "schmaloogium.warn.reload.inert", List.of(),
                    request.lifecycle() + "/" + request.cause(), "schmaloogium.config"));
        }
    }

    /** The mutable install slot. */
    final class Holder {

        private static final Holder INSTANCE = new Holder();

        private volatile ReloadCoordinator delegate;
        private volatile boolean inertWarned;

        private Holder() {
        }

        private Optional<ReloadCoordinator> optional() {
            return Optional.ofNullable(delegate);
        }

        private void set(ReloadCoordinator coordinator) {
            java.util.Objects.requireNonNull(coordinator, "coordinator");
            this.delegate = coordinator;
        }

        private void reset() {
            this.delegate = null;
            this.inertWarned = false;
        }
    }
}
