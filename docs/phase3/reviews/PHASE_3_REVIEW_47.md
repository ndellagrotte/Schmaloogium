## 0. Method and reading order

I independently re-derived the four surviving candidates before consulting prior reviews. I first read the complete Phase 3 document, then the manifest-selected portions of the governing Design v3, the Phase 3 target specification and document gate, the mandatory template, the Phase 1 binding contract, the relevant RESEARCH contract text, and the cited Pintonium and Oculus reference evidence. I compared the public declarations, detailed producer semantics, conformance tables, fingerprint rules, tests, and the complete monitored cross-phase interface region.

Only after settling that source-based interpretation did I read the discovered Phase 3 reviews 1–46, adjudicator-last, to distinguish settled material from newly exposed or incompletely settled defects. Prior dispositions were treated as history rather than authority. In particular, the Round 36 resolutions for `DRAWBUFFERS:N` and texture suffix handling do not settle the present, narrower questions of mixed positional `N` and whether the adopted suffix behavior faithfully addresses the governing Pintonium gap.

There were no source deviations, no network use, and no agent fan-out. I did not use forbidden chatlogs, text files, or transcripts. No candidates were eliminated before adjudication, and the Gate reported no drops.

## 1. Findings

### candidate-001 — Complete the producer-side canonical `ResourceRequirements` codec

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §4.7 and §4.10, especially lines 1857–1929 and 2190–2208.

**Claim:** The document names a canonical `ResourceRequirements` fingerprint payload but does not completely determine the recursive producer encoding of that record graph.

**Evidence:**

- `ResourceRequirements` contains nested records, maps, sets, optionals, variants, booleans, integers, and finite floating-point values, not only the attachment-format branch added in Round 46 (lines 1857–1898).
- The resource rules establish deterministic map/set iteration and closed absence semantics (lines 1920–1926). Those rules determine value order and meaning, but they do not determine collection framing, map-entry projection, optional tags, boolean spelling, or finite-float byte representation.
- Section 4.10 fixes attachment iteration and the exact `DefaultRgba`/`Explicit` format discriminant, then gives materially more explicit field, count, and framing rules for custom expressions and declared uniforms (lines 2190–2208). Its scalar/string/list framing does not state how every resource-specific container and scalar form is projected into those frames.
- The earlier recursive-encoding sentence at lines 1434–1436 is stated for discovery snapshot candidates and diagnostics and expressly names public scalar/string/list fields. It is useful corroboration for declaration-order traversal, but it does not close the additional resource forms.
- `ConfigurationFingerprint` is correctly opaque to consumers. That narrows the required repair—this need not become a downstream wire protocol or mandate portable digest strings—but opacity does not select one canonical producer payload among multiple deterministic encodings.

**Severity:** correction.

**Required correction:** Add a normative resource-payload codec table or equivalent rule that fixes top-level and nested field traversal, map/set counted-sequence projection in the already-defined order, optional and sealed-variant tags, and exact boolean, integer, enum, string, and finite-float encodings, including the signed-zero policy. State that every published resource component participates. Extend tests with per-leaf mutations and equality for semantically equal maps/sets created in different insertion orders. Do not add a separate digest portability promise, domain, or nested schema version unless such interoperability is actually intended.

**Touches interface/change-trigger region:** no. The correction can complete §4.10 producer canonicalization and its tests while leaving the opaque `ConfigurationFingerprint` declaration and §5 consumer contract unchanged.

### candidate-002 — Specify the global-options codec's request/result behavior

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` lines 833–855, §4.3 lines 1539–1613, and §5.1 lines 2243 and 2256–2302.

**Claim:** The global `optionsshaders.txt` API has public request and result shapes, but its global-specific read/write and result invariants are not executable from the contract.

**Evidence:**

- `GlobalShaderOptionsReadRequest` contains a supplied baseline, and the read result contains values, a shared status, an optional failure, and diagnostics; the write request/result expose a similarly consumer-visible algebra (lines 833–855).
- The detailed validation, baseline return, update, failure, and changed-only write paragraph at lines 1600–1609 is expressly about the per-pack option codec and ends by keeping global settings separate. That codec also has catalog/state authentication and a distinct sealed invalid-request result, so its branches cannot silently define the global result algebra.
- Existing global and shared rules do provide substantial coverage: ISO-8859-1 Properties encoding, deterministic ordering and safe replacement, foreign-domain rejection before I/O, stable `EngineOptionData`, unknown-safe-key retention, malformed-entry warnings, and separation from pack options. The finding does not order those rules to be redefined.
- What remains unspecified is how persisted global entries combine with `baseline`; which values/status/failure combinations are returned for invalid requests, absent input, successful application, and access or decode failure; duplicate-key precedence; and whether a successful write emits every validated `EngineOptionData` entry or a defined subset.
- The governing Phase 3 persistence scope requires the global format and read/write model, while Phase 12 owns GUI apply/discard timing rather than this codec contract.

**Severity:** correction.

**Required correction:** Add a global-specific codec paragraph that binds request validation and no-I/O rejection to exact result values, statuses, and failures; defines `ABSENT`, successful application, and access/decode failure; specifies baseline overlay and duplicate behavior; and states the exact successful write content. Reference rather than duplicate the existing encoding, ordering, safe-write, unknown-key, malformed-entry, and target-authentication rules. Update the §5.1 persistence row and expand the global round-trip test with absent, overlay, invalid/foreign-access, access/decode failure, result-invariant, unknown-key, duplicate, and exact-output cases.

**Touches interface/change-trigger region:** yes. This orders completion of consumer-visible semantics in the manifest-monitored §5 persistence contract, so a fresh verification round is required.

### candidate-003 — Preserve positional `N` entries in `DRAWBUFFERS`

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §3.3 lines 1356–1359, §4.7 lines 1818–1821 and 1880–1883, and binding §5.1 lines 2411–2414.

**Claim:** `DrawRouting.Explicit(List<ColorAttachmentKey>)` cannot faithfully represent every valid ordered `DRAWBUFFERS` character when `N` occurs among attachment digits.

**Evidence:**

- RESEARCH Appendix A.3 defines `DRAWBUFFERS` as an ordered routing string whose characters are digits 0–7 or `N = none`; absence instead means all used buffers. A none entry is therefore positional.
- The target's conformance table and parser algorithm define digit-only routing and the special standalone `N`, mapping the latter to an empty attachment list. They define no mixed branch such as `0N2`.
- The public and binding algebra permits only `AllUsed` or `Explicit(List<ColorAttachmentKey>)`. Dropping `N` from `0N2` would shift the later target's output position, while an empty list also loses the distinction between one explicit none slot and zero slots.
- Phase 3 owns complete directive scanning and publication of routing requirements; no alternate retained slot representation exists elsewhere in the document.

**Severity:** correction.

**Required correction:** Replace the attachment-only explicit payload with an ordered closed slot algebra, for example `Attachment(ColorAttachmentKey)` and `None`. Parse one slot per valid character, preserve standalone and repeated `N`, and retain `AllUsed` only for directive absence. Update the conformance map, scanner design, public and binding declarations, resource fingerprint encoding, decisions/checklist, and tests for leading, middle, trailing, repeated, and all-`N` forms. Because the published nested record shape or meaning changes, advance the schema version and add old/new producer-consumer compatibility tests.

**Touches interface/change-trigger region:** yes. `DrawRouting` is part of the binding `ResourceRequirements` graph, and the ordered repair changes its consumer-visible shape or meaning.

### candidate-004 — Do not certify strip-and-ignore as closure of the filter/wrap gap

**Location:** `docs/phase3/v1/PHASE_3_DOC.md` §3.2 lines 1295–1296, §4.8 lines 1974–1981, and binding §5.1 lines 2247 and 2332–2337.

**Claim:** The target adopts the cited Pintonium filter/wrap deficiency as its own conformance behavior by stripping and discarding a recognized suffix, leaving Phase 13 unable to honor the request.

**Evidence:**

- The governing Phase 3 specification labels Pintonium's `texture.<stage>.<sampler>` filter/wrap suffixes being “stripped and ignored” as one of the REV1 gaps Pintonium cannot validate and assigns each such gap a Phase 3-owned conformance row.
- Pintonium §7.4 likewise identifies that exact strip-and-ignore behavior as a gap versus the Appendix F contract, not as evidence that discarding the setting conforms.
- The target's conformance row, detailed parsing rules, and binding `TextureBindingKey` all require the recognized suffix to be removed and never retained as filter/wrap state. The separate `.mcmeta` sidecar cannot recover the discarded request.
- Section 5 simultaneously promises Phase 13 a closed, lossless parsed texture algebra. A downstream loader cannot apply data that the sole published front-end model deliberately erases without violating the no-reparse handoff.
- RESEARCH Appendix F.5 does not itself spell out the exact property-key suffix grammar. That is an authoritative-input ambiguity to resolve or record, not grounds for claiming destructive normalization closes the expressly named gap.

**Severity:** correction.

**Required correction:** Do not invent a suffix grammar. First resolve and record the recognized suffix syntax and value domains from an authoritative contract source. Then preserve each filter/wrap request in an immutable texture sampling-state value, distinct from `.mcmeta`, and expose it through the Phase 13 handoff. Correct the conformance row, detailed rules, decision, §5.1 declaration/semantics, and tests to assert preservation and availability. Advance the schema and add compatibility tests if the published component shape or meaning changes. If the grammar cannot yet be resolved, identify the authoritative input gap and stop claiming strip-and-ignore as conformance rather than silently discarding the request.

**Touches interface/change-trigger region:** yes. Repairing the closed texture handoff changes consumer-visible §5 semantics or shape and triggers fresh verification.

## 2. Checked and clean

All four surviving candidates remain findings after independent re-derivation; none was cleared or downgraded to a note. There were no pre-adjudication eliminations or Gate drops to carry forward.

The following examined areas remain clean apart from the findings above:

- The Round 46 attachment-format sum is consistently `DefaultRgba | Explicit(ColorInternalFormat)`, retains all 37 explicit sized formats, distinguishes explicit `RGBA8`, and makes `gdepth` force explicit `RGBA32F` in either source order. Schema 11 and schema-10 rejection are propagated consistently across declarations, tests, compatibility text, milestones, handoff, and checklist.
- The Phase 1 binding dependencies consumed by Phase 3 match their selected binding contract, aside from already-declared future implementation work that is not a document defect in this round.
- The engine-flag ownership map; option discovery/application; profiles, screens, sliders, and language decoration; custom-expression capture; dimensions/includes/debug dump; implicit resource sizing; shadow, smoothing, clear, mipmap, and geometry directives; ID-mapping grammar; the other mandatory Pintonium pitfall rows; OQ-7's selected architecture; and pure-`:engine` placement showed no additional conformance finding.
- The discovery/load, option-state, materialized-source, geometry, ID-mapping, macro-contribution, program-state, resource, dimension, lifetime, ordering, absence, and version interfaces are otherwise detailed and internally connected. The document also correctly requires later consumer-visible edits to update §5 and undergo fresh verification.
- Prior-review history does not clear the present defects. Round 36 established an absent-versus-explicit-none distinction but not mixed positional `N`, and added a filter/wrap row whose adopted behavior is the governing reference gap itself. Round 37 established the global codec types and common persistence mechanics but did not supply the missing global result matrix. Round 46 specified the attachment-format fingerprint discriminant but not the remaining resource codec forms.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=4; notes=0
Interface changed: yes

The document is structurally complete enough for bounded correction rather than rebuild, but it cannot pass while the canonical resource producer codec remains underdetermined, the global persistence API lacks result semantics, and two valid texture/routing inputs are not representable losslessly in published interfaces.

The correction trend is not converged: rounds 45–47 contain 5, 2, and 4 corrections, so the count has risen from the prior round rather than strictly decreased. Three admitted corrections require edits to the monitored cross-phase interface/change-trigger region. The next action is to apply all four corrections, update §5 and schema compatibility where ordered, and run a fresh Phase 3 verification round before closure.

## Resolutions

### Applied corrections

1. **Canonical resource codec completed.** Section 4.10 now gives the complete
   `ResourceRequirements` graph one recursive counted/length-framed producer encoding: record
   traversal, map entries, collection order/counts, optional/variant tags, all scalar forms, and
   finite float bits with signed zero canonicalized positive. Tests mutate every leaf and equate
   maps/sets built in different insertion orders; no portable digest/wire promise was added.
2. **Global persistence matrix completed.** The codec now validates requests and own-domain access
   pre-I/O; fixes invalid, absent, applied, access-failed, and decode-failed tuples; overlays the
   supplied baseline with last-valid-duplicate precedence; and writes every validated global entry,
   including retained unknown safe keys. Section 5 and one expanded test bind every invariant.
3. **Positional `DRAWBUFFERS` none entries preserved.** `DrawRouting.Explicit` now contains a
   non-empty ordered list of closed `DrawSlot.Attachment`/`DrawSlot.None` values, one per character.
   Leading, middle, trailing, repeated, standalone, and all-`N` forms remain lossless; only absence
   yields `AllUsed`. Declarations, parsing, fingerprinting, §5, decisions, staging, and tests agree.
4. **Destructive texture-suffix claim removed.** Design v3, RESEARCH Appendix F.5, and Pintonium
   establish that strip-and-ignore is a gap but supply no suffix tokens or value domains. Phase 3
   now diagnoses an additional nonnumeric key segment without recognizing, stripping, or inventing
   sampling state, and records an upstream contract request plus the Phase 13 handoff constraint.

The routing shape and former suffix-input meaning advance the schema to 12; tests require both
schema-11/schema-12 producer-consumer rejection directions.

### Interface/change-trigger disposition

Global persistence, `DrawRouting`/`DrawSlot`, texture handoff, and schema edits intentionally change
the monitored §5 region. The manifest trigger applies: a fresh Phase 3 verification round is required.

### Notes deferred

None were admitted. A concrete filter/wrap grammar and sampling-state value are refused because
they require a new design decision without authoritative syntax or domains; candidate-004's
non-destructive authority-gap disposition was applied instead.
