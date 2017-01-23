/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.Random;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.pillar.schema.StructureSchema;
import vazkii.pillar.StructureGenerator;

/**
 *
 * @author Robijnvogel
 */
class PocketTemplate { //there is exactly one pocket placer for each different schematic that is loaded into the game (a Json might load several schematics though)

    //generation parameters
    private StructureSchema schematic;
    private final int size;
    private final int entranceDoorX;
    private final int entranceDoorY;
    private final int entranceDoorZ;
    private final int wallThickness; //determines the thickness of the wall around the pocket. Set to 1 to only generate the "bedRock" outer layer
    private final int floorThickness;
    private final int roofThickness;
    private final EnumPocketType typeID;
    //selection parameters
    private final String variantName;
    private final int minDepth;
    private final int maxDepth;
    private final int[] weights; //weights for chanced generation of dungeons per depth level | weights[0] is the weight for depth "minDepth"

    //this class should contain the actual schematic info, as well as some of the Json info (placement of Rifts and stuff)
    public PocketTemplate(String variantName, StructureSchema schematic, int size, int entranceDoorX, int entranceDoorY, int entranceDoorZ, int wallThickness, int floorThickness, int roofThickness, EnumPocketType typeID,
            int minDepth, int maxDepth, int[] weights) {
        this.variantName = variantName;
        this.weights = weights; //chance that this Pocket will get generated
        this.minDepth = minDepth; //pocket will only be generated from this Pocket-depth
        this.maxDepth = maxDepth; //to this pocket depth
        this.size = size; //size of content of pocket in chunks (walls are 5 thick, so size 0 will be 6*6 blocks, size 1 will be 22*22 blocks, etc.
        this.schematic = schematic;
        this.entranceDoorX = entranceDoorX; //coords of the TOP HALF of the door
        this.entranceDoorY = entranceDoorY;
        this.entranceDoorZ = entranceDoorZ;
        this.typeID = typeID;
        this.wallThickness = wallThickness;
        this.floorThickness = floorThickness;
        this.roofThickness = roofThickness;
    }

    public PocketTemplate(String variantName, int size, int entranceDoorX, int entranceDoorY, int entranceDoorZ, int wallThickness, int floorThickness, int roofThickness, EnumPocketType typeID,
            int minDepth, int maxDepth, int[] weights) {
        this(variantName, null, size, entranceDoorX, entranceDoorY, entranceDoorZ, wallThickness, floorThickness, roofThickness, typeID, minDepth, maxDepth, weights);
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

    void setSchematic(StructureSchema schematic) {
        this.schematic = schematic;
    }

    int place(int xBase, int yBase, int zBase, int dimID) { //returns the riftID of the entrance DimDoor
        IBlockState outerWallBlock = ModBlocks.blockDimWall.getStateFromMeta(2); //@todo, does this return the correct wall?
        IBlockState innerWallBlock;
        IBlockState entryDoorBlock;
        if (typeID == EnumPocketType.PRIVATE) {
            innerWallBlock = ModBlocks.blockDimWall.getStateFromMeta(1); //@todo, does this return the correct wall?
            entryDoorBlock = ModBlocks.blockDimDoorPersonal.getDefaultState();
        } else {
            innerWallBlock = ModBlocks.blockDimWall.getStateFromMeta(0); //@todo, does this return the correct wall?
            entryDoorBlock = ModBlocks.blockDimDoor.getDefaultState();
        }

        WorldServer world = DimDoors.proxy.getWorldServer(dimID);
        int hLimit = (size + 1) * 16; //horizontal relative limit
        int yLimit = roofThickness > 0 ? hLimit : 256 - yBase; //vertical relative limit (build-height + 1)

        //Place walls parallel to the X-axis
        if (wallThickness > 0) { //only place the walls if there are any walls to be placed
            //@todo, if I generate all walls from outside to inside in the outer for-loop, that might be better
            for (int wallLayer = 0; wallLayer < wallThickness; wallLayer++) {
                IBlockState wallBlock;
                if (wallLayer == 0) {
                    wallBlock = outerWallBlock;
                } else {
                    wallBlock = innerWallBlock;
                }
                for (int relativeY = 1; relativeY < yLimit - 1; relativeY++) { //the bottom layer will only be generated if the bottom is solid, just like the top
                    if (relativeY == 1) {
                        wallBlock = innerWallBlock; //the outer layer of the wall should be "bedrock", however the bottom of the dimensionwall should always be pitch-black if the dimension doesn't have a bottom for if you fall out of it.
                    }
                    //generate blocks in wall along the x-axis
                    for (int relativeX = 0; relativeX < hLimit; relativeX++) {
                        if (relativeX < wallLayer) {
                            //don't generate
                        } else {
                            world.setBlockState(new BlockPos(xBase + relativeX, yBase + relativeY, zBase + wallLayer), wallBlock);
                            world.setBlockState(new BlockPos(xBase + relativeX, yBase + relativeY, zBase + hLimit - 1 - wallLayer), wallBlock);
                        }
                    }
                    //generate blocks in wall alont the z-axis
                    for (int relativeZ = 0; relativeZ < hLimit; relativeZ++) {
                        if (relativeZ < wallLayer + 1) { //"+1" explanation: The corner blocks are already being placed by the wall along the x-axis
                            //don't generate
                        } else {
                            world.setBlockState(new BlockPos(xBase + wallLayer, yBase + relativeY, zBase + relativeZ), wallBlock);
                            world.setBlockState(new BlockPos(xBase + hLimit - 1 - wallLayer, yBase + relativeY, zBase + relativeZ), wallBlock);
                        }
                    }
                }
            }
        }

        //Generate the floor
        if (floorThickness > 0) {
            for (int relativeY = 0; relativeY < floorThickness; relativeY++) {
                IBlockState floorBlock;
                if (relativeY == 0) {
                    floorBlock = outerWallBlock;
                } else {
                    floorBlock = innerWallBlock;
                }
                for (int relativeX = 0; relativeX < hLimit; relativeX++) {
                    for (int relativeZ = 0; relativeZ < hLimit; relativeZ++) {
                        if (relativeX < wallThickness || relativeX > hLimit - 1 - wallThickness
                                || relativeZ < wallThickness || relativeZ > hLimit - 1 - wallThickness) { //under the walls
                            if (relativeY == 0) { //only the bottom layer
                                world.setBlockState(new BlockPos(xBase + relativeX, yBase + relativeY, zBase + relativeZ), floorBlock);
                            }
                        } else { //not under the walls
                            world.setBlockState(new BlockPos(xBase + relativeX, yBase + relativeY, zBase + relativeZ), floorBlock);
                        }
                    }
                }
            }
        }

        //Generate the roof
        if (roofThickness > 0) {
            for (int relativeY = yLimit - 1; relativeY > yLimit - 1 - roofThickness; relativeY--) {
                IBlockState floorBlock;
                if (relativeY == 0) {
                    floorBlock = outerWallBlock;
                } else {
                    floorBlock = innerWallBlock;
                }
                for (int relativeX = 0; relativeX < hLimit; relativeX++) {
                    for (int relativeZ = 0; relativeZ < hLimit; relativeZ++) {
                        if (relativeX < wallThickness || relativeX > hLimit - 1 - wallThickness
                                || relativeZ < wallThickness || relativeZ > hLimit - 1 - wallThickness) { // above the walls
                            if (relativeY == yLimit - 1) { //only the top layer
                                world.setBlockState(new BlockPos(xBase + relativeX, yBase + relativeY, zBase + relativeZ), floorBlock);
                            }
                        } else { //not above the walls
                            world.setBlockState(new BlockPos(xBase + relativeX, yBase + relativeY, zBase + relativeZ), floorBlock);
                        }
                    }
                }
            }
        }

        BlockPos dungeonBasePos = new BlockPos(xBase + wallThickness, yBase + floorThickness, zBase + wallThickness);

        //Place the Dungeon content structure
        StructureGenerator.placeStructureAtPosition(new Random(), schematic, Rotation.NONE, world, dungeonBasePos, true);

        //Place the door
        BlockPos doorPos = dungeonBasePos.offset(EnumFacing.EAST, entranceDoorX).offset(EnumFacing.UP, entranceDoorY).offset(EnumFacing.SOUTH, entranceDoorZ);
        EnumFacing facing = getAdjacentAirBlockFacing(world, doorPos);
        entryDoorBlock = entryDoorBlock.withProperty(BlockDoor.FACING, facing);
        world.setBlockState(doorPos, entryDoorBlock.withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER));
        world.setBlockState(doorPos, entryDoorBlock.withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER));
        TileEntity newTileEntity = world.getTileEntity(doorPos);

        //Register the rift and return its ID
        if (newTileEntity instanceof DDTileEntityBase) {
            DDTileEntityBase newTileEntityDimDoor = (DDTileEntityBase) newTileEntity;
            newTileEntityDimDoor.register();
            return newTileEntityDimDoor.getRiftID();
        }
        return -1;
    }

    private EnumFacing getAdjacentAirBlockFacing(World world, BlockPos pos) { //@todo, maybe this should be in some utility class?
        if (world.getBlockState(pos.east()) == Blocks.AIR) {
            return EnumFacing.EAST;
        } else if (world.getBlockState(pos.south()) == Blocks.AIR) {
            return EnumFacing.SOUTH;
        } else if (world.getBlockState(pos.west()) == Blocks.AIR) {
            return EnumFacing.WEST;
        } else { //north or no air blocks adjacent
            return EnumFacing.NORTH;
        }
    }
}
