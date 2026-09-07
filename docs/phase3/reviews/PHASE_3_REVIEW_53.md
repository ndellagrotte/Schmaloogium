## 0. Method and reading order

I first re-derived the four surviving candidates without consulting prior reviews. I examined the whole-document target through structural and search sweeps plus exact reads of the candidate-bearing portions of the Round 52 addendum, canonical public declarations, macro semantics, the complete monitored cross-phase region, test plan, Phase 2 hand-off, decision log, staging plan, and implementation checklist. I then checked the governing Design v3 Part I rules, Phase 3 target specification, documentation gate, and mandatory template; relevant RESEARCH contracts; the manifest-selected Phase 1 binding contract and capability-profile serialization; and focused Pintonium/Oculus reference evidence. Only after recording provisional interpretations did I read all discovered prior reviews, Rounds 1–52, as the final historical-settlement stage.

There were no reading-order deviations. I did not use the network, invoke another verification loop or harness, fan out to agents, or consult forbidden sources. No candidate was eliminated before adjudication, and the Gate dropped none.

## 1. Findings

### candidate-001 — The canonical public API still declares schema 13 after the schema-14 bump

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§0.52, 2.2, 5.1, and 5.3; associated schema tests and checklist items.

**Claim:** Round 52 did not propagate its mandatory schema-14 change to the canonical `PackFrontEnd.CURRENT_SCHEMA_VERSION` declaration. The document therefore gives producers and consumers contradictory implementation instructions.

**Evidence:**

- Section 2.2 explicitly calls its declarations “the canonical public shapes” (lines 417–419), yet `PackFrontEnd` declares `int CURRENT_SCHEMA_VERSION = 13;` (lines 449–451).
- The Round 52 addendum says the changed `ScreenModel` meaning advances the schema to 14 and requires fresh §5 verification (lines 341–343).
- Section 5.3 says the same constant is 14, requires every configuration from this revision to publish 14, and requires consumers to reject every other value before retaining derived state (lines 2896–2902). Its history further makes schema 14 mandatory and requires schema-13/schema-14 peers to reject one another (lines 2929–2934); versions 1 through 13 are expressly incompatible (lines 2949–2951).
- Section 5 says the §2.2 declarations are incorporated into the binding rows rather than being illustrative (line 2453). It also requires an edit to an incorporated declaration or consumer-visible semantic to update its §5.1 row in the same revision, with an explicit `unchanged` entry where applicable, and identifies that as a fresh-verification trigger (lines 2581–2584).
- Named publication and compatibility tests elsewhere in the document expose the contradiction but cannot override the sole executable-style constant declaration.

**Required correction:** Change the canonical declaration to `int CURRENT_SCHEMA_VERSION = 14;`. In the same revision, update the corresponding §5.1 `PackFrontEnd` row as the document's incorporation rule requires. Make `schema_currentValuePublished` assert the literal expected value 14 rather than merely comparing values that could share the same stale constant, and retain the specified bidirectional schema-13/schema-14 rejection coverage; ensure the unsupported ID-mapping boundary exercises version 13 if existing mismatch coverage does not already do so.

**Severity:** correction. The contradiction is consumer-visible but has a localized repair; it does not require architectural reconstruction.

**touches interface/change-trigger region: yes.** The ordered same-revision §5.1 update changes the manifest-declared monitored interface region and requires a fresh Phase 3 verification round.

### candidate-002 — D-P3-54 overstates macro-override typo prevention

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§4.4, 8.1, and 11.1, especially D-P3-54.

**Claim:** D-P3-54's unqualified statement that deterministic pre-I/O rejection “prevents typos” is inconsistent with its own complete action matrix, under which a syntactically valid absent `SUPPRESS` target intentionally succeeds as a no-op.

**Evidence:**

- The complete matrix says `SUPPRESS` against a name absent after base merge is a documented no-op, while a present name is removed (lines 1753–1759).
- The exhaustive validation order checks nulls, identifier grammar, action/replacement shape, protected targets, and `ADD` collisions; it has no existence rejection for `SUPPRESS` (lines 1761–1764).
- The named matrix test explicitly includes “absent suppression no-op” separately from pre-I/O failures and no-partial-map behavior (lines 3150–3152).
- D-P3-54 repeats the no-op/remove rule but then says deterministic pre-I/O rejection prevents “typos or collisions” from publishing a partial identity (lines 3467–3469). A valid-identifier misspelling of an intended suppression target is indistinguishable from an intentionally absent target and is accepted, so the broad typo-prevention rationale is false.

**Required correction:** Keep the selected action matrix unchanged. Narrow D-P3-54 to the guarantee actually provided: deterministic pre-I/O validation rejects malformed overrides, protected targets, and `ADD` collisions and prevents those failures from publishing a partial map. Explicitly avoid a general typo-prevention claim, or acknowledge that absent `SUPPRESS` remains an intentional no-op and therefore cannot detect syntactically valid misspellings.

**Severity:** correction. This is a factual contradiction in a mandatory decision rationale, but the normative matrix and test plan are otherwise coherent.

**touches interface/change-trigger region: no.** The ordered edit is confined to D-P3-54's rationale and does not alter the macro action contract or §5.

## 2. Checked and clean

- **candidate-003 was dropped as an exact duplicate of candidate-001.** It identifies the same stale `CURRENT_SCHEMA_VERSION = 13` declaration, the same schema-14 requirement, the same producer/consumer incompatibility, and the same repair. Counting it separately would duplicate one defect rather than identify an independent correction.
- **candidate-004 was dropped against settled material.** Its proposed missing P3-C20 hand-off schema is the same theory adjudicated and cleared in Rounds 38 and 42: Phase 2 owns the adapter, fixture workflow, harness reporting, and physical golden-file format, while Phase 3 supplies test inputs and the already versioned `PackConfiguration` contract. Phase 1 contributes its existing `GLCapabilityProfile.parse/write` encoding but expressly does not contribute a general golden format. Round 52 changed authorship of selected synthetic capability vectors, not that ownership boundary, and did not introduce a new independently serialized Phase 3 public result. Requiring Phase 3 to define filenames, a second wire schema, and adapter packaging would cross the settled Phase 2 ownership line. If a future revision deliberately introduces a separate serialized cross-phase artifact, that new interface would need an explicit contract and fresh verification; the current text does not establish that premise.
- Round 52's decoded `MinimumEditionRule` accessor policy, compatibility-status fingerprint participation, expanded-slot `ScreenModel` formula and boundary vectors, schema-13/schema-14 rejection plan, and Phase-3-authored synthetic capability-vector selection are otherwise consistent across detailed semantics, §5, tests, staging, decisions, and checklist.
- The consumed Phase 1 contracts are present and accurately represented: module/seam constraints, the needed `GLCapabilityProfile` fields and serialization, fixed log channels, diagnostics, the reserved `saveSources` flag, and notice/SPDX handling. The jcpp build change is requested rather than silently assumed.
- The conformance map covers Appendix F.1 engine flags and ownership, Appendix F.2–F.8 families and parse/model semantics, Appendix A.3 directives and published fields, the required Pintonium pitfalls, and the remaining discovery, preprocessing, source-attribution, persistence, and ID-mapping families. Evidence-qualified policy choices are not misattributed to RESEARCH.
- There were no pre-adjudication eliminations or Gate drops to carry forward.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

The target is substantially implementable, but the stale canonical schema constant is a direct public-contract contradiction and D-P3-54 contains a narrower factual overstatement. The current correction count improves on the preceding two rounds' five corrections, but the recent sequence `5 → 5 → 2` is not strictly decreasing because of the prior plateau; convergence is improving but incomplete.

Next action: apply the two ordered corrections without broadening settled contracts. Because the schema correction must update §5.1, the monitored cross-phase region changes; run a fresh Phase 3 verification round before Phase 3 closes or any dependent consumes the corrected interface.

## Resolutions

### Corrections applied

1. **candidate-001 — applied.** Independent comparison of the canonical §2.2 declaration with the schema discipline in §5.3 confirmed that Round 52 had already made schema 14 mandatory while leaving the executable-style constant at 13. `PackFrontEnd.CURRENT_SCHEMA_VERSION` is now `14`; the corresponding §5.1 row names the constant and its literal value in the same revision. `schema_currentValuePublished` must assert literal `14` rather than derive both sides from the production constant, and the ID-mapping boundary now covers every unsupported version through 13. The existing named schema-13/schema-14 rejection tests remain bidirectional. This repair intentionally changes the monitored cross-phase interface region and therefore fires its fresh-verification trigger; it does not require schema 15 because it aligns the declaration with the already-required schema-14 meaning rather than changing that meaning.
2. **candidate-002 — applied.** The complete macro action matrix remains unchanged. D-P3-54 now limits pre-I/O rejection to malformed overrides, protected targets, and `ADD` collisions, all without partial-map publication. It also states the necessary limit: absent `SUPPRESS` is an intentional no-op, so a syntactically valid misspelling of an intended suppression target cannot be detected.

The target received only compact §0.53 summary prose; the reasoning and interface-trigger disclosure are retained here.

### Notes deferred

None. The adjudication admitted no notes.

### Settled scope preserved

- No second Phase 3 wire or golden-file format was introduced; the settled Phase 2 harness and adapter ownership boundary remains unchanged.
- Round 52's schema-14 screen semantics, compatibility fingerprinting, edition accessors, capability-vector authorship, and macro action matrix were not reopened.
- No correction was refused.
