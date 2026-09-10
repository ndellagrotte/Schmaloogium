// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

/** {@code LOG_ONLY} detail (dependency paths, raw span detail) never reaches chat (§4.9). */
public enum DiagnosticChannel { CHAT_AND_LOG, LOG_ONLY }
