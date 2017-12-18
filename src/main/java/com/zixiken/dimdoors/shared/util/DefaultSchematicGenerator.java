package com.zixiken.dimdoors.shared.util;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.SchematicHandler;
import com.zixiken.dimdoors.shared.blocks.BlockFabric;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.rifts.RiftDestination;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.rifts.WeightedRiftDestination;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

/**
 *
 * @author Robijnvogel
 */
public class DefaultSchematicGenerator {
    public static void generateDefaultSchematics() {
        for (int pocketSize = 0; pocketSize < 8; pocketSize++) {
            generateDefaultSchematic("defaultPublic", pocketSize, ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.ANCIENT), ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.REALITY), ModBlocks.DIMENSIONAL_DOOR, RiftDestination.PocketExitDestination.builder().build());
            generateDefaultSchematic("defaultPrivate", pocketSize, ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.ANCIENT_ALTERED), ModBlocks.FABRIC.getDefaultState().withProperty(BlockFabric.TYPE, BlockFabric.EnumType.ALTERED), ModBlocks.PERSONAL_DIMENSIONAL_DOOR, RiftDestination.PrivatePocketExitDestination.builder().build());
        }
    }

    private static void generateDefaultSchematic(String baseName, int pocketSize, IBlockState outerWallBlockState, IBlockState innerWallBlockState, Block doorBlock, RiftDestination exitDest) {
        int maxbound = (pocketSize + 1) * 16 - 1;

        Schematic schematic = new Schematic();
        schematic.version = 1;
        schematic.author = "Robijnvogel"; //@todo set in build.gradle ${modID}
        schematic.schematicName = baseName + "_" + pocketSize;
        schematic.creationDate = System.currentTimeMillis();
        schematic.requiredMods = new String[1];
        schematic.requiredMods[0] = DimDoors.MODID;
        schematic.width = (short) maxbound;
        schematic.height = (short) maxbound;
        schematic.length = (short) maxbound;
        schematic.offset = new int[]{0, 0, 0};

        schematic.paletteMax = 4;
        schematic.pallette = new ArrayList<>();
        schematic.pallette.add(Blocks.AIR.getDefaultState());
        schematic.pallette.add(outerWallBlockState);
        schematic.pallette.add(innerWallBlockState);
        schematic.pallette.add(doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)); //bottom
        schematic.pallette.add(doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)); //top

        schematic.blockData = new int[maxbound][maxbound][maxbound]; //[x][y][z]
        for (int x = 0; x < maxbound; x++) {
            for (int y = 0; y < maxbound; y++) {
                for (int z = 0; z < maxbound; z++) {
                    if (x == 0 || x == maxbound - 1
                            || y == 0 || y == maxbound - 1
                            || z == 0 || z == maxbound - 1) {
                        schematic.blockData[x][y][z] = 1; //outer dim wall
                    } else if (MathUtils.withinDistanceOf(new int[]{x, y, z}, 5, new int[]{0, maxbound})) {
                        if (z == 4 && x == (maxbound - 1) / 2 && y > 4 && y < 7) {
                            if (y == 5) {
                                schematic.blockData[x][y][z] = 3; //door bottom
                            } else { // y == 6
                                schematic.blockData[x][y][z] = 4; //door top
                            }
                        } else {
                            schematic.blockData[x][y][z] = 2; //inner dim wall
                        }
                    } else {
                        schematic.blockData[x][y][z] = 0; //air
                    }
                }
            }
        }

        schematic.tileEntities = new ArrayList<>();
        TileEntityRift rift = (TileEntityRift) doorBlock.createTileEntity(null, doorBlock.getDefaultState());
        rift.setSingleDestination(RiftDestination.PocketEntranceDestination.builder()
                .ifDestinations(MathUtils.listFrom(new WeightedRiftDestination(exitDest, 1, 0)))
                .build());

        NBTTagCompound tileNBT = rift.serializeNBT();
        tileNBT.setInteger("x", (maxbound - 1) / 2);
        tileNBT.setInteger("y", 5);
        tileNBT.setInteger("z", 4);
        schematic.tileEntities.add(tileNBT);

        SchematicHandler.INSTANCE.saveSchematic(schematic, schematic.schematicName);
    }
}
