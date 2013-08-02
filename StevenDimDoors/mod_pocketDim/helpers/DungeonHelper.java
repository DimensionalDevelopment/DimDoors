package StevenDimDoors.mod_pocketDim.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.DimData;
import StevenDimDoors.mod_pocketDim.DungeonGenerator;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.items.itemDimDoor;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonHelper
{
	private static DungeonHelper instance = null;
	private static DDProperties properties = null;
	public static final Pattern SchematicNamePattern = Pattern.compile("[A-Za-z0-9_\\-]+");
	public static final Pattern DungeonNamePattern = Pattern.compile("[A-Za-z0-9\\-]+");
	
	private static final String DEFAULT_UP_SCHEMATIC_PATH = "/schematics/core/simpleStairsUp.schematic";
	private static final String DEFAULT_DOWN_SCHEMATIC_PATH = "/schematics/core/simpleStairsDown.schematic";
	private static final String DEFAULT_ERROR_SCHEMATIC_PATH = "/schematics/core/somethingBroke.schematic";
	private static final String BUNDLED_DUNGEONS_LIST_PATH = "/schematics/schematics.txt";
	private static final String DUNGEON_CREATION_GUIDE_SOURCE_PATH = "/mods/DimDoors/text/How_to_add_dungeons.txt";

	public static final String SCHEMATIC_FILE_EXTENSION = ".schematic";
	private static final int DEFAULT_DUNGEON_WEIGHT = 100;
	public static final int MAX_DUNGEON_WEIGHT = 10000; //Used to prevent overflows and math breaking down
	private static final int MAX_EXPORT_RADIUS = 50;
	public static final short MAX_DUNGEON_WIDTH = 2 * MAX_EXPORT_RADIUS + 1;
	public static final short MAX_DUNGEON_HEIGHT = MAX_DUNGEON_WIDTH;
	public static final short MAX_DUNGEON_LENGTH = MAX_DUNGEON_WIDTH;
	
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

	private ArrayList<DungeonGenerator> untaggedDungeons = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> registeredDungeons = new ArrayList<DungeonGenerator>();
	
	private ArrayList<DungeonGenerator> simpleHalls = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> complexHalls = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> deadEnds = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> hubs = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> mazes = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> pistonTraps = new ArrayList<DungeonGenerator>();
	private ArrayList<DungeonGenerator> exits = new ArrayList<DungeonGenerator>();
 
	private DungeonGenerator defaultUp;
	private DungeonGenerator defaultDown;
	private DungeonGenerator defaultError;
	
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
		//Dungeon type names must be passed in lowercase to make matching easier.
		dungeonTypeMapping = new HashMap<String, ArrayList<DungeonGenerator>>();
		dungeonTypeMapping.put(SIMPLE_HALL_DUNGEON_TYPE.toLowerCase(), simpleHalls);
		dungeonTypeMapping.put(COMPLEX_HALL_DUNGEON_TYPE.toLowerCase(), complexHalls);
		dungeonTypeMapping.put(HUB_DUNGEON_TYPE.toLowerCase(), hubs);
		dungeonTypeMapping.put(EXIT_DUNGEON_TYPE.toLowerCase(), exits);
		dungeonTypeMapping.put(DEAD_END_DUNGEON_TYPE.toLowerCase(), deadEnds);
		dungeonTypeMapping.put(MAZE_DUNGEON_TYPE.toLowerCase(), mazes);
		dungeonTypeMapping.put(TRAP_DUNGEON_TYPE.toLowerCase(), pistonTraps);
		
		//Load our reference to the DDProperties singleton
		if (properties == null)
			properties = DDProperties.instance();
		
		registerCustomDungeons();
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
	
	private void registerCustomDungeons()
	{
		File file = new File(properties.CustomSchematicDirectory);
		if (file.exists() || file.mkdir())
		{
			copyfile.copyFile(DUNGEON_CREATION_GUIDE_SOURCE_PATH, file.getAbsolutePath() + "/How_to_add_dungeons.txt");
		}
		registerBundledDungeons();
		importCustomDungeons(properties.CustomSchematicDirectory);
	}
	
	public List<DungeonGenerator> getRegisteredDungeons()
	{
		return Collections.unmodifiableList(this.registeredDungeons);
	}
	
	public List<DungeonGenerator> getUntaggedDungeons()
	{
		return Collections.unmodifiableList(this.untaggedDungeons);
	}
	
	public DungeonGenerator getDefaultErrorDungeon()
	{
		return defaultError;
	}
	
	public DungeonGenerator getDefaultUpDungeon()
	{
		return defaultUp;
	}
	
	public DungeonGenerator getDefaultDownDungeon()
	{
		return defaultDown;
	}
	
	public LinkData createCustomDungeonDoor(World world, int x, int y, int z)
	{
		//Create a link above the specified position. Link to a new pocket dimension.
		LinkData link = new LinkData(world.provider.dimensionId, 0, x, y + 1, z, x, y + 1, z, true, 3);
		link = dimHelper.instance.createPocket(link, true, false);
		
		//Place a Warp Door linked to that pocket
		itemDimDoor.placeDoorBlock(world, x, y, z, 3, mod_pocketDim.ExitDoor);
		
		return link;
	}
	
	public boolean validateDungeonType(String type)
	{
		//Check if the dungeon type is valid
		return dungeonTypeChecker.contains(type.toLowerCase());
	}
	
	public boolean validateSchematicName(String name)
	{
		String[] dungeonData;
		
		if (!name.endsWith(SCHEMATIC_FILE_EXTENSION))
			return false;
		
		dungeonData = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length()).split("_");

		//Check for a valid number of parts
		if (dungeonData.length < 3 || dungeonData.length > 4)
			return false;

		//Check if the dungeon type is valid
		if (!dungeonTypeChecker.contains(dungeonData[0].toLowerCase()))
			return false;
		
		//Check if the name is valid
		if (!SchematicNamePattern.matcher(dungeonData[1]).matches())
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
	
	public void registerDungeon(String schematicPath, boolean isInternal, boolean verbose)
	{
		//We use schematicPath as the real path for internal files (inside our JAR) because it seems
		//that File tries to interpret it as a local drive path and mangles it.
		File schematicFile = new File(schematicPath);
		String name = schematicFile.getName();
		String path = isInternal ? schematicPath : schematicFile.getAbsolutePath();
		try
		{
			if (validateSchematicName(name))
			{
				//Strip off the file extension while splitting the file name
				String[] dungeonData = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length()).split("_");
				
				String dungeonType = dungeonData[0].toLowerCase();
				boolean isOpen = dungeonData[2].equalsIgnoreCase("open");
				int weight = (dungeonData.length == 4) ? Integer.parseInt(dungeonData[3]) : DEFAULT_DUNGEON_WEIGHT;
				
				//Add this custom dungeon to the list corresponding to its type
				DungeonGenerator generator = new DungeonGenerator(weight, path, isOpen);

				dungeonTypeMapping.get(dungeonType).add(generator);
				registeredDungeons.add(generator);
				if (verbose)
				{
					System.out.println("Registered dungeon: " + name);
				}
			}
			else
			{
				if (verbose)
				{
					System.out.println("Could not parse dungeon filename, not adding dungeon to generation lists");
				}
				untaggedDungeons.add(new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, path, true));
				System.out.println("Registered untagged dungeon: " + name);
			}
		}
		catch(Exception e)
		{
			System.err.println("Failed to register dungeon: " + name);
			e.printStackTrace();
		}
	}

	private void importCustomDungeons(String path)
	{
		File directory = new File(path);
		File[] schematicNames = directory.listFiles();

		if (schematicNames != null)
		{
			for (File schematicFile: schematicNames)
			{
				if (schematicFile.getName().endsWith(SCHEMATIC_FILE_EXTENSION))
				{
					registerDungeon(schematicFile.getPath(), false, true);
				}
			}
		}
	}

	private void registerBundledDungeons()
	{
		//Register the core schematics
		//These are used for debugging and in case of unusual errors while loading dungeons
		defaultUp = new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, DEFAULT_UP_SCHEMATIC_PATH, true);
		defaultDown = new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, DEFAULT_DOWN_SCHEMATIC_PATH, true);
		defaultError = new DungeonGenerator(DEFAULT_DUNGEON_WEIGHT, DEFAULT_ERROR_SCHEMATIC_PATH, true);
		
		//Open the list of dungeons packaged with our mod and register their schematics
		InputStream listStream = this.getClass().getResourceAsStream(BUNDLED_DUNGEONS_LIST_PATH);
		if (listStream == null)
		{
			System.err.println("Unable to open list of bundled dungeon schematics.");
			return;
		}
		
		try
		{
			BufferedReader listReader = new BufferedReader(new InputStreamReader(listStream));
			String schematicPath = listReader.readLine();
			while (schematicPath != null)
			{
				schematicPath = schematicPath.trim();
				if (!schematicPath.isEmpty())
				{
					registerDungeon(schematicPath, true, false);
				}
				schematicPath = listReader.readLine();
			}
			listReader.close();
		}
		catch (Exception e)
		{
			System.err.println("An exception occurred while reading the list of bundled dungeon schematics.");
			e.printStackTrace();
		}
	}

	public boolean exportDungeon(World world, int centerX, int centerY, int centerZ, String exportPath)
	{
		//Write schematic data to a file
		try
		{
			DungeonSchematic dungeon = DungeonSchematic.copyFromWorld(world,
					centerX - MAX_EXPORT_RADIUS, centerY - MAX_EXPORT_RADIUS, centerZ - MAX_EXPORT_RADIUS,
					MAX_DUNGEON_WIDTH, MAX_DUNGEON_HEIGHT, MAX_DUNGEON_LENGTH, true);
			dungeon.applyExportFilters(properties);
			dungeon.writeToFile(exportPath);
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
					
					if(getDungeonDataInChain(dimHelper.instance.getDimData(incoming.locDimID)).contains(dungeon))
					{
						flag=false;
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
		dimHelper.instance.getDimData(incoming.destDimID).dungeonGenerator = dungeon;
		//dimHelper.instance.getDimData(incoming.destDimID).dungeonGenerator = defaultUp;
	}

	public Collection<String> getDungeonNames() {

		//Use a HashSet to guarantee that all dungeon names will be distinct.
		//This shouldn't be necessary if we keep proper lists without repetitions,
		//but it's a fool-proof workaround.
		HashSet<String> dungeonNames = new HashSet<String>();
		dungeonNames.addAll( parseDungeonNames(registeredDungeons) );
		dungeonNames.addAll( parseDungeonNames(untaggedDungeons) );
		
		//Sort dungeon names alphabetically
		ArrayList<String> sortedNames = new ArrayList<String>(dungeonNames);
		Collections.sort(sortedNames, String.CASE_INSENSITIVE_ORDER);
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
	public static ArrayList<DungeonGenerator> getDungeonDataInChain(DimData dimData)
	{
		DimData startingDim = dimHelper.instance.getDimData(dimHelper.instance.getLinkDataFromCoords(dimData.exitDimLink.destXCoord, dimData.exitDimLink.destYCoord, dimData.exitDimLink.destZCoord, dimData.exitDimLink.destDimID).destDimID);

		return getDungeonDataBelow(startingDim);
	}
	private static ArrayList<DungeonGenerator> getDungeonDataBelow(DimData dimData)
	{
		ArrayList<DungeonGenerator> dungeonData = new ArrayList<DungeonGenerator>();
		if(dimData.dungeonGenerator!=null)
		{
			dungeonData.add(dimData.dungeonGenerator);
			
			for(LinkData link : dimData.getLinksInDim())
			{
				if(dimHelper.dimList.containsKey(link.destDimID))
				{
					if(dimHelper.instance.getDimData(link.destDimID).dungeonGenerator!=null&&dimHelper.instance.getDimDepth(link.destDimID)==dimData.depth+1)
					{
						for(DungeonGenerator dungeonGen :getDungeonDataBelow(dimHelper.instance.getDimData(link.destDimID)) )
						{
							if(!dungeonData.contains(dungeonGen))
							{
								dungeonData.add(dungeonGen);
							}
						}
					}
				}
			}
		}
		return dungeonData;
	}
}