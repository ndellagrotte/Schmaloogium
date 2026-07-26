# PHASE_1_DOC.md — Verify session, round twelve

**Phase:** 1 — Foundation & project architecture
**Document under review:** `docs/phase1/v11/PHASE_1_DOC.md`, 4574 lines.
**Design revision:** `docs/design/v2.0-RC2/DESIGN.md` — the revision this document declares at its
l. 12 and the revision the harness pins for phase 1 (`PHASE_FACTS[1].design`,
`.claude/workflows/verify-loop.js` l. 114). **Every `DESIGN.md` line number in this review is RC2's.**
Every `PHASE_1_DOC.md` line number is the reviewed file's, re-resolved at the line after the review was
written.

**Verdict: PASS-WITH-CORRECTIONS** — nine corrections, three notes, zero blocking. **Four corrections
alter §5**, so §G1.3's re-verify trigger fires again. §3 carries the disposition table and the §G1.3
line.

The distribution is the honest headline and it is not a regression: §0.11's own closing paragraph
predicted it. **Eight of the nine corrections sit in material no verify session has seen** — §4.12,
§4.13, §4.5.2a, the two new §5.1 rows, §12 items 22b/23/30a, and §0.11 itself — and four of the nine
land on the single decision `[D-P1-36]` that round eleven's `## Resolutions` named as *"where a twelfth
session should start"*. The parts eleven rounds have swept came back clean under the checks I ran on
them (§2).

---

## 0. What I read, and in what order

Per §G1.2 step 1: the build session's assigned reading first, then the document, then — and only then
— the prior rounds.

1. `docs/design/v2.0-RC2/DESIGN.md` Part I: §G0.2's path table (ll. 130–133), §G0.3 (135–143), §G0.4
   (145–176), §G1.1 (188–255), §G1.2 (257–300), §G1.3 (302–320), §G5.1's phase table (565–588),
   §G5.3 (611–638).
2. `DESIGN.md` Part II: the **Phase 1 spec** (957–1067) in full, including its *Scope — in*
   (971–1035), its *Required inputs* (1040–1047) and its **Doc gate** (1056–1060). Beyond the
   assigned list — each read disclosed in §0.1 with the finding it turned on — Phase 5's *Scope — in*
   and *Scope — out* (1480–1500) and Phase 6's *Scope — in* (1545–1610).
3. `docs/research/v1/RESEARCH.md`: §3.4's compile-flow and pre-link-attribute bullets (495–505),
   §4.3 (509–526), App B.3's unit map (1227–1248), App E row 17 (1414).
4. `docs/phase1/v11/PHASE_1_DOC.md` — the header and §0.1 (1–66), §0.11's closing block (955–1064),
   §1.2 (1115–1130), §2.1's `:mod` table and §2.4's key-type table (1180–1262), **§3 entire**
   (1264–1300), §4.5.2 and §4.5.2a (1985–2100), §4.3's JPMS paragraph (1830–1850), §4.7.3
   (2315–2355), §4.7.4's `ShaderService` (2405–2420), `FramebufferService` and `TextureService`
   (2436–2495), §4.7.5's head (2800–2815), **§4.12 and §4.13 entire** (3323–3476), **§5 entire**
   (3479–3540), §9's milestone table (3780–3800), §11.1's new decision rows (4060–4072), §11.4's
   Phase 7 and Phase 6 blocks (4240–4340), **§12 entire from item 16** (4470–4529), the closing note
   (4531–4574). Section inventory checked by heading against §G9.
5. `reference-src/pintonium-9c2fcc1/`, read only to verify claims the document builds on:
   `common-shaders/…/MinecraftVersionShimService.java` (the framebuffer members, ll. 125–128),
   `common-shaders/…/com/mitchej123/glsm/GLStateManagerService.java` (complete),
   `common-shaders/…/com/mitchej123/glsm/RenderSystemService.java` (declarations),
   `common-shaders/…/org/taumc/celeritas/api/v0/CeleritasShadersApi.java` (l. 1) and the
   `org/taumc/celeritas/` directory listing,
   `forge122/…/impl/MinecraftVintageVersionShimImpl.java` ll. 505–516,
   `forge122/…/mixin/shaders/startup/Mixin{GameSettings,InitRenderer,GuiMainMenu}.java` (the
   `@Mixin`/`@Inject` lines), `settings.gradle.kts` ll. 118–130, and the top-level directory listing.
   `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md` **§2 only** (ll. 70–102).
6. **Then** `docs/phase1/reviews/PHASE_1_REVIEW_11.md` in full, including its `## Resolutions`
   (ll. 510–664).
7. **Then** the earlier rounds by targeted grep across all eleven review files, reading the passages
   the greps returned: `PHASE_1_REVIEW_2.md` ll. 325–345 (V2-5), `PHASE_1_REVIEW_3.md` ll. 200–216 and
   480–490 (F3-6), `PHASE_1_REVIEW_4.md` ll. 270–300 and 860–870 (F3-6's ruling and F4-7's
   resolution), `PHASE_1_REVIEW_6.md` ll. 155–170 and 460–465, `PHASE_1_REVIEW_7.md` ll. 100–235.
   Greps run over all eleven: `sealed`, `permits`, `unmapped`, `bindAttributeLocation`,
   `copyDepthToTexture`, `shaders.txt`, `common uniform block`.

### 0.1 Reads beyond the assigned list, each with the finding it turned on

- **`DESIGN.md` Phase 5's spec (1480–1500) and Phase 6's *Scope — in* (1545–1610).** V12-1 turns on
  both: they are the ground truth for which phase owns *which texture object backs each unit*. §G1.1
  l. 200 bars a **build** session from other phases' specs; a verify session auditing an ownership
  claim this document makes *about* another phase has no such bar, and the read is disclosed
  regardless. This is the standing precedent §0.8 records and round eleven used for the same purpose.
- **`docs/MOVES.md` (complete) and `.claude/workflows/verify-loop.js` ll. 100–135, plus `git log`,
  read-only.** V12-8 turns on all three: the document asserts, as current fact, what those files say
  about the governing revision and the version roll. No git command that writes was run, and neither
  file was modified.
- **`reference-src/pintonium-9c2fcc1/` beyond the six files §0.1 of the document names** — the
  `MinecraftVintageVersionShimImpl` shim implementation (V12-6), `GLStateManagerService`'s and
  `RenderSystemService`'s member lists (V12-5), and the `api/v0` directory (V12-7). §G11.3's
  search exclusions were observed; nothing was copied, and no `celeritas-shader-refactor.zip`
  recovery was attempted.
- **MCP `cleanroom` `search_mappings("initializeTextures")`**, one call, for V12-11's second limb —
  the javadoc of the method §4.13's stage-2 rationale characterises.

### 0.2 Deviations, and omissions recorded as omissions

- **Forbidden sources honoured.** No directory named `chatlogs/` below `docs/` was opened, and no
  `*.txt` at the repository root was opened — including the one currently sitting there, which is
  larger than several of the documents in this review. §G1.1 ll. 245–249 states the rule by pattern;
  `PHASE_1_REVIEW_9.md` §0.2 records the round-nine sub-agent breach and the discard that followed.
  Not repeated: the fan-out ran under the same rule and no citation below resolves to either pattern.
- **Not re-derived, and named rather than glossed:** §4.2 (Gradle), §4.4, §4.6, §4.8–§4.11, §6, §7,
  §8, §10's four spike specs, §11.2's D-1..D-10 table, §11.3, §11.5, and §12 items 1–15 and 24–45
  except where a candidate landed in them. Eleven rounds have swept these. §2 states what *was*
  derived and by whom.
- **Template ground truth was not opened this round** (`build.gradle`, `settings.gradle`,
  `gradle.properties`, `gradle/scripts/*`, `src/**`, `.github/workflows/*`, `README.md`). No finding
  turns on a template fact. Recorded as an omission, not as coverage.
- **No build, test or gradle invocation.** No file was created except this one.

### 0.3 Network use

**None of any kind.** No finding turns on a platform fact that is not in the checkout or the MCP
mapping index.

### 0.4 Sub-agent disclosure

This round ran as an **automated fan-out of roughly 34 read-only agents** under a mechanised
re-derivation gate: candidate finders swept assigned regions, every candidate was put to **two
independent refuters**, and a gate re-resolved each citation against the file before the candidate
reached this session. **Fourteen candidates survived to me.** The gate confirms anchors only — an
anchor check is not a finding, and none of the findings below is admitted on the fan-out's report.
Every claim in §1 was re-opened at its source by this session; where I report the fan-out's coverage
rather than my own, §2 says so.

**What the refutation stage changed, and where I disagree with it.** Of the fourteen, one arrived
proposed *blocking* and was argued to a note; six arrived *correction* and survived; six were argued
from *correction* to *note*; one was already a note. I re-derived all fourteen. **I restored two of
the six demotions to correction** (V12-4, the unnamed provider interface — because §12 item 22b makes
the undeclared type a Phase 1 v0.1 deliverable, and §G1.2's own third check is precisely "specified,
not gestured at"; and V12-5, the half-run completeness check — because it is a **Doc gate** criterion
and §G1.2 ranks the doc gate first and requires it met *literally*). **I merged the blocking candidate
into its own correction-severity twin** (both were the sealed-`TextureHandle` problem, one framed as a
compile failure and one as a §5 defect — they are one defect and are reported once, as V12-3).
**I cleared one candidate outright** on my own derivation, and it appears in §2 rather than §1
(the §3 unmapped-rows candidate — §2 item 1). I do not have the pre-gate candidate list, so I cannot
enumerate what the gate dropped before it reached me; that limit is stated rather than papered over.

---

## 1. Findings

### V12-1 — §5.1's new provider row makes the vanilla-texture set Phase 6's; RC2 gives that exact half to Phase 5, and the §1.2 row the claim cites says "Phase 5 **and** Phase 6" · **correction** · **touches §5: yes**

**Location.** §5.1's `[D-P1-36]` row, **l. 3505**; §4.12's disposition, **l. 3400**; §11.4's "To
Phase 6" block, **ll. 4328–4331**. Against `DESIGN.md` ll. 1487–1489 and 1563, and against this
document's own §1.2 l. 1124, §3 l. 1284 and `[D-P1-36]` l. 4070.

**Claim under test.** That the binding §5 row names the phase RC2 makes responsible for *which
texture objects back which App B.3 units* — the question the provider slot's contents answer.

**What the document says.**

- l. 3505: *"The slot is named here, its **contents are Phase 6's** (the unit map is its policy,
  §1.2)"*, with the consumer column reading *"**6** (defines the set it needs, with the unit map),
  **13** …, 5"* — Phase 5 bare and last.
- l. 3400: *"Its *contents* are **Phase 6's** — the unit map's binding is Phase 6 policy (§1.2)"*.
- l. 4330: *"which textures the set needs is a question the fixed unit map answers and the unit map is
  yours (§1.2)"* — addressed to Phase 6.

**What the sources say, each opened at the line.**

1. `DESIGN.md` **ll. 1488–1489**, inside **Phase 5's** *Scope — in*: *"(ownership shared with Phase 6:
   **you own which texture object backs each unit per stage**; Phase 6 owns pointing sampler uniforms
   at units)"*. The half at issue — which object backs a unit — is Phase 5's by name.
2. `DESIGN.md` **l. 1563**, Phase 6's *Scope — in*: its unit-map duty is *"**Sampler re-pointing**: on
   every use-program, sampler uniforms re-point to the App B.3 fixed unit map"*. Phase 6's spec
   carries **no** texture-binding bullet at all, so it is not the phase that can enumerate a set of
   vanilla texture objects.
3. **§1.2 l. 1124 — the authority the row cites — does not say what the row claims:** *"All GL
   *policy*: texture formats, **the fixed texture-unit map**, … | **Phase 5** (buffers) and
   **Phase 6** (uniforms/samplers)"*. Joint, in the document's own words.
4. The document contradicts itself twice more, and in the correct direction: §3's App B.3 row
   (**l. 1284**) ends *"Which textures the map needs, and the map itself, stay **Phase 5/6** policy
   (§1.2)"*, and `[D-P1-36]` itself (**l. 4070**) reads *"because the unit map is **Phase 5/6**
   policy"*. §4.7.4's sibling javadoc (**l. 2488**) attributes unit policy to *"Phase 13/5"*. So the
   §5 row — the text a dependent builds against — is the outlier against three internal statements and
   against RC2.
5. `DESIGN.md` **l. 631** names *"the P5/P6 texture-unit-map split"* as one of the shared-ownership
   seams §G5.3's integration review must cross-check. This is that seam, mis-cut in the one place a
   dependent reads it.

**Why it matters beyond tidiness.** `[D-P1-36]` exists so that no dependent discovers at its own v0.1
that units 0 and 1 have no source. As written the row hands that discovery to the wrong phase: Phase 6
would be told to enumerate a set RC2 does not give it, and Phase 5 — which owns the per-stage
object→unit assignment and is listed bare and last in the consumer column — is not told it owns
anything here. §11.4 has no "To Phase 5" block for the slot.

**Fix.** Name **Phase 5** as the phase that defines the vanilla-owned texture set (`DESIGN.md`
l. 1488), with **Phase 6** as the sampler-pointing counterpart and **Phase 13** for the `_n`/`_s`
companions; state §1.2's row as the joint ownership it is; align §4.12 l. 3400 and §11.4 with it, and
add a "To Phase 5" hand-off. Note that this is the *narrower* claim, not a new assignment: §3 and
`[D-P1-36]` already say "Phase 5/6".

**Touches §5: yes** — l. 3505's text and its consumer column both change.

---

### V12-2 — §5.1 tells Phase 7 that bring-up stage 3 is a **requirement** on its hook catalog; the same cell, §9, §4.13 and §11.4's own enumeration all call it a recommendation Phase 1 declines to wire · **correction** · **touches §5: yes**

**Location.** §5.1's `[D-P1-37]` row, **l. 3504**; §11.4's topic sentence, **l. 4245**. Against
§4.13 point 3 (**ll. 3464–3468**), §9's milestone row (**l. 3789**) and §11.4 l. 4252.

**Claim under test.** That the §5 row states the obligation it places on Phase 7 at the strength the
rest of the document assigns it.

**What the document says.** Inside one cell, l. 3504 says both:

- *"**(3)** `GuiMainMenu.initGui` at `RETURN` is the "loading complete" signal, **recommended and not
  wired** — Phase 1 has no consumer for it"*, and
- *"because **stages 2 and 3 are requirements on Phase 7's hook catalog** rather than mixins this
  phase authors (§4.5)"*, with the consumer column reading *"**7** (owns the catalog entries for
  stages 2 and 3)"*.

**What the rest of the document says.**

- §9 **l. 3789**: *"**stage 3 is recommended, not wired** — Phase 7 places it **when its frame driver
  has a use for it**"* — discretion, not obligation.
- §4.13 **l. 3467**: *"Wiring it at v0.1 would be a hook with no caller."* It goes *"to §11.4 as a
  named recommendation … not into §9 as a v0.1 component"*.
- §11.4 **l. 4252**: *"**Stage 3 is a recommendation:**"*, and l. 4254 *"Phase 1 deliberately does
  **not** wire it"*.
- `[D-P1-37]` **l. 4071**: stage 3 *"adopted as a signal but **left to Phase 7 to place**"*.

**And §11.4's topic sentence miscounts against its own enumeration.** l. 4245 opens *"**Two** of the
three bring-up stages are requirements on your hook catalog, and one is only a recommendation"* — then
enumerates exactly one requirement (stage 2, l. 4247), one recommendation (stage 3, l. 4252) and one
deviation (stage 1, l. 4255). Three stages cannot be two requirements plus one recommendation when one
of them was deviated from. The error is the same one as in the §5 row, which is why this is one
finding at two sites rather than a slip.

**Why it matters.** §5 is the text Phase 7 builds against, and the two readings produce different
catalogs: "requirement" obliges an App E row for `GuiMainMenu.initGui` at Phase 7's v0.1; the rest of
the document leaves it to Phase 7's judgement. §4.13's stage-1 argument spends D-5 injection budget as
its currency, so the requirement/recommendation distinction is load-bearing rather than stylistic.

**Fix.** In l. 3504: *"stage 2 is a requirement on Phase 7's hook catalog; stage 3 is a recommendation
Phase 1 does not wire"*, and adjust the consumer-column gloss. In l. 4245: *"one of the three stages is
a requirement on your hook catalog, one is a recommendation, and one was deviated from"*.

**Touches §5: yes** — l. 3504 is a §5.1 row.

---

### V12-3 — the shape §5.1 "fixes" cannot be written in Java: `mod.glue` may not implement `engine.gl.TextureHandle` while that interface is `sealed` and JPMS is rejected · **correction** · **touches §5: yes**

**Location.** §5.1's `[D-P1-36]` row, **l. 3505**; §4.12's disposition, **ll. 3397–3399**;
`[D-P1-36]`, **l. 4070**; §12 item 22b's acceptance criterion, **l. 4481**. Against §4.7.3
**ll. 2322–2342** and §4.3's JPMS paragraph, **ll. 1839–1846**.

**Claim under test.** That *"the shape is fixed: `mod.glue` implements `TextureHandle` for these
textures"* is a buildable contract, and that item 22b's gate — *"The provider compiles"* — can be met
as the design is stated.

**What the document says.**

- l. 2322: the handles live in `package com.schmaloogium.engine.gl;`.
- l. 2330: `public sealed interface TextureHandle extends GLHandle permits …  {}` — **sealed**, so its
  implementations are restricted to a `permits` list.
- l. 2340: *"**Each backend supplies the permitted implementations**: `Lwjgl3GLDevice` wraps ints;
  `RecordingGLDevice` wraps synthetic sequence numbers; a hypothetical Kirino backend wraps whatever
  it uses."*
- l. 3505 / l. 3398: *"`mod.glue` implements `TextureHandle` for these textures so the raw GL name
  never crosses C-1"*, and *"**No facade verb and no §4.7.4 signature is added**"*.
- l. 4481: *"The provider compiles; `SeamBytecodeTest` (item 12) still passes, **which is the whole
  test of the shape**"*.

**Why it does not compile.** For a sealed class or interface, every permitted subtype must be in the
same **module** as the sealed type — or, when the sealed type is in the **unnamed module**, in the same
**package**. §4.3 l. 1839 settles which case applies: *"**JPMS was considered and rejected.**"* — no
`module-info.java`, so `:engine` compiles into the unnamed module and the same-**package** rule binds.
The document's own placement table then rules out every named implementer: `Lwjgl3GLDevice` *"(+ its
service impls)"* is in `mod.glue` (**l. 1257**, and §2.1 **l. 1188**), `RecordingGLDevice` is in
`com.schmaloogium.engine.gl.record` (**l. 2807**), and `com.schmaloogium.mod.glue` is neither the
sealed types' package nor even the same Gradle project. So l. 2340's sentence is not implementable as
written, and l. 3505 promotes the same impossibility into a cross-phase contract.

**Why this is not `PHASE_1_REVIEW_3.md` F3-6 again.** Round three raised the dead
`RenderbufferHandle` permit and round four ruled on it at **note** severity, holding that *"the §4.7.3
listing is deliberate non-compilable pseudo-Java (lines 1045–1048 carry literal `permits …`
ellipses)"* and that *"it would not compile"* was not an escalation route
(`PHASE_1_REVIEW_4.md` ll. 279–283). That ruling covers the **ellipsis**, and I accept it. It does not
cover this: **no filling-in of the ellipsis can make a `mod.glue` class a permitted subtype.** The
ellipsis is what hides the defect rather than what causes it, the arrangement l. 2340 describes is the
substance and not the listing, and `sealed` has never been examined against the JPMS rejection in any
of the eleven rounds (greps for `sealed` and `permits` over all eleven review files return only the
renderbuffer-permit thread and incidental prose).

**Also worth stating: the two available repairs have different costs, and neither is free.** (a)
Dropping `sealed` from the four sub-interfaces keeps the opaqueness property where it actually lives —
no raw-int accessor, plus `SeamBytecodeTest` — which is §4.3 l. 1845's own argument for why a
compiler-enforced boundary is unavailable here; but it edits §4.7.3's declarations, so l. 3397's
*"costs no signature"* and §5.2's changelog claim both need restating. (b) Declaring in
`com.schmaloogium.engine.gl` one permitted, `non-sealed` extension point that `mod.glue` may implement
keeps the sealing and is the smaller edit — and it is also the type V12-4 says is missing, so one edit
closes both. **Do not resolve it with an int-taking engine-side handle constructor:** that is the same
raw-GL-name-in-an-`:engine`-signature leak `[D-P1-36]` refuses `adopt(int)` for, and adopting it would
retract the row's own argument.

**Fix.** Pick (a) or (b) in §4.7.3, say which in §5.1 l. 3505 and `[D-P1-36]`, and re-state item
22b's *"The provider compiles"* against the chosen shape. The fix is not confinable to the new row:
l. 2340 is wrong about `RecordingGLDevice` too.

**Touches §5: yes** — l. 3505's stated shape changes, and under branch (a) the four exposed handle
declarations in §4.7.3 change with it (§5.2's "Opaque handle types" row and its changelog).

---

### V12-4 — the provider interface `:engine` receives vanilla handles through is named nowhere, while §12 item 22b makes it a Phase 1 v0.1 deliverable · **correction** · **touches §5: yes**

**Location.** §5.1's `[D-P1-36]` row, **l. 3505**; §4.12 **l. 3398**; §12 item 22b, **l. 4481**; §9's
milestone row, **l. 3790**; §11.4 **l. 4328**. Against §5's own standard at **l. 3491** and §G1.1
**l. 252**.

**Claim under test.** That naming the slot in §5.1 is enough for a dependent to build against it —
i.e. that this row meets §5's *"sufficient on its own"* promise and §G1.2's *"everything promised to
dependents is specified, not gestured at"*.

**What the document says.**

- l. 3491: *"This section is nevertheless written to be **sufficient on its own** — every obligation
  this document places on another phase appears here"*.
- l. 3398 is the entire specification of the mechanism: *"supplies them through **a provider**"*. No
  interface name, no method, no package, no registration point, no statement of who installs it or
  when. `grep -n provider` over the whole document returns no declaration — §4.7.3 and §4.7.4 add
  nothing, and every other §5 row names its types (`LogSink`, `DiagnosticReporter`, `CompatCheck`,
  `ScriptedResponses`, `BlitSpec`, `PixelLayout`).
- l. 4481 nevertheless schedules *"plus **the provider interface `:engine` receives them through**"*
  as a **v0.1** checklist item whose gate is *"The provider compiles"*. So the interface is this
  document's to declare, and it is undeclared.
- The consumer side is fully specified: l. 2491 `void bindToUnit(int unit, TextureHandle t);`.

**Why it matters.** §G1.1 l. 252 forbids the only route a Phase 5/6 build session has left: *"If it is
missing something you need, flag the request in your doc §5; **do not invent the missing interface as
if it existed**."* So the gap converts into a §5 change-request round trip — the exact cost
`[D-P1-36]` was written to spare a dependent. It is also where the module direction is decided and the
document does not decide it: a provider **declared** in `mod.glue` and consumed by `:engine` inverts
the `:mod → :engine` edge (§2.2, C-1), so the interface must be an `:engine` type — which is what
makes it Phase 1's, and what makes its absence a defect rather than a deferral.

**The document's own standard is the sharpest argument against leaving it.** `[D-P1-33]` deleted
`fullscreenQuadInstanced` partly because *"it carried no semantics, no §3 row, no §5 mention, no §9
tag and no checklist item, in a facade whose own rule is that silent additions are not cheap"*
(l. 4067). This is the exact inverse: a §3 row, a §5 row, a §9 tag and a checklist item for a type
with no declaration.

**Fix.** Declare the interface where the four handle types live, with a signature — something of the
shape `interface ForeignTextureProvider { TextureHandle handleFor(ForeignTexture which); }` — plus
where `:engine` holds it and at which lifecycle point `mod.core` installs it (§4.13 can now name the
point), and reference that type **by name** in l. 3505 and item 22b. Under V12-3 branch (b) the same
declaration closes both findings. If Phase 1 genuinely intends to leave the declaration to a
dependent, say so in the row and drop item 22b's claim that Phase 1 ships it at v0.1 — but then
`[D-P1-36]`'s "no dependent discovers this late" rationale is the thing that has to be re-argued.

**Touches §5: yes** — l. 3505 gains the type name; §5.2's inventory gains an `:engine` type.

---

### V12-5 — the mandated completeness check covers only half the inventory the spec names: the glsm state services were never enumerated · **correction** · **touches §5: no**

**Location.** §4.12, **ll. 3332–3365**, in particular the scope sentence at **l. 3357** and the path
table at **l. 3339**; §0.1's Pintonium-source row, **l. 34**. Against `DESIGN.md` ll. 998–1003 and the
**Doc gate** at ll. 1058–1059.

**Claim under test.** That the Doc-gate criterion *"glue-seam completeness check against PD §2's
inventory present"* (l. 1059) is met **literally**, which is the standard §G1.2 step 2 sets for the
doc gate and the check it ranks first.

**What the spec says the inventory is.** `DESIGN.md` **ll. 999–1000**: *"PD §2's service inventory —
`MinecraftVersionShimService`'s method list (camera/world/dimension/framebuffer/biome/weather
accessors) **plus the glsm state services** — is a field-tested checklist of what a version-facing
glue seam must cover."* Two parts, both named as the checklist.

**What the check does.** §4.12 **l. 3357**: *"**Every member of `MinecraftVersionShimService`** was
placed in one of four buckets, and the buckets are the answer."* `GLStateManagerService` and
`RenderSystemService` appear only as a path row at l. 3339; no member of either is bucketed, and
§0.1 **l. 34** confirms why — they were opened for *"existence and package"* only. So the second half
of the named inventory was never read as a member list, and the check's headline result
(*"the one gap"*) is a result about half the checklist.

**The unexamined half is material, and it lands on this phase's own surface.** `GLStateManagerService`
is 92 lines and ~59 declarations; `RenderSystemService` 65 lines and ~31. Opened at source, they carry
exactly the members a `StateService` audit would want and several the facade has no verb for:
`getColorMask()` (**l. 89**), `getDepthStateMask()` (l. 86), `getBlendMode()`, `getBoundTexture(int)`
(l. 80), `getActiveTexture()` and the viewport getters — the state-**read** members `snapshot()` /
`restore()` are built out of — plus `glPixelStorei` (l. 71), `glGetTexLevelParameteri`,
`glCopyTexSubImage2D`, `glGenVertexArrays` (l. 36) and `glGenBuffers`/`glBindBuffer`. None appears in
any §4.12 bucket or in §4.7.4's absent-verbs table. Several would plainly bucket as "already served"
or "not ours", which is fine — the defect is that the exercise was not run, not that it would have
found more gaps.

**The document's own argument invites the comparison rather than excluding it.** ll. 3350–3355 rest
the check's design on *"Their seam abstracts **the game**; ours abstracts **the game and GL**"* — and
the glsm services are the half of their seam that abstracts **GL**, i.e. the half whose counterpart is
§4.7.4. That is the strongest reason a reader will expect them bucketed and the weakest place to leave
them unmentioned.

**Also relevant, and in the document's favour on the narrow reading:** PD §2's own sentence
(`PINTONIUM_DESIGN.md` l. 96) offers only *"the interface inventory (`MinecraftVersionShimService`'s
method list)"* as the checklist. So a reading of *"PD §2's inventory"* that stops at the shim is
defensible **against PD** — but the criterion is scored against the spec, and the spec's own
definition of that inventory is two-part.

**Fix — either branch closes it.** (a) Run the missing half: bucket the two services' members against
`StateService`/`ShaderService`/`TextureService`/`FramebufferService` and record what falls out
(`glPixelStorei` against `upload`, the snapshot-read members against `StateAspect`, the
buffer-object/VAO verbs against the absent-verbs table). Or (b) state at §4.12's head that the check is
scoped to the shim, **why** the glsm services are excluded — their members are GL verbs the facade owns
directly rather than glue accessors, which is a good reason — and add the exclusion to §0.3's
deviations, so the criterion is met in the letter it is written in.

**Touches §5: no** — branch (b) is §4.12 and §0.3 only. Branch (a) touches §5 only if it finds a gap,
which is exactly what running it would establish.

---

### V12-6 — `bindMainFramebuffer` is bucketed "already served" by `bindDefault`, which names a different framebuffer and carries no javadoc saying which · **correction** · **touches §5: no**

**Location.** §4.12's "Already served" bucket row, **l. 3362**. Against §4.7.4's `FramebufferService`
(**ll. 2436–2477**, especially the undocumented **l. 2448**).

**Claim under test.** That the shim's main-framebuffer pair is served by
`FramebufferService.bindDefault(target)` — the basis on which §4.12 reports the vanilla-texture handle
as the check's **only** positive result.

**What the document says.** l. 3362 answers *"`bindMainFramebuffer`/`unbindMainFramebuffer`"* with
*"`FramebufferService.bindDefault(target)`"*, treating the pair as served by one verb.

**What the reference does.** `forge122/…/impl/MinecraftVintageVersionShimImpl.java`:

- **l. 506** — `bindMainFramebuffer()` → `CLIENT.getFramebuffer().bindFramebuffer(true);` — binds
  **Minecraft's own framebuffer object**.
- **l. 511** — `unbindMainFramebuffer()` → `CLIENT.getFramebuffer().unbindFramebuffer();` — returns to
  the default framebuffer (name 0).

They are opposite operations, and one verb cannot be both. On the natural reading of `bindDefault`
(the GL default framebuffer, name 0) the cell serves the *unbind* half and nothing serves the bind
half; on the other reading (the platform's default target, i.e. MC's FBO) it serves the *bind* half and
nothing returns to 0. **The document never says which**, because l. 2448 is the one verb in the
`FramebufferService` block with no javadoc, and `bindDefault` occurs at exactly two places in the whole
document — that declaration and this bucket cell.

**Why it is not merely a naming quibble.** §4.7.4 offers no way to obtain a `FramebufferHandle` for a
framebuffer the engine did not create, so `bind`, `blit`, `copyDepthToTexture` and `readDepthPixel`
cannot take Minecraft's main FBO as an argument — **the same shape of hole §4.12 just found for
`TextureHandle`**. And the target is contract-relevant: `RESEARCH.md` **l. 526** — *"**Final** renders
to the vanilla framebuffer (anaglyph-aware color masking)"* — and App E row 17 (**l. 1414**) catalogs
`Framebuffer.bindFramebuffer(Z)V` as the hook for the *"final-to-screen handoff"*. So "which
framebuffer is 'default'" decides whether a contract-driven target is expressible.

**The document already has the right disposition pattern for this, one table away.** §4.7.4's
colour-mask absent-verb row (**l. 2795**) handles the analogous case honestly: *"If Phase 7 drives that
through vanilla's own path it needs no verb; if it wants it through the facade, the request is additive
and this row is where it starts"* — with Phase 7 named as the requester.

**Fix.** Give l. 2448 a javadoc stating which framebuffer it binds, and re-bucket the pair on that
answer: if `bindDefault` is name 0, add an absent-verbs row for "bind a framebuffer the engine did not
create" with its named requester (Phase 7 for the final-to-vanilla handoff, Phase 5 if it needs the
main FBO as a blit source); if it is the game's target, say so and note that nothing returns to 0.
Either way §4.12's *"one gap"* becomes *"one gap in the texture-handle direction"*.

**Touches §5: no** on the javadoc-plus-re-bucket branch — l. 2448's declaration does not change.
**It would touch §5** if the fix-up chooses to add an absent-verbs row, because §5.2's non-verbs row
enumerates that table's contents; that is a reason to prefer stating the semantics first and letting
the requester row follow, not a reason to leave the cell as it is.

---

### V12-7 — the "one PD coordinate corrected" showpiece is itself wrong: `org.taumc.celeritas.api.v0` **does** exist in the checkout · **correction** · **touches §5: no**

**Location.** §4.12 **ll. 3343–3348**; repeated in §0.1's Pintonium-source row, **l. 34**, and in
§0.11's bullet at **ll. 996–998**.

**Claim under test.** The document offers this as *"the cheapest possible demonstration that the
verification happened"* (l. 3347): that PD §2's *"`org.taumc.celeritas.api.v0.CeleritasShaderVersionService`
— note the **`api.v0`** segment; **the checkout has no such package on that path**"*.

**What the checkout holds.**
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/org/taumc/celeritas/api/v0/CeleritasShadersApi.java`
**l. 1**: `package org.taumc.celeritas.api.v0;`. The package exists, on exactly that path, in exactly
the module (`common-shaders`) whose `src/main/java` root the document's own table at ll. 3339–3341
uses for the other three seam interfaces. It holds `CeleritasShadersApi`, a `ServiceLoader`-based
public API surface. `org/taumc/celeritas/` contains precisely two entries: `api/` and
`CeleritasShaderVersionService.java`.

So PD's error is the **class's package**, not a nonexistent package: the interface sits directly in
`org.taumc.celeritas` while `api.v0` is a real, adjacent API surface — most plausibly a conflation of
the two. The `[V:observed]`-class claim as written is false, and the diagnosis it supports is the wrong
one. That the coordinate is wrong at all — the point §0.1 l. 34 and §0.11 l. 997 both rest on — stays
true.

**Fix.** Narrow the correction to what is true: *`CeleritasShaderVersionService` sits directly in
`org.taumc.celeritas`; `org.taumc.celeritas.api.v0` exists in the same module and contains
`CeleritasShadersApi`, so PD §2's coordinate conflates two adjacent API surfaces.* That is a sharper
demonstration than the current one and does not itself need correcting later. §0.11's bullet needs the
same one-clause edit.

**Touches §5: no.**

---

### V12-8 — §0.11's "Three states this document is now in" and the header's l. 13 are false against the repository they describe · **correction** · **touches §5: no**

**Location.** §0 header **ll. 12–15**; §0.11's disclosure block **ll. 1038–1057**; repeated in the
closing note **ll. 4566–4571**.

**Claim under test.** The document states, as current fact and as a briefing for this session, that
only §G0.4 step 3 has been performed, that `docs/MOVES.md` and the `/verify-loop` harness both still
read v1.1, that *"v1.1 remains the project's governing design"*, and that the `v10` → `v11` directory
roll is still owed.

**What the document says.**

- l. 13: *"**RC2 is an unadopted candidate and v1.1 remains the project's governing design**"* —
  present tense, in the header.
- l. 1044: *"So `v1.1` remains the *governing* design for the project — **`docs/MOVES.md` still says
  so, and the harness still reads it**"*.
- l. 1050: *"**The `v10` → `v11` directory roll is owed and was deliberately not performed.**"*, with
  l. 1054's *"The rule that `v<K>` equals the highest `§0.K` addendum is now unsatisfied at `v10`,
  knowingly"*.

**What the repository says.** All three named artefacts were changed after the fix-up, in commit
`afeceda` *"verify-loop: finish the §G0.4 adoption the round-eleven fix-up left at step 3"* (the commit
immediately after `c108630`, the fix-up itself):

1. **Step 1 is done.** `.claude/workflows/verify-loop.js` l. 114 reads `design: 'v2.0-RC2'` for phase
   1, with a complete per-revision pin set (`DESIGN_PINS`) and `spec: '957-1067'`,
   `docGate: '1056-1060'` — RC2's own numbers. This session was briefed in RC2 coordinates, and they
   match the document, so the stop-and-report instruction at ll. 1046–1049 fired on a premise that is
   no longer true.
2. **Step 2 is done.** `docs/MOVES.md` — the file l. 1044 cites as saying otherwise — records RC2 as
   *"**governs Phase 1** from its §0.11 on"* (l. 68) and states at l. 76 *"**There is no longer one
   governing revision**, and no longer one set of line numbers"*, with the per-phase resolution rule at
   ll. 83–88. Step 4 is recorded too, as a **ruling** rather than a rename: ll. 126–145 keep the `-RC`
   suffix and sharpen the rule to *"`-RC` drops when **every** downstream phase doc has adopted the
   revision"*.
3. **The roll is done, both halves.** The document now lives at `docs/phase1/v11/PHASE_1_DOC.md`;
   MOVES.md l. 108 records *"Rolled from `v10` on 2026-07-26"* and l. 123 *"performed 2026-07-26, both
   steps together, with no loop running"*; the harness carries `docVersion: 'v11'` (l. 113). The one
   dangling reference the roll cost is recorded at MOVES.md ll. 154–163 and l. 177.

**Why it matters.** l. 1050's disclosure would send a maintainer to run
`git mv docs/phase1/v10 docs/phase1/v11` against a directory that no longer exists, and l. 13 tells
every future reader the wrong thing about which revision governs — while citing, as its evidence, a
file that says the opposite. The only outstanding item of the four steps is the one MOVES.md names:
`PHASE_2_DOC.md` has not migrated.

**The precedent to apply, and the one not to.** §0.10's head note settles that a stale coordinate
inside a *historical record* is a smaller defect than a rewritten record, and V11-5 took exactly that
branch. §0.11's block is not a historical citation — it is a present-tense statement about other files
and an instruction to this session — and l. 13 is the header, which no reader treats as a dated
record. So the fix is to **date and narrow**, not to rewrite the history: say what was true at the
fix-up and what has since landed.

**Fix.** Re-state the block against the current tree: §G0.4 steps 1, 2 and 3 are complete for Phase 1,
step 4 is settled by MOVES.md's partial-adoption ruling (the `-RC` label is retained deliberately), the
roll is done, and the only outstanding item is `PHASE_2_DOC.md`. Correct l. 13 to MOVES.md's per-phase
form. Keep the stop-and-report instruction — it is good practice — and drop its false premise. The
closing note ll. 4566–4571 needs the same treatment.

**Touches §5: no.**

---

### V12-9 — `[D-P1-38]`'s drift test is specified over **nested** mixin packages, so it fails on a correct config set at the first moment it has anything to check · **correction** · **touches §5: no**

**Location.** §4.5.2a **ll. 2093–2094**; §12 item 30a, **l. 4500**. Against §4.5.2's package table
**ll. 1993–1995** and its own observation 1 at **ll. 2045–2053**.

**Claim under test.** That the insurance taken *"instead of"* Pintonium's class-scan plugin — *"the
whole of what the rejection owes"* (l. 4500) — is specified so that it can pass on a correct
configuration.

**What the document specifies.** l. 4500: *"every `@Mixin`-annotated class **under a config's declared
`package`** appears in that config's arrays, and every array entry resolves to such a class"*;
l. 2093 states it the same way. The scope is the package **subtree**.

**Why that fails on correct input.** The three declared packages are **nested**, and §4.5.2 says so in
its own words at **l. 2045**: *"The DEFAULT config's mixin package `com.schmaloogium.mod.mixin` is the
**parent** of the other two configs' packages"* — the table at ll. 1993–1995 gives
`com.schmaloogium.mod.mixin.preinit` (PRE_INIT), `com.schmaloogium.mod.mixin` (DEFAULT) and
`com.schmaloogium.mod.mixin.compat` (MOD). So every PRE_INIT and MOD mixin is also *under* DEFAULT's
declared package. Phase 10's expected first tenant — a `@Mixin` class in `…mixin.preinit`, listed
correctly in `schmaloogium.preinit.mixin.json`'s arrays and correctly absent from DEFAULT's — makes the
test **red on a correct config set**, in the one situation it exists to police. Item 30a's own hook
concedes the test *"passes vacuously at v0.1"*, so the first non-vacuous exercise is the first failure.

**Fix.** Scope the assertion to what a config actually claims: for each config, the `@Mixin` classes in
its declared package **excluding any sub-package declared by another config** must equal its arrays.
Say so in both §4.5.2a and item 30a, and cite §4.5.2 observation 1 as the reason, so that a later
reader does not "simplify" it back. (Observation 2's plugin class needs no exception: an
`IMixinConfigPlugin` is not `@Mixin`-annotated.)

**Touches §5: no** — §5.3 exposes the placement rule, not this test's predicate.

---

### V12-10 — §4.13 counts "two deviations" where §5.1, §0.11 and `[D-P1-37]` all count one · **note** · **touches §5: no**

**Location.** §4.13 **l. 3417** and its banner at **l. 3446**. Against §5.1 **l. 3504**, §0.11
**l. 972** and `[D-P1-37]` **l. 4071**.

**Evidence.** l. 3417: *"this section adopts it with **two deviations**, both argued"*; l. 3446:
*"**Adopted, with two deviations.**"* — followed by three numbered points of which exactly one
(point 2, stage 1 → the FML lifecycle) is a deviation; point 1 is *"adopted as-is"* and point 3 is
*"adopted as a signal and deferred as a placement"*. Against that: §5.1 l. 3504 says *"with **one
deviation**"* and then enumerates three stages; §0.11 l. 972 says *"adopts two of PD §16's three
bootstrap stages and **deviates from the first**"*; `[D-P1-37]` l. 4071 says *"with **stage 1
deviated** to the FML lifecycle"*. §4.13 is the outlier of four statements about one decision, and
§5.1 — the binding one — is on the majority side.

**Why a note rather than a correction.** Counting stage 3's deferral as a second deviation from PD
§16's *sequence* is defensible in itself; the defect is that only one of the four sites counts it that
way, and the one that matters to a dependent already says "one". A reader is not misled about
substance.

**Fix.** In §4.13 say *"one deviation and one deferred placement"*; or, if the deferral is meant to
count, make §5.1 and `[D-P1-37]` say two. One word, either way.

---

### V12-11 — §12 item 23's new ordering check cannot detect the ordering it claims, and §4.13's stage-2 rationale misdescribes the method it rests on · **note** · **touches §5: no**

**Location.** §12 item 23's test hook, **l. 4482**; §4.13 point 1, **ll. 3448–3454** (and the same
sentence restated in §5.1 l. 3504 and §11.4 ll. 4249–4250).

**Evidence, first limb.** l. 4482 asks the item to prove *"**the probe runs after vanilla's texture
setup rather than before** — a profile with a plausible `GL_MAX_TEXTURE_IMAGE_UNITS` is the cheap
signal"*. `GL_MAX_TEXTURE_IMAGE_UNITS` is an implementation-dependent constant of the GL context: it
is queryable from the moment a context exists and nothing Minecraft does changes it. The value can
therefore distinguish *"a context existed"* from *"no context"* and nothing else — a probe placed
**before** `initializeTextures` but after display creation passes this hook. The check as written
cannot fail in the direction it was added to police.

**Evidence, second limb.** l. 3450 grounds the choice of site on
`OpenGlHelper.initializeTextures` @`RETURN` being *"the earliest 1.12.2 moment at which a GL context
exists **and vanilla's own texture setup has completed**"*. MCP `search_mappings` gives that method
(SRG `func_77474_a`) the javadoc *"Initializes the texture constants to be used when rendering lightmap
values"* — it fixes lightmap/default texture-unit constants and vanilla's own GL capability flags; it
is not the completion of vanilla's texture setup, and a GL context exists earlier. **The site is still
the right one** — it is the reference's proven point and it is where vanilla itself has finished
detecting GL capabilities, so a probe at `RETURN` sees the context vanilla sees — but not on the stated
ground. This is a rationale defect, not a design defect, which is why it is a note.

**Fix.** Re-ground the sentence on what the method does (where vanilla detects GL capabilities and
fixes its texture-unit constants), and replace the hook with something that can fail: assert the
probe's log line appears after the `initializeTextures` marker, or that `CapabilityProbe` throws when
invoked before stage 2 has fired — rather than inspecting a driver constant that is invariant across
the boundary.

---

### V12-12 — §3's gbuffers/shadow `countInstances` row asserts a `doc/shaders.txt` structural fact inside a cell whose only named sources are RESEARCH.md · **note** · **touches §5: no**

**Location.** §3's gbuffers/shadow `countInstances` row, **l. 1293**, Provenance column.

**Evidence.** The cell reads *"… App A.3 state it as a **vertex-stage** opt-in … ; `uniform int
instanceId` sits in the *common* uniform block, above the GBuffers heading; RESEARCH.md §4.2 lists
'instance count' …"* — the middle clause sits between two RESEARCH.md citations and names no source, so
a reader attributes it to RESEARCH.md. RESEARCH.md places `instanceId` under **App D.4**
("Per-draw dynamics", l. 1369) and contains no "common uniform block" or "GBuffers" heading anywhere.
The claim is true of a different document — `schlorbium-project/doc/shaders.txt`, where l. 182's
`uniform int instanceId;` sits under the "Uniforms" block and above "GBuffers Uniforms" — which is the
actual, unnamed source. §11.4 **l. 4312** names it for the identical claim, so §3 drops an attribution
the document elsewhere makes.

**What the finder got wrong, and I record it because it changes the size of the fix.** The candidate
argued that no session discloses reading `shaders.txt`. It is disclosed: §0.7's V7-1 bullet, **l. 257**,
cites *"`doc/shaders.txt` puts `countInstances` under *Vertex Shader Configuration*"* as that fix-up's
own derivation, and `PHASE_1_REVIEW_7.md` ll. 38 and 222–230 are where the reading happened. §G7.3
makes it a freely citable contract source. So only the **in-cell attribution** is missing, not the
disclosure — a one-clause fix, and the reason this is a note.

**Fix.** Name the source in the cell as §11.4 already does, and optionally add the file to §0.1's
inputs table with the fix-up session that read it.

---

## 2. What was checked and came back clean

Named because a round reporting only findings misrepresents its coverage, and because on this document
an item that holds on derivation *is* part of the round's product. Items 1–6 are my own derivations;
item 7 reports the fan-out's coverage as the fan-out's.

**1. Cleared on my own derivation: the candidate that §3 leaves the contract's depth copies and
pre-link attribute binding unmapped.** The candidate argued that `FramebufferService.copyDepthToTexture`
(l. 2468) and `ShaderService.bindAttributeLocation` (l. 2413) each answer a contract item — App A.3's
`depthtex0/1/2` row plus App B.2's copy semantics, and RESEARCH.md l. 500's *"Attributes bound pre-link
at fixed locations (10/11/12)"* — with no §3 row naming that item, while their siblings `readDepthPixel`
and `TextureService.upload` each have one. It does not survive the standard two prior rounds already
set on exactly this ground. `PHASE_1_REVIEW_3.md` ll. 200–216 ran an item-by-item contract sweep and
cleared *"§4.2's pre-link attribute binding at 10/11/12 → `bindAttributeLocation`"* by name, on the
reading that "unmapped" means **unserved by a verb**; `PHASE_1_REVIEW_11.md` §2 item 3 re-derived that
standard and dropped the same candidate, adding the second test — a §3 row is owed where *this phase*
takes a contract-visible **design decision** that needs provenance. Both verbs pass both tests:
`copyDepthToTexture` is served, and every contract-bearing parameter (source, destination, region,
moment) is the caller's — l. 2466 puts the call *"mid-frame between two draws into the main FBO"* and §5.2's pixel-transfer row (l. 3520) assigns the `depthtex1`/`depthtex2` copies to Phase 5;
`bindAttributeLocation` is a bare verb whose locations are Phase 4/10's, and §3's `maxVertexAttribs`
row already names 10/11/12 as the consumer. Re-raising a candidate two rounds cleared on a standard I
agree with is loop maintenance, not review. **Not a finding.**

**2. Doc gate, criterion by criterion, against `DESIGN.md` ll. 1056–1060.** *"module/package layout
finalized with dependency rules as testable constraints"* — §2.1's three package tables plus §4.3's
C-1…C-4 with a named test each: **met**. *"every D-1..D-10 either satisfied by this phase or
explicitly deferred with its owner phase named"* — §11.2's table carries all ten with a disposition
each: **met** (eleven rounds have audited it; I checked its presence and shape, not each row).
*"pin table complete with re-verification procedure"* — §4.2.6's thirteen rows plus §10.1's
procedure with its three terminating rulings: **met**. *"bootstrap sequence adopted or deviation
justified"* — §4.13 adopts PD §16 and argues the stage-1 deviation from D-5's budget and from
§4.9.1/§4.10's existing FML placement: **met** (V12-10 is a count inside it, not a gap). The
**glue-seam completeness check** criterion is the one that is half-met, and is V12-5.

**3. §4.13's reference table is exactly right at source, and I opened all three files myself.**
`MixinGameSettings` is `@Mixin(GameSettings.class)` + `@Inject(method = "loadOptions", at =
@At("HEAD"))` (ll. 11, 16); `MixinInitRenderer` is `@Mixin(OpenGlHelper.class)` +
`@Inject(method = "initializeTextures", at = @At("RETURN"))` (ll. 10, 12); `MixinGuiMainMenu` is
`@Mixin(GuiMainMenu.class)` + `@Inject(method = "initGui", at = @At("RETURN"))` (ll. 10, 12) —
including the `HEAD` detail PD omits, which l. 3429 correctly flags as this document's own addition.
§4.12's module-count paragraph likewise checks out at the tree: the checkout holds `forge1710/`,
`forge122/` and `modern/` plus `common/` and `common-shaders/`; `settings.gradle.kts` l. 121 declares
`forge122` as a stonecutter project over `1.12.2` and `1.10.2` and l. 126 declares `babric` over four
versions with no checked-out content — so the sharpening the document claims over REV2's wording is
real, and its stated limit (*"a directory-and-settings observation, not a build audit"*) is honest.

**4. The new material's contract grounding is sound where it matters most.** RESEARCH.md App B.3
(ll. 1229–1246) really does carry sixteen unit rows with **unit 0 = `texture`** and **unit 1 =
`lightmap`** on GBUFFERS/SHADOW (ll. 1231–1232), both Minecraft-owned, neither producible by
`TextureService.create(String)` — so `[D-P1-36]`'s gap is real and not manufactured, and the refusal of
`adopt(int glName)` on the seam is the right call for the reason given (`[D-P1-15]`, §4.7.3, and
`SeamBytecodeTest`'s blindness to it). Everything wrong with that decision is downstream of a correct
finding: V12-1 is its ownership attribution, V12-3 its handle shape, V12-4 its missing type.
§4.13's §G11.4 contract check is correctly reasoned as well: a bring-up ordering is not
contract-visible under §G4.2, and RESEARCH.md §4.1's steps 1–4 impose an *ordering* rather than an
instant, so "probe before discovery, init lazy" is compatible rather than contradictory.

**5. The interface-honesty check in the consumed direction is vacuously clean, and I verified the
premise.** `DESIGN.md` §G5.1 l. 567 gives Phase 1 *"Depends on: —"*, and §5 l. 3481 and §0.2 l. 66
both say so. There is no dependency §5 to audit against, which is why *Interface honesty* ran in one
direction only this round — and that direction is where V12-4 lives.

**6. Structural checks.** All thirteen §G9 sections are present as `##` headings in order (0 Header …
12 Implementation checklist) at ll. 5, 1090, 1143, 1264, 1328, 3479, 3571, 3595, 3660, 3769, 3814,
4029, 4434. §9's milestone table gained rows for both new components (ll. 3789–3790) with the stage-3
*"recommended, not wired"* distinction carried correctly — §9 is on the right side of V12-2. §11.1
carries all three new decisions (ll. 4070–4072), each with the `[REV2 migration — §0.11; DESIGN.md …]`
marker and a `DESIGN.md` coordinate; I re-resolved those coordinates and ll. 998–1003, 1007–1009,
1010–1014 and 1056–1060 all say what the markers claim. §12 gained items 22b, 30a and an amended 23,
so the checklist is not out of step with §4/§5 (its defects are V12-4, V12-9, V12-11, not omission).
§11.4 has the new Phase 6 block and the Phase 7 stage-2/stage-3 hand-off §4.13 promises.

**7. Reported by the fan-out and not independently re-derived by me, marked as such.** §5.2's
signature honesty — the seven services, `drainErrors()`, the four handle types plus
`UniformLocation.isAbsent()`, the three result types, `StateService`'s verb list, the pixel-transfer
overloads, `RecordingGLDevice`'s two constructors, `GLCallLog`'s three factories, all seven
`ScriptedResponses` factories and all six `ReplayAssertions` — was swept member by member against
§4.7.2–§4.7.5 by two agents with no defect found, so the changelog row's claim that **no signature
moved this revision** holds. §3's twenty-five rows were re-resolved against RESEARCH.md and
`DESIGN.md` by the fan-out with no semantic-fidelity defect found (the `blendFunc` row's five-passage
Phase 9 non-attribution and the `scale.<prog>` row's four ownership quotes were each re-derived in RC2
coordinates); I re-opened §3's App B.3 row, its two `countInstances` rows and its `ivec4` row myself.
§4.5.2a's account of the class-scan plugin was checked against `mixins.celeritas.json` and
`CeleritasVintageMixinPlugin.getMixins()` by the fan-out and matches; its rejection argument from
`[D-P1-11]`'s three-config split is sound on my own reading, and V12-9 is about the replacement test,
not the rejection. Provenance-tag discipline across the new prose — every PD-derived claim carrying
`[V:observed — Pintonium <path>]` or `[V:observed — PD §n]`, with the document saying at each site
which applies and why — was checked by the fan-out and spot-checked by me at ll. 3334, 3369, 3421 and
1293; §G11.4's form is used correctly, and V12-7 is a false claim inside a correctly-tagged sentence
rather than a tagging failure.

---

## 3. Verdict

# PASS-WITH-CORRECTIONS

**Nine corrections, three notes, zero blocking.** No finding is blocking and none requires a rebuild:
every one is a bounded edit to §4.7.3, §4.12, §4.13, §4.5.2a, §5.1, §0.11 or §12.

**PASS was not available, and the reason is structural rather than a matter of taste.** Round eleven's
fix-up added ~250 lines of entirely unreviewed design — two new subsections, three new decisions, three
new checklist items and **two new §5.1 rows** — and priced that honestly in its own closing note. Eight
of the nine corrections are in that material, and four of them land on the one decision
(`[D-P1-36]`) that round eleven's `## Resolutions` named as where this session should start. Two of
those four are not stylistic: the shape §5.1 calls *fixed* cannot be written in Java (V12-3), and the
interface §12 schedules Phase 1 to ship at v0.1 is declared nowhere (V12-4). A third gives a
dependent's work to the wrong dependent, against RC2's explicit split and against three statements
this document makes elsewhere (V12-1). Calling any of those a note to reach PASS would be the inverse
of what this cadence guards against.

**Equally, nothing was manufactured to fill a round.** One candidate was cleared outright and is
recorded in §2 with the two prior rounds whose standard clears it; six candidates were argued down to
notes by their refuters and I sustained four of those demotions; three of my twelve findings are notes,
recorded and **left unapplied**. The mature ninety-five per cent of this document — §4.2 through
§4.11, §6 through §8, §10, §11.2–§11.5, and every facade signature — came back clean under every check
run on it, by me and by the fan-out.

### Per-finding §5 disposition

| Finding | Severity | Touches §5? |
|---|---|---|
| **V12-1** §5.1 l. 3505 makes the vanilla-texture set Phase 6's; `DESIGN.md` l. 1488 gives that half to Phase 5, and §1.2 l. 1124 says both | **correction** | **yes** — the row's text and consumer column; §4.12 l. 3400 and §11.4 l. 4330 follow it |
| **V12-2** §5.1 l. 3504 calls stage 3 a *requirement* on Phase 7's catalog; the same cell, §9 l. 3789 and §11.4 l. 4252 call it a recommendation | **correction** | **yes** — l. 3504 is a §5.1 row; §11.4 l. 4245's count goes with it |
| **V12-3** `mod.glue` cannot implement `sealed engine.gl.TextureHandle` (unnamed module ⇒ same package, §4.3 l. 1839); l. 2340 asserts the same for `RecordingGLDevice` | **correction** | **yes** — l. 3505's stated shape, and under the unseal branch the four exposed handle declarations in §4.7.3 |
| **V12-4** the provider interface §12 item 22b ships at v0.1 has no name, method, package or installation point | **correction** | **yes** — l. 3505 must name the type; §5.2's `:engine` inventory gains it |
| **V12-5** the Doc-gate completeness check covers the shim only; the glsm state services named in `DESIGN.md` l. 1000 were never bucketed | **correction** | **no** — §4.12 and §0.3 on either fix branch |
| **V12-6** `bindMainFramebuffer` bucketed "already served" by an undocumented `bindDefault` that names a different framebuffer | **correction** | **no** on the javadoc-plus-re-bucket branch (l. 2448's declaration is unchanged); **yes** only if the fix-up adds an absent-verbs row, which §5.2 enumerates |
| **V12-7** *"the checkout has no such package"* — `org.taumc.celeritas.api.v0` exists and holds `CeleritasShadersApi` | **correction** | **no** — §4.12 and §0.11's bullet |
| **V12-8** §0.11's three-state disclosure and l. 13 are false against MOVES.md, the harness and the tree | **correction** | **no** — §0 and the closing note |
| **V12-9** `[D-P1-38]`'s test is scoped over a package subtree whose children other configs declare, so it fails on a correct config set | **correction** | **no** — §4.5.2a and §12 item 30a |
| **V12-10** §4.13 says "two deviations"; §5.1, §0.11 and `[D-P1-37]` say one | note | **no** — §4.13 only; §5.1 is already on the majority side |
| **V12-11** item 23's `GL_MAX_TEXTURE_IMAGE_UNITS` signal cannot detect probe ordering; §4.13's stage-2 rationale misdescribes `initializeTextures` | note | **no** — §4.13 and §12 item 23 |
| **V12-12** §3 l. 1293 asserts a `doc/shaders.txt` fact in a cell citing only RESEARCH.md | note | **no** — §3's provenance cell |

### §G1.3 line

**§G1.3's *"re-verify only if §5 changed"* trigger fires.** Four corrections alter §5 — V12-1, V12-2,
V12-3 and V12-4, all four of them inside the two §5.1 rows this revision added — on the textual reading
rounds eight through eleven all applied. Accordingly:

- A fix-up session (§G1.3) applies the nine corrections to `docs/phase1/v11/PHASE_1_DOC.md` and records
  each resolution under a `## Resolutions` heading **in this file**. The three notes may be applied or
  declined; a decline should carry its reason, per the convention §0.6 established.
- **V12-3 and V12-4 should be resolved together.** Declaring one permitted, `non-sealed` extension
  point in `com.schmaloogium.engine.gl` and naming it in l. 3505 answers both; unsealing the four
  handle types instead answers V12-3 alone and costs §4.7.3's declarations plus §5.2's *"nothing in any
  signature"* claim. Whichever branch is taken, l. 2340's *"each backend supplies the permitted
  implementations"* has to change too — the defect is not confined to the new row.
- **V12-1 should not be resolved by editing §3 or `[D-P1-36]` down to match §5.1.** Those two already
  agree with `DESIGN.md` l. 1488; the §5 row is the outlier.
- Because that fix-up alters §5, **`PHASE_1_DOC.md` goes through a fresh verify session before any
  dependent consumes it.** Until that round returns the phase is **not** verified under §G1.3's third
  bullet — there is a §5 change outstanding — the document is **not** a valid dependency input, and
  Phase 2, Phase 3 and everything downstream stay blocked (§G5.3).
- **If the fix-up applies only the five non-§5 corrections** (V12-5 … V12-9) and declines the other
  four, §5 is untouched and no further verify session is owed — but a decline of V12-3 or V12-4 must be
  argued at the line against `DESIGN.md` l. 252, §4.3 l. 1839 and §12 item 22b, not against this
  review's framing.
- **One thing this round did not have to guess and the next one will:** §0.11's disclosures are now
  stale (V12-8) *because a maintainer completed §G0.4 steps 1, 2 and 4 after the fix-up wrote them*.
  A thirteenth session should re-derive the repository state from `docs/MOVES.md` and
  `PHASE_FACTS` rather than from any §0 addendum, whatever that addendum says.

*Per §G1.2 this session stops here. It wrote no code, ran no build, no test and no gradle task,
launched no writing agent, made no network request, and created exactly one file: this one.
`docs/phase1/v11/PHASE_1_DOC.md`, `docs/design/v1.1/DESIGN.md`, `docs/design/v2.0-RC1/DESIGN.md`,
`docs/design/v2.0-RC2/DESIGN.md`, `docs/research/v1/RESEARCH.md`,
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`, `docs/MOVES.md`, the `/verify-loop` harness and
its operator documentation, and `PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_11.md` — including their
`## Resolutions` sections, which are evidence — are unmodified.*

---

## Resolutions

**Session:** §G1.3 fix-up, 2026-07-26, on `docs/phase1/v11/PHASE_1_DOC.md` (4,574 → **4,798** lines;
new addendum **§0.12**, exactly 40 lines). **All nine corrections applied. None refused.** The three
notes are **deferred with reasons** under `### Notes deferred` below, so a later round can tell a
considered deferral from an oversight. Two files were touched — the phase doc and this one; `git status
--short` lists both by path, and `docs/phase1/reviews/` holds tracked files, so that check ran **exact
rather than degraded**.

**The §5 gate, run and not asserted.** Content-anchored (`awk '/^## 5\. Cross-phase
interfaces/,/^## 6\. Failure modes/' docs/phase1/v11/PHASE_1_DOC.md | sha256sum`), before the first
edit and after the last:

| | sha256 of §5 |
|---|---|
| before | `82491afc8a821b70d75eae5e4b26ab3ad1ba147813acef2337243b9da998559b` |
| after | `a484a0cdc336812ccdd9cb70f7d6d93704ad317c5eb602474ed23014c2fa9124` |

**§5 changed**, at **five** rows: the two §5.1 rows §3's disposition table predicted, plus §5.2's
changelog, opaque-handle and non-verbs rows. **§G1.3's re-verify trigger fires; a thirteenth verify session is owed** and the
phase stays unverified, not a valid dependency input, with Phase 2/3 and everything downstream blocked
(§G5.3).

**Re-derivation, not adoption.** Every load-bearing claim below was re-opened at its source by this
session before it went into the document: `DESIGN.md` RC2's Phase 1 spec (ll. 957–1067) and Doc gate
(ll. 1056–1060), Phase 5's *Scope — in* (ll. 1484–1493, the ownership clause at 1488), Phase 6's
sampler bullet (l. 1563), Phase 13's companion-atlas and custom-texture bullets (ll. 2253–2262,
2269–2271), §G5.3's seam list (l. 631), §G1.1 l. 252 and the forbidden-source rule (ll. 245–249);
RESEARCH.md App B.3 (ll. 1227–1250), l. 526, App E's header and row 17 (ll. 1395, 1414), App D.4
(ll. 1369–1377); `GLStateManagerService` (92 lines, **59** declarations) and `RenderSystemService`
(65 lines, **31**) **complete**; `MinecraftVersionShimService` ll. 125–139;
`MinecraftVintageVersionShimImpl` ll. 505–512; `org/taumc/celeritas/` and its `api/v0/` listing plus
`CeleritasShadersApi.java` l. 1; `schlorbium-project/doc/shaders.txt` ll. 170–190;
`.claude/workflows/verify-loop.js` ll. 100–128 and `docs/MOVES.md` ll. 60–180 read-only, with
`git log`; MCP `search_mappings("initializeTextures")`. Forbidden sources honoured: no directory named
`chatlogs/` below `docs/` and no root-level `*.txt` was opened, including the one currently sitting
there. No network use, no sub-agents, no build, no test, no writing tool outside the two files above.

### V12-1 — applied at eight sites, and narrowed in one place *toward* RC2

Confirmed at source. `DESIGN.md` **l. 1488** is inside **Phase 5's** *Scope — in* — the bullet running
1487–1493 that reproduces App B.3 as Phase 5's binding table — and reads *"(ownership shared with
Phase 6: you own which texture object backs each unit per stage; Phase 6 owns pointing sampler uniforms
at units)"*. Phase 6's l. 1563 gives it *"Sampler re-pointing"* and its spec carries no
texture-binding bullet. So the half a provider's contents answer is Phase 5's, and the §5 row named the
wrong dependent.

Applied: §5.1's row (l. 3706) now names **Phase 5** for the set and **Phase 6** for the pointing, with
the consumer column reordered **5, 6, 13**; §1.2's row (**l. 1187**) — which four other sites delegate
to — now states the split itself rather than only the joint owners; §4.12's disposition gains a *"Who
owns the contents"* paragraph (**ll. 3570–3582**); §9's provider row (**l. 3991**); `[D-P1-36]`
(**l. 4271**), whose *"Phase 5/6 policy"* clause was right while its opening clause was not; §12 item
22b (**l. 4701**); §11.4 gains a **To Phase 5** hand-off (**ll. 4526–4539**) and its Phase 6 block is
re-framed as the counterpart (**ll. 4545–4556**); and §0.11's own bullet (**ll. 971–978**) is narrowed
in place rather than rewritten, with the correction named.

**Narrowed against the review's fix shape, on evidence:** the review would have Phase 13 named for
*"the `_n`/`_s` companions"*. Phase 13 **builds** those atlases itself (`DESIGN.md` ll. 2253–2262:
*"full companion atlases with matching mip chains"*, and REV1's note that **no atlas reference
exists**), so their handles come from `TextureService.create` like any other engine texture and need no
provider. Phase 13's real claim on this slot is its **`minecraft:`-asset custom-texture forms**
(ll. 2269–2271: *"`minecraft:` asset locations (incl. `dynamic/lightmap_1`, atlas paths …)"*), which do
name textures Minecraft owns. The document now says that, and says which is which.

### V12-2 — applied at two sites; §4.13's own count is a *different* finding and is deferred

Confirmed: §5.1's cell said *"stages 2 and 3 are requirements on Phase 7's hook catalog"* while §9
(l. 3990 in the finished file), §4.13 point 3, §11.4 and `[D-P1-37]` all call stage 3 a recommendation Phase 1
declines to wire. Applied at §5.1 (**l. 3705**) — *"stage 2 is a requirement … while stage 3 is a
recommendation Phase 1 does not wire"*, with the two strengths spelled out and the consumer-column
gloss changed to *"owns the stage-2 catalog entry, and stage 3's if it chooses to place it"* — and at
§11.4's topic sentence (**l. 4446**), now *"one … is a requirement, one is a recommendation, and one
was deviated from"*, which matches the three items it introduces. §4.13's delta paragraph
(**ll. 3669–3673**) carries the same distinction where it hands §11.4 the two stages.

**Deliberately not fixed here:** §4.13's *"two deviations"* banner is **V12-10**, a note, and notes are
not applied this round. That leaves §4.13 counting deviations one way and §11.4 counting stage
strengths another for one round. This is a deferral, not an oversight — see `### Notes deferred`.

### V12-3 — applied on branch (a); branch (b) is **refused with cause**

Re-derived from the JLS rather than from the review: a class or interface that is a **direct subtype of
a sealed type must be `final`, `sealed` or `non-sealed`**, and for a sealed type in the **unnamed
module** every permitted subtype must be in the sealed type's **own package** (in a named module, the
same module). `:engine` has no `module-info.java` because §4.3/`[D-P1-6]` rejected JPMS, so the
same-package rule binds. `Lwjgl3GLDevice` is in `mod.glue` (§2.1, §2.4), `RecordingGLDevice` in
`engine.gl.record` (§4.7.5), a Kirino backend is nowhere in this tree (§10.3's drill presumes one), and
`[D-P1-36]`'s handle is in `mod.glue`. **Every implementation the document describes was excluded by
its own declarations.**

**Refusal with cause, against the review's own preferred repair.** The review's §3 §G1.3 line
recommends branch (b) — one permitted `non-sealed` extension point in `com.schmaloogium.engine.gl` —
as *"the smaller edit"* that *"keeps the sealing"*. It does not work, and the review says so itself two
paragraphs earlier when it notes that *"l. 2340 is wrong about `RecordingGLDevice` too"*: an extension
point admits the **vanilla-texture** handle and leaves both backends' own handles unpermitted, so the
document would still describe an uncompilable arrangement. Branch (a) is taken, scoped by derivation
rather than by the review's framing: **`GLHandle` stays sealed** — its `permits` clause is the
compiler-enforced statement of *"four handle types, not five"*, which §12 item 18 already checks — and
the four leaves become **`non-sealed`** (the only legal modifier, since an interface cannot be
`final`). **`UniformLocation` is unsealed too, a site the review did not name:** `[D-P1-34]` obliges
every *backend* to implement it and retain the `locate` name, so it carried the identical defect.

Applied at §4.7.3 (**ll. 2394–2432**: the declarations, plus the argument replacing the old *"each
backend supplies the permitted implementations"* sentence), `[D-P1-15]` (**l. 4250**, which is where
the decision lives — **no new `[D-P1-39]` was minted**, to keep the decision log from growing a row for
a correction), §5.1 (l. 3706), §5.2's changelog row (**l. 3712**) and opaque-handle row (**l. 3716**),
§9 (l. 3991), §12 items 18 (**l. 4695**) and 22b (**l. 4701**), §4.12's disposition (ll. 3562–3568) and
§0.11 (ll. 971–978). **The dating claim was checked rather than guessed:** `PHASE_1_REVIEW_4.md`'s F3-6
(ll. 278–283) quotes *"lines 1045–1048 carry literal `permits …` ellipses"*, so the sealed leaves date
from the build session's own listing, not from a later revision — the document now says that, and says
that the ellipsis hid the defect rather than causing it, which is the boundary round four's ruling
actually drew.

**A `sealed` sweep was run over the whole document, because a formulation sweep is owed rather than a
site sweep.** The only other `sealed` declaration is `CompatVerdict` (§4.10), whose permitted subtypes
are three records nested in the same compilation unit — sound as written, unchanged.

### V12-4 — applied; the type is declared, with its holder and its installation moment

Confirmed: `grep -n provider` over the pre-edit document returned no declaration, while §12 item 22b
scheduled *"the provider interface `:engine` receives them through"* as a **v0.1** deliverable. The
direction argument re-derived: a provider declared in `mod.glue` and consumed by `:engine` inverts the
`:mod → :engine` edge (§2.2, C-1), so it must be an `:engine` type — which is what makes it Phase 1's.

Applied at §4.7.3 (**ll. 2465–2501**), beside the handle types as the review asked:
`ForeignTextureProvider` with `Optional<TextureHandle> handleFor(String key)`, plus `ForeignTextures`
carrying `install(…)`/`active()`. Three design calls, each with its reason in the document: the key is
**App B.3's sampler name used verbatim** (§G4.1), because a typed enum of members would be Phase 1
enumerating the set V12-1 just established is Phase 5's; the holder reuses **§4.9.1's `LogSink` install
shape**, the convention this document already has for a `:mod`-implemented SPI that `:engine` reaches
from wherever it binds, rather than a new mechanism or a new `GLDevice` method (which would have moved
a §5.2 signature); and the installation moment is **§4.13 stage 2**, now named in §4.13 itself
(**ll. 3649–3653**) so the cross-reference resolves, with lazy per-call resolution so nothing requires
vanilla's textures to exist at install time. Absent-provider behaviour is stated (`active()` answers
empty for every key before install), which keeps §6's ladder able to say something.

Referenced by name at §3 (**l. 1348**), §5.1 (l. 3706, including the row's *Exposed* column), §5.2
(ll. 3712, 3716), §9 (l. 3991), §11.4 (ll. 4532–4533, 4545–4546), `[D-P1-36]` (l. 4271) and §12 item
22b (l. 4701), whose gate now reads *"the provider compiles **from `mod.glue`**"* — the form that
actually tests the shape, since it fails if the sealing regresses. **§2.4's key-type table gains a row**
(**l. 1316**), a site no finding named: that table is where this document says which types it
introduces, and a new `:engine` type absent from it is the same silent-addition failure `[D-P1-33]`
deleted a verb over.

### V12-5 — applied on branch (a): the missing half was **run**, not excused

The criterion is scored against the spec, and `DESIGN.md` **ll. 999–1000** defines the inventory in two
parts — *"`MinecraftVersionShimService`'s method list … **plus the glsm state services**"*. Branch (b)
would have declared half a named checklist out of scope; running it cost one read of 157 lines, so the
document runs it (**§4.12 ll. 3510–3527**, with the scope sentence at ll. 3496–3501 and the path-table
row at l. 3473 recording that both files were read complete). Four buckets, parallel to the shim's:
verbs already served; the **state reads** `snapshot()`/`restore()` are built from; absences already
recorded with a requester; and not-ours.

**Result: no new gap** — and the reason is stated as structural rather than lucky (their glsm services
abstract GL for a core that calls GL statically; ours *is* the GL abstraction, so the mapping runs
verb-to-verb). Three by-products worth more than the null result, all derived at the two files:
`getColorMask`/`getDepthStateMask`/`isBlendEnabled`/`getBlendMode`/`getActiveTexture`/`getBoundTexture`/
the viewport getters are **exactly the member set §12 item 22a's review hook audits** — the hook added
against PD §17 B11's hardcoded `getColorMask()` — which is the strongest available corroboration that
the `StateAspect` list is the right shape; `glPixelStorei` lands on an absent-verbs row that already
exists (*"free-standing pixel-store state … carried inside `TextureData`"*); and the **binding reads**
are answered by `[D-P1-29]` rather than by a verb, because a backend that issues `GlStateManager`-cached
verbs through `GlStateManager` never has to read a binding back to restore it. `glGenBuffers`/
`glBindBuffer`/`glGenVertexArrays`/`glBindVertexArray` are bucketed **not ours** with a reason: the only
geometry this facade draws is `DrawService.fullscreenQuad()`, whose vertex source D-9 leaves to the
backend. `RenderSystemService`'s modern surface (`assertOnRenderThread`, `setShaderTexture`,
`getProjectionMatrix`, `setPositionShader`, …) is 1.17+ vanilla and absent on 1.12.2. The limit is
stated at the site: this is a **member-list** check, as the criterion asks — not a semantic comparison.

### V12-6 — applied: the semantics first, then the re-bucket, then the absent-verb row

Confirmed at the reference: `bindMainFramebuffer()` → `CLIENT.getFramebuffer().bindFramebuffer(true)`
(l. 506) binds **Minecraft's own FBO**; `unbindMainFramebuffer()` → `unbindFramebuffer()` (l. 511)
returns to name 0. Opposite operations, one bucket cell, one verb.

Applied: `bindDefault` gains a javadoc (**§4.7.4 ll. 2577–2580**) fixing it to **framebuffer name 0**
and saying explicitly that nothing here binds the framebuffer object Minecraft renders the world into;
§4.12's *"Already served"* cell (**l. 3505**) now claims only the **unbind** half and points at the
second gap; the section heading (l. 3541) becomes *"The gaps the check found — one in the
texture-handle direction, one in the framebuffer direction"*, and the *"only positive result"* claim in
the paragraph at l. 3584 is dropped; a new **absent-verbs row** (**§4.7.4 l. 2931**) carries *"bind a
framebuffer the engine did not create"* with named requesters, and §4.12 gains the second-gap paragraph
(**ll. 3592–3606**).

**Two things the review's fix shape did not settle, decided here and marked.** *Requesters:* named from
`DESIGN.md`, not from App E — App E's last column is *"Serves hook needs (§7.1)"*, so its *"6"* is a
hook-need number and **not** a phase, and citing it as a phase would have been a fresh defect. The
grounding used instead is Phase 5's growth-posture bullet, *"`Final` renders to the vanilla framebuffer
(handoff contract with Phase 7)"* (`DESIGN.md` l. 1486) → **Phase 7**, with **Phase 5** second if it
ever needs vanilla's FBO as a blit source. *Strength:* the row records that **nobody has asked yet**
and may never — vanilla binds and unbinds its own FBO around the world render (App E row 17's
`bindFramebuffer(Z)V` hook), so a `final` pass that simply does not rebind already writes there; that
inference is tagged `[A]` rather than asserted. And the answer, if a requester does arrive, is
`[D-P1-36]`'s shape (a `mod.glue` `FramebufferHandle`) rather than a new verb — which is now possible
precisely because of V12-3's unsealing. **§5.2's non-verbs row was updated with it** (l. 3722) — that
row enumerates the absent-verbs table's contents and its consumer column carries the requesters, so
adding a row to the table without it would have left §5 enumerating eight absences where §4.7.4 has
nine; the same edit corrects that column's *"**5** is **not** a requester of an absent verb"*
parenthetical, which the new row falsifies. This is the fifth §5 row this fix-up touched, and the
review's V12-6 called it in advance. **No second provider slot was promoted into §5**: §G1.1's
"flag, do not decide" and the facade's own "no verb without a consumer" both point the other way.
*Also checked and left alone:* the pair's siblings `getMainFramebufferWidth`/`Height` (shim ll. 127–128)
are sizing inputs, covered by the existing *"a `mod.glue` provider a later phase owns"* bucket (Phase 5
owns sizing) — not a gap.

### V12-7 — applied, narrowed to what the checkout shows

`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/org/taumc/celeritas/` holds exactly two
entries, `CeleritasShaderVersionService.java` and `api/`; `api/v0/CeleritasShadersApi.java` l. 1 reads
`package org.taumc.celeritas.api.v0;`. So the package exists on that path and the round-eleven claim
was false. Applied at §4.12 (**ll. 3477–3486**) as the narrower and truer correction — PD conflates two
adjacent API surfaces — and at §0.11's bullet (**ll. 1004–1008**), narrowed in place with the
correction named. The document keeps the point that survives: the coordinate is wrong, and a
demonstration-of-verification that is itself wrong demonstrates the opposite, which is now said at the
site.

### V12-8 — applied by **dating**, not by rewriting the record

All three claims re-derived read-only: `.claude/workflows/verify-loop.js` l. 113 `docVersion: 'v11'`,
l. 114 `design: 'v2.0-RC2'`, l. 115 `spec: '957-1067'`/`docGate: '1056-1060'`; `docs/MOVES.md` l. 68
(RC2 *"governs Phase 1"*), l. 76 (*"There is no longer one governing revision"*), ll. 83–88 (per-phase
resolution), l. 105 and ll. 126–145 (the `-RC` **ruling**: the suffix drops when *every* downstream doc
has adopted), l. 108 and ll. 123–124 (the roll, *"performed 2026-07-26, both steps together, with no
loop running"*), ll. 154–163 and l. 177 (the one dangling reference); `git log` — `afeceda`
*"verify-loop: finish the §G0.4 adoption the round-eleven fix-up left at step 3"*, immediately after
`c108630`.

Applied: §0's header (**ll. 13–16**) now states the per-phase form and says where to resolve it
(`docs/MOVES.md`, `PHASE_FACTS`) rather than asserting a governing revision; §0.1's RC2 row (**l. 34**)
follows; §0.11's block (**ll. 1048–1076**) is re-headed *"Three states this document was in **at the end
of the round-eleven fix-up**"*, with each item carrying what has since landed and tagged
`[V:repo 2026-07-26]`; the closing note (**ll. 4779–4798**) is restated the same way. **The
stop-and-report instruction is kept and re-grounded** — it is good practice independent of the adoption
state, which is the review's own recommendation. The `v10`→`v11` sentence no longer instructs anyone to
run a `git mv` against a directory that does not exist.

*One observation for the maintainer, not a change:* `docs/MOVES.md` l. 162 says `PHASE_1_DOC.md` is
absent from the dangling-citation list because *"its l. 1051 quotes the `git mv` command"*. That line is
now **l. 1067** and quotes the command without its path arguments. MOVES.md's acceptance check is
unaffected — it was re-run after these edits and still returns exactly the one recorded line,
`DANGLING: docs/phase1/v10/PHASE_1_DOC.md` — but the line citation in MOVES.md is stale. MOVES.md was
not modified: it is outside this session's two-path scope.

### V12-9 — applied at **three** sites; the review named two

Confirmed: §4.5.2's table gives the three declared packages as `com.schmaloogium.mod.mixin.preinit`,
`com.schmaloogium.mod.mixin` and `com.schmaloogium.mod.mixin.compat`, and observation 1 says in the
document's own words that DEFAULT's is the **parent** of the other two. A subtree-scoped predicate
therefore fails on a *correct* config set at its first non-vacuous exercise.

Applied at §4.5.2a (**ll. 2157–2165**) and §12 item 30a (**l. 4720**) — for each config, the
`@Mixin`-annotated classes in its declared package **excluding any sub-package another config declares**
are exactly the classes its arrays name, with observation 1 cited as the reason so a later reader does
not "simplify" the exclusion away — **and at §8's test table (l. 3877)**, `MixinConfigAgreementTest`,
which states the predicate a third time and which the review's location list did not carry. That site
was found by grepping the *formulation* rather than the finding's sites. Item 30a's hook gains the case
the predicate exists for: a mixin in `…mixin.preinit`, listed only in `schmaloogium.preinit.mixin.json`,
must leave **all three** configs green. Observation 2 needs no exception and the document now says why
(an `IMixinConfigPlugin` is not `@Mixin`-annotated).

### Notes deferred

Per this session's contract the three notes are **not applied**; each is recorded here with the reason,
and each is small enough that a thirteenth round can apply it in a line.

- **V12-10 (§4.13's *"two deviations"*).** Deferred, not doubted: the count is the outlier of four
  statements and the fix is one clause (*"one deviation and one deferred placement"*). It sits one
  paragraph from V12-2's sites, so this round deliberately kept its edits **off** §4.13's banner rather
  than fold an unapplied note into an applied correction. **Consequence a later round should see coming:
  §4.13 now says "two deviations" while §11.4 counts one requirement, one recommendation and one
  deviation.** That inconsistency is this deferral's cost, it is visible rather than hidden, and V12-2's
  fix did not create it — the two counts were already independent.
- **V12-11 (item 23's `GL_MAX_TEXTURE_IMAGE_UNITS` hook; §4.13's stage-2 rationale).** Deferred. Both
  limbs were re-derived and both hold: the constant is a context-lifetime property, so it cannot
  distinguish "after vanilla's texture setup" from "before"; and MCP gives `initializeTextures`
  (`func_77474_a`) the javadoc *"Initializes the texture constants to be used when rendering lightmap
  values"*, so *"vanilla's own texture setup has completed"* overstates it. The site remains right for
  the reason the review gives. Not applied because the note asks for a **replacement test hook**, which
  is new design rather than a correction, and this round already adds more surface than a fix-up should.
  The rationale sentence appears at three sites (§4.13 point 1, §5.1's row, §11.4) and should be
  re-grounded at all three together.
- **V12-12 (§3's `countInstances` provenance cell).** Deferred. Verified: `shaders.txt` l. 182 puts
  `uniform int instanceId;` under *Uniforms* and above *GBuffers Uniforms* (l. 184), while RESEARCH.md
  places `instanceId` under App D.4 (l. 1377) and contains no such heading — so the middle clause of the
  cell is a `shaders.txt` fact sitting between two RESEARCH.md citations, and §11.4 (**l. 4514** in the
  finished file) names the source for the identical claim. The fix is one clause naming
  `schlorbium-project/doc/shaders.txt` in the cell. The review's own note that the reading **is**
  disclosed (§0.7's V7-1 bullet, and `PHASE_1_REVIEW_7.md`) was confirmed, so nothing broader is owed.

### Corrections to this review, recorded rather than worked around

1. **V12-3's recommended repair (b) does not close V12-3** — see the refusal above. The review is
   internally inconsistent on this point, recommending (b) in §3 while §1 observes that l. 2340 is wrong
   about `RecordingGLDevice`. Branch (a) was taken. This is recorded, not edited: the review is evidence.
2. **V12-1's fix names Phase 13 for the `_n`/`_s` companions.** Those atlases are Phase 13's own
   construction and need no vanilla handle; its `minecraft:`-asset custom-texture forms do. The document
   says the narrower, true thing.
3. **V12-6's fix would have taken App E row 17's *"6"* as a phase.** It is a §7.1 hook-need number
   (App E's column header, RESEARCH.md l. 1395). The requesters are grounded in `DESIGN.md` l. 1486
   instead.

### Sites edited that no finding named, and why each was owed

`§1.2 l. 1187` (the row four sites delegate to, so the split has to be stated where the delegation
lands) · `§2.4 l. 1316` (the key-type table, where this document declares what types it introduces) ·
`§4.7.3`'s `UniformLocation` (same defect as the four handle leaves; `[D-P1-34]` requires backend
implementations) · `§8 l. 3877` (`MixinConfigAgreementTest`, V12-9's third statement of the predicate) ·
`§4.13 ll. 3649–3653, 3669–3673` (the install point V12-4's fix promises §4.13 will name, and the delta
paragraph's *"no type, no signature"* claim) · `§12 item 18 l. 4695` (said *"four **sealed**
sub-interfaces"*) · `§4.12 l. 3473` (the path-table row, now recording that both glsm files were read
complete) · `§0.1 ll. 34, 36` and `§0`'s dated-claims note `ll. 21–24` (disclosure of this round's reads
and the §0.4–§0.12 range) · `[D-P1-15] l. 4250` (V12-3's decision home — chosen over minting a new
`[D-P1-39]`) · `§5.2 ll. 3712, 3716` (a *"nothing moved"* changelog claim that would now be false, and
the opaque-handle row) · the closing note `ll. 4751–4753, 4779–4798` (session and fix-up counts).
Two internal self-citations were **removed rather than re-resolved** — the `(l. 1839)` pointers at
§4.7.3 and §5.2 now cite `[D-P1-6]`, on §0.10's own lesson that a self-referential line number is
correct only until the next edit.

### What this fix-up did not do

No file outside `docs/phase1/v11/PHASE_1_DOC.md` and this one was modified: `RESEARCH.md`, all three
`DESIGN.md` revisions, `PINTONIUM_DESIGN.md`, `docs/MOVES.md`, the `/verify-loop` harness and its
runbook, and `PHASE_1_REVIEW_1.md` … `PHASE_1_REVIEW_11.md` including their `## Resolutions` — evidence,
every one — are untouched. No `v11` → `v12` roll was performed: §0.12 makes one owed, and `docs/MOVES.md`
requires it to run with the `docVersion` bump **after** a loop exits, not mid-loop. No code, no build, no
test, no gradle task, no network request, no sub-agent.
