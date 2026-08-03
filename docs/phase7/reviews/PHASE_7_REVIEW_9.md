## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, and the binding §5
regions of Phases 2–6. Only after settling those judgments did I read
`docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_8.md`, in order and last. Round 8 introduced the canonical
digest contract now under review, but no prior review settled either its path comparator or the
projection from Phase 3's `NormalizedPackPath`.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — Canonical digest ordering lacks a defined comparator

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1280`–`:1286`
- **Claim:** The new SHA-256-v1 encoding is not independently implementable as a canonical digest
  contract because “normalized-path order” does not define a total comparator.
- **Evidence:** The contract hashes every snapshot entry “in normalized-path order,” then encodes
  the path as UTF-8 bytes (`docs/phase7/v1/PHASE_7_DOC.md:1280`–`:1286`). Phase 3 likewise promises
  a snapshot in normalized-path order but defines only slash-normalized, root-relative validity
  (`docs/phase3/v1/PHASE_3_DOC.md:300`–`:306`). Neither contract states whether ordering compares
  Unicode code units, code points, UTF-8 bytes, or another representation. Those plausible,
  deterministic comparators can disagree for valid non-ASCII paths and therefore produce different
  canonical byte streams and digests. The exposed interface publishes both the digest and ordered
  path list (`docs/phase7/v1/PHASE_7_DOC.md:1202`–`:1209`), so selected golden vectors cannot supply
  the missing general rule.
- **Required correction:** Define the total normalized-path comparator explicitly, including its
  prefix rule. A suitable contract is unsigned lexicographic comparison of the exact UTF-8 path
  bytes used by SHA-256-v1, with the shorter sequence first when it is a prefix. Require snapshot,
  manifest-path, and digest traversal order to use that same comparator.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — this changes the canonical digest and ordered
  manifest contract inside the manifest-selected §5 region.

### candidate-002 — Canonical digest depends on an unspecified `NormalizedPackPath` projection

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1280`–`:1287`
- **Claim:** Phase 7 cannot deterministically construct its exposed manifest strings and digest
  bytes from Phase 3's binding source protocol without inventing a path projection.
- **Evidence:** Phase 3 exposes each internal entry path only as `NormalizedPackPath`
  (`docs/phase3/v1/PHASE_3_DOC.md:257`–`:263`) and describes its validity as slash-normalized and
  root-relative (`docs/phase3/v1/PHASE_3_DOC.md:303`–`:306`), but publishes no canonical String or
  byte accessor. Phase 7 requires the path's exact UTF-8 bytes in the digest and exposes the same
  paths as `List<String>` (`docs/phase7/v1/PHASE_7_DOC.md:1280`–`:1287`), while the binding
  dependency ledger confirms that Phase 7 supplies content through `NormalizedPackPath`
  (`docs/phase3/v1/PHASE_3_DOC.md:1125`). Validity constraints do not define a callable or exact
  textual projection, and implicit reliance on `Object.toString()` would not be a canonical
  cross-phase contract.
- **Required correction:** Flag a Phase 3 binding request for an explicit canonical slash-separated
  String or UTF-8 projection from `NormalizedPackPath`, then define `manifest().paths()` and the
  SHA-256-v1 path bytes as exactly that projection. Explicitly prohibit implicit `toString()` use;
  do not assume the requested dependency API exists before its governed fix-up and reverification.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the correction changes Phase 7's exposed
  manifest/digest semantics and its §5 dependency-request ledger.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. Apart from the two admitted path rules, the
SHA-256-v1 framing defines its domain separator, entry-kind tags, unsigned 64-bit big-endian
lengths, UTF-8 path bytes, exact file bytes, lowercase hexadecimal result, whole-corpus behavior,
limit rejection, manifest identity, and snapshot consistency. The internal-pack golden test and
implementation checklist consistently require the versioned digest and whole-corpus semantics.

The remaining Phase 2–6 dependency consumptions are either binding consumptions or explicitly
flagged requests. The conformance map continues to trace frame ordering, program dispatch, hook
needs 1–11, lifecycle triggers, engine flags, the internal/off distinction, and assigned OQ-3 and
OQ-4. No supplied candidate was refuted or cleared on independent re-derivation, and the prior
reviews contain no settled disposition that eliminates either Round 9 defect.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both admitted defects are correction-level omissions in the consumer-visible canonical path
contract; neither requires structural rebuilding. Both affect the manifest-declared cross-phase
interface region, so a fresh whole-document/interface verification round is required after fix-up
before Phase 7 can close.

The recent correction trend is 2, 1, 2, 1, 2 across Rounds 5–9. Round 9 reverses Round 8's numerical
improvement, corrections are not strictly decreasing, and literal convergence has not been reached.
The next required action is a scoped fix-up of this review, including its `## Resolutions` record
and Phase 7 addendum, followed by fresh verification of the changed interface and corrected whole
document. The Phase 3 projection request must remain explicitly gated until its owner completes the
governed dependency change.

## Resolutions

### candidate-001 — applied

Section 5.1 now defines one total comparator: unsigned lexicographic comparison of the exact UTF-8
path bytes, with the shorter byte sequence first when it is a prefix. Snapshot production,
manifest path order, and digest traversal are required to use that same comparator. This was
re-derived against Phase 3's binding contract: its normalized-order promise supplies no competing
total comparator, so the added rule closes rather than overrides the dependency contract.

### candidate-002 — applied with dependency gate

Section 5.4 now requests R7-9 from Phase 3: a stable canonical slash-separated String projection
for every valid `NormalizedPackPath`. Section 5.1 defines manifest strings as exactly that future
projection and digest path bytes as its exact UTF-8 encoding, expressly forbids implicit
`toString()` use, and gates internal-pack manifest/digest production until the owner applies and
reverifies the request. No ungranted Phase 3 API is assumed.

### Notes deferred

None. The adjudication admitted no notes.

The target's compact §0.13 records only the correction surface. The manifest-selected §5 interface
region changed, so Phase 7 requires a fresh verification round before closure.
