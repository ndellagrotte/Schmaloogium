## 0. Method and reading order

I independently re-derived the surviving candidates before consulting settled review material. I first read the complete Phase 3 target, the selected Design v3 Part I/target-specification/document-gate/template regions, the relevant RESEARCH contract, the Phase 1 v14 binding contract, and the cited permitted supporting evidence. For the screen candidate, the shipped `shaders.properties` documentation was treated as contract evidence and the cited Java reference implementation only as qualified behavioral observation; no implementation structure is adopted by this review.

After reaching provisional dispositions, I read `PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_51.md`, in round order, and checked each candidate against that settled history. There was no network use, no subagent or session fan-out, no verification harness invocation, and no source-list deviation. No forbidden chatlog or `*.txt` source was opened.

The Gate had already dropped candidate-006 because one required anchor is a forbidden `*.txt` source. I did not reopen or adjudicate that candidate. The surviving set adjudicated below is candidate-001, candidate-002, candidate-003, candidate-004, candidate-005, and candidate-007.

## 1. Findings

### candidate-001 — Compatibility can change without changing the configuration fingerprint

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§2.2, 4.8, 4.10, 5.1, and 5.3, especially lines 588–603, 2119–2123, 2345–2350, 2849–2852, and 2872–2902.

**Claim:** The new edition comparator can change the direct `PackConfiguration.compatibility` value while leaving every concretely enumerated configuration-fingerprint input unchanged. The candidate is admitted in this narrower form; the comparator closure does not, by itself, compel a schema advance.

**Evidence:** `PackConfiguration` publishes both `CompatibilityStatus compatibility` and `ConfigurationFingerprint fingerprint` (lines 588–603). A matching minimum-edition rule is evaluated against canonical runtime `engineEdition`, and the result directly chooses `COMPATIBLE` or `REQUIRES_NEWER_EDITION` (lines 2119–2123). The normative fingerprint-input list includes pack bytes, normalized paths, finalized options, macro policy, renderer features, capability identity, parser schema, ID mappings, and later resource/expression/uniform/dimension/geometry inputs, but it names neither runtime `engineEdition` nor computed `CompatibilityStatus` (lines 2345–2350 and the remainder of §4.10). This omission is not repaired by the generic test name `fingerprintChangesOnEveryLoadSemanticInput`. The retained-state rule nevertheless permits reuse whenever schema and configuration fingerprint remain equal (lines 2849–2852). Holding the pack minimum and all listed inputs fixed while moving only a valid runtime edition across that minimum can therefore change a published component without invalidating retained state.

The stronger schema claim is not established. `COMPATIBLE` remains the absent/no-unmet-rule outcome and `REQUIRES_NEWER_EDITION` remains the unmet-minimum outcome; D-P3-52 supplies ordering for previously unspecified edition spellings. Round 51's settled conclusion that this did not necessarily change a component meaning/default is consistent with retaining schema 13 once fingerprint invalidation is corrected.

**Required correction:** Include computed `CompatibilityStatus`, or an equivalently canonical edition-comparison payload, in the configuration fingerprint. Add a test that holds pack bytes and every other fingerprint input constant while crossing a minimum edition and asserts that both status and fingerprint change. Reconcile the schema discussion explicitly: retain schema 13 if this is only the documented producer-policy closure, and advance it under §5.3 only if the repair deliberately changes an established consumer-visible meaning/default or shape.

**Severity:** correction.

**Touches interface/change-trigger region:** yes. The correction must update the binding fingerprint/reuse semantics in the monitored §5 region.

### candidate-002 — `MinimumEditionRule` does not bind either public string projection

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§2.2, 4.8, and 5.1, especially lines 774, 2104–2123, 2422, and 2427.

**Claim:** The public model does not say whether `minecraftVersion()` and `minimumEdition()` expose decoded source spelling or canonical comparison values.

**Evidence:** The published shape is only `MinimumEditionRule(String minecraftVersion, String minimumEdition)` (line 774). Section 4.8 separately defines a validated suffix, an optional patch interpreted as zero, canonical edition case/number normalization, and a token comparator. None of those rules states whether `minecraftVersion()` contains the complete key, the decoded suffix, or a normalized three-component spelling, nor whether `minimumEdition()` contains Java-Properties-decoded spelling or the comparator-canonical token. Both storage choices permit identical compatibility evaluation but expose observably different records, for example `1.12` versus `1.12.0` and `g006_pre01` versus `G6_PRE1`. The §5.1 row freezes exact record accessors for a model consumed by Phase 12, and §5.1 explicitly incorporates the §2.2 declarations rather than treating them as illustrative.

**Required correction:** Define both accessor projections normatively in §4.8 and mirror them in the incorporated §5.1 row. Add accessor assertions covering two- versus three-component versions and edition spellings that compare equal but differ textually. Preserve the existing source-order contract unless intentionally changing it. Separate raw and canonical fields only if both forms are needed. Apply §5.3 conditionally: advance the schema if the selected repair changes an established component meaning/default or shape; otherwise state why schema 13 remains valid.

**Severity:** correction.

**Touches interface/change-trigger region:** yes. The correction defines consumer-visible accessor semantics and must update the monitored §5.1 row.

### candidate-003 — The test plan attributes a fixture set to a dependency that does not expose it

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §8 opening and §8.2, especially lines 2999–3002 and 3254–3262; compare §5.2 lines 2854–2868 and `docs/phase1/v14/PHASE_1_DOC.md` lines 4233–4252.

**Claim:** The instruction that Phase 3 tests use “Phase 1 recorded `GLCapabilityProfile` fixtures” conflicts with the dependency contract and with Phase 3's own stated fixture ownership.

**Evidence:** Phase 1 exposes the `GLCapabilityProfile` value contract to Phase 3, but says Phase 2 owns the recorded fixture set and refresh workflow and expressly says Phase 1 does not give that set. Its recorded-fixture dependency note names Phases 4/5/6, not Phase 3. The governing design makes dependency §5 exposure—not physical source-set visibility—the authorization boundary. Phase 3 §5.2 correctly consumes only the profile contract and says P3-C20 emits synthetic fixtures and capability profiles. Section 8's opening nevertheless instructs implementers to use “Phase 1 recorded” fixtures, while §8.2 mixes project-authored fixtures with “recorded capability profiles.” The wording therefore leaves an actionable conflict over whether Phase 3 authors test vectors or imports an undeclared, Phase-2-owned set.

**Required correction:** Change the §8 opening and §8.2 item 3 to require Phase-3-authored synthetic `GLCapabilityProfile` values/vectors and to emit the selected vectors in the Phase 2 hand-off, consistently with §5.2. If shared recorded fixtures are actually intended, request and bind that dependency explicitly instead of attributing the fixture set to Phase 1.

**Severity:** correction.

**Touches interface/change-trigger region:** no. The direct repair is confined to the Phase 3 test plan and leaves the existing §5 consumption/hand-off contract unchanged. Choosing shared recorded fixtures instead would be a different, interface-changing repair.

### candidate-004 — `MacroOverrideAction` has no executable action/conflict semantics

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§2.2, 4.4, and 5.1, especially lines 523–540, 609–620, 1739–1746, 1832–1846, and 2788–2799.

**Claim:** The public `ADD`, `SUPPRESS`, and `FORCE` algebra does not define its observable behavior for present and absent allowed macro names.

**Evidence:** `RuntimeIdentityData` accepts a public per-pack override map, and `MacroOverrideAction` publishes three distinct variants. Section 4.4 binds replacement presence, map ordering, and forbidden standard/engine-version/option targets, but it never states whether `ADD` requires absence, whether `FORCE` creates and/or replaces, or whether suppressing an absent name is valid. The preprocessing order says the override family is established after standard, option, identity, and feature families through `Preprocessor.addMacro`; that mechanism does not explain removal or distinguish all three actions. The load-validation text circularly calls the map “validated” under §4.4 without classifying collisions or binding their failure/no-op disposition. OQ-7 defers selection of the identity payload, not the meaning of the already-public action variants.

**Required correction:** Define a complete action-by-target-state matrix in §4.4 and the §5.1 macro row: base-family merge order; `ADD`, `SUPPRESS`, and `FORCE` for present and absent allowed names; collisions with capability-feature and engine-identity families; resulting replacement/removal; deterministic validation and diagnostic precedence; and whether rejected combinations use pre-I/O `Failed(INVALID_REQUEST)` or are documented no-ops. Add the corresponding action × present/absent/collision tests. Apply §5.3 to the resulting nested component semantics rather than assuming either schema outcome.

**Severity:** correction.

**Touches interface/change-trigger region:** yes. The action meaning is part of an incorporated public declaration and its binding §5.1 row must change.

### candidate-007 — Screen widening provenance and semantics conflict with the available contract and observed behavior

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §§2.2, 3.2, 4.3, 5.1, and 8.1, especially lines 734–739, 1359–1362, 1708–1713, and 3109–3110.

**Claim:** Appendix F.4 does not establish the exact resolver attributed to it, and the target's option-only count plus “explicit valid count wins” semantics conflict with the cited reference behavior.

**Evidence:** RESEARCH Appendix F.4 and the shipped `shaders.properties` documentation establish the screen forms, a default of two columns, and automatic widening beyond 18 options. They do not state the exact post-18 formula or exclude navigation/layout slots from the basis. The target nonetheless publishes an executable method that returns every explicit count unchanged and otherwise computes `max(2, ceil(actualOptionCount / 9))`, while its prose excludes `[SUBSCREEN]`, `<profile>`, and `<empty>` from the count.

The qualified behavioral observation at `reference-src/schlorbium-HD_U_G6_pre1/net/schlorbium/shaders/gui/GuiShaderOptions.java:51-57` computes a nine-row minimum from the complete expanded screen-array length and raises a configured count when it is below that minimum. The parser observation at `ShaderPackParser.java:381-417` places applicable empty, profile, rest, and subscreen entries into that array, and `Shaders.java:1291-1307` preserves those slots while expanding `*` before the GUI receives the final array. Thus the observed evidence both contradicts the blanket exclusions and shows that a configured value is a floor candidate, not an unconditional winner. Prior rounds established the current target wording without this qualified behavioral reconciliation; they do not settle the newly evidenced conflict.

**Required correction:** Cite Appendix F.4 only for syntax, the default, and the beyond-18 requirement. If adopting the observed behavior, record a qualified behavioral provenance/contract-check decision and reconcile the public API, detailed prose, conformance rows, tests, checklist, and §5.1 binding semantics so the configured count (default two) is raised to the nine-row minimum computed from the expanded screen-slot array; applicable empty/profile/subscreen and ordinary option slots count, and `*` expands before counting. Rename/document the argument as expanded slot count. If the project will not accept the implementation observation as authority, remove or defer the exact resolver and request upstream closure instead.

**Severity:** correction.

**Touches interface/change-trigger region:** yes. `ScreenModel.resolvedColumns` is an incorporated public declaration exposed to Phase 12, so either permitted repair changes the monitored §5 contract.

## 2. Checked and clean

Candidate-005 is dropped as an exact duplicate of candidate-002. It identifies the same `MinimumEditionRule` declaration, the same missing source-versus-canonical projections, the same Phase 12 publication consequence, and the same corrective work. Counting it separately would duplicate one defect.

Candidate-006 remains eliminated at the Gate. Its required `.txt` anchor is forbidden and was not opened; the remaining accessible anchors cannot revive a candidate that failed closed on required evidence.

The independent read confirmed the finder-reported clean areas. In particular, the fixed `(1,12,2)` and `MC_VERSION=11202` projection is consistent across public shapes, macro construction, tests, decisions, and checklist; the null-reporter `INVALID_REQUEST` branch and filesystem failure precedence are coherent; D-P3-52 honestly labels the edition ordering as provisional local interoperability policy; and the consumed Phase 1 production contracts for module placement, `GLCapabilityProfile`, logging, diagnostics, source dumping, and notices match the selected binding region. No additional defect was found in option-state authentication, persistence, source/materialization, dimensions/includes, resources, custom expressions/textures, program state, ID mapping, diagnostic delivery, or the Phase 6 contribution slot.

The complete directive and Appendix F inventory, conformance ownership, pure-`:engine` placement, atomic publication, OQ-7 configurability, and Phase 2 hand-off architecture are otherwise present. Candidate-001's requested unconditional schema advance was cleared on re-derivation: the surviving correction is fingerprint invalidation, with §5.3 applied according to the final chosen semantics. Candidate-002's and candidate-004's possible schema advances are likewise conditional applications of the target's version rule, not independently admitted schema defects.

Prior reviews 1–51 were read only after these conclusions. They do not contain a settled accessor projection for `MinimumEditionRule`, executable `MacroOverrideAction` conflict semantics, or coverage of runtime edition/status in the fingerprint. Earlier screen resolutions established the current formula and exclusions from incomplete contract inferences; they do not clear the present provenance and observed-behavior conflict. Earlier fixture work establishes the profile type and Phase 2 hand-off but does not erase §8's contradictory attribution of recorded fixtures to Phase 1.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=5; notes=0
Interface changed: yes

All admitted defects are localized contract or test-plan corrections; none requires rebuilding the Phase 3 architecture, so `FAIL` is not warranted. `PASS` is unavailable because five corrections remain. Four corrections require edits to the manifest-declared cross-phase interface/change-trigger region; candidate-003's preferred repair does not.

The recent correction trend remains non-convergent: rounds 50–52 are `4 → 5 → 5` (and the supplied last-three pre-round trend was `6 → 4 → 5`), so findings are not strictly decreasing. The next action is to apply all five corrections, update every affected §5 row and associated tests/version rationale, and run a fresh Phase 3 verification round before closure.

## Resolutions

### Corrections applied

1. **candidate-001 — applied.** Section 4.10 now encodes final `CompatibilityStatus` by exact enum name in `ConfigurationFingerprint`; §5 binds it to retained-state invalidation, and a crossing test holds every other input fixed while requiring status and fingerprint to change.
   This closes an omitted fingerprint input without changing component shape or meaning, so it does not independently increment the schema.
2. **candidate-002 — applied.** `minecraftVersion()` retains the exact decoded suffix after `version.`, while `minimumEdition()` retains the exact decoded value; parsed tuples/canonical tokens are comparison-only. Sections 3/5, tests, D-P3-53, and the checklist cover two-/three-component and comparison-equal differently spelled values in source order.
   These were open accessor projections, not changed established meanings or shape, so they do not independently increment the schema.
3. **candidate-003 — applied.** Section 8 now requires Phase-3-authored synthetic `GLCapabilityProfile` values/vectors and emits the selected vectors in P3-C20's Phase 2 hand-off.
   No Phase 1 fixture set or new dependency contract is claimed.
4. **candidate-004 — applied.** Section 4.4 fixes base merge order and the absent/present matrix: `ADD` inserts/rejects, `SUPPRESS` no-ops/removes, and `FORCE` inserts/replaces. It also binds protected targets, replacements, first-error precedence, pre-I/O `INVALID_REQUEST`, and no partial map.
   Section 5, tests, D-P3-54, staging, and the checklist mirror this closure; because no established nested meaning/default or shape changed, it does not independently increment the schema.
5. **candidate-007 — applied using the permitted observed-behavior branch.** Appendix F.4 and the shipped documentation support only syntax, default two, and beyond-18 widening. D-P3-55 separately records the §G7-qualified OptiFine G6 behavioral observation and contract check.
   Configured columns are now a floor raised to the nine-row minimum over the retained array after `*` expansion, including ordinary option, applicable profile/subscreen, and empty slots. The public argument and §§3–5/tests/staging/checklist agree.
   This replaces an established nested `ScreenModel` meaning, so `CURRENT_SCHEMA_VERSION` advances from 13 to 14 with both rejection directions covered.

### Interface/change-trigger

The fingerprint, macro, edition, screen, and schema edits intentionally change the monitored §5 region.
The declared trigger fires: a fresh whole-document Phase 3 verification round is required before closure.

### Notes deferred

None. No notes were admitted; duplicate candidate-005 and Gate-eliminated candidate-006 were not converted into target prose.

### Refusals

None. All five corrections were resolvable within existing authority and candidate-007's expressly permitted branch.
