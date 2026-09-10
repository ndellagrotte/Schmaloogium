// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

/**
 * Tri-state wire vocabulary (D-P12-10): {@code DEFAULT}/{@code ON}/{@code OFF} ↔
 * wire {@code default}/{@code true}/{@code false}. DEFAULT defers to the pack's
 * App F.1 flag; explicit true/false wins over the pack.
 */
public enum TriStateValue {
    DEFAULT, ON, OFF;

    /** The exact wire token. */
    public String wire() {
        return switch (this) {
            case DEFAULT -> "default";
            case ON -> "true";
            case OFF -> "false";
        };
    }

    /** Decodes an exact wire token; anything else is invalid by contract. */
    public static TriStateValue fromWire(String token) {
        return switch (token) {
            case "default" -> DEFAULT;
            case "true" -> ON;
            case "false" -> OFF;
            default -> throw new IllegalArgumentException("invalid tri-state wire token");
        };
    }
}
