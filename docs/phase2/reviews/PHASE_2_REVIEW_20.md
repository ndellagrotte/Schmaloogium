# Schmaloogium — Phase 2: Conformance harness — Review Round 20

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. I read the
manifest-selected target, `docs/phase2/v1/PHASE_2_DOC.md`; the governing Part I, Phase 2 target
specification, document gate, and mandatory template in `docs/design/v1.1/DESIGN.md`; the contract
ground truth in `docs/research/v1/RESEARCH.md`; the selected binding dependency in
`docs/phase1/v14/PHASE_1_DOC.md`; and the supporting CI evidence under `.github/workflows/`. Only
after settling both candidates did I read `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
`PHASE_2_REVIEW_19.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic role did
not invoke the verification harness, start another Codex session, or use agent fan-out. Forbidden
sources were not read. The Gate reported no drops, and no candidate was eliminated before
adjudication.

The prior reviews were considered only after independent judgment. Round 18 corrected the prior
closing history and removed the ungranted diagnostic-domain consumption from binding §5. Round 19
then removed the remaining domain-type claim from §4.5.4. Neither resolution defines the scalar
domains and absence grammar of the now Phase-2-owned diagnostic wire record, and round 19's fix-up
did not update the closing history to include that round and §0.20.

## 1. Findings

### candidate-001 — Closing verification history omits the round-19 fix-up

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:2219–2221`
- **Claim:** The document's closing verification-history summary reflects the latest addendum and
  current review round.
- **Evidence:** Section 0.20 records that round 19 produced a fix-up removing the remaining
  ungranted diagnostic-domain-type claim (`docs/phase2/v1/PHASE_2_DOC.md:173–175`). The closing
  history instead ends with round 18 and §0.19, describing the document as pending a fresh review
  without recording round 19 or its §0.20 correction
  (`docs/phase2/v1/PHASE_2_DOC.md:2219–2221`). The two document-wide status statements therefore
  disagree about the latest reviewed and corrected surface.
- **Disposition:** Admitted. Extend the closing history to state that round 19 reviewed the §0.19
  surface, required the non-interface §0.20 correction, and that round 20 is the current fresh
  whole-document review. Do not change §5.
- **Severity:** correction
- **touches interface/change-trigger region: no**

### candidate-002 — Diagnostics wire records lack value domains and absence rules

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:847–854` (§4.5.4)
- **Claim:** The exposed run-manifest schema specifies diagnostics precisely enough for independent
  producers and consumers to agree on encoding and for T0 diagnostic severity checks to be
  deterministic.
- **Evidence:** The repeated-record grammar names
  `diagnostics{code,severity,channel,file,line}` but gives no diagnostics-specific scalar types,
  severity or channel domain, or representation for an unavailable file or line
  (`docs/phase2/v1/PHASE_2_DOC.md:847–854`). T0 then branches on the exact diagnostic values
  `FATAL` and `ERROR` (`docs/phase2/v1/PHASE_2_DOC.md:919–921`). Binding §5 exposes the
  `schmaloogium.run-manifest/1` schema by reference to §4.5.4 for production and consumption by
  Phases 3, 4, 5, and 7 (`docs/phase2/v1/PHASE_2_DOC.md:1528–1530`). Independent implementations
  can therefore choose incompatible values or absence encodings, including values that change the
  T0 result.
- **Disposition:** Admitted. In §4.5.4, define every diagnostic field's scalar type, a closed
  severity vocabulary containing the exact `FATAL` and `ERROR` values used by T0, a closed channel
  vocabulary or explicit extension rule, and unambiguous encodings for unavailable `file` and
  `line`. Keep this grammar Phase-2-owned and do not import the ungranted Phase 1 diagnostic types.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the ordered correction completes §4.5.4; §5
  already exposes that schema by reference and need not be edited, so the designated §5 region's
  change trigger does not fire.

## 2. Checked and clean

The conformance audit found no unmapped in-scope requirement: T0–T3, the Appendix G packs, §8
harness requirements, scene families, named runs, §9 exit criteria, the before-renderer subset,
and OQ-10 remain mapped to substantive design elements. The selected Phase 1 grants support the
declared module/seam, capability-profile serialization, recording, debug, logging, replay-aware
GL-error, and CI consumptions. R4B remains an honest unaccepted request rather than a claimed
diagnostic-domain-type grant. The corrected §4.5.4 table and repeated-record field list consistently
describe a Phase-2-owned five-field diagnostic record; the separate golden diagnostics projection
is explicitly scoped to its four named fields and is not inconsistent with the richer run manifest.

Both candidates survived independent re-derivation. Candidate-001 is not cured by §0.20 because
the designated closing status still contradicts it. Candidate-002 is not cured by generic manifest
scalar encoding: no diagnostics-specific domain or source-location absence rule appears elsewhere,
and prior rounds settled ownership rather than this wire grammar. Neither candidate requires an
edit to the designated §5 change-trigger region. The Gate dropped none, no candidate was cleared,
and no finding is created from candidate-free clean areas.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: no

Both defects are bounded fix-up work rather than structural misses requiring rebuild. The first is
verification-history bookkeeping; the second completes the Phase-2-owned wire grammar in §4.5.4.
Neither ordered correction modifies binding §5, so the designated interface change trigger is not
implicated.

Round 19 had one correction and this round admits two, including a newly exposed deterministic
wire-contract gap. The loop has not converged. The next required action is a scoped fix-up resolving
both findings and recording their resolutions in this review. Because the corrections are outside
§5, no interface-triggered review is owed; the normal fresh whole-document review is nevertheless
required before Phase 2 can close with literal PASS.

## Resolutions

### candidate-001 — Resolved

Updated the closing history to record round 19's review of the §0.19 surface, its required
non-interface §0.20 correction, and round 20 as the fresh whole-document review of that corrected
surface. The closing statement now records the §0.21 fix-up and continuing unverified status
(`docs/phase2/v1/PHASE_2_DOC.md:2229–2231`). Binding §5 was not changed.

### candidate-002 — Resolved

Completed the Phase-2-owned diagnostic record grammar in §4.5.4. All five fields are required;
`code`, `severity`, `channel`, and `file` are JSON strings; `code` is non-empty; severity is closed
to `INFO|WARN|ERROR|FATAL`; channel is closed to `CHAT|SHADER_GUI|LOG_ONLY`; empty `file` and zero
`line` encode unavailable values; and a positive line requires a non-empty file
(`docs/phase2/v1/PHASE_2_DOC.md:862–866`). This independently specifies the wire values used by T0
without claiming consumption of Phase 1's ungranted diagnostic domain types. Binding §5 already
exposes the schema by reference and was not changed.

Added the compact §0.21 fix-up record and advanced the header's last-revised pointer
(`docs/phase2/v1/PHASE_2_DOC.md:177–179`).

### Notes deferred

None; the adjudicator admitted no notes.
