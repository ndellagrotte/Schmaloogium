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
 * Constraint C-3 (PHASE_1_DOC §4.3, the mechanical half of §G4.6): no {@code org.lwjgl*}
 * reference outside {@code com.schmaloogium.mod.glue} and its subpackages — LWJGL is
 * confined to the glue layer that implements the facade against the real GL.
 */
class SeamLwjglConfinementTest {

    private static final String GLUE_PREFIX = "com.schmaloogium.mod.glue";

    @Test
    void lwjglIsConfinedToGlue() throws IOException {
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
                "C-3 violation: org.lwjgl* referenced outside mod.glue: " + offenders);
    }

    private static void collect(Path classFile, List<String> offenders) {
        // The referencing class, from the ClassReader's own name (filenames lose packages
        // and the $-nested shape). An org.lwjgl name offends only when its REFERENCING
        // class lives outside mod.glue.
        String[] ownClass = {null};
        Set<String> names = new LinkedHashSet<>();
        try (InputStream in = Files.newInputStream(classFile)) {
            new ClassReader(in).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature,
                                  String superName, String[] interfaces) {
                    ownClass[0] = name.replace('/', '.');
                    add(offenders, names, ownClass[0], name);
                    add(offenders, names, ownClass[0], superName);
                    if (interfaces != null) {
                        for (String i : interfaces) {
                            add(offenders, names, ownClass[0], i);
                        }
                    }
                }

                @Override
                public org.objectweb.asm.FieldVisitor visitField(int access, String name,
                        String descriptor, String signature, Object value) {
                    addDescriptor(offenders, names, ownClass[0], descriptor);
                    return null;
                }

                @Override
                public org.objectweb.asm.MethodVisitor visitMethod(int access, String name,
                        String descriptor, String signature, String[] exceptions) {
                    addDescriptor(offenders, names, ownClass[0], descriptor);
                    if (exceptions != null) {
                        for (String e : exceptions) {
                            add(offenders, names, ownClass[0], e);
                        }
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE);
        } catch (IOException e) {
            offenders.add(classFile + " -> unreadable: " + e);
        }
    }

    private static void addDescriptor(List<String> offenders, Set<String> names,
                                      String ownClass, String descriptor) {
        if (descriptor == null) {
            return;
        }
        if (descriptor.startsWith("(")) {
            for (Type argument : Type.getArgumentTypes(descriptor)) {
                add(offenders, names, ownClass, argument.getInternalName());
            }
            add(offenders, names, ownClass, Type.getReturnType(descriptor).getInternalName());
        } else {
            add(offenders, names, ownClass, Type.getType(descriptor).getInternalName());
        }
    }

    private static void add(List<String> offenders, Set<String> names,
                            String ownClass, String internalName) {
        if (internalName == null || ownClass == null) {
            return;
        }
        String dotted = internalName.replace('/', '.');
        if (!names.add(dotted)) {
            return;
        }
        if (dotted.startsWith("org.lwjgl") && !ownClass.startsWith(GLUE_PREFIX)) {
            offenders.add(ownClass + " -> " + dotted);
        }
    }
}
