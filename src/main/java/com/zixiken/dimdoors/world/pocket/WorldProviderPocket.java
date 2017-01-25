package com.zixiken.dimdoors.world.pocket;

import com.zixiken.dimdoors.world.DimDoorDimensions;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public class WorldProviderPocket extends WorldProvider {
    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.POCKET;
    }
}
