package com.zixiken.dimdoors.world.pocket;

import com.zixiken.dimdoors.world.DimDoorDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderPublicPocket extends WorldProviderPocket {
    @Override
    public String getSaveFolder() {
        return (getDimension() == 0 ? null : "public");
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.PUBLIC;
    }
}
