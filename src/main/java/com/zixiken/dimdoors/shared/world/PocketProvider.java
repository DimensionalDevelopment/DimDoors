package com.zixiken.dimdoors.shared.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public class PocketProvider extends WorldProvider {

    /*@Override
    public String getDimensionName() {
        return "Pocket Dimension";
    }

    @Override
    public String getInternalNameSuffix() {
        return "_pocket";
    }*/

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.OVERWORLD;
    }
}
