package org.dimdev.dimdoors.shared.fluids;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.dimdev.dimdoors.shared.blocks.BlockFabricEternal;

public class ModFluids {
    public static final Fluid ETERNAL_FABRIC = new FluidLiquid(BlockFabricEternal.ID);

    public static void registerFluids() {
        registerFluid(ETERNAL_FABRIC);
    }

    public static void registerFluid(Fluid fluid)
    {
        FluidRegistry.registerFluid(fluid);
        FluidRegistry.addBucketForFluid(fluid);
    }
}
