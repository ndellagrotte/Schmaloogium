// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.PackIdentity;

/** Phase 7's internal source provider; provider defects are reduced to a load failure. */
public interface InternalPackSource {

    PackIdentity identity();

    InternalPackSnapshot snapshot(PackInputLimits limits) throws InternalPackReadException;
}
