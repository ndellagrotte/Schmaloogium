// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.TriState;
import com.schmaloogium.engine.uniforms.HeldItemSample;
import com.schmaloogium.engine.uniforms.UniformEventSink;

/**
 * Resolves held-hand samples into the Phase 6 held tuple (PHASE_9_DOC §4.11). Old mode
 * sends {@code (max(main, off), off)}; normal mode sends {@code (main, off)}; item IDs
 * never swap when old mode chooses the brighter hand. Empty or unknown hands carry ID 0
 * and light 0; non-block items without a captured default-state relation carry light 0.
 * Equal tuples never generate another sink event.
 *
 * <p>Recompute on hand change, alias publication, policy change, world epoch or runtime
 * publication: a resolver instance binds one lookup and one policy, so a new publication
 * or policy is a new resolver wired by glue.
 */
public final class HeldItemResolver {

    private final AliasLookup lookup;
    private final HandLightPolicy policy;
    private final UniformEventSink sink;
    private HeldItemSample last;

    public HeldItemResolver(AliasLookup lookup, HandLightPolicy policy, UniformEventSink sink) {
        this.lookup = java.util.Objects.requireNonNull(lookup, "lookup");
        this.policy = java.util.Objects.requireNonNull(policy, "policy");
        this.sink = java.util.Objects.requireNonNull(sink, "sink");
    }

    /** Accepts one authenticated hands sample and publishes changed values to the sink. */
    public void accept(HeldHandsValue hands) {
        java.util.Objects.requireNonNull(hands, "hands");
        int mainId = aliasId(hands.main());
        int mainLight = staticLight(hands.main());
        int offId = aliasId(hands.off());
        int offLight = staticLight(hands.off());
        boolean old = policy.resolvedOldHandLight() == TriState.TRUE;
        int primaryLight = old ? Math.max(mainLight, offLight) : mainLight;
        HeldItemSample sample = new HeldItemSample(hands.worldEpoch(), hands.logicalTick(),
                mainId, primaryLight, offId, offLight);
        // §4.11: an equal VALUE tuple never generates another sink event; the frame
        // identity rides along only when the values actually change.
        if (!sameTuple(sample, last)) {
            last = sample;
            sink.updateHeldItems(sample);
        }
    }

    /** The last tuple this resolver published (for diagnostics/testing). */
    public HeldItemSample lastPublished() {
        return last;
    }

    private static boolean sameTuple(HeldItemSample a, HeldItemSample b) {
        return a != null && b != null
                && a.heldItemId() == b.heldItemId()
                && a.heldBlockLightValue() == b.heldBlockLightValue()
                && a.heldItemId2() == b.heldItemId2()
                && a.heldBlockLightValue2() == b.heldBlockLightValue2();
    }

    private int aliasId(HeldStackValue hand) {
        if (hand.empty()) {
            return 0;
        }
        AliasValue alias = lookup.itemId(hand.itemOrdinal());
        return alias.present() ? alias.shaderId() : 0;
    }

    private int staticLight(HeldStackValue hand) {
        if (hand.empty()) {
            return 0;
        }
        AliasValue alias = lookup.itemId(hand.itemOrdinal());
        return alias.present() ? hand.staticLight() : 0;
    }
}
