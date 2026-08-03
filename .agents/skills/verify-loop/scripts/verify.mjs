#!/usr/bin/env node

import {
  discoverRepoRoot,
  dryRunFixupPlan,
  dryRunPlan,
  executeFixupContinuation,
  executeVerification,
  resolveContract,
  VerificationError,
} from "./engine.mjs";

const HELP = `Usage:
  scripts/verify --target <id|path> [options]
  node .agents/skills/verify-loop/scripts/verify.mjs --target <id|path> [options]

Options:
  --target <id|path>       Target ID under verification/targets or a repo-relative manifest path.
  --design-version <name>  Override a design-governed target's §0 version (for example: v3).
  --preset <name>          Policy preset (default: lean).
  --start-round <n>        Must equal the next round discovered from prior reviews.
  --max-rounds <n>         Maximum rounds (default: 6; 0 performs resolution without agents).
  --review-only            Stop after one adjudicated review; never run fix-up.
  --fixup-review <path>    Continue only the latest correction-bearing review; use "latest" normally.
  --dry-run                Resolve and validate everything, print plan/cost, run zero agents.
  --estimate               Alias for --dry-run.
  --model <model>          Optional model override passed to each codex exec session.
  --agent-timeout-ms <n>   Per-agent timeout in milliseconds (default: 1800000).
  --json                   Print compact JSON instead of formatted JSON.
  --help                   Show this help.

The orchestrator always resolves the Git root dynamically, validates every configured path and
content selector, rejects ambiguous/missing prior-review state and output collisions, and runs
read-only roles with codex exec --sandbox read-only. Writer stages use workspace-write plus
deterministic post-stage allowlist and immutable-evidence checks.
`;

function parseInteger(flag, value) {
  if (value === undefined || !/^-?\d+$/.test(value)) {
    throw new VerificationError(`${flag} requires an integer`);
  }
  return Number(value);
}

function parseArgs(argv) {
  const options = {
    preset: "lean",
    reviewOnly: false,
    dryRun: false,
    compactJson: false,
  };
  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];
    switch (arg) {
      case "--target":
        options.target = argv[++index];
        break;
      case "--design-version":
        options.designVersion = argv[++index];
        break;
      case "--preset":
        options.preset = argv[++index];
        break;
      case "--start-round":
        options.startRound = parseInteger(arg, argv[++index]);
        break;
      case "--max-rounds":
        options.maxRounds = parseInteger(arg, argv[++index]);
        break;
      case "--review-only":
        options.reviewOnly = true;
        break;
      case "--fixup-review":
        options.fixupReview = argv[++index];
        break;
      case "--dry-run":
      case "--estimate":
        options.dryRun = true;
        break;
      case "--model":
        options.model = argv[++index];
        break;
      case "--agent-timeout-ms":
        options.agentTimeoutMs = parseInteger(arg, argv[++index]);
        break;
      case "--json":
        options.compactJson = true;
        break;
      case "--help":
      case "-h":
        options.help = true;
        break;
      default:
        throw new VerificationError(`Unknown argument: ${arg}`);
    }
  }
  if (!options.help && !options.target) throw new VerificationError("--target is required");
  if (!options.help && (!options.preset || options.preset.startsWith("--"))) {
    throw new VerificationError("--preset requires a value");
  }
  if (options.model !== undefined && (!options.model || options.model.startsWith("--"))) {
    throw new VerificationError("--model requires a value");
  }
  if (
    options.designVersion !== undefined
    && (!options.designVersion || options.designVersion.startsWith("--"))
  ) {
    throw new VerificationError("--design-version requires a design directory label");
  }
  if (options.fixupReview !== undefined && (!options.fixupReview || options.fixupReview.startsWith("--"))) {
    throw new VerificationError("--fixup-review requires latest or a repo-relative review path");
  }
  if (options.fixupReview && options.reviewOnly) {
    throw new VerificationError("--fixup-review and --review-only are mutually exclusive");
  }
  if (
    options.agentTimeoutMs !== undefined
    && (!Number.isSafeInteger(options.agentTimeoutMs) || options.agentTimeoutMs < 1000)
  ) {
    throw new VerificationError("--agent-timeout-ms must be an integer >= 1000");
  }
  return options;
}

async function main() {
  const options = parseArgs(process.argv.slice(2));
  if (options.help) {
    process.stdout.write(HELP);
    return;
  }
  const root = discoverRepoRoot();
  const contract = resolveContract(root, options.target, options);
  let result;
  if (options.fixupReview) {
    result = options.dryRun
      ? dryRunFixupPlan(contract, options.fixupReview)
      : await executeFixupContinuation(contract, options);
  } else {
    result = options.dryRun || options.maxRounds === 0
      ? dryRunPlan(contract, options)
      : await executeVerification(contract, options);
  }
  process.stdout.write(`${JSON.stringify(result, null, options.compactJson ? 0 : 2)}\n`);
}

main().catch((error) => {
  const failure = {
    outcome: "ERROR",
    code: error.code || "ERROR",
    message: error.message,
    details: error.details,
  };
  process.stderr.write(`${JSON.stringify(failure, null, 2)}\n`);
  process.exitCode = 1;
});
