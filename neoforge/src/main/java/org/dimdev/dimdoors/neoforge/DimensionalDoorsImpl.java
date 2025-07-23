package org.dimdev.dimdoors.neoforge;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.dimdev.dimdoors.DimensionalDoors;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DimensionalDoors.MOD_ID)
public class DimensionalDoorsImpl {
    private static List<Pair<ResourceLocation, BiConsumer<HolderLookup.Provider, ResourceManager>>> loaders = new ArrayList<>();

    public static Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void initBuiltinPacks() {
        NeoForge.EVENT_BUS.addListener(DimensionalDoorsImpl::addReloaders);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(DimensionalDoorsImpl::addPackFinders);
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            var classicPack = createPack("classic", "Classic");
            var defaultPack = createPack("default", "Default");
            event.addRepositorySource((packConsumer) -> {
                packConsumer.accept(classicPack);
                packConsumer.accept(defaultPack);
            });
        }
    }

//    @SubscribeEvent
    public static void addReloaders(AddReloadListenerEvent event) {
        loaders.forEach(pair -> event.addListener(new NeoforgeResourceLoader(pair.getSecond())));
    }

    public static Pack createPack(String id, String name) {

        var resourcePath = ModList.get().getModFileById(DimensionalDoors.MOD_ID).getFile().findResource("resourcepacks", id);
        return Pack.readMetaAndCreate(new PackLocationInfo(id, Component.literal(name), PackSource.BUILT_IN, Optional.empty()),
                new PathPackResources.PathResourcesSupplier(resourcePath), PackType.SERVER_DATA, new PackSelectionConfig(false, Pack.Position.BOTTOM, false));
    }

    public static void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
        loaders.add(Pair.of(DimensionalDoors.id(name), consumer));
    }

    private static class NeoforgeResourceLoader extends ContextAwareReloadListener implements ResourceManagerReloadListener {
        private final BiConsumer<HolderLookup.Provider, ResourceManager> consumer;

        public NeoforgeResourceLoader(BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            consumer.accept(this.getRegistryLookup(), resourceManager);
        }
    }
}
