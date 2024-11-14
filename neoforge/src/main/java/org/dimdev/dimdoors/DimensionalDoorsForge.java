package org.dimdev.dimdoors;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.item.component.neoforge.CounterComponentImplDeprecated;
import org.dimdev.dimdoors.world.ModBiomeModifiers;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.function.Consumer;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsForge {
    public DimensionalDoorsForge() {
        // Submit our event bus to let architectury register our content on the right time
//        EventBusesHooks.registerModEventBus(DimensionalDoors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        StreamUtils.setup(this);
        DimensionalDoors.init();

        ModBiomeModifiers.init();
        CapabilityHooks

        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<RegisterCapabilitiesEvent>) registerCapabilitiesEvent -> {
            registerCapabilitiesEvent.register(CounterComponentImplDeprecated.class);
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
