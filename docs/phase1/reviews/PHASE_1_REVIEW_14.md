# PHASE_1_DOC.md — Verify session, round fourteen

**Document under review:** `docs/phase1/v13/PHASE_1_DOC.md` (through its §0.13 addendum).
**Design revision anchored:** `docs/design/v2.0-RC2/DESIGN.md`. Every `DESIGN.md` line number below
is against that file; every RESEARCH.md line number against `docs/research/v1/RESEARCH.md`.

**Verdict: PASS-WITH-CORRECTIONS** — two corrections, five notes, zero blocking. **Both corrections
touch §5.** One correction (V14-1) is in the material the round-thirteen fix-up added — round seven's
price, again; the other (V14-2) is a round-six survivor of exactly the class V13-4 fixed one row over.

---

## 0. What I read, and in what order

Read first, before any prior review, per §G1.2's independence rule:

1. **`docs/phase1/v13/PHASE_1_DOC.md`** — targeted, not linearly: §4.7.3's provider and bind-only
   block (ll. 2540–2586); §4.7.4's service declarations end to end (ll. 2588–2768) and its design
   rules and absent-verbs table (ll. 2771–2810, 3000–3022); §3's contract map (ll. 1375–1408); §0.13
   and the §0.12 tail (ll. 1140–1195); §2.1's package tables (ll. 1270–1302); §2.4's type table
   (l. 1361); §4.9.2 (ll. 3320–3337); §4.12's bucket tables and gap section (ll. 3580–3679); §5's
   preamble and §5.1/§5.2 in full (ll. 3790–3839); §6's table (ll. 3875–3896); §9's rows
   (ll. 4085–4102); §11.1's decision log ll. 4360–4377; §11.4's hand-off blocks (ll. 4560–4700);
   §12 items 16–29 (ll. 4820–4847); the closing §G1.3 paragraph (ll. 4900–4929); §0.1's inputs
   table (ll. 30–66) and the §0.6/§0.7 addenda (ll. 196–330).
2. **`docs/design/v2.0-RC2/DESIGN.md`** — the coverage row at l. 2410 and dropped-item audit
   (ll. 2405–2422); Phase 3's overrides bullet (l. 1245); Phase 4's registry and use-program-barrier
   bullets (ll. 1330–1340, 1360–1370); Phase 5's spec (ll. 1409–1525, swept for "blend" and
   "alphaTest"); Phase 13's companion-atlas bullet (ll. 2248–2260).
3. **`docs/research/v1/RESEARCH.md`** — §3.2 (ll. 246–256), §3.3–§3.5 (ll. 258–326), App A.3's
   directive table (ll. 1155–1170), App F.7 (ll. 1508–1516), App D's headings (ll. 1323–1377);
   whole-file greps for `gl_InstanceID`, `common` (case-insensitive) and `GBuffers`.
4. **`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`** — ll. 120–126 and 178–190 (the
   "Uniforms" block heading at l. 123, `uniform int instanceId;` at l. 182, "GBuffers Uniforms" at
   l. 184). Read to test V14-6's neighbourhood and the candidate dropped as V12-12; §G7 rule 3
   makes it a legally clean contract source.

Read **last**, after every candidate had been re-derived against the sources above:

5. **`PHASE_1_REVIEW_13.md`** in full, including `## Resolutions` — the verdict header (l. 10),
   V13-3 (ll. 281–343) and V13-4 (ll. 345–399), V13-5 through V13-8 (ll. 402–523), the V13-3 and
   V13-4 resolutions (ll. 851–923), the notes-deferred block (ll. 926–971) and the §G1.3 status
   (ll. 975–999).
6. Earlier rounds, greps plus targeted reads where a candidate might restate settled material:
   `PHASE_1_REVIEW_12.md` V12-12 (ll. 642–664), its §2 item 1 head and its notes-deferred entry
   (l. 1124); `PHASE_1_REVIEW_7.md` §0.1 (ll. 30–53) and its App F.7 / instancing checks
   (ll. 112–130); `PHASE_1_REVIEW_6.md` ll. 163–165, 186, 300, 461; `PHASE_1_REVIEW_5.md`
   ll. 278–282, 507; `PHASE_1_REVIEW_3.md` l. 191; grep sweeps of all thirteen for `alphaTest`,
   `per-buffer`, `attachColor`/`attachDepth`, `gl_InstanceID`, `common`, `engine.textures`,
   "Phase 5/6 policy".

**Deviations:** none. **Network use:** none. **Forbidden sources:** none opened — no
`docs/**/chatlogs/` file, no root-level `*.txt`. The only `.txt` opened is
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, the shipped pack-author documentation,
which is a listed contract source and not a transcript.

### 0.1 Sub-agent disclosure

This round ran as an automated fan-out: on the order of 26 read-only agents (finders, two
independent refuters per candidate, and a mechanised gate that re-resolves every citation at the
line and drops findings whose citations do not match verbatim) produced **ten** surviving candidate
findings, which this session received with their post-refutation severities. The candidates the
gate dropped were not itemized in the hand-off and are therefore not listed here; what is listed is
the ten survivors' disposition, which is this session's own: **eight admitted as seven findings**
(two candidates describe the same defect and are merged into V14-4), **two declined** as
restatements of standing deferred notes — one of V12-12, one of V13-6 — with the declination
argued in §2. Every admitted finding was re-derived by this session directly against the cited
sources before admission; severities, the merge, both declinations and the verdict are this
session's, not the fan-out's.

---

## 1. Findings

### V14-1 — the bind-only rule's verb enumeration misses the three `FramebufferService` verbs that also take a `TextureHandle`, and the destructive one is among them · **correction** · **touches §5: yes**

**Location.** §4.7.3's bind-only block, **ll. 2560–2571** (the legal/illegal enumeration at
ll. 2567–2569); against `FramebufferService` at **ll. 2651, 2652 and 2686**. The same enumeration
is repeated at §5.1's provider row (**l. 3809**), §5.2's opaque-handle row (**l. 3819**), §11.4's
*To Phase 5* block (**ll. 4644–4645**) and §12 item 22b (**l. 4834**).

**Claim under test.** V13-3's resolution states the foreign-handle contract in full — *"Three
rules, and none of them is 'obvious enough' to leave unwritten"* (l. 2563–2564) — with a legal set
of exactly two verbs (`TextureService.bindToUnit`, `DebugService.label`, l. 2567) and an illegal
set of exactly five (`allocate`, `setParameters`, `upload`, `generateMipmap`, `delete`, l. 2568),
which a backend *"rejects … on those five"* (ll. 2568–2569). The claim under test is that those
seven verbs are all the §4.7.4 verbs accepting a `TextureHandle`, so the enumeration is closed and
a backend implementing it is compliant.

**They are not.** Three `FramebufferService` verbs also take one, and none is classified:

- l. 2651 — `void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t);`
- l. 2652 — `void attachDepth(FramebufferHandle f, TextureHandle t);`
- l. 2686 — `void copyDepthToTexture(FramebufferHandle src, TextureHandle dst, TextureRegion region);`

A sweep of §4.7.4's declarations confirms these three plus the enumerated seven are the complete
`TextureHandle`-accepting set. `attachColor`/`attachDepth` with a foreign handle would make a
vanilla-owned texture a render target of an engine FBO; `copyDepthToTexture` is the destructive
case — `dst` is *written into*, so a foreign handle there overwrites Minecraft's own texture
object, which is the same §6 rung-5 outcome the block's own second bullet names for a stray
`delete` (ll. 2572–2579), reached through a verb the contract never forbids.

**Why this is a correction and not a note.** The document sets its own standard two lines above
the gap: nothing here is mechanically checkable (`SeamBytecodeTest` sees no GL name crossing,
l. 2564–2565), so the *written* contract is the only contract — and §12 item 22b (l. 4834)
operationalises the five-verb list as the reviewer's entire check, so a backend that rejects
exactly five and silently performs `copyDepthToTexture` onto a foreign `dst` **passes item 22b
while breaking rung 5**. This is fresh round-thirteen material; the round-13 resolution (its
ll. 851–891) framed the question as *which `TextureService` verbs* may take the handle, and the
`FramebufferService` verbs were never in the frame — no recorded reason excludes them.

**Fix.** State the rule by closure where the type is declared — a foreign handle is a legal
argument to `bindToUnit` and `DebugService.label` **and to nothing else in §4.7.4 that accepts a
`TextureHandle`**: `allocate`/`setParameters`/`upload`/`generateMipmap`/`delete`, and
`FramebufferService.attachColor`/`attachDepth`/`copyDepthToTexture(dst)`, the last named as the
destructive case — and carry the widened set into §5.1's provider row, §5.2's opaque-handle row,
§11.4's *To Phase 5* block and §12 item 22b's reviewer list. §2.4's one-clause form (l. 1361,
*"bind-only and outside the handle-lifetime rule"*) is already closure-shaped and needs no edit.

**Touches §5: yes** — §4.7.3 itself rules that *"§5.2 has to repeat it or §5 is not 'sufficient on
its own'"* (l. 2563), so the corrected enumeration must land in §5.2's opaque-handle row and §5.1's
provider row, exactly as V13-3's partial version did.

---

### V14-2 — §3's `alphaTest.<prog>` and `blend.<prog>` rows assign per-program render-state policy to Phases 5/6, where `DESIGN.md` routes it 3 (parse), 4 (apply), 7 (execute) — the class of defect V13-4 fixed, unswept on the adjacent rows · **correction** · **touches §5: yes**

**Location.** §3's contract map, **ll. 1403–1404**, adjacent to the correctly-derived
`scale.<prog>` row at l. 1405; knock-on at §5.2's `StateService` row, **l. 3823** (consumer
column). Against `DESIGN.md` **ll. 1245, 1334, 1365, 2410**.

**Claim under test.** l. 1403: *"the verb only; which program carries which value is **Phase 5/6
policy**"*. l. 1404: *"the per-buffer routing decision is **Phase 5's**"*. Both rows cite only
App F.7, which describes key syntax and names no phase.

**What `DESIGN.md` says.** The coverage table routes this exact item — l. 2410, *"per-program
alphaTest/blend/scale/flip overrides | **3 (parse), 4 (apply), 7 (execute)**"*. Phase 3's spec
confirms the first two independently: l. 1245, *"per-program render-state overrides
(alphaTest/blend/scale/flip/enabled — **stored; applied by Phase 4**)"*. Phase 4's registry carries
*"alpha/blend overrides"* as per-slot state (l. 1334), and Phase 4's use-program barrier makes the
*"per-program alpha/blend lock"* one of its stated obligations (l. 1365). On the other side:
**"blend" occurs zero times in Phase 5's whole spec** (ll. 1409–1525 — swept; the three hits for
`alpha` in that range are all the fog-alpha-1.0 clear-colour quirk, not per-program state), and
"alphaTest" occurs nowhere in the entire design document outside ll. 1245 and 2410. Phase 6 owns
the `blendFunc` *uniform* (a read of current blend state), not blend-state policy. So under no
reading does "which program carries which value" belong to Phase 5 or 6: the values are
pack-authored, parsed and stored by 3, carried and applied by 4, executed by 7.

**The "per-buffer routing" clause is additionally unanchored.** App F.7's form is
`blend.<prog>=off|<src> <dst> [<srcA> <dstA>]` (RESEARCH.md l. 1511) — per-program, with no
per-buffer axis. Per-buffer blending in this project's sources is RESEARCH.md §3.6.7's Iris-side
`PER_BUFFER_BLENDING` feature flag (l. 429), modern-superset material assigned to no phase at v0.1.
The clause attributes to Phase 5 a decision the cited directive cannot express.

**Why this is a correction.** It is the same defect class as V13-4, one row over, with the same
directional consequence: a Phase 5 or Phase 6 build session reading §3 as its contract inherits
work `DESIGN.md` gave Phases 3/4/7, and **Phase 4 — the phase that stores the overrides and locks
the state at the barrier — is named in neither row nor in §5.2's `StateService` consumer column**
(l. 3823: "5, 6, **7**"). The document's own `scale.<prog>` row (l. 1405) — the third directive in
the *same* App F.7 sentence — performs the correct derivation (parse 3, carry 4, dims 5, apply 7,
with `DESIGN.md`'s one silence reported rather than filled) and is the template these two rows
should have been rewritten to when V13-4 corrected the mipmap row against the same coverage table.
Prior-round check: round 6 created the rows (V6-4) and round 7 verified only the *verb mapping*
("map correctly to `StateService.alphaTest` / `.blend`", its ll. 122–126); no round has examined
the ownership clauses — "per-buffer routing" appears in no prior review.

**Fix.** Re-derive both cells the way l. 1405 derives `scale.<prog>`: parse/store Phase 3
(l. 1245), per-slot carry and the use-program alpha/blend lock Phase 4 (ll. 1334, 1365), execution
Phase 7 (l. 2410); delete the "per-buffer routing" clause or re-anchor it to §3.6.7's
`PER_BUFFER_BLENDING` as post-v0.5 and unassigned. Add **4** to §5.2's `StateService` consumer
column with its citation. Two adjacent sentences to keep consistent while editing: §4.7.4's
*"alpha/blend also being per-program state from App F.7"* (l. 2782, phase-neutral, fine as is) and
its *"**Which** state is perturbed at which moment is Phase 5/6/7 policy"* (l. 2792, defensible in
its composite-block scope but worth a glance once the rows above it stop saying 5/6).

**Touches §5: yes** — the coherent fix adds Phase 4 to §5.2's `StateService` row, on the same
"replace one inconsistency with another" ground the V13-4 resolution used for adding 7 and 8 to
the profile row.

---

### V14-3 — the mipmap gate's rewritten consumer set says "the phases that generate: 7 and 8", while §11.4 hands Phase 13 `generateMipmap` by name and `DESIGN.md` gives Phase 13 mip chains · **note** · **touches §5: no (deferred; the applied fix would)**

**Location.** §5.2's `GLCapabilityProfile` row, **l. 3817**; §3's mipmap row, **l. 1385**. Against
this document's own §11.4 *To Phase 13* block, **l. 4691**, `TextureService.generateMipmap`'s
javadoc, **l. 2710**, and `DESIGN.md` **l. 2254**.

**Evidence.** V13-4's rewrite states an exhaustive consumer set — l. 3817: *"its consumers are the
phases that generate: **7** for per-pass composite mipmaps … and **8** on the shadow side"* — and
the widened column reads "2, 3, 4, 5, 6, 7, 8, 14", without 13. But l. 4691 hands Phase 13
*"`TextureService.create`/`allocate`/`setParameters`/`upload`/`bindToUnit`/**`generateMipmap`**"*
as *"the transfer verbs you need"*, l. 2710's javadoc obliges every `generateMipmap` **caller** to
check the gate — the exact rule the V13-4 resolution used to add Phase 8 — and `DESIGN.md` l. 2254
puts *"full companion atlases with matching **mip chains**"* in Phase 13's *Scope — in*. The
round-13 fix-up's term sweep was `grep mipmap` over RC2's Part II (its resolution, ll. 896–899);
l. 2254's string is "mip chains", which that sweep could not see.

**Why a note.** No phase is misdirected into wrong work: Phase 13 gets the verb and the
caller-checks rule from §11.4 and l. 2710 regardless; what is wrong is an exhaustivity sentence
and a column omission. It is the same shape as V13-4's own widening argument, one phase further.

**Fix if applied.** Either add **13** to l. 3817's column and name it in the prose (citing
`DESIGN.md` l. 2254), or state that Phase 13 builds its mip chains by per-level `upload`
(`TextureData` carries a mip level, ll. 2702–2707) and therefore never calls `generateMipmap` — in
which case l. 4691 must stop listing `generateMipmap` among the verbs Phase 13 needs. Either
branch, stated once, closes it; the first touches §5, which is why it should ride the V14-1/V14-2
fix-up rather than wait.

---

### V14-4 — the foreign-handle rejection is delegated to "§6's last row", which is the build-time seam row that disclaims runtime by design · **note** · **touches §5: no**

**Location.** §4.7.3, **l. 2571**; against §6's table, **ll. 3894–3895**.

**Evidence.** l. 2571 says the backend's `IllegalArgumentException` is *"caught by §6's last row
rather than reaching vanilla state"*. §6's actual last row (l. 3895) is *"A seam violation reaches
a build"* — build-time, and its own cell ends *"Not a runtime failure mode — by design"*. The row
that receives this failure is the one above (l. 3894): *"`:engine` throws an unexpected
`RuntimeException`"*, rung 5, `mod.core`'s wrapper — and the round-13 resolution confirms that row
is the intended target and even names the consequence the doc omits: *"engine disabled, vanilla
path resumes … a **stronger** outcome than the corrupted-atlas one it replaces"*
(`PHASE_1_REVIEW_13.md` ll. 872–874). The resolution's own text calls it "§6's last row" too, so
the doc inherited a positional pointer that was wrong when written. The scope objection — the row's
label says `:engine` throws while this exception originates in the `mod.glue` backend — is answered
by the row's mechanism (the wrapper sits at the engine entry points, and a facade call's exception
propagates through them), but a Phase 5 reader gets none of that from a pointer that resolves to a
row disclaiming the case.

**Why a note.** The behaviour is specified and correct; the pointer and the unstated cost are the
defect. Standing note V13-7 (the *empty-provider* case's dangling §6 pointer, enlarged by this same
fix-up and flagged by round 13 as the item a fourteenth session should address first) is the
adjacent gap; a fix-up that adds V13-7's §6 row should settle both delegations in one pass.

**Fix if applied.** Cite the row by name — *"caught by §6's `:engine`-throws row (rung 5)"* — and
state its actual consequence (engine off for the session, `FATAL` chat diagnostic), so §12 item
22b's reviewer and Phase 5 both know the trap's price; or give §6 the dedicated row V13-7 already
asks for and point both cases at it.

---

### V14-5 — §5.1's package-placement rule binds "all phases", but §2.1 assigns no package to Phases 8, 13 or 14, and the table carries neither the completeness statement nor the amendment route its sibling contracts have · **note** · **touches §5: no**

**Location.** §5.1's placement row, **l. 3805**; §2.1's tables, **ll. 1278–1299**. Against §5.2's
`DebugService` row (**l. 3827**), §9 (**l. 4096**) and §4.9.2 (**ll. 3329, 3335**).

**Evidence.** l. 3805 exposes *"a phase's code goes in the package §2.1 assigns it"* to **all
phases**. §2.1's `:engine` table (ll. 1280–1289) ends at `engine.diag` with no package for
Phase 13's texture systems or Phase 8's shadow work, both engine-side subsystems (§5.2's
pixel-transfer row names both as facade consumers); §2.1's `mod.glue` row (l. 1296) is filled by
"Phases 1 (facade impl shape), 6, 7, 9", while §5.2 l. 3827 assigns **Phase 14** the
`DebugService` implementation — KHR_debug GL code whose home is the `mod.glue` device (§12 item 22
puts all seven service implementations there). Read literally, three phases cannot obey the rule.
The contrast is with the document's own siblings: §4.9.2's channel list states its completeness —
*"covers **all fourteen phases**, which is a property worth stating"* (l. 3329) — and carries an
explicit amendment route (l. 3335), as §5.2's facade does for verbs (ll. 3020–3022); the package
table runs neither argument.

**Why a note, and how it sits against V13-6.** Standing note V13-6 already records the `mod.glue`
edge of this for Phases 3/5/13 (and round 13's Resolutions narrowed it: the provider
implementation is Phase 1's own "facade impl shape", so no phase is orphaned there). This finding
is the increment V13-6 does not reach — the two engine-side phases and Phase 14 — and it is a note
for the same reason V13-6 is: §5's preamble already gives every dependent the generic route
(*"A dependent that needs something not here flags the request in its own §5"*, ll. 3790–3793),
so nobody is stranded, only unrouted by the specific rule that claims to bind them.

**Fix if applied.** One sentence under §2.1 mirroring §4.9.2's: a phase with no assigned package
proposes one in its own §11/§5 and this document is amended by a fix-up — or name the missing
homes outright (an `engine.textures` for 13, Phase 8's engine home, 14 on the `mod.glue` row).
The naming branch touches §5 (§2.1 is an exposed §5.1 contract), which is V13-6's stated reason to
prefer the sentence branch unless a fix-up is already re-verifying — as the V14-1/V14-2 fix-up
will be.

---

### V14-6 — "GLSL 120 has no `gl_InstanceID` (RESEARCH.md §3.5)": the cited section says nothing about it, and the string appears nowhere in RESEARCH.md · **note** · **touches §5: no**

**Location.** §3's composite `countInstances` row, **l. 1401**; §4.7.4's instanced-draw absent-verb
row, **l. 3018**; `[D-P1-33]`, **l. 4371** (*"GLSL 120 has neither (RESEARCH.md §3.5)"*); the
doc's own §0.6 inputs record, **ll. 222–223** (*"§3.5 (GLSL 120 has no `gl_InstanceID`)"*);
restated without the citation at ll. 204 and 4575.

**Evidence.** RESEARCH.md §3.5 (ll. 304–326) covers four things: GLSL-120-era fixed-function
coupling, the standard macro header, preprocessor support, and the era bridge. It never mentions
`gl_InstanceID` or instancing, and `grep gl_InstanceID` over the whole of RESEARCH.md returns
nothing. What §3.5 does supply is the *premise* — packs are GLSL-120-era code — not the absence
fact. The substantive claim is true (`gl_InstanceID` is GLSL 1.40 / `EXT_gpu_shader4`), and the
prior rounds knew the difference: round 5 stated it split (*"packs are GLSL-120 (RESEARCH.md §3.5),
and GLSL 120 has no `gl_InstanceID`"*, its l. 280), and round 7 decomposed it explicitly, sourcing
the absence to its own GL knowledge and only the premise to §3.5 (its ll. 114–116). The document's
compressed citation asserts §3.5 states what it does not — in a `[V:doc]`-tagged row and a
decision-log rationale — which is the failure class V7-3 established the standard for
(`[D-P1-30]`'s GL-semantics claim now carries `[V:web]` with two URLs). Round 6 marked the
composite claim ✅ against "§3.5:306–309" (its l. 165), so this survived by a citation that looks
correct; no round has ruled on the attribution itself.

**Why a note.** The design conclusion is unaffected — the deletion of the instanced verb rests
independently on App D.4's uniform argument, which l. 3018 already leads with and which carries
`[D-P1-33]` alone. Provenance, not substance.

**Fix if applied.** Split the citation at all sites the way round 5 and round 7 themselves did:
"packs are GLSL-120-era (RESEARCH.md §3.5), and GLSL 120 has no `gl_InstanceID`" with the second
clause tagged `[A]` or `[V:web]` plus a refpage/spec URL per the `[D-P1-30]` precedent — or drop
the clause and let App D.4's argument stand alone. Sites: ll. 204, 222–223, 1401, 3018, 4371.

---

### V14-7 — the fix-up's closing paragraph counts "three of round thirteen's four corrections" as reaching §5, then describes the fourth reaching §5 in its next sentence · **note** · **touches §5: no**

**Location.** The closing §G1.3 paragraph, **ll. 4916–4925**; the §0.13 header, **ll. 1158–1159**.

**Evidence.** l. 4916: *"**This fix-up altered §5**, at four rows … Three of round thirteen's four
corrections reach it"* — followed at ll. 4924–4925 by *"The fourth, V13-1 … **reaches §5.2's
non-verbs row**"*. All four reach §5; the paragraph says so twice and counts three once. The
history explains it: the round-13 review scored V13-1 *"touches §5: no"* (its ll. 10, 138) and the
fix-up extended it onto §5 as a recorded judgment call (its ll. 732, 996), so §0.13's header line
— *"three corrections touching §5"* (l. 1159) — is defensible as reportage of the review's
scoring, sitting two sentences before the V13-1 bullet that corrects it (l. 1171, *"this
correction **does** touch §5"*). The closing paragraph has no such reading: "reach it" is the
document's own voice and is contradicted by its own next sentence.

**Fix if applied.** l. 4916: "Three" → "All four" (or "Three … were scored so by the review; the
fix-up extended the fourth"). Optionally one clarifying clause at l. 1159. One-word class.

---

## 2. What was checked and came back clean

Named because a round reporting only findings misrepresents its coverage. Items 1–5 are this
session's own derivations; item 6 relays the fan-out's coverage as the fan-out's.

**1. Declined on my own derivation: the candidate that §5.1's foreign-texture provider hands its
contents to Phases 5 and 13, neither of which §2.1 gives a `mod.glue` home.** This is standing
note **V13-6**, deferred by round 13 with its reason, and the round-13 Resolutions *narrowed* it
in a way that answers this candidate's central claim: the key-resolution half of the provider is
part of the *"`mod.glue` implementation shape Phase 1 already owns"* under §2.1's "Phase 1 (facade
impl shape)" — and §12 item 22b confirms Phase 1 ships the `mod.glue` `TextureHandle`
implementation and the `mod.core` install call at v0.1, with Phases 5/13 supplying *which* keys as
contents (§9 l. 4094: "its contents arrive with their owners"). The candidate's remaining edge —
§4.12 l. 3591's *"the inventory adds no phase that table does not name"* being false against its
own cell (which lands members on Phases 3 and 5/13) — is V13-6's literal subject. Nothing here is
new force; not re-admitted. The genuinely uncovered increment (Phases 8/13/14 versus the placement
rule) is V14-5.

**2. Declined on my own derivation: the candidate that §3's gbuffers/shadow `countInstances` row
attributes `doc/shaders.txt`'s uniform-block layout to RESEARCH.md, with the file missing from
§0.1's inputs record.** This is standing note **V12-12**, verbatim — found by round 12
(its ll. 642–664), verified there against shaders.txt l. 182, deferred in its Resolutions
(l. 1124), and still unapplied (l. 1402's cell is unchanged and §0.1 still lacks the file). Round
12 also already sized the inputs-record half: the read *is* disclosed at §0.7's V7-1 bullet
(l. 260 cites `doc/shaders.txt` by name), so only the in-cell attribution is missing. I re-derived
the whole chain — "common" occurs zero times in RESEARCH.md, App D has no GBuffers heading
(ll. 1323–1377: D.1–D.4 by subject), shaders.txt l. 123/182/184 carry the real structure — and
V12-12 holds exactly as recorded. One extension for whenever it is applied, recorded here rather
than as a finding: the same unattributed clause also sits in `[D-P1-33]`'s rationale (l. 4371,
where *"Vertex Shader Configuration"* — a shaders.txt heading, per §0.7 l. 260–261 — is attributed
to *"§3.2, App A.3 and App D.4 — the three sources it cited"*) and in `[D-P1-35]` (l. 4373); the
one-clause fix should sweep all three sites plus §0.1's table.

**3. The rest of the round-thirteen fix-up's application, checked at every site I could reach.**
V13-3's bind-only rule is stated consistently at all five sites (§4.7.3, §5.1, §5.2, §11.4, §12
item 22b — same legal pair, same illegal five, same lifetime exclusion, same per-use resolution
rule); its incompleteness (V14-1) is the finding, its consistency is clean. V13-4's re-attribution
verifies end to end: `DESIGN.md` ll. 1334/1469/1683/1828 say what the cells quote, my own sweep of
Phase 5's spec (ll. 1409–1525) reproduces exactly one "mipmap" occurrence at l. 1469, and §3
l. 1385 and §5.2 l. 3817 agree with each other — the Phase 13 edge (V14-3) excepted. The §0.13
addendum's read list matches what the cells actually cite. The closing §G1.3 paragraph correctly
reports four altered §5 rows and the byte-for-byte declaration claim — I checked §5.2's changelog
row against §4.7.4's declarations (seven services, four `non-sealed` leaves under a sealed
`GLHandle`, `ForeignTextureProvider`/`ForeignTextures` present) and nothing signature-shaped moved.

**4. §6's ladder, walked against the delegations that name it.** The rung-2 row's two-drain
protocol matches §4.7.4's and §5.2's statements (elision bit, loop-to-`GL_NO_ERROR`, replay on
cached values, unattributable → 3→4 row); the 3→4 row receives both stated reasons; the rung-5 row
and the build-time row are the last two in that order — which is V14-4 — and rung 2a's relabel
matches its REV2 history. The `countInstances` complex is internally consistent across §3's two
rows, l. 3018, `[D-P1-33]`/`[D-P1-35]` and §11.4's two blocks: composite loop Phase 7 `[v0.5]`,
upload Phase 6, gbuffers/shadow case open with owners 3/4/7 — all of which I checked against
RESEARCH.md §3.2/§4.2/§4.4 and App A.3/D.4, and which hold; only the two provenance compressions
(V14-6 here, V12-12 standing) mar it.

**5. App F.7 against §3, the check that produced V14-2's boundary.** The five F.7 keys resolve:
`alphaTest`/`blend`/`scale` have rows, `flip` and `program.<prog>.enabled` correctly have none
(round 7's ruling — no facade verb needed; Phase 5 ping-pong policy and Phase 4 registry
respectively — re-derived and still right). The `scale.<prog>` row itself (l. 1405) is exact:
`DESIGN.md` really is silent on who multiplies the rectangle, the three inputs' owners are cited
correctly (ll. 1245, 1334, 1486–1489 territory, Phase 7's part (a)), and the row's refusal to name
an owner is the correct §G1.1 posture. It is the two siblings that failed the same audit.

**6. The fan-out's clean areas, relayed as its coverage** (spot-checked where a finding above
touches the same ground, not independently re-derived in full). V13-1's citation chain
(RESEARCH.md ll. 526, 545–546, App E row 17's column heading and hook need 6 at l. 813;
`DESIGN.md` ll. 1486, 1685, 1709–1713) resolves, and the framebuffer-bind row's v0.1 obligation
reads identically in §5.2, §4.7.4's table and §4.12's second gap. V13-2's two-vocabulary split:
App F.5's `minecraft:` forms (ll. 1481–1484), App B.3's colon-free sampler names (ll. 1229–1246),
`DESIGN.md` ll. 2269–2271/2287 — disjointness and ownership hold; §9's `v0.5` tag matches §G5.1.
Every §5.2-exposed verb exists in §4.7.4 with the stated shape and no §4.7.4 verb is unexposed;
`blit` remains the one provisional verb with its condition stated. §3's five probe rows are exact
against RESEARCH.md §4.1, the macro-header row against §3.5 ll. 312–314, the ivec2/ivec4 rows
against App D.3/D.1/D.4 (with the five-passage Phase 9 exclusion holding at `DESIGN.md` ll. 1601,
1918–1932, 2419), the `centerDepthSmooth`, noise/custom-texture, `GL_QUADS`, no-UBO, no-lwjglx
and geometry-ARB rows against their cited lines, and the extension-set row's REV2 split at
`DESIGN.md` ll. 992–997. §5.3's rows match §4.9.2's all-fourteen channel list, §4.9.3's four
flags, §4.5.2's three configs and §4.11's CI layout. The GL-error contract is stated identically
at §4.7.4, §5.2, §6 and §7. No fan-out agent opened a forbidden source.

---

## 3. Verdict

# PASS-WITH-CORRECTIONS

Two corrections, five notes, zero blocking. Not PASS: V14-1 leaves a client-crashing misuse
(foreign `dst` under `copyDepthToTexture`) formally legal under a contract whose own text says
nothing here is mechanically checkable and whose review item checks only the five-verb list — that
is a real gap in the §5 contract dependents build against, not polish; and V14-2 mis-routes
pack-contract policy across three phase boundaries against the governing document's explicit
coverage row, the exact defect class round 13 corrected one row over. Not FAIL: both corrections
are cell- and paragraph-sized rewrites in a document whose structure, seam design, facade shape
and error architecture survived this round's derivation everywhere else it was probed; nothing
requires a rebuild.

### Per-finding §5 disposition

| Finding | Severity | Touches §5 |
|---|---|---|
| V14-1 foreign-handle enumeration misses three `FramebufferService` verbs | correction | **yes** — §5.1 provider row (l. 3809), §5.2 opaque-handle row (l. 3819) |
| V14-2 alphaTest/blend rows route policy to 5/6 against `DESIGN.md` 3/4/7 | correction | **yes** — §5.2 `StateService` consumer column (l. 3823) gains 4 |
| V14-3 mipmap-gate consumer set omits Phase 13 | note | deferred: no; the widening branch, if applied, touches §5.2 l. 3817 |
| V14-4 "§6's last row" pointer resolves to the build-time seam row | note | no |
| V14-5 §2.1 assigns no package to Phases 8/13/14; no amendment route | note | deferred: no; the naming branch, if applied, touches §5.1 via §2.1 |
| V14-6 `gl_InstanceID` claim cited to a §3.5 that does not state it | note | no |
| V14-7 closing paragraph's three-vs-four §5 count | note | no |

### §G1.3 line

Both corrections alter §5 when applied, so §G1.3's trigger will fire on the fix-up:
`PHASE_1_DOC.md` remains **not verified**, is **not** a valid dependency input, and Phase 2,
Phase 3 and everything downstream stay blocked (§G5.3) until the fix-up applies V14-1 and V14-2
(deferring or applying the notes with recorded reasons under `## Resolutions` here) **and a
fifteenth verify session returns**. What that session should inherit explicitly: the widened
bind-only enumeration and the re-derived App F.7 rows as fresh material at round seven's price;
the standing deferred notes V12-12 and V13-5–V13-8 (V13-7 still first among them, per round 13's
own instruction, and jointly addressable with V14-4); and V14-3/V14-5's conditional-§5 branches,
which are cheapest to take while a re-verify is already owed.

*This verify session wrote no code, ran no build, test or gradle task, made no network request,
and created exactly one file: this one. `PHASE_1_DOC.md`, all three `DESIGN.md` revisions,
`RESEARCH.md`, `PINTONIUM_DESIGN.md`, and `PHASE_1_REVIEW_1.md` through `PHASE_1_REVIEW_13.md` —
including their `## Resolutions` sections, which are evidence — are unmodified.*

---

## Resolutions

**Fix-up session, 2026-07-26.** Both corrections applied; all five notes deferred per this round's
brief ("notes are not applied this round"), each recorded below with its reason. Nothing refused.
Every load-bearing claim was re-derived at its source before being written into the document; where
this session's derivation added anything beyond the review's, it is flagged as this session's.
All line numbers below cite the **finished** `docs/phase1/v13/PHASE_1_DOC.md` unless marked
otherwise. The §5 gate ran by content anchor before and after:
`21283605083beffbcaba020d633690b1abb737467da975544583b430b740623c` before,
`8b4dd46bc5fff3109dc686829cb396d4d9cc465ceddca03fbc703b0060570968` after — **§5 changed**, as both
corrections require, so §G1.3's re-verify trigger fires.

### V14-1 — applied, stated as closure at every site

**Re-derived.** §4.7.4's declarations were swept for `TextureHandle`: exactly **ten** verbs accept
one — `TextureService`'s `allocate`/`setParameters`/`upload`/`bindToUnit`/`generateMipmap`/`delete`
(ll. 2749–2761), `FramebufferService`'s `attachColor` (l. 2701), `attachDepth` (l. 2702) and
`copyDepthToTexture` (l. 2736), and `DebugService.label` (l. 2797), which takes the sealed
`GLHandle` a `TextureHandle` is a leaf of. `TextureService.create` and `handleFor` *return* one and
accept none. So the legal set is two, the illegal set eight, and the review's three-verb gap is
exact: the old enumeration classified seven of ten and its own §12 item 22b operationalised the
five-verb list as the reviewer's whole check, so a backend performing `copyDepthToTexture` onto a
foreign `dst` passed the check while breaking §6's rung 5.

**Applied as** the closure form the finding's fix names — a foreign handle is legal to `bindToUnit`
and `DebugService.label` **and to nothing else in §4.7.4 that accepts a `TextureHandle`** — at all
five sites: **§4.7.3's bullet, ll. 2608–2622** (the declaring site, with `copyDepthToTexture` named
the destructive case); **§5.1's provider row, l. 3861**; **§5.2's opaque-handle row, l. 3871**;
**§11.4's *To Phase 5*, ll. 4696–4700**; **§12 item 22b, l. 4889**, whose reviewer check is now the
closure itself — "every §4.7.4 verb that accepts a `TextureHandle` except `bindToUnit` and
`DebugService.label`" — with the list demoted to "today", so a verb added later is in scope the day
it lands.

**One ground in §4.7.3 is this session's derivation, not the review's, and is flagged as such.** The
review's reason against `attachColor`/`attachDepth` is that a vanilla-owned texture must not become
a render target of an engine FBO. This session added a second, independent ground: a GL framebuffer
attachment *stores* the texture object in the FBO's attachment state, so the per-use resolution rule
that protects a bind cannot protect an attachment — after Minecraft's own resource reload the engine
FBO would hold a dangling attachment. That is why attach is illegal even where no draw ever writes:
the capture itself outlives the object. If round fifteen finds this ground wrong, the review's
render-target ground still carries the classification alone.

**Deliberately preserved inside the rewrite:** the rejection sentence's "caught by §6's last row"
pointer (l. 2620) is V14-4's subject and V14-4 is a deferred note, so the pointer is carried
verbatim rather than silently fixed — see Notes deferred.

**Sweep.** `those five` — zero survivors; the five-verb list without the FramebufferService widening
survives at exactly two deliberate sites: **§0.13's bullet (l. 1177)**, a dated historical record of
round thirteen's ruling (the V12-8 precedent: addenda are dated, not rewritten — §0.14 states the
widening), and **§11.4's *To Phase 13* transfer-verb list (l. 4746)**, which enumerates verbs for
*engine-created* textures and is V14-3's subject, deferred. §2.4 (l. 1402), `[D-P1-36]` (l. 4426)
and the closing paragraph carry the closure-shaped one-clause form ("bind-only and outside the
lifetime rule") and need no edit — exactly the finding's own ruling on §2.4.

### V14-2 — applied, both rows re-derived to the coverage row's routing

**Re-derived.** `DESIGN.md` l. 2410: *"per-program alphaTest/blend/scale/flip overrides | 3 (parse),
4 (apply), 7 (execute)"*. l. 1245: *"per-program render-state overrides
(alphaTest/blend/scale/flip/enabled — stored; applied by Phase 4)"* in Phase 3's spec. l. 1334:
"alpha/blend overrides" among Phase 4's per-slot registry state. l. 1365: the *"per-program
alpha/blend lock"* as a stated obligation of Phase 4's use-program barrier. On the other side, a
whole-file grep: "alphaTest" occurs in `DESIGN.md` **only** at ll. 1245 and 2410, and "blend" has
**zero** occurrences inside Phase 5's spec (ll. 1409–1525 — the hits jump from Phase 4's ll.
1334/1336/1337/1365 to Phase 6's l. 1549). The "per-buffer routing" clause: RESEARCH.md l. 1511's
`blend.<prog>=off|<src> <dst> [<srcA> <dstA>]` is per-program with no per-buffer axis, and
per-buffer blending exists in the sources only as §3.6.7's Iris-side `PER_BUFFER_BLENDING` flag
(l. 429). All exactly as the finding derived.

**One adjacency the review did not cite, found by this session's own sweep and written into the
cell:** `DESIGN.md` **l. 1337** does carry per-buffer blending — *"per-buffer `BufferBlendOverride`
via GL 4.0 `glEnablei`/`ARBDrawBuffersBlend`"* — inside **Phase 4's** registry bullet's REV1
Pintonium cross-check, an inventory to check the slot model against, not an assignment. The
corrected cell names it so the deleted clause's one echo in the design document is accounted for;
it strengthens the re-routing (it sits in Phase 4's bullet, not Phase 5's).

**Applied as:** **§3's two rows, ll. 1444–1445**, rewritten to the derivation the `scale.<prog>` row
(l. 1446) already performs — parse/store 3 (l. 1245), per-slot carry and barrier lock 4 (ll. 1334,
1365), execute 7 (l. 2410) — with the "per-buffer routing" clause **deleted, not re-homed**, and the
tags split per the scale row's precedent: `[V:doc]` for the directive, `[V:design]` for the routing.
**§5.2's `StateService` row, l. 3875**: consumer column gains **4** with its citation, and the
detail cell states why (the alpha/blend lock is applied through `alphaTest(...)`/`blend(...)`, with
Phase 7 invoking the barrier). **§4.7.4's composite-block sentence, ll. 2841–2844** — the neighbour
the review said was "worth a glance once the rows above it stop saying 5/6": glanced, and edited,
because its "**Which** state is perturbed at which moment is Phase 5/6/7 policy" would after this
correction exclude the one phase the rows now name for the override values; the composite-block
defence holds for perturbation *moments*, not for the values, so the sentence now says both. The
other neighbour, l. 2832's "alpha/blend also being per-program state from App F.7", is phase-neutral
and left as-is — the review's own ruling.

**Sweep.** "Phase 5/6 policy" survives at ll. 1434, 4426 and 4752 only — all three are the fixed
unit map / provider *contents* sense (§1.2's joint ownership), not render-state values; "which
program carries which value" survives only inside l. 1444's quoted-defect clause; "per-buffer"
survives at l. 2829 ("per-buffer clears", a different and correct concept) and inside the new
derivation text.

### Bookkeeping applied with the corrections

§0's `Last revised` → §0.14 (l. 10) and the dated-claims range → §0.4–§0.14 (ll. 23–25). §0.13's
status restamped **at the time**, converted to past tense, and closed with the supersession sentence
(ll. 1185–1199). New **§0.14** at ll. 1201–1235 — 35 lines, inside the 40-line cap. The closing
block's counts updated (fourteen verify sessions, eleven fix-ups) with the eleventh fix-up's
sentence appended, and its final paragraph rewritten to describe **this** round's §5 alteration
(three rows) and the **fifteenth** verify session now owed (ll. 4939–4996). §5.2's changelog row was
**not** given a §0.14 entry: no signature moved, and round thirteen's identical no-entry state was
checked clean by this review's §2 item 3.

### Notes deferred

Per this round's brief, notes are recorded rather than applied. Each is a considered deferral with
its reason, not an oversight.

- **V14-3 (mipmap-gate consumer set omits Phase 13).** Deferred on the brief's no-notes rule — and
  that rule, not the note's merits, is the reason: the review recommended taking its §5-touching
  branch while a re-verify was already owed, and that recommendation still stands for whichever
  session next applies notes. Both branches remain open (add **13** to §5.2's profile row l. 3869
  citing `DESIGN.md` l. 2254, or state that Phase 13 mip-builds by per-level `upload` and strike
  `generateMipmap` from l. 4746). Nothing at ll. 1426, 3869 or 4746 was changed.
- **V14-4 ("§6's last row" resolves to the build-time seam row).** Deferred; and because V14-1's
  rewrite passes through the very sentence that carries the pointer, the pointer was **preserved
  verbatim at l. 2620** so this deferral is real rather than a silent half-fix. Round fifteen should
  treat it jointly with standing note V13-7 (the §6 row both delegations want), per this review's
  own suggestion.
- **V14-5 (§2.1 assigns no package to Phases 8/13/14).** Deferred; §5's preamble still gives every
  dependent the generic request route, so nobody is stranded. Sits beside standing note V13-6 as its
  engine-side/Phase-14 increment; the one-sentence amendment-route branch remains the cheaper fix.
- **V14-6 (`gl_InstanceID` cited to a §3.5 that does not state it).** Deferred; provenance, not
  substance — the deletion of the instanced verb rests on App D.4's uniform argument independently.
  The five sites (ll. 204, 222–223, 1442, 3070's row and `[D-P1-33]` at l. 4423, plus §11.4's
  l. 4627) are unchanged; the fix, when applied, is the round-5/round-7 split citation with `[A]`
  or `[V:web]` on the absence clause.
- **V14-7 (the closing paragraph's three-vs-four count).** Deferred as a finding — but the closing
  paragraph is rewritten every round by the mandatory count update, so the defective sentence no
  longer exists; its replacement describes round fourteen (two corrections, both reaching §5). The
  §0.13 header line "three corrections touching §5" (l. 1159) is **left as-is**, per this review's
  own reading of it as defensible reportage of round thirteen's scoring, corrected two sentences
  later. What round fifteen should check instead is the *new* paragraph's own counts.

Standing deferred notes **V12-12** and **V13-5 through V13-8** were not this round's to apply and
were not touched; V14-1's sweep confirmed the sites V12-12 names are in the state round twelve
recorded.

### §G1.3 status

This fix-up **altered §5** — the content-anchored hash moved, at §5.1's provider row and §5.2's
opaque-handle and `StateService` rows — so the phase does **not** close: `PHASE_1_DOC.md` remains
**not verified**, is **not** a valid dependency input, and Phase 2, Phase 3 and everything
downstream stay blocked until a **fifteenth** verify session returns (§G5.3). That session inherits:
the closure-stated bind-only rule and the re-derived App F.7 rows as fresh material at round seven's
price; this session's two flagged derivations (the dangling-attachment ground in §4.7.3, the l. 1337
adjacency in §3's blend row); the five notes above plus the standing ones (V13-7 first, jointly with
V14-4); and V14-3/V14-5's conditional-§5 branches, cheapest while a re-verify is already owed.

*This fix-up session wrote no code, ran no build, test or gradle task, made no network request, and
modified exactly two files: `docs/phase1/v13/PHASE_1_DOC.md` and this one. `RESEARCH.md`, all four
`DESIGN.md` revisions, `PINTONIUM_DESIGN.md`, and `PHASE_1_REVIEW_1.md` through
`PHASE_1_REVIEW_13.md` — including their `## Resolutions` sections, which are evidence — are
unmodified.*
