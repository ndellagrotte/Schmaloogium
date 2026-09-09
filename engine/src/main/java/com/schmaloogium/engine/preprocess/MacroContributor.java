// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.pack.PackConfiguration;

public interface MacroContributor {

    MacroContribution contribute(PackConfiguration configuration);
}
