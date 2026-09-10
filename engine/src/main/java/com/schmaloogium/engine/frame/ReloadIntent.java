// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.pack.PackSelection;

/**
 * What a reload should do to the current publication (PHASE_7_DOC §5.1): install an exact
 * selection, or rebuild the currently-active identity (remap-class requests). A newer
 * intent supersedes an older one — effects never accumulate.
 */
public sealed interface ReloadIntent {

    /** Load exactly this selection (FULL discover + load path; Off publishes shaders-off). */
    record Select(PackSelection selection) implements ReloadIntent {

        public Select {
            java.util.Objects.requireNonNull(selection, "selection");
        }
    }

    /** Rebuild the active publication, expected to still carry this identity. */
    record RebuildActive(PipelineIdentity expectedActive) implements ReloadIntent {

        public RebuildActive {
            java.util.Objects.requireNonNull(expectedActive, "expectedActive");
        }
    }
}
