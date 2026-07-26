# Schmaloogium — Phase 1 verify session review (DESIGN.md §G1.2)

**Document under review:** `docs/phase1/v10/PHASE_1_DOC.md` (2026-07-24)
**Reviewer:** fresh verify session, 2026-07-24. Did not author the document; no author context inherited.
**Read:** DESIGN.md Part I (§G0–§G10) + the Phase 1 spec (Part II); RESEARCH.md §0, §1, §5.1–§5.3,
§6.1, §7.2, §12.2 (plus §4.1, §4.2, §10.1–§10.3, §11, App D.3, App E header — read to check the
doc's own citations); template ground truth (`build.gradle`, `gradle.properties`, `settings.gradle`,
`gradle/scripts/*.gradle`, `gradle/wrapper/gradle-wrapper.properties`, `src/**`,
`.github/workflows/*`, `README.md`, `LICENSE`, `.gitignore`, `git log`/`git status`); MCP
`get_porting_guide("mixin-setup")`, `get_project_template("mixins.json")`,
`get_project_template("checklist")`, `resolve_symbol("func_78471_a")`. No dependency PHASE docs
exist — Phase 1 depends on nothing. Live network checks were made only to re-verify pinned versions
(the one thing the spec orders re-verified).

---

## Verification performed (independent of the doc's characterizations)

**Pins — verified against live sources, all correct.**
`repo.cleanroommc.com/.../cleanroom/maven-metadata.xml` returns `<release>0.6.6-alpha`
(`lastUpdated 20260724133703`), confirming the §4.2.6 pin. `maven.cleanroommc.com` does 301-redirect
to `repo.cleanroommc.com` as claimed. `maven.arcseekers.com` carries kappa builds through
`1.4.26-kappa`; `maven.wagyourtail.xyz/releases` tops out at upstream `1.4.1` with **zero** kappa
versions — the doc's "re-pin trap" note is accurate, not decorative. Template-sourced pins
(Gradle 9.6.1 wrapper + all three workflows, Java 25, MCP `stable`/`39-1.12`,
`sponge-mixin:0.20.13+mixin.0.8.7` compileOnly, Blossom 2.2.0, Shadow 9.5.1, idea-ext 1.4.1, foojay
1.0.0, JUnit Jupiter 6.0.3, lwjglx 1.0.0, template loader pin 0.5.17-alpha) all match the files.

**Template ground truth (§4.1) — spot-checked line by line, no misstatement found.** `main` branch
with no mixin JSON and no `MixinConfigs` attribute; `settings.gradle` has no `include`;
`rootProject.name` derived from the directory; the Unimined block sits at root with an inline
`loader "0.5.17-alpha"`; the AT path hardcodes `rootProject.projectDir`; Blossom on
`sourceSets.main` only with `package = "${root_package}.${mod_id}"`; `contain` copies into `/` with
`ContainedDeps`/`NonModDeps`; `jar` classifier `dev` + `finalizedBy(remap…)`; no `src/test/`;
`.gitignore` already has `**/build/`; lwjglx appears at exactly one `compileOnly` site; all three
workflows hardcode root-relative `build/libs`. The claimed template defects are real:
`modImplementation` is referenced by README and `dependencies.gradle` comments but never declared
(only `modCompileOnly`/`modRuntimeOnly` are, and only those two are remapped);
`extraArgs.split { "\\s+" }` is not `String.split(regex)`; `extra.gradle` documents helper methods
(`assertProperty`, …) that exist nowhere; `publish_to_local_maven` is read by no script.

**Conformance map (§3) — audited against the cited text.** All four §4.1 probes plus the GL-3.0
mipmap gate map correctly to `GLCapabilityProfile` members; the §6.1 rows (compat baseline/`GL_QUADS`,
no UBOs, never compile against `org.lwjglx`) and the §G4.6 row map to real design elements that
enforce rather than merely restate them. The §3.1 flagged delta is **correct and correctly ruled**:
DESIGN.md:614 attributes "extension set" to "the §4.1 probe set", and RESEARCH.md §4.1 lists only
four probes — the doc includes it, sources it to §3.5 instead, and tags it `[A]` rather than
`[V:observed]`. This is exactly the depthtex1-class discipline §G1.2 asks for.

**Quotations checked, all faithful:** RESEARCH.md §5.1 (`MixinConfigs`, deprecated MixinBooter
interfaces, SRG targeting, coremods discouraged), §5.2/§7.2 (backend swap; Kirino README),
§10.1/§10.3 (glsl-transformer AGPL never-copy; platform-not-library; ModularUI jar-in-jar eased under
GPL-3.0-or-later; Fugue GPL-3.0), App E header ("Mixin targets must use SRG name + descriptor"),
template README ("one json per phase (`PRE_INIT`, `DEFAULT`, `MOD`)", "Don't worry about refmap"),
MCP mixin-setup guide (`is_coremod` vestigial-or-required "unconfirmed upstream").

**Scope discipline — clean in both directions.** Every Scope-in bullet is answered, including the
two the spec poses as questions (`contain` for `:engine`: answered no, with a comparison table;
one mixin config per phase: answered yes, with the reason). Nothing from Scope-out is designed: no
harness content, no pack-format work, no GL policy beyond facade shape, no GUI evaluation. The
"What the facade deliberately does NOT contain" paragraph is the right instinct.

**Binding decisions — no contradiction of D-1..D-10.** §11.2 dispositions each of the ten as
satisfied or deferred-with-owner, as the Doc gate requires. No contract-visible component is
"improved" (§G4.2) — Phase 1 owns almost no contract-visible surface, and the facade correctly sits
below the pack vocabulary.

**Template completeness.** All thirteen §G9 sections (0–12) are present and substantive. All four
assigned OQs (2, 12, 20, 21) carry full §G4.4 spike specs — verbatim question, concrete procedure,
success and failure criteria, and a fallback designed now. The OQ-20 `NullGLDevice` drill and the
OQ-21 with/without-shim profile comparison are genuine falsifiable procedures, not restatements.

---

## Findings

### F-1 — The facade has no pixel-transfer verbs, and dependents need them at v0.1 · **correction**

**Location:** §4.7.4 (`TextureService`, `UniformService`, `ShaderService`), surfaced to dependents
via §5.2.

**Claim under test:** §5.2 presents `GLDevice` + the seven services as the interface Phases 4, 5, 6,
7, 8, 13 and 14 build against, and §4.7.4 closes with an explicit list of what the facade "deliberately
does NOT contain" — a list containing only *policy* omissions (formats, unit map, flips, clears,
cadences, backup chains).

**Evidence:** the facade exposes no data-transfer verb in either direction.
- **Readback.** Phase 6 (milestone **v0.1**, consumer of this §5) is assigned "**Synchronous
  center-depth readback design here** (a per-frame `glReadPixels` stall, faithful to reference
  behavior)" — DESIGN.md:978–980, sourced to RESEARCH.md §4.4/§6.2. No service can read a pixel.
- **Texture upload.** Phase 13 must create the noise texture (RESEARCH.md §4.1 step 4; §F.5
  `texture.noise`), the `_n`/`_s` atlases and custom textures. `TextureService` offers
  `create/allocate/setParameters/bindToUnit/generateMipmap/delete` — no way to put texels into a
  `TextureHandle`.
- **Uniform types.** App D.3 lists `atlasSize` and `terrainTextureSize` as `ivec2`; `UniformService`
  has `upload(loc, int)` (scalar) and float vectors only — no integer-vector upload.
- **Geometry programs.** The pack contract includes `.gsh` with
  `#extension GL_ARB_geometry_shader4` + `const int maxVerticesOut` (RESEARCH.md §3.1, App A.3 row,
  §6.2). The ARB form needs pre-link program parameters; `ShaderService` exposes only
  `bindAttributeLocation` for pre-link work.

Each of these is *additive* and the doc does invite additive requests (§5.2's note to Phases 4/5/6,
§11.4's note to Phase 14). The defect is that the omissions are invisible: a dependent reading §5.2
plus the "deliberately does NOT contain" list has every reason to conclude the verb set is complete
for its milestone, and Phase 6 in particular would design a readback against a facade that cannot
express one.

**Fix:** either add the verbs (a pixel-read entry point, a texture-data upload entry point, integer
vector uniform uploads, and a pre-link program-parameter hook or an explicit statement that the ARB
geometry path is translated upstream by Phase 3/4), or state in §4.7.4 that data-transfer verbs are
deliberately deferred and name the phase that requests each. **This changes §5, so per §G1.3 the doc
needs a fresh verify pass before any dependent consumes it.**

### F-2 — Capability-profile fixtures are placed where `:engine` tests cannot read them · **correction**

**Location:** §8.3 item 1 vs §8.1 and §5.2.

**Claim:** "`GLCapabilityProfile` fixtures under `conformance/src/test/resources/profiles/`".

**Evidence:** the profile-consuming tests this phase owns (`GLCapabilityProfileSerializationTest`,
`GLCapabilityProfileDerivationTest`, `RecordingGLDeviceTest`) live in `:engine` (§8.1), and §5.2 tells
Phases 4/5/6 that their "recorded-GL run" impl gates use `RecordingGLDevice` + a `GLCapabilityProfile`
fixture — those phases' subsystems and their headless tests live in `:engine` too (§2.1). Constraint
C-4 fixes the dependency direction as `:conformance → :engine`, never the reverse, so a test resource
in `:conformance` is not on `:engine`'s test classpath. As written, the fixtures are unreachable by
the tests that need them.

**Fix:** put the shared profile fixtures in `:engine`'s test resources (or a small shared
test-fixtures source set that both consume) and leave `:conformance` owning the *set* and the refresh
workflow, which is the split §8.3 intends.

### F-3 — The D-7 disposition and checklist item 1 describe a repo state that does not exist · **correction**

**Location:** §11.2 row D-7 ("the swap was made and then undone, so the task is restore, not
replace"), §12 item 1 ("Restore `LICENSE` … un-stage the deletion"), §11.3 item 2 ("Maintainer
override: Defect details deleted; behavior was intentional").

**Evidence:** `LICENSE` is present at the repo root, tracked, and contains the verbatim GPL-3.0 text
(674 lines); `git log` shows `aa917a6 Update LICENSE from MIT to GPL-V3`; `git status` reports no
staged deletion and no modification (only untracked `RESEARCH.md` and `phase_1_chatlog.md`). §4.8.1's
own statement — LICENSE carries verbatim GPL-3.0 — matches reality; only the two dangling references
do not. §11.3 item 2 now carries a maintainer redaction, leaving a numbered "defect" with no content
inside a list captioned "Defects found in the template … all pre-existing".

**Fix:** reword §12 item 1 to "verify `LICENSE` is the verbatim GPL-3.0 text and that the SPDX
'or-later' grant is stated in `README.md`", drop the "restore / un-stage" framing from §11.2's D-7
row, and remove or renumber §11.3 item 2 so the defect list contains only defects.

### F-4 — §5 does not expose the ModularUI dependency mechanics Phase 12 is told to consume · **correction**

**Location:** §5.1–§5.3.

**Evidence:** Phase 12's Required inputs read "`PHASE_1_DOC.md` (module layout, **ModularUI
dependency mechanics**)" (DESIGN.md:1422), and §G7.5 puts the ModularUI mod-dependency/jar-in-jar
question with Phase 1. The doc does answer it — §4.2.6 (deliberately not pinned; Phase 12 owns the
decision), §4.8.4 (both arrangements, with the obligations each carries), §11.3 item 3 (the missing
`modImplementation` configuration Phase 12 will hit first) — but none of it appears in §5, which is
the section §G5.3 and §G1.1 make the contract a dependent reads. Per §G1.2's interface-honesty
check, a promise to a dependent must be *specified where the dependent looks*.

**Fix:** add a §5.3 row — "mod-dependency declaration mechanics (`modCompileOnly`/`modRuntimeOnly`,
the missing `modImplementation` configuration and its fix, `contain` jar-in-jar and its LGPL-3.0
notice obligations) → consumed by 12" — pointing at §4.2.6/§4.8.4/§11.3. **This is a §5 change; see
the §G1.3 note under F-1.**

### F-5 — The SRG-convention exemplar carries a wrong descriptor · **correction**

**Location:** §4.5.3 code block.

**Claim:** `@Inject(method = "func_78471_a", // renderWorld(FF)V …)`.

**Evidence:** `resolve_symbol("func_78471_a")` (MCP, 1.12.2, MCP stable_39/SRG) returns
`EntityRenderer.renderWorld`, descriptor **`(FJ)V`**, parameters `partialTicks` (float) and
`finishTimeNano` (long). The snippet's own Java parameter list — `(float partialTicks, long
finishTimeNano, CallbackInfo ci)` — agrees with `(FJ)V`, so the comment contradicts the code beside
it. This snippet is the stated convention template for Phases 7/10/13's hook catalog, which is where
a wrong descriptor stops being cosmetic.

**Fix:** `// renderWorld(FJ)V`.

### F-6 — The header under-reports the reading actually done · **note**

**Location:** §0.1, §0.3.

**Evidence:** §4.5.3 quotes RESEARCH.md App E's header, but App E is not in §0.1's list (App H is).
§0.3 item 3 enumerates the deviations as "§4.2 and §7.4 … beyond the assigned §1/§5.1–§5.3/§6.1/
§7.2/§12.2", while §0.1 itself lists §4.1, §7.1, §8.3, §9, §10.1–§10.3, §11 and §12.4 as also read.
§G9's section 0 asks for inputs actually read *and* deviations with reasons; §0.1 is nearly complete
and §0.3 is not, which makes the two tables disagree. Most of the extra reads are plainly justified
(§10 is §G7's source, §11 is required verbatim by §G4.4, §12.4 is named in the spec's inputs) — the
fix is one sentence, not a defence.

### F-7 — Cross-reference errors · **note**

**Location and evidence:**
- §4.1, last table row: CI artifact paths "Breaks under the split — **§4.9**". CI is §4.11; §4.9 is
  logging.
- §4.5.2: "consult the bail registry (**§4.8**)". The bail registry is §4.10; §4.8 is licensing.
- §4.6: "`CapabilityProbe` (**§4.4** of the facade, below)". §4.4 is template conversion;
  `CapabilityProbe` is defined in §4.7.5.

### F-8 — Two small gaps in otherwise-complete tables · **note**

- §4.4.1's `gradle.properties` conversion table omits **`enable_mixin_debug`**, which §4.5.5
  introduces and §12 item 32 depends on. Every other new property (`cleanroom_loader_version`,
  `mixin_configs`) is listed there.
- §4.2.6's ASM row reads "to be pinned at implementation time" and §4.2.3's snippet shows
  `'org.ow2.asm:asm:<pinned>'`, immediately under the section's own "**Nothing floats**" claim and
  against a Doc gate that asks for a *complete* pin table. It is test-scope only, so this is minor —
  but pinning it now costs one lookup and keeps the invariant literally true.

### F-9 — Milestone-tag hazard on the mixin config plugin · **note**

**Location:** §4.5.2 (the MOD config "Carries the config plugin"), §9 (`SchmaloogiumMixinPlugin` slot
→ `v0.3`), §12 items 30 (`v0.1`, three JSONs) and 37 (`v0.3`, plugin skeleton).

**Evidence:** if the v0.1 `schmaloogium.mod.mixin.json` declares a `plugin` key naming a class that
does not exist until v0.3, config load fails at runtime. The common-fields snippet has no `plugin`
key, so the intent is probably correct — but the tag table and the prose read as if the declaration
ships at v0.1. One clarifying clause ("the `plugin` key lands with the class, at v0.3") closes it.

### F-10 — C-4 is the one constraint left to inspection · **note**

**Location:** §8.2.

**Evidence:** the Doc gate asks for "dependency rules as **testable** constraints"; C-1, C-2 and C-3
each get a named test, C-4 is "left as inspection for v0.1" with the mechanical form described but
not adopted. The mechanical check the doc already sketches (assert `:conformance`'s configurations
contain no `project(':mod')`) is a handful of lines and would make the set uniform.

### F-11 — §3 omits a row for the other day-one debug affordance · **note**

**Location:** §3 conformance map.

**Evidence:** §G4.5 reserves two debug affordances from day one — `-Dschmaloogium.debug.saveSources`
*and* KHR_debug labels/groups in dev. The first has a mapped row; the second is designed
(`DebugService` §4.7.4, `schmaloogium.debug.glLabels` §4.9.3, tagged `v0.5` in §9) but has no row.
Adding it costs one line and keeps "zero unmapped in-scope rows" true by construction rather than by
argument.

### F-12 — The jar-merge mechanism reaches across projects at configuration time · **note**

**Location:** §4.2.4 / §4.2.5, `jar { from project(':engine').sourceSets.main.output }`.

**Evidence:** this is cross-project model access at configuration time — the pattern Gradle's
configuration cache and project isolation flag, and the packaging step the Impl gate (item 7:
"`:mod:jar` produces a jar containing `com/schmaloogium/engine/**`") depends on. The decision itself
(merge, not `contain`, not shadow) is well argued and I would not disturb it; only the expression is
fragile. A dependency-derived form (e.g. consuming `:engine`'s artifact through a configuration, or
`project(':engine').tasks.named('jar')`) achieves the same merge without the cross-project reach.
Worth a line in §12 item 7 so the implementation session does not discover it under a Gradle upgrade.

---

## Verdict

**PASS-WITH-CORRECTIONS**

The document meets the Doc gate: the module and package layout is finalized, the seam is stated as
constraints C-1..C-4 with three enforcement layers and named tests behind three of them, every
D-1..D-10 is dispositioned with an owner, and the pin table is complete with a re-pin procedure that
I re-verified independently against live sources and found correct in every particular, including the
non-obvious kappa-fork trap. The conformance map's one contradiction between DESIGN.md and
RESEARCH.md was found by the author, ruled explicitly, provenance-downgraded and escalated upstream —
which is the behavior §G1.2 exists to confirm. All four OQs carry real §G4.4 spikes with pre-designed
fallbacks. Nothing structural is missing; no rebuild is warranted, so FAIL would be wrong.

The corrections are F-1 through F-5, with F-1 the substantive one: the facade's verb set is
incomplete for consumers that arrive as early as v0.1 (Phase 6's synchronous center-depth readback),
and the omission is not disclosed where a dependent would see it.

**§G1.3 note for the fix-up session:** F-1 and F-4 alter §5 (cross-phase interfaces). Under §G1.3's
"re-verify only if §5 changed" rule, this doc must go through a fresh verify session after the
fix-up, before Phase 2 or Phase 3 reads it. F-2, F-3, F-5 and the notes do not touch §5.

---

## Resolutions

Fix-up session (§G1.3), 2026-07-24 — a fresh session that neither authored nor reviewed the doc.
`PHASE_1_DOC.md` was edited; the findings and the verdict above are unchanged. All twelve findings
are **applied**; none declined. `RESEARCH.md` and `DESIGN.md` were not touched — the requested
upstream changes remain in PHASE_1_DOC §11.5.

- **F-1 — applied (the design work).** §4.7.4 gains three pixel-transfer verbs, all policy-free:
  `FramebufferService.readDepthPixel(f,x,y)` (synchronous, stalling, per RESEARCH.md §4.4/§6.2),
  `TextureService.upload(TextureHandle, TextureData)` (value object: region, mip level, `PixelLayout`
  from the same vocabulary as `TextureSpec`, and a JDK `ByteBuffer` — never an LWJGL buffer type), and
  `UniformService.upload(loc,int,int)` for App D.3's `ivec2`. The ARB geometry path is resolved the
  second way the finding allows: a new design rule states that §6.2's adopted internal translation
  makes `GL_ARB_geometry_shader4` + `maxVerticesOut` a source-level rewrite upstream of the facade
  (Phase 3 front-end / Phase 4 compile path), so no pre-link program-parameter verb exists — with the
  additive escape route named for Phase 4. The closing "deliberately does NOT contain" paragraph is
  split into *policy* omissions and a table of *deliberately deferred data-transfer verbs*, each with
  the phase expected to request it (async/PBO readback → 14, general color readback → 14, texture
  readback → 13, `ivec3`/`ivec4`/`mat3` → additive, pre-link parameters → 4). Supporting edits:
  §4.7.5 (`ScriptedResponses.depthPixel`, plus "bulk data logged by summary, never content" so
  `render()` stays golden-file-stable), §3 (four provenance rows), §5.2 (two rows + a note to Phase 6
  naming the verb and its scripted response), §9 (a `v0.1` row), §11.1 (`D-P1-25`), §11.4 (Phase 3 and
  Phase 14 hand-offs), §12 items 19 and 20. Facade shape preserved: grouped role services, opaque
  handles, no GL constant in any signature, no policy.
- **F-2 — applied.** Fixtures move to `:engine`'s `testFixtures` source set
  (`engine/src/testFixtures/resources/profiles/`), consumed by `:conformance` and `:mod` via
  `testImplementation testFixtures(project(':engine'))` — a dependency edge in C-4's legal direction
  rather than a path across modules, and `:engine`'s own tests get it automatically. Edited: §8.3
  item 1 (with the reachability argument spelled out), §4.7.2, §4.2.3 (`java-test-fixtures`), §2.1's
  tree, §5.2's note to Phases 4/5/6, §12 items 5/6/7/17, and `D-P1-26`. Phase 2 still owns the fixture
  *set* and refresh workflow.
- **F-3 — applied.** §12 item 1 is now a verification step ("verify `LICENSE` is the verbatim GPL-3.0
  text … and the SPDX 'or-later' grant is stated in `README.md`"); §11.2's D-7 row states the repo
  fact (present, tracked, verbatim GPL-3.0, `[V:repo]`) and drops the restore/un-stage framing;
  §11.3's redacted item 2 is removed and items 3–10 renumbered 2–9, with every reference updated
  (§11.4 Phase 12, §11.4 Phase 7 twice, §12 item 33). No repo action was taken or is implied.
- **F-4 — applied.** New §5.3 row: mod-dependency declaration mechanics — `modCompileOnly`/
  `modRuntimeOnly`, the missing `modImplementation` configuration and its fix (§12 item 43), and
  `contain` jar-in-jar with its LGPL-3.0 notice obligations — pointing at §4.2.6/§4.8.4/§11.3 item 2,
  consumed by 12. §11.4's Phase 12 hand-off now points at that row.
- **F-5 — applied.** §4.5.3: `// renderWorld(FF)V` → `// renderWorld(FJ)V`, matching the snippet's own
  `(float, long)` parameter list.
- **F-6 — applied.** §0.1 gains App E (header only). §0.3 item 3 is rewritten as the complete list of
  extra RESEARCH.md reads with a one-clause reason each (§4.1, §4.2, §7.1, §7.4, §8.3, §9,
  §10.1–§10.3, §11, §12.4, App E, App H), so it agrees with §0.1. §0.3 item 4 no longer points at a
  removed §11.3 entry: the git inspection is recorded as confirming §4.8.1. A new §0.4 records the
  fix-up session, the inputs it read for F-1, and the §G1.3 re-verify obligation.
- **F-7 — applied.** §4.1 last row `§4.9` → **§4.11**; §4.5.2 bail registry `§4.8` → **§4.10**; §4.6
  `CapabilityProbe (§4.4 of the facade)` → **§4.7.5**. Disclosed beyond the finding's letter: two more
  instances of the same class were found while fixing these and corrected — §3's `saveSources` row
  cited "§4.7's flag namespace" (flags are §4.9.3), and §11.3's refmap item cited "§12 item 22" (the
  refmap check is item 33). Both are one-token fixes of the same kind; nothing else was hunted for.
- **F-8 — applied, both halves.** §4.4.1 gains the `enable_mixin_debug` row (absent → `true`, CI sets
  `false`, §4.5.5, §12 item 32). ASM is pinned to **`9.10.1`** in §4.2.6 and §4.2.3; the pin table
  states its provenance honestly — a Maven Central GAV query on 2026-07-24, **single-source**
  (`repo1.maven.org` metadata returned 403), test-scope only, and inside the re-pin procedure's scope
  like every other row. "Nothing floats" is now literally true.
- **F-9 — applied.** §4.5.2's MOD-config row and a following paragraph state that the `plugin` key is
  written at `v0.3` **with** the class, never at v0.1 (a `plugin` naming a missing class fails config
  load); §9's slot row carries the same clause; §12 item 30 says "no `plugin` key", item 37 ships key
  and class in one change.
- **F-10 — applied.** C-4 becomes mechanical in the C-1 pattern: §8.2 specifies
  `SeamConformanceDependencyTest` (classpath assertion + `com.schmaloogium.mod.` bytecode scan, fed by
  system properties from `conformance/build.gradle`); §8.1 gains its row, §4.3's C-4 statement names
  it, §9 merges the two seam rows into "C-1, C-2, C-3, C-4", and §12 adds it as item **14b** —
  lettered rather than renumbered because items 15+ are referenced by number, including the Impl gate.
- **F-11 — applied.** §3 gains the KHR_debug row: §G4.5's second day-one affordance → `DebugService`
  (§4.7.4) + `schmaloogium.debug.glLabels` (§4.9.3), interface at v0.1, implementation `v0.5`/Phase 14.
- **F-12 — applied.** The merge decision is untouched; §4.2.5 gains a caveat on the *expression* and
  §12 item 7 now instructs the implementation session to use a dependency-derived form (a consumable
  configuration, or `project(':engine').tasks.named('jar')`) instead of
  `project(':engine').sourceSets.main.output`, with the configuration-cache / project-isolation reason
  stated and a test hook that mentions the warning.

**§G1.3 status.** §5 changed (F-1's two rows and the Phase 6 note, F-4's row, and F-2's
fixture-source sentence inside §5.2). Per §G1.3's "re-verify only if §5 changed" rule,
`PHASE_1_DOC.md` must pass a **fresh verify session** before Phase 2, Phase 3, or any other dependent
consumes it; until then it is not a valid dependency input (§G5.3). The same note is recorded in the
doc itself at §0.4 and in its closing line.
