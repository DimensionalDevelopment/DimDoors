package org.dimdev.dimdoors.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.dimdev.dimdoors.DimensionalDoors;

import java.nio.file.Path;

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
        return Pack.readMetaAndCreate("builtin/" + id, Component.literal(name), false,
                (path) -> new PathPackResources(path, resourcePath, false), PackType.SERVER_DATA, Pack.Position.BOTTOM, PackSource.BUILT_IN);
    }

    public static RecipeBookType createTeselattingRecipeBookType() {
        return RecipeBookType.create("TESSELLATING");
    }
}
