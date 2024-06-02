package org.dimdev.dimdoors.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.resource.PathPackResources;
import org.dimdev.dimdoors.DimensionalDoors;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Supplier;

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
            event.addRepositorySource((packConsumer, something) -> {
                packConsumer.accept(classicPack);
                packConsumer.accept(defaultPack);
            });
        }
    }

    public static Pack createPack(String id, String name) {
        var resourcePath = ModList.get().getModFileById(DimensionalDoors.MOD_ID).getFile().findResource("resourcepacks", id);
        return Pack.create("builtin/" + id, false, () -> new PathPackResources(id, resourcePath), new Pack.PackConstructor() {
            @Nullable
            @Override
            public Pack create(String string, Component arg, boolean bl, Supplier<PackResources> supplier, PackMetadataSection arg2, Pack.Position arg3, PackSource arg4, boolean bl2) {
                return new Pack(string, bl, supplier, Component.literal(name), arg, PackCompatibility.COMPATIBLE, Pack.Position.BOTTOM, bl2, arg4);
            }
        }, Pack.Position.BOTTOM, PackSource.BUILT_IN);
    }

    public static RecipeBookType createTeselattingRecipeBookType() {
        return RecipeBookType.create("TESSELLATING");
    }
}

