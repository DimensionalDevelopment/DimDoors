# Rift Registry Split Plan

This is a code-level map for splitting the current broken `RiftRegistry` into subsystem-owned state plus `RiftGraph`.

The old class currently contains four different ownership domains:

```text
RiftRegistry
  graph                                      // shared UUID topology
  locationMap                               // rift subsystem state
  pocketEntranceMap                         // pocket registry entrance state
  uuidMap                                   // old global vertex resolver
  lastPrivatePocketEntrances / Exits        // private registry player state
  overworldRifts / overworldLocations       // separate player/overworld state
```

The split should make each subsystem own its own vertices, then let `RiftGraph` connect those vertices by UUID.

## Current Broken-State Constraint

The current `RiftRegistry` declares:

```java
protected Graph<UUID, Edge> graph
```

But many method bodies still use it like:

```java
this.graph.addVertex(pointer);
this.graph.addEdge(pointer, riftVertex);
this.graph.incomingEdgesOf(rift);
```

That old object-graph behavior should not be preserved. The intended graph model is UUID-only.

## `RiftGraph`

`RiftGraph` owns only topology.

### State

```java
Graph<UUID, DefaultEdge> graph;
```

Do not store:

```java
Map<UUID, RegistryVertex>
Map<Location, Rift>
Map<PocketInfo, PocketEntrancePointer>
Map<UUID, PlayerRiftPointer>
```

### Core API

```java
addVertex(UUID id)
removeVertex(UUID id)
containsVertex(UUID id)

addEdge(UUID from, UUID to)
removeEdge(UUID from, UUID to)
containsEdge(UUID from, UUID to)

Set<UUID> targets(UUID source)
Set<UUID> sources(UUID target)

UUID followPointer(UUID pointer)
Set<Edge> edges()
void rebuild(Collection<? extends RegistryVertex> vertices, Collection<Edge> edges)
```

### Edge Meanings

```text
Rift UUID                  -> Rift UUID       normal rift link/reference
PocketEntrancePointer UUID -> Rift UUID       this pocket has this entrance rift
PlayerRiftPointer UUID     -> Rift UUID       this player pointer targets this rift
```

If private player tracking stops using `PlayerRiftPointer` and stores direct rift UUIDs in `PlayerRiftConnection`, then that last edge type disappears.

### Important Behavior

`RiftGraph.rebuild(vertices, edges)` should:

1. Clear existing graph state.
2. Add every collected subsystem vertex UUID.
3. Add only edges whose source and target UUIDs exist.
4. Drop or report edges with missing endpoints.

Do not let `RiftGraph` create domain vertices. It can add raw UUID vertices, but the authoritative vertex objects come from subsystems.

## `RiftRegsitry1`

`RiftRegsitry1` owns rift vertices only.

### State To Keep From Old `RiftRegistry`

Old:

```java
protected Map<Location, Rift> locationMap;
```

New:

```java
private final Map<Location, Rift> riftsByLocation;
private final Map<UUID, Rift> riftsById;
```

Only `Rift` and `RiftPlaceholder` belong in this subsystem.

### `collectVertices()`

Return:

```java
new ArrayList<>(riftsById.values())
```

This is how rifts enter the global graph rebuild.

### Methods That Stay Here

Move/adapt these old methods:

```java
isRiftAt(Location)
getRift(Location)
getRiftOrPlaceholder(Location)
moveRift(Location, Location)
moveRifts(Map<Location, Location>)
addRift(Location)
removeRift(Location)
setProperties(Location, LinkProperties)
addLink(Location, Location)
removeLink(Location, Location)
getRifts()
getTargets(Location)
getSources(Location)
```

### Placeholder Conversion Detail

Old code used object-graph replacement:

```java
rift.id = currentRift.id;
GraphUtils.replaceVertex(this.graph, currentRift, rift);
```

With UUID graph storage, do not replace graph vertices. Preserve the placeholder UUID:

```java
RiftPlaceholder old = ...
Rift rift = new Rift(location);
rift.setId(old.getId());
riftsById.put(rift.getId(), rift);
riftsByLocation.put(location, rift);
graph.addVertex(rift.getId()); // already exists, harmless if idempotent
```

All existing graph edges remain valid because they point at the UUID, not the object.

### `addLink`

Old behavior:

```java
Rift from = getRiftOrPlaceholder(locationFrom);
Rift to = getRiftOrPlaceholder(locationTo);
addEdge(from, to);
if both real rifts:
    from.targetAdded(to);
    to.sourceAdded(from);
```

New behavior:

```java
Rift from = riftRegistry.getOrCreatePlaceholder(locationFrom, graph);
Rift to = riftRegistry.getOrCreatePlaceholder(locationTo, graph);
graph.addEdge(from.getId(), to.getId());
dispatch targetAdded/sourceAdded if both are non-placeholder rifts;
```

### `removeLink`

Old behavior remains rift-owned:

```java
Rift from = getRift(locationFrom);
Rift to = getRift(locationTo);
graph.removeEdge(from.getId(), to.getId());
from.targetGone(to);
to.sourceGone(from);
```

### `moveRifts`

Keep the old validation:

```java
ignore old == new
reject duplicate new locations
reject moving into an occupied location unless that location is also being moved away
```

Then:

```java
remove old locations from riftsByLocation
update each Rift world/location
put new locations into riftsByLocation
```

Graph edges do not change because UUIDs do not change.

Event dispatch needs the cross-subsystem resolver if adjacent vertices might belong to other subsystems. If this method only handles rift-to-rift links, it can dispatch only to resolved `Rift` vertices.

### Do Not Keep Here

Remove these concepts from the rift subsystem:

```java
pocketEntranceMap
PocketEntrancePointer
PlayerRiftPointer
lastPrivatePocketEntrances
lastPrivatePocketExits
overworldRifts
overworldLocations
readPlayerRiftPointers
writePlayerRiftPointers
readLocations
writeLocations
getPrivatePocketEntrance
getPrivatePocketExit
setLastPrivatePocketEntrance
setLastPrivatePocketExit
getOverworldRift
setOverworldRift
```

## `PocketRegistry`

`PocketRegistry` owns pocket entrance vertices.

The current file already owns pocket directories. It should also own the pointer vertices that describe entrances into pockets.

### State To Move Here

Old:

```java
protected Map<Pocket<?, ?>, PocketEntrancePointer> pocketEntranceMap;
```

New preferred shape:

```java
private final Map<PocketInfo, PocketEntrancePointer> pocketEntrancePointers;
```

Use:

```java
new PocketInfo(pocket.getWorld(), pocket.getId())
```

Reason: `PocketEntrancePointer` persists `world + pocketId`, not object identity. `PocketInfo` matches that durable identity.

### `collectVertices()`

Current:

```java
return List.of();
```

Change to:

```java
return new ArrayList<>(pocketEntrancePointers.values());
```

### NBT / Codec Ownership

Old `RiftRegistry.fromNbt` reads pocket pointers from:

```text
rift_registry.pockets
```

That data should decode into `PocketRegistry`.

Each `PocketEntrancePointer` contains:

```java
UUID id
ResourceKey<Level> pocketDim
int pocketId
```

So `PocketRegistry` can rebuild:

```java
PocketEntrancePointer pointer = PocketEntrancePointer.fromNbt(tag);
PocketInfo info = new PocketInfo(pointer.getWorld(), pointer.getPocketId());
pocketEntrancePointers.put(info, pointer);
```

Do not require the live `Pocket` object just to index the pointer. Live pocket lookup is useful for validation, not for storing the pointer.

### `addPocketEntrance`

Move old method:

```java
public void addPocketEntrance(Pocket pocket, Location location)
```

New shape should take graph/rift access explicitly:

```java
public void addPocketEntrance(Pocket<?, ?> pocket, Location location, RiftRegsitry1 rifts, RiftGraph graph)
```

Behavior:

```java
PocketInfo info = new PocketInfo(pocket.getWorld(), pocket.getId());

PocketEntrancePointer pointer = pocketEntrancePointers.computeIfAbsent(info, key -> {
    PocketEntrancePointer created = new PocketEntrancePointer(key.world(), key.id());
    graph.addVertex(created.getId());
    return created;
});

Rift rift = rifts.getRift(location);
graph.addEdge(pointer.getId(), rift.getId());
setDirty();
```

This creates graph edge:

```text
PocketEntrancePointer -> Rift
```

### `getPocketEntrances`

Move old method:

```java
public Set<Location> getPocketEntrances(Pocket pocket)
```

New shape:

```java
public Set<Location> getPocketEntrances(PocketInfo info, RiftRegsitry1 rifts, RiftGraph graph)
```

Behavior:

```java
PocketEntrancePointer pointer = pocketEntrancePointers.get(info);
if (pointer == null) return Set.of();

return graph.targets(pointer.getId()).stream()
    .map(rifts::resolve)
    .flatMap(Optional::stream)
    .filter(rift -> !(rift instanceof RiftPlaceholder))
    .map(Rift::getLocation)
    .collect(toSet());
```

Provide overloads:

```java
getPocketEntrances(Pocket<?, ?> pocket, ...)
getPocketEntrance(Pocket<?, ?> pocket, ...)
```

### `getPocketEntrance`

Move old first-entrance helper:

```java
return getPocketEntrances(...).stream().findFirst().orElse(null);
```

Keep behavior simple. Selection policy can be improved later.

### `removePocketReferences`

Move old methods:

```java
removePocketReferences(Pocket pocket)
removePocketReferences(ResourceKey<Level> world, int pocketId)
referencesPocket(...)
```

New behavior:

```java
PocketInfo info = new PocketInfo(world, pocketId);
PocketEntrancePointer pointer = pocketEntrancePointers.remove(info);
if (pointer == null) return false;

Set<UUID> affected = new LinkedHashSet<>();
affected.addAll(graph.sources(pointer.getId()));
affected.addAll(graph.targets(pointer.getId()));

graph.removeVertex(pointer.getId());

for each affected UUID:
    resolve through cross-subsystem resolver
    if vertex is Rift: markDirty/notify as needed

setDirty();
return true;
```

The old method also scanned graph vertices for stale `PocketEntrancePointer`s matching the same pocket. With subsystem ownership, that scan should become unnecessary if `PocketRegistry` is authoritative. During migration, keep a cleanup path if duplicate/stale pointers are possible.

## `PrivateRegistry`

`PrivateRegistry` owns private pocket assignment and private-pocket player entrance/exit memory.

It already extends:

```java
PlayerTrackingSubSystem<PrivateRegistry>
```

And `PlayerTrackingSubSystem.PlayerRiftConnection` already has:

```java
UUID entrance;
UUID exit;
```

Use those fields for old private-pocket tracking.

### State To Move Here

Old implied fields:

```java
Map<UUID, PlayerRiftPointer> lastPrivatePocketEntrances;
Map<UUID, PlayerRiftPointer> lastPrivatePocketExits;
```

New direct UUID shape:

```java
locations.get(playerUUID).getEntrance()
locations.get(playerUUID).getExit()
```

These UUIDs should point to rift vertex UUIDs.

### `getPrivatePocketEntrance`

Old behavior:

```java
Pocket pocket = getPrivatePocket(playerUUID);
if (pocket == null) return null;

Rift entrance = getPointedRift(lastPrivatePocketEntrances.get(playerUUID));
if (entrance != null && getPocketEntrances(pocket).contains(entrance.getLocation())) {
    return entrance.getLocation();
}

return getPocketEntrance(pocket);
```

New location:

```java
PrivateRegistry.getPrivatePocketEntrance(UUID playerUUID, PocketRegistry pockets, RiftRegsitry1 rifts, RiftGraph graph)
```

Behavior:

```java
PrivatePocket pocket = getPrivatePocket(playerUUID);
if (pocket == null) return null;

PlayerRiftConnection connection = locations.get(playerUUID);
UUID entranceId = connection == null ? null : connection.getEntrance();

Rift entrance = rifts.resolve(entranceId).orElse(null);
if (entrance != null && pockets.getPocketEntrances(pocket, rifts, graph).contains(entrance.getLocation())) {
    return entrance.getLocation();
}

return pockets.getPocketEntrance(pocket, rifts, graph);
```

The important part: validation still depends on `PocketRegistry` because only `PocketRegistry` knows which rifts are entrances for that pocket.

### `setLastPrivatePocketEntrance`

Old behavior created a `PlayerRiftPointer` and an edge to the target rift.

If using direct UUIDs now:

```java
public void setLastPrivatePocketEntrance(UUID playerUUID, Location location, RiftRegsitry1 rifts)
```

Behavior:

```java
PlayerRiftConnection connection = locations.computeIfAbsent(playerUUID, uuid -> new PlayerRiftConnection());
connection.setEntrance(location == null ? null : rifts.getRift(location).getId());
setDirty();
```

If missing rifts should still create placeholders, call:

```java
rifts.getOrCreatePlaceholder(location, graph).getId()
```

That requires passing `RiftGraph` too.

### `getPrivatePocketExit`

Old behavior:

```java
Rift entrance = getPointedRift(lastPrivatePocketExits.get(playerUUID));
return entrance != null ? entrance.getLocation() : null;
```

New behavior:

```java
PlayerRiftConnection connection = locations.get(playerUUID);
if (connection == null) return null;

return rifts.resolve(connection.getExit())
    .map(Rift::getLocation)
    .orElse(null);
```

### `setLastPrivatePocketExit`

Direct UUID version:

```java
PlayerRiftConnection connection = locations.computeIfAbsent(playerUUID, uuid -> new PlayerRiftConnection());
connection.setExit(location == null ? null : rifts.getRift(location).getId());
setDirty();
```

Use placeholder creation only if exits can intentionally point at not-yet-existing rifts.

### `collectVertices()`

If private tracking uses direct rift UUIDs in `PlayerRiftConnection`, then `PrivateRegistry.collectVertices()` does not need to return `PlayerRiftPointer` for private entrance/exit memory.

If `PlayerRiftPointer` remains required for graph semantics, then `PrivateRegistry` must own those pointer vertices and return them from `collectVertices()`.

The current `PlayerRiftConnection` suggests direct UUID storage is the intended simpler path.

## `PlayerRiftLocationRegistry`

Old `overworldRifts` and `overworldLocations` should not move to `PrivateRegistry`.

Old state:

```java
Map<UUID, PlayerRiftPointer> overworldRifts;
Map<UUID, Location> overworldLocations;
```

New owner should be a player/overworld tracking subsystem.

Likely responsibilities:

```java
getOverworldRift(UUID player)
setOverworldRift(UUID player, Location location)
getOverworldLocation(UUID player)
setOverworldLocation(UUID player, Location location)
```

During `RiftRegsitry1.moveRifts`, old behavior updated:

```java
overworldLocations.replaceAll((uuid, loc) -> movements.getOrDefault(loc, loc));
```

After the split, that movement update cannot live inside `RiftRegsitry1` unless it is expressed as an event:

```text
rifts moved old->new
  -> player/overworld subsystem updates stored locations
```

## Cross-Subsystem Resolver

Old `uuidMap` must become a runtime resolver.

Current old behavior:

```java
uuidMap.get(id)
```

New build:

```java
Map<UUID, RegistryVertex> vertices = new LinkedHashMap<>();

for (SubSystem<?> subsystem : subsystems) {
    for (RegistryVertex vertex : subsystem.collectVertices()) {
        vertices.put(vertex.getId(), vertex);
    }
}
```

This resolver is required by:

```java
removeRift
moveRifts
removePocketReferences
graph edge validation
event dispatch
```

Event dispatch example:

```java
for (UUID sourceId : graph.sources(removedRift.getId())) {
    RegistryVertex source = resolver.get(sourceId);
    if (source != null) {
        source.targetGone(removedRift);
    }
}
```

This replaces old direct object graph calls:

```java
this.graph.getEdgeSource(edge).targetGone(rift)
```

## Migration From Old NBT

Old shape:

```text
rift_registry
  rifts
  pockets
  links
  last_private_pocket_entrances
  last_private_pocket_exits
  overworld_rifts
  overworld_locations
```

New target:

```text
rift_registry1
  rifts

pocket_registry
  directories
  entrance_pointers

private_registry
  private_pockets
  locations
    player_uuid
      entranceId
      exitId

rift_graph
  edges

player_rift_location_registry
  overworld state
```

Migration mapping:

```text
old rifts                          -> RiftRegsitry1
old pockets                        -> PocketRegistry.entrance_pointers
old links                          -> RiftGraph.edges
old last_private_pocket_entrances  -> PrivateRegistry.locations[player].entrance
old last_private_pocket_exits      -> PrivateRegistry.locations[player].exit
old overworld_rifts                -> PlayerRiftLocationRegistry
old overworld_locations            -> PlayerRiftLocationRegistry
```

## Detailed Implementation Order

1. Finish `RiftGraph` UUID topology.
   - Keep only UUID graph state.
   - Keep edge codec.
   - Add rebuild from collected subsystem vertices.

2. Finish `RiftRegsitry1`.
   - Keep only rift fields.
   - Return rifts/placeholders from `collectVertices()`.
   - Preserve UUID on placeholder conversion.
   - Remove any pocket/player assumptions from its API.

3. Extend `PocketRegistry`.
   - Add `Map<PocketInfo, PocketEntrancePointer>`.
   - Move pocket entrance methods.
   - Return entrance pointers from `collectVertices()`.
   - Make entrance methods use `RiftGraph` + `RiftRegsitry1`.

4. Extend `PrivateRegistry`.
   - Move private entrance/exit methods.
   - Store rift UUIDs in `PlayerRiftConnection`.
   - Use `PocketRegistry` to validate selected private pocket entrance.
   - Use `RiftRegsitry1` to resolve UUIDs to rift locations.

5. Decide `PlayerRiftPointer` fate.
   - If direct UUIDs are accepted, private entrance/exit tracking does not need pointer vertices.
   - If graph-visible player pointers are required, `PrivateRegistry` owns those pointer vertices.

6. Move overworld tracking separately.
   - Do not put it in `PrivateRegistry`.
   - Give it a player/overworld subsystem owner.
   - Handle rift movement through an event/update hook.

7. Add runtime resolver.
   - Build `UUID -> RegistryVertex` from every subsystem.
   - Use it for graph event dispatch and edge validation.

8. Migrate old NBT.
   - Split old `rift_registry` payload into the new owners.
   - Preserve UUIDs.
   - Drop edges whose endpoints cannot be resolved after subsystem load.

9. Update callers last.
   - Rift block entity calls go to `RiftRegsitry1`.
   - Pocket entrance callers go to `PocketRegistry`.
   - Private pocket entrance/exit callers go to `PrivateRegistry`.
   - Shared adjacency behavior uses `RiftGraph`.

