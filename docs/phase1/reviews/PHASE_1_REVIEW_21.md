# PHASE_1_DOC.md — Verify session, round twenty-one

## 0. Method and reading order

I first re-derived the supplied candidate from the whole document under review,
`docs/phase1/v14/PHASE_1_DOC.md`, the resolved governing selections in
`docs/design/v2.0-RC2/DESIGN.md` (Part I, the Phase 1 specification, the document gate, and the
mandatory template), and the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`. Phase 1 has no dependency documents. I checked §0.20, §1, the
package layout, the binding §5 region, milestone staging, the Phase 8 hand-off, the implementation
checklist, and the closing status, including every occurrence of the three newly granted package
homes.

Only after completing that independent judgment did I read the discovered prior reviews,
`docs/phase1/reviews/PHASE_1_REVIEW_1.md` through
`docs/phase1/reviews/PHASE_1_REVIEW_20.md`, last. The settled record establishes that round twenty
cleared the pre-§0.20 surface. Earlier rounds also show that §1.2 has previously been treated as the
mandatory anti-sprawl ownership device, but none settles the new Phase 8 package grant introduced
after round twenty.

There were no reading-order deviations. I used no network source and no forbidden source. This was
the already-dispatched atomic Adjudicate role, so, as required by the verify-loop skill's internal-
role rule, I did not invoke the verification orchestrator, start another Codex session, or use agent
fan-out. The Gate reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — The new Phase 8 surface is absent from the mandatory adjacent-ownership table

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:1417`

**Claim.** Section 1 must explicitly identify Phase 8 as owner of the adjacent shadow concerns
touched by §0.20. Section 1.2 presents itself as the complete anti-sprawl ownership table but omits
Phase 8.

**Evidence.** The governing mandatory template requires §1 to contain an explicit “owned by Phase
Y” line for every adjacent concern touched
(`docs/design/v2.0-RC2/DESIGN.md:771`–`:772`). The §0.20 amendment assigns loader-neutral shadow
policy, math, traversal, lifecycle, and result types; Minecraft/Forge/LWJGL adapters; and dumb
shadow Mixins/accessors to the three Phase 8 package homes, while limiting Phase 1's grant to
placement and seam constraints (`docs/phase1/v14/PHASE_1_DOC.md:1378`–`:1383`). Section 1.2 then
claims to list every concern the document touches but does not own, yet its complete table contains
no Phase 8 row (`docs/phase1/v14/PHASE_1_DOC.md:1417`–`:1433`). The accurate Phase 8 ownership
statements in §§2.1, 5, 9, and 11.4 establish that ownership but do not satisfy §G9's location-
specific §1 requirement.

**Severity:** correction.

**Touches interface/change-trigger region:** no.

**Required correction.** Add a §1.2 row assigning shadow policy, traversal/pass lifecycle,
Minecraft/Forge/LWJGL adapters, and shadow Mixins/accessors to Phase 8, while stating that Phase 1
grants only package placement and seam constraints.

## 2. Checked and clean

The finder-reported new surface was re-checked across §0.20, §2.1, §5, §9, §11.4, §12, and the
closing history. The three package identifiers are spelled consistently. Their loader-neutral and
loader-facing split is coherent, milestone tagging is present, existing seam and config-agreement
constraints still apply, and all live status statements correctly require round-twenty-one review
of the §5-changing grant.

The interface lens was independently re-derived. Phase 1 consumes no dependency contract. The
Phase 8 placement grant is consistently specified in the detailed design and binding §5 region,
does not weaken C-1 through C-4 or `.internal` privacy, and accurately separates Phase 1's package
placement authority from Phase 8's subsystem policy and implementation ownership. The admitted
defect is confined to §1.2 and does not require changing §5 or another declared interface/change-
trigger region.

The conformance map, Phase 1 scope, document gate, assigned OQs, D-1 through D-10 dispositions, and
thirteen-section mandatory template were also re-checked. No additional supportable conformance,
scope, interface, or structural defect was established.

Candidate-001 survived independent re-derivation and was admitted as a correction. It was not
previously cleared because the relevant Phase 8 package grant postdates round twenty. No candidate
was refuted, cleared, or dropped during adjudication.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: no

One bounded mandatory-template correction remains. It does not require rebuilding the document,
so FAIL is not warranted; PASS is unavailable while the correction remains. No admitted finding
touches the declared cross-phase-interface/change-trigger region, and this review changes no
interface.

Round twenty was a literal PASS on the pre-§0.20 bytes. The omission arose only when the new Phase
8 surface was added, so it is a localized new-surface regression rather than recurrence of a
previously cleared defect. The package grant itself is otherwise consistently propagated, showing
near-convergence.

Next action: apply a scoped fix-up adding the Phase 8 ownership row to §1.2 and record the resolution
in this review. Because that correction need not alter §5, it does not itself fire the interface
change trigger; the verification loop must nevertheless obtain a subsequent literal PASS before
declaring convergence and allowing Phase 1 to close as a verified dependency input.

## Resolutions

### candidate-001 — applied

Re-derived against `docs/design/v2.0-RC2/DESIGN.md:771`–`:772`, §0.20, and the complete §1.2
table. The table did omit an adjacent concern introduced by the Phase 8 package grant. Added one
row assigning shadow policy, camera/celestial math, traversal/pass lifecycle,
Minecraft/Forge/LWJGL adapters, and shadow Mixins/accessors to Phase 8. The row also states that
Phase 1 grants only package placement and seam constraints, preserving the ownership boundary in
§0.20 and §5.

Added compact §0.21, marked §0.20's live status historical, advanced the revision metadata and
closing counts, and restated the live §G1.3 status. The binding §5 region was not edited; its
SHA-256 remains `2966c2b33410a6deb67f8e25b1cb24dcee42b332cb1d894cf57c787b7118565f`.

### Notes deferred

None. The adjudicator admitted no notes.
