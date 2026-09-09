// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

/**
 * Backend-private view shared by every concrete handle the LWJGL3 backend issues. It is
 * the authentication surface ([D-P1-40]): the concrete package-private class plus the
 * owning-device identity prove a handle is this backend's, live, and same-device —
 * the public marker alone proves nothing.
 */
interface Lwjgl3Handle {

    Lwjgl3GLDevice owner();

    boolean deleted();

    String subjectLabel();
}
