package org.dimdev.dimdoors.compat.immersiveportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.compat.DoorPortalBridge;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.rift.targets.WrappedDestinationTarget;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.List;

/**
 * Immersive Portals backed implementation of {@link DoorPortalBridge}.
 *
 * Opening a dimensional door whose destination can be resolved to a fixed
 * location spawns a see-through portal in the doorway:
 * - mutually linked door pairs get the full bi-directional double-faced
 *   cluster, and their open/close state (and hinge) is synchronized;
 * - one-way destinations (public pocket doors, doors leading to detached
 *   rifts, ...) get a one-way portal, matching DimDoors' one-way teleport
 *   semantics;
 * - personal pockets are deliberately NOT bridged: their destination depends
 *   on which player walks through, so a world-visible portal would be wrong
 *   for everyone else on a multiplayer server. Those doors keep the classic
 *   behavior.
 *
 * Entering the front of a door always brings you out of the front of the
 * far door, like DimDoors' classic teleport. Note Immersive Portals'
 * otherSideOrientation convention: it describes the far-side frame as seen
 * looking BACK through the portal (an extra 180° flip around axisH is baked
 * into PortalManipulation.computeDeltaTransformation), so the correct input
 * is simply the far door's own doorway frame with its normal along the far
 * door's FACING.
 *
 * With the {@code persistentImmersivePortals} config enabled, closing a door
 * keeps its portals (view through the door's window cutout) but makes them
 * non-traversable until the door is opened again.
 *
 * Only loaded when Immersive Portals is present.
 */
public class ImmersivePortalsDoorBridge implements DoorPortalBridge {
	/** Tag applied to every portal of a door cluster so they can be found again. */
	private static final String PORTAL_TAG = "dimdoors:door_portal";
	/**
	 * The portal plane sits this far behind the block center, along the opposite
	 * of the door's facing. Matches the collision plane in DimensionalDoorBlock
	 * and the visual plane in DefaultTransformation.
	 */
	private static final double PORTAL_OFFSET_FROM_CENTER = 0.31;

	/** Guards against hook re-entry while this bridge itself toggles a door. */
	private boolean syncing;

	@Override
	public void onDoorStateChanged(Level level, BlockPos pos, BlockState state, @Nullable Player opener) {
		if (!(level instanceof ServerLevel world) || this.syncing) {
			return;
		}
		BlockPos bottom = bottomPos(pos, state);
		BlockState doorState = world.getBlockState(bottom);
		if (!(doorState.getBlock() instanceof DimensionalDoorBlock)) {
			return;
		}

		ResolvedDestination dest = resolveDestination(world, bottom);
		boolean persistent = DimensionalDoors.getConfig().getDoorsConfig().persistentImmersivePortals;

		if (doorState.getValue(DoorBlock.OPEN)) {
			// Always purge both endpoints before spawning: this replaces any
			// stale portals from an older link (e.g. a one-way portal whose
			// target rift has since been replaced by a door) instead of
			// stacking new portals on top of them.
			removeDoorPortals(world, bottom);
			if (dest == null) {
				return;
			}
			removeDoorPortals(dest.world(), dest.pos());
			spawnPortals(world, bottom, doorState, dest);
			if (dest.doorState() != null) {
				// The portal connects the doors front-to-front, so the far
				// door is seen from its other side through the portal — its
				// hinge must be MIRRORED for the two doors to overlap
				// seamlessly as one.
				DoorHingeSide mirroredHinge = doorState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT
						? DoorHingeSide.RIGHT
						: DoorHingeSide.LEFT;
				syncFarDoor(dest.world(), dest.pos(), true, mirroredHinge);
			}
		} else {
			if (persistent) {
				setTeleportable(findDoorPortals(world, bottom), false);
				if (dest != null) {
					setTeleportable(findDoorPortals(dest.world(), dest.pos()), false);
					if (dest.mutual()) {
						syncFarDoor(dest.world(), dest.pos(), false, null);
					}
				}
			} else {
				removeDoorPortals(world, bottom);
				if (dest != null) {
					removeDoorPortals(dest.world(), dest.pos());
					if (dest.mutual()) {
						syncFarDoor(dest.world(), dest.pos(), false, null);
					}
				}
			}
		}
	}

	@Override
	public void onDoorRemoved(Level level, BlockPos pos, BlockState state) {
		if (!(level instanceof ServerLevel world)) {
			return;
		}
		BlockPos bottom = bottomPos(pos, state);
		// the block entity still exists at this point, so the far side can be cleaned up too
		ResolvedDestination dest = resolveDestination(world, bottom);
		removeDoorPortals(world, bottom);
		if (dest != null) {
			removeDoorPortals(dest.world(), dest.pos());
		}
	}

	@Override
	public boolean handlesTeleport(Level level, BlockPos bottomPos, BlockState doorState, Entity entity) {
		if (!(level instanceof ServerLevel)) {
			return false;
		}
		for (Portal portal : findDoorPortals(level, bottomPos)) {
			if (portal.teleportable) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean suppressesRiftRendering(EntranceRiftBlockEntity rift) {
		Level level = rift.getLevel();
		return level != null && !findDoorPortals(level, rift.getBlockPos()).isEmpty();
	}

	// ------------------------------------------------------------------
	// destination resolution
	// ------------------------------------------------------------------

	/**
	 * @param doorState the far door's (lower half) state, or null when the far
	 *                  side is not a dimensional door (e.g. a detached rift)
	 */
	private record ResolvedDestination(ServerLevel world, BlockPos pos, @Nullable BlockState doorState,
									   boolean mutual) {
	}

	@Nullable
	private static ResolvedDestination resolveDestination(ServerLevel world, BlockPos bottom) {
		if (!(world.getBlockEntity(bottom) instanceof EntranceRiftBlockEntity rift)) {
			return null;
		}
		Location here = new Location(world, bottom);
		Location target = resolveTarget(rift.getDestination(), here);
		if (target == null || target.equals(here)) {
			return null;
		}
		ServerLevel farWorld = target.getWorld();
		if (farWorld == null) {
			return null;
		}
		BlockState farState = farWorld.getBlockState(target.pos);
		boolean farIsDoor = farState.getBlock() instanceof DimensionalDoorBlock
				&& farState.hasProperty(DoorBlock.HALF)
				&& farState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;

		boolean mutual = false;
		if (farIsDoor && farWorld.getBlockEntity(target.pos) instanceof EntranceRiftBlockEntity farRift) {
			Location back = resolveTarget(farRift.getDestination(), new Location(farWorld, target.pos));
			mutual = here.equals(back);
		}

		return new ResolvedDestination(farWorld, target.pos, farIsDoor ? farState : null, mutual);
	}

	/**
	 * Statically resolves a rift destination to a fixed location, or null if
	 * that is not possible. Player-dependent destinations (personal pockets)
	 * intentionally resolve to null: a world-visible portal would only be
	 * correct for one player.
	 */
	@Nullable
	private static Location resolveTarget(@Nullable VirtualTarget dest, Location here) {
		if (dest == null) {
			return null;
		}
		dest.setLocation(here);
		if (dest instanceof RiftReference reference) {
			return reference.getReferencedLocation();
		}
		if (dest instanceof WrappedDestinationTarget wrapped) {
			// e.g. a public pocket door that has already generated its pocket
			VirtualTarget inner = wrapped.getWrappedDestination();
			return inner != null ? resolveTarget(inner, here) : null;
		}
		return null;
	}

	// ------------------------------------------------------------------
	// portal creation / removal
	// ------------------------------------------------------------------

	private static void spawnPortals(ServerLevel world, BlockPos bottom, BlockState doorState, ResolvedDestination dest) {
		Direction facing = doorState.getValue(DoorBlock.FACING);
		Vec3 up = new Vec3(0, 1, 0);
		Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());

		Portal portal = IPRegistry.PORTAL.get().create(world);
		if (portal == null) {
			return;
		}

		portal.setOriginPos(portalPlaneCenter(bottom, facing));
		portal.setOrientationAndSize(up.cross(normal), up, 1.0, 2.0);
		portal.setDestinationDimension(dest.world().dimension());

		if (dest.doorState() != null) {
			Direction farFacing = dest.doorState().getValue(DoorBlock.FACING);
			portal.setDestination(portalPlaneCenter(dest.pos(), farFacing));
			// The far-side frame, rotated through DimDoors' own teleport
			// pipeline for exactness across all facing combinations, then
			// mirrored (axisW and normal negated) because otherSideOrientation
			// is specified as seen looking back through the portal. The result
			// is the far door's own doorway frame: front in, front out.
			CoordinateTransformerBlock srcTransformer = (CoordinateTransformerBlock) doorState.getBlock();
			CoordinateTransformerBlock farTransformer = (CoordinateTransformerBlock) dest.doorState().getBlock();
			Vec3 farAxisW = dimDoorsRotate(srcTransformer, doorState, bottom, farTransformer, dest.doorState(), dest.pos(), portal.axisW).scale(-1);
			Vec3 farAxisH = dimDoorsRotate(srcTransformer, doorState, bottom, farTransformer, dest.doorState(), dest.pos(), portal.axisH);
			portal.setOtherSideOrientation(DQuaternion.matrixToQuaternion(farAxisW, farAxisH, farAxisW.cross(farAxisH)));
		} else {
			// No door on the far side (e.g. detached rift): keep the travel
			// direction (entering the front means moving along the door's
			// FACING) and exit one block PAST the target along that direction,
			// so the arriving player walks away from the rift instead of into
			// it (or into whatever surrounds it).
			portal.setDestination(Vec3.atBottomCenterOf(dest.pos()).add(0, 1.0, 0).add(normal.scale(1.0)));
		}

		portal.portalTag = PORTAL_TAG;

		McHelper.spawnServerEntity(portal);
		if (dest.mutual()) {
			PortalManipulation.completeBiWayBiFacedPortal(portal, p -> {}, p -> {}, IPRegistry.PORTAL.get());
		} else {
			// one-way: see-through and traversable from this doorway only, but
			// from both of its faces — exactly like DimDoors' own teleport
			Portal flipped = PortalManipulation.createFlippedPortal(portal, IPRegistry.PORTAL.get());
			McHelper.spawnServerEntity(flipped);
		}
	}

	private static final TransformationMatrix3d EXIT_FLIP = TransformationMatrix3d.builder().rotateY(Math.PI).build();

	/**
	 * Applies exactly the rotation DimDoors' teleport applies to angles and
	 * velocities: into the source door's frame, 180° exit flip (if the
	 * destination flips exits), out of the destination door's frame. See
	 * RiftBlockEntity#teleport and EntranceRiftBlockEntity#receiveEntity.
	 */
	private static Vec3 dimDoorsRotate(CoordinateTransformerBlock srcTransformer, BlockState srcState, BlockPos srcPos,
									   CoordinateTransformerBlock farTransformer, BlockState farState, BlockPos farPos, Vec3 vector) {
		Vec3 relative = srcTransformer.rotateTo(srcTransformer.rotatorBuilder(srcState, srcPos), vector);
		if (farTransformer.isExitFlipped()) {
			relative = EXIT_FLIP.transform(relative);
		}
		return farTransformer.rotateOut(farTransformer.rotatorBuilder(farState, farPos), relative);
	}

	private static Vec3 portalPlaneCenter(BlockPos bottom, Direction facing) {
		return Vec3.atBottomCenterOf(bottom)
				.add(Vec3.atLowerCornerOf(facing.getOpposite().getNormal()).scale(PORTAL_OFFSET_FROM_CENTER))
				.add(0, 1.0, 0);
	}

	private static List<Portal> findDoorPortals(Level world, BlockPos bottom) {
		AABB doorway = new AABB(bottom).expandTowards(0, 1, 0).inflate(1.0);
		return world.getEntitiesOfClass(Portal.class, doorway, portal -> PORTAL_TAG.equals(portal.portalTag));
	}

	private static void removeDoorPortals(ServerLevel world, BlockPos bottom) {
		for (Portal portal : findDoorPortals(world, bottom)) {
			portal.remove(Entity.RemovalReason.KILLED);
		}
	}

	private static void setTeleportable(List<Portal> portals, boolean teleportable) {
		for (Portal portal : portals) {
			if (portal.teleportable != teleportable) {
				portal.setTeleportable(teleportable);
				portal.reloadAndSyncToClientNextTick();
			}
		}
	}

	/**
	 * Opens/closes the far door of a bridged pair without re-triggering this
	 * bridge, and (on open) sets its hinge so the two doors overlap seamlessly
	 * through the portal. Because the portal joins the doors front-to-front,
	 * the far door is viewed from its other side and must carry the MIRRORED
	 * hinge of the near door (the caller passes the already-mirrored value).
	 */
	private void syncFarDoor(ServerLevel world, BlockPos bottom, boolean open, @Nullable DoorHingeSide hinge) {
		this.syncing = true;
		try {
			BlockState state = world.getBlockState(bottom);
			if (!(state.getBlock() instanceof DoorBlock door)) {
				return;
			}
			if (hinge != null && state.hasProperty(DoorBlock.HINGE) && state.getValue(DoorBlock.HINGE) != hinge) {
				world.setBlockAndUpdate(bottom, state.setValue(DoorBlock.HINGE, hinge));
				state = world.getBlockState(bottom);
			}
			if (state.getValue(DoorBlock.OPEN) != open) {
				door.setOpen(null, world, state, bottom, open);
			}
		} finally {
			this.syncing = false;
		}
	}

	private static BlockPos bottomPos(BlockPos pos, BlockState state) {
		return state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER
				? pos.below()
				: pos;
	}
}
