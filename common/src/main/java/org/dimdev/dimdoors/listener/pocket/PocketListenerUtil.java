package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.Optional;

public class PocketListenerUtil {
	public static <T extends PocketAddon> Optional<T> getAddon(PocketAddon.PocketAddonType<T, ?> clazz, Level world, BlockPos pos) {
		return world.isClientSide ? ClientPacketListener.getAddonClient(clazz, world, pos) : getAddonCommon(clazz, world, pos);
	}

	public static <T extends PocketAddon> Optional<T> getAddonCommon(PocketAddon.PocketAddonType<T, ?> clazz, Level world, BlockPos pos) {
		if (world.isClientSide) throw new UnsupportedOperationException("Cannot call this method on the Client.");
		if (!ModDimensions.isPocketDimension(world)) return Optional.empty();
		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).getPocketAt(pos);
		if (pocket == null) return Optional.empty();
		return pocket.getAddon(clazz);
	}

}