// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

public sealed interface PackOptionsTarget extends PersistenceTarget permits PackOptionsTargetValue {

    String fileName();

    FilesystemCandidateReference reference();

    /** Hidden authentication: true iff this target was issued over that bundle domain. */
    boolean isIssuedBy(Object domain);
}
