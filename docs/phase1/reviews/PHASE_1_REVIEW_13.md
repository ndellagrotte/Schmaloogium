# PHASE_1_DOC.md — Verify session, round thirteen

**Phase:** 1 — Foundation & project architecture
**Document under review:** `docs/phase1/v11/PHASE_1_DOC.md`, 4,798 lines (revision §0.12).
**Design revision:** `docs/design/v2.0-RC2/DESIGN.md` — the revision this document declares at its
ll. 12–17 and the revision the harness pins for phase 1 (`PHASE_FACTS[1].design`,
`.claude/workflows/verify-loop.js` l. 114). **Every `DESIGN.md` line number in this review is RC2's.**
Every `PHASE_1_DOC.md` and `RESEARCH.md` line number was re-resolved at the file by this session.

**Verdict: PASS-WITH-CORRECTIONS** — four corrections, four notes, zero blocking. **Three corrections
alter §5**, so §G1.3's re-verify trigger fires. §3 carries the disposition table and the §G1.3 line.

The shape of the round is the honest headline. **Three of the four corrections sit in the ~140 lines
the §0.12 fix-up added or rewrote** — §4.7.3's new `ForeignTextureProvider` block (ll. 2465–2501),
§4.7.4's new absent-verbs row (l. 2931) and §4.12's second-gap paragraph (ll. 3592–3606) — which is
round seven's rule holding again: unreviewed material yields findings in proportion to its size, not
to the document's maturity. The fourth is older and is the round's one genuine miss by eleven prior
rounds: §3's mipmap row hands Phase 5 a capability `DESIGN.md` gives Phase 7 (V13-4). Everything else
I ran a check over came back clean, including the whole of the sealing repair, and one candidate was
dropped outright on ground three prior rounds already settled (§2 item 1).

---

## 0. What I read, and in what order

Per §G1.2 step 1 and this round's brief: the ground truth first, then the document, then — and only
then — the prior rounds.

1. `docs/design/v2.0-RC2/DESIGN.md`: §G1.2 (257–300), §G1.3 (302–320), §G4.1 (499–504), §G4.2
   (506–519), §G5.1's phase table (565–588), the **Phase 1 spec**'s closing block and its **Doc gate**
   (1050–1067).
2. `DESIGN.md` Part II, beyond the assigned list — each disclosed in §0.1 with the finding it turned
   on: **Phase 4**'s program-registry bullet (1332–1340), **Phase 5**'s spec (1470–1522 in full, and
   the whole 1409–1525 range swept for one term), **Phase 6**'s *Scope — in* (1526–1580), **Phase 7**'s
   *Scope — in* part (a) and the head of part (b) (1655–1714), **Phase 8**'s shadow-mipmap bullet
   (1826–1829), **Phase 13**'s *Scope — in/out* (2240–2288).
3. `docs/research/v1/RESEARCH.md`: §4.1 (473–489), §4.2–§4.4 (491–565), §7.1's hook-need list
   (796–824), §9's milestone table (938–954), App A.3's directive rows (1180–1191), App B.1–B.3
   (1195–1248), App E rows and coverage notes (1396–1429), App F.5 (1479–1488), §3.4 (285–292).
4. `docs/phase1/v11/PHASE_1_DOC.md` — §0's header and anchor block (1–40), §2.1's `:mod` table and
   §2.4's key-type table (1240–1318), **§3 entire** (1325–1361) plus §3.1's head, §4.7.3 from the
   sealing paragraph through the new provider block (2420–2501), **§4.7.4 entire** (2503–2937),
   §4.12's two bucket tables and both gap paragraphs (3480–3606), **§5 entire** (3676–3768), **§6
   entire** (3772–3793), §9's provider row (3991), `[D-P1-36]` (4271), §11.4's Phase 5 / Phase 6 /
   Phase 13 blocks (4520–4580), §12 items 17–24 (4694–4703), the closing note (4757–4798). Section
   inventory checked by heading against §G9.
5. `reference-src/pintonium-9c2fcc1/`, read only to check a claim the document builds on:
   `common-shaders/src/main/java/net/irisshaders/iris/pipeline/FinalPassRenderer.java` ll. 280–300.
6. **Then** `docs/phase1/reviews/PHASE_1_REVIEW_12.md` in full, including its `## Resolutions`
   (ll. 842–1168 as far as the V12-6/V12-7/V12-8 entries).
7. **Then** the earlier rounds by targeted grep across all twelve review files — `ForeignTexture`,
   `did not create`, `bindDefault`, `lifetime rule`, `22a`, `no pack vocabulary`, `DRAWBUFFERS`,
   `supportsMipmapGeneration`, `composite-mipmap`, `G4.1` — reading the passages the greps returned:
   `PHASE_1_REVIEW_11.md` ll. 388–419 (its §2 item 3, which settles this round's dropped candidate),
   `PHASE_1_REVIEW_12.md` ll. 406–455 (V12-6) and ll. 668–689 (its §2 item 1),
   `PHASE_1_REVIEW_4.md` l. 866 (F4-8, the origin of the handle-lifetime rule),
   `PHASE_1_REVIEW_3.md` l. 205 (the round-three contract sweep).

### 0.1 Reads beyond the assigned list, each with the finding it turned on

- **`DESIGN.md` Phase 4 (1332–1340), Phase 5 (1409–1525), Phase 7 (1655–1714) and Phase 8
  (1826–1829).** V13-4 turns on all four: §3 l. 1340 makes an ownership claim *about* Phase 5, and the
  only way to test it is to read where `DESIGN.md` actually puts composite mipmap generation. §G1.1
  l. 200 bars a **build** session from other phases' specs; a verify session auditing an ownership
  claim this document makes about another phase has no such bar. This is the standing precedent §0.8
  records and rounds eleven and twelve both used.
- **`DESIGN.md` Phase 13 (2240–2288) and RESEARCH.md App F.5 (1479–1488).** V13-2 turns on both: §5.1
  and §4.12 name Phase 13 a consumer of the provider *for its `minecraft:`-asset custom-texture
  forms*, and the question is whether the declared key type can express them.
- **`DESIGN.md` Phase 5 l. 1486 and Phase 7 l. 1685, plus §G5.1 ll. 571/573 and RESEARCH.md l. 946.**
  V13-1 turns on the milestone: whether the `final`-to-vanilla-framebuffer target is a v0.1 contract
  item or a future one.
- **`reference-src/pintonium-9c2fcc1/…/FinalPassRenderer.java` ll. 280–300.** V13-1's fifth limb —
  the field-proven implementation on this platform binds Minecraft's framebuffer at the end of the
  final pass. §G11.2's licensing rules and §G11.3's traps were observed; nothing was copied, and the
  observation is behavioural.
- **`.claude/workflows/verify-loop.js` ll. 110–118 and `git log`, read-only.** Round twelve's §G1.3
  line instructed a thirteenth session to re-derive the repository state from `PHASE_FACTS` and
  `docs/MOVES.md` rather than from any §0 addendum. Done; the result is in §2 item 8. No git command
  that writes was run.

### 0.2 Deviations, and omissions recorded as omissions

- **Forbidden sources honoured.** No directory named `chatlogs/` below `docs/` was opened, and no
  `*.txt` at the repository root was opened — including the 108 KB dated transcript currently sitting
  there, which was seen in a directory listing and deliberately not read. The rule is about
  provenance, not location: no file was opened that this session had reason to believe was a prior
  session's terminal transcript. `PHASE_1_REVIEW_9.md` §0.2 records the round-nine breach and the
  discard that followed; the fan-out ran under the same rule and no citation below resolves to either
  pattern.
- **Not re-derived, and named rather than glossed:** §4.1, §4.2 (Gradle and the pin table), §4.4,
  §4.5, §4.6, §4.8–§4.11, §4.13, §7, §8, §10, §11.1–§11.3, §11.5, and §12 items 1–16 and 24–45 except
  where a candidate landed in them. Twelve rounds have swept these. §2 states what *was* derived and
  by whom.
- **Doc gate, partially re-run and stated as such.** I checked the two REV1/REV2 criteria the §0.12
  material touches — *"glue-seam completeness check against PD §2's inventory present"* (§4.12 now
  carries both halves, the shim inventory and the glsm services; V12-5 applied) and *"bootstrap
  sequence adopted or deviation justified"* (§4.13). The other three criteria (module/package layout
  as testable constraints, D-1..D-10 disposition, pin table with re-verification procedure) I took on
  the standing record of rounds three through twelve and did **not** re-derive. Recorded as coverage
  I did not run, not as coverage that passed.
- **Template ground truth was not opened this round** (`build.gradle`, `settings.gradle`,
  `gradle.properties`, `gradle/scripts/*`, `src/**`, `.github/workflows/*`, `README.md`). No finding
  turns on a template fact.
- **No build, test or gradle invocation. No file was created except this one.**

### 0.3 Network use

**None of any kind.** No finding turns on a fact outside the checkout.

### 0.4 Sub-agent disclosure

This round ran as an **automated fan-out of roughly thirty read-only agents** under a mechanised
re-derivation gate: candidate finders swept assigned regions, every candidate was put to **two
independent refuters**, and a gate re-resolved each citation against the file before the candidate
reached this session. **Eleven candidates survived to me.** The gate confirms anchors only — an anchor
check is not a finding, and none of the findings below is admitted on the fan-out's report. Every
claim in §1 was re-opened at its source by this session; where I report the fan-out's coverage rather
than my own, §2 says so.

**What I did with the eleven.** I **merged two pairs**: the two framebuffer-bind candidates are one
defect seen from §4.7.4 and from §4.12 and are reported once, as V13-1; the two foreign-handle
candidates are one defect seen at the type and at the lifetime rule and are reported once, as V13-3.
I **dropped one outright** on my own derivation — the §3 unmapped-rows candidate, which three prior
rounds have settled and which appears in §2 item 1 rather than in §1. Of the eight findings that
remain, four are corrections and four are notes; I **restored one refuter demotion to correction**
(V13-1: the refuters left the framebuffer candidate at *note* on the ground that the `[A]` tag makes
the claim provisional — but an assumption a source in the mandatory reading list contradicts is not
provisional, it is wrong, and it is load-bearing for a classification a v0.1 dependent reads), and I
**sustained three others** (V13-6, V13-7, V13-8 all arrived proposed *correction* and are notes here).
I do not have the pre-gate candidate list, so I cannot enumerate what the gate dropped before it
reached me; that limit is stated rather than papered over.

---

## 1. Findings

### V13-1 — the framebuffer-bind row's `[A]` ground is refuted by RESEARCH.md's own frame flow: when `final` runs, the bound framebuffer is the shader estate's, not Minecraft's · **correction** · **touches §5: no**

**Location.** §4.7.4's absent-verbs table, the *Why absent* cell at **l. 2931**; §4.12's second-gap
paragraph, **ll. 3601–3606**. Against `FramebufferService.bindDefault`'s javadoc (**ll. 2577–2580**),
RESEARCH.md **ll. 526, 545–546, 813, 946, 1414**, `DESIGN.md` **ll. 1486, 1685**.

**Claim under test.** V12-6's classification ground — that the two gaps §4.12's completeness check
found *differ in kind*, the texture gap being a v0.1 slot while the framebuffer gap is a mere absence
because *"nobody has asked"* and *"no contract item is unserved today"*, on the `[A]`-tagged reasoning
that a `final` pass which does not rebind already writes into Minecraft's framebuffer.

**What the document says.** l. 2931: *"**Nobody in the current phase set has asked yet, and it may
never be needed:** App E row 17 catalogs `Framebuffer.bindFramebuffer(Z)V` as the final-to-screen
handoff hook — vanilla binds and unbinds its own FBO around the world render — so a `final` pass that
simply does not rebind writes there already `[A]`"*. l. 3605–3606 draws the distinction from it:
*"there the contract *names* two units at v0.1, here no contract item is unserved today."*

**What the sources say, each opened at the line.**

1. **RESEARCH.md ll. 545–546 refute the premise.** The per-frame flow ends
   *" └─ COMPOSITE passes (fullscreen ping-pong, per-pass mipmap gen + render-scale viewports)"* →
   *"     → FINAL to screen"*. The composite chain is the engine's own ping-pong FBO and it runs
   immediately before FINAL with no vanilla code between. A GL framebuffer binding is sticky, so the
   binding in force when `final` runs is the last composite's target — a colortex, not vanilla's
   framebuffer object. "Does not rebind" therefore writes into the shader estate.
2. **RESEARCH.md l. 526 is an unserved contract item.** *"- **Final** renders to the vanilla
   framebuffer (anaglyph-aware color masking)."* That is §4.3, in the same section the document cites
   two clauses earlier, and it names a render target the facade cannot express: **ll. 2577–2580**'s
   new javadoc fixes `bindDefault` to framebuffer *"**name 0**, the GL default framebuffer"* and says
   in as many words that it is *"NOT 'whatever the platform regards as its default target'"*, while
   `bind` needs a `FramebufferHandle` §4.7.4 yields only for engine-created framebuffers. So the
   §4.12 sentence *"no contract item is unserved today"* is false against the document's own source.
3. **App E row 17 does not carry the inference either.** RESEARCH.md **l. 1414** reads
   *"| 17 | `net.minecraft.client.shader.Framebuffer` | `bvd` | `bindFramebuffer(Z)V` →
   `func_147610_a` | 6 (final-to-screen handoff), 10 |"*, and that last column is headed *"Serves
   hook needs (§7.1)"*. §7.1's need **6** (**l. 813**) is *"Deferred-stage trigger between solid and
   translucent terrain; composite/final at frame end."* The row therefore catalogues
   `bindFramebuffer` as a site **the mod must hook** — work someone does — and states nothing about
   vanilla bracketing the world render in a way that makes a rebind unnecessary. Round twelve's own
   `## Resolutions` (**l. 1028–1029**) caught the adjacent misreading — *"App E's last column is
   'Serves hook needs (§7.1)', so its '6' is a hook-need number and **not** a phase"* — and then
   leaned on the same row for a second inference it does not carry.
4. **The field reference the check is run against does the opposite.**
   `reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/FinalPassRenderer.java`
   **l. 289** is `MINECRAFT_SHIM.bindMainFramebuffer();`, under the comment *"Also bind the 'main'
   framebuffer if it isn't already bound"* — an explicit bind at the end of the final pass, in the
   only working implementation of this contract on this platform. This is behavioural observation
   only, per §G11.2.
5. **The target is v0.1, not future work.** `DESIGN.md` **l. 1486** puts *"`Final` renders to the
   vanilla framebuffer (handoff contract with Phase 7)"* in **Phase 5's** *Scope — in*, and **l. 1685**
   puts *"anaglyph-aware final to the vanilla framebuffer (Phase 5 handoff)"* in **Phase 7's**
   composite/final execution bullet — untagged, while its immediate neighbours `scale.<prog>` and
   `countInstances` both carry `[v0.5]`. §G5.1 **ll. 571/573** put Phase 5 at **v0.1** and Phase 7 at
   **v0.1 exit**, and RESEARCH.md **l. 946** puts *"gbuffers + composite + final"* in v0.1's scope.

**Why it matters beyond tidiness, and what it does *not* mean.** The **disposition** may well be
right: the bind can legitimately happen outside the facade, in `:mod`, at Phase 7's `renderWorldPass`
TAIL hook — which is exactly the escape the colour-mask row one table-row above offers at **l. 2928**
(*"If Phase 7 drives that through vanilla's own path it needs no verb"*). I am not asking for the row
to become a slot. What is wrong is the **reason**, and the reason is the part a dependent reads. A
Phase 7 session told *"nobody has asked, and it may never be needed"* has no cue that its `final` pass
must arrange the bind itself; told the true version — *nothing in this facade binds vanilla's FBO, so
you bind it through vanilla's own path before you call `fullscreenQuad`* — it does. The replay
consequence is unstated too and follows directly: §5.2 **l. 3718** records that `bindsBalanced()`
*"sees facade-level calls only"*, so a headless golden run of the final pass records a draw into the
last composite's target and passes.

**Fix.** Replace the `[A]` sentence at l. 2931 with what holds — no facade verb binds Minecraft's
framebuffer object, and the bind is Phase 7's to make outside the facade through vanilla's own path,
with the `bindsBalanced()` blind spot named — and keep the row in the absent-verbs table on *that*
ground rather than on "nobody has asked". §4.12 ll. 3601–3606 follows: the two gaps still differ (one
needs a facade argument, the other needs a call the facade never makes), but the difference is not
*"no contract item is unserved today"*, which RESEARCH.md l. 526 falsifies.

**Touches §5: no** on that branch. §5.2's non-verbs row (**l. 3722**) already names **7** as the
requester *"for the `final`-to-vanilla handoff"* and carries none of the refuted premise, so the edit
is confined to §4.7.4 and §4.12. It **would** touch §5 if the fix-up chose to state Phase 7's v0.1
obligation in that row — which is a reason to prefer stating it at §4.7.4, not a reason to leave
l. 2931 as it is.

---

### V13-2 — the provider's key vocabulary is App B.3's sampler names and its key set is Phase 5's; §5.1 hands Phase 13 a claim on the provider that neither can express · **correction** · **touches §5: yes**

**Location.** §4.7.3's provider declaration, **ll. 2479–2483**; §5.1's provider row, **l. 3706**;
§4.12, **ll. 3575–3577**; §11.4's *To Phase 5* block, **l. 4534**, and its *To Phase 13* block,
**l. 4572**. Against `DESIGN.md` **ll. 2269–2271** and RESEARCH.md **ll. 1229–1246, 1481–1484**.

**Claim under test.** That V12-4's declared type and V12-1's Phase-13 attribution are mutually
consistent — that a `String` key drawn verbatim from App B.3's sampler names is the right shape for
every consumer §5.1 now names.

**What the document says.**

- **l. 2479**, the declared contract of the key: *"`key` is App B.3's sampler name used verbatim
  (§G4.1): "texture", "lightmap", … ."*
- **ll. 2482–2483**, the key **set**'s owner: *"WHICH keys the set contains is NOT this phase's:
  Phase 5 owns which texture object backs each unit per stage, Phase 6 points the sampler uniforms at
  the units."* §5.1 **l. 3706** repeats the vocabulary in the binding contract — *"keyed on App B.3's
  sampler names"* — and §11.4 **l. 4534** repeats both halves: *"sampler names used verbatim (§G4.1).
  What Phase 1 deliberately does *not* supply is the key set"*.
- **l. 3706**'s consumer column nevertheless carries *"**13** (its `minecraft:`-asset custom-texture
  forms — *not* the `_n`/`_s` companion atlases, which it builds itself and gets from
  `TextureService.create`)"*, and §4.12 **l. 3575** says the same in prose.

**What the sources say.**

1. **RESEARCH.md ll. 1229–1246** — App B.3 is a sixteen-row *per-unit* naming scheme
   (`texture`/`lightmap`/`normals`/`specular`/`colortexN`/`depthtexN`/`shadowtexN`/`noisetex`). It is
   a closed vocabulary with one name per unit per stage.
2. **The forms Phase 13 is given are addressed differently.** RESEARCH.md App F.5 **ll. 1481–1483**:
   *"`texture.<gbuffers|deferred|composite>.<samplerName>[.0-9]=` one of: pack-relative PNG path;
   `minecraft:`-prefixed asset (incl. `dynamic/lightmap_1`, atlas paths; `_n`/`_s` suffix selects
   companion variants)"*, and `DESIGN.md` **l. 2270** repeats it in Phase 13's *Scope — in*. The
   **source** is a resource location; the sampler name is the *destination*, and it is pack data that
   changes per pack. Neither `dynamic/lightmap_1` nor an atlas path is an App B.3 sampler name, and a
   `mod.glue` provider keyed on per-pack sampler names could not be written at all.
3. **The key-set owner excludes the case too.** `DESIGN.md` **l. 2287** puts *"unit-map ownership
   (Phases 5/6)"* in Phase 13's *Scope — out*, so the keys Phase 13 needs are outside the set l. 2482
   assigns to Phase 5 — Phase 13 can neither use the stated vocabulary nor add keys of its own without
   crossing Phase 5's stated ownership.
4. **The one place Phase 13 is told what it inherits is silent.** §11.4's *To Phase 13* block
   (**l. 4572**) still opens *"the transfer verbs you need exist and carry no policy:"* and lists only
   `TextureService` verbs. It was not updated when Phase 13 became a consumer of the provider.

**Why it matters.** `[D-P1-36]` exists so that no dependent discovers at its own milestone that a
handle it needs has no source. As written §5 reproduces that failure one layer down for Phase 13: it
is told the provider is its route, and told a key vocabulary that cannot name what it must fetch.
`dynamic/lightmap_1` is the sharp case — a live Minecraft-owned texture, not a static asset Phase 13
could load and re-upload through `TextureService.create`.

**Fix.** Either widen the javadoc and §5.1's row to *"a pack-facing texture identifier — App B.3's
sampler names for the unit-map rows, `minecraft:` asset locations for App F.5's custom-texture
forms"* (still verbatim per §G4.1, still no enumeration by Phase 1), or drop **13** from §5.1's
consumer column, from §4.12 l. 3575 and from `[D-P1-36]` l. 4271, and say plainly that Phase 13's
`minecraft:`-asset forms need an additive request. Whichever is chosen, add the corresponding
sentence to §11.4's *To Phase 13* block, which currently tells Phase 13 nothing about the provider.

**Touches §5: yes** — l. 3706 carries both the key vocabulary and the **13** in its consumer column,
so either branch edits the row.

---

### V13-3 — a foreign `TextureHandle` is type-identical to an engine-created one, and nothing says which `TextureService` verbs may take it or how long it stays valid · **correction** · **touches §5: yes**

**Location.** §4.7.3's provider block, **ll. 2465–2501** (especially **ll. 2476 and 2484**), against
the handle-lifetime rule immediately above it at **ll. 2441–2448**; `TextureService`, **ll. 2612–2627**;
§5.2's opaque-handle row, **l. 3716**. With RESEARCH.md **l. 488** and §6 **l. 3775**.

**Claim under test.** That declaring the provider *"costs no facade verb"* and changes nothing else —
that a handle for a texture **Minecraft** owns can safely share the type, the §5 row and the lifetime
rule of handles the engine created, with no further specification. This is §G1.2's third check —
*"everything promised to dependents is specified, not gestured at"* — run over the type V12-4 added.

**What the document says.**

- **l. 2476**: the foreign handle is *"consumed like any other TextureHandle, so no §4.7.4 verb
  changes"*, and **l. 2484** returns `Optional<TextureHandle>` — the same type, with nothing in it
  distinguishing a texture the engine must not mutate or destroy.
- **l. 2626** and its neighbours: `void delete(TextureHandle t);`, `allocate`, `setParameters`,
  `upload`, `generateMipmap` all take `TextureHandle` and carry no precondition excluding a handle the
  engine did not create.
- **ll. 2441–2448**, the lifetime rule: *"a handle is invalid the moment its `delete` returns"*, and
  *"**Phase 5 owns re-acquisition** — it owns the buffer estate's lifecycle, so it is the phase that
  must drop and re-create its handles across an uninit/rebuild rather than carry them over."*
- **l. 3716** — the §5 row a dependent builds against — states that rule and then adds *"Also here
  now: **`ForeignTextureProvider`** / `ForeignTextures`"*, with no distinction drawn. §5 presents the
  two as one regime.

**What that leaves unspecified, and the concrete failure.** Phase 5 is simultaneously (a) the phase
§5.1 l. 3706 makes responsible for the vanilla-owned set, and (b) the phase l. 2447 orders to drop and
re-create its handles at every uninit/rebuild — an event RESEARCH.md **l. 488** describes as
*"**Uninit**: delete all GL objects; triggered by pack change, option change, dimension switch"* and
which §5.2 l. 3716 itself calls *"a routine v0.1 event"*. A delete loop walked over a unit table that
holds a foreign handle for unit 0 destroys **Minecraft's block atlas**. §6 **l. 3775** makes that
outcome the one thing the ladder forbids outright — *"nothing in the shader engine ever crashes the
client or corrupts the vanilla framebuffer path; shaders-off must always be a reachable state"* — and
§6 carries no row for it. Nothing in the interface prevents it and nothing in §5 warns of it.

The second half is validity. Minecraft destroys and re-creates its own texture objects on a resource
reload, on its own schedule and without telling the engine. **ll. 2497–2499** say *"resolution is
**lazy per call**, so installation does not require the vanilla textures to exist yet"* — a statement
about `handleFor`, not about the handle it returns. Whether Phase 5 may cache a foreign handle across
a vanilla reload, or must re-ask the provider, is the sentence Phase 5 needs and does not have; the
engine's own delete-based rule cannot answer it, because the engine never calls `delete` on that
object.

**Why this is not "obvious enough".** §5 opens (l. 3692) with the promise that it is *"written to be
**sufficient on its own**"*. A type whose whole purpose is that it is indistinguishable from an
engine handle, exposed in §5 with two v0.1 consumers, is exactly the case where "the owner will
obviously know" is the wrong standard — and `SeamBytecodeTest` cannot see any of it, since no GL name
and no MC type crosses the seam either way.

**Fix.** State the ownership rule where the type is declared and repeat it in §5.2's row: a handle
from `ForeignTextureProvider` is **bind-only** — a legal argument to `bindToUnit` (and to
`DebugService.label`) and an illegal one to `allocate`/`setParameters`/`upload`/`generateMipmap`/
`delete` — and it is explicitly **outside** §4.7.3's lifetime rule: across an uninit/rebuild Phase 5
re-*asks* the provider rather than deleting and re-creating. Say whether a handle may be cached or
must be re-resolved per use. Add the backend obligation (reject or no-op a foreign handle on a
mutating verb) to §12 item 22b's review hook, which is already the place for obligations no test can
catch.

**Touches §5: yes** — l. 3716 is the row that states the lifetime rule and advertises the provider in
one breath; the exclusion has to be stated there or §5 is not sufficient on its own.

---

### V13-4 — §3's mipmap row names Phase 5 as the consumer of a capability `DESIGN.md` gives Phase 7 · **correction** · **touches §5: yes**

**Location.** §3's conformance map, mipmap row, **l. 1340**; §5.2's `GLCapabilityProfile` row,
**l. 3714**. Against `DESIGN.md` **ll. 1334, 1469, 1683, 1828** and this document's own **ll. 1356,
1360, 2625**.

**Claim under test.** That `GLCapabilityProfile.supportsMipmapGeneration()` is *"consumed by Phase 5's
composite-mipmap policy"* — i.e. that `DESIGN.md` assigns composite mipmap generation to Phase 5.

**What the document says.** l. 1340: *"`GLCapabilityProfile.supportsMipmapGeneration()` — derived,
`atLeast(3,0)`; **consumed by Phase 5's composite-mipmap policy**"*.

**What `DESIGN.md` says, at every occurrence of the term in Part II.**

1. **l. 1683** — inside **Phase 7's** *Scope — in, part (a)*, under **Composite/final execution**:
   *"ortho, fog/depth/blend disabled, **per-pass mipmap generation (composite-mipmap bitmask)**,
   `scale.<prog>` sub-viewports [v0.5], `countInstances` instancing loop [v0.5]"*. The generation is
   Phase 7's, in the same bullet and the same breath as two directives this document already routes to
   Phase 7.
2. **l. 1334** — inside **Phase 4's** program-registry bullet: *"routing, **composite-mipmap
   bitmask**, instance count, alpha/blend overrides, render scale, flip config"*. The per-slot state
   is Phase 4's, populated from Phase 3's `PackConfiguration` (and the directive itself,
   `colortexNMipmapEnabled`, is App A.3 l. 1185, which Phase 3 parses).
3. **l. 1469** — the **only** occurrence of "mipmap" anywhere in Phase 5's spec (ll. 1409–1525):
   *"`shadowMapResolution` × shadow-quality multiplier; **per-texture nearest/mipmap filter**"* —
   shadow-texture *filter config*, not composite mipmap generation.
4. **l. 1828** — inside **Phase 8's** shadow bullet: *"per-config mipmap generation on shadow
   textures (Phase 5 texture config)"*. Even here Phase 5 supplies *config*; Phase 8 generates.

Between them the directive is owned end to end by Phases 3/4/7 (with Phase 8 on the shadow side).
**Phase 5 has no composite-mipmap policy to consume the gate.**

**The document contradicts itself, and in the correct direction, twice.** §3's own `scale.<prog>` row
(**l. 1360**) records that *"Phase 5's *Scope — in* has no per-program sub-viewport bullet"* and
routes the application to Phase 7 from **l. 1684** — the line immediately after l. 1683. §3's
`countInstances` row (**l. 1356**) records that *"Phase 5 … has no pass-execution bullet at all"*. The
mipmap row is the outlier against the document's own repeated ruling about the same `DESIGN.md`
bullet.

**The §5 half.** `TextureService.generateMipmap`'s javadoc (**l. 2625**) reads
*"// caller checks supportsMipmapGeneration()"*, and in the composite path the caller is Phase 7. But
§5.2's `GLCapabilityProfile` row (**l. 3714**) lists its consumers as *"2, 3 (**the whole macro
header**), 4, 5, 6, 14"* — **7 is absent**. A Phase 7 session reading §5 is not told it consumes the
profile, while §3 tells a Phase 5 session that it does.

**Fix.** Correct l. 1340 to *"consumed by **Phase 7's** per-pass composite mipmap generation
(`DESIGN.md` l. 1683), from the per-slot bitmask **Phase 4** carries (l. 1334) and **Phase 3**
parses (`colortexNMipmapEnabled`, RESEARCH.md App A.3 l. 1185); Phase 5's mipmap interest is the
shadow-texture filter config at l. 1469, generated on by Phase 8 at l. 1828"*, and add **7** to
l. 3714's consumer column.

**Touches §5: yes** — the §3 cell alone touches nothing, but the coherent fix adds **7** to l. 3714,
and leaving §5 saying the profile has no Phase 7 consumer while §3 says Phase 7 consumes
`supportsMipmapGeneration()` would replace one inconsistency with another.

---

### V13-5 — *"exactly the member set §12 item 22a's review hook audits"* is false in both directions, and one member escapes the four-way taxonomy the "no new gap" result rests on · **note** · **touches §5: no**

**Location.** §4.12's glsm bucket table, **ll. 3518–3519**, and the summary at **l. 3524**. Against
§12 item **22a** (**l. 4700**) and `StateService` (**ll. 2629–2643**).

**Claim under test.** That the new glsm snapshot bucket is the round's *"strongest corroboration"*
because its members coincide exactly with what §12 item 22a audits.

**Evidence.** l. 3518's members are *"`getDepthStateMask`; `isBlendEnabled`, `getBlendMode`;
`getViewportWidth`/`getViewportHeight`"* — three aspects: depth mask, blend, viewport. Item 22a
(l. 4700) audits *"that `StateService.snapshot(...)` reads **real** state for **every** `StateAspect`
and returns no constant"*, and l. 2640 defines `StateAspect` as *"an engine enum with one constant per
verb above"* — eight verbs (viewport, clearColor, clear, depthMask, depthTest, blend, alphaTest, fog).
The glsm reads are a proper **subset**, three of eight. In the other direction, the one member item
22a's rationale is actually built on — PD §17 B11's `getColorMask()` — is bucketed in the **absent**
row at l. 3519, not in the bucket the sentence calls *"exactly"* item 22a's set.

Second limb: l. 3524's taxonomy — *"every member is a facade verb, a snapshot aspect, an absence
already recorded with a requester, or a concept 1.12.2 does not have"* — is what the "no new gap"
result rests on. `glGetTexLevelParameteri` is *placed* in the third bucket but its own cell
(l. 3519) explains that it is answered as a **value** from `mod.glue` and is **not** in §4.7.4's
absent-verbs table. It is therefore in none of the four classes as stated.

**Why it is a note and not a correction.** Both defects are overstatement in a corroboration sentence.
The bucketing itself is sound (§2 item 5), the conclusion — the second half adds no gap — survives
either way, and no design element moves.

**Fix if applied.** Downgrade to what is true: the glsm state reads are a *subset* of the reads item
22a audits, and `getColorMask` is the member whose hardcoding PD §17 B11 documents — which is *why*
the audit covers aspects glsm has no read for. Either add `glGetTexLevelParameteri` to §4.7.4's
absent-verbs table (requester: none today) or add a fifth clause at l. 3524.

---

### V13-6 — §4.12's bucket-2 cell asserts §2.1 already names every phase the inventory needs, in the same cell that hands `mod.glue` work to Phases 3, 5 and 13 · **note** · **touches §5: no**

**Location.** §4.12's first bucket table, **l. 3506**. Against §2.1's `:mod` table, **l. 1251**, and
§5.1's package-placement row, **l. 3702**.

**Claim under test.** That the `mod.glue`-provider bucket's closing reassurance holds against its own
contents.

**Evidence.** l. 3506 assigns the shim's world/camera/player accessors to *"**Phase 6**'s value
providers … **Phase 9** for `populateBlockIds`; **Phase 3** for the resource accessors; **Phase 7**
for `markRendererReloadRequired` …; **Phase 5/13** for mipmap levels"* — and then closes
*"§2.1 already assigns `mod.glue` to *"Phases 1 (facade impl shape), 6, 7, 9"* — the inventory adds no
phase that table does not name"*. It names three: **3**, **5** and **13**. §2.1 **l. 1251** confirms
the quoted column is exactly *"Phases 1 (facade impl shape), 6, 7, 9"*.

The same gap has a second edge worth recording in one place rather than two: §5.1 **l. 3706** now
makes **Phase 5** the definer of the vanilla-owned texture set, and §5.1 **l. 3702** binds every
dependent to *"a phase's code goes in the package §2.1 assigns it"*. Read together, Phase 5 owns
contents whose natural home is a package §2.1 does not assign it. §12 item **22b** (l. 4701) is the
mitigating text — Phase 1 ships *"a `mod.glue` implementation of `TextureHandle`"* and Phase 5 supplies
only *which* textures — so the code has an owner and this is a table gap rather than an orphaned
deliverable. That is why it is a note.

**Fix if applied.** Either add the missing phases to §2.1's `mod.glue` *Filled by* column with what
each fills, or delete the closing sentence at l. 3506 and let the cell's own assignments stand. If the
first branch is taken it touches §5 (§2.1 is an exposed §5.1 contract), which is a reason to prefer
the second unless the fix-up is already re-verifying.

---

### V13-7 — §4.7.3 sends the empty-provider case to §6, which carries no row for it, and the `LogSink` analogy it leans on fails on consequence · **note** · **touches §5: no**

**Location.** §4.7.3, **ll. 2480–2481** and **ll. 2499–2501**. Against §6's table, **ll. 3778–3792**.

**Claim under test.** That an empty `ForeignTextureProvider` — before install, or when a key has no
texture — is a *specified* degradation, *"the same degradation `LogSink` takes (§4.9.1, §6)"*, so the
caller has somewhere to look.

**Evidence.** l. 2481: *"the caller degrades per §6 rather than assuming presence"*. l. 2500 repeats
it and grounds it in the precedent: *"`active()` answers empty for every key, the same degradation
`LogSink` takes (§4.9.1, §6)"*. §6 carries a row for the analogue — **l. 3789**, *"**No `LogSink`
installed yet** … A no-op sink is active until `mod.core` installs the real one. Logging can never be
the thing that breaks startup"* — and **no row for the provider**. The pointer dangles.

The analogy also fails on consequence, which is the reason the missing row is worth a rung rather
than a sentence: a no-op `LogSink` costs a log line, whereas an empty provider means **unit 0 — the
vanilla block atlas — is unbound on every GBUFFERS and SHADOW program**. That is a pack-level visual
outcome in §G2.4's rung 3/4 territory, not a no-op, and §6's own preamble (l. 3775) makes rung 5 the
invariant every row serves.

**Why it is a note.** §4.7.3 does state the *behaviour* inline (`active()` answers empty; resolution
is lazy per call), so no dependent is left without an answer — only without a rung and without a
stated response.

**Fix if applied.** Add a §6 row for *"a vanilla-owned texture has no handle at bind time (provider
absent or key unknown)"* with its rung and behaviour — most plausibly a capability-gate-style pack-off
rather than a silently unbound unit — or drop the `§6` pointer and say the behaviour at §4.7.3.

---

### V13-8 — §3's vocabulary row asserts the facade contains no pack vocabulary at all; the §0.12 revision put App B.3's sampler names into an `engine.gl` signature's contract · **note** · **touches §5: no**

**Location.** §3's conformance map, last row, **l. 1361**. Against §4.7.3 **ll. 2473–2479** and
`DESIGN.md` §G4.1 **l. 501**.

**Claim under test.** That the row discharging §G4.1 for this phase is still true of the design as it
now stands.

**Evidence.** l. 1361's design-element cell is an assertion of absence: *"The facade deliberately
contains no pack vocabulary at all (it is below that layer), so no synonym risk is introduced here.
The phases that do carry pack vocabulary (3, 5, 6) inherit `§G4.1` directly"*. But **l. 2473** puts
the new declaration in `package com.schmaloogium.engine.gl;`, **l. 2478** makes it a normative
interface (`public interface ForeignTextureProvider`), and **l. 2479** specifies its key as *"App
B.3's sampler name used verbatim (§G4.1): "texture", "lightmap", … ."* — pack-facing vocabulary, in
the facade, cited to §G4.1 as its reason.

**Why the row's conclusion survives while its ground does not.** §G4.1 (**l. 501**) regulates verbatim
use — *"Pack-facing terms are used **verbatim** in code, docs, and identifiers … Do not invent
synonyms"* — rather than forbidding pack terms below the pack layer. So the design choice is
compliant; the *stated reason* for compliance ("contains none at all") is what the §0.12 revision
falsified. Note, not correction.

**Fix if applied.** Rewrite the cell to the true position: the facade carries exactly one
pack-vocabulary surface — `ForeignTextureProvider.handleFor(String key)`, keyed on App B.3's sampler
names used verbatim per §G4.1 — and no synonym is introduced because the key *is* the pack term. If
V13-2 is resolved by widening the key vocabulary, this row is where the widening is also stated, and
**13** joins the parenthetical list of vocabulary-carrying phases.

---

## 2. What was checked and came back clean

Named because a round reporting only findings misrepresents its coverage, and because on a document
at this maturity an argument that holds on derivation *is* part of the round's product. Items 1–4 and
7–8 are my own derivations; items 5–6 report the fan-out's coverage as the fan-out's.

**1. Dropped on my own derivation: the candidate that §3 leaves App A.3's `/* DRAWBUFFERS */` `N`
state and the pre-link attribute locations 10/11/12 unmapped.** The candidate argued that
`FramebufferService.drawBuffers` (l. 2572, whose javadoc at ll. 2569–2571 rules that a zero-length
array *is* the contract's `N` state) and `ShaderService.bindAttributeLocation` (l. 2541) each serve a
contract item with no §3 row naming it. **Three prior rounds have settled this, and the third settled
this exact limb by name.** `PHASE_1_REVIEW_3.md` l. 205 ran an item-by-item contract sweep and cleared
*"`DRAWBUFFERS` / `RENDERTARGETS` → `drawBuffers`"* and *"§4.2's pre-link attribute binding at 10/11/12
→ `bindAttributeLocation`"* on the reading that "unmapped" means **unserved by a verb**;
`PHASE_1_REVIEW_11.md` §2 item 3 (ll. 390–405) re-derived that standard, added the second test — a §3
row is owed where *this phase* takes a contract-visible **design decision** that needs provenance —
and then addressed the zero-length ruling explicitly: *"The one exception — `drawBuffers`' ruling that
a zero-length array *is* the "none" state — does carry such a decision, and it **does** cite
provenance inline"*, sending only the wrong section number onward as V11-3. `PHASE_1_REVIEW_12.md` §2
item 1 dropped the sibling candidate on the same ground. The provenance is still cited inline at
ll. 2569–2571 (RESEARCH.md §4.4's first-person overlay; App A.3's `N` = none, which resolves at
RESEARCH.md l. 1187). Re-raising this is loop maintenance, not review. **Not a finding.**

**2. The framebuffer row's *disposition* survives; only its ground fails, and I checked the
difference.** I tested whether V13-1 should ask for the row to become a slot of `[D-P1-36]`'s shape,
and concluded it should not. The texture gap is a slot because `bindToUnit` is a facade verb whose
argument is unreachable — the engine must bind those units itself, during its own program draws. The
framebuffer case has a legitimate outside-the-facade answer: the bind happens in `:mod`, at Phase 7's
hook, which is what `DESIGN.md`'s own injection timeline already contemplates (**ll. 1709–1710** list
*"rebind default FB"* as work at hook sites 3 and 4, and **l. 1713** puts *"composite-all then final
to the MC framebuffer"* at `renderWorldPass` TAIL). That is the colour-mask row's pattern at l. 2928,
and §4.7.4's closing paragraph (ll. 2935–2937) gives Phase 7 a sanctioned additive route if it wants
the verb instead. So the row belongs where it is; V13-1 is confined to the reason printed in it.

**3. V12-1's ownership direction re-derived at the lines, and it is right.** `DESIGN.md` **l. 1488**
puts *"you own which texture object backs each unit per stage"* inside **Phase 5's** *Scope — in*
(the bullet opens at l. 1487), and **l. 1563** gives Phase 6 only *"**Sampler re-pointing**: on every
use-program, sampler uniforms re-point to the App B.3 fixed unit map"*; Phase 6's spec carries no
texture-binding bullet. The narrowing of Phase 13 to its `minecraft:`-asset forms is also right:
`DESIGN.md` **ll. 2253–2257** has Phase 13 *stitching* the `_n`/`_s` companions itself — *"full
companion atlases with matching mip chains … stitch/load hooks on `TextureMap`/`TextureAtlasSprite`"*
— so those come from `TextureService.create` and need no foreign handle, exactly as l. 3706 says.
V13-2 is about the key **space** of the provider, not about this attribution, which holds.

**4. The App B.3 grounding of `[D-P1-36]` itself.** RESEARCH.md **ll. 1231–1232** do put `texture` at
unit 0 and `lightmap` at unit 1 on GBUFFERS/SHADOW, both Minecraft-owned, neither producible by
`TextureService.create(String)` — so the slot is real and the refusal of `adopt(int glName)` on the
seam is the right call for the reason given. The whole of V13-2 and V13-3 is downstream of a correct
decision, which is worth saying plainly: the finding density on `[D-P1-36]` this round is the cost of
a good decision being specified in one pass, not evidence the decision is wrong.

**5. The sealing repair — reported by the fan-out, spot-checked by me, and I could not break it.**
Two independent agents swept the document for `sealed`/`permits` and reported: `GLHandle sealed
permits` over four same-package `non-sealed` interfaces is legal Java; an interface cannot be `final`,
so `non-sealed` is the only legal modifier on a leaf that must admit out-of-package implementations;
no surviving claim rests on leaf sealing (opaqueness lives in the absence of a GL-name accessor plus
`SeamBytecodeTest`, §4.7.3 ll. 2429–2432; §12 item 18 now tests the `permits` clause **and**
out-of-package implementability; §10.3's drill wants the opposite; `UniformLocation`'s `isAbsent()`-only
opacity never used the modifier); and the only other sealed type, `CompatVerdict` (l. 3331), permits
records in its own compilation unit. I re-opened §12 item 18 (**l. 4695**), §5.2's opaque-handle row
(**l. 3716**) and §5.2's changelog row (**l. 3712**) and they agree with each other and with §4.7.3.
Refusing branch (b) was right for the reason given.

**6. The glsm bucketing's completeness — the fan-out's coverage, marked as such.** Every one of the
59 + 31 declarations in `GLStateManagerService` and `RenderSystemService` was checked against the four
buckets by the fan-out with no member unplaced and no misassignment; I re-derived only the two
placements V13-5 turns on (`getColorMask` in the absent bucket, `glGetTexLevelParameteri` dismissed via
the `atlasSize`-as-value route) and both are defensible on their merits. Only the taxonomy sentence
over them overstates, which is V13-5.

**7. The consumed direction of interface honesty is vacuous and correctly stated.** `DESIGN.md` §G5.1
**l. 567** gives Phase 1 *"Depends on: —"*, and §5 **l. 3682** says *"Phase 1 **consumes** nothing — it
has no dependencies (§G5.1)"*. There is no dependency §5 to audit against, so *Interface honesty* ran
in the exposed direction only — which is where V13-2 and V13-3 live.

**8. Structural and repository checks I ran myself.** All thirteen §G9 sections are present as `##`
headings in order (0 Header … 12 Implementation checklist) at ll. 5, 1153, 1206, 1328, 1392, 3680,
3772, 3796, 3861, 3970, 4015, 4230, 4654. On the repository state, which round twelve's §G1.3 line
explicitly handed to this session: `PHASE_FACTS[1]` (`.claude/workflows/verify-loop.js` ll. 113–115)
reads `docVersion: 'v11'`, `design: 'v2.0-RC2'`, `spec: '957-1067'`/`docGate: '1056-1060'`, and `git
log` shows `0a78da5 docs: phase 1 round twelve — verify + fix-up (PASS-WITH-CORRECTIONS)` as HEAD.
The document's §0 anchor block (**ll. 12–17**) and its closing note (**ll. 4779–4798**) describe that
state correctly — including the per-phase resolution rule and the statement that the phase is **not
verified** with a §5 change outstanding. V12-8's defect did not recur.

---

## 3. Verdict

# PASS-WITH-CORRECTIONS

**Four corrections, four notes, zero blocking.** No finding is blocking and none requires a rebuild:
every one is a bounded edit to §3, §4.7.3, §4.7.4, §4.12, §5.1, §5.2, §6 or §11.4.

**Why PASS was not available.** Three of the four corrections are in material one round old and never
reviewed — the `ForeignTextureProvider` declaration §12 item 22b ships at **v0.1**, and the
absent-verbs row V12-6 produced. Two of them are the §G1.2 check named for exactly this case,
*"everything promised to dependents is specified, not gestured at"*: a type exposed in §5 to three
consumers whose key space cannot name what one of them must fetch (V13-2), and a handle that is
type-identical to an engine-created one with no statement of which verbs may take it or how long it
lives, sitting under a lifetime rule that orders the phase which owns it to delete and re-create its
handles routinely (V13-3). The third is an `[A]`-tagged claim that a source in this phase's own
mandatory reading contradicts, printed as the ground for a classification a v0.1 dependent reads
(V13-1). The fourth is an ownership misattribution against `DESIGN.md` that the document's own §3
contradicts twice in adjacent rows (V13-4). Calling any of the four a note to reach PASS would be the
inverse of what this cadence guards against.

**Equally, nothing was manufactured to fill a round.** One candidate was dropped outright on ground
three prior rounds already settled and is recorded in §2 item 1 with the passages that settle it; two
pairs of candidates were merged into single findings rather than reported twice; three candidates that
arrived proposed as corrections are notes here, recorded and **left unapplied**. The mature body of
this document — §4.1 through §4.11, §4.13, §6 through §8, §10, §11, and every facade signature —
came back clean under every check run on it this round, and the sealing repair, which was the largest
single change the last fix-up made, could not be broken by two independent sweeps or by mine.

### Per-finding §5 disposition

| Finding | Severity | Touches §5? |
|---|---|---|
| **V13-1** l. 2931's `[A]` ground — *"a `final` pass that simply does not rebind writes there already"* — is refuted by RESEARCH.md ll. 545–546, and l. 3606's *"no contract item is unserved today"* by l. 526 | **correction** | **no** — §4.7.4 l. 2931 and §4.12 ll. 3601–3606; §5.2 l. 3722 already names the requester and carries none of the premise. **Yes** only if the fix-up chooses to state Phase 7's v0.1 obligation in that row |
| **V13-2** the provider's key is App B.3's sampler names and its key set is Phase 5's, while §5.1 names **13** for `minecraft:`-asset forms neither can express | **correction** | **yes** — l. 3706 carries both the vocabulary and the consumer; §4.7.3 ll. 2479–2483, §4.12 l. 3575, `[D-P1-36]` l. 4271 and §11.4 l. 4572 follow it |
| **V13-3** a foreign `TextureHandle` is type-identical to an engine handle; no verb restriction, no validity rule, and §5.2 l. 3716 presents both under one lifetime regime | **correction** | **yes** — l. 3716 must carry the bind-only rule and the exclusion from the delete-based lifetime rule, or §5 is not "sufficient on its own" |
| **V13-4** §3 l. 1340 gives Phase 5 a *"composite-mipmap policy"*; `DESIGN.md` l. 1683 puts per-pass composite mipmap generation in Phase 7, l. 1334 the bitmask in Phase 4, l. 1469 Phase 5's only mipmap interest in the shadow filter | **correction** | **yes** — the §3 cell alone touches nothing, but the coherent fix adds **7** to §5.2's `GLCapabilityProfile` consumer column at l. 3714 |
| **V13-5** *"exactly the member set §12 item 22a's review hook audits"* is a proper subset in one direction and excludes `getColorMask` in the other; `glGetTexLevelParameteri` escapes l. 3524's four-way taxonomy | note | **no** — §4.12 ll. 3518–3519, 3524 |
| **V13-6** §4.12 l. 3506 asserts §2.1 names every phase the inventory needs, in the cell that hands `mod.glue` work to Phases 3, 5 and 13 | note | **no** on the sentence-fix branch; **yes** if the fix-up instead adds the phases to §2.1, which §5.1 l. 3702 binds dependents to |
| **V13-7** §4.7.3 ll. 2481/2500 delegate the empty-provider case to §6, which has no row for it; the `LogSink` analogue at §6 l. 3789 fails on consequence | note | **no** — §4.7.3 and §6 |
| **V13-8** §3 l. 1361's *"the facade deliberately contains no pack vocabulary at all"* is falsified by §4.7.3 ll. 2473–2479 | note | **no** — §3's last row |

### §G1.3 line

**§G1.3's *"re-verify only if §5 changed"* trigger fires.** Three corrections alter §5 — V13-2, V13-3
and V13-4 — on the textual reading rounds eight through twelve all applied. Accordingly:

- A fix-up session (§G1.3) applies the four corrections to `docs/phase1/v11/PHASE_1_DOC.md` and
  records each resolution under a `## Resolutions` heading **in this file**. The four notes may be
  applied or declined; a decline should carry its reason, per the convention §0.6 established.
- **V13-2 and V13-3 should be resolved together, at §4.7.3 and §5.2 in one pass.** They are the same
  omission seen from two sides — the provider block specifies the type's *existence* thoroughly and
  its *use* not at all. One added paragraph in the javadoc (key space; bind-only; validity across a
  vanilla reload) plus one sentence in §5.1 l. 3706 and one in §5.2 l. 3716 answers both.
- **V13-1 should not be resolved by promoting the row to a slot.** §2 item 2 records why: the bind has
  a legitimate outside-the-facade home at Phase 7's hook, and §G1.1's "flag, do not decide" plus the
  facade's own "no verb without a consumer" both point at fixing the printed reason rather than the
  disposition.
- **V13-4 should not be resolved by editing `DESIGN.md`'s attribution into §3.** §11.5 is the route
  for an upstream request; this is not one — `DESIGN.md` is internally consistent on composite mipmap
  generation across four lines, and §3 is the outlier.
- Because that fix-up alters §5, **`PHASE_1_DOC.md` goes through a fresh verify session before any
  dependent consumes it.** Until that round returns the phase is **not** verified under §G1.3's third
  bullet, the document is **not** a valid dependency input, and Phase 2, Phase 3 and everything
  downstream stay blocked (§G5.3).
- **If the fix-up applies only V13-1 and declines the other three**, §5 is untouched and no further
  verify session is owed — but a decline of V13-2 or V13-3 must be argued at the line against
  `DESIGN.md` ll. 2269–2271, RESEARCH.md ll. 488 and 1481–1483, and §12 item 22b, not against this
  review's framing.
- **What a fourteenth session should expect.** The unreviewed surface after this fix-up is small and
  bounded: whatever §0.13 adds to §4.7.3's provider block and to §5's two rows. Everything else in
  this document has now been swept by thirteen rounds, and the clean-to-dirty ratio in §2 is the
  honest signal — if a fourteenth round finds nothing outside the §0.13 material, that is the expected
  result and PASS is the right verdict for it.

*Per §G1.2 this session stops here. It wrote no code, ran no build, no test and no gradle task,
launched no writing agent, made no network request, and created exactly one file: this one.
`docs/phase1/v11/PHASE_1_DOC.md`, `docs/design/v1.1/DESIGN.md`, `docs/design/v2.0-RC1/DESIGN.md`,
`docs/design/v2.0-RC2/DESIGN.md`, `docs/research/v1/RESEARCH.md`,
`docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`, `docs/MOVES.md`, the `/verify-loop` harness and
its operator documentation, and `PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_12.md` — including their
`## Resolutions` sections, which are evidence — are unmodified.*

---

## Resolutions

*Written by the **§G1.3 fix-up session for round thirteen** (2026-07-26), which applied the four
corrections to `docs/phase1/v11/PHASE_1_DOC.md` and deferred the four notes. Per §G1.3 the argument
lives here and the document's §0.13 addendum carries one-line rulings only. Nothing above this heading
was modified: this review's §0–§3 are the round's evidence and stay as written, including the two
places where its supporting argument is narrowed below.*

**Re-derived, not adopted.** A review's supporting argument is not evidence, so every load-bearing
claim below was re-opened at its source by this session before it was written into the document:
`DESIGN.md` (RC2) ll. 1334, 1409–1525 swept in full for every occurrence of "mipmap", 1469, 1486, 1488,
1563, 1683–1685, 1709–1713, 1828, 2269–2271, 2287, and §G5.1's phase table ll. 565–588; RESEARCH.md
ll. 487–489, 526, 543–546, 810–816, 1185, 1229–1248, 1396 (App E's column heading, which is what
V13-1's third limb turns on), 1414, 1479–1488; and
`reference-src/pintonium-9c2fcc1/common-shaders/src/main/java/net/irisshaders/iris/pipeline/FinalPassRenderer.java`
l. 289, read as **behavioural observation only** per §G11.2 — nothing was copied and no structure,
identifier or class name from it appears in the document. No `chatlogs/` directory below `docs/` and no
root-level `*.txt` was opened, including the 108 KB dated transcript sitting at the repository root.
No network, no sub-agents, no build, no test, no gradle task. Two files were written: the phase doc and
this one.

**The §5 gate, run and not asserted.** `awk '/^## 5\. Cross-phase interfaces/,/^## 6\. Failure modes/'
| sha256sum`, by content anchor rather than line number:

| | Digest |
|---|---|
| **before** | `a484a0cdc336812ccdd9cb70f7d6d93704ad317c5eb602474ed23014c2fa9124` |
| **after** | `21283605083beffbcaba020d633690b1abb737467da975544583b430b740623c` |

**§5 changed.** That is the expected outcome — V13-2, V13-3 and V13-4 all touch it by the review's own
disposition table, and V13-1 was taken onto its §5 branch deliberately (argued under V13-1 below). So
§G1.3's re-verify trigger fires and a **fourteenth** verify session is owed.

---

### V13-1 — applied, on the branch the review's §2 item 2 argued for, **and extended onto §5**

**Re-derived first, because the finding turns on an inference rather than on a coordinate.** All five
limbs hold at their sources, and the third and fifth are the ones that decide it:

1. RESEARCH.md **ll. 545–546** end the per-frame flow *"└─ COMPOSITE passes (fullscreen ping-pong,
   per-pass mipmap gen + render-scale viewports)"* → *"→ FINAL to screen"*, with no vanilla step
   between. A GL framebuffer binding is sticky, so the binding in force when `final` runs is the last
   composite's target.
2. RESEARCH.md **l. 526** — *"**Final** renders to the vanilla framebuffer (anaglyph-aware color
   masking)"* — is an unserved contract item, which falsifies §4.12's *"no contract item is unserved
   today"* directly.
3. **App E's last column is headed *"Serves hook needs (§7.1)"*** (RESEARCH.md l. 1396), so row 17's
   `6` is hook need 6 — *"Deferred-stage trigger between solid and translucent terrain; composite/final
   at frame end"* (l. 813). The row catalogues a **site the mod must hook**. It carries no claim about
   vanilla bracketing at all.
4. `FinalPassRenderer.java` **l. 289** is `MINECRAFT_SHIM.bindMainFramebuffer();` under *"Also bind the
   'main' framebuffer if it isn't already bound"* — the working implementation of this contract on this
   platform binds explicitly. Behavioural observation, §G11.2.
5. `DESIGN.md` **l. 1486** (Phase 5 *Scope — in*) and **l. 1685** (Phase 7's composite/final bullet) put
   the target on both sides of the seam **untagged**, while l. 1684's immediate neighbours `scale.<prog>`
   and `countInstances` both carry `[v0.5]`. §G5.1 (ll. 571, 573) puts Phase 5 at v0.1 and Phase 7 at
   v0.1 exit.

**One correction to the review's own framing, recorded rather than propagated.** §1's limb 1 says the
premise *"vanilla binds and unbinds its own FBO around the world render"* is refuted. It is not — that
premise is true, and the document was right about it. What fails is the **inference** drawn from it: the
engine's own composite chain rebinds between vanilla's bind and the `final` pass, so vanilla's bracketing
does not survive to the moment that matters. The distinction is worth keeping because a fifteenth reader
who checks the premise will find it holds and may conclude the finding was wrong. §4.7.4's replacement
text is written against the *inference*, not against the premise.

**Applied as:** the disposition is unchanged — the row stays in the absent-verbs table and is **not**
promoted to a `[D-P1-36]`-shaped slot. §2 item 2's ground was re-derived and holds: `bindToUnit` is a
facade verb whose *argument* is unreachable, so only a facade-side slot can serve it, whereas the
framebuffer bind is a **call the facade never makes** and `DESIGN.md`'s own injection timeline already
houses it — *"composite-all then final to the MC framebuffer"* at `renderWorldPass` **TAIL** (l. 1713),
with *"rebind default FB"* at hook sites 3 and 4 (ll. 1709–1710). What changes is the printed reason.

- **§4.7.4, l. 3016** (the absent-verbs row's *Why absent* cell): the `[A]` sentence is **deleted**. The
  row now grounds the absence in *no verb here binds it, and the bind has a legitimate home outside the
  facade*, cites the v0.1 sources on both sides, states what App E row 17 actually says, states the
  sticky-binding consequence, and names the `bindsBalanced()` blind spot. The consumer column tells
  Phase 7 the obligation is v0.1.
- **§4.12, ll. 3695–3709**: the *"nobody has asked"* / *"no contract item is unserved today"* pair is
  replaced. The two gaps still differ, and the difference is now stated as what it is — one needs a
  facade **argument**, the other needs a **call outside the facade** — rather than as an absence of
  demand that RESEARCH.md l. 526 falsifies.
- **§5.2, l. 3825** (the non-verbs row): **this is the judgement call.** The review scored V13-1
  *"touches §5: no"*, on the ground that §5.2 already names 7 as the requester and carries none of the
  refuted premise. Both halves of that are true. It was applied to §5 anyway, for a reason the review
  itself names as the only one that would justify it: §5 opens (l. 3795) with the promise that it is
  *"sufficient on its own"*, and a Phase 7 session that reads only §5 would learn that a verb is absent
  and that it may request one — not that its `final` pass must arrange the bind itself at v0.1. The
  "prefer §4.7.4, don't touch §5" argument was weighed and rejected on the ground that it is an argument
  about **cost**: §5 changes regardless under V13-2, V13-3 and V13-4, so keeping V13-1 out of §5 buys no
  avoided re-verify and pays a sufficiency gap for it. The added text is one clause plus two sentences,
  states no new assignment — the obligation is `DESIGN.md`'s at ll. 1486/1685/1713, quoted, not this
  document's — and repeats the `bindsBalanced()` limit where a dependent will actually meet it.

---

### V13-2 — applied on the **widening** branch, not the retraction branch

**Re-derived.** App B.3 (RESEARCH.md ll. 1229–1248) is a sixteen-row per-unit naming scheme, one bare
name per unit per stage, and — checked explicitly, because the fix rests on it — **no name in it
contains a colon**. App F.5 (ll. 1481–1484) gives the custom-texture form as
`texture.<gbuffers|deferred|composite>.<samplerName>[.0-9]=<source>`, where the source may be a
`minecraft:`-prefixed asset *"incl. `dynamic/lightmap_1`, atlas paths"*; `DESIGN.md` ll. 2269–2271
repeat it in Phase 13's *Scope — in*, and l. 2287 puts *"unit-map ownership (Phases 5/6)"* in its
*Scope — out*. The finding's core holds exactly as stated: **the sampler name is the directive's
destination and the resource location is its source**, and the provider was keyed on the destination
while Phase 13 holds the source.

**Why the widening branch rather than dropping 13.** Three reasons, in the order they decided it.
*(a)* §5.1 had already promised Phase 13 the route; retracting a promise a dependent has not yet read is
only better than fixing it if the fix is out of this phase's remit, and it is not — Phase 1 owns the
provider's shape and `mod.glue`'s implementation shape (§2.1, §12 item 22b). *(b)* The widening costs **no
signature change**: `handleFor(String)` is untouched, and only the documented key vocabulary moves.
*(c)* The route it opens is field-proven and was already sitting in the inventory that produced the slot
— §4.12 l. 3628 lists the shim's `getTextureManager()` beside `getColorTextureId()`/`getLightTextureId()`,
and a `TextureManager`-keyed lookup **is** resource-location resolution. The gap was that this document
took the two unit-map accessors from that list and not the third.

**One thing the review's fix text would have got wrong, and the document does not.** Both offered
branches treat every `minecraft:` form as needing a foreign handle. It does not: a `minecraft:` asset
that is a **static** PNG can be read through the resource accessors and pushed through
`TextureService.create`/`allocate`/`upload` like any pack texture, and needs no provider at all. Only the
**live** vanilla-owned objects — the lightmap and the atlases — are unreachable any other way. The
document says so at §4.7.3, §4.12 and §11.4, because a Phase 13 session that routes all three source
forms through the provider would be building the wrong thing.

**Applied as:**

- **§4.7.3, ll. 2520–2541** — `handleFor`'s javadoc declares the key as *a pack-facing texture
  identifier used verbatim (§G4.1)* in **two vocabularies**, (a) App B.3's bare sampler names and
  (b) `minecraft:`-namespaced resource locations for App F.5's live-asset forms, with the disjointness
  stated and its ground given (no App B.3 name has a colon; a resource location always does). The
  key-set ownership sentence is split to match: (a) is Phase 5's with Phase 6 pointing, (b) is Phase
  13's, and **Phase 1 enumerates neither**. §G4.1 is satisfied in both directions — each key *is* the
  pack term, so no synonym is introduced.
- **§5.1, l. 3809** — the provider row carries both vocabularies, the disjointness, the static-asset
  exclusion, and an owner per vocabulary; the consumer column's **13** now says what Phase 13 *owns*
  rather than only what it consumes.
- **§4.12, ll. 3664–3672** — states *why* there are two vocabularies (the source/destination confusion),
  which is the part a later reader would otherwise re-derive.
- **§11.4's *To Phase 13* block, ll. 4699–4712** — new. This block *"currently tells Phase 13 nothing
  about the provider"* was the finding's fourth limb and is the most consequential half of the fix: the
  hand-off now splits Phase 13's `minecraft:` form into its static and live halves, names vocabulary (b)
  as Phase 13's to define, and states the bind-only rule.
- **Neighbours swept and edited because the same formulation lived in them:** §2.4's key-type row
  (l. 1361), §9's provider milestone row (l. 4094 — vocabulary (b)'s keys arrive at Phase 13's `v0.5`,
  not with the unit map at v0.1), `[D-P1-36]` (l. 4374), §12 item 22b (l. 4834), and §11.4's *To Phase 5*
  block (l. 4636, where "keyed on App B.3's sampler names" is now named as vocabulary (a)). A grep for
  `sampler name`, `App B.3`, `ForeignTexture` and `keyed on` was run over the finished file and no
  single-vocabulary formulation survives.

---

### V13-3 — applied, with the two unspecified rules **decided** rather than flagged

**Re-derived.** The type-identity claim is exact: l. 2484 (now l. 2541) returns `Optional<TextureHandle>`,
and `TextureService`'s `allocate`/`setParameters`/`upload`/`generateMipmap`/`delete` all take
`TextureHandle` with no precondition. The lifetime rule at ll. 2486–2493 orders **Phase 5** to *"drop and
re-create its handles across an uninit/rebuild"*; §5.1 makes **Phase 5** the definer of the vanilla-owned
set; RESEARCH.md l. 488 makes uninit *"delete all GL objects"* on a pack, option or dimension change.
The failure is therefore not hypothetical and not obscure: **the same phase owns the delete loop and the
table the foreign handles sit in.** §6 l. 3879's rung 5 is the invariant that breaks, and §6 carries no
row for it.

**Both open questions were decided here rather than handed on, and that is a deliberate call.** §G1.1's
*"flag, do not decide"* governs another phase's boundary; these two are properties of a type this phase
declares and an implementation this phase ships (§12 item 22b), so deciding them is Phase 1's job and
deferring them would reproduce the very failure `[D-P1-36]` exists to prevent.

1. **Bind-only.** Legal to `TextureService.bindToUnit` and `DebugService.label` (§4.7.4 l. 2747 — a debug
   label on a vanilla object is harmless and dev-only); illegal to `allocate`, `setParameters`, `upload`,
   `generateMipmap` and `delete`. The backend **rejects with `IllegalArgumentException`** rather than
   no-op'ing. Rejection was chosen over a silent no-op because §4.7.3 already classes use-after-delete as
   *"a programming error in the caller"* and treats it the same way, and because a no-op would let a
   Phase 5 uninit loop appear to succeed. **No new §6 row is added**: an exception from `mod.glue` is
   already covered by §6's last row (`:engine` throws → `mod.core`'s wrapper, engine disabled, vanilla
   path resumes), which is a *stronger* outcome than the corrupted-atlas one it replaces.
2. **Validity across a vanilla reload.** The `mod.glue` handle **resolves the underlying object at each
   use** rather than capturing a GL name when handed out. This extends `ForeignTextures`' existing
   lazy-per-call rule from the *lookup* to the *handle*, which is why it is the cheap answer: it makes
   caching and re-asking equally correct, so no consumer needs a re-acquisition protocol, and it is the
   only answer that survives Minecraft re-creating its textures on a schedule the engine cannot see.

**Applied as:** a new three-bullet block at **§4.7.3 ll. 2560–2586**; the same two rules restated in
**§5.2's opaque-handle row, l. 3819**, which is where the finding correctly said they had to be or §5 is
not sufficient on its own; the bind-only sentence in **§5.1's provider row (l. 3809)**; the
client-crashing trap spelled out for its owner in **§11.4's *To Phase 5* block (ll. 4643–4651)** —
*"treat a foreign handle as something you were lent: bind it, never free it"*; **§12 item 22b (l. 4834)**
gains both as review obligations, with the reason they cannot be tests (`SeamBytecodeTest` sees no GL
name and no MC type crossing either way); and `[D-P1-36]` (l. 4374) and §2.4 (l. 1361) carry the
one-clause form. §4.7.3's opening javadoc no longer says the handle is *"consumed like any other
TextureHandle"* — that sentence was the defect's source and is replaced by *same type, narrower
contract*; a grep confirms no instance of the old formulation survives.

---

### V13-4 — applied, and **widened by one phase** on re-derivation

**Re-derived at every occurrence of the term, which is what settles it.** `grep -n mipmap` over RC2's
Part II returns exactly five lines: **1334** (Phase 4's program-registry bullet — *"composite-mipmap
bitmask"* as per-slot state, populated from Phase 3's `PackConfiguration`), **1338** (the same bullet's
REV1 cross-check against PD §3.3), **1469**, **1683** and **1828**. Phase 5's spec runs **ll. 1409–1525**
and l. 1469 is its **only** mipmap line — *"per-texture nearest/mipmap filter"* config on the shadow FBO.
l. 1683 sits inside **Phase 7**'s spec (ll. 1642–1793) under *Composite/final execution*; l. 1828 sits
inside **Phase 8**'s (ll. 1794–1871). The directive itself, `colortexNMipmapEnabled`, is RESEARCH.md App
A.3 l. 1185, which Phase 3 parses. **Phase 5 has no composite-mipmap policy anywhere in `DESIGN.md`**,
and the document contradicts itself in the correct direction twice — §3's `scale.<prog>` and
`countInstances` rows route the two directives sitting in the *same* l. 1683–1684 bullet to Phase 7.

**Widened, and this is the round's second judgement call.** The review's fix adds **7** to §5.2's
`GLCapabilityProfile` consumer column. Applying only that would have left the identical defect one phase
over: `TextureService.generateMipmap`'s javadoc (l. 2625, now l. 2710) obliges its **caller** to check
the gate, and `DESIGN.md` l. 1828 makes **Phase 8** a caller — *"per-config mipmap generation on shadow
textures (Phase 5 texture config)"*, where Phase 5 supplies the config and Phase 8 generates. Since the
corrected §3 cell now says exactly that in prose, leaving 8 out of §5.2 would have replaced one
inconsistency with another, which is the ground the review itself gives for adding 7. Both were added,
each with its citation in the cell. This is the finding's blast radius, not an extension of its scope,
but it is a claim about Phase 8 that this session made and not one the review made — flagged as such.

**Applied as:** **§3, l. 1385** — the cell now names Phase 7 as the consumer with l. 1683, traces the
bitmask to Phase 4 (l. 1334) and the directive to Phase 3 (App A.3 l. 1185), names Phase 8 for the
shadow side (l. 1828), and states positively that Phase 5's only mipmap interest is l. 1469's filter
config. **§5.2, l. 3817** — consumers become `2, 3, 4, 5, 6, 7, 8, 14`, with the gate's caller-checks
rule stated as the reason. `DESIGN.md` is **not** edited and no §11.5 item is raised: it is internally
consistent across four lines and §3 was the outlier, exactly as the review's §G1.3 line says.

---

### Notes deferred

Per this round's §G1.3 instruction, notes are recorded rather than applied. Each is a considered
deferral with its reason, not an oversight — and two of the four are **enlarged** by corrections this
session applied, which is stated here so the fourteenth round can price them correctly.

- **V13-5 — the "exactly the member set item 22a audits" overstatement, and `glGetTexLevelParameteri`'s
  escape from §4.12 l. 3609's four-way taxonomy.** Deferred. Both limbs were re-derived and both hold:
  the glsm state reads are three aspects of `StateAspect`'s eight, and `getColorMask` — the member item
  22a's rationale is actually built on — is bucketed in the *absent* row. The reason for deferring is the
  one the note itself gives: the defect is confined to a corroboration sentence, the bucketing under it
  is sound and the *"no new gap"* result survives either way, so nothing a dependent builds against
  moves. **Unaffected by this round's edits** — no correction touched §4.12's glsm half.
- **V13-6 — §4.12 l. 3591's closing reassurance that §2.1 names every phase the inventory needs.**
  Deferred. Re-derived and true as a finding: the cell hands work to Phases 3, 5 and 13 and §2.1's
  `mod.glue` row (l. 1296, unchanged) reads *"Phases 1 (facade impl shape), 6, 7, 9"*. Deferred because
  the note's own preferred branch (delete the sentence) is cosmetic and its other branch edits §2.1,
  which §5.1 l. 3805 binds every dependent to. **One input the fourteenth round should have:** V13-2's
  resolution makes the `minecraft:`-resolution half of the provider part of the **`mod.glue` implementation
  shape Phase 1 already owns** under §2.1's *"Phase 1 (facade impl shape)"*, so the provider is *not*
  another phase the table fails to name. That narrows V13-6 rather than widening it — it is now about the
  world/camera accessors only.
- **V13-7 — §4.7.3's empty-provider case points at a §6 that has no row for it.** Deferred, **and
  enlarged by V13-3's resolution — stated plainly rather than left to be discovered.** The note is
  right that the pointer dangles and that the `LogSink` analogy fails on consequence (a no-op sink costs
  a log line; an unbound unit 0 is a pack-level visual outcome). V13-3's per-use resolution rule adds a
  *second* way to reach the same case: a handle handed out when the texture existed can resolve to
  nothing later, if Minecraft drops the object without replacing it. The document does not paper over
  this — §4.7.3's third bullet says resolution happens at each use, and the empty case still routes to
  §6 — but the missing §6 row is now load-bearing for two paths rather than one. It was not applied
  because it is a note and because the row's *content* (rung, and whether the response is a
  capability-gate-style pack-off or a silently unbound unit) is a degradation-policy call worth a round's
  attention rather than a fix-up's spare paragraph. **This is the item a fourteenth session should
  address first.**
- **V13-8 — §3 l. 1406's *"the facade deliberately contains no pack vocabulary at all"*.** Deferred,
  **and enlarged by V13-2's resolution.** The note is correct that the row's *conclusion* survives while
  its *ground* does not: §G4.1 (`DESIGN.md` l. 501) regulates verbatim use rather than forbidding pack
  terms below the pack layer, so the design is compliant and only the stated reason is false. V13-2's
  widening makes the facade carry **two** pack vocabularies where it carried one, so the sentence is now
  false in one more way than the review found it. It was not applied because the contract for this round
  is explicit that notes are recorded rather than applied, and because the note's own fix text makes the
  rewrite conditional on how V13-2 resolves — which is now known, and is recorded here so the fourteenth
  round rewrites it once, correctly: the facade carries exactly one pack-vocabulary **surface**,
  `ForeignTextureProvider.handleFor(String key)`, whose key is App B.3's sampler names or a `minecraft:`
  resource location, both used verbatim per §G4.1 and neither a synonym; and **13** joins **3, 5, 6** in
  the row's parenthetical list of vocabulary-carrying phases.

---

### §G1.3 status

**§5 changed** (digests above), so §G1.3's *"re-verify only if §5 changed"* trigger **fires**. Under
§G1.3's third bullet `PHASE_1_DOC.md` is **not verified**, it is **not** a valid dependency input, and
Phase 2, Phase 3 and everything downstream stay blocked (§G5.3) until a **fourteenth** verify session
returns.

**What altered §5, at four rows:** §5.1's provider row (V13-2's two vocabularies and their owners,
V13-3's bind-only rule); §5.2's `GLCapabilityProfile` row (V13-4's consumers **7** and **8**); §5.2's
opaque-handle row (V13-3's bind-only rule and lifetime exclusion); §5.2's non-verbs row (V13-1's v0.1
obligation on Phase 7). **No facade verb, result type, value type or declaration changed** — the seven
services, the handle types, `UniformLocation` and `ForeignTextureProvider`'s signature are byte-for-byte
what round thirteen reviewed. Everything that moved is **contract stated in prose about types that
already existed**, which is the honest shape of this round and the reason no dependent's call site
changes.

**What a fourteenth session inherits.** The four deferred notes, two of them enlarged by this fix-up and
both named above. Fresh unreviewed material, priced at round seven's rule: §4.7.3's widened key
vocabulary and its three-bullet bind-only block, §5.1's and §5.2's rewritten rows, §11.4's new *To Phase
13* hand-off and *To Phase 5* trap paragraph, and the corrected grounds in §3, §4.7.4 and §4.12. Three
judgement calls this session made that an adversarial reader should test rather than accept: taking
V13-1 onto its §5 branch when the review scored it *"touches §5: no"*; choosing V13-2's **widening**
branch over dropping Phase 13, and deciding within it that only *live* vanilla-owned textures need a
foreign handle; and adding **Phase 8** to §5.2's profile consumers, which is this session's claim and
not the review's.

*This fix-up session wrote no code, ran no build, no test and no gradle task, launched no agent, made
no network request, and modified exactly two files: `docs/phase1/v11/PHASE_1_DOC.md` and this one.
`docs/design/v1.1/DESIGN.md`, `docs/design/v2.0-RC1/DESIGN.md`, `docs/design/v2.0-RC2/DESIGN.md`,
`docs/research/v1/RESEARCH.md`, `docs/reference/pintonium/v1.0/PINTONIUM_DESIGN.md`, `docs/MOVES.md`,
the `/verify-loop` harness and its operator documentation, and `PHASE_1_REVIEW_1.md` through
`PHASE_1_REVIEW_12.md` — including their `## Resolutions` sections, which are evidence — are
unmodified, as is everything above this file's `## Resolutions` heading.*
