// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Acquires the bounded authentication lifetime over the configured persistence roots. */
final class PersistenceFileAccessProvider {

    private PersistenceFileAccessProvider() {
    }

    static PersistenceFileAccessAcquisition acquire(
            PackFrontEndServices services, PersistenceRootConfiguration roots) {
        if (services == null || roots == null) {
            return new PersistenceFileAccessAcquisition.InvalidRoots(new PersistenceFailure(
                PersistenceFailureCode.INVALID_REQUEST, "null roots"));
        }
        if (!java.nio.file.Files.isDirectory(roots.shaderpacksDirectory())
                || !java.nio.file.Files.isDirectory(roots.gameDirectory())) {
            return new PersistenceFileAccessAcquisition.InvalidRoots(new PersistenceFailure(
                PersistenceFailureCode.UNSAFE_TARGET, "persistence roots are not directories"));
        }
        return new PersistenceFileAccessAcquisition.Acquired(new PersistenceFileAccessValue(
            roots.shaderpacksDirectory().toAbsolutePath().normalize(),
            roots.gameDirectory().toAbsolutePath().normalize()));
    }
}
