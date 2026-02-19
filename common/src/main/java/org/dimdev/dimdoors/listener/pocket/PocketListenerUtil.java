package org.dimdev.dimdoors.listener.pocket;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collections;
import java.util.List;

public class PocketListenerUtil {
	public static <T> List<T> applicableAddons(Class<T> clazz, Level world, BlockPos pos) {
		return world.isClientSide ? ClientPacketListener.applicableAddonsClient(clazz, world, pos) : applicableAddonsCommon(clazz, world, pos);
	}

	public static <T> List<T> applicableAddonsCommon(Class<T> clazz, Level world, BlockPos pos) {
		if (world.isClientSide) throw new UnsupportedOperationException("Cannot call this method on the Client.");
		if (!ModDimensions.isPocketDimension(world)) return Collections.emptyList();
		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.dimension()).getPocketAt(pos);
		if (pocket == null) return Collections.emptyList();;
		return pocket.getAddonsInstanceOf(clazz);
	}

}