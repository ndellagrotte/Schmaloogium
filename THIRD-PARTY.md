# Third-party material and notices

This file is the D-8 compliance mechanism (RESEARCH.md §10.1): one entry per
incorporation of third-party code, created before the first incorporation rather than
reconstructed at release time. At v0.1 the table is empty of code incorporations —
that is the correct state; the mechanism exists first.

## Standing prohibitions and permissions

> **Never copy from glsl-transformer.** Iris bundles it; it is **AGPL-3.0**; its
> network-service terms would attach to the derived portion. Iris's own LGPL-3.0 code is
> fine. **The prohibition has a second address:** Pintonium depends on
> `org.taumc:glsl-transformation-lib`, a fork of the same library — treat it as AGPL:
> never copy from it and never adopt it as a dependency, including transitively through
> any Pintonium incorporation.
>
> **The OptiFine decompile (`reference-src/schlorbium-HD_U_G6_pre1/`, aliased
> `schlorbium-project/`) is behavioral-observation-only.** No identifier, structure, or
> code derived from it ships.
>
> **Pintonium (`reference-src/Pintonium-main/`) is GPL-3.0:** readable as a reference.
> Any future incorporation must carry an entry in the table below and comply with
> GPL-3.0. Two carve-outs ride with any such permission: the `glsl-transformation-lib`
> prohibition above, and the vendored `kroppeb/stareval` expression engine, whose
> **license is unverified** — verify before any reuse, else clean-room from
> RESEARCH.md App F.6, which this project owns regardless.
>
> Within the Oculus reference checkout, `src/main/java/net/coderbot/iris/pipeline/transform/`,
> `libs/`, and `glsl-relocated/` are never read, cited, summarized, copied, or depended on.

## Shipped dependencies

Binary dependencies shipped inside the mod jar (via the `contain` configuration) carry
their upstream license unchanged. Exact versions and verification are recorded in
`PINS.md`.

| Files (shipped as) | Upstream | License | Notice | Modifications |
|---|---|---|---|---|
| `jcpp-1.4.14.jar` | org.anarres:jcpp 1.4.14 — https://github.com/shevek/jcpp | Apache-2.0 (per Maven POM) | Apache License 2.0 applies; jar carries no NOTICE file | None (unmodified artifact) |
| `slf4j-api-1.7.12.jar` | org.slf4j:slf4j-api 1.7.12 — https://github.com/qos-ch/slf4j | MIT (per Maven POM) | Copyright (c) 2004-2025 QOS.ch Sarl (http://qos.ch) — see jar META-INF | None (unmodified artifact) |

## Incorporations

None yet. The first incorporation from Iris, Angelica, or Pintonium adds a row here
before the code lands.
