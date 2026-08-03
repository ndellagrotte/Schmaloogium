## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited dependency evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_4.md`, in order and last. The prior reviews establish why the
current §5 surface requires fresh verification, but none previously identified or settled either
Round 5 candidate.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — The Phase 2 capture listener has no installation contract

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1212`–`:1217`
- **Claim:** The binding capture callback cannot be composed without Phase 2 and the Phase 7 driver
  independently guessing how the listener reaches the driver.
- **Evidence:** Phase 2 requires a frame-end hook after the final pass and before presentation
  (`docs/phase2/v1/PHASE_2_DOC.md:1541`–`:1546`). Phase 7 defines
  `FrameCaptureListener.onFinalized(FrameCaptureView)` and the borrowed view
  (`docs/phase7/v1/PHASE_7_DOC.md:1212`–`:1217`), requires an optional capture grab at that exact
  boundary (`docs/phase7/v1/PHASE_7_DOC.md:687`–`:693`), and exports the listener to the Phase 2
  capture agent (`docs/phase7/v1/PHASE_7_DOC.md:1305`–`:1306`). It specifies that the view expires
  when the listener returns (`docs/phase7/v1/PHASE_7_DOC.md:1282`–`:1288`), but nowhere supplies a
  registrar, construction dependency, or other installation rule. Static composition is a valid
  design, but leaving it unstated makes the cross-phase callback unusable as a binding contract.
- **Required correction:** Define the minimal listener-composition seam in §5.1. For example,
  require `FrameDriver` to receive zero or one listener at construction/bootstrap, define absence
  as capture-disabled, and invoke the installed listener exactly once for each successfully
  finalized frame before presentation. Specify render-thread execution, callback failure handling,
  and the existing borrowed-view lifetime. Add replacement/removal rules only if runtime mutation
  is actually supported, and connect H-CAPTURE-01 to this seam.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the missing installation semantics belong to
  the manifest-selected §5 contract.

### candidate-002 — `FrameReadiness` mis-types independent dependency generations

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1206`–`:1211`
- **Claim:** The readiness schema cannot truthfully identify the accepted Phase 4 and Phase 5
  publications because it types both dependency-owned generations as Phase 7's coordinated
  `PipelineVersion`.
- **Evidence:** Phase 7 defines `PipelineVersion` as its own lifecycle counter and expressly says
  that Phase 4 and Phase 5 generations remain independent authorities whose values are not claimed
  equal (`docs/phase7/v1/PHASE_7_DOC.md:518`–`:521`). Nevertheless, `FrameReadiness` declares both
  `registryVersion` and `bufferVersion` as `PipelineVersion`, while `FinalizedFrame.version` already
  carries the pipeline-level version (`docs/phase7/v1/PHASE_7_DOC.md:1206`–`:1211`). Phase 4's
  accepted registry publication exposes its own `long generation`
  (`docs/phase4/v1/PHASE_4_DOC.md:517`–`:521`), and Phase 5's accepted estate view exposes its own
  `long generation()` (`docs/phase5/v1/PHASE_5_DOC.md:553`–`:556`). The §5 table promises Phase 2
  the “accepted publication versions” (`docs/phase7/v1/PHASE_7_DOC.md:1305`), so interpreting the
  two fields as duplicate Phase 7 tokens contradicts the published semantics.
- **Required correction:** Make `FrameReadiness` carry the actual independent Phase 4 registry and
  Phase 5 estate generations, using unambiguous field names and types consistent with those
  dependency contracts. Keep `FinalizedFrame.version` as the separate Phase 7 `PipelineVersion`
  and state the readiness identity/equality check precisely. If readiness intentionally reports
  only the coordinated Phase 7 token, replace the misleading pair with one field and revise the
  interface summary and invariant accordingly.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — `FrameReadiness` is a consumer-visible schema
  in the selected §5 region.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. Round 4's draw-target correction now uses
Phase 5's payload-free `PassDrawTarget.Screen.INSTANCE` consistently and passes dependency-owned
targets unchanged. The corrected Appendix A.1 and hook-needs conformance tables provide row-local
authoritative provenance without creating a semantic inconsistency.

Phase 3 pack/internal-source consumption, Phase 4 registry/barrier consumption, Phase 5
frame/pass/depth consumption and explicitly flagged gaps, and Phase 6 runtime/ordering consumption
remain honestly represented. The synchronous Phase 8 shadow seam, reload algebras, token and
borrowed-view lifetimes, and downstream ownership hand-offs yielded no additional candidate-backed
defect. The conformance map provides identifiable coverage for the reviewed frame flow, program
dispatch, hook needs, reference-timeline dispositions, engine flags, and both assigned OQ spikes.
Both supplied candidates were confirmed; none was cleared or dropped on independent re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are correction-level omissions or contradictions in consumer-visible contracts and
can be repaired without rebuilding the Phase 7 architecture. Both require changes in the
manifest-declared cross-phase interface region, so a fresh whole-document/interface verification
round is required after fix-up before Phase 7 can close.

The correction trend is 3, 3, 4, 2, 2 across Rounds 1–5. Round 5 does not continue Round 4's
numerical improvement, and two new interface corrections remain, so the loop has not reached
literal convergence and cannot be softened to PASS. The next required action is a scoped fix-up of
this review, including its `## Resolutions` record and Phase 7 addendum, followed by fresh
verification of the changed interface and corrected whole document.

## Resolutions

### candidate-001 — applied

Section 5.1 now makes the capture listener an immutable optional `FrameDriver` construction
dependency: absence disables capture, and runtime replacement/removal is unsupported. It binds
H-CAPTURE-01 to exactly one render-thread invocation after a successful final pass and before
presentation, preserves expiration on listener return, and contains listener exceptions as
Phase-2-reported capture-feature failures without preventing frame commit or forcing shaders off.

### candidate-002 — applied

`FrameReadiness` now carries `long registryGeneration` from Phase 4's accepted
`PublishedRegistry.generation` and `long bufferEstateGeneration` from Phase 5's accepted
`BufferEstateView.generation()`. The text forbids comparing those independent equality tokens to
each other and keeps `FinalizedFrame.version` as the separate Phase 7 `PipelineVersion`.

### Notes deferred

None. The adjudicator admitted no notes.
