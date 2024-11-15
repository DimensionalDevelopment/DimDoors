package org.dimdev.dimdoors;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.ModBiomeModifiers;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsForge {
    public DimensionalDoorsForge() {
        // Submit our event bus to let architectury register our content on the right time
//        EventBusesHooks.registerModEventBus(DimensionalDoors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        StreamUtils.setup(this);
        DimensionalDoors.init();

        ModBiomeModifiers.init();

        ModAttachmentTypes.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
