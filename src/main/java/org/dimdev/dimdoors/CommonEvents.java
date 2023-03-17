package org.dimdev.dimdoors;

import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.dimdev.dimdoors.world.ModDimensions;

public class CommonEvents {

	@SubscribeEvent
	public static void serverStarted(ServerStartedEvent ev) {
		ModDimensions.init();
	}

	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent ev) {

	}
}
