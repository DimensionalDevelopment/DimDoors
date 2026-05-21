# Entity Teleportation, Passengers, Movement, And Sub-Levels

These notes describe how vanilla Minecraft 1.21.1 entity teleportation and movement behave, how Sable-style sub-levels alter the assumptions, and how to reason about teleporting regular entities, players, passengers, and minecarts.

This document intentionally only discusses vanilla Minecraft and Sable dependency behavior. It does not rely on or reference mod code under packages outside those systems.

## Terminology

- **Global space**: normal visible level coordinates after a sub-level pose is applied.
- **Local or plot space**: the raw coordinates inside the sub-level's stored plot chunks.
- **Sub-level pose**: the transform from local/plot space to global space. It can include translation and rotation, and changes over time.
- **Containing sub-level**: the sub-level whose plot contains a raw block/entity coordinate.
- **Tracking sub-level**: the moving sub-level an entity is standing on or attached to, even if the entity is not stored inside that sub-level plot.
- **Kick out / project out**: convert an entity position, velocity, and sometimes look direction from local sub-level space to global world space.

## Vanilla Assumptions

Vanilla entity systems assume a single coordinate frame per `Level`.

- An entity position is absolute in the level.
- Entity bounding boxes are axis-aligned in the level.
- Collision uses world, entity, block, and border shapes in that same frame.
- Passengers and vehicles share the same coordinate frame.
- A rail block found at an entity's `BlockPos` really is the rail the minecart should use.
- Teleport placement is mostly direct position assignment, not movement collision.

Sub-levels violate these assumptions because the visible position of a block/entity can differ from the raw position used for storage.

## Vanilla Movement

Normal entity movement goes through `Entity.move(MoverType, Vec3)`.

The vanilla shape is:

1. If `noPhysics`, directly add motion to position and return.
2. Apply piston or stuck-in-block adjustments.
3. Call `collide(motion)`.
4. Move by the clipped vector.
5. Derive horizontal and vertical collision flags by comparing requested motion with actual motion.
6. Update ground state, fall damage, block landing behavior, movement distance, sounds, and related effects.

`collide(motion)` collects entity collisions first, then calls `collideBoundingBox`. That adds block collisions and world-border collision, then clips the motion axis by axis. Vanilla step-up behavior is also handled there.

## Sable Movement And Collision

Sable wraps vanilla movement rather than replacing the whole entity pipeline.

The important model:

1. Sable first resolves collision between the entity and candidate sub-levels.
2. It considers the entity's bounding box swept through the requested motion.
3. It queries sub-levels whose posed bounds intersect the movement context.
4. For each candidate sub-level, it transforms bounds into local space, iterates local blocks, transforms block collision boxes back through the sub-level pose, and performs oriented-box collision checks.
5. It may set or clear the entity's tracking sub-level.
6. It computes inherited motion from the sub-level pose delta.
7. The resulting motion is still passed through vanilla collision.

That means a normal `move` call can be made sub-level aware, but a direct teleport usually bypasses the movement collision path. Teleports must handle coordinate transforms and destination validation explicitly.

## Vanilla Teleport Forms

There are several related vanilla paths.

### Same-Level Regular Entity

Regular entity teleport in the same level is mostly:

1. Clamp pitch if needed.
2. `moveTo(x, y, z, yaw, pitch)` or `teleportTo(x, y, z)`.
3. Reposition passengers.

There is no client acknowledgement for regular entities. The server moves the entity, and entity tracking later sends position packets.

### Cross-Level Regular Entity

Cross-level regular entity teleport may:

1. Unride or detach passenger/vehicle state.
2. Create a new entity of the same type in the target level.
3. Copy old entity data.
4. Move the new entity to the destination.
5. Mark the old entity as changed dimension.
6. Add the new entity during teleport.

The fuller dimension-change path handles passengers by changing them too, then reattaching them to the changed root.

### Server Player Teleport

Server player teleport is special:

1. Add a post-teleport chunk ticket.
2. Stop riding.
3. If sleeping, stop sleeping.
4. If same dimension, use the player's network connection to teleport.
5. The connection sets an awaiting teleport id and sends `ClientboundPlayerPositionPacket`.
6. The server waits for a matching client acknowledgement.
7. Movement packets are validated against the server's expected position.

This is why players are much more sensitive to local/global frame mismatches than normal entities.

### Relative Teleport

Relative movement flags mean relative to vanilla world coordinates and vanilla yaw/pitch. They do not mean relative to a sub-level's local axes. On a rotated sub-level, a relative `X` adjustment is world X unless transformed intentionally.

## Core Problem With Sub-Levels

Every teleport target must answer one question first:

> Is this destination expressed in global space, or in sub-level local/plot space?

Vanilla cannot infer this.

If local coordinates are sent to vanilla as global coordinates, the entity appears in the hidden plot area. If global coordinates are sent while the entity is supposed to stay inside a sub-level, vanilla local block logic, rails, and plot containment can break.

## General Teleport Rules

Use this checklist before any entity teleport that might touch a sub-level.

1. Determine the source frame: current entity global space, source sub-level local space, or raw plot space.
2. Determine the target frame: normal world global space, target sub-level local space, or visible global point on a target sub-level.
3. Convert the position exactly once.
4. Convert velocity and look direction if the entity is being projected into or out of a sub-level.
5. Decide tracking state explicitly.
6. Validate collision in the target frame before placement.
7. Move the entity.
8. Reposition passengers consistently.
9. For players, ensure server position, sent packet position, and client movement packet interpretation agree.

## Regular Entity Teleport

Regular entities are easier than players because there is no teleport acknowledgement loop. They are still frame-sensitive.

### Teleport To Normal World Space

Use when the entity should leave sub-level behavior.

1. If the entity is currently local to a sub-level, project position out through the sub-level pose.
2. Transform velocity through the pose normal/rotation.
3. Clear tracking sub-level.
4. Place globally.
5. Reposition passengers globally.

### Teleport Onto A Sub-Level Surface

Use when the destination is a visible global point and the entity should stand on or move with the sub-level.

1. Keep final stored entity position global.
2. Validate the target against posed sub-level collision.
3. Set tracking sub-level to the target sub-level.
4. Place the entity globally.
5. Let inherited motion come from tracking on subsequent ticks.

Do not store the entity in raw plot space unless it is intentionally inside the sub-level.

### Teleport Into A Sub-Level Plot

Use when the entity should become part of the sub-level's local world, such as a retained local entity.

1. Convert requested visible/global target to target sub-level local coordinates.
2. Move entity to local/plot position.
3. Ensure containing sub-level resolves to the target.
4. Usually clear tracking state, because being contained and tracking are separate states.
5. Convert velocity and look direction into local frame if the entity should continue naturally in local space.

## Player Teleport

Players need all regular entity rules plus packet consistency.

Potential failure modes:

- Server stores global position but client sends local movement back.
- Server sends local packet position but later validates global coordinates.
- Awaiting teleport id is acknowledged for a position in the wrong frame.
- Movement checks see a huge jump and rubber-band the player.
- Camera interpolation uses old and new positions in different frames.

Rules:

1. Choose the authoritative server frame.
2. Choose the packet frame the client should see.
3. Transform outgoing `ClientboundPlayerPositionPacket` if the player is tracking or inside a sub-level.
4. Transform incoming `ServerboundMovePlayerPacket` back before vanilla validation.
5. Clear or set tracking before movement validation uses the new coordinates.
6. Be careful with relative flags, because they are vanilla-world-relative unless transformed.

## Passenger And Vehicle Teleport

Vanilla vehicle state is a tree:

- Passenger stores `vehicle`.
- Vehicle stores immutable `passengers`.
- `startRiding` prevents cycles and attaches the passenger.
- `rideTick` ticks the passenger, then asks the vehicle to `positionRider`.
- `positionRider` computes passenger riding position from attachment points.

Sub-level risks:

- Vehicle is local but passenger is interpreted as global.
- Passenger position is projected twice.
- Dismount position is computed in local space and used globally.
- Root vehicle teleports but passengers are not transformed consistently.
- Cross-level transitions detach and recreate entities, losing the intended sub-level frame unless explicitly restored.

Rules:

1. Treat the vehicle root as authoritative.
2. Transform the whole passenger tree in the same operation.
3. If the vehicle stays local to a sub-level, keep passengers in the matching local/posed relationship.
4. If the vehicle is projected out to global world space, project passengers out too.
5. Re-run or emulate `positionRider` after moving the root.
6. For player passengers, also satisfy player packet rules.

## Minecart-Specific Notes

Minecarts are harder than generic entities because they are vehicles and rail-locked physics entities.

Vanilla minecart tick logic assumes:

- The cart's `BlockPos` is in normal world coordinates.
- Rails are block states in that same coordinate frame.
- Rail shape lookup controls movement.
- Coming off rails is decided from the local block lookup.
- Passenger and dismount positions are in the same frame.

Sub-levels make this fragile because Sable-aware generic movement collision does not automatically make vanilla rail logic pose-aware.

### Cart Should Stay On Rails In A Sub-Level

Treat the cart as a local sub-level entity.

1. Identify the target sub-level.
2. Convert desired visible/global target to target sub-level local coordinates.
3. Verify a rail exists at or under the local target.
4. Move the cart to the local rail coordinate.
5. Align velocity to the local rail direction, or zero it if uncertain.
6. Keep or restore containment in the target sub-level plot.
7. Reposition passengers through the sub-level pose.

Do not plain-teleport the cart to visible global coordinates and expect vanilla rail logic to find rails. It will look at the global block position.

### Cart Should Leave The Sub-Level

Treat this as an ejection into normal world space.

1. Project cart position out through the sub-level pose.
2. Transform cart velocity through the pose orientation.
3. Clear local containment/tracking assumptions.
4. Place in global world space.
5. Expect it to be off rails unless there is a real global rail at the target.
6. Project passengers out with the cart.

### Cart To Another Sub-Level

This is a local-to-local transfer.

1. Convert source local position to source global position if needed.
2. Convert global target to destination local position.
3. Verify destination local rails.
4. Move cart into destination plot/local frame.
5. Convert velocity from source local/global into destination local rail direction.
6. Reattach/reposition passengers in the destination frame.

## Collision Validation Before Teleport

Teleport does not provide movement-style collision resolution. Validate first.

For normal global targets:

- Use vanilla `noCollision` or similar checks.
- Also test intersecting sub-level posed collision if the target overlaps any sub-level bounds.

For local sub-level targets:

- Query block states in the target sub-level plot.
- Validate against local collision shapes.
- If the sub-level is rotated, consider global posed shape only if the entity will be global/tracking rather than contained.

For standing-on-sub-level targets:

- Check the entity bounding box against posed sub-level block shapes.
- Set tracking only when there is a reliable supporting contact.

## Velocity And Rotation

Position alone is not enough.

When projecting local to global:

- Transform position with the sub-level pose.
- Transform velocity as a normal/vector through the pose orientation.
- Add sub-level point velocity if the entity should inherit moving-platform velocity.
- Transform look direction or recompute yaw/pitch from transformed look vector.

When projecting global to local:

- Transform position with inverse pose.
- Transform velocity with inverse orientation.
- Transform look direction with inverse orientation.

For relative teleports on rotated sub-levels, decide whether the relative delta is world-relative or local-relative. If local-relative, transform the delta by the sub-level pose before calling vanilla logic.

## Packet And Tracking Notes

Regular entities:

- Server entity tracking may send local coordinates while an entity is logically tracking a sub-level.
- Client handling must know whether a packet position is actually inside a sub-level or merely local-for-networking.
- Remote entity interpolation must not mix old global position with new local target.

Players:

- Outgoing teleport and incoming movement must use inverse transforms of each other.
- Movement validation must happen after packet coordinates are normalized to the server's authoritative frame.
- While tracking a moving sub-level, vanilla moved-too-quickly checks can misinterpret inherited sub-level motion as player cheating unless accounted for.

## Common Failure Signatures

- Entity appears in a far-away plot grid instead of the visible destination.
- Entity rubber-bands immediately after teleport.
- Player accepts teleport but next movement packet snaps them back.
- Entity falls through a visible moving platform.
- Entity suffocates in apparently empty space.
- Minecart teleports correctly visually for one tick, then comes off rails.
- Minecart vanishes after leaving its allowed plot.
- Passenger appears offset from vehicle, often by the sub-level transform.
- Rider dismounts into hidden plot space or inside transformed blocks.
- Entity moves with a sub-level after it should have left it, or fails to move with it after landing on it.

## Implementation Recipes

### Regular Entity To Global

```text
input: entity, globalTarget, yaw, pitch

sourceSubLevel = containing(entity) or tracking(entity)
if entity is local/contained:
    target = projectOut(sourceSubLevel, entity.localPosition)
else:
    target = globalTarget

clear tracking if leaving sub-level behavior
validate global target against vanilla and sub-level posed collision
moveTo(globalTarget, yaw, pitch)
teleport/reposition passengers in global frame
```

### Regular Entity Onto Sub-Level

```text
input: entity, targetSubLevel, visibleGlobalTarget, yaw, pitch

validate entity box at visibleGlobalTarget against targetSubLevel posed collision
set trackingSubLevel = targetSubLevel
moveTo(visibleGlobalTarget, yaw, pitch)
set old position if needed to avoid apparent lerp movement
reposition passengers
```

### Entity Into Sub-Level Local Space

```text
input: entity, targetSubLevel, visibleGlobalTarget or localTarget

if input is global:
    localTarget = inversePose(targetSubLevel, visibleGlobalTarget)

validate local target against plot block collision
clear trackingSubLevel
moveTo(localTarget)
transform velocity and look direction into local frame
reposition passengers in local/posed frame
```

### Minecart Staying On Sub-Level Rails

```text
input: cart, targetSubLevel, targetRailPosition

if targetRailPosition is global:
    localRailPosition = inversePose(targetSubLevel, targetRailPosition)
else:
    localRailPosition = targetRailPosition

assert rail block exists at or below localRailPosition
move cart to local rail coordinate
set velocity along local rail, or zero velocity
keep cart contained in targetSubLevel
reposition passenger tree through targetSubLevel pose
```

## Final Rule

Never call a vanilla teleport near sub-levels until the coordinate frame is known. Once the frame is known, convert exactly once, validate collision in the same frame the entity will occupy, update tracking/containment intentionally, and transform passengers with the root entity.
