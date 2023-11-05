package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.*;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.world.ModDimensions;

// A list of the default targets provided by dimcore. Add your own in ModTargets
public final class Targets {
	public static final Class<EntityTarget> ENTITY = EntityTarget.class;
	public static final Class<ItemTarget> ITEM = ItemTarget.class;
	public static final Class<FluidTarget> FLUID = FluidTarget.class;
	public static final Class<RedstoneTarget> REDSTONE = RedstoneTarget.class;

	public static void registerDefaultTargets() {

		DefaultTargets.registerDefaultTarget(ENTITY, new EntityTarget() {
            @Override
            public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeRotation, Vec3 relativeVelocity, Location location) {

				if(location != null){
					TeleportUtil.teleport(entity, location.getWorld(), Vec3.upFromBottomCenterOf(location.pos,0.0), relativeRotation, relativeVelocity);
					return true;
				}

                EntityUtils.chat(entity, Component.translatable("rifts.unlinked2"));
                return false;
            }
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
