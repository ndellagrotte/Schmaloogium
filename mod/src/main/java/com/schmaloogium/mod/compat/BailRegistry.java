// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The bail registry (PHASE_1_DOC §4.10): Phase 1 ships the mechanism, all three
 * evaluation points' machinery, and zero checks — the mod-id list, detection technique
 * and message text are Phase 10 / OQ-5 (D-P1-23).
 *
 * <p>Evaluation semantics: a check that throws is treated as {@code Bail} — a check that
 * cannot decide is not evidence of compatibility (§6). Registration of an early check
 * must happen before the MOD plugin's first evaluation; repeated registration of the
 * same object/id is a no-op, but a different check reusing an id fails registration.
 */
public final class BailRegistry {

    private static final List<CompatCheck> CHECKS = new CopyOnWriteArrayList<>();
    private static final Map<String, CompatCheck> IDS = new LinkedHashMap<>();
    private static final Map<String, EarlyCompatCheck> EARLY_CHECKS = new LinkedHashMap<>();

    // The latched early veto (D-P1-50): retained for the session; later evaluation
    // cannot undo partial transformation.
    private static volatile CompatVerdict.Bail latchedEarlyBail = null;

    private BailRegistry() {
    }
    /** Test-only: restores the pristine registry between headless tests. */
    static void resetForTests() {
        CHECKS.clear();
        synchronized (IDS) {
            IDS.clear();
            EARLY_CHECKS.clear();
        }
        latchedEarlyBail = null;
    }

    /** Registers a check. Early-safe checks register before MOD plugin evaluation; others in preInit. */
    public static void register(CompatCheck check) {
        synchronized (IDS) {
            CompatCheck existing = IDS.get(check.id());
            if (existing != null) {
                if (existing != check) {
                    throw new IllegalArgumentException(
                            "compat check id already registered by a different check: " + check.id());
                }
                return; // repeated registration of the same object is a no-op
            }
            if (check instanceof EarlyCompatCheck early) {
                EARLY_CHECKS.put(check.id(), early);
            }
            IDS.put(check.id(), check);
        }
        if (!CHECKS.contains(check)) {
            CHECKS.add(check);
        }
    }

    /** Evaluates the early subset only, in stable registration order (D-P1-50). */
    public static CompatEvaluation evaluateEarly(EarlyCompatContext ctx) {
        List<CompatVerdict.Bail> bails = new ArrayList<>();
        List<CompatVerdict.Degrade> degradations = new ArrayList<>();
        List<EarlyCompatCheck> snapshot;
        synchronized (EARLY_CHECKS) {
            snapshot = new ArrayList<>(EARLY_CHECKS.values());
        }
        for (EarlyCompatCheck check : snapshot) {
            CompatVerdict verdict;
            try {
                verdict = check.checkEarly(ctx);
            } catch (Throwable t) {
                verdict = new CompatVerdict.Bail("schmaloogium.compat.check.threw",
                        List.of(check.id(), String.valueOf(t)));
            }
            accumulate(check.id(), verdict, bails, degradations);
        }
        CompatEvaluation evaluation = new CompatEvaluation(bails, degradations);
        if (evaluation.shouldBail() && latchedEarlyBail == null) {
            latchedEarlyBail = bails.get(0);
        }
        return evaluation;
    }

    /** Evaluates all checks at a runtime point (bootstrap, vertex-format change). */
    public static CompatEvaluation evaluate(CompatContext ctx) {
        List<CompatVerdict.Bail> bails = new ArrayList<>();
        List<CompatVerdict.Degrade> degradations = new ArrayList<>();
        // A latched early veto outranks everything (D-P1-50: terminal, session-scoped).
        if (latchedEarlyBail != null) {
            bails.add(latchedEarlyBail);
        }
        for (CompatCheck check : CHECKS) {
            CompatVerdict verdict;
            try {
                verdict = check.check(ctx);
            } catch (Throwable t) {
                verdict = new CompatVerdict.Bail("schmaloogium.compat.check.threw",
                        List.of(check.id(), String.valueOf(t)));
            }
            accumulate(check.id(), verdict, bails, degradations);
        }
        return new CompatEvaluation(bails, degradations);
    }

    private static void accumulate(String id, CompatVerdict verdict,
                                   List<CompatVerdict.Bail> bails,
                                   List<CompatVerdict.Degrade> degradations) {
        if (verdict instanceof CompatVerdict.Bail bail) {
            bails.add(bail);
        } else if (verdict instanceof CompatVerdict.Degrade degrade) {
            degradations.add(degrade);
        }
    }
}
