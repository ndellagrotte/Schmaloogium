import {
  lstatSync,
  mkdirSync,
  readlinkSync,
  readFileSync,
  readdirSync,
  realpathSync,
  renameSync,
  statSync,
  writeFileSync,
} from "node:fs";
import { createHash } from "node:crypto";
import { spawn, spawnSync } from "node:child_process";
import { basename, dirname, isAbsolute, join, relative, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";

export const STAGE_ORDER = [
  "Attack",
  "Refute",
  "Steelman",
  "Gate",
  "Adjudicate",
  "Fix-up",
];

const SKILL_ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const SCHEMA_ROOT = join(SKILL_ROOT, "schemas");
const PROMPT_ROOT = join(SKILL_ROOT, "prompts");

export class VerificationError extends Error {
  constructor(message, code = "VALIDATION_ERROR", details = undefined) {
    super(message);
    this.name = "VerificationError";
    this.code = code;
    this.details = details;
  }
}

export function discoverRepoRoot(start = process.cwd()) {
  const result = spawnSync("git", ["rev-parse", "--show-toplevel"], {
    cwd: start,
    encoding: "utf8",
  });
  if (result.status !== 0) {
    throw new VerificationError(
      `Cannot resolve repository root from ${start}: ${result.stderr.trim()}`,
    );
  }
  return realpathSync(result.stdout.trim());
}

export function isInside(root, absolutePath) {
  const rel = relative(root, absolutePath);
  return rel === "" || (!rel.startsWith(`..${sep}`) && rel !== ".." && !isAbsolute(rel));
}

function pathEntryExists(path) {
  try {
    lstatSync(path);
    return true;
  } catch (error) {
    if (error?.code === "ENOENT") return false;
    throw error;
  }
}

function nearestExisting(path) {
  let current = path;
  while (!pathEntryExists(current)) {
    const parent = dirname(current);
    if (parent === current) return current;
    current = parent;
  }
  return current;
}

export function resolveRepoPath(root, repoRelative, { mustExist = true, kind = "path" } = {}) {
  if (typeof repoRelative !== "string" || repoRelative.length === 0 || isAbsolute(repoRelative)) {
    throw new VerificationError(`${kind} must be a non-empty repository-relative path: ${repoRelative}`);
  }
  const segments = repoRelative.split(/[\\/]/);
  if (segments.some((segment) => segment === "." || segment === ".." || segment === "")) {
    throw new VerificationError(`${kind} must be canonical and contain no empty, '.' or '..' segments: ${repoRelative}`);
  }
  const absolute = resolve(root, repoRelative);
  if (!isInside(root, absolute)) {
    throw new VerificationError(`${kind} escapes the repository: ${repoRelative}`);
  }
  const existing = nearestExisting(absolute);
  let realExisting;
  try {
    realExisting = realpathSync(existing);
  } catch (error) {
    throw new VerificationError(
      `${kind} contains a dangling or unresolvable symlink component: ${repoRelative} (${error.message})`,
    );
  }
  if (!isInside(root, realExisting)) {
    throw new VerificationError(`${kind} resolves through a symlink outside the repository: ${repoRelative}`);
  }
  if (mustExist && !pathEntryExists(absolute)) {
    throw new VerificationError(`Missing ${kind}: ${repoRelative}`);
  }
  if (mustExist) {
    const real = realpathSync(absolute);
    if (!isInside(root, real)) {
      throw new VerificationError(`${kind} resolves outside the repository: ${repoRelative}`);
    }
  }
  return absolute;
}

export function loadJson(path) {
  try {
    return JSON.parse(readFileSync(path, "utf8"));
  } catch (error) {
    throw new VerificationError(`Invalid JSON at ${path}: ${error.message}`);
  }
}

function equalJson(a, b) {
  return JSON.stringify(a) === JSON.stringify(b);
}

function resolveLocalRef(rootSchema, ref) {
  if (!ref.startsWith("#/")) {
    throw new VerificationError(`Only local JSON Schema references are supported: ${ref}`);
  }
  return ref
    .slice(2)
    .split("/")
    .map((part) => part.replaceAll("~1", "/").replaceAll("~0", "~"))
    .reduce((value, part) => value?.[part], rootSchema);
}

export function validateSchema(value, schema, label = "$", rootSchema = schema) {
  const errors = [];

  function visit(current, rule, path) {
    if (!rule) {
      errors.push(`${path}: unresolved schema rule`);
      return;
    }
    if (rule.$ref) {
      visit(current, resolveLocalRef(rootSchema, rule.$ref), path);
      return;
    }
    for (const child of rule.allOf || []) visit(current, child, path);
    if (rule.not) {
      const before = errors.length;
      const local = [];
      const originalPush = errors.push;
      errors.push = (...items) => local.push(...items);
      visit(current, rule.not, path);
      errors.push = originalPush;
      if (local.length === 0) errors.push(`${path}: matches forbidden schema`);
      if (before !== errors.length) return;
    }
    if (Object.hasOwn(rule, "const") && !equalJson(current, rule.const)) {
      errors.push(`${path}: expected constant ${JSON.stringify(rule.const)}`);
      return;
    }
    if (rule.enum && !rule.enum.some((item) => equalJson(item, current))) {
      errors.push(`${path}: expected one of ${rule.enum.map(JSON.stringify).join(", ")}`);
      return;
    }

    const actualType = Array.isArray(current)
      ? "array"
      : current === null
        ? "null"
        : Number.isInteger(current)
          ? "integer"
          : typeof current;
    const types = rule.type === undefined
      ? []
      : Array.isArray(rule.type)
        ? rule.type
        : [rule.type];
    if (types.length && !types.includes(actualType) && !(actualType === "integer" && types.includes("number"))) {
      errors.push(`${path}: expected ${types.join("|")}, got ${actualType}`);
      return;
    }

    if (typeof current === "string") {
      if (rule.minLength !== undefined && current.length < rule.minLength) {
        errors.push(`${path}: string is shorter than ${rule.minLength}`);
      }
      if (rule.pattern !== undefined && !(new RegExp(rule.pattern).test(current))) {
        errors.push(`${path}: does not match /${rule.pattern}/`);
      }
    }
    if (typeof current === "number" && rule.minimum !== undefined && current < rule.minimum) {
      errors.push(`${path}: must be >= ${rule.minimum}`);
    }
    if (Array.isArray(current)) {
      if (rule.minItems !== undefined && current.length < rule.minItems) {
        errors.push(`${path}: requires at least ${rule.minItems} items`);
      }
      if (rule.maxItems !== undefined && current.length > rule.maxItems) {
        errors.push(`${path}: allows at most ${rule.maxItems} items`);
      }
      if (rule.items) current.forEach((item, index) => visit(item, rule.items, `${path}[${index}]`));
    }
    if (current && actualType === "object") {
      const keys = Object.keys(current);
      if (rule.minProperties !== undefined && keys.length < rule.minProperties) {
        errors.push(`${path}: requires at least ${rule.minProperties} properties`);
      }
      for (const required of rule.required || []) {
        if (!Object.hasOwn(current, required)) errors.push(`${path}: missing required property ${required}`);
      }
      for (const [key, child] of Object.entries(rule.properties || {})) {
        if (Object.hasOwn(current, key)) visit(current[key], child, `${path}.${key}`);
      }
      const known = new Set(Object.keys(rule.properties || {}));
      for (const key of keys.filter((candidate) => !known.has(candidate))) {
        if (rule.additionalProperties === false) {
          errors.push(`${path}: unexpected property ${key}`);
        } else if (rule.additionalProperties && typeof rule.additionalProperties === "object") {
          visit(current[key], rule.additionalProperties, `${path}.${key}`);
        }
      }
    }
  }

  visit(value, schema, label);
  return errors;
}

function assertSchema(value, schemaPath, label) {
  const schema = loadJson(schemaPath);
  const errors = validateSchema(value, schema, label);
  if (errors.length) {
    throw new VerificationError(`${label} failed schema validation:\n- ${errors.join("\n- ")}`);
  }
}

function walkJsonFiles(directory, out = []) {
  if (!pathEntryExists(directory)) return out;
  for (const entry of readdirSync(directory, { withFileTypes: true })) {
    const path = join(directory, entry.name);
    if (entry.isDirectory()) walkJsonFiles(path, out);
    else if (entry.isFile() && entry.name.endsWith(".json")) out.push(path);
  }
  return out;
}

export function resolveManifestPath(root, spec) {
  if (!spec) throw new VerificationError("A target manifest/profile is required.");
  if (spec.includes("/") || spec.endsWith(".json")) {
    return resolveRepoPath(root, spec, { kind: "target manifest" });
  }
  const directory = join(root, "verification", "targets");
  const matches = [];
  for (const path of walkJsonFiles(directory)) {
    const nameMatch = basename(path, ".json") === spec;
    let idMatch = false;
    try {
      idMatch = loadJson(path).id === spec;
    } catch {
      // The selected manifest is validated later. Other invalid JSON must not hide a direct match.
    }
    if (nameMatch || idMatch) matches.push(path);
  }
  const unique = [...new Set(matches.map((path) => realpathSync(path)))];
  if (unique.length === 0) throw new VerificationError(`No verification target matches ${spec}`);
  if (unique.length > 1) {
    throw new VerificationError(
      `Ambiguous verification target ${spec}; matches:\n${unique.map((path) => `- ${relative(root, path)}`).join("\n")}`,
    );
  }
  return unique[0];
}

export function resolveSelector(root, repoPath, selector, label = "selector") {
  const absolute = resolveRepoPath(root, repoPath, { kind: `${label} source` });
  const lines = readFileSync(absolute, "utf8").split(/\r?\n/);
  let startRegex;
  let endRegex;
  try {
    startRegex = new RegExp(selector.start);
    if (selector.end) endRegex = new RegExp(selector.end);
  } catch (error) {
    throw new VerificationError(`Invalid ${label} regex for ${repoPath}: ${error.message}`);
  }
  const starts = [];
  lines.forEach((line, index) => {
    startRegex.lastIndex = 0;
    if (startRegex.test(line)) starts.push(index);
  });
  if (starts.length !== 1) {
    throw new VerificationError(
      `${label} start /${selector.start}/ in ${repoPath} matched ${starts.length} lines; expected exactly one`,
    );
  }
  const startIndex = starts[0];
  let endIndex = lines.length;
  if (endRegex) {
    const ends = [];
    lines.forEach((line, index) => {
      if (index <= startIndex) return;
      endRegex.lastIndex = 0;
      if (endRegex.test(line)) ends.push(index);
    });
    const endMode = selector.end_mode || "unique-after-start";
    if (ends.length === 0 || (endMode === "unique-after-start" && ends.length !== 1)) {
      throw new VerificationError(
        `${label} end /${selector.end}/ after its start in ${repoPath} matched ${ends.length} lines; mode ${endMode} is not satisfied`,
      );
    }
    endIndex = selector.include_end ? ends[0] + 1 : ends[0];
  }
  const result = {
    path: repoPath,
    start_line: startIndex + 1,
    end_line: Math.max(startIndex + 1, endIndex),
    text: lines.slice(startIndex, endIndex).join("\n"),
  };
  if (selector.expected_start_line && selector.expected_start_line !== result.start_line) {
    throw new VerificationError(
      `${label} in ${repoPath} moved: expected start line ${selector.expected_start_line}, resolved ${result.start_line}`,
    );
  }
  if (selector.expected_end_line && selector.expected_end_line !== result.end_line) {
    throw new VerificationError(
      `${label} in ${repoPath} moved: expected end line ${selector.expected_end_line}, resolved ${result.end_line}`,
    );
  }
  return result;
}

export function globToRegExp(pattern) {
  let out = "^";
  for (let i = 0; i < pattern.length; i += 1) {
    const char = pattern[i];
    if (char === "*" && pattern[i + 1] === "*") {
      i += 1;
      if (pattern[i + 1] === "/") {
        i += 1;
        out += "(?:.*/)?";
      } else {
        out += ".*";
      }
    } else if (char === "*") out += "[^/]*";
    else if (char === "?") out += "[^/]";
    else out += char.replace(/[\\^$+.()|[\]{}]/g, "\\$&");
  }
  return new RegExp(`${out}$`);
}

function runGitFileList(root, args) {
  const result = spawnSync("git", ["ls-files", ...args, "-z"], {
    cwd: root,
    encoding: "utf8",
    maxBuffer: 64 * 1024 * 1024,
  });
  if (result.status !== 0) {
    throw new VerificationError(`git ls-files failed: ${result.stderr.trim()}`);
  }
  return result.stdout.split("\0").filter(Boolean);
}

function gitFileList(root, { includeIgnored = false } = {}) {
  const files = new Set(runGitFileList(root, ["-co", "--exclude-standard"]));
  if (includeIgnored) {
    for (const path of runGitFileList(root, ["-oi", "--exclude-standard"])) files.add(path);
  }
  return [...files].sort();
}

function expandPatterns(root, patterns) {
  const files = gitFileList(root, { includeIgnored: true });
  const resolved = new Set();
  for (const pattern of patterns) {
    const hasGlob = /[*?]/.test(pattern);
    if (!hasGlob) {
      const absolute = resolveRepoPath(root, pattern, { mustExist: false, kind: "declared path" });
      if (pathEntryExists(absolute)) {
        if (statSync(absolute).isDirectory()) {
          for (const file of files) if (file === pattern || file.startsWith(`${pattern}/`)) resolved.add(file);
        } else resolved.add(pattern);
      }
      continue;
    }
    const regex = globToRegExp(pattern);
    for (const file of files) if (regex.test(file)) resolved.add(file);
  }
  return [...resolved].sort();
}

function isForbiddenPath(root, manifest, repoPath) {
  const absolute = resolve(root, repoPath);
  const variants = new Set([relative(root, absolute).split(sep).join("/")]);
  if (pathEntryExists(absolute)) {
    try {
      variants.add(relative(root, realpathSync(absolute)).split(sep).join("/"));
    } catch {
      // A dangling symlink has no real target to classify; the logical path still applies.
    }
  }
  return manifest.forbidden_sources.path_patterns.some((pattern) => {
    const regex = globToRegExp(pattern);
    return [...variants].some((candidate) => regex.test(candidate));
  });
}

function validateArtifact(root, manifest, artifact, resolvedSelectors, prefix) {
  resolveRepoPath(root, artifact.path, { kind: `${prefix} artifact` });
  if (isForbiddenPath(root, manifest, artifact.path)) {
    throw new VerificationError(`${prefix} artifact matches a forbidden-source pattern: ${artifact.path}`);
  }
  const selectorIds = (artifact.selectors || []).map((item) => item.id);
  if (new Set(selectorIds).size !== selectorIds.length) {
    throw new VerificationError(`${prefix} has duplicate selector IDs`);
  }
  for (const item of artifact.selectors || []) {
    resolvedSelectors[`${prefix}:${item.id}`] = resolveSelector(
      root,
      artifact.path,
      item.selector,
      `${prefix}:${item.id}`,
    );
  }
  if (artifact.binding_contract) {
    resolvedSelectors[`${prefix}:binding_contract`] = resolveSelector(
      root,
      artifact.path,
      artifact.binding_contract,
      `${prefix}:binding_contract`,
    );
  }
}

function resolveManifestContent(root, manifest) {
  const interfaceIds = manifest.interface_regions.map((region) => region.id);
  if (new Set(interfaceIds).size !== interfaceIds.length) {
    throw new VerificationError("Interface/change-trigger region IDs must be unique.");
  }
  const artifacts = [
    ...manifest.targets.map((artifact, index) => ({ artifact, prefix: `target[${index}]` })),
    ...manifest.authoritative_sources.map((artifact, index) => ({
      artifact,
      prefix: `authority[${index}]`,
    })),
    ...manifest.supporting_evidence.map((artifact, index) => ({
      artifact,
      prefix: `evidence[${index}]`,
    })),
    ...manifest.dependencies.map((artifact, index) => ({
      artifact,
      prefix: `dependency[${index}]`,
    })),
    ...manifest.interface_regions.map((region) => ({
      artifact: {
        path: region.path,
        role: "interface/change-trigger region",
        selectors: [{ id: region.id, selector: region.selector }],
      },
      prefix: "interface",
    })),
  ];
  const resolvedSelectors = {};
  for (const { artifact, prefix } of artifacts) {
    validateArtifact(root, manifest, artifact, resolvedSelectors, prefix);
  }
  return resolvedSelectors;
}

export function discoverPriorReviews(root, manifest) {
  const directory = resolveRepoPath(root, manifest.prior_reviews.directory, {
    kind: "prior-review directory",
  });
  if (!statSync(directory).isDirectory()) {
    throw new VerificationError(`Prior-review path is not a directory: ${manifest.prior_reviews.directory}`);
  }
  let regex;
  try {
    regex = new RegExp(manifest.prior_reviews.filename_regex);
  } catch (error) {
    throw new VerificationError(`Invalid prior-review filename_regex: ${error.message}`);
  }
  const byRound = new Map();
  for (const entry of readdirSync(directory, { withFileTypes: true })) {
    if (!entry.isFile()) continue;
    const match = regex.exec(entry.name);
    if (!match) continue;
    const rawRound = match.groups?.[manifest.prior_reviews.round_group];
    const round = Number(rawRound);
    if (!Number.isSafeInteger(round) || round < 1) {
      throw new VerificationError(`Review ${entry.name} has invalid round ${rawRound}`);
    }
    if (byRound.has(round)) {
      throw new VerificationError(`Ambiguous prior reviews for round ${round}: ${byRound.get(round)} and ${entry.name}`);
    }
    byRound.set(round, `${manifest.prior_reviews.directory}/${entry.name}`.replaceAll("//", "/"));
  }
  const rounds = [...byRound.keys()].sort((a, b) => a - b);
  if (!manifest.prior_reviews.allow_gaps) {
    for (let index = 0; index < rounds.length; index += 1) {
      if (rounds[index] !== index + 1) {
        throw new VerificationError(
          `Prior-review sequence has a gap: found ${rounds.join(", ") || "none"}; expected 1..${rounds.at(-1)}`,
        );
      }
    }
  }
  return rounds.map((round) => ({ round, path: byRound.get(round) }));
}

function renderOutputPath(root, manifest, round) {
  const filename = manifest.output.review_template.replaceAll("{round}", String(round));
  const repoPath = `${manifest.output.directory}/${filename}`.replaceAll("//", "/");
  resolveRepoPath(root, repoPath, { mustExist: false, kind: "review output" });
  return repoPath;
}

function renderAllowedPaths(root, manifest, role, reviewOutput) {
  return manifest.write_permissions[role].map((pattern) => {
    const repoPath = pattern.replaceAll("{review_output}", reviewOutput);
    resolveRepoPath(root, repoPath, { mustExist: false, kind: `${role} write permission` });
    return repoPath;
  });
}

export function resolveContract(root, manifestSpec, options = {}) {
  const manifestPath = resolveManifestPath(root, manifestSpec);
  const manifest = loadJson(manifestPath);
  assertSchema(manifest, join(SCHEMA_ROOT, "manifest.schema.json"), "target manifest");

  const policyPath = resolveRepoPath(root, manifest.policy, { kind: "verification policy" });
  const policy = loadJson(policyPath);
  assertSchema(policy, join(SCHEMA_ROOT, "policy.schema.json"), "verification policy");
  const requiredVerdicts = ["PASS", "PASS-WITH-CORRECTIONS", "FAIL"];
  const requiredStops = [
    "PASS",
    "FAIL",
    "REVIEW_ONLY",
    "ROUND_CAP",
    "AGENT_ERROR",
    "VALIDATION_ERROR",
  ];
  if (!equalJson(policy.verdict.allowed, requiredVerdicts)) {
    throw new VerificationError(
      `verdict.allowed declares fixed engine semantics and must equal ${requiredVerdicts.join(", ")}`,
    );
  }
  if (!equalJson(policy.stop_conditions, requiredStops)) {
    throw new VerificationError(
      `stop_conditions declares fixed engine semantics and must equal ${requiredStops.join(", ")}`,
    );
  }
  if (
    policy.survival.refuting_majority !== "strict"
    || policy.survival.severity_aggregation !== "upper-median-non-refuting"
    || policy.survival.interface_aggregation !== "strict-majority-non-refuting"
  ) {
    throw new VerificationError("survival policy must match the engine's fixed aggregation semantics");
  }
  const lensSetPath = resolveRepoPath(root, manifest.attack_lenses, { kind: "attack lens set" });
  const lensSet = loadJson(lensSetPath);
  assertSchema(lensSet, join(SCHEMA_ROOT, "lens-set.schema.json"), "attack lens set");

  const presetName = options.preset || "lean";
  const preset = policy.presets[presetName];
  if (!preset) {
    throw new VerificationError(
      `Unknown preset ${presetName}; expected one of ${Object.keys(policy.presets).join(", ")}`,
    );
  }

  const resolvedSelectors = resolveManifestContent(root, manifest);

  const lensById = new Map(lensSet.lenses.map((lens) => [lens.id, lens]));
  if (lensById.size !== lensSet.lenses.length) {
    throw new VerificationError("Attack lens IDs must be unique.");
  }
  if (lensSet.lenses.filter((lens) => lens.prior_resolution_access).length > 1) {
    throw new VerificationError("At most one attack lens may have prior_resolution_access.");
  }
  for (const [kind, order] of Object.entries(manifest.lens_order)) {
    if (new Set(order).size !== order.length) throw new VerificationError(`${kind} lens order contains duplicates`);
    for (const id of order) if (!lensById.has(id)) throw new VerificationError(`${kind} lens order names unknown lens ${id}`);
    if (preset.finders > order.length) {
      throw new VerificationError(`Preset ${presetName} requests ${preset.finders} finders but ${kind} order has ${order.length} lenses`);
    }
  }

  const priorReviews = discoverPriorReviews(root, manifest);
  let pendingFixup = null;
  if (priorReviews.length) {
    const latest = priorReviews.at(-1);
    const latestText = readFileSync(join(root, latest.path), "utf8");
    const latestVerdicts = [...latestText.matchAll(/^# (PASS|PASS-WITH-CORRECTIONS|FAIL)\s*$/gm)]
      .map((match) => match[1]);
    if (
      latestVerdicts.length === 1
      && latestVerdicts[0] === "PASS-WITH-CORRECTIONS"
      && !/^## Resolutions\s*$/m.test(latestText)
    ) {
      pendingFixup = latest;
    }
    if (pendingFixup && !options.fixupReview) {
      throw new VerificationError(
        `Latest review ${latest.path} has unresolved corrections; use --fixup-review latest before starting another round`,
      );
    }
  }
  const expectedRound = priorReviews.length ? priorReviews.at(-1).round + 1 : 1;
  const startRound = options.startRound ?? expectedRound;
  if (!Number.isSafeInteger(startRound) || startRound < 1) {
    throw new VerificationError(`start-round must be a positive integer, got ${startRound}`);
  }
  if (startRound !== expectedRound) {
    throw new VerificationError(
      `start-round ${startRound} conflicts with discovered review state; expected ${expectedRound} after ${priorReviews.length} prior review(s)`,
    );
  }
  const maxRounds = options.maxRounds ?? 6;
  if (!Number.isSafeInteger(maxRounds) || maxRounds < 0) {
    throw new VerificationError(`max-rounds must be a non-negative integer, got ${maxRounds}`);
  }

  for (let offset = 0; offset < Math.max(maxRounds, 1); offset += 1) {
    const output = renderOutputPath(root, manifest, startRound + offset);
    if (pathEntryExists(join(root, output))) {
      throw new VerificationError(`Review output already exists and is immutable evidence: ${output}`);
    }
  }

  const outputDirectory = resolveRepoPath(root, manifest.output.directory, {
    kind: "review output directory",
  });
  const priorReviewDirectory = resolveRepoPath(root, manifest.prior_reviews.directory, {
    kind: "prior-review directory",
  });
  if (outputDirectory !== priorReviewDirectory) {
    throw new VerificationError(
      "output.directory must equal prior_reviews.directory so every produced review is discoverable",
    );
  }
  let reviewRegex;
  try {
    reviewRegex = new RegExp(manifest.prior_reviews.filename_regex);
  } catch (error) {
    throw new VerificationError(`Invalid prior-review filename_regex: ${error.message}`);
  }
  for (const round of [1, 37]) {
    reviewRegex.lastIndex = 0;
    const filename = manifest.output.review_template.replaceAll("{round}", String(round));
    const match = reviewRegex.exec(filename);
    if (
      !match
      || match[0] !== filename
      || Number(match.groups?.[manifest.prior_reviews.round_group]) !== round
    ) {
      throw new VerificationError(
        `output review_template and prior-review discovery disagree for ${filename}`,
      );
    }
  }
  const journalDirectory = resolveRepoPath(root, manifest.output.journal_directory, {
    mustExist: false,
    kind: "journal directory",
  });
  const outputComparison = realpathSync(outputDirectory);
  const journalComparison = pathEntryExists(journalDirectory) ? realpathSync(journalDirectory) : journalDirectory;
  if (
    isInside(outputComparison, journalComparison)
    || isInside(journalComparison, outputComparison)
  ) {
    throw new VerificationError("Journal and review-output directories must not overlap");
  }
  const immutablePaths = expandPatterns(root, manifest.immutable_paths);
  if (new Set(manifest.immutable_paths).size !== manifest.immutable_paths.length) {
    throw new VerificationError("immutable_paths contains duplicate patterns");
  }
  const firstReview = priorReviews.length === 0;
  const reviewOutput = renderOutputPath(root, manifest, startRound);
  for (const pattern of manifest.immutable_paths) {
    const matches = expandPatterns(root, [pattern]);
    if (matches.length === 0 && !globToRegExp(pattern).test(reviewOutput)) {
      throw new VerificationError(
        `Immutable pattern matches no current file or next review output: ${pattern}`,
      );
    }
  }
  for (const prior of priorReviews) {
    if (!immutablePaths.includes(prior.path)) {
      throw new VerificationError(`Prior review is not protected by immutable_paths: ${prior.path}`);
    }
  }
  const allowedWrites = {
    adjudicator: renderAllowedPaths(root, manifest, "adjudicator", reviewOutput),
    fixup: renderAllowedPaths(root, manifest, "fixup", reviewOutput),
  };
  for (const [role, paths] of Object.entries(allowedWrites)) {
    if (new Set(paths).size !== paths.length) {
      throw new VerificationError(`${role} write permissions contain duplicates`);
    }
    for (const path of paths) {
      const absolute = resolveRepoPath(root, path, { mustExist: false, kind: `${role} write permission` });
      const writeComparison = pathEntryExists(absolute) ? realpathSync(absolute) : absolute;
      if (
        isInside(journalComparison, writeComparison)
        || isInside(writeComparison, journalComparison)
      ) {
        throw new VerificationError(`${role} write permission overlaps the journal directory: ${path}`);
      }
    }
  }
  if (allowedWrites.adjudicator.length !== 1 || allowedWrites.adjudicator[0] !== reviewOutput) {
    throw new VerificationError("Adjudicator write permission must be exactly {review_output}");
  }
  if (!allowedWrites.fixup.includes(reviewOutput)) {
    throw new VerificationError("Fix-up write permissions must include {review_output}");
  }
  for (const path of [...allowedWrites.adjudicator, ...allowedWrites.fixup]) {
    if (immutablePaths.includes(path)) {
      throw new VerificationError(`Write permission conflicts with existing immutable evidence: ${path}`);
    }
  }

  return {
    root,
    manifestPath: relative(root, manifestPath),
    policyPath: relative(root, policyPath),
    lensSetPath: relative(root, lensSetPath),
    manifest,
    policy,
    attackLenses: lensSet.lenses,
    presetName,
    preset,
    priorReviews,
    pendingFixup,
    expectedRound,
    startRound,
    maxRounds,
    firstReview,
    resolvedSelectors,
    immutablePaths,
    journalDirectory,
    reviewOutput,
    allowedWrites,
  };
}

function sha256Text(text) {
  return createHash("sha256").update(text).digest("hex");
}

function sha256File(path) {
  return createHash("sha256").update(readFileSync(path)).digest("hex");
}

function fingerprintPath(path, { metadataOnly = false } = {}) {
  const stat = lstatSync(path);
  const mode = (stat.mode & 0o7777).toString(8);
  if (stat.isFile() && metadataOnly) {
    return `file-metadata:${mode}:${stat.size}:${stat.mtimeMs}:${stat.ctimeMs}`;
  }
  if (stat.isFile()) return `file:${mode}:${sha256File(path)}`;
  if (stat.isSymbolicLink()) return `symlink:${mode}:${readlinkSync(path)}`;
  return `other:${mode}`;
}

export function snapshotWorkspace(root, { manifest } = {}) {
  const snapshot = new Map();
  for (const repoPath of gitFileList(root, { includeIgnored: true })) {
    const absolute = join(root, repoPath);
    let stat;
    try {
      stat = lstatSync(absolute);
    } catch (error) {
      if (error?.code === "ENOENT") continue;
      throw error;
    }
    if (stat.isFile() || stat.isSymbolicLink()) {
      snapshot.set(
        repoPath,
        fingerprintPath(absolute, {
          metadataOnly: Boolean(manifest && isForbiddenPath(root, manifest, repoPath)),
        }),
      );
    }
  }
  return snapshot;
}

export function changedPaths(before, after) {
  const paths = new Set([...before.keys(), ...after.keys()]);
  return [...paths]
    .filter((path) => before.get(path) !== after.get(path))
    .sort();
}

function snapshotImmutable(root, immutablePaths, manifest) {
  const snapshot = new Map();
  for (const repoPath of immutablePaths) {
    const absolute = resolveRepoPath(root, repoPath, { kind: "immutable evidence" });
    snapshot.set(repoPath, fingerprintPath(absolute, {
      metadataOnly: Boolean(manifest && isForbiddenPath(root, manifest, repoPath)),
    }));
  }
  return snapshot;
}

export function verifyPermittedWrites(before, after, allowedPaths) {
  const changed = changedPaths(before, after);
  const allowed = new Set(allowedPaths);
  return {
    changed,
    unauthorized: changed.filter((path) => !allowed.has(path)),
  };
}

function assertImmutable(root, baseline, manifest) {
  const violations = [];
  for (const [repoPath, hash] of baseline) {
    const absolute = join(root, repoPath);
    if (
      !pathEntryExists(absolute)
      || fingerprintPath(absolute, {
        metadataOnly: Boolean(manifest && isForbiddenPath(root, manifest, repoPath)),
      }) !== hash
    ) {
      violations.push(repoPath);
    }
  }
  if (violations.length) {
    throw new VerificationError(
      `Immutable evidence changed:\n${violations.map((path) => `- ${path}`).join("\n")}`,
      "WRITE_VIOLATION",
    );
  }
}

export function resolveCitation(root, manifest, evidence) {
  let absolute;
  try {
    if (isForbiddenPath(root, manifest, evidence.path)) {
      return { ok: false, detail: `forbidden-source path: ${evidence.path}` };
    }
    absolute = resolveRepoPath(root, evidence.path, { kind: "citation path" });
    if (!lstatSync(absolute).isFile()) {
      return { ok: false, detail: `citation path is not a regular file: ${evidence.path}` };
    }
  } catch (error) {
    return { ok: false, detail: error.message };
  }
  let source;
  try {
    source = readFileSync(absolute, "utf8");
  } catch (error) {
    return { ok: false, detail: `cannot read citation path: ${error.message}` };
  }
  const normalizedSource = source.replaceAll("\r\n", "\n");
  const lines = normalizedSource.split("\n");
  if (evidence.line_end < evidence.line_start || evidence.line_end > lines.length) {
    return { ok: false, detail: `invalid line range ${evidence.line_start}-${evidence.line_end}` };
  }
  const atRange = lines.slice(evidence.line_start - 1, evidence.line_end).join("\n");
  if (atRange.includes(evidence.quote)) return { ok: true, evidence };
  if (!manifest.citation.allow_unique_relocation) {
    return { ok: false, detail: "quote does not occur at the cited line range" };
  }
  const matches = [];
  let searchFrom = 0;
  while (searchFrom <= normalizedSource.length) {
    const offset = normalizedSource.indexOf(evidence.quote, searchFrom);
    if (offset === -1) break;
    matches.push(offset);
    searchFrom = offset + 1;
  }
  if (matches.length !== 1) {
    return {
      ok: false,
      detail: `quote does not resolve uniquely (matches: ${matches.length})`,
    };
  }
  const lineStart = normalizedSource.slice(0, matches[0]).split("\n").length;
  const corrected = {
    ...evidence,
    line_start: lineStart,
    line_end: lineStart + evidence.quote.split("\n").length - 1,
  };
  return { ok: true, evidence: corrected, relocated: true };
}

function resolveEvidenceList(root, manifest, candidateId, items, label) {
  const corrected = [];
  for (const evidence of items || []) {
    const result = resolveCitation(root, manifest, evidence);
    if (!result.ok) {
      return {
        ok: false,
        detail: `${candidateId} ${label}: ${evidence.path}:${evidence.line_start}-${evidence.line_end}: ${result.detail}`,
      };
    }
    corrected.push(result.evidence);
  }
  return { ok: true, evidence: corrected };
}

export function resolveCandidateEvidence(root, manifest, candidate) {
  const top = resolveEvidenceList(
    root,
    manifest,
    candidate.candidate_id,
    candidate.evidence,
    "finder evidence",
  );
  if (!top.ok) return top;
  const refutations = [];
  for (let index = 0; index < (candidate.refutations || []).length; index += 1) {
    const judgment = candidate.refutations[index];
    const nested = resolveEvidenceList(
      root,
      manifest,
      candidate.candidate_id,
      judgment.evidence,
      `refuter ${index + 1} evidence`,
    );
    if (!nested.ok) return nested;
    refutations.push({ ...judgment, evidence: nested.evidence });
  }
  let steelman = candidate.steelman;
  if (steelman) {
    const nested = resolveEvidenceList(
      root,
      manifest,
      candidate.candidate_id,
      steelman.evidence,
      "steelman evidence",
    );
    if (!nested.ok) return nested;
    steelman = { ...steelman, evidence: nested.evidence };
  }
  return {
    ok: true,
    candidate: {
      ...candidate,
      evidence: top.evidence,
      ...(candidate.refutations ? { refutations } : {}),
      ...(candidate.steelman ? { steelman } : {}),
    },
  };
}

function normKey(finding) {
  const location = String(finding.location || "").toLowerCase().replace(/[^a-z0-9.]+/g, "");
  const title = String(finding.title || "").toLowerCase().replace(/[^a-z0-9]+/g, "").slice(0, 60);
  return `${location.slice(0, 40)}|${title}`;
}

export function dedupeCandidates(candidates) {
  const seen = new Set();
  const output = [];
  for (const candidate of candidates) {
    const key = normKey(candidate);
    if (seen.has(key)) continue;
    seen.add(key);
    output.push({
      ...candidate,
      source_candidate_id: candidate.candidate_id,
      candidate_id: `candidate-${String(output.length + 1).padStart(3, "0")}`,
    });
  }
  return output;
}

export function aggregateRefutations(candidate, judgments, policy) {
  if (judgments.length === 0) {
    throw new VerificationError(`No refuter results for ${candidate.candidate_id}`, "AGENT_ERROR");
  }
  const refuted = judgments.filter((judgment) => judgment.verdict === "REFUTED").length;
  if (refuted > judgments.length / 2) return null;
  const live = judgments.filter((judgment) => judgment.verdict !== "REFUTED");
  if (live.length === 0) return null;
  const ranks = live
    .map((judgment) => policy.severity_rank[judgment.severity_should_be])
    .sort((a, b) => a - b);
  const rank = ranks[Math.floor(ranks.length / 2)];
  if (!rank) return null;
  const severity = Object.entries(policy.severity_rank).find(([, value]) => value === rank)?.[0];
  const touchesVotes = live.filter((judgment) => judgment.touches_interface).length;
  return {
    ...candidate,
    severity,
    touches_interface: touchesVotes > live.length / 2,
    refutations: judgments,
  };
}

function loadPrompt(name) {
  return readFileSync(join(PROMPT_ROOT, name), "utf8");
}

function selectorCoordinates(resolvedSelectors) {
  return Object.fromEntries(
    Object.entries(resolvedSelectors).map(([id, value]) => [
      id,
      { path: value.path, start_line: value.start_line, end_line: value.end_line },
    ]),
  );
}

export function renderTemplate(template, variables) {
  const rendered = template.replace(/\{\{([A-Z0-9_]+)\}\}/g, (_, key) => {
    if (!Object.hasOwn(variables, key)) throw new VerificationError(`Missing prompt variable ${key}`);
    return String(variables[key]);
  });
  const leftovers = rendered.match(/\{\{[A-Z0-9_]+\}\}/g);
  if (leftovers) throw new VerificationError(`Unresolved prompt variables: ${leftovers.join(", ")}`);
  return rendered;
}

function contractContext(contract) {
  const artifactView = (artifact) => ({
    path: artifact.path,
    role: artifact.role,
    selectors: (artifact.selectors || []).map((item) => item.id),
  });
  return {
    targets: contract.manifest.targets.map(artifactView),
    authoritative_sources: contract.manifest.authoritative_sources.map(artifactView),
    supporting_evidence: contract.manifest.supporting_evidence.map(artifactView),
    dependencies: contract.manifest.dependencies.map((artifact) => ({
      ...artifactView(artifact),
      binding_contract: true,
    })),
    prior_reviews: {
      directory: contract.manifest.prior_reviews.directory,
      filename_regex: contract.manifest.prior_reviews.filename_regex,
      read_order: contract.manifest.prior_reviews.read_order,
      discovered_deny_list: contract.priorReviews.map((item) => item.path),
    },
    resolved_selectors: selectorCoordinates(contract.resolvedSelectors),
    forbidden_sources: contract.manifest.forbidden_sources,
    interface_regions: contract.manifest.interface_regions.map((region) => ({
      id: region.id,
      path: region.path,
      change_trigger: region.change_trigger,
    })),
    target_context: contract.manifest.target_context || {},
  };
}

function maturityContext(contract, firstReview) {
  if (firstReview) {
    return [
      "This is the first review of this target. The entire target is unreviewed surface.",
      "A substantial candidate list is not suspicious; anti-inflation applies to severity, not volume.",
      "Nothing has been cleared by an earlier review.",
    ].join("\n");
  }
  return [
    `${contract.priorReviews.length} review round(s) already exist.`,
    "Do not manufacture findings to keep a mature loop alive, and do not soften a real defect.",
    "Prior review material remains barred to read-only reviewers; the adjudicator handles it last.",
  ].join("\n");
}

function commonPrompt(contract, round, firstReview) {
  return renderTemplate(loadPrompt("common-readonly.md"), {
    REPO_ROOT: contract.root,
    TARGET_TITLE: contract.manifest.title,
    TARGET_ID: contract.manifest.id,
    ROUND: round,
    CONTRACT_CONTEXT: JSON.stringify(contractContext(contract), null, 2),
    MATURITY_CONTEXT: maturityContext(contract, firstReview),
  });
}

export async function runCodexAgent({
  root,
  prompt,
  schema,
  sandbox = "read-only",
  model,
  timeoutMs = 30 * 60 * 1000,
  label = "agent",
}) {
  const args = [
    "--ask-for-approval",
    "never",
    "exec",
    "--ephemeral",
    "--ignore-user-config",
    "--sandbox",
    sandbox,
    "--output-schema",
    schema,
    "-C",
    root,
  ];
  if (model) args.push("--model", model);
  args.push("-");

  return await new Promise((resolvePromise, rejectPromise) => {
    const child = spawn("codex", args, {
      cwd: root,
      stdio: ["pipe", "pipe", "pipe"],
      env: process.env,
      detached: process.platform !== "win32",
    });
    let stdout = "";
    let stderr = "";
    let timedOut = false;
    let forceKillTimer;
    const killTree = (signal) => {
      try {
        if (process.platform !== "win32" && child.pid) process.kill(-child.pid, signal);
        else child.kill(signal);
      } catch {
        // The process may already have exited between the timeout and the signal.
      }
    };
    const timer = setTimeout(() => {
      timedOut = true;
      killTree("SIGTERM");
      forceKillTimer = setTimeout(() => killTree("SIGKILL"), 5000);
    }, timeoutMs);
    child.stdout.on("data", (chunk) => { stdout += chunk; });
    child.stderr.on("data", (chunk) => { stderr += chunk; });
    child.on("error", (error) => {
      clearTimeout(timer);
      clearTimeout(forceKillTimer);
      rejectPromise(new VerificationError(`${label} failed to start: ${error.message}`, "AGENT_ERROR"));
    });
    child.on("close", (code) => {
      clearTimeout(timer);
      clearTimeout(forceKillTimer);
      if (timedOut) {
        rejectPromise(new VerificationError(`${label} timed out after ${timeoutMs}ms`, "AGENT_ERROR"));
        return;
      }
      if (code !== 0) {
        rejectPromise(
          new VerificationError(
            `${label} exited ${code}: ${stderr.trim() || stdout.trim()}`,
            "AGENT_ERROR",
          ),
        );
        return;
      }
      try {
        const parsed = JSON.parse(stdout.trim());
        assertSchema(parsed, schema, `${label} result`);
        resolvePromise(parsed);
      } catch (error) {
        rejectPromise(
          error instanceof VerificationError
            ? error
            : new VerificationError(`${label} returned invalid JSON: ${error.message}\n${stdout}`, "AGENT_ERROR"),
        );
      }
    });
    child.stdin.end(prompt);
  });
}

async function mapLimit(items, limit, fn) {
  const output = new Array(items.length);
  let cursor = 0;
  async function worker() {
    while (true) {
      const index = cursor;
      cursor += 1;
      if (index >= items.length) return;
      output[index] = await fn(items[index], index);
    }
  }
  await Promise.all(Array.from({ length: Math.min(limit, items.length) }, () => worker()));
  return output;
}

function selectedLenses(contract, firstReview = contract.firstReview) {
  const order = firstReview
    ? contract.manifest.lens_order.first_review
    : contract.manifest.lens_order.mature;
  const byId = new Map(contract.attackLenses.map((lens) => [lens.id, lens]));
  return order.slice(0, contract.preset.finders).map((id) => byId.get(id));
}

function priorSurface(contract, priorFix, firstReview) {
  if (priorFix) {
    return [
      "Previous fix-up data (claims to test, never verdicts to reach):",
      JSON.stringify({
        sites_edited: priorFix.sites_edited,
        weakest_points: priorFix.weakest_points,
        do_not_refight: priorFix.do_not_refight,
        refused_with_cause: priorFix.refused_with_cause,
      }, null, 2),
    ].join("\n\n");
  }
  if (firstReview) {
    return "There is no prior fix-up. Treat the whole target as unreviewed surface.";
  }
  return "No in-process fix-up data exists. The configured prior-Resolutions lens may inspect only the latest prior review's Resolutions, and only after recording its independent candidates.";
}

function estimateOneRound(contract, reviewOnly) {
  const finders = contract.preset.finders;
  const candidates = finders * contract.policy.cost_estimate.assumed_candidates_per_finder;
  const attack = finders;
  const refute = candidates * contract.preset.refuters;
  const refuteCorrection = refute;
  const steelman = contract.preset.steelman ? candidates : 0;
  const gate = candidates > 0 ? 1 : 0;
  const adjudicate = 1;
  const fixup = reviewOnly ? 0 : 1;
  return {
    attack,
    refute,
    refute_correction: refuteCorrection,
    steelman,
    gate,
    adjudicate,
    fixup,
    total: attack + refute + refuteCorrection + steelman + gate + adjudicate + fixup,
  };
}

export function estimateRun(contract, { reviewOnly = false } = {}) {
  const oneRound = estimateOneRound(contract, reviewOnly);
  const rounds = reviewOnly ? Math.min(contract.maxRounds, 1) : contract.maxRounds;
  const totalAgents = oneRound.total * rounds;
  return {
    assumptions: {
      candidates_per_finder: contract.policy.cost_estimate.assumed_candidates_per_finder,
      full_rounds: rounds,
      maximum_refute_corrections_per_result: 1,
    },
    per_round_agent_calls: oneRound,
    maximum_agent_calls: totalAgents,
    estimated_input_tokens: totalAgents * contract.policy.cost_estimate.input_tokens_per_agent,
    estimated_output_tokens: totalAgents * contract.policy.cost_estimate.output_tokens_per_agent,
    max_concurrency: contract.preset.max_concurrency,
  };
}

export function isFirstReviewRound(contract, offset) {
  return contract.firstReview && offset === 0;
}

function interfaceHashes(contract) {
  return contract.manifest.interface_regions.map((region) => {
    const resolved = resolveSelector(
      contract.root,
      region.path,
      region.selector,
      `interface:${region.id}`,
    );
    return { id: region.id, hash: sha256Text(resolved.text) };
  });
}

function authoritativeInterfaceRegions(contract, hashesBefore, hashesAfter) {
  const afterById = new Map(hashesAfter.map((item) => [item.id, item]));
  if (afterById.size !== hashesAfter.length) {
    throw new VerificationError("Engine resolved duplicate post-fix-up interface region IDs");
  }
  if (hashesBefore.length !== hashesAfter.length) {
    throw new VerificationError("Engine resolved a different interface region count after fix-up");
  }
  return hashesBefore.map((before) => {
    const after = afterById.get(before.id);
    const region = contract.manifest.interface_regions.find((item) => item.id === before.id);
    if (!after || !region) {
      throw new VerificationError(`Engine could not reconcile interface region ${before.id} after fix-up`);
    }
    const unchanged = before.hash === after.hash;
    return {
      id: before.id,
      hash_before: before.hash,
      hash_after: after.hash,
      unchanged,
      change_trigger: unchanged ? "" : region.change_trigger,
    };
  });
}

function resolveRoundContext(contract, round) {
  const resolvedSelectors = resolveManifestContent(contract.root, contract.manifest);
  const priorReviews = discoverPriorReviews(contract.root, contract.manifest);
  const nextRound = priorReviews.length ? priorReviews.at(-1).round + 1 : 1;
  if (nextRound !== round) {
    throw new VerificationError(
      `Prior-review state changed before round ${round}; current filesystem resolves next round ${nextRound}`,
    );
  }
  const immutablePaths = expandPatterns(contract.root, contract.manifest.immutable_paths);
  for (const prior of priorReviews) {
    if (!immutablePaths.includes(prior.path)) {
      throw new VerificationError(
        `Prior review is not protected by immutable_paths at round ${round}: ${prior.path}`,
      );
    }
  }
  return { resolvedSelectors, priorReviews, immutablePaths };
}

function extendImmutableBaseline(root, baseline, immutablePaths, manifest) {
  const additions = immutablePaths.filter((path) => !baseline.has(path));
  for (const [path, fingerprint] of snapshotImmutable(root, additions, manifest)) {
    baseline.set(path, fingerprint);
  }
}

function writeJournal(path, journal) {
  mkdirSync(dirname(path), { recursive: true });
  const temporary = `${path}.tmp`;
  writeFileSync(temporary, `${JSON.stringify(journal, null, 2)}\n`);
  renameSync(temporary, path);
}

function makeJournalPath(contract) {
  const stamp = new Date().toISOString().replaceAll(":", "").replaceAll(".", "");
  return join(
    contract.journalDirectory,
    `${contract.manifest.id}-${stamp}-${process.pid}-${process.hrtime.bigint()}.json`,
  );
}

function assertWriterChanges(contract, before, immutableBaseline, allowed, role) {
  const after = snapshotWorkspace(contract.root, { manifest: contract.manifest });
  const result = verifyPermittedWrites(before, after, allowed);
  assertImmutable(contract.root, immutableBaseline, contract.manifest);
  if (result.unauthorized.length) {
    throw new VerificationError(
      `${role} modified paths outside its allowlist:\n${result.unauthorized.map((path) => `- ${path}`).join("\n")}`,
      "WRITE_VIOLATION",
      result,
    );
  }
  return result.changed;
}

async function runCheckedWriter({
  contract,
  role,
  allowed,
  immutableBaseline,
  invoke,
}) {
  const before = snapshotWorkspace(contract.root, { manifest: contract.manifest });
  let result;
  let agentError;
  try {
    result = await invoke();
  } catch (error) {
    agentError = error;
  }

  let changes;
  let enforcementError;
  try {
    changes = assertWriterChanges(contract, before, immutableBaseline, allowed, role);
  } catch (error) {
    enforcementError = error;
  }
  if (enforcementError) {
    enforcementError.details = {
      ...(enforcementError.details || {}),
      agent_error: agentError
        ? { message: agentError.message, code: agentError.code || "AGENT_ERROR" }
        : undefined,
    };
    throw enforcementError;
  }
  if (agentError) throw agentError;
  return { result, changes };
}

function validateGateCoverage(candidates, gate) {
  const expected = new Set(candidates.map((candidate) => candidate.candidate_id));
  const seen = new Set();
  for (const result of gate.results) {
    if (!expected.has(result.candidate_id)) {
      throw new VerificationError(`Gate returned unknown candidate ${result.candidate_id}`, "AGENT_ERROR");
    }
    if (seen.has(result.candidate_id)) {
      throw new VerificationError(`Gate returned duplicate candidate ${result.candidate_id}`, "AGENT_ERROR");
    }
    seen.add(result.candidate_id);
  }
  const missing = [...expected].filter((id) => !seen.has(id));
  if (missing.length) {
    throw new VerificationError(`Gate omitted candidate(s): ${missing.join(", ")}`, "AGENT_ERROR");
  }
}

export function applyGateEvidenceCorrection(candidate, correctedEvidence) {
  if (correctedEvidence.length === 0) return candidate;
  if (correctedEvidence.length !== candidate.evidence.length) {
    throw new VerificationError(
      `Gate changed evidence cardinality for ${candidate.candidate_id}`,
      "AGENT_ERROR",
    );
  }
  correctedEvidence.forEach((evidence, index) => {
    const original = candidate.evidence[index];
    for (const field of ["path", "quote", "shows"]) {
      if (evidence[field] !== original[field]) {
        throw new VerificationError(
          `Gate changed ${field} rather than coordinates for ${candidate.candidate_id}`,
          "AGENT_ERROR",
        );
      }
    }
  });
  return { ...candidate, evidence: correctedEvidence };
}

export function validateAdjudication(root, expectedReview, adjudication, candidates = []) {
  if (adjudication.review_file !== expectedReview) {
    throw new VerificationError(
      `Adjudicator reported ${adjudication.review_file}; expected ${expectedReview}`,
      "AGENT_ERROR",
    );
  }
  const absolute = resolveRepoPath(root, expectedReview, { kind: "written review" });
  if (!lstatSync(absolute).isFile()) {
    throw new VerificationError("Written review must be a regular file, not a symlink or directory", "AGENT_ERROR");
  }
  const text = readFileSync(absolute, "utf8");
  const sectionPatterns = [
    /^## 0\. Method and reading order\s*$/m,
    /^## 1\. Findings\s*$/m,
    /^## 2\. Checked and clean\s*$/m,
    /^## 3\. Verdict\s*$/m,
  ];
  const sectionOffsets = sectionPatterns.map((pattern) => text.search(pattern));
  if (sectionOffsets.some((offset) => offset < 0) || sectionOffsets.some((offset, index) => index > 0 && offset <= sectionOffsets[index - 1])) {
    throw new VerificationError(
      "Review is missing the required ordered Method, Findings, Checked and clean, and Verdict sections",
      "AGENT_ERROR",
    );
  }
  const headings = [...text.matchAll(/^# (PASS|PASS-WITH-CORRECTIONS|FAIL)\s*$/gm)].map((match) => match[1]);
  if (headings.length !== 1 || headings[0] !== adjudication.verdict) {
    throw new VerificationError(
      `Review must contain exactly one verdict heading matching ${adjudication.verdict}; found ${headings.join(", ") || "none"}`,
      "AGENT_ERROR",
    );
  }
  const zero = adjudication.blocking_count === 0 && adjudication.correction_count === 0;
  if ((adjudication.verdict === "PASS") !== zero) {
    throw new VerificationError(
      `Literal PASS consistency failure: verdict=${adjudication.verdict}, blocking=${adjudication.blocking_count}, corrections=${adjudication.correction_count}`,
      "AGENT_ERROR",
    );
  }
  if (adjudication.verdict === "PASS-WITH-CORRECTIONS" && adjudication.correction_count === 0) {
    throw new VerificationError("PASS-WITH-CORRECTIONS requires at least one correction", "AGENT_ERROR");
  }
  const countLines = [...text.matchAll(/^Counts: blocking=(\d+); corrections=(\d+); notes=(\d+)\s*$/gm)];
  if (countLines.length !== 1) {
    throw new VerificationError("Review must contain exactly one machine-checkable Counts line", "AGENT_ERROR");
  }
  const onDiskCounts = countLines[0].slice(1).map(Number);
  if (
    onDiskCounts[0] !== adjudication.blocking_count
    || onDiskCounts[1] !== adjudication.correction_count
    || onDiskCounts[2] !== adjudication.note_count
  ) {
    throw new VerificationError("Structured adjudication counts do not match the review", "AGENT_ERROR");
  }
  const interfaceLines = [...text.matchAll(/^Interface changed: (yes|no)\s*$/gm)];
  if (interfaceLines.length !== 1 || (interfaceLines[0][1] === "yes") !== adjudication.interface_changed) {
    throw new VerificationError("Structured interface disposition does not match the review", "AGENT_ERROR");
  }

  const expectedIds = new Set(candidates.map((candidate) => candidate.candidate_id));
  const dispositions = adjudication.candidate_dispositions || [];
  const disposition = new Set(dispositions.map((item) => item.candidate_id));
  if (
    disposition.size !== dispositions.length
    ||
    disposition.size !== expectedIds.size
    || [...disposition].some((id) => !expectedIds.has(id))
  ) {
    throw new VerificationError("Adjudication must disposition every surviving candidate exactly once", "AGENT_ERROR");
  }
  for (const item of dispositions) {
    if (
      (item.disposition === "ADMITTED" && item.final_severity === "none")
      || (item.disposition === "DROPPED" && item.final_severity !== "none")
    ) {
      throw new VerificationError(
        `Invalid final disposition/severity pair for ${item.candidate_id}`,
        "AGENT_ERROR",
      );
    }
  }
  const admitted = dispositions.filter((item) => item.disposition === "ADMITTED");
  const severityCounts = {
    blocking: admitted.filter((item) => item.final_severity === "blocking").length,
    correction: admitted.filter((item) => item.final_severity === "correction").length,
    note: admitted.filter((item) => item.final_severity === "note").length,
  };
  if (
    severityCounts.blocking !== adjudication.blocking_count
    || severityCounts.correction !== adjudication.correction_count
    || severityCounts.note !== adjudication.note_count
  ) {
    throw new VerificationError("Adjudication counts do not match final candidate dispositions", "AGENT_ERROR");
  }
  const interfaceChanged = admitted.some((item) => item.touches_interface);
  if (interfaceChanged !== adjudication.interface_changed) {
    throw new VerificationError("Adjudication interface flag does not match final candidate dispositions", "AGENT_ERROR");
  }
  for (const item of admitted) {
    if (!text.includes(item.candidate_id)) {
      throw new VerificationError(`Review does not identify admitted candidate ${item.candidate_id}`, "AGENT_ERROR");
    }
  }
}

export function validateFixupAppend(beforeText, afterText) {
  if (/^## Resolutions\s*$/m.test(beforeText)) {
    throw new VerificationError("Review already contains a Resolutions section");
  }
  if (!afterText.startsWith(beforeText)) {
    throw new VerificationError("Fix-up rewrote existing review evidence instead of append-only resolution");
  }
  const suffix = afterText.slice(beforeText.length);
  const headings = [...afterText.matchAll(/^## Resolutions\s*$/gm)];
  if (headings.length !== 1 || !suffix.trimStart().startsWith("## Resolutions")) {
    throw new VerificationError("Fix-up must append exactly one ## Resolutions section");
  }
}

export function adjudicationStop(adjudication, { reviewOnly = false } = {}) {
  if (adjudication.verdict === "PASS") return "PASS";
  if (adjudication.verdict === "FAIL") return "FAIL";
  if (reviewOnly) return "REVIEW-ONLY";
  return "CONTINUE";
}

function convergenceWarning(trend, policy) {
  const window = policy.convergence.window;
  if (trend.length < window) return "";
  const corrections = trend.slice(-window).map((item) => item.corrections);
  for (let index = 1; index < corrections.length; index += 1) {
    if (corrections[index - 1] <= corrections[index]) {
      return `Corrections are not strictly decreasing across the last ${window} rounds: ${corrections.join(" -> ")}`;
    }
  }
  return "";
}

export function dryRunPlan(contract, options = {}) {
  if (contract.pendingFixup) {
    throw new VerificationError(
      `Latest review ${contract.pendingFixup.path} requires dryRunFixupPlan/--fixup-review latest`,
    );
  }
  const reviewOnly = Boolean(options.reviewOnly);
  const estimates = estimateRun(contract, { reviewOnly });
  return {
    outcome: "DRY-RUN",
    target_id: contract.manifest.id,
    manifest: contract.manifestPath,
    repo_root: contract.root,
    preset: contract.presetName,
    first_review: contract.firstReview,
    discovered_prior_reviews: contract.priorReviews,
    start_round: contract.startRound,
    max_rounds: contract.maxRounds,
    review_only: reviewOnly,
    next_review: contract.reviewOutput,
    stage_order: STAGE_ORDER,
    barriers: true,
    selected_lenses: selectedLenses(contract).map((lens) => lens.id),
    resolved_selectors: selectorCoordinates(contract.resolvedSelectors),
    immutable_paths: contract.immutablePaths,
    allowed_writes: contract.allowedWrites,
    estimates,
  };
}

export function resolveFixupReview(contract, spec = "latest") {
  if (contract.priorReviews.length === 0) {
    throw new VerificationError("No prior review exists for fix-up continuation");
  }
  const latest = contract.priorReviews.at(-1);
  const requested = spec === "latest"
    ? latest.path
    : relative(
      contract.root,
      resolveRepoPath(contract.root, spec, { kind: "fix-up review" }),
    ).split(sep).join("/");
  if (requested !== latest.path) {
    throw new VerificationError(
      `Fix-up continuation must target the latest review ${latest.path}; got ${requested}`,
    );
  }
  if (!lstatSync(join(contract.root, requested)).isFile()) {
    throw new VerificationError(`Pending review must be a regular file: ${requested}`);
  }
  const text = readFileSync(join(contract.root, requested), "utf8");
  const requiredSections = [
    "## 0. Method and reading order",
    "## 1. Findings",
    "## 2. Checked and clean",
    "## 3. Verdict",
  ];
  const offsets = requiredSections.map((heading) => text.indexOf(heading));
  if (offsets.some((offset) => offset < 0) || offsets.some((offset, index) => index > 0 && offset <= offsets[index - 1])) {
    throw new VerificationError(`Pending review is missing required ordered sections: ${requested}`);
  }
  const verdicts = [...text.matchAll(/^# (PASS|PASS-WITH-CORRECTIONS|FAIL)\s*$/gm)]
    .map((match) => match[1]);
  if (verdicts.length !== 1 || verdicts[0] !== "PASS-WITH-CORRECTIONS") {
    throw new VerificationError(
      `Fix-up continuation requires one PASS-WITH-CORRECTIONS verdict in ${requested}`,
    );
  }
  if (/^## Resolutions\s*$/m.test(text)) {
    throw new VerificationError(`Review already has Resolutions and is not pending fix-up: ${requested}`);
  }
  const counts = [...text.matchAll(/^Counts: blocking=(\d+); corrections=(\d+); notes=(\d+)\s*$/gm)];
  if (counts.length !== 1 || Number(counts[0][2]) < 1) {
    throw new VerificationError(`Pending review has no valid correction count: ${requested}`);
  }
  const allowedWrites = renderAllowedPaths(contract.root, contract.manifest, "fixup", requested);
  return {
    round: latest.round,
    path: requested,
    reviewText: text,
    corrections: Number(counts[0][2]),
    allowedWrites,
  };
}

export function dryRunFixupPlan(contract, fixupReview) {
  const pending = resolveFixupReview(contract, fixupReview);
  return {
    outcome: "DRY-RUN-FIXUP",
    target_id: contract.manifest.id,
    manifest: contract.manifestPath,
    review: pending.path,
    round: pending.round,
    allowed_writes: pending.allowedWrites,
    immutable_paths: contract.immutablePaths.filter((path) => path !== pending.path),
    next_review: contract.reviewOutput,
    estimates: {
      maximum_agent_calls: 1,
      estimated_input_tokens: contract.policy.cost_estimate.input_tokens_per_agent,
      estimated_output_tokens: contract.policy.cost_estimate.output_tokens_per_agent,
      max_concurrency: 1,
    },
  };
}

function assertFixupResult(contract, pending, fixup, changes) {
  if (!changes.includes(pending.path)) {
    throw new VerificationError(`Fix-up did not append Resolutions to ${pending.path}`, "AGENT_ERROR");
  }
  validateFixupAppend(
    pending.reviewText,
    readFileSync(join(contract.root, pending.path), "utf8"),
  );
  const reportedFiles = [...new Set(fixup.files_modified)].sort();
  if (
    reportedFiles.length !== fixup.files_modified.length
    || JSON.stringify(reportedFiles) !== JSON.stringify([...changes].sort())
  ) {
    throw new VerificationError("Fix-up files_modified does not match actual changes", "AGENT_ERROR");
  }
}

export async function executeFixupContinuation(contract, options = {}) {
  const pending = resolveFixupReview(contract, options.fixupReview);
  const agentRunner = options.agentRunner || runCodexAgent;
  const immutableBaseline = snapshotImmutable(
    contract.root,
    contract.immutablePaths.filter((path) => path !== pending.path),
    contract.manifest,
  );
  const journalPath = makeJournalPath(contract);
  const journalRepoPath = relative(contract.root, journalPath);
  const journal = {
    version: 1,
    target_id: contract.manifest.id,
    manifest: contract.manifestPath,
    started_at: new Date().toISOString(),
    status: "running",
    stage: `R${pending.round} Fix-up continuation`,
    rounds: [{ round: pending.round, review: pending.path, completed_stages: [] }],
    pending_work: "",
  };
  writeJournal(journalPath, journal);
  const hashesBefore = interfaceHashes(contract);
  const prompt = renderTemplate(loadPrompt("fixup.md"), {
    REPO_ROOT: contract.root,
    TARGET_TITLE: contract.manifest.title,
    TARGET_ID: contract.manifest.id,
    REVIEW_FILE: pending.path,
    MANIFEST_FILE: contract.manifestPath,
    CONTRACT_CONTEXT: JSON.stringify(contractContext(contract), null, 2),
    FIXUP_ALLOWED_PATHS: pending.allowedWrites.map((path) => `- \`${path}\``).join("\n"),
    FIXUP_INSTRUCTIONS: contract.manifest.fixup_instructions || "Record minimal resolutions; no target-specific changelog convention is required.",
    ADDENDUM_LINE_CAP: contract.policy.convergence.addendum_line_cap,
    TREND_WITH_CURRENT: JSON.stringify([
      { round: pending.round, verdict: "PASS-WITH-CORRECTIONS", corrections: pending.corrections },
    ], null, 2),
  });
  try {
    const run = await runCheckedWriter({
      contract,
      role: "Fix-up continuation",
      allowed: pending.allowedWrites,
      immutableBaseline,
      invoke: () => agentRunner({
        root: contract.root,
        prompt,
        schema: join(SCHEMA_ROOT, "fixup.schema.json"),
        sandbox: "workspace-write",
        model: options.model,
        timeoutMs: options.agentTimeoutMs,
        label: `fixup-continuation:R${pending.round}`,
      }),
    });
    const hashesAfter = interfaceHashes(contract);
    assertFixupResult(contract, pending, run.result, run.changes);
    const interfaceRegions = authoritativeInterfaceRegions(contract, hashesBefore, hashesAfter);
    const resolutionRecord = { ...run.result, interface_regions: interfaceRegions };
    journal.status = "complete";
    journal.stage = "complete";
    journal.outcome = "FIXUP-COMPLETE";
    journal.rounds[0].completed_stages.push("Fix-up");
    journal.rounds[0].fixup = resolutionRecord;
    journal.pending_work = `Round ${pending.round} fix-up is unreviewed until ${contract.reviewOutput}.`;
    journal.completed_at = new Date().toISOString();
    writeJournal(journalPath, journal);
    return {
      outcome: "FIXUP-COMPLETE",
      target_id: contract.manifest.id,
      review: pending.path,
      round: pending.round,
      files_modified: run.changes,
      resolution_record: resolutionRecord,
      interface_regions: interfaceRegions,
      pending_work: journal.pending_work,
      next_review: contract.reviewOutput,
      journal: journalRepoPath,
    };
  } catch (error) {
    journal.status = "error";
    journal.error = { message: error.message, code: error.code || "ERROR", details: error.details };
    journal.pending_work = `Recover from ${journal.stage}; inspect all worktree changes before retrying.`;
    writeJournal(journalPath, journal);
    error.details = {
      ...(error.details || {}),
      journal: journalRepoPath,
      stage: journal.stage,
      round: pending.round,
    };
    throw error;
  }
}

export async function executeVerification(contract, options = {}) {
  if (contract.pendingFixup) {
    throw new VerificationError(
      `Latest review ${contract.pendingFixup.path} requires executeFixupContinuation/--fixup-review latest`,
    );
  }
  const reviewOnly = Boolean(options.reviewOnly);
  const agentRunner = options.agentRunner || runCodexAgent;
  const logger = options.logger || ((message) => process.stderr.write(`${message}\n`));
  const immutableBaseline = snapshotImmutable(
    contract.root,
    contract.immutablePaths,
    contract.manifest,
  );
  const journalPath = makeJournalPath(contract);
  const journalRepoPath = relative(contract.root, journalPath);
  const journal = {
    version: 1,
    target_id: contract.manifest.id,
    manifest: contract.manifestPath,
    started_at: new Date().toISOString(),
    status: "running",
    stage: "startup",
    rounds: [],
    pending_work: "",
  };
  writeJournal(journalPath, journal);

  let outcome = "CAP";
  let lastVerdict = null;
  let priorFix = null;
  const trend = [];
  const resolutionRecords = [];
  let lastRound = contract.startRound - 1;

  const runAgent = async (role, prompt, schema, sandbox = "read-only") => agentRunner({
    root: contract.root,
    prompt,
    schema: join(SCHEMA_ROOT, schema),
    sandbox,
    model: options.model,
    timeoutMs: options.agentTimeoutMs,
    label: role,
  });

  try {
    for (let offset = 0; offset < contract.maxRounds; offset += 1) {
      const round = contract.startRound + offset;
      const firstReviewRound = isFirstReviewRound(contract, offset);
      lastRound = round;
      const reviewOutput = renderOutputPath(contract.root, contract.manifest, round);
      const roundJournal = { round, review: reviewOutput, completed_stages: [], gate_drops: [] };
      journal.rounds.push(roundJournal);
      const stageDispositions = [];

      journal.stage = `R${round} Context refresh`;
      writeJournal(journalPath, journal);
      try {
        if (pathEntryExists(join(contract.root, reviewOutput))) {
          throw new VerificationError(`Review output collision before round ${round}: ${reviewOutput}`);
        }
        assertImmutable(contract.root, immutableBaseline, contract.manifest);
        const refreshed = resolveRoundContext(contract, round);
        contract.resolvedSelectors = refreshed.resolvedSelectors;
        contract.priorReviews = refreshed.priorReviews;
        contract.immutablePaths = refreshed.immutablePaths;
        extendImmutableBaseline(
          contract.root,
          immutableBaseline,
          refreshed.immutablePaths,
          contract.manifest,
        );
        roundJournal.context_refresh = {
          status: "complete",
          resolved_selectors: selectorCoordinates(refreshed.resolvedSelectors),
          discovered_prior_reviews: refreshed.priorReviews,
        };
        writeJournal(journalPath, journal);
      } catch (error) {
        roundJournal.context_refresh = {
          status: "error",
          error: { message: error.message, code: error.code || "ERROR" },
          previous_resolved_selectors: selectorCoordinates(contract.resolvedSelectors),
          expected_prior_reviews: contract.priorReviews,
        };
        journal.pending_work = `Round ${round} filesystem context failed revalidation before Attack; inspect the named source and current review state before retrying.`;
        writeJournal(journalPath, journal);
        throw error;
      }

      const allowedWrites = {
        adjudicator: renderAllowedPaths(contract.root, contract.manifest, "adjudicator", reviewOutput),
        fixup: renderAllowedPaths(contract.root, contract.manifest, "fixup", reviewOutput),
      };
      logger(`Round ${round} Attack`);
      journal.stage = `R${round} Attack`;
      writeJournal(journalPath, journal);
      const lenses = selectedLenses(contract, firstReviewRound);
      const common = commonPrompt(contract, round, firstReviewRound);
      const attacks = await mapLimit(lenses, contract.preset.max_concurrency, async (lens) => {
        const instructions = firstReviewRound && lens.first_review_instructions
          ? lens.first_review_instructions
          : lens.instructions;
        const prompt = renderTemplate(loadPrompt("attack.md"), {
          COMMON: common,
          LENS_TITLE: lens.title,
          LENS_ID: lens.id,
          LENS_INSTRUCTIONS: instructions,
          SURFACE_CONTEXT: priorSurface(contract, priorFix, firstReviewRound),
          REVIEW_EXCEPTION: !firstReviewRound && !priorFix && lens.prior_resolution_access
            ? `Narrow exception: after recording your independent candidate list, you may read only the \`## Resolutions\` section of the latest prior review, \`${contract.priorReviews.at(-1).path}\`, to identify the last fix-up's stated sites and weakest points. Do not read its findings/verdict first and do not read older target reviews.`
            : "No prior-review exception applies to this lens.",
        });
        return runAgent(`attack:${lens.id}:R${round}`, prompt, "attack.schema.json");
      });
      roundJournal.completed_stages.push("Attack");
      const cleanAreas = attacks.map((attack, index) => ({
        lens: lenses[index].id,
        areas_examined: attack.areas_examined,
        clean_areas: attack.clean_areas,
      }));
      let candidates = dedupeCandidates(attacks.flatMap((attack) => attack.findings));

      logger(`Round ${round} Refute (${candidates.length} candidate(s))`);
      journal.stage = `R${round} Refute`;
      writeJournal(journalPath, journal);
      const refuteJobs = [];
      for (const candidate of candidates) {
        for (let index = 0; index < contract.preset.refuters; index += 1) {
          refuteJobs.push({ candidate, index });
        }
      }
      const refuteResults = await mapLimit(
        refuteJobs,
        contract.preset.max_concurrency,
        async ({ candidate, index }) => {
          const promptVariables = {
            COMMON: common,
            CANDIDATE: JSON.stringify(candidate, null, 2),
            REFUTER_INDEX: index + 1,
            REFUTER_COUNT: contract.preset.refuters,
          };
          const prompt = renderTemplate(loadPrompt("refute.md"), {
            ...promptVariables,
            CORRECTION_CONTEXT: "",
          });
          let result = await runAgent(
            `refute:${candidate.candidate_id}:${index + 1}:R${round}`,
            prompt,
            "refute.schema.json",
          );
          if (result.candidate_id !== candidate.candidate_id) {
            throw new VerificationError(
              `Refuter returned ${result.candidate_id}; expected ${candidate.candidate_id}`,
              "AGENT_ERROR",
            );
          }
          let resolved = resolveEvidenceList(
            contract.root,
            contract.manifest,
            result.candidate_id,
            result.evidence,
            `refuter ${index + 1} evidence`,
          );
          if (!resolved.ok) {
            const correctionPrompt = renderTemplate(loadPrompt("refute.md"), {
              ...promptVariables,
              CORRECTION_CONTEXT: [
                "## Bounded citation correction",
                "",
                `This is the only correction attempt for candidate \`${candidate.candidate_id}\`, refuter ${index + 1}.`,
                "A prior isolated Refute result was rejected by the deterministic citation resolver:",
                "",
                resolved.detail,
                "",
                "Rejected result (untrusted; do not patch or assume any citation is valid):",
                "",
                "```json",
                JSON.stringify(result, null, 2),
                "```",
                "",
                "Return a complete replacement Refute result for the same candidate. Re-read every file",
                "you cite and validate every additional evidence item from scratch: exact repo-relative",
                "path, inclusive physical line range, and a verbatim quote copied from that range. Do not",
                "guess, normalize, reflow, relocate, or paraphrase a rejected quote. Use an empty",
                "`evidence` array when no additional evidence is needed; finder evidence remains preserved",
                "separately by the orchestrator.",
              ].join("\n"),
            });
            result = await runAgent(
              `refute:${candidate.candidate_id}:${index + 1}:R${round}:correction`,
              correctionPrompt,
              "refute.schema.json",
            );
            if (result.candidate_id !== candidate.candidate_id) {
              throw new VerificationError(
                `Refute correction for ${candidate.candidate_id} refuter ${index + 1} returned ${result.candidate_id}; expected ${candidate.candidate_id}`,
                "AGENT_ERROR",
              );
            }
            resolved = resolveEvidenceList(
              contract.root,
              contract.manifest,
              result.candidate_id,
              result.evidence,
              `refuter ${index + 1} evidence`,
            );
            if (!resolved.ok) {
              throw new VerificationError(
                `Refuter returned unverifiable evidence after one correction attempt: ${resolved.detail}`,
                "AGENT_ERROR",
              );
            }
          }
          return { ...result, evidence: resolved.evidence };
        },
      );
      const byCandidate = new Map(candidates.map((candidate) => [candidate.candidate_id, []]));
      refuteResults.forEach((result) => byCandidate.get(result.candidate_id).push(result));
      candidates = candidates
        .map((candidate) => {
          const result = aggregateRefutations(
            candidate,
            byCandidate.get(candidate.candidate_id),
            contract.policy,
          );
          if (!result) {
            stageDispositions.push({
              candidate_id: candidate.candidate_id,
              title: candidate.title,
              stage: "Refute",
              disposition: "dropped by strict refuting majority or no live severity",
            });
          }
          return result;
        })
        .filter(Boolean);
      roundJournal.completed_stages.push("Refute");

      logger(`Round ${round} Steelman (${contract.preset.steelman ? "enabled" : "disabled"})`);
      journal.stage = `R${round} Steelman`;
      writeJournal(journalPath, journal);
      if (contract.preset.steelman) {
        const eligible = candidates.filter(
          (candidate) => contract.policy.severity_rank[candidate.severity] >= contract.policy.severity_rank.correction
            || candidate.touches_interface,
        );
        const steelResults = await mapLimit(
          eligible,
          contract.preset.max_concurrency,
          async (candidate) => {
            const prompt = renderTemplate(loadPrompt("steelman.md"), {
              COMMON: common,
              CANDIDATE_WITH_REFUTERS: JSON.stringify(candidate, null, 2),
            });
            const result = await runAgent(
              `steelman:${candidate.candidate_id}:R${round}`,
              prompt,
              "steelman.schema.json",
            );
            if (result.candidate_id !== candidate.candidate_id) {
              throw new VerificationError(
                `Steelman returned ${result.candidate_id}; expected ${candidate.candidate_id}`,
                "AGENT_ERROR",
              );
            }
            const resolved = resolveCandidateEvidence(contract.root, contract.manifest, {
              candidate_id: result.candidate_id,
              evidence: result.evidence,
            });
            if (!resolved.ok) {
              throw new VerificationError(
                `Steelman returned unverifiable evidence: ${resolved.detail}`,
                "AGENT_ERROR",
              );
            }
            return { ...result, evidence: resolved.candidate.evidence };
          },
        );
        const steelById = new Map(steelResults.map((result) => [result.candidate_id, result]));
        candidates = candidates
          .map((candidate) => {
            const steel = steelById.get(candidate.candidate_id);
            if (!steel) return candidate;
            if (steel.final_severity === "none") {
              stageDispositions.push({
                candidate_id: candidate.candidate_id,
                title: candidate.title,
                stage: "Steelman",
                disposition: "defence held; final severity none",
              });
              return null;
            }
            return {
              ...candidate,
              severity: steel.final_severity,
              touches_interface: steel.touches_interface,
              steelman: steel,
            };
          })
          .filter(Boolean);
      }
      roundJournal.completed_stages.push("Steelman");

      logger(`Round ${round} Gate`);
      journal.stage = `R${round} Gate`;
      writeJournal(journalPath, journal);
      const deterministicallyResolved = [];
      for (const candidate of candidates) {
        const resolvedEvidence = resolveCandidateEvidence(contract.root, contract.manifest, candidate);
        if (!resolvedEvidence.ok) {
          const drop = { candidate_id: candidate.candidate_id, detail: resolvedEvidence.detail };
          roundJournal.gate_drops.push(drop);
          stageDispositions.push({ ...drop, title: candidate.title, stage: "Gate", disposition: "unverifiable evidence" });
          logger(`Gate DROPPED ${candidate.candidate_id}: ${resolvedEvidence.detail}`);
        } else deterministicallyResolved.push(resolvedEvidence.candidate);
      }
      candidates = deterministicallyResolved;
      if (candidates.length) {
        const prompt = renderTemplate(loadPrompt("gate.md"), {
          COMMON: common,
          CANDIDATES: JSON.stringify(candidates, null, 2),
        });
        const gate = await runAgent(`gate:R${round}`, prompt, "gate.schema.json");
        validateGateCoverage(candidates, gate);
        const resultById = new Map(gate.results.map((result) => [result.candidate_id, result]));
        const gated = [];
        for (const candidate of candidates) {
          const result = resultById.get(candidate.candidate_id);
          if (!result.anchor_ok) {
            const drop = { candidate_id: candidate.candidate_id, detail: result.detail };
            roundJournal.gate_drops.push(drop);
            stageDispositions.push({ ...drop, title: candidate.title, stage: "Gate", disposition: "anchor rejected" });
            logger(`Gate DROPPED ${candidate.candidate_id}: ${result.detail}`);
            continue;
          }
          const corrected = applyGateEvidenceCorrection(candidate, result.corrected_evidence);
          const recheck = resolveCandidateEvidence(contract.root, contract.manifest, corrected);
          if (!recheck.ok) {
            const drop = { candidate_id: candidate.candidate_id, detail: recheck.detail };
            roundJournal.gate_drops.push(drop);
            stageDispositions.push({ ...drop, title: candidate.title, stage: "Gate", disposition: "corrected anchor rejected" });
            logger(`Gate DROPPED ${candidate.candidate_id}: ${recheck.detail}`);
            continue;
          }
          gated.push(recheck.candidate);
        }
        candidates = gated;
      }
      roundJournal.completed_stages.push("Gate");

      logger(`Round ${round} Adjudicate`);
      journal.stage = `R${round} Adjudicate`;
      writeJournal(journalPath, journal);
      const adjudicatePrompt = renderTemplate(loadPrompt("adjudicate.md"), {
        REPO_ROOT: contract.root,
        TARGET_TITLE: contract.manifest.title,
        TARGET_ID: contract.manifest.id,
        ROUND: round,
        REVIEW_FILE: reviewOutput,
        MANIFEST_FILE: contract.manifestPath,
        CONTRACT_CONTEXT: JSON.stringify(contractContext(contract), null, 2),
        FORBIDDEN_SOURCES: [
          ...contract.manifest.forbidden_sources.path_patterns.map((item) => `- path pattern: \`${item}\``),
          ...contract.manifest.forbidden_sources.provenance_patterns.map((item) => `- provenance: ${item}`),
        ].join("\n"),
        CANDIDATES: JSON.stringify(candidates, null, 2),
        CLEAN_AREAS: JSON.stringify(cleanAreas, null, 2),
        STAGE_DISPOSITIONS: JSON.stringify(stageDispositions, null, 2),
        GATE_DROPS: JSON.stringify(roundJournal.gate_drops, null, 2),
        TREND_SO_FAR: JSON.stringify(trend, null, 2),
        PRIOR_REVIEWS: JSON.stringify(contract.priorReviews, null, 2),
      });
      const adjudicatorRun = await runCheckedWriter({
        contract,
        role: "Adjudicator",
        immutableBaseline,
        allowed: allowedWrites.adjudicator,
        invoke: () => runAgent(
          `adjudicate:R${round}`,
          adjudicatePrompt,
          "adjudicate.schema.json",
          "workspace-write",
        ),
      });
      const adjudication = adjudicatorRun.result;
      const adjudicatorChanges = adjudicatorRun.changes;
      if (!adjudicatorChanges.includes(reviewOutput)) {
        throw new VerificationError(`Adjudicator did not create the expected review: ${reviewOutput}`, "AGENT_ERROR");
      }
      validateAdjudication(contract.root, reviewOutput, adjudication, candidates);
      roundJournal.completed_stages.push("Adjudicate");
      lastVerdict = adjudication.verdict;
      const trendEntry = {
        round,
        verdict: adjudication.verdict,
        blocking: adjudication.blocking_count,
        corrections: adjudication.correction_count,
        notes: adjudication.note_count,
        interface_changed: adjudication.interface_changed,
        review: reviewOutput,
      };
      trend.push(trendEntry);
      contract.priorReviews.push({ round, path: reviewOutput });

      const stop = adjudicationStop(adjudication, { reviewOnly });
      if (stop === "PASS") {
        outcome = stop;
        roundJournal.completed_stages.push("Fix-up (not required)");
        break;
      }
      if (stop === "FAIL") {
        outcome = stop;
        journal.pending_work = "FAIL requires a human-authorized rebuild/build-session rerun; it is never automated.";
        break;
      }
      if (stop === "REVIEW-ONLY") {
        outcome = stop;
        journal.pending_work = `Apply corrections from ${reviewOutput} in a later authorized fix-up run.`;
        break;
      }

      logger(`Round ${round} Fix-up`);
      journal.stage = `R${round} Fix-up`;
      writeJournal(journalPath, journal);
      const hashesBefore = interfaceHashes(contract);
      const reviewBeforeFixup = readFileSync(join(contract.root, reviewOutput), "utf8");
      const fixupPrompt = renderTemplate(loadPrompt("fixup.md"), {
        REPO_ROOT: contract.root,
        TARGET_TITLE: contract.manifest.title,
        TARGET_ID: contract.manifest.id,
        REVIEW_FILE: reviewOutput,
        MANIFEST_FILE: contract.manifestPath,
        CONTRACT_CONTEXT: JSON.stringify(contractContext(contract), null, 2),
        FIXUP_ALLOWED_PATHS: allowedWrites.fixup.map((path) => `- \`${path}\``).join("\n"),
        FIXUP_INSTRUCTIONS: contract.manifest.fixup_instructions || "Record minimal resolutions; no target-specific changelog convention is required.",
        ADDENDUM_LINE_CAP: contract.policy.convergence.addendum_line_cap,
        TREND_WITH_CURRENT: JSON.stringify(trend, null, 2),
      });
      const fixupRun = await runCheckedWriter({
        contract,
        role: "Fix-up",
        immutableBaseline,
        allowed: allowedWrites.fixup,
        invoke: () => runAgent(
          `fixup:R${round}`,
          fixupPrompt,
          "fixup.schema.json",
          "workspace-write",
        ),
      });
      const agentFixup = fixupRun.result;
      const fixupChanges = fixupRun.changes;
      const hashesAfter = interfaceHashes(contract);
      assertFixupResult(
        contract,
        { path: reviewOutput, reviewText: reviewBeforeFixup },
        agentFixup,
        fixupChanges,
      );
      const interfaceRegions = authoritativeInterfaceRegions(contract, hashesBefore, hashesAfter);
      const fixup = { ...agentFixup, interface_regions: interfaceRegions };
      priorFix = fixup;
      const resolutionRecord = { round, review: reviewOutput, ...fixup };
      resolutionRecords.push(resolutionRecord);
      roundJournal.fixup = fixup;
      trendEntry.new_prose_lines = fixup.new_prose_lines;
      trendEntry.addendum_lines = fixup.addendum_lines;
      trendEntry.interface_regions = interfaceRegions.map(({ id, unchanged, change_trigger }) => ({
        id,
        unchanged,
        change_trigger,
      }));
      trendEntry.convergence_warning = convergenceWarning(trend, contract.policy);
      roundJournal.completed_stages.push("Fix-up");
      if (fixup.addendum_lines > contract.policy.convergence.addendum_line_cap) {
        trendEntry.convergence_warning = [
          trendEntry.convergence_warning,
          `Addendum ${fixup.addendum_lines} lines exceeds cap ${contract.policy.convergence.addendum_line_cap}`,
        ].filter(Boolean).join("; ");
      }
      journal.pending_work = `Round ${round} fix-up is unreviewed until round ${round + 1}.`;
    }
  } catch (error) {
    outcome = "ERROR";
    journal.status = "error";
    journal.error = { message: error.message, code: error.code || "ERROR", details: error.details };
    journal.pending_work = journal.pending_work || `Recover from stage ${journal.stage}; inspect worktree changes before resuming.`;
    writeJournal(journalPath, journal);
    error.details = {
      ...(error.details || {}),
      journal: journalRepoPath,
      stage: journal.stage,
      round: lastRound,
    };
    throw error;
  }

  if (contract.maxRounds === 0) outcome = "CAP";
  if (outcome === "CAP") {
    journal.pending_work = trend.length
      ? `No literal PASS was reached. The last fix-up/review state at round ${lastRound} requires inspection before raising the cap.`
      : "No rounds were configured.";
  }
  assertImmutable(contract.root, immutableBaseline, contract.manifest);
  journal.status = "complete";
  journal.stage = "complete";
  journal.outcome = outcome;
  journal.completed_at = new Date().toISOString();
  writeJournal(journalPath, journal);

  return {
    outcome,
    target_id: contract.manifest.id,
    manifest: contract.manifestPath,
    last_verdict: lastVerdict,
    rounds_run: trend.length,
    last_round: Math.max(lastRound, 0),
    preset: contract.presetName,
    review_only: reviewOnly,
    trend,
    resolution_records: resolutionRecords,
    pending_work: journal.pending_work,
    journal: journalRepoPath,
  };
}
