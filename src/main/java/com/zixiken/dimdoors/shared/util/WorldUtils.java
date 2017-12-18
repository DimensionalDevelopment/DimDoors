package com.zixiken.dimdoors.shared.util;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.world.World;

public class WorldUtils {
    public static World getWorld(int dim) {
        return DimDoors.proxy.getWorldServer(dim);
    }

    public static int getDim(World world) {
        return world.provider.getDimension();
    }
}
