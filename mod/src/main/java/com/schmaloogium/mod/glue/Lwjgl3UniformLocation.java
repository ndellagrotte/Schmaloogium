// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.UniformLocation;

/**
 * The LWJGL3 backend's uniform lookup result (PHASE_1_DOC §4.7.3, [D-P1-34]): retains
 * the name passed to {@code locate(program, name)} — the backend obligation that gives
 * §G2.4 rung 2's attributed replay something to name — plus the owning program, so an
 * upload can verify the program-selection barrier without any GL query.
 */
final class Lwjgl3UniformLocation implements UniformLocation {

    final Lwjgl3GLDevice owner;
    final Lwjgl3ProgramHandle program;
    final String name;
    private final boolean absent;
    private final int location;

    private Lwjgl3UniformLocation(Lwjgl3GLDevice owner, Lwjgl3ProgramHandle program,
                                  String name, boolean absent, int location) {
        this.owner = owner;
        this.program = program;
        this.name = name;
        this.absent = absent;
        this.location = location;
    }

    static Lwjgl3UniformLocation present(Lwjgl3GLDevice owner, Lwjgl3ProgramHandle program,
                                         String name, int location) {
        return new Lwjgl3UniformLocation(owner, program, name, false, location);
    }

    static Lwjgl3UniformLocation absent(Lwjgl3GLDevice owner, Lwjgl3ProgramHandle program, String name) {
        return new Lwjgl3UniformLocation(owner, program, name, true, -1);
    }

    @Override
    public boolean isAbsent() {
        return absent;
    }

    Lwjgl3ProgramHandle programHandle() {
        return program;
    }

    int glLocation() {
        return location;
    }
}
