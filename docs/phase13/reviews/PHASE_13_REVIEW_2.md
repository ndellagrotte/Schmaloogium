# Phase 13 verification review — round 2

## 0. Method and reading order

Sole adjudicator for round 2 of target `phase-13` (manifest `verification/targets/phase-13.json`,
governing design revision `docs/design/v3/DESIGN.md`).

Reading order actually followed — independent re-derivation first, prior review last:

1. Target `docs/phase13/v1/PHASE_13_DOC.md`: §2.2/§2.3 (199–253), §3 conformance rows, §4.3.6 and
   §4.4 (819–863), §4.6 hook rows (940–947), the declared interface/change-trigger region §5
   (1024–1128), §9 threading (1181–1182), and §12 (1390–1461).
2. Authority: `docs/design/v3/DESIGN.md` §G1.3, §G9 mandatory template (817–855), Phase 13 spec and
   Doc gate (2436–2513).
3. Contract ground truth: `docs/research/v1/RESEARCH.md` at the manifest-selected selectors.
4. Dependency binding regions, re-resolved line by line: Phase 3 (notably 957–998 and 1427–1515),
   Phase 5, Phase 7.
5. Supporting evidence: the Phase 3/5/7 review anchors and the Pintonium evidence-map selectors.
6. Prior Phase 13 review `docs/phase13/reviews/PHASE_13_REVIEW_1.md` — read last, including its
   Resolutions block, and used only to check what is already settled.

Deviations: `reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt` and `doc/shaders.properties`
are listed as supporting evidence but `doc/shaders.txt` matches the `*.txt` forbidden path pattern.
As in round 1 I resolved the conflict conservatively and did not open it; the one finding that
concerns it (candidate-007) is admitted only as an unordered note and rests on the deterministic
citation resolution already performed upstream, not on my own read of a forbidden source.

No network use. No agent fan-out: this session dispatched no subagents, ran no verification script
and started no nested loop; the candidate set arrived pre-refuted and pre-gated.

Gate drops: candidate-001 ("the round-1 flat-normal repair missed §4.3.2") was rejected at the Gate
for a one-line evidence mis-anchor. candidate-003 (§3.5 row E-10 omitting `H13-ATLAS-04`) was
eliminated at the Refute stage. Both are settled pre-adjudication, are discussed in §2, and are not
findings.

## 1. Findings

### Finding 1 (candidate-005) — `atlasSize(AtlasId)` is exposed without any specification of the `AtlasId` value domain or of which atlas a consumer must query

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:209` (§2.2), `:236`/`:243` (§2.3), `:833`–`:857`
  (§4.4), `:1034` (§5.1 interface region), restated at `:1182` (§9).
- **Claim under test:** the exposed `AtlasSizeResult`/`atlasSize` contract carries enough detail for
  Phase 6, through Phase 7's composition, to obtain the App D uniform value without guessing.
- **Evidence and re-derivation:** the operation is parameterized — `AtlasSizeResult
  atlasSize(AtlasId atlas)` (`:209`) — over `public record AtlasId(String canonicalName) {}`
  (`:236`), an unconstrained string wrapper whose canonical-name grammar is fixed nowhere in the
  document. The catalog is `AtlasCatalog(List<AtlasDescriptor> atlases)` (`:243`), supplied inbound
  from `mod.glue` (`:229`) and not exposed on any §5.1 surface, while §4.4 reasons about a singular
  "base atlas's `width`/`height`" (`:847`–`:848`) and §4.1's scope is explicitly plural
  ("block/item atlases", `:126`). I searched the whole target for a resolution: §4.6's hook rows
  pass an unspecified `atlasIdentity` (`:941`, `:944`) never tied to `AtlasId.canonicalName`; §9
  restates the parameterized form without constraining the argument (`:1182`); the §5.1 row at
  `:1034` omits the parameter entirely; §5.2 names no atlas-identity type supplied by Phase 5 or 7;
  and §12 items 1 and 12 add nothing. I tested the strongest objection — that the parameter is
  harmlessly opaque because a consumer holds the same catalog — and it fails, because the catalog is
  an input to Phase 13, not part of Phase 13's exposed surface. A Phase 6/7 implementer of the App D
  upload must therefore guess the argument.
- **Severity:** correction. The value semantics, the `Known`/`Unknown` domain, the validity window
  and the invalidation trigger are all fully specified; only the selector is missing, and the repair
  is a bounded local addition to §4.4 and §5.1, not a rebuild.
- **Touches interface/change-trigger region: yes.** The ordered edit changes the exposed-surface
  statement for `AtlasSizeResult`/`atlasSize` at `:1034`, inside the declared region `:1024`–`:1128`.
- **Fix:** in §4.4 state the `AtlasId.canonicalName` domain (the Minecraft atlas resource-identity
  string as captured at `H13-ATLAS-02`, i.e. the same value the `atlasIdentity` hook parameter
  carries) and name the block/item atlas value used during world rendering; in §5.1 either expose a
  no-argument accessor for that atlas's `AtlasSizeResult` or state which `AtlasId` Phase 7 passes on
  Phase 6's behalf. One paragraph plus one row; no restructuring.

### Finding 2 (candidate-006) — conformance-map and §5.2 Phase 3 line anchors point at unrelated content

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md` §3.1 rows F5-1/F5-2/F5-9/F5-13/F5-16 (`:316`–`:331`),
  §3.4 row F3-1 (`:362`), and the same anchors reproduced inside the interface region at §5.2
  (`:1050`–`:1054`).
- **Claim under test:** each mapped row's cited Phase 3 location actually supports the design element
  it claims.
- **Evidence and re-derivation:** I re-resolved the anchors against the current Phase 3 v1 text.
  `docs/phase3/v1/PHASE_3_DOC.md:1443` is the `IdMappingInput` binding row, not the texture-key
  algebra; `TextureBindingKey` is declared at `:1482`, with the discriminator rule at `:1484`–`:1486`
  and the stage expansion at `:1487`–`:1488`. Raw-target dimension arity ("arity 1, 2, 3, and 2") is
  at `:1496`–`:1497`, not `:1455`–`:1456`; `NoiseTextureSpec` `Override` is at `:1502`–`:1503`, not
  `:1461`–`:1463` (which is the custom-expression enum block); and F3-1's `NoiseRequirement(enabled,
  resolution)` anchor `:1493` lands on the `Raw`/`MinecraftResource` `CustomTextureSpec` variants,
  the record itself being declared at `:1159` and bound in the `ResourceRequirements` shape near
  `:1533`. The drift is systemic (~40 lines), not a single typo. Every underlying semantic claim is
  nonetheless correct against the real Phase 3 text — which is why this is provenance repair rather
  than a contract error. §5.4's caveat at `:1116` is not equivalent coverage: it says only that the
  round-36 fix-up "may also have moved the Phase 3 line anchors cited in §5.2", a conditional scoped
  to §5.2 that names no row and does not reach §3.1/§3.4, while the conformance map presents the
  anchors as verified provenance.
- **Severity:** correction. A reader following any of these anchors lands on the wrong contract text,
  in a conformance map whose whole function is resolvable provenance; but no design element or
  consumed shape is wrong, so the repair is re-resolution, not rebuilding.
- **Touches interface/change-trigger region: yes.** The identical anchors sit in §5.2 at `:1050`–`:1054`,
  inside `:1024`–`:1128`, and must be corrected there too.
- **Fix:** re-resolve every `docs/phase3/v1/PHASE_3_DOC.md` line anchor used in §3.1, §3.4, §4.2.1,
  §4.3.1/§4.3.3/§4.3.4 and §5.2 against the current Phase 3 v1 text — `TextureBindingKey` and its
  stage expansion `:1482`–`:1488`, `CustomTextureSpec`/raw arity `:1490`–`:1497`,
  `NoiseTextureSpec.Override` `:1502`–`:1503`, custom-spec ordering `:1504`–`:1508`, the Phases-4/13
  sampler-type grant `:1509`–`:1510`, `NoiseRequirement` `:1159`/`:1533` and the absent noise
  baseline in the absent-directive paragraph — or replace the line anchors with stable
  section/type references so a future Phase 3 edit cannot silently invalidate them. This scope
  explicitly includes the `:979`–`:980` macro anchors discussed under candidate-004 in §2, whose
  correct location is `docs/phase3/v1/PHASE_3_DOC.md:991`–`:992`.

### Finding 3 (candidate-002) — §4.3.6 points the R2 follow-through at the wrong §12 checklist item

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:830`–`:831` (§4.3.6) against §12 items 8, 12 and 19
  (`:1418`, `:1432`, `:1449`).
- **Claim under test:** §4.3.6's cross-reference to the checklist item implementing the R2-granted
  publication resolves to the correct §12 entry.
- **Evidence and re-derivation:** §4.3.6 reads "§5.3 request **R2** asks for it, and §12 item 12 is
  the one-line follow-through once it is granted". §12 item 12 is `[v0.5]` "Implement `atlasSize` of
  §4.4 …" (`:1432`) — unrelated to R2. Item 8 is the unconditional `UnsupportedBinding`
  implementation (`:1418`), so it is not an alternate correct reading. Item 19 is the only
  R2-conditional entry, and carries exactly the described semantics: `[v0.5, conditional]` "On R2
  being granted, publish the previously-`UnsupportedBinding` entries" with the bound half of
  `custom_colortexOverrideDiagnosedOrBound` as its test (`:1449`–`:1450`).
- **Severity:** correction. It is a factual internal pointer that misroutes an implementer, not a
  wording preference; but it is one number.
- **Touches interface/change-trigger region: no.** Line 830 lies outside `:1024`–`:1128` and the
  ordered edit changes nothing inside it.
- **Fix:** in §4.3.6 change "§12 item 12" to "§12 item 19".

### Note 1 (candidate-007) — row B3-4's shipped-doc citation range is shifted by one line and omits gaux1

- **Location:** `docs/phase13/v1/PHASE_13_DOC.md:341` (§3.2 row B3-4).
- **Claim under test:** `doc/shaders.txt:280`–`:283` shows units 7–10 carrying gaux1–gaux4.
- **Re-derivation:** I did not open the cited file (see §0: `*.txt` is a forbidden path pattern).
  On the deterministically resolved coordinates supplied upstream, the gaux1 row is at `:279` and
  `:283` is the unrelated `12 depthtex1` row, so the cited range covers gaux2–gaux4 plus one
  unrelated row; the shadow-stage half of the row's "gbuffers/shadow" claim is at `:298`–`:301`.
- **Severity:** note. The row's substantive contract surface (units 7–10 = gaux1–gaux4) is correct
  and independently backed by its RESEARCH citation `:1239`–`:1242`; only one provenance cell's
  range is imprecise, and I decline to order a fix-up on a citation I am not permitted to verify
  myself.
- **Touches interface/change-trigger region: no.**
- **Suggested (unordered) improvement:** change B3-4's shipped-doc citation to `:279`–`:282` and,
  since the row says "gbuffers/shadow", add `:298`–`:301`.

## 2. Checked and clean

**Finder clean areas accepted on re-derivation.**

- *New surface.* The round-1 edited sites (§0.2 item 1, §0.4 addendum, §3.6 item 3, §4.1.4, §5.2
  headers, §5.4, §11 closing) are mutually consistent, and the §11/§12 closing statements correctly
  state that a fresh whole-document round is required because §5 changed. Test names are used
  consistently across §3, §6, §8, §9 and §12; the noise sizing (256²×3 = 192 KiB), raw arity
  1/2/3/2 and companion memory factors agree with their sources; `D-P13-1`…`14` each have a matching
  referring site. Round 1's Finding 3 repair holds: §4.1.4 no longer calls `0xFF7F7FFF` a flat
  normal and states the literal-reading tension with its C-TX01 route.
- *Interfaces.* The overlay-publication surface (lease/`publicationId` atomicity, id and
  `registryFingerprint` pairing, rejection handling, destruction on close) matches Phase 5's binding
  §4.12/§5.1 faithfully; the resize-consumer participation matches Phase 5's `BufferResizeReason`
  ordering and `ResizeConsumerResult` domain; the App B.3 key domain and absence vocabulary are
  consumed unchanged, with the out-of-domain case honestly diagnosed as `UnsupportedBinding` plus a
  flagged R2 rather than an invented Phase 5 interface; §4.6 uses Phase 7's catalog format with the
  correct `require = 0`/`expect = 1` posture; and R2/R3/R4 each state a specified ungranted fallback.
  Findings 1 and 2 are the only interface-region defects, and neither widens or misstates a consumed
  contract shape.
- *Conformance.* Every scope-in requirement of the Phase 13 spec has at least one mapped row and I
  found no unmapped in-scope contract item. The two contract deviations (F5-17/R2 and M-1/R1) are
  flagged decisions with fallbacks, not silent gaps. Findings 2 and Note 1 concern anchor precision
  in the map, not its coverage or its semantic claims.
- *Doc gate.* All thirteen §G9 template sections are present, in order, and substantive; §10
  correctly records the absence of an assigned open question with its citation rather than being
  left blank.

**Candidates refuted or narrowed on re-derivation.**

- *candidate-004* (Phase 3 macro contract allegedly misattributed, and the quoted "normal/specular
  toggles" wording allegedly absent from Phase 3) — **dropped**. Its central factual assertion is
  false: `docs/phase3/v1/PHASE_3_DOC.md:991`–`:992` states verbatim that "OF option macros include
  normal/specular toggles …" and that FXAA is "normally absent, not falsely set", which is precisely
  the honest-flags posture Phase 13's R1 ungranted fallback rests on. So Phase 13's characterization
  of what Phase 3 provides, its quotation, and its R1 framing are all accurate, and the recommended
  fix — re-anchoring to the generic `optionMacros // OF H` line and deleting the quote — would
  replace an accurate citation with a weaker one. The only residue is that the anchors are stale by
  twelve lines; that is the same defect class as Finding 2 and is folded into Finding 2's ordered
  scope rather than counted twice.
- *candidate-005*'s severity was held at correction, not raised: the value semantics are complete
  and only the selector is missing.
- *candidate-006*'s fix was widened to include the §4.x Phase 3 anchors and the `:979`–`:980` macro
  anchors, and I declined the reading that §5.4's conditional caveat already covers it.
- *candidate-007* was held at note rather than correction, because I am not permitted to open the
  cited `*.txt` evidence and the row's contract surface is independently supported.

**Eliminated before adjudication (settled; not findings).** candidate-001 — the assertion that
round 1's flat-normal repair left §4.3.2 still calling the `NORMALS` `DefaultFill` a flat normal —
was dropped at the Gate for a one-line evidence mis-anchor; it was not revived or used as a finding
here. candidate-003 — §3.5 row E-10 mapping only `H13-ATLAS-01`…`03` while §9 counts
`H13-ATLAS-04` under the same App E class — was dropped at the Refute stage by strict refuting
majority. Both are excluded for those stated pre-adjudication reasons.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=1
Interface changed: yes

Zero blocking findings. Three admitted corrections and one unordered note. No structural miss: the
§G9 skeleton is complete, the conformance map covers every in-scope requirement, the consumed Phase
5 contracts are honoured, and each repair is a scoped edit — one paragraph and one row, a set of
re-resolved anchors, and one numeral — so FAIL is not warranted. PASS is unavailable because three
corrections stand.

Interface disposition: Findings 1 and 2 each order edits inside the declared change-trigger region
`docs/phase13/v1/PHASE_13_DOC.md:1024`–`:1128` (the §5.1 `atlasSize` exposed-surface row, and the
§5.2 Phase 3 anchors), so `Interface changed: yes`. Finding 3 and Note 1 order no edit inside that
region.

Trend/convergence: the supplied prior-round trend is empty. Against round 1 on disk, the count is
flat at three corrections but the character has converged: round 1's three corrections concerned
dependency-state truth and a self-contradictory default fill, all applied and confirmed repaired
here, while round 2's are provenance re-resolution and one missing parameter domain — narrower and
non-overlapping. No candidate cleared in round 1 was re-litigated.

Next required action: a scoped §G1.3 fix-up resolving all three corrections — specify the
`AtlasId.canonicalName` domain and the atlas-selection route for `atlasSize` in §4.4/§5.1,
re-resolve the Phase 3 line anchors in §3.1/§3.4/§4.x/§5.2 (or replace them with stable section
references), and correct §4.3.6's "§12 item 12" to "§12 item 19". Because the first two repairs
change the declared interface/change-trigger region, a fresh whole-document §G1.2 verification round
is required before Phase 13 can close.

## Resolutions

Applied under §G1.3 to `docs/phase13/v1/PHASE_13_DOC.md` only. Every anchor below was re-resolved
against the current dependency text in this session; the review's argument was not adopted as
evidence.

**Finding 1 (`atlasSize` selector) — applied.** Re-derivation confirmed the gap: `AtlasId` is an
unconstrained `String` wrapper, `AtlasCatalog` is an inbound-only shape, and no §5 row named the
argument. Repair, taking the review's first offered route because it removes the need for a consumer
to hold any identity at all: §2.2 gains `AtlasSizeResult atlasSize()`; §4.4 gains two bullets fixing
the `AtlasId.canonicalName` domain (the Minecraft atlas resource-identity string as reported at
stitch time — the same value `H13-ATLAS-01`/`H13-ATLAS-04` pass as `atlasIdentity` and the `id` of
`H13-ATLAS-02`'s `AtlasDescriptor`, neither invented nor normalized here) and stating that the
parameterless form answers for the world block/item atlas while `atlasSize(AtlasId)` serves any other
catalogued atlas and returns `Unknown` for an absent id; §5.1's `AtlasSizeResult` row names both
forms and points at §4.4 for the domain; §9's threading row covers both. No Minecraft resource string
was asserted, because none could be verified from the permitted inputs — the domain is defined
referentially through the already-specified hook parameter instead. `Unknown` for an out-of-catalog
id is the only added semantic, and it is forced by the pre-existing `Known`/`Unknown` closure rather
than a new decision. §12 item 12 already reads "Implement `atlasSize` of §4.4" and so covers both
overloads unchanged.

**Finding 2 (Phase 3 anchor drift) — applied, and swept wider than the cited sites.** Re-resolved
against `docs/phase3/v1/PHASE_3_DOC.md`: `TextureBindingKey` `:1482`–`:1488` (discriminator
`:1485`–`:1486`, stage expansion `:1487`–`:1488`); `CustomTextureSpec` `:1490`–`:1500` with raw arity
`:1496`–`:1497` and the format/integer-compatibility sentences `:1493`–`:1498`;
`NoiseTextureSpec.Override` `:1502`–`:1503`; custom-spec ordering and last-valid-wins `:1504`–`:1508`;
the Phases-4/13 sampler-type grant `:1509`–`:1510`; `NoiseRequirement` declared `:1159` and bound in
the `ResourceRequirements` shape `:1533`, absent noise baseline `:1543`–`:1544`; `ResourceRequirements`
binding row `:1440`; `DeclaredUniformCatalog` `:1434`; `PackConfiguration` `:1431`; `MacroConfiguration`
`:1435` with the option-macro sentence `:991`–`:992`; the `.mcmeta` ownership row `:309` and the
sidecar-retention row `:750`; §5.1 spans `:1422`–`:1625`, §3.2's texture rows `:749`–`:753`, §3.3's
noise directive row `:803`, §4.4 macros `:957`–`:996`. Edited sites: §0.1 Phase 3 reading row, §3.1
rows F5-1/F5-2/F5-9/F5-13/F5-16, §3.4 row F3-1, §4.1.1, §4.1.6 (both `:979`/`:980` macro anchors,
the candidate-004 residue), §4.2.1, §4.2.4, §4.3.1, §4.3.3, §4.3.4 (including the last-valid-wins
anchor, which had drifted onto Phase 3's debug-dump paragraph), §4.5.1, §5.2's nine Phase 3 rows, and
§5.3 rows R1 and R4. Line anchors were kept rather than replaced with section references, because the
§G-style provenance convention used throughout this document and its dependencies is line-anchored;
§5.4's existing conditional caveat already warns that a future Phase 3 round may move them. Every
semantic claim at those sites was re-checked and none changed: the quoted "normal/specular toggles"
and "normally absent, not falsely set" wording is verbatim at `:991`–`:992`, so candidate-004's
recommended rewording was correctly refused upstream and is not applied here either.

**Finding 3 (§12 pointer) — applied.** §12 item 12 is the unconditional `atlasSize` entry and item 8
the unconditional `UnsupportedBinding` entry; item 19 is the only `[v0.5, conditional]` "On R2 being
granted, publish the previously-`UnsupportedBinding` entries" row. §4.3.6 now names item 19.

### Notes deferred

- **Note 1 (row B3-4's `doc/shaders.txt` citation range).** Not applied. `*.txt` is a forbidden path
  pattern for this session, so the corrected range `:279`–`:282` plus `:298`–`:301` could not be
  verified first-hand, and editing a provenance cell to coordinates I cannot read would launder an
  unverified claim into the document. The row's contract surface stays backed by its RESEARCH
  citation. A round with an explicit grant to open that file should apply it.

### Interface disposition and residue

The declared change-trigger region (§5, formerly `:1024`–`:1128`) was edited intentionally: §5.1's
`AtlasSizeResult` row now names both `atlasSize` forms and the domain source, §5.2's nine Phase 3
rows carry re-resolved anchors, and §5.3's R1/R4 anchors were corrected. Region line numbers also
shift because §0.5, the §2.2 overload, and the §4.4 bullets were added above it. A fresh
whole-document §G1.2 round is therefore still required, as §0.5 and the closing note both state.

Settled, not to be re-fought without contrary evidence: candidate-001 and candidate-003 (dropped
pre-adjudication), candidate-004's central assertion (false against `:991`–`:992`), and round 1's
three repairs (dependency-state truth, Phase 7 anchor, §4.1.4 flat-normal wording), all re-checked
intact here.
