# Phase 11 adversarial review — round 5

## 0. Method and reading order

I independently re-derived both gated candidates against the complete Phase 11 target, the
manifest-selected v3 governing design selectors, the selected RESEARCH.md authority, the binding
Phase 3 and Phase 6 contracts, and the relevant permitted supporting evidence. Only after settling
those interpretations did I read `docs/phase11/reviews/PHASE_11_REVIEW_1.md` through
`docs/phase11/reviews/PHASE_11_REVIEW_4.md`, including their resolutions.

I did not use the network, forbidden transcripts, or forbidden path patterns. I did not invoke the
verification harness, start another Codex process, or use agent fan-out. There were no reading-order
deviations, pre-adjudication eliminations, or Gate drops. Pre-existing worktree changes were
observed and left untouched.

## 1. Findings

### candidate-001 — The Round-4 addendum leaves the verification-state ruling stale

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:1240`–`:1241`.
- **Claim:** Current verification-state prose must consistently identify the completed review
  chronology and the latest §5-changing fix-up that triggered this fresh round.
- **Evidence:** The Round-4 addendum records that Round 4 reconciled the listed contracts and that
  its §5 lifecycle changes require another fresh verification round
  (`docs/phase11/v1/PHASE_11_DOC.md:115`–`:118`). Section 11.2 item 4 nevertheless says only Rounds
  1–3 “now exist,” identifies the Round-2 fix-up as the reason Round 3 was required, and calls the
  then-current work “this fix-up” (`docs/phase11/v1/PHASE_11_DOC.md:1240`–`:1241`). The item is
  labeled “Verification state,” uses current-relative language, and has no historical or
  supersession marker, so it conflicts with §0.7 rather than serving as a preserved historical
  ruling.
- **Severity:** correction. Update the ruling to state that Rounds 1–4 existed before this review,
  the Round-4 fix-up made the latest prior §5 change, and Round 5 was the fresh verification it
  required.
- **Touches interface/change-trigger region:** no.

### candidate-002 — The lifecycle handoff does not define when each reset reason must be forwarded

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:880`–`:920` and `:1002`–`:1012`.
- **Claim:** A composition consumer must be able to align every Phase 11 reset with Phase 6
  lifecycle events without inventing event mappings or old-state/new-state ordering.
- **Evidence:** Section 4.12 closes the Phase 11 reset-reason enum and specifies deactivation,
  inactivity until fresh activation, idempotence, and terminal `CLOSE`, but it gives no mapping or
  call boundary for pack replacement, shaders off, world epoch, framebuffer resize, GL context
  loss, or close (`docs/phase11/v1/PHASE_11_DOC.md:880`–`:920`). The binding composition handoff
  nevertheless requires consumers to forward resets “in the order specified by §4.12”
  (`docs/phase11/v1/PHASE_11_DOC.md:1002`–`:1012`). Phase 6 separately distinguishes generation
  adoption for `PACK_REPLACEMENT`, `SHADERS_OFF`, and `GL_CONTEXT_LOSS` from direct reset for
  `WORLD_EPOCH` and `CLOSE`, and fixes final-old-use/first-new-use boundaries
  (`docs/phase6/v1/PHASE_6_DOC.md:1382`–`:1384`). Those Phase 6 boundaries do not determine when the
  separately installed Phase 11 controller must reset, and Phase 11 additionally publishes
  `FRAMEBUFFER_RESIZE`. Different consumers can therefore choose incompatible sequences while
  claiming compliance with both contracts.
- **Severity:** correction. Add a binding lifecycle mapping in §4.12 and synchronize §§5.1 and
  5.5. For each applicable Phase 6 adoption/direct-reset event and framebuffer resize, identify the
  Phase 11 reason and its boundary relative to final old-state use, Phase 6 adoption/reset, and
  first new-state use; require successful fresh activation before customs participate in new-state
  use and define terminal close ordering without prescribing unrelated Phase 7 internals.
- **Touches interface/change-trigger region:** yes.

## 2. Checked and clean

The Round-4 addendum and status prose, §4.8 refresh ordering, §§4.9 and 4.11 diagnostic identity,
§4.12 tuple/random/reset lifecycle, the manifest-declared §5 interface region, the full §3
conformance surface and provenance, the governing Phase 11 scope/doc gate, and the binding Phase 3
and Phase 6 contracts were checked. Apart from the findings above, reset effects and the
`NoCustoms`-until-reactivation postcondition are internally consistent; diagnostic identity and
unsupported-backend handling are synchronized; function-surface attribution is sufficiently
distinguished from Phase 11's exact semantic decisions; and Phase 3/Phase 6 expression contracts
are accurately consumed.

No candidate was cleared on re-derivation. Reading Rounds 1–4 last confirmed that candidate-001 is
the same class of repeated current-state synchronization defect corrected in Round 3 but newly
reintroduced by the Round-4 addendum/fix-up chronology. Round 4 settled tuple deactivation and
fresh-random activation, but did not settle candidate-002's event-to-reason mapping or ordering;
its resolution therefore does not clear the present lifecycle handoff omission. Finder-reported
clean areas remain clean except where candidate-002's narrower consumer-ordering defect survives.
There were no eliminated candidates or Gate drops to revisit.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are bounded corrections rather than structural misses. Candidate-002
requires synchronizing the lifecycle contract in the declared §5 interface/change-trigger region;
candidate-001 does not.

Trend/convergence: correction counts are four, three, four, four, and two across Rounds 1–5. The
count improved this round, but the loop has not converged to literal PASS; repeated status
synchronization and a second-order lifecycle-interface omission remain.

Next required action: apply a scoped fix-up for this review, append resolutions, and conduct a
fresh Phase 11 verification round because the lifecycle correction changes §5 before Phase 11 can
close.

## Resolutions

### candidate-001 — applied

Re-derived the chronology from the target's §0 addenda and the current review. Section 11.2 now
states that Rounds 1–4 preceded this review, identifies the Round-4 fix-up as the latest prior §5
change, and identifies Round 5 as its required fresh verification. Added compact §0.8 to record this
round's two corrections and renewed verification obligation.

### candidate-002 — applied

Re-derived the event boundaries from Phase 11's tuple/reset ownership and Phase 6's binding
adoption/direct-reset contract. Section 4.12 now maps the three adoption reasons, `WORLD_EPOCH`,
Phase-11-only `FRAMEBUFFER_RESIZE`, and terminal `CLOSE`; it orders final old-state use, Phase 11
reset, Phase 6 adoption/reset where applicable, fresh activation, and first new-state custom use.
It also makes failed or absent activation a `NoCustoms` condition that cannot stall Phase 6.
Sections 5.1 and 5.5 were synchronized with that lifecycle contract. These intentional §5 edits
change the manifest-declared interface region and require a fresh verification round.

### Notes deferred

None; the adjudicator admitted no notes.
