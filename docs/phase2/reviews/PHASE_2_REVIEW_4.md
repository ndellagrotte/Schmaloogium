# Schmaloogium — Phase 2: Conformance harness — Review Round 4

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading order
was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, including its conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The whole target, `docs/phase2/v1/PHASE_2_DOC.md`.
5. Only after settling both candidates, `docs/phase2/reviews/PHASE_2_REVIEW_1.md`,
   `docs/phase2/reviews/PHASE_2_REVIEW_2.md`, and
   `docs/phase2/reviews/PHASE_2_REVIEW_3.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents or other agent fan-out and did not invoke the verification
harness. The canonical engine supplied the finder, refuter, steelman, and Gate material. The Gate
dropped no candidates, and no candidates were eliminated before adjudication. Forbidden sources
were not read.

Round 3's resolution supplied the concrete run-manifest wire schema, but it settled syntax,
canonical encoding, and reader mechanics rather than the semantic completeness tested here. Neither
the earlier broad approval of T0 nor the full-block round-trip requirement defines encodings or
derivations for the missing T0 evidence or baseline identities. The present candidates are
therefore new-surface defects rather than repetitions of settled findings.

## 1. Findings

### candidate-001 — The run-manifest schema cannot encode every T0 predicate

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:415–424`, with the canonical schema at
  `docs/phase2/v1/PHASE_2_DOC.md:679–706`.
- **Claim:** `schmaloogium.run-manifest/1` contains sufficient evidence to evaluate all four T0
  predicates solely from the serialized manifest.
- **Evidence:** Section 4.2.1 requires the manifest to prove that a `PackConfiguration` was
  produced without fatal/error diagnostics and that the frame loop had no uncaught exception,
  `CompatVerdict.Bail`, shaders-off transition, or frame beyond the hang ceiling
  (`docs/phase2/v1/PHASE_2_DOC.md:415–424`). The exhaustive block catalogue and canonical field
  list provide diagnostics and frame counts/durations, but no front-end-completion state,
  exception state, compatibility verdict, shaders-active transition state, or applicable hang
  ceiling/verdict (`docs/phase2/v1/PHASE_2_DOC.md:679–706`). `run.exitStatus` has no normative
  value domain or mapping that derives these distinct conditions. Moreover, the normal lifecycle
  writes the manifest only after capture, while a timeout or earlier failure can prevent its
  emission (`docs/phase2/v1/PHASE_2_DOC.md:607–619`), contradicting the stronger claim that every
  T0 result is re-derivable solely from that artifact.
- **Disposition:** Admitted. Specify canonical serialized evidence and derivation rules for every
  T0 condition, including a coherent artifact/result rule for failures before normal manifest
  emission. Extend the canonical fixture and evaluator tests so each T0 failure mode is
  reconstructed from serialized evidence.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-002 — The run-manifest schema omits baseline invalidation identities

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:692–717`, with baseline use at
  `docs/phase2/v1/PHASE_2_DOC.md:821–855`.
- **Claim:** `schmaloogium.run-manifest/1` records the world and mod-set identities required to
  populate and validate baseline manifests.
- **Evidence:** The canonical schema's exhaustive required-field list contains per-mod records but
  no `worldSha256` or `modSetSha256`, and it defines no canonical aggregate hash derivation
  (`docs/phase2/v1/PHASE_2_DOC.md:692–706`). The immediately following world design nevertheless
  says the world-directory hash is recorded in the manifest
  (`docs/phase2/v1/PHASE_2_DOC.md:712–717`). Baseline manifests require both `worldSha256` and
  `modSetSha256` (`docs/phase2/v1/PHASE_2_DOC.md:821–832`), and invalidation compares those fields
  to produce `NO_BASELINE` (`docs/phase2/v1/PHASE_2_DOC.md:849–855`). Per-mod records might support
  a mod-set digest only if an exact canonical derivation were defined; they cannot supply the
  missing world-directory identity.
- **Disposition:** Admitted. Add canonical serialized world and mod-set identities, or define an
  exact canonical derivation for the mod-set identity while still serializing an unambiguous world
  identity. Require baseline approval and invalidation to consume those values, and cover them in
  the full-block fixture and tests.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

## 2. Checked and clean

The capture-plan schema is internally aligned with its field catalogue and validation rules. The
Phase 3/Phase 4 snapshot-enrichment gate is repeated consistently across detailed design, §5,
staging, and the checklist. The expanded §3 traceability rows resolve to substantive design and
test sites. No additional defect was found in the exit-criterion mapping, fixture-acquisition
split, golden update workflow, or OQ-10 fallback.

The dependency-consumption and interface sweep found no additional interface-honesty defect:
Phase 2 distinguishes existing Phase 1 contracts from requested additions and identifies its
consumer-facing contracts in §5. The conformance sweep found no unmapped in-scope requirement;
tier semantics, fixture policy, harness requirements, scene coverage, and §9 exit-criterion
mappings were substantively supported. Both current candidates survived independent
re-derivation; neither was refuted or cleared. The Gate dropped none, and no candidate-free finding
is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded schema and lifecycle corrections rather than structural misses requiring
a rebuild. Both affect the run-manifest wire schema exposed in §5 to Phases 4 and 7.

The loop has not converged: round 3 introduced the canonical schema while resolving three
corrections, and this fresh review of that new surface finds two semantic completeness corrections.
The next required action is a scoped fix-up resolving both findings and recording their resolutions
in this review. Because the exposed wire contract must change, a fresh verify round is required
before Phase 2 can close or its interface can be consumed as verified.

## Resolutions

### candidate-001 — resolved

Re-derived from the T0 table, lifecycle, schema catalogue, Phase 1 GL-error contract, and failure
posture. Added canonical front-end completion/configuration evidence and stable-loop evidence:
normative exit-status values, exception state, compatibility verdict, shaders-active state, timeout
state, and the hang ceiling. Section 4.5.4 now gives the complete artifact-only derivation for T0.

`CaptureRunner` now validates and atomically publishes agent output and synthesizes the same
canonical schema on launch failure, timeout, crash, malformed/truncated output, or earlier failure.
Unknown client evidence has explicit failing values, so an attempted run cannot disappear or depend
on live state. The full-block reader fixture, per-failure evaluator cases, and runner-synthesized
failure tests cover the new contract.

### candidate-002 — resolved

Re-derived from world caching, the environment block, baseline approval, and invalidation. Added
required `environment.worldSha256` and `environment.modSetSha256` fields with exact order-independent
canonical derivations. Baseline approval copies those run-manifest values unchanged, and
invalidation compares the current run values to the baseline values.

The full-block fixture now includes both identities. `BaselineIdentityTest` covers order
independence, input sensitivity, approval propagation, and `NO_BASELINE` on mismatch.

### Interface/change-trigger status

Section 5's run-manifest wire-schema row now exposes the added T0 evidence and baseline identities
and names Phase 4 as producer of its front-end evidence. The `cross-phase-interfaces` region changed,
so the manifest-declared fresh verify round is required before Phase 2 can close.

### Notes deferred

None. The adjudicator admitted no notes.
