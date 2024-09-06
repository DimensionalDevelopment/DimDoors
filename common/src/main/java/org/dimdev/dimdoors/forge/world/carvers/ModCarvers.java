package org.dimdev.dimdoors.world.carvers;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.forge.world.carvers.LimboCarver;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public class ModCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.CARVER_REGISTRY);

    public static final RegistrySupplier<LimboCarver> LIMBO_CARVER = CARVERS.register("limbo", () -> new LimboCarver(CaveCarverConfiguration.CODEC));

    public static final ResourceKey<ConfiguredWorldCarver<?>> LIMBO = register("limbo");

    private static ResourceKey<ConfiguredWorldCarver<?>> register(String name) {
        return ResourceKey.create(Registry.CONFIGURED_CARVER_REGISTRY, id(name));
    }

    public static void init() {
        CARVERS.register();
    }

    public static void bootstrap(BootstapContext<ConfiguredWorldCarver<?>> bootstapContext) {

        bootstapContext.register(LIMBO, new ConfiguredWorldCarver<>(LIMBO_CARVER.get(), new CaveCarverConfiguration(
                0.2f,
                UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(1)),
                ConstantFloat.of(0.5f),
                VerticalAnchor.aboveBottom(10),
                CarverDebugSettings.DEFAULT,
                HolderSet.direct(ModBlocks.UNRAVELLED_FABRIC.get().arch$holder()),
                ConstantFloat.of(1),
                ConstantFloat.of(1),
                ConstantFloat.of(-0.7f)
        )));

    }
}
