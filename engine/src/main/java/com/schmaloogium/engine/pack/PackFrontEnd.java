// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** The Phase 3 front end; one bundle issues every token it authenticates. */
public interface PackFrontEnd {

    int CURRENT_SCHEMA_VERSION = 23;

    DiscoveryLimits discoveryLimits();

    PackDiscoveryResult discover(PackDiscoveryRequest request);

    FilesystemCandidateResolution resolveFilesystemCandidate(
        FilesystemCandidateReference reference, PackDiscoveryResult current);

    PackOptionsTargetAcquisition packOptionsTarget(PackCandidateId candidate);

    PackLoadResult load(PackLoadRequest request);

    PackInspectionResult inspect(PackLoadRequest request);
}
