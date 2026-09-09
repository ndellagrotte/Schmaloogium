// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod;

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
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Constraint C-2 (PHASE_1_DOC §4.3): no {@code :mod} class references
 * {@code com.schmaloogium.engine.*.internal.*} — engine internals are invisible across
 * the seam. Same constant-pool walk as {@code SeamBytecodeTest}, over :mod's classes.
 */
class SeamInternalsTest {

    private static final String INTERNAL_PATTERN = ".internal.";

    @Test
    void modBytecodeReferencesNoEngineInternalType() throws IOException {
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
                "C-2 violation: :mod bytecode references engine internals: " + offenders);
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
        // engine.*.internal.* — one package segment named "internal" under the engine root
        if (dotted.startsWith("com.schmaloogium.engine.") && dotted.contains(INTERNAL_PATTERN)) {
            offenders.add(dotted);
        }
    }
}
