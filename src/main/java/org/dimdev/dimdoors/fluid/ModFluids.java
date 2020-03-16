package org.dimdev.dimdoors.fluid;

import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

public class ModFluids {
    public static final BaseFluid ETERNAL_FLUID = register("dimdoors:eternal_fluid", new EternalFluid.Still());
    public static final BaseFluid FLOWING_ETERNAL_FLUID = register("dimdoors:flowing_eternal_fluid", new EternalFluid.Flowing());

    private static <T extends Fluid> T register(String string, T fluid) {
        return (T) Registry.register(Registry.FLUID, string, fluid);
    }
}
