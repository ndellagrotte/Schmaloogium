# PHASE_1_DOC.md — Independent whole-document review, round twenty-six

## 0. Method, authority and inspected state

Target: `docs/phase1/v14/PHASE_1_DOC.md`, complete current document, lines 1–5715, including §§0.24–0.27, every incorporated §5 declaration/semantic contract, §8 and §11. Governing authority: `docs/design/v2.0-RC2/DESIGN.md`, not the v3 override used by historical reviews 24/25. This review read the complete RC2 Part I and Phase 1 specification, including the actual Doc gate. Research remains above design except for explicitly scoped maintainer decisions.

This is an architecture review, not an implementation test or §G5.3 integration clearance. No source, phase document, earlier review or authority file was edited. No build, formatter, linter, test, client, GL runtime or retired verification harness was run. Main will persist this review text as `docs/phase1/reviews/PHASE_1_REVIEW_26.md`; the reviewer’s tools are read-only.

The initial working-tree diff was empty. The review also inspected the Phase 1 changes since `11e6524`, rather than attributing every historical assertion to the September amendments. The finding below concerns the newly granted native geometry route and its receiving draw contract. Current-template drift is distinguished from patch-introduced findings below.

Readiness inputs: `docs/build/BUILD.md` complete; `docs/MOVES.md` relevant navigation/retirement material; integration reader notice, original findings, Phase 1 handoff ledger and all Resolutions/follow-ons. The latest reviewed integration disposition supersedes the original IR-03/12/18/24 statements but does not close IR-01 or grant implementation consumption.

Research inputs: complete §§0–1; assigned §§5.1–5.3, 6.1, 7.2, 12.2 and relevant §12.4 recipes; all additional sections actually used for the facade, geometry, texture, instancing, milestones, licensing and OQs, including §§3.1–3.5, 4.1–4.5, 6.2, 7.1/7.4, 8–11 and relevant Apps A/B/D/E/F/H. PD inputs: §§2 and 16, with license/trap orientation and standing §§17–18 lists. PD was treated as a pointer, never as conformance authority.

Template inputs: complete current `build.gradle`, `settings.gradle`, `gradle.properties`, all three `gradle/scripts/*.gradle`, wrapper properties, three CI workflows, README, `.gitignore`, and the complete current source/resource template set. MCP inputs: current `mixin-setup` guide and template mixin/checklist snapshots. Those MCP template examples identify older upstream pins; they do not override the executable checkout.

Source checks followed the selected design’s licensing rules before reading implementation. The available trees are `reference-src/Pintonium-main` and `reference-src/Cleanroom-0.6.12-alpha`, not authenticated checkouts of the historical revisions. Neither contains a `.git` entry at its root. The Pintonium root LICENSE contains GPLv3 text while its README declares LGPL-3.0; this review does not resolve that discrepancy into blanket source-copy permission or relabel the archive as historical `9c2fcc1`. Cleanroom’s inspected LICENSE is LGPL-2.1. No source was copied or newly adopted.

Bounded Pintonium checks read the complete current `MinecraftVersionShimService`, `GLStateManagerService`, `RenderSystemService`, and `CeleritasShaderVersionService`; the three startup mixins; the complete vintage mixin plugin/configuration; relevant settings platform declarations; the framebuffer extension/mixin and vanilla framebuffer bind/unbind implementation. They corroborate the service separation, startup sites and foreign-framebuffer distinction without proving behavioral equivalence to the old source revision. Cleanroom was skimmed only for CleanMix bootstrap/service/hook layout; this newer source is not proof about the pinned runtime. The published Khronos ARB_geometry_shader4 specification was read at its native parameter, input primitive, output-limit and OpenGL 3.2 interaction rules.

Receiving contracts were followed rather than inferred from the new producer rows: P4 §§4.7–4.8/5.2 for native configuration and fallback; P2’s source-free inspection adapter and R4B consumption; P7’s loader/bootstrap, fullscreen executor and prepared-submission contract; P10’s client/VBO/list adapters; P8’s prepared-shadow adoption. The P4 §11.5 contract incorporated by P1 was read in full. P3/P13 observations refer to the inspected pre-U1-amendment snapshots, not concurrent owner edits.

During this review Main relayed the maintainer’s explicit narrow U1 ruling: numeric discriminators and `.mcmeta` only, removing the unspecified filter/wrap property-key suffix requirement. This review leaves P1’s input unchanged, does not characterize that policy as still awaiting a decision, and does not certify the concurrent P3/P13 amendments or their future verification.

Historical reviews 24 and 25 were read, including R24 resolutions. R25 was read before the complete target pass, so this review does not claim a findings-blind reading order. Neither review supplied a current authority override or a PASS shortcut; the complete current target and required authority were independently checked. Reviews 1–23 were not freshly reread in full and are not claimed as fresh inputs.

## 1. Findings

### R26-1 — Route native geometry programs to compatible draw primitives

**Severity:** correction. **Priority:** P1. **Location:** `docs/phase1/v14/PHASE_1_DOC.md:3479–3482`, together with incorporated `DrawService.fullscreenQuad()` at lines 3166–3170 and the receiving P7 §4.6 step 8.

The newly granted native route fixes `GL_GEOMETRY_INPUT_TYPE_ARB` to `GL_TRIANGLES`, but the existing fullscreen consumer still chooses QUADS whenever supported. The governing compatibility profile supports QUADS, and neither the new grant nor its mandatory P4 transaction changes that dispatch. The published [ARB_geometry_shader4 specification](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_geometry_shader4.txt), §2.16.1 and Errors, allows a TRIANGLES-input geometry shader only with TRIANGLES, TRIANGLE_STRIP or TRIANGLE_FAN; GL_QUADS produces INVALID_OPERATION. Thus, after the separately required native-preserving source grant, a valid legacy composite/deferred geometry program can compile and link, pass the new pre-link checks, and still fail at its first specified fullscreen draw. [INFERENCE] P7 then takes draw-failure containment rather than executing the supported pack pass. Recorder configuration/link success cannot detect this missing primitive compatibility rule.

**Required correction:** close the new route’s draw-side contract as well as its pre-link contract. For the P1-owned fullscreen primitive, select an existing triangle-compatible implementation when the active linked geometry program requires it, and synchronize P7’s QUADS-preference sentence through the owner/consumer amendment process. State how the backend obtains that selection without a per-draw raw-GL query or source rescan. Add a planned behavioral case that reaches the actual draw after native configuration/link on a compatibility profile where QUADS is available. Also record the corresponding primitive-compatibility obligation for native vanilla submissions; P10 currently excludes triangle conversion, so silently assuming terrain/list conversion would be another ungranted contract, not a fix. This finding does not authorize a chunk-renderer rewrite, changing the observed legacy topology, dropping `.gsh`, or declaring the native source gate closed.

The repair is bounded to the native route’s primitive-selection/receiving obligations; it does not require rebuilding Phase 1’s module architecture or facade.

## 2. Checked and clean

- All thirteen required sections are present and substantive. D-1…D-10 have explicit dispositions; the remaining phase work is named rather than silently owned by P1. OQ-2/12/20/21 retain question/procedure/decision/fallback treatment. Planned experiments remain unperformed, not fabricated successes.
- The package grants preserve the engine-policy/platform-adapter/dumb-hook split. R7-8 was already granted; D-P1-43 adds only the missing Phase 13 texture homes. No new conformance-to-mod edge is granted.
- The legacy configuration enums, exact positive count, private origin/liveness/pre-link checks, native extension requirement, three native setters, immediate drain/abort transaction, queued dispatch errors and recorder event are mutually consistent. GL 3.2 alone is correctly not treated as ARB extension support. Output-count and link limits are not guessed from a missing profile field. P4 explicitly receives the operation and cleanup/fallback protocol; P3 native source preservation remains a separate gate. The draw-side defect in R26-1 is not a claim that those compile-time checks are absent.
- D-P1-45 grants diagnostic-type consumption without inventing coordinates in EngineDiagnostic or authorizing args/detail in goldens. The receiving P2 adapter uses P3’s attributed source-free projection, preserves the acquisition/archive-provenance distinction, and does not turn a source-bearing configuration into golden text.
- The loader-stage amendment is accepted in P7’s current H-BOOT-01 event row, not an unhandled new event or reinstated GameSettings CORE Mixin. GL-ready remains H-BOOT-02 RETURN. First-hook refmap/JAVA_8-versus-Java25 verification is explicitly acknowledged by P7. No runtime ordering or injection result is claimed here.
- D-P1-47’s N adjacent prepared submissions, IDs 0…N−1, effective-provider count, separate main/shadow authentication, saved-parent restoration, list-capture exclusion, single-wrapper protection and failure containment have actual P7/P10/P8 receiving clauses. The contract does not accidentally repeat world traversal or claim a new facade instanced-draw operation. Its ordering is recorded as the maintainer’s decision, not reference-observed behavior.
- The source check still supports keeping ordinary foreign textures distinct from owned storage and authenticated borrowed depth. The vanilla framebuffer bind is not equivalent to binding framebuffer zero. Current reference source was not mistaken for an implementation permission to copy the reference’s renderer policy.
- Historical review authority is preserved. R25 covers an earlier surface; subsequent binding §5 amendments are not certified by it. This review does not clear dependent implementation or final integration.

## 3. Additional readiness observations, not new patch findings

These observations must remain visible to the scheduled prerequisite/build work. They are not charged as newly introduced defects in §§0.24–0.27 and do not inflate the correction count.

1. **Current executable pins versus historical P1 pins.** BUILD and executable files now specify Gradle 9.7.0, Unimined 1.4.36-kappa and Cleanroom 0.6.10-alpha. P1 §4.2.6, the root build example, loader setup/OQ text and the PINS creation instruction still carry the honestly dated July values 9.6.1/1.4.26-kappa/0.6.6-alpha; the old explicit sponge-mixin compileOnly row is also absent from the current dependency script. BUILD expressly makes the executable checkout authoritative. Preserve the July evidence and do not downgrade or re-date it; reconcile the active architecture/build instructions during the authorized foundation prerequisite work. No latest-release upgrade or fresh Maven availability claim is made by this review.
2. **The retained extra script is no longer inert.** P1 §4.2.2 applies `gradle/scripts/extra.gradle` only at the source-set-free aggregator. The current script contains the documented Buildship integration, including `sourceSets.main.compileClasspath` and `tasks.named('generateJavaTemplates')`. [INFERENCE] Applying that script to an Eclipse-imported aggregator without those objects fails and cannot supply the mod’s Minecraft/generated-source model. The upcoming split must preserve this current repository feature in the project that actually owns Unimined/Blossom rather than mechanically copying the historical root-only placement. This is current-template migration work, not a claim that a build was run.

The explicit P3 jcpp build/pin/seam request remains undisposed in the inspected P1 contract; the existing THIRD-PARTY mechanism is not that grant. Native-preserving source output and other new P10/P14 requests also remain separately owned prerequisites. Their deliberately open status is not treated as a newly introduced silent-drop bug, and this review grants none of them. U1 is subject to the newly relayed approved scope correction and subsequent owner/consumer amendments, not the old pending-policy characterization.

## 4. Verdict

# PASS-WITH-CORRECTIONS

Counts: blocking=0; corrections=1; notes=0.

Interface changed: yes — the required correction affects the native geometry contract and its incorporated draw-side semantics/receiving obligations. No interface was edited by this review.

The complete current Phase 1 architecture has one admitted bounded correction: the newly granted TRIANGLES-input native geometry path does not reconcile its receiving QUADS draw dispatch. Its compile-time transaction and recent diagnostic/package/prepared-submission grants were followed through their consumers rather than assumed correct. Structural rebuild is not warranted.

Phase 1 remains unverified under §G1.3. Apply the correction in a separate authorized fix-up, preserve this review and all prior history, reconcile the separately scheduled prerequisites, and obtain a fresh independent whole-document literal PASS before verified downstream consumption. This is not §G5.3 final integration clearance or evidence that any shader has compiled, linked or rendered.

## Resolutions

**Separate authorized fix-up, 2026-09-07; not reviewer re-verification.**

- **R26-1 — addressed in owner architecture, verification pending.** P1 §0.28 /
  D-P1-48 / §4.7.4a and binding §5.2 keep `fullscreenQuad()`'s signature and require
  the retained triangle strip under a TRIANGLES-input linked executable even on
  QUADS-capable contexts. Backend effective-link metadata covers native and ordinary
  core geometry, including GL3.2 source-layout precedence, with link-time acquisition,
  selection/deletion/restoration/unknown-state lifecycle and no per-draw query or
  source scan. Incompatible input draws nothing and follows existing failure containment.
  Recorder scripts/events and §8/§12 acceptance now cover compatibility-profile dispatch,
  native layout override, failed link/use/draw, lifetime and predecessor restoration.
  P7's exact receiving amendment is requested in §11.4, not claimed applied here.
- **Native vanilla primitives remain a distinct obligation.** During this fix-up the
  maintainer explicitly approved the narrow conditional quad-to-triangle adapter recorded
  in `docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md`. P1 §11.4 records that authority,
  earliest affected-support milestone and P7/P10 adoption/evidence gates without treating
  policy as still awaiting approval. Fullscreen correctness is not terrain/list correctness.
  No renderer rewrite or arbitrary topology conversion is granted.
- **Readiness observation 1 — active build instructions reconciled, history preserved.**
  D-P1-51 / §4.2.6a retain the current exact wrapper/plugin/loader values
  9.7.0/1.4.36-kappa/0.6.10-alpha and current absence of an explicit sponge-mixin row.
  The July table remains dated evidence. File inspection additionally distinguishes the
  still-9.6.1 CI setup inputs from their wrapper-driven build commands; implementation must
  align setup inputs. No release lookup, resolution result or pin upgrade is claimed.
- **Readiness observation 2 — Buildship placement corrected in architecture.**
  Current `extra.gradle` moves to the real `:mod` Unimined/Blossom owner after its
  source-set/template-task setup; root IDEA configuration stays root-owned. Lazy Eclipse
  classpath and generated-source synchronization are preserved. No build was run.
- **Separately commissioned grants, not invented R26 findings.** D-P1-49 publishes P3's
  exact pure-JVM jcpp implementation-time pin/closure/license/packaging rule in §5.1.
  D-P1-50 publishes P10 R10-1 exact packages, bounded vertex-input source/bind/restore/
  recorder contract and early class-only compatibility mechanism. Consumer adoption,
  source-schema completion and lifecycle/topology integration remain separately owned.
  U1's approved documented-mechanism correction is reflected in active handoff ledgers;
  no texture defaults or public option/macro/session/locale contracts change.

Only P1 and this append-only resolution section were edited by this fixer. No code,
build, test, linter, formatter, validation command or runtime/conformance experiment was run.
The original review, count and PASS-WITH-CORRECTIONS verdict above remain unchanged.
All changed §5 surfaces are **unverified** pending fresh independent whole-document
review and final integration; this resolution is neither PASS nor implementation clearance.

**Same fix-up owner/receiver coordination:** P4 additionally requested a precise producer
surface for comparing its P3-derived category to the actual linked result before publication.
D-P1-48 now includes `ShaderService.linkedGeometryInput(ProgramHandle)` returning cached
`Optional<LinkedGeometryInputPrimitive>` with exact no-geometry/invalid-lifetime semantics,
no GL query, and a recorder event. This does not widen native pre-link configuration or
give P7/P10 private active-program access. It is included in the same fresh-review gate.