# Schmaloogium — Phase 2: Conformance harness — Review Round 14

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially its conformance, fixture, and milestone
   requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially its
   `GLError`, drain-window attribution, and §5 contracts.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete target, `docs/phase2/v1/PHASE_2_DOC.md`.
6. Only after settling both candidates, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_13.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, steelman, and Gate material. The Gate reported no drops, and
no candidates were eliminated before adjudication. Forbidden sources were not read.

The prior reviews do not settle the admitted defects. Round 13 introduced the shared cache/run-root
establishment routine; candidate-001 tests the ordering between its directory creation and the
pre-existing Git-worktree refusal. Round 6 made `attributed` a required manifest field and defined
the reported count from it, while Rounds 8 and 11 settled that attribution never exempts an error
from T0. None established how Phase 7 obtains a reliable value for that required field from Phase
1's four-field `GLError` surface, which is candidate-002's distinct producer-contract question.

## 1. Findings

### candidate-001 — Shared root establishment creates an in-repository cache before refusal

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1113–1143`, with the structural claim at
  `docs/phase2/v1/PHASE_2_DOC.md:1150–1151` and test coverage at
  `docs/phase2/v1/PHASE_2_DOC.md:1633`.
- **Claim:** The shared root-establishment algorithm prevents creation of a cache root inside a
  Git worktree.
- **Evidence:** The algorithm resolves the configured root and then “creates missing cache and
  `runs` directories one component at a time”
  (`docs/phase2/v1/PHASE_2_DOC.md:1115–1118`). Only afterward, in a separate rule, does
  `FixtureResolver` walk that resolved root and its ancestors for `.git` and refuse an in-worktree
  root (`docs/phase2/v1/PHASE_2_DOC.md:1139–1143`). The later licensing argument makes the stronger
  assertion that packs are never in the repository “because the cache root cannot be in the repo”
  (`docs/phase2/v1/PHASE_2_DOC.md:1150–1151`), not merely that downloading is refused after empty
  directories are created. The failure table guarantees refusal only before download
  (`docs/phase2/v1/PHASE_2_DOC.md:1578`), and `FixtureCacheRootTest` checks refusal without requiring
  the rejected path to remain absent (`docs/phase2/v1/PHASE_2_DOC.md:1633`). An implementation
  following the specified sequence can therefore create the purported cache hierarchy inside the
  worktree before it fails.
- **Disposition:** Admitted. Perform the no-follow Git-worktree ancestry check from the nearest
  existing ancestor before creating any missing cache or `runs` component, and extend
  `FixtureCacheRootTest` to assert that refusal leaves the requested path absent.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the algorithm and its test can be corrected
  within §4.10.3 and §8 without changing the declared §5 region.

### candidate-002 — Required GL-error attribution has no producer in the consumed contract

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:763–789`, with mandatory-field semantics at
  `docs/phase2/v1/PHASE_2_DOC.md:815–817`, interface exposure at
  `docs/phase2/v1/PHASE_2_DOC.md:1433`, and the dependency contract at
  `docs/phase1/v14/PHASE_1_DOC.md:3977`.
- **Claim:** Phase 7 can serialize the required `gl_errors.<n>.attributed` boolean for every
  record using Phase 2's declared dependency inputs.
- **Evidence:** The run-manifest catalogue includes `attributed` in every `GLError` record
  (`docs/phase2/v1/PHASE_2_DOC.md:763`), the dense wire grammar requires that field
  (`docs/phase2/v1/PHASE_2_DOC.md:785–789`), and the validation rule makes it a required boolean
  on every record (`docs/phase2/v1/PHASE_2_DOC.md:815–817`). Phase 1's binding interface instead
  exposes `GLError` with only `op`, `subjectLabel`, `kind`, and `detail`
  (`docs/phase1/v14/PHASE_1_DOC.md:3977`; declaration at
  `docs/phase1/v14/PHASE_1_DOC.md:2880–2897`). Phase 2's `recordGL` cadence does not supply a total
  derivation: Phase 1 expressly says a single-call drain window may contain a foreign error and
  that replay, not the record, is what attribution rests on
  (`docs/phase1/v14/PHASE_1_DOC.md:2880–2887`). Phase 2 specifies neither a replay-result handoff
  to Phase 7 nor an additive dependency member carrying that classification. Consequently, a
  conforming Phase 7 producer cannot serialize the mandatory boolean reliably for batched,
  replay-clean, and foreign-error cases.
- **Disposition:** Admitted. Specify one total attribution source: either request an additive
  dependency contract with defined semantics for single-call, batched, replay-clean, and foreign
  errors, or define an explicit replay-result handoff to Phase 7. State the chosen source in §5.2
  and the Phase 7 capture/serialization request; do not infer it solely from `op` or
  `subjectLabel`.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — §5 exposes the required run-manifest field
  to Phase 7 for capture and serialization, and the repair must make that cross-phase producer
  contract implementable.

## 2. Checked and clean

The Round-13 canonical evidence-index, run-manifest, and attestation locations are consistent
across the ledger, cache layout, exposed interfaces, tests, and checklist. Their digest,
regular-file, and no-follow descendant-validation rules are otherwise coherent. The conformance
map substantively covers the Phase 2 scope, T0–T3 machinery, named runs, fixture policy,
before-renderer subset, milestone exit criteria, and OQ-10 spike. The module/C-4 boundary,
capability-profile serialization and placement, recording/replay mechanism, debug flags,
capture-plan schema, resource snapshot handoff, golden direction, scene contract, and test-task
split otherwise align with the selected Phase 1 contract and governing specification.

Both candidates survive independent re-derivation; neither was refuted or cleared. For
candidate-001, refusing before download does prevent re-hosting pack bytes, but it does not satisfy
the document's stronger claim that an in-repository cache root cannot be created. For
candidate-002, per-call cadence improves diagnostic naming but cannot distinguish a facade-caused
error from the foreign-error case the binding dependency explicitly admits. The Gate dropped none,
and no candidate-free finding is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded fix-up work rather than structural omissions requiring a rebuild.
Candidate-001 corrects Phase 2-owned filesystem ordering outside §5. Candidate-002 completes the
exposed run-manifest producer contract and therefore fires the interface change trigger.

The correction count has remained at two in Rounds 11, 12, 13, and 14. The loop is plateaued and
has not converged to literal PASS; this round tests newly specified root establishment and a
previously underspecified producer side of the manifest attribution field. The next required
action is a scoped fix-up resolving both findings and recording their resolutions in this review.
Because the exposed capture/run-manifest contract must change, a fresh verify round is required
before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Re-derived from the root-establishment sequence and the structural never-rehost claim. §4.10.3 now
finds the nearest existing ancestor and performs the no-follow `.git` ancestry check before it
creates any cache or `runs` component. `FixtureCacheRootTest` now requires a refused missing
in-worktree root to remain absent. Neighboring secure-stream, stable-identity, and descendant
containment rules are unchanged.

### candidate-002 — resolved

Re-derived against Phase 1's binding four-field `GLError` contract and its drain/replay semantics.
Phase 2 no longer implies that those four fields encode attribution. §5.2 states that the only
admissible source is an additive replay-aware Phase 1 result requested as R4A, with one total boolean
per drained error: true only for an error replay reproduces and isolates to the named facade
operation, false for replay-clean or still-batched/foreign windows. Phase 7 request R17 copies that
classification verbatim for single-call, batched, replay-clean, and foreign-error records and is
explicitly gated until Phase 1 accepts R4A; inference from `op` or `subjectLabel` is forbidden.

The §5 cross-phase interface changed, so a fresh verify round is required before Phase 2 can close.

### Notes deferred

None.
