package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.rift.target.DefaultTargets;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.FluidTarget;
import org.dimdev.dimdoors.api.rift.target.ItemTarget;
import org.dimdev.dimdoors.api.rift.target.RedstoneTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;

import net.minecraft.fluid.Fluid;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

// A list of the default targets provided by dimcore. Add your own in ModTargets
public final class Targets {
	public static final Class<EntityTarget> ENTITY = EntityTarget.class;
	public static final Class<ItemTarget> ITEM = ItemTarget.class;
	public static final Class<FluidTarget> FLUID = FluidTarget.class;
	public static final Class<RedstoneTarget> REDSTONE = RedstoneTarget.class;

	public static void registerDefaultTargets() {
		DefaultTargets.registerDefaultTarget(ENTITY, (entity, relativePos, relativeRotation, relativeVelocity) -> {
			EntityUtils.chat(entity, new TranslatableText("rifts.unlinked2"));
			return false;
		});
		DefaultTargets.registerDefaultTarget(ITEM, stack -> false);

		DefaultTargets.registerDefaultTarget(FLUID, new FluidTarget() {
			@Override
			public boolean addFluidFlow(Direction relativeFacing, Fluid fluid, int level) {
				return false;
			}

			@Override
			public void subtractFluidFlow(Direction relativeFacing, Fluid fluid, int level) {
				throw new RuntimeException("Subtracted fluid flow that was never accepted");
			}
		});

		DefaultTargets.registerDefaultTarget(REDSTONE, new RedstoneTarget() {
			@Override
			public boolean addRedstonePower(Direction relativeFacing, int strength) {
				return false;
			}

			@Override
			public void subtractRedstonePower(Direction relativeFacing, int strength) {
				throw new RuntimeException("Subtracted redstone that was never accepted");
			}
		});
	}
}
