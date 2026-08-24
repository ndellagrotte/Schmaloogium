# Phase 12 — Verification Review, Round 3

Target: `docs/phase12/v1/PHASE_12_DOC.md` (Options GUI, persistence & reload)
Manifest: `verification/targets/phase-12.json`

## 0. Method and reading order

I re-derived every surviving candidate directly from the target and its authoritative sources
before reading any prior review. Sources consulted this session:

- Target: `docs/phase12/v1/PHASE_12_DOC.md` — §2.2 (ll. 249–366), §3.3 (ll. 433–453), §4.6.3
  (ll. 869–887), §4.7.2 (ll. 899–912), §5.1 (ll. 1159–1173).
- Authority: `docs/design/v3/DESIGN.md` §G4.1 (ll. 545–550), Phase 12 spec and doc gate
  (ll. 2357–2435); `docs/research/v1/RESEARCH.md` §4.7, §4.8, App F.3/F.4.
- Dependencies: `docs/phase3/v1/PHASE_3_DOC.md` binding region (ll. 1420–1689), in particular
  ll. 1571–1585; `docs/phase1/v14/PHASE_1_DOC.md` binding region.
- Prior reviews `docs/phase12/reviews/PHASE_12_REVIEW_1.md` and `…_2.md`, read last.

No network use. No subagents spawned by me; the candidate set arrived pre-adjudicated from the
engine's finder/refuter/steelman/Gate pipeline. No forbidden sources (`docs/**/chatlogs/**`,
`*.txt`, transcripts) were read. Gate drops: none. Two candidates were eliminated before
adjudication (candidate-003, candidate-005) — discussed in §2.

Deviation: none.

## 1. Findings

### F-1 (candidate-006) — §3.3 row C-15 asserts a §2.2 fact that is false for four of ten terms

**Location.** `docs/phase12/v1/PHASE_12_DOC.md` §3.3 row C-15, l. 451, against §2.2 ll. 254–366.

**Claim.** C-15 discharges DESIGN §G4.1 (pack-facing vocabulary used verbatim, ll. 545–550) with
the satisfying element "`profile`, `screen`, `slider`, `<empty>`, `*`, `columns`, `option`,
`value`, `prefix`, `suffix` all appear unrenamed in the model's type and field names (§2.2)".

**Evidence.** Re-resolved independently: §2.2 renders `<empty>` as `record Blank()` with the pack
token surviving only in a trailing comment (l. 277); `*` appears only inside the comment on l. 262
(`// post-`*`-expansion`) and cannot be a Java identifier; `prefix` and `suffix` do not occur in
§2.2 at all — they are honored in §4.3.5 (l. 635) and rows F3-14/F3-15 (ll. 407–408). §3.2 row
F4-10 (l. 428) itself maps `<empty>` to `Blank`, contradicting C-15 on the doc's own terms. The
underlying §G4.1 obligation *is* met by the document as a whole; only the row's stated location and
scope are wrong. In a conformance map whose discipline is "zero unmapped rows", a row whose
evidence pointer is false leaves the §G4.1 mapping unverifiable at the place it points.

**Severity.** correction. Not blocking: no design content changes, only the justification cell.

**Touches interface/change-trigger region: no.** The fix edits §3.3 l. 451; the declared
interface region is §5.1 ll. 1157–1314 and is untouched.

**Ordered fix.** Restate C-15's design-element cell truthfully: keep the terms that do survive as
§2.2 identifiers (`profile`→`ProfileCycle`, `screen`→`PresentationScreen`/`ScreenId`,
`slider`→`SliderOption`, `columns`→`resolvedColumns`, `option`→`OptionId`, `value`→`rawValue`), and
state that `<empty>` is realized as `Blank` (pack token retained in the adjacent comment because
`<empty>` is not a legal Java identifier) while `*`, `prefix` and `suffix` are used verbatim in the
§4.3.3/§4.3.4/§4.3.5 prose and rows F3-14/F3-15/F4-10/F4-11.

### F-2 (candidate-001) — `shaderPack` token domain states no sentinel-vs-name precedence

**Location.** §4.6.3 ll. 875–887; mirrored in the §5.1 row at l. 1169.

**Claim.** The new persisted-selection binding fully specifies the token domain and its
re-resolution rule.

**Evidence.** The domain is a flat string space: `off` | `(internal)` | a sanitized display name
byte-for-byte (l. 877). The only tie-break stated is candidate-vs-candidate (l. 886). Phase 3
(ll. 1577–1579) places no lexical constraint on sanitized display names, so a filesystem candidate
sanitizing to exactly `off` or `(internal)` is not excluded. However, every reading resolves
deterministically to a defined, benign outcome: either the sentinel is read (shaders off /
internal pack), or the name fails to match an `AVAILABLE` candidate and the doc's own rule
(l. 884–885) resolves to `off` with a `schmaloogium.config` warning. Phase 3 also orders `Off` and
`Internal` first as candidates (P3 ll. 1574–1575), which the stated tie-break then decides in the
sentinel's favour. No consumer can be left with divergent restore behavior; only a user with a pack
folder literally named `off`/`(internal)` observes the edge, and the observable effect coincides
with an already-licensed degradation.

**Severity.** note — an explicitness gap, not an under-specified consumer-hittable behavior.
Notes are not ordered for fix-up.

**Touches interface/change-trigger region: no.** The §5.1 row delegates the binding to §4.6.3
("Phase 12 writes it (§4.7.2) and binds it (§4.6.3)"), so the optional clarification lands in
§4.6.3 (l. 886) outside ll. 1157–1314. The proposed reserved-prefix/`pack:` grammar change is
rejected: it would rewrite a published token domain to close an edge the existing rule decides.

**Suggested (not ordered) wording.** After the tie-break in §4.6.3: "`off` and `(internal)` are
always read as the sentinels; a filesystem candidate whose sanitized display name equals either
spelling loses the tie (P3 l. 1575) and is warned as unrestorable."

### F-3 (candidate-002) — no §8.1 test hook or §12 checklist item names the `shaderPack` key

**Location.** §8.1 ll. 1381–1503; §12 items 10–11 (ll. 1755–1761).

**Claim.** The round-2 edit binding the persisted pack selection is consistent with the doc's own
testability/checklist coverage discipline.

**Evidence.** `shaderPack` occurs only at ll. 160, 877, 1169; no `selection_*` or
`engineSettings_*` test names it, and items 10 (seven engine settings) and 11 (in-memory selection
model) omit it — unlike `engineSettings_wireTokensAreExactlyTheSevenPublishedSpellings` (ll. 1460–
1462), which asserts the parallel §4.6.3 table literally. But the write obligation is already
normative in §4.7.2's trigger table (l. 904, "any pack selection change" →
`GlobalShaderOptionsCodec`, covered by item 10's global write path), and re-resolution is
explicitly Phase 7's duty (l. 884). So the contract is complete; only the doc's own coverage
bookkeeping lags, and the candidate's proposed re-resolution test would test another phase's duty.

**Severity.** note.

**Touches interface/change-trigger region: no.**

**Suggested (not ordered) wording.** Add one write-side §8.1 test asserting the §4.6.3 `shaderPack`
row literally, and extend §12 item 10's hook sentence to include writing the key on selection
change.

## 2. Checked and clean

- **candidate-004** — duplicate of F-2 (same sentinel/name collision, same §4.6.3 and §5.1 lines,
  same recommended sentence). Dropped as redundant rather than as refuted; the substance is
  admitted once, as note F-2, and counting it twice would inflate the note count.
- **candidate-003** (§5.1's ":engine" closing sentence vs the new file-key row) and
  **candidate-005** (§5.1 row omitting case-sensitivity/tie-break, relying on the §4.6.3
  cross-reference) were eliminated by strict refuting majority before adjudication. I concur on
  re-derivation: l. 1172–1173's sentence is scoped "every row above `OptionScreenView` lives in
  `:engine`", which is true of the key's owning code, and the §5.1 row's explicit delegation to
  §4.6.3 is a legitimate in-document pointer, not a missing binding.
- **New-surface lens clean areas confirmed.** §0.6's three fix-up claims resolve at their cited
  sites: §4.7.3's F3+R and `/reloadShaders` rows both read `FULL` with `worldRendererReload` = yes,
  consistent with the below-matrix unconditional rule and `reload_fullAlwaysSetsWorldRendererReload`;
  §4.5.3's write-through now cites DESIGN.md l. 2381 while §4.7.2 independently cites RESEARCH
  §4.7 ll. 604–605, with no residual double-attribution; §4.6.3's engine-settings tokens, §2.2's
  `TriStateValue`, §4.6.2's defaults, `[D-P12-19]` and the §5.1 engine-settings row all agree on
  `default`/`true`/`false`.
- **Interfaces lens clean areas confirmed.** Every consumed Phase 3 row in §5.2 resolves inside the
  manifest-selected binding region (ll. 1420–1689); the provisional/⟳ marking is honest about
  round-36 drift; §5.3 flags rather than assumes the Phase 4/Phase 7 consumptions; §5.4's four
  requests refuse to invent Phase 3 surface and state interim assumptions with named tests.
- **Conformance lens clean areas confirmed except C-15.** App F.3/F.4 rows are complete against the
  doc gate and the locators I re-resolved into RESEARCH.md land on supporting text, including the
  lang-key rows (1468 vs 1469) and the const-visibility predicate (1465–1466). Phase 3 locators for
  sliders, profile tokens, the column formula, `*`-expansion deferral, discovery ordering, closed
  enums, sanitized display names and the persisted-selection rule (P3 ll. 1583–1585) resolve
  correctly; the `EngineOptionData` off-by-one (cited 1601–1604 for a sentence spanning 1602–1605)
  still lands on supporting text and is below finding threshold.
- **Prior rounds.** Round 2's three corrections (F-1…F-3 there) were verified as discharged at
  their cited sites; C-15 was not previously examined, so F-1 here is new material rather than a
  regression or a re-litigation.

## 3. Verdict

# PASS-WITH-CORRECTIONS

Counts: blocking=0; corrections=1; notes=2
Interface changed: no

Interface disposition: no admitted finding orders an edit to the declared change-trigger region
`cross-phase-interfaces` (§5.1, ll. 1157–1314). The round-2 interface change is therefore not
re-opened by this round, and after the ordered fix-up the region will be unchanged relative to the
state entering round 3.

Trend/convergence: round 2 was PASS-WITH-CORRECTIONS with 3 corrections / 2 notes and an interface
change; round 3 is PASS-WITH-CORRECTIONS with 1 correction / 2 notes and no interface change. The
remaining correction is a single false justification cell in a conformance row, confined to one
line and touching no design content. This is convergent; no thrash and no convergence warning.

Next required action: apply the F-1 fix-up to §3.3 row C-15 (and only that), then run one further
verify round. Notes F-2 and F-3 are recorded, not ordered, and must not gate closure.


## Resolutions

### F-1 — applied

Re-derived independently before editing. §2.2 (ll. 254–296) contains `ProfileCycle`,
`PresentationScreen`/`ScreenId`, `SliderOption`, `resolvedColumns`, `OptionId` and `rawValue`, so
six of the ten terms do survive as identifiers; `Blank` carries `<empty>` only in the trailing
comment on l. 277; `*` occurs in §2.2 solely inside the l. 262 comment; `prefix`/`suffix` occur
nowhere in §2.2 but are honored verbatim at §4.3.5 l. 635 and rows F3-14/F3-15. The §G4.1
obligation itself holds document-wide, so only the row's evidence pointer was wrong. Row C-15
(now l. 462) restates the mapping per term and points `<empty>`→`Blank` (with F4-10) and
`*`/`prefix`/`suffix` at §4.3.3–§4.3.5 and rows F3-14/F3-15/F4-10/F4-11. Provenance
(`DESIGN.md ll. 545–550`) unchanged; no design content, decision ID, test, or checklist item moved.
Neighbors C-14 and C-16 were re-read and are unaffected.

A new `### 0.7 Round-3 fix-up` subsection records the change in §0's existing style (no
supersession sentences, no dated-claim ranges, no session bookkeeping).

### Notes deferred

- **F-2 (`shaderPack` sentinel-vs-name precedence)** — not applied: recorded as a note, not
  ordered. Adding the clarification would edit the published token domain's rule text in §4.6.3 on
  an edge the existing tie-break plus the `off` fallback already decide deterministically; that is
  new design surface, not a correction.
- **F-3 (no §8.1 test / §12 item names `shaderPack`)** — not applied: recorded as a note, not
  ordered. The write obligation is already normative in §4.7.2's trigger table (l. 904) and
  re-resolution is Phase 7's duty (l. 884); adding a test and checklist text would create new
  unreviewed surface without closing a contract gap.

### Interface disposition

The declared change-trigger region `cross-phase-interfaces` (§5.1) was not edited. The only target
edits are §3.3 row C-15 and the new §0.7 subsection; both sit above §5.1, so the region's content
is unchanged although its line numbers shift by the 11 inserted §0.7 lines.

### Refusal

None.