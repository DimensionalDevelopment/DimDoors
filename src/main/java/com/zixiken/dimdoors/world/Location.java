package com.zixiken.dimdoors.world;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author Robijnvogel
 */
public class Location {    
    public String dimensionUID;
    public BlockPos pos;
    
    Location(String dimUID, int x, int y, int z) {
        this.dimensionUID = dimUID;
        pos = new BlockPos(x, y, z);
    }
    
    Location(String dimUID, BlockPos pos) {
        this.dimensionUID = dimUID;
        this.pos = pos.up(0); //copyOf
    }

    TileEntity getTileEntity() {
        World world = getWorld(dimensionUID); //@todo HOW?
        return world.getTileEntity(pos);
    }
    
}
