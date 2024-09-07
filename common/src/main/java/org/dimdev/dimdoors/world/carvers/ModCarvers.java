package org.dimdev.dimdoors.world.carvers;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.CARVER_REGISTRY);

    public static final RegistrySupplier<LimboCarver> LIMBO_CARVER = CARVERS.register("limbo", () -> new LimboCarver(CaveCarverConfiguration.CODEC));

    public static void init() {
        CARVERS.register();
    }
}
