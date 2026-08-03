## 0. Method and reading order

I independently re-derived both Gate-surviving candidates from the whole Phase 7 target, the
manifest-selected governing design sections, authoritative RESEARCH material, the binding §5
regions of Phases 2–6, and the cited dependency evidence. Only after settling those judgments did I
read `docs/phase7/reviews/PHASE_7_REVIEW_1.md` through
`docs/phase7/reviews/PHASE_7_REVIEW_7.md`, in order and last. The Round 7 resolution introduced the
manifest and digest now under review; no prior review settled its missing digest semantics.

There were no reading-order deviations, no network use, no forbidden source use, and no agent
fan-out. This was the canonical engine's already-dispatched atomic adjudication role, so the
`verify-loop` instructions required completing only this role without invoking the loop or
delegating. No candidates were eliminated before adjudication, and Gate dropped none.

## 1. Findings

### candidate-001 — The exposed internal-pack manifest has no defined digest contract

- **Location:** `docs/phase7/v1/PHASE_7_DOC.md:1195`–`:1200`
- **Claim:** `InternalPackManifest` is not an exact, independently implementable cross-phase
  contract because its `ContentDigest` type and deterministic construction semantics are absent.
- **Evidence:** The binding signature exposes `InternalPackManifest(PackIdentity identity,
  ContentDigest digest, List<String> paths)` (`docs/phase7/v1/PHASE_7_DOC.md:1195`–`:1200`), but
  neither the target nor any binding dependency contract declares `ContentDigest`, its immutable
  representation, algorithm/version, canonical inputs, path/content encoding, or equality
  semantics. This is executable consumer surface rather than illustrative prose: the exposed
  contract promises a deterministic GPL content manifest and digest to bootstrap, Phase 3, and
  Phase 2 golden input (`docs/phase7/v1/PHASE_7_DOC.md:1335`–`:1338`), while the test matrix requires
  deterministic entry order/digest (`docs/phase7/v1/PHASE_7_DOC.md:1537`–`:1539`) and the
  implementation checklist requires a deterministic digest (`docs/phase7/v1/PHASE_7_DOC.md:1746`–`:1749`).
  Phase 3 publishes the bounded ordered snapshot protocol and assigns content supply to Phase 7,
  but supplies no digest semantics (`docs/phase3/v1/PHASE_3_DOC.md:1123`–`:1125`). The governing
  template requires exact semantics and named exposed data contracts
  (`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).
- **Required correction:** Define `ContentDigest` in §5.1 or explicitly reuse a fully specified
  published type. Specify its immutable representation, algorithm and version, canonical path
  ordering, path/content byte encoding, and equality semantics. Require `manifest().identity()` to
  equal `identity()`, and require manifest paths and digest to describe the same immutable shipped
  corpus exposed by every successful snapshot. Caller-supplied limits validate or reject that
  snapshot; they must not alter the corpus digest. Reflect these invariants in §4.7 and the golden
  test/checklist wording.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — the undefined type and semantics occur in the
  manifest-selected §5 cross-phase contract, so correction fires the fresh-review trigger.

## 2. Checked and clean

The finder-reported clean areas survived re-derivation. The Round 7 inheritance correction now
matches Phase 3's `InternalPackSource` identity and checked bounded-snapshot signatures. Its
provider/limit failure mapping remains consistent with Phase 3. Mixed hook rows preserve the active
Phase 7 hook while deferring only the later owner-phase augmentation, and the multi-valued hook
report retains the class set and deferred owner. The reviewed Phase 4–6 consumptions, frame flow,
program dispatch, hook catalog, OQ-3/OQ-4 treatment, and mandatory document structure yielded no
additional candidate-backed defect.

`candidate-002` is dropped on independent re-derivation. Phase 2 owns the schemas and runner-side
policy, while Phase 7 hosts the client capture agent (`docs/phase7/v1/PHASE_7_DOC.md:165`–`:167`).
Capture and shutdown are internal `mod.conformance` services called at finalization
(`docs/phase7/v1/PHASE_7_DOC.md:266`–`:270`), and H-CAPTURE-02 already fixes the Minecraft shutdown
entry, main-thread scheduling, post-artifact-commit ordering, and no-timeout-kill behavior
(`docs/phase7/v1/PHASE_7_DOC.md:928`–`:929`). The owning `CaptureAgent` atomically commits the image
and manifest, acknowledges the shot, and then schedules either the next shot or shutdown on the
client thread (`docs/phase7/v1/PHASE_7_DOC.md:1005`–`:1013`). No external Phase 2 caller needs a
separate shutdown operation or outcome algebra, so the proposed public interface would add an
unnecessary seam. This disposition is also consistent with the settled Round 6 adjudication.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The admitted digest defect is a local correction-level omission in a consumer-visible contract; it
does not require rebuilding the Phase 7 architecture. Because its repair changes the declared
cross-phase interface region, a fresh whole-document/interface verification round is required after
fix-up before Phase 7 can close.

The correction trend for Rounds 5–8 is 2, 1, 2, 1. Round 8 improves on Round 7, but corrections are
not strictly decreasing across the recent rounds and literal convergence has not been reached while
one interface correction remains. The next required action is a scoped fix-up of this review,
including its `## Resolutions` record and Phase 7 addendum, followed by fresh verification of the
changed interface and corrected whole document.

## Resolutions

### candidate-001 — resolved

Added the closed `ContentDigestAlgorithm.SHA_256_V1` and immutable `ContentDigest` value contract
to §5.1, including its exact lowercase-hex representation and record-value equality. The digest now
has a versioned SHA-256 canonical byte stream with a domain prefix, entry-kind tags, normalized
path order, UTF-8 paths, unsigned 64-bit big-endian lengths, and exact file bytes. The same contract
requires manifest identity equality, exact ordered manifest/snapshot path correspondence, and one
immutable shipped corpus across all successful snapshots; caller limits can only accept or reject
that whole corpus. §4.7, the internal-pack golden test, and implementation checklist now exercise
those invariants. The §5 interface changed, so a fresh verification round is required.

### Notes deferred

None.
