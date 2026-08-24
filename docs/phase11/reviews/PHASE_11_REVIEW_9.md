# Phase 11 — Verification Review, Round 9

## 0. Method and reading order

Sources read: the target `docs/phase11/v1/PHASE_11_DOC.md` (whole document, with line-exact
re-reads of §4.1 `:440-:503`, §4.9 `:755-:793`, §4.11 `:894-:906`, §5 interface region
`:970-:1063`, §10.1 `:1231-:1249`, §11–§12); the governing design revision
`docs/design/v3/DESIGN.md` (Part I, Phase 11 target spec, doc gate, mandatory template);
`docs/research/v1/RESEARCH.md` (declared selectors); the binding dependency regions in
`docs/phase3/v1/PHASE_3_DOC.md:1420-1689` and `docs/phase6/v1/PHASE_6_DOC.md:1376-1477`; and the
declared supporting evidence as needed for candidate re-derivation.

Every candidate citation was re-resolved against the primary text **before** any prior review was
opened. Prior reviews Rounds 1–8 were then read last, adjudicator-last, and used only to
determine what is already settled.

Deviations: none. Network use: none. Agent fan-out: none — this session dispatched no subagents;
the candidate set was supplied by the engine. Gate drops: none reported.

## 1. Findings

### Finding 1 (candidate-002) — published `ExpressionDiagnostic` names two types declared nowhere

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:767-:768` (§4.9), incorporated as binding by the
  §5 publication clause at `:990-:993`.
- **Claim under test:** every published record component type is pinned well enough that a
  consumer can implement it without guessing.
- **Evidence:** the record at `:764-:772` types two components `DiagnosticSeverity` and
  `DiagnosticChannel`. A whole-document search resolves both names to exactly those two lines:
  no closed-domain declaration (contrast `ExpressionDiagnosticKind` at `:758-:762`,
  `ExpressionType` at `:269`), no owner attribution, and no cited dependency binding region —
  unlike `SourceAttribution`, which §5.2 pins to the Phase 3 binding block. §0.1/§1 declare only
  Phase 3 and Phase 6 dependencies, and §5.5 item 6 (`:1058`) speaks only of *routing* Phase 11
  diagnostics through Phase 1 channels, which is a composition instruction, not a type-provenance
  statement. The §5.1 row at `:985` records "severity/channel" without pinning either domain.
  A consumer of the binding record therefore cannot switch on or display either field.
- **Severity:** correction. This is not the recoverable-by-inference situation of a missing enum
  with an equal-named same-document source; neither domain nor owner is recoverable from the
  target at all, and the field sits in an incorporated, binding publication. It is repairable by
  one short declaration or citation, so it is not blocking.
- **Touches interface/change-trigger region: no.** The ordered repair is a declaration or a
  provenance sentence at §4.9 (`:757-:773`) or in §2.3, both outside the manifest region
  `:970-:1063`. The existing §5.1 row wording ("severity/channel") remains correct under either
  repair, so no edit inside the region is ordered.
- **Ordered fix:** pin both types — either declare their closed domains in Phase 11 alongside
  `ExpressionDiagnosticKind`, or name them as externally owned diagnostic types with a cited
  binding location for the owning phase. If the intended source is an upstream diagnostic
  contract, verify the exact type names before citing them.

### Finding 2 (candidate-001) — p99 budget stated strictly in §4.11, non-strictly in §10.1

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:902-:903` versus `:1238-:1239`.
- **Claim under test:** repeated numeric thresholds are internally consistent.
- **Evidence:** §4.11 writes "p95 total expression time **at or below** 0.25 ms … p99 must stay
  **below** 0.50 ms"; §10.1 restates the same criterion as "p95 ≤ 0.25 ms, p99 ≤ 0.50 ms". The
  deliberate "at or below" for p95 in the same bullet shows the strict phrasing for p99 is not
  loose shorthand. Both statements govern the same retain-interpreter decision (`:904`), and no
  other passage reconciles them — §12's OQ-22 row only defers to "the §4.11 budget".
- **Severity:** note. The divergence bites only on an exact-boundary measurement of a
  deferred (OQ-22 / Phase 14) measured budget; nothing in the buildable contract is falsified.
- **Touches interface/change-trigger region: no.**
- **Recommended (not ordered):** change §4.11 `:903` to "p99 at or below 0.50 ms".

### Finding 3 (candidate-004) — `DeclarationKind` still undeclared in the §4.1 published record

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:456` (§4.1), incorporated by the §5.1 row at
  `:976`.
- **Claim under test:** published record component types are pinned for a direct `compile(request)`
  consumer.
- **Evidence:** `DeclarationKind` resolves to exactly one line, `:456`; the sibling
  `ExpressionType` is declared at `:269` and Phase 3's `CustomExpressionKind { UNIFORM, VARIABLE }`
  is reproduced verbatim at `:1001`. The variant set is determinate only through the equal-named
  conversion sentence at `:487-:491`.
- **Severity:** note, unchanged from Round 8 Finding 2. The variant set is deterministically
  recoverable from same-document text, so no consumer is blocked; only an inference step is
  required. Re-raised here for the record; it remains not ordered.
- **Touches interface/change-trigger region: no.** The remedy is one enum line at §4.1, outside
  `:970-:1063`; as a note it orders no edit at all.

## 2. Checked and clean

Finder clean areas accepted after spot re-derivation:

- **new-surface:** the Round-8 edit is coherent — §0.2's closing sentence, the new §0.11 addendum,
  and §11.2 item 4 name the same outstanding item and each state that a fresh round is required;
  addenda §0.4–§0.11 map one-to-one onto Rounds 1–8; D-P11-9's seven-name union matches RESEARCH
  App F.6; "fourteen booleans" is used consistently throughout; the backend semantic ID and
  `UNSUPPORTED_BACKEND` stable-ID recipe agree between §4.11 and the §5.1 row.
- **interfaces:** §5.2 reproduces the Phase 3 binding algebra field-for-field with matching order,
  duplicate, and fingerprint semantics; §5.3/§5.4 match Phase 6's bridge signature, view algebra,
  six upload commands, three sink outcomes, and the superseding invalid-counter branch; §4.8's
  counter discipline and `Aborted` reservation agree with Phase 6's authoritative-ledger rule;
  §4.12's reset ordering and §5.5's composition handoff are implementable without Phase 7
  internals; the §5.1 incorporation-and-synchronization clause and its change trigger are present.
- **conformance:** every App F.6 token, operator, and function has a conformance row with a
  citation that supports it; the seven-name per-draw exclusion divergence is handled as D-P11-9
  plus the §11.4 upstream request; no unmapped in-scope DESIGN requirement was found.

Cleared before adjudication: **candidate-003** ("two different binding algebras named
`ExpressionValue`") was dropped at Refute by strict majority. On independent re-reading the two
occurrences are distinguished by their declaring contexts and do not create a consumer-visible
collision in the published surface; I concur with the drop and record no finding.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=2
Interface changed: no

One correction (Finding 1) and two notes (Findings 2 and 3). No admitted finding orders an edit
inside the manifest-declared interface region `cross-phase-interfaces`
(`docs/phase11/v1/PHASE_11_DOC.md:970-:1063`), so the region's change trigger is not fired by this
round.

Trend/convergence: Rounds 6→9 show 5 → 4 → 1 → 1 corrections with interface churn ceasing after
Round 6. The remaining correction is a single localized declaration gap, not a structural miss, so
FAIL is not warranted and convergence is intact; no convergence warning is raised.

Next required action: apply Finding 1's ordered fix in §4.9 (pin `DiagnosticSeverity` and
`DiagnosticChannel` by declaration or cited provenance, verifying exact upstream type names),
record the Round-9 addendum, and run one further verify round before Phase 11 can close.


## Resolutions

### Finding 1 (correction) — applied

Re-derived independently: a whole-document search for `DiagnosticSeverity` and `DiagnosticChannel`
resolved to exactly the two record component lines in §4.9, with no declaration, owner, or cited
dependency binding anywhere in the target; §5.1's `ExpressionDiagnostic` row records only
"severity/channel". Phase 1 is not a declared dependency of Phase 11, so citing an upstream
diagnostic type would have invented a dependency. The repair therefore declares both domains as
Phase 11-owned and closed, in the §4.9 code block beside `ExpressionDiagnosticKind`:
`DiagnosticSeverity { WARNING, ERROR }` and `DiagnosticChannel { CHAT_AND_LOG, LOG_ONLY }`, plus
four lines mapping them onto §4.9's existing ladder — `ERROR` for a diagnostic that disabled a
uniform or variable, `WARNING` otherwise; `CHAT_AND_LOG` for the chat-visible sanitized summary
that is also logged, `LOG_ONLY` for detail such as the dependency path that §4.9 already says is
log-only. The variants are read off existing §4.9 prose, not newly decided. §5.1's row wording
remains accurate and was not edited, so the interface region `:970-:1063` is unchanged.

### Notes deferred

- **Finding 2 (p99 strict/non-strict).** Not applied. The review records it as a note ordering no
  edit; it bites only at an exact boundary of a measured budget deferred to OQ-22 / Phase 14, and
  changing §4.11 would edit a normative threshold sentence on a non-ordered basis, adding
  unreviewed surface without resolving the deferred measurement.
- **Finding 3 (`DeclarationKind` undeclared).** Not applied. Re-checked: the name appears only at
  §4.1 and its variant set is deterministically recoverable from the equal-named conversion
  sentence and Phase 3's verbatim `CustomExpressionKind { UNIFORM, VARIABLE }`. The review
  re-raises it as a note "for the record; it remains not ordered", so no edit is warranted this
  round.

Also recorded: addendum §0.12 in the target, six lines, matching the existing §0.4–§0.11 style.