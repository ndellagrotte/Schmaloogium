import test from "node:test";
import assert from "node:assert/strict";
import {
  chmodSync,
  cpSync,
  existsSync,
  mkdirSync,
  mkdtempSync,
  readdirSync,
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
import { Worker } from "node:worker_threads";
import {
  STAGE_ORDER,
  applyGateEvidenceCorrection,
  adjudicationStop,
  aggregateRefutations,
  assertTargetAvailable,
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
import {
  createProgressReporter,
  formatDuration,
  progressSinks,
  renderProgressEvent,
  shortRoleName,
} from "../.agents/skills/verify-loop/scripts/progress.mjs";

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
    // All five lens ids, so the fixture can execute the thorough preset too.
    // lean slices the first three, which keeps its expected dispatch order.
    lens_order: {
      first_review: ["contract", "edge-cases", "consistency", "dependencies", "operability"],
      mature: ["consistency", "contract", "edge-cases", "dependencies", "operability"],
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

function addSecondMiniTarget(root, sourceManifest, id = "mini-b") {
  const manifest = structuredClone(sourceManifest);
  manifest.id = id;
  manifest.title = "Second mini target";
  manifest.targets[0].path = "target-b.md";
  manifest.prior_reviews.directory = "reviews-b";
  manifest.immutable_paths = ["spec.md", "dep.md", "evidence.md", "reviews-b/*.md"];
  manifest.write_permissions = {
    adjudicator: ["{review_output}"],
    fixup: ["target-b.md", "{review_output}"],
  };
  manifest.output = {
    directory: "reviews-b",
    review_template: "review_{round}.md",
    journal_directory: `.runs/${id}`,
  };
  manifest.interface_regions[0].path = "target-b.md";
  mkdirSync(join(root, "reviews-b"));
  writeFileSync(join(root, "target-b.md"), readFileSync(join(root, "target.md"), "utf8"));
  writeJson(join(root, "verification", "targets", `${id}.json`), manifest);
  return manifest;
}

function runConcurrencyWorker(root, target, rendezvous) {
  const worker = join(ROOT, "tests", "verify-concurrency-worker.mjs");
  return new Promise((resolvePromise, rejectPromise) => {
    const child = new Worker(worker, {
      workerData: { root, target, rendezvous },
    });
    let result;
    child.on("message", (message) => { result = message; });
    child.on("error", rejectPromise);
    child.on("exit", (code) => {
      if (code !== 0 || !result) {
        rejectPromise(new Error(`Concurrency worker ${target} exited ${code} without a result`));
        return;
      }
      resolvePromise(result);
    });
  });
}

function configureMiniDesignSource(root, manifest, version = "v1") {
  writeFileSync(
    join(root, "target.md"),
    [
      "# Target",
      "## 0. Header",
      `**Governing design:** \`docs/design/${version}/DESIGN.md\``,
      "## Public",
      "alpha",
      "## Internal",
      "private",
      "",
    ].join("\n"),
  );
  for (const designVersion of ["v1", "v2"]) {
    mkdirSync(join(root, "docs", "design", designVersion), { recursive: true });
    writeFileSync(
      join(root, "docs", "design", designVersion, "DESIGN.md"),
      `# Design ${designVersion}\n\n## Required\n\nalpha\n\n## Gate\n\ncomplete\n`,
    );
  }
  manifest.targets[0].selectors[0].selector = {
    start: "^## Public$",
    end: "^## Internal$",
  };
  manifest.interface_regions[0].selector = {
    start: "^## Public$",
    end: "^## Internal$",
  };
  manifest.design_revision = {
    target_index: 0,
    section_zero: { start: "^## 0\\. Header$", end: "^## Public$" },
    declaration_pattern: "^\\*\\*Governing design:\\*\\* `(?<path>docs/design/[A-Za-z0-9._-]+/DESIGN\\.md)`$",
  };
  manifest.authoritative_sources[0].path = "docs/design/{design_version}/DESIGN.md";
  manifest.immutable_paths.push("docs/design/**");
  writeJson(join(root, "verification", "targets", "mini.json"), manifest);
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
  fixupInterfaceValue = "",
  fixupDesignVersion = "",
  reportedInterfaceRegions,
  steelmanOutcome = "uphold",
} = {}) {
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
    if (label.startsWith("steelman:")) {
      const defended = steelmanOutcome === "defend";
      return {
        candidate_id: "candidate-001",
        best_defence: "The authority permits the cited wording.",
        defence_holds: defended,
        residual_defect: defended ? "" : "The wording is still ambiguous.",
        final_severity: defended ? "none" : "correction",
        touches_interface: false,
        evidence: [fixtureEvidence()],
        disagreement_with_refuters: defended ? "Refuters overstated the defect." : "",
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
    if (label.startsWith("adjudicate:R1") && steelmanOutcome === "defend") {
      writeReview(root, "reviews/review_1.md", "PASS", { blocking: 0, corrections: 0, notes: 0 });
      return {
        verdict: "PASS",
        blocking_count: 0,
        correction_count: 0,
        note_count: 0,
        interface_changed: false,
        candidate_dispositions: [],
        review_file: "reviews/review_1.md",
        rationale: "the steelman defence held",
        findings_dropped_on_derivation: "candidate-001 dropped at Steelman",
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
      const targetBefore = readFileSync(join(root, "target.md"), "utf8");
      let targetAfter = targetBefore;
      if (fixupInterfaceValue) {
        const currentRegion = "## Public\n\nalpha\n\n## Internal";
        assert.ok(targetAfter.includes(currentRegion));
        targetAfter = targetAfter.replace(
          currentRegion,
          `## Public\n\n${fixupInterfaceValue}\n\n## Internal`,
        );
      }
      if (fixupDesignVersion) {
        targetAfter = targetAfter.replace(
          /docs\/design\/[A-Za-z0-9._-]+\/DESIGN\.md/,
          `docs/design/${fixupDesignVersion}/DESIGN.md`,
        );
      }
      targetAfter += fixupTargetAppend;
      const targetChanged = targetAfter !== targetBefore;
      if (targetChanged) writeFileSync(join(root, "target.md"), targetAfter);
      const result = {
        sites_edited: targetChanged
          ? ["review resolution", "target append"]
          : ["review resolution"],
        addendum_lines: 3,
        new_prose_lines: 3,
        weakest_points: ["wording"],
        do_not_refight: ["resolved wording"],
        files_modified: targetChanged ? [review, "target.md"] : [review],
        refused_with_cause: "",
      };
      if (reportedInterfaceRegions !== undefined) {
        result.interface_regions = reportedInterfaceRegions;
      }
      return result;
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
  const paths = [];
  const walk = (directory) => {
    for (const entry of readdirSync(join(ROOT, directory), { withFileTypes: true })) {
      const rel = `${directory}/${entry.name}`;
      if (entry.isDirectory()) walk(rel);
      else if (entry.isFile() && entry.name.endsWith(".json")) paths.push(rel);
    }
  };
  walk(".agents/skills/verify-loop/schemas");
  walk("verification");
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
  const phase4 = resolveContract(ROOT, "phase-4", {
    preset: "lean",
    maxRounds: 0,
    fixupReview: "latest",
  });
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

test("design revision defaults to §0 and supports a fixed explicit override", () => {
  const { root, manifest } = makeMiniRepo();
  configureMiniDesignSource(root, manifest);

  const fromSectionZero = resolveContract(root, "mini", { maxRounds: 0 });
  assert.deepEqual(fromSectionZero.designRevision, {
    version: "v1",
    path: "docs/design/v1/DESIGN.md",
    selection_source: "section-zero",
    declaration_line: 3,
  });
  assert.equal(
    fromSectionZero.resolvedSelectors["authority[0]:required"].path,
    "docs/design/v1/DESIGN.md",
  );

  const overridden = resolveContract(root, "mini", { maxRounds: 0, designVersion: "v2" });
  assert.deepEqual(overridden.designRevision, {
    version: "v2",
    path: "docs/design/v2/DESIGN.md",
    selection_source: "override",
  });
  assert.equal(
    overridden.resolvedSelectors["authority[0]:required"].path,
    "docs/design/v2/DESIGN.md",
  );

  assert.throws(
    () => resolveContract(root, "mini", { maxRounds: 0, designVersion: "../v2" }),
    /design directory label/,
  );
  assert.throws(
    () => resolveContract(root, "mini", { maxRounds: 0, designVersion: "v9" }),
    /Missing governing design revision/,
  );

  writeFileSync(
    join(root, "target.md"),
    readFileSync(join(root, "target.md"), "utf8").replace(
      "**Governing design:** `docs/design/v1/DESIGN.md`",
      "**Governing design:** `docs/design/v1/DESIGN.md`\n**Governing design:** `docs/design/v2/DESIGN.md`",
    ),
  );
  assert.throws(
    () => resolveContract(root, "mini", { maxRounds: 0 }),
    /matched 2 declarations/,
  );
  rmSync(root, { recursive: true, force: true });
});

test("round refresh re-resolves the §0 design while an explicit override stays fixed", async () => {
  const first = makeMiniRepo();
  configureMiniDesignSource(first.root, first.manifest);
  const defaultContract = resolveContract(first.root, "mini", { preset: "lean", maxRounds: 2 });
  const defaultCalls = [];
  const defaultResult = await executeVerification(defaultContract, {
    agentRunner: makeFakeRunner(first.root, defaultCalls, {
      secondRoundPass: true,
      fixupDesignVersion: "v2",
    }),
    logger: () => {},
  });
  assert.equal(defaultResult.outcome, "PASS");
  assert.equal(defaultContract.designRevision.version, "v2");
  assert.match(
    defaultCalls.find((call) => call.label === "attack:consistency:R2").prompt,
    /"path": "docs\/design\/v2\/DESIGN\.md"/,
  );
  rmSync(first.root, { recursive: true, force: true });

  const second = makeMiniRepo();
  configureMiniDesignSource(second.root, second.manifest);
  const overrideContract = resolveContract(second.root, "mini", {
    preset: "lean",
    maxRounds: 2,
    designVersion: "v1",
  });
  const overrideCalls = [];
  const overrideResult = await executeVerification(overrideContract, {
    agentRunner: makeFakeRunner(second.root, overrideCalls, {
      secondRoundPass: true,
      fixupDesignVersion: "v2",
    }),
    logger: () => {},
  });
  assert.equal(overrideResult.outcome, "PASS");
  assert.equal(overrideContract.designRevision.version, "v1");
  assert.match(
    overrideCalls.find((call) => call.label === "attack:consistency:R2").prompt,
    /"path": "docs\/design\/v1\/DESIGN\.md"/,
  );
  rmSync(second.root, { recursive: true, force: true });
});

test("generic targets reject --design-version without changing default behavior", () => {
  const generic = resolveContract(ROOT, "non-phase-fixture", { maxRounds: 0 });
  assert.equal(generic.designRevision, null);
  assert.throws(
    () => resolveContract(ROOT, "non-phase-fixture", { maxRounds: 0, designVersion: "v3" }),
    /does not declare a design source/,
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
  assert.deepEqual(plan.coordination, { status: "available" });
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

test("parallel target workers overlap read stages and serialize writer snapshots", async () => {
  const { root, manifest } = makeMiniRepo();
  addSecondMiniTarget(root, manifest);
  const rendezvous = mkdtempSync(join(tmpdir(), "verify-concurrency-"));
  try {
    const settled = await Promise.allSettled([
      runConcurrencyWorker(root, "mini", rendezvous),
      runConcurrencyWorker(root, "mini-b", rendezvous),
    ]);
    for (const result of settled) {
      if (result.status === "rejected") throw result.reason;
    }
    const [first, second] = settled.map((result) => result.value);
    assert.equal(first.outcome, "PASS");
    assert.equal(second.outcome, "PASS");
    assert.match(readFileSync(join(root, "reviews", "review_1.md"), "utf8"), /^# PASS$/m);
    assert.match(readFileSync(join(root, "reviews-b", "review_1.md"), "utf8"), /^# PASS$/m);
  } finally {
    rmSync(rendezvous, { recursive: true, force: true });
    rmSync(root, { recursive: true, force: true });
  }
});

test("duplicate target runs and dry runs fail fast with owner diagnostics", async () => {
  const { root } = makeMiniRepo();
  const firstContract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const duplicateContract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const baseRunner = makeFakeRunner(root, calls);
  let releaseAttack;
  const attackRelease = new Promise((resolvePromise) => { releaseAttack = resolvePromise; });
  let signalAttack;
  const attackStarted = new Promise((resolvePromise) => { signalAttack = resolvePromise; });
  const blockingRunner = async (args) => {
    const result = await baseRunner(args);
    if (args.label.startsWith("attack:")) {
      signalAttack();
      await attackRelease;
    }
    return result;
  };

  const firstRun = executeVerification(firstContract, {
    reviewOnly: true,
    agentRunner: blockingRunner,
    logger: () => {},
    coordinationPollMs: 5,
    coordinationTimeoutMs: 1000,
  });
  await attackStarted;
  await assert.rejects(
    executeVerification(duplicateContract, {
      reviewOnly: true,
      agentRunner: baseRunner,
      logger: () => {},
      coordinationPollMs: 5,
      coordinationTimeoutMs: 1000,
    }),
    (error) => {
      assert.equal(error.code, "RUN_CONFLICT");
      assert.equal(error.details.owner.pid, process.pid);
      assert.equal(error.details.owner.target_id, "mini");
      assert.match(error.details.owner.journal, /^\.runs\//);
      return true;
    },
  );
  assert.throws(
    () => dryRunPlan(duplicateContract, { reviewOnly: true }),
    (error) => error.code === "RUN_CONFLICT" && error.details.owner.target_id === "mini",
  );
  assert.throws(
    () => assertTargetAvailable(root, "mini"),
    (error) => error.code === "RUN_CONFLICT" && error.details.owner.target_id === "mini",
  );
  releaseAttack();
  assert.equal((await firstRun).outcome, "REVIEW-ONLY");
  rmSync(root, { recursive: true, force: true });
});

test("dead leases are reclaimed and live workspace leases time out without dispatch", async () => {
  const first = makeMiniRepo();
  const staleTarget = join(first.root, ".verification-runs", ".locks", "targets", "mini");
  mkdirSync(staleTarget, { recursive: true });
  writeJson(join(staleTarget, "owner.json"), {
    version: 1,
    pid: 2147483647,
    nonce: "dead-owner",
    kind: "target-run",
    target_id: "mini",
    stage: "run",
    journal: ".runs/dead.json",
    acquired_at: new Date(0).toISOString(),
    deadline_at: new Date(1).toISOString(),
  });
  const staleCalls = [];
  const recovered = await executeVerification(
    resolveContract(first.root, "mini", { preset: "lean", maxRounds: 1 }),
    {
      reviewOnly: true,
      agentRunner: makeFakeRunner(first.root, staleCalls),
      logger: () => {},
      coordinationPollMs: 5,
      coordinationTimeoutMs: 1000,
    },
  );
  assert.equal(recovered.outcome, "REVIEW-ONLY");
  assert.ok(staleCalls.length > 0);
  rmSync(first.root, { recursive: true, force: true });

  const second = makeMiniRepo();
  const liveWorkspace = join(second.root, ".verification-runs", ".locks", "workspace-mutation");
  mkdirSync(liveWorkspace, { recursive: true });
  writeJson(join(liveWorkspace, "owner.json"), {
    version: 1,
    pid: process.pid,
    nonce: "live-owner",
    kind: "workspace-mutation",
    target_id: "other-target",
    stage: "Adjudicator",
    journal: ".runs/other.json",
    acquired_at: new Date().toISOString(),
    deadline_at: new Date(Date.now() + 1000).toISOString(),
  });
  const blockedCalls = [];
  await assert.rejects(
    executeVerification(
      resolveContract(second.root, "mini", { preset: "lean", maxRounds: 1 }),
      {
        reviewOnly: true,
        agentRunner: makeFakeRunner(second.root, blockedCalls),
        logger: () => {},
        coordinationPollMs: 5,
        coordinationTimeoutMs: 25,
      },
    ),
    (error) => {
      assert.equal(error.code, "LOCK_TIMEOUT");
      assert.equal(error.details.owner.pid, process.pid);
      assert.equal(error.details.owner.target_id, "other-target");
      return true;
    },
  );
  assert.deepEqual(blockedCalls, []);
  rmSync(second.root, { recursive: true, force: true });
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
  const events = [];
  const result = await executeVerification(contract, {
    agentRunner: makeFakeRunner(root, calls, { secondRoundPass: true }),
    logger: (message) => log.push(message),
    onProgress: (event) => events.push(event),
    heartbeatMs: 0,
  });
  assert.equal(result.outcome, "PASS");
  assert.equal(result.rounds_run, 2);
  assert.equal(result.resolution_records.length, 1);
  assert.equal(result.resolution_records[0].round, 1);
  // Every stage announces entry and completion, in order, both rounds.
  const stageFlow = events
    .filter((event) => ["stage-start", "stage-end", "stage-skip", "round-start", "round-end"].includes(event.kind))
    .map((event) => `${event.kind}:${event.round}${event.stage ? `:${event.stage}` : ""}`);
  assert.deepEqual(stageFlow, [
    "round-start:1",
    "stage-start:1:Attack",
    "stage-end:1:Attack",
    "stage-start:1:Refute",
    "stage-end:1:Refute",
    "stage-skip:1:Steelman",
    "stage-start:1:Gate",
    "stage-end:1:Gate",
    "stage-start:1:Adjudicate",
    "stage-end:1:Adjudicate",
    "stage-start:1:Fix-up",
    "stage-end:1:Fix-up",
    "round-end:1",
    "round-start:2",
    "stage-start:2:Attack",
    "stage-end:2:Attack",
    "stage-start:2:Refute",
    "stage-end:2:Refute",
    "stage-skip:2:Steelman",
    "stage-start:2:Gate",
    "stage-end:2:Gate",
    "stage-start:2:Adjudicate",
    "stage-end:2:Adjudicate",
    "round-end:2",
  ]);
  // Every dispatched role session is announced when it starts and when it ends.
  const roleStarts = events.filter((event) => event.kind === "role-start").map((event) => event.role);
  const roleEnds = events.filter((event) => event.kind === "role-end").map((event) => event.role);
  assert.deepEqual(roleStarts, calls.map((call) => call.label));
  assert.deepEqual(roleEnds, roleStarts);
  assert.ok(events.every((event) => typeof event.at === "string"));
  assert.equal(
    events.filter((event) => event.kind === "role-end").every((event) => Number.isFinite(event.ms)),
    true,
  );
  // Rendered lines name the round, the stage, and the finding count.
  assert.ok(log.includes(
    "r1 Attack: entering - 3 session(s), concurrency 6, first review; whole target unreviewed",
  ));
  assert.ok(log.includes("r2 Attack: entering - 3 session(s), concurrency 6"));
  assert.ok(log.some((line) => /^r1 Attack: complete in .* - 1 candidate\(s\) after dedupe from 3 lens\(es\); next Refute$/.test(line)));
  assert.ok(log.includes("r1 Steelman: skipped - disabled by preset lean; next Gate"));
  assert.ok(log.some((line) => /^r2 Adjudicate: done adjudicate in .* - verdict PASS /.test(line)));
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

test("fix-up records engine-derived hashes for an unchanged interface despite legacy agent mismatch", async () => {
  const { root } = makeMiniRepo();
  const reviewContract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  await executeVerification(reviewContract, {
    reviewOnly: true,
    agentRunner: makeFakeRunner(root, []),
    logger: () => {},
  });

  const continuationContract = resolveContract(root, "mini", {
    preset: "lean",
    maxRounds: 1,
    fixupReview: "latest",
  });
  const bogusHash = "0".repeat(64);
  const calls = [];
  const fixed = await executeFixupContinuation(continuationContract, {
    fixupReview: "latest",
    agentRunner: makeFakeRunner(root, calls, {
      reportedInterfaceRegions: [{
        id: "public",
        hash_before: bogusHash,
        hash_after: "f".repeat(64),
        unchanged: false,
      }],
    }),
  });

  const expectedHash = createHash("sha256").update("## Public\n\nalpha\n").digest("hex");
  const expected = [{
    id: "public",
    hash_before: expectedHash,
    hash_after: expectedHash,
    unchanged: true,
    change_trigger: "",
  }];
  assert.deepEqual(fixed.interface_regions, expected);
  assert.deepEqual(fixed.resolution_record.interface_regions, expected);
  const journal = JSON.parse(readFileSync(join(root, fixed.journal), "utf8"));
  assert.deepEqual(journal.rounds[0].fixup.interface_regions, expected);
  const prompt = calls.find((call) => call.label === "fixup-continuation:R1").prompt;
  assert.match(prompt, /Do not calculate or return\s+cryptographic hashes/);
  assert.doesNotMatch(prompt, /report the provided before hashes|compute the after hashes/);

  const schema = JSON.parse(readFileSync(
    join(ROOT, ".agents/skills/verify-loop/schemas/fixup.schema.json"),
    "utf8",
  ));
  assert.equal(schema.required.includes("interface_regions"), false);
  assert.equal(Object.hasOwn(schema.properties, "interface_regions"), false);
  rmSync(root, { recursive: true, force: true });
});

test("fix-up fires the manifest change trigger from an engine-detected interface change", async () => {
  const { root } = makeMiniRepo();
  const reviewContract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  await executeVerification(reviewContract, {
    reviewOnly: true,
    agentRunner: makeFakeRunner(root, []),
    logger: () => {},
  });

  const continuationContract = resolveContract(root, "mini", {
    preset: "lean",
    maxRounds: 1,
    fixupReview: "latest",
  });
  const fixed = await executeFixupContinuation(continuationContract, {
    fixupReview: "latest",
    agentRunner: makeFakeRunner(root, [], { fixupInterfaceValue: "beta" }),
  });

  const expected = [{
    id: "public",
    hash_before: createHash("sha256").update("## Public\n\nalpha\n").digest("hex"),
    hash_after: createHash("sha256").update("## Public\n\nbeta\n").digest("hex"),
    unchanged: false,
    change_trigger: "re-review",
  }];
  assert.deepEqual(fixed.interface_regions, expected);
  assert.deepEqual(fixed.resolution_record.interface_regions, expected);
  const journal = JSON.parse(readFileSync(join(root, fixed.journal), "utf8"));
  assert.deepEqual(journal.rounds[0].fixup.interface_regions, expected);
  assert.match(readFileSync(join(root, "target.md"), "utf8"), /^beta$/m);
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
  assert.equal(
    existsSync(join(root, ".verification-runs", ".locks", "targets", "mini")),
    false,
  );
  assert.equal(
    existsSync(join(root, ".verification-runs", ".locks", "workspace-mutation")),
    false,
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

test("pre-execution hook enforces write allowlist and read denial deterministically", async () => {
  const { makeEnforcementExtension } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const root = join(tmpdir(), "verify-hook-repo");
  const handlers = [];
  const extension = makeEnforcementExtension(root, {
    readDenyPatterns: ["docs/**/chatlogs/**", "*.txt"],
    readDenyPaths: ["docs/phase3/reviews/PHASE_3_REVIEW_34.md"],
    writeAllow: ["docs/phase3/reviews/PHASE_3_REVIEW_35.md"],
  }, "test-role");
  extension({ on: (event, handler) => handlers.push({ event, handler }) });
  assert.equal(handlers.length, 1);
  assert.equal(handlers[0].event, "tool_call");
  const call = handlers[0].handler;

  const writeAllowed = call({ toolName: "write", input: { path: "docs/phase3/reviews/PHASE_3_REVIEW_35.md" } });
  assert.equal(writeAllowed, undefined);
  const writeAbsolute = call({ toolName: "write", input: { path: join(root, "docs/phase3/reviews/PHASE_3_REVIEW_35.md") } });
  assert.equal(writeAbsolute, undefined);
  const writeDenied = call({ toolName: "edit", input: { path: "docs/phase3/v1/PHASE_3_DOC.md" } });
  assert.equal(writeDenied?.block, true);
  const writeEscape = call({ toolName: "write", input: { path: "../outside.md" } });
  assert.equal(writeEscape?.block, true);

  const readDeniedReview = call({ toolName: "read", input: { path: "docs/phase3/reviews/PHASE_3_REVIEW_34.md" } });
  assert.equal(readDeniedReview?.block, true);
  assert.match(readDeniedReview?.reason, /independence rule/);
  const readForbidden = call({ toolName: "read", input: { path: "docs/phase9/chatlogs/x.md" } });
  assert.equal(readForbidden?.block, true);
  const readRootTranscript = call({ toolName: "read", input: { path: "2026-08-08-export.txt" } });
  assert.equal(readRootTranscript?.block, true);
  const readOk = call({ toolName: "read", input: { path: "docs/phase3/v1/PHASE_3_DOC.md" } });
  assert.equal(readOk, undefined);
  const grepNoPath = call({ toolName: "grep", input: { pattern: "verdict" } });
  assert.equal(grepNoPath, undefined);
  const globDenied = call({ toolName: "glob", input: { path: "docs/phase3/reviews/PHASE_3_REVIEW_34.md" } });
  assert.equal(globDenied?.block, true);
});

test("pre-execution hook without enforcement payload permits ordinary reads", async () => {
  const { makeEnforcementExtension } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const root = join(tmpdir(), "verify-hook-open");
  const handlers = [];
  makeEnforcementExtension(root, undefined, "open-role")({ on: (event, handler) => handlers.push(handler) });
  const call = handlers[0];
  assert.equal(call({ toolName: "read", input: { path: "docs/anything.md" } }), undefined);
  const write = call({ toolName: "write", input: { path: "docs/anything.md" } });
  assert.equal(write?.block, true);
});

test("arrayValuedSchemaLabels finds the array fields of the attack schema", async () => {
  const { arrayValuedSchemaLabels } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const schema = JSON.parse(readFileSync(
    join(import.meta.dirname, "../.agents/skills/verify-loop/schemas/attack.schema.json"),
    "utf8",
  ));
  const labels = arrayValuedSchemaLabels(schema);
  assert.deepEqual([...labels].sort(), ["areas_examined", "findings"]);
  assert.equal(labels.has("clean_areas"), false);
  assert.equal(arrayValuedSchemaLabels(undefined).size, 0);
  assert.equal(arrayValuedSchemaLabels({ properties: { note: { type: "string" } } }).size, 0);
});

test("resolveYieldOutcome returns a terminal yield's data verbatim", async () => {
  const { resolveYieldOutcome } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const labels = new Set(["findings"]);
  const terminalData = { findings: [{ candidate_id: "x-1" }], areas_examined: [], clean_areas: "none" };
  const outcome = resolveYieldOutcome([
    { status: "success", type: ["findings"], data: { candidate_id: "stale-section" } },
    { status: "success", data: terminalData },
  ], labels);
  assert.equal(outcome.kind, "data");
  assert.deepEqual(outcome.data, terminalData);
  assert.deepEqual(outcome.sectionCounts, { findings: 1 });
});

test("resolveYieldOutcome assembles incremental sections in order", async () => {
  const { resolveYieldOutcome } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const labels = new Set(["findings", "areas_examined"]);
  const outcome = resolveYieldOutcome([
    { status: "success", type: ["findings"], data: { candidate_id: "c-1" } },
    { status: "aborted", type: ["findings"], data: { candidate_id: "aborted" } },
    { status: "success", type: ["findings"], data: { candidate_id: "c-2" } },
    { status: "success", type: ["areas_examined"], data: "conformance map" },
    { status: "success", type: ["clean_areas"], data: "rest is clean" },
    { status: "success", type: "result" },
  ], labels);
  assert.equal(outcome.kind, "sections");
  assert.deepEqual(outcome.data, {
    findings: [{ candidate_id: "c-1" }, { candidate_id: "c-2" }],
    areas_examined: ["conformance map"],
    clean_areas: "rest is clean",
  });
  assert.deepEqual(outcome.sectionCounts, { findings: 2, areas_examined: 1, clean_areas: 1 });
});

test("resolveYieldOutcome honors sections without a terminal finalize", async () => {
  const { resolveYieldOutcome } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const outcome = resolveYieldOutcome([
    { status: "success", type: ["findings"], data: { candidate_id: "only" } },
  ], new Set(["findings"]));
  assert.equal(outcome.kind, "sections");
  assert.deepEqual(outcome.data, { findings: [{ candidate_id: "only" }] });
});

test("resolveYieldOutcome falls back when nothing usable arrived", async () => {
  const { resolveYieldOutcome } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  assert.equal(resolveYieldOutcome([], new Set()).kind, "fallback");
  assert.equal(
    resolveYieldOutcome([{ status: "success" }], new Set(["findings"])).kind,
    "fallback",
  );
  assert.equal(
    resolveYieldOutcome([{ status: "aborted", type: ["findings"], data: { candidate_id: "x" } }], new Set(["findings"])).kind,
    "fallback",
  );
});

test("resolveYieldOutcome substitutes last text for data-less sections", async () => {
  const { resolveYieldOutcome } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const outcome = resolveYieldOutcome(
    [{ status: "success", type: ["clean_areas"], useLastTurn: true }],
    new Set(["findings"]),
    "the prose turn",
  );
  assert.equal(outcome.kind, "sections");
  assert.deepEqual(outcome.data, { clean_areas: "the prose turn" });
});

test("buildYieldNudge names rejections, then sections, then the protocol", async () => {
  const { buildYieldNudge } = await import(
    "../.agents/skills/verify-loop/scripts/omp-agent-runner.mjs"
  );
  const rejected = buildYieldNudge({
    yieldRejections: ["Output does not match schema: findings/0/evidence required"],
    sectionCounts: {},
  });
  assert.match(rejected, /was rejected: Output does not match schema/);
  assert.match(rejected, /do not restart/i);

  const sectioned = buildYieldNudge({
    yieldRejections: [],
    sectionCounts: { findings: 3, areas_examined: 2 },
  });
  assert.match(sectioned, /findings×3, areas_examined×2/);
  assert.match(sectioned, /finalize/);

  const fresh = buildYieldNudge({ yieldRejections: [], sectionCounts: {} });
  assert.match(fresh, /terminal yield/);
  assert.match(fresh, /incrementally/);
});

test("read-only role AGENT_ERROR is retried once and recorded in the journal", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const baseRunner = makeFakeRunner(root, calls);
  const attempts = new Map();
  const flakyRunner = async (args) => {
    if (args.label === "attack:consistency:R1") {
      const seen = (attempts.get(args.label) ?? 0) + 1;
      attempts.set(args.label, seen);
      if (seen === 1) {
        const error = new Error("attack:consistency:R1 produced no yield and no final text (accepted yields: 0)");
        error.code = "AGENT_ERROR";
        throw error;
      }
    }
    return await baseRunner(args);
  };
  const log = [];
  const result = await executeVerification(contract, {
    reviewOnly: true,
    agentRunner: flakyRunner,
    logger: (message) => log.push(message),
  });
  assert.equal(result.outcome, "REVIEW-ONLY");
  assert.equal(attempts.get("attack:consistency:R1"), 2);
  assert.ok(log.some((message) => /attack:consistency:R1 failed .*retrying once/.test(message)));
  const journalFiles = readdirSync(join(root, ".runs")).filter((name) => name.endsWith(".json"));
  assert.equal(journalFiles.length, 1);
  const journal = JSON.parse(readFileSync(join(root, ".runs", journalFiles[0]), "utf8"));
  assert.deepEqual(journal.rounds[0].role_retries, [{
    role: "attack:consistency:R1",
    error: "attack:consistency:R1 produced no yield and no final text (accepted yields: 0)",
  }]);
});

test("a persistent read-only AGENT_ERROR exhausts the single retry", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const baseRunner = makeFakeRunner(root, calls);
  let attempts = 0;
  const failingRunner = async (args) => {
    if (args.label === "attack:consistency:R1") {
      attempts += 1;
      const error = new Error("empty completion");
      error.code = "AGENT_ERROR";
      throw error;
    }
    return await baseRunner(args);
  };
  await assert.rejects(
    executeVerification(contract, { reviewOnly: true, agentRunner: failingRunner, logger: () => {} }),
    (error) => error.code === "AGENT_ERROR" && /empty completion/.test(error.message),
  );
  assert.equal(attempts, 2);
});

test("writer roles are never auto-retried", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const baseRunner = makeFakeRunner(root, calls);
  let attempts = 0;
  const writerFailRunner = async (args) => {
    if (args.label.startsWith("adjudicate:")) {
      attempts += 1;
      const error = new Error("adjudicate session died");
      error.code = "AGENT_ERROR";
      throw error;
    }
    return await baseRunner(args);
  };
  await assert.rejects(
    executeVerification(contract, { reviewOnly: true, agentRunner: writerFailRunner, logger: () => {} }),
    (error) => error.code === "AGENT_ERROR" && /adjudicate session died/.test(error.message),
  );
  assert.equal(attempts, 1);
});

test("progress lines name the round, stage, role, and duration", () => {
  assert.equal(formatDuration(840), "840ms");
  assert.equal(formatDuration(47_000), "47s");
  assert.equal(formatDuration(261_000), "4m21s");
  assert.equal(formatDuration(3_720_000), "1h02m");
  assert.equal(shortRoleName("attack:contract:R34"), "contract");
  assert.equal(shortRoleName("refute:candidate-004:2:R34"), "candidate-004 #2");
  assert.equal(shortRoleName("refute:candidate-004:2:R34:correction"), "candidate-004 #2 (correction)");
  assert.equal(shortRoleName("gate:R34"), "gate");
  assert.equal(
    renderProgressEvent({ kind: "stage-start", round: 34, stage: "Attack", planned: 7, concurrency: 4 }),
    "r34 Attack: entering - 7 session(s), concurrency 4",
  );
  assert.equal(
    renderProgressEvent({
      kind: "role-end",
      round: 34,
      stage: "Attack",
      role: "attack:contract:R34",
      ms: 261_000,
      detail: "3 finding(s)",
      done: 1,
      planned: 7,
      inFlight: 3,
    }),
    "r34 Attack: done contract in 4m21s - 3 finding(s) [1/7 done, 3 in flight]",
  );
  assert.equal(
    renderProgressEvent({
      kind: "heartbeat",
      round: 34,
      stage: "Attack",
      elapsedMs: 1_087_000,
      running: ["attack:edge-cases:R34", "attack:consistency:R34"],
      done: 4,
      planned: 7,
    }),
    "r34 Attack: still running 18m07s - 2 in flight (edge-cases, consistency), 4/7 done",
  );
  assert.equal(
    renderProgressEvent({ kind: "heartbeat", round: 34, stage: "Adjudicate", elapsedMs: 5000, running: [], done: 0, planned: 1 }),
    "r34 Adjudicate: still running 5s - no session in flight, 0/1 done",
  );
  // A retry line keeps the full internal label so it greps against the journal.
  assert.equal(
    renderProgressEvent({
      kind: "role-retry",
      round: 34,
      stage: "Attack",
      role: "attack:contract:R34",
      message: "empty completion",
    }),
    "r34 Attack: role attack:contract:R34 failed (empty completion); retrying once with a fresh session",
  );
});

test("a stage heartbeats while sessions are in flight and stops when it finishes", async () => {
  const lines = [];
  const progress = createProgressReporter({ logger: (line) => lines.push(line), heartbeatMs: 15 });
  const stage = progress.stage({ round: 7, stage: "Attack", planned: 2, concurrency: 2 });
  stage.roleStart("attack:contract:R7");
  await new Promise((resolve) => setTimeout(resolve, 90));
  const beats = lines.filter((line) => line.includes("still running"));
  assert.ok(beats.length >= 2, `expected repeated heartbeats, got ${beats.length}`);
  assert.ok(beats.every((line) => line.includes("1 in flight (contract)")));
  stage.roleEnd("attack:contract:R7", "3 finding(s)");
  stage.finish({ summary: "3 candidate(s)", next: "Refute" });
  const settled = lines.length;
  await new Promise((resolve) => setTimeout(resolve, 60));
  assert.equal(lines.length, settled);
  progress.dispose();
});

test("a run tees its rendered progress to a log beside the journal", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "lean", maxRounds: 1 });
  const calls = [];
  const result = await executeVerification(contract, {
    reviewOnly: true,
    agentRunner: makeFakeRunner(root, calls),
    logger: () => {},
    heartbeatMs: 0,
  });
  assert.equal(result.outcome, "REVIEW-ONLY");
  const logPath = join(root, result.journal.replace(/\.json$/, ".log"));
  assert.ok(existsSync(logPath), `expected a progress log at ${logPath}`);
  const contents = readFileSync(logPath, "utf8");
  assert.match(contents, /r1 Attack: entering/);
  assert.match(contents, /r1 Adjudicate: complete in/);
  assert.match(contents, /run end: REVIEW-ONLY/);
  // Each record is timestamped so a tail can be correlated with the journal.
  for (const line of contents.trimEnd().split("\n")) {
    assert.match(line, /^\d{4}-\d{2}-\d{2}T[\d:.]+Z \S/);
  }
});

test("console progress sinks render, stream JSON, or stay silent", () => {
  const event = { kind: "stage-start", round: 34, stage: "Attack", planned: 7, concurrency: 4 };
  const written = [];
  let clock = Date.parse("2026-08-09T14:00:32.000Z");
  const human = progressSinks("human", { write: (text) => written.push(text), now: () => clock });
  clock += 261_000;
  human.logger(renderProgressEvent(event));
  assert.equal(human.onProgress, undefined);
  assert.match(written[0], /^\[\d{2}:\d{2}:\d{2} \+4m21s\] r34 Attack: entering - 7 session\(s\), concurrency 4\n$/);

  const json = progressSinks("json", { write: (text) => written.push(text) });
  json.logger("ignored");
  json.onProgress(event);
  assert.equal(written.length, 2);
  assert.deepEqual(JSON.parse(written[1]), event);

  const silent = progressSinks("none", { write: (text) => written.push(text) });
  silent.logger("ignored");
  assert.equal(silent.onProgress, undefined);
  assert.equal(written.length, 2);
});

test("the thorough preset runs Steelman as a reported stage and upheld candidates survive", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "thorough", maxRounds: 1 });
  assert.equal(contract.preset.steelman, true);
  assert.equal(contract.preset.refuters, 3);
  const calls = [];
  const log = [];
  const events = [];
  const result = await executeVerification(contract, {
    reviewOnly: true,
    agentRunner: makeFakeRunner(root, calls),
    logger: (message) => log.push(message),
    onProgress: (event) => events.push(event),
    heartbeatMs: 0,
  });
  assert.equal(result.outcome, "REVIEW-ONLY");
  assert.equal(result.last_verdict, "PASS-WITH-CORRECTIONS");
  // Steelman is a real stage here, not the skip the lean preset reports.
  const steelmanEvents = events.filter((event) => event.stage === "Steelman");
  assert.deepEqual(
    steelmanEvents.map((event) => event.kind),
    ["stage-start", "role-start", "role-end", "stage-end"],
  );
  assert.equal(steelmanEvents[0].planned, 1);
  assert.equal(steelmanEvents[1].role, "steelman:candidate-001:R1");
  assert.equal(steelmanEvents[2].detail, "final severity correction");
  assert.ok(log.includes("r1 Steelman: entering - 1 session(s), concurrency 8, 1 of 1 candidate(s) eligible"));
  assert.ok(log.some((line) => /^r1 Steelman: complete in .* - 1 candidate\(s\) survived the defence; next Gate$/.test(line)));
  assert.ok(!log.some((line) => line.includes("Steelman: skipped")));
  // Three refuters, and the steelman result is dispatched after the Refute barrier.
  assert.equal(calls.filter((call) => call.label.startsWith("refute:")).length, 3);
  assert.ok(
    calls.findIndex((call) => call.label.startsWith("steelman:"))
      > calls.findLastIndex((call) => call.label.startsWith("refute:")),
  );
  assert.match(
    calls.find((call) => call.label.startsWith("steelman:")).prompt,
    /"verdict": "CONFIRMED"/,
  );
});

test("a held Steelman defence drops the candidate before Gate and Adjudicate", async () => {
  const { root } = makeMiniRepo();
  const contract = resolveContract(root, "mini", { preset: "thorough", maxRounds: 1 });
  const calls = [];
  const log = [];
  const result = await executeVerification(contract, {
    reviewOnly: true,
    agentRunner: makeFakeRunner(root, calls, { steelmanOutcome: "defend" }),
    logger: (message) => log.push(message),
    heartbeatMs: 0,
  });
  assert.equal(result.outcome, "PASS");
  assert.ok(log.some((line) => /^r1 Steelman: complete in .* - 0 candidate\(s\) survived the defence; next Gate$/.test(line)));
  // No survivors means Gate dispatches no session at all.
  assert.ok(log.includes("r1 Gate: entering - 0 session(s), 0 candidate(s) to anchor-check"));
  assert.equal(calls.filter((call) => call.label.startsWith("gate:")).length, 0);
  // The drop is carried into the adjudicator's context, not silently forgotten.
  const adjudicatePrompt = calls.find((call) => call.label.startsWith("adjudicate:")).prompt;
  assert.match(adjudicatePrompt, /"stage": "Steelman"/);
  assert.match(adjudicatePrompt, /"disposition": "defence held; final severity none"/);
});