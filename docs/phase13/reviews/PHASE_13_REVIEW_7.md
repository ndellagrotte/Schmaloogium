# Phase 13 — Independent Architecture Review R7

## Frozen identity and verdict summary

- **Owner:** `docs/phase13/v1/PHASE_13_DOC.md`, complete §§0–12.
- **Round:** R7, frozen attempt 7.
- **Supplied SHA-256:** `eeca510722c4bf168dde5b6d4f92eb91f9962c9b5834faac31756f5058b4e28f`.
- **Inventory:** the Phase 13 entry in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_7.json:77-82` agrees with the assigned identity. This is the supplied inventory identity, not a checksum execution claim.
- **Substantive corrections:** 2. **Notes:** 2. **Blocking structural findings:** 0.
- **§5 impact:** **yes**. Both corrections concern semantics incorporated by §5.1 and consumed across the texture binding/parameter boundary.

The outer atlas-attempt lifetime, target-limit receipt and reciprocal application-v2 catalogue are materially specified and agree with the inspected receiving contracts. Two remaining companion-texture contracts are incomplete: their effective sampling baseline has no producer rule, and their atlas identity is not consumed when choosing among generated companions. These are localized architecture corrections, not grounds for a structural rebuild.

## Scope, authority and independent evidence

I read the entire owner, including its historical addenda, active algorithms, exposed/consumed interfaces, failure taxonomy, threading, planned checks, milestones, decisions and implementation checklist. Authority was selected from its header: `docs/design/v3/DESIGN.md:132-1134` and the Phase 13 assignment at `:2436-2510`, not the newest design globally. `docs/MOVES.md` was used for versioned-path interpretation. Governing research reads covered §§0–1, the texture portion of §4.6, the milestone row, Apps B.3/B.4/D.3/E/F.3/F.5 in `docs/research/v1/RESEARCH.md`. The narrow U1 correction and separate source-specific sidecar policy were independently read in `docs/decisions/U1_TEXTURE_SAMPLING.md` and `docs/decisions/TEXTURE_SIDECAR_DEFAULTS.md`; they do not authorize unspecified key suffixes or establish measured G6 parity.

Dependency reading included complete §5 regions of current Phase 3, Phase 5 and Phase 7, with load-bearing definitions and actual receiving algorithms: Phase 3's immutable acquisition and texture declaration rules; Phase 5's full candidate/lease algebra, format/target policy and physical selection/binding; Phase 7's ordinary draw, resource-NONE, accepted-generation publication, current-atlas and health-report branches. Additional bounded reads covered Phase 1's §5 and incorporated capability, allocation, parameter and ownership grants; Phase 6's atlas receiver; Phase 8's actual lease/binding/activation/finally transaction; Phase 14's actual parameter/cache/baseline and lifetime consumers; and Phase 2's actual owner13 report projection and rejection rules. These reads establish the Phase 13 boundary review, not sibling-owner verdicts.

Permitted reference evidence was PD §§7.4/7.6/11 and relevant §§17–18 rows, the texture-system section only of `reference-src/schlorbium-HD_U_G6_pre1/SHADER_ENGINE_IMPL.md:456-487`, and bounded shipped author-document texture/sampler/macro rows. No OptiFine implementation or prohibited Oculus/transformer/transcript material was read. Fresh MCP queries independently confirmed the outer loadSprites SRG descriptor, allocateTextureImpl parameter order, TextureStitchEvent's map-only exposure, the three atlas accessor mappings, and the sprite metadata/counter mappings. The available Cleanroom patch was read directly, with the historical-pin limitations below.

## Independent contract checks

1. **Outer atlas lifetime and actual extent.** `docs/phase13/v1/PHASE_13_DOC.md:1397-1486` puts H13-ATLAS-06 around the complete outer `loadSprites` body, before Pre. The wrapper encloses Pre listeners, population, object replacement, inner allocation, Post listeners and all exceptional exits. Acceptance requires one authenticated current-map/current-epoch allocation capture and a matching Post; later failure discards accepted/pending data. Duplicate/reentrant/unscoped work cannot mint replacement authority. The available patch independently places Pre before `registerSprites` at `reference-src/Cleanroom-0.6.12-alpha/patches/minecraft/net/minecraft/client/renderer/texture/TextureMap.java.patch:20-27`, passes Stitcher dimensions into allocation at `:166-175`, and dispatches Post at `:183-188`. MCP independently confirms both method descriptors. This is architecture/source correspondence, not executed exception-safety proof.
2. **Exact application-v2 receiving path.** The eight ordered IDs, seven active FEATURE rows and dormant SPRITE02, event/member/injection count distinctions, immutable application counts and new domain in P13 `:1470-1494,1564-1580` reach the real P7 catalogue/validation at `docs/phase7/v1/PHASE_7_DOC.md:1832-1864` and P2 projection/rejection at `docs/phase2/v2/PHASE_2_DOC.md:1344-1384`. Old application-v1 evidence is explicitly rejected. Runtime extent failures remain catalog/diagnostic/Unknown evidence, not overwritten frozen counts.
3. **Actual target-limit preflight.** P13 `:978-1009` consumes distinct captured 2D/3D/rectangle maxima and applies them to the appropriate used axes before allocation; unsupported-zero has the target/format failure disposition rather than being treated as a small extent. The supplying capture/replay law is present at `docs/phase1/v14/PHASE_1_DOC.md:2924-3024`, and native allocation admission actually consumes it at `:4596-4602`. P5 receives the same facts at `docs/phase5/v1/PHASE_5_DOC.md:1469-1477`. No guessed rectangle/3D limit or new engine GL query is needed.
4. **Typed source acquisition, upload and sidecars.** P3's actual acquisition dispatch at `docs/phase3/v1/PHASE_3_DOC.md:1889-1986` supplies immutable same-load bytes and the closed Missing/Unreadable/InvalidReference branches. P13 `:914-1078` consumes typed references, performs source-to-facade conversion for all four raw targets and all owned source roles, and leaves foreign storage untouched. P13 `:1134-1200,1293-1316` specifies bounded atomic sidecar recovery and role-sensitive identity. P7 `:3580-3601` retains the actual configuration/assets through NONE instead of reopening the selected pack. Current §5 schema23 adoption explicitly supersedes dated older receipts; those historical numerals were not treated as competing current gates.
5. **Physical binding, ownership and retirement.** The actual P5 consumer at `docs/phase5/v1/PHASE_5_DOC.md:2480-2603` filters complete sampler shapes, preserves custom precedence and performs zero-GL preflight followed by physical binds. Bound alone transfers the overlay lease. P7's ordinary branch at `docs/phase7/v1/PHASE_7_DOC.md:1116-1160` and P8's shadow branch at `docs/phase8/v1/PHASE_8_DOC.md:900-982` consume the closed results and retain exactly-one closure duties. P13 retirement and same-owner reuse at `:1366-1384,1521-1544` do not use content equality to revive authority or delete borrowed storage.
6. **Sampling restoration and atlasSize.** P1's complete owner conversion and baseline law at `docs/phase1/v14/PHASE_1_DOC.md:4489-4523,4622-4632` and P14's actual setter/cache logic at `docs/phase14/v1/PHASE_14_DOC.md:635-653` preserve complete object state for ordinary sampler-zero boundaries. P7's actual atlas sink at `docs/phase7/v1/PHASE_7_DOC.md:3475-3487` authenticates current binding before querying P13 and forwards Known or zero to P6; `docs/phase6/v1/PHASE_6_DOC.md:1486-1500` receives it with active-immediate/inactive-cache semantics. Stitch availability alone is not bind evidence. The missing companion policy identified below precedes, rather than invalidates, this otherwise explicit conversion machinery.

## Substantive corrections

### C1 — Define the effective sampling baseline for owned companion atlases

**Severity:** correction (P2). **Owner locations:** `docs/phase13/v1/PHASE_13_DOC.md:1297-1301`; related production at `:415-417,453-456,722-744,1058-1059` and incorporated exposure at `:1609-1616`.

The parameter fingerprint requires baseline and effective min/mag/wrap for the COMPANION and DEFAULT_FILL roles, but delegates no-sidecar roles to their “existing actual owner/generator policy.” The owner supplies such a rule for generated noise and PNG/raw/noise overrides, but does not supply one for its own newly created full companion atlases or standalone default-fill textures. AtlasDescriptor/CompanionAtlasPlan carry extent/mips/content, not sampling metadata; the companion algorithm defines mip generation but never selects minification, magnification or wrapping. This is not supplied downstream: `docs/phase1/v14/PHASE_1_DOC.md:4489-4495` maps P13's already-selected triple, and `docs/phase14/v1/PHASE_14_DOC.md:612-615,1743-1768` expressly derives rather than authors that state.

**Observable breakage:** even with a valid enabled companion and accepted atlas, preparation cannot derive the required ReadyAsset parameters, canonical fingerprint and complete setParameters call from the documented inputs. An implementer must invent a policy; for a mipped normal/specular atlas, choosing NEAREST versus a mipmapped min filter changes which mip levels are sampled and therefore shader-visible results. Falling back to GL defaults is also contrary to the mandatory complete-object baseline contract.

**Minimal owner fix:** specify the exact owner-authored companion and standalone-default min/mag/wrap policy, including the zero-mip versus mipped case. If it is derived from resource/base-atlas state instead, name the exact captured input and derivation/lifetime rule rather than an unspecified existing policy. Incorporate that rule into §5.1/§5.5 and the parameter fingerprint, with a planned case proving the selected mip/filter policy. Do not extend the shader-property grammar or import an unapproved filter-override feature.

### C2 — Consume the base-atlas identity when selecting generated companions

**Severity:** correction (P2). **Owner locations:** `docs/phase13/v1/PHASE_13_DOC.md:768-775`; related plural catalog and ordering at `:398-399,906-909`, current-atlas query at `:1237-1239`, and shared selection at `:1100-1117`.

P13 permits a catalog of multiple accepted atlases and emits an atlas-tagged NORMALS/SPECULAR candidate for each into the same stage/name cell. Its ordering explicitly includes AtlasId. However, the actual receiving selection algorithm at `docs/phase5/v1/PHASE_5_DOC.md:2491-2519` filters by sampler shape/target/format/comparison, ranks only Custom entries, and then chooses a compatible companion fallback. It has no branch correlating `CandidateOrigin.Companion.atlas` with the base atlas used for this draw, nor a rule selecting one designated world atlas from the plural catalog. P7's authenticated current-atlas path at `docs/phase7/v1/PHASE_7_DOC.md:3475-3487` only updates atlasSize; it does not participate in companion selection.

**Observable breakage:** for two accepted atlases with distinct contents/layouts but the same ordinary sampler2D capability, both generated normal candidates survive every documented compatibility check. The consumer cannot determine which companion matches the draw's base atlas. Picking by list order can bind the other atlas's normal/specular pixels; treating the ambiguity as unavailable suppresses an otherwise supported draw. Correct dimensions and a correct atlasSize uniform cannot repair sampling a different atlas.

**Minimal owner fix:** define which accepted base-atlas identity governs generated world companions and make that identity available to the binding decision. Require a corresponding P5 receiving branch that selects only that atlas's companion, with explicit non-atlas/unavailable behavior and unchanged compatible-custom precedence. If only the designated block/item atlas contributes generated world candidates, state that producer restriction explicitly while retaining other catalog metadata/query semantics. Update §5's exact producer/receiver contract and add a planned two-atlas, equal-sampler-shape case; canonical ordinal alone is not the association.

## Notes and limitations

### N1 — Historical source and licence confidence remains qualified

**Severity:** note. `docs/MOVES.md` names historical `reference-src/cleanroom-0.6.6-alpha` and `reference-src/pintonium-9c2fcc1`; a bounded inventory lookup found both missing. The available Cleanroom 0.6.12-alpha patch corroborates the outer Pre/population/allocation/Post structure, but is not the historical pin and does not prove the selected runtime's transformed methods. Its available root licence is LGPL-2.1 (`reference-src/Cleanroom-0.6.12-alpha/LICENSE:1-7`). The available `reference-src/Pintonium-main/LICENSE:1-7` is GPLv3, so the historical PD/pinned-tree LGPL characterization cannot clear arbitrary present-main files for reuse. P13 already preserves this qualification at `docs/phase13/v1/PHASE_13_DOC.md:241-251`. No implementation was copied and no file-level reuse clearance is issued.

### N2 — Explicit normal-byte and mip assumptions are not newly certified

**Severity:** note. `docs/phase13/v1/PHASE_13_DOC.md:729-737,746-764` retains the explicit mip/filtering assumption and the literal `0xFF7F7FFF` channel-order tension. The unresolved C-TX01 disposition is governing text at `docs/design/v3/DESIGN.md:1088`; this review does not silently substitute a different packed normal or convert an acknowledged assumption into measured flat-normal/parity evidence. Likewise, the signed noise recurrence is specified reproducibly but was not executed in this review. These acknowledged evidence limitations are not additional corrections or demands to run future tests at this architecture gate.

No files were edited. No formatter, linter, build, test, validation command, checksum command, runtime, client or GL experiment was run. Source/mapping inspection is the evidence for this architecture review. No sibling owner, actual Mixin application, rendering behavior, runtime safety, source-licence closure or classic-matrix conformance is certified.

## Final verdict

**Corrections: 2. Notes: 2. §5 impact: yes.**

PASS-WITH-CORRECTIONS

## Resolutions

2026-09-08 architecture-only fix-up; original body, notes and verdict are preserved.

- **C1 / P13C1:** D-P13-42 specifies mipped full companions NEAREST_MIPMAP_LINEAR/
  NEAREST/REPEAT, zero-mip companions and standalone defaults NEAREST/NEAREST/REPEAT.
  Actual preparation stores and fingerprints the complete triple and build passes it through
  mandatory complete setParameters. This is local policy, not a new observed default or grammar.
- **C2 / P13C2:** D-P13-43 and reciprocal P5 D-P5-50/P7 D-P7-73/P8 D-P8-39 make the
  accepted actual base-object/atlas association a required context-bound lease input. P5 resolves
  winning base then custom-first matching companion/defaults; equal-shape atlases cannot be
  selected by ordinal. Required ordinary/nested/shadow before-draw refresh retains the pass/
  selector/root activation, with closed failures and independent binding/lease cleanup.
- Actual algorithms, complete declarations, §5, planned cases and checklist are amended.
  N1/N2 confidence and normal-byte qualifications remain. No implementation or validation command
  was run; planned scenarios are not runtime proof. §5 changed and requires fresh owner/receiver review.
