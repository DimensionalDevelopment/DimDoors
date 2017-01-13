package com.zixiken.dimdoors.shared;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 *
 * @author Robijnvogel
 */
public class Location {

    public int dimensionID;
    public BlockPos pos;

    Location(int dimID, int x, int y, int z) {
        this.dimensionID = dimID;
        pos = new BlockPos(x, y, z);
    }

    Location(int dimID, BlockPos pos) {
        this.dimensionID = dimID;
        this.pos = pos.up(0); //copyOf
    }

    public TileEntity getTileEntity() {
        World world = DimensionManager.getWorld(dimensionID); //@todo HOW?
        return world.getTileEntity(pos);
    }

    static Location getLocation(TileEntity tileEntity) {
        World world = tileEntity.getWorld();
        int dimID = world.provider.getDimension();
        BlockPos blockPos = tileEntity.getPos();
        return new Location(dimID, blockPos);
    }

    static NBTBase writeToNBT(Location location) {
        NBTTagCompound locationNBT = new NBTTagCompound();
        locationNBT.setInteger("worldID", location.dimensionID);
        locationNBT.setInteger("x", location.pos.getX());
        locationNBT.setInteger("y", location.pos.getY());
        locationNBT.setInteger("z", location.pos.getZ());
        return locationNBT;
    }

    static Location readFromNBT(NBTTagCompound locationNBT) {
        int worldID = locationNBT.getInteger("worldID");
        int x = locationNBT.getInteger("x");
        int y = locationNBT.getInteger("y");
        int z = locationNBT.getInteger("z");
        BlockPos blockPos = new BlockPos(x, y, z);
        return new Location(worldID, blockPos);
    }

}
