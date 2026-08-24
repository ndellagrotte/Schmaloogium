# Phase 11 — Verification Review, Round 8

Target: `docs/phase11/v1/PHASE_11_DOC.md` (whole document)
Manifest: `verification/targets/phase-11.json`
Governing design revision: `docs/design/v3/DESIGN.md` (v3, override selection)

## 0. Method and reading order

Sources read before judgment: the target regions cited by every candidate and their surrounding
context (§0.2 and the full §0.4–§0.10 addendum block, §4.1, §4.4, §4.10, §5.1, §11.2), plus a
whole-document grep for `DeclarationKind` and for the published enum declarations; the DESIGN v3
Phase 11 target spec and doc gate; and the manifest-declared interface region span
(`docs/phase11/v1/PHASE_11_DOC.md:962`–`:1055`). Each candidate's citation was re-resolved
line-exact against the primary text before any prior review was opened. Prior reviews (Rounds 1–7)
were read last, adjudicator-last, only to check settled material and recurrence.

Deviations: none. No forbidden source was read — no `docs/**/chatlogs/**`, no `*.txt`, no prior
agent or session transcript. No network use. No subagent fan-out from this session; the candidate
set was supplied by the engine. Gate drops: none. Candidates eliminated before adjudication: none.

## 1. Findings

### Finding 1 (candidate-001) — the Round-7 addendum falsifies the two self-referential "latest addendum" verification-state claims

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:68`–`:71` (§0.2), `:133`–`:138` (§0.10),
  `:1280`–`:1282` (§11.2 item 4).
- **Claim under test:** "the latest of those addenda made the most recent §5 change, so a fresh
  verification round is required before closure" (§0.2), and "the newest of those addenda states
  the fresh verification still required" (§11.2 item 4).
- **Evidence:** the newest addendum is §0.10 (Round 7); §1 begins at line 142, so no later
  addendum exists. §0.10 states the opposite of both claims: "No §5 text changed and no contract
  substance changed", and it contains no fresh-verification sentence. The addendum that actually
  made the most recent §5 change and recorded the still-required round is §0.9 (Round 6):
  "Its §5 citation changes require another fresh verification round before closure" (`:131`).
  Nothing between §0.2 and §11.2 reconciles the two pointers with §0.10.
- **Severity:** correction. I weighed the steelman that the doc gate (`docs/design/v3/DESIGN.md`
  `:2344`–`:2349`) imposes no chronology requirement and that §0.2's own operative conclusion
  ("a fresh verification round is required before closure") remains true, which would reduce this
  to a note. It fails on this document's own settled precedent: Round 6's Finding 1 adjudicated
  precisely this defect class — a false self-statement of verification state routing a consumer to
  the wrong place — as a correction, and the single-place chronology rule adopted in that fix-up is
  exactly what the Round-7 addendum broke. A closure consumer directed to the newest addendum lands
  on text asserting no §5 change and no still-required round.
- **Fix ordered:** append one sentence to §0.10 stating that §0.9's §5 citation change still
  requires a fresh verification round before closure; alternatively repoint §0.2 and §11.2 item 4
  to §0.9 by name. Do not restructure the single-place chronology rule, and re-derive the addendum
  identity from the current text rather than adopting §0.9 verbatim.
- **Touches interface/change-trigger region:** no. §0.2, §0.10 and §11.2 all lie outside
  `:962`–`:1055`, and the ordered edit changes no §5 text.

### Finding 2 (candidate-002) — the published compile request references an undeclared `DeclarationKind` enum

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:448` (§4.1), incorporated by §5.1 row at `:968`.
- **Claim under test:** §5.1 publishes the "exact `compile(request)` and `compilePhase3(...)`"
  shapes.
- **Evidence:** a whole-document grep resolves `DeclarationKind` to exactly one line, `:448`, where
  it types a component of the published `CustomExpressionSource` record. Every other enum used in
  the published shapes is declared inline — `ExpressionType` at `:261`, Phase 3's
  `CustomExpressionKind { UNIFORM, VARIABLE }` at `:993`.
- **Severity:** note. The variant set is determinate without guessing: §4.1 (`:479`–`:482`) states
  the adapter "converts the two closed enums by equal-named variant" from `CustomExpressionDecl`,
  whose kind enum is published verbatim at `:993`. What remains is a missing one-line declaration
  against the document's own convention, not an unspecified semantic or a wrong contract claim.
- **Recommended (not ordered):** add `enum DeclarationKind { UNIFORM, VARIABLE }` beside
  `CustomExpressionSource` in §4.1.
- **Touches interface/change-trigger region:** no. The remedy sits at §4.1 line 448, outside
  `:962`–`:1055`; as a note it orders no edit at all, and none inside the region.

### Finding 3 (candidate-003) — §4.4/§5.1 describe `ExpressionContextSchema` as carrying content its published record does not

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:819` (§4.10) vs `:585`–`:586` (§4.4) and `:972`
  (§5.1).
- **Claim under test:** the schema "carries an immutable sorted map … and the fixed fourteen-name
  boolean set".
- **Evidence:** the declared record has two components,
  `ExpressionContextSchema(String version, Map<String, Integer> biomeConstants)`; no boolean-name
  component exists.
- **Severity:** note. Equivalent coverage removes the consumer risk: §3.3 (`:396`, `:401`–`:404`)
  fixes the fourteen `is_*` names as a contract-level language constant sourced from the context
  snapshot, and §4.10's binding `ViewEntityFlags` record (`:813`–`:817`) enumerates exactly those
  fourteen fields in the same block. §4.10 also states "Schema fields are non-null immutable
  copies" (`:824`), so "immutable sorted" constrains the value rather than contradicting the
  declared `Map` type. Since §5.1's closing clause makes the §4.10 declarations binding, the exact
  record shape governs and no implementer can construct a third component. This is summary-prose
  imprecision, not a defect a consumer can hit.
- **Recommended (not ordered):** reword §4.4 `:585`–`:586` and the §5.1 row so they describe the
  schema as carrying only the immutable, deterministically ordered `BIOME_*` map, with the fourteen
  names named as a fixed language constant delivered via `ViewEntityFlags`.
- **Touches interface/change-trigger region:** no. As a note it orders no edit; the cited §5.1 row
  sits inside `:962`–`:1055`, but no correction is ordered there and the region's change trigger
  does not fire.

## 2. Checked and clean

**Candidate re-derivation.** All three candidates were factually confirmed on independent
re-derivation; two were downgraded to notes on impact (Findings 2 and 3), consistent with their
refuters. No candidate was refuted outright. No candidate was eliminated before adjudication and
there were no Gate drops, so there is no pre-settled eliminated material to discuss.

**New-surface lens.** I accept the reported clean areas after spot-verification: the interface
region declared at `:962`–`:1055` lines up with §5 after the §0.10 insertion; §0.10's four claimed
repoint sites are consistent with the Round-7 resolutions; the DESIGN five-name versus RESEARCH
seven-name exclusion divergence remains correctly handled as D-P11-9 plus a §11.4 upstream request
across §3.3, §11.2 item 1 and §11.4.

**Interfaces lens.** The consumed Phase 6 grants (`:1200`–`:1205`, `:1207`–`:1225`, `:1226`–`:1250`,
`:1253`–`:1259`, `:1283`–`:1286`, `:1325`–`:1334`, `:1389`–`:1391`) and the Phase 3 publication row
and algebra (`:1442`, `:1459`–`:1480`) support the §5.2/§5.3 claims; §4.8's refresh ledger,
§4.12's lifecycle map and §5.5's composition handoff are implementable as written. Apart from
Findings 2 and 3, both note-level, no interface mismatch survived.

**Conformance lens.** Doc-gate coverage remains complete: every App F.6 token, operator,
declaration form, named function and constructor, member/matrix access, input family, exclusion
union, cadence rule and precipitation handoff is a mapped row with provenance, alongside the smooth
state machine, evaluator interface and selection criteria, the error ladder, and the stareval
license outcome. The mandatory thirteen-section structure is present; the phase carries no assigned
open question.

## 3. Verdict

# PASS-WITH-CORRECTIONS

Counts: blocking=0; corrections=1; notes=2
Interface changed: no

Interface disposition: no admitted finding orders an edit inside the manifest-declared
`cross-phase-interfaces` region (`docs/phase11/v1/PHASE_11_DOC.md:962`–`:1055`). The single ordered
correction touches §0.10 (or §0.2/§11.2), all outside that region, so the region's change trigger
does not fire on this round's ordered work.

Trend and convergence: Round 6 closed with 5 corrections and an interface change; Round 7 with 4
corrections and none; Round 8 has 1 correction and 2 notes, none interface-affecting. The
line-anchor drift class that dominated Rounds 6–7 is fully discharged — every re-checked external
anchor resolves. The one surviving correction is a self-inflicted recurrence of Round 6's
chronology defect: each fix-up appends an addendum that invalidates the "latest addendum" pointers.
Convergence is real and monotone; no convergence warning is warranted, but the fix-up must make the
chronology statement addendum-insertion-proof rather than patch it again.

Next required action: apply the one ordered correction in a Round-8 fix-up, preferring a form that
survives future addendum insertion (name the specific addendum, or have the newest addendum always
restate the outstanding verification requirement). The two notes are not ordered. If the fix-up
leaves §5 untouched, no interface change trigger fires from this round.

## Resolutions

**Finding 1 (correction) — applied.** Re-derived independently: the newest addendum before this
fix-up was §0.10 (Round 7), which states "No §5 text changed and no contract substance changed" and
carries no fresh-verification sentence, while §0.2 and §11.2 item 4 asserted that the latest
addendum made the most recent §5 change and stated the still-required round. Both pointers were
false. Rather than patching the pointer to §0.9 by name only, the fix makes the rule
insertion-proof: §0.2 and §11.2 item 4 now say the newest addendum always restates whether a fresh
verification round is still required, and each names the outstanding item (§0.9's §5 citation
change). A new `### 0.11 Round-8 contract corrections` addendum was added in the existing §0
header style; it restates the outstanding requirement, so the invariant holds for the current
newest entry and for any future addendum that follows the stated rule. The single-place chronology
rule was not restructured. No §5 text was touched and no edit lands inside the declared interface
region `docs/phase11/v1/PHASE_11_DOC.md:962`–`:1055` (§5 content is unchanged; only line numbers
below §0 shift by the nine added addendum lines).

### Notes deferred

**Finding 2 (note) — deferred.** `DeclarationKind` remains undeclared at §4.1. Not applied because
the finding is note-level and adding a new enum declaration beside a published record would create
new normative surface in the compile-request shape incorporated by §5.1, which is the interface
region's subject matter; the variant set is already determinate via the equal-named conversion from
`CustomExpressionKind { UNIFORM, VARIABLE }` at `:993`. Deferred to a round that orders it.

**Finding 3 (note) — deferred.** The §4.4/§5.1 summary prose about `ExpressionContextSchema` is
imprecise but non-binding: §5.1's closing clause makes §4.10's two-component record govern, and the
fourteen `is_*` names are fixed by §3.3 and delivered via `ViewEntityFlags`. The recommended
rewording would edit a §5.1 row inside the interface region, firing its change trigger for a
note-level imprecision no implementer can hit. Not applied.

**Refusal:** none.