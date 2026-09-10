// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackIdentity;

import java.util.Objects;

/**
 * The identity of one active pipeline publication (PHASE_7_DOC §5.1): the selected pack,
 * the dimension it was prepared for, and the configuration fingerprint it compiled from.
 */
public record PipelineIdentity(
        PackIdentity pack,
        DimensionKey dimension,
        ConfigurationFingerprint configuration) {

    public PipelineIdentity {
        Objects.requireNonNull(pack, "pack");
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(configuration, "configuration");
    }
}
