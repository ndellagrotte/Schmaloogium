# Phase 5 Verification Review — Round 13

## 0. Method and reading order

I independently re-derived all three Gate-surviving candidates before consulting prior reviews.
The first pass read the whole target as selected by the manifest, the governing Phase 5
specification and document gate in `docs/design/v2.0-RC3/DESIGN.md`, the applicable ground-truth
contracts in `docs/research/v1/RESEARCH.md`, and the binding §5 regions of
`docs/phase1/v14/PHASE_1_DOC.md`, `docs/phase3/v1/PHASE_3_DOC.md`, and
`docs/phase4/v1/PHASE_4_DOC.md`. Supporting evidence was used only as evidence, never as contract.
I then read `docs/phase5/reviews/PHASE_5_REVIEW_1.md` through
`docs/phase5/reviews/PHASE_5_REVIEW_12.md`, in numeric order, last.

There were no reading-list deviations, no network use, and no agent fan-out. I did not read a
forbidden source. The Gate reported no drops, and there were no pre-adjudication eliminations.

## 1. Findings

### candidate-001 — Adjacent-ownership summary understates the Phase 1 contract changes

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:207`–`:208`

**Claim.** The adjacent-ownership summary contradicts the document's binding dependency-change
account by saying that Phase 5 requests two corrections and assumes neither. Section 5.5 actually
defines three required Phase 1 corrections, and the document assumes none of them.

**Evidence.** Section 1.2 says, “Phase 5 requests two narrow facade-contract corrections in §5.5
and assumes neither” (`docs/phase5/v1/PHASE_5_DOC.md:207`–`:208`). The binding interface summary
says, “The three required corrections are §5.5”
(`docs/phase5/v1/PHASE_5_DOC.md:1348`–`:1350`), and §5.5 introduces, then enumerates, “Three Phase
1 changes” (`docs/phase5/v1/PHASE_5_DOC.md:1379`–`:1408`). The upstream-change list independently
requires all three (`docs/phase5/v1/PHASE_5_DOC.md:1678`). No prior review settled the stale count.

**Severity:** correction.

**Required correction.** In §1.2, change “two” to “three” and “neither” to “none.”

**touches interface/change-trigger region: no**

### candidate-002 — Unit 15 lacks an exact `NOISE` overlay resolution rule

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1264`

**Claim.** The fixed texture-unit contract does not define the exact unit-15 backing-object or
missing-binding outcome even though Phase 5 owns that resolution and exposes a total, closed
overlay lookup.

**Evidence.** Phase 5 owns the table answering which texture object backs every fixed unit per
stage (`docs/phase5/v1/PHASE_5_DOC.md:193`–`:198`) and requires a deterministic row addressing unit
15 (`docs/phase5/v1/PHASE_5_DOC.md:583`–`:587`). The overlay model makes `NOISE` a closed key and
requires total `entry(StageId,TextureOverlayKey)` lookup returning exactly `Present(TextureHandle)`
or typed `Absent` (`docs/phase5/v1/PHASE_5_DOC.md:1228`–`:1234`). Yet the unit-15 row merely says
“Phase 13 `noisetex` slot” for both stage groups
(`docs/phase5/v1/PHASE_5_DOC.md:1259`–`:1266`); unlike the other overlay rows, it does not connect
the slot to `NOISE` or state how present and absent entries resolve. The §5 contract exposes the
total lookup, fixed table, missing outcome, and pass-coherent unit-to-object mapping together
(`docs/phase5/v1/PHASE_5_DOC.md:1331`). Phase 13's ownership of object creation does not discharge
Phase 5's assigned resolution responsibility. Round 1 established the typed overlay seam, and
Round 12 completed its publication and lifetime protocol, but neither resolution supplied this
unit-15 mapping.

**Severity:** correction.

**Required correction.** Replace the unit-15 placeholder with an exact overlay rule keyed by
`NOISE`, defining the `Present(TextureHandle)` backing and the outcome for every applicable
`TextureOverlayAbsence` value (or explicitly collapsing all three absence values to one stated
`MissingTextureBinding` outcome). Repeat the exact rule in §5.1 so the exposed fixed-table contract
is sufficient on its own.

**touches interface/change-trigger region: yes**

### candidate-003 — Candidate views expose generation before the publisher assigns it

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:323`–`:343`, `:395`–`:406`

**Claim.** The public candidate/view/publication contract has no implementable, single source of
truth for estate generation: a caller may inspect a generation-bearing view before the publisher
installs the publication generation.

**Evidence.** A caller-owned, unpublished `BufferEstateCandidate` exposes `view()`
(`docs/phase5/v1/PHASE_5_DOC.md:323`–`:325`), and pre-publication inspection is expressly permitted
(`docs/phase5/v1/PHASE_5_DOC.md:386`–`:394`). That view unconditionally exposes `long
generation()` and generation-sensitive operations (`docs/phase5/v1/PHASE_5_DOC.md:395`–`:406`).
Publication occurs later through a separate publisher and returns the published generation
(`docs/phase5/v1/PHASE_5_DOC.md:328`–`:343`). The lifecycle assigns a generation to each installed
ready or off publication (`docs/phase5/v1/PHASE_5_DOC.md:1214`–`:1216`), and the accepted-
publication procedure says the publisher installs the new generation
(`docs/phase5/v1/PHASE_5_DOC.md:1164`–`:1168`). The contract defines no sentinel, reservation,
delayed binding, view replacement, or prohibition on the generation-bearing view escaping before
acceptance. Fingerprint provenance does not resolve publication-order generation. No prior review
settled this gap.

**Severity:** correction.

**Required correction.** Define one publisher-owned generation protocol throughout the detailed
design and §5. Prefer either a distinct pre-publication inspection type followed by an accepted
generation-bearing view, or an equally explicit atomic reservation/binding protocol. State what a
pre-publication caller can observe, when the accepted generation becomes immutable, and require
snapshots and stale checks to use only the accepted generation.

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The governing Phase 5 specification, mandatory template, document gate, and dependency binding
regions were checked. The Round-12 main-depth-refresh and resize-registration changes are
internally consistent across detailed design, §5, and tests. The overlay publication ID,
registry-fingerprint comparison, and lease lifetime are also coherent apart from the admitted
unit-15 object-resolution omission.

The conformance map covers the in-scope App B.1, B.2, B.3, and B.4 requirements, including flip,
clear, depth/shadow, sizing, fixed-unit, format, pixel-transfer, fallback, and growth posture.
Consumed Phase 3 and Phase 4 contracts match their selected binding regions. Phase 5 correctly
flags, rather than assumes, the three Phase 1 facade changes.

No candidate was refuted or cleared on re-derivation. Prior reviews do not duplicate or settle
candidate-001 or candidate-003. Their earlier overlay findings establish adjacent machinery but
do not specify candidate-002's exact `NOISE` unit-15 resolution.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

There are no structural defects requiring a rebuild, but all three admitted corrections are
required. Two corrections alter the declared cross-phase interface/change-trigger region. The
round therefore continues the correction-bearing trend rather than demonstrating convergence.

Next required action: perform a scoped fix-up for this review, append its `## Resolutions`, and
update the Phase 5 document consistently. Because the fix-up must change §5, Phase 5 then owes a
fresh verification round before it can close.

## Resolutions

### candidate-001 — Applied

Re-derived against §5.5's three enumerated Phase 1 changes and its explicit statement that none is
assumed. Section 1.2 now says “three” and “none.” The correction changes no interface contract.

### candidate-002 — Applied

Re-derived from Phase 5's ownership of the fixed unit-to-object table and the already-closed overlay
lookup. Unit 15 now resolves `NOISE`: `Present(handle)` binds that handle, while each of
`NOT_CONFIGURED`, `NOT_APPLICABLE_TO_STAGE`, and `PUBLICATION_UNAVAILABLE` yields
`MissingTextureBinding`. The same total rule is stated in §5.1; no fallback object is invented.

### candidate-003 — Applied

Re-derived from publication order and the lifecycle's rule that installed publications consume
generations. `BufferEstateCandidate` now exposes only a metadata-only `BufferEstateInspection`,
with no generation or estate operations. Acceptance atomically transfers ownership, assigns the
next generation, and creates the sole accepted `BufferEstateView`; its generation is immutable.
Snapshots and stale checks may use only that accepted, publisher-assigned generation. The same
protocol is explicit in §5.1.

### Notes deferred

None.

### Fix-up status

All three admitted corrections were applied. The cross-phase interface changed for candidates 002
and 003, so Phase 5 owes a fresh verification round before it can close.
