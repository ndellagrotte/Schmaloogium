## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited dependency evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_5.md`, in order and last. The prior reviews establish that the
Round 5 fix-up changed §5 and therefore required this fresh review; neither Round 6 candidate was
previously settled.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Hook report schema cannot represent the catalog's failure classes

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1233`–`:1239`
- **Claim:** The exposed `HookApplicationReport` schema cannot faithfully encode the hook-health
  classes declared by the catalog and used by application/runtime degradation policy.
- **Evidence:** The catalog defines the four classes `CORE`, `FEATURE`, `OBSERVER`, and
  `DEFERRED(Pn)`, with materially different application and runtime policies
  (`docs/phase7/v1/PHASE_7_DOC.md:800`–`:807`). The report promises each row's group and is consumed
  by diagnostics, Phase 2, and Phase 10 (`docs/phase7/v1/PHASE_7_DOC.md:982`–`:985`, `:1325`), but
  its closed enum is instead `CORE`, `OPTIONAL`, and `DEFERRED`
  (`docs/phase7/v1/PHASE_7_DOC.md:1233`–`:1239`). The document defines no mapping that collapses
  `FEATURE` and `OBSERVER` into `OPTIONAL`; such a collapse would also erase their distinct
  policies. Some catalog rows carry more than one class, such as `FEATURE` plus `DEFERRED(P9)` or
  `CORE` plus `FEATURE` (`docs/phase7/v1/PHASE_7_DOC.md:859`, `:882`), which the singular field also
  cannot represent without an explicit row-splitting rule.
- **Required correction:** Replace the report taxonomy with the catalog's closed vocabulary and
  define faithful encoding of multi-class catalog entries, either as separate report rows or an
  explicitly multi-valued class field. Preserve deferred owner-phase information if downstream
  consumers require the `DEFERRED(Pn)` parameter; do not collapse `FEATURE` and `OBSERVER`.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — `HookApplicationReport` is an exposed contract
  inside the manifest-selected §5 region, so correcting it fires the fresh-review trigger.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. Round 5's readiness correction consistently
keeps the Phase 4 registry generation and Phase 5 estate generation as independent `long` equality
tokens, while `FinalizedFrame.version` remains the separate Phase 7 `PipelineVersion`. Listener
installation is construction-only, the borrowed capture view expires on listener return, and the
capture notification remains exactly once after successful final-pass completion and before
presentation. The reviewed Phase 3–6 consumptions and the explicitly gated dependency requests
remain honest. The conformance map, hook catalog and deferral ledger, assigned OQ spikes, and v0.1
assembly narrative yielded no additional candidate-backed defect.

`candidate-002` is cleared on re-derivation. Phase 7 expressly owns the capture-agent host and
clean-shutdown bridge (`docs/phase7/v1/PHASE_7_DOC.md:145`–`:148`) and keeps capture and shutdown as
`mod.conformance` services called at the finalization notification
(`docs/phase7/v1/PHASE_7_DOC.md:256`–`:260`). Its detailed design identifies the concrete Minecraft
shutdown entry and main-thread scheduling (`docs/phase7/v1/PHASE_7_DOC.md:917`–`:918`), and the
owning `CaptureAgent` schedules that bridge after the final image and manifest commits
(`docs/phase7/v1/PHASE_7_DOC.md:992`–`:1005`). This satisfies Phase 2 R14 as an internally hosted
mod-side service; no separate cross-phase callable or result algebra is needed. The failure table
also keeps clean shutdown available after artifact-write failure
(`docs/phase7/v1/PHASE_7_DOC.md:1474`). Adding the proposed public seam would broaden §5 without a
demonstrated external consumer.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted schema defect is local and correction-level; it does not require rebuilding the Phase
7 architecture. It changes the manifest-declared cross-phase interface region, so a fresh
whole-document/interface verification round is required after fix-up before Phase 7 can close.

The correction trend is 3, 3, 4, 2, 2, 1 across Rounds 1–6. This is numerical improvement and the
lowest count so far, but literal convergence has not been reached because one interface correction
remains. The next required action is a scoped fix-up of this review, including its `## Resolutions`
record and Phase 7 addendum, followed by fresh verification of the changed interface and corrected
whole document.

## Resolutions

### candidate-001 — corrected

Re-derived from the catalog rather than treating the finding as evidence. Sections 4.10.1 and
4.10.2–4.10.6 establish the closed `CORE`, `FEATURE`, `OBSERVER`, and `DEFERRED(Pn)` vocabulary,
distinct policies, and mixed-class rows. Section 5.1 now exposes a non-empty multi-valued
`Set<HookHealthClass>` with exactly those four classes and an `OptionalInt deferredOwnerPhase` that
is present exactly for `DEFERRED` rows and preserves `Pn`. The report description and contract-table
summary now use the same taxonomy. No `FEATURE`/`OBSERVER` collapse or row-splitting rule remains.

This correction changes the manifest-selected cross-phase interface region, so Phase 7 still owes
a fresh verification round before closure.

### Notes deferred

None; the adjudicator admitted no notes.
