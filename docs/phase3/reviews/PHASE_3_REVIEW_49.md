## 0. Method and reading order

I independently re-derived every surviving candidate before consulting historical reviews. The
load-bearing reads were:

1. `docs/phase3/v1/PHASE_3_DOC.md` as a whole, with focused comparison of §0's actual-input
   ledger, §2.2's canonical declarations, the §3 conformance rows, §§4.3–4.9, the complete
   manifest-declared §5 interface region, §8 tests, §11 decisions/requests, and §12 checklist;
2. `docs/design/v3/DESIGN.md` Part I, including the verification and dependency-contract rules,
   §G9 mandatory template, §G11 provenance rules, the Phase 3 target specification, and the doc
   gate selected by this round's override;
3. `docs/research/v1/RESEARCH.md` as contract ground truth, especially §§3.5, 3.6.8, 3.7 and
   Appendix F.5;
4. the binding §5 contract of `docs/phase1/v14/PHASE_1_DOC.md`; and
5. the permitted Pintonium report where it bears on option rewriting and standard macros.

I searched the whole target for equivalent numeric formatters, tag/resource-identifier grammar,
custom-texture declarations, and row-local Pintonium provenance. I settled each candidate's
interpretation, severity, and interface classification from the current bytes and authorities.
Only afterward did I examine the discovered prior reviews 1–48, in round order and including their
resolution history, to test whether the same premise had already been settled. Prior reviews were
historical evidence, not authority over the current target.

There were no deviations from the resolved source contract, no network use, and no agent fan-out.
I did not invoke `$verify-loop`, run a verification harness, start another session, or read a
forbidden transcript, `docs/**/chatlogs/**` path, or `*.txt` source. Candidates 002 and 007 were
eliminated before adjudication and were not revived. The Gate reported no drops.

## 1. Findings

### candidate-001 — The actual-input ledger omits the RESEARCH section expressly checked for D-P3-47

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:11-21,42-46,2145-2148,3195`.
- **Claim:** The mandatory header must list every RESEARCH section actually read and explain any
  deviation from the assigned reading list.
- **Evidence:** Section 0.1 presents a section-level RESEARCH inventory but omits §3.6.8
  (`PHASE_3_DOC.md:19-21`) and then says the fix-up additionally used only the listed v3 override
  selectors (`:42-46`). The current design nevertheless derives the modern tag risk, 1.12 shim,
  and entries-before-tags rule from RESEARCH §3.6.8 (`:2145-2148`), and D-P3-47 expressly says the
  decision was made “after checking” that section (`:3195`). Section G9 requires the header to name
  inputs actually read, including RESEARCH sections, and give reasons for deviations
  (`docs/design/v3/DESIGN.md:824-826`). This is substantive use, not an incidental bibliography
  reference.
- **Severity:** correction. Add RESEARCH §3.6.8 to §0.1 and identify it as an additional read needed
  to assess the tag-shim/priority premise and D-P3-47. Keep the ledger factual: add only material
  actually read and give the required deviation reason.
- **Touches interface/change-trigger region: no.** The ordered repair is confined to §0 input
  provenance and need not change §5 or parser semantics.

### candidate-003 — Accepted numeric aliases have no exact canonical macro replacement

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1695-1710,2376-2383,2867-2874`.
- **Claim:** Every accepted multiplier and antialiasing spelling must project to one byte-exact,
  testable shader-macro replacement.
- **Evidence:** The raw contract accepts many aliases for one numeric value: the three multipliers
  use a signed decimal/exponent grammar parsed to a finite positive Java `float`, and
  `antialiasingLevel` accepts signed and zero-padded decimal integers (`:2376-2383`). The emission
  rule then promises only “canonical decimal spelling” and “canonical-base-10-integer”
  (`:1695-1700`). No `Float.toString`, `Integer.toString`, equivalent algorithm, fixed/scientific
  threshold, exponent convention, or expected byte vectors appear in the target. Parsing selects a
  primitive value but does not select its serialization: preserving a token, Java's formatter, a
  plain-decimal formatter, and other shortest-round-trip formatters can disagree while remaining
  plausibly “canonical.” The generic `macro_optionFamilyMembershipAndValues` test name (`:2870-2873`)
  supplies no oracle. RESEARCH makes these pack-branched option macros contract-visible
  (`docs/research/v1/RESEARCH.md:313-319`), and the governing Phase 3 specification owns the
  standard macro header (`docs/design/v3/DESIGN.md:1375-1388`).
- **Severity:** correction. Define the exact post-parse serialization operation for each positive
  finite Java float and nonnegative Java integer, including scientific-notation thresholds,
  exponent case/sign, decimal point, and trailing-zero behavior. Add byte-exact equivalence and
  boundary tests for aliases such as `1`, `1.0`, `1.`, `.1e1`, and `+01e0`, plus signed/zero-padded
  integer aliases. Update the incorporated macro row as the target's own change rule requires.
- **Touches interface/change-trigger region: yes.** Macro replacement strings are published in
  `MacroConfiguration` to Phase 4 materialization, and `PHASE_3_DOC.md:2408-2411` requires a §5.1
  update for a consumer-visible semantic completion.

### candidate-004 — The mandatory v0.1 tag parser still relies on an undefined identifier predicate

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1411,2145-2148,2182-2190,2303,2955-2967,3277-3279,3357-3360`.
- **Claim:** D-P3-47 must provide an executable phase-local `%` syntax if v0.1 is required to parse,
  classify, canonicalize, and reject tag selectors.
- **Evidence:** RESEARCH §3.6.8 defines only the need for a 1.12 shim and entries-before-tags
  priority (`docs/research/v1/RESEARCH.md:439-448`). The target properly labels `%` as a local
  interpretation, but its actual predicate remains “a valid lower-case short name or
  `namespace:path`” (`PHASE_3_DOC.md:2185-2189`). No named inherited validator or exact character
  production defines punctuation, slash placement, colon count, empty components, ASCII versus
  non-ASCII, or case handling. The target itself asks authority to standardize the spelling,
  canonicalization, and rejection grammar (`:3277-3279`) while unconditionally scheduling v0.1
  entry/tag classification and all `idMap_*` tests (`:2955-2967,3357-3360`). Phase 9 owns
  membership expansion and registry resolution, not Phase 3's earlier lexical classification
  (`docs/design/v3/DESIGN.md:1440-1443`). Round 48 settled attribution of the choice as local; it
  did not supply the missing lexical production.
- **Severity:** correction. Either define one exact provisional identifier grammar now—namespace
  and path character classes, colon/slash and empty-component rules, case policy, short-name
  expansion, stored canonical form, and boundary tests—while retaining the ratification request,
  or defer/disable tag parsing and align the schema, handoff, tests, and checklist. Do not leave a
  mandatory parser operation dependent on an unnamed validity predicate.
- **Touches interface/change-trigger region: yes.** D-P3-47's tag rules are part of the binding
  `IdMappingInput`/`IdMappingParser` contract consumed by Phase 9 (`:2303`); completing or deferring
  their observable acceptance semantics requires a monitored §5 update.

### candidate-005 — Section 5 does not bind the complete option-macro projection it publishes

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1690-1707,2304-2307,2331-2335,2376-2383,2408-2411`.
- **Claim:** The declared §5 dependency contract must itself bind the producer-owned option-macro
  mapping, defaults, and emission/omission semantics consumed through `MacroConfiguration`.
- **Evidence:** Detailed §4.4 maps all eight engine-option keys to macro names, gives Boolean and
  positivity gates, and fixes all eight missing-key defaults (`:1690-1707`). The §5 macro row says
  only that the eight-name family exists, six members are effective at v0.1, and the two companion
  macros have capability gates (`:2304`). It does not bind the other six key-to-name projections,
  their always/true-only/positive-only rules, the eight defaults, or the resulting replacement
  values/order. The neighboring `EngineOptionData` contract defines accepted input text
  (`:2376-2383`) but not this distinct projection. Section 5 nevertheless claims exact defaults and
  semantics are part of its rows (`:2331-2335`). The governing dependency rule says consumers build
  against what a dependency exposes in §5 (`docs/design/v3/DESIGN.md:295-298`), and packs directly
  branch on the produced macros (`docs/research/v1/RESEARCH.md:313-319`). Opaque list consumption
  does not make the produced names, omissions, order, and replacement strings unobservable.
- **Severity:** correction. Amend §5.1's macro contract, or add an immediately adjacent binding
  paragraph, to state or explicitly incorporate the complete ordered key-to-macro projection,
  every emission/omission gate, and all eight missing-key defaults. Incorporate the exact numeric
  serialization supplied by candidate-003 rather than copying the present ambiguous phrase. Keep
  §4.4 and the binding contract synchronized and state the canonical baseline expected from the
  Phase 7/12 input path.
- **Touches interface/change-trigger region: yes.** This correction directly changes the
  manifest-declared §5 cross-phase contract. It is distinct from candidate-003: candidate-003
  supplies a missing algorithm even in detailed design, while this finding places the complete
  resulting projection in the sole dependency contract.

### candidate-006 — The custom-texture handoff is not a closed nominal Java type algebra

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:396-399,607-608,724-734,1941-1948,2032-2044,2310-2312,2436-2467`.
- **Claim:** Phase 13 must be able to implement against canonical public texture-spec declarations
  without inventing types or deciding whether raw and color-attachment formats share one enum.
- **Evidence:** Section 2.2 calls its declarations the canonical public shapes (`:396-399`) and
  `ShaderPropertiesModel` directly exposes `CustomTextureSpec` and `NoiseTextureSpec` nominal types
  (`:724-734`), but that canonical block declares neither sum nor the raw component domains. The
  binding prose later names `TextureBindingKey`, a sealed `CustomTextureSpec`, `NoiseTextureSpec`,
  `TextureTarget`, `InternalTextureFormat`, `PixelFormat`, and `PixelType` only as prose/pseudo-shapes
  (`:2436-2467`). The only concrete 37-value enum is `ColorInternalFormat` (`:1941-1948`), while
  `Raw` names the undefined `InternalTextureFormat`. Section 4.8 says raw textures and colortex
  directives use the same 37-value internal-format enum (`:2032-2044`), which does not resolve
  whether the differently named Java type is a typo, a missing distinct enum, or an impossible
  alias. The §5 row names only five top-level types and still calls the algebra closed (`:2310-2312`).
  Prior Round 28 added semantic prose, but its resolution assertion is not borne out by complete
  canonical nominal declarations; later rounds established the same closure standard for other
  public leaves.
- **Severity:** correction. Add canonical public declarations for `TextureBindingKey`, sealed
  `CustomTextureSpec` and all variants, sealed `NoiseTextureSpec` and its variants, `TextureTarget`,
  `PixelFormat`, and `PixelType`, retaining the already declared `TexturePropertyStage` and
  `TextureSidecarRef`. Resolve `Raw.internalFormat` explicitly—prefer the existing
  `ColorInternalFormat` if “same enum” is literal, otherwise declare and justify a distinct type
  and its relationship. Bind every exposed leaf in §5.1, add nominal producer/consumer compatibility
  tests, and apply §5.3's schema discipline to the selected shape.
- **Touches interface/change-trigger region: yes.** These are Phase-13-facing declarations and
  component identities incorporated into the monitored texture row.

### candidate-008 — Two Pintonium-derived conformance rows omit mandatory row-level provenance

- **Location:** `docs/phase3/v1/PHASE_3_DOC.md:1294,1407,1548-1554,1712-1719,3153,3157`.
- **Claim:** A conformance row that adopts or rejects a Pintonium mechanism must carry its PD
  citation and, for contract-visible behavior, its recorded contract-check decision.
- **Evidence:** G9 states this as a row-local requirement (`docs/design/v3/DESIGN.md:831-835`), and
  G11.4 requires a recorded decision for contract-visible adoptions
  (`docs/design/v3/DESIGN.md:943-951`). The compile-time option-application row cites only the
  contract and tests (`PHASE_3_DOC.md:1294`), although detailed design identifies Pintonium's
  location-aware `OptionAnnotatedSource` rewriting and D-P3-4 (`:1548-1554`), with PD §7.3 as the
  supporting mechanism (`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md:419-428`). The Standard
  macro row likewise omits PD §7.6 and D-P3-9 (`PHASE_3_DOC.md:1407`), even though detailed design
  adopts the reference's version/parser shape and rejects enumerate-everything extensions
  (`:1712-1719`), and D-P3-9 records that contract-visible disposition (`:3157`). The correctly
  repaired profile row does not cover the separate OptionAnnotatedSource mechanism.
- **Severity:** correction. Add PD §7.3, the verified observed `OptionAnnotatedSource` provenance,
  and D-P3-4 to the compile-time option-application row. Add PD §7.6 and D-P3-9 to the Standard
  macro row, stating the adopted version/parser shape and rejected enumerate-all policy. Verify and
  correct the detailed observed-source tag: PD §7.6 identifies the StandardMacros mechanism, while
  the current target tags `IrisDefines.java` for that sentence.
- **Touches interface/change-trigger region: no.** The substantive macro and rewrite behavior
  already exists; this orders row-local provenance/disposition repair, not a §5 edit.

## 2. Checked and clean

- The finder-reported clean areas were rechecked. The current texture filter/wrap suffix policy is
  consistently an authority-gap diagnosis rather than destructive normalization; schema 12 and its
  compatibility tests are internally repeated; the global-codec key/value validity, duplicate
  overlay, escaping, and exact-output rules are coherent apart from the separate macro projection
  defects above; resource signed-zero canonicalization remains producer-only; and the closing
  ledger correctly says a fresh review is pending.
- The selected Phase 1 contracts used here are honest: the module/seam rules,
  `GLCapabilityProfile`, fixed logging/diagnostic channels, debug flag, and notice mechanism exist
  in the binding dependency region. The absent jcpp build pin remains requested rather than
  assumed.
- The Appendix F.1 ownership map, remaining Appendix F and Appendix A.3 mappings, B1/B2/B3/B12
  pitfall tests, discovery, dimensions, include ordering, persistence, program state, resource
  aggregation, OQ-7 spike, pure-`:engine` placement, and thirteen mandatory sections showed no
  additional candidate-backed finding.
- `candidate-002` (null global-read request result) remains eliminated at Refute. The current §5
  matrix defines invalid-request fallback to the valid supplied baseline or canonical empty when
  the baseline is invalid, so it was not revived.
- `candidate-007` (missing ID-map preprocessing conformance) remains eliminated at Refute. The load
  pipeline, §3 ID-map rows, §4.9 parser path, standard-A–G macro restriction, tests, and checklist
  provide equivalent preprocessing coverage.
- Historical resolutions do not clear the admitted findings. Round 40 required option-macro
  replacement representation but resolved it only as “canonical finite decimals”; Round 48 made
  `%` syntax an explicit local decision but did not define its identifier production; Round 28's
  texture prose did not reconcile the current nominal `InternalTextureFormat`/
  `ColorInternalFormat` mismatch; and Round 42's profile correction confirms rather than waives
  G9's row-local provenance rule.
- No surviving candidate was dropped on independent derivation, and no finding was created outside
  the supplied surviving candidate set.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=6; notes=0
Interface changed: yes

All six admitted findings are bounded provenance, algorithm, parser-contract, or public-interface
repairs. None requires rebuilding Phase 3's architecture, so `FAIL` is not warranted; six live
corrections make literal `PASS` unavailable.

The recent trend is not converged. Corrections moved 5 → 2 → 4 → 5 in Rounds 45–48 and rise to 6
in Round 49; in particular, the latest three rounds increase 4 → 5 → 6. Prior literal passes and
resolved broad findings do not override defects present in the current bytes.

The next required action is a scoped fix-up resolving candidates 001, 003, 004, 005, 006, and 008
and recording their resolutions in this review. The fix-up must correct the input ledger, close
numeric macro serialization and the binding option-macro projection, make the local tag grammar
executable or defer it consistently, complete the public custom-texture type algebra, and repair the
two row-local Pintonium provenance cells. Because candidates 003, 004, 005, and 006 require edits to
the manifest-declared `cross-phase-interfaces` region, the change trigger fires and a fresh
whole-document verification round is required before Phase 3 may close or be consumed as a verified
dependency.

## Resolutions

- **candidate-001 — applied.** Section 0.1 now lists RESEARCH §3.6.8 and states why the fix-up
  additionally read it: to assess the modern tag-shim/entries-before-tags premise and D-P3-47.
  The surrounding deviation statement now reports both additional authority reads without
  broadening the ledger to material not read.
- **candidate-003 — applied.** D-P3-48 and §§4.4/5.1 define multiplier replacement as Java 25
  `Float.toString` after accepted-float parsing and FXAA replacement as `Integer.toString` after
  accepted-integer parsing. The notation thresholds, exponent/decimal/trailing-zero rules, alias
  equivalence, zero omission, integer normalization, and byte-exact boundary vectors are explicit.
- **candidate-004 — applied.** D-P3-49 and §§3.5/4.9/5.1 now bind one provisional executable tag
  grammar: lower-case ASCII namespace/path classes, exact colon and slash structure, no empty or
  dot segments, rejection rather than case folding, short-name expansion to `minecraft:`, and the
  stored canonical form. Boundary tests are named; the upstream ratification request remains.
- **candidate-005 — applied.** The monitored macro row and adjacent binding text now state the
  ordered eight-key projection, every always/true-only/positive-only/capability gate, every
  missing-key default, the Phase 7/12 canonical baseline, exact replacement strings, omissions,
  and family order.
- **candidate-006 — applied.** Section 2.2 now canonically declares `TextureBindingKey`, both sealed
  texture sums and variants, `TextureTarget`, the shared 37-value `ColorInternalFormat`,
  `PixelFormat`, and `PixelType`. `Raw.internalFormat` uses `ColorInternalFormat`; §5 binds every
  leaf and names producer/consumer compatibility coverage. The nested component-type closure
  advances the configuration schema from 12 to 13 with both rejection directions.
- **candidate-008 — applied.** The compile-time option row now cites PD §7.3, observed
  `OptionAnnotatedSource`, and D-P3-4. The Standard macro row now cites PD §7.6, observed
  `gl/shader/StandardMacros.java`, and D-P3-9, including adoption of the version/parser shape and
  rejection of enumerate-all extension emission; the detailed stale `IrisDefines.java` tag is
  corrected to the same observed source.

The edits to ID-map acceptance, macro publication, texture declarations, and schema/version
discipline intentionally change the manifest-declared `cross-phase-interfaces` region. Its change
trigger fires, so Phase 3 remains unverified pending a fresh whole-document verification round.

### Notes deferred

None. The adjudication admitted zero notes, so no note was applied or deferred.
