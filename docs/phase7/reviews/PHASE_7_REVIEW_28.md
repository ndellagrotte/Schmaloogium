# Phase 7 verification review — Round 28

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the complete target at
`docs/phase7/v1/PHASE_7_DOC.md`, the manifest-selected `docs/design/v3/DESIGN.md` Part I, Phase 7
specification, document gate, and mandatory template, RESEARCH ground truth, and the binding §5
contracts of Phases 2–6. I checked the relevant permitted supporting evidence and the target's
whole-document terminology before settling either disposition. Only after that independent
judgment did I read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_27.md`, in order and last, and compare the candidates against
settled material.

The selected v3 design revision is the supplied verification-only override; it does not rewrite
the target's declared adoption state. I did not read
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`, because the resolved contract forbids every
`*.txt` source; that source is immaterial to the two surviving candidates. There was no network
use, forbidden-transcript use, or agent fan-out. This was the canonical engine's already-dispatched
atomic adjudication role, so the supplied `verify-loop` instructions required completing only this
role without invoking the loop or delegating. No candidates were eliminated before adjudication,
and Gate dropped none.

## 1. Findings

### candidate-001 — Closing status is stale after three later fix-ups

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:2402`–`:2404`
- **Claim:** The designated closing summary does not accurately identify the corrections currently
  applied or the latest reason that whole-document verification remains pending.
- **Evidence:** The latest addendum says Round 27 recognizes the R7-9 grant, corrects three §3.1
  coordinates, changes §5, and requires fresh verification
  (`docs/phase7/v1/PHASE_7_DOC.md:262`–`:266`). The closing paragraph instead says only that
  Rounds 22–23's corrections are applied and attributes the pending review to the older §0.25
  interface change (`docs/phase7/v1/PHASE_7_DOC.md:2402`–`:2404`). Section 0.30 preserves the true
  current status elsewhere, but it does not make this contradictory closing governance summary
  accurate.
- **Required correction:** Synchronize the closing paragraph with §0.30: state that corrections
  through Round 27 are applied, that Round 27 most recently changed binding §5 and therefore
  requires a fresh whole-document review, and retain the no-version-roll statement.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the stale closing paragraph is outside the
  manifest-declared §5 region, and its correction need not alter an interface contract.

### candidate-002 — Publication protocol omits mandatory Phase 6 registry-generation adoption

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1947`–`:1956` and `:1968`–`:1989`
- **Claim:** Phase 7's binding Phase 6 inventory and coordinated-publication protocol omit the
  required runtime transition to the newly accepted Phase 4 registry generation.
- **Evidence:** Phase 6 requires `UniformRuntimeFactory.create` to receive the current
  `PublishedRegistry.generation`. After accepted publication and reacquisition, Phase 7 must call
  `adoptRegistryGeneration` with the replacement's authoritative generation and an accepted reset
  reason before the runtime's first `beginFrame`, event, or participant activation; the closed
  outcomes are `ADOPTED`, `ALREADY_CURRENT`, and `REJECTED_RETIRED_GENERATION`
  (`docs/phase6/v1/PHASE_6_DOC.md:1382`–`:1383`). Phase 4's detached candidate view has no
  publication generation and does not observe publication
  (`docs/phase4/v1/PHASE_4_DOC.md:1564`). Phase 7 nevertheless creates the runtime before compiling
  and publishing the new Phase 4 candidate, then advances from Phase 4 acceptance through the
  other publications to Active without any adoption call or closed-outcome handling
  (`docs/phase7/v1/PHASE_7_DOC.md:1968`–`:1989`). Its Phase 6 inventory names only a runtime per
  publication, frame begin, events, participants, and providers
  (`docs/phase7/v1/PHASE_7_DOC.md:1947`–`:1956`). Fingerprint validation cannot perform this
  lifecycle transition.
- **Required correction:** Amend §§5.2–5.3 to pass the currently published registry generation to
  `UniformRuntimeFactory.create`; after Phase 4 accepts, reacquire its authoritative generation and
  call `adoptRegistryGeneration` with the applicable Phase 6 accepted reason before any new-runtime
  event, participant activation, shadow-runtime use, or `beginFrame`. Exhaustively handle
  `ADOPTED`, `ALREADY_CURRENT`, and `REJECTED_RETIRED_GENERATION` through the existing
  publication recovery/off policy before activation, without inventing broader compensation
  semantics unsupported by a dependency contract.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the omitted consumed contract and publication
  ordering are in binding §§5.2–5.3, so correcting them triggers a fresh Phase 7 verification
  round.

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation apart from the two admitted
defects. Round 27's R7-9 grant is consistently represented across §5, dependency/blocker text,
tests, staging, requested-upstream text, and the checklist while retaining Phase 3's pending
reverification production gate. The three Round-27 §3.1 provenance corrections are consistent.
The skipped §0.28 label does not itself establish a defect.

No additional interface-honesty defect was established in Phase 2 capture/report consumption,
Phase 3 pack/configuration consumption, Phase 4 registry/barrier consumption, or Phase 5
buffer/resize consumption and downstream hand-offs. The conformance map continues to cover the
in-scope frame flow, all Appendix A.1 program families, hook needs 1–11, the revised injection
timeline, assigned engine flags, and identified contradictions. Prior reviews do not settle or
clear either present-byte defect: they contain no generation-adoption disposition, and their
earlier closing-status checks predate the Round-25 through Round-27 fix-ups now omitted from the
closing paragraph. There were no refuted, eliminated, or Gate-dropped candidates to clear.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded corrections rather than structural misses requiring a rebuild.
Relative to Round 27's three corrections, the count falls to two, but literal convergence has not
been restored because corrections remain. The next required action is a scoped fix-up of
candidate-001 and candidate-002, with this review's `## Resolutions` record and a new §0 addendum.
Because candidate-002 changes the manifest-declared §5 interface region, a fresh whole-document
and interface verification round is required before Phase 7 can close. No version roll may occur
until that loop exits.

## Resolutions

### candidate-001 — resolved

Synchronized the terminal status in `docs/phase7/v1/PHASE_7_DOC.md` with §0.30. It now states that
corrections through Round 27 are applied, identifies Round 27 as the latest binding-§5 change that
requires whole-document verification, and preserves the prohibition on a version roll while the
loop remains open.

### candidate-002 — resolved

Amended the binding Phase 6 inventory and coordinated-publication protocol in
`docs/phase7/v1/PHASE_7_DOC.md` §§5.2–5.3. Runtime construction now receives the current Phase 4
publication generation. After Phase 4 accepts, Phase 7 reacquires the authoritative generation and
adopts it with `PACK_REPLACEMENT` before any event, participant activation, shadow use, or
`beginFrame`. `ADOPTED` and `ALREADY_CURRENT` proceed; `REJECTED_RETIRED_GENERATION` enters the
existing recovery/off path before activation. The later publication steps were renumbered without
adding compensation semantics beyond existing dependency contracts.

This intentionally changes the manifest-declared §5 interface region, so a fresh whole-document
and interface verification round is required before Phase 7 can close. A compact §0.31 addendum
records that status; the `v1` directory was not rolled.

### Notes deferred

None. The adjudicator admitted no notes.
