package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import org.dimdev.dimdoors.DimDoors;

// A list of the default targets provided by dimcore. Add your own in ModTargets
public final class Targets {
    public static final Class<IEntityTarget> ENTITY = IEntityTarget.class;
    public static final Class<IItemTarget> ITEM = IItemTarget.class;
    public static final Class<IFluidTarget> FLUID = IFluidTarget.class;
    public static final Class<IRedstoneTarget> REDSTONE = IRedstoneTarget.class;

    public static void registerDefaultTargets() {
        DefaultTargets.registerDefaultTarget(ENTITY, (entity, relativeYaw, relativePitch) -> {
            DimDoors.sendTranslatedMessage(entity, "rifts.unlinked");
            return false;
        });
        DefaultTargets.registerDefaultTarget(ITEM, stack -> false);

        DefaultTargets.registerDefaultTarget(FLUID, new IFluidTarget() {
            @Override public boolean addFluidFlow(EnumFacing relativeFacing, Fluid fluid, int level) {
                return false;
            }

            @Override public void subtractFluidFlow(EnumFacing relativeFacing, Fluid fluid, int level) {
                throw new RuntimeException("Subtracted fluid flow that was never accepted");
            }
        });

        DefaultTargets.registerDefaultTarget(REDSTONE, new IRedstoneTarget() {
            @Override public boolean addRedstonePower(EnumFacing relativeFacing, int strength) {
                return false;
            }

            @Override public void subtractRedstonePower(EnumFacing relativeFacing, int strength) {
                throw new RuntimeException("Subtracted redstone that was never accepted");
            }
        });
    }
}
