## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited dependency evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md`,
`docs/phase7/reviews/PHASE_7_REVIEW_2.md`, and
`docs/phase7/reviews/PHASE_7_REVIEW_3.md`, in that order and last. The prior reviews show how the
current extent-bearing target arose, but none previously identified or settled either Round 4
candidate.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Phase 7 redefines Phase 5's singleton screen draw target

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:652`–`:653` and `:1111`–`:1126`
- **Claim:** Phase 7 does not consume Phase 5's published final-pass draw target unchanged; it
  publishes and uses an incompatible extent-bearing type under the same `PassDrawTarget` name.
- **Evidence:** The executor directs the implementation to “construct
  `PassDrawTarget.Screen(extent)` for the current target extent”
  (`docs/phase7/v1/PHASE_7_DOC.md:652`–`:653`). Its exposed render-port schema correspondingly
  declares `record Screen(Extent2i extent) implements PassDrawTarget`
  (`docs/phase7/v1/PHASE_7_DOC.md:1111`–`:1126`), and §5.2 nevertheless lists Phase 5's
  `PassDrawTarget.Screen` as the consumed final handoff
  (`docs/phase7/v1/PHASE_7_DOC.md:1357`–`:1366`). Phase 5's binding contract instead fixes the
  final snapshot target as payload-free `Screen.INSTANCE` with no engine handle
  (`docs/phase5/v1/PHASE_5_DOC.md:1676`). Thus the Phase 7 port cannot directly accept the
  dependency-owned snapshot target it claims to consume; an implementer would have to invent an
  undocumented conversion and decide independently where its extent comes from.
- **Required correction:** Consume Phase 5's `PassDrawTarget.Screen.INSTANCE` unchanged. Remove or
  rename Phase 7's colliding `PassDrawTarget` declaration and, if the glue port needs current target
  extent independently, carry it through a distinctly named Phase-7-owned value or existing estate
  metadata. Update §4.6 and §5 consistently.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the incompatible declaration and render-port
  signature are in the manifest-selected §5 region, so correcting the public seam fires the fresh
  verification trigger.

### candidate-002 — Two conformance tables omit mandatory per-row provenance

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:320`–`:369`
- **Claim:** Sections 3.2 and 3.3 do not map every in-scope contract row to both its satisfying
  design element and a provenance tag as the mandatory template requires.
- **Evidence:** The governing template requires every conformance-map row to identify the contract
  item, satisfying design element, and provenance tag, with zero unmapped rows
  (`docs/design/v2.0-RC3/DESIGN.md:804`–`:808`). Section 3.2's Appendix-A.1 table has only
  “Appendix-A.1 slot/family” and “Phase-7 execution route” columns
  (`docs/phase7/v1/PHASE_7_DOC.md:320`–`:347`). Section 3.3's hook-needs table likewise has only
  “Need” and “Disposition” columns (`docs/phase7/v1/PHASE_7_DOC.md:353`–`:369`). The aggregate
  RESEARCH coordinate following §3.3 does not assign a provenance tag to each row. By contrast, the
  immediately preceding §3.1 table demonstrates the required per-row `Provenance` field
  (`docs/phase7/v1/PHASE_7_DOC.md:304`–`:306`).
- **Required correction:** Add a provenance column to §§3.2 and 3.3 and populate every row with the
  applicable confidence or decision tag and authoritative RESEARCH/Appendix coordinate. Retain
  dependency or reference citations where they substantiate the selected mechanism.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the traceability correction is confined to §3
  and does not alter the selected §5 cross-phase interface.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. Round 3's identifier corrections are
internally consistent: finalization uses only `NORMAL`, `EARLY_RETURN`, and `THROWN`; the
post-shadow state is `GBUFFERS`; and the fullscreen primitive is `QUADS` with the retained
`TRIANGLE_STRIP` fallback. The corrected D-1–D-3 and D-7–D-9 dispositions retain their governing
subjects.

No additional mismatch survived review of Phase 2 capture/harness consumption, Phase 3 pack and
configuration consumption, Phase 4 registry/barrier consumption, Phase 6 uniform-runtime
consumption, or the downstream shadow and reload hand-offs. Apart from candidate-001's final-target
schema collision, the selected dependency boundaries remain explicit and consistent.

The target provides substantive design dispositions for the RESEARCH §4.4 frame flow, Appendix
A.1 program families, hook needs 1–11, the seven-row reference timeline, Phase 7 engine flags, and
Appendix E rows. Frame ordering, early-exit finalization, assigned OQ spikes, reference-free hook
treatment, milestone gates, and the v0.1 assembly narrative yielded no additional candidate-backed
defect. Both supplied candidates were confirmed on independent re-derivation; none was cleared or
dropped.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are correction-level and can be repaired without rebuilding the Phase 7
architecture. Candidate-001 requires a change in the manifest-declared cross-phase interface
region, so a fresh whole-document/interface verification round is required after fix-up before
Phase 7 can close.

The correction count decreases from four in Round 3 to two in Round 4, after the earlier
non-monotonic sequence of three, three, and four. That is improvement but not literal convergence:
two corrections remain, and the interface trigger independently requires another round. The next
required action is a scoped fix-up of this review, including its `## Resolutions` record and Phase 7
addendum, followed by fresh verification of the changed interface and corrected document.

## Resolutions

### candidate-001 — applied

Re-derived against Phase 5's binding contract and its defining schema. Phase 5 owns the closed
`PassDrawTarget`: raster snapshots carry `EngineFramebuffer(handle)`, while final carries the
payload-free `Screen.INSTANCE`. Phase 7 now passes that dependency-produced value unchanged to
`FrameRenderPort.bind`, removes its colliding extent-bearing declaration, names
`Screen.INSTANCE` consistently in §3.2, §4.6, and §5.2, and obtains no undocumented screen extent
through the draw target. This changes the declared §5 interface region, so fresh verification is
required before Phase 7 can close.

### candidate-002 — applied

Re-derived from the mandatory §G9 conformance-map rule. Sections 3.2 and 3.3 now include a
row-local `Provenance` column. Every Appendix-A.1 route cites the authoritative `[V:doc]` registry
range at `docs/research/v1/RESEARCH.md:1101`–`:1141`; every hook-need disposition cites the
authoritative `[D-5]` hook-strategy range at `docs/research/v1/RESEARCH.md:796`–`:819`. Existing
dependency and mechanism citations remain in place.

### Notes deferred

None. The adjudicator admitted no notes.
