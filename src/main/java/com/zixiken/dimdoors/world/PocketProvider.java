package com.zixiken.dimdoors.world;

import net.minecraft.world.WorldProvider;

public class PocketProvider extends WorldProvider {
    @Override
    public String getDimensionName() {
        return "Pocket Dimension";
    }

    @Override
    public String getInternalNameSuffix() {
        return "_pocket";
    }
}
