// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.proxy;

import com.schmaloogium.engine.diag.Diagnostics;
import com.schmaloogium.mod.core.ClientDiagnosticRouter;

/** Client side of the {@link IProxy} boundary; client-only work lands here from Phase 7. */
@net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void installSideServices() {
        Diagnostics.install(new ClientDiagnosticRouter());
    }
}
