# Phase 1 — R33 independent whole-owner architecture review

## Frozen owner and disposition

- **Owner:** `docs/phase1/v14/PHASE_1_DOC.md`, frozen attempt 6.
- **Frozen SHA256:** `9354727aaa47329fdc6003b3d234a70b41e625787ed4a5261927e1bdcaced71a`, as recorded in `docs/build/reviews/ARCHITECTURE_REVIEW_ATTEMPT_6.json`.
- **Scope:** the complete current owner, §§0–12, including historical/current precedence, foundation/build/seam contracts, every facade service, recording, threading, failure containment, staging, decisions, and implementation checklist—not only D-P1-63–65.
- **Result:** one substantive owner correction; one nonblocking source-confidence note. This is not implementation, native-GL, pack-tier, or sibling certification.

## Authority and independent checks

The governing design is **`docs/design/v2.0-RC2/DESIGN.md`**, selected by the owner's header at `docs/phase1/v14/PHASE_1_DOC.md:12–18`, not the globally newest design. Reviewed its governing Part I and full Phase 1 assignment at `docs/design/v2.0-RC2/DESIGN.md:957–1067`, the relevant governing `docs/research/v1/RESEARCH.md` sections and appendices, and Phase 1's assigned Pintonium architecture/platform material and standing divergence/bug constraints. `docs/MOVES.md` was used to distinguish moved references and historical revision coordinates. Prior reviews were not used as proof.

Phase 1 has no dependency owners (`docs/phase1/v14/PHASE_1_DOC.md:5201–5203`). Its reciprocal receivers were nevertheless checked at their actual consuming paths, not merely at producer declarations:

- **Synchronous target-bearing values:** traced D-P1-63's four-target representation, canonical unused axes, mip-count and region rules, target-specific image/subimage dispatch, scalar versus packed byte counts, synchronous borrowed-buffer lifetime, unpack/PBO restoration, native-error containment, and preserved depth-copy semantics (`docs/phase1/v14/PHASE_1_DOC.md:4376–4464`). P5 incorporates the exact values and owns format legality (`docs/phase5/v1/PHASE_5_DOC.md:1395–1464`); P13 supplies explicit conversions for raw 1D/2D/3D/rectangle, decoded images, generated noise, companion defaults/atlas levels, animation regions, and foreign-live exclusions (`docs/phase13/v1/PHASE_13_DOC.md:1006–1057`). P2 explicitly receives metadata-only recording evidence (`docs/phase2/v2/PHASE_2_DOC.md:2651–2658`). The remaining target-limit input defect is C33-1 below.
- **Complete parameter baseline:** the closed parameter domains and owner conversions at `docs/phase1/v14/PHASE_1_DOC.md:4285–4373`, together with D-P1-64 at `4466–4475`, agree with the receiving native-state/cache commitment and ordinary fixed-function clearing routes at `docs/phase14/v1/PHASE_14_DOC.md:627–645` and `707–736`. A successful setter must establish the complete object baseline, not just base/max/swizzle. Borrowed objects remain outside this mutation permission; partial establishment cannot authorize drawing. This remains an architecture contract, not a measured sampler optimization.
- **Exact-current schema:** D-P1-65 at `docs/phase1/v14/PHASE_1_DOC.md:5270` agrees with P3's exact schema23 containing/nested/inspection equality and `MaterializedSource-v23` rejection-before-derivation law at `docs/phase3/v1/PHASE_3_DOC.md:4154–4158,4197–4216`. Earlier numeric receipts are historical. No new P1 parser, asset capability, projection tree, or optional API follows from the receipt.
- **Other load-bearing foundation receivers:** checked P4's linked-input comparison and total sampler-initialization dispatch, including restored versus unproven-restoration failures (`docs/phase4/v1/PHASE_4_DOC.md:1410–1451`); P7's concrete cancellable alpha/blend hooks and effective-state publication (`docs/phase7/v1/PHASE_7_DOC.md:1676–1717`); P10's actual binding result, rollback, one-close restoration, generic-zero isolation, capture allowlist, and pointer-free replay receipt (`docs/phase10/v1/PHASE_10_DOC.md:1714–1767`); and P2's positional draw-buffer, error-attribution, profile-serialization, and recorder consumption (`docs/phase2/v2/PHASE_2_DOC.md:2584–2622`). No additional required Phase 1 correction was established in these paths.
- **Foundation structure and migration:** checked the current module/package and C-1–C-4 obligations, mod-only platform/tooling placement, current-pin override rather than restoration of July examples, lifecycle/bootstrap split, real early MOD veto, logging/diagnostic projection, source-free conformance boundary, and explicit separation between mandatory operations and still-ungranted modernization requests. Current root build files corroborate the active wrapper/plugin/loader migration posture; no build or dependency-resolution result is claimed.

## Required correction

### C33-1 — Publish target-specific texture limits for the new admission contract

**Severity:** P2, substantive capability/recording contract defect. **Owner:** Phase 1. **Patch anchor:** `docs/phase1/v14/PHASE_1_DOC.md:4445–4449`; related new identical-recorder obligation at `4453–4457`.

D-P1-63 now requires target-aware capability/limit rejection before mutation and identical admission in the recording backend. However, the complete `GLCapabilityProfile` declaration at `docs/phase1/v14/PHASE_1_DOC.md:2914–2926` and its serialized representation at `2955–2979` expose only `maxTextureSize`. Neither supplies the independently queried **3D** or **rectangle** maximum. The recorder's complete construction inputs are the profile, scripted responses, and optional log (`3968–4008`); its scripts provide no missing target-limit input. P13's real consuming route explicitly requires checking every dimension against target-specific capability limits before allocation (`docs/phase13/v1/PHASE_13_DOC.md:977–983`).

These limits cannot be derived from `GL_MAX_TEXTURE_SIZE`, the GL version, or extension presence. Khronos' [glGet reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glGet.xhtml) exposes `GL_MAX_3D_TEXTURE_SIZE` and `GL_MAX_RECTANGLE_TEXTURE_SIZE` separately; [ARB_texture_rectangle, issue 12](https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/extensions/ARB/ARB_texture_rectangle.txt) explicitly says rectangle limits may differ from standard texture limits. Consequently, a raw 3D or rectangle extent between the target-specific maximum and the published ordinary maximum cannot receive the promised correct pure preflight/identical recorder result: using the ordinary maximum admits an invalid target allocation, while a guessed conservative bound rejects supported input. A private live-driver query alone does not repair the receiver/profile-replay boundary.

**Minimal owner fix:** extend the captured capability value and its stable text serialization with the required target-specific limits, define capability-gated probe/unsupported-target representation, and specify which limit each allocation target consumes. Incorporate this into the profile and D-P1-63 §5 rows, recorder admission, and planned boundary evidence. Coordinate P2 profile capture/replay and P13 pure preflight receipts; do not replace the missing data with a guessed constant, engine-side GL query, or post-allocation driver failure. This correction changes **§5** and therefore requires fresh affected owner/receiver review.

## Nonblocking note

### N33-1 — Preserve historical pinned-source confidence separately from current corroboration

The exact historical `reference-src/pintonium-9c2fcc1/` files cited at `docs/phase1/v14/PHASE_1_DOC.md:39` are unavailable in this checkout. The corresponding permitted files under `reference-src/Pintonium-main/` are available and were independently read: `MinecraftVersionShimService.java`, `GLStateManagerService.java`, `RenderSystemService.java`, and `CeleritasVintageMixinPlugin.java`. They corroborate the service-seam inventory and class-enumeration mechanism, but do **not** authenticate the historical commit or its old line numbers. Likewise, the live template is now at repository root; current root build files corroborate the active migration ledger rather than recreating July's nested `Schmaloogium/` checkout. Preserve that distinction. This is an evidence limitation, not a required architecture correction or a claim that permitted corroboration is unavailable.

## Limitations and final verdict

Read-only review only: no repository edits, formatter, linter, build, test, runtime execution, native capture, or pack run. No prohibited chatlog/transcript, Oculus pipeline/transform, relocated library, glsl-transformer implementation, or OptiFine decompile mining was used. Public Khronos documentation establishes API distinctions, not observed G6 parity. Unexecuted implementation/native evidence remains at its existing gate and is not itself counted as a defect here. Cross-owner receiver checks do not certify those complete owners.

**Final verdict: PASS-WITH-CORRECTIONS.** The architecture is not structurally rejected, but C33-1 must be corrected and the changed §5 capability/admission boundary freshly reviewed before Phase 1 can receive a literal PASS. Main retains integration and all mutation ownership.

## Resolutions

2026-09-08 architecture-only fix-up; original frozen identity, findings, confidence and
PASS-WITH-CORRECTIONS verdict above remain unchanged.

- **C33-1 — corrected in D-P1-66.** P1 §4.7.2 now declares captured
  max3DTextureSize/maxRectangleTextureSize and exact mandatory serialized keys,
  capability-gated positive-or-unsupported-zero probes, failed-capture nonpublication
  and strict parse/replay. §4.7.7a consumes each actual target maximum before mutation;
  §5 incorporates recorder/receiver admission, §6 removes fabricated conservative native
  profiles after probe failure, and §8/§12 carry distinct-maxima/axis-boundary cases.
  Khronos glGet and ARB_texture_rectangle issue12 distinguish these native maxima.
  P13 receives the actual pure-preflight contract under D-P13-41; Main owns P2's
  corresponding replay/fixture receipt. No guessed maximum or engine-side GL query.
- **N33-1 — preserved.** Historical pinned paths/commit confidence have not been
  rewritten as current corroboration; §0.33 explicitly retains that distinction.
- **Coordinated §5 changes, not extra R33 findings:** D-P1-67 grants P5 R43's exact
  typed clear/native tier/restoration/recorder contract (§4.7.4b), and D-P1-68 receives
  P10 D-P10-29 complete conventional participation/identity (§4.7.6). Both have live
  §5/§8/§12 incorporation and separately owned receiving changes.

Evidence is the published architecture and cited native specifications, not executed
native parity. No validation command, build, test, checksum, formatter or linter was
run. Fresh applicable owner/receiver review and Main's consolidated integration remain
required; this appendix supplies no replacement verdict or implementation clearance.
