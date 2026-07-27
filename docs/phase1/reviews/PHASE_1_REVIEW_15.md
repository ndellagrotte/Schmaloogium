# PHASE_1_DOC.md — Verify session, round fifteen

**Document under review:** `docs/phase1/v13/PHASE_1_DOC.md` (through its §0.14 addendum).
**Design revision anchored:** `docs/design/v2.0-RC2/DESIGN.md`. Every `DESIGN.md` line number below
is against that file; every RESEARCH.md line number against `docs/research/v1/RESEARCH.md`.

**Verdict: PASS** — zero blocking, zero corrections, two notes. The round-fourteen material — the
closure-stated bind-only rule and the re-derived App F.7 rows, the fresh unreviewed surface this
round was priced against — verified sound at source everywhere it was probed. Both admitted notes
are recorded and left unapplied, per the standard §G1.3 sets: notes do not block, and neither
misdirects any dependent that reads the surfaces §G1.1 makes binding.

---

## 0. What I read, and in what order

Read first, before any prior review, per §G1.2's independence rule:

1. **`docs/phase1/v13/PHASE_1_DOC.md`** — targeted, not linearly: §3's contract map (ll. 1420–1447,
   every row, with the App F.7 trio ll. 1444–1446 and the vocabulary row l. 1447 read closely);
   §4.7.3's provider declaration and bind-only block (ll. 2555–2634); §4.7.4's service declarations
   (ll. 2694–2800, swept for every `TextureHandle`-accepting verb); §5's preamble and §5.1/§5.2 in
   full (ll. 3840–3884), string-swept for `empty`/`Optional`/`handleFor`; §5.4 (l. 3923); §6's
   table entire (ll. 3927–3947); §11.4's *To Phase 5* and *To Phase 13* blocks (ll. 4655–4768);
   §12 items 20–24 incl. 22b (ll. 4885–4891); §0.1's inputs table and §0.3's deviations
   (ll. 30–82); the §0.7 addendum's V7-1 bullet (ll. 258–270); the §0.13 bullet list (ll. 1170–1180);
   §0.14 entire (ll. 1201–1235); the closing block (ll. 4939–4996).
2. **`docs/design/v2.0-RC2/DESIGN.md`** — §G4.1 (l. 501); Phase 3's overrides bullet (l. 1245);
   Phase 4's registry and use-program-barrier bullets (ll. 1332–1338, 1363–1367, incl. the
   `BufferBlendOverride` line 1337); the coverage rows (ll. 2408–2412); §G1.2/§G1.3 (ll. 257–320)
   for the verdict semantics this file closes on.
3. **`docs/research/v1/RESEARCH.md`** — App D.4's `instanceId`/`blendFunc` rows (ll. 1373–1380);
   App F.5 (ll. 1478–1486, the `minecraft:` source forms at l. 1482); App F.7 (ll. 1508–1514, the
   per-program `blend.<prog>` form at l. 1511); §3.6.7's `PER_BUFFER_BLENDING` (l. 429).
4. **`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`** — ll. 178–190 only
   (`uniform int instanceId;` at l. 182, the "GBuffers Uniforms" heading at l. 184), to re-derive
   the candidate declined as a V12-12 restatement in §2. A shipped pack-author document, §G7 rule 3
   clean, and not a transcript.

Read **last**, after every candidate had been re-derived against the sources above:

5. **`PHASE_1_REVIEW_14.md`** in full, including `## Resolutions` — V14-1/V14-2's applications and
   sweeps, the five deferred notes with their reasons (V14-4's deliberate verbatim preservation of
   the l. 2620 pointer in particular), and its §G1.3 status.
6. **`PHASE_1_REVIEW_13.md`**, targeted — V13-7 (ll. 466–492) and V13-8 (ll. 496–523) with their
   Resolutions entries (ll. 948–971). Greps across all fourteen prior reviews for `pack
   vocabulary`/`synonym`/`G4.1`, `F.5`, `texel upload`, `1433`, `last row`, `shaders.txt` to
   disposition each candidate against the settled list.

Reads beyond the assigned list, each with the item it turned on: shaders.txt (item 4 above — the
V12-12 disposition); `DESIGN.md` ll. 257–320 (the §G1.3 closing conditions this verdict invokes).
**Deviations:** none. **Network use:** none. **Forbidden sources:** none opened — no
`docs/**/chatlogs/` path, no root-level `*.txt`, no prior session transcript in any form.

### 0.1 Sub-agent disclosure

This round ran as an automated fan-out: on the order of 26 read-only agents (finders, two
independent refuters per candidate, and a mechanised gate that re-resolves every citation at the
line and drops findings whose citations do not match verbatim) produced **five** surviving
candidates, delivered with post-refutation severities (all five arrived as notes). Candidates the
gate dropped were not itemized in the hand-off and cannot be listed here. Disposition of the five
survivors is this session's own: **two admitted** (V15-1; V15-2, narrowed to the increment beyond
standing note V13-7), **three declined** as restatements of standing deferred notes — one of
V14-4, one of V13-8, one of V12-12 — with each declination argued in §2. Every admitted finding
was re-derived by this session directly against the cited text before admission; severities, the
narrowing, the declinations and the verdict are this session's, not the fan-out's.

---

## 1. Findings

### V15-1 — §3 maps App F.5's custom-texture forms solely to `TextureService.upload`, while the document's own design routes the live `minecraft:` subset through `ForeignTextureProvider` — the map lags V13-2 · **note** · **touches §5: no**

**Location.** §3's contract map, **ll. 1433–1434**. Against §4.7.3 **ll. 2570–2574**, §5.1's
provider row **l. 3861**, and §11.4's *To Phase 13* block **ll. 4754–4761**.

**Claim under test.** Row 1433 cites *"App F.5 (`texture.noise`, custom-texture source forms)"* in
its provenance cell and names `TextureService.upload(TextureHandle, TextureData)` as the design
element — *"the verb only; generation, formats and unit assignment are Phase 13's"* — without
qualification. Row 1434 maps `ForeignTextureProvider` to App **B.3**'s unit map only. I tested
whether the cited App F.5 text is fully served by the mapped element.

**Evidence.** App F.5's source forms (RESEARCH.md l. 1482) include *"`minecraft:`-prefixed asset
(incl. `dynamic/lightmap_1`, atlas paths)"* — textures Minecraft owns and keeps live, not
pack-supplied texel data. The document's own design splits exactly there: §4.7.3 assigns
vocabulary (b) — *"`minecraft:`-namespaced resource locations — App F.5's custom-texture forms
that name a texture Minecraft owns and keeps LIVE"* (ll. 2570–2572) — to the provider, with only
the *static* subset going through `create`/`upload` (ll. 2572–2574). §5.1's provider row states
the same split (*"A `minecraft:` form that is a static asset needs no foreign handle and goes
through `TextureService.create`/`upload`"*, l. 3861), and `upload` is one of the eight verbs the
same row makes **illegal** on a foreign handle — so the element row 1433 maps is unusable for the
live subset. §11.4 says it outright: *"The transfer verbs do not cover all of App F.5, and the
route for the rest is `ForeignTextureProvider`"* (l. 4755). §0.13's V13-2 bullet (ll. 1172–1176)
records the sites that correction updated — §4.7.3, §5.1, §11.4's *To Phase 13* — and §3 is not
among them, which is how the map came to lag the design. No prior round examined this row's App F.5
scope (grep over all fourteen reviews: no hit on the row or its contract item).

**Why a note and not a correction.** The calibration is V14-3's, one round back and accepted
there: no phase is misdirected into wrong work. The surfaces that bind a dependent — §5.1's
provider row, §11.4's *To Phase 13*, §12 item 22b — all state the two-vocabulary split and the
static/live boundary correctly and at length; what is wrong is one conformance-map cell's
unqualified mapping and the absence of the symmetric App F.5 → vocabulary (b) association in
row 1434. §3 is internal traceability, not the contract dependents build against (§G1.1: *"What a
dependency's PHASE doc exposes in its §5 is what you build against"*). Contrast V14-2, which was a
correction because §3 there mis-routed work *across phase boundaries* against `DESIGN.md`'s
coverage row; nothing here hands any phase work that belongs elsewhere.

**Fix if applied.** Qualify row 1433's element cell (`upload` serves `texture.noise`, pack PNGs,
the raw form, and *static* `minecraft:` assets) and extend row 1434 — or add one row — mapping App
F.5's **live** `minecraft:` forms → `ForeignTextureProvider.handleFor`, vocabulary (b), owner
Phase 13, per §4.7.3/§5.1. Cheapest taken together with standing note V13-8, whose recorded
rewrite touches the adjacent vocabulary row.

---

### V15-2 — §5 never states the provider's absent-value contract, against its own sufficiency sentence — the increment beyond standing note V13-7 · **note** · **touches §5: no**

**Location.** §5's preamble, **l. 3847**; §5.1's provider row, **l. 3861**; §5.2's opaque-handle
row, **l. 3871**. Against §4.7.3 **ll. 2575–2576, 2589, 2597–2599** and §12 item 22b, **l. 4889**.

**Scope.** The fan-out's candidate bundled two halves. The half that says §4.7.3's *"the caller
degrades per §6"* points at a §6 with no row for the empty-provider case is **standing note V13-7,
verbatim** — found by round 13, deferred there with its content question named (which rung, and
whether the response is a pack-off or an unbound unit), re-affirmed by round 14 as jointly
addressable with V14-4 — and it is **not re-admitted**. What is admitted is only the increment
V13-7 does not record.

**Evidence for the increment.** §5's preamble promises it is *"sufficient on its own — every
obligation this document places on another phase appears here, not only in §4 or §11"* (l. 3847).
§4.7.3 places exactly such an obligation on the provider's consumers (Phases 5, 6, 13): the lookup
is *"Empty when the platform has no such texture yet, or none under that key — the caller degrades
per §6 rather than assuming presence"* (ll. 2575–2576), with the before-install behaviour stated
at l. 2589 (*"before install: empty for every key"*) and ll. 2597–2599. A string sweep of §5
entire (ll. 3840–3923) finds no occurrence of `empty`, `Optional`, `handleFor` or any absent-value
statement in §5.1's provider row or §5.2's opaque-handle row: the empty case and the
degrade-not-assume obligation live in §4.7.3 (and, for the before-install fact, in §12 item 22b's
test column, l. 4889) — precisely the "only in §4" state the preamble forswears.

**Why a note and not a correction.** Three mitigations, each checked: the declared signature
itself — `Optional<TextureHandle> handleFor(String key)`, l. 2582 — carries absence into the type,
so no consumer can compile past the empty case unhandled (unlike the bind-only rules, which V13-3
rightly forced into §5 *because* the type cannot carry them); §5.1's row declares the type by name
with its §4.7.3 pointer, so the skimming reader the preamble guards against still lands one hop
from the full contract; and §12 item 22b independently states the before-install-empty behaviour
as a v0.1 conformance obligation. By V13-7's own reasoning — *"no dependent is left without an
answer"* — this is a gap in a self-imposed redundancy promise, not in the contract.

**Fix if applied.** One clause in §5.1's provider row: `handleFor` returns `Optional` — empty
before install and for unknown keys — and the consumer degrades rather than assumes presence
(§4.7.3). That edit touches §5, so it should ride whichever future change re-verifies anyway —
none is owed after this round — and its natural companion is V13-7's §6 row, whose rung question
remains the substantive part.

---

## 2. What was checked and came back clean

Named because a round reporting only findings misrepresents its coverage. Items 1–3 are the
declined candidates, each re-derived before declination; items 4–6 are this session's own
derivations on the round-fourteen material; item 7 relays the fan-out's coverage as the fan-out's.

**1. Declined: the candidate that the rejection sentence's "§6's last row" pointer resolves to the
build-time seam row.** Re-derived and true at the letter: §4.7.3 l. 2620 delegates the
`IllegalArgumentException` to *"§6's last row"*, §6's literal last row (l. 3947) is the build-time
seam row ending *"Not a runtime failure mode — by design"*, and the receiving row is the rung-5
`:engine`-throws row above it (l. 3946). But this is **standing note V14-4, verbatim** — admitted
by round 14 with the same derivation, including the answer to the scope objection (the glue-boundary
wrapper receives a backend exception propagating through the engine entry points) — and the
round-fourteen fix-up records that V14-1's rewrite **preserved the pointer verbatim at l. 2620
deliberately**, *"so this deferral is real rather than a silent half-fix"*. The candidate's one new
wrinkle — that the rewrite "re-endorses" the pointer — is answered by that recorded deferral: the
carry-through is the deferral mechanism working as documented, not a fresh defect. Not re-admitted;
V14-4 stands, jointly addressable with V13-7 as round 14 already directs.

**2. Declined: the candidate that §3's §G4.1 vocabulary row (l. 1447) asserts the facade contains
no pack vocabulary while `ForeignTextureProvider`'s key space is pack vocabulary used verbatim.**
Re-derived and true: l. 1447's cell says *"no pack vocabulary at all"* and lists the carrying
phases as 3, 5, 6; §4.7.3 ll. 2566–2570 put App B.3 sampler names and `minecraft:` resource
locations — both bound to §G4.1 (`DESIGN.md` l. 501) — into an `engine.gl` interface's contract,
§5.1 l. 3861 exposes the same, and §11.4 l. 4761 hands vocabulary (b) to Phase 13 under §G4.1. But
this is **standing note V13-8, verbatim and already enlarged**: round 13 admitted it, and its
Resolutions entry (review 13, ll. 960–971) records the exact rewrite the candidate proposes — the
facade carries exactly one pack-vocabulary surface, `handleFor`'s key space, both vocabularies
verbatim and no synonym minted, *"and **13** joins **3, 5, 6** in the row's parenthetical list"* —
held for a round that applies notes. Nothing in the candidate adds force beyond that recorded
state. Not re-admitted.

**3. Declined: the candidate that §3's gbuffers/shadow `countInstances` row (l. 1443) attributes a
`doc/shaders.txt` fact to RESEARCH.md, with the file absent from §0.1's inputs table.** Re-derived
end to end: RESEARCH.md's only `instanceId` home is App D.4's table (l. 1377 — the candidate's own
corrected coordinate; l. 1380 is cadence prose), RESEARCH.md has no "common" uniform block and no
GBuffers heading; the real structure is shaders.txt's (`uniform int instanceId;` at l. 182, two
lines above the "GBuffers Uniforms" heading at l. 184), so row 1443's middle clause is true but
mis-chained under the RESEARCH.md citation, while §11.4 l. 4669 names the true source; §0.1's
table still lacks the file, though §0.7's V7-1 bullet (ll. 260–262) discloses the read by name.
All of which is **standing note V12-12, verbatim** — found by round 12, declined again as a
restatement by round 14's §2 item 2, which also recorded the `[D-P1-33]`/`[D-P1-35]` extension for
whenever it is applied. Still unapplied, exactly as recorded. Not re-admitted.

**4. The round-fourteen corrections, verified at source by this session.** V14-1's closure is
arithmetically and textually exact: §4.7.4 declares precisely ten verbs that admit a
`TextureHandle` — `FramebufferService.attachColor`/`attachDepth`/`copyDepthToTexture` (ll. 2701,
2702, 2736), `TextureService.allocate`/`setParameters`/`upload`/`bindToUnit`/`generateMipmap`/
`delete` (ll. 2749–2761), and `DebugService.label(GLHandle, String)` (l. 2797) by subtyping — so
two legal, eight illegal is a complete partition, and all five closure sites (§4.7.3 ll.
2608–2621, §5.1 l. 3861, §5.2 l. 3871, §11.4 ll. 4696–4700, §12 item 22b l. 4889) state the same
rule with no stale five-verb survivor outside the two deliberate historical sites the fix-up's
sweep names. V14-2's re-routing holds at the governing document, opened by this session rather
than taken from the fan-out: `DESIGN.md` l. 2410 reads *"3 (parse), 4 (apply), 7 (execute)"*,
l. 1245 stores at 3 and applies at 4, l. 1365 puts the per-program alpha/blend lock among the
use-program barrier's obligations, and l. 1337's per-buffer `BufferBlendOverride` sits inside
**Phase 4's** registry bullet — so the deleted per-buffer clause was rightly deleted, not
re-homed (App F.7's form, RESEARCH.md l. 1511, has no per-buffer axis; the only per-buffer
material is §3.6.7's flag, l. 429). §3's rewritten rows 1444–1445 perform the derivation the
`scale.<prog>` row (l. 1446) templates, and §5.2's `StateService` row names Phase 4 with the same
citations. The fix-up's two flagged own-derivations both hold: a GL framebuffer attachment
captures the texture object in attachment state, beyond the reach of per-use resolution — the
independent ground for making `attachColor`/`attachDepth` illegal is sound; and the l. 1337
adjacency is where the fix-up says it is.

**5. The empty-provider complex, walked while deriving V15-2.** §4.7.3's lazy-per-call
resolution, the before-install empty answer, the `LogSink`-shaped `ForeignTextures` holder and
§4.13 stage-2 install point are stated consistently at §4.7.3, §5.1's provider row (install
point), §12 item 22b and §11.4; the two §6 delegations (l. 2576's and l. 2599's) both dangle
exactly as V13-7 records — no new inconsistency has grown around the standing note.

**6. Round-fourteen bookkeeping.** §0.14 (ll. 1201–1235) reports what the review and Resolutions
actually did — two corrections applied, five notes deferred, three §5 rows altered, no
declaration moved — and the closing block's counts (fourteen verify sessions, eleven fix-ups) and
its final paragraph's description of the §5 alteration are consistent with the addendum series,
which is the specific check round 14 asked of this session. §12 item 22b's reviewer obligation is
the closure, not the list, with later-added verbs in scope by construction.

**7. The fan-out's clean areas, relayed as its coverage** (spot-checked where a finding above
touches the same ground, not independently re-derived in full). The bring-up sequence is
consistent at every site (§4.13, §5.1's row, §7's `CapabilityProbe` row, §9's tag, §11.4's Phase 7
block, §12 item 23 — stage 2 a requirement on Phase 7's App E catalog, stage 3 an unwired
recommendation, stage 1 deviated onto FML). §5.2's non-verbs row is complete against §4.7.4's
table — ten absent verbs, matching requesters, the framebuffer-bind row's Phase 7 v0.1 obligation
included. §5.4 is honestly vacuous (no dependencies, §G5.1). The GL-error row agrees with §4.7.4,
§6's rung-2 and 3→4 rows, §7 and §11.4 on the protocol's three properties, the elision's facade
scope, and the two-remedies framing. The provider's two key vocabularies are disjoint as claimed
(no App B.3 sampler name contains a colon; the two vanilla-owned rows are units 0/1 on
GBUFFERS/SHADOW). The unmapped-row sweep found no in-scope contract item §3 omits other than the
App F.5 live-asset case admitted as V15-1; the §4.1 probe rows, the GL-3.0 mipmap gate, the §3.5
macro-header row, App F.8, §G4.5's KHR_debug affordance, §6.1's three hard constraints and
§G4.6's facade-only rule all carry accurate rows; the ivec2/ivec4 rows are faithful to App
D.1/D.3/D.4, and App D's full type inventory is serviceable through §4.7.4's verb set. No fan-out
agent opened a forbidden source.

---

## 3. Verdict

# PASS

Zero blocking, zero corrections, two notes. Not PASS-WITH-CORRECTIONS: both admitted findings sit
squarely in the severity class this review series has settled twice over — a conformance-map cell
lagging a design whose binding surfaces (§5, §11.4, §12) are correct and consistent (V15-1, the
V14-3/V13-8 calibration), and a redundancy promise unmet where the type system and two other
sections already carry the contract (V15-2, kept deliberately narrower than standing note V13-7,
which owns the substantive half) — and the two tests that made rounds thirteen and fourteen
corrections both come up empty this round: no misuse is formally legal under the written contract
(the closure partitions all ten `TextureHandle` verbs), and no work is routed across a phase
boundary against `DESIGN.md`'s coverage rows. Manufacturing a correction out of either note would
be the failure mode this round was warned against by name. Not FAIL, trivially: the round-fourteen
material — the only fresh surface — survived source-level re-derivation intact.

### Per-finding §5 disposition

| Finding | Severity | Touches §5 |
|---|---|---|
| V15-1 §3's App F.5 rows lag V13-2's two-vocabulary split | note | **no** as recorded; the fix, if ever applied, is §3-only |
| V15-2 §5 omits the provider's absent-value contract | note | **no** as recorded (unapplied); the fix, if ever applied, adds one clause to §5.1's provider row |

### §G1.3 line

**The verdict is PASS, so the loop's exit condition is met and no fix-up is owed.** Per §G1.3
(`DESIGN.md` ll. 302–320): a phase is verified when its latest review verdict is PASS — this
review is that verdict, it orders no correction, and it leaves no §5 change outstanding.
`PHASE_1_DOC.md` (`docs/phase1/v13/PHASE_1_DOC.md`, through §0.14) is therefore **verified** and
is a **valid dependency input** under §G5.3: Phase 2, Phase 3 and everything downstream are
unblocked. The standing deferred notes — V12-12, V13-5 through V13-8, V14-3 through V14-7, and
now V15-1/V15-2 — remain recorded with their reasons across this review series' `## Resolutions`
sections and §1 above; none blocks, and none obliges a session. If a future maintainer pass
applies any of them and the edit alters §5 (V15-2's would; V14-3's and V14-5's naming branches
would), §G1.3's re-verify trigger applies at that time, not now.

*This verify session wrote no code, ran no build, test or gradle task, made no network request,
and created exactly one file: this one. `PHASE_1_DOC.md`, all `DESIGN.md` revisions,
`RESEARCH.md`, `PINTONIUM_DESIGN.md`, and `PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_14.md` —
including their `## Resolutions` sections, which are evidence — are unmodified.*
