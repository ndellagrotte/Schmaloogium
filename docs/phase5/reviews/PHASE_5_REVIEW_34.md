# Phase 5 Verification Review — Round 34

## 0. Method and reading order

I independently re-derived both Gate-surviving candidates before consulting prior reviews. I read
the complete Phase 5 target as selected by the manifest, with focused rechecks of its header and
fix-up history, public planning/build signatures, main-depth preparation and resize paths,
manifest-declared §5 interface region, and downstream hand-off checklist. I compared those
surfaces with the selected RC3 mandatory template and Phase 5 assignment, the authoritative
research contract, and the binding §5 regions of Phases 1, 3, and 4. Supporting implementation
evidence was unnecessary because both candidates concern target-local metadata or public-contract
declaration.

Only after settling those interpretations did I read Phase 5 reviews 1 through 33 and their
resolutions. That last step was dispositive for candidate-002: Round 8 considered the same alleged
missing `BufferPlan.mainExtent()` declaration and expressly cleared it on the settled rule that the
two normative `plan.mainExtent()` call sites define the accessor even though the plan's
representation remains opaque. Round 29 separately established that a stale header pointer to a
superseded fix-up addendum is a correction, supporting rather than clearing candidate-001.

I used no network access, forbidden source, or prior-session transcript. In particular, I did not
open the supporting `*.txt` source barred by the resolved forbidden-source rule. There was no
agent fan-out or delegation. In accordance with the already-dispatched atomic role and the
verify-loop skill, I did not invoke the loop, run `scripts/verify`, or start another Codex session.
There were no deviations from the resolved reading contract, no candidates eliminated before
adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — Header points to §0.34 despite the newer §0.35 fix-up

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:13` and
`docs/phase5/v1/PHASE_5_DOC.md:305`–`:308`

**Claim.** The header's latest-revision metadata is stale: it identifies §0.34 although the
document contains the later §0.35 Round-33 fix-up.

**Evidence.** The header states, *"Last revised: 2026-08-03 (§0.34)"*
(`docs/phase5/v1/PHASE_5_DOC.md:13`). The revision history subsequently contains
`### 0.35 Round-33 fix-up` and records that Round 33 added the omitted public
`clearPlan(ClearRequest)` operation to binding §5
(`docs/phase5/v1/PHASE_5_DOC.md:305`–`:308`). The date remains current, but the addendum pointer
does not identify the actual latest revision.

**Required correction.** Change the header's last-revised pointer from §0.34 to §0.35, retaining
the existing date.

**Severity:** correction

**touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The Round-33 clear-contract repair
is consistently represented in the detailed clear protocol and binding §5: Phase 7 obtains an
immutable plan through `clearPlan(ClearRequest)` before passing it to `executeClear`, with the
existing effective-full-clear, exactly-once, validation, batching, and restoration semantics.
The conformance lens found no unresolved Phase 5 assignment or Appendix B coverage defect, and the
consumed Phase 1, Phase 3, and Phase 4 binding contracts remain consistently represented.

Candidate-002 is dropped against settled material. The target normatively requires Phase 7, after
planning and before candidate creation, to call `prepare(plan.mainExtent())`, and candidate
creation accepts only a ready snapshot whose extent equals the plan
(`docs/phase5/v1/PHASE_5_DOC.md:1218`–`:1224`). The resize-recovery path repeats the same accessor
and no-draw obligation (`docs/phase5/v1/PHASE_5_DOC.md:1264`–`:1268`), while the hand-off again
assigns `MainDepthSource.prepare(plan.mainExtent())` to Phase 7
(`docs/phase5/v1/PHASE_5_DOC.md:2191`–`:2195`). Calling `BufferPlan` opaque hides its
representation, not this expressly specified public accessor. Round 8 adjudicated and cleared
this same question; no changed target material or governing contract supplied in Round 34
invalidates that settled disposition. Repeating the accessor as a Java signature or in §5 could
improve discoverability, but it is not an admissible correction under that settled interpretation.

No admitted finding touches the manifest-declared interface region. No candidate other than
candidate-002 was dropped during adjudication, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

One localized metadata correction is admitted. It does not require structural rebuilding, so
`FAIL` is not warranted. Candidate-002 remains cleared under Round 8's settled disposition and
does not count.

Round 33 also reported one correction, and its fix-up corrected the binding interface; this fresh
round confirms that repaired surface but finds the new, non-interface stale-header defect.
Literal convergence has therefore not yet been reached. The next required action is a scoped
fix-up changing the header pointer to §0.35 and appending the resolution to this review. Because
the ordered correction does not touch §5 or another interface change-trigger region, this review
does not itself require a fresh interface verification round; closure still requires the normal
post-fix-up verification outcome prescribed by the loop.

## Resolutions

### candidate-001 — resolved

Updated `docs/phase5/v1/PHASE_5_DOC.md`'s last-revised metadata to the actual newest subsection.
Because the target-specific fix-up contract requires a compact next-numbered §0 subsection when
the target changes, this fix-up adds §0.36 and points the header to §0.36; pointing it to §0.35
while adding §0.36 would recreate the admitted stale-pointer defect immediately. The date remains
2026-08-03. No binding §5 or other interface-region content changed.

### Notes deferred

None. The adjudicator admitted no notes.
