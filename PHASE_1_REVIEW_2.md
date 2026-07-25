# Schmaloogium — Phase 1 re-verify session review (DESIGN.md §G1.2, second round)

**Document under review:** `Schmaloogium/PHASE_1_DOC.md` as it now stands (build 2026-07-24, fix-up
2026-07-24 applying `PHASE_1_REVIEW.md` F-1 … F-12).
**Why this session exists:** the fix-up altered §5 (cross-phase interfaces), so §G1.3's "re-verify
only if §5 changed" rule requires a fresh verify pass before Phase 2, Phase 3 or any other dependent
consumes the doc. The verdict below is on the **whole document as it now stands**, not on the diff.
**Reviewer:** fresh session, 2026-07-24. Did not author the doc, did not review it in round one, did
not apply the fix-up, and inherited no context from any of those sessions.
**Deliverable filename:** `PHASE_1_REVIEW_2.md` rather than an append to `PHASE_1_REVIEW.md`, so
round one's findings, verdict and `## Resolutions` stay intact as the record of that round
(§G1.3 speaks of the *latest* review verdict; §G1.2 names only the first file).

---

## 0. What I read, and in what order

**Assigned reading, read in full and first:**

- `DESIGN.md` Part I §G0–§G10 (lines 1–575) and the Phase 1 spec in Part II (lines 585–658);
  other phases from §G5.1's table only (title / milestone / depends-on / OQ columns).
- `RESEARCH.md` §0 and §1; then the Phase 1 spec's Required inputs: §5.1–§5.3, §6.1, §7.2, §12.2.
- Template ground truth, complete: `build.gradle`, `gradle.properties`, `settings.gradle`,
  `gradle/scripts/{dependencies,extra,publishing}.gradle`, `gradle/wrapper/gradle-wrapper.properties`,
  all eight files under `src/**`, all three `.github/workflows/*`, `README.md`.
- Skim of `cleanroom-src/src/main/java/com/cleanroommc/` (directory layout + file list, 52 files;
  one grep for `MixinConfigs`, which lands only in the deprecated MixinBooter-API re-implementation
  `cleanmix/CleanMixHooks.java` — consistent with RESEARCH.md §5.1's "legacy interfaces deprecated").
- MCP `cleanroom` per §12.4: `get_porting_guide("mixin-setup")`,
  `get_project_template("mixins.json")`, `explain_concept("mcmod.info", loader="cleanroom")`,
  `resolve_symbol("func_78471_a")`.
- `PHASE_1_DOC.md` in full.
- **Last, after my own findings were fixed:** `PHASE_1_REVIEW.md` — round one's findings, verdict and
  the fix-up session's `## Resolutions`, read only to audit whether each claimed resolution is real
  and whether any of them introduced a new defect.

**Read beyond the assigned list, each with its reason** (§G1.1/§G1.2 recording requirement):

| Extra input | Why |
|---|---|
| RESEARCH.md §3.1, §3.2, §3.4, §3.5 | The doc's §3 map and §4.7.4 cite them for `.gsh`/ARB geometry, implicit resource declaration, the uniform/sampler contract and the `MC_<GL_extension>` macro requirement. The §3.1 flagged delta cannot be judged without §3.5. |
| RESEARCH.md §4.1–§4.8 | §4.1 (probe set, noise texture), §4.2 (state barrier, location caching), §4.3 (framebuffer/final behavior), §4.4 (composite pass state, center-depth readback) and §4.8 (Keep/Adapt/Skip, the ARB row) are all cited by the doc; §4.3/§4.4 are also the only way to test whether the facade's verb set is *sufficient*. |
| RESEARCH.md §6.2 | The ARB-geometry claim rests on it, and §6's preamble governs how §6.2 rows may be used. |
| RESEARCH.md §8.1–§8.3, §9, §10.1–§10.3, §11, §12.4, §12.5 | Cited by the doc's §8, §9, §4.8, §10 and §11.5; §11 carries the four OQ rows the spikes must quote verbatim. |
| RESEARCH.md App A.2/A.3, App B.1–B.5, App C (headers), App D (all four sub-tables), App E header, App F.1/F.3/F.5/F.6/F.7/F.8, App G (first rows) | Every appendix row the doc cites, plus App D in full and App F.1/F.7 — the only way to check the facade against the *whole* built-in uniform and per-program-state contract rather than against the rows the doc chose to cite. This is where finding V2-1 came from. |
| Repository state: `LICENSE` (full), `git log`, `git status`, `.gitignore` | The doc makes repo-state claims (§11.2 D-7, §12 item 1, §4.1's `.gitignore` row, §0.3 item 4). Read-only inspection only. |
| Live network: `repo.cleanroommc.com` maven-metadata, `maven.arcseekers.com` maven-metadata, `maven.wagyourtail.xyz` maven-metadata, `search.maven.org` GAV query, `repo1.maven.org` metadata | The Phase 1 spec *orders* re-verification of the pins, and this session's brief additionally orders the ASM row re-verified. |

**Deliberately not read:** `phase_1_chatlog.md` (not an input to either session type); other phases'
specs in Part II. One incidental exception, disclosed: my single read of DESIGN.md lines 1–700 to
capture Part I also covered the first ~40 lines of the Phase 2 spec (lines 662–700). I used it only
to check §5.2's "explicit note to Phase 2" against Phase 2's own declared scope; every other
statement below about another phase is sourced to §G5.1's table or §G10's OQ-owner table.

**Hard rules observed:** no code, no builds, no tests, no review/adversarial sub-agents, no fixes.
`PHASE_1_DOC.md`, `RESEARCH.md`, `DESIGN.md` and the existing content of `PHASE_1_REVIEW.md` are
unmodified.

---

## 1. Verification performed, independent of the doc's characterizations

**Pins — all three the brief orders re-verified are correct, and the ASM row can be upgraded.**

| Pin | Independent check (2026-07-24) | Result |
|---|---|---|
| Cleanroom loader `0.6.6-alpha` | `repo.cleanroommc.com/releases/com/cleanroommc/cleanroom/maven-metadata.xml` → `<release>0.6.6-alpha`, `<latest>0.6.6-alpha`; tail of `<versions>` is `0.6.1 … 0.6.6-alpha` | ✅ correct |
| Unimined kappa `1.4.26-kappa` | `maven.arcseekers.com/releases/…/xyz.wagyourtail.unimined.gradle.plugin/maven-metadata.xml` → `<release>` and `<latest>` both `1.4.26-kappa`; versions run `1.4.19-kappa … 1.4.26-kappa` | ✅ correct |
| The "re-pin trap" | `maven.wagyourtail.xyz/releases/…` → `<release>1.4.1`, **no** version string containing `kappa` | ✅ the trap note is accurate, not decorative |
| ASM `9.10.1` (test scope) | `repo1.maven.org` metadata returned **403** for me too — the doc's caveat is honest. Second-sourced instead via the Maven Central search API (`search.maven.org/solrsearch`, `g:org.ow2.asm a:asm`, gav core): newest is **9.10.1**, ahead of 9.10 / 9.9.1 / 9.9 / 9.8 | ✅ correct, and **no longer single-source** — §4.2.6's caveat can be relaxed to "second-sourced 2026-07-24 (Central search API); `repo1.maven.org` metadata refuses direct requests" |

**Repository claims — all true.** `LICENSE` exists at the repo root, is tracked, and is the
**verbatim, unmodified GPL-3.0 text** (674 lines, FSF preamble intact, "How to Apply" placeholders
`<one line to give the program's name…>` / `<year>` / `<name of author>` still unfilled — i.e. the
license document, not a filled-in notice, which is exactly what `[D-P1-17]` describes). `git log`
shows `aa917a6 Update LICENSE from MIT to GPL-V3`. `git status` shows no staged deletion: modified
`PHASE_1_DOC.md` / `PHASE_1_REVIEW.md`, untracked `RESEARCH.md` / `phase_1_chatlog.md`. `.gitignore`
does contain `**/build/`. §11.2's D-7 row and §12 item 1 therefore describe the repo accurately, and
no dangling "restore the LICENSE" reference survives anywhere in the doc (the only remaining "MIT"
occurrences are the OQ-12 discussion, Angelica's MIT portions, and the D-7 row's quotation of the
decision text).

**Template ground truth (§4.1) — re-checked line by line against the files, no misstatement found.**
`main` branch with no mixin JSON and no `MixinConfigs` attribute; `settings.gradle` has no `include`
and derives `rootProject.name` from the directory; the Unimined block sits at root with inline
`loader "0.5.17-alpha"`; the AT wiring hardcodes `${rootProject.projectDir}`; Blossom on
`sourceSets.main` only with `package = "${root_package}.${mod_id}"`; `jar` classifier `dev` and
`finalizedBy(remapJar)`; `contain` copies into `/` with `ContainedDeps`/`NonModDeps`; no `src/test/`;
lwjglx appears at exactly one `compileOnly` site; all three workflows pin Gradle 9.6.1 / Temurin 25
and hardcode root-relative `build/libs`. The four claimed template defects are all real and I
reproduced each: `modImplementation` appears only in a `dependencies.gradle` comment and the README
while only `modCompileOnly`/`modRuntimeOnly` are declared and remapped; `extraArgs.split { "\\s+" }`
is Groovy's closure-partitioning `split`, not `String.split(regex)`; `extra.gradle` documents
`assertProperty`/`assertSubProperties`/`setDefaultProperty` which exist nowhere in `build.gradle`;
`publish_to_local_maven` is read by no script.

**MCP claims — verbatim-faithful.** `get_porting_guide("mixin-setup")` confirms, in its own words,
every `[V:mcp]` assertion in §4.5: the `MixinConfigs` comma-separated manifest attribute; that
`IEarlyMixinLoader`, `ILateMixinLoader`, `IMixinConfigHijacker` and `@MixinLoader` are all
`@Deprecated` ("do not use them for new mods"); PRE_INIT/DEFAULT/MOD phase targeting; "Refmaps are
handled by Unimined at build"; SRG targeting through `Srg2McpRemapper` (dev) and an
`FMLDeobfuscatingRemapper` wrapper (production); `crl.dev.mixin`; `-Dmixin.debug.export=true` and
`-Dmixin.checks.interfaces=true` in the template's client run; the mixin branch's `is_coremod=true`
with an **empty** `IFMLLoadingPlugin` being "unconfirmed upstream"; and "Never remove `final` via a
mixin — use the AT." `get_project_template("mixins.json")` returns exactly the field set §4.5.2
reproduces (`required`, `package`, `compatibilityLevel: JAVA_8`, `target: @env(DEFAULT)`, `client`,
`mixins`, `server`, `setSourceFile`, `minVersion: 0.8.7`). `resolve_symbol("func_78471_a")` returns
`EntityRenderer.renderWorld`, descriptor **`(FJ)V`**, parameters `partialTicks`/`finishTimeNano` —
so round one's F-5 fix is correct as applied.

**Conformance map (§3) — audited against the cited text, one hole (V2-1) and one loose citation
(V2-7).** All four §4.1 probes and the GL-3.0 mipmap gate map faithfully; the §6.1 rows (compat
baseline / `GL_QUADS`, no UBOs, never compile against `org.lwjglx`) map to design elements that
*enforce* rather than restate; the §3.1 flagged delta is correctly found, correctly ruled, and
correctly downgraded to `[A]` (DESIGN.md line 614 does attribute "extension set" to "the §4.1 probe
set", RESEARCH.md §4.1 lists four probes, and §3.5's on-demand `MC_<GL_extension>` macros are the
real source — I checked all three). The three fix-up rows (`centerDepthSmooth` → §3.2 / App A.3 /
§4.4; noise + custom textures → §4.1 step 4 / App F.5; `ivec2` → App D) each check out against the
cited text. What the map does not have is a row for the one built-in uniform whose type the facade
cannot express — see V2-1.

**Interface honesty, both directions.** Phase 1 consumes nothing (correct — no dependencies).
Everything §5 promises exists in §4 with real signatures, with the exceptions recorded in V2-2/V2-3;
everything §4 specifies that a dependent needs is surfaced in §5, with the exception recorded in
V2-3 (Phase 3's ARB obligation) and the smaller one in V2-5.

**Scope discipline — clean in both directions.** Every Scope-in bullet is answered, including the
three the spec poses as questions (ATs for v0.1: no, with the re-enable path recorded; `contain` for
`:engine`: no, with a comparison table; one mixin config per phase: yes, with the reason). Nothing
from Scope-out is designed: no harness content (the `:conformance` module and CI job are explicitly
empty slots), no pack-format work, no GL policy beyond facade shape, no GUI-framework evaluation
(§4.8.4 is the licensing question OQ-12 assigns here, and it explicitly leaves fitness to Phase 12).

**Template completeness.** All thirteen §G9 sections (0–12) present and substantive. All four
assigned OQs carry full §G4.4 spikes; I checked each quoted question against RESEARCH.md §11 lines
1007 (OQ-2), 1017 (OQ-12), 1025 (OQ-20) and 1026 (OQ-21) — all verbatim. The OQ-20 `NullGLDevice`
drill and the OQ-21 with/without-shim profile comparison are falsifiable procedures with pre-designed
fallbacks, not restatements.

**Binding decisions.** No D-1..D-10 contradicted; §11.2 dispositions all ten with an owner named for
each deferral (D-3→2, D-4→4, D-5→7, D-9→5+/7, D-10→2). No contract-visible component is "improved" in
the §G4.2 sense — with one item that needs its provenance corrected rather than its design changed
(V2-3).

**The fixture move (§8.3 / §4.2.3 / §2.1) — mechanically correct.** Core Gradle's
`java-test-fixtures` creates a `testFixtures` source set whose output the project's own `test` source
set consumes implicitly, so `:engine`'s `GLCapabilityProfileSerializationTest` /
`…DerivationTest` / `RecordingGLDeviceTest` (§8.1) do get `engine/src/testFixtures/resources/profiles/`
on their test runtime classpath. `testImplementation testFixtures(project(':engine'))` is the
documented consumption idiom and puts the fixtures on `:conformance`'s and `:mod`'s test compile and
runtime classpaths — a `:conformance → :engine` edge, i.e. **C-4's legal direction**, so no violation.
It does not weaken C-1 or the §4.3 assertions: `testFixtures` is a distinct source set, so nothing it
carries appears on `sourceSets.main.compileClasspath` / `runtimeClasspath` / `output.classesDirs`,
which are the three things §4.2.3 injects and §4.3 asserts over. The plugin adds no production
dependency and composes with the `java-library` plugin the root `subprojects {}` block applies. The
placement argument in §8.3 ("a resource in `:conformance` is not on `:engine`'s test classpath") is
correct, and the split of ownership (Phase 1 owns format + capture, Phase 2 owns the set + refresh)
survives the move.

**C-4 made mechanical (§8.2 / §12 item 14b) — the test can fail on a real violation.** Adding
`project(':mod')` to `:conformance` puts either `mod/build/classes/java/main` or
`mod/build/libs/…jar` on the resolved classpath; both contain the `mod/build` path segment the test
looks for, so the classpath half fires, and the `com.schmaloogium.mod.` bytecode half fires
independently on any actual usage. One dead sub-pattern is noted in V2-6. The `14b` numbering is
sound in practice — see V2-9, where I re-checked every surviving cross-reference.

---

## 2. Findings

### V2-1 — §4.7.4 states a falsehood about the pack contract, and the facade cannot upload `blendFunc` · **blocking**

**Location:** §4.7.4's "deliberately deferred verbs" table, row *`ivec3` / `ivec4` / `mat3` uniform
uploads*; `UniformService` in the same section; §3's conformance map; surfaced to dependents by
§5.2's "facade's stated non-verbs" row.

**Claim under test:** "The 1.12.2 contract declares none — App D.3's integer uniforms are all
`ivec2`/`int`, and custom uniforms are `float/int/bool/vec2/vec3/vec4` (App F.6)", with the requester
column left as "whichever phase meets the first one; additive".

**Evidence:** RESEARCH.md **App D.4** — the fourth sub-table of the same appendix — declares

> `blendFunc` | **ivec4** | current blend srcRGB, dstRGB, srcA, dstA

`blendFunc` is not an obscure row: RESEARCH.md §3.4 item 2 names it in the built-in uniform
inventory, and DESIGN.md §G4.6 names it as the canonical example of state we observe from
`GlStateManager` ("we *observe* its state (e.g. `blendFunc` uniform)"). Its owner is Phase 6 (§G5.1:
"App D inventory, cadences, smoothing, unit map, value providers", milestone **v0.1**) with Phase 9
owning per-draw dynamics at v0.3 — either way, inside the current phase set and no later than v0.3.
`UniformService` offers `upload(loc,int)` and `upload(loc,int,int)`; there is no four-integer
overload, and an `ivec4` cannot be uploaded through `glUniform1i`-shaped or `glUniform2i`-shaped
verbs. The claim's scope-narrowing to "App D.3's integer uniforms" is precisely what hides the
counterexample, which lives in App D.4. Correspondingly, §3's map has a row for the `ivec2` uniforms
and **no row for `blendFunc`/`ivec4`** — an unmapped in-scope contract row by the same standard that
put the `ivec2` row there.

This is the F-1 failure mode recurring one row down: a dependent reading §5.2 plus §4.7.4's
now-explicit "these are the verbs that are absent on purpose" table is told, in terms, that its
missing verb reflects a contract that does not exist. Round one's F-1 evidence made the same
narrowing (it cited only App D.3), and the fix-up inherited it.

**Fix (small):** add `void upload(UniformLocation loc, int x, int y, int z, int w);` to
`UniformService`, add the matching §3 row (`blendFunc` is `ivec4` → RESEARCH.md App D.4, §3.4;
provenance `[V:doc]`), replace the deferred-row justification with the true one (`ivec3`/`mat3` have
no contract consumer; `ivec4` does, and it is served), and name the owner (6/9) instead of "whichever
phase". **Touches §5.**

### V2-2 — `StateService` cannot express three states the v0.1 pass path needs, and the deferred-verbs table does not cover them · **correction**

**Location:** §4.7.4 (`StateService`, `DrawService`, and the closing two-part "what the facade does
NOT contain" split); §5.2 exposes the service set to Phases 4/5/6/7/8/13/14 as the interface they
build against.

**Claim under test:** "`StateService` is deliberately narrow. It exposes only what §G4.6 says we
perturb — viewport, clears, depth mask, blend, alpha test — plus explicit `snapshot`/`restore`. …
The narrowness is the enforcement: you cannot misuse an entry point that does not exist."

**Evidence:** §G4.6 does not enumerate perturbed state; the list is the doc's own, and three states
that the reference behavior requires are outside it, none of them appearing in either the *policy*
list or the *deferred data-transfer verbs* table:

1. **Depth test.** RESEARCH.md §4.4: "Composite passes draw one fullscreen quad … under an identity
   ortho, **fog/depth/blend disabled**". Composite and final are **v0.1** (RESEARCH.md §9).
   `depthMask(boolean)` controls depth *writes*, not the depth *test*. Either `StateService` needs
   the verb, or `DrawService.fullscreenQuad()` must be specified as establishing the whole composite
   draw state (it is currently specified only as "the backend picks `GL_QUADS` or the triangle-strip
   fallback") — and if the latter, then `blend`/`alphaTest` being separately exposed is confusing,
   because those are part of the same state block.
2. **Face culling.** App F.1 makes `backFace.solid` / `.cutout` / `.cutoutMipped` / `.translucent`
   pack-settable engine flags — contract-visible, parsed at v0.1, applied when gbuffers terrain
   renders at v0.1. Nothing in the facade can turn face culling on or off. `alphaTest.<prog>` and
   `blend.<prog>` (App F.7) got verbs; the F.1 backface flags did not, and the doc does not say who
   applies them.
3. **Colour mask.** RESEARCH.md §4.3: "**Final** renders to the vanilla framebuffer (anaglyph-aware
   colour masking)." Lower stakes than the first two — a phase may reasonably decide anaglyph is out
   of scope — but that decision is not recorded anywhere, so today it reads as an oversight.

The point is not that Phase 5/6/7 cannot ask for these additively; it is that §4.7.4's closing
tables exist precisely so a dependent can tell *gap* from *decision*, and for these three it cannot.

**Fix:** add the verbs, or add rows to the deferred table with the owning phase and the reason, or
(for the composite state block) specify `DrawService.fullscreenQuad()` as state-establishing. Any of
the three closes it. **Touches §5**, since §5.2 exposes the service set as the built-against contract.

### V2-3 — The ARB-geometry rule over-claims its provenance, and the obligation it creates for Phase 3 is not in §5 · **correction**

**Location:** §3's map row "Geometry programs may declare the **ARB form**" (tagged `[V:doc]`);
§4.7.4's "No pre-link program-parameter hook, and that is a statement rather than an omission";
`[D-P1-25]`; §5.2's non-verbs row; §11.4's note to Phase 3.

**Claim under test:** "RESEARCH.md §6.2 records the modernization this project adopts: core GL 3.2
geometry shaders **with internal translation** … The ARB form is therefore handled *upstream of the
facade*, as a source-level rewrite in the Phase 3 front-end / Phase 4 compile path", provenance
`[V:doc]`.

**Evidence, three parts.**

1. **§6.2 is a modernization-*opportunity* ledger, not a commitment, and its `[V:doc]` tag is on the
   other half of the row.** The §6 preamble says so in terms: "Highest-density `[U]` zone in this
   document: most items originate from AI reasoning in the idea doc. Each carries a risk note; **none
   should be promoted into the design doc without its OQ row being resolved or the claim
   spot-checked**." The geometry row's risk-note text is "Packs still declare the ARB extension +
   `maxVerticesOut`; the preprocessor must keep accepting both forms `[V:doc]`" — the `[V:doc]`
   certifies the *requirement on us*, not the *feasibility of internal translation*. The translation
   half is a §6.2 opportunity with no OQ row of its own, which puts it squarely inside **OQ-22**
   ("catch-all for … the §6.2/§6.3 modernization claims without their own row … spot-check each at
   the milestone that touches it"), owned by **P14** per §G10. So the doc tags as `[V:doc]` a claim
   that RESEARCH.md classifies as unspot-checked, and resolves it on the authority of the phase
   ledger that is scheduled to check it at v0.5.
2. **The milestone that first compiles a `.gsh` is v0.1.** RESEARCH.md §3.1 makes `.gsh` part of the
   program set and §9's v0.1 row scopes "gbuffers + composite + final; program registry w/ backup
   chains; preprocessor + macros + includes" with no geometry-shader carve-out. If the translation is
   not built by then, the facade cannot express the ARB path at all at the milestone that needs it,
   and the escape route runs Phase 4 → additive request → Phase 1 fix-up → another §5 re-verify,
   mid-v0.1. The doc calls the escape route "additive … not a redesign", which is true of the
   *facade*, but understates the schedule cost and does not flag any v0.1 risk.
3. **The translation is not a pure text rewrite, and the escape verb is not GL-constant-free.** Under
   `GL_ARB_geometry_shader4` the input and output primitive types are set through
   `glProgramParameteriARB` (`GEOMETRY_INPUT_TYPE`/`GEOMETRY_OUTPUT_TYPE`) — they are *not* in the
   source, whereas core GL 3.2 requires them as `layout(…) in;` / `layout(…, max_vertices = N) out;`.
   A front-end rewrite must therefore synthesize qualifiers it cannot read from the pack, on an
   assumption about what the reference engine passed. And should Phase 4 need the escape verb after
   all, its natural signature carries primitive-type constants, which §4.7.4's own "no GL constants
   appear in any signature" rule forbids without new engine enums. Both facts make "translation
   proves insufficient" a likelier branch than the text implies.
4. **The obligation is not where the obligated phase looks.** §5's own preamble says "per §G5.3 a
   dependent phase reads this section, not the rest of the document." Phase 3 is the phase told to
   own the rewrite — and §5 contains no row placing that obligation on it. It appears only in §11.4's
   hand-off prose and in §4.7.4. §5.2's non-verbs row names Phase **4** ("only if the ARB geometry
   form is not translated upstream") and never Phase 3.

**Fix:** re-tag the §3 row's provenance (the requirement is `[V:doc]`; the adopted translation is
`[A]` / `[Q:OQ-22]`), state the v0.1 exposure in one clause, and add a §5 row exposing the
translation obligation to Phase 3 as a flagged assumption it may contest. The design decision itself
— no pre-link program-parameter verb — is defensible and I would not disturb it. **Touches §5.**

### V2-4 — Two specified build files cannot work as written · **correction**

**Location:** §4.2.2 (root aggregator), §12 item 6 (`conformance/build.gradle`), §4.2.3 vs §8.2
(system-property injection), §8.1/§12 items 13–14, §2.1's tree.

**Evidence:**

1. **`:conformance` has no repository.** Repositories are per-project in Gradle. §4.2.3 gives
   `:engine` its own `repositories { mavenCentral() }`; `:mod` inherits one by applying
   `gradle/scripts/dependencies.gradle`; the root `subprojects {}` block in §4.2.2 declares
   **none** while adding `testImplementation 'org.junit.jupiter:junit-jupiter:6.0.3'` to every
   subproject; and §12 item 6's contents list for `conformance/build.gradle` (`java-library`, JUnit,
   `implementation project(':engine')`, `testImplementation testFixtures(project(':engine'))`,
   classpath system properties) has no repositories block either. As specified, `:conformance` cannot
   resolve JUnit. One line in either place fixes it — but the doc is the spec the implementation
   session works from, and item 6's test hook (`./gradlew :conformance:compileJava`) would not even
   surface it.
2. **`:mod`'s two bytecode scans have no specified way to find the classes.** §4.3's half-two
   mechanism and §8.1's `SeamInternalsTest` (C-2) and `SeamLwjglConfinementTest` (C-3) both "scan
   `:mod`'s compiled classes", but the `schmaloogium.test.classesDir` / `…compileClasspath`
   injection is specified only for `:engine` (§4.2.3) and, since the fix-up, for `:conformance`
   (§8.2). `mod/build.gradle` (§4.2.4, §12 item 7) has no equivalent. Relatedly, §2.1's tree gives
   `engine/src/{main,test}` and `conformance/src/{main,test}` but only `mod/src/main/**`, although
   §8.1 assigns four tests to `:mod`.

**Fix:** add a repositories line to `:conformance` (or hoist `repositories { mavenCentral() }` into
the root `subprojects {}` block, which also removes the duplication in `:engine`), add the same
system-property block to `mod/build.gradle`, and show `mod/src/test/java` in §2.1's tree. Does not
touch §5.

### V2-5 — `GLHandle` permits a handle type that is never declared and can never be produced · **note**

**Location:** §4.7.3; §12 item 18 ("`GLHandle` + the five sealed sub-interfaces").

**Evidence:** `public sealed interface GLHandle permits ProgramHandle, ShaderHandle, TextureHandle,
FramebufferHandle, RenderbufferHandle {}` — but only four sub-interfaces are declared, and no service
creates, attaches, or deletes a renderbuffer: `FramebufferService` attaches colour and depth
**textures** only. Either the permit is dead (drop it — the facade deliberately makes every
attachment a sampleable texture, which is what the pack contract needs) or a verb is missing, and
Phase 5 is the phase that would discover which. §12 item 18's "five" makes the ambiguity worse by
sounding deliberate. Dropping the permit does not touch §5; adding renderbuffer verbs would.

### V2-6 — One half of C-4's classpath pattern can never match · **note**

**Location:** §8.2 — "no classpath entry resolves to the `:mod` project (no `mod/build` path entry,
no `mod-*.jar`)".

**Evidence:** `:mod` keeps the template's `base { archivesName = mod_id }` (§4.2.4 moves the root
machinery "verbatim", and §2.3 keeps `mod_id = schmaloogium`), so its artifacts are
`schmaloogium-0.1.0-dev.jar` / `schmaloogium-0.1.0.jar`, never `mod-*.jar`. The `mod/build` path
check still catches every real violation, so the test works; the second pattern is simply dead and
should be `schmaloogium-*.jar`, or dropped in favour of comparing against the resolved project path.

### V2-7 — `eyeBrightness` is cited to the wrong appendix sub-table · **note**

**Location:** §3's map row "`atlasSize` / `eyeBrightness` are **`ivec2`** uniforms | RESEARCH.md
App D.3"; the same citation in §4.7.4's `// ivec2 — App D.3: atlasSize, eyeBrightness` comment and in
§5.2's pixel-transfer row.

**Evidence:** `atlasSize` is App **D.3**; `eyeBrightness` and `eyeBrightnessSmooth` are App **D.1**
(held item / player). The *type* is right in both cases, so nothing downstream breaks — but this is
the depthtex1-unit-11 discipline, and a Phase 6 session following the citation to D.3 will not find
the uniform there.

### V2-8 — Residual cross-reference slips of the class round one's F-7 fixed · **note**

**Location and evidence:**

- §3, last map row: "Pack-facing vocabulary used **verbatim** in identifiers … Naming convention
  recorded in **§4.9**." §4.9 is logging channels, debug flags and error channels; it contains no
  identifier-naming convention for pack vocabulary (the closest thing is the channel-name list). The
  row's *substance* — the facade sits below the pack vocabulary, so it introduces no synonym risk —
  is correct; only the pointer is wrong.
- §4.2.5: "Recorded against §12 item 7, **which is the Impl-gate item** that depends on this merge."
  Item 7 creates `mod/build.gradle`; the Impl gate is item **15** (§12's own preamble says so).

I checked the three F-7 fixes and the two the fix-up disclosed beyond the finding's letter: §4.1's
CI row now says §4.11, §4.5.2's bail registry says §4.10, §4.6 points at §4.7.5, §3's `saveSources`
row cites §4.9.3, and §11.3's refmap item cites §12 item 33 — all correct now.

### V2-9 — The `14b` numbering holds, with one caveat · **note**

**Location:** §12 item 14b; §G9's requirement that §12 be "ordered, independently actionable".

**Evidence:** I resolved every surviving numeric cross-reference in the document: §12 items 1
(LICENSE verification), 7 (`mod/build.gradle` + merge), 15 (Impl gate), 14 (`SeamLwjglConfinementTest`),
30 and 37 (mixin configs / plugin), 32 (dev flags), 33 (refmap check), 43 (`modImplementation`), and
§11.3 items 2 (missing `modImplementation`), 8 (`compatibilityLevel`), 9 (refmap unexercised) — all
resolve to the intended entries after the fix-up's renumbering of §11.3 (items 3–10 → 2–9). `14b` is
ordered where it belongs, is independently actionable, and item 6's forward reference to it is
correct. The choice was the right trade against silently shifting the Impl-gate ordinal. The caveat
is only that a second such insertion would start to erode "ordered"; if §12 is ever revised
wholesale, renumber then and sweep the references in one pass.

### V2-10 — The three mixin configs declare nested packages; worth verifying with item 33 · **note**

**Location:** §4.5.2's config table — `com.schmaloogium.mod.mixin.preinit` (PRE_INIT),
`com.schmaloogium.mod.mixin` (DEFAULT), `com.schmaloogium.mod.mixin.compat` (MOD).

**Evidence:** the DEFAULT config's `package` is the *parent* of the other two configs' packages.
Mixin implementations have historically been particular about a config's mixin package containing
another config's package (package-based scanning and classloader exclusions are registered per
config), and CleanMix's behaviour here is undocumented in every source I read — RESEARCH.md §5.1, the
MCP `mixin-setup` guide and the template README all describe the phase split without addressing
package nesting. **I did not verify this and I am not asserting it is broken.** The cheap insurance
is either sibling packages (`…mixin.preinit` / `…mixin.main` / `…mixin.compat`) or one extra clause on
§12 item 30's test hook, which already says `runClient` must load all three configs without error —
item 33's throwaway-mixin check is the natural moment to confirm it for all three.

---

## 3. Audit of round one's resolutions

Read last, and checked against the current document rather than against the fix-up's description of
it. **All twelve are real**; two left a residue, recorded above.

| Finding | Claimed | Verified in the doc | Residue |
|---|---|---|---|
| F-1 pixel-transfer verbs | applied | ✅ `readDepthPixel` (§4.7.4), `TextureService.upload(TextureHandle, TextureData)`, `upload(loc,int,int)`; `ScriptedResponses.depthPixel` + summary-not-content logging (§4.7.5); four §3 rows; §5.2 rows + Phase 6 note; §9 row; `D-P1-25`; §11.4 Phase 3/14; §12 items 19–20. All three verbs are policy-free, carry no GL constant, no raw object int, and no LWJGL type (`TextureData` is explicitly a JDK `ByteBuffer`) | **V2-1** (the `ivec4` half of the same table is still wrong) and **V2-3** (the ARB half's provenance and Phase-3 surfacing). **V2-2** is a gap the split table was supposed to make visible and does not |
| F-2 fixtures | applied | ✅ `java-test-fixtures` in §4.2.3, `engine/src/testFixtures/resources/profiles/` in §2.1/§4.7.2/§8.3, `testFixtures(project(':engine'))` in §5.2's note and §12 items 5/6/7/17, `D-P1-26`. Mechanically correct; C-1 and C-4 both intact (§1 above) | none |
| F-3 LICENSE/D-7 | applied | ✅ §12 item 1 is a verification step; §11.2's D-7 row states the repo fact `[V:repo]`; the empty §11.3 entry is gone and items renumbered 2–9 with every reference updated. I verified the repo state independently and re-resolved every §11.3 reference | none — no information lost; the removed entry was the redacted one |
| F-4 ModularUI mechanics in §5 | applied | ✅ §5.3 row present, pointing at §4.2.6 / §4.8.4 / §11.3 item 2, consumed by 12; §11.4's Phase 12 hand-off points back at it | none |
| F-5 SRG descriptor | applied | ✅ `// renderWorld(FJ)V`, confirmed by my own `resolve_symbol` call | none |
| F-6 header | applied | ✅ §0.1 lists App E (header); §0.3 item 3 is now the complete extra-reads list and agrees with §0.1; §0.4 records the fix-up, its inputs, and the §G1.3 obligation | trivial: `gradle/wrapper/gradle-wrapper.properties` and `.gitignore` appear in §0.1 but are not in the spec's Required-inputs list and are not called out in §0.3. Both are template build ground truth; not worth a finding |
| F-7 cross-references | applied (+2 disclosed) | ✅ all five verified | **V2-8**: two more of the same class survive |
| F-8 `enable_mixin_debug` + ASM pin | applied | ✅ property row present; ASM pinned `9.10.1` with an honest single-source caveat. My independent second-sourcing confirms `9.10.1` and retires the caveat | none (the caveat can now be softened) |
| F-9 `plugin` key timing | applied | ✅ §4.5.2 prose + table, §9 row, §12 items 30/37 all say the key ships with the class at v0.3 | none |
| F-10 C-4 mechanical | applied | ✅ `SeamConformanceDependencyTest` in §8.2, row in §8.1, named in §4.3's C-4, merged §9 row, §12 item 14b | **V2-6** (dead `mod-*.jar` pattern); **V2-4**(2) shows the sibling injection for `:mod` was never specified |
| F-11 KHR_debug row | applied | ✅ §3 row present, tied to `DebugService` + `glLabels`, v0.1 interface / v0.5 implementation | none |
| F-12 jar-merge expression | applied | ✅ §4.2.5 caveat on the expression, §12 item 7 instructs a dependency-derived form with the reason and a test hook | none |

**§0 header honesty, judged on the whole:** the header neither over-reports nor under-reports
materially. Everything §0.1 claims to have read is corroborated by the doc's use of it (App E's
header is quoted in §4.5.3 and the quote is exact; App D.3's `ivec2` claim is used in §3/§4.7.4;
§4.2's state-barrier language shapes §4.7.4's `use()`), the two omissions from §0.3's deviation list
are trivial build files disclosed in §0.1, §0.3 item 1 honestly records the un-done `cleanroom-src`
skim as a deliberate omission rather than claiming it, and §0.4 correctly declares both the §5 change
and the resulting re-verify obligation. On the skim: I performed it, and it changed nothing — the
loader's `MixinConfigs` handling that appears under `com/cleanroommc/` is the deprecated MixinBooter
compatibility path, so the build session's judgement that RESEARCH.md §5.1 plus the MCP guide fully
specify the manifest contract was right.

---

## 4. Verdict

**PASS-WITH-CORRECTIONS**

The document meets the Doc gate literally: module and package layout finalized with the dependency
rules stated as mechanically checkable constraints C-1..C-4 (each now with a named test), every
D-1..D-10 dispositioned with an owner named for each deferral, and a complete pin table whose three
volatile rows I re-verified independently against live sources and found correct in every particular
— including the kappa-fork trap and the ASM row the brief singled out, which I was able to
second-source. All thirteen §G9 sections are present and substantive, all four assigned OQs carry
genuine §G4.4 spikes with pre-designed fallbacks, scope discipline holds in both directions, and no
binding decision is contradicted. The fix-up's twelve resolutions are all real, and the two structural
ones the brief flagged for extra scrutiny — the `testFixtures` move and the mechanical C-4 test —
are correct as specified. Nothing here calls for rebuilding the document, so FAIL would be wrong.

The corrections are V2-1 through V2-4, and V2-1 is the substantive one: the same table that round one
added to make absences legible states, as fact, that the 1.12.2 contract declares no `ivec4` uniform,
when App D.4 declares `blendFunc` as exactly that — so the facade cannot upload a built-in uniform a
v0.1/v0.3 dependent must upload, and the table that was supposed to disclose the gap denies it exists.
V2-2 is the same shape in `StateService`, and V2-3 leaves the ARB-geometry rule resting on a §6.2 row
that RESEARCH.md itself routes through OQ-22, with the obligation it creates for Phase 3 recorded
outside the section §5 tells dependents to read. All three are small, local edits; none disturbs the
facade's shape, the seam, or any decision the doc argues for.

**§G1.3 line.** The doc is **not yet verified**. This session's verdict is PASS-WITH-CORRECTIONS with
corrections outstanding and no `## Resolutions` recorded for them, so §G1.3's "PASS, or
PASS-WITH-CORRECTIONS with all resolutions recorded and no §5 change outstanding" is not satisfied,
and per the §G5.3 gating invariant `PHASE_1_DOC.md` is still not a valid dependency input for Phase 2,
Phase 3, or any other dependent. **V2-1, V2-2 and V2-3 all touch §5** (§5.2's facade rows and non-verbs
row, and a new row Phase 3 needs), so once a fix-up session applies them and records its resolutions,
§G1.3's "re-verify only if §5 changed" rule fires again and the doc requires **one more verify pass**
before any dependent consumes it. V2-4 through V2-10 do not touch §5 on their own; V2-5 touches it
only if it is closed by adding renderbuffer verbs rather than by dropping the dead permit.

*End of PHASE_1_REVIEW_2.md. Per §G1.2 this session stops here and fixes nothing.*

---

## Resolutions

*Recorded by the fix-up session of 2026-07-24 (§G1.3), which applied rounds two, three and four
together. Nothing above this heading was modified. Round two's findings were re-raised almost
verbatim by round three (F3-1 … F3-12) and then dispositioned adversarially by round four, so each
row below points at the disposition that was actually applied rather than restating it.*

| Finding | Disposition | Where in `PHASE_1_DOC.md` |
|---|---|---|
| **V2-1** `ivec4`/`blendFunc` (blocking) | **Applied**, at the strength round four established rather than the one round two argued: `DESIGN.md`'s Phase 6 Scope-in and its **v0.1** milestone are cited, not the §G4.6 parenthetical. `UniformService` gained `upload(loc,int,int,int,int)`; §3 gained a `blendFunc` row citing App D.4 and §3.4; the deferred row is now `ivec3`/`mat3` with the true reason. See F3-2 in `PHASE_1_REVIEW_3.md`'s resolutions | §3, §4.7.4, §5.2, §9, §12 items 19–20, `[D-P1-25]` |
| **V2-2** `StateService` gaps | **Applied, narrowed** to what survived round four's attack: `depthTest` and `fog` added; colour mask given a deferred row naming Phase 7; "identity ortho" struck (the facade has no matrix-state verb by design); `fullscreenQuad()` now states that it establishes no draw state. Round two's provenance point — that the perturbed-state list is the doc's own, not §G4.6's — is adopted verbatim in the rewritten rule | §4.7.4, §5.2, `[D-P1-31]` |
| **V2-3** ARB provenance + Phase 3's obligation | **Split, as round four directed.** The provenance half is closed by a hedge: §3's row now says RESEARCH.md §6.2 *lists* the modernization as an opportunity, which this project adopts. The tag is **not** changed — round four showed the tag qualifies the contract item (the pack-side ARB declaration), which is `[V:doc]` three times over. The surfacing half is closed in full: §5.2 carries a row addressed to **Phase 3**, phrased as contestable, and noting that the translation *strategy* is Phase 4's per `DESIGN.md`. See F4-4 | §3, §4.7.4, §5.2, §11.4 |
| **V2-4(1)** `:conformance` has no repository | **Applied** — new §4.2.4a specifies the file with `repositories { mavenCentral() }`, and `[D-P1-27]` records why it is per-project rather than hoisted into the root `subprojects {}`. §12 item 6's hook moved to `:conformance:compileTestJava`, the configuration the failure actually occurs in | §4.2.4a, §12 item 6 |
| **V2-4(2)** `:mod` has no classes-dir injection | **Applied and widened**: `mod/build.gradle` now carries the three `schmaloogium.test.*` properties **and** test-scope ASM, `[D-P1-3]` is widened to all three modules, and a resolution note forces 9.10.1 over Unimined's inherited `asm-debug-all` 5.x, which cannot read Java 25 class files | §4.2.4, §12 item 7, `[D-P1-3]` |
| **V2-5** dead `RenderbufferHandle` permit | **Applied by dropping the permit**, not by adding verbs — §4.7.3 now states four handle types and why a renderbuffer has no contract consumer; §12 item 18 says four. No §5 effect | §4.7.3, §12 item 18 |
| **V2-6** dead `mod-*.jar` pattern | **Applied as `schmaloogium-*.jar`**, not by deletion: round four showed the surviving path check misses a real violation shape (a Maven-cache path with no `mod/build` segment). The path check is also now a **segment-pair** test rather than a literal substring, so it matches on Windows | §8.2 |
| **V2-7** `eyeBrightness` cited to App D.3 | **Applied at all four sites plus the header** — §3, §4.7.4's comment, §4.7.4's rationale, `[D-P1-25]`, and §0.1's inputs record. Round three cited two of them; round four found the other two | §0.1, §3, §4.7.4 ×2, `[D-P1-25]` |
| **V2-8(a)** §3's pointer to §4.9 | **Applied by deletion** — the clause is gone and the substantive claim, which is what discharges the §G4.1 row, stays | §3 |
| **V2-8(b)** §4.2.5 calls item 7 "the Impl-gate item" | **Not applied — refuted.** Round four showed the clause is restrictive ("the Impl-gate item **that depends on this merge**"), that item 7 does carry the caveat §4.2.5 says it does, and that repointing at item 15 would point at an item carrying no merge instruction. Applying this would have been actively harmful | — |
| **V2-9** `14b` numbering | **No change required** — the numbering holds, as round two itself concluded. This fix-up added `36b` and `41b` in the same style, for the same reason | §12 |
| **V2-10** nested mixin packages | **No change required**, on round four's evidence: §12 item 30's hook already requires `runClient` to load all three configs without error, so the detection exists, and the platform sources state no non-nesting constraint. Recorded in §4.5.2 as an observation with the first thing to try if that hook ever fails — together with a second placement worth Phase 10's eye, the plugin FQN inside the DEFAULT config's package | §4.5.2 |

**§5 status after this fix-up:** V2-1, V2-2 and V2-3 all touched §5, as round two predicted, and so
did six items no round before four had raised. §5 changed, so §G1.3 requires a **fifth verify
session** before any dependent consumes the document.
