# Phase 4 — architecture review 40

**Date:** 2026-09-08. **Frozen document:** `docs/phase4/v1/PHASE_4_DOC.md` SHA-256 `244150ae0cc7afbbc28409b5e0badf9aaf32b3a5b88f2512df6fd3b9aa34f296`. **Verdict:** PASS-WITH-CORRECTIONS (0 blocking, 8 corrections, 1 note). **§5 impact:** none — §5 was untouched this wave and every consumed/exposed interface audited exists at current dependency bytes; all findings are citation-coordinate and verification-status hygiene in §§0.2/3.1/4.5/4.7/11.5.

---

## 1. Scope, identity and authority actually verified

- Frozen-hash gate recomputed before any reading and again after the audit:
  `sha256sum docs/phase4/v1/PHASE_4_DOC.md` → `244150ae0cc7afbbc28409b5e0badf9aaf32b3a5b88f2512df6fd3b9aa34f296`
  (exact match, 3125 lines; document bytes unchanged by this review).
- Read the complete Phase 4 document in ranges (lines 1–3125): §0 header and all forty-one
  addenda, §1 scope, §2 architecture/public shape, §3.1–§3.4 conformance maps, §4.1–§4.12
  detailed design, §5.1–§5.7 cross-phase interfaces, §6–§12.
- Read the governing design `docs/design/v2.0-RC3/DESIGN.md` Part I lines 1–1109 (§G0–§G12,
  including §G1.2 verify protocol, §G2 D-1..D-10 digest and G2.4 ladder, §G4, §G9 template,
  §G11/§G12 evidence rules) and the Phase 4 specification at lines 1471–1571.
- Read `docs/research/v1/RESEARCH.md` lines 11–107 (§0–§1) plus the cited spot ranges
  218–229, 330–365, 481–510, 1107–1147, 1228–1258, 1482–1498.
- Read dependency §0/§1/§5/§11 surfaces: `docs/phase1/v14/PHASE_1_DOC.md` (§0 header,
  ShaderService/StateService blocks 3284–3348, D-P1-57/§4.7.4 areas 3519–3604, §5.2 rows
  5482–5514, recorder/replay 4202–4276, §0.15 grant 1239–1300, §11 "To Phase 4" 6401–6406,
  closing §G1.3 status 6940–7023), `docs/phase3/v1/PHASE_3_DOC.md` (§0 header, §2.2
  declaration algebra 1399–1594, §4.10 framing/fingerprints 3204–3360, §5.1 rows 3368–3463,
  §5 resource/dimension prose 3776–3833, schema23 gates 4223–4241, §11 hand-offs 5095–5298),
  and consumer-side `docs/phase6/v1/PHASE_6_DOC.md` (§0 header status, §4.9 1187–1272,
  §4.10 1273–1308, §5 rows 1812–1816).
- Pin verification was performed against current bytes for all five repointed pin groups
  (§3.1 :952, :954, :958; §11.5 :2929–2934) plus a sampled spread of other dependency
  citations (§3.1 :957/:959, §4.5, §4.7, §4.11, §3.2).
- No build, test, gradle, GL, or network execution was performed; no source, dependency
  doc, review file, or `docs/build/**` artifact was modified. `reference-src/**`,
  `docs/**/chatlogs/`, root `*.txt`, and all other `PHASE_*_REVIEW_*.md` files were not read.

## 2. Scope and §5 audit

- **Doc gate:** both required configurations are shown explicitly (§4.2 G6 six-step table and
  full-superset nine-identity table); every Appendix A.1 row is mapped in §3.2 (complete slot
  walk from `<none>` through `final`, cross-checked against RESEARCH 1107–1145's 60-slot
  count); the barrier is fully specified as an interface (§2.2 signatures + §4.10 protocol);
  the backup-chain table is cross-validated against PD §3.1's resolver (§3.4 row 1, §4.6 with
  `[V:observed — Pintonium …ProgramFallbackResolver.java:39]`); version-counter invalidation
  is exposed in §5.1 (`PublishedRegistry.generation` row). Gate met.
- **Conformance-map audit:** §3.1 rows :939–:951, :953, :955, :957 spot-verified against the
  cited RESEARCH text at current coordinates — modern order (:939→336–355 ✓), 0…99 arrays
  (:940→340–353 ✓), G6 order (:941→220–224 ✓), `.csh` exclusion (:945→357–361 ✓), lifecycle
  (:946→483–488, :947→488–491 ✓), compile flow (:948→DESIGN 1511–1514 + RESEARCH 497–505 ✓),
  failure cleanup (:951→501–505 ✓), barrier duties (:953→505–507 ✓), shadow override
  (:955→506–507 ✓), shared-unit custom textures (:957→1484–1490 ✓). Rows :952/:958/:959 carry
  the mispointed dependency pins corrected below; their RESEARCH-side pins are accurate.
- **Interface honesty — consumed:** every P1 contract §5.2 consumes exists in current P1 §5
  bytes: `use(ProgramHandle)` (P1:3305), `useFixedFunction()` (P1:3306, D-P1-39), `locate`
  (P1:3321), `configureLegacyGeometry` + closed enums (P1:3297–3312, §5.2 row 5484),
  `linkedGeometryInput` (P1:3301, §5.2 row 5510), `initializeSamplerUnits`/`SamplerUnitAssignment`
  (P1:3302–3317, §5.2 row 5499), `lockAlphaBlend`/`AlphaBlendOverride`/`effectiveBlend`
  (P1:3439–3441, D-P1-57 at 3561–3589, §5.2 row 5488), recorder events (P1:4202–4208, 5496).
  Every P3 contract §5.3 consumes exists in current P3 §5 bytes: `CURRENT_SCHEMA_VERSION = 23`
  (P3:723), `MaterializedSource-v23` (P3:3328), `evaluateProgramStates(Optional<ProfileName>,
  DiagnosticReporter)` (P3:2991/3701), `GeometrySourceRequest.None/PreserveNative` and
  `GeometrySourceForm.None/CoreLayout/NativeLegacy` (P3 §5.1 row 3379; corroborated by P1 §5.2
  row 5514), `resources().programs()` lookup with `mipmappedAfterPass()`/`vertices()`
  (P3:3779–3804), and the DeclaredUniformCatalog merge duty (P3:5153–5156).
- **Interface honesty — exposed with receiver adoption:** §5.1 rows carry explicit consumer
  columns covering P5/P6/P7/P8/P10/P13 (plus P2). Receiver adoption verified on the P6 side:
  P6 §4.10 consumes the exact callback shapes and cites `PHASE_4_DOC.md:1646-1656` (content
  present). P3's §11 (P3:4238) records P4 as a required dated schema23 receiver. P1's §11
  "To Phase 4" (P1:6401–6406) records the fixed-function grant acceptance with invocation
  constraints matching §4.10/§5.2.
- **Scope discipline:** §1.2 assigns every Scope—out concern (FBO/textures P5, uniform
  values/upload P6, execution P7, shadow camera P8) plus the further adjacencies; no
  Scope—in item is dropped (stage registry, program registry, backup chains, compile/link,
  dual-form geometry, failure handling, barrier interface, reload invalidation, per-program
  state semantics all present with owning sections).
- **Template completeness:** all thirteen §G9 sections present and substantive; §6 maps the
  G2.4 ladder including rung 2a; §10 correctly states Phase 4 has no assigned OQ (spec:
  "OQs: —").
- **D-1..D-10:** §11.2 disposition table present; no contradiction found (D-4 central to §2/§4;
  D-9 compat/fixed-function terminals retained; D-6 seam kept — no Minecraft/Forge/LWJGL type
  enters `engine.registry`).
- **Pintonium/licensing compliance:** every PD-derived claim carries
  `[V:observed — Pintonium <path>]`; §G11.4-gated adoptions have recorded decisions
  (D-P4-4/5/6/8/10/11 with contract checks); pre-decided rejections honored (locations
  11–14 rejected as negative fixture, per-buffer blend and heuristic generated shaders
  rejected); no AGPL transformation-lib trace; §11.5's countInstances evidence uses published
  author docs and licensed Angelica/Iris checks with the license named.
- **Current-identity discipline:** schema23/`MaterializedSource-v23` current with older
  numeric receipts marked historical (§5.3, D-P4-41 superseding D-P4-38);
  `RegistryFingerprint/profile-selection-v3` current, superseding positional-route-v2 with
  D-P4-33's routing payload retained inside v3 (§4.11/§5.1 row :2142 consistent);
  `capture-plan/4` + `run-manifest/4` current with `/1`–`/3` aliases forbidden (§4.6, D-P4-35
  superseding D-P4-31's historical `/3`); `projectionVersion1` unchanged (§5.3/§5.6);
  ShadowHookHealth referenced without a stale version. One stale-as-current verification-state
  claim found (C7 below).
- **§5 impact of this wave:** none. The five repointed pins live in §3.1 and §11.5; §5 bytes
  were not touched, and the audit above re-verified §5 content against current P1/P3/P6 bytes.

## 3. Required corrections

**C1 — §3.1 row :952: P3 pins do not contain the cited "closed published algebra/materialization
contract".**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:952` (provenance cell citing
  `docs/phase3/v1/PHASE_3_DOC.md:3360,3787-3791,3823-3826`).
- Failure path: at current P3 bytes, :3360 is §4.10's publication-closure line ("No mutable
  builder… reachable from the published configuration"), :3787-3791 is §5 resource-map sparsity
  prose (no synthetic entry/densify; ResourceRequirements), and :3823-3826 is §5 dimension-mode
  prose (BASE/OVERRIDE/DISABLED). None publishes the declaration/materialization algebra the
  row's design element (merge `DeclaredUniformCatalog`s; reject unequal structural types)
  rests on, so a reader following the pin lands on unrelated contract text.
- Owner+receivers: Phase 4 owner; receivers P2/P6/P7 reading the conformance map.
- Minimal resolution: repoint to the binding anchors — P3 §5.1 source/materialization row at
  `docs/phase3/v1/PHASE_3_DOC.md:3379` ("complete final declaration catalog… No Translate
  alias, caller option state or source reparse") and the closed type algebra at
  `docs/phase3/v1/PHASE_3_DOC.md:1591-1594` (optionally P3:133-135 for the no-reopen/no-GL-
  activity statement).

**C2 — §3.1 row :958: P3 pins do not contain the cited "complete algebra" for same-unit
incompatible sampler declarations.**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:958` (citing
  `docs/phase3/v1/PHASE_3_DOC.md:3568-3576,3660-3665`).
- Failure path: at current bytes 3568-3576 is the custom-expression algebra
  (`CustomExpressionKind`/`CustomExpressionType`/`CustomExpressionDecl` — Phase 11 data) and
  3660-3665 is `CustomTextureSpec`'s `.png` suffix predicate and `TextureTarget` external
  spellings (P13 texture specs). Neither contains the sampler-declaration type algebra the
  row's SAMPLER_LAYOUT conflict model needs.
- Owner+receivers: Phase 4 owner; receivers P5/P6/P7 consuming the sampler-layout contract.
- Minimal resolution: repoint to the `DeclaredGlslType` sealed interface including
  `Sampler(SampledKind, TextureDimension, arrayed, shadow, multisample)` at
  `docs/phase3/v1/PHASE_3_DOC.md:1468-1484` (published through §5.1 row :3379).

**C3 — §11.5 (lines 2929-2934): P1:3532 does not publish the `shaders.useFixedFunction`
recorder/replay semantics.**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:2931-2932`.
- Failure path: at current P1 bytes line 3532 sits inside D-P1-60 sampler-normalization prose
  ("admitted sampler strategy it clears native sampler bindings on unoccupied units 0–15;");
  the only nearby mention (P1:3536/3548) is normalization, not recorder/replay. The distinct
  recorder/replay semantics are published at P1:4202-4208
  (`GLCall("shaders.useFixedFunction", List.of())`, `calledInOrder`/`neverCalled`/
  `noUseAfterDelete`) and P1:1265-1269 (§0.15).
- Owner+receivers: Phase 4 owner; receivers P2/P7 using the fixed-terminal replay assertion.
- Minimal resolution: repoint the second pin to `docs/phase1/v14/PHASE_1_DOC.md:4202-4208`
  (or additionally :1265-1269).

**C4 — §3.1 row :959: P6:993-998 is centerDepthSmooth math, not the fixed 0–15 map /
conditional shadow alias consumer content.**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:959` (citing
  `docs/phase6/v1/PHASE_6_DOC.md:993-998`).
- Failure path: at current P6 bytes 993-998 is §4.8's decay-constant formula. The row's
  subject — "Phase5 alone validates/resolves names; Phase6 consumer migration pending" — is
  published in P6 §4.9 at 1187-1226 ("Phase 5 is the sole policy owner… The conditional
  `shadow` rule is evaluated only by Phase 5…"). The row's RESEARCH pin (:1228-1255, App B.3
  fixed-unit table) is correct.
- Owner+receivers: Phase 4 owner; receivers P5/P6.
- Minimal resolution: repoint to `docs/phase6/v1/PHASE_6_DOC.md:1187-1226`.

**C5 — §4.7 (lines 1440-1441): P3:1403-1410 does not contain "Phase3 §4.10's length-prefixed
scalar/string/list framing".**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:1440-1441`.
- Failure path: at current bytes 1403-1410 declares `SourceKey`/`SourceDocument`/`IncludeEdge`
  (§2.2). The canonical framing (`encode`/`atom`/`seq` rules) is defined in P3 §4.10 at
  3236-3243, with the exact "length-prefixed canonical scalar/string/list framing" phrase at
  :3325 and the `MaterializedSource-v23` payload at :3328-3331.
- Owner+receivers: Phase 4 owner; any receiver hashing the sampler/policy digests.
- Minimal resolution: repoint to `docs/phase3/v1/PHASE_3_DOC.md:3236-3243` (optionally
  :3325-3331).

**C6 — §4.5 (line 1195): RESEARCH:1142 quote sits at :1143.**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:1195`.
- Failure path: current `docs/research/v1/RESEARCH.md` line 1142 is blank; the quoted
  "Count: 60 named shader/virtual slots, excluding the external `<none>` sentinel" begins at
  line 1143 (through 1145). Off-by-one only; the 60-row accounting itself is correct.
- Owner+receivers: Phase 4 owner.
- Minimal resolution: repoint to `docs/research/v1/RESEARCH.md:1143-1145`.

**C7 — §0.2 (lines 68-73) and §11.5 (lines 2929, 2933-2934): stale claim that Phase 1 is
"currently verified by round 20" / that Review 20 "closes the current dependency surface".**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:68-69, 72-73, 2929, 2933-2934`.
- Failure path: P1's own current §G1.3 status (§0.35, `docs/phase1/v14/PHASE_1_DOC.md:7015-
  7023`) states the document "is **not verified** and is not a valid dependency input until a
  fresh **whole-document** review returns literal PASS", after the post-Review-20 §0.21-§0.35
  amendments; Review 20's PASS verified the §0.19/§0.20-era surface only. Phase 4 itself
  consumes post-R20 grants and honestly labels them owner-granted/unverified in §5.2/§5.4
  (D-P1-48/R26, D-P1-57, D-P1-59, D-P1-60), so §0.2/§11.5's blanket "verified… current
  dependency gate/surface" wording is internally inconsistent as well as stale-as-current.
- Owner+receivers: Phase 4 owner; every P4 consumer relying on the stated dependency gate.
- Minimal resolution: reword to P1's actual posture (surface verified through Review 20
  historically; current bytes carry later §5 amendments and require fresh whole-document
  verification, with the specific P4-consumed grants already tracked as unverified in §5.2/
  §5.4). No interface change.

**C8 — §3.1 row :954: "verified downstream request" overstates P6's current state.**
- Sites: `docs/phase4/v1/PHASE_4_DOC.md:954`.
- Failure path: the pinned P6 text (1273-1301, content present) itself says the shape is
  "subject to §5.2's owner-review gate", and P6's current §G1.3 status (P6:139-142) says its
  bytes "remain **not verified** until a fresh review returns literal PASS". Calling the
  request "verified" at current coordinates is stale.
- Owner+receivers: Phase 4 owner; receivers P6/P7.
- Minimal resolution: reword the provenance cell to "adopted downstream request (P6 §5.2
  owner-review gate open, P6 currently unverified)" or equivalent; keep the pin.

## 4. Notes

- **N1 — §11.5 :2930 pin precision.** `docs/phase1/v14/PHASE_1_DOC.md:3290` lands on the
  code-fence line opening the `ShaderService` block; the published member
  `void useFixedFunction(); // … ([D-P1-39])` is at :3306 within that block. Content is
  present at the pinned block, so this is anchor looseness only; :3291 or :3306 would be
  exact.
- Reviewer-scope disclosure: citations to `PHASE_1_REVIEW_20.md`, `PHASE_1_REVIEW_15.md`,
  `PHASE_3_REVIEW_*`, `PHASE_4_REVIEW_*` and `PHASE_13_REVIEW_3.md` inside the document were
  not opened (reviewer hard boundary on other review files); no finding is raised against
  them, and C7 rests on P1's own in-document status, not on a review file.
- §0.35/§0.36-§0.41's recorded historical reads (P3/P6/P8 coordinate ranges) are provenance
  records of past sessions, not current claims, and were not graded as pins.
- The §0.2 treatment of P3 ("Review36 applied resolutions and changed §5 without a fresh
  PASS; provisional under this rebuild authorization") matches P3's current self-description
  (P3:5536-5540) and is honest.

## 5. Areas audited and found sound

- Stage/schedule model (§2.2/§4.1/§4.2): dual gbuffers occurrence, sparse 0…99 with holes,
  virtual-prelude membership invariants, pre-first ascending traversal, both required
  configurations, and the §8.1 pre/0/5/99 receiver trace.
- Classic catalog and backup chains (§3.2/§4.5/§4.6): complete App A.1 row coverage, 60-count
  reconciliation, memoized whole-binding inheritance, shadow-root fixedness, total
  `sourcePresent`/`ownBuild` classification with its exhaustive matrix and `run-manifest/4`
  token discipline.
- Compile/link state machine (§4.7): pre-GL planning, deterministic V/G/F ordering, declared-
  only attribute binding at 10/11/12 with capability gates, drain-configure-drain native
  transaction, linked-input agreement before READY, ownership ledger and cleanup.
- Dual-form geometry (§4.8) and conditional native-submission boundary (§4.9/D-P4-29): P3
  request/result forms match current P3 §5; P1 configure/linked-input/fullscreen contracts
  match current P1 §5.
- Barrier, selection credential, participants, activity token, lease lifetime (§4.10);
  publication/generation/fingerprint identity incl. profile-selection-v3 (§4.11); closed
  diagnostics and publication-failure kinds (§4.12).
- §5.1 export inventory with consumer columns; §5.2/§5.3 consumed-contract existence at
  current dependency bytes; §5.4 grants recorded as unverified with open gates; §5.6
  inspection enrichment; §5.7 pending-only P14 split.
- Failure ladder mapping (§6), threading/allocation posture (§7), test plan including the
  planned-not-executed boundary statements (§8), milestone staging (§9), decision log and
  receipts (§11), implementation checklist (§12).
- Legal posture (§0.4): LGPL-3.0 evidence handling, AGPL prohibition, OF-decompile
  behavioral-only, notices/modification rules; countInstances §11.5 evidence licensing.

## 6. Verdict

**PASS-WITH-CORRECTIONS** — 0 blocking, 8 corrections (C1-C8), 1 note (N1). All findings are
citation-coordinate and verification-status hygiene: the five repointed pin groups were
checked at current dependency bytes and three of them (rows :952, :958, and the §11.5 P1:3532
member) do not carry their cited content; three further sampled pins (row :959 P6, §4.7 P3
framing, §4.5 RESEARCH count) are likewise mispointed; and two provenance/status adjectives
(§0.2/§11.5 "verified by round 20"; row :954 "verified downstream request") are stale against
the dependencies' current self-declared states. No contract shape, grant, receiver obligation,
scope boundary, or §5 interface is affected; the underlying contracts all exist at the
dependency coordinates given in the corrections. No structural rebuild is required. No
build/test/GL execution was performed by this review.
