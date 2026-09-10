// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import java.util.Objects;
import java.util.Optional;

/**
 * The closed sidecar interpretation outcome (§4.3.5/§4.5.1). Carries the outcome class, byte
 * evidence, failure class and blur/clamp presence tags exactly as the
 * {@code phase13.sidecar/v1} digest encodes them, plus the decoded fields when VALID.
 */
public record SidecarOutcome(
        Outcome outcome,
        Optional<String> normalizedPath,
        ByteEvidence byteEvidence,
        Optional<String> byteSha256,
        FailureClass failureClass,
        Presence blurPresence,
        Presence clampPresence,
        Optional<Boolean> effectiveBlur,
        Optional<Boolean> effectiveClamp) {

    public enum Outcome { NOT_APPLICABLE, ABSENT, VALID, MALFORMED, UNREADABLE }

    public enum ByteEvidence { NONE, COMPLETE }

    public enum FailureClass {
        NONE, ENCODING, JSON, SHAPE, DUPLICATE_MEMBER, BYTE_LIMIT, DEPTH_LIMIT, IO
    }

    public enum Presence { OMITTED, FALSE, TRUE, DISCARDED }

    public SidecarOutcome {
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(normalizedPath, "normalizedPath");
        normalizedPath = normalizedPath.map(Optional::of).orElse(Optional.empty());
        Objects.requireNonNull(byteEvidence, "byteEvidence");
        Objects.requireNonNull(byteSha256, "byteSha256");
        byteSha256 = byteSha256.map(Optional::of).orElse(Optional.empty());
        Objects.requireNonNull(failureClass, "failureClass");
        Objects.requireNonNull(blurPresence, "blurPresence");
        Objects.requireNonNull(clampPresence, "clampPresence");
        Objects.requireNonNull(effectiveBlur, "effectiveBlur");
        effectiveBlur = effectiveBlur.map(Optional::of).orElse(Optional.empty());
        Objects.requireNonNull(effectiveClamp, "effectiveClamp");
        effectiveClamp = effectiveClamp.map(Optional::of).orElse(Optional.empty());
        validate(outcome, byteEvidence, byteSha256, failureClass, blurPresence,
            clampPresence, effectiveBlur, effectiveClamp);
    }

    private static void validate(Outcome outcome, ByteEvidence byteEvidence,
                                 Optional<String> byteSha256, FailureClass failureClass,
                                 Presence blurPresence, Presence clampPresence,
                                 Optional<Boolean> effectiveBlur,
                                 Optional<Boolean> effectiveClamp) {

        switch (outcome) {
            case VALID -> {
                if (byteEvidence != ByteEvidence.COMPLETE || byteSha256.isEmpty()) {
                    throw new IllegalArgumentException("VALID requires COMPLETE byte evidence");
                }
                if (failureClass != FailureClass.NONE) {
                    throw new IllegalArgumentException("VALID requires failure class NONE");
                }
                // VALID keeps omission distinctions: a field is decoded exactly when its
                // presence tag is TRUE/FALSE, and the value must equal the tag.
                if (effectiveBlur.isEmpty() != (blurPresence == Presence.OMITTED)
                        || effectiveClamp.isEmpty() != (clampPresence == Presence.OMITTED)) {
                    throw new IllegalArgumentException(
                        "VALID effective fields must match OMITTED presence exactly");
                }
                if (effectiveBlur.isPresent()
                        && effectiveBlur.get() != (blurPresence == Presence.TRUE)
                        || effectiveClamp.isPresent()
                        && effectiveClamp.get() != (clampPresence == Presence.TRUE)) {
                    throw new IllegalArgumentException(
                        "VALID effective values must match TRUE/FALSE presence");
                }
            }
            case MALFORMED -> {
                if (failureClass == FailureClass.NONE || failureClass == FailureClass.IO) {
                    throw new IllegalArgumentException(
                        "MALFORMED requires a parse-class failure");
                }
                if (blurPresence != Presence.DISCARDED || clampPresence != Presence.DISCARDED) {
                    throw new IllegalArgumentException(
                        "MALFORMED discards both field presences");
                }
                if (!effectiveBlur.isEmpty() || !effectiveClamp.isEmpty()) {
                    throw new IllegalArgumentException("MALFORMED has no effective fields");
                }
                // Over-limit outcomes keep no byte evidence; a bounded complete input that
                // failed syntax/semantics keeps COMPLETE evidence with the full-byte hash.
                boolean overLimit = failureClass == FailureClass.BYTE_LIMIT
                    || failureClass == FailureClass.DEPTH_LIMIT;
                if (overLimit != (byteEvidence == ByteEvidence.NONE)) {
                    throw new IllegalArgumentException(
                        "MALFORMED byte evidence must match its limit class");
                }
                if (!overLimit && byteSha256.isEmpty()) {
                    throw new IllegalArgumentException(
                        "complete malformed bounded input keeps the full-byte hash");
                }
                if (overLimit && byteSha256.isPresent()) {
                    throw new IllegalArgumentException("over-limit input keeps no byte hash");
                }
            }
            case UNREADABLE -> {
                if (failureClass != FailureClass.IO) {
                    throw new IllegalArgumentException("UNREADABLE requires failure class IO");
                }
                if (byteEvidence != ByteEvidence.NONE || byteSha256.isPresent()) {
                    throw new IllegalArgumentException(
                        "UNREADABLE keeps no byte evidence (no partial-byte hash)");
                }
                if (blurPresence != Presence.DISCARDED || clampPresence != Presence.DISCARDED) {
                    throw new IllegalArgumentException(
                        "UNREADABLE discards both field presences");
                }
            }
            case ABSENT, NOT_APPLICABLE -> {
                if (byteEvidence != ByteEvidence.NONE || byteSha256.isPresent()) {
                    throw new IllegalArgumentException("absent outcomes carry no bytes");
                }
                if (failureClass != FailureClass.NONE) {
                    throw new IllegalArgumentException("absent outcomes carry failure NONE");
                }
                if (blurPresence != Presence.OMITTED || clampPresence != Presence.OMITTED) {
                    throw new IllegalArgumentException("absent outcomes are OMITTED, no bytes");
                }
            }
        }
    }

    /** The canonical phase13.sidecar/v1 digest of this outcome. */
    public String digest() {
        String pathTag = normalizedPath.isPresent() ? "PRESENT" : "ABSENT";
        String pathValue = normalizedPath.orElse("");
        String byteSha = byteSha256.orElse("");
        return TextureDigests.sidecarDigest(pathTag, pathValue, outcome.name(),
            byteEvidence.name(), byteSha, failureClass.name(),
            blurPresence.name(), clampPresence.name());
    }

    /** The fixed NOT_APPLICABLE outcome: no path, no bytes, OMITTED fields. */
    public static SidecarOutcome notApplicableOutcome() {
        return new SidecarOutcome(Outcome.NOT_APPLICABLE, Optional.empty(),
            ByteEvidence.NONE, Optional.empty(), FailureClass.NONE,
            Presence.OMITTED, Presence.OMITTED, Optional.empty(), Optional.empty());
    }

    /** The ABSENT outcome: a retained reference whose sidecar is simply not present. */
    public static SidecarOutcome absent() {
        return new SidecarOutcome(Outcome.ABSENT, Optional.empty(),
            ByteEvidence.NONE, Optional.empty(), FailureClass.NONE,
            Presence.OMITTED, Presence.OMITTED, Optional.empty(), Optional.empty());
    }
}
