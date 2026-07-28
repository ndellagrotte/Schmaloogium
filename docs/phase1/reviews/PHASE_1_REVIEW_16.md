# PHASE_1_DOC.md — Verify session, round sixteen

## 0. Method and reading order

I first read the complete document under review,
`docs/phase1/v14/PHASE_1_DOC.md`, against the resolved governing selections in
`docs/design/v2.0-RC2/DESIGN.md` (Part I, the Phase 1 specification, the document gate, and the
mandatory template) and the cited contract ground truth in `docs/research/v1/RESEARCH.md`. I
re-derived both candidates from those sources and checked the relevant neighboring target text,
especially §0.15, §3, §5, and §11.4. There are no dependency documents for this target.

Only after fixing those independent dispositions did I consult the discovered prior reviews,
`docs/phase1/reviews/PHASE_1_REVIEW_1.md` through
`docs/phase1/reviews/PHASE_1_REVIEW_15.md`, last. They contain no settled finding or resolution that
clears either new defect. Round fifteen's PASS applies to the byte state before §0.15 introduced
the present surface.

There were no reading-order deviations. I used no network source, no forbidden source, and no
agent fan-out. This was the already-dispatched atomic Adjudicate role, so I did not invoke the
verification orchestrator or delegate. The Gate reported no drops; no candidate was eliminated
before adjudication.

## 1. Findings

### candidate-001 — The §3 conformance row maps absent `final` passthrough to fixed-function selection

**Location.** `docs/phase1/v14/PHASE_1_DOC.md:1482`.

**Claim.** The conformance row incorrectly groups absent `final` with fixed-function terminals and
maps the entire group to `ShaderService.useFixedFunction()`. That operation selects program zero;
it does not implement the passthrough copy required when `final` is absent.

**Evidence.**

- `docs/research/v1/RESEARCH.md:1139`–`:1140` distinguishes skipped absent composite passes from
  absent `final`, whose terminal is explicitly a “passthrough copy.”
- `docs/phase1/v14/PHASE_1_DOC.md:1482` labels external `<none>`, absent
  `shadow`/`gbuffers_basic`, and absent `final` together as fixed-function terminals, then maps that
  whole group to `ShaderService.useFixedFunction()` and program-zero selection.
- The document's own re-derivation at
  `docs/phase1/v14/PHASE_1_DOC.md:1247`–`:1251` correctly distinguishes the fixed-pipeline
  terminals from absent-`final` passthrough. That accurate statement does not cure the contradictory
  normative conformance mapping.

**Severity: correction.** A downstream implementer could replace the required copy with no-program
selection, but the defect is localized. Split the conformance entry: map only external `<none>` and
absent `shadow`/`gbuffers_basic` to `useFixedFunction()`, and state absent `final` separately as a
downstream-owned passthrough-copy terminal. No facade declaration need change.

**Touches interface/change-trigger region: no.** The defective mapping is in §3, outside the
manifest-declared §5 region, and the correction can preserve §5 and the existing facade operation.

### candidate-002 — Phase 2's derived-artifact workflow obligations are missing from binding §5

**Location.** `docs/phase1/v14/PHASE_1_DOC.md:3912`–`:4003`, contrasted with
`docs/phase1/v14/PHASE_1_DOC.md:4648`–`:4655`.

**Claim.** Phase 1 assigns Phase 2 concrete golden-artifact workflow obligations in §11.4 but omits
them from §5, even though §5 declares itself the binding, self-sufficient list of every obligation
placed on another phase.

**Evidence.**

- `docs/design/v2.0-RC2/DESIGN.md:250`–`:252` makes what a dependency exposes in §5 the contract a
  dependent builds against.
- `docs/phase1/v14/PHASE_1_DOC.md:3916`–`:3925` adopts that rule and expressly promises that every
  obligation this document places on another phase appears in §5.
- `docs/phase1/v14/PHASE_1_DOC.md:4648`–`:4655` says the REV2 clause “lands on” Phase 2 and requires
  that goldens contain no pack source text, rendered images never enter the repository, regeneration
  be explicit through `-PupdateGoldens`, and the regenerating run still fail.
- The Phase 2-facing entries within `docs/phase1/v14/PHASE_1_DOC.md:3912`–`:4003` expose golden
  serialization and replay facilities but do not state those workflow constraints. Their presence
  in §11.4 cannot substitute for omission from the binding interface.

**Severity: correction.** A Phase 2 build following the dependency contract can miss repository and
regeneration requirements. Add these derived-artifact obligations, narrowly phrased as Phase 2
consumer constraints, to §5; §11.4 may remain as rationale.

**Touches interface/change-trigger region: yes.** The necessary correction changes the declared
cross-phase-interface region. Its change trigger requires a fresh verify round before Phase 1 can
close.

## 2. Checked and clean

The new fixed-function surface was checked beyond candidate-001: the facade signature,
program-zero encapsulation, recorder operation name, replay assertions, live-backend obligation,
§5 publication, tests, milestone assignment, Phase 4 handoff, and checklist are mutually
consistent. No additional signature, handle, path, milestone, or consumer mismatch was established.

The interface sweep found Phase 1 correctly consumes no dependency contract. The facade services,
opaque handles, fixed-function addition, foreign-texture provider, GL-error protocol, non-verbs,
structural constraints, and convention rows otherwise appear in §5 with named consumers and
implementable detail. The conformance sweep found no additional admitted unmapped or unsupported
in-scope row.

Both supplied candidates survived re-derivation. No candidate was refuted or cleared, and there
were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both corrections are localized and do not require a structural rebuild, so FAIL is not warranted.
PASS is unavailable because two corrections survive.

The prior round was a literal PASS on the pre-§0.15 byte state. This round therefore does not show
failure to converge on previously reviewed material: a downstream-request fix-up introduced fresh
surface, and that surface has one conformance error plus one omitted binding handoff. Convergence
remains achievable by applying the two corrections without widening their scope.

Next action: run a fix-up for this review. Split the §3 terminal mapping and add the Phase 2
derived-artifact workflow constraints to §5. Because that second edit changes the declared
interface region, Phase 1 remains unverified and downstream use remains blocked until a fresh verify
round returns a literal PASS with zero blocking findings and zero corrections.

## Resolutions

### Corrections applied

| Candidate | Resolution |
|---|---|
| candidate-001 | **Applied.** Re-derived from `docs/research/v1/RESEARCH.md:1139`–`:1140` and the neighboring distinction already present in §0.15: absent `final` requires a passthrough copy, whereas external `<none>` and absent `shadow`/`gbuffers_basic` are fixed-pipeline terminals. The single §3 row was split. Only the latter terminals map to `ShaderService.useFixedFunction()`; absent `final` is now explicitly downstream-owned and forbidden from selecting fixed function merely because the shader is absent. No facade declaration or §5 operation changed. |
| candidate-002 | **Applied.** Re-derived from the governing dependency-contract rule and from §8.3/§11.4's existing Phase 2 assignment. The binding §5 Phase 2 note now requires goldens to contain no pack source text, keeps rendered images out of the repository in favor of manifests/hashes/provenance, and makes regeneration explicit through `-PupdateGoldens`, never automatic, while still failing the regenerating run. This changes the declared cross-phase interface region and fires its fresh-review trigger. |

The target also gains compact §0.16 provenance/status text, marks §0.15's live status historical,
updates the header and closing counts to sixteen verify sessions and thirteen fix-ups, and names
round seventeen as the next required verification. No directory roll was performed.

### Notes deferred

None. The adjudicator admitted zero notes.

### Refusals

None. Both corrections were supported by existing authority and required no new design decision.
