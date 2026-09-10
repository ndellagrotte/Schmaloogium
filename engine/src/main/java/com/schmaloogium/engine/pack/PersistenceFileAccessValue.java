// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;
import java.util.Optional;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;

/** Package-private same-pack persistence access bound to one acquisition. */
final class PersistenceFileAccessValue implements PersistenceFileAccess {

    private final java.nio.file.Path shaderpacksRoot;
    private final java.nio.file.Path gameRoot;

    PersistenceFileAccessValue(java.nio.file.Path shaderpacksRoot, java.nio.file.Path gameRoot) {
        this.shaderpacksRoot = java.util.Objects.requireNonNull(shaderpacksRoot, "shaderpacksRoot");
        this.gameRoot = java.util.Objects.requireNonNull(gameRoot, "gameRoot");
    }

    @Override
    public PersistenceReadSource read(PersistenceTarget target) {
        java.util.Objects.requireNonNull(target, "target");
        java.nio.file.Path file;
        try {
            file = fileFor(target);
        } catch (TargetFailure failure) {
            return new PersistenceReadSource.Failed(failure.failure);
        }
        if (!java.nio.file.Files.isRegularFile(file)) {
            return new PersistenceReadSource.Absent();
        }
        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(file);
            if (!isValidUtf8PropertiesText(bytes)) {
                return new PersistenceReadSource.Failed(new PersistenceFailure(
                    PersistenceFailureCode.INVALID_ENCODING,
                    "non-UTF-8 persistence file " + file));
            }
            return new PersistenceReadSource.Present(ImmutableBytes.of(bytes));
        } catch (java.io.IOException e) {
            return new PersistenceReadSource.Failed(new PersistenceFailure(
                PersistenceFailureCode.UNREADABLE, e.toString()));
        }
    }

    @Override
    public PersistenceWriteReceipt writeAtomically(PersistenceTarget target, ImmutableBytes content) {
        java.util.Objects.requireNonNull(target, "target");
        java.util.Objects.requireNonNull(content, "content");
        java.nio.file.Path file;
        try {
            file = fileFor(target);
        } catch (TargetFailure failure) {
            return new PersistenceWriteReceipt(PersistenceWriteStatus.FAILED,
                Optional.of(failure.failure), false);
        }
        try {
            java.nio.file.Files.createDirectories(file.getParent());
            java.nio.file.Path tmp = java.nio.file.Files.createTempFile(file.getParent(),
                file.getFileName().toString(), ".tmp");
            java.nio.file.Files.write(tmp, content.copy());
            try {
                java.nio.file.Files.move(tmp, file,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
                return new PersistenceWriteReceipt(PersistenceWriteStatus.COMMITTED,
                    Optional.empty(), true);
            } catch (AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(tmp, file,
                    StandardCopyOption.REPLACE_EXISTING);
                return new PersistenceWriteReceipt(PersistenceWriteStatus.COMMITTED,
                    Optional.empty(), false);
            }
        } catch (java.io.IOException e) {
            return new PersistenceWriteReceipt(PersistenceWriteStatus.FAILED,
                Optional.of(new PersistenceFailure(
                    PersistenceFailureCode.WRITE_FAILED, e.toString())), false);
        }
    }

    /** Checked carrier so the record-based failure never has to be a Throwable. */
    private static final class TargetFailure extends Exception {
        final PersistenceFailure failure;

        TargetFailure(PersistenceFailure failure) {
            super(failure.detail());
            this.failure = failure;
        }
    }

    /** Global options live next to the game directory; pack options in the pack folder. */
    private java.nio.file.Path fileFor(PersistenceTarget target) throws TargetFailure {
        if (target instanceof GlobalOptionsTarget) {
            return gameRoot.resolve("optionsshaders.txt");
        }
        if (target instanceof PackOptionsTargetValue pack) {
            // target authentication: same-pack issuance verified by credential; the issued
            // file name is the exact direct-child host name plus .txt (section 4.8.1)
            String hostName = FilesystemCandidateReferences.decode(pack.reference());
            if (!pack.fileName().equals(hostName + ".txt")) {
                throw new TargetFailure(new PersistenceFailure(PersistenceFailureCode.UNSAFE_TARGET,
                    "pack target file name does not match its reference"));
            }
            try {
                return shaderpacksRoot.resolve(sanitized(pack.fileName()));
            } catch (IllegalArgumentException unsafe) {
                throw new TargetFailure(new PersistenceFailure(
                    PersistenceFailureCode.UNSAFE_TARGET, unsafe.getMessage()));
            }
        }
        throw new TargetFailure(new PersistenceFailure(PersistenceFailureCode.INVALID_REQUEST,
            "unknown persistence target"));
    }

    private static String sanitized(String fileName) {
        if (fileName.isEmpty() || fileName.equals(".") || fileName.equals("..")
                || fileName.indexOf('/') >= 0 || fileName.indexOf('\\') >= 0
                || fileName.indexOf('\u0000') >= 0) {
            throw new IllegalArgumentException("unsafe persistence file name");
        }
        return fileName;
    }

    private static boolean isValidUtf8PropertiesText(byte[] bytes) {
        try {
            var decoder = java.nio.charset.StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
                .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT);
            decoder.decode(java.nio.ByteBuffer.wrap(bytes));
            return true;
        } catch (java.nio.charset.CharacterCodingException e) {
            return false;
        }
    }
}
