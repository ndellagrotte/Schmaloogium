// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;
import java.util.List;


import com.schmaloogium.engine.pack.PackInspectionResult;

/**
 * The T0 compile leg's stage boundary (§4.2.1 "programs compile"). The default
 * implementation is {@link RegistryCompileStage}, driving Phase 4's
 * {@code ProgramRegistryCompiler} headlessly over the scripted device; tests may
 * inject other stages.
 */
public interface CompileStage {

    /** Per-slot compile evidence, or the named reason it cannot be produced. */
    CompileEvidence compile(PackInspectionResult.Inspected inspected);

    /** Either a closed per-slot table or a deferral reason. {@code rows} is the
     *  per-slot resolution projection: empty while deferred, and once P4 lands one
     *  row per slot in P4's status vocabulary (SOURCED/CHAIN/ABSENT/FAILED) — exactly
     *  what the manifest's programs family will carry. {@code failureReason} is
     *  non-empty when the leg ran but the registry build failed pack-wide (P4's
     *  closed {@code ShadersOff} outcome); it is distinct from a deferral, which
     *  reports the leg as not run at all. */
    record CompileEvidence(boolean runnable, String deferredReason, String failureReason,
            List<SlotRow> rows) {

        public CompileEvidence {
            deferredReason = deferredReason == null ? "" : deferredReason;
            failureReason = failureReason == null ? "" : failureReason;
            rows = List.copyOf(rows);
        }

        /** The leg could not run; T0 stays undecided on it, never failed by it. */
        public static CompileEvidence deferred(String reason) {
            return new CompileEvidence(false, java.util.Objects.requireNonNull(reason),
                "", List.of());
        }

        /** The leg ran and the registry build failed pack-wide. */
        public static CompileEvidence failed(String reason) {
            return new CompileEvidence(true, "", java.util.Objects.requireNonNull(reason),
                List.of());
        }

        /** The leg ran and every slot resolved; rows may legitimately be empty when
         *  nothing is selected (all slots ABSENT). */
        public static CompileEvidence resolved(List<SlotRow> rows) {
            return new CompileEvidence(true, "", "", rows);
        }
    }

    /** One slot row of the resolution projection. */
    record SlotRow(String slot, String status) {
    }
}
