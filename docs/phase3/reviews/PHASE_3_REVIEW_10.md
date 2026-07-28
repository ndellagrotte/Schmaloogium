# Phase 3 verification review — round 10

## 0. Method and reading order

I independently re-derived both gated candidates from the complete Phase 3 target, the selected
Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, and the selected binding Phase 1 §5 contract. I searched the whole
target for an equivalent discovery operation/result contract and for governing or phase-local
support for profile inference precedence before deciding either disposition. Only after settling
those judgments did I read prior reviews 1 through 9, in order, including their resolutions.

There were no deviations from the supplied reading contract, no network use, and no agent fan-out.
The dispatched-role exception in the supplied `verify-loop` skill was followed: I did not invoke
the verification harness or start another session. I read no forbidden source. The Gate reported
no drops. `candidate-001` was eliminated at Refute and was not revived.

## 1. Findings

### candidate-002 — Filesystem selection depends on an unpublished discovery interface

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:175-178`, `:501-510`, and §5.1

**Claim:** Phase 7 and Phase 12 have no implementable publication contract through which to
discover filesystem packs and obtain the current opaque `PackCandidateId` required to construct a
valid `PackSelection.Filesystem`.

**Evidence:** The illustrative public entry point exposes only
`PackLoadResult load(PackLoadRequest request)` (`docs/phase3/v1/PHASE_3_DOC.md:175-178`), so it
accepts a selection but does not discover one. Detailed design assigns deterministic enumeration
of the sentinels and filesystem candidates to `PackDiscovery`
(`docs/phase3/v1/PHASE_3_DOC.md:501-510`), consistently with the governing Phase 3 objective and
scope assigning pack discovery and `shaderpacks/` enumeration to this phase
(`docs/design/v2.0-RC3/DESIGN.md:1325-1329`, `:1333-1337`). Yet §5 declares the complete
publication surface while listing only the atomic load entry point for Phase 7 and Phase 12
(`docs/phase3/v1/PHASE_3_DOC.md:861-866`). Its selection contract then requires
`Filesystem(PackCandidateId candidate)` to carry an opaque ID from the current deterministic
discovery result and rejects stale or unknown IDs (`docs/phase3/v1/PHASE_3_DOC.md:881-887`).
Neither §5 nor the public shape publishes a callable discovery operation or immutable result from
which either consumer can obtain that required ID.

**Severity:** correction. Add a minimal public Phase 3 discovery operation and immutable ordered
discovery-result/candidate contracts to the illustrative public shape and §5. Define how
`shaderpacksDirectory` is supplied, how consumers receive opaque candidate IDs and the
consumer-required candidate status/display information and diagnostics, the current-result and
stale-ID rules, and whether Phase 7, Phase 12, or both invoke discovery. Keep root-search, hashing,
and archive mechanics private unless consumers require them.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The Round 9 screen-column formula and boundary values are internally consistent across the
  conformance map, detailed design, §5 publication rule, and tests.
- `Off`, `Internal`, and `Filesystem` request validation; identity and engine-option defaults;
  internal snapshot limits; provider exception sanitization; and the closed load-failure surface
  are consistently specified apart from the missing discovery publication.
- The materialization, macro-contribution, internal-pack-source, schema-version, fingerprint,
  reload-retention, diagnostic, logging, debug-flag, licensing, module-seam, and conformance
  contracts have substantive support in the target or the selected Phase 1 binding contract.
- The Appendix F and Appendix A.3 maps, engine-flag ownership, macro families, discovery/include
  behavior, ID mappings, and the required reference pitfalls yielded no additional admitted
  finding.
- `candidate-003` is cleared on re-derivation. The governing Phase 3 specification expressly
  recognizes “constraint-count profile precedence” as valid Appendix F.3/F.4 machinery
  (`docs/design/v2.0-RC3/DESIGN.md:1386-1389`), and the target records its adoption in D-P3-4
  (`docs/phase3/v1/PHASE_3_DOC.md:1178`). Appendix F.4 supplies option-state inference and the
  `Custom` fallback (`docs/research/v1/RESEARCH.md:1471-1474`); deterministic source order is a
  compatible tie-break elaboration, not an unsupported contradiction. A conformance provenance
  cell need not claim that every elaborated implementation detail appears verbatim in the
  abbreviated Appendix text.
- Prior reviews do not settle `candidate-002`. They repaired the selected-pack load protocol and
  its request constituents, but none published the discovery operation/result needed to obtain a
  current filesystem candidate ID.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The surviving defect is a bounded consumer-interface fix-up rather than a structural miss. The
correction count falls from two in Round 9 to one, so the document is moving toward convergence,
but repeated interface omissions mean it has not reached literal PASS. The next required action is
a scoped fix-up resolving `candidate-002` and appending this review's `## Resolutions`. Because the
fix changes §5's declared complete publication surface, the interface change trigger fires and a
fresh verification round is required before Phase 3 may close.

## Resolutions

### candidate-002 — applied

Re-derived from the governing Phase 3 ownership of discovery, the existing deterministic
enumeration rules, the hostile-input boundary, and the binding requirement that dependent callers
receive executable named contracts. Sections 2.2 and 5.1 now publish `discover` with an immutable
ordered discovery result and candidate records. The request supplies `shaderpacksDirectory` and
the loader-neutral diagnostic reporter; candidates expose only opaque IDs, closed kind/status,
sanitized display names, and immutable diagnostics.

The contract binds filesystem IDs to the latest completed discovery generation for the same
normalized directory and front-end instance, making stale/unknown selection rejection executable.
Phase 12 invokes discovery for selection display; Phase 7 may invoke it for bootstrap/reload and
must obtain a current result before loading a persisted filesystem selection. Root search, hashes,
paths, and archive mechanics remain private.

The `cross-phase-interfaces` region changed, so the manifest's fresh-verification trigger fires.

### Notes deferred

None; the adjudicator admitted no notes.
