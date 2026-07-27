#!/usr/bin/env node

import { existsSync, readFileSync } from "node:fs";
import { spawnSync } from "node:child_process";
import { join } from "node:path";

const rootResult = spawnSync("git", ["rev-parse", "--show-toplevel"], { encoding: "utf8" });
if (rootResult.status !== 0) {
  process.stderr.write(`Cannot resolve repository root: ${rootResult.stderr}\n`);
  process.exit(2);
}
const root = rootResult.stdout.trim();
const filesResult = spawnSync("git", ["ls-files", "-co", "--exclude-standard", "-z"], {
  cwd: root,
  encoding: "utf8",
});
if (filesResult.status !== 0) {
  process.stderr.write(`Cannot enumerate repository files: ${filesResult.stderr}\n`);
  process.exit(2);
}

const stalePatterns = [
  { id: "claude-code", regex: /\b(?:fresh\s+)?Claude Code\b/i },
  { id: "claude-config", regex: /\.claude(?:\/|\\)/i },
  { id: "workflow-runtime", regex: /\bWorkflow\s*\(/ },
  { id: "claude-command", regex: /(^|[\s`'"])\/verify-loop(?:\b|`)/ },
  { id: "claude-agent-type", regex: /\bagentType\b|\bagent type\b.*\b(?:Explore|general-purpose)\b/i },
];

const historicalPatterns = [
  /^docs\/design\/(?:v1\.1|v2\.0-RC1|v2\.0-RC2|v2\.0-RC3)\/DESIGN\.md$/,
  /^docs\/design\/briefs\//,
  /^docs\/phase\d+\/reviews\//,
  /^docs\/phase\d+\/briefs\//,
  /^docs\/tooling\/history\//,
  /^docs\/tooling\/CODEX_MIGRATION_OVERLAY\.md$/,
];

function isHistorical(path, line) {
  if (historicalPatterns.some((pattern) => pattern.test(path))) return true;
  if (/^docs\/phase\d+\/v\d+\/PHASE_\d+_DOC\.md$/.test(path)) {
    return /\b(?:historical|previous|prior|this session|at the time|was still|provenance)\b/i.test(line);
  }
  return false;
}

const active = [];
const historical = [];
const files = filesResult.stdout.split("\0").filter(Boolean);
for (const path of files) {
  if (!existsSync(join(root, path))) continue;
  if (path === "CLAUDE.md" || path.startsWith(".claude/")) {
    active.push({
      path,
      line: 0,
      pattern: "legacy-active-path",
      text: "Legacy Claude path exists in the active worktree",
    });
  }
}
for (const path of files) {
  if (path === "scripts/audit-verification-migration.mjs") continue;
  let text;
  try {
    text = readFileSync(join(root, path), "utf8");
  } catch {
    continue;
  }
  text.split(/\r?\n/).forEach((line, index) => {
    for (const pattern of stalePatterns) {
      pattern.regex.lastIndex = 0;
      if (!pattern.regex.test(line)) continue;
      const match = { path, line: index + 1, pattern: pattern.id, text: line.trim() };
      (isHistorical(path, line) ? historical : active).push(match);
    }
  });
}

const report = {
  status: active.length ? "FAIL" : "PASS",
  active_claude_dependencies: active,
  intentional_historical_mentions: historical,
  historical_groups: [
    "immutable governing design revisions (execution wording superseded by CODEX_MIGRATION_OVERLAY.md)",
    "completed design/phase briefs",
    "completed review and resolution evidence",
    "phase-document historical addenda and provenance records",
    "quarantined provider-era prompt narrative",
  ],
};

process.stdout.write(`${JSON.stringify(report, null, 2)}\n`);
if (active.length) process.exitCode = 1;
