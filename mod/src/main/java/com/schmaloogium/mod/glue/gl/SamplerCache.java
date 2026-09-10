// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

import com.schmaloogium.engine.buffers.BufferResizeReason;
import com.schmaloogium.engine.gl.SamplerKey;
import com.schmaloogium.engine.gl.TextureParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A1's sampler-object cache (PHASE_14_DOC §4.1; helper home mod.glue.gl per P1
 * D-P1-54). D-P14-1: a sampler is derived, never authored — every native sampler is
 * interned under the {@link SamplerKey} that is the pure sampler-state prefix of the
 * owner's complete {@link TextureParameters}, so equal parameters reuse one native
 * object and no second spelling of filter/wrap policy exists. The fixed
 * {@code SamplerHandle[16]} indexed by the App B.3 unit is the bind cache (D-P14-3: no
 * per-program allocation, no allocator); {@code prepareUnits} is the D-P14-27
 * normalization and {@link #clearAllUnits} the D-P14-4 fixed-function dispatch.
 *
 * <p>Cardinality is small but uncapped — no dozen-state assumption. Lifecycle follows
 * actual ownership: all eight {@link BufferResizeReason} values invalidate bound-unit
 * knowledge and retire every entry (D-P14-19/§4.1.3), retirement is reference
 * accounted (live/retiring leases prevent premature deletion), retired publications
 * cannot bind no matter how equal a key looks, and deletion is exactly once, only at
 * zero outstanding leases. Associations retain the COMPLETE authenticated parameter
 * value per live owned identity (D-P14-31), so a stale identity is never reused as a
 * hit — equal keys alone do not authenticate a subject.
 *
 * <p>Render-thread confined (PHASE_14_DOC §7.1); render-thread/context guards and the
 * GL-error boundary are the installing adapter's duty, as for every facade verb.
 * Allocation posture (§7.2): binds and clears touch only preallocated arrays; maps
 * mutate at estate build/resize time only.
 */
public final class SamplerCache {

    /** Authenticated identity of the estate an association came from. Any component
     *  difference — accepted generation, P13 source/configuration fingerprint, or
     *  parameter publication serial — makes a stored association stale (§4.1.3: equal
     *  handle/digest/generation ALONE cannot reuse a stale entry). */
    public record EstateIdentity(long acceptedGeneration, String sourceFingerprint,
                                 long publicationSerial) {

        public EstateIdentity {
            if (sourceFingerprint == null || sourceFingerprint.isEmpty()) {
                throw new IllegalArgumentException("sourceFingerprint must not be empty");
            }
        }
    }

    /** One owned texture's association: the complete authenticated parameter value
     *  (D-P14-31 — never just the key) plus the interned handle it currently names. */
    public record Association(EstateIdentity identity, TextureParameters parameters,
                              Handle handle) {

        public Association {
            if (identity == null || parameters == null || handle == null) {
                throw new IllegalArgumentException("association fields must not be null");
            }
        }
    }

    /** The result of interning: an existing-or-new live handle, or a contained failure
     *  (native creation/parameterization failed — no handle exists, nothing cached, the
     *  owner contains and may demote per §6). */
    public sealed interface InternResult {
        record Interned(Handle handle, boolean freshlyCreated) implements InternResult {
        }

        record Failed(String reason) implements InternResult {
        }
    }

    /** Lifecycle of one interned native sampler. */
    public enum State {
        LIVE,
        /** Retired by an estate transaction; cannot bind; deleted at zero leases. */
        RETIRING,
        /** Native object deleted (exactly once). */
        DELETED
    }

    /** One interned native sampler. The native name never leaves the glue package. */
    public static final class Handle {

        final int name;
        final SamplerKey key;
        State state = State.LIVE;
        int leases;

        Handle(int name, SamplerKey key) {
            this.name = name;
            this.key = key;
        }

        /** The key this handle was interned under (its complete derived state). */
        public SamplerKey key() {
            return key;
        }

        /** Current lifecycle state. */
        public State state() {
            return state;
        }

        /** Outstanding live/retiring references (D-P14 lease accounting). */
        int leases() {
            return leases;
        }

        int name() {
            return name;
        }
    }

    private final SamplerOps ops;

    private final Map<SamplerKey, Handle> interned = new HashMap<>();
    private final Map<Object, Association> associations = new IdentityHashMap<>();
    /** Retired, not yet deletable (lease outstanding) or not yet deleted (GL error). */
    private final List<Handle> retiring = new ArrayList<>();

    /** D-P14-3: fixed 16-slot bind cache, index == App B.3 unit. */
    private final int[] boundName = new int[16];
    private final boolean[] unitKnown = new boolean[16];

    public SamplerCache(SamplerOps ops) {
        if (ops == null) {
            throw new IllegalArgumentException("ops must not be null");
        }
        this.ops = ops;
    }

    // ------------------------------------------------------------------ interning

    /** Returns the live handle for {@code key}, creating the native sampler on first
     *  use. Creation failure is contained: a {@link InternResult.Failed} with no cache
     *  entry, so a later call may retry. */
    public InternResult intern(SamplerKey key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        Handle existing = interned.get(key);
        if (existing != null && existing.state == State.LIVE) {
            return new InternResult.Interned(existing, false);
        }
        int name = ops.create(key);
        if (name < 0) {
            return new InternResult.Failed("native sampler creation/parameterization failed");
        }
        Handle handle = new Handle(name, key);
        interned.put(key, handle);
        return new InternResult.Interned(handle, true);
    }

    // ------------------------------------------------------------------ associations

    /** Commits the association of one owned texture identity with its COMPLETE
     *  authenticated parameters (D-P14-31). The caller commits only after validation
     *  and complete establishment; a replacement association supersedes the old one. */
    public void associate(Object subject, EstateIdentity identity,
                          TextureParameters parameters, Handle handle) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (handle.state != State.LIVE) {
            throw new IllegalArgumentException("associations require a LIVE handle");
        }
        associations.put(subject, new Association(identity, parameters, handle));
    }

    /** Returns the association for {@code subject} only when its stored estate identity
     *  equals {@code expected} exactly — anything else is stale and demands the
     *  complete baseline write, never a reuse (D-P14-31). */
    public Optional<Association> associationFor(Object subject, EstateIdentity expected) {
        if (subject == null || expected == null) {
            throw new IllegalArgumentException("subject and expected identity must not be null");
        }
        Association association = associations.get(subject);
        if (association != null && association.identity().equals(expected)) {
            return Optional.of(association);
        }
        return Optional.empty();
    }

    /** Forgets one subject's association (owned-texture retirement; the caller owns the
     *  zeroing of native bindings — D-P14-41). */
    public void disassociate(Object subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        associations.remove(subject);
    }

    // ------------------------------------------------------------------ binding + knowledge

    /** Binds the interned handle to one App B.3 unit through the bind cache: a bind is
     *  issued only when current knowledge does not already prove the unit holds this
     *  live handle. Retired publications cannot bind. */
    public boolean bind(int unit, Handle handle) {
        requireUnit(unit);
        if (handle == null) {
            throw new IllegalArgumentException("handle must not be null");
        }
        if (handle.state != State.LIVE) {
            throw new IllegalArgumentException(
                    "a " + handle.state + " sampler publication cannot bind (retired cannot rebind)");
        }
        if (unitKnown[unit] && boundName[unit] == handle.name) {
            return true; // proven redundant: same-live-identity, error-free knowledge
        }
        if (!ops.bind(unit, handle.name)) {
            unitKnown[unit] = false;
            return false;
        }
        boundName[unit] = handle.name;
        unitKnown[unit] = true;
        return true;
    }

    /** Binds sampler 0 to one unit. Unknown knowledge is cleared, never skipped
     *  (D-P14-4); a proven-zero unit is the only skip. */
    public boolean clearUnit(int unit) {
        requireUnit(unit);
        if (unitKnown[unit] && boundName[unit] == 0) {
            return true;
        }
        if (!ops.bind(unit, 0)) {
            unitKnown[unit] = false;
            return false;
        }
        boundName[unit] = 0;
        unitKnown[unit] = true;
        return true;
    }

    /** D-P14-27 normalization: clears the native sampler on every unoccupied unit of
     *  the 16-bit occupied mask (bit n = fixed unit n). Returns false when any clear
     *  failed — the caller must not admit draws on a failed clear. */
    public boolean prepareUnits(int occupiedUnitMask) {
        if ((occupiedUnitMask & ~0xFFFF) != 0) {
            throw new IllegalArgumentException(
                    "occupiedUnitMask must be a 16-bit set: " + occupiedUnitMask);
        }
        boolean allCleared = true;
        for (int unit = 0; unit < 16; unit++) {
            if ((occupiedUnitMask & (1 << unit)) == 0) {
                allCleared &= clearUnit(unit);
            }
        }
        return allCleared;
    }

    /** D-P14-4 fixed-function dispatch: clears all sixteen units before vanilla
     *  resumes. Returns false when any clear failed (containment at the owner). */
    public boolean clearAllUnits() {
        boolean allCleared = true;
        for (int unit = 0; unit < 16; unit++) {
            allCleared &= clearUnit(unit);
        }
        return allCleared;
    }

    // ------------------------------------------------------------------ lifecycle

    /** One resize/publication event of the estate transaction. All eight
     *  {@link BufferResizeReason} values are treated identically, as §4.1.3 requires:
     *  bound-unit knowledge is forgotten (no GL — the transaction owns GL sequencing),
     *  associations are dropped, and every interned entry is retired; replacement
     *  parameters arrive only from the transaction's accepted pairing. */
    public void onResize(BufferResizeReason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("reason must be one of the eight resize reasons");
        }
        Arrays.fill(boundName, 0);
        Arrays.fill(unitKnown, false);
        associations.clear();
        for (Handle handle : interned.values()) {
            handle.state = State.RETIRING;
            retiring.add(handle);
        }
        interned.clear();
    }

    /** Acquires one live/retiring reference; outstanding leases prevent deletion. */
    public void retain(Handle handle) {
        requireReferenceable(handle);
        handle.leases++;
    }

    /** Releases one reference; a RETIRING handle reaching zero leases is deleted
     *  immediately (exactly once). */
    public void release(Handle handle) {
        requireReferenceable(handle);
        if (handle.leases <= 0) {
            throw new IllegalArgumentException("unbalanced sampler lease release");
        }
        handle.leases--;
        if (handle.state == State.RETIRING && handle.leases == 0) {
            deleteNow(handle);
        }
    }

    /** Deletes every RETIRING handle whose leases have reached zero; a handle whose
     *  native delete failed stays queued and is retried here. */
    public void drainRetired() {
        Iterator<Handle> iterator = retiring.iterator();
        while (iterator.hasNext()) {
            Handle handle = iterator.next();
            if (handle.leases == 0) {
                if (ops.delete(handle.name)) {
                    handle.state = State.DELETED;
                    iterator.remove();
                }
            }
        }
    }

    private void deleteNow(Handle handle) {
        if (ops.delete(handle.name)) {
            handle.state = State.DELETED;
            retiring.remove(handle);
        }
        // Delete failure keeps the handle RETIRING; drainRetired() retries.
    }

    private static void requireReferenceable(Handle handle) {
        if (handle == null) {
            throw new IllegalArgumentException("handle must not be null");
        }
        if (handle.state == State.DELETED) {
            throw new IllegalArgumentException("sampler already deleted");
        }
    }

    private static void requireUnit(int unit) {
        if (unit < 0 || unit > 15) {
            throw new IllegalArgumentException("unit must be 0..15: " + unit);
        }
    }
}
