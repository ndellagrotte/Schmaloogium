# PHASE_1_DOC.md — Verify session, round nineteen

## 0. Method and reading order

I first re-derived each supplied candidate from the whole document under review,
`docs/phase1/v14/PHASE_1_DOC.md`, the resolved selections in the governing
`docs/design/v2.0-RC2/DESIGN.md` (Part I, the Phase 1 specification, the document gate, and the
mandatory template), and the relevant framebuffer ground truth in
`docs/research/v1/RESEARCH.md`. Phase 1 declares no dependencies. I checked the §0.18 amendment,
header, conformance map, handle and service definitions, recording backend, binding §5 region,
decision log, Phase 5 handoff, milestone table, and implementation checklist, including
whole-document searches for equivalent issuance and provenance.

Only after completing that independent judgment did I read the discovered prior reviews,
`docs/phase1/reviews/PHASE_1_REVIEW_1.md` through
`docs/phase1/reviews/PHASE_1_REVIEW_18.md`, last. They settle the pre-§0.18 surface only; round
eighteen expressly predates the amendment now under review.

There were no reading-order deviations. I used no network source and no forbidden source. This was
the already-dispatched atomic Adjudicate role, so I did not invoke the verification orchestrator,
start another Codex session, or use agent fan-out. The Gate reported no drops, and no candidate was
eliminated before adjudication.

## 1. Findings

### candidate-001 — The mandatory header omits §0.18 and its revision date

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:10`

**Claim.** The document header must identify the latest revision accurately. It instead calls
§0.17 the latest revision and its explanatory inventory stops at the same superseded amendment.

**Evidence.** The header says, *“Last revised: 2026-07-28 (§0.17)”*
(`docs/phase1/v14/PHASE_1_DOC.md:10`). Its convention paragraph says the date is the latest of
§0.4–§0.17 and ends with *“§0.15–§0.17 are 2026-07-28”*
(`docs/phase1/v14/PHASE_1_DOC.md:23`–`:25`). The same document later contains
*“### 0.18 Downstream-request addendum (Phase 5 framebuffer/depth contract — 2026-07-29)”*
(`docs/phase1/v14/PHASE_1_DOC.md:1317`). The mandatory template requires the §0 header to carry
the document date (`docs/design/v2.0-RC2/DESIGN.md:765`–`:768`). Later historical recognition of
§0.18 does not cure the contradictory reader-facing metadata.

**Severity:** correction.

**Touches interface/change-trigger region:** no.

**Required correction.** Change the header to `Last revised: 2026-07-29 (§0.18)` and extend the
adjacent date inventory to record §0.18 as dated 2026-07-29.

### candidate-002 — The live backend exposes no way to issue an authenticated borrowed-depth handle

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:4091`

**Claim.** Phase 5 cannot obtain the same-device/context-authenticated
`BorrowedDepthAttachmentHandle` that the new binding contract requires without inventing an
unexposed interface.

**Evidence.** The marker is expressly insufficient: the backend must authenticate that it issued
the concrete value (`docs/phase1/v14/PHASE_1_DOC.md:2616`–`:2620`). §5 then excludes
`ForeignTextureProvider` as its source and says an otherwise undeclared *“main-depth bridge”*
issues it (`docs/phase1/v14/PHASE_1_DOC.md:4091`–`:4095`). The Phase 5 handoff says
`MainDepthSnapshot` carries the handle and must reattach it, but supplies no issuance operation
(`docs/phase1/v14/PHASE_1_DOC.md:4948`–`:4952`). The recorder does have explicit private-token
minting for tests (`docs/phase1/v14/PHASE_1_DOC.md:3343`–`:3346`), confirming that issuance is a
necessary operation, while checklist item 22c specifies only live authentication and acceptance
behavior (`docs/phase1/v14/PHASE_1_DOC.md:5151`). No production factory, adoption operation, or
named bridge contract appears.

This is not policy Phase 5 can supply privately: Phase 1 owns the facade and receiving backend's
origin authentication, and §5 declares itself sufficient and binding
(`docs/phase1/v14/PHASE_1_DOC.md:4067`–`:4077`). The governing template requires named interfaces
exposed to dependents and forbids silently assumed contracts
(`docs/design/v2.0-RC2/DESIGN.md:782`–`:784`).

**Severity:** correction.

**Touches interface/change-trigger region:** yes.

**Required correction.** Add a narrow engine-visible issuance/adoption operation through which
the main-depth bridge presents the live texture identity and exact depth format to the receiving
device and receives its authenticated borrowed handle. Specify same-device/context origin,
render-thread use, staleness/non-ownership boundaries, and the recorder equivalent; expose it in
§5 and carry it into tests and the implementation checklist. Preserve Phase 5 ownership of
identity/version detection, freshness, reattachment cadence, and Minecraft-side lifetime policy.

### candidate-003 — The conformance map makes non-governing RC3 framebuffer policy normative

**Location:** `docs/phase1/v14/PHASE_1_DOC.md:1551`

**Claim.** The RC2-governed Phase 1 document imports RC3-specific framebuffer mechanisms as
binding conformance requirements even though RC2 excludes GL policy beyond facade shape.

**Evidence.** The two conformance rows expressly source the borrowed-depth bridge, packed
attachment, exact-format initialization, and storage-preserving copy split from RC3
(`docs/phase1/v14/PHASE_1_DOC.md:1551`–`:1552`). RESEARCH.md supports a real `depthtex0` and the
contents/timing of `depthtex1` and `depthtex2`, but not those mechanisms
(`docs/research/v1/RESEARCH.md:511`–`:516`). The governing Phase 1 specification says
*“all GL policy beyond the facade shape (Phases 5+)”* is out of scope
(`docs/design/v2.0-RC2/DESIGN.md:1037`–`:1038`). Nevertheless the imported mechanics are
propagated as detailed binding guarantees in §5
(`docs/phase1/v14/PHASE_1_DOC.md:4102`–`:4103`) and D-P1-40 identifies RC3 as their requirement
source (`docs/phase1/v14/PHASE_1_DOC.md:4670`).

A downstream request can justify adding facade capabilities, but it does not silently make a
different design revision authoritative or transfer Phase 5's policy assignment into Phase 1.
The defect is therefore narrower than removing every requested verb: generic facade shape may
remain where RC2/RESEARCH and the documented request justify it, but RC3-specific behavioral
policy cannot be represented as Phase 1 conformance under this target's resolved authority.

**Severity:** correction.

**Touches interface/change-trigger region:** yes.

**Required correction.** Recast the additions as only the narrowly justified facade shape and
remove or make non-binding the RC3-derived authentication, packed-attachment, allocation, copy,
and restoration policy in the conformance map and §5. Defer downstream-owned mechanics to Phase 5.
If those exact mechanics are intended to govern, complete the repository's formal design-adoption
procedure first and then re-verify the resulting §5 surface.

## 2. Checked and clean

The finder-reported new surface was re-checked across §0.18, the facade declarations, validation
matrix, recorder behavior, tests, milestones, D-P1-40, downstream handoff, checklist, and §5.
Apart from candidate-001's header metadata, candidate-002's missing live issuance route, and
candidate-003's authority/scope defect, the additions are propagated consistently.

The interface lens was re-derived across the module/seam, capability profile, ordinary
foreign-texture provider, GL-error surface, recording/replay, state service, pixel transfer,
logging, Mixin, compatibility, CI, and dependency declarations. Phase 1 honestly consumes no
dependency contract. No additional missing or internally inconsistent cross-phase promise was
established.

The conformance lens was re-checked against RC2 and RESEARCH.md. The spot-checked capability
profile rows correctly map the four startup probes and extension-set requirement. The thirteen
mandatory sections remain present, and no additional document-gate, assigned-OQ, or structural
defect was established.

All three candidates survived independent re-derivation and were admitted. None was refuted,
cleared, or dropped. The prior reviews contain no settled disposition of the post-round-eighteen
§0.18 surface.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=3; notes=0
Interface changed: yes

Two admitted corrections touch the declared §5 change-trigger region; the metadata correction
does not. These are bounded fix-up defects rather than a structural miss requiring rebuild, so
FAIL is not warranted. PASS is unavailable because three corrections remain.

The prior round was a literal PASS on the pre-§0.18 byte state. The amendment introduced new
metadata, completeness, and authority defects, so this is new-surface regression rather than
recurrence of a previously cleared round-eighteen finding. Convergence is not yet established for
the amended interface.

Next action: run a scoped fix-up for all three corrections and record the resolutions in this
review. Because the fix-up must change §5 to supply the issuance route and correct or remove its
unsupported RC3-derived commitments, a fresh verify round is required before Phase 1 can close or
be treated as a verified dependency input.

## Resolutions

### Corrections applied

| Candidate | Resolution |
|---|---|
| candidate-001 | Applied. The header now identifies §0.19 as the latest revision, dates it 2026-07-29, and extends the adjacent date inventory through §0.19. §0.19 is the required compact fix-up addendum, so stamping the header as §0.18 would already be stale after this session. |
| candidate-002 | Applied after re-derivation. `FramebufferService.borrowDepthAttachment(TextureHandle platformTexture)` is the engine-visible issuance route. The input is the live ordinary platform handle already confined behind the facade; the receiving device recognizes its own value and returns an opaque, non-owned borrowed handle, rejecting unknown, stale, forged, or wrong-device inputs before GL. §5 publishes the operation. Phase 5 retains identity/version detection, freshness, reattachment cadence, and Minecraft lifetime. |
| candidate-003 | Applied. The two conformance rows and binding §5 row now describe operation shape only, cite RC2/RESEARCH or the downstream request rather than RC3 as authority, and expressly leave format, allocation, copy tier, cadence, restoration, freshness, and Minecraft lifetime to Phase 5. §0.19 supersedes the §0.18 mechanics wherever the older addendum propagated them; RC3 remains non-governing evidence and supplies no binding Phase 1 policy. |

The interface region changed, so the manifest's fresh-review trigger fires. The immediately
previous live §0.18 status is restamped historical/superseded in §0.19, the closing counts now say
nineteen verify sessions and sixteen fix-up/maintenance sessions, and the live status remains not
verified pending a literal PASS on these bytes.

### Notes deferred

None. The adjudicator admitted no notes.
