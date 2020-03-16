package org.dimdev.pocketlib;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public abstract class PocketWorldDimension extends Dimension {
    public PocketWorldDimension(World world, DimensionType dimensionType, float f) {
        super(world, dimensionType, f);
    }
}
