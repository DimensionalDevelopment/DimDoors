package org.dimdev.dimdoors.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Hooks for optional see-through portal integration (Immersive Portals).
 * The default implementation does nothing; platform code installs a real
 * implementation when Immersive Portals is present.
 */
public interface DoorPortalBridge {
	DoorPortalBridge NONE = new DoorPortalBridge() {};

	static DoorPortalBridge get() {
		return Holder.instance;
	}

	static void set(DoorPortalBridge bridge) {
		Holder.instance = bridge;
	}

	/**
	 * Called server-side after a dimensional door's open state changed.
	 * {@code pos}/{@code state} may be either door half. {@code opener} is the
	 * player who toggled the door, or null (e.g. redstone); it is used to
	 * resolve player-dependent destinations such as personal pockets.
	 */
	default void onDoorStateChanged(Level level, BlockPos pos, BlockState state, @Nullable Player opener) {
	}

	/**
	 * Called server-side when a dimensional door block is removed or replaced.
	 * {@code state} is the old state; {@code pos} may be either door half.
	 */
	default void onDoorRemoved(Level level, BlockPos pos, BlockState state) {
	}

	/**
	 * True if a see-through portal currently handles teleportation of
	 * {@code entity} for this door, in which case DimDoors' own portal-plane
	 * teleport must not run as well.
	 */
	default boolean handlesTeleport(Level level, BlockPos bottomPos, BlockState doorState, Entity entity) {
		return false;
	}

	/**
	 * True if the rift's portal surface should not be rendered because a
	 * see-through portal is displayed in the doorway instead.
	 */
	default boolean suppressesRiftRendering(EntranceRiftBlockEntity rift) {
		return false;
	}

	class Holder {
		private static DoorPortalBridge instance = NONE;
	}
}
