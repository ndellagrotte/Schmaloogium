# Schmaloogium — Phase 2: Conformance harness — Review Round 11

## 0. Method and reading order

I independently re-derived all three gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially the authoritative conformance-tier requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5 and
   its GL-error attribution contract.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete target, `docs/phase2/v1/PHASE_2_DOC.md`.
6. Only after settling every candidate, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_10.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, and Gate material. The Gate reported no drops, and no
candidates were eliminated before adjudication. Forbidden sources were not read.

The prior reviews do not settle the admitted defects. Round 10 introduced the richer evidence
index that candidate-002 tests, but its resolution did not define how an attestation hash resolves
to bytes. Earlier review material discussed unattributable GL errors, but the current authoritative
comparison still leaves the direct T0 contradiction identified by candidate-003. Candidate-001 is
cleared on re-derivation because the full evidence grammar always requires a non-manual `PRIMARY`
record for each constituent scene; consequently every recorded tier does retain run-id and
manifest-hash evidence even when its additional `MANUAL` record uses only an attestation hash.

## 1. Findings

### candidate-002 — Manual T3 evidence has no deterministic artifact locator

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:532–547`.
- **Claim:** A consumer can retrieve and hash-validate the sign-off artifact named by a manual T3
  evidence record without inventing a storage convention.
- **Evidence:** The evidence grammar permits a `MANUAL` record whose non-empty
  `attestationSha256` identifies the sign-off artifact while its manifest fields are empty when no
  capture applies (`docs/phase2/v1/PHASE_2_DOC.md:532–544`). The record has no path field, canonical
  filename, or content-addressed location rule. The only general artifact-path grammar is for
  captured PNGs—`<runId>/<packId>@<version>/<sceneId>/<shot>.png`
  (`docs/phase2/v1/PHASE_2_DOC.md:428`)—and cannot locate a runless attestation. Nevertheless,
  `TierLedgerTest` promises that missing and hash-mismatched evidence yields effective
  `NOT_ATTEMPTED` (`docs/phase2/v1/PHASE_2_DOC.md:1608`), which cannot be implemented for the
  attestation until its hash resolves deterministically to bytes.
- **Disposition:** Define one deterministic, artifact-root-contained locator for every manual
  attestation—such as a normalized relative path, unique canonical filename, or
  content-addressed location—and require the resolved regular file's bytes to match
  `attestationSha256`. Extend `TierLedgerTest` for missing and hash-mismatched attestations, plus
  escape or ambiguity cases if the chosen locator can express them.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-003 — T0 permits recorded GL errors despite the authoritative no-error gate

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:449–478`, repeated in the evaluator contract at
  `docs/phase2/v1/PHASE_2_DOC.md:1604–1605`.
- **Claim:** The Phase 2 T0 decision procedure faithfully implements the authoritative requirement
  that a passing run have no GL errors.
- **Evidence:** RESEARCH defines T0 as “pack parses, programs compile, no GL errors, stable frame
  loop” without an attribution exception (`docs/research/v1/RESEARCH.md:913–916`). Phase 2 instead
  fails the predicate only for a `GLError` attributed to a facade call
  (`docs/phase2/v1/PHASE_2_DOC.md:455–460`) and explicitly makes surviving unattributable errors
  warnings that never fail T0 (`docs/phase2/v1/PHASE_2_DOC.md:475–478`). Phase 1 establishes that
  attribution can be unavailable because the GL flag is per-context while cadence tracks facade
  calls (`docs/phase1/v14/PHASE_1_DOC.md:3977`); it does not redefine the authoritative T0 gate.
  The target's tests deliberately preserve this divergence by treating attributed errors as
  failures and deriving unattributable warning counts
  (`docs/phase2/v1/PHASE_2_DOC.md:1604–1605`). Section 5 exposes the tier decision procedures to
  all behavioural phases (`docs/phase2/v1/PHASE_2_DOC.md:1390–1393`).
- **Disposition:** Make every recorded GL error fail T0 and update the evaluator, tests, reporting
  semantics, and exposed tier contract. If unattributable errors are intentionally exempt, record
  the conflict and request upstream clarification instead of silently narrowing the source-of-truth
  predicate.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The Round-10 evidence-index changes otherwise preserve exact constituent-scene coverage through one
required `PRIMARY` record per scene and retain distinct automated `FEATURE_OFF` and `FEATURE_ON`
manifests. The resource wire grammar, record ordering, Phase 5 resource-snapshot ownership, direct
Phase 1 consumptions, capture-plan schema, and the remaining run-manifest blocks were internally
consistent in the examined areas. The conformance sweep otherwise found the Appendix G pack rows,
T1–T3 mappings, harness requirements, scene families, OQ-10, and milestone exit criteria
substantively covered. Supporting CI inspection introduced no candidate relevant to this round.

Candidate-001 is dropped. Its decision-log sentence says a tier is recorded only with a run id and
manifest hash (`docs/phase2/v1/PHASE_2_DOC.md:1871`). Although compressed, it remains true of the
complete evidence index: exactly one `PRIMARY` record is mandatory for every constituent scene
(`docs/phase2/v1/PHASE_2_DOC.md:535–539`), and every non-manual record requires a run id and
manifest hash (`docs/phase2/v1/PHASE_2_DOC.md:542–544`). The manual attestation is additional
evidence, not a replacement for those mandatory primary records. Rewriting the row to imply that an
attestation alone can support the whole tier would be less accurate. Candidates 002 and 003 survive
independent re-derivation. The Gate dropped none.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted findings are bounded fix-up work rather than structural omissions requiring a
rebuild. Candidate-002 completes manual-evidence reachability outside the declared §5 region.
Candidate-003 changes a tier predicate that §5 expressly exposes by reference, so it fires the
cross-phase interface change trigger.

The correction count declined from three in Round 10 to two in Round 11, but literal convergence
has not been reached because two corrections remain. The next required action is a scoped fix-up
resolving candidates 002 and 003 and recording their resolutions in this review. Because the
exposed tier semantics must change, a fresh verify round is required before Phase 2 can close.

## Resolutions

### candidate-002 — resolved

Defined the manual attestation as the content-addressed regular file
`<artifact-directory>/attestations/<attestationSha256>.attestation`. Resolution rejects links and
non-regular entries, stays beneath the existing row artifact directory, and validates the file
bytes against the lowercase SHA-256 in the filename. `TierLedgerTest` now covers missing,
hash-mismatched, linked, and non-regular attestations. This closes deterministic reachability
without expanding the evidence-index record grammar.

### candidate-003 — resolved

Re-derived RESEARCH.md §8.2's unqualified “no GL errors” predicate and removed the attribution
exemption. Every recorded `GLError` now fails T0; `attributed=false` remains diagnostic information
and a separately reported count, not a pass exception. The evaluator test, OQ-10 real-smoke
criterion, decision rationale, and exposed §5 tier contract now use the same rule. This changes the
cross-phase interface and requires a fresh verify round.

### Notes deferred

None.
