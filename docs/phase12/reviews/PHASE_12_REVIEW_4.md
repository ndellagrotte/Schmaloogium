# Phase 12 — Options GUI, persistence & reload — Verification Review, Round 4

Target: `docs/phase12/v1/PHASE_12_DOC.md` (ll. 1–1806)
Governing design revision: `docs/design/v3/DESIGN.md` (v3, override selection)

## 0. Method and reading order

Sources read, in order: the target (whole document, with focused re-reads of §2.2 ll. 259–303,
§3.3 ll. 444–465, §4.6.2 ll. 806–829, §5 ll. 1168–1325, §11.4 ll. 1680–1695); the governing design
revision (§G1.3 re-verify rule ll. 340–355, mandatory template ll. 817–855, Phase 12 spec and doc
gate ll. 2357–2435); `docs/research/v1/RESEARCH.md` at the manifest selectors; the two binding
dependency contracts (`docs/phase1/v14/PHASE_1_DOC.md` §5, `docs/phase3/v1/PHASE_3_DOC.md` §5); and
the listed supporting evidence. Prior reviews (rounds 1–3) were read **last**, after independent
re-derivation of every candidate, as directed.

No network use. No agent fan-out from this session: the single adjudication role was executed
directly. Forbidden sources (`docs/**/chatlogs/**`, `*.txt`, any prior agent/session transcript)
were neither opened nor cited. Gate drops: none — the Gate forwarded three candidates and
eliminated none, so `## 2` records no pre-adjudication eliminations beyond the refuter splits
discussed there. No deviations from the manifest selectors.

Only this file was created; nothing else in the worktree was modified.

## 1. Findings

### Finding 1 (candidate-001) — note: row C-15's `<empty>` line pointer is stale (`l. 277` → `l. 288`)

**Location.** `docs/phase12/v1/PHASE_12_DOC.md` §3.3 row C-15, l. 462.

**Claim adjudicated.** That the `<empty>` pack token is retained in the comment adjacent to the
`Blank` record "at l. 277".

**Evidence (re-derived independently).** Reading §2.2 physically: l. 276 opens
`public sealed interface PresentationEntry {`, l. 277 is the first line of
`record SwitchOption(OptionId id, String label, boolean value,`, and the `<empty>` comment sits on
l. 288, `record Blank() implements PresentationEntry {}                     // \`<empty>\``. There is
no wrapped declaration or alternate `<empty>` occurrence that rescues the pointer. Round 3's review
(ll. 36–37, 172–173) cited l. 277 correctly under the pre-fix-up numbering; the Round-3 fix-up
inserted lines and the pointer was carried over unshifted.

**Severity: note.** The substantive contract claim is true — the pack token *is* retained, the
§G4.1 doc-gate obligation holds, and the same cell already names §2.2, the type (`Blank`), the token,
and cross-row F4-10. A reader following the pointer lands eleven lines away inside the very same
code block and cannot be misled about contract content. This is a stale self-reference produced by
renumbering, not a contractual or traceability failure that changes any consumer's decision, so it
is recorded as a note and is **not** ordered for fix-up. An editor touching the document anyway may
change `(l. 277)` to `(l. 288)` or drop the number in favour of the already-present §2.2 reference.

**Touches interface/change-trigger region: no.** The cell sits in §3.3, outside the declared region
(ll. 1168–1325), and the suggested edit would not alter it.

## 2. Checked and clean

**candidate-002 — dropped on re-derivation.** The alleged missing Phase 5/8/7 published-list
obligation is in fact stated: §4.6.2 ll. 806–829 names the `Choice` domains, the owning phases, the
identity-entry default, the single-identity-entry fallback, and the `interactive=false` inert-row
behavior when no list exists, and routes the hand-off to §11.4 ll. 1688–1690. DESIGN's mandatory
template (l. 850) assigns "items handed to later phases" to §11, not §5, and DESIGN l. 2359 shows
Phases 5 and 8 are not Phase 12 dependencies, so §5's dependency-request mandate does not reach
their lists. §5.1 l. 1178 additionally carries the wire rule ("the owner-published list entry
verbatim for `Choice`"). Nothing is unimplementable or guessable and the degradation path is fully
specified; what remains is a placement preference. Dropped, `final_severity: none`.

**candidate-003 — dropped on re-derivation.** Phase 12 §5 indeed states no phase-local
incorporation/edit-discipline clause (grep of the target confirms the only such language is quoted
*from* Phase 3 or concerns requests *to* Phase 3). But the mandatory §5 template (DESIGN ll. 838–840)
imposes no such clause, and DESIGN §G1.3 l. 354 already carries the governing "re-verify only if §5
changed" rule, adjudicated at verify time over consumer-visible semantics. Phase 3's clause is a
phase-local artifact of its own review history, a precedent rather than an obligation binding this
document. Every load-bearing §5.1 row except `OptionScreenView` restates its semantics inline (wire
text, merge algebra, RS-1/RS-2, the closed action set), and the delegating rows carry explicit
section pointers, so no consumer is currently misinformed. This is optional hardening, not a defect
a dependent can hit. Dropped, `final_severity: none`.

**Finder clean areas, spot-confirmed and accepted.**
- *new-surface*: the six §G4.1 terms survive verbatim as §2.2 type/field names; `*` appears literally
  in §4.3.3's heading and §4.3.4 items 1 and 3; `prefix.<NAME>`/`suffix.<NAME>` appear literally in
  §4.3.5 and rows F3-14/F3-15; the §0.7 fix-up block's numbering and wording follow the §0.6
  precedent.
- *interfaces*: the Phase 3 and Phase 1 consumed-contract locators re-resolve exactly against the
  dependencies' binding §5 regions; the ⟳ provisional marking of rows touched by Phase 3 round-36
  corrections is honest and consistent with §0.2; `ReloadRequest`/`ReloadCoordinator`,
  `EngineSettingsModel` wire text, `PackSelectionModel` intents and the persisted `shaderPack` token
  domain are stated with enough detail for Phase 7 to implement without guessing; the P12→P7
  exposure to a non-dependent phase is explicitly routed to final integration review.
- *conformance*: every §3.1/§3.2/§3.3 citation resolves to supporting text (the one exception being
  Finding 1's stale internal pointer); every doc-gate item (`*`, `<empty>`, red-"!", auto-widen,
  sliders-with-no-reference, the Pintonium fallback reference) and every Scope-in requirement is
  mapped; rows that decline actuation (F3-3, F4-3, C-9…C-11) locate the obligation in the owning
  phase without dropping the contract row.

No candidate was eliminated by the Refute/Steelman/Gate pipeline before adjudication.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=1
Interface changed: no

Zero blocking findings and zero corrections; one note (candidate-001), which does not block PASS
and is not ordered for fix-up. The declared interface/change-trigger region
`cross-phase-interfaces` (ll. 1168–1325) is **unchanged** by this round: no admitted finding orders
an edit inside it, so no fresh-verification trigger fires from this review.

Trend/convergence: round 2 PASS-WITH-CORRECTIONS (3 corrections, 2 notes, interface changed),
round 3 PASS-WITH-CORRECTIONS (1 correction, 2 notes, interface unchanged), round 4 PASS (0
corrections, 1 note, interface unchanged). Severity and volume are monotonically decreasing and the
sole surviving item is a renumbering artifact of the round-3 fix-up; the loop has converged. No
convergence warning.

**Next required action.** None on this document. Phase 12 §12's gate may close on this literal PASS,
subject to the already-recorded external condition in §0.2/§5.2 that the ⟳-marked Phase 3 locators
be re-confirmed once Phase 3 completes its own fresh post-round-36 verification.
