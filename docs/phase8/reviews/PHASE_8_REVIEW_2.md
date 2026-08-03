# Phase 8 — Shadow pass — Verification Review 2

## 0. Method and reading order

I independently re-derived all five Gate-surviving candidates against the whole target,
`docs/design/v2.0-RC3/DESIGN.md` Part I and the Phase 8 assignment/doc gate, the binding §5
contracts of Phases 4–7, and the cited permitted evidence. Only after settling those judgments did
I read `docs/phase8/reviews/PHASE_8_REVIEW_1.md`, including its resolutions, last.

There were no deviations from the resolved source contract. I did not use the network, invoke the
verification harness, start another Codex process, or fan out to agents. I did not read forbidden
sources. The Gate dropped candidate-005 because its finder quote did not resolve uniquely; it was
not adjudicated or recreated.

## 1. Findings

### candidate-001 — Invocation sequence assigns execution-view establishment to the wrong owner

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:548`–`:550`
- **Claim:** The corrected R8-1 authentication lifecycle is internally inconsistent about who
  opens the `ShadowExecutionView` and when.
- **Evidence:** Invocation step 6 says opening `ShadowStateLease` “establishes the
  Phase-7-granted `ShadowExecutionView`” (`docs/phase8/v1/PHASE_8_DOC.md:548`–`:550`). Yet the
  public `openState` operation consumes an existing view (`:255`–`:258`), the glue validates that
  borrowed Phase 7 view (`:357`–`:360`), and R8-1 makes Phase 7 the sole bridge issuer/owner whose
  driver opens and closes the bridge around `invoke` (`:1045`, `:1051`–`:1055`). The view must
  therefore already exist when step 6 runs; the lease can validate and use it but cannot establish
  it.
- **Severity:** correction. This is a bounded lifecycle contradiction in otherwise explicit
  ownership rules.
- **Required correction:** Revise step 6 to say that the lease validates and uses the already
  borrowed active `ShadowExecutionView` while installing the shadow camera and reversible state.
  Retain Phase 7 as the sole bridge opener and closer around `invoke`.
- **Touches interface/change-trigger region:** no.

### candidate-002 — Cleanup closes a borrowed binding snapshot that Phase 8 must not close

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:565`–`:568`
- **Claim:** The invocation cleanup contradicts the corrected R8-2 ownership and lifetime contract.
- **Evidence:** Step 13 directs Phase 8 to close “texture binding” in reverse-order cleanup
  (`docs/phase8/v1/PHASE_8_DOC.md:565`–`:568`). The only specified fixed shadow binding is the
  immutable, Phase-5-owned borrowed `ShadowBindingSnapshot`, which Phase 8 “neither closes it nor
  retains it” and which remains valid only until completion, abort, or invalidation (`:1075`–`:1078`).
  Texture-related GL state restoration is separately owned by `ShadowStateLease` (`:601`–`:608`,
  `:614`–`:618`), so the cleanup instruction cannot be reconciled as restoration of a distinct
  Phase-8-owned lease.
- **Severity:** correction. Removing the contradictory cleanup action conforms the transaction to
  the already declared ownership contract.
- **Required correction:** Remove texture binding from step 13. Limit explicit reverse-order
  cleanup to Phase-8-owned leases and state that the borrowed snapshot simply ceases to be usable
  when its shadow snapshot completes, aborts, or invalidates.
- **Touches interface/change-trigger region:** no.

### candidate-003 — Plan identity names an input absent from the public plan and fingerprint recipe

- **Location:** `docs/phase8/v1/PHASE_8_DOC.md:217`–`:220`, `:312`–`:317`, `:348`–`:351`,
  `:523`–`:526`
- **Claim:** `ShadowPlanFingerprint` cannot represent every value the lifecycle says constitutes
  plan identity.
- **Evidence:** `ShadowPlanInput` contains registry, policy, and hook health only
  (`docs/phase8/v1/PHASE_8_DOC.md:217`–`:220`), while `ShadowPlan` likewise exposes no resolved-
  configuration fingerprint (`:312`–`:317`). The canonical recipe hashes the registry fingerprint,
  every policy value, and hook fingerprint (`:348`–`:351`), but the lifecycle separately enumerates
  the resolved-configuration fingerprint in addition to those inputs (`:523`–`:526`). The target
  never declares that the policy projection is equivalent to that separately named fingerprint.
  Publication reuse and stale checks therefore cannot implement all stated identity requirements.
- **Severity:** correction. This is a consumer-visible identity inconsistency, but either coherent
  resolution is a bounded fix-up rather than a rebuild.
- **Required correction:** Choose one canonical identity contract: either carry the resolved-
  configuration fingerprint through `ShadowPlanInput`/`ShadowPlan` and its canonical hash, or
  remove the distinct fingerprint claim and explicitly define the complete effective policy as
  the configuration-derived identity.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

The corrected public shapes, authentication seam, Phase 5 operation algebra, provenance tags, and
forced-shadow conformance row from round 1 are otherwise present. The declared consumption of the
Phase 4–7 contracts remains honest about requested versus granted surfaces. Finder checks found no
additional defect in the new camera/frustum/hook-health shapes or the new conformance provenance.
The broader shadow directives, buffers, samplers, uniforms, camera behavior, traversal, render
order, depth split, PCF, mipmaps, blob suppression, clouds, and App-E-format hook ledger remain
substantively covered.

**candidate-004 is dropped as duplicative of candidate-003.** It cites the same public input, hash
recipe, lifecycle promise, consumer impact, interface classification, and alternative correction.
Admitting it separately would double-count one defect. Final disposition: dropped, severity none,
interface impact yes because the duplicated subject is the cross-phase plan contract.

**candidate-006 is dropped on re-derivation.** The mandatory §G9 map covers in-scope RESEARCH.md
§3/Appendix contract rows (`docs/design/v2.0-RC3/DESIGN.md:804`–`:806`), while the Phase 8 doc gate
separately requires added hook sites in App E format (`:2024`–`:2027`). Section 4.13 provides that
ledger, including traversal reuse and blob suppression (`docs/phase8/v1/PHASE_8_DOC.md:948`–`:958`).
The assignment bullet is thus fulfilled without a redundant meta-row in §3. Final disposition:
dropped, severity none, interface impact no.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Candidates 001–003 are admitted as corrections; none is blocking or a note. candidate-004 is
dropped as a duplicate of candidate-003, and candidate-006 is cleared on re-derivation. Compared
with round 1, the five prior corrections were applied, but three new consistency defects remain,
so the document has not converged to literal PASS. FAIL is not warranted because all three defects
are bounded fix-ups.

The candidate-003 resolution touches the declared §5 cross-phase plan contract if the fingerprint
is added; even the prose-only alternative directly settles that consumer-facing contract. The
interface change trigger therefore fires. Next action: perform the governed fix-up for this review,
append `## Resolutions`, add the next §0 addendum, and run a fresh verification round before Phase 8
can close.

## Resolutions

### candidate-001 — corrected

Re-derived ownership from the public `openState(camera, execution)` shape and R8-1: Phase 7 opens
and closes the execution bridge around `invoke`; Phase 8 only authenticates and uses the borrowed
active view. Invocation step 6 now says exactly that while retaining camera/state installation in
the Phase-8-owned lease.

### candidate-002 — corrected

Re-derived the lifetime distinction between Phase-8-owned reversible state and Phase 5's borrowed
fixed binding snapshot. Step 13 now closes only Phase-8-owned leases and states that the borrowed
binding becomes unusable on completion, abort, or invalidation; it no longer directs Phase 8 to
close Phase-5-owned binding state.

### candidate-003 — corrected

Selected the adjudicator's bounded prose-contract alternative. `ShadowPolicy` is now explicitly
the complete effective configuration-derived projection used for plan identity, and the lifecycle
no longer names a separate resolved-configuration fingerprint. The canonical fingerprint remains
registry fingerprint + every effective policy value + hook-health fingerprint. §5.1 exposes the
same identity contract, so the declared interface region changed and a fresh verify round is owed.

### Notes deferred

None. The adjudicator admitted no notes.
