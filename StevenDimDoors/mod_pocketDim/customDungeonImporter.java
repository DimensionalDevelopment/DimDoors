package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockStairs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.ChestGenHooks;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ByteArrayTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.CompoundTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.NBTOutputStream;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ShortTag;
 
public class customDungeonImporter 
{
	NBTTagCompound nbtdata= new NBTTagCompound();
	
	public static HashMap<Integer, LinkData> customDungeonStatus = new HashMap<Integer, LinkData>();
	
	
	public static DungeonGenerator exportDungeon(World world, int xI, int yI, int zI, String file)
	{
		int xMin;
		int yMin;
		int zMin;
		
		int xMax;
		int yMax;
		int zMax;
		
		xMin=xMax=xI;
		yMin=yMax=yI;
		zMin=zMax=zI;
		
		for(int count=0;count<50;count++)
		{
		
			if(world.getBlockId(xMin, yI, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				xMin--;
			}
			if(world.getBlockId(xI, yMin, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				yMin--;
			}
			if(world.getBlockId(xI, yI, zMin)!=mod_pocketDim.blockDimWallPermID)
			{
				zMin--;
			}
			if(world.getBlockId(xMax, yI, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				xMax++;
			}
			if(world.getBlockId(xI, yMax, zI)!=mod_pocketDim.blockDimWallPermID)
			{
				yMax++;
			}
			if(world.getBlockId(xI, yI, zMax)!=mod_pocketDim.blockDimWallPermID)
			{
				zMax++;
			}
		}
		
		short width =(short) (xMax-xMin);
		short height= (short) (yMax-yMin);
		short length= (short) (zMax-zMin);
		
	
		
		 byte[] blocks = new byte[width * height * length];
	        byte[] addBlocks = null;
	        byte[] blockData = new byte[width * height * length];

	        for (int x = 0; x < width; ++x) 
	        {
	            for (int y = 0; y < height; ++y) {
	                for (int z = 0; z < length; ++z) {
	                    int index = y * width * length + z * width + x;
	                    int blockID = world.getBlockId(x+xMin, y+yMin, z+zMin);
	                    int meta= world.getBlockMetadata(x+xMin, y+yMin, z+zMin);
	                    // Save 4096 IDs in an AddBlocks section
	                    if (blockID > 255) {
	                        if (addBlocks == null) { // Lazily create section
	                            addBlocks = new byte[(blocks.length >> 1) + 1];
	                        }

	                        addBlocks[index >> 1] = (byte) (((index & 1) == 0) ?
	                                addBlocks[index >> 1] & 0xF0 | (blockID >> 8) & 0xF
	                                : addBlocks[index >> 1] & 0xF | ((blockID >> 8) & 0xF) << 4);
	                    }

	                    blocks[index] = (byte) blockID;
	                    blockData[index] = (byte) meta;
	                }
	            }
	        }
	        /**
	         *   
	         *   this.nbtdata.setShort("Width", width);
	        this.nbtdata.setShort("Height", height);
	        this.nbtdata.setShort("Length", length);
	        
	    	 this.nbtdata.setByteArray("Blocks", blocks);
        	 this.nbtdata.setByteArray("Data", blockData);
	         */
	        
	        HashMap schematic = new HashMap();
	        schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
	        schematic.put("Data", new ByteArrayTag("Data", blockData));
	        
	        schematic.put("Width", new ShortTag("Width", (short) width));
	        schematic.put("Length", new ShortTag("Length", (short) length));
	        schematic.put("Height", new ShortTag("Height", (short) height));
	        if (addBlocks != null) {
	            schematic.put("AddBlocks", new ByteArrayTag("AddBlocks", addBlocks));
	        }
	      
	        CompoundTag schematicTag = new CompoundTag("Schematic", schematic);
	        try{
	        	
	        
        	 NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(file));
             stream.writeTag(schematicTag);
             stream.close();
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        return new DungeonGenerator(0,file,true);
	}
	
}