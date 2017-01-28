/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.util;

import com.zixiken.dimdoors.DimDoors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 *
 * @author Robijnvogel
 */
public class Schematic {

    private static final String[] OLDDIMDOORBLOCKNAMES = new String[]{
        "Dimensional Door",
        "Fabric of Reality",
        "transientDoor", //only used in the two old Overworld gateway (worldgen) structures
        "Warp Door"};

    private static final String[] NEWDIMDOORBLOCKNAMES = new String[]{
        "blockDimDoor",
        "blockDimWall", //I think [type=fabric] is the default blockstate
        "blockDimDoorTransient",
        "blockDimDoorWarp"}; //@todo make these lists complete, possibly with specific blockstate as well?

    int version = Integer.parseInt("1"); //@todo set in build.gradle ${spongeSchematicVersion}
    String author = "DimDoors"; //@todo set in build.gradle ${modID}
    String schematicName = "Unknown";
    long creationDate;
    String[] requiredMods = new String[0];
    short width;
    short height;
    short length;
    int[] offset = new int[]{0, 0, 0};
    int paletteMax;
    List<IBlockState> pallette = new ArrayList();
    int[][][] blockData; //[x][y][z]
    List<NBTTagCompound> tileEntities = new ArrayList();

    private Schematic() {
    }

    public static Schematic loadFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("Version")) {
            return loadOldDimDoorSchematicFromNBT(nbt);
        }

        Schematic schematic = new Schematic();
        schematic.version = nbt.getInteger("Version"); //Version is required

        schematic.creationDate = System.currentTimeMillis();
        if (nbt.hasKey("Metadata")) { //Metadata is not required
            NBTTagCompound metadataCompound = nbt.getCompoundTag("Metadata").getCompoundTag(".");
            if (nbt.hasKey("Author")) { //Author is not required
                schematic.author = metadataCompound.getString("Author");
            }
            if (nbt.hasKey("Name")) { //Name is not required
                schematic.schematicName = metadataCompound.getString("Name");
            }
            if (nbt.hasKey("Date")) { //Date is not required
                schematic.creationDate = metadataCompound.getLong("Date");
            }
            if (nbt.hasKey("RequiredMods")) { //RequiredMods is not required (ironically)
                NBTTagList requiredModsTagList = ((NBTTagList) metadataCompound.getTag("RequiredMods"));
                schematic.requiredMods = new String[requiredModsTagList.tagCount()];
                for (int i = 0; i < requiredModsTagList.tagCount(); i++) {
                    schematic.requiredMods[i] = requiredModsTagList.getStringTagAt(i);
                }
            }
        }

        //@todo, check if the required mods are loaded, otherwise abort
        schematic.width = nbt.getShort("Width"); //Width is required
        schematic.height = nbt.getShort("Height"); //Height is required
        schematic.length = nbt.getShort("Length"); //Length is required
        if (nbt.hasKey("Offset")) { //Offset is not required
            schematic.offset = nbt.getIntArray("Offset");
        }

        NBTTagCompound paletteNBT = nbt.getCompoundTag("Palette"); //Palette is not required, however since we assume that the schematic contains at least some blocks, we can also assume that thee has to be a Palette
        Map<Integer, String> paletteMap = new HashMap();
        for (String key : paletteNBT.getKeySet()) {
            int paletteID = paletteNBT.getInteger(key);
            paletteMap.put(paletteID, key); //basically use the reversed order (key becomes value and value becomes key)
        }
        for (int i = 0; i <= paletteMap.size(); i++) {
            String blockStateString = paletteMap.get(i);
            char lastBlockStateStringChar = blockStateString.charAt(blockStateString.length() - 1);
            String blockString;
            String stateString;
            if (lastBlockStateStringChar == ']') {
                String[] blockAndStateStrings = blockStateString.split("[");
                blockString = blockAndStateStrings[0];
                stateString = blockAndStateStrings[1];
                stateString = stateString.substring(0, stateString.length() - 1); //remove the "]" at the end
            } else {
                blockString = blockStateString;
                stateString = "";
            }
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockString));

            IBlockState blockstate = block.getDefaultState();
            if (!stateString.equals("")) {
                String[] properties = stateString.split(",");
                blockstate = getBlockStateWithProperties(block, properties);
            } else {
            }
            schematic.pallette.add(blockstate);
        }
        if (nbt.hasKey("PaletteMax")) { //PaletteMax is not required
            schematic.paletteMax = nbt.getInteger("PaletteMax");
        } else {
            schematic.paletteMax = schematic.pallette.size() - 1;
        }

        byte[] blockDataIntArray = nbt.getByteArray("BlockData"); //BlockData is required
        schematic.blockData = new int[schematic.width][schematic.height][schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    schematic.blockData[x][y][z] = blockDataIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                }
            }
        }

        if (nbt.hasKey("TileEntities")) { //TileEntities is not required
            NBTTagList tileEntitiesTagList = (NBTTagList) nbt.getTag("TileEntities");
            for (int i = 0; i < tileEntitiesTagList.tagCount(); i++) {
                NBTTagCompound tileEntityTagCompound = tileEntitiesTagList.getCompoundTagAt(i);
                schematic.tileEntities.add(tileEntityTagCompound);
            }
        }

        return schematic;
    }

    public static NBTTagCompound saveToNBT(Schematic schematic) {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("Version", schematic.version);
        NBTTagCompound metadataCompound = new NBTTagCompound();
        metadataCompound.setString("Author", schematic.author);
        metadataCompound.setString("Name", schematic.schematicName);
        metadataCompound.setLong("Date", schematic.creationDate);
        NBTTagList requiredModsTagList = new NBTTagList();
        for (String requiredMod : schematic.requiredMods) {
            requiredModsTagList.appendTag(new NBTTagString(requiredMod));
        }
        metadataCompound.setTag("RequiredMods", requiredModsTagList);
        nbt.setTag("Metadata", metadataCompound);

        nbt.setShort("Width", schematic.width);
        nbt.setShort("Height", schematic.height);
        nbt.setShort("Length", schematic.length);
        nbt.setIntArray("Offset", schematic.offset);
        nbt.setInteger("PaletteMax", schematic.paletteMax);

        NBTTagCompound paletteNBT = new NBTTagCompound();
        Map<Integer, String> paletteMap = new HashMap();
        for (int i = 0; i < schematic.pallette.size(); i++) {
            IBlockState state = schematic.pallette.get(i);
            String blockStateString = getBlockStateStringFromState(state);
            paletteNBT.setInteger(blockStateString, i);
        }
        nbt.setTag("Palette", paletteNBT);

        byte[] blockDataIntArray = new byte[schematic.width * schematic.height * schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    blockDataIntArray[x + z * schematic.width + y * schematic.width * schematic.length] = (byte) schematic.blockData[x][y][z]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                }
            }
        }
        nbt.setByteArray("BlockData", blockDataIntArray);

        NBTTagList tileEntitiesTagList = new NBTTagList();
        for (int i = 0; i < schematic.tileEntities.size(); i++) {
            NBTTagCompound tileEntityTagCompound = schematic.tileEntities.get(i);
            tileEntitiesTagList.appendTag(tileEntityTagCompound);
        }
        nbt.setTag("TileEntities", tileEntitiesTagList);

        return nbt;
    }

    private static IBlockState getBlockStateWithProperties(Block block, String[] properties) {
        Map<String, String> propertyAndBlockStringsMap = new HashMap();
        for (int i = 0; i < properties.length; i++) {
            String propertyString = properties[i];
            String[] propertyAndBlockStrings = propertyString.split("=");
            propertyAndBlockStringsMap.put(propertyAndBlockStrings[0], propertyAndBlockStrings[1]);
        }
        BlockStateContainer container = block.getBlockState();
        IBlockState chosenState = block.getDefaultState();
        for (Entry<String, String> entry : propertyAndBlockStringsMap.entrySet()) {
            IProperty<?> property = container.getProperty(entry.getKey());
            if (property != null) {
                Comparable<?> value = null;
                for (Comparable<?> object : property.getAllowedValues()) {
                    if (object.equals(entry.getValue())) {
                        value = object;
                        break;
                    }
                }
                if (value != null) {
                    chosenState = chosenState.withProperty((IProperty) property, (Comparable) value);
                }
            }
        }
        return chosenState;
    }

    private static String getBlockStateStringFromState(IBlockState state) {
        String blockNameString = state.getBlock().getLocalizedName(); //@todo, check if this is the correct method
        String blockStateString = "";
        String totalString;
        if (state.equals(state.getBlock().getDefaultState())) {
            totalString = blockNameString;
        } else {
            BlockStateContainer container = state.getBlock().getBlockState();
            container.getProperties();
            for (IProperty property : container.getProperties()) {
                String firstHalf = property.getName();
                String secondHalf = state.getProperties().get(property).toString();
                String propertyString = firstHalf + "=" + secondHalf;
                blockStateString += propertyString + ",";
            }
            blockStateString = blockStateString.substring(0, blockStateString.length() - 1); //removes the last comma
            totalString = blockNameString + "[" + blockStateString + "]";
        }
        return totalString;
    }

    public int getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String[] getRequiredMods() {
        return requiredMods;
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public short getLength() {
        return length;
    }

    public int[] getOffset() {
        return offset;
    }

    public int getPaletteMax() {
        return paletteMax;
    }

    public List<IBlockState> getPallette() {
        return pallette;
    }

    public int[][][] getBlockData() {
        return blockData;
    }

    public List<NBTTagCompound> getTileEntities() {
        return tileEntities;
    }

    public static Schematic loadOldDimDoorSchematicFromNBT(NBTTagCompound nbt) { //@todo, maybe make this a separate class, so values can be final so they HAVE TO  be set in a newly designed constructor
        Schematic schematic = new Schematic();

        //schematic.version = 1; //already the default value
        //schematic.author = "DimDoors"; //already the default value
        schematic.schematicName = "This schematic was converted from an MC 1.7.10 DimDoors schematic";
        schematic.creationDate = System.currentTimeMillis();
        schematic.requiredMods = new String[]{DimDoors.MODID};

        schematic.width = nbt.getShort("Width");
        schematic.height = nbt.getShort("Height");
        schematic.length = nbt.getShort("Length");
        //schematic.offset = new int[]{0, 0, 0}; //already the default value

        NBTTagList paletteNBT = (NBTTagList) nbt.getTag("Palette");
        for (int i = 0; i <= paletteNBT.tagCount(); i++) {
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
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("blockDimWall"));
                blockstate = getBlockStateWithProperties(block, new String[]{"type=ancient"});
            }
            schematic.pallette.add(blockstate);
        }

        //check whether or not this blockstate is already in the list
        byte[] blockIntArray = nbt.getByteArray("Blocks");
        byte[] dataIntArray = nbt.getByteArray("Data");
        schematic.blockData = new int[schematic.width][schematic.height][schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    int blockInt = blockIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                    int metadata = dataIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                    if (metadata != 0) {
                        IBlockState basesState = schematic.pallette.get(blockInt); //this is needed for the various "types" that a block can have
                        IBlockState additionalState = basesState.getBlock().getStateFromMeta(metadata);
                        for (IProperty property : basesState.getProperties().keySet()) {
                            Comparable value = basesState.getProperties().get(property);
                            additionalState = additionalState.withProperty(property, value);
                        }
                        if (schematic.pallette.contains(additionalState)) {
                            blockInt = schematic.pallette.indexOf(additionalState);
                        } else {
                            schematic.pallette.add(additionalState);
                            blockInt = schematic.pallette.size() - 1;
                        }
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
            DimDoors.warn(Schematic.class, "The array of old dimdoors block names, somehow isn't the same length as the array of new names, therefore the dimdoors blocks in this schematic will not be loaded.");
            return null;
        }

        int i = 0;
        for (; i < OLDDIMDOORBLOCKNAMES.length; i++) {
            if (OLDDIMDOORBLOCKNAMES[i].equals(dimdoorsBlockName)) {
                return NEWDIMDOORBLOCKNAMES[i];
            } else {
                if (i == OLDDIMDOORBLOCKNAMES.length - 1) {
                    DimDoors.warn(Schematic.class, dimdoorsBlockName + " as an old dimdoors block name is unknown.");
                }
            }
        }
        return null;
    }
}
