## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited supporting evidence. I searched the target and dependencies
for an equivalent owner, source, lifecycle, or comparison rule for the named active-world
publication before settling the candidates. Only after that judgment did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_15.md`, in order and last. Round 15 introduced the current
world-authenticated resize wording, but its resolution did not define the authority against which
the newly added caller-supplied values are authenticated.

There were no reading-order deviations, no network use, no forbidden-source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Resize authentication has no defined canonical world publication

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1231`–`:1275`
- **Claim:** The resize contract requires authentication to the active world incarnation but does
  not define an independently held source of truth, so implementations must guess how to validate
  caller-supplied epoch and dimension values and when that authority changes or disappears.
- **Evidence:** Every public observation carries a caller-supplied `worldEpoch` and `DimensionKey`,
  and internal boundary application receives the same scalar pair
  (`docs/phase7/v1/PHASE_7_DOC.md:1231`–`:1264`). The prose then requires rejection when the epoch
  differs from “the active world publication” (`docs/phase7/v1/PHASE_7_DOC.md:1266`–`:1275`), but
  neither this interface nor equivalent coverage elsewhere identifies that publication's owner,
  how `ResizeLifecycleSink` reads it independently of the submitted values, or its install,
  replacement, unload, and no-active-world transitions. The lifecycle only says that
  `FrameHookBridge` copies plain values and increments `worldEpoch` for a new world object
  (`docs/phase7/v1/PHASE_7_DOC.md:787`–`:800`); that propagation rule is not an authentication
  authority. The result taxonomy separately exposes `STALE_WORLD_EPOCH` and `STALE_DIMENSION`
  (`docs/phase7/v1/PHASE_7_DOC.md:1257`–`:1259`), while the binding prose specifies only the epoch
  comparison. The implementation checklist requires both resize-epoch handling and world-unload
  teardown (`docs/phase7/v1/PHASE_7_DOC.md:1892`–`:1893`), confirming that these transition
  semantics cannot safely be left implicit.
- **Required correction:** Define the minimum canonical active-world identity consumed by
  `ResizeLifecycleSink`: its single owner, atomic `(worldEpoch, DimensionKey)` value, how the sink
  obtains it independently of observation payloads, and the exact install/replace/clear moments for
  new-world and unload transitions. Bind `FrameBeginSignal.worldEpoch` to that same authority and
  specify mutation-free outcomes for epoch mismatch, dimension mismatch, and no active world. A
  private composition dependency is sufficient; no broad new public API or unforgeable credential
  is required.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the missing authentication and lifecycle
  semantics belong to the manifest-selected §5 resize contract, so a fresh verification round is
  required after correction.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The frame-begin ordering remains Phase 6
sampling before Phase 5 resize/clear; the observation stream otherwise specifies render-thread hook
order, duplicate handling, extent and attachment-epoch coalescing, exclusive safe-boundary
coordination, mutation-free failures, and the `Recorded(true)` mid-frame abort path. The governing
scope and conformance map, program and hook coverage, OQ-3/OQ-4 spikes, dependency consumptions, and
implementation gates yielded no other candidate-backed defect.

`candidate-002` is dropped as an exact duplicate of admitted `candidate-001`. It identifies the
same undefined active-world publication in the same §5 resize contract and seeks the same owner,
transition lifecycle, and authoritative comparison source. Its useful refinement—that
`FrameBeginSignal.worldEpoch` must derive from the same authority—is incorporated into
candidate-001's required correction; admitting it separately would double-count one defect.

Prior reviews do not clear the admitted issue. Round 14 introduced the observation-to-boundary seam,
and Round 15 added world-epoch fields and stale-world rejection, but the Round 15 resolution merely
repeats comparison with an “active world publication” without defining its publication source or
lifecycle. No earlier settled disposition supplies the missing authority.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The single admitted finding is a localized interface implementability correction, not a structural
miss requiring rebuild. The recent trend remains non-convergent: Rounds 14–16 are 1 → 2 → 1, so
corrections are not strictly decreasing and literal PASS has not been reached. The next required
action is a scoped fix-up of this review, including a `## Resolutions` record and Phase 7 addendum;
because the correction changes the cross-phase interface region, a fresh whole-document and
interface verification round is then required before Phase 7 can close.

## Resolutions

### candidate-001 — applied

Re-derived the defect from the existing lifecycle and §5 resize seam. The target now makes
`FrameHookBridge` the sole writer of one atomic internal `ActiveWorldIdentityPublication`, gives
`ResizeLifecycleSink` that publication as a private composition dependency, and defines install,
dimension replacement, and unload clearing moments. `FrameBeginSignal` takes its epoch and
dimension from one snapshot of the same publication. Observation and boundary authentication now
have explicit mutation-free `NO_ACTIVE_WORLD`, `STALE_WORLD_EPOCH`, and `STALE_DIMENSION`
outcomes. This changes the manifest-selected interface region, so a fresh verification round is
required before Phase 7 can close.

### Notes deferred

None.
