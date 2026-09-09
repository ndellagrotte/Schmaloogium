// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Closed per-pack option persistence codec. */
public interface OptionPersistenceCodec {

    OptionPersistenceReadResult read(OptionPersistenceReadRequest request);

    OptionPersistenceWriteResult write(OptionPersistenceWriteRequest request);
}
