# PHASE_1_DOC.md — Verify session, round twenty-four

## 0. Method and reading order

I first independently re-derived both surviving candidates from the whole target,
`docs/phase1/v14/PHASE_1_DOC.md`; the resolved v3 governing selections in
`docs/design/v3/DESIGN.md` (Part I, Phase 1 specification, document gate, and mandatory template);
and the relevant contract ground truth in `docs/research/v1/RESEARCH.md`. Phase 1 has no dependency
documents. I used the supplied repository and template evidence only where needed to test the
claims.

Only after settling that interpretation did I read
`docs/phase1/reviews/PHASE_1_REVIEW_1.md` through
`docs/phase1/reviews/PHASE_1_REVIEW_23.md`, last. Round twenty-three returned literal PASS and is
therefore material to candidate-001. Earlier resolutions also establish that §5's stated
self-sufficiency has repeatedly been treated as a substantive contract-honesty standard, not
discarded as harmless wording.

There were no reading-order deviations. I used no network source and no forbidden source. This was
an already-dispatched atomic Adjudicate role, so I did not invoke the verification harness, start
another Codex session, or use agent fan-out. The Gate dropped no candidates; both surviving
candidates received a final disposition below.

## 1. Findings

### candidate-001 — Closing history and current verification status omit round twenty-three

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:5323` and
`docs/phase1/v14/PHASE_1_DOC.md:5377`.

**Claim.** The closing history does not accurately record the completed verification rounds or the
review state governing Phase 1.

**Evidence.** The epilogue says, *"Twenty-two verify sessions have since run"* and ends its
chronology with *"round twenty-two returned literal PASS"*
(`docs/phase1/v14/PHASE_1_DOC.md:5323-5329`). Its status then says round twenty-two's PASS is
historical and the document remains unverified until a subsequent fresh review
(`docs/phase1/v14/PHASE_1_DOC.md:5377-5379`). But
`docs/phase1/reviews/PHASE_1_REVIEW_23.md:60-70` records that subsequent fresh review as literal
PASS, with zero findings, and explicitly says that it verifies the §0.22 interface change. The
governing rule makes the latest review outcome decisive: a phase is verified when its latest
review is PASS (`docs/design/v3/DESIGN.md:355-359`).

**Severity:** correction. Update the completed-review count and chronology to include round
twenty-three's literal PASS, and update the current §G1.3 status accordingly. Leave §5 unchanged.

**Touches interface/change-trigger region: no.** The correction is confined to closing history and
status.

### candidate-002 — The declared interface region excludes facade declarations it incorporates as binding

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:4173` and
`docs/phase1/v14/PHASE_1_DOC.md:4197`.

**Claim.** The selected §5 region is not sufficient on its own to represent Phase 1's binding
facade promises or reliably trigger review when those promises change.

**Evidence.** Section 5 expressly promises that it is *"sufficient on its own"* and that *"every
obligation this document places on another phase appears here, not only in §4 or §11"*
(`docs/phase1/v14/PHASE_1_DOC.md:4173-4175`). Yet its principal facade row exposes `GLDevice` and
seven services only by referring to *"§4.7.4 signatures"*
(`docs/phase1/v14/PHASE_1_DOC.md:4197-4201`). The actual `GLDevice` methods and load-bearing
`drainErrors()` semantics are outside the declared region
(`docs/phase1/v14/PHASE_1_DOC.md:2867-2894`). The mandatory whole-document reading rule makes that
incorporation usable by a consumer, but it does not make §5 self-contained, nor does it cause a
direct change to an incorporated declaration to touch the manifest-selected §5 range. The
governing architecture requires exact semantics and interfaces exposed to dependents
(`docs/design/v3/DESIGN.md:251-254`), while the mandatory template requires §5 to state the named
interfaces and data contracts exposed to dependents (`docs/design/v3/DESIGN.md:832-835`).

**Severity:** correction. Make the change-trigger coverage include the detailed public facade
declarations incorporated from §4.7.2–§4.7.5, or place the complete binding declarations and exact
semantics in §5. If incorporation by reference is retained, qualify the unsupported
"sufficient on its own" statement and ensure a change to an incorporated contract necessarily
changes a declared interface region.

**Touches interface/change-trigger region: yes.** The correction must change §5 or the declared
coverage of its incorporated binding contracts, so a fresh review is owed before Phase 1 closes.

## 2. Checked and clean

The new-surface lens's §0.22 additions were checked across the package table, replay value,
binding contracts, tests, milestones, decisions, handoff text, and implementation checklist. The
package paths, consumer assignments, replay-attribution semantics, and `[D-P1-41]` / `[D-P1-42]`
identifiers are internally consistent.

The interface surface otherwise consistently assigns module/package ownership, conventions,
consumers, borrowed-texture permissions, error replay, recording fixtures, and intentional
non-verbs. Phase 1 consumes no dependency contract, and no independent contradiction in those
promises was found. Candidate-002's strongest contrary reading—whole-document reading plus precise
incorporation is enough for consumers—does explain how consumers find the declarations, but does
not reconcile the explicit self-sufficiency promise or the narrower mechanical change-trigger
region, so the candidate was not cleared.

The conformance map and reviewed Phase 1 scope were checked against the governing contract rows,
including lifecycle probes, directives, GL opportunities, depth textures, fixed texture-unit
mapping, uniforms, and render-state properties. No unmapped in-scope requirement or unsupported
mapping was established.

No candidate was dropped on re-derivation. Prior-review-last inspection disclosed no settled
resolution that cures either present defect: round twenty-three proves candidate-001's omitted
outcome, while prior treatment of §5's self-sufficiency reinforces rather than disposes of
candidate-002.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted findings are bounded corrections; neither requires rebuilding the architecture.
Candidate-001 does not touch the interface. Candidate-002 requires correction of the binding
interface/change-trigger coverage and therefore fires the fresh-review requirement.

The round trend has not converged: round twenty-three's PASS verified §0.22, but the target failed
to record that completed state, and this round identifies a still-open interface-coverage defect.
This is not a structural failure or a reason to soften either correction.

Next action: run a scoped fix-up for both corrections and record the resolutions in this review.
Because candidate-002 touches the interface/change-trigger contract, run a fresh whole-document
verification round afterward; Phase 1 remains unverified until that round returns literal PASS.

## Resolutions

### candidate-001 — applied

Re-derived from the round files and the governing §G1.3 rule: round twenty-three returned literal
PASS on the §0.22 byte state, while round twenty-four is the present correction-bearing review. The
epilogue now counts twenty-four verify sessions, includes both outcomes, and makes round
twenty-three's PASS historical because this fix-up changes binding §5. The earlier live §0.22 status
is restamped historical/superseded, and §0.23 plus the closing status consistently say that a fresh
literal PASS is required. No §5 declaration or facade semantic was changed for this correction.

### candidate-002 — applied

The manifest is immutable in this atomic role, so widening its selector to cover §4.7.2–§4.7.5 was
not an available fix. Re-reading the neighboring §5 rows confirmed that they intentionally expose
detailed facade declarations and semantics by precise incorporation from those sections. The §5
preamble now states the narrower truth: §5 is self-contained as an index of cross-phase obligations,
not as a reproduction of incorporated declarations. It also makes synchronization normative: an
edit to an incorporated declaration or semantic contract is incomplete unless the corresponding §5
row changes in the same revision, including an explicit unchanged entry when incorporation remains
exact. Consequently every valid public-contract change touches the manifest-selected interface
region. This intentionally changes §5 and fires the declared fresh-review trigger; it does not alter
the facade declarations or their semantics.

### Notes deferred

None. The adjudicator admitted no notes.

### §G1.3 status

Both corrections are applied with none refused. Because candidate-002 changes binding §5, Phase 1
remains unverified and is not a valid dependency input until a fresh whole-document review returns
literal PASS. The directory remains `v14` while the loop is open.
