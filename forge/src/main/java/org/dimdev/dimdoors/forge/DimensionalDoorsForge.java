package org.dimdev.dimdoors.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.item.component.forge.CounterComponentImpl;
import org.dimdev.dimdoors.world.ModBiomeModifiers;
import org.dimdev.dimdoors.forge.world.level.component.ChunkLazilyGeneratedComponent;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;

import java.util.function.Consumer;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsForge {
    public DimensionalDoorsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(DimensionalDoors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        StreamUtils.setup(this);
        DimensionalDoors.init();

        ModBiomeModifiers.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<RegisterCapabilitiesEvent>) registerCapabilitiesEvent -> {
            registerCapabilitiesEvent.register(CounterComponentImpl.class);
            registerCapabilitiesEvent.register(ChunkLazilyGeneratedComponent.class);
            registerCapabilitiesEvent.register(DimensionalRegistry.class);
        });

//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(AttachCapabilitiesEvent.class, event -> {
//            CounterComponentImpl.Provider.attach(event)
//        });
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(ChunkLazilyGeneratedComponentImpl.Provider::attach);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(DimensionalRegistryImpl.Provider::attach);
    }
}
