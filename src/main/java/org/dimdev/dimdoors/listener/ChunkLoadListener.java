package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.generator.LazyPocketGenerator;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.LazyGenerationPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ChunkLoadListener implements ServerChunkEvents.Load {
	@Override
	public void onChunkLoad(ServerWorld world, WorldChunk chunk) {
		if (!ModDimensions.isPocketDimension(world)) return;
		Pocket pocket = DimensionalRegistry.getPocketDirectory(world.getRegistryKey()).getPocketAt(chunk.getPos().getStartPos());
		if (!(pocket instanceof LazyGenerationPocket)) return;
		if (LazyPocketGenerator.currentlyGenerating) {
			LazyPocketGenerator.generationQueue.add(chunk);
		} else {
			MinecraftServer server = DimensionalDoorsInitializer.getServer();
			DimensionalDoorsInitializer.getServer().send(new ServerTask(server.getTicks(), () -> ((LazyGenerationPocket) pocket).chunkLoaded(chunk)));
		}
	}
}
