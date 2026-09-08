# Phase 12 — architecture review 10

**Date:** 2026-09-08. **Frozen document:** `docs/phase12/v1/PHASE_12_DOC.md` SHA-256 `bf36b70d669ea529b233ce1bb6b50447f83bf3be0e5224fac463607ce0d39030` (recomputed this session; match). **Verdict:** PASS-WITH-CORRECTIONS (0 blocking, 1 correction, 3 notes). **§5 impact:** none — the single correction re-points evidence coordinates only; every recomputed §5/§4 consumption matched the dependency docs' current bytes, so no interface row changes.

---

## 1. Scope, identity and authority actually verified

Read this session, in this order, against current bytes: the complete frozen phase doc (ll. 1–2269);
`docs/design/v3/DESIGN.md` Part I (ll. 1–1136) and the Phase 12 spec (ll. 2357–2434); the Phase 4
reload-safety row (ll. 1560–1563) and Phase 7 spec rows (ll. 1850–1886, 1967–1969) that the phase
doc cites under its §0.4 item 1; `docs/research/v1/RESEARCH.md` ll. 11–107 (§0–§1) plus the
doc-cited App E.2 (ll. 1420–1433), App F.1/F.2 (ll. 1442–1452), App F.3/F.4 (ll. 1454–1480), §4.7
(ll. 601–618), §7.6 (ll. 873–878), §9 (ll. 940–955), §11 OQ-9 row (l. 1015), App H (ll. 1554–1587);
dependency docs' §0 header, §1, §5 and §11: `docs/phase1/v14/PHASE_1_DOC.md` (§5 whole ll.
5420–5552, package tables, §4.9.4 routing text, §6/§7/§9 rows, §11.3 item 2, §11.4 "To Phase 12"),
`docs/phase3/v1/PHASE_3_DOC.md` (§0 header ll. 3–594 incl. §0.58–§0.65, §1 ll. 595–651, §2.2
declarations, §3.2 rows, §5 ll. 3343–4358, §6, §7, §11), and the soft dependency
`docs/phase7/v1/PHASE_7_DOC.md` (§0 header, §1, §5 incl. ll. 3336–3560 and §5.4/§5.5, §11).
Forbidden sources (`reference-src/**`, `docs/**/chatlogs/`, root `*.txt`, every other
`PHASE_*_REVIEW_*.md`, `docs/build/**`, `docs/PHASE_INTEGRATION_REVIEW.md`) were not opened.
No build, test, Gradle, GL or network execution was performed; this is a documentary review.

Identity recomputed at current bytes: P3 publishes `PackFrontEnd.CURRENT_SCHEMA_VERSION = 23`
(`PHASE_3_DOC.md:704`, restated `:4161`), `MaterializedSource-v23` (`:3309`, `:4212`), exact
containing/nested/inspection gating (`:4207–4212`), `projectionVersion=1` with nine source-free
trees (`:4215`) — the phase doc's schema23/MaterializedSource-v23/projectionVersion1 adoption
(D-P12-36, §4.1 I-3, §5.2, §8.1) is the current identity, and its schema22/21/20/19 receipts are
each explicitly marked historical inside the doc. D-P12-39's "no P7 read-back" premise checks out
at the owner: P7's `ProgrammaticOptionSnapshot(PipelineIdentity, OptionConfiguration,
EngineOptionData)` carries no profile field (`PHASE_7_DOC.md:3414–3415`) and D-P7-60 exists
exactly as cited (`:4535`, acceptance detail `:3497–3500`). P12 cites no current capture-plan,
run-manifest, health-ledger or P13 parameter identity, so none of those needed reconciliation.

## 2. Scope and §5 audit

**Doc gate (spec ll. 2423–2427): met.** The conformance map covers every App F.3/F.4 construct —
F3-1…F3-20 and F4-1…F4-13 with zero unmapped rows — including `*` (F4-11 + §4.3.3's three decided
questions), `<empty>` (F4-10 + `Blank`), red-"!" (F3-5 + §4.3.5), and auto-widen (F4-13 + §4.3.4).
I spot-checked all 33 F-row claims against the cited RESEARCH text; every one is semantically
faithful (e.g. F3-4/F3-5 vs `RESEARCH.md:1457–1458`; F4-12/F4-13 vs `:1480`; the column examples
"18/19/27/28 → 2/3/3/4" and "explicit 1 with 19 → 3" recompute correctly under
`max(explicitColumns.orElse(2), ceil(expandedSlotCount/9))`). The reload × lifecycle matrix
(§4.7.3) is total over `ReloadCause`'s eight values, classifies dimension switch as explicitly
not-a-trigger with its owner named, and `reload_triggerMatrixIsTotal` enforces totality. The OQ-9
spike carries the full §G4.4 protocol (verbatim question, procedure a–g, success/partial/failure
criteria, complete fallback, recording rule). All thirteen §G9 sections are present and
substantive.

**Interface honesty: verified in both directions.** Everything §4/§5.2/§5.3 consumes exists in the
owners' current §5s: `PackFrontEnds.create`/`PackFrontEndServices` (:3353), discovery/reference
rows with the five `FilesystemCandidateResolution` outcomes (:737–742) and the six
`PackOptionsTargetRejection` reasons (:748–750), closed `PackCandidateKind`/`PackCandidateStatus`
(:734–735), payload-free `ScreenProfileEntry()` with the explicit P12/P2 contract paragraph
(:3373–3380), `profiles()`/`constraints()`/`disabledPrograms()`/`Inferred|InvalidState`
(:1011–1024), `Map<String,LangDecorations> localizedDecorations` (:1010, R-P12-5 grant closed at
:4352–4356), `InternalOptionFailure{NULL_INPUT,FOREIGN_CATALOG,NOT_INTERNAL,INVALID_STATE}`
(:1002), the eight-key `EngineOptionData` domain with defaults
`true,true,1.0,1.0,0.125,default,default,0` and zero-only `antialiasingLevel` (:3452–3461,
:3493–3496), D-P3-64's user→pack→true fallback (:3506–3510), obsolete-spelling non-aliases
(:3497–3498), and the column rule with all-slot counting (:2269–2273, §5.1 row :3363). On the P7
side, every §5.3 adapter is a literal match: `ReloadCoordinator.submit(ReloadRequest)` with
`NONE|REPUBLISH|FULL` and independent OR flags (:3340–3343, :1511–1515),
`RegistryGenerationListener` (:3407), `ReloadDrainReceipt`/`ReloadOutcomeSource` (:3408–3410),
`ExpressionDiagnosticGuiSource` plus the exact snapshot/entry shape and P12 polling/lifetime rules
(:3411–3413, :3536–3549), `ProgrammaticOptionBridge` (:3416–3420), `InternalOptionCommitter`
(:3421–3424), H-RESOURCE-01's pre-destructive AROUND wrap with listener-merge and no second
reacquisition (:1547–1550, :1819, :3336–3338, :3386–3390), and P1's package grants, seam
constraints, `EngineDiagnostic`/`UserChannel` routing with the per-pack `SHADER_GUI` store,
mod-dependency mechanics row naming 12, and the "To Phase 12" hand-off. The doc's exports are
specified with receiver adoption recorded (P7 §5.5/§12 items; P3 §11.4; P2's goldens kept as
future work — §8.2/§11.4/§9 explicitly do not claim executed conformance or the pending P2
R15/R16 goldens).

**Scope discipline: held.** Scope-in is fully designed (pack selection §4.6, generated screens
§4.3, sliders §4.4.3, profiles §4.5, persistence timing §4.7.2, reload paths §4.8, error
surfacing §4.9, OQ-9 §10); scope-out (option parsing → P3 under I-1's no-re-parsing invariant,
reload internals → P4–7, ModularUI licensing note → P1) is respected, with anti-sprawl rows for
every adjacent concern (§1.2). D-1…D-10 are honored (§11.2); the design authors no Mixin (App E.2
`RESEARCH.md:1427` is correctly quoted; all three platform bindings are Forge mechanisms).
Licensing/Pintonium compliance is in order: §0.5's provenance posture, `[V:observed — Pintonium
path:line]` tags on every PD-derived claim, the §3.4 disposition table handling every touched
do-not-inherit row (column clamp rejected against App F.4, unconditional `loadRenderers` rejected
as a non-contract internal, `sliders=` absence confirmed not inherited, PD §17/§18 not engaged
with the two GUI-relevant rows shown satisfied), and no trace of glsl-transformation-lib or
stareval.

**Recent-change identities (D-P12-37/38/39 and the brief's cross-checks): integrated and
consistent.** D-P12-39's revalidation-on-replacement semantics appear coherently in §4.5.2's
transition table, §4.5.3, §5.1's edit-session row, §5.3(E), §8.1's
`profile_summaryTransitionLifetime`, and the decision log — never implying a P7 read-back. The
columns rule retains the IR-16 all-slots counting; callback rechecks precede every
preview/intent/I/O/reload (§4.4.4); the pending-profile summary is immutable presenter-prepared
data delivered through the existing `showOptions` to both views independently of inference and
count (§4.5.2/§4.10.1); typed/source-less P11 delivery and the complete seven-entry settings view
with three exact-value intents are as specified; R-P12-5 and Internal session-only mutation are
recorded as fulfilled-architecturally with owner grants verified closed in P3 §11.4/§11.5.

## 3. Required corrections

**Sites.** C1 — dependency-doc line coordinates are stale against current bytes. The doc's P1 and
P3 citations in §§0.2/1.2/2.1/3/4/5/6/8/11 pin the pre-amendment layouts: `PHASE_1_DOC.md` has
grown to 6995 lines (§0.24–§0.34 added 2026-09-07/08) and `PHASE_3_DOC.md` to 5521 lines (schemas
14→23, addenda §0.55–§0.65), so nearly every `P1 §n l. k` / `P3 §n l. k` pin now lands in the
wrong section. Representative recomputations (all content-bearing, all verified to still exist):
P12 §2.1 "P1 §2.1 l. 1534" → `PHASE_1_DOC.md:1736`; "l. 1556" → `:1765`. P12 §5.2b "P1 §5.1 ll.
4198–4200" → `:5443–5445`; "P1 §5.3 l. 4268" → `:5536`; "l. 4274" → `:5543`; "l. 4275" → `:5544`;
"P1 §5.1 l. 4204" → `:5452`. P12 §4.9 "P1 §4.9.4 ll. 3776/3790" → `:4982–4997`; "P1 §5.3 l.
4268" → `:5536`; "P1 §11.4 l. 5167" → `:6713–6716`; "P1 §9 l. 4513" → `:5865`; "P1 §6 l. 4294" →
`:5563`; "P1 §7 l. 4319" → `:5589`; "P1 §10.2 l. 4604" → `:5944–5955`. P12 §4.1 "P3 §5.1 l.
1436/1432" → §5.1 begins `PHASE_3_DOC.md:3349`; "ll. 1621–1622" → `:3422–3428`. P12 §4.6.1 "P3
§5.1 ll. 1571–1584, 1619" → `:3354–3355`, `:3977–3982`, enums now `:734–735`. P12 §4.3.3 "P3 §4.8
ll. 1251–1252" → `:2918–2920`; "P3 §4.3 ll. 953–955" → `:2269–2273`. P12 §4.7.1 "P3 §4.3 ll.
938–944/946" → `:2261–2263`; "P3 §11.4 l. 2044" → `:5170–5172`. P12 §3.3/§4/§6 "P3 §6 ll.
1696/1697/1699/1701" → §6 now begins `:4359` (the ambiguous/version rows now live in §3.2 at
`:1698`/`:1706`). P12 §7 "P3 §7 ll. 1709–1716" → §7 begins `:4388`. D-P12-11's "P3 §11.5 item 2
(ll. 2052–2053)" → §11.5 begins `:5281`. P12 §0.2's extent pins for P3 §2.2 (ll. 335–641 → now
:660+) and P3 §5 (ll. 1420–1689 → now 3343–4358) are likewise pre-growth. RESEARCH.md and
DESIGN.md pins, by contrast, are all accurate as recomputed.

**Failure path.** §0.4 item 5 records that every Phase 3 locator was re-resolved "against the
current" document — true for the 2026-08-09 re-run, but the 2026-09-07/08 amendments
(schema23 cutover, D-P12-37/38/39) re-adopted the owners' current §5 semantics without re-resolving
the coordinate pins, and both dependency docs have since moved underneath them. A reader who
follows a pin lands in unrelated text; nothing flags the drift. Section-level adoption (§5.2's
"consume the complete §5.1 declarations") is what governs, so no consumed semantic is wrong — this
is evidence hygiene, not an interface defect.

**Owner+receivers.** Owner: Phase 12 document only. No dependency doc changes; P1/P3/P7 §5s are
correct as published and were not modified by this review.

**Minimal resolution.** One §G1.3 fix-up pass over the phase doc that either (a) re-points each
P1/P3 line citation to the recomputed current coordinates (a representative mapping is in
**Sites**), or (b) replaces fragile line pins with section anchors (e.g. "P1 §5.3
Mod-dependency-declaration row") — which is the form the doc's own current §5.2 adoption rows
already use. No semantic edit, no §5 change, no new verdict inputs.

## 4. Notes

N-1 — §0.2's Phase 3/Phase 7 verification ledgers ("round 36 is latest", Phase 3 not verified) are
accurate only as of their own read time; P3 has since run further rounds (D-P3-74 records R60 C1)
and P7 has addenda §0.41–§0.50. The header's "§§0.1–0.7 are historical" disclaimer covers this,
but the tables read as current to a skimmer; consider one sentence pointing at §§5/11 as the only
current state.

N-2 — the historical D-P12-31 (schema21) receipt cites "P4's opaque RegistryFingerprint/own-build-v1
identity"; that versioned string matches no current owner vocabulary (the current integration
identity set uses profile-selection-v3). It is acceptable solely because D-P12-36 marks all prior
numeric receipts historical; the receipt must not be revived as an admission alternative.

N-3 — wording tension at the resolver boundary: P12 §4.3.4 counts "a retained disabled link"
(unresolved `[SUBSCREEN]`, D-P12-6) in `expandedSlotCount`, while P3's binding text
(`PHASE_3_DOC.md:2270–2273`) describes counting "applicable `[SUBSCREEN]`" slots; P3's §5.1 row
(`:3363`) says "counts the retained array after `*` expansion", which supports P12's reading.
Reconcile the adjective at the next P3/P12 touch so a shared boundary test cannot split readings.

N-4 — observed and already reconciled inside the doc, recorded so the trail stays visible: the
"8 engine options" count (RESEARCH §4.7 ll. 608–609) vs the implemented seven is §11.3 item 4's
note with the DESIGN.md l. 2374 ruling; the PD §14 tooltip overstatement is §11.3 item 1 with the
upstream request at §11.5 item 2; the slider-evidence conflict is §11.3 item 3 with §11.5 item 1.
All three rulings are correct against the sources I re-read.

## 5. Areas audited and found sound

Doc-gate conformance and zero-unmapped conformance map with faithful provenance (all 33 F-rows and
17 C-rows spot-checked against RESEARCH/DESIGN text, including App E.2 l. 1427, App F.1 l. 1448,
App F.2 l. 1452, §G4.1 ll. 545–550, §G4.5 ll. 582–585, Phase 4 ll. 1560–1563, Phase 7 ll.
1879–1882 and 1967–1969); the `*`-expansion decisions D-P12-1…4 and their degradation rows;
column-resolution arithmetic; locale per-key empty-preserving fallback exactly matching P3 §4.3
rule 6 (`:2095–2104`) including P12-owned final fallback; tooltip split/severity matching P3
(`:2056–2058`, `:3828–3830`); out-of-list value retention; slider-as-discrete-selector with
payload equivalence; profile inference/click-to-cycle/explicit-intent state machine including the
equal-options/different-disabled-programs case and D-P7-60 reciprocity; session availability
preflight D-P12-38 with stale-callback rejection; pending-profile summary D-P12-37 transitions;
apply/discard/reset/Done timing with changed-only writes and empty-set reset; the seven settings,
exact wire vocabulary, reserved zero-only AA, tri-state priority, and inert-without-ladder gates;
durable `shaderPack` reference with the five resolver outcomes and six acquisition rejections;
reload classification, bake-set predicate, merge algebra, RS-1/RS-2; the three Forge-only platform
bindings; P1 `SHADER_GUI` sink posture plus direct P11 snapshot delivery (shape verified against
P7 `:3536–3540`); programmatic bridge and Internal committer contracts (exact signature matches);
failure-mode table mapped to the §G2.4 ladder incl. rung 2a; threading ownership incl. the
`DiagnosticReporter` client-thread hop; headless test plan; milestone staging; OQ-9 spike and
fallback; D-1..D-10 disposition; licensing/provenance posture and do-not-inherit handling;
template completeness.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 1 correction (C1, stale dependency-doc line coordinates),
4 notes (N-1…N-4). The correction is a documentation-hygiene fix-up: re-pin or de-pin the P1/P3
coordinates; no consumed or exposed interface changes, so no §5 impact and no dependent
re-derivation follows. No build, test, or GL execution was performed; no files other than this
review were written.

---

## Resolutions

**Date:** 2026-09-08. Applied by a fresh §G1.3 fix-up session that read this report, the phase doc
and the cited dependency ranges against current bytes. The report body above is byte-identical to
the published text; everything below is additive.

- **C1 — applied in full (option (a) re-pins, with option (b)'s section-anchor allowance where the
  cited sentence now lives in a different section than the old pin).** Every P1/P3 line citation
  in the phase doc — 80 pin-bearing lines across §§0.2, 1.2, 2.1, 3, 4, 5.1, 5.2b, 5.4, 6, 7, 8
  and 11.1 — was re-resolved against the current `PHASE_1_DOC.md`/`PHASE_3_DOC.md` bytes, with the
  cited content verified at each recomputed target before repointing. That includes the two §0.2
  extent rows (P1: §2.1 → ll. 1707–1780, §4.9.4 → ll. 4970–5012, §5 → ll. 5420–5549, §10.2 →
  ll. 5937–5966, §11.3 item 2 + §11.4 block → ll. 6713–6717, §9 row → l. 5865; P3: §2.2 →
  ll. 671–1588, §3.1 → ll. 1660–1690, §3.2 rows → ll. 1712–1717, §4.3 → ll. 2037–2275, §4.8 →
  ll. 2809–2991, §5 → ll. 3343–4357, §6 → ll. 4359–4386, §7 → ll. 4388–4411, §11.4 →
  ll. 5130–5279, §11.5 → ll. 5281–5312), the Sites-representative placeholders resolved to their
  content-bearing lines (§4.8 edition gate → ll. 2845–2846; `*` deferral → §4.8 ll. 2919–2920 and
  §4.3 ll. 2270–2272; `discover`-never-throws → l. 3354; the closed enums → §2.2 ll. 734–735 per
  the Sites note; `P3 §3.1 l. 693` → l. 1665; `P3 §5.1 l. 1619` → ll. 3981–3982; display-name
  sanitization → §4.1 l. 1838 and §5.1 ll. 3419–3420), C-9's provenance cell (`P1 §4.9.4 l. 3790`
  → l. 4983; both `§6 l. 4294` → l. 5563) and the D-P12-4/7/8/10/11/14 rationale cells. Three
  quotes the owners' current text no longer carries were re-quoted faithfully from it: §4.3.3's
  `*`-counting sentence → *"Phase 12 expands `*` first, then passes the complete retained
  screen-array length as `expandedSlotCount`"* (P3 §4.3 ll. 2270–2271); §4.4.2's out-of-list
  sentence → *"Values outside a non-empty `allowedValues` list" valid "with one warning, per
  D-P3-14"* (P3 §4.3 ll. 2209–2210); §4.7.1 → *"Phase 12 owns apply/discard timing; codecs never
  mutate a published configuration"* (P3 §4.3 l. 2262) and *"It owns discard timing and
  global-setting UX"* (P3 §11.4 l. 5175). The first §4.3.3 quote (*"`*` expansion is intentionally
  deferred to Phase 12…"*) still matches P3 ll. 2919–2920 verbatim and was kept with its pin
  repointed. The phase doc records the pass as a new §0.8 and its §0 preamble historical-records
  range now reads §§0.1–0.8. No §5 row changed — confirming this report's "§5 impact: none" — no
  new decision IDs were minted, and no existing decision text was altered beyond the rationale
  cells' re-pins.
- **N1, N2, N3, N4 — not applied.** All four are non-binding observations and C1's minimal
  resolution does not incorporate any of them. Verifiable state: the phase doc's §0 preamble
  disclaimer wording (beyond the §§0.1–0.8 range extension recorded above), §0.4 item 5, §5.2's
  prose preamble and §9's record/receipt wording are otherwise byte-identical to the frozen
  bytes.
- **Cross-check (relayed from FixP7 via Main, re the `D-P12-39` §11.1 row):** left unchanged —
the row cites `D-P7-60` by decision ID with no P7 line range, the ID resolves to the correct
decision (P7's ledger row at current :4552, "P12 R6: retain accepted profile-only intent through
REPUBLISH and P3 evaluation"), the gloss "ProgrammaticOptionSnapshot carries no profile field"
paraphrases a clause that actually lives in P7 §5.3(D)'s programmatic-bridge prose (current :3519,
"Programmatic maps add no profile field and preserve the accepted choice"), and because this
session's C1 sweep neither edited that row (now at :2107 after §0.8's insertion) nor any P7
citation, the row was left alone per this report's P1/P3-only scope.
