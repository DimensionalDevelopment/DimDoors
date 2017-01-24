/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

/**
 *
 * @author Robijnvogel
 */
class PocketTemplate { //there is exactly one pocket placer for each different schematic that is loaded into the game (a Json might load several schematics though)

    //generation parameters
    private Schematic schematic;
    private final int size;
    private final EnumPocketType typeID;
    //selection parameters
    private final String variantName;
    private final int minDepth;
    private final int maxDepth;
    private final int[] weights; //weights for chanced generation of dungeons per depth level | weights[0] is the weight for depth "minDepth"

    //this class should contain the actual schematic info, as well as some of the Json info (placement of Rifts and stuff)
    public PocketTemplate(String variantName, Schematic schematic, int size, EnumPocketType typeID,
            int minDepth, int maxDepth, int[] weights) {
        this.variantName = variantName;
        this.weights = weights; //chance that this Pocket will get generated
        this.minDepth = minDepth; //pocket will only be generated from this Pocket-depth
        this.maxDepth = maxDepth; //to this pocket depth
        this.size = size; //size of content of pocket in chunks (walls are 5 thick, so size 0 will be 6*6 blocks, size 1 will be 22*22 blocks, etc.
        this.schematic = schematic;
        this.typeID = typeID;
    }

    public PocketTemplate(String variantName, int size, EnumPocketType typeID, int minDepth, int maxDepth, int[] weights) {
        this(variantName, null, size, typeID, minDepth, maxDepth, weights);
    }

    int getSize() {
        return size;
    }

    int getMinDepth() {
        return minDepth;
    }

    int getMaxDepth() {
        return maxDepth;
    }

    int getWeight(int depth) {
        int index = depth - minDepth;
        if (index >= 0 && index < weights.length) {
            return weights[index];
        }
        return 0; //do not generate
    }

    String getName() {
        return variantName;
    }

    Object getSchematic() {
        return schematic;
    }

    void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }

    int place(int xBase, int yBase, int zBase, int dimID) { //returns the riftID of the entrance DimDoor
        if (schematic == null) {
            DimDoors.log(this.getClass(), "The schematic for variant " + variantName + " somehow didn't load correctly against despite all precautions.");
        }
        //@todo make sure that the door tile entities get registered!
        WorldServer world = DimDoors.proxy.getWorldServer(dimID);

        //Place the Dungeon content structure
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.width; z++) {
                    world.setBlockState(new BlockPos(xBase + x, yBase + y, zBase + z), schematic.pallette.get(schematic.blockData[x][y][z]));
                }
            }
        }
        //Place TileEntities
        List<DDTileEntityBase> rifts = new ArrayList();
        for (NBTTagCompound tileEntityNBT : schematic.tileEntities) {
            BlockPos pos = new BlockPos(xBase + tileEntityNBT.getInteger("x"), yBase + tileEntityNBT.getInteger("y"), zBase + tileEntityNBT.getInteger("z"));
            IBlockState state = world.getBlockState(pos);
            state.getBlock().createTileEntity(world, state);
            TileEntity tileEntity = world.getTileEntity(pos);
            tileEntity.readFromNBT(tileEntityNBT);
            tileEntity.markDirty();

            if (tileEntity instanceof DDTileEntityBase) {
                DDTileEntityBase rift = (DDTileEntityBase) tileEntity;
                rifts.add(rift);
            }
        }

        List<TileEntityDimDoor> dimDoorTiles = new ArrayList();
        for (DDTileEntityBase rift : rifts) {
            rift.register();
            if (rift instanceof TileEntityDimDoor) {
                TileEntityDimDoor dimDoorTile = (TileEntityDimDoor) rift;
                dimDoorTiles.add(dimDoorTile);
            }
        }

        if (dimDoorTiles.isEmpty()) {
            return -1;
        } else if (dimDoorTiles.size() == 1) {
            return dimDoorTiles.get(0).getRiftID();
        } else {
            Random random = new Random();
            int index = random.nextInt(dimDoorTiles.size());
            return dimDoorTiles.get(index).getRiftID();
        }
    }
}
