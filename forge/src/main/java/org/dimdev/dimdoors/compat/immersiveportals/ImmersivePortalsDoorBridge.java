package org.dimdev.dimdoors.compat.immersiveportals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.compat.DoorPortalBridge;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.platform_specific.IPRegistry;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.List;

/**
 * Immersive Portals backed implementation of {@link DoorPortalBridge}.
 *
 * When a dimensional door that is mutually linked to another dimensional door
 * is opened, a bi-directional, double-faced see-through portal cluster is
 * spawned between the two doorways and the far door is opened as well.
 * Closing (or breaking) either door removes the cluster and closes both doors.
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

	@Override
	public void onDoorStateChanged(Level level, BlockPos pos, BlockState state) {
		if (!(level instanceof ServerLevel world)) {
			return;
		}
		BlockPos bottom = bottomPos(pos, state);
		BlockState doorState = world.getBlockState(bottom);
		if (!(doorState.getBlock() instanceof DimensionalDoorBlock)) {
			return;
		}

		DoorPair pair = resolvePair(world, bottom);

		if (doorState.getValue(DoorBlock.OPEN)) {
			if (pair == null) {
				return; // not mutually linked to another dimensional door
			}
			if (findDoorPortals(world, bottom).isEmpty()) {
				spawnPortalCluster(world, bottom, doorState, pair);
			}
			setDoorOpen(pair.world(), pair.pos(), true);
		} else {
			removeDoorPortals(world, bottom);
			if (pair != null) {
				removeDoorPortals(pair.world(), pair.pos());
				setDoorOpen(pair.world(), pair.pos(), false);
			}
		}
	}

	@Override
	public void onDoorRemoved(Level level, BlockPos pos, BlockState state) {
		if (!(level instanceof ServerLevel world)) {
			return;
		}
		removeDoorPortals(world, bottomPos(pos, state));
	}

	@Override
	public boolean handlesTeleport(Level level, BlockPos bottomPos, BlockState doorState) {
		return level instanceof ServerLevel && !findDoorPortals(level, bottomPos).isEmpty();
	}

	@Override
	public boolean suppressesRiftRendering(EntranceRiftBlockEntity rift) {
		Level level = rift.getLevel();
		return level != null && !findDoorPortals(level, rift.getBlockPos()).isEmpty();
	}

	private record DoorPair(ServerLevel world, BlockPos pos, BlockState state) {
	}

	/**
	 * Resolves the door this one leads to, requiring a mutual link: this door's
	 * rift must reference the far door and the far door's rift must reference
	 * this door back. Returns null otherwise.
	 */
	private static DoorPair resolvePair(ServerLevel world, BlockPos bottom) {
		if (!(world.getBlockEntity(bottom) instanceof EntranceRiftBlockEntity rift)) {
			return null;
		}
		Location here = new Location(world, bottom);
		Location target = referencedLocation(rift, here);
		if (target == null) {
			return null;
		}
		ServerLevel farWorld = target.getWorld();
		if (farWorld == null) {
			return null;
		}
		BlockState farState = farWorld.getBlockState(target.pos);
		if (!(farState.getBlock() instanceof DimensionalDoorBlock)
				|| farState.getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
			return null;
		}
		if (!(farWorld.getBlockEntity(target.pos) instanceof EntranceRiftBlockEntity farRift)) {
			return null;
		}
		Location back = referencedLocation(farRift, target);
		if (back == null || !back.equals(here)) {
			return null;
		}
		return new DoorPair(farWorld, target.pos, farState);
	}

	private static Location referencedLocation(EntranceRiftBlockEntity rift, Location riftLocation) {
		if (!(rift.getDestination() instanceof RiftReference reference)) {
			return null;
		}
		reference.setLocation(riftLocation); // relative/local references resolve against the rift's own location
		return reference.getReferencedLocation();
	}

	private static void spawnPortalCluster(ServerLevel world, BlockPos bottom, BlockState doorState, DoorPair pair) {
		Direction facing = doorState.getValue(DoorBlock.FACING);
		Direction farFacing = pair.state().getValue(DoorBlock.FACING);
		Vec3 up = new Vec3(0, 1, 0);

		Portal portal = IPRegistry.PORTAL.get().create(world);
		if (portal == null) {
			return;
		}

		portal.setOriginPos(portalPlaneCenter(bottom, facing));
		Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
		portal.setOrientationAndSize(up.cross(normal), up, 1.0, 2.0);

		portal.setDestinationDimension(pair.world().dimension());
		portal.setDestination(portalPlaneCenter(pair.pos(), farFacing));

		// Entering the front of this door exits out of the front of the far
		// door, so the far side of the transform faces into the far door's back.
		Vec3 farNormal = Vec3.atLowerCornerOf(farFacing.getOpposite().getNormal());
		portal.setOtherSideOrientation(DQuaternion.matrixToQuaternion(up.cross(farNormal), up, farNormal));

		portal.portalTag = PORTAL_TAG;

		McHelper.spawnServerEntity(portal);
		PortalManipulation.completeBiWayBiFacedPortal(portal, p -> {}, p -> {}, IPRegistry.PORTAL.get());
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

	private static void setDoorOpen(ServerLevel world, BlockPos bottom, boolean open) {
		BlockState state = world.getBlockState(bottom);
		if (state.getBlock() instanceof DoorBlock door && state.getValue(DoorBlock.OPEN) != open) {
			door.setOpen(null, world, state, bottom, open);
		}
	}

	private static BlockPos bottomPos(BlockPos pos, BlockState state) {
		return state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER
				? pos.below()
				: pos;
	}
}
