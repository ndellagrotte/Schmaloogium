# Phase 3 Adversarial Review — Round 19

## 0. Method and reading order

I independently re-derived both surviving candidates from, in order:

1. `docs/phase3/v1/PHASE_3_DOC.md` in full, including the public configuration shape, directive
   conformance map, milestone decisions and checklist, and binding §5 interface region;
2. the selected Part I, Phase 3 specification, document gate, and mandatory-template material in
   `docs/design/v2.0-RC3/DESIGN.md`;
3. the relevant contract ground truth and milestone table in
   `docs/research/v1/RESEARCH.md`;
4. the binding interface region of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the permitted supporting evidence and supplied candidate records.

Only after settling both candidate dispositions did I read
`docs/phase3/reviews/PHASE_3_REVIEW_1.md` through
`docs/phase3/reviews/PHASE_3_REVIEW_18.md`, in round order and including their resolutions.
There were no deviations from the assigned reading order, no network use, no agent fan-out, and
no use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill,
I did not invoke the verification harness or start another session.

The Gate dropped `candidate-001` because its finder evidence at
`docs/design/v2.0-RC3/DESIGN.md:817-826` did not resolve uniquely. I did not reconstruct or admit
that candidate.

## 1. Findings

### candidate-002 — Record-component changes are incorrectly version-neutral

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1199-1204`

**Claim:** The published schema policy cannot make an added `PackConfiguration` component safely
compatible while retaining the same schema version.

**Evidence:** The public configuration is a closed Java record whose canonical shape includes
twelve named components (`docs/phase3/v1/PHASE_3_DOC.md:281-293`). Adding another component changes
the record's canonical constructor, accessor surface, and structural value semantics. The binding
version rule nevertheless says, “Adding an optional field is additive and does not bump the
version” (`docs/phase3/v1/PHASE_3_DOC.md:1199-1204`). The target defines no extension container,
serialization-only field layer, optional-component absence encoding, or default semantics that
would preserve the record shape. Exact rejection of schema values other than the producer's
`CURRENT_SCHEMA_VERSION` cannot signal a structural change when the producer continues publishing
version 1. This leaves dependent consumers without the exact interface semantics required by
`docs/design/v2.0-RC3/DESIGN.md:225-228`.

**Severity:** correction. Require a schema-version increment whenever the published
`PackConfiguration` record-component set changes, unless a shape-stable extension mechanism with
exact absence and default semantics is defined. The existing exact-version rejection policy may
remain if every structural change increments the version.

**Touches interface/change-trigger region:** yes.

### candidate-003 — `RENDERTARGETS` parsing is staged too early

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1482`

**Claim:** The implementation plan contradicts the authoritative post-v0.5 milestone for modern
`RENDERTARGETS`.

**Evidence:** RESEARCH places “`RENDERTARGETS` + >8 colortex” in the explicit post-v0.5 row
(`docs/research/v1/RESEARCH.md:946-951`). The governing design permits the full subsystem to be
architected now but requires each component to retain its authoritative implementation milestone
(`docs/design/v2.0-RC3/DESIGN.md:154-160`), and its modern S1 slice includes
`/* RENDERTARGETS */` (`docs/design/v2.0-RC3/DESIGN.md:754-758`). The target instead decides to
parse `RENDERTARGETS` at v0.1 (`docs/phase3/v1/PHASE_3_DOC.md:1482`) and maps that parser behavior
to the executable `directive_rendertargetsAndPrecedence` test
(`docs/phase3/v1/PHASE_3_DOC.md:583-585`). Checklist item 11 then schedules the complete routing
scanner and every §3.3 row at v0.1 (`docs/phase3/v1/PHASE_3_DOC.md:1564-1565`). A growth-shaped
routing representation may exist early, but recognition, precedence behavior, and its parser test
remain post-v0.5 work.

**Severity:** correction. Retain the growth-shaped routing type and conformance mapping, but tag
`RENDERTARGETS` recognition, precedence behavior, and its named parser test post-v0.5. Revise
D-P3-21 and split or narrow P3-C12/checklist item 11 so v0.1 covers only the legacy routing forms.

**Touches interface/change-trigger region:** no.

## 2. Checked and clean

- The Round 18 program-state correction is coherent across §4.8 and §5: repeated keys, ordering,
  OFF versus absence, profile-disable combination, Phase 4 backup-chain disposition, and Phase 5's
  flip-only projection agree.
- Phase 3's consumed Phase 1 contracts match the manifest-selected binding region. Capability,
  logging, diagnostics, debug, seam, licensing, and conformance inputs are published there, while
  the missing jcpp dependency allowance remains an explicit request rather than an assumption.
- Discovery/load lifecycle, internal-pack snapshots, materialization, declared-uniform metadata,
  geometry translation, resource requirements, and the remaining publication contracts retain
  substantive consumer semantics.
- The Appendix F and Appendix A.3 maps retain named coverage, the four mandated Pintonium pitfalls,
  option-3-shaped identity architecture, and the reserved `centerDepthSmooth` contributor.
- Prior reviews do not settle `candidate-002`: earlier schema corrections exposed the version and
  exact-version rejection policy, but none supplies a shape-stable extension mechanism or rules
  that make a changed public record component version-neutral.
- Prior reviews do not settle `candidate-003`: retaining complete future-facing architecture does
  not supersede RESEARCH's implementation milestone, and no earlier resolution reconciles the
  explicit v0.1 parser decision with the post-v0.5 schedule.
- Neither surviving candidate was refuted or cleared on independent re-derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both findings are bounded specification repairs rather than structural misses requiring a rebuild.
Round 18 had one correction and Round 19 has two distinct corrections, so the correction count has
increased and the current bytes have not converged to literal PASS.

The next required action is a scoped fix-up resolving `candidate-002` and `candidate-003` and
appending this review's `## Resolutions`. Because `candidate-002` requires changing the binding §5
cross-phase-interface region, the interface change trigger fires and a fresh verification round
is required before Phase 3 may close.

## Resolutions

### candidate-002 — applied

Re-derived from the closed `PackConfiguration` record and the governing requirement for exact
interface semantics. Section 5.3 now requires a schema-version increment for every change to the
published record-component set, including additions, as well as component removal, semantic
change, or executable-default change. A future exception requires a defined shape-stable extension
mechanism with exact absence and default semantics. The exact-version rejection policy remains.

### candidate-003 — applied

Re-derived from RESEARCH's explicit post-v0.5 milestone and the governing rule that future-shaped
architecture retains its implementation milestone. The §3.3 row and test, §4.7 recognizer,
milestone register, D-P3-21, and checklist now place `RENDERTARGETS` recognition and precedence at
post-v0.5. The growth-shaped routing representation remains in the architecture, while v0.1
implements only legacy `DRAWBUFFERS` routing.

### Notes deferred

None.

The §5 cross-phase-interface region changed, so the manifest trigger requires a fresh verification
round before Phase 3 can close.
