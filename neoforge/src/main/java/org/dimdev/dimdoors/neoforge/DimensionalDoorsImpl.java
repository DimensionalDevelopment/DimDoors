package org.dimdev.dimdoors.neoforge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.dimdev.dimdoors.DimensionalDoors;

import java.nio.file.Path;

public class DimensionalDoorsImpl {
    public static Path getConfigRoot() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void initBuiltinPacks() {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(DimensionalDoorsImpl::addPackFinders);
    }

//    TODO: Renable

    public static void addPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(DimensionalDoors.id("resourcepacks/classic"), PackType.SERVER_DATA, Component.literal("Classic"), PackSource.BUILT_IN, false, Pack.Position.TOP);
        event.addPackFinders(DimensionalDoors.id("resourcepacks/Default"), PackType.SERVER_DATA, Component.literal("Default"), PackSource.BUILT_IN, true, Pack.Position.TOP);
//        if (event.getPackType() == PackType.SERVER_DATA) {

//            var classicPack = createPack("classic", "Classic");
//            var defaultPack = createPack("default", "Default");
//            event.addRepositorySource((packConsumer) -> {
//                packConsumer.accept(classicPack);
//                packConsumer.accept(defaultPack);
//            });
        }
    }

//    public static Pack createPack(String id, String name) {
//        var resourcePath = ModList.get().getModFileById(DimensionalDoors.MOD_ID).getFile().findResource("resourcepacks", id);
//        return Pack.readMetaAndCreate("builtin/" + id, Component.literal(name), false,
//                (path) -> new PathPackResources(path, resourcePath), PackType.SERVER_DATA, Pack.Position.BOTTOM, PackSource.BUILT_IN);
//    }

//    public static RecipeBookType createTeselattingRecipeBookType() {
//        return RecipeBookType.getExtensionInfo().create("TESSELLATING");
//    }
//}
