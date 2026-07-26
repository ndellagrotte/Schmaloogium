# PHASE_1_DOC.md — Verify session, round seven

**Session type:** verify (`DESIGN.md` §G1.2) · **Document under review:** `Schmaloogium/PHASE_1_DOC.md`
**Date:** 2026-07-25 (pin re-verification performed 2026-07-25 ~03:05–03:07 UTC; the document header is
stamped 2026-07-24) · **Verdict:** **PASS-WITH-CORRECTIONS**

---

## 0. What I read, and in what order

Assigned reading, in the order §G1.2 prescribes:

1. `DESIGN.md` Part I in full (§G0–§G10, lines 1–574) and the Phase 1 spec in Part II (585–658);
   other phases by title and §G5.1 row only, plus the disclosed reads below.
2. `RESEARCH.md` §0 (reading guide, confidence tags) and §1 (mission, non-goals, decision log), then
   the spec's **Required inputs**: §1, §5.1, §5.2, §5.3, §6.1, §7.2, §12.2.
3. Template ground truth, complete: `build.gradle`, `settings.gradle`, `gradle.properties`,
   `gradle/scripts/{dependencies,extra,publishing}.gradle`, `gradle/wrapper/gradle-wrapper.properties`,
   all eight files under `src/**`, all three `.github/workflows/*.yml`, `README.md`.
4. `PHASE_1_DOC.md` in full, 2 901 lines.
5. **Last, and only after my own findings were formed:** `PHASE_1_REVIEW.md` through
   `PHASE_1_REVIEW_6.md`, including all six `## Resolutions` sections.

### 0.1 Read beyond the assigned list, each because a finding turns on it

Recorded per §G1.1/§G1.2. All are targeted reads, not whole-appendix studies.

- **RESEARCH.md §3.2** and **App A.3** — the `countInstances` directive row and its wording, which
  V7-1 turns on entirely.
- **RESEARCH.md §3.5** — the GLSL-120-era premise `[D-P1-33]` rests on, and the standard macro
  header §3's profile row maps.
- **RESEARCH.md §4.1** — the probe set, to re-check §3's five probe rows and §3.1's flagged delta.
- **RESEARCH.md §4.2, §4.3, §4.4, §4.5** — the program registry's per-slot "instance count" (V7-1),
  the depth-copy/attachment architecture, the composite/final draw state, and the shadow pass's
  depth→`shadowtex1` copy (the Phase 8 consumer this fix-up added to §5).
- **RESEARCH.md App B.2, App D.1–D.4, App F.7** — to verify V5-7's, V6-4's and the `ivec3`/`mat3`
  rows against source rather than inherit round five's agreement.
- **`schlorbium-project/doc/shaders.txt`**, two passages: the common-uniform block containing
  `uniform int instanceId` (l. 182) and the **"Vertex Shader Configuration"** table (ll. 342–349).
  §G7.3 makes this a legally clean contract source and it is the provenance App A.3 and App D
  themselves cite; §G1.2's priority-2 check is to spot-check a mapped conformance row *"against the
  cited RESEARCH.md/App text"*, and this row's citation chain terminates here. Behavior only was
  read; no identifier or structure from the decompiled sources was touched (§G7.2).

### 0.2 Precedents relied on, and one disclosure

Two precedents that have held for three rounds are used again: reading another phase's §G5.1 scope
line is legitimate for a verify session auditing an ownership claim, and RESEARCH.md App D / App F
end to end is where the worst defects have hidden.

**No adversarial sub-agents were used.** §G1.2 permits them; this session did not use any, so every
quote below was read against its source file by this session directly. No code was written, no build
or test was run, and no file other than this one was created or modified.

---

## 1. Verification performed independently of the document

### 1.1 Pins — live, executed per §4.2.6, and **nothing has drifted**

The Phase 1 spec orders re-verification and the loader ships daily, so this is the one check the
brief permits live network for. Executed 2026-07-25 ~03:05 UTC, one day after the document's stated
re-verification date.

| Row | Pinned | Observed live | Verdict |
|---|---|---|---|
| Cleanroom loader | `0.6.6-alpha` | `<release>0.6.6-alpha`; `<lastUpdated>20260724133703` | **current** |
| — cross-check | — | GitHub releases API: `0.6.6-alpha`, `published_at` 2026-07-24T13:37:05Z, not a draft, not a prerelease | **agree** |
| Unimined (kappa fork) | `1.4.26-kappa` | arcseekers metadata tops out at `1.4.26-kappa`, `lastUpdated` 20260711 | **current** |
| ASM (test-scope) | `9.10.1` | Central mirror `<release>9.10.1`, `<latest>9.10.1` | **current** |
| Gradle | `9.6.1` | `services.gradle.org/versions/current` → `9.6.1`, `"current": true` | **current** |

Two things worth stating. First, §4.2.6 step 2's method — maven metadata authoritative for
resolvability, the GitHub API for release notes, *"a tag that appears in one and not the other is
itself a finding"* — executes exactly as written and the two sources agree. Second, the metadata's
own `lastUpdated` timestamp (2026-07-24 13:37:03Z) is the strongest available evidence that
**nothing shipped since the document's re-verification**: the daily cadence has not fired in ~13.5
hours. No pin row needs correction, and the "Re-verified 2026-07-24" column is accurate for the work
it describes.

### 1.2 Doc gate — **PASS**, all three criteria, read literally

- *"module/package layout finalized with dependency rules as testable constraints"* — §2.1's package
  tables for all three modules, and C-1…C-4 in §4.3, each with a named test in §8.1. Met.
- *"every D-1..D-10 either satisfied by this phase or explicitly deferred with its owner phase
  named"* — §11.2, ten rows, each disposition carrying an owner where deferred (D-3→P2, D-4→P4,
  D-5 catalog→P7, D-9 policy→P5+/P7, D-10→P2). Met.
- *"pin table complete with re-verification procedure"* — §4.2.6's thirteen rows plus the
  seven-step procedure and the three-ruling decision table, restated in §G4.4 form at §10.1. Met.

### 1.3 Template completeness, OQ spikes, scope, binding decisions

- **Thirteen §G9 sections present and substantive** (§0 through §12). Confirmed by heading sweep.
- **All four assigned OQs carry full spike specs** — OQ-2 (§10.1), OQ-12 (§10.2), OQ-20 (§10.3),
  OQ-21 (§10.4) — each with the question verbatim from RESEARCH.md §11, a concrete procedure,
  success *and* failure criteria, and a fallback designed now. I re-checked the four verbatim
  quotations against §11's rows; all four match.
- **Scope discipline holds in both directions.** Every one of the spec's twelve *Scope — in* bullets
  has a home in §4, and nothing from *Scope — out* is designed: no harness content (Phase 2), no
  pack-format work (Phase 3), no GL policy beyond the facade's shape, no GUI-framework evaluation.
  §1.2's adjacent-concerns table is the anti-sprawl device §G9 asks for and it is complete.
- **No D-1…D-10 contradicted**, and no contract-visible component "improved" (§G4.2) — with one
  qualification that is V7-1 below: a contract-visible behavior is *narrowed* in the conformance
  map without the narrowing being stated.

### 1.4 Contract fidelity of the material this fix-up touched — re-derived, not inherited

The brief's instruction was to test round six's prediction rather than adopt it. I re-derived from
source every claim the last fix-up wrote into §5 or §3.

- **`instanceId` is an `int` uniform.** `doc/shaders.txt` l. 182: *"uniform int instanceId; instance
  ID when instancing is enabled (countInstances > 1), 0 = original, 1-N = copies"*, matching App
  D.4's row exactly. **Confirmed.**
- **Packs are GLSL-120-era compat code.** RESEARCH.md §3.5's first bullet. `gl_InstanceID` is a GLSL
  1.40 / `EXT_gpu_shader4` construct and is not available to a 120-era pack. **Confirmed** — and
  therefore `[D-P1-33]`'s central argument is sound: one instanced draw cannot vary a *uniform*
  between copies, so no `fullscreenQuadInstanced(int)` could ever have expressed the directive.
- **Phase 8's shadow-pass depth copy** (V5-7's addition to §5.2 and §9). RESEARCH.md §4.5: the
  shadow pass *"copies depth→shadowtex1 (water-shadow split)"*; App B.2 lists `shadowtex1` as a
  *"copy before shadow translucents"* in the same table as `depthtex1`/`depthtex2`. The citation is
  accurate and the consumer belongs in the list. **Confirmed correct.**
- **App F.7's per-program state keys** (V6-4's three new §3 rows). F.7 declares
  `alphaTest.<prog>`, `blend.<prog>`, `scale.<prog>` (*"composite/deferred sub-viewport, 0.0–1.0"*),
  `flip.<prog>.<buf>` and `program.<prog>.enabled`. The three mapped rows map correctly to
  `StateService.alphaTest` / `.blend` + `snapshot()`/`restore()` / `.viewport`. The two unmapped keys
  need no facade verb (flip is Phase 5 ping-pong policy, `enabled` is Phase 4 registry), so their
  absence from §3 is correct, not an omission. **Confirmed correct.**
- **`ivec3` / `mat3` have no contract consumer.** Swept App D.1–D.4 row by row: the declared types
  are `int`, `float`, `ivec2`, `ivec4`, `vec3`, `vec4`, `mat4` and nothing else. The facade's
  overloads cover each. **Confirmed correct.**
- **The composite/final draw state** underpinning `[D-P1-31]`. RESEARCH.md §4.4: composites draw one
  fullscreen quad *"under an identity ortho, fog/depth/blend disabled … optional sub-viewport
  (`scale.<prog>`)"*. `depthTest`, `fog`, `blend` and `viewport` are all present and `depthMask` is
  correctly distinguished from depth *test*. **Confirmed correct.**
- **The four-handle / no-renderbuffer claim.** §4.3 makes `depthtex1`/`depthtex2` *copies* into
  standalone textures rather than attachments, and every attachment sampleable. `copyDepthToTexture`
  is the right verb and a `RenderbufferHandle` would have no consumer. **Confirmed correct.**
- **§3.1's flagged delta.** RESEARCH.md §4.1 step 1 names four probes plus the GL-3.0 mipmap gate
  and does **not** name an extension set; DESIGN.md l. 614 attributes it to §4.1. The document's
  ruling — include it, tag `[A]`, source it to §3.5's `MC_<GL_extension>` requirement, and request
  the DESIGN.md correction in §11.5 — is exactly what §G0.1 and §G1.1 prescribe. **Confirmed correct.**

---

## 2. Audit of the six `## Resolutions` tables, and of §0.6

### 2.1 Are the "applied" rows real and complete?

I checked each of round five's eight and round six's six at every site its `Where` column names.

| Round | Finding | Sites claimed | Found at all sites? |
|---|---|---|---|
| 5 | V5-1 `glGetError` cadence | §2.4, §4.7.4 ×4, §5.2, §6, §7, §9, §11.1, §12/22 | **yes** — nine sites, all consistent, no stale "cannot lose an error" text survives anywhere |
| 5 | V5-2 instanced verb | §3, §4.7.4, §5.2, §11.1, §12/19 | **yes** — and the verb name survives only in the four places that record its deletion |
| 5 | V5-3 CI ordering | §4.2.4a, §4.11, §6, §11.1, §12/38 | **yes** — but see V7-5 |
| 5 | V5-4 ASM remedy | §4.2.4, §4.2.6, §11.1, §12/7 | **yes** — `resolutionStrategy.force` is gone from all four |
| 5 | V5-5 `[D-P1-24]` residue | §11.1 | **yes** |
| 5 | V5-6 recorder log | §4.7.5, §4.9.3, §5.2, §7, §8.1, §12/24 | **yes** — six sites |
| 5 | V5-7 Phase 8 | §5.2, §9 | **yes** |
| 5 | V5-8 lazy classpath | §4.2.3, §4.2.4, §4.2.4a | **yes** — but see V7-6 |
| 6 | V6-1 stale cadence | §0.5, §0.6, closing ¶, `_5`'s Resolutions | **yes** — §0.5 retitled *"at the time"* and closed with a forward pointer; §0.6 written; closing ¶ now reads six verify sessions and three fix-ups |
| 6 | V6-2 rung 1 | §2.4, §4.7.4, §5.2, §6 ×2, §11.1 | **yes** — "rung 1" no longer appears misassigned at any site |
| 6 | V6-3 two unrecorded sites | seven sites + two decision rows | **yes** |
| 6 | V6-4 App F.7 rows | §3 | **yes** — four rows, not three |
| 6 | V6-5 `blit` | §4.7.4 | **yes** (narrowed — ruled on below) |
| 6 | V6-6 `enable_mixin_debug` | §4.4.1, §4.5.5, §12/32 | **yes** — all three, by deletion |

**Fourteen of fourteen land where they claim to.** This is the first round in six at which that is
true of the whole list, and it is worth recording plainly.

### 2.2 Did the two "narrowed" rows talk the fix-up out of a real defect?

- **V5-8, narrowed by leaving the checklist alone.** Partly right, partly not — V7-6.
- **V6-5, `blit` kept rather than moved to the absent-verbs table.** **Ruled acceptable, and not
  re-raised.** The narrowing supplies exactly what the note asked for: a named consumer (Phase 5),
  an honest statement that no *contract* item demands the verb today, and a written condition for
  revisiting it. A conditional exception with a stated sunset trigger is a legitimate answer to a
  note; declining to overturn round four's deliberate specification on the strength of a note is
  also legitimate (§G1.3 does not oblige a fix-up to apply notes as written). I re-checked the
  premise: §4.3's `final` renders to the vanilla framebuffer as a *draw* and the depth copies target
  textures, so the "no contract consumer" statement is true.

### 2.3 §0.6's own claims

- **"The five design calls this session made … since the reviews left them open."** Substantiated.
  I found the open-disposition language at `PHASE_1_REVIEW_5.md`:262 (*"a design call for the
  fix-up, not [mine]"*), :297 (*"Disposition options for the fix-up (its call, not mine)"*), :324
  (*"Fix shape (for the fix-up)"*) and `PHASE_1_REVIEW_6.md`:263 (*"the fix-up's call"*). The
  reviews did leave these to the fix-up. One presentational wobble, not a finding: the fifth bullet
  carries two decisions (V6-5 and V6-6), so "five design calls" is five *bullets* covering six
  decisions.
- **The reads-beyond-the-list.** Each named read is one a finding genuinely turned on, and each is
  traceable to the text it produced. The Phase 11 *Scope — in* read is disclosed, which is the right
  posture. One gap, folded into V7-3 rather than raised separately: the fix-up's central new claim
  about GL error-flag semantics has **no cited source anywhere in the document** and no GL
  specification appears in §0.6's read list — which is how V7-3's imprecision survived.

---

## 3. Findings

Eight: five corrections, three notes, **zero blocking**.

### V7-1 — `countInstances` is mapped to a composite-only mechanism, and none of the cited sources restricts it to composites · **correction** · **§5**

**Location.** §3's conformance row (l. 402); §4.7.4's `DrawService` javadoc (ll. 1493–1495) and its
absent-verbs "Instanced draw" row (l. 1637); §5.2's non-verbs row (l. 2158); `[D-P1-33]` (l. 2645).

**Claim under test.** That *"a caller-side loop over `DrawService.fullscreenQuad()` with
`UniformService.upload(instanceIdLoc, i)` between copies"* is the design element satisfying
`const int countInstances = N`.

**The deletion itself is right, and I confirm it independently.** `instanceId` is an `int` uniform
(`doc/shaders.txt` l. 182; App D.4) and packs are GLSL-120-era (§3.5), so a single instanced draw
cannot vary it per copy and `fullscreenQuadInstanced(int)` could never have expressed the directive.
`[D-P1-33]`'s reasoning survives every check I could put to it.

**The replacement mapping is the defect.** `DrawService.fullscreenQuad()` is specified in the very
same section as *"The composite/final full-screen pass primitive"* (l. 1489). But nothing in the
cited provenance scopes `countInstances` to composite programs:

- `doc/shaders.txt` places `const int countInstances = 1;` under **"Vertex Shader Configuration"**
  (ll. 342–349), beside `mc_Entity`, `mc_midTexCoord` and `at_tangent` — three directives that are
  unambiguously gbuffers/terrain-side — with the effect *"when countInstances > 1 the **geometry**
  will be rendered several times"*. No program restriction is stated.
- App A.3 tags the row `(vsh)` and repeats *"instanced re-render with `instanceId` uniform"*.
- RESEARCH.md §3.2's vertex-stage bullet: *"`const int countInstances = N` **re-renders geometry** N
  times with `instanceId` incrementing"* — in the same sentence as the `mc_Entity`/`at_tangent`
  opt-ins.
- `uniform int instanceId` sits in shaders.txt's **common** uniform block, above the
  "GBuffers Uniforms" heading — i.e. available to every program, not composite-only.
- RESEARCH.md §4.2 lists **"instance count"** among the per-program state carried by *all 43 program
  slots*, alongside draw-buffer routing and alpha/blend overrides.

For a gbuffers program carrying the directive, "the geometry" is vanilla terrain or entity geometry,
drawn by Minecraft's own draw calls through Phase 7's hooks — which never reach the facade at all.
The named design element cannot satisfy that half of the contract row.

**What partially rescues it, and why that is not enough.** RESEARCH.md §4.4 *does* describe the
`countInstances` instancing loop only in the composite-pass line. So a composite-only restriction is
defensible **as observed reference behavior** — but the document neither states the restriction nor
cites §4.4 for it. It cites §3.2, App A.3 and App D.4, none of which support it. That is a semantic
fidelity gap between a mapped row and its cited text: the `depthtex1`-unit-11 class of error §G1.2
names as the thing a verify session exists to catch, and the four-site regression pattern the brief
predicted — one fix touching four places, all four inheriting the same over-narrow claim.

**Fix shape** (the fix-up's call): scope the §3 row to composite/deferred, cite §4.4 for the
restriction, and either name the owner of the non-composite case (Phase 7 owns the gbuffers draw
path; Phase 3 owns the directive scan that would detect it) or record it as an open item handed
onward. §4.7.4's row and §5.2's consumer attribution follow.

**Touches §5:** yes — §5.2's non-verbs row states the mechanism and assigns the consumer to Phase 5
(*"the composite owner"*) alone.

---

### V7-2 — the stated rung-2 protocol performs two drains per sweep, and four sites cost it at one `glGetError` · **correction** · §5 marginal

**Location.** §4.7.4 (l. 1575); §6's rung-2 row (l. 2219); §7 (ll. 2266–2267); `[D-P1-32]`
(l. 2644). §5.2's row (l. 2149) states the protocol but not the cost.

**Claim under test.** That the clean path costs *"exactly one query"*.

**Evidence.** The protocol is *"drain, upload the program's uniform set, drain"* — two calls to
`drainErrors()`. The backend's stated cadence is `glGetError` *"**once per `drainErrors()`**"*
outside debug mode (ll. 1560–1561). Two drains × one query = **two** `glGetError` calls per sweep,
not one. The four sites say one:

- §4.7.4: *"Empty — the ordinary case, every frame — and the sweep cost one `glGetError`."*
- §6: *"Phase 6 drains, uploads the set, and drains again; if that drain is empty the frame cost one
  `glGetError`."*
- §7: *"draining once per program set, one query per sweep, is the default … the clean sweep still
  costs exactly one query."*
- `[D-P1-32]`: *"the clean sweep still costs exactly one `glGetError`."*

**Why it is not pedantry.** The figure is load-bearing twice over. §7 uses it to argue
`[D-P1-32]`'s replay *"does not reintroduce [the query] on this path"*, and `[D-P1-32]` uses it to
justify rejecting the debug-mode-only alternative. A program switch is the universal state barrier
(RESEARCH.md §4.2) and there are 43 slots, so a factor of two on a **synchronous driver query** is
paid per program switch per frame — the exact cost §7 exists to bound. A Phase 6 session
implementing §5.2's protocol literally pays it.

**The claim is rescuable two ways, neither stated.** Either the leading drain is amortized — in a
per-frame sweep loop the trailing drain of set *N* is the leading drain of set *N+1*, making the
steady state one drain per set — or `drainErrors()` skips the query when no mutating call has
occurred since the last drain, which the backend can track with a flag. Both make "one query per
sweep" true. Neither is written down, and the protocol as stated in §5.2 does not permit a Phase 6
reader to infer either.

**Touches §5:** marginally — §5.2's protocol sentence is where the amortization would have to be
stated for Phase 6 to implement the one-query version.

---

### V7-3 — the corrected cadence is still imprecise about GL, and the imprecision attacks the attribution `[D-P1-32]` exists to establish · **correction** · **§5**

**Location.** §4.7.4's `[D-P1-30]` bullet, ll. 1558–1561; `[D-P1-32]`, ll. 1563–1571; §12 item 22
(l. 2840), which instructs the implementer to build it.

**Claim under test.** *"`Lwjgl3GLDevice` calls `glGetError` … **once per `drainErrors()`**"*, and
the GL fact it rests on: *"GL sets the error flag to the *first* error that occurs and records no
further error until `glGetError` clears it."*

**Evidence.** The document's account is right for the single-flag case, which is the common one, and
is a genuine improvement on the §0.5 text it replaced. It is not the whole rule. The OpenGL
specification allows an implementation to maintain **several** error flags, and states that
`glGetError` *returns and clears an arbitrary one* of them — which is why the specification's own
instruction is that `glGetError` *"should always be called in a loop, until it returns
`GL_NO_ERROR`, if all error flags are to be reset."* Specifying a **single** call per drain has two
consequences the document does not account for:

1. **A leaked flag is misattributed.** A flag left set by drain *N* surfaces at drain *N+1* and is
   attributed to *that* window. During `[D-P1-32]`'s replay — where each window deliberately holds
   exactly one upload — a leaked flag makes the *first* replayed window produce a spurious record,
   and Phase 6 disables an innocent uniform. The per-window attribution the whole decision was
   written to establish is what the single call undermines.
2. **`drainErrors()` cannot return what its own signature promises.** It returns `List<GLError>` and
   its javadoc says *"errors observed since the last drain, **in call order**"*; in the shipping
   cadence it can only ever hold zero or one element.

**The fix is free.** The loop form terminates on the first `GL_NO_ERROR`, so on the clean path —
every frame — it still costs exactly one query. It pays a second query only when there was something
to report. Adopting it costs nothing that §7's argument depends on and removes the cross-window
leak. (It does not fix V7-2, which is about the *number of drains*, not the cost of one.)

**A sub-point at the same site.** The sentence makes the cadence flip to per-call when *"any
`-Dschmaloogium.debug.*` flag is set"*. That includes `saveSources`, whose entry in §4.9.3's flag
table (l. 1930) describes only *"Dump fully-processed shader sources to disk"* and is owned by Phase
3. A Phase 3 developer enabling a source dump silently changes the facade's GL-error cadence and
per-frame query count; §4.9.3 nowhere says so. Either narrow the trigger to the flags that mean it
(`recordGL`, `glLabels`) or record the coupling in §4.9.3.

**Also worth recording:** the GL semantics claim carries no provenance tag and no cited source
anywhere in the document, and §0.6's reads list names no GL specification. That is how an imprecise
version of it survived a fix-up written specifically to correct the previous imprecise version.

**Touches §5:** yes — §5.2's GL-error row states the attribution as contract (*"this is contract,
not implementation detail"*), and the cadence that delivers it is the one in question.

---

### V7-4 — `[D-P1-32]`'s replay leaves three preconditions unstated, on a protocol §5.2 makes contract · **correction** · **§5**

**Location.** §4.7.4 ll. 1573–1583; §5.2's GL-error row (l. 2149); §6's rung-2 row (l. 2219).

**Claim under test.** That *"re-upload the set draining between uploads"* is a sufficient
specification for Phase 6 — §5 being *"written to be sufficient on its own"* (l. 2130).

**Evidence.** Three things a Phase 6 implementer must get right are not written down, and the brief
is correct that the fix-up did not address the first of them at all.

1. **Re-upload must reuse the values already computed for this sweep, not re-evaluate the
   providers.** "Re-upload the set" does not say which. App D's inventory includes uniforms whose
   value providers advance a halflife filter per sample — `wetness` (*"smoothed by
   wetness/drynessHalflife"*), `eyeBrightnessSmooth`, `centerDepthSmooth` — and RESEARCH.md App D's
   cadence model has them refreshing on program switch. Re-evaluating those a second time within one
   sweep double-advances the smoothing, producing a visible artifact on precisely the frame the
   engine is already degrading. Re-uploading *cached* values is safe (`glUniform*` is idempotent on
   the bound program); re-sampling is not. The document asserts neither.
2. **The protocol assumes the error reproduces.** `GLErrorKind` declares `OUT_OF_MEMORY` (l. 1513) —
   the one kind that need not recur on replay. If it does not, the replay finds nothing, Phase 6
   disables nothing, and rung 2 silently no-ops after having correctly detected a failure. §6's
   general row (*"not attributable to one uniform or feature | 3→4"*) is the natural home for that
   case but does not currently cover it.
3. **`GLError.subjectLabel` promises a uniform name nothing obliges a backend to retain.** The
   javadoc (ll. 1508–1509) says `subjectLabel` is *"the debug label of the handle **or the uniform
   name** involved"*. Handles get their label from `create(String debugLabel)`. `UniformLocation` has
   no such parameter and exposes only `isAbsent()` (ll. 1319–1322); the only place a name exists is
   the argument to `locate(p, name)`. A backend *can* retain it — it supplies the permitted
   implementation — but the facade never says it must, and rung 2's attribution is worthless without
   it. `[D-P1-29]` already set the precedent for stating a backend obligation as prose *"because no
   test can catch it"*; the same treatment is owed here.

**Touches §5:** yes — §5.2's GL-error row is where the protocol binds Phase 6, and (3) is a property
of a §5-exposed type.

---

### V7-5 — §4.2.4a's tying sentence is wrong in both directions · **correction** · no §5

**Location.** §4.2.4a, ll. 719–721. Introduced by the V5-3 fix-up, which cites it as *"what makes
the two claims consistent"*.

**Claim under test.** *"That path — `build` → `check` → `test` → `compileTestJava` — is also why
§4.11 orders the named seam steps **before** `./gradlew build` and does not scope the build step with
`-x test`."*

**Evidence.** The sentence is correct about `-x test` and wrong about the ordering.

- **The `-x test` half holds.** Gradle's `--exclude-task` drops the named task together with tasks
  reachable only through it. `compileTestJava` is a dependency of `test` and is not required by
  `assemble`/`jar`, so `build -x test` does skip it and the `:conformance` repository gate would
  indeed be disarmed. §4.11 item 3 states this correctly and on its own.
- **The ordering half does not.** The ordering's rationale is `[D-P1-24]`'s, stated at §4.11
  ll. 2069–2074 and §11.1 l. 2636: `build` runs the seam tests itself and would go red first under
  an anonymous name. That has nothing to do with `compileTestJava`.
- **And the stated causality runs backwards.** With the named `:conformance:test` step placed
  **first** (§4.11 item 2), a missing `:conformance` repository fails *there* — `test` depends on
  `compileTestJava` too — and the job aborts before `./gradlew build` ever runs. So under §4.11's own
  ordering, `build` **never reaches** `:conformance:compileTestJava` in CI, which is the opposite of
  what the sentence says the ordering exists to ensure.

**Not a design defect.** The gate remains armed: the named `:conformance:test` step catches the
missing repository, and catches it under a legible name, which is better than the outcome the
sentence describes. Only the explanation is wrong — but it is explanation a later session will
inherit as reasoning, which is why it is a correction rather than a note. Fix: delete the ordering
clause from the sentence and leave the `-x test` clause, which stands.

---

### V7-6 — `SeamClasspathArguments` has no stated home, and the two homes offered are not equivalent · **note** · no §5

**Location.** §4.2.3 ll. 563–573 (definition and the deferral), §4.2.4 ll. 647–652, §4.2.4a
ll. 702–707 (two more references). §12 items 5, 6 and 7.

**First, the ruling the brief asked for: this is _not_ the same defect as V5-2.** V5-2 concerned a
**facade verb** — a §5 cross-phase interface — added with no semantics, no §3 row, no §5 mention, no
§9 tag and no checklist item, in a section whose own rule is that silent additions are not cheap.
`SeamClasspathArguments` is build machinery, governed by no such rule, and its *wiring* is covered by
three checklist items: item 5 (*"classpath system properties"*), item 6 (*"classpath system
properties for item 14b"*) and item 7 (*"the three `schmaloogium.test.*` system properties C-2/C-3
read"*). And those items' "system properties" wording is **accurate, not stale** — a
`CommandLineArgumentProvider` on `jvmArgumentProviders` emits `-Dschmaloogium.test.*` arguments, so
system properties are exactly what the tests read. I checked the mechanism itself as well: annotating
the provider's inputs `@Classpath`/`@InputFiles` both defers resolution to execution *and* gives
Gradle the task dependency on Unimined's artifact-producing task, which is the right fix for the
hazard §4.2.5 describes. V5-8's narrowing was substantially correct.

**What is left is smaller and real.** §4.2.3 says *"where it lives — inline in the build script or in
`buildSrc` — is the implementation session's call."* Those two options are not interchangeable. A
class declared inline in a Gradle build script is compiled into **that script's** class scope; it is
not visible to a sibling subproject's build script, and a class declared in the root
`build.gradle` is not visible to `:engine/build.gradle` either. Since three build files instantiate
the type, "inline" means **three copies of the class**, while the next sentence — *"§4.2.4 and
§4.2.4a use the same form and point back here"* — reads as one shared definition. `buildSrc`, or a
shared script plugin under `gradle/`, is effectively mandatory, and neither exists in the template.

Fix shape: say so in §4.2.3, and give the class a checklist home (a sub-item under item 5, or a new
item ahead of it, since items 5, 6 and 7 all depend on it existing).

---

### V7-7 — §6's unnumbered rung is a real gap in DESIGN.md's ladder, and it is not surfaced where a gap belongs · **note** · no §5

**Location.** §6's unnumbered row (l. 2221); §11.3 and §11.5, which carry no corresponding entry.

**Claim under test.** Whether an unnumbered row satisfies §G2.4 line 225 (*"Every phase doc has a §6
mapping this ladder onto its subsystem"*), or quietly invents a category.

**It satisfies it.** All five rungs are mapped, each to its real owner: rung 1 at l. 2220 (Phase 11's
expression evaluator at v0.4, above the facade), rung 2 at l. 2219, rung 3 at l. 2218, rung 4 at
ll. 2216/2217/2223/2224, rung 5 at l. 2227 and in the section preamble. The unnumbered row is an
*addition* to a complete mapping, not a substitution for a missing one, and §G2.4 forbids no
addition. Nor is it invented quietly: the row states outright that the ladder has no rung for the
case and that labelling it would be inventing a step. Refusing to mislabel is the right call, and
round six's disposition of V6-2 was correct.

**What is missing is the other half of the obligation.** Having ruled that DESIGN.md's ladder does
not cover a real failure mode, the document should surface that ruling where the DESIGN.md
maintainer will see it. §G1.1 puts input contradictions in §3/§11 *"with your ruling and its
provenance"*, and §11.5 is the named home for requested upstream changes — it currently carries three
entries, none about the ladder. Without it, Phases 5, 6 and 13 will each meet the same case and each
independently re-derive the unnumbered category or mislabel it, which is exactly the sibling drift
§G5.3's integration review is described as structurally unable to catch.

Fix shape: one §11.5 entry proposing a rung (or an explicit "between 2 and 3" note) in §G2.4, citing
§6's row.

---

### V7-8 — the header date · **note** · no §5

**Location.** §0's `**Date:** 2026-07-24` (l. 10) against §0.6's *"Fix-up session addendum (rounds
five and six — **2026-07-25**)"* (l. 152). The fix-up left this open deliberately and said so at
ll. 203–205.

**Ruling: do not restamp, and do not leave it as-is either.** Restamping the header to 2026-07-25
would be worse than the current state, because the header date is the date the rest of the document
reads against: §4.1's template facts are *"read from the checkout on 2026-07-24"*, §4.2.6's thirteen
pin rows are re-verified 2026-07-24, and the `[V:repo]` tag is defined at l. 43 as inspection *"on
2026-07-24"*. A single 2026-07-25 stamp would silently re-date all of those to a day on which they
were not performed.

The correct form is both: *"Authored 2026-07-24; last revised 2026-07-25 (§0.6)."* That preserves
every dated claim below it and removes the discrepancy a reader currently has to reconcile from
§0.6's footnote. The fix-up was right that no finding had asked for it; this one does.

---

## 4. What came back clean

Named explicitly, because a seventh pass owes the reader the negative space more than the findings —
and because the brief is right that a round which manufactures findings to keep the cadence going is
worse than useless.

- **Every check §G1.2 lists, at the top level, passes.** Doc gate on all three criteria read
  literally. Template completeness: thirteen sections, all substantive. All four OQ spikes complete
  in all four §G4.4 parts, with the questions verbatim. Scope discipline in both directions. D-1…D-10
  each satisfied or deferred with an owner named. No binding decision contradicted.
- **The pins did not drift** — four rows checked live against their own Repository-column
  coordinates, and §4.2.6's two-source method executes exactly as written, the two sources agreeing.
- **`[D-P1-33]`'s reasoning is sound**, verified independently against the primary contract source
  rather than inherited. The verb deletion was the right call; only its replacement mapping is
  narrow.
- **Every claim this fix-up wrote into §5 from RESEARCH.md is accurate.** Phase 8's shadow-copy
  consumer (App B.2, §4.5), the recorder's supplied-log constructor, the ivec4 row, the ivec2 rows,
  the `ivec3`/`mat3` absence, the four App F.7 mappings — I re-derived all of them from source and
  agreed at every row. Round five's contract sweep holds on a second independent reading.
- **The Gradle work is correct.** `org.ow2.asm:asm` and `org.ow2.asm:asm-debug-all` are different
  `group:name` modules, so `resolutionStrategy.force` genuinely could not arbitrate between them and
  removing it loses nothing; an exclude declared on `testImplementation` does reach
  `testCompileClasspath`, because both resolvable test configurations extend it. `[D-P1-3]`'s
  split-package account is right, and §12 item 7's `dependencyInsight` hook over both configurations
  is the correct verification. `[D-P1-27]`'s per-`Project` repository reasoning holds.
- **The CI ordering itself works.** Named seam steps before `./gradlew build` does produce §12 item
  38's asserted property: a deliberate violation of C-1, C-2 or C-3 turns the first named step red and
  a violation of C-4 turns the second red, with the job aborting before `build` in each case, and the
  `if: failure()` report upload still running. Only §4.2.4a's *explanation* of the ordering is wrong.
- **The multi-GL-call question the brief raised comes back clean.** A backend verb that issues
  several GL calls does not break `[D-P1-32]`'s claim (a), because `op` is defined as *the facade
  verb* and the window is defined in terms of *mutating facade calls* — so the record still names the
  facade call that contained the failure. For rung 2 specifically the verb is one GL call anyway.
- **§5.2's GL-error row is sufficient for Phase 6 without reading §4.7.4**, which was the brief's
  fourth question about `[D-P1-32]`: the row carries the attribution rule, the full protocol, the
  shipping-configuration guarantee and the headless test hook. Its gaps are V7-2's and V7-4's, which
  are gaps in the protocol itself rather than in the row's coverage of it.
- **V6-5's `blit` narrowing was right**, and is not re-raised.
- **The six rulings the brief listed as settled were not re-litigated**, and nothing I found bears on
  any of them.
- **The `## Resolutions` record is complete for all six rounds**, and all fourteen of this fix-up's
  applied items land at every site they claim. Round six's prediction on that point was correct.

**The honest ratio.** Eight items: **five corrections, three notes, zero blocking**, on a 2 901-line
document at its seventh pass. Three of the five corrections (V7-2, V7-3, V7-4) are neighbours of a
single decision — `[D-P1-32]`, the largest piece of unreviewed design in the document, which is
exactly where the brief pointed and exactly where the yield was. One (V7-1) is the four-site
regression the brief predicted, in the one place the brief did *not* predict it: the deletion was
sound and the replacement was not. One (V7-5) is a sentence of inherited reasoning. The three notes
are one-paragraph tidying.

**And the shape of it has changed.** For the first time in six rounds, no finding concerns a missing
verb, a wrong appendix citation, an unmapped contract row, or an interface a dependent cannot reach.
Every correction here is about *precision in a protocol the document already got structurally right*.
That is what convergence looks like.

---

## 5. Verdict

**PASS-WITH-CORRECTIONS**

FAIL is reserved for structural misses requiring a rebuild (§G1.2), and there is nothing structural
here. The module split, the seam and its four constraints, the facade's granularity and handle model,
the pin machinery, the mixin wiring, the licensing work, the bail-registry mechanism and the whole §5
contract surface are sound and have now survived seven passes.

**PASS was genuinely available and I looked hard for it.** The brief was right that it was reachable:
the resolution record is complete, no prior finding is outstanding, the pins hold, and the contract
sweep agreed at every row on a second independent derivation. It is not available in fact, for two
reasons and only two. V7-1 is a conformance-map row whose named design element does not cover part of
its contract item — the category §G1.2 puts second in priority and describes as the reason verify
sessions exist. V7-3 is a GL-conformance defect in the cadence that carries `[D-P1-32]`'s central
promise, in a decision written specifically to correct an earlier GL error. Passing over either would
close the phase on the unreviewed material this round was commissioned to attack.

### Per-finding disposition

| Finding | Severity | Touches §5? | Consequence |
|---|---|---|---|
| **V7-1** `countInstances` mapped to a composite-only mechanism | correction | **yes** — §5.2's non-verbs row states the mechanism and names Phase 5 as sole consumer | forces round eight |
| **V7-2** two drains per sweep vs. the one-query claim | correction | **marginal** — §5.2's protocol sentence, if the amortization is stated there | rides with V7-3/V7-4 |
| **V7-3** single `glGetError` per drain is not the GL-sanctioned drain | correction | **yes** — §5.2's GL-error row states the attribution as contract | forces round eight |
| **V7-4** the replay's three unstated preconditions | correction | **yes** — same row; and (3) is a property of a §5-exposed type | rides with V7-3 |
| **V7-5** §4.2.4a's tying sentence | correction | no | fix-up only |
| **V7-6** `SeamClasspathArguments` home + checklist item | note | no | fix-up only |
| **V7-7** the unnumbered rung is not in §11.5 | note | no | fix-up only |
| **V7-8** header date | note | no | fix-up only |

**Four of the eight do not touch §5 at all**, and close permanently with the fix-up that applies
them. The §5 surface is opened on exactly two axes: §5.2's **non-verbs row** (V7-1) and §5.2's
**GL-error row** (V7-3, with V7-2 and V7-4 landing inside it). No new axis, and — as with the last
round — **no service signature needs to be added or removed by any of these**: V7-1 is a scope
qualifier and an owner, V7-2 is a cost statement, V7-3 is a backend cadence, V7-4 is three sentences
of precondition. The facade's verb list is untouched by this entire findings list.

### §G1.3 line

**`PHASE_1_DOC.md` is NOT verified.**

§G1.3 makes a phase verified only when its latest verdict is PASS, or PASS-WITH-CORRECTIONS *"with
all resolutions recorded and no §5 change outstanding."* The verdict is PASS-WITH-CORRECTIONS, the
resolutions for V7-1 … V7-8 are not yet recorded, and a §5 change is unavoidable: V7-1 forces an edit
to §5.2's non-verbs row and V7-3 forces one to §5.2's GL-error row, independent of every choice the
fix-up gets to make. The *"re-verify only if §5 changed"* rule will therefore fire again, and an
**eighth verify session** must run before Phase 2, Phase 3 or any other dependent consumes this
document (§G5.3). Until then Phase 2 and Phase 3 cannot start.

One thing recorded alongside that, because the shape of the remaining work matters more than the
count. Unlike rounds six and seven, the next pass inherits **no unapplied list and no re-derivation
debt**: the resolution record is complete for six rounds, this round's findings are specified down to
the fix shape, the contract fidelity of §5 has now been independently re-derived from RESEARCH.md end
to end by three separate sessions and found correct at every row, and the §5 edits owed are two rows
of prose in one section. If the fix-up applies these eight and records them, the eighth pass has a
narrow, well-defined surface to check and PASS is the expected outcome.

Two requests to the fix-up session, since this round exists because fix-up sessions get no
adversarial review of their own. First: V7-3 turns on a GL specification fact the document currently
asserts with no source. Cite it. Second: V7-1 and V7-4 both require a judgement the reviews have not
made for you — who owns the non-composite `countInstances` case, and whether Phase 6 re-uploads
cached values. Record the reasoning in §0.7, as §0.6 did, so round eight audits an argument rather
than an outcome.

Per §G1.2 this session stops here and fixes nothing.

---

## Resolutions

*Recorded by the fix-up session of 2026-07-25 (§G1.3). **Nothing above this heading was modified.**
All eight findings are applied: five corrections, three notes, **none narrowed and none refused** —
the first round at which that is true of a whole list. The two judgement calls this round handed to
the fix-up (V7-1's owner, V7-4's cached-value question) and the citation it asked for (V7-3) are
recorded as arguments in the document's new **§0.7**, per the two requests at the end of §5 above.*

### V7-1 … V7-8

| Finding | Disposition | Where |
|---|---|---|
| **V7-1** `countInstances` mapped to a composite-only mechanism | **Applied, at all four inherited sites plus two new homes.** The §3 row is scoped to **composite/deferred** and now **cites RESEARCH.md §4.4** — *"optional sub-viewport (`scale.<prog>`), `countInstances` instancing loop"* — as the source of the restriction, which is the citation the finding correctly identified as missing. A **second §3 row** carries the other half honestly: on a gbuffers/shadow program the geometry is vanilla's, drawn through Phase 7's hooks and never reaching the facade, so **no Phase 1 design element satisfies it** and none is invented. Ownership is split and named in a new `[D-P1-35]`: **Phase 3** detects the directive in its `const`-scan, **Phase 4** carries it as the per-slot instance count (§4.2's "instance count", all 43 slots), **Phase 7** owns the re-render itself. §11.4 gains a Phase 7 hand-off stating plainly that the case is **open, not designed** — RESEARCH.md observes no non-composite instancing loop, so there is no reference behavior to be faithful to — and §11.4's Phase 3 entry gains the detection clause. `[D-P1-33]`'s rationale records why the mapping was over-narrow (three sources cited, none restricting the directive; the one that restricts the loop uncited) | §3 (two rows), §4.7.4 (`DrawService` javadoc + absent-verbs row), §5.2 (non-verbs row), §11.1 (`[D-P1-33]` amended, `[D-P1-35]` new), §11.4 (Phases 3 and 7) |
| **V7-2** two drains per sweep vs. the one-query claim | **Applied by making the claim true, not by weakening it.** The finding named two rescues; a third was available and is what shipped. `[D-P1-30]` now states a **backend obligation**: `drainErrors()` issues **no `glGetError` at all** when no mutating facade call has occurred since the previous drain — one bit, set by every mutating call, cleared by every drain. Program sets swept back-to-back therefore cost **one** query for the protocol's two drains, and — unlike caller-side amortization, which was considered and rejected — the property is **self-correcting**: an intervening mutating call re-arms the leading drain exactly when the window needs bounding, and Phase 6 is asked for no discipline at all. All four sites that costed the sweep at one query now say *why* it is one. §7's paragraph in particular no longer asserts the figure; it derives it, and names the 43-slot-per-frame reason the factor of two would have mattered | §4.7.4 (`[D-P1-30]`, `[D-P1-32]`), §5.2 (GL-error row), §6 (rung-2 row), §7, §9, §11.1 (`[D-P1-30]`, `[D-P1-32]`), §12 item 22 |
| **V7-3** single `glGetError` per drain is not the GL-sanctioned drain | **Applied, and the missing provenance is the part that mattered most.** A drain is now a **loop terminating on `GL_NO_ERROR`**, quoted and cited: *"To allow for distributed implementations, there may be several error flags … If more than one flag has recorded an error, `glGetError` returns and clears an arbitrary error flag value. Thus, `glGetError` should always be called in a loop, until it returns `GL_NO_ERROR`, if all error flags are to be reset"* — tagged `[V:web]`, the OpenGL `glGetError` reference page, wording identical in the GL 2.1-era and GL 4 refpages, **read live 2026-07-25** and disclosed in §0.7. This is the document's first GL specification citation, which is precisely the gap the finding named as how the imprecision survived. The loop costs nothing clean (it stops at the first `GL_NO_ERROR`) and removes the cross-window leak that would have made the **first** replayed window disable an innocent uniform. `drainErrors()`'s javadoc no longer promises "in call order" unconditionally — under the default cadence the list is a set of **flags**, not a sequence. **The sub-point is applied too:** the per-call cadence trigger is narrowed from *any* `-Dschmaloogium.debug.*` flag to **`recordGL` and `glLabels` only**, and §4.9.3 records the coupling on all four flag rows plus a paragraph saying which two carry it and why the other two must not | §4.7.4 (`drainErrors()` javadoc, `[D-P1-30]`), §4.9.3 (four rows + note), §5.2, §11.1 (`[D-P1-30]`), §12 item 22 |
| **V7-4** the replay's three unstated preconditions | **Applied, all three, and (1) is the design call the reviews left open.** (1) The replay **re-uploads the values already computed for this sweep and never re-evaluates the providers** — `wetness`, `eyeBrightnessSmooth` and `centerDepthSmooth` advance a halflife filter per sample, so re-sampling double-advances the smoothing on exactly the frame the engine is degrading, while `glUniform*` is idempotent on the bound program and re-uploading cached values changes only which drain window an upload lands in. The reasoning is in §0.7. (2) A replay that **reproduces nothing** (`OUT_OF_MEMORY` need not recur) is unattributable and falls to §6's *"not attributable to one uniform or feature"* row, which now says so on both sides — rung 2 degrades to rung 3's shape rather than to a silent no-op. (3) New **`[D-P1-34]`** states the backend obligation the finding identified: a `UniformLocation` implementation **retains the name passed to `locate(program, name)`**, because `GLError.subjectLabel` promises a uniform name and no signature carries one. Written as prose on `[D-P1-29]`'s precedent, with §12 item 22's review hook extended to check it — no signature changed | §4.7.4 (`GLError` javadoc, `[D-P1-32]`), §5.2 (GL-error row, three lettered properties), §6 (rung-2 and 3→4 rows), §11.1 (`[D-P1-32]` amended, `[D-P1-34]` new), §12 item 22 |
| **V7-5** §4.2.4a's tying sentence | **Applied by deletion of the wrong half only.** The `-x test` clause stands and is now stated on its own mechanism (`--exclude-task` drops tasks reachable only through the excluded one; `compileTestJava` is required by `test` and not by `assemble`/`jar`). The ordering clause is deleted, with an italic note recording *why* it was wrong in both directions — the ordering's rationale is `[D-P1-24]`'s, and under §4.11's own ordering the named `:conformance:test` step fails first so `build` never reaches `:conformance:compileTestJava` in CI at all. The gate's behavior is unchanged and remains armed under a legible name; only the inherited explanation was defective. §4.11 item 3's parallel sentence was re-grounded on the same mechanism (and on §12 item 15's **local** run, which is where `build` does reach that compile) rather than left implying the CI ordering | §4.2.4a, §4.11 item 3 |
| **V7-6** `SeamClasspathArguments` has no stated home | **Applied.** §4.2.3 no longer offers inline and `buildSrc` as equivalent: a script-declared class lives in *that script's* class scope, so with three build files instantiating the type "inline" means **three copies**, which contradicts the very next sentence. The home is now `buildSrc` **or** a shared script plugin under `gradle/` — that choice remains the implementation session's, "inline" is not — and the section states that neither exists in the template yet. A new **§12 item 4b** gives the class its own checklist home ahead of items 5, 6 and 7 (the `14b` numbering precedent, so nothing downstream renumbers), and those three items now point at it. Their "system properties" wording was **not** changed: this round confirmed it accurate, since a `CommandLineArgumentProvider` on `jvmArgumentProviders` emits exactly those `-D` arguments | §4.2.3, §12 items 4b (new), 5, 6, 7 |
| **V7-7** the unnumbered rung is not in §11.5 | **Applied.** §11.5's DESIGN.md list goes from one entry to two: item 4 asks §G2.4 for a rung covering *"a single feature's GL call fails"* — or an explicit note that the case exists between rungs 2 and 3 — citing §6's unnumbered row, and states the cost of leaving it (Phases 5, 6 and 13 each re-deriving the category or mislabelling it, the sibling drift §G5.3 describes the integration review as unable to catch). §6's row now points at the request, so the local ruling and the upstream ask are reachable from each other. The row itself is unchanged: this round confirmed that refusing to mislabel was correct | §6 (unnumbered row), §11.5 (new item 4) |
| **V7-8** the header date | **Applied in the form the finding prescribed — both dates, not a restamp.** The header reads **"Authored: 2026-07-24 · Last revised: 2026-07-25 (§0.7)"**, with an italic line stating why a single later stamp would have been worse: §4.1's template reads, §4.2.6's thirteen pin rows and `[V:repo]`'s own definition are all dated 2026-07-24 and would have been silently re-dated to a day on which the work was not performed. §0.6's footnote deferring the question to round seven is closed rather than left dangling | §0 header, §0.6 |

### §5 status, and the §G1.3 line

**§5 changed at exactly two rows, and no signature changed anywhere.** §5.2's **non-verbs row**
(V7-1's composite scope and the Phase 7 owner) and its **GL-error row** (V7-2's cost, V7-3's cadence,
V7-4's three preconditions). Round seven's own prediction was exact: *"no service signature needs to
be added or removed by any of these"* — the facade's verb list, all seven service interfaces, the
four handle types and every value type are byte-for-byte what round seven reviewed. §5.2's opening
row records that explicitly so the eighth session can check the claim in one place.

Because §5 changed, §G1.3's *"re-verify only if §5 changed"* rule fires and **`PHASE_1_DOC.md`
remains NOT verified**. An **eighth verify session** must run before Phase 2, Phase 3 or any other
dependent consumes it (§G5.3). This was unavoidable from the moment V7-1 and V7-3 were raised, and
round seven said so.

What the eighth session inherits, stated plainly because the shape of the remaining work matters
more than the count: **no unapplied findings and no re-derivation debt.** The resolution record is
complete for all seven rounds; every one of round seven's eight items is applied at every site its
*Location* field named; the §5 surface opened is two rows of prose in one section; and the three
things this session decided rather than inherited — the `countInstances` ownership split, the drain
elision, and re-uploading cached values — are recorded with their arguments in §0.7 rather than as
bare outcomes, so round eight can attack the reasoning.

*Per §G1.3 this fix-up session stops here. It wrote no code, ran no build and no test, and modified
exactly two files: `PHASE_1_DOC.md` and this `## Resolutions` section. `DESIGN.md` and `RESEARCH.md`
are unmodified — the requests against them are in §11.5.*

---
