# Phase 3 verification review — round 12

## 0. Method and reading order

I independently re-derived the gated candidate from the complete Phase 3 target, the selected
Part I, Phase 3 specification, document gate, and mandatory-template material in
`docs/design/v2.0-RC3/DESIGN.md`, the relevant contract ground truth in
`docs/research/v1/RESEARCH.md`, the selected binding Phase 1 §5 contract, and the permitted
supporting evidence. I searched the whole target and selected authorities for a host
`shaderpacksDirectory` identity or normalization rule and distinguished it from normalization of
entries inside a selected pack. Only after settling the candidate's disposition did I read prior
reviews 1 through 11, in order, including their resolutions.

There were no deviations from the supplied reading contract, no network use, and no agent
fan-out. The dispatched-role exception in the supplied `verify-loop` skill was followed: I did not
invoke the verification harness or start another session. I read no forbidden source. The Gate
reported no drops, and no candidate was eliminated before adjudication.

## 1. Findings

### candidate-001 — Discovery-generation identity depends on an undefined directory normalization rule

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:907-924`

**Claim:** The discovery/load interface does not determine when two caller-supplied
`shaderpacksDirectory` values share a discovery generation.

**Evidence:** The public operations independently accept `Path shaderpacksDirectory` in both
`PackDiscoveryRequest` and `PackLoadRequest`
(`docs/phase3/v1/PHASE_3_DOC.md:184-195`, `:223-225`). Section 5 then makes a filesystem candidate
ID valid only for the latest completed discovery result for the “same normalized
`shaderpacksDirectory`” and requires a filesystem selection to come from the current result for
the load request's directory (`docs/phase3/v1/PHASE_3_DOC.md:913-924`). The named generation test
repeats that same normalized-directory boundary without defining it
(`docs/phase3/v1/PHASE_3_DOC.md:1046-1052`).

The document does define slash and Unicode normalization, containment, and symlink rejection for
entries *inside* folders and archives (`docs/phase3/v1/PHASE_3_DOC.md:530-540`), but it never
defines an identity procedure for the host directory supplied by callers. Consequently,
relative/absolute aliases, redundant path segments, symlink aliases, platform case behavior, and
normalization or resolution failures have no determinate effect on generation supersession or
`INVALID_SELECTION`. The governing design assigns pack discovery to Phase 3
(`docs/design/v2.0-RC3/DESIGN.md:1325-1337`), so no dependency or downstream owner can supply the
missing public behavior.

**Severity:** correction. Define in §5 one deterministic `shaderpacksDirectory` identity
procedure shared by `discover` and `load`, including its treatment of syntactic aliases, absolute
paths, symlinks, platform case behavior, nonexistent or unreadable directories, and resolution
failures. Extend the generation test with equivalent aliases and intentionally distinct
directories.

**Touches interface/change-trigger region:** yes.

## 2. Checked and clean

- The Round 11 additions consistently name tests and checklist hooks for same-directory
  supersession, cross-directory independence, and stale or unknown IDs returning
  `INVALID_SELECTION`; the defect is the undefined directory-equivalence predicate those tests
  rely on.
- Discovery otherwise exposes an immutable ordered result with opaque IDs, closed candidate
  kinds/statuses, sanitized display data, and attributed diagnostics without leaking roots,
  paths, hashes, or archive leases.
- The selected Phase 1 contracts are consumed honestly, and the missing jcpp build allowance
  remains an explicit upstream request rather than an assumed dependency contract.
- The conformance finder found no missing Appendix F or Appendix A.3 disposition, and the
  new-surface finder found no separate defect in the Round 11 edits.
- No candidate was refuted or cleared on re-derivation. Prior reviews do not settle
  `candidate-001`: Round 10 introduced the “same normalized directory” rule, and Round 11 added
  tests against it, but neither defined host-directory identity. Their resolutions address
  discovery publication and coverage, not this equivalence boundary.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The surviving issue is a bounded public-contract correction, not a structural miss requiring a
rebuild. The correction count remains one across Rounds 10–12: the earlier publication and test
omissions were repaired, but those repairs exposed a narrower undefined identity predicate.
Progress is therefore incremental but literal convergence has not been reached. The next required
action is a scoped fix-up defining the shared directory-identity rule and its alias/failure tests,
with a `## Resolutions` section appended to this review. Because §5 must change, the interface
change trigger fires and a fresh verification round is required before Phase 3 may close.

## Resolutions

### candidate-001 — corrected

Section 5 now gives `discover` and `load` one host-directory identity procedure:
absolute syntactic normalization followed by real-path resolution, readability/directory checks,
and equality under the returned filesystem provider. This makes relative, absolute,
redundant-segment, and resolved-symlink aliases share a generation without inventing a
cross-platform case-folding policy. It also assigns nonexistent, non-directory, unreadable, and
resolution/security failures to the existing invalid-request outcomes for each operation.

The headless matrix now requires alias supersession, distinct-real-directory independence,
provider case behavior, and every named failure class for both operations; the implementation
checklist names that coverage. The compact §0.15 entry records only the contract change.

The §5 interface region changed, so the manifest trigger requires a fresh verification round
before Phase 3 can close.

### Notes deferred

None.
