# Dimensional Registry Old Structure

This note records the old persisted `dimensional_registry` shape for future DFU work.

The registry is stored as overworld `SavedData` named `dimensional_registry`. Its payload lives under the saved-data root `data` compound.

```text
data
  RiftDataVersion: int

  pocket_registry
    <dimension id>
      grid_size: int
      private_pocket_size: int
      public_pocket_size: int
      next_id_map
        <base3 size as string>: int
      pockets
        <pocket id as string>
          type: dimdoors:pocket | dimdoors:private_pocket | dimdoors:id_reference
          id: int
          world: dimension id
          range: int                         // real pockets only
          box: [minX, minY, minZ, maxX, maxY, maxZ]
          virtualLocation                    // real pockets only
            world: dimension id
            x: int
            z: int
            depth: int
          addons: list
          referenced_id: int                 // id_reference only

  private_registry
    private_pocket_map
      <player uuid string>
        world: dimension id
        id: int

  rift_registry
    rifts: list
      type: dimdoors:rift | dimdoors:rift_placeholder
      id: uuid
      location
        world: dimension id                  // rift only
        pos: [x, y, z]                       // rift only
      isDetached: boolean                    // rift only
      properties                             // optional
        floatingWeight: float
        entranceWeight: float
        groups: int array
        linksRemaining: int
        oneWay: boolean

    pockets: list
      type: dimdoors:entrance
      id: uuid
      pocketDim: dimension id
      pocketId: int

    links: list
      from: uuid
      to: uuid

    last_private_pocket_entrances: list
      player: uuid
      rift: uuid

    last_private_pocket_exits: list
      player: uuid
      rift: uuid

    overworld_rifts: list
      player: uuid
      rift: uuid

    overworld_locations: list
      player: uuid
      location
        world: dimension id
        pos: [x, y, z]
```

Old runtime reconstruction order:

```text
raw saved-data compound
  -> version check / migration
  -> decode pocket_registry
  -> load private_registry
  -> load rift_registry with pocket_registry available
```

`rift_registry` depends on the already-loaded `pocket_registry` so pocket entrance pointers can resolve `pocketDim` + `pocketId` to actual pocket objects while rebuilding runtime indexes.
