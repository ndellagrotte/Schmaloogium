// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferEstateCandidate;
import com.schmaloogium.engine.buffers.BufferEstatePublisher;
import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferEstatePublishers;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferResizeConsumer;
import com.schmaloogium.engine.buffers.BufferResizeNotice;
import com.schmaloogium.engine.buffers.BufferResizeReason;
import com.schmaloogium.engine.buffers.BufferResizeRegistrationResult;
import com.schmaloogium.engine.buffers.BufferResourceProjection;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.CapabilityGate;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainDepthSource;
import com.schmaloogium.engine.buffers.ResourceEvidenceStage;
import com.schmaloogium.engine.buffers.ShadowResourceProjection;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.record.RecordingGLDevice;
import com.schmaloogium.engine.gl.record.ScriptedResponses;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Publisher installation, registration ledger, and resize-dispatch semantics (§4.11). */
class BufferPublisherImplTest {

    private static final RegistryFingerprint ACCEPTED = new RegistryFingerprint("accepted");

    private static RecordingGLDevice device() {
        GLCapabilityProfile profile = new GLCapabilityProfile(3, 3, "3.30 publisher profile",
            "vendor", "renderer", 8, 8, 16, 16, 4096, 256, 0, Set.of());
        return new RecordingGLDevice(profile, new ScriptedResponses());
    }

    private static BufferEstateCandidate candidate(RecordingGLDevice device, String fingerprint,
            Extent2i extent) {
        BufferResourceProjection projection = new BufferResourceProjection(
            ResourceEvidenceStage.REALIZED, List.of(), 1,
            new ShadowResourceProjection(0, 0, 0, List.of(), List.of()), false, 0,
            List.of(), List.of(), CapabilityGate.OK, List.of());
        PlanningArtifacts plan = new PlanningArtifacts(
            new BufferSizing(extent, Optional.empty()), new BufferInventory(List.of()),
            1, List.of(), Map.of(), List.of(), List.of(), List.of(), projection);
        TextureHandle platformDepth = new TextureHandle() {
        };
        BorrowedDepthAttachmentHandle borrowed = device.framebuffers()
            .borrowDepthAttachment(platformDepth);
        MainDepthSnapshot.Available depth = new MainDepthSnapshot.Available(1L, borrowed,
            DepthAttachmentFormat.DEPTH_COMPONENT, extent);
        MainDepthSource depthSource = new MainDepthSource() {
            @Override
            public MainDepthPreparation prepare(Extent2i requiredExtent) {
                return new MainDepthPreparation.Ready(depth);
            }

            @Override
            public MainDepthSnapshot current() {
                return depth;
            }
        };
        EstateCore core = new EstateCore(device, diagnostic -> {
        }, new RegistryFingerprint(fingerprint), plan,
            new BufferResourceSnapshot.Available(projection), depthSource, depth);
        core.generation = 1;
        return BufferEstateCandidate.wrap(new CandidateImpl(core,
            new CandidateBuilder.Ledger(device)));
    }

    @Test
    void initialPublicationIsShadersOffGenerationZero() {
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        assertEquals(0L, publisher.current().generation());
        assertTrue(publisher.current().estate().isEmpty());
    }

    @Test
    void publishInstallsGenerationAndRejectsForeignProvenance() {
        RecordingGLDevice device = device();
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        BufferEstateCandidate candidate = candidate(device, "accepted", new Extent2i(32, 32));
        BufferPublicationResult rejected = publisher.publish(candidate,
            new RegistryFingerprint("mismatched"));
        assertInstanceOf(BufferPublicationResult.ProvenanceRejected.class, rejected);
        assertEquals(0L, publisher.current().generation());

        BufferPublicationResult accepted = publisher.publish(candidate, ACCEPTED);
        BufferPublicationResult.Published published =
            assertInstanceOf(BufferPublicationResult.Published.class, accepted);
        assertEquals(1L, published.publication().generation());
        assertTrue(published.publication().estate().isPresent());
        candidate.close(); // idempotent backstop; publication already owns the estate
    }

    @Test
    void registrationValidatesAgainstTheGenerationSizingLedger() {
        RecordingGLDevice device = device();
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        publisher.publish(candidate(device, "accepted", new Extent2i(32, 32)), ACCEPTED);
        BufferSizing sizing = publisher.current().estate().orElseThrow().sizing();

        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer(" ", notice -> null, sizing, 1L));
        BufferResizeConsumer consumer = notice -> null;
        assertInstanceOf(BufferResizeRegistrationResult.Registered.class,
            publisher.addResizeConsumer("dup", consumer, sizing, 1L));
        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer("dup", consumer, sizing, 1L));
        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer("future", consumer, sizing, 99L));
        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer("off", consumer, sizing, 0L));
        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer("mismatch", consumer,
                new BufferSizing(new Extent2i(64, 32), Optional.empty()), 1L));
        BufferResizeRegistrationResult result =
            publisher.addResizeConsumer("ok", consumer, sizing, 1L);
        assertInstanceOf(BufferResizeRegistrationResult.Registered.class, result);
        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer("ok", consumer, sizing, 1L));
    }

    @Test
    void successfulPublicationDispatchesNoticesAndAdvancesBaselines() {
        RecordingGLDevice device = device();
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        publisher.publish(candidate(device, "accepted", new Extent2i(32, 32)), ACCEPTED);
        BufferSizing first = publisher.current().estate().orElseThrow().sizing();
        List<BufferResizeNotice> received = new java.util.ArrayList<>();
        BufferResizeRegistrationResult registration = publisher.addResizeConsumer("consumer",
            notice -> {
                received.add(notice);
                return com.schmaloogium.engine.buffers.ResizeConsumerResult.SUCCESS;
            }, first, 1L);
        assertInstanceOf(BufferResizeRegistrationResult.Registered.class, registration);

        publisher.publish(candidate(device, "accepted", new Extent2i(64, 32)), ACCEPTED);
        assertEquals(1, received.size());
        BufferResizeNotice notice = received.get(0);
        assertEquals(first, notice.oldSizing());
        assertEquals(new BufferSizing(new Extent2i(64, 32), Optional.empty()),
            notice.newSizing());
        assertEquals(2L, notice.newGeneration());
        assertEquals(BufferResizeReason.DISPLAY_EXTENT, notice.reason());

        // A retained historical ready generation stays acknowledgeable with its recorded
        // sizing even after a newer generation is installed (§4.11 ledger).
        assertInstanceOf(BufferResizeRegistrationResult.Registered.class,
            publisher.addResizeConsumer("historical", n ->
                com.schmaloogium.engine.buffers.ResizeConsumerResult.SUCCESS, first, 1L));

        // Idempotent close removes the consumer; a later publication dispatches nothing.
        ((BufferResizeRegistrationResult.Registered) registration).registration().close();
        publisher.publish(candidate(device, "accepted", new Extent2i(64, 32)), ACCEPTED);
        assertEquals(1, received.size());
    }

    @Test
    void firstFailedCallbackReplacesTheEstateWithShadersOff() {
        RecordingGLDevice device = device();
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        publisher.publish(candidate(device, "accepted", new Extent2i(32, 32)), ACCEPTED);
        BufferSizing first = publisher.current().estate().orElseThrow().sizing();
        publisher.addResizeConsumer("good", notice ->
            com.schmaloogium.engine.buffers.ResizeConsumerResult.SUCCESS, first, 1L);
        publisher.addResizeConsumer("bad", notice -> {
            throw new IllegalStateException("consumer blew up");
        }, first, 1L);
        publisher.addResizeConsumer("unreached", notice ->
            com.schmaloogium.engine.buffers.ResizeConsumerResult.SUCCESS, first, 1L);

        BufferPublicationResult result = publisher.publish(
            candidate(device, "accepted", new Extent2i(64, 32)), ACCEPTED);
        BufferPublicationResult.ConsumerFailed failed =
            assertInstanceOf(BufferPublicationResult.ConsumerFailed.class, result);
        assertEquals(2L, failed.failedGeneration());
        assertEquals("bad", failed.consumerId());
        assertEquals(1, failed.deliveredCount());
        assertTrue(failed.offPublication().estate().isEmpty());
        assertEquals(3L, failed.offPublication().generation());
        assertEquals(3L, publisher.current().generation());

        // The failed generation never opened, so it is unacknowledgeable (§4.11).
        assertInstanceOf(BufferResizeRegistrationResult.Rejected.class,
            publisher.addResizeConsumer("late", notice ->
                com.schmaloogium.engine.buffers.ResizeConsumerResult.SUCCESS, first, 2L));
    }

    @Test
    void publishOffAdvancesTheGenerationWithoutANotice() {
        RecordingGLDevice device = device();
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        publisher.publish(candidate(device, "accepted", new Extent2i(32, 32)), ACCEPTED);
        BufferSizing sizing = publisher.current().estate().orElseThrow().sizing();
        List<BufferResizeNotice> received = new java.util.ArrayList<>();
        publisher.addResizeConsumer("consumer", notice -> {
            received.add(notice);
            return com.schmaloogium.engine.buffers.ResizeConsumerResult.SUCCESS;
        }, sizing, 1L);

        BufferPublicationResult result = publisher.publishOff(
            new com.schmaloogium.engine.buffers.BufferFailure(
                com.schmaloogium.engine.buffers.BufferFailureCode.FRAMEBUFFER_INCOMPLETE,
                "key", "detail", List.of(), Optional.empty(), Optional.empty()));
        BufferPublicationResult.Published published =
            assertInstanceOf(BufferPublicationResult.Published.class, result);
        assertEquals(2L, published.publication().generation());
        assertTrue(published.publication().estate().isEmpty());
        assertEquals(0, received.size());
    }

    @Test
    void closedCandidateCannotBePublished() {
        BufferEstateCandidate candidate = candidate(device(), "fp", new Extent2i(32, 32));
        candidate.close();
        assertThrows(IllegalStateException.class, () -> candidate.internal());
    }

    @Test
    void acceptanceStampsThePublicationGenerationOnTheEstateView() {
        BufferEstatePublisher publisher = BufferEstatePublishers.create();
        BufferEstateCandidate candidate = candidate(device(), "accepted", new Extent2i(32, 32));
        BufferPublicationResult result = publisher.publish(candidate, ACCEPTED);
        BufferPublicationResult.Published published =
            org.junit.jupiter.api.Assertions.assertInstanceOf(
                BufferPublicationResult.Published.class, result);
        org.junit.jupiter.api.Assertions.assertEquals(published.publication().generation(),
            published.publication().estate().orElseThrow().generation(),
            "the accepted estate view must answer the publication's generation (§5.1)");
    }
}
