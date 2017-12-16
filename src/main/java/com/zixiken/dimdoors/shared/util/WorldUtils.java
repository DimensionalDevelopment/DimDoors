package com.zixiken.dimdoors.shared.util;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class WorldUtils {
    public static World getWorld(int dim) {
        World world = DimDoors.proxy.getWorldServer(dim);
        if (world == null) {
            throw new RuntimeException("Something went wrong!");
        }
        return world;
    }

    public static int getDim(World world) {
        return world.provider.getDimension();
    }
}
