# Oculus — Shader-Engine Analysis for the Schmaloogium Project

> **Status:** v1.0, 2026-07-26.
> **Role:** Mining report and evidence catalogue. This document records what can be
> learned from the gated Oculus tree; it cannot change Schmaloogium's contract.
> `docs/research/v1/RESEARCH.md` wins every conflict, without silent resolution.
> **Reading guide:** §1–§3 establish scope, provenance, method, and licensing. §4–§10
> group all citation-gated findings by subsystem; each ends with a
> **Relevance to Schmaloogium** block. §11–§16 record gaps, pitfalls, divergences,
> trust, and the complete evidence audit. §17 maps the evidence onto all 14 phases.

---

## 1. Orientation: what this tree is, and what it is not

The directory name and build properties suggest a 1.12.2 port. The source reality does
not support that interpretation. The build metadata says:

- `reference-src/Oculus-1.12/gradle.properties:7` — `minecraft_version=1.12.2`
- `reference-src/Oculus-1.12/gradle.properties:9` — `forge_version=14.23.5.2860`
- `reference-src/Oculus-1.12/gradle.properties:12` — `mappings_channel=stable`
- `reference-src/Oculus-1.12/gradle.properties:13` — `mappings_version=39`

The repository also describes itself as closed:

- `reference-src/Oculus-1.12/README.md:9` — `# !!!PROJECT CLOSED!!! ####`
- `reference-src/Oculus-1.12/README.md:11` — `# There will be no more works anymore #`

Those facts describe declared intent and repository status, not a working 1.12.2 hook
reference. The Stage 1 provenance gate found:

- git describe `1.16.x-v1.2.2-147-g22bdab676`, 147 commits after the named tag;
- a **current modern-marker union of 74 unique Java files**, not the commissioning
  brief's 76;
- an MCP-marker union of 12 files;
- 132 `@Mixin` annotations, with zero targets named `RenderGlobal`,
  `EntityRenderer`, or `Framebuffer`;
- 20 targets across the modern group `LevelRenderer`, `GameRenderer`,
  `RenderTarget`, and `ParticleEngine`;
- `mods.toml` metadata that says `javafml`, loader `[36,)`, and LGPL-3.0-only:
  - `reference-src/Oculus-1.12/src/main/resources/META-INF/mods.toml:1` —
    `modLoader="javafml"`
  - `reference-src/Oculus-1.12/src/main/resources/META-INF/mods.toml:2` —
    `loaderVersion="[36,)"`
  - `reference-src/Oculus-1.12/src/main/resources/META-INF/mods.toml:3` —
    `license="LGPL-3.0-only"`
- no `mcmod.info`, although the active build contains an expansion task for that name.

The discrepancy is substantive: the declared 1.12.2 build target overlays predominantly
1.16.5-facing source, modern Mixin targets, hybrid imports, and mismatched loader
metadata. Therefore:

1. Oculus is **not a usable 1.12.2 hook-point reference**.
2. No finding in this report is classified `1.12.2-hook`.
3. Every surviving finding is classified `loader-independent`.
4. Pintonium remains the working source for 1.12.2 hook-point evidence.
5. Build metadata is never promoted into proof that the source runs on 1.12.2.

Oculus remains useful as a loader-independent Iris/Oculus logic mine: pack parsing,
preprocessing, pass and buffer policy, shadow math, uniform models, GUI behavior, and
companion-texture policy. The distinction is the organizing rule for the rest of this
report.

---

## 2. Method, gates, and reproducibility

### 2.1 Workflow

The evidence passed through five explicit filters:

1. **Provenance/licensing gate.** Inventory the tree, identify excluded code and
   sub-licenses, and test the claimed port target against the actual source.
2. **Mining.** Eight bounded assignments produced **70 raw candidates**.
3. **Contract cross-check.** Candidates were checked against RESEARCH and DESIGN;
   **43 survived** and 27 were dropped.
4. **Citation Gate.** **67 anchors** were reopened: **54 exact**, 0 relocated,
   **13 failed**. The result was **37 findings kept / 6 dropped** and
   **3 conflict records kept / 2 dropped**.
5. **Synthesis.** Only Stage 4-kept findings and retained anchors appear as Oculus
   evidence below.

The initial brief claimed a modern-marker union of 76. The reproducible current result
was 74. This report records the discrepancy rather than forcing the tree to match the
brief.

### 2.2 Evidence rules used here

Every finding has:

- an ID retained by Stage 4;
- one or more exact repo-relative `file:line` anchors;
- the exact verbatim text at every cited line;
- classification exactly `loader-independent`;
- a contract cross-check against RESEARCH or DESIGN.

No failed quote was silently whitespace-normalized. Three ancillary anchors were omitted
by Stage 4 while their findings survived: `TX-05a`, `TX-07b`, and `TX-08b`. This report
uses only `TX-05b`, `TX-07a`, and `TX-08a` for those findings.

### 2.3 Inputs beyond the commissioned read list

The synthesis read these additional inputs, and only for the stated reasons:

- `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` — front matter, section shape,
  comparative trust language, and the standing 1.12.2 hook baseline.
- `docs/design/v2.0-RC2/DESIGN.md` §G0.1, §G0.1a, §G7, and §G11 — contract precedence,
  licensing, evidence adoption, and trust tiers.
- `docs/research/v1/RESEARCH.md` §0, §1, §10, §3, §4, §7, and Apps A, B, D, E, F —
  provenance vocabulary and only the contract sections needed to cross-check the
  surviving findings.
- Exact source windows around every Stage 4-retained anchor — quote verification.
- `README.md`, `gradle.properties`, `mods.toml`, `LICENSE`, and the six explicit
  lineage-notice lines — provenance and licensing only.

The temporary Stage 3 and Stage 4 handoffs supplied gate decisions and counts. They are
workflow inputs, not source citations.

### 2.4 Deliberate omissions

- Nothing beneath `src/main/java/net/coderbot/iris/pipeline/transform/`, `libs/`, or
  `glsl-relocated/` was read, searched, mined, quoted, paraphrased, summarized, or cited.
- `net/coderbot/iris/vendored/joml/` was excluded from every survey denominator and
  finding set.
- No `docs/**/chatlogs/`, root-level `.txt`, prior transcript, future design draft, build output,
  Gradle resolution, network source, or git mutation was used.
- No Oculus Minecraft hook was inferred from a Mixin, import, method name, build target,
  or loader declaration.

---

## 3. Provenance and licensing boundary

### 3.1 Survey result

The Stage 1 inventory counted 614 Java files and 171,532 LOC in the physical tree.
The excluded JOML subtree accounted for 69 files and 128,413 LOC, leaving the usable
survey at **545 files / 43,119 LOC**. These figures are provenance-gate outcomes, not
sampling claims about the excluded subtree.

The tree carries the LGPLv3 license text:

- `reference-src/Oculus-1.12/LICENSE:1` —
  `                   GNU LESSER GENERAL PUBLIC LICENSE`
- `reference-src/Oculus-1.12/LICENSE:2` —
  `                       Version 3, 29 June 2007`

The Stage 1 scan found **zero SPDX headers**. That does not negate the tree license, but
it makes lineage notices and sub-license boundaries operationally important.

### 3.2 Sub-license and stripped-binary boundaries

| Boundary | Gate outcome | Rule for Schmaloogium |
|---|---|---|
| Main Oculus tree | LGPL-3.0-only | May inform or be incorporated only with LGPL/GPL compliance, notices preserved, modifications marked |
| Vendored JOML | MIT | Survey-excluded; do not use it as subsystem evidence |
| Vendored digraph | Apache-2.0 | Preserve its separate notice if reused |
| Vendored stareval | 28 files with no license/header found | Upstream/license unresolved; clean-room from RESEARCH App F.6 unless independently verified |
| Bundled transformation JAR | 431 entries / 426 classes, all under the douira namespace, with no META-INF, manifest, or POM | Stripped provenance and AGPL risk: do not copy, do not depend |
| Seven transformation importers | All seven lie inside the hard-blocklisted transformer boundary | Boundary fact only; they were not mined or quoted |

The bundled JAR and its importers were characterized by the upstream provenance stage
before synthesis. This report did not open the blocked binary or importer sources.

The license text for `org.taumc:glsl-transformation-lib` was not retrieved from a primary
source. Its status is therefore **unresolved**, and the existing prohibition stands:
do not copy it, do not adopt it as a dependency, and retain the AGPL-risk label.
The same network outcome applies to `kroppeb/stareval`: upstream/license was not
verified, so the clean-room outcome remains.

### 3.3 Six explicit lineage notices

Six scoped files contain direct Sodium/Canvas LGPL lineage notices:

1. `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/vertices/NormI8.java:9` —
   ` * Copied from Sodium, licensed under the LGPLv3. Modified to support a W component.`
2. `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gl/shader/ShaderType.java:1` —
   `// This file is based on code from Sodium by JellySquid, licensed under the LGPLv3 license.`
3. `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gl/shader/ProgramCreator.java:1` —
   `// This file is based on code from Sodium by JellySquid, licensed under the LGPLv3 license.`
4. `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gl/shader/GlShader.java:1` —
   `// This file is based on code from Sodium by JellySquid, licensed under the LGPLv3 license.`
5. `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/JcppProcessor.java:7` —
   `    // Derived from GlShader from Canvas, licenced under LGPL`
6. `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/PropertiesPreprocessor.java:13` —
   `    // Derived from ShaderProcessor.glslPreprocessSource, which is derived from GlShader from Canvas, licenced under LGPL`

These are lineage clues, not replacements for copyright files or dependency licenses.

### 3.4 JCPP correction

The prior claim that Oculus actively wires JCPP 1.4.14 is not admissible. Stage 4
verified that the declaration lies inside the `build.gradle:84-212` block comment, so
it is historical intent rather than active dependency wiring. Finding `PB-08` was
nevertheless dropped because its jointly required JCPP-line quote failed exact
whitespace matching. This report records the correction but does not repair, reinstate,
or cite the failed candidate quote. JCPP's suitability must be established independently
of this inactive Oculus stanza.

**Relevance to Schmaloogium — Phases 1, 3, 11:** Oculus logic can inform GPL-3.0-or-later
Schmaloogium only through a documented LGPL-compliance path. The transformation boundary
remains prohibited, JCPP needs independent active dependency evidence, and stareval
remains clean-room-only unless its license is verified.

---

## 4. Pack front-end and preprocessing

**Phases:** P2, P3, P12.

### 4.1 Properties, options, includes, directives, and discovery

**FE-01 — classification: `loader-independent`.** Screen/value layout deliberately uses
the original non-preprocessed properties because preprocessing could alter layout data.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ShaderProperties.java:298` —
  `        // We need to use a non-preprocessed property file here since we don't want any weird preprocessor changes to be applied to the screen/value layout.`

This is a useful split: macro-preprocess semantic properties, but bind GUI layout against
the original data. Cross-check with RESEARCH §3.3 and App F.4 before adopting the split.

**FE-04 — classification: `loader-independent`.** Persisted or profile-provided option
values are intentionally not restricted to the GUI's advertised allowed-value list.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/option/values/MutableOptionValues.java:86` —
  `            // NB: We don't check if the option is in the allowed values here. This matches OptiFine`

This matches the distinction between discovery/UI values and source rewriting, but it
also requires validation and diagnostics at the boundary.

**FE-07 — classification: `loader-independent`.** The loader constructs one include graph
from all start files after recursively reading sources.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ShaderPack.java:129` —
  `        IncludeGraph graph = new IncludeGraph(root, starts.build());`

The graph is sound structure for include expansion and failure aggregation. It does not
override RESEARCH §3.2's depth cap or App F.3's same-file Boolean-option rule.

**FE-08 — classification: `loader-independent`.** When both legacy `DRAWBUFFERS` and
modern `RENDERTARGETS` directives exist, source order decides which one applies.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ProgramDirectives.java:135` —
  `            if (optionalDrawbuffersDirective.get().getLocation() > optionalRendertargetsDirective.get().getLocation()) {`

This is a precise last-directive-wins policy. It is a modern extension and must be
contract-tested rather than assumed for the G6 baseline.

**FE-09 — classification: `loader-independent`.** Pack discovery uses deterministic
case-insensitive ordering with natural-order tie-breaking.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/discovery/ShaderpackDirectoryManager.java:85` —
  `        Comparator<String> baseComparator = String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder());`

Stable discovery order is portable UX behavior and useful for deterministic tests.

### 4.2 Preprocessor constraints

**PB-03 — classification: `loader-independent`.** The marker-hoisting implementation
documents that it can violate the rule that `#version` be the first significant token.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/JcppProcessor.java:22` —
  `        // TODO: This allows #version to not appear as the first non-comment non-whitespace thing in the file.`

The hoisting technique is informative, but the documented caveat prevents direct adoption.

**PB-05 — classification: `loader-independent`.** Properties preprocessing splits on all
line terminators and applies `String::trim` to every line.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/PropertiesPreprocessor.java:73` —
  `        source = Arrays.stream(source.split("\\R")).map(String::trim)`

This normalizes separators and removes both leading and trailing whitespace. It therefore
is not a line-preserving implementation of the RESEARCH §3.3 properties contract.

**PB-06 — classification: `loader-independent`.** Hash-prefixed properties comments are
made compatible with the C preprocessor by suppressing expected directive diagnostics.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/PropertyCollectingListener.java:30` —
  `            // Suppress log spam since hashed lines also function as comments in preprocessed files.`

The compatibility rule is portable; the error policy still needs conformance tests so
real malformed directives are not swallowed.

**Relevance to Schmaloogium — Phases 2, 3, 12:** Reuse the graph/model separation,
deterministic discovery, directive precedence as a test candidate, and hash-comment
compatibility. Do not adopt the `#version` caveat or whitespace-destructive properties
path. The pack-facing vocabulary remains RESEARCH's: `gbuffers`, `DRAWBUFFERS`,
`RENDERTARGETS`, screens, profiles, and source-discovered options.

---

## 5. Program, pass, buffer, and pipeline policy

**Phases:** P4, P5, P7, P14.

### 5.1 Flip and compute-pass behavior

**BF-03 — classification: `loader-independent`.** Explicit virtual pre-pass flips are
excluded from the “flipped at least once” record.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/CompositeRenderer.java:65` —
  `                // NB: Flipping deferred_pre or composite_pre does NOT cause the "flippedAtLeastOnce" flag to trigger`

This separates initial virtual-program `flip` state from writes performed by real passes.
Schmaloogium must still follow RESEARCH App B.1/App F.7 for contract-visible carryover.

**BF-04 — classification: `loader-independent`.** A missing raster program can still
produce a compute-only pass, and a barrier is issued after compute execution.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/CompositeRenderer.java:77` —
  `                    ComputeOnlyPass pass = new ComputeOnlyPass();`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/CompositeRenderer.java:205` —
  `                IrisRenderSystem.memoryBarrier(40);`

The separation is good superset-shaped architecture for later compute stages. The literal
barrier mask must be decoded and re-derived, not copied as an unexplained number.

**BF-07 — classification: `loader-independent`.** Copy-back uses the general framebuffer
bind rather than a read-only bind because the latter broke temporal antialiasing.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/FinalPassRenderer.java:241` —
  `            // NB: We need to use bind(), not bindAsReadBuffer()... Previously we used bindAsReadBuffer() here which`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/FinalPassRenderer.java:242` —
  `            //     broke TAA on many packs and on many drivers.`

This is a concrete GL-state pitfall. It does not authorize Oculus's alt-to-main copy-back
when RESEARCH App F.7 requires last-writer flip carryover.

**BF-08 — classification: `loader-independent`.** Render targets detect both depth object
replacement and depth-format changes; the latter recomputes the copy strategy.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/rendertarget/RenderTargets.java:112` —
  `        if (cachedDepthBufferVersion != newDepthBufferVersion) {`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/rendertarget/RenderTargets.java:121` —
  `        if (depthFormatChanged) {`

Versioned invalidation is portable lifecycle structure for rebuilding dependent FBO
attachments and depth-copy policy.

### 5.2 Pass matching, fallback, and teardown

**PL-02 — classification: `loader-independent`.** Missing program source falls back to an
internal default pass whose two frame-position framebuffers are explicit.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:307` —
  `                    return createDefaultPass();`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:621` —
  `        return new Pass(null, framebufferBeforeTranslucents, framebufferAfterTranslucents, null,`

This is local fallback plumbing, not RESEARCH App A.2's program **backup chain**, and it
does not answer the required `(internal)` pack.

**PL-05 — classification: `loader-independent`.** Phase matching is gated to world
rendering on the main target, outside fullscreen/post-chain work, and stops the previous
pass before changing it.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:582` —
  `        if (!isRenderingWorld || isRenderingFullScreenPass || isPostChain || !isMainBound) {`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:600` —
  `            current.stopUsing();`

The predicate and transition discipline are portable. The actual phase-producing hooks
are modern and cannot establish 1.12.2 integration.

**PL-09 — classification: `loader-independent`.** Teardown first restores all framebuffer
bindings away from owned custom targets.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:770` —
  `        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, 0);`

This is a useful ownership rule for safe deletion and reload.

**Relevance to Schmaloogium — Phases 4, 5, 7, 14:** Adopt lifecycle structure:
superset-shaped compute/raster slots, explicit phase predicates, stop-before-switch,
depth-version invalidation, and binding-neutral teardown. Preserve the contract's
colortex estate, fixed units, `gbuffers` timing, full backup chain, and exact `flip`
semantics. No Oculus code here identifies a valid 1.12.2 hook.

---

## 6. Shadow math, culling, and target policy

**Phases:** P5, P8.

**SH-01 — classification: `loader-independent`.** Shadow matrix arrays are explicitly
column-major.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadow/ShadowMatrices.java:13` —
  `    // NB: These matrices are in column-major order, not row-major order like what you'd expect!`

**SH-02 — classification: `loader-independent`.** The shadow model-view construction
contains a fixed −100 Z translation before celestial rotations.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadow/ShadowMatrices.java:55` —
  `        target.last().pose().multiply(Matrix4f.createTranslateMatrix(0.0f, 0.0f, -100.0f));`

**SH-03 — classification: `loader-independent`.** The snapping implementation documents
Java remainder behavior for negative camera coordinates.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadow/ShadowMatrices.java:72` —
  `        // expression (-2.0f % 32.0f) returns -2.0f, negative inputs will result in negative outputs.`

These three anchors make the layout, transform order, and negative-coordinate edge case
explicit. All numeric values and snapping behavior must be re-derived against RESEARCH
§4.5 rather than copied.

**SH-04 — classification: `loader-independent`.** The clipping-plane transform is defined
as the transpose of projection times view.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/frustum/advanced/BaseClippingPlanes.java:22` —
  `        // Transform = Transpose(Projection x View)`

**SH-06 — classification: `loader-independent`.** The surviving terminal anchor in the
corner-visibility routine is an unconditional partial-visibility code.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/frustum/advanced/AdvancedShadowCullingFrustum.java:371` —
  `        return 2;`

This is a warning, not a reusable culling result: it prevents the terminal path from
reporting the fully-inside state.

**SH-07 — classification: `loader-independent`.** Coarse box culling begins with
axis-separation rejection.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/frustum/BoxCuller.java:34` —
  `        if (maxX < this.minAllowedX || minX > this.maxAllowedX) {`

**SH-08 — classification: `loader-independent`.** Pre-translucent shadow depth is copied
only while a dirty flag is set.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/ShadowRenderTargets.java:124` —
  `        if (translucentDepthDirty) {`

This gives a portable once-per-dirty-period policy for the water-shadow depth split.

**Relevance to Schmaloogium — Phases 5, 8:** Oculus provides math and internal-policy
checks only: matrix layout, transform order, snap pitfalls, plane transform, coarse
culling, and dirty depth copies. It provides **no gated addition** for a 1.12.2 shadow
traversal or invocation hook. Phase 8 remains governed by RESEARCH §4.5 and the
Pintonium/vanilla hook evidence.

---

## 7. Uniform cadence and identity plumbing

**Phases:** P6, P9.

**UN-02 — classification: `loader-independent`.** Per-frame work is modeled as listeners
registered on a frame notifier.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/FrameUpdateNotifier.java:13` —
  `    public void addListener(Runnable onNewFrame) {`

The observer shape is portable; the 1.12.2 frame-begin producer is not supplied here.

**UN-06 — classification: `loader-independent`.** Held-item IDs use cached per-frame
suppliers, with −1 as the invalid ID.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/IdMapUniforms.java:32` —
  `                .uniform1i(UniformUpdateFrequency.PER_FRAME, "heldItemId", mainHandSupplier::getIntID)`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/IdMapUniforms.java:66` —
  `            intID = -1;`

The same registration block also models the off hand, but the retained evidence is enough
to establish the main-hand cadence/default policy. Acquisition and 1.12.2 hook placement
remain unanswered.

**UN-07 — classification: `loader-independent`.** `blockEntityId` is registered against
captured render state plus an invalidation notifier.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/IdMapUniforms.java:43` —
  `        uniforms.uniform1i("blockEntityId", CapturedRenderingState.INSTANCE::getCurrentRenderedBlockEntity,`

This is delivery plumbing after a setter exists. It does not establish the scope or hook
that stamps the current 1.12.2 block entity.

**Relevance to Schmaloogium — Phases 6, 9:** The notifier, cached-supplier, invalid-default,
and captured-state patterns are useful. G11.5 changes from “no help” to **partial** for
held-item uniforms and `blockEntityId`: names and plumbing are answered; 1.12.2
acquisition/scope are not. `entityColor` delivery remains unanswered.

---

## 8. GUI, navigation, persistence, and reload

**Phases:** P3, P12.

**GUI-01 — classification: `loader-independent`.** The option menu chooses a discrete
slider widget for slider-marked string options, and drag position maps to a bounded value
index.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/widget/OptionMenuConstructor.java:38` —
  `                element.slider ? new SliderElementWidget(element) : new StringElementWidget(element));`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/widget/SliderElementWidget.java:71` —
  `        int newValueIndex = Math.min(valueCount - 1, (int) (mousePositionAcrossWidget * valueCount));`

This directly answers the prior sliders-UI gap as a loader-independent model and discrete
widget policy.

**GUI-04 — classification: `loader-independent`.** Subscreen navigation records opened
screen IDs in a history deque.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/NavigationController.java:37` —
  `        history.addLast(screen);`

**GUI-06 — classification: `loader-independent`.** Reload is conditional on pack,
enabled-state, queued-option, or reset-state changes.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:512` —
  `        if (!name.equals(previousPackName) || enabled != previousShadersEnabled || !Iris.getShaderPackOptionQueue().isEmpty() || Iris.shouldResetShaderPackOptionsOnNextReload()) {`

**GUI-07 — classification: `loader-independent`.** Opening settings first commits the
selected pack, while closing can explicitly discard changes.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:217` —
  `                this.applyChanges();`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:477` —
  `            discardChanges();`

**GUI-08 — classification: `loader-independent`.** Imported properties are queued through
the normal option path, and export reads the active per-pack `.txt` file.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:448` —
  `            Iris.queueShaderPackOptionsFromProperties(properties);`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/ShaderPackOptionList.java:292` —
  `                            Path sourceTxtPath = Iris.getShaderpacksDirectory().resolve(Iris.getCurrentPackName() + ".txt");`

**GUI-09 — classification: `loader-independent`.** Pack-list enumeration failure is caught
and logged rather than crashing the selection UI.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/ShaderPackSelectionList.java:50` —
  `            Iris.logger.error("Error reading files while constructing selection UI", e);`

**Relevance to Schmaloogium — Phases 3, 12:** Oculus answers the discrete-slider gap and
provides a coherent UI transaction model: history, apply/discard, conditional reload,
queued imports, per-pack export, and graceful list failure. The widgets are modern
Minecraft UI code, so only the model/behavior is portable; ModularUI or the 1.12.2
fallback must implement it independently.

---

## 9. Companion textures, atlas policy, and LabPBR mipmaps

**Phases:** P3, P13.

**TX-02 — classification: `loader-independent`.** Simple textures attempt normal and
specular companions independently, and a failed load returns no companion for that
channel.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/SimplePBRLoader.java:18` —
  `        AbstractTexture normalTexture = createPBRTexture(location, resourceManager, PBRType.NORMAL);`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/SimplePBRLoader.java:37` —
  `            return null;`

**TX-04 — classification: `loader-independent`.** Atlas policy iterates base sprites and
publishes a companion atlas only when upload succeeds.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:55` —
  `        for (TextureAtlasSprite sprite : ((TextureAtlasAccessor) atlas).getTexturesByName().values()) {`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:84` —
  `            if (specularAtlas.tryUpload(atlasWidth, atlasHeight, mipLevel)) {`

The policy is useful, but the retained quote also exposes the modern accessor dependency.
It cannot identify a 1.12.2 atlas hook.

**TX-05 — classification: `loader-independent`.** A companion sprite reuses the base
sprite's atlas X coordinate.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:146` —
  `            int x = ((TextureAtlasSpriteAccessor) sprite).getX();`

Together with the surrounding constructor policy, this supports the portable invariant
that companion atlas layouts mirror base atlas coordinates. The omitted `TX-05a` scaling
anchor is not used.

**TX-06 — classification: `loader-independent`.** Companion animation is reduced modulo
its own cycle and receives the source animation's subframe offset.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:182` —
  `        ticks %= cycleTime;`
- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:196` —
  `        targetAccessor.setSubFrame(ticks + sourceAccessor.getSubFrame());`

This handles companions whose frame timing differs from the base while preserving phase.

**TX-07 — classification: `loader-independent`.** Before sprite upload, all companion
atlas mip levels are filled with the PBR type's default.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/PBRAtlasTexture.java:64` —
  `        TextureManipulationUtil.fillWithColor(glId, mipLevel, type.getDefaultValue());`

This makes missing per-sprite companions semantically neutral. The omitted `TX-07b`
overlay anchor is not used.

**TX-08 — classification: `loader-independent`.** LabPBR specular mipmapping uses a
discrete classification function on a packed channel.

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/format/LabPBRTextureFormat.java:15` —
  `            new DiscreteBlendFunction(v -> v < 230 ? 0 : v - 229),`

The architecture preserves categorical values during downsampling. The threshold and
channel meaning must be re-derived from the adopted LabPBR contract; the omitted
`TX-08b` anchor is not used.

**Relevance to Schmaloogium — Phases 3, 13:** Oculus partially answers companion-atlas
policy: independent channel discovery, mirrored placement, default fill, animation-phase
sync, publish-on-success, and category-aware mipmaps. It does not answer 1.12.2 sprite
enumeration, stitch insertion, lifecycle, reload, tick, or deletion hooks. RESEARCH
§4.6/App E remains authoritative for those hooks and for packed default representation.

---

## 10. Cross-cutting synthesis

### 10.1 What the surviving pipeline evidence says

The loader-independent architecture is coherent:

1. Build an include/options/properties model.
2. Compile or fall back through the program **backup chain**.
3. Match a phase only while rendering the world to the main target.
4. Switch passes through a stop-before-start barrier.
5. Route `gbuffers` into the main colortex set.
6. Treat deferred/composite writes as explicit `flip` transitions.
7. Track depth-object versions and rederive copy policy on format change.
8. Unbind owned FBOs before teardown.

That sequence is evidence about engine structure, not evidence about where Minecraft
1.12.2 must be injected.

### 10.2 Pack-facing vocabulary and identity

Schmaloogium must preserve the contract's vocabulary exactly: colortex resources,
per-buffer `flip`, `gbuffers` phase names, the program backup chain, and the
`mc_Entity` vertex attribute. Oculus terminology or modern source types do not rename
those surfaces and cannot change the fixed RESEARCH contracts.

### 10.3 Scope of portable reuse

The strongest portable areas are:

- front-end model separation and deterministic discovery;
- pass/buffer lifecycle and reload safety;
- shadow math as a numeric reference;
- frame-notifier/captured-state uniform plumbing;
- option-screen transaction behavior;
- companion-atlas policy independent of hook placement.

The weakest areas are exactly the integration boundaries: source-set correctness,
loader metadata, Minecraft class targets, sprite/accessor hooks, and any behavior whose
only support is a modern Mixin.

**Relevance to Schmaloogium — all phases:** Use Oculus to sharpen policy, invariants,
tests, and pitfalls. Use RESEARCH for the contract and Pintonium/vanilla evidence for
1.12.2 hooks. Never reverse those roles.

---

## 11. G11.5 prior-gap disposition

| Prior G11.5 gap | Result after gated Oculus mining | Evidence boundary |
|---|---|---|
| sliders UI | **Answered** | `GUI-01`: loader-independent model plus discrete slider widget/index behavior |
| held-item uniforms | **Partial** | `UN-06`: names, per-frame cached supplier, invalid −1; no 1.12.2 acquisition/hook |
| `blockEntityId` delivery | **Partial** | `UN-07`: captured-state/notifier registration; no 1.12.2 setter scope/hook |
| companion atlas stitching | **Partial policy only** | `TX-02/04/05/06/07/08`: layout/default/animation/mipmap policy; no admissible 1.12.2 sprite/lifecycle hooks |
| shadow pass on 1.12.2 | **Unanswered** | Shadow math/internal policy only |
| sky/weather/cloud hooks | **Unanswered** | No gated hook evidence; abstract phase predicates do not supply injection sites |
| `entityColor` delivery | **Unanswered** | Zero `ONCE` placeholder only; no delivery |
| `version.<mcver>` gate | **Unanswered** | No gated implementation |
| internal default pack | **Unanswered** | `PL-02` is a default pass, not an `(internal)` pack |
| render-quality multipliers | **Unanswered** | No gated implementation |

The exact outcome is therefore: answered sliders UI; partial held-item,
`blockEntityId`, and companion-atlas policy; unanswered 1.12.2 shadow pass,
sky/weather/cloud hooks, `entityColor` delivery, version gate, internal pack, and
render-quality multipliers.

---

## 12. Numbered pitfalls catalogue

1. **Build target is not source reality.** `minecraft_version=1.12.2` cannot outweigh
   the 74-file modern-marker union and modern loader metadata.
2. **Closed repository, frozen uncertainty.** The README says the project is closed;
   mismatches are unlikely to be repaired upstream.
3. **No active JCPP wiring proof.** The JCPP stanza is inside a block comment, and its
   candidate quote failed Citation Gate.
4. **`#version` placement is explicitly wrong in an edge case.** `PB-03` documents it.
5. **Properties preprocessing is whitespace-destructive.** `PB-05` trims every line.
6. **Diagnostic suppression can hide real errors.** `PB-06` is only safe with narrow
   message matching and conformance tests.
7. **Allowed option values are UI guidance, not validation.** `FE-04` permits unchecked
   persisted/profile values.
8. **Whole-graph option confirmation conflicts with the contract.** The include graph
   stub returns the whole graph; see conflict `C-FE02`.
9. **Virtual pre-flips are special.** `BF-03` excludes them from the write-history flag.
10. **Magic barrier masks are not design.** `BF-04` uses literal `40`; re-derive named
    barrier bits.
11. **Framebuffer bind targets affect TAA.** `BF-07` records a real driver/pack failure.
12. **Copy-back conflicts with last-writer carryover.** `C-BF06`; RESEARCH wins.
13. **Negative-coordinate snapping is easy to get wrong.** `SH-03` records `%` behavior.
14. **The advanced corner classifier has a suspicious terminal code.** `SH-06` returns
    2 unconditionally at the retained terminal anchor.
15. **Modern accessors are not hook references.** `TX-04/05/06` expose useful policy
    while proving the integration itself is source-era-coupled.
16. **PBR constants are representation-sensitive.** The TX-01 conflict was found but
    failed Gate; do not choose byte order by visual similarity.
17. **GUI state is transactional.** Applying selected packs before settings, explicit
    discard, and conditional reload must remain coherent (`GUI-06/07`).
18. **Licensing is layered.** LGPL tree status does not cleanse stripped transformation
    binaries or unlicensed vendored subtrees.

---

## 13. Contract divergences and conflicts

### 13.1 Divergence table

| Area | Oculus evidence/behavior | Schmaloogium contract response |
|---|---|---|
| Source/runtime target | Declares 1.12.2 but source markers and loader metadata are modern/hybrid | Never use Oculus for `1.12.2-hook`; Pintonium remains hook evidence |
| Option confirmation | Include graph returns whole graph | RESEARCH App F.3 same-file confirmation wins |
| Directive precedence | Later `DRAWBUFFERS`/`RENDERTARGETS` location wins | Treat as a tested modern extension, not a silent G6 rule |
| Properties preprocessing | Trims every line | Preserve contract-significant values/spacing; write a safer path |
| Frame-end flip | Alt-to-main copy-back exists | RESEARCH App F.7 last-writer flip carryover wins |
| Smoothing units | Deciseconds in Oculus/PD | RESEARCH App A.3 says ticks; record and resolve in phase work |
| Held item / block entity | Portable suppliers/notifiers | Add genuine 1.12.2 acquisition and setter scope |
| Companion atlases | Modern sprite/accessor stitching | Reuse policy only; implement against RESEARCH App E TextureMap/Sprite hooks |
| Normal default | Conflict candidate `0x7F7FFFFF` failed Gate | RESEARCH `0xFF7F7FFF` remains contract text; investigate representation, do not silently swap |
| Internal fallback | Default pass | Still must implement the `(internal)` pack |
| Build platform | Dropped RFG/Java 8 candidate | RESEARCH's selected platform remains; no Oculus build conflict adopted |

### 13.2 All five RESEARCH conflicts

**C-FE02 — retained conflict quote.** RESEARCH App F.3 requires same-file Boolean-option
confirmation. Oculus intends component scope but the implementation returns the whole
graph:

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/include/IncludeGraph.java:240` —
  `        return Collections.singletonList(this);`

Disposition: RESEARCH wins. Implement same-file behavior and test ambiguous names.

**C-BF06 — retained conflict quote.** RESEARCH App F.7 says the last writer leaves
`flip` enabled. Oculus documents alt-to-main copy-back:

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/FinalPassRenderer.java:93` —
  `        // TODO: We don't actually fully swap the content, we merely copy it from alt to main`

Disposition: RESEARCH wins. Copy-back may be studied as mechanics, not adopted as
contract behavior.

**C-UN04 — retained conflict quote.** RESEARCH App A.3 labels directive values as ticks,
while Oculus/PD converts the value as deciseconds:

- `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/transforms/SmoothedFloat.java:53` —
  `        this.decayConstantUp = computeDecay(halfLifeUp * 0.1F);`

Disposition: unresolved unit conflict to be surfaced in Phase 6; RESEARCH remains
normative until that contract record is deliberately revised.

**C-PB07 — conflict found, quote dropped.** Oculus's active build appeared to use
RetroFuturaGradle/Java 8/MixinBooter, conflicting with RESEARCH's selected
Unimined/Java 25/CleanMix platform. The candidate quote failed Citation Gate for leading
whitespace mismatch. Do not cite or adopt the failed Oculus quote. The conflict discovery
is recorded; no build-platform conclusion is drawn from it.

**C-TX01 — conflict found, quote dropped.** Oculus/PD appeared to use packed normal
default `0x7F7FFFFF`, while RESEARCH §4.6 records `0xFF7F7FFF`. The candidate quote failed
Citation Gate for leading whitespace mismatch. Do not cite or adopt it. The likely
representation/byte-order question remains unresolved, and RESEARCH stays normative.

---

## 14. Trust tiers

The exact §G11.5 labels are retained, with Oculus-specific scope:

### Trust and reuse freely

Subject to LGPL compliance and a contract check:

- front-end dataflow structure, deterministic discovery, include graph architecture;
- pass-transition, invalidation, teardown, notifier, and captured-state patterns;
- discrete slider/navigation/apply-discard UI model;
- companion-atlas policy that is independent of Minecraft API types.

“Freely” never bypasses RESEARCH, license notices, or the hook-point prohibition.

### Reuse structure, re-derive values

- shadow matrices, celestial transform order, clipping-plane math, snapping, and culling;
- LabPBR thresholds and packed defaults;
- compute barrier selection;
- smoothing units and constants;
- companion scaling/mipmap numeric policy.

### Do not copy (contract conflict or license)

- blocked transformation code or stripped transformation binary behavior;
- alt-to-main copy-back as replacement for App F.7 flip semantics;
- whole-graph option confirmation;
- whitespace-destructive properties preprocessing;
- source-era-specific Mixin/accessor integration;
- unverified stareval code or unresolved taumc transformation code;
- any failed Citation Gate quote.

### No help available (design from RESEARCH.md alone)

- 1.12.2 hook points of every kind;
- 1.12.2 shadow traversal/invocation;
- sky/weather/cloud hooks;
- `entityColor` delivery;
- `version.<mcver>` gate;
- internal default pack;
- render-quality multipliers.

---

## 15. Citation-Gate drops

### 15.1 Six dropped findings

| ID | Why Stage 4 dropped it | Synthesis treatment |
|---|---|---|
| PB-07 | Expected spaces; actual active RFG-plugin line used a tab | Not cited, repaired, or adopted |
| PB-08 | Expected spaces; actual JCPP line used a tab; both block-comment delimiters matched but all three anchors were jointly required | Negative block-comment correction recorded without reinstating the finding |
| SH-05 | CC BY-SA notice quote had different leading indentation | Notice claim omitted |
| UN-08 | stareval parser quote expected a tab where the source had spaces | Parser finding omitted; unresolved license outcome retained independently |
| TX-03 | Both manager quotes had different leading indentation | Manager finding omitted |
| TX-09 | Both format quotes had different leading indentation | Format-loader finding omitted |

### 15.2 Two dropped conflict quotes

| ID | Discovered conflict | Why quote was dropped | Treatment |
|---|---|---|---|
| C-PB07 | RFG/Java 8/MixinBooter vs Unimined/Java 25/CleanMix | Leading-whitespace mismatch | Discovery logged; failed Oculus quote neither cited nor adopted |
| C-TX01 | Packed normal default differs from RESEARCH | Leading-whitespace mismatch | Discovery logged; representation unresolved; failed Oculus quote neither cited nor adopted |

No drop was silently corrected.

---

## 16. Gated-findings audit

All 37 Stage 4-kept findings are placed below. Every row is `loader-independent`; there
are zero `1.12.2-hook` findings.

| ID | Phase(s) | Retained exact source anchor(s) | Placement |
|---|---|---|---|
| FE-01 | P3/P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ShaderProperties.java:298` | §4.1 |
| FE-04 | P3/P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/option/values/MutableOptionValues.java:86` | §4.1 |
| FE-07 | P3 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ShaderPack.java:129` | §4.1 |
| FE-08 | P3/P4 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/ProgramDirectives.java:135` | §4.1 |
| FE-09 | P3/P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/discovery/ShaderpackDirectoryManager.java:85` | §4.1 |
| PB-03 | P3 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/JcppProcessor.java:22` | §4.2 |
| PB-05 | P3 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/PropertiesPreprocessor.java:73` | §4.2 |
| PB-06 | P3 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shaderpack/preprocessor/PropertyCollectingListener.java:30` | §4.2 |
| BF-03 | P5 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/CompositeRenderer.java:65` | §5.1 |
| BF-04 | P4/P14 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/CompositeRenderer.java:77`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/CompositeRenderer.java:205` | §5.1 |
| BF-07 | P5/P14 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/FinalPassRenderer.java:241`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/postprocess/FinalPassRenderer.java:242` | §5.1 |
| BF-08 | P5 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/rendertarget/RenderTargets.java:112`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/rendertarget/RenderTargets.java:121` | §5.1 |
| SH-01 | P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadow/ShadowMatrices.java:13` | §6 |
| SH-02 | P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadow/ShadowMatrices.java:55` | §6 |
| SH-03 | P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadow/ShadowMatrices.java:72` | §6 |
| SH-04 | P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/frustum/advanced/BaseClippingPlanes.java:22` | §6 |
| SH-06 | P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/frustum/advanced/AdvancedShadowCullingFrustum.java:371` | §6 |
| SH-07 | P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/frustum/BoxCuller.java:34` | §6 |
| SH-08 | P5/P8 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/shadows/ShadowRenderTargets.java:124` | §6 |
| UN-02 | P6 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/FrameUpdateNotifier.java:13` | §7 |
| UN-06 | P6/P9 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/IdMapUniforms.java:32`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/IdMapUniforms.java:66` | §7 |
| UN-07 | P6/P9 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/uniforms/IdMapUniforms.java:43` | §7 |
| GUI-01 | P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/widget/OptionMenuConstructor.java:38`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/widget/SliderElementWidget.java:71` | §8 |
| GUI-04 | P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/NavigationController.java:37` | §8 |
| GUI-06 | P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:512` | §8 |
| GUI-07 | P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:217`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:477` | §8 |
| GUI-08 | P3/P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/screen/ShaderPackScreen.java:448`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/ShaderPackOptionList.java:292` | §8 |
| GUI-09 | P12 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/gui/element/ShaderPackSelectionList.java:50` | §8 |
| PL-02 | P4/P7 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:307`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:621` | §5.2 |
| PL-05 | P4/P7 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:582`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:600` | §5.2 |
| PL-09 | P5/P7 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/pipeline/DeferredWorldRenderingPipeline.java:770` | §5.2 |
| TX-02 | P13 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/SimplePBRLoader.java:18`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/SimplePBRLoader.java:37` | §9 |
| TX-04 | P13 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:55`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:84` | §9 |
| TX-05 | P13 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:146` | §9 |
| TX-06 | P13 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:182`; `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/loader/AtlasPBRLoader.java:196` | §9 |
| TX-07 | P13 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/pbr/PBRAtlasTexture.java:64` | §9 |
| TX-08 | P13 | `reference-src/Oculus-1.12/src/main/java/net/coderbot/iris/texture/format/LabPBRTextureFormat.java:15` | §9 |

Count placed: **37 / 37**.

---

## 17. Quick reference: phase → Oculus's gated answer

| Phase | Gated answer from Oculus | Confidence / boundary |
|---|---|---|
| P1 Foundation | Provenance counterexample: declared target can diverge radically from source; LGPL/sub-license boundary checklist | High for audit lesson; **no gated addition** for the selected build platform |
| P2 Conformance | Deterministic pack ordering, properties/preprocessor edge cases, TAA framebuffer-bind regression, modern-source mismatch as negative fixtures | Medium |
| P3 Front-end | Include graph construction, layout/original-properties split, directive precedence, unchecked stored values, preprocessing pitfalls | High for portable logic; JCPP active wiring not proven |
| P4 Registry | Compute-only slot, default-pass fallback, guarded phase matching, stop-before-switch | Medium; preserve RESEARCH backup chain and fixed contract |
| P5 Buffers | Virtual-pre flip distinction, TAA-sensitive copy binding, depth-version/format invalidation, dirty shadow-depth copy, teardown unbind | High for lifecycle policy; copy-back conflict rejected |
| P6 Uniforms | Frame listener, cached held-item supplier/default, captured `blockEntityId` source, smoothing unit conflict | Medium; no 1.12.2 producers |
| P7 Render loop | Abstract world/main/fullscreen/post-chain phase predicate and teardown discipline | Low; **no gated addition** for 1.12.2 hook points or sky/weather/clouds |
| P8 Shadow pass | Column-major matrices, transform order, negative snap warning, clipping-plane transform, culling/depth-copy policy | Medium for math; **no gated addition** for 1.12.2 traversal/pass |
| P9 Aliasing | Held-item invalid default and `blockEntityId` captured-state plumbing | Low/partial; no acquisition or setter hooks |
| P10 Vertex pipeline | **No gated addition**; Oculus hook/source era is inadmissible, and `mc_Entity` remains governed by RESEARCH/Pintonium |
| P11 Expressions | **No gated addition**; stareval finding dropped and license remains unresolved |
| P12 GUI | Discrete slider, navigation history, apply/discard, conditional reload, import/export, graceful enumeration failure | High for loader-independent UI model |
| P13 Textures | Optional simple companions; mirrored atlas policy; animation sync; default fill; category-aware LabPBR mipmaps | Medium/partial; no 1.12.2 stitching hooks |
| P14 GL modernization | Compute-only pass/barrier shape and GL binding pitfalls | Low-to-medium; re-derive barrier bits and use Pintonium for deployed GL tiers |

---

## 18. Bottom line

Oculus contributes 37 citation-gated, loader-independent findings. Its strongest value is
not as a port but as a set of policy and failure examples: how an Iris-lineage front end
organizes data, how pass/buffer lifecycles are guarded, how shadow math can fail at edge
cases, how UI state is transacted, and how companion-atlas policy fits together.

Its declared build target does not make its modern Minecraft-facing source a 1.12.2
reference. The modern-marker union is 74, the metadata is mismatched, no admissible
Oculus hook finding survived, and every 1.12.2 integration decision remains with
RESEARCH plus Pintonium/vanilla evidence. That boundary is the main conclusion of this
report, not a footnote.

*End of analysis.*
