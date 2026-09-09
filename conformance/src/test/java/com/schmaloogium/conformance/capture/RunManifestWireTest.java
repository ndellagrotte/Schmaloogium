// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.Manifests;
import com.schmaloogium.conformance.wire.CanonicalText;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The {@code schmaloogium.run-manifest/4} wire (§4.5.4 + D-P2-50): strict parse,
 * byte-identical round trip, and rejection of every non-canonical form.
 */
class RunManifestWireTest {

    @Test
    void validManifestRoundTripsByteIdentically() throws IOException {
        RunManifest manifest = Manifests.valid();
        String rendered = RunManifestWriter.render(manifest);
        RunManifest parsed = RunManifestReader.parse(new StringReader(rendered));
        assertEquals(rendered, RunManifestWriter.render(parsed));
    }

    @Test
    void schemaLineIsRequiredFirst() {
        RunManifest manifest = Manifests.valid();
        String withoutSchema = RunManifestWriter.render(manifest)
            .replaceFirst("schema = schmaloogium\\.run-manifest/4\\n", "");
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(withoutSchema));
        String v3 = RunManifestWriter.render(manifest).replace("/4", "/3");
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(v3));
    }

    @Test
    void unknownCoreKeyIsRejected() {
        RunManifest manifest = Manifests.with(Manifests.valid(), b ->
            b.set("run.unknownKey", new RunManifest.Value.Text("\"surprise\"")));
        assertThrows(RuntimeException.class, () -> RunManifestReader
            .parse(RunManifestWriter.render(manifest)));
    }

    @Test
    void duplicateKeyIsRejected() {
        String body = RunManifestWriter.render(Manifests.valid());
        int insertion = body.indexOf("frontEnd.");
        String duplicated = body.substring(0, insertion) + "frontEnd.completed = true\n"
            + body.substring(insertion);
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(duplicated));
    }

    @Test
    void missingRequiredKeyIsRejected() {
        RunManifest manifest = Manifests.valid();
        String body = RunManifestWriter.render(manifest)
            .replaceFirst("pack\\.id = .*\\n", "");
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(body));
    }

    @Test
    void wrongValueTypeIsRejected() {
        RunManifest manifest = Manifests.with(Manifests.valid(), b ->
            b.set("run.shadersActiveThroughout", new RunManifest.Value.Int(1)));
        assertThrows(RuntimeException.class, () -> RunManifestReader
            .parse(RunManifestWriter.render(manifest)));
    }

    @Test
    void outOfLexicographicOrderIsRejected() {
        RunManifest base = Manifests.valid();
        String body = RunManifestWriter.render(base);
        String swapped = body.replaceFirst(
            "(pack\\.id = [^\\n]+\\n)(pack\\.licence = [^\\n]+\\n)",
            "$2$1");
        assertNotEquals(body, swapped);
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(swapped));
    }

    @Test
    void badEscapeAndRawControlAreRejected() {
        RunManifest base = Manifests.valid();
        String body = RunManifestWriter.render(base);
        assertThrows(RuntimeException.class,
            () -> RunManifestReader.parse(body.replace("\\\"", "\\x22")));
        assertThrows(RuntimeException.class,
            () -> RunManifestReader.parse(body.replaceFirst("\\n", "\n\t")));
    }

    @Test
    void nonCanonicalNumberIsRejected() {
        String corrupted = RunManifestWriter.render(Manifests.valid())
            .replace("clock.partialTicks = 0.0", "clock.partialTicks = 0");
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(corrupted));
    }

    @Test
    void conditionalTimingKeysAreChecked() {
        // available=false demands complete=false, zero steps, no origin block.
        RunManifest notReached = Manifests.with(Manifests.valid(), b -> b
            .set("timing.available", new RunManifest.Value.Bool(false))
            .set("timing.restoration", new RunManifest.Value.Token("NOT_REACHED"))
            .set("timing.complete", new RunManifest.Value.Bool(false))
            .set("timing.failureReason", new RunManifest.Value.Text("\"owner timing absent\""))
            .set("timing.steps.count", new RunManifest.Value.Int(0))
            .set("gl.available", new RunManifest.Value.Bool(false))
            .set("resources.available", new RunManifest.Value.Bool(false))
            .set("hooks.available", new RunManifest.Value.Bool(false))
            .set("run.compatVerdict", new RunManifest.Value.Token("NOT_REACHED"))
            .set("run.exitStatus", new RunManifest.Value.Token("SKIPPED"))
            .set("run.failureReason", new RunManifest.Value.Text("\"no owner evidence\"")));
        for (String key : notReached.entries().keySet().stream()
            .filter(k -> k.startsWith("timing.origin.")
                || (k.startsWith("timing.steps.") && !k.equals("timing.steps.count")))
            .toList()) {
            notReached = Manifests.with(notReached, b -> b.unset(key));
        }
        notReached = Manifests.with(notReached, b -> b.unset("gl.profile_text"));
        RunManifestReader.parse(RunManifestWriter.render(notReached)); // accepted grammar-wise

        // available=true with restoration=NOT_REACHED parses but is nonsense; the
        // inconsistent pair the READER rejects is available=false keeping the origin.
        RunManifest originKept = Manifests.with(notReached, b -> {
            b.text("timing.origin.checkpointId", "\"checkpoint-0\"");
            return b;
        });
        assertThrows(RuntimeException.class, () -> RunManifestReader
            .parse(RunManifestWriter.render(originKept)));
    }

    @Test
    void conditionalGlAvailabilityIsChecked() {
        // gl.available=true without profile_text is rejected.
        RunManifest missing = Manifests.with(Manifests.valid(), b -> b.unset("gl.profile_text"));
        assertThrows(RuntimeException.class, () -> RunManifestReader
            .parse(RunManifestWriter.render(missing)));

        // gl.available=false with profile_text is rejected.
        RunManifest present = Manifests.with(Manifests.valid(), b -> {
            b.set("gl.available", new RunManifest.Value.Bool(false));
            return b;
        });
        assertThrows(RuntimeException.class, () -> RunManifestReader
            .parse(RunManifestWriter.render(present)));
    }

    @Test
    void denseFamilyGapsAndCompletenessAreRejected() {
        RunManifest base = Manifests.valid();
        String body = RunManifestWriter.render(base);
        String missingMember = body.replaceFirst("programs\\.0\\.driverLog = [^\\n]*\\n", "");
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(missingMember));

        String sparse = body.replaceAll("programs\\.2\\.", "programs.3.");
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(sparse));
    }

    @Test
    void extensionKeysArePreservedVerbatimAndNeverCore() {
        RunManifest extended = Manifests.with(Manifests.valid(), b ->
            b.text("x.mytool.buildStamp", "stamp-2026-09-09"));
        RunManifest parsed = RunManifestReader.parse(RunManifestWriter.render(extended));
        assertEquals(new RunManifest.Value.Token("\"stamp-2026-09-09\""),
            parsed.entries().get("x.mytool.buildStamp"));
        String with = RunManifestWriter.render(extended);
        String without = with.replaceFirst("x\\.mytool\\.buildStamp = [^\\n]*\\n", "");
        assertEquals(with, without + "x.mytool.buildStamp = \"stamp-2026-09-09\"\n");
    }

    @Test
    void hostileProfileTextSurvivesTheWire() {
        String hostile = "line1\nquote\" backslash\\ tab\t";
        RunManifest hostileDoc = Manifests.with(Manifests.valid(), b -> {
            b.set("gl.profile_text", new RunManifest.Value.Text(hostile));
            return b;
        });
        String rendered = RunManifestWriter.render(hostileDoc);
        RunManifest parsed = RunManifestReader.parse(rendered);
        RunManifest.Value value = parsed.entries().get("gl.profile_text");
        assertEquals(hostile, ((RunManifest.Value.Text) value).value());
        assertEquals(rendered, RunManifestWriter.render(parsed));
    }

    @Test
    void readerRejectsEmptyAndGarbageInput() {
        assertThrows(RuntimeException.class, () -> RunManifestReader.parse(""));
        assertThrows(RuntimeException.class,
            () -> RunManifestReader.parse("schema = schmaloogium.run-manifest/4\n"));
    }
}
