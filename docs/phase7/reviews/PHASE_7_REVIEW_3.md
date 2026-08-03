## 0. Method and reading order

I independently re-derived all four Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited supporting evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` and
`docs/phase7/reviews/PHASE_7_REVIEW_2.md`, in that order and last. Those reviews establish that the
current document incorporates two resolved correction rounds, but they do not settle any Round 3
candidate.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. Gate dropped candidate-004 and candidate-005 because their finder quotes did not resolve
uniquely; neither is admitted or reconsidered as a finding here.

## 1. Findings

### candidate-001 — Frame-finalization calls use nonexistent exit-kind constants

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:541`–`:542`
- **Claim:** The normative finalization paths cannot be implemented literally against Phase 7's
  closed `FrameExitKind` schema.
- **Evidence:** The detailed lifecycle calls `finish(NORMAL_RETURN)` at ordinary TAIL and
  `finish(EARLY_RETURN|VANILLA_THROW)` from the outer `finally`
  (`docs/phase7/v1/PHASE_7_DOC.md:541`–`:542`). The exposed enum contains only `NORMAL`,
  `EARLY_RETURN`, and `THROWN` (`docs/phase7/v1/PHASE_7_DOC.md:1033`). The target defines no aliases
  for `NORMAL_RETURN` or `VANILLA_THROW`. The mismatch is therefore a concrete identifier error,
  while its intended resolution is unambiguous.
- **Required correction:** Change the §4.2 calls to `finish(NORMAL)` and
  `finish(EARLY_RETURN|THROWN)`, preserving the existing closed enum.
- **Severity:** correction
- **touches interface/change-trigger region: no** — only the §4.2 call-site text must change; the
  manifest-selected §5 enum is already correct.

### candidate-002 — Fullscreen executor names variants absent from its exposed schema

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:647` and `:661`–`:662`
- **Claim:** The fullscreen executor algorithm is not directly implementable against the corrected
  §5.1 render-port schema because two concrete identifiers disagree with that schema.
- **Evidence:** The algorithm requests `Screen.INSTANCE` and selects `COMPAT_QUAD`
  (`docs/phase7/v1/PHASE_7_DOC.md:647`, `:661`–`:662`). The exposed target is instead the
  extent-bearing record `PassDrawTarget.Screen(Extent2i extent)`, and the primitive enum contains
  `QUADS` and `TRIANGLE_STRIP` (`docs/phase7/v1/PHASE_7_DOC.md:1118`–`:1122`). No singleton or
  `COMPAT_QUAD` alias exists elsewhere in the target.
- **Required correction:** Update §4.6 to construct/use `PassDrawTarget.Screen(extent)` and select
  `FullscreenPrimitive.QUADS`, leaving the existing §5.1 schema unchanged.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the defect is confined to §4.6's algorithmic
  identifiers; §5 already supplies the intended types.

### candidate-003 — Frame-begin sequence enters an absent frame state

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:572`–`:574`
- **Claim:** The normative frame-begin sequence names a transition that does not exist in Phase 7's
  closed state vocabulary.
- **Evidence:** After the shadow slot, §4.3 instructs the driver to enter `GBUFFERS_OPAQUE`
  (`docs/phase7/v1/PHASE_7_DOC.md:572`–`:574`). The normative state machine transitions from
  `SHADOW_DONE` to `GBUFFERS` (`docs/phase7/v1/PHASE_7_DOC.md:517`–`:525`), and the exposed
  `FrameState` enum likewise contains `GBUFFERS` but no `GBUFFERS_OPAQUE`
  (`docs/phase7/v1/PHASE_7_DOC.md:1037`–`:1040`). No alias or distinct local state is defined.
- **Required correction:** Rename the §4.3 transition to `GBUFFERS`, matching both the state machine
  and the existing enum.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the §5 state enum is already correct; only the
  detailed sequence text changes.

### candidate-006 — Binding-decision disposition mislabels six decisions

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1649`–`:1662`
- **Claim:** The explicit §11.2 conformance table does not faithfully dispose the binding decisions
  named D-1 through D-3 and D-7 through D-9.
- **Evidence:** The governing decision table defines D-1 as Cleanroom exclusivity, D-2 as the
  shaders-only/non-goals boundary, D-3 as the fixed pack-compatibility matrix, D-7 as
  GPL-3.0-or-later licensing, D-8 as permitted and prohibited source use, and D-9 as the
  compatibility-profile baseline (`docs/design/v2.0-RC3/DESIGN.md:351`–`:364`). Phase 7's rows for
  those IDs instead discuss jcpp ownership, harness runs, optional geometry, clean-room evidence,
  minimum GL 2.1, and internal-pack behavior (`docs/phase7/v1/PHASE_7_DOC.md:1653`–`:1661`). Other
  target text substantively covers at least some of the real requirements—for example the legal and
  provenance posture at `docs/phase7/v1/PHASE_7_DOC.md:90`–`:96`—but that does not make an expressly
  numbered binding-decision map faithful. D-4, D-5, D-6, and D-10 are semantically aligned and do
  not require rewriting.
- **Required correction:** Realign only D-1 through D-3 and D-7 through D-9 with their governing
  subjects, reusing and citing existing Phase 7 coverage where available. Preserve the already
  aligned D-4 through D-6 and D-10 rows.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the correction belongs to §11.2, outside the
  manifest-declared §5 region.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. `FailureId` now rejects null and blank
diagnostic IDs, and `MainDepthPreparation` is a valid Phase 5-owned binding type. Phase 3
configuration/internal-pack consumption, Phase 4 candidate/barrier/publication consumption, and
Phase 6 runtime/participant consumption remain consistent with their selected binding regions.
Phase 7 continues to flag unresolved Phase 1/2/4/5 requests rather than assuming ungranted APIs.

The conformance map contains identifiable mappings for the enumerated RESEARCH §4.4 frame-flow
items, Appendix A.1 program families, §7.1 hook needs 1–11, all seven reference-timeline rows, and
the assigned engine flags. Apart from candidate-006's mislabeled binding-decision rows, no further
candidate-backed conformance defect survived. All four Gate-surviving candidates were confirmed;
none was refuted or cleared on independent re-derivation. Gate's candidate-004 and candidate-005
drops remain excluded because their evidence was unverifiable.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: no

All four admitted defects are local consistency or conformance-map corrections and require no
architectural rebuild. None changes the manifest-declared cross-phase interface region, so the
interface change trigger does not apply to this fix-up.

The correction count rises from three in each of Rounds 1 and 2 to four in Round 3, but the defects
are narrow identifier drift and a disposition-table realignment exposed after the prior schema
expansions. The loop has not reached literal convergence and cannot be softened to PASS. The next
required action is a scoped fix-up of this review, including a `## Resolutions` record and Phase 7
addendum. Under §G1.3, if that fix-up remains outside §5 as directed, the resolved
PASS-WITH-CORRECTIONS review closes the phase without another interface-triggered verification
round; any §5 change would instead require a fresh review.

## Resolutions

### candidate-001 — resolved

Re-derived against the closed `FrameExitKind` declaration and corrected only the §4.2 call sites:
ordinary TAIL now uses `NORMAL`, while the outer `finally` uses `EARLY_RETURN|THROWN`. The enum and
the manifest-selected §5 region were not changed.

### candidate-002 — resolved

Re-derived against `PassDrawTarget.Screen(Extent2i extent)` and `FullscreenPrimitive`. Section 4.6
now constructs `PassDrawTarget.Screen(extent)` for the current target extent and selects
`FullscreenPrimitive.QUADS`, retaining the existing triangle-strip fallback. No §5 schema changed.

### candidate-003 — resolved

Re-derived against both the §4.2 state machine and the closed `FrameState` vocabulary. The §4.3
post-shadow transition now enters `GBUFFERS`; no alias or new state was introduced.

### candidate-006 — resolved

Re-derived D-1 through D-10 from the governing decision table at
`docs/design/v2.0-RC3/DESIGN.md:351`–`:364`. The §11.2 rows for D-1, D-2, D-3, D-7, D-8, and D-9
now respectively dispose Cleanroom exclusivity, shaders-only scope, the fixed compatibility
matrix, GPL-3.0-or-later licensing, permitted/prohibited evidence use, and the compatibility-profile
baseline. Existing Phase 7 coverage is reused by section reference where useful. The already
aligned D-4, D-5, D-6, and D-10 rows remain byte-for-byte unchanged.

### Notes deferred

None. The adjudicator admitted no notes, and all four corrections were implementable without a new
design decision or an authority conflict.

### Fix-up status

All admitted Round 3 corrections are applied. The only target changelog addition is compact §0.7.
The manifest-selected §5 cross-phase-interface region is unchanged, so this fix-up does not fire
its fresh-verification change trigger.
