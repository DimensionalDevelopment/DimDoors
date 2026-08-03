# Target Resolution Refactor Followups

Quick audit notes from the private/dialing target cleanup pass.

## Still Undone

### Entrance target resolution

`PlayerTrackingEntranceTarget` still does direct block-entity lookup:

- `common/src/main/java/org/dimdev/dimdoors/rift/targets/PlayerTrackingEntranceTarget.java`
- around `destLoc.getBlockEntity() instanceof EntityTarget`

Exit target already uses `TargetResolver.entity(...)`, so entrance should probably use the same path.

### Rift/block-target lookup helpers

These still do local/manual rift lookup instead of a shared helper:

- `common/src/main/java/org/dimdev/dimdoors/pockets/TemplateUtils.java`
- `common/src/main/java/org/dimdev/dimdoors/rift/targets/RandomTarget.java`
- `common/src/main/java/org/dimdev/dimdoors/rift/targets/RestoringTarget.java`
- `common/src/main/java/org/dimdev/dimdoors/rift/targets/TempTarget.java`

The common shape is "look up a rift block entity at a location, then operate on it".

### Door lower-half normalization

Still repeated in:

- `common/src/main/java/org/dimdev/dimdoors/DimensionalDoors.java`
- `common/src/main/java/org/dimdev/dimdoors/command/PocketCommand.java`
- `common/src/main/java/org/dimdev/dimdoors/block/door/DialingDoor.java`
- `common/src/main/java/org/dimdev/dimdoors/block/door/DimensionalDoorBlock.java`

This is the old "bottom half owns the rift" artifact.

### Sable tracking update glue

Same conceptual sequence appears in:

- `common/src/main/java/org/dimdev/dimdoors/compat/sable/mixins/RiftRegistrySableTrackingMixin.java`
- `common/src/main/java/org/dimdev/dimdoors/compat/sable/mixins/BlockAssemblyMixin.java`

Common shape: get registered rift for a `Location`, then call `SableHelper.INSTANCE.updateRiftTrackingPoint(...)`.

### Small player-tracking cleanup

`PlayerTrackingEntranceTarget` assigns `key`, then still recomputes `getKey(uuid)` in at least one spot.

Prefer reusing the local key consistently.

## Already Folded

- `RiftReference` now resolves through `TargetResolver.target(...)`.
- Private/dialing exit behavior is folded into `PlayerTrackingExitTarget`.
- Private/dialing entrance generation is mostly folded into `PlayerTrackingEntranceTarget`.
- Exit dimension checks are now delegated through the subsystem.
- Dialing `setCurrentKey(...)` delegates through `setPlayerAddress(...)`.
