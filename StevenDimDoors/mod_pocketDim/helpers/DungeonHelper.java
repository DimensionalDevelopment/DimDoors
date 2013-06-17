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
	
	private ArrayList<DungeonGenerator> weightedDungeonGenList = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> simpleHalls = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> complexHalls = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> deadEnds = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> hubs = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> mazes = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> pistonTraps = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> exits = new ArrayList<DungeonGenerator>();

	public ArrayList<Integer> metadataFlipList = new ArrayList<Integer>();
	public ArrayList<Integer> metadataNextList = new ArrayList<Integer>();
	public DungeonGenerator defaultBreak = new DungeonGenerator(0, "/schematic/somethingBroke.schematic", true);
	public DungeonGenerator defaultUp = new DungeonGenerator(0, "/schematic/simpleStairsUp.schematic", true);
	
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
				weightedDungeonGenList.add(generator);
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
		this.metadataFlipList.add(Block.dispenser.blockID);
		this.metadataFlipList.add(Block.stairsStoneBrick.blockID);
		this.metadataFlipList.add(Block.lever.blockID);
		this.metadataFlipList.add(Block.stoneButton.blockID);
		this.metadataFlipList.add(Block.redstoneRepeaterIdle.blockID);
		this.metadataFlipList.add(Block.redstoneRepeaterActive.blockID);
		this.metadataFlipList.add(Block.tripWireSource.blockID);
		this.metadataFlipList.add(Block.torchWood.blockID);
		this.metadataFlipList.add(Block.torchRedstoneIdle.blockID);
		this.metadataFlipList.add(Block.torchRedstoneActive.blockID);
		this.metadataFlipList.add(Block.doorIron.blockID);
		this.metadataFlipList.add(Block.doorWood.blockID);
		this.metadataFlipList.add(Block.pistonBase.blockID);
		this.metadataFlipList.add(Block.pistonStickyBase.blockID);
		this.metadataFlipList.add(Block.pistonExtension.blockID);
		this.metadataFlipList.add(Block.redstoneComparatorIdle.blockID);
		this.metadataFlipList.add(Block.redstoneComparatorActive.blockID);
		this.metadataFlipList.add(Block.signPost.blockID);
		this.metadataFlipList.add(Block.signWall.blockID);
		this.metadataFlipList.add(Block.skull.blockID);
		this.metadataFlipList.add(Block.ladder.blockID);
		this.metadataFlipList.add(Block.vine.blockID);
		this.metadataFlipList.add(Block.anvil.blockID);
		this.metadataFlipList.add(Block.chest.blockID);
		this.metadataFlipList.add(Block.chestTrapped.blockID);
		this.metadataFlipList.add(Block.hopperBlock.blockID);
		this.metadataFlipList.add(Block.stairsNetherBrick.blockID);
		this.metadataFlipList.add(Block.stairsCobblestone.blockID);
		this.metadataFlipList.add(Block.stairsNetherBrick.blockID);
		this.metadataFlipList.add(Block.stairsNetherQuartz.blockID);
		this.metadataFlipList.add(Block.stairsSandStone.blockID);
		
		this.metadataNextList.add(Block.redstoneRepeaterIdle.blockID);
		this.metadataNextList.add(Block.redstoneRepeaterActive.blockID);
	}
	
	public void registerBaseDungeons()
	{
		this.hubs.add(new DungeonGenerator(0, "/schematics/4WayBasicHall.schematic", false));
		this.hubs.add(new DungeonGenerator(0, "/schematics/4WayBasicHall.schematic", false));
		this.hubs.add(new DungeonGenerator(0, "/schematics/doorTotemRuins.schematic", true));
		this.hubs.add(new DungeonGenerator(0, "/schematics/hallwayTrapRooms1.schematic", false));
		this.hubs.add(new DungeonGenerator(0, "/schematics/longDoorHallway.schematic", false));
		this.hubs.add(new DungeonGenerator(0, "/schematics/smallRotundaWithExit.schematic", false));
		this.hubs.add(new DungeonGenerator(0, "/schematics/fortRuins.schematic", true));
		this.hubs.add(new DungeonGenerator(0, "/schematics/4WayHallExit.schematic", false));
		this.hubs.add(new DungeonGenerator(0, "/schematics/4WayHallExit.schematic", false));

		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/collapsedSingleTunnel1.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/singleStraightHall1.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/smallBranchWithExit.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/smallSimpleLeft.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/smallSimpleRight.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/simpleStairsUp.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/simpleStairsDown.schematic", false));
		this.simpleHalls.add(new DungeonGenerator(0, "/schematics/simpleSmallT1.schematic", false));

		this.complexHalls.add(new DungeonGenerator(0, "/schematics/tntPuzzleTrap.schematic", false));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/brokenPillarsO.schematic", true));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/buggyTopEntry1.schematic", true));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/exitRuinsWithHiddenDoor.schematic", true));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/hallwayHiddenTreasure.schematic", false));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/mediumPillarStairs.schematic", true));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/ruinsO.schematic", true));
		this.complexHalls.add(new DungeonGenerator(0, "/schematics/pitStairs.schematic", true));

		this.deadEnds.add(new DungeonGenerator(0, "/schematics/azersDungeonO.schematic", false));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/diamondTowerTemple1.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/fallingTrapO.schematic", false));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/hiddenStaircaseO.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/lavaTrapO.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/randomTree.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/smallHiddenTowerO.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/smallSilverfishRoom.schematic", false));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/tntTrapO.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/smallDesert.schematic", true));
		this.deadEnds.add(new DungeonGenerator(0, "/schematics/smallPond.schematic", true));

		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/fakeTNTTrap.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/hallwayPitFallTrap.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/hallwayPitFallTrap.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/pistonFallRuins.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/pistonFloorHall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/pistonFloorHall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/pistonSmasherHall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/simpleDropHall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/wallFallcomboPistonHall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/wallFallcomboPistonHall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/fallingTNThall.schematic", false));
		this.pistonTraps.add(new DungeonGenerator(0, "/schematics/lavaPyramid.schematic", true));

		this.mazes.add(new DungeonGenerator(0, "/schematics/smallMaze1.schematic", false));
		this.mazes.add(new DungeonGenerator(0, "/schematics/smallMultilevelMaze.schematic", false));

		this.exits.add(new DungeonGenerator(0, "/schematics/exitCube.schematic", true));
		this.exits.add(new DungeonGenerator(0, "/schematics/lockingExitHall.schematic", false));
		this.exits.add(new DungeonGenerator(0, "/schematics/smallExitPrison.schematic", true));
		this.exits.add(new DungeonGenerator(0, "/schematics/lockingExitHall.schematic", false));

		this.weightedDungeonGenList.addAll(this.simpleHalls);
		this.weightedDungeonGenList.addAll(this.exits);
		this.weightedDungeonGenList.addAll(this.pistonTraps);
		this.weightedDungeonGenList.addAll(this.mazes);
		this.weightedDungeonGenList.addAll(this.deadEnds);
		this.weightedDungeonGenList.addAll(this.complexHalls);
		this.weightedDungeonGenList.addAll(this.hubs);

		for(DungeonGenerator data : this.weightedDungeonGenList)
		{
			if(!this.registeredDungeons.contains(data))
			{
				this.registeredDungeons.add(data);
			}
		}
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
		 *   this.nbtdata.setShort("Width", width);
	        this.nbtdata.setShort("Height", height);
	        this.nbtdata.setShort("Length", length);

	    	 this.nbtdata.setByteArray("Blocks", blocks);
        	 this.nbtdata.setByteArray("Data", blockData);
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

	public void generateDungeonlink(LinkData incoming)
	{
		//DungeonGenerator dungeon = mod_pocketDim.registeredDungeons.get(new Random().nextInt(mod_pocketDim.registeredDungeons.size()));
		DungeonGenerator dungeon;
		int depth = dimHelper.instance.getDimDepth(incoming.locDimID)+2;

		int depthWeight = rand.nextInt(depth)+rand.nextInt(depth)-2;

		depth = depth - 2;
		boolean flag = true;
		int count = 10;
		try
		{
			if (dimHelper.dimList.get(incoming.destDimID) != null&&dimHelper.dimList.get(incoming.destDimID).dungeonGenerator!=null)
			{
				mod_pocketDim.loader.init(incoming);
				//TODO: Check this!
				//What the hell? Isn't this line saying X = X..? ~SenseiKiwi 
				dimHelper.dimList.get(incoming.destDimID).dungeonGenerator=dimHelper.dimList.get(incoming.destDimID).dungeonGenerator;
				return;
			}
			if(incoming.destYCoord>15)
			{
				do
				{
					count--;
					flag = true;
					dungeon = this.weightedDungeonGenList.get(rand.nextInt(weightedDungeonGenList.size()));

					if(depth<=1)
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
					else if (depth<=3&&(deadEnds.contains(dungeon)||exits.contains(dungeon)||rand.nextBoolean()))
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
							flag=false;
						}
					}
					else if(rand.nextInt(3)==0&&!complexHalls.contains(dungeon))
					{
						if(rand.nextInt(3)==0)
						{
							dungeon = simpleHalls.get(rand.nextInt(simpleHalls.size()));
						}
						else if(rand.nextBoolean())
						{
							dungeon = pistonTraps.get(rand.nextInt(pistonTraps.size()));

						}
						else if(depth<4)
						{
							dungeon = hubs.get(rand.nextInt(hubs.size()));

						}
					}
					else if(depthWeight-depthWeight/2>depth-4&&(deadEnds.contains(dungeon)||exits.contains(dungeon)))
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
							flag=false;
						}
					}
					else if(depthWeight>7&&hubs.contains(dungeon))
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
					else if(depth>10&&hubs.contains(dungeon))
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
			if (weightedDungeonGenList.size() > 0)
			{
				dungeon = weightedDungeonGenList.get(rand.nextInt(weightedDungeonGenList.size()));
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
}