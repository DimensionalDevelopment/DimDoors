package StevenDimDoors.mod_pocketDim.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ByteArrayTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.CompoundTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ListTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.NBTOutputStream;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.ShortTag;
import StevenDimDoors.mod_pocketDim.helpers.jnbt.Tag;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonHelper
{
	private static DungeonHelper instance = null;
	private static DDProperties properties = null;
	public static final Pattern NamePattern = Pattern.compile("[A-Za-z0-9_]+");

	private static final String SCHEMATIC_FILE_EXTENSION = ".schematic";
	private static final int DEFAULT_DUNGEON_WEIGHT = 100;
	private static final int MAX_DUNGEON_WEIGHT = 10000; //Used to prevent overflows and math breaking down
	
	private static final String HUB_DUNGEON_TYPE = "Hub";
	private static final String TRAP_DUNGEON_TYPE = "Trap";
	private static final String SIMPLE_HALL_DUNGEON_TYPE = "SimpleHall";
	private static final String COMPLEX_HALL_DUNGEON_TYPE = "ComplexHall";
	private static final String EXIT_DUNGEON_TYPE = "Exit";
	private static final String DEAD_END_DUNGEON_TYPE = "DeadEnd";
	private static final String MAZE_DUNGEON_TYPE = "Maze";
	
	//The list of dungeon types will be kept as an array for now. If we allow new
	//dungeon types in the future, then this can be changed to an ArrayList.
	private static final String[] DUNGEON_TYPES = new String[] {
		HUB_DUNGEON_TYPE,
		TRAP_DUNGEON_TYPE,
		SIMPLE_HALL_DUNGEON_TYPE,
		COMPLEX_HALL_DUNGEON_TYPE,
		EXIT_DUNGEON_TYPE,
		DEAD_END_DUNGEON_TYPE,
		MAZE_DUNGEON_TYPE
	};
	
	private Random rand = new Random();
	
	public HashMap<Integer, LinkData> customDungeonStatus = new HashMap<Integer, LinkData>();

	public ArrayList<DungeonGenerator> customDungeons = new ArrayList<DungeonGenerator>();
	public ArrayList<DungeonGenerator> registeredDungeons = new ArrayList<DungeonGenerator>();
	
	private ArrayList<DungeonGenerator> simpleHalls = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> complexHalls = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> deadEnds = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> hubs = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> mazes = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> pistonTraps = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> exits = new ArrayList<DungeonGenerator>();

	public ArrayList<Integer> metadataFlipList = new ArrayList<Integer>();
	public ArrayList<Integer> metadataNextList = new ArrayList<Integer>();
	public DungeonGenerator defaultBreak = new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematic/somethingBroke.schematic", true);
	public DungeonGenerator defaultUp = new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematic/simpleStairsUp.schematic", true);
	
	private HashSet<String> dungeonTypeChecker;
	private HashMap<String, ArrayList<DungeonGenerator>> dungeonTypeMapping;
	
	private DungeonHelper()
	{
		//Load the dungeon type checker with the list of all types in lowercase.
		//Capitalization matters for matching in a hash set.
		dungeonTypeChecker = new HashSet<String>();
		for (String dungeonType : DUNGEON_TYPES)
		{
			dungeonTypeChecker.add(dungeonType.toLowerCase());
		}
		
		//Add all the basic dungeon types to dungeonTypeMapping
		dungeonTypeMapping = new HashMap<String, ArrayList<DungeonGenerator>>();
		dungeonTypeMapping.put(SIMPLE_HALL_DUNGEON_TYPE, simpleHalls);
		dungeonTypeMapping.put(COMPLEX_HALL_DUNGEON_TYPE, complexHalls);
		dungeonTypeMapping.put(HUB_DUNGEON_TYPE, hubs);
		dungeonTypeMapping.put(EXIT_DUNGEON_TYPE, exits);
		dungeonTypeMapping.put(DEAD_END_DUNGEON_TYPE, deadEnds);
		dungeonTypeMapping.put(MAZE_DUNGEON_TYPE, mazes);
		dungeonTypeMapping.put(TRAP_DUNGEON_TYPE, pistonTraps);
		
		//Load our reference to the DDProperties singleton
		if (properties == null)
			properties = DDProperties.instance();
		
		initializeDungeons();
	}
	
	private void initializeDungeons()
	{
		File file = new File(properties.CustomSchematicDirectory);
		if (file.exists() || file.mkdir())
		{
			copyfile.copyFile("/mods/DimDoors/How_to_add_dungeons.txt", file.getAbsolutePath() + "/How_to_add_dungeons.txt");
		}
		registerFlipBlocks();
		importCustomDungeons(properties.CustomSchematicDirectory);
		registerBaseDungeons();
	}
	
	public static DungeonHelper initialize()
	{
		if (instance == null)
		{
			instance = new DungeonHelper();
		}
		else
		{
			throw new IllegalStateException("Cannot initialize DungeonHelper twice");
		}
		
		return instance;
	}
	
	public static DungeonHelper instance()
	{
		if (instance == null)
		{
			//This is to prevent some frustrating bugs that could arise when classes
			//are loaded in the wrong order. Trust me, I had to squash a few...
			throw new IllegalStateException("Instance of DungeonHelper requested before initialization");
		}
		return instance;
	}
	
	public boolean validateSchematicName(String name)
	{
		String[] dungeonData = name.split("_");

		//Check for a valid number of parts
		if (dungeonData.length < 3 || dungeonData.length > 4)
			return false;

		//Check if the dungeon type is valid
		if (!dungeonTypeChecker.contains(dungeonData[0].toLowerCase()))
			return false;
		
		//Check if the name is valid
		if (!NamePattern.matcher(dungeonData[1]).matches())
			return false;
		
		//Check if the open/closed flag is present
		if (!dungeonData[2].equalsIgnoreCase("open") && !dungeonData[2].equalsIgnoreCase("closed"))
			return false;
		
		//If the weight is present, check that it is valid
		if (dungeonData.length == 4)
		{
			try
			{
				int weight = Integer.parseInt(dungeonData[3]);
				if (weight < 0 || weight > MAX_DUNGEON_WEIGHT)
					return false;
			}
			catch (NumberFormatException e)
			{
				//Not a number
				return false;
			}
		}
		return true;
	}
	
	public void registerCustomDungeon(File schematicFile)
	{
		String name = schematicFile.getName();
		String path = schematicFile.getAbsolutePath();
		try
		{
			if (name.endsWith(SCHEMATIC_FILE_EXTENSION) && validateSchematicName(name))
			{
				//Strip off the file extension while splitting the file name
				String[] dungeonData = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length()).split("_");
				
				String dungeonType = dungeonData[0].toLowerCase();
				boolean open = dungeonData[2].equals("open");
				int weight = (dungeonData.length == 4) ? Integer.parseInt(dungeonData[3]) : DEFAULT_DUNGEON_WEIGHT;
				
				//Add this custom dungeon to the list corresponding to its type
				DungeonGenerator generator = new DungeonGenerator(weight, path, open);

				dungeonTypeMapping.get(dungeonType).add(generator);
				registeredDungeons.add(generator);
				customDungeons.add(generator);
				System.out.println("Imported " + name);
			}
			else
			{
				System.out.println("Could not parse dungeon filename, not adding dungeon to generation lists");
				customDungeons.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, path, true));
				System.out.println("Imported " + name);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Failed to import " + name);
		}
	}

	public void importCustomDungeons(String path)
	{
		File directory = new File(path);
		File[] schematicNames = directory.listFiles();

		if (schematicNames != null)
		{
			for (File schematicFile: schematicNames)
			{
				if (schematicFile.getName().endsWith(SCHEMATIC_FILE_EXTENSION))
				{
					registerCustomDungeon(schematicFile);
				}
			}
		}
	}

	public void registerFlipBlocks()
	{
		metadataFlipList.add(Block.dispenser.blockID);
		metadataFlipList.add(Block.stairsStoneBrick.blockID);
		metadataFlipList.add(Block.lever.blockID);
		metadataFlipList.add(Block.stoneButton.blockID);
		metadataFlipList.add(Block.redstoneRepeaterIdle.blockID);
		metadataFlipList.add(Block.redstoneRepeaterActive.blockID);
		metadataFlipList.add(Block.tripWireSource.blockID);
		metadataFlipList.add(Block.torchWood.blockID);
		metadataFlipList.add(Block.torchRedstoneIdle.blockID);
		metadataFlipList.add(Block.torchRedstoneActive.blockID);
		metadataFlipList.add(Block.doorIron.blockID);
		metadataFlipList.add(Block.doorWood.blockID);
		metadataFlipList.add(Block.pistonBase.blockID);
		metadataFlipList.add(Block.pistonStickyBase.blockID);
		metadataFlipList.add(Block.pistonExtension.blockID);
		metadataFlipList.add(Block.redstoneComparatorIdle.blockID);
		metadataFlipList.add(Block.redstoneComparatorActive.blockID);
		metadataFlipList.add(Block.signPost.blockID);
		metadataFlipList.add(Block.signWall.blockID);
		metadataFlipList.add(Block.skull.blockID);
		metadataFlipList.add(Block.ladder.blockID);
		metadataFlipList.add(Block.vine.blockID);
		metadataFlipList.add(Block.anvil.blockID);
		metadataFlipList.add(Block.chest.blockID);
		metadataFlipList.add(Block.chestTrapped.blockID);
		metadataFlipList.add(Block.hopperBlock.blockID);
		metadataFlipList.add(Block.stairsNetherBrick.blockID);
		metadataFlipList.add(Block.stairsCobblestone.blockID);
		metadataFlipList.add(Block.stairsNetherBrick.blockID);
		metadataFlipList.add(Block.stairsNetherQuartz.blockID);
		metadataFlipList.add(Block.stairsSandStone.blockID);
		
		metadataNextList.add(Block.redstoneRepeaterIdle.blockID);
		metadataNextList.add(Block.redstoneRepeaterActive.blockID);
	}
	
	public void registerBaseDungeons()
	{
		hubs.add(new DungeonGenerator(2 * DEFAULT_DUNGEON_WEIGHT, "/schematics/4WayBasicHall.schematic", false));
		hubs.add(new DungeonGenerator(2 * DEFAULT_DUNGEON_WEIGHT, "/schematics/4WayHallExit.schematic", false));
		hubs.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/doorTotemRuins.schematic", true));
		hubs.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/hallwayTrapRooms1.schematic", false));
		hubs.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/longDoorHallway.schematic", false));
		hubs.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallRotundaWithExit.schematic", false));
		hubs.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/fortRuins.schematic", true));

		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/collapsedSingleTunnel1.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/singleStraightHall1.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallBranchWithExit.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallSimpleLeft.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallSimpleRight.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/simpleStairsUp.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/simpleStairsDown.schematic", false));
		simpleHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/simpleSmallT1.schematic", false));

		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/tntPuzzleTrap.schematic", false));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/brokenPillarsO.schematic", true));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/buggyTopEntry1.schematic", true));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/exitRuinsWithHiddenDoor.schematic", true));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/hallwayHiddenTreasure.schematic", false));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/mediumPillarStairs.schematic", true));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/ruinsO.schematic", true));
		complexHalls.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/pitStairs.schematic", true));

		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/azersDungeonO.schematic", false));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/diamondTowerTemple1.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/fallingTrapO.schematic", false));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/hiddenStaircaseO.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/lavaTrapO.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/randomTree.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallHiddenTowerO.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallSilverfishRoom.schematic", false));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/tntTrapO.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallDesert.schematic", true));
		deadEnds.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallPond.schematic", true));

		pistonTraps.add(new DungeonGenerator(2 * DEFAULT_DUNGEON_WEIGHT, "/schematics/hallwayPitFallTrap.schematic", false));
		pistonTraps.add(new DungeonGenerator(2 * DEFAULT_DUNGEON_WEIGHT, "/schematics/pistonFloorHall.schematic", false));
		pistonTraps.add(new DungeonGenerator(2 * DEFAULT_DUNGEON_WEIGHT, "/schematics/wallFallcomboPistonHall.schematic", false));
		pistonTraps.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/fakeTNTTrap.schematic", false));
		pistonTraps.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/pistonFallRuins.schematic", false));
		pistonTraps.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/pistonSmasherHall.schematic", false));
		pistonTraps.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/simpleDropHall.schematic", false));
		pistonTraps.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/fallingTNThall.schematic", false));
		pistonTraps.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/lavaPyramid.schematic", true));

		mazes.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallMaze1.schematic", false));
		mazes.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallMultilevelMaze.schematic", false));

		exits.add(new DungeonGenerator(2 * DEFAULT_DUNGEON_WEIGHT, "/schematics/lockingExitHall.schematic", false));
		exits.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/exitCube.schematic", true));
		exits.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, "/schematics/smallExitPrison.schematic", true));
		
		registeredDungeons.addAll(simpleHalls);
		registeredDungeons.addAll(exits);
		registeredDungeons.addAll(pistonTraps);
		registeredDungeons.addAll(mazes);
		registeredDungeons.addAll(deadEnds);
		registeredDungeons.addAll(complexHalls);
		registeredDungeons.addAll(hubs);
	}

	public boolean exportDungeon(World world, int xI, int yI, int zI, String exportPath)
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

		for (int count = 0; count < 50; count++)
		{
			if(world.getBlockId(xMin, yI, zI)!=properties.PermaFabricBlockID)
			{
				xMin--;
			}
			if(world.getBlockId(xI, yMin, zI)!=properties.PermaFabricBlockID)
			{
				yMin--;
			}
			if(world.getBlockId(xI, yI, zMin)!=properties.PermaFabricBlockID)
			{
				zMin--;
			}
			if(world.getBlockId(xMax, yI, zI)!=properties.PermaFabricBlockID)
			{
				xMax++;
			}
			if(world.getBlockId(xI, yMax, zI)!=properties.PermaFabricBlockID)
			{
				yMax++;
			}
			if(world.getBlockId(xI, yI, zMax)!=properties.PermaFabricBlockID)
			{
				zMax++;
			}
		}

		short width =(short) (xMax-xMin);
		short height= (short) (yMax-yMin);
		short length= (short) (zMax-zMin);

		//ArrayList<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();
		ArrayList<Tag> tileEntites = new ArrayList<Tag>();
		byte[] blocks = new byte[width * height * length];
		byte[] addBlocks = null;
		byte[] blockData = new byte[width * height * length];

		for (int x = 0; x < width; ++x) 
		{
			for (int y = 0; y < height; ++y) 
			{
				for (int z = 0; z < length; ++z) 
				{
					int index = y * width * length + z * width + x;
					int blockID = world.getBlockId(x+xMin, y+yMin, z+zMin);
					int meta= world.getBlockMetadata(x+xMin, y+yMin, z+zMin);

					if(blockID==properties.DimensionalDoorID)
					{
						blockID=Block.doorIron.blockID;
					}
					if(blockID==properties.WarpDoorID)
					{
						blockID=Block.doorWood.blockID;

					}

					// Save 4096 IDs in an AddBlocks section
					if (blockID > 255) 
					{
						if (addBlocks == null) 
						{ // Lazily create section
							addBlocks = new byte[(blocks.length >> 1) + 1];
						}

						addBlocks[index >> 1] = (byte) (((index & 1) == 0) ?
								addBlocks[index >> 1] & 0xF0 | (blockID >> 8) & 0xF
								: addBlocks[index >> 1] & 0xF | ((blockID >> 8) & 0xF) << 4);
					}

					blocks[index] = (byte) blockID;
					blockData[index] = (byte) meta;

					if (Block.blocksList[blockID] instanceof BlockContainer) 
					{
						//TODO fix this
						/**
	                        TileEntity tileEntityBlock = world.getBlockTileEntity(x+xMin, y+yMin, z+zMin);
	                        NBTTagCompound tag = new NBTTagCompound();
	                        tileEntityBlock.writeToNBT(tag);

	                        CompoundTag tagC = new CompoundTag("TileEntity",Map.class.cast(tag.getTags()));



	                        // Get the list of key/values from the block

	                        if (tagC != null) 
	                        {
	                        	tileEntites.add(tagC);
	                        }
						 **/
					}
				}
			}
		}
		/**
		 *   
		 *   nbtdata.setShort("Width", width);
	        nbtdata.setShort("Height", height);
	        nbtdata.setShort("Length", length);

	    	 nbtdata.setByteArray("Blocks", blocks);
        	 nbtdata.setByteArray("Data", blockData);
		 */

		HashMap<String, Tag> schematic = new HashMap<String, Tag>();

		schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
		schematic.put("Data", new ByteArrayTag("Data", blockData));
		
		schematic.put("Width", new ShortTag("Width", (short) width));
		schematic.put("Length", new ShortTag("Length", (short) length));
		schematic.put("Height", new ShortTag("Height", (short) height));
		schematic.put("TileEntites", new ListTag("TileEntities", CompoundTag.class,tileEntites));
		
		if (addBlocks != null)
		{
			schematic.put("AddBlocks", new ByteArrayTag("AddBlocks", addBlocks));
		}

		CompoundTag schematicTag = new CompoundTag("Schematic", schematic);
		try
		{
			NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(exportPath));
			stream.writeTag(schematicTag);
			stream.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void generateDungeonLink(LinkData incoming)
	{
		DungeonGenerator dungeon;
		int depth = dimHelper.instance.getDimDepth(incoming.locDimID);
		int depthWeight = rand.nextInt(depth + 2) + rand.nextInt(depth + 2) - 2;

		int count = 10;
		boolean flag = true;
		try
		{
			if (dimHelper.dimList.get(incoming.destDimID) != null &&
				dimHelper.dimList.get(incoming.destDimID).dungeonGenerator != null)
			{
				mod_pocketDim.loader.init(incoming);
				return;
			}
			
			if (incoming.destYCoord > 15)
			{
				do
				{
					count--;
					flag = true;
					//Select a dungeon at random, taking into account its weight
					dungeon = getRandomDungeon(rand, registeredDungeons);

					if (depth <= 1)
					{
						if(rand.nextBoolean())
						{
							dungeon = complexHalls.get(rand.nextInt(complexHalls.size()));

						}
						else if(rand.nextBoolean())
						{
							dungeon = hubs.get(rand.nextInt(hubs.size()));

						}
						else  if(rand.nextBoolean())
						{
							dungeon = hubs.get(rand.nextInt(hubs.size()));

						}
						else if(deadEnds.contains(dungeon)||exits.contains(dungeon))
						{
							flag=false;
						}
					}
					else if (depth <= 3 && (deadEnds.contains(dungeon) || exits.contains(dungeon) || rand.nextBoolean()))
					{
						if(rand.nextBoolean())
						{
							dungeon = hubs.get(rand.nextInt(hubs.size()));

						}
						else if(rand.nextBoolean())
						{
							dungeon = mazes.get(rand.nextInt(mazes.size()));
						}
						else if(rand.nextBoolean())
						{
							dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));

						}
						else
						{
							flag = false;
						}
					}
					else if (rand.nextInt(3) == 0 && !complexHalls.contains(dungeon))
					{
						if (rand.nextInt(3) == 0)
						{
							dungeon = simpleHalls.get(rand.nextInt(simpleHalls.size()));
						}
						else if(rand.nextBoolean())
						{
							dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));
						}
						else if (depth < 4)
						{
							dungeon = hubs.get(rand.nextInt(hubs.size()));
						}
					}
					else if (depthWeight - depthWeight / 2 > depth -4 && (deadEnds.contains(dungeon) || exits.contains(dungeon)))
					{
						if(rand.nextBoolean())
						{
							dungeon = simpleHalls.get(rand.nextInt(simpleHalls.size()));
						}
						else if(rand.nextBoolean())
						{
							dungeon = complexHalls.get(rand.nextInt(complexHalls.size()));
						}
						else if(rand.nextBoolean())
						{
							dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));
						}
						else	
						{
							flag = false;
						}
					}
					else if (depthWeight > 7 && hubs.contains(dungeon))
					{
						if(rand.nextInt(12)+5<depthWeight)
						{
							if(rand.nextBoolean())
							{
								dungeon = exits.get(rand.nextInt(exits.size()));
							}
							else if(rand.nextBoolean())
							{
								dungeon = deadEnds.get(rand.nextInt(deadEnds.size()));
							}
							else
							{
								dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));
							}
						}
						else
						{
							flag = false;
						}
					}
					else if (depth > 10 && hubs.contains(dungeon))
					{
						flag = false;
					}
				}
				while (!flag && count > 0);
			}
			else
			{
				dungeon = defaultUp;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (registeredDungeons.size() > 0)
			{
				//Select a random dungeon
				dungeon = getRandomDungeon(rand, registeredDungeons);
			}
			else
			{
				return;
			}
		}
		dimHelper.dimList.get(incoming.destDimID).dungeonGenerator = dungeon;
	}

	public Collection<String> getDungeonNames() {

		//Use a HashSet to guarantee that all dungeon names will be distinct.
		//This shouldn't be necessary if we keep proper lists without repetitions,
		//but it's a fool-proof workaround.
		HashSet<String> dungeonNames = new HashSet<String>();
		dungeonNames.addAll( parseDungeonNames(registeredDungeons) );
		dungeonNames.addAll( parseDungeonNames(customDungeons) );
		
		//Sort dungeon names alphabetically
		ArrayList<String> sortedNames = new ArrayList<String>(dungeonNames);
		Collections.sort(sortedNames);
		return sortedNames;
	}
	
	private static ArrayList<String> parseDungeonNames(ArrayList<DungeonGenerator> dungeons)
	{
		String name;
		File schematic;
		ArrayList<String> names = new ArrayList<String>(dungeons.size());
		
		for (DungeonGenerator dungeon : dungeons)
		{
			//Retrieve the file name and strip off the file extension
			schematic = new File(dungeon.schematicPath);
			name = schematic.getName();
			name = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length());
			names.add(name);
		}
		return names;
	}
	
	private static DungeonGenerator getRandomDungeon(Random random, Collection<DungeonGenerator> dungeons)
	{
		//Use Minecraft's WeightedRandom to select our dungeon. =D
		ArrayList<WeightedContainer<DungeonGenerator>> weights =
				new ArrayList<WeightedContainer<DungeonGenerator>>(dungeons.size());
		for (DungeonGenerator dungeon : dungeons)
		{
			weights.add(new WeightedContainer<DungeonGenerator>(dungeon, dungeon.weight));
		}
		
		@SuppressWarnings("unchecked")
		WeightedContainer<DungeonGenerator> resultContainer = (WeightedContainer<DungeonGenerator>) WeightedRandom.getRandomItem(random, weights);
		return 	(resultContainer != null) ? resultContainer.getData() : null;
	}
}