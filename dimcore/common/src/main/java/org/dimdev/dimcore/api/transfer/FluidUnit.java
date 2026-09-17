package org.dimdev.dimcore.api.transfer;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

/** Droplets on Fabric, millibuckets on NeoForge. */
public record FluidUnit(Fluid fluid, DataComponentPatch components, long amount) implements Unit<FluidUnit> {
    public FluidUnit(Fluid fluid, long amount) {
        this(fluid, DataComponentPatch.EMPTY, amount);
    }

    @Override
    public FluidUnit withAmount(long amount) {
        return new FluidUnit(fluid, components, amount);
    }

    @Override
    public boolean sameResource(FluidUnit other) {
        return fluid == other.fluid && components.equals(other.components);
    }
}
