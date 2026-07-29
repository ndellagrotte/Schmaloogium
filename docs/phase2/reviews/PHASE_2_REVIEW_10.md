# Schmaloogium — Phase 2: Conformance harness — Review Round 10

## 0. Method and reading order

I independently re-derived all three gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially its conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete selected target, `docs/phase2/v1/PHASE_2_DOC.md`.
6. Only after settling every candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_9.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, and Gate material. The Gate reported no drops, and no
candidates were eliminated before adjudication. Forbidden sources were not read.

The prior reviews do not settle the present defects. Round 9 introduced and then resolved the
complete resource wire grammar and the scene-set evidence index. Candidate-001 and candidate-002
test newly added details of those resolutions rather than reopening the settled omissions.
Candidate-003 tests producer ownership left unresolved by the new live resource block.

## 1. Findings

### candidate-001 — Instance records have an impossible secondary sort key

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:769–772`.
- **Claim:** The completed resource wire grammar defines an implementable canonical order and
  uniqueness rule for both repeated record types.
- **Evidence:** The target defines vertex-attribute records as `{program,name}` and instance
  records as `{program,count}`, then requires “Both” to be sorted by program then name and to have
  no duplicates (`docs/phase2/v1/PHASE_2_DOC.md:769–772`). An instance record has no `name`
  field. The corresponding sizing representation is keyed only by program
  (`docs/phase2/v1/PHASE_2_DOC.md:1190`). A producer therefore cannot literally implement the
  exposed canonical order, and consumers may choose incompatible substitute rules.
- **Disposition:** Admitted. Define vertex-attribute ordering and uniqueness by `(program,name)`,
  and instance ordering and uniqueness by `program`; retain the positive-integer constraint on
  instance counts.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — One evidence-index row per scene cannot retain T3 A/B evidence

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:498–515`, `527–533`.
- **Claim:** The scene-set evidence index can retain complete evidence for T3 feature checks.
- **Evidence:** An automated T3 feature check requires off/on captures of the same scene and shot,
  with both states passing their own T1 baselines
  (`docs/phase2/v1/PHASE_2_DOC.md:498–515`). Pack options are fixed for a capture run and one
  manifest is published after its shots (`docs/phase2/v1/PHASE_2_DOC.md:650–654`), so the two
  states require distinct run manifests. The sole hash-bound `evidence.index` instead permits
  exactly one `{sceneId,runId,manifestSha256}` row per constituent scene and rejects duplicate
  scene rows (`docs/phase2/v1/PHASE_2_DOC.md:527–533`). No feature/variant evidence structure in
  the run-manifest grammar supplies the missing reachability.
- **Disposition:** Admitted. Permit deterministically keyed multiple evidence records per scene,
  including feature/variant and run identity, while separately enforcing exact scene-set
  coverage. Define T3 completeness to retain both option-state manifests and applicable manual
  attestations.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-003 — Required live resource data has no honest producer contract

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md` §§5.1 and 5.4.
- **Claim:** The cross-phase interface assigns every required run-manifest field to a phase that
  owns and can expose the underlying runtime data.
- **Evidence:** Phase 2 assigns buffer decisions to Phase 5, separately from Phase 3's pack
  front-end and Phase 4's per-slot program resolution
  (`docs/phase2/v1/PHASE_2_DOC.md:144–148`). The manifest requires live resource-sizing decisions
  (`docs/phase2/v1/PHASE_2_DOC.md:724–725`), but §5 says Phase 4 produces “the slot, front-end,
  and resource data” (`docs/phase2/v1/PHASE_2_DOC.md:1385`). The onward requests obtain
  front-end data from Phase 3 and program resolution from Phase 4
  (`docs/phase2/v1/PHASE_2_DOC.md:1456–1471`), yet make no request to Phase 5 for the required
  runtime resource snapshot. Phase 7 can serialize observations, but no contract gives it an
  honest source for Phase-5-owned state.
- **Disposition:** Admitted. Split producer obligations by owner: Phase 3 supplies front-end and
  pack-configuration evidence, Phase 4 supplies per-slot program resolution, Phase 5 exposes an
  immutable runtime snapshot covering every canonical `resources.*` field and absence rule, and
  Phase 7 captures and serializes those observations. Add the Phase 5 request and align §11.4.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The resource availability and absence rules, capability-shortfall encoding, GL-error attribution
derivation, world-tree hashing rules, and scene-set identity invalidation were internally
consistent. The capture-plan and other run-manifest blocks remain sufficiently detailed to
implement across the process boundary. Phase 2's direct Phase 1 consumptions—module and seam
constraints, `GLCapabilityProfile` serialization and fixtures, recording/replay types, debug
flags, logging, and the CI extension point—are represented in the selected binding contract.

The conformance map substantively covers the matrix packs, T0–T3 semantics, harness requirements,
scene families, fixture constraints, and milestone exit criteria. Supporting CI inspection
introduced no candidate relevant to this round. All three candidates survived independent
re-derivation; none was refuted or cleared. The Gate dropped none, and no candidate-free finding
is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are bounded fix-up work rather than structural misses requiring a rebuild.
Each affects a consumer-facing promise in the declared cross-phase interface: two repair the newly
exposed evidence/wire contracts, and one repairs ownership of the runtime data feeding that wire
contract.

The loop has not converged. Round 9 admitted two corrections, and Round 10 admits three on the
newly completed surfaces; the correction count increased rather than reaching literal PASS. The
next required action is a scoped fix-up resolving all three findings and recording their
resolutions in this review. Because the cross-phase interface must change, a fresh verify round is
required before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Re-derived the two record shapes from §4.5.4 and the `[sizing]` representation. The document now
orders and deduplicates vertex attributes by `(program,name)` and instances by `program`; the
positive instance-count rule remains unchanged.

### candidate-002 — resolved

Re-derived T3's evidence needs from its automated A/B predicate, manual alternative, and the
ledger's independent scene-set coverage duty. `evidence.index` now permits deterministic multiple
records per scene keyed by scene, feature, variant, and run. One `PRIMARY` record per constituent
scene separately enforces exact coverage; `FEATURE_OFF`/`FEATURE_ON` retain both passing T1
manifests, and `MANUAL` retains the applicable attestation hash.

### candidate-003 — resolved

Re-derived ownership from §1.2 and separated decision production from observation serialization.
The run-manifest interface now assigns front-end and pack evidence to Phase 3, program resolution
to Phase 4, the immutable live `resources.*` snapshot and absence rules to Phase 5, and capture plus
serialization to Phase 7. New request R10A and §11.4 carry the Phase 5 obligation onward.

### Notes deferred

None.

The declared cross-phase interface changed. A fresh verify round is required before Phase 2 can
close.
