package org.dimdev.dimdoors.shared.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidLiquid extends Fluid {
    public FluidLiquid(String fluidName) {
        super(fluidName, new ResourceLocation(String.format("dimdoors:blocks/%s_still", fluidName)), new ResourceLocation(String.format("dimdoors:blocks/%s_flow", fluidName)));
        this.setUnlocalizedName(fluidName);
    }
}
