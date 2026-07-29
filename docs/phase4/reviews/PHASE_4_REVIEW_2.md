## 0. Method and reading order

This round independently re-derived every supplied candidate against, in order:

1. the whole target, `docs/phase4/v1/PHASE_4_DOC.md:1-1274`;
2. the governing Part I, Phase 4 specification, document gate, and mandatory template in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the contract ground truth in `docs/research/v1/RESEARCH.md`;
4. the binding contracts in `docs/phase1/v14/PHASE_1_DOC.md:3944-4039` and
   `docs/phase3/v1/PHASE_3_DOC.md:890-1015`; and
5. the supplied supporting evidence where relevant.

Only after settling those interpretations was
`docs/phase4/reviews/PHASE_4_REVIEW_1.md` read. Its resolved findings establish the corrected
surface being reviewed but do not settle any finding below. There were no reading-set deviations,
no network use, no agent fan-out, no forbidden-source use, and no Gate drops. The canonical engine
had already dispatched this atomic adjudication role, so the verification harness was not invoked.

## 1. Findings

### candidate-001 — Publication provides no context with which to release the old barrier

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:357-376`,
  `docs/phase4/v1/PHASE_4_DOC.md:843-846`, and
  `docs/phase4/v1/PHASE_4_DOC.md:911-928`
- **Claim:** The publisher cannot perform its mandatory pre-replacement old-barrier release through
  the exposed API.
- **Evidence:** `ProgramRegistryPublisher.publish` accepts only a `RegistryPublication`;
  `Ready` carries the new registry and barrier, while `ShadersOff` carries only a failure
  (`docs/phase4/v1/PHASE_4_DOC.md:357-376`). The only old-barrier release operation is
  `releaseToFixedFunction(BarrierContext context)`
  (`docs/phase4/v1/PHASE_4_DOC.md:843-846`). Nevertheless, publication assigns release of the old
  barrier to the publisher before atomic replacement
  (`docs/phase4/v1/PHASE_4_DOC.md:911-928`). The document supplies no context argument, retained
  context invariant, or derivation rule. Section 5 exposes both operations to dependent phases
  (`docs/phase4/v1/PHASE_4_DOC.md:986-988`), and the governing gate requires a fully specified
  barrier interface (`docs/design/v2.0-RC3/DESIGN.md:1559-1563`).
- **Required correction:** Provide one complete, validated release route. Add the appropriate
  `BarrierContext` to the publication call/input (covering both ready and off replacement), or
  define a context-free release operation with sufficient semantics. Mirror validity, caller
  duties, and focused publication tests in §5 and §8.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

### candidate-002 — Unsafe old-barrier release is exposed as an unchanged, usable publication

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:833-846`,
  `docs/phase4/v1/PHASE_4_DOC.md:877-885`,
  `docs/phase4/v1/PHASE_4_DOC.md:911-933`, and
  `docs/phase4/v1/PHASE_4_DOC.md:1175-1188`
- **Claim:** A release failure cannot uniformly preserve the old publication and generation as
  authoritative because some release outcomes forbid continued shader-path use.
- **Evidence:** `releaseToFixedFunction` returns the same closed `BarrierResult` algebra that
  includes `ShadersOff` and `FailedSafe` (`docs/phase4/v1/PHASE_4_DOC.md:833-846`).
  `FailedSafe` means restoration could not be proved, forbids drawing through the shader path,
  and requires immediate shaders-off/vanilla recovery
  (`docs/phase4/v1/PHASE_4_DOC.md:877-885`). Publication performs release before replacement, but
  maps any release failure to `Rejected` while leaving `current` and generation unchanged
  (`docs/phase4/v1/PHASE_4_DOC.md:911-933`). That can leave consumers observing the same generation
  and old registry after its barrier has become unusable. The failure ladder independently
  requires shaders-off recovery when safe restoration fails
  (`docs/phase4/v1/PHASE_4_DOC.md:1067-1068`), while §8 has no test for post-release or partial
  release failure (`docs/phase4/v1/PHASE_4_DOC.md:1175-1188`).
- **Required correction:** Distinguish validation rejection before release from outcomes after
  release begins. Preserve unchanged `current` only when the old publication is provably usable;
  define an observable off/recovery transition for `FailedSafe` or partial restoration, including
  generation, ownership, close/quarantine, candidate disposition, and caller duties. Mirror the
  state machine in §5 and test each release outcome.
- **Severity:** correction
- **Touches interface/change-trigger region:** yes

Candidate-003 is dropped as a duplicate of candidate-002. Both identify the same contradiction
between failed old-barrier release and ordinary rejection with unchanged publication/generation,
and both require the same release-aware publication state machine. It does not warrant a second
counted finding.

### candidate-004 — Two conformance rows cite ranges that do not support their complete claims

- **Location:** `docs/phase4/v1/PHASE_4_DOC.md:419-420`
- **Claim:** The lifecycle and ordered compile-flow rows do not have semantically complete
  provenance.
- **Evidence:** The uninitialization row cites
  `docs/research/v1/RESEARCH.md:489-491`, but deletion is stated at line 488; the cited range
  contains only the trigger continuation and next heading. The ordered
  compile → attach → bind → link → validate row cites
  `docs/research/v1/RESEARCH.md:497-505`, which supports preprocessing, fixed pre-link attribute
  binding, validation failure, fallback, and barrier behavior, but not compile, attach, link, or
  their ordering. The governing Phase 4 specification does establish the complete sequence at
  `docs/design/v2.0-RC3/DESIGN.md:1511-1514`. Equivalent detailed design means this is a narrow
  traceability defect, not a missing architecture.
- **Required correction:** Expand the lifecycle citation to include
  `docs/research/v1/RESEARCH.md:488`, and replace or supplement the compile-flow provenance with
  `docs/design/v2.0-RC3/DESIGN.md:1511-1514`.
- **Severity:** correction
- **Touches interface/change-trigger region:** no

## 2. Checked and clean

The following examined areas produced no additional admitted finding:

- The prior round's corrected public API, stage traversal, barrier result taxonomy, publication
  ownership, generation protocol, registry-wide diagnostics, §5 interface table, and focused test
  plan were rechecked.
- Immutable stage traversal remains coherent; both gbuffers occurrences reject compute
  descriptors; registry-wide failure aggregation remains deterministic.
- Phase 1 facade operations and result types used by Phase 4 are exposed, and Phase 3
  configuration/materialization contracts are consumed without invented geometry support.
- The barrier preserves the semantic distinction between `ShadersOff` and `FailedSafe`; the
  defect is specifically publication's failure to preserve that distinction after old-barrier
  release.
- The conformance map otherwise covers the modern and G6 stage shapes, sparse 0…99 families,
  compute placeholders and gbuffers exclusion, Appendix A.1 catalog rows, Appendix A.2 fallback
  semantics, Appendix A.3 state, fixed attributes, barrier duties, and generation invalidation.
- Candidate-003 was cleared as duplicative, not substantively refuted: candidate-002 fully captures
  its location, unsafe state, interface impact, and required correction.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

The three admitted defects are bounded corrections rather than structural omissions requiring a
rebuild. Literal PASS is unavailable while they remain.

Compared with round 1, corrections decreased from five to three, but two new defects are in the
publication/barrier interface added by that round's fix-up. This is progress without convergence.
The next required action is a scoped Phase 4 fix-up resolving candidates 001, 002, and 004,
appending this review's `## Resolutions`, and adding the required Phase 4 document addendum.
Because candidates 001 and 002 change the `cross-phase-interfaces` change-trigger region, a fresh
verification round is required before Phase 4 can close.

## Resolutions

### candidate-001 — applied

Re-derived against the barrier signature and publication ownership rules. The publication API now
requires a current `BarrierContext` for both ready and shaders-off replacement, validates it before
release, and passes it unchanged to `releaseToFixedFunction`. Section 5 exposes that required
context, and §8 covers both publication forms.

### candidate-002 — applied

Publication now has two explicit state transitions. Pre-release validation failure returns
`Rejected` with the usable old publication and generation unchanged. Once release begins, only
`FixedFunction` permits the requested replacement; `ShadersOff`, `FailedSafe`, exception, partial,
or protocol-invalid release instead publishes observable empty `RecoveredOff`, increments once,
quarantines and closes the old registry, leaves a ready candidate caller-owned, and requires
immediate vanilla-path recovery. Section 5 mirrors the result/ownership contract, and §8 tests the
release outcomes and candidate/old-registry duties.

### candidate-004 — applied

The lifecycle citation now includes `docs/research/v1/RESEARCH.md:488`. The ordered compile-flow row
now cites the governing sequence at `docs/design/v2.0-RC3/DESIGN.md:1511`–`:1514` and retains the
research range only for its supported failure/barrier evidence.

### Notes deferred

None. The adjudicator admitted no notes, and no correction required a new governing decision or
an upstream change.
