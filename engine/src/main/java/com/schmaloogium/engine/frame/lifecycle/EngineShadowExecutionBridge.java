// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.ShadowBaseBindingReceiver;
import com.schmaloogium.engine.frame.ShadowExecutionBridge;
import com.schmaloogium.engine.frame.ShadowExecutionCloseResult;
import com.schmaloogium.engine.frame.ShadowExecutionCloseRejection;
import com.schmaloogium.engine.frame.ShadowExecutionIdentity;
import com.schmaloogium.engine.frame.ShadowExecutionOpenResult;
import com.schmaloogium.engine.frame.ShadowExecutionOpenRejection;
import com.schmaloogium.engine.frame.ShadowExecutionValidationResult;
import com.schmaloogium.engine.frame.ShadowExecutionValidationRejection;
import com.schmaloogium.engine.frame.ShadowExecutionView;
import com.schmaloogium.engine.frame.ShadowSlotEpoch;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.frame.spi.SignalResult;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

/**
 * The driver-owned shadow execution bridge (PHASE_7_DOC §5.1). Exactly one non-nestable
 * dynamic-extent view; WRONG_THREAD before ALREADY_ACTIVE on open; the exact
 * WRONG_ISSUER → INACTIVE → WRONG_EXECUTION → STALE_SLOT_EPOCH → WRONG_THREAD order on
 * validate; close checks WRONG_ISSUER, INACTIVE, WRONG_EXECUTION and invalidates the view
 * before returning in every branch.
 */
public final class EngineShadowExecutionBridge implements ShadowExecutionBridge {

    private final BooleanSupplier renderThread;

    private final AtomicReference<Live> live = new AtomicReference<>();

    private static final class Live {
        final ShadowExecutionIdentity identity;
        final ShadowSlotEpoch epoch;
        final ViewImpl view;

        Live(ShadowExecutionIdentity identity, ShadowSlotEpoch epoch, ViewImpl view) {
            this.identity = identity;
            this.epoch = epoch;
            this.view = view;
        }
    }

    private static final class ViewImpl implements ShadowExecutionView {
        final ShadowExecutionIdentity opener;
        final ShadowSlotEpoch epoch;
        volatile boolean invalidated;
        volatile ShadowBaseBindingReceiver receiver;
        volatile AtlasBindingEvidence currentBaseBinding;

        ViewImpl(ShadowExecutionIdentity opener, ShadowSlotEpoch epoch) {
            this.opener = opener;
            this.epoch = epoch;
        }

        @Override
        public AtlasBindingEvidence currentBaseBinding() {
            AtlasBindingEvidence evidence = this.currentBaseBinding;
            if (invalidated || evidence == null) {
                throw new IllegalStateException(
                        "no valid live shadow execution base binding to lend");
            }
            return evidence;
        }

        @Override
        public SignalResult installBaseBindingReceiver(ShadowBaseBindingReceiver receiver) {
            Objects.requireNonNull(receiver, "receiver");
            if (invalidated) {
                return new SignalResult.Rejected(com.schmaloogium.engine.frame.HookRejection.WRONG_TOKEN);
            }
            if (this.receiver != null) {
                return new SignalResult.Failed(new FailureId(
                        "schmaloogium.frame.shadow.receiver-duplicate"));
            }
            this.receiver = receiver;
            return new SignalResult.Accepted();
        }
    }

    public EngineShadowExecutionBridge(BooleanSupplier renderThread) {
        this.renderThread = Objects.requireNonNull(renderThread, "renderThread");
    }

    @Override
    public ShadowExecutionOpenResult open(
            ShadowExecutionIdentity activeExecutionIdentity, ShadowSlotEpoch slotEpoch) {
        Objects.requireNonNull(activeExecutionIdentity, "activeExecutionIdentity");
        Objects.requireNonNull(slotEpoch, "slotEpoch");
        if (!renderThread.getAsBoolean()) {
            return new ShadowExecutionOpenResult.Rejected(ShadowExecutionOpenRejection.WRONG_THREAD);
        }
        if (live.get() != null) {
            return new ShadowExecutionOpenResult.Rejected(ShadowExecutionOpenRejection.ALREADY_ACTIVE);
        }
        ViewImpl view = new ViewImpl(activeExecutionIdentity, slotEpoch);
        live.set(new Live(activeExecutionIdentity, slotEpoch, view));
        return new ShadowExecutionOpenResult.Opened(view);
    }

    @Override
    public ShadowExecutionValidationResult validate(
            ShadowExecutionView view,
            ShadowExecutionIdentity activeExecutionIdentity,
            ShadowSlotEpoch slotEpoch) {
        Objects.requireNonNull(activeExecutionIdentity, "activeExecutionIdentity");
        Objects.requireNonNull(slotEpoch, "slotEpoch");
        Live current = live.get();
        if (current != null && current.identity != activeExecutionIdentity) {
            return new ShadowExecutionValidationResult.Rejected(
                    ShadowExecutionValidationRejection.WRONG_ISSUER);
        }
        if (view == null || !(view instanceof ViewImpl impl) || impl.invalidated) {
            return new ShadowExecutionValidationResult.Rejected(
                    ShadowExecutionValidationRejection.INACTIVE);
        }
        if (impl != current.view) {
            return new ShadowExecutionValidationResult.Rejected(
                    ShadowExecutionValidationRejection.WRONG_EXECUTION);
        }
        if (impl.epoch != slotEpoch) {
            return new ShadowExecutionValidationResult.Rejected(
                    ShadowExecutionValidationRejection.STALE_SLOT_EPOCH);
        }
        if (!renderThread.getAsBoolean()) {
            return new ShadowExecutionValidationResult.Rejected(
                    ShadowExecutionValidationRejection.WRONG_THREAD);
        }
        return new ShadowExecutionValidationResult.Valid();
    }

    @Override
    public ShadowExecutionCloseResult close(ShadowExecutionView view) {
        Live current = live.get();
        if (current == null) {
            if (view instanceof ViewImpl impl) {
                impl.invalidated = true;
            }
            return new ShadowExecutionCloseResult.Rejected(ShadowExecutionCloseRejection.INACTIVE);
        }
        if (view == null || !(view instanceof ViewImpl impl)) {
            return new ShadowExecutionCloseResult.Rejected(ShadowExecutionCloseRejection.WRONG_ISSUER);
        }
        if (current == null || impl.invalidated) {
            impl.invalidated = true;
            return new ShadowExecutionCloseResult.Rejected(ShadowExecutionCloseRejection.INACTIVE);
        }
        if (impl != current.view) {
            impl.invalidated = true;
            return new ShadowExecutionCloseResult.Rejected(ShadowExecutionCloseRejection.WRONG_EXECUTION);
        }
        // Invalidate before returning.
        impl.invalidated = true;
        live.set(null);
        return new ShadowExecutionCloseResult.Closed();
    }

    /** Publishes fresh base-binding evidence to the live view (binding-observer route). */
    SignalResult publishBaseBinding(AtlasBindingEvidence evidence) {
        Objects.requireNonNull(evidence, "evidence");
        Live current = live.get();
        if (current == null || current.view.invalidated) {
            return new SignalResult.Rejected(com.schmaloogium.engine.frame.HookRejection.STALE_PUBLICATION);
        }
        current.view.currentBaseBinding = evidence;
        ShadowBaseBindingReceiver receiver = current.view.receiver;
        if (receiver != null) {
            return receiver.refreshBaseBinding(evidence);
        }
        return new SignalResult.Accepted();
    }
}
