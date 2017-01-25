/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.util;

import java.util.ArrayList;
import java.util.Collection;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 *
 * @author Robijnvogel
 */
public class Schematic {

    int version;
    String author;
    String schematicName;
    long creationDate;
    String[] requiredMods;
    short width;
    short height;
    short length;
    int[] offset = new int[3];
    int paletteMax;
    List<IBlockState> pallette = new ArrayList();
    int[][][] blockData; //[x][y][z]
    List<NBTTagCompound> tileEntities = new ArrayList();

    private Schematic() {
    }

    public static Schematic loadFromNBT(NBTTagCompound nbt) {
        Schematic schematic = new Schematic();

        schematic.version = nbt.getInteger("Version");
        NBTTagCompound metadataCompound = nbt.getCompoundTag("Metadata").getCompoundTag(".");
        schematic.author = metadataCompound.getString("Author");
        schematic.schematicName = metadataCompound.getString("Name");
        schematic.creationDate = metadataCompound.getInteger("Date");
        NBTTagList requiredModsTagList = ((NBTTagList) metadataCompound.getTag("RequiredMods"));
        schematic.requiredMods = new String[requiredModsTagList.tagCount()];
        for (int i = 0; i < requiredModsTagList.tagCount(); i++) {
            schematic.requiredMods[i] = requiredModsTagList.getStringTagAt(i);
        }

        //@todo, check if the needed mods are loade; otherwise abort
        schematic.width = nbt.getShort("Width");
        schematic.height = nbt.getShort("Height");
        schematic.length = nbt.getShort("Length");
        schematic.offset = nbt.getIntArray("Offset");
        schematic.paletteMax = nbt.getInteger("PaletteMax");

        NBTTagCompound paletteNBT = nbt.getCompoundTag("Palette");
        Map<Integer, String> paletteMap = new HashMap();
        for (String key : paletteNBT.getKeySet()) {
            int paletteID = paletteNBT.getInteger(key);
            paletteMap.put(paletteID, key); //basically use the reversed order
        }
        for (int i = 0; i <= schematic.paletteMax; i++) {
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
                blockstate = getBlockStateWithProperties(block, properties); //@todo get the blockState from string
            } else {
            }
            schematic.pallette.add(blockstate);
        }

        byte[] blockDataIntArray = nbt.getByteArray("BlockData");
        schematic.blockData = new int[schematic.width][schematic.height][schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    schematic.blockData[x][y][z] = blockDataIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                }
            }
        }

        NBTTagList tileEntitiesTagList = (NBTTagList) nbt.getTag("TileEntities");
        for (int i = 0; i < tileEntitiesTagList.tagCount(); i++) {
            NBTTagCompound tileEntityTagCompound = tileEntitiesTagList.getCompoundTagAt(i);
            schematic.tileEntities.add(tileEntityTagCompound);
        }

        return schematic;
    }

    private static IBlockState getBlockStateWithProperties(Block block, String[] properties) {
        Map<String, String> propertyAndBlockStringsMap = new HashMap();
        for (int i = 0; i < properties.length; i++) {
            String propertyString = properties[i];
            String[] propertyAndBlockStrings = propertyString.split("=");
            propertyAndBlockStringsMap.put(propertyAndBlockStrings[0], propertyAndBlockStrings[1]);
        }
        BlockStateContainer container = block.getBlockState();
        Collection<IBlockState> possibleBlockStates = container.getValidStates();
int newInt = possibleBlockStates.size();
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
}
