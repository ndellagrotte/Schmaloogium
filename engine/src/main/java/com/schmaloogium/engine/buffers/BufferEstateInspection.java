// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.RegistryFingerprint;

/**
 * Handle- and generation-free pre-publication metadata view of an estate candidate
 * (PHASE_5_DOC §2.2).
 */
public interface BufferEstateInspection {

    RegistryFingerprint registryFingerprint();

    BufferSizing sizing();

    BufferInventory inventory();

    BufferResourceSnapshot.Available resources();
}
