// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Opaque same-bundle internal-session option snapshot (D-P3-65); nonserializable, identity-equal. */
public sealed interface InternalOptionSnapshot permits InternalOptionSnapshotValue {
}
