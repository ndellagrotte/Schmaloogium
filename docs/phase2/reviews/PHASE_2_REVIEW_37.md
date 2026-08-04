# Phase 2 verification review — round 37

## 0. Method and reading order

I first independently re-derived the gated candidate from the manifest-selected whole target,
`docs/phase2/v2/PHASE_2_DOC.md`; the governing Part I, Phase 2 specification, document gate, and
mandatory template in `docs/design/v3/DESIGN.md`; the contract ground truth in
`docs/research/v1/RESEARCH.md`; the binding §5 contract in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supplied supporting evidence. Only after settling that
judgment did I read `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`PHASE_2_REVIEW_36.md`, in round order, and compare the candidate against settled findings and
resolutions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. No
forbidden source was consulted. The Gate reported no drops, and no candidate was eliminated before
adjudication.

Prior round 14 settled the then-missing producer contract by requiring the replay-aware result that
Phase 1 later accepted as `[D-P1-42]`; round 17 settled that result's availability. Those resolutions
support rather than clear the present candidate: the rebuilt target correctly consumes the
replay-aware result in §5.2, but newly states in the tier interface that per-call cadence itself
makes attribution exact. No prior review settles that new contradiction.

## 1. Findings

### candidate-001 — Per-call error cadence is incorrectly described as making attribution exact

- **Location:** `docs/phase2/v2/PHASE_2_DOC.md:605–612`, against the correct dependency consumption
  at `docs/phase2/v2/PHASE_2_DOC.md:1781–1784`.
- **Claim:** The tier contract consistently distinguishes a drain record naming one facade call
  from replay-confirmed attribution.
- **Evidence:** The target says `recordGL`'s per-call cadence is “precisely the configuration in
  which ... attribution is exact,” grounding that statement in a one-call window yielding a record
  naming the call (`docs/phase2/v2/PHASE_2_DOC.md:605–609`). The binding dependency instead says a
  one-call facade window may still contain a foreign-context error and defines
  `ReplayAwareGLError.attributed=true` only when replay isolates the named call and the error recurs
  (`docs/phase1/v14/PHASE_1_DOC.md:4220–4221`). Phase 2 itself correctly says elsewhere that
  `ReplayAwareGLError` is the sole admissible source and that `op` and `subjectLabel` are
  non-evidence (`docs/phase2/v2/PHASE_2_DOC.md:1781–1784`). The later correct rule does not cure the
  contradictory implementation direction inside the independently consumable tier contract.
- **Disposition:** Admitted. Replace the exact-attribution claim with language that per-call cadence
  narrows facade-controlled drain windows and permits diagnostic naming of the sole facade call,
  but does not establish causation. Reserve `attributed=true` for Phase 6's replay-confirmed,
  isolated recurrence, while preserving that every recorded GL error fails T0.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the defect is within the declared
  `tier-definitions-and-evidence` region at lines 585–705.

## 2. Checked and clean

The governing Phase 2 scope, document gate, mandatory template, full target, and binding Phase 1
interface were checked. The conformance map covers the §9 exit criteria, all seven pack rows, T0–T3
gates, motion and reference-gap scenes, and the Pintonium calibration obligations. No additional
defect was found in the rebuilt motion schemas, fixture and golden interfaces, reporting contract,
identifiers, schema-major references, or closing status. The exposed interfaces otherwise
distinguish accepted Phase 1 contracts from unresolved requests and provide implementable schemas,
ownership boundaries, and change-trigger coverage.

Candidate-001 survives re-derivation. The nearby count of `attributed=false` records and §5.2's
correct replay-aware rule establish that the architecture has the required source, but they expose
rather than neutralize the tier text's contradictory claim. The defect is bounded wording with
consumer-visible diagnostic consequences, so correction severity is proportionate. No candidate
was refuted or cleared, the Gate dropped none, and no finding is created from the finder-reported
clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted defect is a localized consistency correction rather than a structural miss requiring
rebuild. Round 36 required a v3-driven rebuild; this round shows substantial convergence because
the rebuilt v2 target cures those structural findings, but it has not reached literal PASS while
this interface contradiction remains.

Next required action: apply candidate-001's narrow tier-language correction and record its
resolution in this review. Because a declared interface/change-trigger region must change, run a
fresh whole-document verification round before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against `docs/phase1/v14/PHASE_1_DOC.md` §5.2 and the target's existing §5.2
consumption. In `docs/phase2/v2/PHASE_2_DOC.md` §4.2.1, replaced the claim that per-call
`glGetError` cadence makes attribution exact. The tier contract now says that cadence narrows the
facade-controlled drain window and permits diagnostic naming of its sole facade call, but does not
establish causation. It reserves `ReplayAwareGLError.attributed=true` for Phase 6 replay that
isolates the named call and reproduces the error. The adjacent rule remains unchanged: every
recorded GL error fails T0 regardless of attribution. The §8 decision-index summary was swept to
carry the same no-causation boundary.

Added compact §0.37 and advanced the closing status to record that Round 37's correction is
applied and fresh whole-document Review 38 is required. The §4.2.1 edit intentionally changes the
declared `tier-definitions-and-evidence` interface region and therefore fires its manifest change
trigger.

### Notes deferred

None; the adjudicator admitted no notes.
