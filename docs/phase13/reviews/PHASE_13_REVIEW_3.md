# Phase 13 verification review — round 3

## 0. Method and reading order

This was a sole-adjudicator verification round for `phase-13`, using the manifest-resolved target and
v3 governing design. I re-derived the surviving candidates before consulting either prior Phase 13
review, in this order:

1. the whole `docs/phase13/v1/PHASE_13_DOC.md`, including §§0–12, the §5 interface region, the
   conformance map, the closing status, and the implementation checklist;
2. the relevant Part I rules and the Phase 13 specification in `docs/design/v3/DESIGN.md`, including
   G0/G1, G4/G5, G9, G11.4, and the Phase 13 objective, scope, REV1 checklist, and doc gate;
3. the selected `docs/research/v1/RESEARCH.md` contract selectors;
4. the Phase 3, Phase 5, and Phase 7 binding-contract regions, plus their latest dependency review
   state; and
5. the permitted Pintonium texture evidence, the permitted behavioral texture digest, and the
   shipped `shaders.properties` documentation where relevant.

Only after that independent pass did I read `PHASE_13_REVIEW_1.md` and
`PHASE_13_REVIEW_2.md`, last, to distinguish new surfaces from already applied resolutions. The
prior reviews did not override the current governing rule or the current target text.

No network access, verification script, nested loop, or agent fan-out was used. I did not open any
forbidden `*.txt` source, any `docs/**/chatlogs/**` path, or any prior session transcript. In
particular, `shaders.properties` and the Markdown behavioral digest were permitted; the similarly
named `shaders.txt` was not opened. No write was made outside this review file.

The pre-adjudication Gate drops remain excluded: candidates 012–019 had unresolvable supplied
quotes. Candidates 007, 008, 011, and 014 were already dropped by the Refute stage. None of those
settled exclusions is revived here.

## 1. Findings

### Finding 1 (candidate-001) — Phase 7 is still incorrectly consumed provisionally after its literal PASS

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:47`–`:63`, `:1090`, `:1124`–`:1130`,
  `:1475`–`:1480`, and `D-P13-2` at `:1331`.
- **Claim:** the dependency-state labels and D-P13-2 decision consistently apply the governing
  verified-state rule.
- **Evidence:** DESIGN §G1.3 says a phase is verified from its latest review verdict, with
  `PASS` sufficient when no interface change remains (`docs/design/v3/DESIGN.md:348`–`:359`).
  The latest Phase 7 review is literal `PASS`, with zero corrections and `Interface changed: no`
  (`docs/phase7/reviews/PHASE_7_REVIEW_36.md:105`–`:123`), and explicitly says no further action
  is required. The target nevertheless keeps every Phase-7-derived row provisional solely because
  the Phase 7 footer awaits a version roll. That footer is not a governing exception and is stale
  after review 36.
- **Severity:** correction. The consumed facts and unchanged interface are usable, but the live
  dependency state and residual trigger are wrong.
- **Touches interface/change-trigger region:** yes. The required correction edits the §5.2/§5.4
  state disclosure and the related live decision/closing status.
- **Ordered correction:** mark Phase 7 verified from review 36, make D-P13-2 historical or
  superseded rather than live gating, and retain only any genuinely applicable future trigger.

### Finding 2 (candidate-002) — Phase 13 denies a suffix behavior that its current Phase 3 contract publishes

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:394`–`:410`, `:712`–`:727`, `:768`–`:795`,
  and the Phase 3 consumption row at `:1067`–`:1075`.
- **Claim:** the custom-key conformance ruling is semantically consistent with the unchanged Phase 3
  algebra consumed by Phase 13.
- **Evidence:** the current Phase 3 binding contract says a recognized terminal filter/wrap suffix
  is stripped before `TextureBindingKey` construction and ignored, while `.mcmeta` is retained
  separately (`docs/phase3/v1/PHASE_3_DOC.md:1482`–`:1500`; the applied review records the same
  distinction at `docs/phase3/reviews/PHASE_3_REVIEW_36.md:324`–`:328`). Phase 13 instead says such
  suffixes do not exist, while claiming to consume the key unchanged and separately identifying
  the suffix disposition as a live Phase 3 exposure (`:1131`–`:1135`). The normalized downstream
  shape may remain `(stage, sampler, discriminator)`, but the published semantics are not
  reconciled.
- **Severity:** correction. This is a fixable contract/conformance clarification, not a whole
  texture-binding rebuild.
- **Touches interface/change-trigger region:** yes. The Phase 3 consumption and exposure rows in
  §5 must state the normalized boundary accurately.
- **Ordered correction:** distinguish Phase 3's recognized-property-key suffix stripping/ignoring
  from the separately retained `.mcmeta` sidecar, preserve the no-reparse boundary, and either
  record or explicitly route the upstream conflict under the governing rule.

### Finding 3 (candidate-005) — custom source/content identity is absent from the publication fingerprint

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:278`–`:298`, `:729`–`:766`, `:882`–`:899`,
  `:981`–`:994`, and §5.1 at `:1051`.
- **Claim:** equal `TextureOverlayFingerprint` values make two custom-texture publications
  interchangeable.
- **Evidence:** Phase 3 preserves distinct `PackPath`, raw-byte path, and exact `minecraft:`
  resource identities, including dynamic/atlas identities (`docs/phase3/v1/PHASE_3_DOC.md:1490`–`:1500`).
  The Phase 13 `CustomTexturePlanEntry` has no source identity, and its documented
  `TextureUploadSpec` only carries target-specific dimensions and validation semantics
  (`:764`–`:766`). The stated fingerprint covers the source-less plan and declares equal values
  interchangeable (`:894`–`:899`). Yet resource reload explicitly re-resolves live `minecraft:`
  objects because the object may change (`:981`–`:985`). Different same-key bytes, paths, effective
  configuration, or foreign live objects can therefore collide or be reused as if interchangeable.
- **Severity:** correction. A canonical identity/digest addition is localized, though it changes the
  publication identity contract.
- **Touches interface/change-trigger region:** yes. `TexturePlan`/fingerprint semantics are exposed
  through the §5.1 fingerprint row and consumed by Phases 5 and 7.
- **Ordered correction:** define source-kind and content/configuration identity for owned uploads,
  logical resource identity plus an appropriate reload/object epoch for foreign live textures, and
  include those values in the canonical fingerprint without hashing GL names. Add same-key
  different-content and reload-replacement tests.

### Finding 4 (candidate-006) — companion animation requires state that no active hand-off exposes

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:248`–`:254`, `:486`–`:490`, `:598`–`:627`,
  and `:950`–`:964`.
- **Claim:** the active hook and Minecraft-free model are sufficient for the exact frame and
  interpolation synchronization promised by §4.1.7.
- **Evidence:** `SpriteDescriptor` contains only `frameCount` and `animated` (`:248`–`:250`). The
  documented engine hand-off supplies a presence bit and decoded pixels (`:486`–`:490`), while
  `H13-ATLAS-04` supplies only an atlas identity and the sprite-state rows expose no frame
  durations, frame map, current index, or interpolation fraction (`:959`–`:964`). The algorithm
  nevertheless requires the current base frame and identical interpolation weight every tick
  (`:612`–`:620`). The dormant per-sprite update hooks are catalogued, not an active state path.
- **Severity:** correction. An explicit state snapshot/accessor and ordering rule can repair the
  seam without rebuilding the texture estate.
- **Touches interface/change-trigger region:** yes. The atlas-to-engine hand-off or the H13 hook
  contract audited by Phase 7 must change.
- **Ordered correction:** expose immutable frame metadata and a post-vanilla-tick current-frame/
  interpolation snapshot, or activate and specify an equivalent read-only sprite-state accessor;
  define reload invalidation and retain the existing frame-0 feature fallback.

### Finding 5 (candidate-010) — the macro cycle break is asserted but not wired before preprocessing

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:225`–`:228`, `:449`–`:482`, `:567`–`:598`,
  `:1053`, `:1108`, and `:1392`–`:1395`.
- **Claim:** Phase 13 can provide `MC_NORMAL_MAP` and `MC_SPECULAR_MAP` state before Phase 3
  materializes shader sources.
- **Evidence:** Phase 13's plan request requires an already published `PackConfiguration` (`:238`–`:244`),
  but Phase 3's load order preprocesses and analyzes sources before publishing that configuration
  (`docs/phase3/v1/PHASE_3_DOC.md:644`–`:666`). Phase 3's materializer accepts only its existing
  `MacroContribution`, and its contributor algebra has no normal/specular variant
  (`docs/phase3/v1/PHASE_3_DOC.md:532`–`:613`). The target says the state is available by
  construction, but R1 remains ungranted and its fallback explicitly leaves both macros undefined.
- **Severity:** correction. The honest fallback remains renderable; the claimed PBR path is simply
  not executable as specified.
- **Touches interface/change-trigger region:** yes. A real preprocessor-time producer and call order
  must be added to R1 and the §5.1/§5.3 contract, or the preprocessor-time claim must be withdrawn.
- **Ordered correction:** define the preliminary demand input, the Phase 13 state producer, and the
  same-build Phase 3 option-macro hand-off before jcpp; do not treat a completed `TexturePlan` as the
  missing preprocessor input.

### Finding 6 (candidate-020) — the required shared-unit texture behavior has no executable binding seam

- **Location:** `docs/design/v3/DESIGN.md:2467`–`:2478`; `docs/phase13/v1/PHASE_13_DOC.md:238`–`:244,
  :768`–`:795`, `:887`–`:899`, `:1048`–`:1051`, and PD-8 at `:390`; Phase 5 at
  `docs/phase5/v1/PHASE_5_DOC.md:1879`–`:1899` and `:1933`–`:1947`.
- **Claim:** F5-10's per-program sampler-type disambiguation, together with the REV1
  `customtexN`/program-patching checklist, can deliver the selected typed custom texture through
  the published Phase 5/7 path.
- **Evidence:** the governing Phase 13 specification makes multiple target types per unit and the
  validated `customtexN` program-patching capability part of the v0.5 checklist. Phase 13's request
  has no merged per-linked-program sampler layout or effective program selector, despite assigning
  that layout to Phase 4 (`:165` and `:238`–`:244`). Its only published texture result is one
  `(StageId, TextureOverlayKey) -> Present(TextureHandle)|Absent` entry (`:887`–`:899`, `:1048`–`:1051`).
  Phase 5's `textureBindings` receives no program or sampler-type selector and reduces the fixed
  rows to one `Bindable(TextureHandle)` per unit; Phase 7 binds those rows before program activation.
  The target explicitly rejects generated `customtexN` names and source patching at PD-8 instead of
  providing an equivalent executable path. Research and the shipped pack-author documentation make
  the one-type-per-unit-per-program rule normative (`docs/research/v1/RESEARCH.md:1484`–`:1490`;
  `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties:103`–`:119`).
- **Severity:** blocking. A legal pack with different target types on one fixed unit in different
  programs cannot receive the required handle through any declared path, and the target's explicit
  rejection does not implement the governing REV1 checklist. Repair requires a coordinated
  cross-phase binding/program-layout design (or a fully specified program-patching equivalent), not
  a local wording fix-up.
- **Touches interface/change-trigger region:** yes. The Phase 4/5/7/13 hand-off, publication/lease
  shape, and binding operation must be changed and freshly verified.
- **Ordered correction/rebuild action:** expose the effective linked-program sampler layout and
  current-program selection at binding time, retain target-specific candidates with deterministic
  precedence and lifetime/fingerprint semantics, and extend the Phase 5/7 operation or implement the
  mandated equivalent `customtexN` patch path. Define typed absence/degradation and ownership before
  attempting closure.

### Finding 7 (candidate-021) — `KEY_DOMAIN` UnsupportedBinding has no valid closed `wouldBe` value

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:720`–`:722`, `:820`–`:842`, and `:1048`–`:1055`.
- **Claim:** every App F.5 binding outside Phase 5's current overlay domain can be represented by the
  declared diagnostic without inventing a key.
- **Evidence:** the target routes documented sampler names outside the current domain to
  `UnsupportedBinding` and acknowledges that Phase 5's `TextureOverlayKey` is closed to
  `NORMALS`, `SPECULAR`, `GAUX1`–`GAUX4`, and `NOISE` (`:720`–`:724`). The record nevertheless
  requires a non-optional `TextureOverlayKey wouldBe` for both `KEY_DOMAIN` and `STAGE_COLUMN`
  (`:826`–`:832`). Phase 5 supplies no out-of-domain enum member
  (`docs/phase5/v1/PHASE_5_DOC.md:1879`–`:1885`). `TextureBindingKey` preserves the sampler identity,
  but it cannot make the separate mandatory field well-typed.
- **Severity:** correction. The fallback remains feature-local rung 2a; only its closed diagnostic
  representation is under-specified.
- **Touches interface/change-trigger region:** yes. `UnsupportedBinding` is an exposed §5.1
  planning/diagnostic projection and its payload/fingerprint encoding must change.
- **Ordered correction:** use an explicit optional or discriminated requested-target value, with no
  sentinel or unrelated in-domain key for `KEY_DOMAIN`, and update the canonical diagnostic and
  test encodings.

### Finding 8 (candidate-022) — the specified noise recurrence is not the cited recurrence

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:641`–`:672`.
- **Claim:** §4.2.2 faithfully restates the permitted behavioral noise digest.
- **Evidence:** the target uses logical right shift `>>>` and absolute-value reduction before `mod 128`
  (`:648`–`:658`). The permitted digest specifies arithmetic right shift `>>` and direct `rand(seed)%128`
  (`reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:469`–`:474`). Over signed 32-bit states
  these choices are observably different. The target marks channel decorrelation and amplitude as
  assumptions but does not disclose the shift/absolute-value divergence while saying it matches.
- **Severity:** correction. The generator can be aligned or the divergence can be explicitly recorded
  and tested without changing the exposed texture interface.
- **Touches interface/change-trigger region:** no. The repair is internal generator arithmetic and
  provenance, outside §5.
- **Ordered correction:** either use explicitly wrapping signed arithmetic with the digest's `>>` and
  direct remainder, or identify the logical-shift/absolute-value variant as a deliberate divergence and
  remove the unqualified fidelity claim.

### Finding 9 (candidate-023) — conformance-map provenance contains non-resolvable path abbreviations

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:329`–`:343`, especially F5-3–F5-7,
  F5-11–F5-12, and F5-13–F5-17.
- **Claim:** every mapped row's provenance cell supplies a repository-relative citation that the gate
  can independently resolve.
- **Evidence:** multiple cells contain the literal `…/doc/shaders.properties` path, and F5-12 says
  `same rows` rather than repeating an independently resolvable source and line range. DESIGN §G1.2
  binds hand-run reviews to the repository-relative citation rule (`docs/design/v3/DESIGN.md:332`–`:342`),
  while the target's own map preface says provenance is row-level (`:320`–`:321`). The full path in
  §0 does not establish a sanctioned alias for a malformed map cell.
- **Severity:** correction. This is a conformance-map repair, not a runtime or architecture failure.
- **Touches interface/change-trigger region:** no. The affected §3 provenance cells are outside the
  manifest-declared §5 change-trigger region, and the correction changes no contract shape.
- **Ordered correction:** replace every ellipsis and cross-row shorthand in the map with full
  repository-relative paths and inclusive line ranges, repeated independently per row.

### Finding 10 (candidate-024) — the `.mcmeta` section cites an unrelated Phase 3 line

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:797`–`:818`, especially `:801`.
- **Claim:** the Phase 3 citation attached to sidecar retention supports that claim.
- **Evidence:** the target cites `docs/phase3/v1/PHASE_3_DOC.md:740`, but that current line is a
  sliders row. The relevant current dependency rows are the property-key suffix row at `:749` and
  the `.mcmeta` sidecar-retention row at `:750`; the target's own cross-phase table already points
  to `:750` (`:1075`).
- **Severity:** correction. The surrounding conformance coverage is valid, but the local pointer is
  false and misdirects implementation review.
- **Touches interface/change-trigger region:** no. The ordered edit is confined to §4.3.5.
- **Ordered correction:** cite `:750` for retention and, only where the distinction is discussed,
  cite `:749` for the separate suffix behavior; do not alter the interface.

### Finding 11 (candidate-025) — contract-visible Pintonium rows omit required decision references

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:383`–`:390`, especially PD-3 and PD-8.
- **Claim:** every contract-visible Pintonium adoption/rejection row carries the required PD and
  §G11.4 decision reference.
- **Evidence:** G9 requires a PD row carrying the decision reference for a contract-visible adopted or
  rejected mechanism (`docs/design/v3/DESIGN.md:817`–`:855`), and G11.4 requires a recorded phase
  decision for such an adoption (`:943`–`:960`). PD-3 has a PD citation and spec citation but only
  says §3.6 records a ruling; it does not carry `[D-P13-6]`. PD-8 explicitly rejects the
  `customtexN`/program-patching mechanism but carries no phase decision identifier. The later
  `D-P13-6` entry at target `:1335` does not satisfy a row-local map requirement, and no separate
  decision covers PD-8's rejection.
- **Severity:** correction. The missing references are a documentation-gate defect; the underlying
  decisions can be recorded without changing the §5 interface.
- **Touches interface/change-trigger region:** no. The ordered repair is limited to the §3.5 map and
  its decision log/provenance, not the exposed cross-phase shape.
- **Ordered correction:** attach the applicable decision identifier to PD-3, add an explicit decision
  for the customtexN rejection if it remains a rejection, and audit the analogous contract-visible
  PD rows for the same row-local requirement.

### Finding 12 (candidate-026) — T-7 labels an inference as observed behavior

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:366`.
- **Claim:** T-7's `[V:observed]` provenance tag accurately identifies reference behavior.
- **Evidence:** RESEARCH defines `[V:observed]` as verified reference-implementation behavior
  (`docs/research/v1/RESEARCH.md:24`–`:38`). The T-7 row itself says synchronization is merely
  implied by T-2 and that there is no reference; the governing design calls it a requirement to
  design (`docs/design/v3/DESIGN.md:2482`–`:2483`), while permitted Pintonium evidence says its
  approach does not solve sprite-animation synchronization (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:631`–`:637`).
- **Severity:** correction. The animation design may remain, but its provenance confidence must be
  truthful.
- **Touches interface/change-trigger region:** no. Retagging a §3 map row does not alter §5.
- **Ordered correction:** use a design/decision provenance tag and state that no permitted observed
  reference was found, or add a real positive permitted behavioral citation before retaining
  `[V:observed]`.

## 2. Checked and clean

The mandatory thirteen-section skeleton is present and substantive. The objective, v0.5 scope,
companion defaults/layout, noise size/override/unit, all three custom-texture source forms,
`.mcmeta` interpretation, stage expansion, atlas-size validity, hook catalog, lifecycle, failure
ladder, threading, milestone tags, and no-OQ treatment are all present. The fixed App B.3 facts,
Phase 5 lease/ownership/rejection vocabulary, Phase 7 reload and hook ownership, module/thread
boundaries, shaders-off behavior, and the basic fallback ladder remain consistent where no finding
above changes them.

The prior round-1 and round-2 applied repairs for Phase 3 state disclosure, Phase 7 review anchoring,
the normal-default wording, `atlasSize`, and stale Phase 3 citations were rechecked. They do not
clear the new Phase 7 verified-state contradiction, the new Phase 3 suffix-semantic contradiction,
or the new source/fingerprint and binding seams.

Candidates 003 and 009 were independently confirmed as describing the same missing per-program
sampler-type input/output seam retained in the broader candidate-020 finding; they are not counted
as separate duplicate findings. Candidate 004 is the same closed-domain `UnsupportedBinding`
shape defect retained as candidate-021. This is consolidation, not a refutation of the underlying
claims.

The finder-reported clean areas remain clean outside those findings: fixed unit numbers and absence
vocabulary, resize types/reasons, broad Phase 7 atlas/overlay hand-off, ownership boundaries, and
mandatory-section/milestone coverage. The pre-adjudication refuted candidates 007, 008, 011, and
014 remain dropped. Gate-dropped candidates 012–019 remain excluded because their supplied evidence
was not independently resolvable; no new evidence was used to revive them.

## 3. Verdict

# FAIL
Counts: blocking=1; corrections=11; notes=0
Interface changed: yes

The admitted candidate-020 defect is structural: the target claims a mandatory per-program,
shared-unit behavior but exposes neither the linked-program selection input nor a binding/publication
shape that can carry the selected target. It also expressly rejects the REV1 customtexN/program-
patching checklist without an executable equivalent. This requires a coordinated cross-phase design
rebuild, not merely a §G1.3 wording fix-up. The other eleven admitted findings are correction-sized,
with the interface flag driven by the admitted §5-affecting corrections (and independently by the
candidate-020 rebuild).

The current round is not converged. Round 1 and round 2 repairs were applied, but this round exposes
new substantive contract, lifecycle, binding, fingerprint, animation-state, and provenance surfaces;
the prior literal-PASS history does not clear them. No note is admitted.

Next required action: rerun the Phase 13 build/rebuild session with this review as input, first
redesigning and explicitly publishing the Phase 4/5/7/13 typed shared-unit binding seam (or a fully
specified accepted program-patching equivalent), then resolving the remaining corrections. Because
§5 changes are required, a fresh whole-document verification round must follow the rebuild before
Phase 13 can close or be consumed.
