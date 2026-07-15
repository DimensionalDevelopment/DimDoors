package org.dimdev.dimdoors;

import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.compat.sable.SableCompat;
import org.dimdev.dimdoors.fluid.EternalFluid;
import org.dimdev.dimdoors.fluid.LeakFluid;

public interface IDimensionalDoorsSided<T extends IDimensionalDoorsSided<T>> extends ISided<T> {
    public RecipeBookType getTesselatingRecipeBookType();

    default void checkCompat() {
        if(isModLoaded("sable")) {
            SableCompat.init();
        }
    }

    default Fluid createFlowingEternalFluid() {
        return new EternalFluid.Flowing();
    }

    default FlowingFluid createEternalFluid() {
        return new EternalFluid.Still();
    }

    default Fluid createFlowingLeakFluid() {
        return new LeakFluid.Flowing();
    }

    default FlowingFluid createLeakFluid() {
        return new LeakFluid.Still();
    }

}
