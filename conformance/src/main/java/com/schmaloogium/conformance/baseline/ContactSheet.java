// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.baseline;

import com.schmaloogium.conformance.capture.RunManifest;
import com.schmaloogium.conformance.capture.RunManifestWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * §4.7.3 step 1: an HTML index of every captured image at review size, grouped by capture
 * and ordinal, with the run manifest inline. The reviewer reads the images directly.
 */
public final class ContactSheet {

    private ContactSheet() {
    }

    public static Path write(Path runDir, RunManifest manifest) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><meta charset=\"utf-8\"><title>").append(esc(manifest.token("run.id")))
            .append(" · ").append(esc(manifest.text("pack.id"))).append("@")
            .append(esc(manifest.text("pack.version"))).append(" · ").append(esc(manifest.token("run.sceneId")))
            .append("</title><style>body{font-family:sans-serif;margin:16px}figure{display:inline-block;"
                + "margin:8px}img{max-width:427px;border:1px solid #888}figcaption{font-size:12px}"
                + "pre{font-size:11px;background:#f4f4f4;padding:8px;overflow:auto}</style>");
        html.append("<h1>").append(esc(manifest.token("run.id"))).append(" — ")
            .append(esc(manifest.text("pack.id"))).append('@').append(esc(manifest.text("pack.version")))
            .append(" — ").append(esc(manifest.token("run.sceneId"))).append("</h1>");
        html.append("<p>exit ").append(esc(manifest.token("run.exitStatus")))
            .append(" · images ").append(manifest.familyCount("images")).append("</p>");
        String lastGroup = null;
        for (RunManifest.Row image : manifest.family("images")) {
            String group = image.token("captureKind") + " " + image.text("captureId");
            if (!group.equals(lastGroup)) {
                if (lastGroup != null) {
                    html.append("</section>");
                }
                html.append("<section><h2>").append(esc(group)).append("</h2>");
                lastGroup = group;
            }
            html.append("<figure><img src=\"").append(esc(image.text("path"))).append("\" alt=\"")
                .append(esc(group)).append(" #").append(image.integer("sampleOrdinal"))
                .append("\"><figcaption>ordinal ").append(image.integer("sampleOrdinal"))
                .append(" · ").append(image.integer("width")).append('x').append(image.integer("height"))
                .append(" · ").append(esc(image.token("pixelSha256").substring(0, 16))).append("…</figcaption></figure>");
        }
        if (lastGroup != null) {
            html.append("</section>");
        }
        html.append("<h2>manifest</h2><pre>").append(esc(RunManifestWriter.render(manifest))).append("</pre>");
        Path sheet = runDir.resolve("contact-sheet.html");
        Files.writeString(sheet, html.toString(), StandardCharsets.UTF_8);
        return sheet;
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
