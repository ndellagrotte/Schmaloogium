// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferFailureCode;
import com.schmaloogium.engine.buffers.ShadowEstateAvailable;
import com.schmaloogium.engine.buffers.ShadowEstateNotRequested;
import com.schmaloogium.engine.buffers.ShadowEstateResult;
import com.schmaloogium.engine.buffers.ShadowEstateUnavailable;

/**
 * The {@code BufferEstateView#shadow()} collaborator (PHASE_5_DOC §4.10): reports the
 * P5-owned shadow estate per the declarative plan. The estate is planned exactly when at
 * least one shadow depth or shadow color buffer is planned; a planned-but-unoperable estate
 * reports {@code Unavailable} fail-closed with the first recorded failure.
 */
public final class ShadowOperator {

    ShadowEstateResult shadow(EstateCore core) {
        if (core.shadowPlannedDepthCount() == 0 && core.shadowPlannedColorCount() == 0) {
            return new ShadowEstateNotRequested(core.generation);
        }
        if (core.shadowFailure != null) {
            return new ShadowEstateUnavailable(core.shadowFailure, core.generation);
        }
        if (core.shadowView == null) {
            return new ShadowEstateUnavailable(core.failure(
                BufferFailureCode.CAPABILITY_LIMIT,
                "schmaloogium.buffers.error.shadow.unavailable"), core.generation);
        }
        return new ShadowEstateAvailable(core.shadowView);
    }
}
