import {
  mkdirSync,
  readFileSync,
  rmSync,
  writeFileSync,
} from "node:fs";
import { join } from "node:path";
import { parentPort, workerData } from "node:worker_threads";
import {
  executeVerification,
  resolveContract,
} from "../.agents/skills/verify-loop/scripts/engine.mjs";

const [root, target, rendezvous] = workerData
  ? [workerData.root, workerData.target, workerData.rendezvous]
  : process.argv.slice(2);
if (!root || !target || !rendezvous) {
  throw new Error("Expected repository root, target ID, and rendezvous directory");
}

const delay = (ms) => new Promise((resolvePromise) => setTimeout(resolvePromise, ms));
const contract = resolveContract(root, target, { preset: "lean", maxRounds: 1 });
let announcedAttack = false;

async function waitForPeerAttack() {
  if (!announcedAttack) {
    writeFileSync(join(rendezvous, `attack-${target}`), "ready\n");
    announcedAttack = true;
  }
  const deadline = Date.now() + 5000;
  while (true) {
    const bothReady = ["mini", "mini-b"]
      .every((id) => {
        try {
          readFileSync(join(rendezvous, `attack-${id}`));
          return true;
        } catch (error) {
          if (error?.code === "ENOENT") return false;
          throw error;
        }
      });
    if (bothReady) return;
    if (Date.now() >= deadline) throw new Error("Timed out waiting for peer Attack stage");
    await delay(10);
  }
}

function writePassReview(path) {
  writeFileSync(
    join(root, path),
    [
      "## 0. Method and reading order",
      "",
      "Independent derivation completed.",
      "",
      "## 1. Findings",
      "",
      "No findings.",
      "",
      "## 2. Checked and clean",
      "",
      "All requested areas checked.",
      "",
      "## 3. Verdict",
      "",
      "# PASS",
      "Counts: blocking=0; corrections=0; notes=0",
      "Interface changed: no",
      "",
    ].join("\n"),
  );
}

const agentRunner = async ({ label }) => {
  if (label.startsWith("attack:")) {
    await waitForPeerAttack();
    return { findings: [], areas_examined: ["public"], clean_areas: "clean" };
  }
  if (label.startsWith("adjudicate:")) {
    const writerMarker = join(rendezvous, "writer-active");
    mkdirSync(writerMarker);
    try {
      await delay(100);
      writePassReview(contract.reviewOutput);
    } finally {
      rmSync(writerMarker, { recursive: true, force: true });
    }
    return {
      verdict: "PASS",
      blocking_count: 0,
      correction_count: 0,
      note_count: 0,
      interface_changed: false,
      candidate_dispositions: [],
      review_file: contract.reviewOutput,
      rationale: "clean",
      findings_dropped_on_derivation: "",
    };
  }
  throw new Error(`Unexpected fake role ${label}`);
};

const result = await executeVerification(contract, {
  reviewOnly: true,
  agentRunner,
  logger: () => {},
  coordinationPollMs: 5,
  coordinationTimeoutMs: 5000,
  coordinationLogIntervalMs: 1000,
});
if (parentPort) parentPort.postMessage(result);
else process.stdout.write(`${JSON.stringify(result)}\n`);
