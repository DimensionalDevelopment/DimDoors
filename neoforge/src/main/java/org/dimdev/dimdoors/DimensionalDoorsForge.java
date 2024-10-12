package org.dimdev.dimdoors;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.ModBiomeModifiers;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsForge {
    public DimensionalDoorsForge(IEventBus bus) {
        // Submit our event bus to let architectury register our content on the right time

        StreamUtils.setup(this);
        DimensionalDoors.init();

        ModBiomeModifiers.init(bus);

//        bus.addListener((Consumer<RegisterCapabilitiesEvent>) registerCapabilitiesEvent -> {
//            registerCapabilitiesEvent.register(IdCounterImpl.class);
//            registerCapabilitiesEvent.register(ChunkLazilyGeneratedComponent.class);
//            registerCapabilitiesEvent.register(DimensionalRegistry.class);
//        });

//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(AttachCapabilitiesEvent.class, event -> {
//            CounterComponentImpl.Provider.attach(event)
//        });
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(ChunkLazilyGeneratedComponentImpl.Provider::attach);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(DimensionalRegistryImpl.Provider::attach);
    }
}
