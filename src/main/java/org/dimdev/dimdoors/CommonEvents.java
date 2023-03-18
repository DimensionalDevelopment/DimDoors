package org.dimdev.dimdoors;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import org.dimdev.dimdoors.world.ModDimensions;

public class CommonEvents {

	//Replacement for CCA on the fabric side since NBT data can be easily loaded like this
	@SubscribeEvent
	public static void attachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
		Level level = event.getObject();
		if(level.dimension()==Level.OVERWORLD && !level.isClientSide())
			event.addCapability(DimensionalDoors.resource("dimensional_registry"), Constants.DIMENSIONAL_REGISTRY_PROVIDER);
	}

	@SubscribeEvent
	public static void attachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
		LevelChunk chunk = event.getObject();
		if(ModDimensions.isPocketDimension(chunk.getLevel()))
			event.addCapability(DimensionalDoors.resource("chunk_lazily_generated"), chunk);
	}

	@SubscribeEvent
	public static void serverStarted(ServerStartedEvent ev) {
		ModDimensions.init();
	}

	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent ev) {

	}
}
