# Phase 3 Adversarial Review — Round 23

## 0. Method and reading order

I independently re-derived the sole candidate from the complete Phase 3 target, the
manifest-selected governing-design regions, RESEARCH.md, the Phase 1 binding contract, and the
manifest and engine passages cited by the refuters. The supplied Pintonium and Oculus reports were
permitted supporting evidence but were not needed to decide the candidate. Only after settling the
candidate's interpretation, severity, and interface classification did I read prior reviews 1–22,
in round order and including their resolutions.

There were no deviations from the required reading order, no network use, no agent fan-out, and no
use of forbidden sources. Under the dispatched-role rule in the supplied `verify-loop` skill, I did
not invoke the verification harness or start another session. The Gate reported no drops, and no
candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — The declared interface region delegates binding semantics to unmonitored detailed-design sections

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1286-1302`, with incorporated contracts at
`docs/phase3/v1/PHASE_3_DOC.md:1019-1070`

**Claim:** The manifest-declared §5 interface region is not sufficient to ensure that every change
to Phase 3's consumer-visible contract triggers the required fresh verification round.

**Evidence:** Section 5 declares itself the complete publication surface
(`docs/phase3/v1/PHASE_3_DOC.md:1286-1287`), but its `ProgramStateModel` row incorporates the closed
§4.8 aggregate and its parsing, disablement, and ordering semantics, while its
`ResourceRequirements` row incorporates §4.7's closed algebra, defaults, absence rules, and
deterministic order (`docs/phase3/v1/PHASE_3_DOC.md:1301-1302`). The actual public record components
are declared outside §5 (`docs/phase3/v1/PHASE_3_DOC.md:1019-1039`), and their executable defaults,
absence representation, directive bounds, and winning-occurrence rules are likewise normative
outside §5, partly through a further reference to §3.3
(`docs/phase3/v1/PHASE_3_DOC.md:1063-1070`). These are binding consumer semantics, not merely
explanatory implementation detail.

The manifest selects only the text from the §5 heading to the §6 heading as
`cross-phase-interfaces` (`verification/targets/phase-3.json:142-150`). The engine hashes exactly
each manifest-declared selector (`.agents/skills/verify-loop/scripts/engine.mjs:1248-1257`) and
emits the configured fresh-review trigger only when that selected hash changes
(`.agents/skills/verify-loop/scripts/engine.mjs:1274-1280`). A record-component, default, absence,
or ordering change in the incorporated §§3.3, 4.7, or 4.8 contract can therefore alter what a
dependent must implement without changing the monitored hash. The governing template legitimately
places exact semantics in §4 and exposed contracts in §5
(`docs/design/v2.0-RC3/DESIGN.md:809-812`); the defect is incomplete change-trigger coverage, not
the use of cross-references itself.

**Severity:** correction. Expand the manifest's interface/change-trigger coverage to the exact
normative detailed-design regions incorporated by §5, or make §5 itself contain the binding public
shapes and executable semantics. The repair must ensure that every consumer-visible semantic
change fires the fresh-review trigger without creating competing normative copies. This is bounded
contract/verification configuration work and does not require rebuilding the Phase 3 architecture.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The §0.25 `NormalizedPackPath` closure is coherent across its public signature, canonical-path
  grammar, ordering and hashing, schema-version rule, §5 handoff, tests, decision log, and
  implementation checklist. It does not alter the `PackConfiguration` component set, and the
  document correctly classifies a future grammar change as interface-breaking.
- Phase 3's consumed Phase 1 module/seam, capability, logging, diagnostics, debug-flag, licensing,
  and conformance-extension contracts exist in the selected binding region. The missing jcpp
  dependency declaration remains an explicit requested upstream change.
- The complete conformance map and equivalent detailed coverage were rechecked for discovery,
  preprocessing, options, macros, directives, properties, ID mappings, persistence, publication,
  engine-flag ownership, and pure-`:engine` placement. No additional candidate was supplied or may
  be admitted from those clean areas.
- The sole candidate was not refuted or cleared on independent re-derivation. Prior Round 16's
  resolution correctly permits precise references from §5 to detailed semantics instead of
  duplicating them, but it does not make those referenced ranges part of the manifest's monitored
  interface hash. Round 8 previously treated operative downstream semantics outside the declared
  interface region as a correction. Later PASS rounds did not specifically settle the distinct
  selector-coverage defect now evidenced by the manifest and engine behavior.
- There were no candidates eliminated before adjudication and no findings dropped on derivation.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The one admitted finding is a bounded interface/change-trigger correction, not a structural miss.
The supplied prior-round trend contains no computed entries; independently, Round 22 reached
literal PASS, but this round exposes a distinct monitoring gap, so the current verification surface
has not converged to literal PASS.

The next required action is a scoped fix-up resolving `candidate-001` and appending this review's
`## Resolutions`. Because the correction changes the authoritative interface/change-trigger
coverage or the §5 interface region itself, a fresh verification round is required before Phase 3
may close.

## Resolutions

### candidate-001 — resolved

Re-derived result: the finding is valid. The manifest is immutable in this dispatched fix-up, and
merely retaining §5 references to §§3.3, 4.7, and 4.8 would leave consumer-visible record shapes,
defaults, absence rules, and ordering outside the selector that carries the fresh-review trigger.

The target now makes §5 the sole binding consumer contract for `ProgramStateModel` and
`ResourceRequirements`. Section 5 contains their closed public shapes/value domains, executable
defaults and absence representations, deterministic collection order, winning/malformed
occurrence behavior, and downstream projections. It explicitly demotes the detailed-design text
to producer-side elaboration and requires any detailed change that affects a published value or
consumer interpretation to change §5 in the same edit. This avoids a second competing normative
copy while ensuring such a contract change alters the monitored interface region.

The compact §0.26 addendum records only that correction. No schema increment is required because
the published contract itself is unchanged; this fix makes its existing semantics independently
present in the monitored region. The §5 interface region was intentionally changed, so the
manifest's declared fresh-verification trigger fires and Phase 3 cannot close before another
whole-document review returns literal PASS.

### Notes deferred

None; the adjudicator admitted no notes.
