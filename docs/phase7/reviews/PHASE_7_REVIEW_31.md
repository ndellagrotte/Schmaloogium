# Phase 7 verification review — Round 31

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the complete target at
`docs/phase7/v1/PHASE_7_DOC.md`, the manifest-selected `docs/design/v3/DESIGN.md` Part I, Phase 7
specification, document gate, and mandatory template, RESEARCH ground truth, and the binding §5
contracts of Phases 2–6. I settled each candidate's interpretation, severity, and interface
classification before reading `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_30.md`, in order and last, and then checked the candidates
against that settled material.

The selected v3 design revision is the supplied verification-only override; it does not rewrite
the target's declared adoption state. I did not read
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`, because the resolved contract forbids every
`*.txt` source and it is immaterial to these target-owned consistency candidates. The remaining
supporting evidence was not needed to decide either candidate. There was no network use,
forbidden-transcript use, or agent fan-out. This was the canonical engine's already-dispatched
atomic adjudication role, so the supplied `verify-loop` instructions required completing only this
role without invoking the loop or delegating. There were no other reading-order deviations, no
candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Scope still labels granted manifest projections as ungranted

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:303`–`:304`
- **Claim:** The scope summary incorrectly subjects Phase 2 capture functionality to ungranted
  manifest projections even though the target's binding request-status section records all such
  projections as granted history.
- **Evidence:** Scope item 8 says the Phase 2 capture point, readiness signal, agent host, and
  shutdown bridge are “subject to the ungranted manifest projections in §5.4”
  (`docs/phase7/v1/PHASE_7_DOC.md:303`–`:304`). Section 5.4 instead marks R7-4 through R7-7 granted,
  states that R7-1 through R7-7 impose no feature, COMPLETE, or T3 gate, and identifies the actual
  remaining conditions as R7-8 package placement and Phase 3 reverification before production from
  granted R7-9 (`docs/phase7/v1/PHASE_7_DOC.md:2051`–`:2060`). R7-8 is not a manifest projection,
  and R7-9 is not an ungranted Phase 2 capture projection. The two implementation-facing
  statements are therefore incompatible.
- **Required correction:** Replace the stale scope qualifier with language consistent with §5.4.
  If the scope summary retains outstanding gates, name R7-8 package placement and the separate
  Phase 3/R7-9 reverification condition precisely.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the stale statement and required correction
  are in §1.1; the settled §5 contract need not change.

### candidate-002 — Exact exposed-contract index omits the resize-observation seam

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1812`–`:1888`
- **Claim:** Section 5.1's purportedly exact exposed-contract inventory omits a public Phase 7 API
  that crosses the `:engine`/`mod.glue.frame` seam.
- **Evidence:** Phase 7 publicly declares `ResizeObservationPort`, the closed
  `ResizeObservation` input algebra, the closed `ResizeObservationResult` algebra, and public
  `ResizeLifecycleRejection` (`docs/phase7/v1/PHASE_7_DOC.md:1566`–`:1598`). The binding prose says
  `mod.glue.frame` submits H-RESIZE-02, H-RESIZE-01, and H-FBO-01 through that port and handles every
  closed result (`docs/phase7/v1/PHASE_7_DOC.md:1608`–`:1618`). Section 5.1 then introduces its table
  as “The exact exposed contracts” (`docs/phase7/v1/PHASE_7_DOC.md:1812`–`:1815`), but the table
  ends without a row for any member of this public resize-observation family
  (`docs/phase7/v1/PHASE_7_DOC.md:1817`–`:1888`). Detailed narrative semantics do not repair an
  explicitly exhaustive consumer-facing index.
- **Required correction:** Add a §5.1 row for `ResizeObservationPort`, `ResizeObservation`,
  `ResizeObservationResult`, and `ResizeLifecycleRejection`, naming `mod.glue.frame` as consumer and
  summarizing the already-specified authenticated observation, duplicate/rejection/failure,
  abort-open-frame, and frame-boundary coalescing semantics without introducing new behavior.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the correction changes the exposed-interface
  inventory inside the manifest-declared §5 region and fires its fresh-review trigger.

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation apart from the two admitted
defects. Round 30's edited depth-copy ordering, programless virtual transition, overlay lease,
replay-aware GL-result ownership, COMPLETE/T3 gate language, milestone staging, blocker list,
implementation checklist, and closing status are internally synchronized. The Phase 2–6 consumed
contracts match their manifest-selected binding regions. The Phase 8 and Phase 9 downstream slots
remain explicitly conditional on verified future §5 contracts. The conformance map covers the
governing frame-flow requirements, hook needs 1–11, classic-program rows, injection timeline,
milestone/seam requirements, event preference, and both spike specifications.

Neither candidate was refuted or cleared on re-derivation. Prior findings concerning the resize
seam's authentication and visibility settled different defects; they do not settle omission of
the now-public seam from the exact inventory. Round 30 settled R7-1 through R7-7 as granted history,
which confirms rather than clears the stale §1.1 qualifier. No absent candidate was converted into
a finding, and no finding was dropped on derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded consistency corrections and do not require structural rebuilding.
Corrections remain at two across Rounds 30 and 31, following the recent 2 → 1 → 2 → 2 sequence;
strict decrease and literal convergence have not been reached, so the result cannot be softened to
PASS. The next required action is a scoped fix-up of candidate-001 and candidate-002, including this
review's `## Resolutions` record and a new target addendum. Because candidate-002 changes the
manifest-declared §5 interface region, a fresh whole-document and interface verification round is
required before Phase 7 can close. No version roll may occur until the loop exits.

## Resolutions

### candidate-001 — applied

Re-derived from the §5.4 request-status table, R7-1 through R7-7 are granted dependency history that
imposes no feature, COMPLETE, or T3 gate; the only outstanding conditions are R7-8 package placement
and the separate Phase 3/R7-9 reverification condition. The §1.1 scope item for the Phase 2 capture
point now states exactly that, naming both remaining gates instead of referring to ungranted manifest
projections in §5.4. No other elaboration is changed.

### candidate-002 — applied

Re-derived from the declared resize-observation family and its binding prose, the public
`ResizeObservationPort` / `ResizeObservation` / `ResizeObservationResult` /
`ResizeLifecycleRejection` surface crosses the `:engine`/`mod.glue.frame` seam and was absent from
the table introduced as "The exact exposed contracts". That table now carries a row for the family,
naming `mod.glue.frame` as consumer and restating only the already-specified semantics: authenticated
render-thread observations, mutation-free duplicate/rejection/failure, `Recorded(true)` aborting the
open shader frame, and coalescing of accepted changes into one frame-boundary rebuild/publication.
No new behavior was introduced.

### Interface/change-trigger record

candidate-002 intentionally changes the manifest-declared §5 cross-phase-interface region by adding a
row to the exact exposed-contract inventory. A fresh whole-document and interface verification round
is therefore required before Phase 7 can close. candidate-001 is a §1.1 scope-summary correction and
does not touch the §5 region.

### Notes deferred

None; the adjudicator admitted no notes.
