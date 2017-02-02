package com.zixiken.dimdoors.shared.world.pocketdimension;

import com.zixiken.dimdoors.shared.EnumPocketType;

public class WorldProviderDungeonPocket extends WorldProviderPublicPocket {

    @Override
    EnumPocketType getPocketType() {
        return EnumPocketType.DUNGEON;
    }
    
    @Override
    public String getSaveFolder() {
        return ("DIM" + getDimension() + "DimDoorsDungeon");
    }
}
