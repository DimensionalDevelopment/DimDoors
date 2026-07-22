# DimDoors × Immersive Portals Compatibility (Forge 1.20.1)

This document explains the two features added to this fork of DimDoors
5.4.4 for Minecraft 1.20.1, how they were designed, and where the code
lives:

1. **Optional Immersive Portals integration** — mutually linked
   dimensional doors become genuine see-through portals, and their
   open/close state is synchronized across the link.
2. **The "camachange" portal visuals** — the classic swirly portal
   surface is slightly transparent and has real 3D depth (used whenever
   Immersive Portals is not installed or a door is not bridged).

---

## 1. The problem

DimDoors' doors are not "portals" in the engine sense. The doorway is an
opaque animated surface drawn by a block-entity renderer, and
teleportation is done by custom math: `DimensionalDoorBlock` checks every
tick whether an entity's movement segment crossed an invisible plane
inside the doorway (0.31 blocks behind the block center) and, if so,
teleports it via the rift system.

Immersive Portals (IP), on the other hand, renders and teleports through
real `Portal` *entities* — arbitrary rectangles in space that show the
destination and move entities seamlessly across dimensions.

The goal: when two dimensional doors are linked *to each other*, place an
IP portal exactly on DimDoors' teleport plane on both sides so you can
see through the doorway — while the doors still open, close, and sync
with each other — and without breaking anything when IP is absent.

## 2. Architecture: a bridge interface with a no-op default

DimDoors is an Architectury multiplatform project (`common` + `fabric` +
`forge`), and IP for 1.20.1 is Forge-only. Common code therefore cannot
reference IP classes. The integration uses a tiny service interface:

- **`common/.../compat/DoorPortalBridge.java`** — four hooks, all with
  do-nothing defaults, plus a static holder (`DoorPortalBridge.get()` /
  `set()`). Without IP, the `NONE` implementation is used everywhere and
  the mod behaves exactly as before, on both loaders.
- **`forge/.../compat/immersiveportals/ImmersivePortalsCompat.java`** —
  called from the Forge mod constructor. If (and only if)
  `imm_ptl_core` is loaded, it installs the real implementation. All IP
  imports live in the implementation class, so the JVM never loads IP
  types when the mod is absent.
- **`forge/.../compat/immersiveportals/ImmersivePortalsDoorBridge.java`**
  — the actual integration (~200 lines, described below).

### Hook call sites (common code)

| Hook | Called from | Purpose |
|---|---|---|
| `onDoorStateChanged` | `DimensionalDoorBlock.use()` (hand), `neighborChanged` (redstone, fires only on a real OPEN transition) | create/remove portals, sync the far door |
| `onDoorRemoved` | `DimensionalDoorBlock.onRemove()` (only when the block actually changes, not on state toggles) | clean up portals when a door is broken |
| `handlesTeleport` | `DimensionalDoorBlock.onCollision()` | suppress DimDoors' plane-crossing teleport while an IP portal covers the doorway, so entities aren't teleported twice |
| `suppressesRiftRendering` | `EntranceRiftBlockEntityRenderer.render()` | skip drawing the swirly surface when you can literally see through the door instead |

## 3. When does a door get a see-through portal?

Whenever its destination can be **resolved to a fixed location** at the
moment the door is opened. The resolver understands:

- **`RiftReference`** (local/global/relative rift-to-rift links) — the
  plain "this door leads there" case.
- **`WrappedDestinationTarget`** (e.g. public pocket doors) — once the
  pocket has been generated (first walk-through), the wrapped target is a
  concrete `RiftReference` and resolves like one. The very first entry
  stays classic, because the pocket literally does not exist yet.
- **`PrivatePocketTarget` / `PrivatePocketExitTarget`** (personal
  pockets) — these depend on *who* is asking, so they are resolved using
  the player who opened the door (via the rift registry's per-player
  entrance/exit pointers). The resulting portal is bound to that player
  with IP's `specificPlayerId`: IP only syncs such portals to their
  owner, so other players see the classic swirl and get DimDoors'
  classic per-player teleport. Redstone can't open a personal-pocket
  portal (there is no player to resolve against).

What kind of portal is spawned depends on the link topology:

- **Mutual link** (this door's rift references the far door and the far
  door's rift references back — DimDoors' "green link"): the full
  four-portal bi-directional, double-faced cluster, plus door open/close
  synchronization.
- **Anything else that resolves** (pocket doors, a door leading to a
  detached rift, any one-way reference): a **one-way portal pair** — the
  doorway is see-through and traversable from both of its faces, but
  there is no return portal on the far side, exactly mirroring DimDoors'
  one-way teleport semantics. If the far side is a door it is forced
  open (so the exit isn't blocked); if it is not a door (e.g. the
  floating rift of a gateway), the exit point is offset one block clear
  of the target so the arriving player doesn't immediately re-trigger
  the rift.

Destinations that cannot be statically resolved (dungeon `RandomTarget`
before first use, `EscapeTarget`, unlinked doors, ...) keep the classic
behavior untouched.

A re-entrancy guard prevents cascades: when the bridge itself force-opens
a far door, that door's own state-change hook is suppressed, so opening a
door can never recursively open a chain of third doors.

## 4. Placing the portal: geometry

Everything is anchored to constants that already exist in DimDoors:

- The teleport plane sits `0.31` blocks behind the block center, along
  `FACING.getOpposite()` (see `portalOffsetFromCenter` in
  `DimensionalDoorBlock.onCollision` and the translations in
  `DefaultTransformation`). The IP portal is placed on exactly that
  plane: `bottom-center + opposite(facing) × 0.31 + (0, 1, 0)` — a
  1 × 2 rectangle centered mid-doorway.
- Orientation uses IP's axis convention (`axisW × axisH = normal`):
  `axisH = up`, `axisW = up × facing`, so the portal's front face is the
  door's front face.
- The cross-dimension transform must match DimDoors' own teleport
  convention. `EntranceRiftBlockEntity.receiveEntity` places and pushes
  the arriving entity along `getOrientation().getOpposite()` — **you
  always come out of the BACK of the destination door.** So the rotation
  sends `-facingA → -facingB` (walk into A's front, emerge from B's
  back, with up staying up), expressed by giving the far side the basis
  `axisW' = up × facingB`, `axisH' = up`, `normal' = facingB` via
  `DQuaternion.matrixToQuaternion(...)` +
  `Portal.setOtherSideOrientation(...)`. (The first version of this
  compat mapped front→front, which made some door pairs feel "flipped"
  compared to walking through them classically.)

A doorway is visible and enterable from **both** faces and must work in
**both** directions, so one `Portal` entity isn't enough. After spawning
the primary portal, `PortalManipulation.completeBiWayBiFacedPortal(...)`
builds the standard IP four-portal cluster: front and back faces on the
source side, and the two reverse portals on the destination side.

Every portal in the cluster carries `portalTag = "dimdoors:door_portal"`
(the tag is propagated by IP's `copyAdditionalProperties`), so the
bridge can always re-find "our" portals by searching entities in a small
box around a doorway — no persistent bookkeeping needed, which also
makes cleanup robust across server restarts (IP portals are ordinary
entities and save with the world, exactly like the door state they
mirror).

## 4b. Persistent portals (config)

`doors.persistentImmersivePortals` (default **off**, in the standard
DimDoors config screen): when enabled, closing a bridged door does *not*
remove its portals. Instead every portal in the cluster is made
non-teleportable (`Portal.setTeleportable(false)` + client resync) and
stays in place, so the destination remains visible through the door's
window cutout while the closed door physically blocks passage. Opening
the door re-enables traversal. Breaking a door always removes its
portals regardless of this setting. Portals (and their teleportable
flag) are ordinary entities, so the state survives server restarts.

## 5. Open/close synchronization

"Open one side, both open; close one side, both close":

- **Open:** after the mutual-link check, the bridge spawns the portal
  cluster *first*, then opens the far door with vanilla
  `DoorBlock.setOpen` (which plays the right sound/game event). Ordering
  matters: any re-entrant hook fired by the far door's state change sees
  the portals already in place and does nothing, so exactly one cluster
  is ever created.
- **Close:** remove all tagged portals on both sides, then close the far
  door. The handler is idempotent — the far door's own hook finds no
  portals left and no state to change, so there is no recursion.
- **Break:** `onRemove` kills the cluster near the broken door.

Redstone works because the `neighborChanged` override compares the OPEN
value before/after vanilla processing and only fires the hook on a real
transition.

## 6. The camachange visuals (no-IP flair)

Recovered from the earlier `5.4.4ip` source tree and merged in as three
files under `common/`:

- `assets/minecraft/shaders/core/dimensional_portal.fsh` — fragment
  alpha `1.0 → 0.75`.
- `api/client/DimensionalPortalRenderer.java` — the render type now uses
  the mod's own `dimensional_portal` shader (so the 0.75 alpha actually
  applies) with `TRANSLUCENT_TRANSPARENCY`, sorted uploads, and
  color-only depth-mask writes; the portal `ModelPart.Cube` depth went
  from `0.01` to `1.0` blocks — a real volume instead of a flat quad.
- `api/client/DefaultTransformation.java` — the four door-facing
  transforms retuned so the deeper box seats correctly in the frame.

When IP is installed **and** a door is bridged, this rendering is
skipped for that door (`suppressesRiftRendering`); everywhere else — no
IP installed, unlinked doors, dungeon doors, floating rifts — the
translucent 3D effect shows.

## 7. Build & dependency setup

- `forge/build.gradle`: IP is a `modCompileOnly` dependency —
  `maven.modrinth:immersive-portals-neoforge:${immersive_portals_version}`
  (3.0.7, the Forge 1.20.1 build, served from the Modrinth maven, which
  is scoped with a `content` filter to the `maven.modrinth` group). A
  commented `modRuntimeOnly` line can be enabled to test IP inside
  `:forge:runClient`.
- `gradle.properties`: `immersive_portals_version=3.0.7`.
- `forge/src/main/resources/META-INF/mods.toml`: optional
  (`mandatory = false`) dependency on `imm_ptl_core`, ordered `AFTER`.
- Unrelated build fix: ModMenu (fabric) now resolves from the Modrinth
  maven instead of the frequently-timing-out Terraformers maven.

## 8. Testing & tuning

Drop `forge/build/libs/dimdoors-<version>-1.20.1-forge.jar` and
Immersive Portals for Forge 3.0.7 into a Forge 47.x instance, link two
doors both ways (rift configuration tools), and open one.

If a view ever looks offset or mirrored, the two knobs are both in
`ImmersivePortalsDoorBridge`:

- `PORTAL_OFFSET_FROM_CENTER` (plane depth in the doorway, default 0.31
  to match DimDoors), and
- the `setOtherSideOrientation(...)` basis (exit rotation).

Removing the mod jar (or IP) at any time is safe: without IP the bridge
is never installed, and stale portal entities can't exist because they
are only ever spawned while IP is present.
