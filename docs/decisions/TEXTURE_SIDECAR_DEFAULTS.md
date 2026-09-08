# Owned texture sidecars — defaults and recovery decision

**Date:** 2026-09-07. **Status:** explicit maintainer-selected local compatibility policy; not measured G6 parity or implementation clearance.

## Evidence and separate authority

The narrow U1 correction in `docs/decisions/U1_TEXTURE_SAMPLING.md` did not ratify sampling defaults or malformed-sidecar behavior. An independent follow-up investigated the existing Phase 13 §4.3.5/§11.3 concern.

The G6 shipped author document (`reference-src/schlorbium-HD_U_G6_pre1/doc/shaders.properties`, custom-texture section) establishes the source forms, duplicate numeric discriminators, `.mcmeta` use and noise override, but does not specify field defaults or malformed-file recovery. The published [Iris custom-texture documentation](https://shaders.properties/current/reference/buffers/custom_textures/#mcmeta-file), read 2026-09-07, explicitly gives image nearest/wrapping and raw bilinear/clamping defaults, defines `texture.blur`/`texture.clamp`, and excludes shader-provided sidecars for resource-pack/atlas textures. It does not specify malformed-sidecar recovery or this project's noise default. These limits were presented before asking for policy authority.

## Exact maintainer selections

For owned custom textures, the maintainer selected **“Source-specific defaults with isolated errors”**:

> PNG defaults NEAREST/REPEAT; raw defaults LINEAR/CLAMP_TO_EDGE. Omitted fields retain the source baseline; explicit boolean fields override it. Malformed/unreadable sidecar resets the whole sidecar to baseline with one diagnostic and fingerprinted outcome. Preserve target/format validation and foreign-object ownership; no silent legality coercion.

For a pack-provided noise PNG, the maintainer selected **“Retain noise-specific LINEAR/REPEAT”**:

> Keep the existing noise baseline. Apply the same omitted-field and malformed-sidecar rules using this baseline; generated noise remains unchanged. Record this as a maintainer-selected policy pending runtime evidence.

These decisions are independent of U1 and do not grant unspecified property-key syntax or modern texture/stage features.

## Binding owned-texture policy

| Owned source | Absent sidecar / baseline | Missing field in valid sidecar |
|---|---|---|
| Ordinary pack PNG | NEAREST filtering, REPEAT wrapping | Retain that field's baseline |
| Raw texture | LINEAR filtering, CLAMP_TO_EDGE wrapping | Retain that field's baseline |
| Pack noise PNG override | LINEAR filtering, REPEAT wrapping | Retain that field's baseline |

An explicit `texture.blur` boolean selects LINEAR when true and NEAREST when false. An explicit `texture.clamp` boolean selects CLAMP_TO_EDGE when true and REPEAT when false. Omission is not synonymous with false.

Interpret the sidecar atomically. A malformed or unreadable sidecar discards all its overrides, retains the usable source with its source-specific baseline, emits one diagnostic for that sidecar outcome and fingerprints the outcome. It must not preserve the first successfully parsed override after a later field fails. The owning phase must specify exact JSON/type/unknown-member handling and deterministic fingerprint encoding consistently with its existing bounds and diagnostic contracts; this decision is not a new parser/API declaration.

Generated noise is unchanged. Minecraft/resource-pack/atlas/dynamic borrowed textures retain actual owner parameters; a shader-provided sidecar must not mutate them. Their acquisition/availability failures remain under existing owner contracts, not this owned-sidecar recovery policy.

## Legality, identity and verification

These defaults are requested sampling policy, not permission to use illegal/incomplete target/format combinations. Existing target/format/capability validation and failure containment remain required. Do not silently coerce explicit or default sampling state to make an incompatible texture appear supported; any missing legality disposition must be specified by its owner before implementation and conformance claims.

Phase 13 must adopt the policy in active §5/incorporated semantics, §8 cases, §11 and its parameterization identity/fingerprint schema. Phase 3 source parsing and sidecar references remain unchanged; downstream consumers use actual effective parameters rather than independently selecting defaults. Propagate any owner-required parameterization schema change explicitly; no inferred old/new conversion.

Required cases include source-specific absence, one omitted field, explicit false distinct from omission for raw/noise, full atomic fallback after a partially valid malformed sidecar, unreadable sidecar, unchanged generated noise, borrowed-owner nonmutation, target/format rejection and deterministic parameterization identity. These are planned checks, not test results.

Fresh owner/receiver verification, IR-01, actual runtime behavior and the v0.5 classic-matrix T3 gate remain. No historical research/design/review text is rewritten and no OptiFine parity is claimed by this policy selection.
