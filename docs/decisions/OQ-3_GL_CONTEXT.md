# OQ-3 — GL context creation mechanics under Cleanroom

**Open question (verbatim, `docs/research/v1/RESEARCH.md:1009`):** "GL context creation
mechanics under Cleanroom (compat request, GLFW hints, lwjglx runtime role, HiDPI)"

**Why it matters (RESEARCH):** Debug/shared contexts; resize hooks.
**Phase 7 stake:** where a current GL context is first guaranteed (H-BOOT-02 probe
placement), whether Schmaloogium may keep default context hints, and how window extents
vs framebuffer extents (HiDPI) reach the frame driver.
**Status: RESOLVED for Phase 7 implementation (2026-09-09) — spike recorded here; default
fallback retained until a live-client run repeats the capture.**

## Evidence gathered (headless spike, this session)

1. **Pinned runtime**: Cleanroom 0.6.10-alpha is the pinned runtime (project build).
   Reference checkout inspected: `reference-src/Cleanroom-0.6.12-alpha` (closest
   available; behavior of `Display`/context creation is stable across the 0.6.x line)
   and the Cleanroom EntityRenderer patches under
   `reference-src/cleanroom-0.6.6-alpha/patches/minecraft/...` where the Phase 7 doc
   cites exact anchors (e.g. `EntityRenderer.java.patch:175`-`:206`).
2. **Context creation path**: 1.12.2 under Cleanroom creates the context during
   `Minecraft.init()` → `Display.create()` (lwjglx-shimmed `org.lwjgl.opengl.Display`),
   after mod loading, before `OpenGlHelper.initializeTextures()`. The GL calls in
   `func_77474_a` (lightmap texture constants) are the first texture-state work the
   client performs. Consequence used by Phase 7: **the RETURN of
   `OpenGlHelper.func_77474_a()V` is the first render-thread point at which a current
   context is guaranteed** — exactly the H-BOOT-02 anchor in `PHASE_7_DOC` §4.10.2.
3. **Compat request / GLFW hints**: Schmaloogium requests **no** non-default context
   (no core-profile forward-compat request, no debug-context request). Phase 1's probe
   (`mod.glue.CapabilityProbe.capture()`) reads the context the loader gave it
   (`GL.getCapabilities()`), and P1 D-P1-54 installs debug **activity** on an actual
   GL4.3/KHR backend + glLabels — never a debug-context prerequisite. Inactive is no GL;
   this changes no OQ-3 default context hints. Decision: keep vanilla/Cleanroom default
   hints; never request a debug or core context (coexistence with OptiFine-replacement
   class of mods and with vanilla fixed-function bring-up).
4. **lwjglx runtime role**: the lwjglx bridge is what makes `GL.getCapabilities()` and
   `GL11.glGetFloatv` valid on the render thread under Cleanroom's Java-25 LWJGL 3
   runtime. Phase 7 consumes it only through P1's device seam; no direct
   `Display`/`Context` API use anywhere in `mod.glue.frame`.
5. **HiDPI / resize mechanics**: window size (`Minecraft.resize`, H-RESIZE-02) and the
   actual framebuffer size (`Minecraft.updateFramebufferSize`, H-RESIZE-01) are distinct
   hooks in vanilla 1.12.2; under HiDPI the framebuffer extent is the truth that
   `Framebuffer.func_147613_a(II)V` (H-FBO-01) sees. Phase 7 therefore samples the
   frame-driver `targetView` from `Minecraft.displayWidth/displayHeight` at frame begin
   and lets the depthtex0 bridge version the depth attachment on
   `Framebuffer`-identity/extent change — a resize mid-frame surfaces as
   `MainDepthRefreshResult.ResizeRequired` → `RESIZE_EPOCH` abort, never a mutated open
   Phase 5 generation.

## Decision

- H-BOOT-02 probe placement at `OpenGlHelper.func_77474_a()V` RETURN is **correct and
  minimal**: first guaranteed-current context, before any texture work. Implemented in
  `mod/mixin/preinit/MixinOpenGlHelper.java` (require=0, expect=1).
- Default context hints retained; no debug/core-context request. Capability profile is
  captured, cached (`mod/glue/frame/CapabilityHolder`), and served to the compat context
  (`SchmaloogiumMod.FmlCompatContext.capabilities()`); before H-BOOT-02 the context
  answers "not available before display init" as before.
- Window vs framebuffer extents stay split across H-RESIZE-02/H-RESIZE-01; the frame
  driver sees only engine-owned `Extent2i` facts.

## Live leg (deferred to client smoke)

The spike is headless (source + patch evidence). The live-client leg — confirming the
captured profile under the actual Cleanroom 0.6.10-alpha window creation and that no
hint change is needed on real hardware — repeats `CapabilityProbe.capture()` at first
launch and is recorded as `RUN-BOOT-PROFILE` evidence. Default fallback retained until
then (per §12 item 26).
