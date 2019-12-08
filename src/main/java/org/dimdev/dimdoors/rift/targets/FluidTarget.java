package org.dimdev.dimdoors.rift.targets;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Direction;

public interface FluidTarget extends Target {
    boolean addFluidFlow(Direction relativeFacing, Fluid fluid, int level);

    void subtractFluidFlow(Direction relativeFacing, Fluid fluid, int level);
}
