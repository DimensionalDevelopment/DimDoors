package org.dimdev.dimdoors.util;

import net.minecraft.server.world.ServerWorld;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.Map;

public class PocketGenerationParameters {
	private final ServerWorld world;
	private final String group;
	private final VirtualLocation sourceVirtualLocation;
	private final VirtualTarget linkTo;
	private final LinkProperties linkProperties;

	public PocketGenerationParameters(ServerWorld world, String group, VirtualLocation sourceVirtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		this.world = world;
		this.group = group;
		this.sourceVirtualLocation = sourceVirtualLocation;
		this.linkTo = linkTo;
		this.linkProperties = linkProperties;
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public String getGroup() {
		return this.group;
	}

	public VirtualLocation getSourceVirtualLocation() {
		return this.sourceVirtualLocation;
	}

	public VirtualTarget getLinkTo() {
		return this.linkTo;
	}

	public LinkProperties getLinkProperties() {
		return this.linkProperties;
	}

	public Map<String, Double> toVariableMap(Map<String, Double> stringDoubleMap) {
		stringDoubleMap.put("depth", (double) this.sourceVirtualLocation.getDepth());
		stringDoubleMap.put("public_size", (double) DimensionalDoorsInitializer.getConfig().getPocketsConfig().publicPocketSize);
		stringDoubleMap.put("private_size", (double) DimensionalDoorsInitializer.getConfig().getPocketsConfig().privatePocketSize);
		return stringDoubleMap;
	}
}
