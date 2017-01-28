package com.zixiken.dimdoors.shared.world.pocket;

import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.world.DimensionType;

public class WorldProviderPublicPocket extends WorldProviderPocket {
    @Override
    public String getSaveFolder() {
        return ("DIM" + getDimension() + "DimDoorsPublic");
    }

    @Override
    public DimensionType getDimensionType() {
        return DimDoorDimensions.PUBLIC;
    }
}
