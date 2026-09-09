// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Constraint C-4 (PHASE_1_DOC §8.2): the conformance harness depends on {@code :engine}
 * and never on {@code :mod}. Two mechanical halves, same shape as C-1: no classpath
 * entry resolves to the {@code :mod} project (published-coordinate artifact pattern or
 * {@code mod}+{@code build} path-segment pair), and no compiled {@code :conformance}
 * class references a type under {@code com.schmaloogium.mod.}.
 */
class SeamConformanceDependencyTest {

    private static final String MOD_PACKAGE_PREFIX = "com.schmaloogium.mod.";

    @Test
    void noModArtifactOnClasspaths() {
        assertNoMod("schmaloogium.test.compileClasspath", "main compileClasspath");
        assertNoMod("schmaloogium.test.runtimeClasspath", "main runtimeClasspath");
        // The test's own runtime classpath carries JUnit and ASM; :mod must be absent
        // there too.
        assertNoModEntry(System.getProperty("java.class.path"), "test runtime classpath");
    }

    @Test
    void conformanceBytecodeReferencesNoModType() throws IOException {
        String classesDirProperty = System.getProperty("schmaloogium.test.classesDir");
        assertTrue(classesDirProperty != null && !classesDirProperty.isBlank(),
                "the build did not inject schmaloogium.test.classesDir");

        List<String> offenders = new ArrayList<>();
        for (String dirName : classesDirProperty.split(File.pathSeparator)) {
            Path dir = Path.of(dirName);
            if (!Files.isDirectory(dir)) {
                continue; // no compiled classes for this source set yet: nothing to scan
            }
            try (Stream<Path> classes = Files.walk(dir)) {
                classes.filter(p -> p.toString().endsWith(".class"))
                        .forEach(p -> collect(p, offenders));
            }
        }
        assertTrue(offenders.isEmpty(),
                "C-4 violation: :conformance bytecode references :mod types: " + offenders);
    }

    private static void assertNoMod(String property, String configurationName) {
        assertTrue(property != null,
                "the build did not inject " + property);
        assertNoModEntry(System.getProperty(property), configurationName);
    }

    private static void assertNoModEntry(String classpath, String configurationName) {
        List<String> offenders = new ArrayList<>();
        for (String entry : classpath.split(File.pathSeparator)) {
            if (entry.isBlank()) {
                continue;
            }
            String fileName = new File(entry).getName().toLowerCase(Locale.ROOT);
            // :mod keeps archivesName = mod_id, so its artifacts are schmaloogium-*.jar —
            // not mod-*.jar, which can never fire. A Maven-cache path carries the same
            // file name with no mod/build segment, so both checks are needed.
            if (fileName.startsWith("schmaloogium-") && fileName.endsWith(".jar")) {
                offenders.add(entry + " (artifact pattern)");
                continue;
            }
            // Path-segment pair mod + build, using the platform separator (a literal
            // "mod/build" substring would not match a Windows classpath entry).
            String separator = File.separator;
            String doubleSep = separator + separator;
            String normalized = entry.replace(doubleSep, separator);
            boolean sawMod = false;
            for (String segment : normalized.split(separator)) {
                if ("mod".equals(segment)) {
                    sawMod = true;
                } else if (sawMod && "build".equals(segment)) {
                    offenders.add(entry + " (mod/build path segment pair)");
                    break;
                } else {
                    sawMod = false;
                }
            }
        }
        assertTrue(offenders.isEmpty(),
                "C-4 violation on " + configurationName + ": " + offenders);
    }

    private static void collect(Path classFile, List<String> offenders) {
        Set<String> names = new LinkedHashSet<>();
        try (InputStream in = Files.newInputStream(classFile)) {
            new ClassReader(in).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature,
                                  String superName, String[] interfaces) {
                    add(offenders, names, name);
                    add(offenders, names, superName);
                    if (interfaces != null) {
                        for (String i : interfaces) {
                            add(offenders, names, i);
                        }
                    }
                }

                @Override
                public org.objectweb.asm.FieldVisitor visitField(int access, String name,
                        String descriptor, String signature, Object value) {
                    addDescriptor(offenders, names, descriptor);
                    return null;
                }

                @Override
                public org.objectweb.asm.MethodVisitor visitMethod(int access, String name,
                        String descriptor, String signature, String[] exceptions) {
                    addDescriptor(offenders, names, descriptor);
                    if (exceptions != null) {
                        for (String e : exceptions) {
                            add(offenders, names, e);
                        }
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE);
        } catch (IOException e) {
            offenders.add(classFile + " -> unreadable: " + e);
        }
    }

    private static void addDescriptor(List<String> offenders, Set<String> names, String descriptor) {
        if (descriptor == null) {
            return;
        }
        if (descriptor.startsWith("(")) {
            for (Type argument : Type.getArgumentTypes(descriptor)) {
                add(offenders, names, argument.getInternalName());
            }
            add(offenders, names, Type.getReturnType(descriptor).getInternalName());
        } else {
            add(offenders, names, Type.getType(descriptor).getInternalName());
        }
    }

    private static void add(List<String> offenders, Set<String> names, String internalName) {
        if (internalName == null) {
            return;
        }
        String dotted = internalName.replace('/', '.');
        if (!names.add(dotted)) {
            return;
        }
        if (dotted.startsWith(MOD_PACKAGE_PREFIX)) {
            offenders.add(dotted);
        }
    }
}
