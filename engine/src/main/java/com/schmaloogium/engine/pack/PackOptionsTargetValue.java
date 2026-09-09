// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

final class PackOptionsTargetValue implements PackOptionsTarget {

    private final FilesystemCandidateReference reference;
    private final String fileName;
    private final Object domain;
    private final PackCandidateIdToken credential;

    PackOptionsTargetValue(FilesystemCandidateReference reference, String fileName,
        Object domain, PackCandidateIdToken credential) {
        this.reference = java.util.Objects.requireNonNull(reference, "reference");
        this.fileName = java.util.Objects.requireNonNull(fileName, "fileName");
        this.domain = domain;
        this.credential = java.util.Objects.requireNonNull(credential, "credential");
    }

    Object domain() {
        return domain;
    }

    PackCandidateIdToken credential() {
        return credential;
    }

    @Override
    public String fileName() {
        return fileName;
    }

    @Override
    public FilesystemCandidateReference reference() {
        return reference;
    }

    @Override
    public boolean isIssuedBy(Object domain) {
        return this.domain == domain;
    }

    @Override
    public String toString() {
        return "PackOptionsTarget[" + fileName + "]";
    }
}
