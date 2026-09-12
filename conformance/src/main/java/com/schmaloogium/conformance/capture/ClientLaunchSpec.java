// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.wire.FlatDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The client launch command serialised by the {@code :mod:writeClientLaunchSpec} Gradle task
 * to {@code mod/build/conformance/client.launch} (schema {@code schmaloogium.client-launch/1}):
 * executable, main class, working directory, dense classpath / JVM args / program args /
 * environment, and the subject mod's classes and resources directories. {@code CaptureRunner}
 * execs exactly this command plus the conformance system properties; it never nests Gradle.
 */
public record ClientLaunchSpec(String javaExecutable, String mainClass, Path workingDir, List<String> classpath,
        List<String> jvmArgs, List<String> args, Map<String, String> environment, List<Path> subjectDirs) {

    public static final String SCHEMA_LINE = "schema = schmaloogium.client-launch/1";

    public ClientLaunchSpec {
        classpath = List.copyOf(classpath);
        jvmArgs = List.copyOf(jvmArgs);
        args = List.copyOf(args);
        environment = new TreeMap<>(environment);
        subjectDirs = List.copyOf(subjectDirs);
    }

    public static ClientLaunchSpec parse(String text) {
        FlatDocument d = FlatDocument.parse(text, SCHEMA_LINE);
        return new ClientLaunchSpec(d.text("java"), d.text("mainClass"), Path.of(d.text("workingDir")),
            list(d, "classpath"), list(d, "jvmArgs"), list(d, "args"), env(d),
            list(d, "subject").stream().map(Path::of).toList());
    }

    private static List<String> list(FlatDocument d, String family) {
        int count = d.count(family);
        List<String> out = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            out.add(d.text(family + "." + i));
        }
        return out;
    }

    private static Map<String, String> env(FlatDocument d) {
        int count = d.count("env");
        Map<String, String> out = new TreeMap<>();
        for (int i = 0; i < count; i++) {
            out.put(d.text("env." + i + ".name"), d.text("env." + i + ".value"));
        }
        return out;
    }

    /** The full command line with extra JVM system properties inserted before the main class. */
    public List<String> command(List<String> extraJvmArgs) {
        List<String> cmd = new ArrayList<>();
        cmd.add(javaExecutable);
        cmd.addAll(jvmArgs);
        cmd.addAll(extraJvmArgs);
        cmd.add("-cp");
        cmd.add(String.join(java.io.File.pathSeparator, classpath));
        cmd.add(mainClass);
        cmd.addAll(args);
        return cmd;
    }
}
