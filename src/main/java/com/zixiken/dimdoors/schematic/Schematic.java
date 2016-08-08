package com.zixiken.dimdoors.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Represents an MC schematic and provides functions for loading, storing, and manipulating schematics.
 * This functionality has no dependencies to Dimensional Doors.
 */
public class Schematic {

	protected BlockPos volume;
	protected IBlockState[] states;
	protected NBTTagList tileEntities;

	protected Schematic(BlockPos volume, IBlockState[] state, NBTTagList tileEntities) {
		this.volume = volume;
		this.states = state;
		this.tileEntities = tileEntities;
	}
	
	protected Schematic(Schematic source) {
		//Shallow copy constructor - critical for code reuse in derived classes since
		//source's fields will be inaccessible if the derived class is in another package.
		this.volume = source.volume;
		this.states = source.states;
		this.tileEntities = source.tileEntities;
	}

	public int calculateIndex(BlockPos pos) {
		if (pos.getX() < 0 || pos.getX() >= volume.getX())
			throw new IndexOutOfBoundsException("x must be non-negative and less than width");
		if (pos.getY() < 0 || pos.getY() >= volume.getY())
			throw new IndexOutOfBoundsException("y must be non-negative and less than height");
		if (pos.getZ() < 0 || pos.getZ() >= volume.getZ())
			throw new IndexOutOfBoundsException("z must be non-negative and less than length");

		return (pos.getY() * volume.getX() * volume.getZ() + pos.getZ() * volume.getX() + pos.getX());
	}
	
	public BlockPos calculatePoint(int index) {
		int y = index / (volume.getX() * volume.getZ());
		index -= y * volume.getX() * volume.getZ();
		int z = index / volume.getX();
		index -= z * volume.getX();
		int x = index;
		
		return new BlockPos(x, y, z);
	}
	
	public int calculateIndexBelow(int index) {
		return index - (volume.getX() * volume.getZ());
	}

	public BlockPos getVolume() {
		return volume;
	}
	
	public Block getBlock(BlockPos pos) {
		return states[calculateIndex(pos)].getBlock();
	}

	public IBlockState getBlockState(BlockPos pos) {
		return states[calculateIndex(pos)];
	}

	public NBTTagList getTileEntities() {
		return (NBTTagList) tileEntities.copy();
	}

	public static Schematic readFromFile(String schematicPath) throws FileNotFoundException, InvalidSchematicException {
		return readFromFile(new File(schematicPath));
	}

	public static Schematic readFromFile(File schematicFile) throws FileNotFoundException, InvalidSchematicException {
		// There is no resource leak... readFromStream() closes the stream TWICE.
		return readFromStream(new FileInputStream(schematicFile));
	}

	public static Schematic readFromResource(String resourcePath) throws InvalidSchematicException {
		InputStream schematicStream = Schematic.class.getResourceAsStream(resourcePath);
		return readFromStream(schematicStream);
	}

	public static Schematic readFromStream(InputStream schematicStream) throws InvalidSchematicException {
		short width;
		short height;
		short length;
		int volume;
		int pairs;

		IBlockState[] state = null;			//block state
		NBTTagList tileEntities = null;	//storage for tile entities in NBT form
		NBTTagCompound schematicTag;	//the NBT data extracted from the schematic file

		try {
			try {
				schematicTag = CompressedStreamTools.readCompressed(schematicStream);
				schematicStream.close(); //readCompressed() probably closes the stream anyway, but close again to be sure.
			}
			catch (Exception ex) {
				ex.printStackTrace();
				throw new InvalidSchematicException("The schematic could not be decoded.");
				
			}

			//load size of schematic to generate
			width = schematicTag.getShort("Width");
			height = schematicTag.getShort("Height");
			length = schematicTag.getShort("Length");
			volume = width * length * height;

			if (width < 0)
				throw new InvalidSchematicException("The schematic cannot have a negative width.");
			if (height < 0)
				throw new InvalidSchematicException("The schematic cannot have a negative height.");
			if (length < 0)
				throw new InvalidSchematicException("The schematic cannot have a negative length.");

			//load block info
			int[] temp = schematicTag.getIntArray("Data");
			state = new IBlockState[temp.length];

			for(int i = 0; i < temp.length; i++)
				state[i] = Block.getStateById(temp[i]);

			if (volume != state.length)
				throw new InvalidSchematicException("The schematic has IBlockState for fewer blocks than its dimensions indicate.");

			//Get the list of tile entities
			tileEntities = schematicTag.getTagList("TileEntities", 10);
			
			Schematic result = new Schematic(new BlockPos(width, height, length), state, tileEntities);
			return result;
		}
		catch (InvalidSchematicException ex)
		{
			//Throw the exception again to pass it to the caller.
			throw ex;
		}
		catch (Exception ex)
		{
			throw new InvalidSchematicException("An unexpected error occurred while trying to decode the schematic.", ex);
		}
	}

	public static Schematic copyFromWorld(World world, BlockPos pos, BlockPos volume, boolean doCompactBounds) {
		if (doCompactBounds) {
			//Adjust the vertical bounds to reasonable values if necessary
			int worldHeight = world.getHeight();
			int fixedY = (pos.getY() < 0) ? 0 : pos.getY();
			int fixedHeight = volume.getY() + pos.getY() - fixedY;

			if (fixedHeight + fixedY >= worldHeight) {
				fixedHeight = worldHeight - fixedY;
			}

			//Compact the area to be copied to remove empty borders
			CompactBoundsOperation compactor = new CompactBoundsOperation();
			compactor.apply(world, new BlockPos(pos.getX(), fixedY, pos.getZ()), new BlockPos(volume.getX(), fixedHeight, volume.getZ()));
			BlockPos minCorner = compactor.getMinCorner();
			BlockPos maxCorner = compactor.getMaxCorner();

			BlockPos compact = maxCorner.subtract(minCorner).add(1,1,1);

			return copyFromWorld(world, minCorner, compact);
		} else {
			return copyFromWorld(world, pos, volume);
		}
	}

	private static Schematic copyFromWorld(World world, BlockPos pos, BlockPos volume) {
		//Short and sweet ^_^
		WorldCopyOperation copier = new WorldCopyOperation();
		copier.apply(world, pos, volume);
		return new Schematic(volume, copier.getBlockState(), copier.getTileEntities());
	}

	public NBTTagCompound writeToNBT() {
		return writeToNBT(true);
	}

	protected NBTTagCompound writeToNBT(boolean copyTileEntities) {
		return writeToNBT(volume, states, tileEntities, copyTileEntities);
	}
	
	protected static NBTTagCompound writeToNBT(BlockPos volume, IBlockState[] state, NBTTagList tileEntities, boolean copyTileEntities) {
		//This is the main storage function. Schematics are really compressed NBT tags, so if we can generate
		//the tags, most of the work is done. All the other storage functions will rely on this one.

		NBTTagCompound schematicTag = new NBTTagCompound();

		schematicTag.setInteger("Width", volume.getX());
		schematicTag.setInteger("Length", volume.getZ());
		schematicTag.setInteger("Height", volume.getY());

		schematicTag.setTag("Entities", new NBTTagList());
		schematicTag.setString("Materials", "Alpha");

		int[] temp = new int[state.length];

		for(int i = 0; i < temp.length; i++)
			temp[i] = Block.getStateId(state[i]);

		schematicTag.setIntArray("Data", temp);

		if (copyTileEntities) {
			//Used when the result of this function will be passed outside this class.
			//Avoids exposing the private field to external modifications.
			schematicTag.setTag("TileEntities", tileEntities.copy());
		} else {
			//Used when the result of this function is for internal use.
			//It's more efficient not to copy the tags unless it's needed.
			schematicTag.setTag("TileEntities", tileEntities);
		}
		return schematicTag;
	}

	public void writeToFile(String schematicPath) throws IOException {
		writeToFile(new File(schematicPath));
	}

	public void writeToFile(File schematicFile) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(schematicFile);
		CompressedStreamTools.writeCompressed(writeToNBT(false), outputStream);
		//writeCompressed() probably closes the stream on its own - call close again just in case.
		//Closing twice will not throw an exception.
		outputStream.close();
	}
	
	public boolean applyFilter(SchematicFilter filter) {
		return filter.apply(this, this.states);
	}
	
	public void copyToWorld(World world, BlockPos pos, boolean notifyClients, boolean ignoreAir) {
		if (notifyClients) {
			copyToWorld(world, pos, new WorldBlockSetter(false, true, ignoreAir));
		} else {
			copyToWorld(world, pos, new ChunkBlockSetter(ignoreAir));
		}
	}
	
	protected void copyToWorld(World world, BlockPos pos, IBlockSetter blockSetter) {
		//This isn't implemented as a WorldOperation because it doesn't quite fit the structure of those operations.
		//It's not worth the trouble in this case.
		int index;
		int count;
		int dx, dy, dz;

		//Copy blocks and metadata into the world
		index = 0;
		for (dy = 0; dy < volume.getY(); dy++) {
			for (dz = 0; dz < volume.getZ(); dz++) {
				for (dx = 0; dx < volume.getX(); dx++) {
					blockSetter.setBlock(world, pos.add(dx, dy, dz), states[index]);
					index++;
				}
			}
		}

		//Copy tile entities into the world
		count = tileEntities.tagCount();
		for (index = 0; index < count; index++)
		{
			NBTTagCompound tileTag = (NBTTagCompound) tileEntities.getCompoundTagAt(index);
			//Rewrite its location to be in world coordinates
			dx = tileTag.getInteger("x") + pos.getX();
			dy = tileTag.getInteger("y") + pos.getY();
			dz = tileTag.getInteger("z") + pos.getZ();
			tileTag.setInteger("x", dx);
			tileTag.setInteger("y", dy);
			tileTag.setInteger("z", dz);
			//Load the tile entity and put it in the world
			world.setTileEntity(new BlockPos(dx, dy, dz), TileEntity.createAndLoadEntity(tileTag));
		}
	}
}
