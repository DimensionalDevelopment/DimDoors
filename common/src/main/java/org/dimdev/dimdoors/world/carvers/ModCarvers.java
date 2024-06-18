package org.dimdev.dimdoors.world.carvers;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.dimdev.dimdoors.DimensionalDoors;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public class ModCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.CARVER);

    public static final RegistrySupplier<WorldCarver<?>> LIMBO_CARVER = CARVERS.register("limbo", () -> new LimboCarver(CaveCarverConfiguration.CODEC));

    public static final ResourceKey<WorldCarver<?>> LIMBO = register("limbo");

    private static ResourceKey<WorldCarver<?>> register(String name) {
        return ResourceKey.create(Registries.CARVER, id(name));
    }

    public static void init() {
        CARVERS.register();
    }
}
