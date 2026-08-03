# VirtualTarget Dungeon Generation Audit

This audit traces how dungeon and pocket generation is reached from `VirtualTarget`
instances. It covers the current non-dialing variants. The dialing door path is
left out because `DialingTargetImpl` currently mirrors the private-pocket flow and
should be audited separately when that implementation diverges.

## Entry Points

Virtual targets are serialized through `VirtualTarget.CODEC` and stored on rifts
or door data. The main runtime entry is a rift receiving an entity, which resolves
the rift's `VirtualTarget` and calls that target's receive method.

The main source files are:

| Area | Source |
| --- | --- |
| Virtual target registry | `common/src/main/java/org/dimdev/dimdoors/rift/targets/VirtualTarget.java` |
| Random dungeon/link behavior | `common/src/main/java/org/dimdev/dimdoors/rift/targets/RandomTarget.java` |
| Dungeon group target | `common/src/main/java/org/dimdev/dimdoors/rift/targets/DungeonTarget.java` |
| Direct virtual-pocket target | `common/src/main/java/org/dimdev/dimdoors/rift/targets/TemplateTarget.java` |
| Public pocket target | `common/src/main/java/org/dimdev/dimdoors/rift/targets/PublicPocketTarget.java` |
| Private pocket target | `common/src/main/java/org/dimdev/dimdoors/rift/targets/PrivatePocketTarget.java` |
| Generation facade | `common/src/main/java/org/dimdev/dimdoors/pockets/PocketGenerator.java` |
| Generator execution | `common/src/main/java/org/dimdev/dimdoors/pockets/generator/PocketGenerator.java` |
| Rift wiring after placement | `common/src/main/java/org/dimdev/dimdoors/pockets/TemplateUtils.java` |
| Door-data source | `fabric/src/main/datagen/org/dimdev/dimdoors/datagen/DoorDataDataGen.java` |

## Common Generation Flow

For generated pockets, the flow is:

1. A source rift has a `VirtualTarget`.
2. The target builds a `VirtualLocation` from the source rift location.
3. The target calls one of the `PocketGenerator` facade methods.
4. `PocketGenerator` loads a `VirtualPocket` group or direct `VirtualPocket`.
5. The `VirtualPocket` resolves a weighted `PocketGeneratorReference`.
6. The referenced concrete generator creates and places the pocket.
7. Generator modifiers collect or create rifts, assign `RiftData`, and mark a
   pocket entrance.
8. `TemplateUtils.registerRifts(...)` selects one `PocketEntranceMarker`,
   registers it with `PocketRegistry`, and replaces `PocketExitMarker` with the
   supplied `linkTo` target.

`PocketCreator.create(...)` guards against re-entrant generation by source rift
or virtual location before delegating to the concrete generator.

## RandomTarget Path

`RandomTarget` is the core random-link/new-dungeon algorithm used by
`AvailableLinkTarget` and `DungeonTarget`.

Selection steps:

1. Convert the source rift to a `VirtualLocation`.
2. Build a weighted candidate map from all registered rifts in `RiftRegistry`.
3. Candidate rifts are skipped when they have no properties, no matching accepted
   groups, zero weight, or no links remaining.
4. Add a `null` candidate when `newRiftWeight > 0`; that means "generate a new
   rift or dungeon".
5. Pick a candidate with `MathUtil.weightedRandom(...)`.
6. If an existing rift is picked, link source to target and optionally target back
   to source.
7. If `null` is picked, generate a virtual destination:
   - depth `<= 0`: create a detached overworld rift at the heightmap position.
   - depth `> 0`: generate a dungeon pocket in `dimdoors:dungeon_pockets`.

For new dungeon pockets, `RandomTarget` passes:

| Parameter | Value |
| --- | --- |
| `virtualLocation` | Randomized x, z, depth from the source virtual location |
| `linkTo` | `RiftReference(source)` unless `noLinkBack` is true |
| `linkProperties` | Source rift properties copied with `linksRemaining(0)` when available |

## VirtualTarget Variants

| Target type | Generates | Destination world | Selection source | Notes |
| --- | --- | --- | --- | --- |
| `dimdoors:dungeon` | Dungeon pocket or existing compatible rift | `dimdoors:dungeon_pockets` for new dungeon pockets | Configured `dungeonGroup` | Extends `RandomTarget`; only difference from `available_link` is the group passed to `generateDungeonPocketV2(...)`. |
| `dimdoors:available_link` | Dungeon pocket, overworld detached rift, or existing compatible rift | Overworld when generated depth is `<= 0`; otherwise `dimdoors:dungeon_pockets` | Hardcoded default dungeon group | This is the built-in recursive dungeon exit target in `pockets/rift_data/default_dungeon.json`. |
| `dimdoors:template` | A specific virtual pocket | `dimdoors:dungeon_pockets` | Direct `Holder<VirtualPocket>` in the target | Uses source x/z and depth + 1, then links back to the source rift. |
| `dimdoors:public_pocket` | Public pocket | `dimdoors:public_pockets` | `dimdoors:public` group | Restoring target. After generation, the source rift is rewritten to the generated entrance. |
| `dimdoors:private` | Owner private pocket | `dimdoors:personal_pockets` | `dimdoors:private` group | Entity target. Uses owner UUID, reuses an existing private pocket when present, otherwise creates one at depth `-1`. |
| `dimdoors:private_pocket_exit` | No generation | Existing stored exit location | `PrivateRegistry` | Used inside private pockets. It resolves the remembered exit target or sends the entity to Limbo on failure. |
| `dimdoors:escape` | No pocket generation | Configured escape world or Limbo | N/A | Used by oak/escape doors and explicit escape rift data. |
| `dimdoors:unstable` | Sometimes dungeon generation | Same as `dimdoors:dungeon` on success branch | Hardcoded `DungeonTarget.builder()` | 50 percent chance to use dungeon generation, otherwise Limbo. |

Excluded:

| Target type | Reason |
| --- | --- |
| `dimdoors:dialing` | Current implementation is a private-pocket style copy and was intentionally excluded from this audit. |
| `dimdoors:dialing_pocket_exit` | Same dialing exclusion. |

## Door Variants

Door items get their target behavior from dynamic `door/data` entries generated by
`DoorDataDataGen`. The generated files are under `common/src/main/generated`.

| Door item | Generated door behavior | Virtual target | Group/properties | Result |
| --- | --- | --- | --- | --- |
| `dimdoors:stone_door` | Dungeon door | `dimdoors:dungeon` | `dungeonGroup=dimdoors:dungeon`; groups `[0, 1]`; linksRemaining `1` | Generates any default dungeon group member, including path-selected dungeon generators and `dimdoors:lab/lab_hallway`. |
| `minecraft:crimson_door` | Nether dungeon door | `dimdoors:dungeon` | `dungeonGroup=dimdoors:nether`; groups `[0, 1]`; linksRemaining `1` | Selects generators tagged `nether`. |
| `dimdoors:amalgam_door` | Myth dungeon door | `dimdoors:dungeon` | `dungeonGroup=dimdoors:myth`; groups `[0, 1]`; linksRemaining `1` | Selects generators tagged `myth`. |
| `dimdoors:quartz_door` | Private pocket door or private exit | `dimdoors:private` outside personal pockets; `dimdoors:private_pocket_exit` inside personal pockets | No link properties in generated data | Creates or re-enters an owner private pocket outside personal pockets; exits when already inside the personal pocket dimension. |
| `minecraft:iron_door` | Public pocket door | `dimdoors:public_pocket` | No explicit link properties | Creates a public pocket and rewrites the source rift to the generated entrance. |
| `minecraft:oak_door` | Escape door | `dimdoors:escape` | `canEscapeLimbo=true` | Does not generate a dungeon. Escapes from pocket dimensions or Limbo. |

Autogenerated item fallback behavior:

| Item class | Missing door data fallback |
| --- | --- |
| Autogenerated door item | `PublicPocketTarget` |
| Autogenerated trapdoor item | `EscapeTarget(true)` |

## Built-In Dungeon Content

The default dungeon group is:

```text
common/src/main/resources/resourcepacks/default/data/dimdoors/pockets/groups/dungeon.json
```

It contains:

| Selector | Meaning |
| --- | --- |
| `path=dimdoors:dungeon/` | Includes all virtual pockets whose IDs start with `dimdoors:dungeon/`. |
| `id=dimdoors:lab/lab_hallway` | Adds the lab hallway virtual pocket explicitly. |

The recursive dungeon rift data is:

```text
common/src/main/resources/resourcepacks/default/data/dimdoors/pockets/rift_data/default_dungeon.json
```

It uses `dimdoors:available_link` with:

```text
newRiftWeight=1
weightMaximum=100
coordFactor=1
positiveDepthFactor=80
negativeDepthFactor=10000
acceptedGroups=[0]
noLink=false
noLinkBack=false
```

Generated dungeon-door data uses the opposite depth bias:

```text
positiveDepthFactor=10000
negativeDepthFactor=160
```

That makes initial dungeon doors strongly prefer increasing dungeon depth, while
default dungeon exits strongly prefer returning toward shallower depth.

## Rift Wiring After Placement

After a pocket is placed, generator setup does this:

1. Set each rift destination's location to the placed block position.
2. Find rifts whose destination is `PocketEntranceMarker`.
3. Randomly select one entrance marker by marker weight.
4. On the selected entrance, replace the marker with `ifDestination`.
5. On unselected entrances, replace the marker with `otherwiseDestination`.
6. Register the selected entrance in `PocketRegistry`.
7. Replace any `PocketExitMarker` destination with the supplied `linkTo`, unless
   link properties are one-way.
8. Register and mark every rift changed.

In the common `pockets/rift_data/pocket_entrance.json`, the selected entrance's
`ifDestination` is `PocketExitMarker`, so the selected entrance becomes the exit
rift that links back to the source.

## Audit Findings

| Severity | Finding | Impact |
| --- | --- | --- |
| High | `TemplateUtils.registerRifts(...)` returns immediately when no `PocketEntranceMarker` exists. | If a generated pocket has exit markers but no entrance marker, none of the exits are converted or registered. That leaves placeholder targets in the world. |
| Medium | `MathUtil.weightedRandom(Map<T, Float>)` accumulates float weights into an `int`. | Fractional weights are truncated during total-weight calculation, biasing or breaking selection for existing rift weights. `WeightedList` does not have this issue because it uses `double`. |
| Medium | New generated dungeon entrance properties are copied with `linksRemaining(0)` and then `TemplateUtils.linkRifts(source, entrance)` decrements the entrance target's links remaining. | Newly generated linked entrances can end up with negative link counts. They are skipped by future random-link selection anyway, but the negative state is surprising and should be intentional or clamped. |
| Medium | `DungeonTarget` and `AvailableLinkTarget` share almost all behavior, but only `DungeonTarget` carries an explicit dungeon group. | This is easy to misread in JSON: recursive dungeon exits use `available_link`, while door variants use `dungeon`. |
| Low | `DoorDataDataGen` source registers crimson door to `dimdoors:nether`, while language labels call it "Myth Door". | This looks like label/content drift rather than a generation bug. |
| Low | `PathSelector` caches path results after first initialization. | Runtime datapack changes after initialization would not be seen by that selector instance. This is probably acceptable for current load behavior but worth noting. |

## Short Summary

The normal generated-dungeon path is:

```text
door data -> DungeonTarget -> RandomTarget.receiveOther()
  -> generateDungeonPocketV2(..., dungeonGroup)
  -> group VirtualPocket
  -> weighted PocketGeneratorReference
  -> concrete PocketGenerator
  -> modifiers
  -> TemplateUtils.registerRifts(...)
```

The recursive in-dungeon path is:

```text
default_dungeon rift data -> AvailableLinkTarget -> RandomTarget.receiveOther()
  -> existing compatible rift, overworld detached rift, or default dungeon group
```

The public/private/template paths use the same pocket-generation facade but avoid
the random existing-rift selection step.
