package org.dimdev.dimdoors.fluid.neoforge;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.dimdev.dimdoors.DimensionalDoors;

public final class ModFluidTypes {
    public static final FluidType ETERNAL = new FluidType(FluidType.Properties.create().lightLevel(15).temperature(1300).viscosity(6000));
    public static final FluidType LEAK = new FluidType(FluidType.Properties.create());

    private ModFluidTypes() {
    }

    public static void init() {
        Registry.register(NeoForgeRegistries.FLUID_TYPES, DimensionalDoors.id("eternal_fluid"), ETERNAL);
        Registry.register(NeoForgeRegistries.FLUID_TYPES, DimensionalDoors.id("leak"), LEAK);
    }
}
