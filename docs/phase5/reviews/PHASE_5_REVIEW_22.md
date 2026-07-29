# Phase 5 Verification Review — Round 22

## 0. Method and reading order

I independently re-derived the sole Gate-surviving candidate before consulting prior reviews. I
read the Phase 5 target's public identity and attachment declarations, complete shadow signature
family, and manifest-declared binding §5 interface region; the RC3 mandatory template, Phase 5
specification, and document gate; and the manifest-selected binding regions of Phases 1, 3, and
4. Supporting evidence was not needed because the candidate concerns completeness of Phase 5's
own dependent-facing data contract.

Only after settling that interpretation did I read Phase 5 reviews 1 through 21, in numeric order,
to check the candidate against prior dispositions and settled resolutions. I used no network
access, forbidden source, or prior-session transcript. In particular, I did not open the supplied
`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.txt`, because the resolved forbidden-source rule
bars `*.txt` and it was unnecessary. There was no agent fan-out or delegation. In accordance with
the dispatched atomic-role instruction and the verify-loop skill, I did not invoke the loop, run
`scripts/verify`, or start another Codex session. There were no deviations from the resolved
reading contract, no candidates eliminated before adjudication, and no Gate drops.

## 1. Findings

### candidate-001 — The binding §5 surface omits public types required by the shadow contract

**Location:** `docs/phase5/v1/PHASE_5_DOC.md:1296`–`:1305` and
`docs/phase5/v1/PHASE_5_DOC.md:1575`

**Claim.** Binding §5 exposes `ShadowPassSnapshot` to Phase 8 but does not expose the exact shapes
of the Phase-5-owned `ColorAttachment` and `LogicalBuffer` types embedded in that public record.
Because `LogicalBuffer` itself embeds `BufferDomain` and `BufferIndex`, the dependent-facing
contract is transitively incomplete.

**Evidence.** The complete public declaration makes `ShadowPassSnapshot` carry
`List<ColorAttachment>`, `Map<LogicalBuffer, TextureHandle>`, and `Set<LogicalBuffer>`
(`docs/phase5/v1/PHASE_5_DOC.md:1296`–`:1305`). The concrete records are defined only in detailed
design: `LogicalBuffer(BufferDomain domain, BufferIndex index)`, with the closed domain enum and
non-negative index invariant (`docs/phase5/v1/PHASE_5_DOC.md:314`–`:324`), and
`ColorAttachment(outputOrdinal, framebufferAttachment, logicalBuffer, physicalTexture)`
(`docs/phase5/v1/PHASE_5_DOC.md:753`–`:757`). Binding §5 names the shadow result family and says
the snapshot carries color attachments and readable shadowcolor handles, but it neither names nor
defines these component contracts (`docs/phase5/v1/PHASE_5_DOC.md:1575`). That section labels its
table “Exposed interfaces and data contracts” with “Exact content”
(`docs/phase5/v1/PHASE_5_DOC.md:1564`–`:1566`), consistent with the mandatory template's
requirement that §5 contain the named interfaces and data contracts exposed to dependents
(`docs/design/v2.0-RC3/DESIGN.md:811`–`:813`).

**Required correction.** Add `ColorAttachment`, `LogicalBuffer`, `BufferDomain`, and
`BufferIndex` to §5.1 with their exact immutable shapes and invariants, or redesign
`ShadowPassSnapshot` so every Phase-5-owned type in its public signature is already completely
specified in §5.

**Severity:** correction

**touches interface/change-trigger region: yes**

## 2. Checked and clean

The finder-reported clean areas remain clean on re-derivation. The Round-21 edits consistently
distinguish ordinary no-shadow planning from creation failure, expose the complete top-level
shadow family publicly, preserve generation and publication provenance, and honestly record the
ungranted Phase 4 `CompiledRegistryCandidate.view()` binding clarification. The shadow lifecycle
otherwise has consistent acquisition, exact rejection, operation failure, completion, abort, and
flip semantics across detailed design, §5.1, tests, and the Phase 8 handoff.

The conformance map covers the governing Appendix B.1, B.2, B.3, and B.4 contract families and
the additional RC3 frame-end, fog-alpha, depth bridge/copy, shadow flip, sizing, resize, growth,
and Final-framebuffer requirements. Phase 1 and Phase 3 consumption remains explicit, and Phase 5
does not silently assume its outstanding dependency changes.

Prior settled material does not clear candidate-001. Round 20 required and introduced the
complete concrete shadow signature family, and Round 21 corrected its availability and public
accessibility, but neither resolution mirrored the Phase-5-owned component types of
`ShadowPassSnapshot` into the binding §5 surface. No surviving candidate was dropped on
re-derivation, and there were no Gate drops.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=1; notes=0
Interface changed: yes

The sole admitted defect is a localized dependent-facing data-contract correction and does not
require rebuilding the Phase 5 architecture, so `FAIL` is not warranted. The correction trend is
2 → 2 → 3 → 1 for Rounds 19–22. The count has decreased, but this finding is another incomplete
closure of the shadow interface introduced in Round 20, so convergence has not yet been
established and the verdict cannot be softened to `PASS`.

The next required action is a scoped fix-up resolving candidate-001 and appending its resolution
to this review. Because the correction must change the binding §5 cross-phase interface region,
the `cross-phase-interfaces` change trigger applies: Phase 5 owes a fresh verification round
before it can close.

## Resolutions

### candidate-001 — resolved

Re-derived against the concrete public declarations in §2.2 and §4.1 and the dependent-interface
requirement in RC3 §G9. Binding §5.1 now exposes `BufferDomain`, `BufferIndex`, `LogicalBuffer`,
and `ColorAttachment` with their exact immutable shapes and the non-negative buffer-index
invariant. This closes every Phase-5-owned component type embedded by `ShadowPassSnapshot`
without changing the snapshot design.

The target records this correction in compact §0.24. The binding §5 interface region changed, so
the manifest change trigger applies and a fresh verification round is required before Phase 5 can
close.

### Notes deferred

None.
