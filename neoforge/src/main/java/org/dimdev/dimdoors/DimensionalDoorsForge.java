package org.dimdev.dimdoors;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.ModBiomeModifiers;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsForge {
    public DimensionalDoorsForge(IEventBus bus) {
        // Submit our event bus to let architectury register our content on the right time
//        EventBusesHooks.registerModEventBus(DimensionalDoors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        StreamUtils.setup(this);
        ModAttachmentTypes.register(bus);
        DimensionalDoors.init();

        ModBiomeModifiers.init(bus);

    }
}
