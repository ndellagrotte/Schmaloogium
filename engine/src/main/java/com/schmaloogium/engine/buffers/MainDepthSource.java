// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Engine-side main-depth SPI implemented by mod.glue. {@link #prepare} is a safe-point call that
 * may resize the platform FBO; {@link #current()} is render-thread and never null.
 */
public interface MainDepthSource {
    MainDepthPreparation prepare(Extent2i requiredExtent);

    MainDepthSnapshot current();
}
