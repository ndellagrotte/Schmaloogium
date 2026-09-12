// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.CanonicalText;

import java.util.Map;

/**
 * Renders a {@link SceneSpec} in the §4.3.1 grammar: sections in the fixed order
 * {@code [scene] [world] [client] [pack]} then captures in file order, keys sorted inside
 * each section, every field written (defaults included), fixed-decimal doubles ([D-P2-4]).
 * parse → write → parse is identity and the output is byte-stable.
 */
public final class SceneWriter {

    private SceneWriter() {
    }

    public static String render(SceneSpec scene) {
        StringBuilder sb = new StringBuilder();
        sb.append("[scene]\n");
        line(sb, "description", scene.description());
        line(sb, "family", scene.family());
        line(sb, "id", scene.id());
        line(sb, "minMilestone", scene.minMilestone());
        line(sb, "schema", SceneSpec.SCHEMA);
        sb.append("\n[world]\n");
        SceneSpec.World w = scene.world();
        line(sb, "difficulty", w.difficulty());
        line(sb, "dimension", Long.toString(w.dimension()));
        for (int i = 0; i < w.entities().size(); i++) {
            SceneSpec.Entity e = w.entities().get(i);
            line(sb, "entity." + i + ".nbt", e.nbt());
            line(sb, "entity." + i + ".pos", e.pos());
            line(sb, "entity." + i + ".type", e.type());
        }
        line(sb, "gamemode", w.gamemode());
        for (Map.Entry<String, String> rule : w.gamerules().entrySet()) {
            line(sb, "gamerule." + rule.getKey(), rule.getValue());
        }
        line(sb, "generateStructures", CanonicalText.formatBoolean(w.generateStructures()));
        line(sb, "prepTicks", Long.toString(w.prepTicks()));
        line(sb, "seed", Long.toString(w.seed()));
        line(sb, "time", Long.toString(w.time()));
        line(sb, "weather", w.weather());
        line(sb, "weatherTicks", Long.toString(w.weatherTicks()));
        line(sb, "worldType", w.worldType());
        sb.append("\n[client]\n");
        SceneSpec.Client c = scene.client();
        line(sb, "anaglyph", CanonicalText.formatBoolean(c.anaglyph()));
        line(sb, "ao", Integer.toString(c.ao()));
        line(sb, "clouds", Integer.toString(c.clouds()));
        line(sb, "entityShadows", CanonicalText.formatBoolean(c.entityShadows()));
        line(sb, "fancyGraphics", CanonicalText.formatBoolean(c.fancyGraphics()));
        line(sb, "fov", CanonicalText.formatDouble(c.fov()));
        line(sb, "fullscreen", CanonicalText.formatBoolean(c.fullscreen()));
        line(sb, "gamma", CanonicalText.formatDouble(c.gamma()));
        line(sb, "guiScale", Integer.toString(c.guiScale()));
        line(sb, "height", Integer.toString(c.height()));
        line(sb, "hideGui", CanonicalText.formatBoolean(c.hideGui()));
        line(sb, "mipmapLevels", Integer.toString(c.mipmapLevels()));
        line(sb, "particles", Integer.toString(c.particles()));
        line(sb, "pauseOnLostFocus", CanonicalText.formatBoolean(c.pauseOnLostFocus()));
        line(sb, "renderDistance", Integer.toString(c.renderDistance()));
        line(sb, "smoothCamera", CanonicalText.formatBoolean(c.smoothCamera()));
        line(sb, "viewBobbing", CanonicalText.formatBoolean(c.viewBobbing()));
        line(sb, "vsync", CanonicalText.formatBoolean(c.vsync()));
        line(sb, "width", Integer.toString(c.width()));
        sb.append("\n[pack]\n");
        for (Map.Entry<String, String> e : scene.pack().engine().entrySet()) {
            line(sb, "engine." + e.getKey(), e.getValue());
        }
        for (Map.Entry<String, String> e : scene.pack().options().entrySet()) {
            line(sb, "option." + e.getKey(), e.getValue());
        }
        line(sb, "shaderpack", scene.pack().shaderpack());
        for (SceneSpec.Capture capture : scene.captures()) {
            if (capture instanceof SceneSpec.Shot shot) {
                sb.append("\n[shot ").append(shot.id()).append("]\n");
                line(sb, "captureFrames", Integer.toString(shot.captureFrames()));
                line(sb, "heldMain", shot.heldMain());
                line(sb, "heldOff", shot.heldOff());
                line(sb, "look", look(shot.pose()));
                line(sb, "note", shot.note());
                line(sb, "pos", pos(shot.pose()));
                line(sb, "warmupFrames", Integer.toString(shot.warmupFrames()));
            } else if (capture instanceof SceneSpec.Path path) {
                sb.append("\n[path ").append(path.id()).append("]\n");
                line(sb, "captureSampleCount", Integer.toString(path.captureSampleCount()));
                line(sb, "captureStartSample", Integer.toString(path.captureStartSample()));
                line(sb, "heldMain", path.heldMain());
                line(sb, "heldOff", path.heldOff());
                line(sb, "note", path.note());
                for (int i = 0; i < path.samples().size(); i++) {
                    line(sb, "sample." + i + ".look", look(path.samples().get(i)));
                    line(sb, "sample." + i + ".pos", pos(path.samples().get(i)));
                }
                line(sb, "samples.count", Integer.toString(path.samples().size()));
                line(sb, "warmupFrames", Integer.toString(path.warmupFrames()));
            }
        }
        return sb.toString();
    }

    private static String pos(SceneSpec.Pose p) {
        return CanonicalText.formatDouble(p.x()) + " " + CanonicalText.formatDouble(p.y()) + " "
            + CanonicalText.formatDouble(p.z());
    }

    private static String look(SceneSpec.Pose p) {
        return CanonicalText.formatDouble(p.yaw()) + " " + CanonicalText.formatDouble(p.pitch());
    }

    private static void line(StringBuilder sb, String key, String value) {
        if (value.contains("\n") || value.contains("#")) {
            throw new IllegalArgumentException("scene value for " + key
                + " may not contain a newline or '#'");
        }
        sb.append(key).append(" = ").append(value).append('\n');
    }
}
