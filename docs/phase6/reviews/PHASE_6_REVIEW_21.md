## 0. Method and reading order

I independently re-derived each supplied candidate against the complete Phase 6 target, the
governing RC3 Part I, Phase 6 specification, document gate and mandatory template, the binding
Phase 1/3/4 interface regions, and the cited Pintonium evidence. Only after settling those
interpretations did I read Phase 6 Reviews 1–20, with their resolutions, to test for prior
settlement or contradiction. I used no network, no forbidden source, no agent fan-out, and no
deviation from the resolved contract. The Gate reported no drops; no candidate had been eliminated
before adjudication.

## 1. Findings

### candidate-001 — The current verification-status marker remains anchored to §0.18

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:138-141` and
  `docs/phase6/v1/PHASE_6_DOC.md:224-228`
- **Claim:** The paragraph explicitly labeled `Current §G1.3 status` is stale: it identifies
  Review 18 and the §0.18 interface change as the reason the bytes remain unverified, while the
  document now contains Reviews 19–20 and a §0.20 fix-up that changed §5 by requesting the missing
  Phase 1 `ivec3` upload verb.
- **Evidence:** The status marker says Review 18 passed the §0.17 bytes and that §0.18 subsequently
  changed §5 (`docs/phase6/v1/PHASE_6_DOC.md:138-141`). The latest addendum instead says compact
  provenance includes §§0.19–0.20 and records the retained `Int3` command's new Phase 1 dependency
  request (`docs/phase6/v1/PHASE_6_DOC.md:224-228`). Review 20's settled resolution confirms that
  this latest correction intentionally changed §5 and requires a fresh review
  (`docs/phase6/reviews/PHASE_6_REVIEW_20.md`). Thus the designated current marker is internally
  inconsistent with both the current target and settled maintenance history.
- **Required correction:** Synchronize the current-status paragraph with §§0.19–0.20 and identify
  §0.20's dependency/interface update as the latest reason the current bytes require fresh
  verification. State round-specific history only to the extent established by the settled
  reviews.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — §5's change trigger does not cover incorporated binding definitions outside §5

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:549-602` and
  `docs/phase6/v1/PHASE_6_DOC.md:1382-1388`
- **Claim:** Section 5 publishes exact consumer-visible schemas and semantics by reference to
  definitions in §§4.2, 4.9 and 4.13, but it does not require changes to those incorporated
  definitions to update §5. Consequently, a binding interface can change without changing the
  manifest-watched region that triggers fresh verification.
- **Evidence:** The exact provider and event record shapes are defined outside §5
  (`docs/phase6/v1/PHASE_6_DOC.md:549-566`, `docs/phase6/v1/PHASE_6_DOC.md:582-602`). Section 5
  exposes those exact schemas and likewise incorporates the fixed sampler and custom-refresh plans
  by reference (`docs/phase6/v1/PHASE_6_DOC.md:1382-1388`). The governing protocol makes an actual
  §5 change the re-verification condition (`docs/design/v2.0-RC3/DESIGN.md:327-331`). No target-wide
  synchronization invariant closes the case where an incorporated declaration changes while §5's
  reference remains byte-identical. Earlier reviews show individual synchronized edits, but none
  settles a general rule covering all incorporated interface definitions.
- **Required correction:** Add a normative §5 synchronization rule identifying its incorporated
  external binding declarations and semantics, at minimum the referenced portions of §§4.2, 4.9
  and 4.13, and require every semantic or schema change to them to update §5 in the same revision.
  Complete schema duplication is unnecessary.
- **Severity:** correction
- **touches interface/change-trigger region: yes**

### candidate-003 — Two Pintonium disposition rows omit mandatory row-local mappings

- **Location:** `docs/phase6/v1/PHASE_6_DOC.md:458-461`
- **Claim:** The conformance-map rows for GPU `centerDepthSmooth` rejection and smoothing-math
  adoption do not carry all row-local provenance and decision references required for
  contract-visible Pintonium dispositions.
- **Evidence:** The mandatory template requires every adoption or rejection row to carry its PD
  citation and, for contract-visible items, its §G11.4 decision reference
  (`docs/design/v2.0-RC3/DESIGN.md:804-808`); §G11.4 requires the recorded contract-check decision
  (`docs/design/v2.0-RC3/DESIGN.md:916-921`). The center-depth row names `D-P6-1` but has no PD
  citation, while the smoothing row cites source code but supplies neither a PD citation nor
  `D-P6-3` (`docs/phase6/v1/PHASE_6_DOC.md:458-461`). PD §6.3 and §6.4 are the corresponding
  evidence sections (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:288-323`). The decisions
  already exist in §11 (`docs/phase6/v1/PHASE_6_DOC.md:1672-1676`), but their presence elsewhere
  does not satisfy the mandatory row-local mapping.
- **Required correction:** Add the applicable PD §6.3 citation to the GPU center-depth row, and add
  the PD §6.4 citation plus `D-P6-3` to the smoothing-math row.
- **Severity:** correction
- **touches interface/change-trigger region: no**

## 2. Checked and clean

The §0.20 `Int3`/`ivec3` request is otherwise synchronized across the custom-uniform design, §5,
failure handling, tests, decisions, upstream requests and checklist. The counter-mismatch branch is
also consistent across its behavior, interface, failure, test and checklist surfaces. Header and
closing maintenance ranges both end at §0.20.

Dependency consumption is otherwise candid and compatible with the binding Phase 1, Phase 3 and
Phase 4 contracts: in particular, Phase 6 does not pretend the requested `ivec3` verb is already
granted. Frame-begin ordering and the current custom-refresh outcomes are stated clearly.

The Appendix D inventory, fixed sampler map including unit 11, activation-barrier behavior,
frame-begin-before-resize/clear ordering, CPU center-depth decision, smoothing behavior, and
notifier-to-producer audit are substantively mapped. None of the three supplied candidates was
refuted or cleared on re-derivation, and prior settled reviews do not already dispose of them.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

All three findings are bounded document corrections rather than structural omissions requiring a
rebuild, so `FAIL` is not warranted. Literal `PASS` is unavailable while three corrections remain.
Candidate-002 requires changing the manifest-selected interface/change-trigger region;
candidates-001 and -003 do not.

The next required action is a governed fix-up resolving all three candidates and appending
resolutions to this review. Because candidate-002 changes §5, a fresh verification round is then
required before Phase 6 can close.

Trend: Round 18 reached literal PASS, but Rounds 19 and 20 each returned two corrections after new
maintenance changes, and Round 21 now has three. The loop is not converging: compact-status drift
has recurred and the interface-trigger coverage itself is incomplete. These remain locally
repairable, so the trend warrants explicit fix-up attention and another review, not escalation to
`FAIL`.

## Resolutions

### candidate-001 — resolved

Updated the designated current-status paragraph to include §§0.19–0.20, identify §0.20's Phase 1
`ivec3` dependency/interface request, and state that Review 21's §0.21 correction also leaves the
current bytes awaiting literal PASS. Header and closing provenance now end at §0.21.

### candidate-002 — resolved

Added a normative §5 synchronization rule making the exact schemas and semantics incorporated
from §§4.2, 4.9, and 4.13 binding parts of §5 and requiring any semantic or schema change to update
the corresponding §5 row in the same revision. This intentionally changes the manifest-selected
interface region, so a fresh verification round is required before Phase 6 can close.

### candidate-003 — resolved

Added the row-local PD §6.3 citation to the GPU `centerDepthSmooth` rejection and the row-local PD
§6.4 citation plus D-P6-3 decision reference to the smoothing-math disposition.

### Notes deferred

None; Review 21 admitted no notes.
