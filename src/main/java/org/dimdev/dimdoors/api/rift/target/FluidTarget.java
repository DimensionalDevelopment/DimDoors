package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;

public interface FluidTarget extends Target {
	boolean addFluidFlow(Direction relativeFacing, Fluid fluid, int level);

	void subtractFluidFlow(Direction relativeFacing, Fluid fluid, int level);
}
