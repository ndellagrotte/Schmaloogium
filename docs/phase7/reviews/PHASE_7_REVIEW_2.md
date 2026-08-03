## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH lifecycle material, the binding
§5 regions of Phases 2–6, and the cited supporting evidence. Only after settling those judgments did
I read `docs/phase7/reviews/PHASE_7_REVIEW_1.md`, last. Round 1 confirms that the current §5 surface
is newly changed and owed this fresh review; it does not settle any Round 2 candidate.

There were no reading-order deviations, no network use, and no forbidden source use. There was no
agent fan-out: this was the canonical engine's already-dispatched atomic adjudication role, so the
supplied `verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication and the Gate dropped none.

## 1. Findings

### candidate-001 — `FailureId` does not define a valid diagnostic identifier domain

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1033`
- **Claim:** Phase 7's shared public failure payload admits invalid identifiers and does not preserve
  the diagnostic-ID guarantee of the Phase 6 contract it wraps.
- **Evidence:** Phase 7 declares `public record FailureId(String diagnosticId) {}` with no canonical
  constructor, factory, or prose invariant (`docs/phase7/v1/PHASE_7_DOC.md:1033`). Java record
  construction therefore permits null and empty strings. The same interface section characterizes
  exposed inputs and values as validated or closed (`docs/phase7/v1/PHASE_7_DOC.md:1122`–`:1126`),
  but supplies no identifier-domain rule. Phase 6's binding factory result promises
  `Failure(non-empty diagnostic ID)` (`docs/phase6/v1/PHASE_6_DOC.md:1183`–`:1186`). Phase 7 also
  creates orchestration failures of its own, so the dependency guarantee alone cannot constrain all
  instances of its wrapper.
- **Required correction:** Give `FailureId` a binding construction invariant that rejects null and
  empty identifiers (and explicitly decide whether blank identifiers are also rejected), and state
  that dependency diagnostic IDs are preserved when mapped into it. Do not add sanitization or
  lifetime requirements absent another governing diagnostic contract.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — `FailureId` is declared inside the selected
  binding §5 region.

### candidate-002 — Consumer-facing exports remain names without implementable schemas

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1091`–`:1143`
- **Claim:** Several Phase 7-owned contracts promised to concrete consumers still lack callable
  signatures, complete fields, closed results, and caller obligations, forcing downstream phases or
  a replacement backend to guess.
- **Evidence:** The §5.1 table calls its entries “exact exposed contracts” but supplies only short
  summaries for `PipelineCoordinator` and its build request/results, the dimension cache records,
  fullscreen execution types, internal-pack content, reload types, capture readiness and callbacks,
  hook reports, and the uniform bridge (`docs/phase7/v1/PHASE_7_DOC.md:1122`–`:1143`). For example,
  §5.3 gives a detailed internal candidate-publication algorithm but no public coordinator operation,
  request fields, result payloads, or caller duties (`docs/phase7/v1/PHASE_7_DOC.md:1219`–`:1237`).
  Reload prose fixes queueing and safe-point behavior without defining `ReloadRequest` fields or
  `ReloadResult` variants (`docs/phase7/v1/PHASE_7_DOC.md:726`–`:730`), and capture prose fixes the
  sequence without defining `FrameReadiness`, `FinalizedFrame`, or the listener operation
  (`docs/phase7/v1/PHASE_7_DOC.md:969`–`:983`). Even the declared `FrameRenderPort` relies on
  undefined `PortResult`, `PassDrawTarget`, and `FullscreenDraw` support schemas
  (`docs/phase7/v1/PHASE_7_DOC.md:1091`–`:1097`). The governing verification standard requires that
  everything promised to dependents be specified rather than gestured at
  (`docs/design/v2.0-RC3/DESIGN.md:291`–`:292`).
- **Required correction:** Add implementable signatures and closed data/result schemas for the
  externally consumed Phase 7-owned contracts that remain undefined, including their validation,
  threading, ownership/retention, failure behavior, and caller duties. Prioritize pipeline build and
  reload, capture/readiness notification, fullscreen/backend support types, hook-report access, and
  the uniform bridge. Explicitly remove or defer any name Phase 7 is not ready to bind rather than
  mechanically expanding an internal-only detail.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — these exports are promises in the selected §5
  interface region.

### candidate-003 — The conformance map omits two assigned lifecycle requirements

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:289`–`:305`
- **Claim:** The mandatory contract-to-design map does not trace the internal/off distinction or the
  assigned dimension and uninit/reinit lifecycle, despite substantive designs for both elsewhere.
- **Evidence:** The governing Phase 7 assignment explicitly includes the internal default pack and
  shaders-off path, then dimension-switch lifecycle and pack/resolution uninit triggers using the
  mandated per-dimension cache/version mechanism (`docs/design/v2.0-RC3/DESIGN.md:1849`–`:1855`).
  RESEARCH identifies `OFF` and `(internal)` separately and requires uninit on pack change, option
  change, qualifying dimension switch, and resolution-multiplier change
  (`docs/research/v1/RESEARCH.md:478`–`:489`). Phase 7 substantively designs those behaviors in §4.7
  and §4.8 (`docs/phase7/v1/PHASE_7_DOC.md:704`–`:730`), but §3.1 maps only the §4.4 frame-flow rows
  and contains neither lifecycle trace (`docs/phase7/v1/PHASE_7_DOC.md:291`–`:305`). Other §3
  subsections map programs, hooks, reference timelines, and flags, not these requirements. The
  mandatory template requires every in-scope item to appear in §3 with design element and provenance,
  with zero unmapped rows (`docs/design/v2.0-RC3/DESIGN.md:804`–`:808`).
- **Required correction:** Add §3 conformance rows for (1) `(internal)` versus `Off`, mapping to §4.7
  and RESEARCH §4.1, and (2) the per-dimension lifecycle plus pack-, option-, dimension-, and
  resolution-triggered uninit/reinit, mapping to §4.8 with RESEARCH semantics and the assigned
  Pintonium mechanism/decision provenance.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the required traceability edits belong to §3,
  not the selected §5 interface region.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The Round 1 corrections are internally
consistent: `ProgramStateBundle` is now attributed to Phase 4, the frame-operation result algebras
state their terminal cleanup and token behavior, and the shadow invocation seam defines synchronous
borrowing and deterministic handling of all four outcomes, exceptions, and invalid credentials.
Nothing under the new-surface lens establishes a need for a richer failure payload than a properly
constrained identifier.

The dependency-consumption ledger accurately recognizes the major selected Phase 2–6 surfaces and
flags Phase 5 gaps instead of inventing APIs. No additional dependency mismatch survived the
interfaces lens. Apart from candidate-002's remaining underspecified Phase 7 exports, the corrected
frame and shadow protocols are implementable and mutually consistent.

The conformance audit found identifiable, semantically aligned mappings for the §4.4 frame flow,
Appendix A.1 program families, §7.1 hook needs 1–11, the seven-row reference timeline, the Phase 7
flag slice, and Appendix E rows 1–18. Candidate-003 is limited to the two omitted lifecycle traces;
the corresponding detailed design is present. No candidate was refuted or cleared on re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three admitted defects are correction-level and can be repaired without rebuilding the Phase 7
architecture. Candidates 001 and 002 require changes in the manifest-declared interface region, so
the interface change trigger applies and a fresh verification round is required after fix-up before
Phase 7 can close.

The trend is flat at three corrections in each of Rounds 1 and 2, but the findings have moved from
the first round's frame/shadow algebra and dependency-attribution defects to newly exposed identifier,
remaining-schema, and traceability defects. This is not yet literal convergence and cannot be
softened to PASS. The next required action is a scoped fix-up of this review, including a
`## Resolutions` record and Phase 7 addendum, followed by a fresh whole-document/interface review.

## Resolutions

### candidate-001 — corrected

Added a compact canonical constructor to `FailureId` that rejects null and blank identifiers; this
deliberately chooses the stricter blank rule requested by the finding. The adjacent interface prose
now binds dependency failures to verbatim ID preservation and forbids sanitization or added lifetime
semantics. A formulation sweep found no other Phase-7 diagnostic wrapper construction rule.

### candidate-002 — corrected

Expanded the consumer-facing §5.1 surface with callable operations and closed support/result schemas
for pipeline construction/status, dimension-cache snapshots, fullscreen execution and render-port
support, internal-pack metadata, reload request/status, readiness/finalized-frame capture, hook-report
access, and uniform-signal delivery. Added one shared paragraph binding validation, thread, snapshot,
retention, ownership, and failure rules. Dependency-owned descriptor internals and opaque credentials
remain referenced rather than duplicated; the new types expose only the information Phase 7 owns.

### candidate-003 — corrected

Added two §3.1 rows: one traces the distinct `(internal)` and `Off` paths to §4.7 and RESEARCH §4.1;
the other traces the per-dimension cache and pack/option/dimension/resolution rebuild triggers to §4.8,
RESEARCH, and the assignment's explicitly evidentiary Pintonium mechanism. No lifecycle behavior was
changed because the substantive design was already present.

### Notes deferred

None. The adjudication admitted no notes.
