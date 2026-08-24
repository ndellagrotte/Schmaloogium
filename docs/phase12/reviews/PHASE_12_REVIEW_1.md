# Phase 12 verification review — round 1

## 0. Method and reading order

Target: `docs/phase12/v1/PHASE_12_DOC.md` (whole document, ll. 1–1553).
Authorities: `docs/design/v3/DESIGN.md` (Part I, Phase 12 target spec ll. 2357–2435, doc gate
ll. 2423–2428, mandatory template ll. 817–855) and `docs/research/v1/RESEARCH.md` (manifest
selectors). Dependencies: `docs/phase1/v14/PHASE_1_DOC.md` §5 (ll. 4175–4283) and
`docs/phase3/v1/PHASE_3_DOC.md` §5 (ll. 1420–1689). Supporting evidence: Phase 1 review 25,
Phase 3 reviews 34/35/36, the Pintonium design map and the cited Pintonium/OptiFine sources.

Reading order was as instructed: I re-derived every candidate independently against the target,
the authorities, the dependency binding regions and the evidence before consulting prior reviews.
The prior-review list for this target is empty (first round), so nothing was previously cleared and
no settled material constrained this adjudication.

Independent re-derivations performed this round: the Phase 3 dependency state (reviews 34, 35, 36),
`PackFrontEnd.CURRENT_SCHEMA_VERSION` at P3 ll. 341 and 1644–1660, the true positions of the P3 §5
consumer rules (ll. 1621–1624 vs. the target's ll. 1572–1575), the `sliders=` row (P3 l. 740 vs.
the target's l. 731), the P3 §5/§6 boundary (§6 opens at l. 1690, so §5 does not end at l. 1632),
RESEARCH §4.7 ll. 612–616, and the target's §5.1/§5.2 interface region (ll. 973–1117).

Deviations: none. No network use. No agent fan-out (single adjudicator; no subagents spawned, no
verify script or nested session run). No forbidden source was opened: no chatlog directory and no
`*.txt` file was read, and no prior agent or session transcript was consulted.

Gate drops: none were reported. Three candidates (007, 012, 013) were eliminated at Refute before
adjudication; they are discussed in §2 and carry no disposition.

## 1. Findings

### F-1 (candidate-001) — Phase 3 declared verified on a superseded review round — blocking

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 26 (§0.2) and ll. 1429–1435 (§11.3 item 2).

Claim: §0.2 records Phase 3 as **verified** on `PHASE_3_REVIEW_34.md`'s literal `PASS` with
`Interface changed: no`, and §11.3 item 2 issues a present-tense ruling that "Phase 3 is treated as
verified on the review evidence", explicitly so that a verify session reading Phase 3's own
not-verified status line does not conclude an unverified dependency was consumed.

Evidence: `PHASE_3_REVIEW_35.md` ll. 277–279 record `FAIL`, `Interface changed: yes`;
`PHASE_3_REVIEW_36.md` ll. 270–272 record `PASS-WITH-CORRECTIONS`, `Interface changed: yes`, and
ll. 287–292 state that because every admitted repair changes the monitored §5 region, "a fresh
whole-document verification round is required before Phase 3 can close or be consumed by a
dependent". Under DESIGN §G1.3 (ll. 354–359) a phase is verified when its *latest* verdict is PASS,
or PASS-WITH-CORRECTIONS with resolutions recorded **and no §5 change outstanding**; round 36 leaves
a §5 change outstanding. DESIGN §G5.3 item 1 (ll. 659–660) makes verified dependency status a
gating invariant binding on the consuming build session, and the only sanctioned exception in this
document's scope is the Phase 7 soft dependency (DESIGN ll. 630–632, target §0.3). No section of the
target mentions rounds 35 or 36.

Severity: blocking. This is not header bookkeeping: the target affirmatively adjudicates the
question in the opposite direction from the repository record, on the one dependency whose §5 has
since moved, and Phase 12's own §5 is derived wholesale from that region.

Touches interface/change-trigger region: **yes** — the ordered correction re-states the consumed
Phase 3 surface and obliges re-derivation of the affected §5.2 rows inside ll. 973–1117.

Fix: correct §0.2 and §11.3 item 2 to the actual latest state (round 35 FAIL; round 36
PASS-WITH-CORRECTIONS with `Interface changed: yes` and a fresh whole-document round required before
a dependent consumes it ⇒ not verified per §G1.3), record Phase 3 as a hard dependency consumed
while unverified, and mark the §5.2 rows touched by round 36's six corrections as owing
re-derivation once Phase 3 obtains a fresh literal PASS.

### F-2 (candidate-002) — schema gate hard-codes 3 while Phase 3 publishes 4 — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` ll. 398–399 (I-3), l. 409 (§4.2 step 1), l. 996 (§5.2),
l. 1259 (§8.1).

Claim under test: `PackFrontEnd.CURRENT_SCHEMA_VERSION` is 3 and any other value is rejected.

Evidence: P3 l. 341 declares `int CURRENT_SCHEMA_VERSION = 4;` and P3 §5.3 ll. 1644–1645 states the
constant is `4` and that every configuration produced by this revision publishes it; ll. 1659–1660
declare versions 1, 2 and 3 "incompatible with the current surface and … never upgraded by
inference". A Phase 12 implementation following §4.2 step 1 literally would therefore reject every
configuration Phase 3 actually publishes. I verified that no passage in the target states the gate
symbolically; all four sites carry the literal 3, and the cited anchor (P3 §5.3 l. 1593) is itself
stale.

Severity: correction. The retention rule itself is correct; only the constant and its anchor are
wrong.

Touches interface/change-trigger region: **yes** — l. 996 lies in ll. 973–1117 and the ordered edit
changes that row.

Fix: express the gate as `schemaVersion == PackFrontEnd.CURRENT_SCHEMA_VERSION` (currently 4) in
I-3, §4.2 step 1 and the §5.2 `PackConfiguration` row, repointing the citation to P3 §5.3
ll. 1644–1660.

### F-3 (candidate-003) — Phase 3 citations resolve to a superseded revision — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 26 (§0.2 extent), §3.1–§3.3, §4.1, §4.3.4, §4.4.3,
§4.5.1 and the §5.2 consumed-row table (ll. 993–1008). Candidate-008 reports the same defect over
the §3/§4.1 range and is dispositioned as a duplicate; this finding orders the whole re-resolution.

Claim under test: the target's `P3 §… l./ll. …` locators resolve to the binding text they quote.

Evidence, re-resolved by me: P3 §5 runs to l. 1689 (§6 opens at l. 1690), not to the target's stated
l. 1632 — the claimed end line falls inside P3 §5.2's consumed-Phase-1 table (l. 1628). The consumer
rules cited as ll. 1572–1575 are at ll. 1621–1624. The `sliders=` row cited as l. 731 is at l. 740
(l. 731 is the compile-time option-application row). The discovery ordering/kind/status/sanitized-
name statements cited as ll. 1526–1533 are at ll. 1575–1578. The offsets are **not uniform**
(−9, −12, −49, −50), so no bulk shift is valid; each citation needs individual re-resolution. The
drift is substantive rather than cosmetic — F-2's stale constant is a direct product of the same
superseded read — and nothing in the target flags a revision mismatch.

Severity: correction. Section anchors keep intent recoverable and the spot-checked semantics are
otherwise unchanged, so no design content need move.

Touches interface/change-trigger region: **yes** — the §5.2 rows at ll. 993–1008 sit in ll. 973–1117
and are edited by the ordered fix.

Fix: re-resolve every Phase 3 locator in §0.2, §1.3, §3, §4.1, §4.3.4, §4.4.3, §4.5.1 and §5.2
individually against the current `docs/phase3/v1/PHASE_3_DOC.md` (§5 = ll. 1420–1689), confirming in
the same pass that each consumed semantic is unchanged.

### F-4 (candidate-005) — `PackSelectionActions` published but never declared — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 250 (§2.2 parameter), l. 908 (§4.10.1), l. 984 (§5.1
exposed row).

Claim under test: a view adapter can implement `OptionScreenView.showPackSelection` from the
published interface without inventing the intent surface.

Evidence: §2.2 gives a full Java declaration for every other exposed type (`OptionEditSession`,
`ApplyOutcome`, `ReloadRequest`, `EngineSettingsModel`, `EngineSettingEntry`, `OptionScreenView`),
while `PackSelectionModel` and `PackSelectionActions` appear only as parameter names. §5.1 l. 984
binds them to the view adapters and describes "the intents a view reports back" without naming one
operation; §4.6.1 names the affordances (candidate selection, `Refresh`, `Open folder`, link into
options) only in prose. §4.10.1 l. 906 declares §2.2 to be "the entire surface a view must
implement", which raises the bar for leaving a row undeclared. The types are Phase 12's own, so no
other owner supplies the shape.

Severity: correction — behaviour is fixed in §4.6.1, so nothing contract-level is undecided; only
the published shape is missing.

Touches interface/change-trigger region: **yes** — the fix adds a declaration bound by the §5.1 row
inside ll. 973–1117.

Fix: declare `PackSelectionActions` in §2.2 covering at least select-candidate (by
`PackCandidateId`), refresh, open-folder, open-options and close, plus a one-line row shape for
`PackSelectionModel` matching the fields §5.1 already lists.

### F-5 (candidate-006) — engine-setting wire spellings unpublished — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 983 (§5.1) with §4.6.2–§4.6.3.

Claim under test: the named behaviour owners can parse the seven engine-setting keys from
`EngineOptionData` without guessing the string encoding.

Evidence: §5.1 l. 983 states values travel "as decoded strings inside `EngineOptionData`; each
behavior owner parses its own key", and §4.6.3 disclaims any Phase 3 interface change, so Phase 12
is the nominated owner of key names, domains and defaults. P3 §5.1 defines `EngineOptionData` only
as an opaque decoded-string map. The only canonical-text statement anywhere in the target is
§4.4.1 l. 539's `"true"`/`"false"` for pack switch options. Ambiguity is real, not theoretical:
Phase 3's own adjacent tri-state surface spells its states `DEFAULT/TRUE/FALSE` (P3 l. 692) while
Phase 12's enum is `DEFAULT/ON/OFF`, so Phase 9 or 10 can parse a spelling Phase 12 never writes.

Severity: correction. Keys, domains, defaults and semantics are all bound; only the token text is
missing. The `Choice` part of the candidate is weaker — those tokens are the owning phase's own
published list entries — and the fix is scoped accordingly.

Touches interface/change-trigger region: **yes** — the binding statement is referenced from the
§5.1 row at l. 983 and the fix amends it.

Fix: in §4.6.3, bind the persisted token for each `TriState` value and confirm the two `Toggle` keys
use §4.4.1's canonical `"true"`/`"false"` text; state that a `Choice` persists the owner-published
list entry verbatim, and reference the statement from the §5.1 row.

### F-6 (candidate-010) — row C-9's contract item is not what its cited §4.7 lines say — correction

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 334 (§3.3 row C-9).

Claim under test: RESEARCH §4.7 ll. 612–614 state that invalid programs produce a user-visible
error.

Evidence: the cited range says capability gates produce chat errors and that invalid programs
"delete themselves and fall back through backup chains"; nothing in ll. 612–616 attaches a
user-visible error to invalid programs. DESIGN §G4.5 ll. 583–585 does map per-program compile errors
to the GUI channel "per RESEARCH.md §4.7", so the framing is not invented — but as written, §3.3
(the contract-conformance trace) asserts a §4.7 statement §4.7 does not make, and the actual §4.7
obligation is left untraced. C-14 maps the §G4.5 channel separately and does not repair the
attribution.

Severity: correction — a conformance row misstating its authority is more than a note, but the
design behaviour described is coherent, so it is not blocking.

Touches interface/change-trigger region: **no** — l. 334 lies outside ll. 973–1117 and the fix
changes no interface row.

Fix: restate C-9's item as what §4.7 ll. 612–614 say and add DESIGN §G4.5 ll. 583–585 to the
provenance for the GUI-surfacing framing, keeping the existing P1 §4.9.4/§6 `SHADER_GUI` citations.

### F-7 (candidate-004) — `ReloadCoordinator.submit` is `void` yet §4.8.2 replies with an outcome — note

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 982 (§5.1) with §4.8.2.

On re-derivation three of the four claimed gaps are covered: §4.7.4 l. 830 assigns Phase 7 "the
drain point, the executing thread, and everything downstream of publication"; `merge` is exposed as
a public static function on the request record (l. 225) so the drain side can invoke it; §7
ll. 1151–1152 fix callers to the client thread and give Phase 7 the executing thread; and l. 1139
specifies the coordinator-absent failure mode. What survives is narrow: `submit` returns `void`,
`ApplyOutcome` (l. 213) carries only the produced request, and §4.8.2 says `/reloadShaders` "replies
with the outcome" without any reply shape anywhere in the document.

Severity: note — one clarifying sentence closes it; nothing prevents Phase 7 from implementing the
seam today.

Touches interface/change-trigger region: **no** — the ordered clarification lands in §4.8.2 and
changes no §5 row. (If the author instead chooses to give `submit` a return type, that would be an
interface change, but this finding does not order it.)

### F-8 (candidate-011) — C-10's pin-cite for the capability-gate chat error — note

Location: `docs/phase12/v1/PHASE_12_DOC.md` l. 335 (§3.3 row C-10).

The clause "capability gates produce chat errors" is at RESEARCH l. 612, not the cited l. 615.
However l. 615 is the continuation of the same failure-handling bullet and does substantiate the
row's second column ("the GUI additionally shows the resulting shaders-off state"), and the
neighbouring row cites the same bullet as a range, so the doc's pin-cites here are bullet-granular.
No substance is displaced.

Severity: note. Touches interface/change-trigger region: **no**.

Fix (not ordered): cite `§4.7 ll. 612, 615` so both clauses the row relies on are covered; a bare
swap to l. 612 would drop support for the second column.

## 2. Checked and clean

I accept the finder-reported clean areas after spot-verification:

- **Doc gate.** All 13 mandatory template sections (0–12) are present, correctly named and
  substantive. The four literal Phase 12 gate criteria hold: every App F.3/F.4 construct including
  `*` (F4-11), `<empty>` (F4-10), the red trailing-`!` (F3-5) and auto-widening past 18 (F4-13) is
  mapped; §4.7.3's reload-path × lifecycle matrix is closed and explicitly total, including the
  out-of-scope dimension-switch row; §10.1 gives OQ-9 a full question / procedure /
  success-failure / fallback structure per §G4.4; and the absence of a slider reference is stated
  explicitly (§4.4.3, §3.4, `[D-P12-5]`).
- **Phase 1 consumption.** Review 25 is the latest Phase 1 round and is a literal `PASS` with
  `Interface changed: no`; every §5.2b row maps onto Phase 1 §5.1/§5.3 without invention.
- **Phase 7 soft dependency.** §0.3 correctly invokes the single sanctioned §G5.3 exception, scoped
  by DESIGN ll. 630–632, with assumptions enumerated in §5.3(B) and a fix-up trigger recorded.
- **RESEARCH conformance.** The App F.3/F.4 citations in §3.1–§3.2 and most of §3.3 resolve
  faithfully, as do §3.4's Pintonium evidence lines.

Candidates refuted or reclassified on my re-derivation:

- **candidate-004** was reduced from correction to note: slot ownership, merge site and thread
  affinity are all fixed elsewhere in the target; only the `void submit` / "replies with the
  outcome" mismatch survives.
- **candidate-011** was reduced to a note: the cited line is intra-bullet and supports half the row.
- **candidate-009** is dropped as an exact duplicate of candidate-002 (same four sites, same stale
  constant, same fix); admitting both would double-count one defect. Its stale-anchor observation is
  folded into F-2.
- **candidate-008** is dropped as a duplicate of candidate-003 — one stale-revision read producing
  drifted locators. F-3 orders the re-resolution across §3 and §4.1 as well as §0.2 and §5.2, and
  F-2 carries the substantive constant, so nothing from candidate-008 is lost.

Candidates eliminated before adjudication (settled, no disposition returned): **candidate-007**
(`Choice` list sourcing) fell to the refuters because §4.6.2 states Phase 12 renders whatever
ordered list the owning phase publishes, so no separate consumed shape is owed; **candidate-012**
(the "8 minus AA/AF = 7" arithmetic in row C-5) did not survive re-derivation against the cited
list; **candidate-013** (§3.4 rows citing Pintonium source lines without a PD section) did not
survive against the §G9 item 3 REV1 text. There were no Gate drops.

## 3. Verdict

# FAIL
Counts: blocking=1; corrections=5; notes=2
Interface changed: yes

Interface disposition: five admitted findings (F-1 through F-5) order edits inside the declared
change-trigger region `cross-phase-interfaces` (`docs/phase12/v1/PHASE_12_DOC.md` ll. 973–1117), so
the flag derives as `yes`. F-6, F-7 and F-8 order no edit to that region.

Trend/convergence: this is round 1 and the supplied trend is empty, so no convergence claim is
available and none is asserted.

FAIL rather than PASS-WITH-CORRECTIONS is driven solely by F-1: the target consumes and
affirmatively re-certifies a dependency that the repository record shows is not verified and whose
§5 has since moved under `Interface changed: yes`. That is a gating-invariant violation (DESIGN
§G5.3 item 1), not a fix-up-sized wording repair, because the correctness of Phase 12's own consumed
surface cannot be established until Phase 3's §5 settles.

Next required action: a scoped repair session that (1) corrects §0.2 and §11.3 item 2 to Phase 3's
actual latest state and records the consumed §5.2 rows as owing re-derivation, (2) re-resolves every
Phase 3 locator individually against the current dependency doc, (3) fixes the schema gate to
`CURRENT_SCHEMA_VERSION` (currently 4), (4) declares `PackSelectionActions`, (5) publishes the
engine-setting token spellings, and (6) repairs row C-9's attribution. Because every one of those
except (6) changes the monitored §5 region, a fresh whole-document verification round is required
before Phase 12 can close — and the Phase 3 re-derivation cannot be finally discharged until
Phase 3 itself obtains a literal `PASS`.
