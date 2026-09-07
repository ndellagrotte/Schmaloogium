# Phase 3 Verification Review — Round 54

## 0. Method and reading order

I independently re-derived every surviving candidate before consulting prior reviews. The source
order was:

1. `docs/phase3/v1/PHASE_3_DOC.md`, with focused reads of the revision ledger, canonical public
   declarations, discovery limits, directive and properties conformance rows, resource and program-
   state models, canonical encoding, the complete manifest-declared §5 interface region, tests, and
   the closing chronology;
2. the resolved authority in `docs/design/v3/DESIGN.md`: Part I, the Phase 3 target specification,
   the document gate, and the mandatory thirteen-section template;
3. the relevant contract ground truth in `docs/research/v1/RESEARCH.md`, especially §§3.2–3.3,
   Appendix A.3, Appendix B.1/B.5, and Appendix F.7;
4. the binding Phase 1 contract in `docs/phase1/v14/PHASE_1_DOC.md`, especially the public
   `EngineDiagnostic` shape, routing semantics, and §5 consumer assignment; and
5. the supplied candidate, refutation, Gate, and finder-clean-area records.

I searched the whole target for equivalent diagnostic-field mappings, closed diagnostic-argument
runtime types, snapshot encodings, and canonical/legacy buffer-name projections before settling
interpretation, severity, and interface impact. The permitted Pintonium and Oculus reports were not
needed to decide these candidates. Only after those judgments were settled did I read all prior
reviews `PHASE_3_REVIEW_1.md` through `PHASE_3_REVIEW_53.md`, in round order and including their
resolutions, as historical settled material rather than authority over the current bytes.

There were no reading-list deviations, no network use, and no agent or session fan-out. I did not
invoke `$verify-loop`, run `scripts/verify`, start another verification session, or read any
forbidden transcript, `docs/**/chatlogs/**` path, or `*.txt` source.

The Gate dropped pre-adjudication `candidate-004` because a required evidence anchor was a forbidden
`*.txt` source. I did not open that source, revive the candidate, or derive a finding from it.

## 1. Findings

### candidate-001 — The closing verification ledger stops two fix-up transitions before the current surface

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:341-347,3689-3698`.

**Claim:** The closing chronological ledger accurately identifies the current §0 fix-up surface and
the review/fix-up transitions that produced it.

**Evidence:** The header contains `### 0.52 Round 52 fix-up` and `### 0.53 Round 53 fix-up`; §0.53 is
also the current `Last revised` marker. The terminal italic ledger, however, ends with “round 51
reviewed §0.50 and produced §0.51” before its still-correct statement that Phase 3 remains
unverified. This is not an intentionally arbitrary history prefix: the target previously calls the
footer the “closing verification ledger” (`:260`) and describes it as the “latest-surface hand-off
and ledger” (`:314-315`). The current ledger therefore omits that Round 52 reviewed §0.51 and
produced §0.52 and that Round 53 reviewed §0.52 and produced §0.53.

Prior history reinforces rather than clears this defect. Rounds 26, 33, 40, and 46 treated stale
terminal chronology as a correction, and Round 53's resolution changed the current surface without
appending its transition to this separate ledger.

**Severity:** correction. Append the Round 52 and Round 53 transitions through §0.53 while retaining
the existing pending-fresh-whole-document-review and no-version-roll status. No schema or §5 change
is needed.

**Touches interface/change-trigger region:** no. The ordered repair is footer-only and does not edit
the manifest-declared cross-phase region.

### candidate-002 — `PackLoadFailure.primaryDiagnostic` has no executable field mapping

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:2846-2866,3237-3246`; dependency contract at
`docs/phase1/v14/PHASE_1_DOC.md:3763-3797`.

**Claim:** For each closed load-failure outcome, Phase 3 implementers and Phase 7/12 consumers can
determine the exact public `EngineDiagnostic` carried by `PackLoadFailure` without inventing
localization, routing, argument, or detail policy.

**Evidence:** `PackLoadFailure` publicly embeds a non-null Phase 1 `EngineDiagnostic`, and Phase 1
makes six values observable: severity, `UserChannel`, message key, `List<Object>` arguments, detail,
and log channel. Phase 1 further makes the key the localization seam and the channel determine chat,
shader-GUI, or log-only routing. Phase 3 exhaustively maps causes to eight `PackLoadFailureCode`
values, but describes the embedded diagnostic only as having an immutable loader-neutral argument
list, “sanitized” detail, and the “appropriate” severity, channel, key, and log channel. A whole-
target search finds concrete `WARN/LOG_ONLY` choices only for malformed directives, not a load-
failure mapping.

The named `loadFailure_primaryDiagnosticReporterCorrespondenceAndLocalization` test requires a
message key, immutable arguments, sanitized detail, and exact reporter/result correspondence, but
there is no expected key, argument schema, sanitization rule, or routing tuple against which to
implement those assertions. Round 50 correctly replaced unsupported display text and synthetic IDs
with one `EngineDiagnostic`, and Round 51 closed cause-to-code classification and reporter behavior;
neither resolution closed the diagnostic's public field values. General earlier clean statements do
not specifically settle that missing mapping.

**Severity:** correction. Add a binding §5.1 table or deterministic default-plus-exceptions rule for
every `PackLoadFailureCode` and any cause variant intentionally producing a different payload. It
must fix `DiagnosticSeverity`, `UserChannel`, exact localization key, ordered non-null loader-neutral
argument types and values, deterministic detail presence/sanitization/redaction and bound, and one
fixed Phase 3 log channel. Update the named localization test to assert those values, exact equality
between the reported diagnostic and `failure.primaryDiagnostic()`, and the null-reporter branch.

**Touches interface/change-trigger region:** yes. The correction defines observable fields of the
Phase 7/12-facing failure value in binding §5.1, so the manifest change trigger fires.

### candidate-003 — Discovery snapshot byte accounting cannot encode diagnostic arguments deterministically

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1510-1519,2363-2399,2439-2446,2781-2787`;
dependency type at `docs/phase1/v14/PHASE_1_DOC.md:3774-3780`.

**Claim:** `maxSnapshotBytes` has one canonical encoding for every public value that Phase 3 says
participates in a discovery snapshot's exact byte count.

**Evidence:** Discovery counts the §4.10 canonical encoding of every candidate and result diagnostic,
recursively including all public scalar, string, and list fields. The exposed §5 discovery row makes
the exact byte-overflow result part of the Phase 7/12 contract. The referenced codec defines records,
sealed variants, optionals, maps, lists/sets, booleans, integers, enums, strings, and finite floats.
The incorporated Phase 1 diagnostic, however, exposes `List<Object> args`. Phase 3 constrains its
arguments only as immutable and loader-neutral. It neither closes the non-null runtime value set nor
defines dynamic type tags, dispatch, nested-value policy, or rejection behavior for an `Object`
element. Equal-looking values of different Java runtime types can consequently have no specified
encoding, and an implementation cannot derive the promised exact snapshot count for every value the
public carrier permits.

Producer ownership does not cure an unstated invariant: Phase 3 could choose to emit only strings or
a small primitive algebra, but the document must state that restriction and its encoding. Round 45
introduced the exact snapshot-byte/overflow policy, while Round 47 completed the distinct
`ResourceRequirements` producer codec; neither addressed the erased `Object` element type in
`EngineDiagnostic.args`. No prior review specifically settled this boundary.

**Severity:** correction. In the detailed codec and binding §5 discovery contract, define a closed,
non-null value algebra for arguments in every Phase-3-produced diagnostic that may enter
`PackDiscoveryResult` or `PackCandidate`, with an unambiguous type tag and canonical encoding for
each allowed variant and an explicit nested-list/null/unsupported-value policy. Define the framing
and nullability of the remaining diagnostic components as used by snapshot accounting. Add boundary
vectors for same-text/different-type values, each allowed argument variant, exact counts immediately
below/at/above the limit, unsupported-value rejection, and proof that the fixed overflow snapshot
fits the configured minimum.

**Touches interface/change-trigger region:** yes. This closes the exact discovery-byte semantics
published in §5.1 to Phases 7 and 12 and therefore requires a monitored interface update.

### candidate-005 — Pack buffer spellings are not projected to `ColorAttachmentKey` indices

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1388,1412-1436,2083-2086,2223-2225,3287-3292`;
authoritative mapping at `docs/research/v1/RESEARCH.md:1200-1208,1272-1274`.

**Claim:** The producer grammar for `flip.<prog>.<buf>`, uniform sizing, and legacy attachment
format forms completely maps accepted pack-facing buffer names into the index-only
`ColorAttachmentKey` model.

**Evidence:** RESEARCH fixes the projection: `colortex0` through `colortex7` map to 0 through 7,
and `gcolor`, `gdepth`, `gnormal`, `composite`, and `gaux1` through `gaux4` map to those same indices
0 through 7. The target publishes only `ColorAttachmentKey(int colortexIndex)` and makes
`FlipBufferKey` a wrapper around that index-backed value. Its conformance rows nevertheless say only
“legacy names,” “legacy-name format,” “aliases,” or a “normalized” `ColorAttachmentKey`; no target
text enumerates the accepted spellings and all sixteen exact projections.

The named tests do not supply the missing oracle. The expanded flip test covers eligible outer
program families and rejection of a virtual program token in the buffer position, while the alias
labels do not state expected legacy-name indices. Isolated `gdepth`→1 and `GAUX4FORMAT`→7 behavior
cannot derive the other six legacy projections or prove that every applicable parser uses one
normalizer. Earlier reviews settled virtual-pre placement, family filtering, and the index-only
consumer shape, not this complete producer-side spelling map.

**Severity:** correction. Define one canonical, case-sensitive Phase 3 buffer-name normalizer that
maps `colortex0`–`colortex7` and `gcolor`, `gdepth`, `gnormal`, `composite`, `gaux1`–`gaux4` to
indices 0–7 and rejects every other spelling through the existing line-local warn/ignore behavior.
Reference it from `flip` and every uniform/directive parser whose grammar admits canonical or legacy
buffer names. Add exhaustive normalizer vectors and integration assertions for each applicable
parser family.

**Touches interface/change-trigger region:** no. The correction closes producer grammar in §§3–4
and tests while preserving the existing index-only public representation; the target itself
classifies directive spellings and aliases as producer grammar rather than downstream contract.

## 2. Checked and clean

- The finder-reported new §0.52/§0.53 surface is otherwise aligned. The canonical schema constant,
  §5.1 declaration row, §5.3 history, ID-map boundary, staging/checklist references, and literal and
  bidirectional schema-13/schema-14 tests consistently use schema 14. The macro action matrix and
  narrowed D-P3-54 agree, including absent `SUPPRESS` as a no-op. The expanded-slot screen formula,
  provenance, interface row, tests, and schema rationale consistently use the nine-row floor.
- The remaining cross-phase interface surface is substantively connected and honest against the
  Phase 1 binding contract. Public acquisition/domain isolation, durable discovery references,
  safe persistence, finalized-option-only materialization/evaluation, dimensions and sources,
  resource and texture algebras, ID maps, and schema/version discipline yielded no additional
  candidate-backed finding. The missing jcpp build pin/seam allowance remains requested rather than
  silently assumed.
- The conformance sweep otherwise retains the complete Appendix F.1 ownership table, the remaining
  Appendix F.2–F.8 and Appendix A.3 rows, the required Pintonium B1/B2/B3/B12 dispositions, source
  attribution, persistence, preprocessing, macros/OQ-7, pure-`:engine` placement, tests, and
  checklist coverage.
- No surviving candidate was refuted, cleared, consolidated, or downgraded on re-derivation. Each
  identifies a distinct repair: ledger traceability, load-failure diagnostic payloads, discovery
  diagnostic byte encoding, and pack-buffer spelling normalization.
- Pre-adjudication `candidate-004` remains excluded solely by the Gate's fail-closed rejection of its
  forbidden `*.txt` anchor. It is not included in candidate dispositions or counts.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

All four admitted findings are localized documentation, producer-grammar, codec, or public-contract
repairs; none requires rebuilding the Phase 3 architecture, so `FAIL` is not warranted. The nonzero
correction count makes literal pass unavailable. Candidates 002 and 003 require changes to the
manifest-declared cross-phase interface region; candidates 001 and 005 do not.

The recent correction trend is not converged. Rounds 52 and 53 reported five and two corrections,
while this round rises to four; prior literal passes and broad clean statements do not override the
current independently derived defects.

Next action: apply scoped fixes for candidates 001, 002, 003, and 005, recording their resolutions
without broadening settled contracts. Because the diagnostic-payload and discovery-byte repairs must
update binding §5.1, the manifest change trigger fires. A fresh whole-document Phase 3 verification
round is required before Phase 3 may close or be consumed as a verified dependency.

## Resolutions

### candidate-001 — resolved

The terminal chronology was independently compared with the extant §0.51–§0.53 headings. It now
records the Round 52 and Round 53 transitions omitted from the reviewed surface and the present
Round 54 transition to §0.54, while retaining the pending fresh-review and no-version-roll status.
This footer-only repair is outside the manifest interface region.

### candidate-002 — resolved

Binding §5.1 now gives every `PackLoadFailureCode` one exact Phase 1 diagnostic payload: all use
`ERROR`, `CHAT`, immutable empty arguments, empty detail, and `schmaloogium.pack`, with one exact
localization key per code. Empty detail deterministically bounds and redacts cause data, and no
cause variant changes the code-selected payload. The tests now assert every field, exact instance
correspondence with the one reported value, every cause variant, and the null-reporter result.
This intentionally changes the manifest-declared interface region.

### candidate-003 — resolved

Section 4.10 now restricts Phase-3-produced diagnostic arguments to exact non-null boxed `String`,
`Boolean`, `Integer`, and `Long` values, gives each a dynamic type tag and canonical payload, frames
all remaining diagnostic fields, and rejects null, nested, or unsupported values before
publication. Binding §5.1 applies that codec to candidate/result diagnostics and fixes below/at/
above-limit behavior plus the minimum encoded overflow snapshot. Added tests cover every variant,
same-text/different-type values, unsupported inputs, exact boundaries, and the fixed minimum.
This intentionally changes the manifest-declared interface region.

### candidate-005 — resolved

The target now defines one case-sensitive normalizer with the exact Appendix B.1/B.5 projection:
`colortex0`–`colortex7` and `gcolor`, `gdepth`, `gnormal`, `composite`, `gaux1`–`gaux4` map to
indices 0–7. Uniform sizing, format, clear, clear-color, mipmap, fixed `GAUX4FORMAT`, and flip
parsers use it before constructing `ColorAttachmentKey`; other spellings warn/ignore at a
buffer-bearing grammar site. Exhaustive normalizer and per-parser integration tests were added.
The public index-only representation is unchanged, so this repair is outside §5.

### Interface/change-trigger disposition

The edits for candidates 002 and 003 intentionally modify the manifest-declared
`cross-phase-interfaces` region. The declared trigger fires: Phase 3 remains unverified and requires
a fresh whole-document verification round before closure or dependent consumption.

### Notes deferred

None. Round 54 admitted no notes, so there is nothing to apply or defer.
