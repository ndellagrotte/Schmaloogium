// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui;

import com.schmaloogium.mod.gui.model.ReloadRequest;
import com.schmaloogium.mod.gui.model.ReloadTrigger;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.input.Keyboard;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * The F3+R chord observation and the rebindable open-screen key binding
 * (PHASE_12_DOC §4.8.1). 1.12.2's {@link KeyBinding} system cannot express a chord,
 * so the chord is <em>observed</em> on {@code InputEvent.KeyInputEvent} while the
 * separate single-key binding is only the screen convenience and is not part of the
 * contract.
 *
 * <p>Gating [D-P12-13]: the chord activates only while the shader engine is in a
 * state where a reload is meaningful; when the gate says no, the handler does
 * nothing and never shadows the key. The gate itself is the pure static
 * {@link #chordActivated}, so the decision stays headless-testable.
 */
@SideOnly(Side.CLIENT)
public final class ShaderPackKeyBindings {

    /** The translation key of the convenience open-screen binding. */
    static final String OPEN_SCREEN_KEY = "key.schmaloogium.openShaderPacks";

    /** The key binding category, so users find the binding under one group. */
    static final String CATEGORY = "key.categories.schmaloogium";

    private final BooleanSupplier engineActive;
    private final Consumer<ReloadRequest> submitter;
    private final Runnable chatAcknowledgement;

    /** The stored convenience binding; {@code null} until {@link #register()}. */
    private KeyBinding openScreenBinding;

    /** F3 hold state, tracked from key events (polling is unavailable; see onKeyInput). */
    private boolean f3Held;

    /**
     * @param engineActive        whether a reload is currently meaningful (gate, [D-P12-13])
     * @param submitter           receives the classified {@link ReloadTrigger#F3R_KEYBIND} request
     * @param chatAcknowledgement the one-line chat/log acknowledgement on activation
     */
    public ShaderPackKeyBindings(BooleanSupplier engineActive,
                                 Consumer<ReloadRequest> submitter,
                                 Runnable chatAcknowledgement) {
        this.engineActive = engineActive;
        this.submitter = submitter;
        this.chatAcknowledgement = chatAcknowledgement;
    }

    /** Registers the convenience binding and this class on the FML input event bus. */
    public void register() {
        openScreenBinding =
                new KeyBinding(OPEN_SCREEN_KEY, Keyboard.KEY_P, CATEGORY);
        ClientRegistry.registerKeyBinding(openScreenBinding);
        // InputEvent.KeyInputEvent fires on the FML bus in 1.12.2; MinecraftForge.EVENT_BUS
        // carries the other (non-input) events and must not get a second registration.
        FMLCommonHandler.instance().bus().register(this);
    }

    /** The stored open-screen binding, for the assembly to poll on client ticks. */
    public KeyBinding openScreenBinding() {
        return openScreenBinding;
    }

    /**
     * The chord gate lives MC-free in {@link ReloadTrigger#chordActivated}; this
     * delegate keeps existing call sites stable.
     */
    public static boolean chordActivated(boolean engineActive, boolean anyScreenOpen,
                                         boolean f3Down, boolean rDown, boolean rWasDown) {
        return ReloadTrigger.chordActivated(engineActive, anyScreenOpen, f3Down,
                rDown, rWasDown);
    }

    /**
     * Observes one key event. The 1.12.2 event carries no key payload, so the current
     * event is read from {@link Keyboard}: the event fires per {@code Keyboard.next()}
     * dispatch and only while no {@code GuiScreen} handles the input, hence
     * {@code anyScreenOpen} is {@code false} at this seam. R transitioning to pressed
     * is the fresh press; every other event while R is held reports {@code rWasDown}.
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Event-state observation only: LWJGLX's Keyboard.isKeyDown polls the GLFW
        // window handle, which Cleanroom's input path never installs — every poll
        // throws NPE and would crash the client mid-frame (§G2.4 forbids that). The
        // queued-event accessors carry the same information without touching GL/GLFW.
        int eventKey = Keyboard.getEventKey();
        boolean eventState = Keyboard.getEventKeyState();
        if (eventKey == Keyboard.KEY_F3) {
            f3Held = eventState;
            return;
        }
        if (eventKey != Keyboard.KEY_R || !eventState) {
            return;
        }
        // A pressed R is the rising edge by construction: rWasDown is false.
        boolean screenOpen = net.minecraft.client.Minecraft.getMinecraft().currentScreen != null;
        if (!chordActivated(engineActive.getAsBoolean(), screenOpen, f3Held, true, false)) {
            return;
        }
        submitter.accept(ReloadTrigger.F3R_KEYBIND.request());
        chatAcknowledgement.run();
    }
}
