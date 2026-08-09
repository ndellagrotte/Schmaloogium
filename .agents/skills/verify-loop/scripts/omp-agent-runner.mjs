/**
 * omp SDK adapter for the verification engine.
 *
 * One call to `runOmpAgent` = one fresh, isolated omp agent session
 * (`createAgentSession`) with:
 *   - the role prompt as a full system-prompt override (legacy role prompts
 *     are self-contained; no AGENTS.md/skills/MCP/LSP ambient context);
 *   - a tool allowlist in place of the Codex sandbox flag: "read-only" maps to
 *     read/grep/glob, "workspace-write" adds write/edit. No role session ever
 *     receives bash/eval/task, so no indirect write or spawn route exists;
 *   - `requireYieldTool` + `outputSchema` (strict mode) in place of
 *     `codex exec --output-schema`: the role submits its machine-consumed JSON
 *     through `yield` — one terminal call, or incremental `type: ["section"]`
 *     submissions assembled here at finalization, which ends the session;
 *   - a deterministic pre-execution `tool_call` hook built from the engine's
 *     per-role enforcement payload: writer sessions may write only their
 *     exact allowlist, and read-only sessions are barred from forbidden-source
 *     patterns and deny-listed prior reviews before the read happens;
 *   - an in-memory session manager, so role sessions leave no resumable state.
 *
 * The engine's post-stage worktree-hash and immutable-evidence checks are
 * unchanged and remain the backstop; this module adds the deterministic
 * pre-write layer on top.
 *
 * This module is imported lazily by engine.mjs and is the only verification
 * source file that touches the omp SDK. It requires Bun (the SDK's entry
 * point is TypeScript) and an installed `@oh-my-pi/pi-coding-agent`
 * (pinned: 17.2.11). Resolution order: `OMP_SDK_PATH`, a bare specifier
 * resolvable from this file, then the package behind the `omp` binary on PATH.
 */
import { spawnSync } from "node:child_process";
import { existsSync, readFileSync, realpathSync } from "node:fs";
import { dirname, isAbsolute, join, relative, resolve, sep } from "node:path";
import { pathToFileURL } from "node:url";
import {
  DEFAULT_AGENT_TIMEOUT_MS,
  VerificationError,
  assertSchema,
  globToRegExp,
  loadJson,
} from "./engine.mjs";

const READ_ONLY_TOOLS = ["read", "grep", "glob"];
const WRITER_TOOLS = ["read", "grep", "glob", "write", "edit"];
const SDK_PACKAGE = "@oh-my-pi/pi-coding-agent";
const SDK_PINNED_VERSION = "17.2.11";
const TIMEOUT_DISPOSE_GRACE_MS = 5000;

let cachedSdkPromise;
let sessionCounter = 0;

function candidateSdkEntries() {
  const candidates = [];
  if (process.env.OMP_SDK_PATH) {
    candidates.push(process.env.OMP_SDK_PATH);
  }
  const which = process.platform === "win32" ? "where" : "which";
  const found = spawnSync(which, ["omp"], { encoding: "utf8" });
  if (found.status === 0) {
    const first = found.stdout.trim().split(/\r?\n/)[0];
    if (first) {
      try {
        const binary = realpathSync(first);
        // <pkg>/dist/cli.js → package root; also tolerate a bare bin symlink.
        candidates.push(resolve(binary, "..", ".."));
      } catch {
        // Fall through to the bare-specifier attempt.
      }
    }
  }
  return candidates;
}

async function importSdk() {
  const errors = [];
  for (const candidate of candidateSdkEntries()) {
    const entry = /\.(ts|js|mjs)$/.test(candidate) ? candidate : join(candidate, "src", "sdk.ts");
    if (!existsSync(entry)) {
      errors.push(`${candidate}: no SDK entry at ${entry}`);
      continue;
    }
    try {
      const module = await import(pathToFileURL(entry).href);
      if (typeof module.createAgentSession !== "function") {
        errors.push(`${entry}: createAgentSession export missing`);
        continue;
      }
      const sessionsEntry = join(dirname(entry), "session", "session-manager.ts");
      const sessions = await import(pathToFileURL(sessionsEntry).href);
      if (typeof sessions.SessionManager?.inMemory !== "function") {
        errors.push(`${sessionsEntry}: SessionManager.inMemory export missing`);
        continue;
      }
      return { createAgentSession: module.createAgentSession, SessionManager: sessions.SessionManager, origin: entry };
    } catch (error) {
      errors.push(`${entry}: ${error.message}`);
    }
  }
  try {
    const module = await import(SDK_PACKAGE);
    const sessions = await import(`${SDK_PACKAGE}/session/session-manager`);
    if (typeof module.createAgentSession === "function" && typeof sessions.SessionManager?.inMemory === "function") {
      return {
        createAgentSession: module.createAgentSession,
        SessionManager: sessions.SessionManager,
        origin: SDK_PACKAGE,
      };
    }
    errors.push(`${SDK_PACKAGE}: createAgentSession/SessionManager exports missing`);
  } catch (error) {
    errors.push(`${SDK_PACKAGE}: ${error.message}`);
  }
  throw new VerificationError(
    "Cannot load the omp SDK. Live verification runs require Bun and the pinned "
      + `@oh-my-pi/pi-coding-agent ${SDK_PINNED_VERSION} package. Tried:\n- ${errors.join("\n- ")}`,
    "AGENT_ERROR",
  );
}

async function loadSdk() {
  cachedSdkPromise ??= importSdk();
  try {
    return await cachedSdkPromise;
  } catch (error) {
    cachedSdkPromise = undefined;
    throw error;
  }
}

function sdkVersion(origin) {
  try {
    const packagePath = origin.endsWith(".ts") || origin.endsWith(".js")
      ? join(dirname(dirname(origin)), "package.json")
      : join(origin, "package.json");
    return JSON.parse(readFileSync(packagePath, "utf8")).version;
  } catch {
    return "unknown";
  }
}

/** Normalize a tool-supplied path to a repo-relative POSIX path, or null when it escapes. */
function normalizeRepoPath(root, supplied) {
  if (typeof supplied !== "string" || supplied.length === 0) return null;
  const absolute = isAbsolute(supplied) ? resolve(supplied) : resolve(root, supplied);
  const rel = relative(root, absolute);
  if (rel === "" || rel === ".." || rel.startsWith(`..${sep}`) || isAbsolute(rel)) return null;
  return rel.split(sep).join("/");
}

function pathMatchesAny(repoPath, patterns) {
  return patterns.some((pattern) => globToRegExp(pattern).test(repoPath));
}

/**
 * Build the inline extension enforcing the role's path policy before any tool
 * executes. Headless (`ctx.hasUI` false), a block is final. Exported for the
 * dependency-free unit tests; the engine itself only calls runOmpAgent.
 */
export function makeEnforcementExtension(root, enforcement, label) {
  const readDenyPatterns = enforcement?.readDenyPatterns ?? [];
  const readDenyPaths = new Set(enforcement?.readDenyPaths ?? []);
  const writeAllow = new Set(enforcement?.writeAllow ?? []);
  const denyRead = (repoPath) => {
    if (repoPath === null) {
      return "path escapes the repository";
    }
    if (readDenyPaths.has(repoPath)) {
      return `prior review ${repoPath} is barred to this role (independence rule)`;
    }
    if (pathMatchesAny(repoPath, readDenyPatterns)) {
      return `${repoPath} matches a forbidden-source pattern`;
    }
    return "";
  };
  return (pi) => {
    pi.on("tool_call", (event) => {
      const input = event.input && typeof event.input === "object" ? event.input : {};
      if (event.toolName === "write" || event.toolName === "edit") {
        const repoPath = normalizeRepoPath(root, input.path);
        if (repoPath === null || !writeAllow.has(repoPath)) {
          return {
            block: true,
            reason: `${label}: ${event.toolName} to ${input.path} is outside this role's write allowlist`,
          };
        }
        return undefined;
      }
      if (event.toolName === "read" || event.toolName === "grep" || event.toolName === "glob") {
        const supplied = input.path;
        // grep/glob may omit path (whole-tree search) — allowed; hits on denied
        // paths are not returned because the targets are never opened by path.
        if (typeof supplied !== "string" || supplied.length === 0) return undefined;
        const reason = denyRead(normalizeRepoPath(root, supplied));
        if (reason) return { block: true, reason: `${label}: ${reason}` };
      }
      return undefined;
    });
  };
}

/**
 * Top-level property names of a role result schema declared as arrays.
 * Incremental yield sections under these labels accumulate into lists even
 * when the role submits a single element. Mirrors the SDK's arrayValuedLabels,
 * narrowed to plain JSON Schema: every role schema in this repository declares
 * `type: "array"` directly on the property.
 */
export function arrayValuedSchemaLabels(schemaObject) {
  const labels = new Set();
  const properties = schemaObject && typeof schemaObject === "object" ? schemaObject.properties : undefined;
  if (!properties || typeof properties !== "object") return labels;
  for (const [key, value] of Object.entries(properties)) {
    if (value && typeof value === "object" && value.type === "array") labels.add(key);
  }
  return labels;
}

function truncateTelemetry(text, limit = 240) {
  const flat = String(text).replace(/\s+/g, " ").trim();
  return flat.length <= limit ? flat : `${flat.slice(0, limit)}…`;
}

/** One-line summary of a rejected yield call, for nudges and error telemetry. */
function extractYieldRejection(event) {
  const content = event?.result?.content;
  if (Array.isArray(content)) {
    const text = content
      .filter((block) => block?.type === "text" && typeof block.text === "string")
      .map((block) => block.text)
      .join(" ")
      .trim();
    if (text) return truncateTelemetry(text);
  }
  const fallback = event?.result?.details?.error ?? event?.result?.error ?? event?.error?.message ?? event?.error;
  if (typeof fallback === "string" && fallback.trim()) return truncateTelemetry(fallback);
  return "yield call rejected (the tool surfaced no error detail)";
}

/**
 * Fold a session's accepted yield events into the role's final payload.
 * Mirrors assembleYieldResult in the pinned SDK (src/task/yield-assembly.ts,
 * @oh-my-pi/pi-coding-agent 17.2.11), with one deliberate extension: sections
 * are honored even when no terminal finalize arrived, because this adapter
 * only resolves results after its reminder ladder is exhausted — the schema
 * check downstream decides whether the assembled object is complete.
 *
 * Precedence: a terminal yield carrying data wins verbatim; otherwise
 * incremental sections (array-typed yields, aborted ones excluded) assemble
 * into one object in submission order; otherwise the caller falls back to the
 * last assistant text. `sectionCounts` rides along for nudges and telemetry.
 */
export function resolveYieldOutcome(yieldItems, arrayLabels, lastText = "") {
  let terminal;
  for (let index = yieldItems.length - 1; index >= 0; index -= 1) {
    const item = yieldItems[index];
    if (!item || (Array.isArray(item.type) && item.type.length > 0)) continue;
    terminal = item;
    break;
  }
  const sections = {};
  const counts = new Map();
  for (const item of yieldItems) {
    if (!item || item.status === "aborted") continue;
    if (!Array.isArray(item.type) || item.type.length === 0) continue;
    const labels = item.type.map((value) => String(value).trim()).filter(Boolean);
    const hasData = item.data !== undefined && item.data !== null;
    for (const label of labels) {
      const value = hasData ? item.data : lastText;
      const count = counts.get(label) ?? 0;
      if (count === 0) {
        sections[label] = arrayLabels.has(label) ? [value] : value;
      } else if (Array.isArray(sections[label])) {
        sections[label].push(value);
      } else {
        sections[label] = [sections[label], value];
      }
      counts.set(label, count + 1);
    }
  }
  const sectionCounts = Object.fromEntries(counts);
  if (terminal && terminal.data !== undefined && terminal.data !== null) {
    return { kind: "data", data: terminal.data, sectionCounts };
  }
  if (counts.size > 0) {
    return { kind: "sections", data: sections, sectionCounts };
  }
  return { kind: "fallback", sectionCounts };
}

/**
 * Build the next reminder for a session whose turn ended without a terminal
 * yield. State-aware: a rejected yield names the rejection so the model can
 * repair instead of repeat; accumulated incremental sections get an explicit
 * finalize instruction; otherwise the generic contract reminder carries the
 * incremental protocol so oversized results stop depending on one giant turn.
 */
export function buildYieldNudge({ yieldRejections, sectionCounts }) {
  const base = "Your turn ended without the required terminal yield call. Continue the assigned role's work now.";
  if (yieldRejections.length > 0) {
    const last = yieldRejections[yieldRejections.length - 1];
    return `${base} Your previous yield call was rejected: ${last} Correct the payload and resubmit`
      + " — do not restart the work, and do not repeat the identical call.";
  }
  const labels = Object.keys(sectionCounts);
  if (labels.length > 0) {
    const summary = labels.map((key) => `${key}×${sectionCounts[key]}`).join(", ");
    return `${base} Incremental result sections received so far: ${summary}. If the result is complete, finalize`
      + " with one terminal yield call (`type: \"result\"`, empty `result` object); otherwise continue submitting"
      + " sections, one `yield` per element.";
  }
  return `${base} When the role is complete, submit the JSON result matching the supplied schema with one terminal`
    + " yield call (`result.data`). If the object is large, submit it incrementally instead: one `yield` per array"
    + " element with `type: [\"<array-field>\"]`, each scalar field once with `type: [\"<field>\"]`, then a terminal"
    + " finalize (`type: \"result\"`, empty `result`). Never answer in prose.";
}

function formatYieldTelemetry(yields, yieldRejections, sectionCounts) {
  const parts = [`accepted yields: ${yields.length}`];
  const labels = Object.keys(sectionCounts ?? {});
  if (labels.length > 0) {
    parts.push(`sections: ${labels.map((key) => `${key}×${sectionCounts[key]}`).join(", ")}`);
  }
  if (yieldRejections.length > 0) {
    parts.push(`rejected yields: ${yieldRejections.length} (last: ${yieldRejections[yieldRejections.length - 1]})`);
  }
  return parts.join("; ");
}

/** Extract the last assistant text, for the legacy-style raw-JSON fallback. */
function lastAssistantText(session) {
  const messages = session.state?.messages ?? [];
  for (let index = messages.length - 1; index >= 0; index -= 1) {
    const message = messages[index];
    if (message?.role !== "assistant") continue;
    const content = message.content;
    if (typeof content === "string" && content.trim()) return content;
    if (Array.isArray(content)) {
      const text = content
        .filter((block) => block?.type === "text" && typeof block.text === "string")
        .map((block) => block.text)
        .join("");
      if (text.trim()) return text;
    }
  }
  return "";
}

/**
 * Run one isolated omp role session and return its schema-validated JSON.
 * Mirrors the legacy runCodexAgent contract: throws VerificationError with
 * code AGENT_ERROR on startup failure, timeout, missing/invalid output.
 */
export async function runOmpAgent({
  root,
  prompt,
  schema,
  sandbox = "read-only",
  model,
  timeoutMs = DEFAULT_AGENT_TIMEOUT_MS,
  label = "agent",
  enforcement,
  onEvent,
}) {
  // Session-level telemetry reaches the operator through the engine's progress
  // stream when there is one; a standalone caller still gets it on stderr.
  const report = onEvent || ((message) => process.stderr.write(`[verify-loop] ${label}: ${message}\n`));
  const { createAgentSession, SessionManager, origin } = await loadSdk();
  const version = sdkVersion(origin);
  if (version !== "unknown" && version !== SDK_PINNED_VERSION) {
    report(`WARNING omp SDK ${version} differs from pinned ${SDK_PINNED_VERSION} (${origin})`);
  }

  const schemaObject = loadJson(schema);
  const toolNames = sandbox === "workspace-write" ? WRITER_TOOLS : READ_ONLY_TOOLS;
  const extensions = enforcement ? [makeEnforcementExtension(root, enforcement, label)] : [];

  let session;
  sessionCounter += 1;
  // Sessions register in the process-global agent registry; the default "Main"
  // identity makes concurrent role sessions replace one another and fail
  // initialization, so every role session gets a unique id.
  const agentId = `Verify-${label.replace(/[^A-Za-z0-9]+/g, "-")}-${process.pid}-${sessionCounter}`;
  try {
    ({ session } = await createAgentSession({
      cwd: root,
      agentId,
      systemPrompt: prompt,
      toolNames,
      restrictToolNames: true,
      requireYieldTool: true,
      outputSchema: schemaObject,
      outputSchemaMode: "strict",
      sessionManager: SessionManager.inMemory(root),
      hasUI: false,
      skills: [],
      contextFiles: [],
      promptTemplates: [],
      slashCommands: [],
      enableMCP: false,
      enableLsp: false,
      enableIrc: false,
      disableExtensionDiscovery: true,
      skipPythonPreflight: true,
      extensions,
      ...(model ? { modelPattern: model } : {}),
      deadline: Date.now() + timeoutMs,
    }));
  } catch (error) {
    throw new VerificationError(`${label} failed to start: ${error.message}`, "AGENT_ERROR");
  }

  const arrayLabels = arrayValuedSchemaLabels(schemaObject);
  const yields = [];
  const yieldRejections = [];
  session.subscribe((event) => {
    if (event?.type !== "tool_execution_end" || event.toolName !== "yield") return;
    if (event.isError) {
      yieldRejections.push(extractYieldRejection(event));
      return;
    }
    const details = event.result?.details;
    if (!details || typeof details !== "object") return;
    if (details.status !== "success" && details.status !== "aborted") return;
    yields.push({
      data: details.data,
      status: details.status,
      type: details.type,
      useLastTurn: details.useLastTurn === true,
      schemaOverridden: details.schemaOverridden === true,
    });
  });

  let timedOut = false;
  const timer = setTimeout(() => {
    timedOut = true;
    session.dispose().catch(() => {});
  }, timeoutMs + TIMEOUT_DISPOSE_GRACE_MS);
  const hasTerminalYield = () => yields.some(
    (item) => !Array.isArray(item.type) || item.type.length === 0,
  );
  // A plain SDK prompt() is turn-scoped: a model that ends its turn in prose
  // without the terminal yield gets no executor-side reminder ladder. Drive it
  // here — re-prompt until the terminal yield lands, bounded like a reminder
  // ladder, then fall through to section assembly and the legacy raw-JSON text
  // fallback. Each nudge is state-aware (buildYieldNudge) and every iteration
  // leaves a stderr telemetry line so a hung role is diagnosable mid-run.
  const MAX_NUDGES = 12;
  try {
    await session.prompt(
      "Execute exactly the one role assigned in your system prompt, then submit the final "
        + "JSON result with the yield protocol from your instructions (one terminal yield call, "
        + "or incremental sections followed by a terminal finalize).",
      { synthetic: true, expandPromptTemplates: false },
    );
    for (let nudge = 0; !hasTerminalYield() && nudge < MAX_NUDGES; nudge += 1) {
      const state = resolveYieldOutcome(yields, arrayLabels);
      report(
        `turn ended without terminal yield; ${formatYieldTelemetry(yields, yieldRejections, state.sectionCounts)}`
          + `; nudging (${nudge + 1}/${MAX_NUDGES})`,
      );
      await session.prompt(
        buildYieldNudge({ yieldRejections, sectionCounts: state.sectionCounts }),
        { synthetic: true, expandPromptTemplates: false },
      );
    }
  } catch (error) {
    if (timedOut) {
      throw new VerificationError(`${label} timed out after ${timeoutMs}ms`, "AGENT_ERROR");
    }
    throw new VerificationError(`${label} prompt failed: ${error.message}`, "AGENT_ERROR");
  } finally {
    clearTimeout(timer);
  }

  const fallbackText = lastAssistantText(session);
  const outcome = resolveYieldOutcome(yields, arrayLabels, fallbackText);
  const telemetry = formatYieldTelemetry(yields, yieldRejections, outcome.sectionCounts);
  let parsed;
  let resultLabel = `${label} result`;
  if (outcome.kind === "data") {
    parsed = outcome.data;
  } else if (outcome.kind === "sections") {
    parsed = outcome.data;
    resultLabel = `${label} result assembled from incremental yield sections`;
  } else {
    if (!fallbackText.trim()) {
      await session.dispose().catch(() => {});
      throw new VerificationError(
        `${label} produced no yield and no final text (${telemetry})`,
        "AGENT_ERROR",
      );
    }
    try {
      parsed = JSON.parse(fallbackText.trim());
    } catch (error) {
      await session.dispose().catch(() => {});
      throw new VerificationError(
        `${label} returned invalid JSON: ${error.message} (${telemetry})\n${fallbackText}`,
        "AGENT_ERROR",
      );
    }
  }
  await session.dispose().catch(() => {});
  // An agent payload that fails the role schema is invalid *output*, so it
  // belongs to the same AGENT_ERROR contract as missing/invalid results —
  // not the uncoded VerificationError assertSchema would otherwise raise.
  try {
    assertSchema(parsed, schema, resultLabel);
  } catch (error) {
    throw new VerificationError(`${error.message} (${telemetry})`, "AGENT_ERROR");
  }
  return parsed;
}
