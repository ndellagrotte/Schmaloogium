# Phase 5 Verification Review — Round 21

## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates before consulting prior reviews. I
read the Phase 5 target's planning, publication, shadow lifecycle, public signatures, binding §5
interface region, and handoffs; the RC3 Part I rules, mandatory template, Phase 5 specification,
and document gate; and the manifest-selected binding regions of Phases 1, 3, and 4. Supporting
material was not needed to decide these interface-completeness candidates.

Only after settling those interpretations did I read Phase 5 reviews 1 through 20, in numeric
order, to check the candidates against prior dispositions and settled resolutions. I used no
network access, forbidden source, or prior-session transcript. In particular, I did not open the
supplied `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden
source rule bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In
accordance with the dispatched atomic-role instruction and the verify-loop skill, I did not invoke
the loop, run `scripts/verify`, or start another Codex session. There were no deviations from the
resolved reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Normal no-shadow planning has no truthful shadow-result carrier

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:471`–`:473`,
`docs/phase5/v1/PHASE_5_DOC.md:1240`, `docs/phase5/v1/PHASE_5_DOC.md:1260`–`:1272`, and
`docs/phase5/v1/PHASE_5_DOC.md:1565`

**Claim.** A valid estate may intentionally omit the shadow framebuffer, but the total
`BufferEstateView.shadow()` contract can represent only an available estate or an unavailable
estate carrying a `BufferFailure`. It therefore conflates ordinary not-requested absence with
shadow-estate creation failure.

**Evidence.** `BufferSizing.shadowExtent` is empty exactly when no sfb is planned
(`docs/phase5/v1/PHASE_5_DOC.md:471`–`:473`), and §4.10 likewise says the sfb is absent unless
Phase 3 requests a shadow depth buffer (`docs/phase5/v1/PHASE_5_DOC.md:1240`). Nevertheless, the
complete result declaration permits only `ShadowEstateAvailable` and
`ShadowEstateUnavailable(BufferFailure reason, long estateGeneration)`
(`docs/phase5/v1/PHASE_5_DOC.md:1266`–`:1272`). The binding §5 row repeats those as the exhaustive
outcomes exposed to Phase 8 (`docs/phase5/v1/PHASE_5_DOC.md:1565`), while the closed failure-code
vocabulary has no truthful not-requested value (`docs/phase5/v1/PHASE_5_DOC.md:1520`–`:1534`).
Neutral fallback textures keep bindings operational but do not supply a truthful value for this
independently exposed total method.

**Required correction.** Add a distinct normal-absence result such as
`ShadowEstateNotRequested`, or a closed unavailability reason that separates `NOT_REQUESTED` from
`CREATION_FAILED`. Reserve `BufferFailure` for actual failure and mirror the exhaustive distinction
in §5.1.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-002 — Phase 8-facing shadow types are package-private

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1266`–`:1319` and
`docs/phase5/v1/PHASE_5_DOC.md:1565`

**Claim.** The detailed block calls the shadow family its complete public shape and binding §5
exposes that family to Phase 8, but the top-level result, carrier, view, snapshot, enum, and sealed
result-family declarations omit `public`. They are therefore inaccessible outside their package.

**Evidence.** The block begins, “`shadow()` and the available view have this complete public
shape,” but declares `sealed interface ShadowEstateResult`, package-private available/unavailable
records, and `interface ShadowEstateView` without public modifiers
(`docs/phase5/v1/PHASE_5_DOC.md:1266`–`:1275`). The remaining top-level shadow snapshot, enums,
and result-family interfaces in the same complete signature block likewise omit `public`
(`docs/phase5/v1/PHASE_5_DOC.md:1276`–`:1319`). Section 5 explicitly exposes these named contracts
to Phase 8 (`docs/phase5/v1/PHASE_5_DOC.md:1565`). No public enclosing type, same-package
constraint, or equivalent public declarations supply the required accessibility.

**Required correction.** Add `public` to every top-level type in the complete Phase 8-facing
shadow signature family. Member records nested in interfaces are already implicitly public and
need no redundant modifier. Keep the detailed declarations and §5.1 exposure consistent.

**Severity:** correction

**touches interface/change-trigger region: yes**

### candidate-003 — The pre-publication Phase 4 view is used without a binding dependency contract

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:611`–`:615` and
`docs/phase5/v1/PHASE_5_DOC.md:1601`–`:1648`

**Claim.** The required capability exists in Phase 4's detailed design, so the candidate's broad
title is overstated. A narrower interface-honesty defect survives: Phase 5 requires
`CompiledRegistryCandidate.view()` before Phase 4 publication, but Phase 4 §5 does not expose that
method as a binding cross-phase contract, and Phase 5 declares that no Phase 4 contract change is
requested.

**Evidence.** Phase 5 requires Phase 7 to build the Phase 5 candidate from the Phase 4 candidate's
read-only view before publishing Phase 4 (`docs/phase5/v1/PHASE_5_DOC.md:611`–`:615`). Phase 4's
detailed signature does define `public ProgramRegistryView view()` on
`CompiledRegistryCandidate` (`docs/phase4/v1/PHASE_4_DOC.md:414`–`:419`), so no new inspection
mechanism is needed. Its binding §5, however, exposes `ProgramRegistryView` through
`PublishedRegistry.registry` and describes the unpublished candidate as opaque without naming
`CompiledRegistryCandidate.view()` (`docs/phase4/v1/PHASE_4_DOC.md:1177`). Phase 5's consumed
Phase 4 table similarly names only `ProgramRegistryView.resolve` and related published-view
contracts (`docs/phase5/v1/PHASE_5_DOC.md:1601`–`:1613`), then states that no Phase 4 contract
change is requested (`docs/phase5/v1/PHASE_5_DOC.md:1648`). The mandatory template requires
consumed dependency changes to be flagged rather than silently assumed
(`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).

**Required correction.** Record a requested Phase 4 §5 clarification/addition exposing the
already-defined `CompiledRegistryCandidate.view()` as a non-owning pre-publication
`ProgramRegistryView` consumed by Phases 5 and 7, and update Phase 5 §§5.4–5.5 accordingly. Preserve
the existing candidate ownership and provenance rules; do not invent a new inspection API.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The Round-20 shadow additions
consistently include `PASS_ALREADY_OPEN`, and the snapshot fields and operation-result payloads are
otherwise mirrored between §4.10 and §5.1. The conformance map covers the in-scope Appendix B.1,
B.2, B.3, and B.4 requirements plus the RC3 flip, clear, depth, shadow, sizing, resize, growth, and
Final-handoff requirements. Phase 1 and Phase 3 consumption is explicit, including the three
honestly isolated Phase 1 change requests.

Candidate-003 is admitted only in its narrower form. The detailed Phase 4 declaration refutes the
claim that a pre-publication view mechanism is absent, but it does not cure the omission from the
binding dependency contracts. Round 16 settled Phase 5's cross-subsystem publication transaction
and expressly required use of the Phase 4 candidate's read-only view; it did not add that method
to Phase 4's binding §5 surface or flag the dependency-contract mismatch. No prior settled
material clears candidates 001 or 002; both arise from the concrete shadow declarations added by
Round 20's fix-up.

No candidate was dropped on independent re-derivation. There were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three admitted defects are localized interface corrections and do not require rebuilding the
Phase 5 architecture, so `FAIL` is not warranted. The recent correction counts are 1 → 2 → 2 → 3
for Rounds 18–21. The count has increased, and two findings expose incomplete closure of the
Round-20 shadow-interface fix-up; convergence has not been established.

The next required action is a scoped fix-up resolving candidates 001, 002, and 003 and appending
their resolutions to this review. Because every correction changes or requests reconciliation of
binding §5 cross-phase interface material, the `cross-phase-interfaces` change trigger applies:
Phase 5 owes a fresh verification round before it can close.

## Resolutions

### candidate-001 — applied

Added public `ShadowEstateNotRequested(long estateGeneration)` as the ordinary no-sfb outcome and
made `ShadowEstateResult` exhaustive over available, not-requested, and creation-failed states.
Updated §4.10 and binding §5.1 so ordinary absence never creates a `BufferFailure`.

### candidate-002 — applied

Added `public` to every top-level type in the complete Phase 8-facing shadow signature family.
Nested result records remain implicitly public. Binding §5.1 now states the accessibility rule.

### candidate-003 — applied

Added `CompiledRegistryCandidate.view()` to Phase 5's consumed Phase 4 contracts and requested that
Phase 4 expose its already-defined non-owning pre-publication view in binding §5. Preserved the
existing candidate opacity, ownership, compiler-origin credential, and publication provenance;
no new inspection API was invented.

### Notes deferred

None.

### §G1.3 status

All three admitted corrections were applied. The binding §5 interface region changed, so Phase 5
requires a fresh verification round before it can close.
