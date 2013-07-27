package StevenDimDoors.mod_pocketDim.schematic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.Point3D;

public class Schematic {
	
	protected short width;
	protected short height;
	protected short length;
	
	protected short[] blocks;
	protected byte[] metadata;
	protected NBTTagList tileEntities = new NBTTagList();

	protected Schematic(short width, short height, short length, short[] blocks, byte[] metadata, NBTTagList tileEntities)
	{
		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = blocks;
		this.metadata = metadata;
		this.tileEntities = tileEntities;
	}
	
	private int calculateIndex(int x, int y, int z)
	{
		if (x < 0 || x >= width)
			throw new IndexOutOfBoundsException("x must be non-negative and less than width");
		if (y < 0 || y >= height)
			throw new IndexOutOfBoundsException("y must be non-negative and less than height");
		if (z < 0 || z >= length)
			throw new IndexOutOfBoundsException("z must be non-negative and less than length");
		
		return (y * width * length + z * width + x);
	}
	
	public short getBlockID(int x, int y, int z)
	{
		return blocks[calculateIndex(x, y, z)];
	}
	
	public byte getBlockMetadata(int x, int y, int z)
	{
		return metadata[calculateIndex(x, y, z)];
	}
	
	public NBTTagList getTileEntities()
	{
		return (NBTTagList) tileEntities.copy();
	}
	
	public static Schematic readFromFile()
	{
		throw new UnsupportedOperationException();
	}
	
	public static Schematic copyFromWorld(World world, int x, int y, int z, short width, short height, short length, boolean doCompactBounds)
	{
		if (doCompactBounds)
		{
			//Adjust the vertical bounds to reasonable values if necessary
			int worldHeight = world.getHeight();
			int fixedY = (y < 0) ? 0 : y;
			int fixedHeight = height + y - fixedY;

			if (fixedHeight + fixedY >= worldHeight)
			{
				fixedHeight = worldHeight - fixedY;
			}
			
			//Compact the area to be copied to remove empty borders
			CompactBoundsOperation compactor = new CompactBoundsOperation();
			compactor.apply(world, x, fixedY, z, width, fixedHeight, length);
			Point3D minCorner = compactor.getMinCorner();
			Point3D maxCorner = compactor.getMaxCorner();
			
			short compactWidth = (short) (maxCorner.getX() - minCorner.getX() + 1);
			short compactHeight = (short) (maxCorner.getY() - minCorner.getY() + 1);
			short compactLength = (short) (maxCorner.getZ() - minCorner.getZ() + 1);
			
			return copyFromWorld(world, minCorner.getX(), minCorner.getY(), minCorner.getZ(),
					compactWidth, compactHeight, compactLength);
		}
		else
		{
			return copyFromWorld(world, x, y, z, width, height, length);
		}
	}
	
	private static Schematic copyFromWorld(World world, int x, int y, int z, short width, short height, short length)
	{
		//Short and sweet ^_^
		WorldCopyOperation copier = new WorldCopyOperation();
		copier.apply(world, x, y, z, width, height, length);
		return new Schematic(width, height, length, copier.getBlockIDs(), copier.getMetadata(), copier.getTileEntities());
	}
	
	private static boolean encodeBlockIDs(short[] blocks, byte[] lowBits, byte[] highBits)
	{
		int index;
		int length = blocks.length - (blocks.length & 1);
		boolean hasHighBits = false;
		for (index = 0; index < length; index += 2)
		{
			highBits[index >> 1] = (byte) (((blocks[index] >> 8) & 0x0F) + ((blocks[index + 1] >> 4) & 0xF0));
			hasHighBits |= (highBits[index >> 1] != 0);
		}
		if (index < blocks.length)
		{
			highBits[index >> 1] = (byte) ((blocks[index] >> 8) & 0x0F);
			hasHighBits |= (highBits[index >> 1] != 0);
		}
		return hasHighBits;
	}
	
	public NBTTagCompound writeToNBT()
	{
		return writeToNBT(true);
	}
	
	private NBTTagCompound writeToNBT(boolean copyTileEntities)
	{
		//This is the main storage function. Schematics are really compressed NBT tags, so if we can generate
		//the tags, most of the work is done. All the other storage functions will rely on this one.
		
		NBTTagCompound schematicTag = new NBTTagCompound("Schematic");

		schematicTag.setShort("Width", width);
		schematicTag.setShort("Length", length);
		schematicTag.setShort("Height", height);
		
		schematicTag.setTag("Entities", new NBTTagList());
		schematicTag.setString("Materials", "Alpha");
		
		byte[] lowBytes = new byte[blocks.length];
		byte[] highBytes = new byte[(blocks.length >> 1) + 1];
		boolean hasExtendedIDs = encodeBlockIDs(blocks, lowBytes, highBytes);
		
		schematicTag.setByteArray("Blocks", lowBytes);
		schematicTag.setByteArray("Data", metadata);
		
		if (hasExtendedIDs)
		{
			schematicTag.setByteArray("AddBlocks", highBytes);
		}
		
		if (copyTileEntities)
		{
			//Used when the result of this function will be passed outside this class.
			//Avoids exposing the private field to external modifications.
			schematicTag.setTag("TileEntities", (NBTTagList) tileEntities.copy());
		}
		else
		{
			//Used when the result of this function is for internal use.
			//It's more efficient not to copy the tags unless it's needed.
			schematicTag.setTag("TileEntities", tileEntities);
		}
		return schematicTag;
	}
	
	public void writeToFile(String schematicPath) throws IOException
	{
		writeToFile(new File(schematicPath));
	}
	
	public void writeToFile(File schematicFile) throws IOException
	{
		FileOutputStream outputStream = new FileOutputStream(schematicFile);
		CompressedStreamTools.writeCompressed(writeToNBT(false), outputStream);
		//writeCompressed() probably closes the stream on its own - call close again just in case.
		//Closing twice will not throw an exception.
		outputStream.close();
	}
}
