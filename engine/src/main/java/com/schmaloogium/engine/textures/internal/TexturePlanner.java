// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.config.CustomTextureSpec;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.textures.CompanionMacroState;
import com.schmaloogium.engine.textures.CompanionPolicy;
import com.schmaloogium.engine.textures.NoisePlan;
import com.schmaloogium.engine.textures.OwnedTextureSourceKind;
import com.schmaloogium.engine.textures.TextureMemoryEstimate;
import com.schmaloogium.engine.textures.TexturePlan;
import com.schmaloogium.engine.textures.TexturePlanRequest;
import com.schmaloogium.engine.textures.TextureSourceAsset;
import com.schmaloogium.engine.textures.TextureSourceIdentity;
import com.schmaloogium.engine.config.NoiseTextureSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The planning orchestrator (§2.3): schema gate, macro/policy pairing validation, then the
 * three pure planners — companions (discovery derived from the prepared-source catalog's
 * {@code companion:<atlas>:<kind>:<icon>} assets), noise, and custom textures. Pure and
 * total: Planned or thrown, never partial; the system maps the typed exceptions.
 */
public final class TexturePlanner {

    private TexturePlanner() {
    }

    /** Raised on the D-P13-44 schema gate and pairing contradictions. */
    public static final class SchemaGateException extends RuntimeException {
        public SchemaGateException(String message) {
            super(message);
        }
    }

    public static TexturePlan plan(TexturePlanRequest request) {
        Objects.requireNonNull(request, "request");
        var configuration = request.configuration();
        if (configuration.schemaVersion() != PackFrontEnd.CURRENT_SCHEMA_VERSION) {
            throw new SchemaGateException("configuration schema gate: "
                + configuration.schemaVersion() + " != "
                + PackFrontEnd.CURRENT_SCHEMA_VERSION);
        }
        var policy = request.companionPolicy();
        var macros = request.macroState();
        if (!CompanionPlanner.pairingConsistent(policy, macros)) {
            throw new SchemaGateException(
                "companion macro/policy pairing contradiction");
        }
        var companionPlanning = CompanionPlanner.plan(request.atlases(), policy, macros,
            catalogDiscovery(request));
        var noise = NoisePlanning.resolve(configuration.resources(),
            noiseSpec(configuration));
        var customPlanning = CustomTexturePlanner.plan(configuration,
            preparedAssets(request), parameterizations(request));
        return new TexturePlan(request, companionPlanning.plans(), noise,
            customPlanning.entries(), customPlanning.unsupported(), macros,
            new TextureMemoryEstimate(companionPlanning.memory().companionBytes(),
                NoisePlanning.noiseMemory(noise).noiseBytes(), 0L));
    }

    /** Catalog-derived total discovery: exact {@code companion:} logical sources only. */
    private static CompanionPlanner.Discovery catalogDiscovery(TexturePlanRequest request) {
        Map<String, String> discovered = new HashMap<>();
        for (var asset : request.sources().assets()) {
            if (asset instanceof TextureSourceAsset.ReadyAsset ready
                && ready.identity() instanceof TextureSourceIdentity.OwnedUpload owned
                && owned.sourceKind() == OwnedTextureSourceKind.COMPANION_RESOURCE
                && owned.logicalSource().startsWith("companion:")) {
                discovered.put(owned.logicalSource(), owned.logicalSource());
            }
        }
        return (base, kind, iconName) -> Optional.ofNullable(discovered.get(
            "companion:" + base.value() + ":" + kind.name() + ":" + iconName));
    }

    private static Map<CustomTextureSpec, TextureSourceAsset> preparedAssets(
            TexturePlanRequest request) {
        Map<CustomTextureSpec, TextureSourceAsset> bySpec = new HashMap<>();
        var specs = request.configuration().properties().textures();
        Map<String, TextureSourceAsset.ReadyAsset> byLogical = new HashMap<>();
        for (var asset : request.sources().assets()) {
            if (asset instanceof TextureSourceAsset.ReadyAsset ready
                && ready.identity() instanceof TextureSourceIdentity.OwnedUpload owned) {
                byLogical.put(owned.logicalSource(), ready);
            } else if (asset instanceof TextureSourceAsset.ReadyAsset foreignReady
                && foreignReady.identity()
                instanceof TextureSourceIdentity.ForeignLive foreign) {
                byLogical.put(foreign.exactResourceIdentity(), foreignReady);
            }
        }
        for (var spec : specs) {
            var match = switch (spec) {
                case CustomTextureSpec.PackPath p ->
                    byLogical.get("pack:" + p.image().canonicalString());
                case CustomTextureSpec.Raw r ->
                    byLogical.get("raw:" + r.bytes().canonicalString());
                case CustomTextureSpec.MinecraftResource m ->
                    byLogical.get(m.resourceIdentity());
            };
            if (match != null) {
                bySpec.put(spec, match);
            }
        }
        return bySpec;
    }

    private static Map<CustomTextureSpec, CustomTexturePlanner.PreparedParameterization>
        parameterizations(TexturePlanRequest request) {
        Map<CustomTextureSpec, CustomTexturePlanner.PreparedParameterization> bySpec =
            new HashMap<>();
        var prepared = preparedAssets(request);
        for (var entry : prepared.entrySet()) {
            if (entry.getValue() instanceof TextureSourceAsset.ReadyAsset ready) {
                var kind = sourceKind(ready.identity());
                var role = roleOf(kind);
                bySpec.put(entry.getKey(),
                    new CustomTexturePlanner.PreparedParameterization(ready.parameters(),
                        SidecarPolicy.fixedRoleFingerprint(kind, role,
                            ready.parameters())));
            }
        }
        return bySpec;
    }

    private static OwnedTextureSourceKind sourceKind(TextureSourceIdentity identity) {
        return switch (identity) {
            case TextureSourceIdentity.OwnedUpload owned -> owned.sourceKind();
            case TextureSourceIdentity.ForeignLive foreign ->
                OwnedTextureSourceKind.MINECRAFT_DECODED_ASSET;
        };
    }

    private static SidecarPolicy.Role roleOf(OwnedTextureSourceKind kind) {
        return switch (kind) {
            case PACK_PNG -> SidecarPolicy.Role.CUSTOM_PNG;
            case RAW_BYTES -> SidecarPolicy.Role.RAW;
            case GENERATED_NOISE -> SidecarPolicy.Role.NOISE_OVERRIDE;
            case MINECRAFT_DECODED_ASSET -> SidecarPolicy.Role.FOREIGN_OWNER;
            case COMPANION_RESOURCE -> SidecarPolicy.Role.COMPANION;
            case DEFAULT_FILL -> SidecarPolicy.Role.DEFAULT_FILL;
        };
    }

    private static NoiseTextureSpec noiseSpec(PackConfiguration configuration) {
        return configuration.properties().noise();
    }
}
