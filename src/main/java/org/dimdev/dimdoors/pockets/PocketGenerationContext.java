package org.dimdev.dimdoors.pockets;

import net.minecraft.server.world.ServerWorld;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.Map;

public record PocketGenerationContext(ServerWorld world, VirtualLocation sourceVirtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
	public Map<String, Double> toVariableMap(Map<String, Double> stringDoubleMap) {
		stringDoubleMap.put("depth", (double) this.sourceVirtualLocation.getDepth());
		stringDoubleMap.put("public_size", (double) DimensionalDoors.getConfig().getPocketsConfig().publicPocketSize);
		stringDoubleMap.put("private_size", (double) DimensionalDoors.getConfig().getPocketsConfig().privatePocketSize);
		return stringDoubleMap;
	}
}
