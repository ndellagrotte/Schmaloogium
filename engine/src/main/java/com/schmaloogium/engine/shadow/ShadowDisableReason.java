// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * Why the shadow feature is disabled (PHASE_8_DOC §2.2). Stable field/hook identity, never a
 * clamped value; the diagnostic id pairs with it for the {@code schmaloogium.shadow} channel.
 */
public sealed interface ShadowDisableReason {

    record HookUnavailable(String hookId) implements ShadowDisableReason {

        public HookUnavailable {
            java.util.Objects.requireNonNull(hookId, "hookId");
        }
    }

    record InvalidPolicy(String field) implements ShadowDisableReason {

        public InvalidPolicy {
            java.util.Objects.requireNonNull(field, "field");
        }
    }

    record EstateUnavailable(com.schmaloogium.engine.buffers.BufferFailure failure)
            implements ShadowDisableReason {

        public EstateUnavailable {
            java.util.Objects.requireNonNull(failure, "failure");
        }
    }

    record RuntimeFailure(String operation) implements ShadowDisableReason {

        public RuntimeFailure {
            java.util.Objects.requireNonNull(operation, "operation");
        }
    }
}
