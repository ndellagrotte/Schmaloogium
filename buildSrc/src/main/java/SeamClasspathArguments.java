// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFiles;
import org.gradle.process.CommandLineArgumentProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Hands the seam architecture tests the exact classpaths they must assert over, lazily
 * (PHASE_1_DOC §4.2.3). Resolution is deferred to execution time; the annotated inputs
 * keep consuming test tasks' up-to-date checking honest.
 *
 * Lives in the default package on purpose: the three consuming build scripts print
 * {@code new SeamClasspathArguments(...)} with no import (§12 item 4b).
 */
public final class SeamClasspathArguments implements CommandLineArgumentProvider {

    private final FileCollection compileClasspath;
    private final FileCollection runtimeClasspath;
    private final FileCollection classesDirs;

    public SeamClasspathArguments(FileCollection compileClasspath,
                                  FileCollection runtimeClasspath,
                                  FileCollection classesDirs) {
        this.compileClasspath = compileClasspath;
        this.runtimeClasspath = runtimeClasspath;
        this.classesDirs = classesDirs;
    }

    @Classpath
    public FileCollection getCompileClasspath() {
        return compileClasspath;
    }

    @Classpath
    public FileCollection getRuntimeClasspath() {
        return runtimeClasspath;
    }
    @org.gradle.api.tasks.Classpath
    public FileCollection getClassesDirs() {
        return classesDirs;
    }

    @Override
    public Iterable<String> asArguments() {
        List<String> args = new ArrayList<>(3);
        args.add("-Dschmaloogium.test.compileClasspath=" + join(compileClasspath));
        args.add("-Dschmaloogium.test.runtimeClasspath=" + join(runtimeClasspath));
        args.add("-Dschmaloogium.test.classesDir=" + join(classesDirs));
        return args;
    }

    private static String join(FileCollection collection) {
        StringBuilder sb = new StringBuilder();
        for (File f : collection) {
            sb.append(f.getAbsolutePath());
        }
        return sb.toString();
    }
}
