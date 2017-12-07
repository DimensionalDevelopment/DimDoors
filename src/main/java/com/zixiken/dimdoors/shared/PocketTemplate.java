/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Schematic;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.shared.util.Location;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

/**
 *
 * @author Robijnvogel
 */
public class PocketTemplate { //there is exactly one pocket placer for each different schematic that is loaded into the game (a Json might load several schematics though)

    //generation parameters
    @Getter @Setter(value = AccessLevel.PACKAGE) private Schematic schematic; // TODO: access level
    @Getter private final int size;
    @Getter private final EnumPocketType typeID;
    //selection parameters
    @Getter private final String groupName;
    @Getter private final String name;
    private String variantType;
    @Getter private final int minDepth;
    @Getter private final int maxDepth;
    private final int[] weights; //weights for chanced generation of dungeons per depth level | weights[0] is the weight for depth "minDepth"

    //this class should contain the actual schematic info, as well as some of the Json info (placement of Rifts and stuff)
    public PocketTemplate(String groupName, String name, Schematic schematic, int size,
                          EnumPocketType typeID, int minDepth, int maxDepth, int[] weights) {
        this.groupName = groupName;
        this.name = name;
        this.weights = weights; //chance that this Pocket will get generated
        this.minDepth = minDepth; //pocket will only be generated from this Pocket-depth
        this.maxDepth = maxDepth; //to this pocket depth
        this.size = size; //size of pocket in chunks (0 -> 1*1 chunk, 1 -> 2*2 chunks etc.)
        this.schematic = schematic;
        this.typeID = typeID;
    }

    public PocketTemplate(String groupName, String name, int size,
                          EnumPocketType typeID, int minDepth, int maxDepth, int[] weights) {
        this(groupName, name, null, size, typeID, minDepth, maxDepth, weights);
    }

    int getWeight(int depth) {
        int index = depth - minDepth;
        if (index >= 0 && index < weights.length) {
            return weights[index];
        }
        return weights[weights.length - 1]; // return last weight
    }

    //@todo make sure that the "pocketID" parameter gets used, or remove it.
    public Pocket place(int shortenedX, int yBase, int shortenedZ, int gridSize, int dimID, int pocketID, int depth, EnumPocketType pocketTypeID, Location depthZeroLocation) { //returns the riftID of the entrance DimDoor
        int xBase = shortenedX * gridSize * 16;
        int zBase = shortenedZ * gridSize * 16;
        DimDoors.log(getClass(), "Placing new pocket at x = " + xBase + ", z = " + zBase);
        DimDoors.log(getClass(), "Name of new pocket schematic is " + schematic.getSchematicName());

        if (schematic == null) {
            DimDoors.log(getClass(), "The schematic for variant " + name + " somehow didn't load correctly despite all precautions.");
            return null;
        }
        //@todo make sure that the door tile entities get registered!
        WorldServer world = DimDoors.proxy.getWorldServer(dimID);

        //Place the Dungeon content structure
        List<IBlockState> palette = schematic.getPallette();
        int[][][] blockData = schematic.getBlockData();
        for (int x = 0; x < blockData.length; x++) {
            for (int y = 0; y < blockData[x].length; y++) {
                for (int z = 0; z < blockData[x][y].length; z++) {
                    world.setBlockState(new BlockPos(xBase + x, yBase + y, zBase + z), palette.get(blockData[x][y][z]), 2); //the "2" is to make non-default door-halves not break upon placement
                }
            }
        }

        //Load TileEntity Data
        List<DDTileEntityBase> rifts = new ArrayList<>();
        for (NBTTagCompound tileEntityNBT : schematic.getTileEntities()) {
            BlockPos pos = new BlockPos(xBase + tileEntityNBT.getInteger("x"), yBase + tileEntityNBT.getInteger("y"), zBase + tileEntityNBT.getInteger("z"));
            DimDoors.log(getClass(), "Re-loading tile-entity at blockPos: " + pos);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                if (tileEntity instanceof DDTileEntityBase) {
                    DimDoors.log(getClass(), "Rift found in schematic: " + pos);
                    DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
                    rifts.add(rift);
                    if (rift instanceof TileEntityDimDoor) {
                        DimDoors.proxy.updateDoorTE((BlockDimDoorBase) world.getBlockState(pos).getBlock(), world, pos);
                    }
                } else {
                    try {
                        tileEntity.readFromNBT(tileEntityNBT); //this reads in the wrong blockPos
                    } catch(Exception e) {
                        DimDoors.warn(getClass(), "Loading in the data for TileEntity of type " + tileEntity + " went wrong. Details: " + e.getLocalizedMessage());
                    }
                }
                tileEntity.setPos(pos); //correct the position
                tileEntity.markDirty();
            }
        }

        List<Integer> riftIDs = new ArrayList<>();
        for (DDTileEntityBase rift : rifts) {
            rift.register(depth);
            rift.setIsInPocket();
            rift.setPocket(pocketID, pocketTypeID);
            rift.setDepth(depth);

            riftIDs.add(rift.getRiftID());
        }

        return new Pocket(size, depth, pocketTypeID, shortenedX, shortenedZ, riftIDs, depthZeroLocation);
    }
}
