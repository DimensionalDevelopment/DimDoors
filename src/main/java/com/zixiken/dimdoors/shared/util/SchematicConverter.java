package com.zixiken.dimdoors.shared.util;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robijnvogel
 */
public class SchematicConverter {

    private static final String[] OLDDIMDOORBLOCKNAMES = {
            "Dimensional Door",
            "Fabric of Reality",
            "transientDoor", //only used in the two old Overworld gateway (worldgen) structures
            "Warp Door"};
    private static final String[] NEWDIMDOORBLOCKNAMES = {
            "dimensional_door",
            "fabric", //[type=fabric] is the default blockstate
            "transient_dimensional_door",
            "warp_dimensional_door"};

    public static Schematic loadOldDimDoorSchematicFromNBT(NBTTagCompound nbt, String parName) { //@todo, maybe make this a separate class, so values can be final so they HAVE TO  be set in a newly designed constructor?
        Schematic schematic = new Schematic();

        //schematic.version = 1; //already the default value
        //schematic.author = "DimDoors"; //already the default value
        schematic.schematicName = parName.equals("") ? "Auto-converted-DimDoors-for-MC-1.7.10-schematic" : parName;
        schematic.creationDate = System.currentTimeMillis();
        schematic.requiredMods = new String[]{DimDoors.MODID};

        schematic.width = nbt.getShort("Width");
        schematic.height = nbt.getShort("Height");
        schematic.length = nbt.getShort("Length");
        //schematic.offset = new int[]{0, 0, 0}; //already the default value

        byte[] blockIntArray = nbt.getByteArray("Blocks");
        if (nbt.hasKey("Palette")) {
            NBTTagList paletteNBT = (NBTTagList) nbt.getTag("Palette");
            for (int i = 0; i < paletteNBT.tagCount(); i++) {
                //DimDoors.log.info("reading pallete from schematic... i = " + i);
                String blockString = paletteNBT.getStringTagAt(i);
                boolean isAncientFabric = false;
                if (blockString.startsWith("dimdoors")) {
                    String dimdoorsBlockName = blockString.split(":")[1];
                    if (dimdoorsBlockName.equals("Fabric of RealityPerm")) { //only special case, because this is now another state of another block
                        isAncientFabric = true;
                    } else {
                        dimdoorsBlockName = convertOldDimDoorsBlockNameToNewDimDoorsBlockName(dimdoorsBlockName);
                        blockString = "dimdoors:" + dimdoorsBlockName;
                    }
                }
                IBlockState blockstate;
                if (!isAncientFabric) {
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockString));
                    blockstate = block.getDefaultState();
                } else {
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("dimdoors:FABRIC"));
                    blockstate = Schematic.getBlockStateWithProperties(block, new String[]{"type=ancient"});
                }
                schematic.pallette.add(blockstate);
            }
        } else {
            byte[] addId = nbt.getByteArray("AddBlocks");
            Map<Integer, Byte> palletteMap = new HashMap<>(); // block ID -> pallette index
            byte currentPalletteIndex = 0;
            for (int i = 0; i < blockIntArray.length; i++) {
                int id;
                if (i >> 1 >= addId.length) {
                    id = (short) (blockIntArray[i] & 0xFF);
                } else if ((i & 1) == 0) {
                    id = (short) (((addId[i >> 1] & 0x0F) << 8) + (blockIntArray[i] & 0xFF));
                } else {
                    id = (short) (((addId[i >> 1] & 0xF0) << 4) + (blockIntArray[i] & 0xFF));
                }
                if (palletteMap.containsKey(id)) {
                    blockIntArray[i] = palletteMap.get(id);
                } else {
                    Block block = Block.getBlockById(id);
                    switch (id) {
                        case 1975:
                            block = ModBlocks.WARP_DIMENSIONAL_DOOR;
                            break;
                        case 1970:
                            block = ModBlocks.DIMENSIONAL_DOOR;
                            break;
                        case 1979:
                            block = ModBlocks.TRANSIENT_DIMENSIONAL_DOOR;
                            break;
                    }
                    //if (id != 0  && block.getRegistryName().toString().equals("minecraft:air")) throw new RuntimeException("Change conversion code!");
                    schematic.pallette.add(block.getDefaultState());
                    palletteMap.put(id, currentPalletteIndex);
                    blockIntArray[i] = currentPalletteIndex;
                    currentPalletteIndex++;
                }
            }
        }

        byte[] dataIntArray = nbt.getByteArray("Data");
        schematic.blockData = new int[schematic.width][schematic.height][schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    int blockInt = blockIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                    int metadata = dataIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md

                    IBlockState baseState = schematic.pallette.get(blockInt); //this is the default blockstate except for ancient fabric
                    if (baseState == baseState.getBlock().getDefaultState()) { //should only be false if {@code baseState} is ancient fabric
                        IBlockState additionalState = baseState.getBlock().getStateFromMeta(metadata); // TODO: this was getStateFromMeta(metadata), but that method got deprecated and just calls getDefaultState(). Is this right?
                        if (schematic.pallette.contains(additionalState)) { //check whether or not this blockstate is already in the list
                            blockInt = schematic.pallette.indexOf(additionalState);
                        } else {
                            schematic.pallette.add(additionalState);
                            // DimDoors.log.info("New blockstate detected. Original blockInt = " + blockInt + " and baseState is " + baseState);
                            blockInt = schematic.pallette.size() - 1;
                        }
                    } else { //if this is ancient fabric
                        // DimDoors.log.info("Non-default blockstate in palette detected. Original blockInt = " + blockInt + " and baseState is " + baseState.toString()); //@todo should only print a line on load of ancient fabric
                        blockInt = schematic.pallette.indexOf(baseState);
                    }
                    schematic.blockData[x][y][z] = blockInt;
                }
            }
        }
        schematic.paletteMax = schematic.pallette.size() - 1;

        NBTTagList tileEntitiesTagList = (NBTTagList) nbt.getTag("TileEntities");
        for (int i = 0; i < tileEntitiesTagList.tagCount(); i++) {
            NBTTagCompound tileEntityTagCompound = tileEntitiesTagList.getCompoundTagAt(i);
            schematic.tileEntities.add(tileEntityTagCompound);
        }

        return schematic;
    }

    private static String convertOldDimDoorsBlockNameToNewDimDoorsBlockName(String dimdoorsBlockName) {
        if (OLDDIMDOORBLOCKNAMES.length != NEWDIMDOORBLOCKNAMES.length) {
            DimDoors.log.error("The array of old DimDoors block names somehow isn't the same length as the array of new names, therefore the dimdoors blocks in this schematic will not be loaded. This is a bug in the DimDoors mod itself.");
            return null;
        }

        int i = 0;
        for (; i < OLDDIMDOORBLOCKNAMES.length; i++) {
            if (OLDDIMDOORBLOCKNAMES[i].equals(dimdoorsBlockName)) {
                return NEWDIMDOORBLOCKNAMES[i];
            } else {
                if (i == OLDDIMDOORBLOCKNAMES.length - 1) {
                    DimDoors.log.warn(dimdoorsBlockName + " as an old dimdoors block name is unknown.");
                }
            }
        }
        return null;
    }
}
