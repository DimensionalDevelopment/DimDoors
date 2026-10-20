# Presence storage design

This doc covers where each piece of Presence data from `presence_spec.md` lives and how it survives schematics.

Code keeps the existing Mask naming (`MaskEntity`, `MaskType`, `MaskMode`, `MaskEvents`). "Presence" below is the spec's word for the same thing.

The entity is the source of truth:
- A Presence is saved in the .schem with all of its config.
- Pocket placement spawns it from the schematic (`RelativeBlockSample.kt:97-106`).
- The home block holds only the UUID of its bound mask.

---

## 1. Presence entity

### Persisted (`addAdditionalSaveData` / `readAdditionalSaveData`, MonolithEntity style)
| Field | Type | Notes |
|---|---|---|
| `type` | `PresenceType` (string) | `RANDOM` is allowed in saved data. |
| `mode` | `PresenceMode` | |
| `home_offset` | `BlockPos?` | **Home minus current block position**, rewritten on every save. Schematic placement relocates only `Pos`, so an absolute home would be wrong after placement. Load: `home = blockPosition() + offset`. |
| `waypoints` | `List<BlockPos>` | Relative to home. Max 10. |
| `waypoint_index` | int | |
| `waypoint_reverse` | bool | Replaces the ±1 direction. |
| `frozen` | bool | |

### Not persisted
- **Fields:** `state` (`MaskState`: IDLE/STUNNED/CHASING/RETURNING), `stateTimer`, `target`, the guard-turn timer, and the chime timer.
- **On load:** `state = IDLE`, with no target. A chunk unload already counts as losing the target (spec §4.3), so a chase never needs to survive a save.
- **Return home:** a mask that loses its target floats back home visibly (`RETURNING`, no collision) instead of teleporting. If it's unloaded mid-return, it comes back as IDLE, and its mode behaviour walks it home.
- **BLACK on load:** a `BLACK` Presence that loads from disk is discarded, because its chase can't resume.

### Synced (`SynchedEntityData`)
- `type`: mask texture and sight-bubble shape.
- `state`: glow and chase rendering.
- `frozen`.
- Yaw syncs through vanilla.

Waypoints are not synced. The wand route display (spec §9) gets them from a one-off S2C packet on right-click.

### RANDOM resolution
- On the first server tick: if `type == RANDOM` and the level is a pocket dimension (`PocketRegistry.peekPocketDirectory(level.dimension()) != null`), pick a random type excluding `BLACK` and `RANDOM`.
- In a mapmaker's build world it stays `RANDOM`, so re-exporting the schematic keeps it random.

### Schematic placement
- `frozen` is cleared when a Presence spawns from a schematic, in `RelativeBlockSample.kt:97-106`. This is temporary.

### Home check
- When it runs: on the first tick, and every 40 ticks.
- If the home's block entity is not a `MaskHomeBlockEntity`: set `home = null` and `mode = WANDER`.
- Otherwise the mask writes its own UUID into the block entity. Schematic placement strips entity UUIDs, so this re-binds after every placement.

## 2. MaskHomeBlock + MaskHomeBlockEntity
- Invisible, with no collision. It has an outline only while the player holds the Mask Wand.
- **Block entity:** `mask: UUID?`, the bound mask. It's a cache that the mask rewrites (see Home check).
- **Breaking:** `onRemove` looks the mask up by UUID and switches it to `WANDER` if it's loaded. If it isn't loaded, the mask's own home check catches it later.
- **Wand actions** on a home block go through the UUID:
  - Shift + left-click replaces the waypoints.

## 3. Mask Wand
- Already exists: `ModDataComponentTypes.MASK_WAND_WAYPOINTS` stores absolute `BlockPos` values.
- On bind (left-click a block), convert to home-relative offsets on the entity.

## 4. MaskItem
- Uses the existing `ModDataComponentTypes.MASK_TYPE`.
- The worn mask carries Curse of Binding, so it can't be taken off, and Curse of Vanishing, so death removes it with no drop.

## 5. Player attachment
These are new `DataValue`s in `world/DataValues.kt`, created with `DataValuePlatformRegistry.create`.

**`MASK_BODY_STACKS: Int`**
- Synced, so other players see the body stacks.
- Death resets it, because there's no `copyOnDeath`.
- Cleared on sleep in a root dimension, on Limbo burn, and on death.

**Timers**
- **Mask damage:** uses `player.tickCount % interval`, so nothing is stored.
- **Cyclops teleport:** `MASK_CYCLOPS_TIMER: Int`, persistent and not synced.

**`PENDING_REAPER`: `PendingReaper(entry: BlockPos, ticks: Int)`**
- Used for Chased I. Persistent, not synced.
- Set in `PlayerTeleportEvents.AFTER` when a player with Chased I arrives in the dungeon dimension.
- Any later teleport replaces it or clears it, so it never needs a dimension or pocket id.
- At 0 ticks it spawns a `BLACK` mask at `entry`, then `remove()`.

## 5b. MaskEvents (`entity/mask/MaskEvents.kt`)
- **Gates:** `ALLOW_DETECT`, `ALLOW_CATCH`, `ALLOW_WORN_EFFECTS`. These are the hooks for the Totem of Stability.
- **Fold:** `DETECTION_RANGE`, for the Decorative mask's ×0.5.
- **Notifications:** `ALERT`, `STUN`, `SHATTER`, `CATCH`, `WORN_TICK` (the hook for Echo's "disentangled" effect), `MASKS_REMOVED`.

## 6. Gamerule
- `ModGameRules.DUNGEON_REAPER = registerGameRule("dungeonReaper", MISC, false)`.

## 7. Alert propagation
- Nothing is stored.
- Resolve the pocket through `PocketDirectory.getPocketAt(pos)`, then query Presence entities inside that pocket's bounds.

## 8. Placed-content tracking (for Echo)
It only needs a yes/no per position. Who placed the block doesn't matter.

### API (dimcore, `org.dimdev.dimcore.api`)
```kotlin
fun interface PlacedContentTracker {
    fun isPlaced(level: Level, pos: BlockPos): Boolean
    fun onPlaced(level: ServerLevel, pos: BlockPos) {}
    fun onRemoved(level: ServerLevel, pos: BlockPos) {}
}

object PlacedContent {
    @JvmStatic var tracker: PlacedContentTracker = PlacedContentTracker { _, _ -> false }
    @JvmStatic fun isPlaced(level: Level, pos: BlockPos) = tracker.isPlaced(level, pos)
}
```
- **Replacing it:** set `PlacedContent.tracker`. The last one set wins, with no priority or chaining between mods.
- **Replacements that don't use our hooks:** a tracker backed by some other source can leave `onPlaced`/`onRemoved` as no-ops.

### Platform hooks (dimcore `Platform`)
Add `onAfterBlockPlace(callback: (ServerLevel, BlockPos, BlockState, Entity?) -> Unit)` and `onAfterBlockBreak(callback: (ServerLevel, BlockPos, BlockState, Player) -> Unit)`.

**Fabric**
- Place: a new mixin on `BlockItem.place(BlockPlaceContext)` at `RETURN`. Fire when the result `consumesAction()`, using `context.clickedPos`, which is the real placed position.
- Break: `PlayerBlockBreakEvents.AFTER`.

**NeoForge**
- Place: `BlockEvent.EntityPlaceEvent` at `EventPriority.LOWEST`, with canceled events skipped.
- Break: `BlockEvent.BreakEvent` at `LOWEST`, with canceled events skipped.

The existing Fabric `onBeforeBlockPlace` (`FabricPlatform.kt:84`) uses `UseBlockCallback`, which fires before placement and with the clicked block's position, so it can't be used for tracking.

### Default implementation (dimdoors)
- **Storage:** a chunk `DataValue`, `DataValues.PLACED_CONTENT`, with Codec only and no sync. It's a list of `SectionBits(sectionY: Int, bits: LongArray(64))`, and sections are allocated on first set.
- **Index:** `i = ((y and 15) shl 8) or ((z and 15) shl 4) or (x and 15)`, then word `i ushr 6`, bit `i and 63`.
- **Writes:** set or clear the bit, then `chunk.isUnsaved = true` (the `PocketChunkClaims` pattern).
- **Wiring:**
  - dimdoors init sets `PlacedContent.tracker` to this implementation.
  - The two platform hooks forward to `PlacedContent.tracker.onPlaced`/`onRemoved`.
  - They're gated to pocket dimensions only, checked with `PocketRegistry.peekPocketDirectory(level.dimension()) != null`.
- **Not covered:** only player place and break events update the bits, so pistons and explosions leave stale bits. Echo checks the block state before acting on a bit.
