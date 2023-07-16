package org.dimdev.dimdoors;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.item.component.forge.CounterComponentImpl;
import org.dimdev.dimdoors.world.level.component.ChunkLazilyGeneratedComponent;
import org.dimdev.dimdoors.world.level.component.forge.ChunkLazilyGeneratedComponentImpl;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.level.registry.forge.DimensionalRegistryImpl;

import java.util.function.Consumer;

@Mod(DimensionalDoors.MOD_ID)
public class DimensionalDoorsForge {
    public DimensionalDoorsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(DimensionalDoors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        DimensionalDoors.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<FMLClientSetupEvent>) event -> DimensionalDoorsClient.init());

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
