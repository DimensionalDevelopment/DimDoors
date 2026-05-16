package org.dimdev.dimdoors.world.carvers;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public class ModCarvers {
    public static final LimboCarver LIMBO_CARVER = DimensionalDoors.getSided().registerCarver("limbo", new LimboCarver(CaveCarverConfiguration.CODEC));

    public static final ResourceKey<ConfiguredWorldCarver<?>> LIMBO = register("limbo");

    private static ResourceKey<ConfiguredWorldCarver<?>> register(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, id(name));
    }

    public static void init() {
    }
}
