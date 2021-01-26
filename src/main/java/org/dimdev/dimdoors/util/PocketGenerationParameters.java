package org.dimdev.dimdoors.util;

import net.minecraft.server.world.ServerWorld;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public class PocketGenerationParameters {
	private final ServerWorld world;
	private final String group;
	private final VirtualLocation virtualLocation;
	private final VirtualTarget linkTo;
	private final LinkProperties linkProperties;

	public PocketGenerationParameters(ServerWorld world, String group, VirtualLocation virtualLocation, VirtualTarget linkTo, LinkProperties linkProperties) {
		this.world = world;
		this.group = group;
		this.virtualLocation = virtualLocation;
		this.linkTo = linkTo;
		this.linkProperties = linkProperties;
	}

	public ServerWorld getWorld() {
		return world;
	}

	public String getGroup() {
		return group;
	}

	public VirtualLocation getVirtualLocation() {
		return virtualLocation;
	}

	public VirtualTarget getLinkTo() {
		return linkTo;
	}

	public LinkProperties getLinkProperties() {
		return linkProperties;
	}
}
