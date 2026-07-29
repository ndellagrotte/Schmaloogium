# Schmaloogium — Phase 2: Conformance harness — Review Round 13

## 0. Method and reading order

I independently re-derived both gated candidates before reading any prior review. The reading
order was:

1. `docs/design/v1.1/DESIGN.md` Part I, the Phase 2 target specification, document gate, and
   mandatory template.
2. `docs/research/v1/RESEARCH.md`, especially its conformance and milestone requirements.
3. The manifest-selected binding dependency, `docs/phase1/v14/PHASE_1_DOC.md`, especially §5.
4. The supporting CI workflows under `.github/workflows/`.
5. The complete target, `docs/phase2/v1/PHASE_2_DOC.md`.
6. Only after settling both candidates, `docs/phase2/reviews/PHASE_2_REVIEW_1.md` through
   `docs/phase2/reviews/PHASE_2_REVIEW_12.md`, in round order.

There were no reading-list deviations and no network use. This already-dispatched atomic
adjudication role started no subagents, agent fan-out, or nested verification run. The canonical
engine supplied the finder, refuter, steelman, and Gate material. The Gate reported no drops, and
no candidates were eliminated before adjudication. Forbidden sources were not read.

The prior reviews do not settle the admitted defects. Round 12 introduced the independently
resolved trusted `<cache>/runs` root and descendant containment rule. Candidate-001 tests whether
that newly established trust boundary covers every artifact used by the ledger rather than only
manual attestations. Candidate-002 tests whether “independently resolved” actually specifies how
the trusted root itself is established. Round 12's resolution names the root but defines neither
of those missing mechanics.

## 1. Findings

### candidate-001 — Trusted-root containment omits the evidence index and run manifests

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:538–560`, with cache layout at
  `docs/phase2/v1/PHASE_2_DOC.md:1103–1111` and test coverage at
  `docs/phase2/v1/PHASE_2_DOC.md:1621`.
- **Claim:** Every file whose bytes determine a ledger row is deterministically located and
  validated beneath the trusted run-output root.
- **Evidence:** The ledger names an `evidenceIndexSha256` and records
  `{runId,manifestSha256}` for non-manual evidence, but gives neither the index nor a referenced
  manifest a canonical pathname (`docs/phase2/v1/PHASE_2_DOC.md:538–550`). It then supplies an
  exact path, component-by-component no-link traversal, regular-file requirement, and digest
  validation only for
  `<artifact-directory>/attestations/<attestationSha256>.attestation`
  (`docs/phase2/v1/PHASE_2_DOC.md:551–557`). The cache tree merely places unspecified artifacts
  somewhere under `<cache>/runs/<runId>/…`
  (`docs/phase2/v1/PHASE_2_DOC.md:1103–1111`); the ellipsis does not identify a unique manifest
  leaf or locate `evidence.index`. Correspondingly, `TierLedgerTest` exercises trusted-root
  resolution only for artifact directories and manual attestations
  (`docs/phase2/v1/PHASE_2_DOC.md:1621`). Independent ledger readers therefore cannot locate and
  safely validate the index and constituent manifests without inventing storage and traversal
  rules.
- **Disposition:** Define canonical trusted-root-relative locations for `evidence.index` and each
  referenced run manifest. Require component-by-component no-link containment, a regular-file
  leaf, existence, and digest validation for both, and extend `TierLedgerTest` with missing,
  escaping, linked-component, non-regular, and hash-mismatch cases for the index and manifests.
- **Severity:** correction
- **touches interface/change-trigger region: yes** — §5 exposes §4.2's tier decision procedures
  to every behavioural phase and separately exposes the run-manifest schema to Phases 3, 4, 5,
  and 7 (`docs/phase2/v1/PHASE_2_DOC.md:1403–1412`).

### candidate-002 — The trusted run-output root has no establishment procedure

- **Location:** `docs/phase2/v1/PHASE_2_DOC.md:551–555`, with cache-root selection at
  `docs/phase2/v1/PHASE_2_DOC.md:1103–1122` and tests at
  `docs/phase2/v1/PHASE_2_DOC.md:1611–1621`.
- **Claim:** `FixtureResolver` and `TierLedger` derive one stable, unambiguous trusted base from
  the configurable cache-root value.
- **Evidence:** The ledger calls `<cache>/runs` “independently resolved” and specifies no-follow
  traversal only below that assumed base (`docs/phase2/v1/PHASE_2_DOC.md:551–555`). Section
  4.10.3 supplies source precedence and the cache layout, but does not state how a relative value
  becomes absolute, how cache and `runs` roots are created, whether their existing components may
  be symbolic links, or what stable root is retained for descendant traversal
  (`docs/phase2/v1/PHASE_2_DOC.md:1103–1111`). The `.git`-ancestor rule itself begins from an
  undefined “resolved root” (`docs/phase2/v1/PHASE_2_DOC.md:1118–1122`). Existing tests cover
  only the ancestor-walk result and descendant artifact-directory/attestation cases, not relative,
  linked, or replaced cache/runs roots (`docs/phase2/v1/PHASE_2_DOC.md:1611–1621`). Consequently,
  conforming implementations may disagree on both containment and the structural never-rehost
  guarantee.
- **Disposition:** Define one shared cache/run-root establishment policy covering absolute
  normalization or real-path semantics, existence and creation, treatment of linked cache and
  `runs` components, and traversal relative to the established root without following links.
  Extend `FixtureCacheRootTest` or `TierLedgerTest` with relative-root, linked cache/runs-root,
  and root-replacement cases appropriate to the chosen filesystem mechanism.
- **Severity:** correction
- **touches interface/change-trigger region: no** — the repair can remain in §4.10.3 and its
  tests without changing the declared §5 region.

## 2. Checked and clean

The OQ-10 Round-12 repair is consistent: Stage D supplies the real-pack smoke, S6 makes it part of
success, the inverse appears in the failure criteria, and checklist item 21 cites S1–S6. The
ledger's scene-set identity, exact constituent coverage, mandatory `PRIMARY` records, automated
feature-pair records, manual-attestation content addressing, and stale-evidence outcome are
otherwise internally consistent. The conformance map substantively covers all in-scope Phase 2
requirements, including every milestone exit criterion. The consumed Phase 1 module/seam,
capability-profile, recording/replay, diagnostics, and CI contracts align with the selected
binding dependency. The remaining downstream requests and exposed interfaces were sufficiently
explicit in the examined areas.

Both candidates survive independent re-derivation; neither was refuted or cleared. The prior
round's phrase “independently resolved” establishes the intended root but does not supply the
missing root algorithm, index locator, or manifest locator. The Gate dropped none, and no
candidate-free finding is added.

## 3. Verdict

# PASS-WITH-CORRECTIONS
Counts: blocking=0; corrections=2; notes=0
Interface changed: yes

Both defects are bounded fix-up work rather than structural omissions requiring a rebuild.
Candidate-001 completes evidence reachability and safe validation in the tier procedure and
run-manifest evidence contract, firing the cross-phase interface change trigger. Candidate-002
completes Phase 2-owned cache-root establishment outside the declared §5 region.

The correction trend is 3 in Round 10, 2 in Round 11, 2 in Round 12, and 2 in Round 13. The loop
has plateaued and has not converged to literal PASS; the new findings probe the trust boundary
introduced by the preceding fixes. The next required action is a scoped fix-up resolving both
findings and recording their resolutions in this review. Because the exposed tier/evidence
contract must change, a fresh verify round is required before Phase 2 can close.

## Resolutions

### candidate-001 — resolved

Section 4.2.5 now fixes `evidence.index` at
`<cache>/runs/<artifact-directory>/evidence.index` and every referenced run manifest at
`<cache>/runs/<runId>/manifest.manifest`. It applies the same root-relative, component-by-component
no-follow traversal to indexes, manifests, and attestations; requires an existing regular-file
leaf; and validates each file against its recorded digest before a ledger row is decided.
Section 5 now exposes those canonical locations and validation duties, and `TierLedgerTest` covers
missing, escaping, linked-component, non-regular, replacement, and digest-mismatch cases. This
changes the declared cross-phase interface, so another fresh verify round is required.

### candidate-002 — resolved

Section 4.10.3 now defines the shared establishment routine used by `FixtureResolver` and
`TierLedger`: relative values resolve against the process working directory; the result is made
absolute and normalized; missing cache/`runs` directories are created component by component;
linked or non-directory components are rejected; and both roots are retained as
`SecureDirectoryStream`s without following links. Descriptor-relative traversal plus stable
filesystem-identity checks before and after every operation reject unsupported filesystems,
disappearance, or replacement instead of traversing a substituted root. `FixtureCacheRootTest`
covers relative roots, linked cache/`runs` roots, unavailable secure streams or identities, and
replacement before or during traversal.

### Notes deferred

None.
