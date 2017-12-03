package com.zixiken.dimdoors.shared.world.pocketdimension;

import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.player.EntityPlayerMP;

public class WorldProviderDungeonPocket extends WorldProviderPublicPocket {

    @Override
    public EnumPocketType getPocketType() {
        return EnumPocketType.DUNGEON;
    }
    
    @Override
    public String getSaveFolder() {
        return ("DIM" + getDimension() + "DimDoorsDungeon");
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return DimDoorDimensions.LIMBO.getId();
    }
}
