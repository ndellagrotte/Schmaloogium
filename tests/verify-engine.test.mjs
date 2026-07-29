import test from "node:test";
import assert from "node:assert/strict";
import {
  chmodSync,
  cpSync,
  mkdirSync,
  mkdtempSync,
  readFileSync,
  rmSync,
  symlinkSync,
  unlinkSync,
  writeFileSync,
} from "node:fs";
import { createHash } from "node:crypto";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { fileURLToPath } from "node:url";
import { spawnSync } from "node:child_process";
import {
  STAGE_ORDER,
  applyGateEvidenceCorrection,
  adjudicationStop,
  aggregateRefutations,
  changedPaths,
  dedupeCandidates,
  discoverPriorReviews,
  discoverRepoRoot,
  dryRunPlan,
  dryRunFixupPlan,
  executeFixupContinuation,
  executeVerification,
  globToRegExp,
  isFirstReviewRound,
  resolveCandidateEvidence,
  resolveCitation,
  resolveContract,
  resolveManifestPath,
  resolveRepoPath,
  resolveFixupReview,
  validateAdjudication,
  validateFixupAppend,
  validateSchema,
  verifyPermittedWrites,
} from "../.agents/skills/verify-loop/scripts/engine.mjs";

const ROOT = discoverRepoRoot(fileURLToPath(new URL("..", import.meta.url)));

function writeJson(path, value) {
  writeFileSync(path, `${JSON.stringify(value, null, 2)}\n`);
}

function makeMiniRepo() {
  const root = mkdtempSync(join(tmpdir(), "verify-engine-"));
  spawnSync("git", ["init", "-q"], { cwd: root });
  mkdirSync(join(root, "verification", "targets"), { recursive: true });
  mkdirSync(join(root, "reviews"), { recursive: true });
  writeFileSync(join(root, "target.md"), "# Target\n\n## Public\n\nalpha\n\n## Internal\n\nprivate\n");
  writeFileSync(join(root, "spec.md"), "# Spec\n\n## Required\n\nalpha\n\n## Gate\n\ncomplete\n");
  writeFileSync(join(root, "dep.md"), "# Dep\n\n## Binding\n\nstable\n\n## Internal\n\nprivate\n");
  writeFileSync(join(root, "evidence.md"), "# Evidence\n\nalpha\n");
  writeFileSync(join(root, ".gitignore"), ".runs/\nignored.txt\n");
  writeFileSync(join(root, "ignored.txt"), "baseline\n");
  cpSync(join(ROOT, "verification", "policy.json"), join(root, "policy.json"));
  cpSync(join(ROOT, "verification", "lenses", "non-phase.json"), join(root, "lenses.json"));
  const manifest = {
    $schema: "manifest.schema.json",
    version: 1,
    id: "mini",
    title: "Mini target",
    targets: [
      {
        path: "target.md",
        role: "target",
        selectors: [
          { id: "public", selector: { start: "^## Public$", end: "^## Internal$" } },
        ],
      },
    ],
    authoritative_sources: [
      {
        path: "spec.md",
        role: "spec",
        selectors: [
          { id: "required", selector: { start: "^## Required$", end: "^## Gate$" } },
        ],
      },
    ],
    supporting_evidence: [{ path: "evidence.md", role: "evidence" }],
    dependencies: [
      {
        path: "dep.md",
        role: "dependency",
        binding_contract: { start: "^## Binding$", end: "^## Internal$" },
      },
    ],
    prior_reviews: {
      directory: "reviews",
      filename_regex: "^review_(?<round>\\d+)\\.md$",
      round_group: "round",
      read_order: "adjudicator-last",
      allow_gaps: false,
    },
    forbidden_sources: {
      path_patterns: ["docs/**/chatlogs/**", "*.txt"],
      provenance_patterns: ["session transcript"],
    },
    immutable_paths: ["spec.md", "dep.md", "evidence.md", "reviews/*.md"],
    write_permissions: {
      adjudicator: ["{review_output}"],
      fixup: ["target.md", "{review_output}"],
    },
    output: {
      directory: "reviews",
      review_template: "review_{round}.md",
      journal_directory: ".runs",
    },
    attack_lenses: "lenses.json",
    lens_order: {
      first_review: ["contract", "edge-cases", "consistency"],
      mature: ["consistency", "contract", "edge-cases"],
    },
    citation: {
      format: "repo-relative-line-quote",
      resolver: "line-quote",
      allow_unique_relocation: true,
    },
    interface_regions: [
      {
        id: "public",
        path: "target.md",
        selector: { start: "^## Public$", end: "^## Internal$" },
        change_trigger: "re-review",
      },
    ],
    policy: "policy.json",
    target_context: {},
    fixup_instructions: "Minimal correction only.",
  };
  writeJson(join(root, "verification", "targets", "mini.json"), manifest);
  return { root, manifest };
}

function fixtureEvidence() {
  return {
    path: "target.md",
    line_start: 5,
    line_end: 5,
    quote: "alpha",
    shows: "the public value",
  };
}

function writeReview(root, path, verdict, counts, admitted = []) {
  const findings = admitted.map((id) => `### ${id}\n\nConfirmed correction.\n`).join("\n");
  writeFileSync(
    join(root, path),
    [
      "## 0. Method and reading order",
      "",
      "Independent derivation completed.",
      "",
      "## 1. Findings",
      "",
      findings,
      "## 2. Checked and clean",
      "",
      "Remaining areas checked.",
      "",
      "## 3. Verdict",
      "",
      `# ${verdict}`,
      `Counts: blocking=${counts.blocking}; corrections=${counts.corrections}; notes=${counts.notes}`,
      "Interface changed: no",
      "",
    ].join("\n"),
  );
}

function makeFakeRunner(root, calls, {
  secondRoundPass = false,
  fixupTargetAppend = "",
} = {}) {
  const interfaceText = "## Public\n\nalpha\n";
  const interfaceHash = createHash("sha256").update(interfaceText).digest("hex");
  return async ({ label, prompt, sandbox }) => {
    calls.push({ label, prompt, sandbox });
    if (label.startsWith("attack:")) {
      const isFirstLens = label.startsWith("attack:contract:R1");
      return {
        findings: isFirstLens
          ? [{
            candidate_id: "raw-1",
            title: "Public value needs correction",
            location: "target.md:5",
            claim_under_test: "alpha is valid",
            evidence: [fixtureEvidence()],
            severity: "correction",
            touches_interface: false,
            not_already_covered_because: "first review",
            recommended_fix: "clarify alpha",
          }]
          : [],
        areas_examined: ["public contract"],
        clean_areas: "No other issue.",
      };
    }
    if (label.startsWith("refute:")) {
      return {
        candidate_id: "candidate-001",
        verdict: "CONFIRMED",
        severity_should_be: "correction",
        touches_interface: false,
        strongest_refutation_attempted: "Checked whether authority permits it.",
        why_it_held_or_failed: "The cited value remains material.",
        evidence: [fixtureEvidence()],
        recommended_fix: "clarify alpha",
      };
    }
    if (label.startsWith("gate:")) {
      return {
        results: [{
          candidate_id: "candidate-001",
          anchor_ok: true,
          detail: "all anchors resolve",
          corrected_evidence: [],
        }],
        summary: "complete",
      };
    }
    if (label.startsWith("adjudicate:R1")) {
      writeReview(root, "reviews/review_1.md", "PASS-WITH-CORRECTIONS", {
        blocking: 0,
        corrections: 1,
        notes: 0,
      }, ["candidate-001"]);
      return {
        verdict: "PASS-WITH-CORRECTIONS",
        blocking_count: 0,
        correction_count: 1,
        note_count: 0,
        interface_changed: false,
        candidate_dispositions: [{
          candidate_id: "candidate-001",
          disposition: "ADMITTED",
          final_severity: "correction",
          touches_interface: false,
          rationale: "confirmed on independent derivation",
        }],
        review_file: "reviews/review_1.md",
        rationale: "one correction",
        findings_dropped_on_derivation: "",
      };
    }
    if (label.startsWith("fixup")) {
      const review = label.includes("continuation") ? "reviews/review_1.md" : "reviews/review_1.md";
      const before = readFileSync(join(root, review), "utf8");
      writeFileSync(join(root, review), `${before}\n## Resolutions\n\nCorrection recorded.\n`);
      if (fixupTargetAppend) {
        const targetBefore = readFileSync(join(root, "target.md"), "utf8");
        writeFileSync(join(root, "target.md"), `${targetBefore}${fixupTargetAppend}`);
      }
      return {
        sites_edited: fixupTargetAppend
          ? ["review resolution", "target append"]
          : ["review resolution"],
        interface_regions: [{
          id: "public",
          hash_before: interfaceHash,
          hash_after: interfaceHash,
          unchanged: true,
        }],
        addendum_lines: 3,
        new_prose_lines: 3,
        weakest_points: ["wording"],
        do_not_refight: ["resolved wording"],
        files_modified: fixupTargetAppend ? [review, "target.md"] : [review],
        refused_with_cause: "",
      };
    }
    if (label.startsWith("adjudicate:R2") && secondRoundPass) {
      writeReview(root, "reviews/review_2.md", "PASS", {
        blocking: 0,
        corrections: 0,
        notes: 0,
      });
      return {
        verdict: "PASS",
        blocking_count: 0,
        correction_count: 0,
        note_count: 0,
        interface_changed: false,
        candidate_dispositions: [],
        review_file: "reviews/review_2.md",
        rationale: "clean",
        findings_dropped_on_derivation: "",
      };
    }
    throw new Error(`Unexpected fake role ${label}`);
  };
}

function selectorRangeFromPrompt(prompt, selectorId) {
  const escaped = selectorId.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const match = prompt.match(
    new RegExp(
      `"${escaped}"\\s*:\\s*\\{[^}]*"start_line"\\s*:\\s*(\\d+),[^}]*"end_line"\\s*:\\s*(\\d+)`,
    ),
  );
  assert.ok(match, `selector ${selectorId} was not present in prompt`);
  return { start: Number(match[1]), end: Number(match[2]) };
}

test("all JSON configuration and schemas parse", () => {
  const paths = spawnSync("rg", [
    "--files",
    ".agents/skills/verify-loop/schemas",
    "verification",
    "-g",
    "*.json",
  ], { cwd: ROOT, encoding: "utf8" }).stdout.trim().split("\n").filter(Boolean);
  assert.ok(paths.length >= 10);
  for (const path of paths) assert.doesNotThrow(() => JSON.parse(readFileSync(join(ROOT, path), "utf8")), path);
});

test("manifest schema rejects missing required fields", () => {
  const schema = JSON.parse(readFileSync(join(ROOT, ".agents/skills/verify-loop/schemas/manifest.schema.json"), "utf8"));
  const errors = validateSchema({ version: 1, id: "broken" }, schema, "manifest");
  assert.ok(errors.some((error) => error.includes("missing required property targets")));
});

test("repository path containment rejects traversal and absolute paths", () => {
  assert.throws(() => resolveRepoPath(ROOT, "../outside"), /must be canonical|escapes the repository/);
  assert.throws(() => resolveRepoPath(ROOT, "/tmp/outside"), /repository-relative/);
  assert.equal(resolveRepoPath(ROOT, "verification/policy.json"), join(ROOT, "verification/policy.json"));
});

test("target selection resolves IDs and rejects missing or ambiguous matches", () => {
  assert.match(resolveManifestPath(ROOT, "phase-3"), /verification\/targets\/phase-3\.json$/);
  assert.match(resolveManifestPath(ROOT, "phase-4"), /verification\/targets\/phase-4\.json$/);
  assert.throws(() => resolveManifestPath(ROOT, "does-not-exist"), /No verification target/);

  const root = mkdtempSync(join(tmpdir(), "verify-targets-"));
  mkdirSync(join(root, "verification", "targets"), { recursive: true });
  writeJson(join(root, "verification", "targets", "first.json"), { id: "duplicate" });
  writeJson(join(root, "verification", "targets", "duplicate.json"), { id: "other" });
  assert.throws(() => resolveManifestPath(root, "duplicate"), /Ambiguous verification target/);
  rmSync(root, { recursive: true, force: true });
});

test("contract resolution validates selectors, state, and first-review behavior", () => {
  const phase1 = resolveContract(ROOT, "phase-1", { preset: "lean", maxRounds: 0 });
  assert.equal(phase1.startRound, phase1.priorReviews.length + 1);
  assert.equal(phase1.firstReview, false);
  const phase3 = resolveContract(ROOT, "phase-3", { preset: "lean", maxRounds: 0 });
  assert.equal(phase3.startRound, phase3.priorReviews.length + 1);
  assert.equal(phase3.firstReview, false);
  const phase4 = resolveContract(ROOT, "phase-4", { preset: "lean", maxRounds: 0 });
  assert.equal(phase4.startRound, phase4.priorReviews.length + 1);
  assert.equal(phase4.firstReview, false);
  const firstReview = resolveContract(ROOT, "non-phase-fixture", { preset: "lean", maxRounds: 0 });
  assert.equal(firstReview.startRound, 1);
  assert.equal(firstReview.firstReview, true);
  assert.equal(isFirstReviewRound(firstReview, 0), true);
  assert.equal(isFirstReviewRound(firstReview, 1), false);
  assert.equal(isFirstReviewRound(phase1, 0), false);
  assert.equal(phase3.resolvedSelectors["authority[0]:target_spec"].start_line, 1316);
  assert.throws(
    () => resolveContract(ROOT, "phase-2", { preset: "lean", startRound: 1, maxRounds: 0 }),
    /conflicts with discovered review state/,
  );
});

test("missing inputs, selector ambiguity, review gaps, and write conflicts fail loudly", () => {
  const { root, manifest } = makeMiniRepo();
  const manifestPath = join(root, "verification", "targets", "mini.json");
  assert.doesNotThrow(() => resolveContract(root, "mini", { maxRounds: 0 }));

  unlinkSync(join(root, "target.md"));
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /Missing target\[0\] artifact/);
  writeFileSync(join(root, "target.md"), "# Target\n\n## Public\n\nalpha\n\n## Internal\n\nprivate\n");

  writeFileSync(join(root, "spec.md"), "# Spec\n\n## Required\n\none\n\n## Required\n\ntwo\n\n## Gate\n");
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /matched 2 lines/);
  writeFileSync(join(root, "spec.md"), "# Spec\n\n## Required\n\nalpha\n\n## Gate\n\ncomplete\n");

  writeFileSync(join(root, "reviews", "review_2.md"), "# PASS\n");
  assert.throws(() => discoverPriorReviews(root, manifest), /sequence has a gap/);
  unlinkSync(join(root, "reviews", "review_2.md"));

  manifest.immutable_paths.push("target.md");
  writeJson(manifestPath, manifest);
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /Write permission conflicts/);
  rmSync(root, { recursive: true, force: true });
});

test("stage ordering and zero-agent dry-run are deterministic", () => {
  assert.deepEqual(STAGE_ORDER, ["Attack", "Refute", "Steelman", "Gate", "Adjudicate", "Fix-up"]);
  const contract = resolveContract(ROOT, "non-phase-fixture", { maxRounds: 0, preset: "lean" });
  const plan = dryRunPlan(contract, { reviewOnly: true });
  assert.equal(plan.outcome, "DRY-RUN");
  assert.equal(plan.target_id, "non-phase-fixture");
  assert.deepEqual(plan.stage_order, STAGE_ORDER);
  assert.equal(plan.barriers, true);
  assert.equal(plan.resolved_selectors["interface:public-contract"].path, "verification/fixtures/non-phase/TARGET.md");
});

test("dry-run estimates include one conditional Refute correction per result", () => {
  const contract = resolveContract(ROOT, "non-phase-fixture", { maxRounds: 1, preset: "lean" });
  const plan = dryRunPlan(contract);
  assert.equal(plan.estimates.assumptions.maximum_refute_corrections_per_result, 1);
  assert.deepEqual(plan.estimates.per_round_agent_calls, {
    attack: 3,
    refute: 12,
    refute_correction: 12,
    steelman: 0,
    gate: 1,
    adjudicate: 1,
    fixup: 1,
    total: 30,
  });
  assert.equal(plan.estimates.maximum_agent_calls, 30);
  assert.equal(plan.estimates.estimated_input_tokens, 1_350_000);
  assert.equal(plan.estimates.estimated_output_tokens, 150_000);
});

test("dedupe and refuter survival/severity policy match the legacy contract", () => {
  const duplicate = {
    candidate_id: "raw",
    title: "Mismatch here",
    location: "§4 line 10",
    evidence: [],
  };
  assert.equal(dedupeCandidates([duplicate, { ...duplicate, candidate_id: "raw-2" }]).length, 1);
  const policy = resolveContract(ROOT, "non-phase-fixture", { maxRounds: 0 }).policy;
  const candidate = { candidate_id: "candidate-001", severity: "correction", touches_interface: false };
  const tieSurvives = aggregateRefutations(candidate, [
    { verdict: "REFUTED", severity_should_be: "none", touches_interface: false },
    { verdict: "CONFIRMED", severity_should_be: "blocking", touches_interface: true },
  ], policy);
  assert.equal(tieSurvives.severity, "blocking");
  assert.equal(tieSurvives.touches_interface, true);
  assert.equal(aggregateRefutations(candidate, [
    { verdict: "REFUTED", severity_should_be: "none", touches_interface: false },
    { verdict: "REFUTED", severity_should_be: "none", touches_interface: false },
  ], policy), null);
  const upperMedian = aggregateRefutations(candidate, [
    { verdict: "CONFIRMED", severity_should_be: "note", touches_interface: true },
    { verdict: "OVERSTATED", severity_should_be: "blocking", touches_interface: false },
  ], policy);
  assert.equal(upperMedian.severity, "blocking");
  assert.equal(upperMedian.touches_interface, false);
});

test("citation resolver rejects bad evidence and uniquely relocates stale lines", () => {
  const contract = resolveContract(ROOT, "non-phase-fixture", { maxRounds: 0 });
  const base = {
    path: "verification/fixtures/non-phase/TARGET.md",
    line_start: 1,
    line_end: 1,
    quote: "Accepted names are `alpha` and `beta`.",
    shows: "accepted vocabulary",
  };
  const relocated = resolveCitation(ROOT, contract.manifest, base);
  assert.equal(relocated.ok, true);
  assert.equal(relocated.relocated, true);
  assert.equal(relocated.evidence.line_start, 7);
  const bad = resolveCitation(ROOT, contract.manifest, { ...base, quote: "not present" });
  assert.equal(bad.ok, false);
  const forbidden = resolveCitation(ROOT, contract.manifest, {
    ...base,
    path: "docs/phase1/chatlogs/session.md",
  });
  assert.equal(forbidden.ok, false);
  const candidate = resolveCandidateEvidence(ROOT, contract.manifest, {
    candidate_id: "x",
    evidence: [{ ...base, quote: "not present" }],
  });
  assert.equal(candidate.ok, false);
  assert.match(candidate.detail, /x finder evidence/);
  assert.doesNotMatch(candidate.detail, /refuter \d+ evidence/);
  const nested = resolveCandidateEvidence(ROOT, contract.manifest, {
    candidate_id: "nested",
    evidence: [{ ...base, line_start: 7, line_end: 7 }],
    refutations: [{ evidence: [{ ...base, quote: "not present" }] }],
    steelman: { evidence: [{ ...base, line_start: 7, line_end: 7 }] },
  });
  assert.equal(nested.ok, false);
  assert.match(nested.detail, /refuter 1 evidence/);
  const original = {
    candidate_id: "candidate-001",
    evidence: [{ ...base, line_start: 7, line_end: 7 }],
  };
  assert.throws(
    () => applyGateEvidenceCorrection(original, [{
      ...original.evidence[0],
      path: "verification/fixtures/non-phase/SPEC.md",
    }]),
    /changed path/,
  );
});

test("permitted-write check distinguishes allowed, unauthorized, added, and deleted files", () => {
  const before = new Map([["target.md", "a"], ["immutable.md", "x"]]);
  const after = new Map([["target.md", "b"], ["new.md", "n"]]);
  assert.deepEqual(changedPaths(before, after), ["immutable.md", "new.md", "target.md"]);
  const checked = verifyPermittedWrites(before, after, ["target.md"]);
  assert.deepEqual(checked.unauthorized, ["immutable.md", "new.md"]);
});

test("literal PASS, verdict consistency, and stop conditions are enforced", () => {
  const root = mkdtempSync(join(tmpdir(), "verify-verdict-"));
  spawnSync("git", ["init", "-q"], { cwd: root });
  writeReview(root, "review.md", "PASS", { blocking: 0, corrections: 0, notes: 0 });
  const pass = {
    verdict: "PASS",
    blocking_count: 0,
    correction_count: 0,
    note_count: 0,
    interface_changed: false,
    candidate_dispositions: [],
    review_file: "review.md",
    rationale: "clean",
    findings_dropped_on_derivation: "",
  };
  assert.doesNotThrow(() => validateAdjudication(root, "review.md", pass));
  assert.equal(adjudicationStop(pass), "PASS");
  assert.throws(() => validateAdjudication(root, "review.md", { ...pass, correction_count: 1 }), /Literal PASS/);
  writeReview(root, "review.md", "PASS-WITH-CORRECTIONS", {
    blocking: 0,
    corrections: 1,
    notes: 0,
  }, ["candidate-001"]);
  const corrections = {
    ...pass,
    verdict: "PASS-WITH-CORRECTIONS",
    correction_count: 1,
    candidate_dispositions: [{
      candidate_id: "candidate-001",
      disposition: "ADMITTED",
      final_severity: "correction",
      touches_interface: false,
      rationale: "confirmed",
    }],
  };
  const correctionCandidate = [{
    candidate_id: "candidate-001",
    severity: "correction",
    touches_interface: false,
  }];
  assert.doesNotThrow(() => validateAdjudication(root, "review.md", corrections, correctionCandidate));
  assert.equal(adjudicationStop(corrections), "CONTINUE");
  assert.equal(adjudicationStop(corrections, { reviewOnly: true }), "REVIEW-ONLY");
  writeReview(root, "review.md", "PASS", {
    blocking: 0,
    corrections: 0,
    notes: 1,
  }, ["candidate-001"]);
  const reclassified = {
    ...pass,
    note_count: 1,
    candidate_dispositions: [{
      candidate_id: "candidate-001",
      disposition: "ADMITTED",
      final_severity: "note",
      touches_interface: false,
      rationale: "independent adjudication downgraded the surviving correction",
    }],
  };
  assert.doesNotThrow(() => validateAdjudication(root, "review.md", reclassified, correctionCandidate));
  writeReview(root, "review.md", "FAIL", { blocking: 0, corrections: 0, notes: 0 });
  assert.equal(adjudicationStop({ ...pass, verdict: "FAIL" }), "FAIL");
  assert.throws(
    () => validateAdjudication(root, "review.md", { ...pass, verdict: "FAIL", blocking_count: 1 }),
    /counts do not match/,
  );
  rmSync(root, { recursive: true, force: true });
});

test("forbidden patterns are provenance-shaped rather than filename enumerations", () => {
  const chatlogs = globToRegExp("docs/**/chatlogs/**");
  assert.equal(chatlogs.test("docs/phase99/chatlogs/new-export.md"), true);
  assert.equal(chatlogs.test("docs/reference/x/chatlogs/session.txt"), true);
  assert.equal(chatlogs.test("docs/phase99/reviews/review.md"), false);
  const rootTxt = globToRegExp("*.txt");
  assert.equal(rootTxt.test("2026-07-27-session.txt"), true);
  assert.equal(rootTxt.test("docs/safe.txt"), false);
});

test("forbidden citations reject traversal aliases, symlink aliases, directories, and non-unique relocation", () => {
  const { root, manifest } = makeMiniRepo();
  mkdirSync(join(root, "sub"), { recursive: true });
  writeFileSync(join(root, "session.txt"), "header\nalpha alpha\n");
  symlinkSync("session.txt", join(root, "safe.md"));
  const evidence = {
    path: "sub/../session.txt",
    line_start: 1,
    line_end: 1,
    quote: "alpha",
    shows: "transcript text",
  };
  assert.equal(resolveCitation(root, manifest, evidence).ok, false);
  assert.equal(resolveCitation(root, manifest, { ...evidence, path: "safe.md" }).ok, false);
  assert.equal(resolveCitation(root, manifest, { ...evidence, path: "reviews" }).ok, false);
  assert.equal(resolveCitation(root, manifest, {
    ...evidence,
    path: "session.txt",
  }).ok, false);
  manifest.forbidden_sources.path_patterns = [];
  assert.match(
    resolveCitation(root, manifest, { ...evidence, path: "session.txt" }).detail,
    /matches: 2/,
  );
  rmSync(root, { recursive: true, force: true });
});

test("selector endings and manifest cross-field invariants fail on ambiguity or conflict", () => {
  const { root, manifest } = makeMiniRepo();
  const manifestPath = join(root, "verification", "targets", "mini.json");
  writeFileSync(
    join(root, "spec.md"),
    "# Spec\n\n## Required\n\nalpha\n\n## Gate\n\none\n\n## Gate\n\ntwo\n",
  );
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /mode unique-after-start/);
  writeFileSync(join(root, "spec.md"), "# Spec\n\n## Required\n\nalpha\n\n## Gate\n\ncomplete\n");

  manifest.output.directory = "other-reviews";
  mkdirSync(join(root, "other-reviews"));
  writeJson(manifestPath, manifest);
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /must equal prior_reviews/);

  manifest.output.directory = "reviews";
  manifest.interface_regions.push({ ...manifest.interface_regions[0] });
  writeJson(manifestPath, manifest);
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /region IDs must be unique/);

  manifest.interface_regions.pop();
  manifest.immutable_paths.push("typo-that-matches-nothing/**");
  writeJson(manifestPath, manifest);
  assert.throws(() => resolveContract(root, "mini", { maxRounds: 0 }), /Immutable pattern matches no/);
  rmSync(root, { recursive: true, force: true });
});

test("fix-up validation is append-only", () => {
  const review = "## 0. Method\n\noriginal\n";
  assert.doesNotThrow(() => validateFixupAppend(review, `${review}\n## Resolutions\n\nfixed\n`));
  assert.throws(
    () => validateFixupAppend(review, "rewritten\n## Resolutions\n\nfixed\n"),
    /rewrote existing review/,
  );
  assert.throws(
    () => validateFixupAppend(review, `${review}\nNo resolution heading\n`),
    /exactly one/,
  );
});

test("fake-agent execution proves barriers, full loop stages, and first-to-mature transition", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 2 });
  const calls = [];
  const log = [];
  const result = await executeVerification(contract, {
    agentRunner: makeFakeRunner(root, calls, { secondRoundPass: true }),
    logger: (message) => log.push(message),
  });
  assert.equal(result.outcome, "PASS");
  assert.equal(result.rounds_run, 2);
  assert.equal(result.resolution_records.length, 1);
  assert.equal(result.resolution_records[0].round, 1);
  assert.deepEqual(log, [
    "Round 1 Attack",
    "Round 1 Refute (1 candidate(s))",
    "Round 1 Steelman (disabled)",
    "Round 1 Gate",
    "Round 1 Adjudicate",
    "Round 1 Fix-up",
    "Round 2 Attack",
    "Round 2 Refute (0 candidate(s))",
    "Round 2 Steelman (disabled)",
    "Round 2 Gate",
    "Round 2 Adjudicate",
  ]);
  const attackLabels = calls.filter((call) => call.label.startsWith("attack:")).map((call) => call.label);
  assert.deepEqual(attackLabels, [
    "attack:contract:R1",
    "attack:edge-cases:R1",
    "attack:consistency:R1",
    "attack:consistency:R2",
    "attack:contract:R2",
    "attack:edge-cases:R2",
  ]);
  assert.match(calls.find((call) => call.label === "attack:contract:R1").prompt, /entire target is unreviewed/);
  const round2Attack = calls.find((call) => call.label === "attack:consistency:R2");
  assert.match(round2Attack.prompt, /1 review round\(s\) already exist/);
  assert.match(
    round2Attack.prompt,
    /"discovered_deny_list": \[\s*"reviews\/review_1\.md"\s*\]/,
  );
  const refuterPrompt = calls.find((call) => call.label === "refute:candidate-001:1:R1").prompt;
  assert.match(refuterPrompt, /finder evidence is already preserved by the orchestrator/);
  assert.match(refuterPrompt, /Do not copy that\s+evidence into your own `evidence` array/);
  assert.match(refuterPrompt, /Never reflow wrapped lines/);
  const adjudicatorPrompt = calls.find((call) => call.label === "adjudicate:R1").prompt;
  assert.match(adjudicatorPrompt, /"path": "target\.md"/);
  assert.match(adjudicatorPrompt, /"path": "spec\.md"/);
  assert.match(adjudicatorPrompt, /Candidates eliminated before adjudication/);
  assert.match(
    calls.find((call) => call.label === "adjudicate:R2").prompt,
    /prior reviews listed below[\s\S]*"round": 1,\s*"path": "reviews\/review_1\.md"/i,
  );
  assert.match(readFileSync(join(root, "reviews", "review_1.md"), "utf8"), /^## Resolutions$/m);
  rmSync(root, { recursive: true, force: true });
});

test("round refresh dispatches post-fix-up selector bounds to Attack and Adjudicate", async () => {
  const { root, manifest } = makeMiniRepo();
  manifest.targets[0].selectors[0].selector = { start: "^# Target$" };
  writeJson(join(root, "verification", "targets", "mini.json"), manifest);
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 2 });
  const initialEnd = contract.resolvedSelectors["target[0]:public"].end_line;
  const appendedText = "\n## Added contract\n\nnewly appended substantive requirement\n";
  const calls = [];
  const result = await executeVerification(contract, {
    agentRunner: makeFakeRunner(root, calls, {
      secondRoundPass: true,
      fixupTargetAppend: appendedText,
    }),
    logger: () => {},
  });
  assert.equal(result.outcome, "PASS");

  const finalLines = readFileSync(join(root, "target.md"), "utf8").split(/\r?\n/);
  const appendedLine = finalLines.indexOf("newly appended substantive requirement") + 1;
  const refreshedEnd = contract.resolvedSelectors["target[0]:public"].end_line;
  assert.ok(refreshedEnd > initialEnd);
  assert.equal(refreshedEnd, finalLines.length);

  for (const label of ["attack:consistency:R2", "adjudicate:R2"]) {
    const range = selectorRangeFromPrompt(
      calls.find((call) => call.label === label).prompt,
      "target[0]:public",
    );
    assert.equal(range.end, refreshedEnd, `${label} must receive the refreshed EOF`);
    assert.notEqual(range.end, initialEnd, `${label} must not receive the pre-fix-up endpoint`);
    assert.ok(appendedLine >= range.start && appendedLine <= range.end);
  }
  rmSync(root, { recursive: true, force: true });
});

test("round refresh fails closed before Attack when a fix-up invalidates a selector", async () => {
  const { root, manifest } = makeMiniRepo();
  manifest.targets[0].selectors[0].selector = { start: "^# Target$" };
  writeJson(join(root, "verification", "targets", "mini.json"), manifest);
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 2 });
  const calls = [];
  await assert.rejects(
    executeVerification(contract, {
      agentRunner: makeFakeRunner(root, calls, {
        secondRoundPass: true,
        fixupTargetAppend: "\n# Target\n\nambiguous post-fix-up surface\n",
      }),
      logger: () => {},
    }),
    (error) => {
      assert.equal(error.code, "VALIDATION_ERROR");
      assert.match(error.message, /target\[0\]:public start.*matched 2 lines/);
      assert.equal(error.details.stage, "R2 Context refresh");
      assert.match(error.details.journal, /^\.runs\//);
      const journal = JSON.parse(readFileSync(join(root, error.details.journal), "utf8"));
      assert.equal(journal.rounds[1].context_refresh.status, "error");
      assert.match(journal.rounds[1].context_refresh.error.message, /matched 2 lines/);
      assert.match(journal.pending_work, /failed revalidation before Attack/);
      return true;
    },
  );
  assert.equal(calls.some((call) => call.label.startsWith("attack:") && call.label.endsWith(":R2")), false);
  rmSync(root, { recursive: true, force: true });
});

test("Refute replaces invalid additional evidence with one valid correction inside its barrier", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const baseRunner = makeFakeRunner(root, calls);
  const correctedRefuterRunner = async (args) => {
    const result = await baseRunner(args);
    if (args.label !== "refute:candidate-001:1:R1") return result;
    return {
      ...result,
      evidence: [{ ...fixtureEvidence(), quote: "not present" }],
    };
  };
  const result = await executeVerification(contract, {
    agentRunner: correctedRefuterRunner,
    logger: () => {},
  });
  assert.equal(result.outcome, "CAP");
  const correctionLabel = "refute:candidate-001:1:R1:correction";
  const correctionIndex = calls.findIndex((call) => call.label === correctionLabel);
  assert.ok(correctionIndex > -1);
  assert.equal(calls.filter((call) => call.label === correctionLabel).length, 1);
  assert.equal(calls[correctionIndex].sandbox, "read-only");
  assert.match(calls[correctionIndex].prompt, /only correction attempt for candidate `candidate-001`, refuter 1/);
  assert.match(calls[correctionIndex].prompt, /candidate-001 refuter 1 evidence/);
  assert.match(calls[correctionIndex].prompt, /Return a complete replacement Refute result/);
  assert.ok(calls.findIndex((call) => call.label.startsWith("gate:")) > correctionIndex);
  assert.ok(calls.findIndex((call) => call.label.startsWith("adjudicate:")) > correctionIndex);
  assert.ok(calls.findIndex((call) => call.label.startsWith("fixup:")) > correctionIndex);
  rmSync(root, { recursive: true, force: true });
});

test("Refute fails closed after one invalid correction and attributes refuter evidence accurately", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const baseRunner = makeFakeRunner(root, calls);
  const invalidRefuterRunner = async (args) => {
    const result = await baseRunner(args);
    if (!args.label.startsWith("refute:")) return result;
    return {
      ...result,
      evidence: [{ ...fixtureEvidence(), quote: "not present" }],
    };
  };
  await assert.rejects(
    executeVerification(contract, {
      agentRunner: invalidRefuterRunner,
      logger: () => {},
    }),
    (error) => {
      assert.equal(error.code, "AGENT_ERROR");
      assert.match(error.message, /after one correction attempt/);
      assert.match(error.message, /candidate-001 refuter [12] evidence/);
      assert.doesNotMatch(error.message, /finder evidence/);
      return true;
    },
  );
  const correctionCalls = calls.filter((call) => call.label.endsWith(":correction"));
  assert.equal(correctionCalls.length, 2);
  assert.ok(correctionCalls.every((call) => call.sandbox === "read-only"));
  assert.equal(calls.some((call) => call.label.includes(":correction:correction")), false);
  assert.equal(calls.some((call) => call.label.startsWith("gate:")), false);
  assert.equal(calls.some((call) => call.label.startsWith("adjudicate:")), false);
  assert.equal(calls.some((call) => call.label.startsWith("fixup:")), false);
  rmSync(root, { recursive: true, force: true });
});

test("review-only has a validated fix-up continuation and leaves the next review unreviewed", async () => {
  const { root } = makeMiniRepo();
  const calls = [];
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const reviewed = await executeVerification(contract, {
    reviewOnly: true,
    agentRunner: makeFakeRunner(root, calls),
    logger: () => {},
  });
  assert.equal(reviewed.outcome, "REVIEW-ONLY");
  assert.equal(calls.some((call) => call.label.startsWith("fixup")), false);
  assert.doesNotMatch(readFileSync(join(root, "reviews", "review_1.md"), "utf8"), /^## Resolutions$/m);

  assert.throws(
    () => resolveContract(root, "mini", { preset: "lean", maxRounds: 1 }),
    /use --fixup-review latest/,
  );
  const continuationContract = resolveContract(root, "mini", {
    preset: "lean",
    maxRounds: 1,
    fixupReview: "latest",
  });
  const pending = resolveFixupReview(continuationContract, "latest");
  assert.equal(pending.path, "reviews/review_1.md");
  const plan = dryRunFixupPlan(continuationContract, "latest");
  assert.equal(plan.outcome, "DRY-RUN-FIXUP");
  assert.equal(plan.estimates.maximum_agent_calls, 1);
  const continuationCalls = [];
  const fixed = await executeFixupContinuation(continuationContract, {
    fixupReview: "latest",
    agentRunner: makeFakeRunner(root, continuationCalls),
  });
  assert.equal(fixed.outcome, "FIXUP-COMPLETE");
  assert.deepEqual(fixed.resolution_record.sites_edited, ["review resolution"]);
  assert.equal(fixed.next_review, "reviews/review_2.md");
  assert.match(readFileSync(join(root, "reviews", "review_1.md"), "utf8"), /^## Resolutions$/m);
  rmSync(root, { recursive: true, force: true });
});

test("writer failures still enforce ignored-file and mode changes and report the recovery journal", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const failingRunner = async ({ label }) => {
    if (label.startsWith("attack:")) {
      return { findings: [], areas_examined: ["public"], clean_areas: "clean" };
    }
    if (label.startsWith("adjudicate:")) {
      chmodSync(join(root, "ignored.txt"), 0o600);
      symlinkSync("missing-target", join(root, "broken-link"));
      throw new Error("writer failed after mutation");
    }
    throw new Error(`Unexpected fake role ${label}`);
  };
  await assert.rejects(
    executeVerification(contract, { agentRunner: failingRunner, logger: () => {} }),
    (error) => {
      assert.equal(error.code, "WRITE_VIOLATION");
      assert.match(error.message, /ignored\.txt/);
      assert.match(error.message, /broken-link/);
      assert.match(error.details.journal, /^\.runs\//);
      assert.equal(error.details.agent_error.message, "writer failed after mutation");
      return true;
    },
  );
  rmSync(root, { recursive: true, force: true });
});

test("dangling output and ancestor symlinks fail closed during preflight", () => {
  const first = makeMiniRepo();
  symlinkSync("missing-review", join(first.root, "reviews", "review_1.md"));
  assert.throws(
    () => resolveContract(first.root, "mini", { maxRounds: 1 }),
    /already exists|dangling or unresolvable symlink/,
  );
  rmSync(first.root, { recursive: true, force: true });

  const second = makeMiniRepo();
  const manifestPath = join(second.root, "verification", "targets", "mini.json");
  symlinkSync("/tmp/verify-engine-never-exists", join(second.root, "dangling"));
  second.manifest.targets[0].path = "dangling/file.md";
  writeJson(manifestPath, second.manifest);
  assert.throws(
    () => resolveContract(second.root, "mini", { maxRounds: 0 }),
    /dangling or unresolvable symlink/,
  );
  rmSync(second.root, { recursive: true, force: true });
});

test("all internal role prompts contain the recursion and subagent guard", () => {
  const promptDirectory = join(ROOT, ".agents", "skills", "verify-loop", "prompts");
  for (const name of ["common-readonly.md", "adjudicate.md", "fixup.md"]) {
    const text = readFileSync(join(promptDirectory, name), "utf8");
    assert.match(text, /Do not invoke `\$verify-loop`/);
    assert.match(text, /spawn\/delegate to subagents/);
  }
});
