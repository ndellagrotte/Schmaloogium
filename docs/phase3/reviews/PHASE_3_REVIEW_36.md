# Phase 3 verification review — round 36

## 0. Method and reading order

I first re-derived every surviving candidate from the target's cited and adjacent contract ranges,
using a whole-target search for equivalent coverage, then checked the manifest-selected v3
`DESIGN.md` Part I, Phase 3 target specification, doc gate, and mandatory template, the relevant
`RESEARCH.md` contract rows, and the Phase 1 binding §5 contract. The decisive source set was:

1. `docs/phase3/v1/PHASE_3_DOC.md`, especially §§2–5, the Appendix F/A.3 maps, §8, and the
   implementation/decision hand-offs;
2. `docs/design/v3/DESIGN.md`, especially the Phase 3 scope and doc gate, the Phase 2 ownership
   boundary, and G1.3's §5 fresh-review trigger;
3. `docs/research/v1/RESEARCH.md`, especially Appendix A.3, Appendix B.1, §3.2, and Appendix
   F.5; and
4. `docs/phase1/v14/PHASE_1_DOC.md` §5, including its explicit incorporated-declaration and
   consumer-assignment rules.

I settled each candidate's interpretation, severity, and interface classification before reading
any prior review. I then read the discovered Phase 3 reviews 1–35 as the final historical step,
including their resolutions. The permitted Pintonium and Oculus reports were not needed to decide
these candidates. There were no deviations from the assigned sources, no network use, and no
agent fan-out. I did not invoke a verification harness, start another session, or read a forbidden
source.

The Gate drops were not revived: `candidate-001`, `candidate-002`, and `candidate-005` had
unverifiable citations, and `candidate-007` was dropped at Refute for lack of a live severity. No
Gate-surviving candidate was dropped on independent re-derivation.

## 1. Findings

### candidate-003 — First-created attachment baseline erases the contract's colortex0/colortex1 clear colors

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:1093-1123, 1151-1157, 1491-1514`; authoritative
clear behavior at `docs/research/v1/RESEARCH.md:1200-1208`.

**Claim:** An attachment with no explicit clear-color override must preserve the per-buffer clear
behavior required by the pack contract.

**Evidence:**

- The target publishes `ColorAttachmentRequirement(ColorInternalFormat format, boolean clear,
  Vec4f clearColor)` as part of `ResourceRequirements`, explicitly assigns the sizing/format/clear
  subset to Phase 5, and says that absence is represented only by the published typed baselines or
  the few named empty collections/Optionals (`PHASE_3_DOC.md:1103-1105, 1121-1123,
  1151-1157`).
- The sole binding §5 baseline creates every first-created attachment with `RGBA`, clearing enabled,
  and `Vec4f(0,0,0,0)` (`PHASE_3_DOC.md:1502-1509`). It provides neither an absent/override marker
  nor an index-aware default, and §5 expressly says that the producer sections do not add
  consumer-visible defaults or interpretation (`PHASE_3_DOC.md:1511-1514`).
- The authoritative buffer table requires colortex0 to clear to fog color, colortex1 to clear to
  solid white, and only colortex2–7 to use transparent black (`RESEARCH.md:1200-1208`). Thus a
  Phase 5 consumer following the published baseline cannot recover the required index-specific
  behavior, and it cannot distinguish an omitted clear-color directive from an explicit transparent
  black override.

**Severity:** correction. The target needs an explicit clear-color override/absence policy, or an
index-aware binding default that lets Phase 5 supply runtime fog color for colortex0, solid white
for colortex1, and transparent black for later buffers while retaining explicit overrides and the
required alpha semantics.

**Touches interface/change-trigger region:** yes. This changes the published
`ResourceRequirements` value or its consumer interpretation in the §5 region and therefore fires
the fresh-review trigger.

### candidate-004 — Phase 3 consumes the conformance extension point without honoring its Phase 1 consumer assignment

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:47-51, 1577-1589, 1791-1798, 1846, 1937-1939,
2042-2043`; dependency binding at `docs/phase1/v14/PHASE_1_DOC.md:4264-4276`.

**Claim:** A manifest-selected dependency consumer assignment must be honored, or a widening and
its module, API, and ownership must be explicitly requested before Phase 3 consumes the extension
point.

**Evidence:**

- The target calls Phase 1 its sole dependency and says it consumes the dependency's `:conformance`
  extension point (`PHASE_3_DOC.md:47-51`), then lists that extension point in §5.2 as a Phase 3
  consumed contract for headless golden/materialization runs (`PHASE_3_DOC.md:1577-1589`).
- The target schedules P3-C20 as a Phase 3 “headless manifests, fuzz fixtures, and seven-pack
  front-end harness adapter” component (`PHASE_3_DOC.md:1840-1847`) and its checklist says to
  implement that adapter (`PHASE_3_DOC.md:2042-2043`). Those statements are not accompanied by a
  Phase 1 fix-up naming Phase 3 as an additional `:conformance` consumer or defining the adapter's
  shared module/API ownership.
- The target itself describes the material as being received by “Phase 2's runnable-before-renderer
  harness” (`PHASE_3_DOC.md:1791-1798`) and its decision disposition names the Phase 2 adapter
  (`PHASE_3_DOC.md:1937-1939`). The binding Phase 1 table nevertheless assigns the CI job/step
  layout and `:conformance` extension point to **2**, not Phase 3 (`PHASE_1_DOC.md:4264-4276`).
  The governing module definition likewise places the headless golden-run harness under Phase 2.

**Severity:** correction. Align §5.2, P3-C20, the checklist, and the hand-off by removing
`:conformance` from Phase 3's consumed dependency surface and stating that Phase 3 emits fixtures,
manifests, and a specified Phase 2 hand-off. If the implementation truly must consume or modify
the extension slot, first request a Phase 1/Phase 2 contract fix-up naming Phase 3 and defining the
exact API, module, and ownership.

**Touches interface/change-trigger region:** yes. The mismatch is in the target's consumed Phase 1
contract table and changes the declared cross-phase dependency surface.

### candidate-006 — Public consumer declarations outside §5 are not covered by the declared fresh-review trigger

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:326-405, 519-605, 1398-1418, 1511-1514,
1591-1616`; governing trigger at `docs/design/v3/DESIGN.md:348-359`.

**Claim:** The manifest-selected cross-phase interface region must bind or explicitly incorporate
every detailed consumer-facing declaration, so a public shape or semantic edit cannot bypass the
fresh verification required for an interface change.

**Evidence:**

- The target's §2.2 declares the public `PackFrontEnd`, request/result records, `InternalPackSource`,
  `PackConfiguration`, and related public types (`PHASE_3_DOC.md:326-405`). It separately declares
  `SourceMaterializer`, `MaterializationResult`, and the materialization value graph outside §5
  (`PHASE_3_DOC.md:519-605`).
- Section 5 calls its abbreviated rows the complete publication surface and names
  `PackFrontEnd.discover/load`, `SourceMaterializer`, and their surrounding types, but does not
  reproduce or explicitly incorporate their exact method signatures, variants, fields, defaults,
  absence rules, and other consumer-visible semantics (`PHASE_3_DOC.md:1398-1418`).
- The target's only explicit “sole binding consumer contract” and same-edit rule is limited to the
  `ProgramStateModel` and `ResourceRequirements` aggregates and the named producer sections
  (`PHASE_3_DOC.md:1511-1514`). Its schema rule separately addresses the
  `PackConfiguration` record-component set and meaning, not changes to the other public APIs
  (`PHASE_3_DOC.md:1591-1616`). A later edit to `PackFrontEnd`, `SourceMaterializer`, a referenced
  request/result type, or another Phase 3-owned public type could therefore leave the monitored §5
  text unchanged.
- The dependency's own binding contract demonstrates the required pattern: detailed public
  declarations and semantics incorporated from another section remain binding, and every such edit
  must update its corresponding §5 row, including an explicit “unchanged” entry, so the declared
  interface region changes (`PHASE_1_DOC.md:4187-4192`). G1.3 makes a §5 change the fresh-review
  trigger (`DESIGN.md:348-359`).

**Severity:** correction. Move or duplicate the canonical consumer declarations into §5, or add an
explicit incorporation statement and corresponding binding row for the public entry points,
request/result types, source/materialization types, and macro-contributor types. Require every
consumer-visible shape, enum, method, default, absence, ordering, or semantic edit to update that
row and trigger fresh verification, while keeping `PackConfiguration` schema/version changes under
the separate schema rule.

**Touches interface/change-trigger region:** yes. The defect is specifically a gap in the
monitored binding region and its fresh-review coverage.

### candidate-008 — Texture property-key filter/wrap suffix behavior has no dedicated conformance mapping

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:737-740, 1415, 1443-1469, 1769-1773`; governing
requirement at `docs/design/v3/DESIGN.md:1436-1439`; grammar at
`docs/research/v1/RESEARCH.md:1482-1490`.

**Claim:** The Phase 3 conformance map must explicitly cover the
`texture.<stage>.<sampler>` filter/wrap-suffix case and distinguish it from `.mcmeta` sidecar
handling before publishing the sampler key.

**Evidence:**

- The governing Phase 3 specification explicitly identifies “`texture.<stage>.<sampler>`
  filter/wrap suffixes stripped and ignored” as a REV1 gap that Phase 3 must own as a conformance
  row (`DESIGN.md:1436-1439`).
- The target's F.5 map covers a numeric `[.0-9]` duplicate discriminator, pack/Minecraft/raw
  source variants, and the adjacent `.mcmeta` blur/clamp sidecar row, but contains no property-key
  filter/wrap-suffix row or named test (`PHASE_3_DOC.md:737-740`). The test gate requires every
  Phase-3-owned parser/model row to have an assertion (`PHASE_3_DOC.md:1769-1773`).
- The authoritative F.5 grammar separately describes the numeric discriminator and says that
  `.mcmeta` sidecars set blur/clamp (`RESEARCH.md:1482-1490`). Retaining a `TextureSidecarRef` is
  therefore not a disposition for a suffix in the property key.
- The published `TextureBindingKey` says `sampler` is the exact non-empty key segment and defines
  only the numeric discriminator (`PHASE_3_DOC.md:1443-1447`). No strip/ignore or preservation rule
  makes the resulting Phase 13-facing sampler value determinate.

**Severity:** correction. Add a dedicated F.5 conformance row and named tests for the filter/wrap
property-key forms, state the exact strip/ignore disposition, and make the published
`TextureBindingKey.sampler` semantics agree. Keep the rule separate from `.mcmeta`
`TextureSidecarRef` retention and Phase 13 sidecar loading.

**Touches interface/change-trigger region:** yes. `TextureBindingKey` and its sampler meaning are
published to Phase 13, so resolving this omission changes a consumer-visible interface semantics.

### candidate-009 — The gdepth mapping weakens the required RGBA32F upgrade to default-only

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:775, 1079-1084, 1093-1105, 1491-1514`; authoritative
mapping at `docs/research/v1/RESEARCH.md:1155-1167` and the governing scope at
`docs/design/v3/DESIGN.md:1417-1429`.

**Claim:** Declaring the `gdepth` uniform must produce the authoritative colortex1 RGBA32F
upgrade, without an unsupported silent default-only qualification.

**Evidence:**

- RESEARCH Appendix A.3 states that `uniform … gdepth` upgrades buffer 1 from RGBA to RGBA32F
  (`RESEARCH.md:1155-1167`) without limiting the trigger to cases where no explicit colortex1
  format has already been seen. The governing Phase 3 scope likewise calls it the `gdepth` →
  RGBA32F upgrade trigger (`DESIGN.md:1417-1429`).
- The target's sole mapping instead says “format request for colortex1 `RGBA32F` **if still
  default RGBA**” and names only `directive_gdepthUpgradeOnlyDefault`
  (`PHASE_3_DOC.md:775`). A prior explicit non-RGBA format can therefore make a later `gdepth`
  declaration a no-op, with no target rule specifying whether that is a conflict, which occurrence
  wins, or what requirement is published.
- The target publishes attachment formats in the Phase-5-consumed `ResourceRequirements` and says
  format conflicts retain diagnostics plus a deterministic last-active occurrence
  (`PHASE_3_DOC.md:1083-1084, 1103-1105, 1121-1123`). That generic statement does not define how
  an implicit `gdepth` upgrade participates in the conflict or justify suppressing it. The §5
  contract is the sole consumer interpretation and requires published-value changes to be stated
  there (`PHASE_3_DOC.md:1511-1514`).

**Severity:** correction. Remove the silent default-only suppression or explicitly define the
contract-required precedence and diagnostic for `gdepth` versus an explicit colortex1 format. Add
conformance cases combining `gdepth` with prior and subsequent non-RGBA format directives and bind
the resulting `ResourceRequirements` meaning.

**Touches interface/change-trigger region:** yes. The disputed format is a published attachment
requirement consumed by Phase 5, and changing its meaning requires the §5 fresh-review trigger.

### candidate-010 — Absent DRAWBUFFERS routing is not given its all-used-buffers meaning

**Location:** `docs/phase3/v1/PHASE_3_DOC.md:799, 1076-1084, 1142-1150, 1491-1514`; authoritative
routing at `docs/research/v1/RESEARCH.md:230-238, 1187-1192`.

**Claim:** The routing interface must distinguish an absent routing directive, which writes all
used buffers, from an explicit `N` route, which writes none.

**Evidence:**

- RESEARCH explicitly says that absent `DRAWBUFFERS` means the program writes all used buffers,
  while an explicit digit list routes selected buffers and `N` means none (`RESEARCH.md:230-238`).
  The directive table separately identifies explicit `DRAWBUFFERS:XXXX` routing and `N` as no output
  (`RESEARCH.md:1187-1192`).
- The target maps only the explicit syntax to an ordered routing list and describes `DrawRouting` as
  the sequence from a winning `DRAWBUFFERS`/`RENDERTARGETS` occurrence (`PHASE_3_DOC.md:799,
  1142-1145`). It has no no-directive mapping, no all-used state, and no rule distinguishing the
  empty baseline from explicit no output.
- Section 5 makes a first-created program's routing and sets empty, but never assigns that empty
  value the authoritative all-used meaning or explains how an explicit `N` is represented
  (`PHASE_3_DOC.md:1507-1509`). It also declares §5 the sole consumer contract and forbids consumers
  from inferring absence or semantics from producer sections (`PHASE_3_DOC.md:1511-1514`). An
  explicit `[NONE]` sequence could be distinct from an absent/empty state, but the target does not
  specify that distinction or test it.

**Severity:** correction. Define no active `DRAWBUFFERS` as ALL_USED/all used buffers and explicit
`DRAWBUFFERS:N` as no output, using a tagged absent state or an expressly documented empty
representation as appropriate. Add separate no-directive and explicit-N tests and bind the
consumer-visible result.

**Touches interface/change-trigger region:** yes. `DrawRouting` is part of the published
`ResourceRequirements` program projection consumed downstream, so its missing absence meaning is a
§5 interface correction.

## 2. Checked and clean

- The finder-reported new-surface checks remain clean for the repeated schema version 3, the
  world −128…128 dimension scan, include depth 10, the OQ-7-shaped macro slot,
  source-ordered custom-expression publication/fingerprinting, the ordinary versus forced-11300
  ID-map split, and the declared-uniform materialization fingerprint.
- The Phase 1 package/module seam, `GLCapabilityProfile`, logging channels, loader-neutral
  diagnostics, debug-flag namespace, SPDX/third-party notice mechanism, and the other named
  dependency uses match the selected Phase 1 binding. The conformance ownership mismatch admitted
  above is limited to `:conformance`; it does not invalidate those otherwise supported contracts.
- The conformance examination found explicit coverage for the Appendix F.1 ownership map,
  F.2–F.4 and F.6–F.8 families, most F.5 source forms and stage expansion, the remaining Appendix
  A.3 directive families, the four named Pintonium pitfalls, OQ-7 shaping, and pure-`:engine`
  placement. The six admitted omissions/contradictions above are the exceptions, not a basis for
  manufacturing additional findings.
- The target retains all thirteen mandatory sections and the issues admitted here are localized
  contract, ownership, or conformance repairs. None requires rebuilding the Phase 3 architecture,
  changing the publication boundary, or assigning implementation work to an already excluded
  phase.
- No candidate from the surviving set was refuted, consolidated as a duplicate, or cleared on
  independent re-derivation. The Gate-dropped candidates were excluded only for the stated
  pre-adjudication reasons and were not used as findings.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=6; notes=0
Interface changed: yes

All six admitted findings are correction-sized, but each is a real contract defect: the clear-color
baseline, conformance ownership, public-interface change coverage, texture-key suffix disposition,
gdepth precedence, and absent-routing semantics are independently consumer-visible. None is a
structural miss requiring FAIL, and none is a note that can be deferred without fix-up. All six
final dispositions touch the declared interface/change-trigger region, so the interface flag is
derived as `yes`.

The supplied prior-round trend is empty. The actual history is not literal convergence evidence:
prior rounds reached PASS more than once and then exposed new post-fix-up surfaces, while Round 35
also contained a separate verification-readiness failure and several corrections. This round does
not revive those prior findings; it independently admits the six current candidates that survived
this round's Gate. The current surface therefore cannot close on a trend assumption.

The next required action is a scoped fix-up resolving all six candidates: repair the §5 clear and
routing/gdepth semantics, align the texture conformance row and test, correct the Phase 1
`:conformance` hand-off, and bind/incorporate the remaining public declarations under the monitored
§5 region. Because every admitted repair changes that region or its declared dependency surface, a
fresh whole-document verification round is required before Phase 3 can close or be consumed by a
dependent.

## Resolutions

### candidate-003 — Applied

`ResourceRequirements.ColorAttachmentRequirement` now publishes an `Optional<Vec4f>
clearColorOverride`. Empty means no explicit clear-color directive; Phase 5 resolves the
index-aware defaults to runtime fog for colortex0, solid white for colortex1, and transparent
black thereafter, while an explicit transparent-black override remains distinguishable and
`clear=false` suppresses the operation. The binding algebra, producer map, named tests, and
decision log were updated. Because the published `resources` component semantics changed, the
configuration schema is now 4 and unsupported schema values through 3 are rejected.

### candidate-004 — Applied

Phase 3 no longer consumes Phase 1's `:conformance` extension point. The dependency statement,
§5.2, test ownership, P3-C20 register/checklist, and D-10 disposition now say that Phase 3 emits
the manifest-only goldens, synthetic fixtures, capability profiles, and matrix-run hand-off;
Phase 2 owns the adapter, CI job, and `:conformance` integration.

### candidate-006 — Applied

The §2.2 public declarations are explicitly incorporated into the corresponding §5.1 binding
rows for the front-end entry points and request/results, source materialization/geometry, macro
contributors, and internal-source values. The binding rule covers exact signatures, record
components, variants, defaults, absence, ordering, validation, failure, and lifecycle semantics;
any edit must update its row, including an explicit unchanged entry. PackConfiguration schema
changes remain governed separately by §5.3.

### candidate-008 — Applied

The F.5 map and texture model now give recognized terminal filter/wrap property-key suffixes an
explicit disposition: strip them before key construction and ignore their requested setting.
The base sampler and numeric duplicate discriminator remain published, while `.mcmeta`
blur/clamp is retained as a separate `TextureSidecarRef`. Dedicated filter- and wrap-suffix
tests were added to the conformance row and the binding sampler semantics were aligned.

### candidate-009 — Applied

An active `gdepth` declaration is now a mandatory colortex1 `RGBA32F` request independent of
source order. A valid explicit non-RGBA colortex1 format is retained as a conflict diagnostic
but cannot override the upgrade; explicit `RGBA32F` agrees. The directive map, aggregation rule,
§5 published meaning, tests for prior/subsequent explicit formats, and decision log were aligned.

### candidate-010 — Applied

`DrawRouting` is now a tagged immutable algebra: `AllUsed` means no active routing directive and
writes all buffers used by the program, while `Explicit` preserves ordered targets and an empty
target list means explicit `DRAWBUFFERS:N`/no output. The A.3 map, scanner baseline, §5 binding,
tests, and decision log now distinguish the two states.

### Notes deferred

None. Round 36 admitted no note-severity finding; all six admitted corrections were applied.

### Verification trigger

All six resolutions change the monitored §5 interface region or the declared dependency surface.
The target remains in `v1` and requires a fresh whole-document verification before Phase 3 can
close or be consumed by a dependent. No refusal was necessary.
