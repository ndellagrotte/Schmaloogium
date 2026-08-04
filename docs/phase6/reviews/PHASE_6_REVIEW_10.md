# Phase 6 verification review — round 10

## 0. Method and reading order

This adjudication first independently re-derived every surviving candidate against the whole
target, `docs/phase6/v1/PHASE_6_DOC.md`; the override-selected governing Part I, Phase 6
assignment, document gate, and mandatory template in `docs/design/v3/DESIGN.md`; the relevant
contract ground truth in `docs/research/v1/RESEARCH.md`; and the manifest-selected binding
interfaces of Phases 1, 3, and 4. Supporting and Pintonium material was treated only as evidence,
never as contract.

Only after settling those interpretations did adjudication read
`docs/phase6/reviews/PHASE_6_REVIEW_1.md` through
`docs/phase6/reviews/PHASE_6_REVIEW_9.md`, in numeric order. There were no reading-order
deviations, no network use, and no agent fan-out. Gate dropped no candidates, and no candidate was
eliminated before adjudication. All four candidates were independently re-derived rather than
accepted from their incoming labels.

## 1. Findings

### candidate-001 — Shaders-off provider-reference retention is internally contradictory

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:307-312` and
  `docs/phase6/v1/PHASE_6_DOC.md:1189-1204`
- **Claim:** The runtime does not define one consistent ownership rule for provider references
  when shaders are turned off.
- **Evidence:** The construction ownership contract says that a successful runtime retains its
  borrowed service references until the `close` reset
  (`docs/phase6/v1/PHASE_6_DOC.md:307-312`). The binding reset prose instead says shaders-off
  retains no provider references (`docs/phase6/v1/PHASE_6_DOC.md:1189-1194`), while the neighboring
  summary again postpones release until close (`docs/phase6/v1/PHASE_6_DOC.md:1200-1204`). Close is
  separately defined as a permanent transition that cannot be followed by adoption
  (`docs/phase6/v1/PHASE_6_DOC.md:1195-1198`), so it cannot be treated as another name for the
  shaders-off adoption itself.
- **Required correction:** Choose one shaders-off ownership rule and apply it consistently to the
  construction ownership contract and §4.14. If shaders-off releases providers during adoption,
  say so consistently and keep close as separate terminal cleanup; otherwise remove the claim
  that shaders-off retains no provider references.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — Lifecycle diagram omits GL-context loss from the reset inventory

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:432-440` and
  `docs/phase6/v1/PHASE_6_DOC.md:1185-1198`
- **Claim:** The lifecycle summary does not reflect the complete reset inventory defined by the
  binding reset section.
- **Evidence:** The lifecycle diagram says `RESET` occurs on world epoch, pack replacement,
  shaders-off, or close (`docs/phase6/v1/PHASE_6_DOC.md:432-440`). Section 4.14 defines five reset
  reasons and additionally includes GL-context loss
  (`docs/phase6/v1/PHASE_6_DOC.md:1185-1187`). The diagram cannot be read as limited to direct
  `reset` calls because it already includes pack replacement and shaders-off, which §4.14 assigns
  to generation adoption alongside GL-context loss
  (`docs/phase6/v1/PHASE_6_DOC.md:1189-1198`).
- **Required correction:** Add GL-context loss to the lifecycle diagram's `RESET` causes.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-003 — Maintenance metadata still stops at §0.8

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:15-18` and
  `docs/phase6/v1/PHASE_6_DOC.md:1629-1631`
- **Claim:** The maintenance provenance does not consistently acknowledge the substantive §0.9
  addendum.
- **Evidence:** Section 0.9 records the Review-9 generation-adoption correction
  (`docs/phase6/v1/PHASE_6_DOC.md:150-156`), but both the introductory provenance statement
  (`docs/phase6/v1/PHASE_6_DOC.md:15-18`) and the closing provenance
  (`docs/phase6/v1/PHASE_6_DOC.md:1629-1631`) say later governed maintenance ends at §0.8. The
  header's correct “through §0.9” description does not cure those two contrary statements.
- **Required correction:** Change `§§0.3–0.8` to `§§0.3–0.9` in both the introductory maintenance
  statement and the footer.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The finder-reported clean areas survived re-derivation:

- The initial-generation installation, equality-only replacement-generation adoption, retired
  generation rejection, frame-generation validation, lifecycle tests, and §5 handshake summary
  are mutually consistent and do not reintroduce numeric ordering or subtraction.
- The selected Phase 1 upload/readback/error contracts, Phase 3 declaration metadata, and Phase 4
  layout, access, activity-token, and generation contracts support the target's claimed
  consumption.
- The complete Appendix D inventory remains mapped to exact names, types, providers, cadences, and
  milestones. The sampler maps, unit 11, smoothing formulas, temporal snapshots, matrix capture,
  custom-after-built-ins order, notifier audit, provider seam, and frame-before-resize/clear rule
  remain covered.

Candidate disposition on independent derivation:

- **candidate-004 — dropped.** The target declares its Java signatures illustrative while making
  the data contracts binding (`docs/phase6/v1/PHASE_6_DOC.md:307-312`). Section 4.14 semantically
  closes the reason set to pack replacement, shaders-off, world epoch, GL-context loss, and close,
  then partitions the three generation-adoption transitions from the two direct-reset transitions
  and defines fail-fast behavior for a wrong direct-reset reason
  (`docs/phase6/v1/PHASE_6_DOC.md:1185-1198`). “Pack/registry replacement” in the immediately
  following summary makes the terminology equivalence sufficiently explicit
  (`docs/phase6/v1/PHASE_6_DOC.md:1200-1201`). Exact enum-constant spellings are therefore not
  required to implement the binding semantic contract. Read last, Round 8 had already settled
  that exact reset-reason spellings were permissible implementation detail, and Round 9 expressly
  preserved that settled semantic set while adding the generation-adoption handshake. The new
  method does not supply changed authority or a new ambiguity sufficient to reopen that cleared
  representation issue.

Reading the prior reviews last did not alter the three admitted findings. Candidate-001 and
candidate-002 arise from the new Round-9 lifecycle text or its interaction with older summaries;
candidate-003 is the corresponding stale maintenance metadata. None was settled by an earlier
review.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: no

The three admitted inconsistencies are bounded corrections rather than structural misses, so
`FAIL` is not warranted. Literal `PASS` is unavailable while they remain.

The next required action is a fix-up resolving candidate-001, candidate-002, and candidate-003 and
appending their resolutions to this review. None requires changing the manifest-selected
`cross-phase-interfaces` region. As with every fix-up, the resulting target bytes remain
unreviewed until a fresh verification round returns literal PASS.

Trend: Round 9 introduced and resolved one localized interface correction; Round 10 confirms that
the core generation handshake is coherent but finds three non-interface consistency defects around
its lifecycle and maintenance edits. Convergence is interrupted, not structurally reversed, and
closure cannot be claimed until the bounded fix-up receives a fresh review.

## Resolutions

### candidate-001 — resolved

The construction contract already established one lifecycle-wide rule: a successful runtime
borrows its service references until terminal `close`. Section 4.14 now applies that rule to
shaders-off consistently. Shaders-off still retires the old generation, clears its stated runtime
state, and makes participants no-ops, but it does not release borrowed providers early; terminal
close performs that release. This keeps close distinct from a reversible shaders-off transition.

### candidate-002 — resolved

The §4.1 lifecycle diagram now includes GL-context loss among the causes summarized by `RESET`,
matching §4.14's closed five-reason inventory. This is a summary correction only; the existing
generation-adoption behavior for GL-context loss is unchanged.

### candidate-003 — resolved

Both stale maintenance ranges now end at §0.9. A compact §0.10 addendum records this round's three
corrections without importing version-roll or session-accounting conventions.

### Interface-change disclosure

The manifest-selected `cross-phase-interfaces` region was not changed. These corrections affect
only internal lifecycle ownership, a lifecycle diagram, and maintenance provenance.

### Notes deferred

None. The adjudication admitted no notes, and all three corrections were applied without requiring
a new design decision or a change to authority.
