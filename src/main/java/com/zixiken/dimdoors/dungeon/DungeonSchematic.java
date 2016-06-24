package com.zixiken.dimdoors.dungeon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.IDimDoor;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.ticking.CustomLimboPopulator;
import com.zixiken.dimdoors.ticking.MobMonolith;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.schematic.BlockRotator;
import com.zixiken.dimdoors.schematic.ChunkBlockSetter;
import com.zixiken.dimdoors.schematic.CompoundFilter;
import com.zixiken.dimdoors.schematic.IBlockSetter;
import com.zixiken.dimdoors.schematic.InvalidSchematicException;
import com.zixiken.dimdoors.schematic.Schematic;
import com.zixiken.dimdoors.schematic.WorldBlockSetter;
import com.zixiken.dimdoors.util.Point4D;

public class DungeonSchematic extends Schematic {

	private static final int NETHER_DIMENSION_ID = -1;
	
	private EnumFacing orientation;
	private BlockPos entranceDoorLocation;
	private ArrayList<BlockPos> exitDoorLocations;
	private ArrayList<BlockPos> dimensionalDoorLocations;
	private ArrayList<BlockPos> monolithSpawnLocations;
	private ArrayList<Block> modBlockFilterExceptions;

	private DungeonSchematic(Schematic source) {
		super(source);
        modBlockFilterExceptions = new ArrayList<Block>(5);
        modBlockFilterExceptions.add(DimDoors.blockDimWall);
        modBlockFilterExceptions.add(DimDoors.blockDimWallPerm);
        modBlockFilterExceptions.add(DimDoors.warpDoor);
        modBlockFilterExceptions.add(DimDoors.dimensionalDoor);
        modBlockFilterExceptions.add(DimDoors.transientDoor);
	}
	
	public EnumFacing getOrientation() {
		return orientation;
	}
	
	public BlockPos getEntranceDoorLocation() {
		return (entranceDoorLocation != null) ? entranceDoorLocation : null;
	}
	
	private DungeonSchematic() {
		//Used to create a dummy instance for readFromResource()
		super(BlockPos.ORIGIN, null, null);
	}

	public static DungeonSchematic readFromFile(String schematicPath) throws FileNotFoundException, InvalidSchematicException {
		return readFromFile(new File(schematicPath));
	}

	public static DungeonSchematic readFromFile(File schematicFile) throws FileNotFoundException, InvalidSchematicException {
		return readFromStream(new FileInputStream(schematicFile));
	}

	public static DungeonSchematic readFromResource(String resourcePath) throws InvalidSchematicException {
		//We need an instance of a class in the mod to retrieve a resource
		DungeonSchematic empty = new DungeonSchematic();
		InputStream schematicStream = empty.getClass().getResourceAsStream(resourcePath);
		return readFromStream(schematicStream);
	}

	public static DungeonSchematic readFromStream(InputStream schematicStream) throws InvalidSchematicException {
		return new DungeonSchematic(Schematic.readFromStream(schematicStream));
	}
	
	public void applyImportFilters(DDProperties properties) {
		//Search for special blocks (warp doors, dim doors, and end portal frames that mark Monolith spawn points)
		SpecialBlockFinder finder = new SpecialBlockFinder(DimDoors.warpDoor, DimDoors.dimensionalDoor,
				Blocks.end_portal_frame, Blocks.sandstone);applyFilter(finder);
		
		//Flip the entrance's orientation to get the dungeon's orientation
		orientation = BlockRotator.transformMetadata(finder.getEntranceOrientation(), 2, Blocks.oak_door);

		entranceDoorLocation = finder.getEntranceDoorLocation();
		exitDoorLocations = finder.getExitDoorLocations();
		dimensionalDoorLocations = finder.getDimensionalDoorLocations();
		monolithSpawnLocations = finder.getMonolithSpawnLocations();
		
		//Filter out mod blocks except some of our own
		CompoundFilter standardizer = new CompoundFilter();
		standardizer.addFilter(new ModBlockFilter(modBlockFilterExceptions, DimDoors.blockDimWall.getDefaultState()));
		
		//Also convert standard DD block IDs to local versions
		applyFilter(standardizer);
	}
	
	public void applyExportFilters(DDProperties properties)
	{		
		//Check if some block IDs assigned by Forge differ from our standard IDs
		//If so, change the IDs to standard values
		CompoundFilter standardizer = new CompoundFilter();
		
		//Filter out mod blocks except some of our own
		//This comes after ID standardization because the mod block filter relies on standardized IDs
		standardizer.addFilter(new ModBlockFilter(modBlockFilterExceptions, DimDoors.blockDimWall.getDefaultState()));
		
		applyFilter(standardizer);
	}

	public static DungeonSchematic copyFromWorld(World world, int x, int y, int z, short width, short height, short length, boolean doCompactBounds)
	{
		return new DungeonSchematic(Schematic.copyFromWorld(world, x, y, z, width, height, length, doCompactBounds));
	}

	public void copyToWorld(World world, BlockPos pocketCenter, int targetOrientation, DimLink entryLink,
			Random random, DDProperties properties, boolean notifyClients)
	{
		if (notifyClients)
		{
			copyToWorld(world, pocketCenter, targetOrientation, entryLink, random, properties, new WorldBlockSetter(false, true, false));
		}
		else
		{
			copyToWorld(world, pocketCenter, targetOrientation, entryLink, random, properties, new ChunkBlockSetter(false));
		}
	}
	
	public void copyToWorld(World world, BlockPos pocketCenter, int targetOrientation, DimLink entryLink, Random random, DDProperties properties, IBlockSetter blockSetter)
	{
		//TODO: This function is an improvised solution so we can get the release moving. In the future,
		//we should generalize block transformations and implement support for them at the level of Schematic,
		//then just use that support from DungeonSchematic instead of making this local fix.
		//It might be easiest to support transformations using a WorldOperation
		
		final int turnAngle = targetOrientation - this.orientation;
		
		int index;
		int count;
		Block block;
		int blockMeta;
		int dx, dy, dz;
		BlockPos pocketPoint = new BlockPos(0, 0, 0);
		
		//Copy blocks and metadata into the world
		index = 0;
		for (dy = 0; dy < height; dy++)
		{
			for (dz = 0; dz < length; dz++)
			{
				for (dx = 0; dx < width; dx++)
				{
					pocketPoint.setX(dx);
					pocketPoint.setY(dy);
					pocketPoint.setZ(dz);
					block = blocks[index];
					BlockRotator.transformPoint(pocketPoint, entranceDoorLocation, turnAngle, pocketCenter);
					blockMeta = BlockRotator.transformMetadata(metadata[index], turnAngle, block);

					//In the future, we might want to make this more efficient by building whole chunks at a time
					blockSetter.setBlock(world, pocketPoint.getX(), pocketPoint.getY(), pocketPoint.getZ(), block, blockMeta);
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
			pocketPoint.setX(tileTag.getInteger("x"));
			pocketPoint.setY(tileTag.getInteger("y"));
			pocketPoint.setZ(tileTag.getInteger("z"));
			BlockRotator.transformPoint(pocketPoint, entranceDoorLocation, turnAngle, pocketCenter);
			tileTag.setInteger("x", pocketPoint.getX());
			tileTag.setInteger("y", pocketPoint.getY());
			tileTag.setInteger("z", pocketPoint.getZ());
			//Load the tile entity and put it in the world
			world.setTileEntity(pocketPoint.getX(), pocketPoint.getY(), pocketPoint.getZ(), TileEntity.createAndLoadEntity(tileTag));
		}
		
		setUpDungeon(PocketManager.createDimensionData(world), world, pocketCenter, turnAngle, entryLink, random, properties, blockSetter);
	}
	
	private void setUpDungeon(NewDimData dimension, World world, BlockPos pocketCenter, int turnAngle, DimLink entryLink, Random random, DDProperties properties, IBlockSetter blockSetter)
	{
        //Transform dungeon corners
        BlockPos minCorner = new BlockPos(0, 0, 0);
        BlockPos maxCorner = new BlockPos(width - 1, height - 1, length - 1);
        transformCorners(entranceDoorLocation, pocketCenter, turnAngle, minCorner, maxCorner);
        
		//Fill empty chests and dispensers
		FillContainersOperation filler = new FillContainersOperation(random, properties);
		filler.apply(world, minCorner, maxCorner);
		
		//Set up entrance door rift
		createEntranceReverseLink(world, dimension, pocketCenter, entryLink);
		
		//Set up link data for dimensional doors
		for (BlockPos location : dimensionalDoorLocations)
		{
			createDimensionalDoorLink(world, dimension, location, entranceDoorLocation, turnAngle, pocketCenter);
		}
		
		//Set up link data for exit door
		for (BlockPos location : exitDoorLocations)
		{
			createExitDoorLink(world, dimension, location, entranceDoorLocation, turnAngle, pocketCenter, blockSetter);
		}
		
		//Remove end portal frames and spawn Monoliths, if allowed
		boolean canSpawn = CustomLimboPopulator.isMobSpawningAllowed();
		for (BlockPos location : monolithSpawnLocations)
		{
			spawnMonolith(world, location, entranceDoorLocation, turnAngle, pocketCenter, canSpawn, blockSetter);
		}
		
		// If this is a Nether dungeon, search for a sign near the entry door and write the dimension's depth.
		// Checking if this is specifically a Nether pack dungeon is a bit tricky, so I'm going to use this
		// approach to check - if the dungeon is rooted in the Nether, then it SHOULD be a Nether dungeon.
		// This isn't necessarily true if someone uses dd-rift to spawn a dungeon, but it should work under
		// normal use of the mod.
		if (dimension.root().id() == NETHER_DIMENSION_ID)
		{
			writeDepthSign(world, pocketCenter, dimension.depth());
		}
	}
	
	private static void transformCorners(BlockPos schematicEntrance, BlockPos pocketCenter, EnumFacing turnAngle, BlockPos minCorner, BlockPos maxCorner) {
		int temp;
		BlockRotator.transformPoint(minCorner, schematicEntrance, turnAngle, pocketCenter);
		BlockRotator.transformPoint(maxCorner, schematicEntrance, turnAngle, pocketCenter);
		if (minCorner.getX() > maxCorner.getX()) {
			temp = minCorner.getX();
			minCorner = new BlockPos(maxCorner.getX(), minCorner.getY(), minCorner.getZ());
			maxCorner = new BlockPos(temp, maxCorner.getY(), maxCorner.getZ());
		} if (minCorner.getY() > maxCorner.getY()) {
			temp = minCorner.getY();
			minCorner.setY(maxCorner.getY());
			maxCorner.setY(temp);
		} if (minCorner.getZ() > maxCorner.getZ()) {
			temp = minCorner.getZ();
			minCorner.setZ(maxCorner.getZ());
			maxCorner.setZ(temp);
		}
	}
	
	private static void createEntranceReverseLink(World world, NewDimData dimension, BlockPos pocketCenter, DimLink entryLink)
	{
		int orientation = world.getBlockMetadata(pocketCenter.getX(), pocketCenter.getY() - 1, pocketCenter.getZ());
		DimLink reverseLink = dimension.createLink(pocketCenter.getX(), pocketCenter.getY(), pocketCenter.getZ(), LinkType.REVERSE, orientation);
		Point4D destination = entryLink.source();
		NewDimData prevDim = PocketManager.getDimensionData(destination.getDimension());
		prevDim.setLinkDestination(reverseLink, destination.getX(), destination.getY(), destination.getZ());
		initDoorTileEntity(world, pocketCenter);
	}
	
	private static void createExitDoorLink(World world, NewDimData dimension, BlockPos point, BlockPos entrance, EnumFacing rotation, BlockPos pocketCenter, IBlockSetter blockSetter)
	{
		//Transform the door's location to the pocket coordinate system
		BlockPos location = point;
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		int orientation = world.getBlockMetadata(location.getX(), location.getY() - 1, location.getZ());
		dimension.createLink(location.getX(), location.getY(), location.getZ(), LinkType.DUNGEON_EXIT, orientation);
		//Replace the sandstone block under the exit door with the same block as the one underneath it
		int x = location.getX();
		int y = location.getY() - 3;
		int z = location.getZ();
		if (y >= 0)
		{
			Block block = world.getBlock(x, y, z);
			int metadata = world.getBlockMetadata(x, y, z);
			blockSetter.setBlock(world, x, y + 1, z, block, metadata);
		}
		initDoorTileEntity(world, location);
	}
	
	private static void createDimensionalDoorLink(World world, NewDimData dimension, BlockPos point, BlockPos entrance, int rotation, BlockPos pocketCenter)
	{
		//Transform the door's location to the pocket coordinate system
		BlockPos location = point.clone();
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		int orientation = world.getBlockMetadata(location.getX(), location.getY() - 1, location.getZ());

		dimension.createLink(location.getX(), location.getY(), location.getZ(), LinkType.DUNGEON, orientation);
		initDoorTileEntity(world, location);
	}
	
	private static void spawnMonolith(World world, BlockPos point, BlockPos entrance, int rotation, BlockPos pocketCenter, boolean canSpawn, IBlockSetter blockSetter)
	{
		//Transform the frame block's location to the pocket coordinate system
		BlockPos location = point.clone();
		BlockRotator.transformPoint(location, entrance, rotation, pocketCenter);
		//Remove frame block
		blockSetter.setBlock(world, location.getX(), location.getY(), location.getZ(), Blocks.air, 0);
		//Spawn Monolith
		if (canSpawn)
		{
			Entity mob = new MobMonolith(world);
			mob.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), 1, 1);
			world.spawnEntityInWorld(mob);
		}
	}

	private static void initDoorTileEntity(World world, BlockPos point)
	{
		Block door = world.getBlock(point.getX(), point.getY(), point.getZ());
		Block door2 = world.getBlock(point.getX(), point.getY() - 1, point.getZ());

		if (door instanceof IDimDoor && door2 instanceof IDimDoor)
		{
			((IDimDoor) door).initDoorTE(world, point.getX(), point.getY(), point.getZ());
			((IDimDoor) door).initDoorTE(world, point.getX(), point.getY() - 1, point.getZ());
		}
		else
		{
			throw new IllegalArgumentException("Tried to init a dim door TE on a block that isnt a Dim Door!!");
		}
	}
	
	private static void writeDepthSign(World world, BlockPos pocketCenter, int depth)
	{
		final int SEARCH_RANGE = 6;
		
		int x, y, z;
        Block block;
		int dx, dy, dz;
		
		for (dy = SEARCH_RANGE; dy >= -SEARCH_RANGE; dy--)
		{
			for (dz = -SEARCH_RANGE; dz <= SEARCH_RANGE; dz++)
			{
				for (dx = -SEARCH_RANGE; dx <= SEARCH_RANGE; dx++)
				{
					x = pocketCenter.getX() + dx;
					y = pocketCenter.getY() + dy;
					z = pocketCenter.getZ() + dz;
					block = world.getBlock(x, y, z);
					if (block == Blocks.wall_sign || block == Blocks.standing_sign)
					{
						TileEntitySign signEntity = new TileEntitySign();
						signEntity.signText[1] = "Level " + depth;
						world.setTileEntity(x, y, z, signEntity);
						return;
					}
				}
			}
		}
	}
}
