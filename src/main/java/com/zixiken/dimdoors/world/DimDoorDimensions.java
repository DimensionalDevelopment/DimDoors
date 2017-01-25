package com.zixiken.dimdoors.world;

import com.zixiken.dimdoors.world.limbo.WorldProviderLimbo;
import com.zixiken.dimdoors.world.pocket.WorldProviderPocket;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimDoorDimensions {
    public static DimensionType LIMBO;
    public static DimensionType POCKET;

    public static void init() {
        LIMBO = DimensionType.register("Limbo", "_limbo", DimensionManager.getNextFreeDimId(), WorldProviderPocket.class, false);
        POCKET = DimensionType.register("Pocket", "_pocket", DimensionManager.getNextFreeDimId(), WorldProviderLimbo.class, false);

        registerDimension(LIMBO);
        registerDimension(POCKET);
    }

    public static void registerDimension(DimensionType dimension) {
        DimensionManager.registerDimension(dimension.getId(), dimension);
    }
}
