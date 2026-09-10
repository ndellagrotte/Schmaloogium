// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The reload entry surface (PHASE_7_DOC §5.1). Requests may be queued from any thread but
 * cross as immutable values and commit only on the render thread; status polling is
 * mutation-free everywhere.
 */
public interface ShaderReloadController {

    ReloadResult request(DriverReloadRequest request);

    ReloadStatus status(ReloadToken token);
}
