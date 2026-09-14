// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.id.IdScopeAdmission;

import org.junit.jupiter.api.Test;

/** One live admission; closing or opening over it revokes it. */
class IdAdmissionGateTest {

    @Test
    void openCloseAndOverlapRevokeExactly() {
        IdAdmissionGate.closeAll();
        IdScopeAdmission main = IdAdmissionGate.openMain(3, 100);
        assertTrue(main.active());
        assertFalse(main.shadow());
        assertEquals(3, main.generation());
        assertEquals(100, main.frameId());
        assertEquals(Thread.currentThread(), main.ownerThread());
        assertEquals(main, IdAdmissionGate.current());

        IdScopeAdmission shadow = IdAdmissionGate.openShadow(3, 100);
        assertFalse(main.active(), "opening over a live admission revokes it");
        assertTrue(shadow.shadow());
        IdAdmissionGate.close(main); // closing a stale one leaves the live one alone
        assertEquals(shadow, IdAdmissionGate.current());
        IdAdmissionGate.close(shadow);
        assertFalse(shadow.active());
        assertNull(IdAdmissionGate.current());
    }
}
