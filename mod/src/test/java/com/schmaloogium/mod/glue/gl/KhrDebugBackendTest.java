// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.TextureHandle;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8.1 rows {@code DebugGroupBalanceTest}, {@code DebugLabelCoverageTest} and
 * {@code DebugInactiveIsFreeTest} (PHASE_14_DOC §8.1 rows 17–19), headless: the
 * D-P14-14 activity gate, balance-safe groups (D-P14-13), the D-P14-37 boundary drain,
 * the D-P14-36 encoded-byte label budget, and emission only for existing native
 * objects (D-P14-40). Deterministic; no GL context anywhere.
 */
class KhrDebugBackendTest {

    // KHR_debug identifiers, pinned from the GL specification (stable forever).
    private static final int GL_TEXTURE = 0x1706;
    private static final int GL_SHADER = 0x82E0;
    private static final int GL_PROGRAM = 0x82E2;
    private static final int GL_FRAMEBUFFER = 0x82D8;

    private static final TextureHandle TEXTURE = new TextureHandle() {
    };
    private static final ShaderHandle SHADER = new ShaderHandle() {
    };
    private static final ProgramHandle PROGRAM = new ProgramHandle() {
    };
    private static final FramebufferHandle FRAMEBUFFER = new FramebufferHandle() {
    };

    // ------------------------------------------------------------------ fakes

    private static final class FakeDebugOps implements DebugOps {
        final List<String> log = new ArrayList<>();
        int maxLabel = 64;
        int maxDepth = 4;
        boolean failPush;
        boolean failNextPop;
        int successfulPops;

        int pushes() {
            return (int) log.stream().filter(entry -> entry.startsWith("push")).count();
        }

        List<String> labels() {
            return log.stream().filter(entry -> entry.startsWith("label")).toList();
        }

        @Override
        public int maxLabelLength() {
            return maxLabel;
        }

        @Override
        public int maxGroupStackDepth() {
            return maxDepth;
        }

        @Override
        public boolean pushGroup(String label) {
            log.add("push " + label);
            return !failPush;
        }

        @Override
        public boolean popGroup() {
            log.add("pop");
            if (failNextPop) {
                failNextPop = false;
                return false;
            }
            successfulPops++;
            return true;
        }

        @Override
        public boolean label(int identifier, int name, String label) {
            log.add("label " + identifier + " " + name + " " + label);
            return true;
        }
    }

    private static final class RecordingReporter implements DiagnosticReporter {
        final List<EngineDiagnostic> reported = new ArrayList<>();

        @Override
        public void report(EngineDiagnostic diagnostic) {
            reported.add(diagnostic);
        }
    }

    // ------------------------------------------------------------------ fixtures

    private static GLCapabilityProfile profile(int major, int minor, String... extensions) {
        return new GLCapabilityProfile(major, minor, major + "." + minor + "0",
                "vendor", "renderer", 8, 8, 16, 16, 4096, 256, 0, Set.of(extensions));
    }

    /** A resolver naming every materialized object; negative = unmaterialized. */
    private static LabelTargetResolver resolver(TextureHandle t, ShaderHandle s,
                                                ProgramHandle p, FramebufferHandle f) {
        return handle -> {
            if (handle == t) {
                return 101;
            }
            if (handle == s) {
                return 102;
            }
            if (handle == p) {
                return 103;
            }
            if (handle == f) {
                return 104;
            }
            return -1;
        };
    }

    private static KhrDebugBackend backend(GLCapabilityProfile profile, boolean glLabels,
                                           FakeDebugOps ops, LabelTargetResolver targets,
                                           RecordingReporter reporter) {
        return new KhrDebugBackend(profile, () -> glLabels, targets, ops, reporter);
    }

    // ------------------------------------------------------------------ gate (row 19 / D-P14-14)

    @Test
    void activityGateIsCapabilityAndFlagTogether() {
        RecordingReporter reporter = new RecordingReporter();
        assertAll(
                () -> assertTrue(backend(profile(4, 3), true,
                        new FakeDebugOps(), resolver(null, null, null, null), reporter).isActive()),
                () -> assertTrue(backend(profile(3, 3, "GL_KHR_debug"), true,
                        new FakeDebugOps(), resolver(null, null, null, null), reporter).isActive()),
                () -> assertFalse(backend(profile(3, 3), true,
                        new FakeDebugOps(), resolver(null, null, null, null), reporter).isActive(),
                        "capability is required"),
                () -> assertFalse(backend(profile(4, 3), false,
                        new FakeDebugOps(), resolver(null, null, null, null), reporter).isActive(),
                        "the flag is required"));
    }

    @Test
    void inactiveIsFreeForAWholeFrameOfCalls() {
        FakeDebugOps ops = new FakeDebugOps();
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), false, ops,
                resolver(TEXTURE, SHADER, PROGRAM, FRAMEBUFFER), reporter);

        for (int i = 0; i < 3; i++) {
            backend.pushGroup("frame");
            backend.label(TEXTURE, "colortex0");
            backend.popGroup();
        }

        assertTrue(ops.log.isEmpty(), "DebugTier.NONE must issue zero debug records");
        assertEquals(0, backend.realDepth());
        assertEquals(0, backend.virtualDepth());
        assertTrue(reporter.reported.isEmpty());
        assertFalse(backend.isActive());
    }

    // ------------------------------------------------------------------ groups (row 17 / D-P14-13/37)

    @Test
    void overflowGoesVirtualAndUnderflowIsARateLimitedNoOp() {
        FakeDebugOps ops = new FakeDebugOps();
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(null, null, null, null), reporter);
        int realCapacity = ops.maxDepth - 1;

        for (int i = 0; i < realCapacity + 1; i++) {
            backend.pushGroup("g" + i);
        }
        assertEquals(realCapacity, backend.realDepth());
        assertEquals(1, backend.virtualDepth(), "the overflow push went virtual, no GL");
        assertEquals(realCapacity, ops.pushes());

        backend.popGroup();
        assertEquals(0, backend.virtualDepth());
        assertEquals(0, ops.successfulPops, "a virtual pop issues no GL");

        while (backend.realDepth() > 0) {
            backend.popGroup();
        }
        assertEquals(realCapacity, ops.successfulPops, "each real depth step is one native pop");
        assertTrue(ops.successfulPops <= ops.pushes(), "native pops never exceed pushes");

        backend.popGroup(); // underflow
        assertEquals(1, reporter.reported.size(), "one diagnostic names the underflow");
        backend.popGroup(); // second underflow
        assertEquals(1, reporter.reported.size(), "rate-limited: one per session");
        assertEquals(0, backend.realDepth());
        assertEquals(0, backend.virtualDepth());
    }

    @Test
    void boundaryDrainDropsVirtualWithoutGlAndPopsExactlyTheRealGroups() {
        FakeDebugOps ops = new FakeDebugOps();
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(null, null, null, null), reporter);

        for (int i = 0; i < 5; i++) {
            backend.pushGroup("g" + i);
        }
        assertEquals(3, backend.realDepth());
        assertEquals(2, backend.virtualDepth());

        backend.drainAtBoundary();

        assertEquals(0, backend.virtualDepth(), "virtual entries left with no GL");
        assertEquals(0, backend.realDepth());
        assertEquals(3, ops.successfulPops, "exactly the outstanding real groups were popped");
        assertEquals(1, reporter.reported.size(), "one imbalance diagnostic, not one per leak");

        backend.drainAtBoundary();
        assertEquals(3, ops.successfulPops,
                "a balanced boundary drains nothing");
        assertEquals(1, reporter.reported.size(), "rate-limited to one per session");

        // The next balanced frame runs on the preserved default native group.
        backend.pushGroup("frame");
        backend.popGroup();
        assertEquals(4, ops.pushes());
        assertEquals(4, ops.successfulPops);
    }

    @Test
    void drainRetriesAfterAFailedPopWithoutOverpopping() {
        FakeDebugOps ops = new FakeDebugOps();
        ops.maxDepth = 6; // real capacity 5
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(null, null, null, null), reporter);

        for (int i = 0; i < 8; i++) {
            backend.pushGroup("g" + i);
        }
        assertEquals(5, backend.realDepth());
        assertEquals(3, backend.virtualDepth());

        ops.failNextPop = true; // the first drain pop fails: the count stays honest
        backend.drainAtBoundary();
        assertEquals(5, backend.realDepth(), "a failed native pop leaves the count unchanged");

        backend.drainAtBoundary();
        assertEquals(0, backend.realDepth());
        assertEquals(5, ops.successfulPops, "exactly the outstanding real groups were popped");
        assertEquals(0, backend.virtualDepth(), "virtual entries left no GL trace");
        assertEquals(1, reporter.reported.size());
    }

    @Test
    void pushFailureLeavesTheCountersHonest() {
        FakeDebugOps ops = new FakeDebugOps();
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(null, null, null, null), reporter);
        ops.failPush = true;

        backend.pushGroup("a");
        assertEquals(0, backend.realDepth(), "a failed push must not advance the depth");
        assertEquals(0, backend.virtualDepth());
    }

    // ------------------------------------------------------------------ labels (row 18 / D-P14-36/40)

    @Test
    void labelsEmitOnlyForExistingNativeObjectsWithTheirIdentifiers() {
        FakeDebugOps ops = new FakeDebugOps();
        RecordingReporter reporter = new RecordingReporter();
        // The texture is unmaterialized: retained on the handle, no GL (D-P14-40).
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(null, SHADER, PROGRAM, FRAMEBUFFER), reporter);

        backend.label(TEXTURE, "colortex0");
        assertTrue(ops.labels().isEmpty(), "an unmaterialized texture is retained-only");

        backend.label(SHADER, "s");
        backend.label(PROGRAM, "p");
        backend.label(FRAMEBUFFER, "f");
        assertEquals(List.of(
                "label " + GL_SHADER + " 102 s",
                "label " + GL_PROGRAM + " 103 p",
                "label " + GL_FRAMEBUFFER + " 104 f"),
                ops.labels());
    }

    @Test
    void textureIdentifierIsTheSpecConstant() {
        assertEquals(5894, GL_TEXTURE); // 0x1706, KHR_debug/GL spec
        FakeDebugOps ops = new FakeDebugOps();
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(TEXTURE, null, null, null), reporter);
        backend.label(TEXTURE, "colortex0");
        assertEquals(List.of("label 5894 101 colortex0"), ops.labels());
    }

    @Test
    void utf8BudgetTruncatesOnlyAtCompleteCodePoints() {
        // budget = GL_MAX_LABEL_LENGTH - 1 = 7 for a probed maximum of 8.
        assertAll(
                () -> assertEquals("1234567", KhrDebugBackend.clampToUtf8Budget("1234567", 7)),
                () -> assertEquals("123456", KhrDebugBackend.clampToUtf8Budget("1234567", 6)),
                // € encodes as three bytes: the cut never splits it.
                () -> assertEquals("a", KhrDebugBackend.clampToUtf8Budget("a€", 2)),
                () -> assertEquals("a€", KhrDebugBackend.clampToUtf8Budget("a€!", 4)),
                () -> assertEquals("", KhrDebugBackend.clampToUtf8Budget("€", 2),
                        "truncation need not preserve uniqueness"),
                () -> assertEquals("", KhrDebugBackend.clampToUtf8Budget("\uD83D\uDE00", 3),
                        "a four-byte code point never splits"),
                () -> assertEquals("ab", KhrDebugBackend.clampToUtf8Budget("ab€", 3)));

        // Property sweep: clamping only truncates, and every result stays within budget.
        String mixed = "a€\uD83D\uDE00b€c";
        for (int budget = 0; budget <= 16; budget++) {
            String clamped = KhrDebugBackend.clampToUtf8Budget(mixed, budget);
            assertTrue(mixed.startsWith(clamped), "clamping only truncates (budget " + budget + ")");
            assertTrue(clamped.getBytes(StandardCharsets.UTF_8).length <= budget,
                    "budget " + budget + " produced " + clamped);
        }
    }

    @Test
    void emittedLabelsNeverExceedTheEncodedBudget() {
        FakeDebugOps ops = new FakeDebugOps();
        ops.maxLabel = 8; // budget 7 bytes
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(TEXTURE, null, null, null), reporter);

        backend.label(TEXTURE, "colortex0-of-very-long-name");
        assertEquals(1, ops.labels().size());
        String payload = ops.labels().get(0).substring("label 5894 101 ".length());
        assertTrue(payload.getBytes(StandardCharsets.UTF_8).length <= 7,
                "the emitted payload is clamped to GL_MAX_LABEL_LENGTH - 1 bytes");
    }

    @Test
    void labelBudgetProbeFailureDisablesLabeling() {
        FakeDebugOps ops = new FakeDebugOps();
        ops.maxLabel = 0; // GL_MAX_LABEL_LENGTH unavailable
        RecordingReporter reporter = new RecordingReporter();
        KhrDebugBackend backend = backend(profile(4, 3), true, ops,
                resolver(TEXTURE, null, null, null), reporter);
        backend.label(TEXTURE, "colortex0");
        assertTrue(ops.labels().isEmpty(), "without a probed budget the backend never guesses one");
    }

}
