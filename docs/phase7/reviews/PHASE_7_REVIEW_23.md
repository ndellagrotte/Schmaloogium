## 0. Method and reading order

I independently re-derived the Gate-surviving candidate from the whole Phase 7 target, the
manifest-selected RC3 governing sections (including the Phase 7 specification, document gate, and
mandatory template), RESEARCH ground truth, and the binding §5 contracts of Phases 2–6. I first
compared Phase 7's Phase 5 consumption table and coordinated-publication protocol with Phase 5's
manifest-selected binding surface. Only after settling that judgment did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_22.md`, in order and last.

The only reading-list deviation was
`reference-src/schlorbium-HD_U_G6_pre1/files.txt`: the resolved contract also forbids every
`*.txt` source, so I did not read it. It was immaterial to this dependency-contract accounting
candidate. There was no network use, forbidden-source use, or agent fan-out. This was the canonical
engine's already-dispatched atomic adjudication role, so the supplied `verify-loop` instructions
required completing only this role without invoking the loop or delegating. No candidates were
eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Phase 5 consumption table omits the consumed resize contract and cites a stale range

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1905`–`:1917`
- **Claim:** Section 5.2 does not completely or accurately identify the manifest-selected Phase 5
  binding contracts consumed by Phase 7.
- **Evidence:** The Phase 5 consumption table lists planning/publication, frame/pass, main-depth,
  depth-copy, draw-target, and overlay contracts, then asserts that these are the current binding
  rows at `docs/phase5/v1/PHASE_5_DOC.md:1777`–`:1796`
  (`docs/phase7/v1/PHASE_7_DOC.md:1907`–`:1917`). That range is outside the manifest-selected Phase
  5 §5 region. The actual binding surface exposes `BufferResizeNotice`, `BufferResizeReason`,
  `BufferResizeConsumer`, the registration types, and their closed delivery results, including
  ordered synchronous delivery after installation and before drawing
  (`docs/phase5/v1/PHASE_5_DOC.md:1813`–`:1833`, specifically `:1832`). Phase 7 consumes that
  behavior explicitly when its publication protocol publishes Phase 5 second and synchronously
  delivers the resize notice (`docs/phase7/v1/PHASE_7_DOC.md:1955`–`:1959`). Section 5.3 therefore
  establishes the runtime use but does not repair §5.2's incomplete dependency declaration or its
  stale claim about the authoritative rows.
- **Required correction:** Add the consumed `BufferResizeNotice`/`BufferResizeConsumer`
  registration, delivery, and result contract to the Phase 5 table with its coordinated-publication
  use, and replace the stale Phase 5 citation with the manifest-selected §5.1 range that contains
  the listed contracts (currently `docs/phase5/v1/PHASE_5_DOC.md:1819`–`:1833`).
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the incomplete consumption declaration and
  stale binding citation are inside the manifest-selected cross-phase interface region.

## 2. Checked and clean

The finder-reported clean areas survived independent re-derivation apart from the admitted Phase 5
dependency-accounting defect. The Round 22 edit is internally consistent across §0.26, the compact
`QUIESCING` diagram, detailed reverse-composition teardown, implementation checklist, and closing
status. Phase 2, Phase 3, Phase 4, and Phase 6 consumptions materially align with their selected
binding surfaces. Phase 7's exposed consumer contracts and downstream Phase 8/9 hand-offs otherwise
provide implementable identities, ownership, ordering, closed outcomes, and lifetime rules.

The governing scope and conformance map retain zero-unmapped coverage of the Phase 7 frame flow,
all eleven hook needs, classic program families, seven reference-timeline rows, engine flags,
lifecycle triggers, and reference-free sky/weather/cloud rows. No supplied candidate was cleared or
dropped on re-derivation. Prior reviews do not settle this finding: Rounds 14–17 addressed the
resize observation, authentication, and scheduling seams, but none corrected the current §5 Phase 5
consumption inventory or its stale binding-range citation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The single admitted defect is a localized cross-phase contract-accounting correction and does not
require structural rebuilding. Round 22 also had one correction, so the recent count is flat rather
than converged; literal PASS has not been regained. The next required action is a scoped fix-up of
candidate-001, including this review's `## Resolutions` record and the Phase 7 addendum. Because the
correction changes the manifest-selected cross-phase interface region, a fresh whole-document and
interface verification round is required before Phase 7 can close.

## Resolutions

### candidate-001 — resolved

Re-derived against the manifest-selected Phase 5 §5 surface. Phase 5 exposes the resize notice,
consumer, registration, delivery, and closed-result family at
`docs/phase5/v1/PHASE_5_DOC.md:1832`, and Phase 7's coordinated-publication protocol consumes its
ordered synchronous delivery at `docs/phase7/v1/PHASE_7_DOC.md` §5.3 step 11. Section 5.2 now
declares that consumed family and its before-draw, closed-result use. The stale citation was
replaced with `docs/phase5/v1/PHASE_5_DOC.md:1819`–`:1833`, the manifest-selected range containing
every Phase 5 contract listed in the table.

The target received compact §0.27 and closing-status updates. Because the substantive table edit
is inside the manifest-selected cross-phase interface region, Phase 7 remains unverified and a
fresh whole-document/interface round is required before closure.

### Notes deferred

None. The adjudicator admitted no notes.
