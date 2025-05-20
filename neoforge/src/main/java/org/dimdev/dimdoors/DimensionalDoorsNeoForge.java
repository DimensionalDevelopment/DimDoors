package org.dimdev.dimdoors;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.dimdev.dimdoors.api.event.ChunkServedCallback;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.ModBiomeModifiers;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsNeoForge {
    public DimensionalDoorsNeoForge(IEventBus bus) {
        // Submit our event bus to let architectury register our content on the right time
//        EventBusesHooks.registerModEventBus(DimensionalDoors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        StreamUtils.setup(this);
        ModAttachmentTypes.register(bus);
        DimensionalDoors.init();

        ModBiomeModifiers.init(bus);

        NeoForge.EVENT_BUS.<ChunkEvent.Load>addListener(load -> {
            if(!load.isNewChunk() && load.getLevel() instanceof ServerLevel level && load.getChunk() instanceof LevelChunk chunk)
                ChunkServedCallback.EVENT.invoker().onChunkServed(level, chunk);
        });
    }
}
