package org.dimdev.dimdoors.fabric;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.dimdev.dimdoors.DimensionalDoors;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.dimdev.dimdoors.DimensionalDoors.*;

public class DimensionalDoorsImpl {
    public static Path getConfigRoot() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void initBuiltinPacks() {
        ResourceManagerHelper.registerBuiltinResourcePack(id("default"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.DEFAULT_ENABLED);
        ResourceManagerHelper.registerBuiltinResourcePack(id("classic"), FabricLoader.getInstance().getModContainer(MOD_ID).get(), ResourcePackActivationType.DEFAULT_ENABLED);
    }

    public static void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
        var id = DimensionalDoors.id(name);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(id, provider -> new FabricResourceLoader(id, manager -> consumer.accept(provider, manager)));
    }

    private record FabricResourceLoader(ResourceLocation id, Consumer<ResourceManager> consumer) implements IdentifiableResourceReloadListener, ResourceManagerReloadListener {

        @Override
        public ResourceLocation getFabricId() {
            return id;
        }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            consumer.accept(resourceManager);
        }
    }
}
