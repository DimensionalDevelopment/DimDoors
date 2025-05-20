package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.block.entity.RiftData;

public class DimensionalDoorsFabric implements ModInitializer {

	@Override
    public void onInitialize() {
		StreamUtils.setup(this);
        ModAttachmentTypes.register();
        DimensionalDoors.init();
		PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);

        ServerChunkEvents.CHUNK_LOAD.register((serverLevel, levelChunk) -> ChunkServedCallback.EVENT.invoker().onChunkServed(serverLevel, levelChunk));
    }
}
