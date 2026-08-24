# Phase 11 — Verification Review, Round 10

Target: `docs/phase11/v1/PHASE_11_DOC.md` (whole document)
Manifest: `verification/targets/phase-11.json`
Governing design revision: `docs/design/v3/DESIGN.md` (v3, override selection)

## 0. Method and reading order

Sources read before judgment: the target regions cited by every candidate plus their surrounding
context (§3.2 function table `:350-:391`, §4.7–§4.9 `:700-:812`, §4.11 `:879-:921`, §4.12
`:923-:981`, the declared interface region §5 `:985-:1078`, §6 opening); the governing design
revision's Part I, Phase 11 target spec, doc gate, and mandatory template; the declared RESEARCH
selectors (including App F.6 `:1493-:1513`); the binding dependency regions
`docs/phase3/v1/PHASE_3_DOC.md:1420-1689` and `docs/phase6/v1/PHASE_6_DOC.md:1376-1477`, plus the
Phase 6 bridge-installation paragraph `:1248-:1265` that candidate-002 turns on; and
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties` around the documented function list.

Every candidate citation was re-resolved line-exact against the primary text **before** any prior
review was opened. Prior reviews Rounds 1–9 were then read last, adjudicator-last, and used only to
establish settled material and recurrence.

Deviations: none. No forbidden source was read — no `docs/**/chatlogs/**`, no `*.txt`, no prior
agent or session transcript. Network use: none. Agent fan-out: none; this session dispatched no
subagents and the candidate set was supplied by the engine. Gate drops: none. Candidates eliminated
before adjudication: none.

## 1. Findings

All four surviving candidates were confirmed as real but note-level on independent re-derivation.
No finding is ordered for fix-up.

### Finding 1 (candidate-001) — §4.9's ERROR/WARNING sentence is phrased only for per-declaration disablement

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:785-:786`, against `:890-:893` (§4.11), `:997`
  (§5.1 backend-selection row), and `:807-:808` (§4.9 abort bullet).
- **Claim under test:** the Round-9 sentence "`ERROR` marks a diagnostic that disabled a uniform or
  variable; `WARNING` marks one that did not" classifies every `ExpressionDiagnostic` the document
  specifies.
- **Evidence:** the pre-plan `UNSUPPORTED_BACKEND` diagnostic is called an "error" at `:891` and at
  the binding §5.1 row `:997`, yet `:893` states that no declarations are parsed and no plan is
  produced, so no individual uniform or variable is disabled by it; the feature-level abort bullet
  at `:807-:808` likewise disables no single declaration.
- **Severity: note.** The classification is recoverable, not contradictory: a build that produces
  no plan leaves every custom uniform disabled, and the abort path explicitly leaves "custom
  uniforms absent", both of which satisfy the ERROR criterion under the natural reading. §4.11 and
  the incorporated §5.1 row state the severity explicitly, so no consumer can derive WARNING for
  these cases. What remains is prose that names only the per-declaration case.
- **Touches interface/change-trigger region: no.** The recommended wording change lands at
  `:785-:786`, outside `:985-:1078`; §5.1's "severity/channel" row stays correct either way, and as
  a note it orders no edit at all.
- **Recommended (not ordered):** extend the sentence so ERROR also covers a diagnostic that
  disabled the plan build or the custom-expression feature for the refresh.

### Finding 2 (candidate-002) — §5.5's install-timing phrasing is looser than Phase 6's binding window

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:1066` (§5.5 item 1); also `:949-:950` and
  `:715-:716`.
- **Claim under test:** the published composition handoff matches the consumed Phase 6
  bridge-installation window.
- **Evidence:** Phase 6 closes the window "after construction and before the first `beginFrame` or
  participant activation" and throws `IllegalStateException` on a late call
  (`docs/phase6/v1/PHASE_6_DOC.md:1253`–`:1257`). The target never uses the token `beginFrame`; it
  says "before first activation" (`:1066`) and "before first use" (`:715`, `:949-:950`).
- **Severity: note.** Phase 6 owns and defines the window, and §4.8 defers to it by exact binding
  citation (`docs/phase6/v1/PHASE_6_DOC.md:1253`–`:1259`, "as Phase 6 requires"); §5.3 enumerates
  the consumed grants by line range. Phase 11 grants no wider permission and states no conflicting
  rule — it under-specifies a rule it explicitly delegates. §5.5 item 4 additionally forbids any
  registry/uniform-runtime use before activation, which orders installation ahead of first frame
  use in practice. This is editorial precision, not a consumer-hittable divergence.
- **Touches interface/change-trigger region: no.** The cited line sits inside `:985-:1078`, but as
  a note this finding orders no edit there, so the region's change trigger does not fire.
- **Recommended (not ordered):** in §5.5 item 1, state Phase 6's bound explicitly — install after
  uniform-runtime construction and before the first `beginFrame` or participant activation.

### Finding 3 (candidate-003) — §5.1's incorporation list omits §4.8

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:1005-:1009` versus §4.8 `:713-:759`.
- **Claim under test:** the declared change-trigger region plus its incorporated sections bound
  every consumer-visible Phase 11 promise.
- **Evidence:** the incorporation clause names §§2.3, 4.1, and 4.9–4.12 but not §4.8, which carries
  the `Aborted` reservation (`:737-:742`) and the declared-type→Phase 6 command mapping
  (`:748-:759`).
- **Severity: note.** The Phase-6-facing substance is already inside the binding set: incorporated
  §4.9 assigns `Aborted` to provider/backend invariant failures (`:807`) and binds rung-1 isolation
  for expression-local errors (`:810`), and §5.4 (`:1058`) — itself inside the declared region —
  binds Phase 6's three authoritative refresh counters and closed sink outcomes. What is genuinely
  unhooked is the declared-type→command mapping table, whose semantics Phase 6 owns (type
  validation and `Bool1` encoding, §5.4). No consumer-visible promise can drift undetected.
- **Touches interface/change-trigger region: no.** As a note it orders no edit; extending the
  incorporation set would in any case bind controller-internal refresh sequencing as published
  interface, which is broader than the defect warrants.
- **Recommended (not ordered):** add a non-normative "(see §4.8)" pointer, or fold only the
  declared-type→command mapping into the §5.1 controller row.

### Finding 4 (candidate-004) — `pow(x,y)`'s arity is attributed to a source that fixes only the name

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:378`, under the §3.2 header claim at `:354-:356`.
- **Claim under test:** each conformance row's disposition is supported by its cited source and any
  divergence from the shipped pack-author specification is flagged.
- **Evidence:** §3.2's header asserts that Appendix F.6 establishes each non-`smooth` row's name
  *and documented signature shape*, but RESEARCH `:1507-:1508` lists `pow` as a bare name with no
  arity, while the shipped pack-author documentation writes `#   pow(x)`
  (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:396`) — the same source that does
  supply explicit arities for the neighbouring rows (`min(x, y ,...)`, `atan2(y, x)`,
  `clamp(x, min, max)`, `fmod(x, y)`), all of which the table matches. A whole-document search for
  `pow` finds only this row and the §8.2 test bullet at `:1170`; §11.2/§11.4 flag `round`, varargs,
  and the exclusion-list mismatch but never this one.
- **Severity: note.** The mandated behavior is unambiguous and correct (two-argument base/exponent,
  Java real-power, non-finite is an error); the defect is a provenance/flagging gap, not a
  contradiction an implementer can be misled by.
- **Touches interface/change-trigger region: no.** Line 378 is far outside `:985-:1078`.
- **Recommended (not ordered):** add one qualifier recording that Appendix F.6 fixes only the name,
  that the shipped documentation's `pow(x)` conflicts with the two-argument form, and that
  D-P11-13/§4.6 rule the two-argument signature authoritative — the treatment already given to
  `round` and `.w/.a`.

## 2. Checked and clean

**Candidate re-derivation.** All four candidates are factually confirmed but none rises above a
note. Candidate-001 and candidate-003 each survive only as descriptive imprecision because the
binding text elsewhere (§4.11/§5.1 for the diagnostic severity; §4.9/§5.4 for the abort reservation
and counter fidelity) already determines the answer. Candidate-002 survives only as wording, since
Phase 6 owns the install window and §4.8 defers to the exact lines that define it. Candidate-004
survives as a provenance gap over correct behavior. No candidate was eliminated before adjudication
and there were no Gate drops, so there is no pre-settled eliminated material to discuss.

**New-surface lens (accepted after spot re-derivation).** The Round-9 edit stands: `WARNING/ERROR`
and `CHAT_AND_LOG/LOG_ONLY` are declared inline in the §4.9 code block beside
`ExpressionDiagnosticKind`, both domains are stated as Phase 11-owned and closed, and the variants
are exercised consistently by §4.9's bullets, §6's coalescing rule, and §5.1's "severity/channel"
row. No upstream Phase 3, Phase 6, or DESIGN type of either name exists, so the ownership claim is
unchallenged. §0.12's claims that no §5 text changed and that the §0.9 outstanding requirement
still stands are consistent with §0.2 and §11.2 item 4. This discharges Round 9's Finding 1.

**Interfaces lens.** §5.2 reproduces Phase 3's binding algebra field-for-field with accurate
anchors (`:1442` publication row, `:1459`–`:1480` algebra), including immutability, source order,
duplicate retention, and fingerprint participation. §5.3/§5.4's Phase 6 citations land on the
incorporated declarations and the §5.1 rows at `:1389`–`:1391`; the bridge signature, view algebra,
upload commands, three sink outcomes, `Bool1` ownership, accepted-prefix rule, and the superseding
invalid-counter branch are restated faithfully. The declared region `:985`–`:1078` exactly spans §5
including §5.5. §4.12's lifecycle map and §5.5's handoff remain implementable without Phase 7
internals.

**Conformance lens.** Every App F.6 token, operator, declaration form, named function and
constructor, member/matrix access, input family, cadence rule, and precipitation handoff is a
mapped row with resolving provenance; the fourteen `is_*` names, the `BIOME_*` map, and the
seven-name per-draw exclusion union are correct, with the DESIGN five-name divergence flagged as
D-P11-9 plus the §11.4 upstream request rather than silently adopted. The doc-gate items (App F.6
coverage, smooth state machine, evaluator interface and selection criteria, error semantics per
ladder, stareval license outcome) are all present, the mandatory thirteen sections exist, and the
phase carries no assigned open question.

**Prior-round notes re-checked.** Round 9's Findings 2 (p99 strict/non-strict) and 3
(`DeclarationKind`) were deferred as notes and remain unrepaired; both are unchanged in the text
and were not re-raised by any surviving candidate this round. They remain non-ordered and do not
affect this verdict.

## 3. Verdict

# PASS
Counts: blocking=0; corrections=0; notes=4
Interface changed: no

Interface disposition: no admitted finding orders an edit inside the manifest-declared
`cross-phase-interfaces` region (`docs/phase11/v1/PHASE_11_DOC.md:985`–`:1078`). Candidate-002's
cited line sits inside that region, but as a note it orders no edit, so `touches_interface` is
`false` under the "would change the region" test and the region's change trigger does not fire.

Trend and convergence: Rounds 6→10 show 5 → 4 → 1 → 1 → 0 corrections, with interface churn ceasing
after Round 6. Round 9's convergence warning (corrections not strictly decreasing, 4 → 1 → 1) is
discharged this round: the one ordered correction from Round 9 was applied and verified, and no
candidate survived re-derivation above note level. The four notes are localized wording and
provenance refinements, none of which is a structural miss, so neither FAIL nor a further ordered
round is warranted.

Next required action: none ordered. Phase 11 may close on this round. The four notes are recorded
for optional editorial pickup and are not fix-up work; if any is applied, keep the edit outside
§5 so the interface change trigger stays unfired.
