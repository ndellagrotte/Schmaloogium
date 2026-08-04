# Phase 2 verification review — round 36

## 0. Method and reading order

I first re-derived every candidate from the manifest-selected whole target,
`docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target specification, document gate,
and mandatory template in `docs/design/v3/DESIGN.md`; the contract ground truth in
`docs/research/v1/RESEARCH.md`; the supporting CI evidence under `.github/workflows`; and the
binding contract in `docs/phase1/v14/PHASE_1_DOC.md`. Only after settling those judgments did I
read `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`docs/phase2/reviews/PHASE_2_REVIEW_35.md`, in round order, and compare the candidates with settled
findings and resolutions.

There were no reading-order deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. No
forbidden source was consulted. The Gate dropped candidate-004 because its quoted evidence did not
resolve; it is not reconsidered as a finding.

Candidate-005 identifies the same missing-motion defect as candidate-001. It is dropped as a
duplicate; candidate-001 states the governing doc-gate consequence and carries the controlling
severity. No prior review settled any of the surviving round-36 defects. Prior round 26's statement
that camera-path coverage was clean applied the then-selected v1.1 design, whereas this round's
explicit v3 override adds mandatory actual motion and therefore changes the governing premise.

## 1. Findings

### candidate-001 — The scene model explicitly omits mandatory camera-path motion

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:740–750`, with the field catalogue at lines 722–727
  and the repeated decision at lines 2137–2139.
- **Claim:** The architecture fails v3's explicit requirement and document gate for at least one
  moving-camera scene per motion-sensitive family.
- **Evidence:** The governing specification requires camera-path motion and says static-frame
  testing misses temporal defects involving `previous*` matrices, depth copies, motion vectors,
  TAA, and bloom (`docs/design/v3/DESIGN.md:1276–1284`); the document gate separately requires
  motion scenes for every motion-sensitive family (`docs/design/v3/DESIGN.md:1330–1334`). The
  target instead defines only fixed `pos`/`look`, warm-up, and capture-count fields
  (`docs/phase2/v1/PHASE_2_DOC.md:722–727`), expressly represents a path as static shots, forces
  previous-frame state to equal current state, and postpones actual motion to a possible future
  `[path]` extension (`docs/phase2/v1/PHASE_2_DOC.md:740–750`). Its decision log confirms the same
  narrowing (`docs/phase2/v1/PHASE_2_DOC.md:2137–2139`). No named run or equivalent mechanism
  restores moving-camera coverage.
- **Disposition:** Admitted. Define deterministic time-parameterized camera paths and capture
  sampling, classify motion-sensitive families, and provide at least one moving scene per such
  family with reproducible manifest and diff evidence.
- **Severity:** blocking
- **touches interface/change-trigger region: yes**

### candidate-002 — The implementation checklist reintroduces defaults into a default-free capture plan

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:2243`, against the binding plan description at
  lines 816–821.
- **Claim:** Repeated implementation instructions disagree about whether `CapturePlan` and its
  writer apply defaults.
- **Evidence:** The detailed design requires every value to be resolved before serialization and
  states that the plan and reader have no defaults (`docs/phase2/v1/PHASE_2_DOC.md:816–821`). The
  actionable checklist nevertheless assigns “with defaults” to `CapturePlan` + writer
  (`docs/phase2/v1/PHASE_2_DOC.md:2241–2244`). The §4.5.2 cross-reference does not remove that
  contradictory implementation direction.
- **Disposition:** Admitted. State that the runner resolves scene defaults and runner-owned facts
  before serialization and that the plan, writer, and reader perform no defaulting.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-003 — Golden update workflow is outside every declared interface change-trigger region

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:1442–1448`.
- **Claim:** A consumed Phase 1 contract is implemented outside all declared change-trigger
  regions, allowing a future semantic change to evade mandatory fresh verification.
- **Evidence:** Phase 1 requires explicit `-PupdateGoldens` regeneration, forbids automatic
  regeneration, and requires the regenerating run to fail
  (`docs/phase1/v14/PHASE_1_DOC.md:4243–4246`). Phase 2 implements those semantics in §4.11.5
  (`docs/phase2/v1/PHASE_2_DOC.md:1442–1448`), but the resolved
  `golden-format-sizing-and-adapter` region ends at line 1441 and the next region begins at line
  1450. Broad §5 prose does not expand these line-bounded triggers.
- **Disposition:** Admitted. Extend the golden interface region through §4.11.5 or declare that
  subsection as a separate interface region.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-006 — The conformance map omits both v3 reference-calibration requirements

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:417–490`, with the run catalogue at lines
  1213–1218.
- **Claim:** The map's “zero unmapped rows” assertion is false under the governing v3 override.
- **Evidence:** V3 requires shadow, sky, and weather coverage specifically because Pintonium cannot
  validate those families, while preserving the rule that Pintonium may inform but never define
  coverage (`docs/design/v3/DESIGN.md:1285–1289`). It separately requires the seven matrix packs'
  front-end parse successes and failures to be calibrated against observed Pintonium outcomes
  (`docs/design/v3/DESIGN.md:1303–1308`). The target's map claims complete coverage
  (`docs/phase2/v1/PHASE_2_DOC.md:417–423`) and incidentally includes the three scene families
  (`docs/phase2/v1/PHASE_2_DOC.md:483–490`), but records neither calibration rationale nor D-3's
  limitation. Its seven-pack golden run checks byte identity, and its explicit parse run checks
  round-trip identity; neither records or compares the required observed parse outcomes
  (`docs/phase2/v1/PHASE_2_DOC.md:1213–1218`).
- **Disposition:** Admitted. Map both v3 calibration obligations, preserve Pintonium as evidence
  rather than contract, and define a source-text-free headless calibration record or test for the
  seven packs' observed parse-success/failure expectations.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

Candidate-005 was cleared only as a duplicate of candidate-001, not because its premise was
refuted. Its conformance-map symptom and proposed interface work are subsumed by the admitted
blocking motion finding. Candidate-004 remains dropped at Gate for unverifiable evidence; no
finding is recreated from it.

The §0.35 registry-backed-capture correction is consistently propagated through scene validation,
lifecycle preflight, both wire schemas, and §5: `internal` and `OFF` remain headless-only and are
rejected before client capture. Apart from the specific §4.11.5 trigger gap, the declared regions
cover the detailed tier evidence, scenes, determinism, capture and manifest schemas, image
verdicts, baselines, oracle protocol, runs, fixtures, golden adapter, micro-packs, profiles, CI
policy, and §5 interface index. Accepted Phase 1 grants remain distinguished from unresolved
requests.

The App G fixture rows, T0–T3 tier rows, base RESEARCH.md §8.3 harness requirements, and §9
exit-criterion-to-run table are substantively mapped. The remaining conformance defects are the
v3-specific motion and calibration requirements absent from the older v1.1-authored surface.

## 3. Verdict

# FAIL
Counts: blocking=1; corrections=3; notes=0
Interface changed: yes

The missing moving-camera model fails an explicit v3 document gate and requires coordinated
redesign of multiple binding scene, determinism, capture, run, and evidence contracts. That is a
structural miss rather than a localized fix-up, so FAIL is required. Candidate-002 is a localized
non-interface correction; candidates-003 and -006 require interface/change-trigger changes.

There is no usable prior-round correction trend under this v3 override: the earlier rounds were
governed by the target's v1.1 declaration, and round 26's contrary clean statement therefore does
not demonstrate convergence against v3. Next required action: obtain human authorization for the
v3-driven rebuild, apply all four admitted dispositions, and record resolutions in this review.
Because binding interface regions must change, Phase 2 remains unverified and a fresh
whole-document verification round is required after the rebuild; do not roll the version while the
loop is open.
