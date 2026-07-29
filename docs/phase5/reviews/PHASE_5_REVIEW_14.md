# Phase 5 Verification Review — Round 14

## 0. Method and reading order

I independently re-derived the Gate-surviving candidate before consulting prior reviews. The
first pass read the whole target selected by the manifest, the governing Part I, Phase 5
specification, document gate, and mandatory template in
`docs/design/v2.0-RC3/DESIGN.md`, the applicable ground-truth contracts in
`docs/research/v1/RESEARCH.md`, and the binding §5 regions of
`docs/phase1/v14/PHASE_1_DOC.md`, `docs/phase3/v1/PHASE_3_DOC.md`, and
`docs/phase4/v1/PHASE_4_DOC.md`. Supporting material was treated only as evidence, never as
contract. After fixing the independent disposition, I read
`docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_13.md` last.

I used no network access and no agent fan-out. I did not invoke the verification loop, run
`scripts/verify`, or start another Codex session. I did not read a forbidden source. In
particular, the supplied `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` supporting
source was not opened because the resolved forbidden-source rule bars `*.txt`; it was not needed
to adjudicate the candidate. There were no Gate drops and no candidates eliminated before
adjudication.

## 1. Findings

### candidate-001 — Published sizing, inventory, and resize-notice contracts lack implementable field definitions

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:334`–`:338`,
`docs/phase5/v1/PHASE_5_DOC.md:1138`–`:1152`,
`docs/phase5/v1/PHASE_5_DOC.md:1169`–`:1170`, and
`docs/phase5/v1/PHASE_5_DOC.md:1344`–`:1353`

**Claim.** Phase 5 publishes `BufferSizing`, `BufferInventory`, and `BufferResizeNotice` as
consumer-visible data contracts but does not define their public components or accessors.
Consequently Phases 6, 7, 8, 13, and 14 cannot implement metadata inspection, sizing equality,
or resize handling without inventing Phase-5-owned interface details. The resize notice also
leaves `reason` without a named closed type or value semantics.

**Evidence.** `BufferEstateInspection` exposes only `registryFingerprint()`, `sizing()`, and
`inventory()` (`docs/phase5/v1/PHASE_5_DOC.md:334`–`:338`), but the document contains no record
or accessor contract for either returned metadata type. The sizing design computes exact main
and shadow extents and says that `superSamplingLevel` is retained in `BufferSizing`
(`docs/phase5/v1/PHASE_5_DOC.md:1138`–`:1152`), without defining how a consumer obtains those
values. The resize value is specified only positionally as
`BufferResizeNotice(oldSizing,newSizing,newGeneration,reason)`
(`docs/phase5/v1/PHASE_5_DOC.md:1169`–`:1170`), with no type or closed vocabulary for `reason`.
Binding §5 promises that the sizing and inventory contracts expose exact extents, counts, and
formats to five dependent phases and separately publishes the notice contract to Phases 13 and
14 (`docs/phase5/v1/PHASE_5_DOC.md:1344`–`:1353`). The mandatory template requires detailed data
models with exact semantics and named cross-phase data contracts
(`docs/design/v2.0-RC3/DESIGN.md:809`–`:813`).

**Required correction.** Define minimal immutable public records or complete accessor contracts
for `BufferSizing`, `BufferInventory`, and `BufferResizeNotice` in the detailed design and mirror
their exact consumer-visible content in §5. Include main and shadow extents, supersampling level,
logical buffer identities/counts and resolved formats, sizing equality semantics, notice
old/new sizing and new generation, and a closed resize-reason type tied to the documented change
classifications. Preserve the settled rule that candidate inspection exposes metadata only and
does not expose a generation or estate operations.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The Round-13 candidate/publication
split is coherent: pre-publication inspection remains metadata-only, while acceptance alone
creates the immutable generation-bearing estate view. Unit 15 consistently resolves `NOISE`
`Present(handle)` to that handle and all three closed absence values to
`MissingTextureBinding`. The generation, publication, resize-registration, stale-estate,
overlay-lifetime, main-depth refresh, clear, flip, shadow, and fixed-unit contracts are otherwise
internally consistent.

The governing Phase 5 scope and document gate, all thirteen mandatory sections, the selected
Phase 1/3/4 binding regions, the conformance map, and the exposed/consumed contract tables were
checked. No additional candidate may be created by adjudication. Candidate-001 does not clear:
internal sizing formulas and inventory derivation do not substitute for an accessible public
data shape, and downstream ownership does not authorize dependents to invent Phase 5's published
types.

Prior reviews do not settle this defect. Round 1 established resize-notice delivery, later rounds
completed registration, acknowledgement, and failure semantics, and Round 13 narrowed
pre-publication access to `BufferEstateInspection`. None defines the consumer-visible components
of `BufferSizing` or `BufferInventory`, or the notice's closed reason type. The finding therefore
does not reopen the settled prohibition on pre-publication generation or estate operations.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

One localized interface correction is admitted. It does not require rebuilding the Phase 5
architecture, so `FAIL` is not warranted. Round 13 had three corrections and Round 14 has one,
so the count decreased, but literal convergence has not been reached.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction must define and update the declared §5 cross-phase data
contracts, the interface change trigger applies and Phase 5 owes a fresh verification round
before it can close.

## Resolutions

### candidate-001 — resolved

Added minimal immutable public value contracts in §2 for `BufferSizing`, `BufferInventory`,
`BufferInventoryEntry`, `ResolvedBufferFormat`, `BufferResizeNotice`, and the closed
`BufferResizeReason`. Sizing now exposes exact main and optional shadow extents plus the
supersampling level and uses structural record equality. Inventory is an immutable,
domain/index-ordered list with one row per logical texture, an exact per-domain count, and each
row's final fallback-resolved color or depth format; it exposes no GL handle.

The notice now exposes old/new sizing, new generation, and a closed reason mapped to the
rebuild-causing classifications in §4.11.2. Declaration order supplies deterministic priority when
multiple changes coincide. §5.1 mirrors these exact consumer-visible fields and semantics. The
candidate inspection boundary remains metadata-only: no pre-publication generation, snapshot, or
estate operation was added.

### Notes deferred

None.
