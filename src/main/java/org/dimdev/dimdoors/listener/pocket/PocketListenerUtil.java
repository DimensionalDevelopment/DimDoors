package org.dimdev.dimdoors.listener.pocket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.network.client.ExtendedClientPlayNetworkHandler;
import org.dimdev.dimdoors.api.util.math.GridUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PocketListenerUtil {
	public static <T> List<T> applicableAddons(Class<T> clazz, World world, BlockPos pos) {
		if (world.isClient) throw new UnsupportedOperationException("Cannot call this method on the Client.");
		if (!ModDimensions.isPocketDimension(world)) return Collections.emptyList();
		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).getPocketAt(pos);
		if (pocket == null) return Collections.emptyList();;
		return pocket.getAddonsInstanceOf(clazz);
	}

	@Environment(EnvType.CLIENT)
	public static <T> List<T> applicableAddonsClient(Class<T> clazz, World world, BlockPos pos) {
		ClientPacketHandler packetHandler = ((ExtendedClientPlayNetworkHandler) MinecraftClient.getInstance().getNetworkHandler()).getDimDoorsPacketHandler();

		if (!world.getRegistryKey().equals(packetHandler.getPocketWorld())) return Collections.emptyList();

		int pocketId = GridUtil.gridPosToID(new GridUtil.GridPos(pos, packetHandler.getGridSize()));
		if (pocketId < packetHandler.getPocketId() || pocketId >= packetHandler.getPocketId() + packetHandler.getPocketRange()) {
			return Collections.emptyList();
		}
		return packetHandler.getAddons().stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}
}
