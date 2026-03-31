package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.pockets.PocketData;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.Optional;

public class PocketListenerUtil {
	public static <T extends PocketAddon> Optional<T> getAddon(PocketAddon.PocketAddonType<T, ?> clazz, Level world, BlockPos pos) {
		return world.isClientSide ? ClientPacketListener.getAddonClient(clazz, world, pos) : getAddonCommon(clazz, world, pos);
	}

	public static <T extends PocketAddon> Optional<T> getAddonCommon(PocketAddon.PocketAddonType<T, ?> clazz, Level world, BlockPos pos) {
		if (world.isClientSide) throw new UnsupportedOperationException("Cannot call this method on the Client.");
        PocketData pocket = PocketData.get((ServerLevel) world);
		if (pocket == null) return Optional.empty();
		return pocket.getAddon(clazz);
	}

}