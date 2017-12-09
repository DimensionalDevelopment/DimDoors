package com.zixiken.dimdoors.shared.util;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class WorldUtils {
    public static World getWorld(int dim) {
        return DimensionManager.getWorld(dim);
    }

    public static int getDim(World world) {
        return world.provider.getDimension();
    }
}
