# Phase 11 adversarial review — round 4

## 0. Method and reading order

I independently re-derived all six gated candidates against the complete Phase 11 target, the
manifest-selected v3 governing design selectors, the selected RESEARCH.md authority, the binding
Phase 3 and Phase 6 contracts, and the relevant permitted supporting evidence. Only after settling
those interpretations did I read `docs/phase11/reviews/PHASE_11_REVIEW_1.md`,
`docs/phase11/reviews/PHASE_11_REVIEW_2.md`, and
`docs/phase11/reviews/PHASE_11_REVIEW_3.md`, including their resolutions.

I did not use the network, forbidden transcripts, or forbidden path patterns. I did not invoke the
verification harness, start another Codex process, or use agent fan-out. There were no reading-order
deviations, pre-adjudication eliminations, or Gate drops.

## 1. Findings

### candidate-001 — Unsupported-backend diagnostics conflict with the general stable-ID rule

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:735`–`:736`, `:832`–`:836`, and the binding
  incorporation at `:928`–`:932`.
- **Claim:** The pre-plan unsupported-backend failure must be reconciled with the document-wide
  diagnostic identity contract.
- **Evidence:** Section 4.9 unqualifiedly says stable IDs derive from diagnostic kind, plan
  fingerprint, declaration ordinal, and source span (`docs/phase11/v1/PHASE_11_DOC.md:735`–`:736`).
  Section 4.11 instead requires an unsupported-backend diagnostic before parsing or plan creation,
  deriving its ID from kind, pack fingerprint, and requested backend ID
  (`docs/phase11/v1/PHASE_11_DOC.md:832`–`:836`). Both semantics are expressly incorporated into
  the binding §5 publication (`docs/phase11/v1/PHASE_11_DOC.md:928`–`:932`).
- **Severity:** correction. Qualify §4.9 for diagnostics with plan/declaration context and state the
  pre-plan exception explicitly, keeping §4.11 and the §5 backend row synchronized.
- **Touches interface/change-trigger region:** yes.

### candidate-002 — Refresh requests context before its required frame counter exists and validates a nonexistent snapshot epoch

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:675`–`:680` and `:763`–`:777`.
- **Claim:** The refresh algorithm must order and describe context acquisition using values exposed
  by its published API.
- **Evidence:** The algorithm fetches and “validate[s]” a context snapshot before resolving
  `frameCounter`/`frameTime` (`docs/phase11/v1/PHASE_11_DOC.md:675`–`:680`), but constructing
  `ExpressionContextRequest` already requires `frameCounter`, and the returned snapshot contains no
  epoch (`docs/phase11/v1/PHASE_11_DOC.md:763`–`:777`). The target identifies `frameCounter` and
  `frameTime` as Phase 6 fixed inputs (`docs/phase11/v1/PHASE_11_DOC.md:550`–`:552`), so no earlier
  source is defined.
- **Severity:** correction. Resolve the clock inputs first, then define validation as correlation of
  the controller-issued request epoch with the current refresh/memo epoch (or remove the inaccurate
  snapshot-epoch wording). This repair need not change the provider record shapes.
- **Touches interface/change-trigger region:** no.

### candidate-004 — Reset promises random-state invalidation without an implementable mechanism

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:597`–`:603`, `:868`–`:904`, and `:988`–`:992`.
- **Claim:** The lifecycle contract must expose or assign a mechanism that fulfills every promised
  random-stream reset.
- **Evidence:** The injected plan-lifetime `RandomSource` exposes only `nextFloat()`, yet its stream
  must reset on lifecycle events (`docs/phase11/v1/PHASE_11_DOC.md:597`–`:603`). The controller
  receives that source only through `activate`, while `reset` receives only a reason
  (`docs/phase11/v1/PHASE_11_DOC.md:868`–`:889`), and every reset promises to invalidate random state
  without specifying tuple deactivation or reactivation (`:891`–`:904`). The composition handoff
  requires initial source construction and later reset forwarding, but not replacement
  (`docs/phase11/v1/PHASE_11_DOC.md:988`–`:992`).
- **Severity:** correction. Define per-reset postconditions: either deactivate the tuple and require
  fresh activation with a new source, including interim refresh behavior, or publish a reset/reseed
  or source-factory capability. Synchronize §4.12 and the affected §5 rows.
- **Touches interface/change-trigger region:** yes.

### candidate-006 — Function rows over-attribute phase-selected semantics to Appendix F.6

- **Location:** `docs/phase11/v1/PHASE_11_DOC.md:316`–`:344`.
- **Claim:** Each conformance-map row must distinguish the authoritative function surface from
  phase-local choices that supply additional exact semantics.
- **Evidence:** Appendix F.6 enumerates function names and broad signature shapes, adding exact
  behavioral detail only for `smooth()` (`docs/research/v1/RESEARCH.md:1506`–`:1510`). The Phase 11
  rows nevertheless cite only Appendix F.6 for additional choices such as Java `atan2` signed-zero
  behavior, exact domains and formulas, argument visitation, invalid-range handling, laziness, and
  coercion (`docs/phase11/v1/PHASE_11_DOC.md:316`–`:344`). The target itself says the authority
  enumerates the named surface (`docs/phase11/v1/PHASE_11_DOC.md:72`–`:78`) and records phase-local
  semantic decisions in §11 (`:1197`–`:1203`). The mandatory template requires provenance and
  flagged decisions in the conformance map (`docs/design/v3/DESIGN.md:831`–`:835`).
- **Severity:** correction. Audit §3.1–§3.2 provenance cells: retain Appendix F.6 for names and
  documented shapes, and cite applicable phase decisions, detailed normative sections, or permitted
  behavioral evidence for added semantics. Do not imply Appendix F.6 specifies those details.
- **Touches interface/change-trigger region:** no.

## 2. Checked and clean

The Round-3 addendum, corrected `pi` bits, newest dependency-review references, compiler/backend
selection, diagnostic publication, provider seam, refresh ordering, lifecycle/random ownership,
complete §3 language/input coverage, Phase 3 declaration projection, and Phase 6 schema/bridge
contracts were checked. Apart from the admitted findings, the canonical interpreter ID and
unsupported-ID outcome are synchronized between §4.11 and §5, and the dependency contracts remain
truthfully consumed.

Candidate-003 is cleared as duplicative and over-prescriptive. Candidate-002 captures the same
epoch defect plus the independent frame-ordering defect. Because `snapshot(request)` is synchronous,
the specification can satisfy freshness by defining controller-side request/current-epoch
correlation; the evidence does not compel an echoed epoch field or a §5 record-shape change.

Candidate-005 is cleared against settled material. Round 1 expressly adjudicated and cleared the
same proposed omission: the v3 doc gate separately requires every Appendix F.6 token/function/
operator in the conformance map and requires evaluator criteria and error semantics to be written
(`docs/design/v3/DESIGN.md:2344`–`:2348`). Section 3 maps the RESEARCH/App F.6 contract surface,
while §§4.9 and 4.11 provide the separately required error and evaluator architecture. No new
authority or target change overturns that prior disposition.

Finder-reported clean areas were confirmed: the `pi` constant is consistently `0x40490fda`, Phase
3 Review 34 and Phase 6 Review 22 are consistently cited, the seven-name exclusion union is
complete, and the Phase 3/Phase 6 binding interfaces are accurately represented.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

All four admitted defects are bounded corrections rather than structural misses. Candidates 001
and 004 require changes or clarifications within the incorporated §5 interface/change-trigger
region; candidates 002 and 006 do not. Candidates 003 and 005 are dropped.

Trend/convergence: correction counts are four, three, four, and four across Rounds 1–4. The loop
has not converged to literal PASS; this round is flat rather than worsening, but repeated
second-order interface and traceability defects remain.

Next required action: apply a scoped fix-up for this review, append resolutions, and conduct a
fresh Phase 11 verification round because §5 changes before Phase 11 can close.

## Resolutions

### candidate-001 — applied

Qualified §4.9's plan/declaration stable-ID formula and named the §4.11 pre-plan
`UNSUPPORTED_BACKEND` exception. The backend-selection row in §5 now repeats the exception's kind,
pack-fingerprint, and requested-ID inputs, so the incorporated diagnostic contract is consistent.

### candidate-002 — applied

Reordered §4.8 to resolve `frameCounter` and `frameTime` from the published Phase 6 value view
before constructing the context request. Freshness is now controller-side: the request carries the
new refresh/memo epoch and its synchronous result is accepted only while that issued epoch remains
current. No nonexistent snapshot epoch or provider-record change was introduced.

### candidate-004 — applied

Selected tuple deactivation rather than adding an unsupported reseed API. Every reset now drops the
sole plan/provider/random tuple, discards the old stream, and makes refresh return `NoCustoms` until
fresh activation supplies a new source. Synchronized the §5 controller and random rows and the
composition handoff. These are intentional edits to the declared interface region, so a fresh
verification round is required.

### candidate-006 — applied

Audited §§3.1–3.2 and separated authoritative surface provenance from Phase 11 semantics. The table
text now limits Appendix F.6 to names and documented shapes, points exact behavior to §§4.2/4.6,
and records D-P11-13 for the phase-selected typing, domains, coercion, evaluation-order, and finite
result rules. `smooth` retains its distinct behavioral evidence and D-P11-7.

### Notes deferred

None. The adjudicator admitted no notes, and no correction required a new upstream decision or
contradicted the selected authority.
