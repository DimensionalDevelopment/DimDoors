package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;

public interface IFluidTarget extends ITarget {
    boolean addFluidFlow(EnumFacing relativeFacing, Fluid fluid, int level);
    void subtractFluidFlow(EnumFacing relativeFacing, Fluid fluid, int level);
}
