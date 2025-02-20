package org.dimdev.dimdoors.neoforge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.dimdev.dimdoors.DimensionalDoors;

import java.nio.file.Path;
import java.util.Optional;

public class DimensionalDoorsImpl {
    public static Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void initBuiltinPacks() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DimensionalDoorsImpl::addPackFinders);
    }

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

    public static Pack createPack(String id, String name) {

        var resourcePath = ModList.get().getModFileById(DimensionalDoors.MOD_ID).getFile().findResource("resourcepacks", id);
        return Pack.readMetaAndCreate(new PackLocationInfo(id, Component.literal(name), PackSource.BUILT_IN, Optional.empty()),
                new PathPackResources.PathResourcesSupplier(resourcePath), PackType.SERVER_DATA, new PackSelectionConfig(false, Pack.Position.BOTTOM, false));
    }
}
