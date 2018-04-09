package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;

public interface IFluidTarget extends ITarget {
    public boolean addFluidFlow(EnumFacing relativeFacing, Fluid fluid, int level);
    public void subtractFluidFlow(EnumFacing relativeFacing, Fluid fluid, int level);
}
