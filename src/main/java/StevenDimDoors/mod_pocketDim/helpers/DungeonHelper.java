package StevenDimDoors.mod_pocketDim.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.regex.Pattern;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPackConfig;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPackConfigReader;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonType;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;
import StevenDimDoors.mod_pocketDim.util.FileFilters;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

public class DungeonHelper
{
	private static DungeonHelper instance = null;
	private static DDProperties properties = null;
	
	public static final Pattern SCHEMATIC_NAME_PATTERN = Pattern.compile("[A-Za-z0-9_\\-]+");
	public static final Pattern DUNGEON_NAME_PATTERN = Pattern.compile("[A-Za-z0-9\\-]+");
	
	public static final String SCHEMATIC_FILE_EXTENSION = ".schematic";
	
	private static final String DEFAULT_ERROR_SCHEMATIC_PATH = "/schematics/core/somethingBroke.schematic";
	private static final String DUNGEON_CREATION_GUIDE_SOURCE_PATH = "/assets/dimdoors/text/How_to_add_dungeons.txt";
	private static final String BUNDLED_PACK_BASE_PATH = "/schematics/";
	private static final String STANDARD_CONFIG_FILE_NAME = "rules.txt";
	
	private static final int NETHER_DIMENSION_ID = -1;
	
	private static final int MIN_PACK_SWITCH_CHANCE = 0;
	private static final int PACK_SWITCH_CHANCE_PER_LEVEL = 1;
	private static final int MAX_PACK_SWITCH_CHANCE = 500;
	private static final int START_PACK_SWITCH_CHANCE = MAX_PACK_SWITCH_CHANCE / 9;
	
	private static final int DEFAULT_DUNGEON_WEIGHT = 100;
	public static final int MIN_DUNGEON_WEIGHT = 1; //Prevents MC's random selection algorithm from throwing an exception
	public static final int MAX_DUNGEON_WEIGHT = 10000; //Used to prevent overflows and math breaking down
	
	private static final int MAX_EXPORT_RADIUS = 50;
	public static final short MAX_DUNGEON_WIDTH = 2 * MAX_EXPORT_RADIUS + 1;
	public static final short MAX_DUNGEON_HEIGHT = MAX_DUNGEON_WIDTH;
	public static final short MAX_DUNGEON_LENGTH = MAX_DUNGEON_WIDTH;
	
	private ArrayList<DungeonData> untaggedDungeons = new ArrayList<DungeonData>();
	private ArrayList<DungeonData> registeredDungeons = new ArrayList<DungeonData>();
 
	private DungeonPack RuinsPack;
	private DungeonPack NetherPack;
	private HashMap<String, DungeonPack> dungeonPackMapping = new HashMap<String, DungeonPack>();
	private ArrayList<DungeonPack> dungeonPackList = new ArrayList<DungeonPack>();
	
	private DungeonData defaultError;
	
	private DungeonHelper()
	{
		//Load our reference to the DDProperties singleton
		if (properties == null)
			properties = DDProperties.instance();
		
		registerDungeons();
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
	
	private void registerDungeons()
	{
		File file = new File(properties.CustomSchematicDirectory);
		if (file.exists() || file.mkdir())
		{
			copyfile.copyFile(DUNGEON_CREATION_GUIDE_SOURCE_PATH, file.getAbsolutePath() + "/How_to_add_dungeons.txt");
		}
		
		DungeonPackConfigReader reader = new DungeonPackConfigReader();		
		registerBundledDungeons(reader);
		registerCustomDungeons(properties.CustomSchematicDirectory, reader);
	}
	
	private static DungeonPackConfig loadDungeonPackConfig(String configPath, String name, boolean isInternal, DungeonPackConfigReader reader)
	{
		try
		{
			DungeonPackConfig config;
			if (isInternal)
			{
				config = reader.readFromResource(configPath);
			}
			else
			{
				config = reader.readFromFile(configPath);
			}
			config.setName(name);
			return config;
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Could not find a dungeon pack config file: " + configPath);
		}
		catch (Exception e) // handles IOException and ConfigurationProcessingException
		{
			System.err.println(e.getMessage());
			if (e.getCause() != null)
			{
				System.err.println(e.getCause());
			}
		}
		return null;
	}
	
	private DungeonPack registerDungeonPack(String directory, Iterable<String> schematics, boolean isInternal, boolean verbose, DungeonPackConfigReader reader)
	{
		//First determine the pack's name and validate it
		File packDirectory = new File(directory);
		String name = packDirectory.getName().toUpperCase();
		//TODO: ADD VALIDATION HERE?
		
		//Check for naming conflicts
		//That could happen if a user has a custom pack with a name that conflicts with a bundled pack,
		//or if a user is running Linux and has two directories with names differing only by capitalization.
		
		DungeonPack pack = dungeonPackMapping.get(name);
		if (pack == null)
		{
			//Load the pack's configuration file
			
			String configPath;
			if (isInternal)
			{
				configPath = directory + "/" + STANDARD_CONFIG_FILE_NAME;
			}
			else
			{
				configPath = directory + File.separator + STANDARD_CONFIG_FILE_NAME;
			}
			DungeonPackConfig config = loadDungeonPackConfig(configPath, name, isInternal, reader);
			if (config == null)
			{
				System.err.println("Could not load config file: " + configPath);
				return null;
			}
			
			//Register the pack
			pack = new DungeonPack(config);
			dungeonPackMapping.put(name, pack);
			dungeonPackList.add(pack);
		}
		else
		{
			//Show a warning that there is a naming conflict but keep going. People can use this to extend
			//our built-in packs with custom schematics without tampering with our mod's JAR file.
			System.err.println("A dungeon pack has the same name as another pack that has already been loaded: " + directory);
			System.err.println("We will try to load its schematics but will not check its config file.");
		}
		
		//Register the dungeons! ^_^
		for (String schematicPath : schematics)
		{
			registerDungeon(schematicPath, pack, isInternal, verbose);
		}
		return pack;
	}
	
	public List<DungeonData> getRegisteredDungeons()
	{
		return Collections.unmodifiableList(this.registeredDungeons);
	}
	
	public List<DungeonData> getUntaggedDungeons()
	{
		return Collections.unmodifiableList(this.untaggedDungeons);
	}
	
	public DungeonData getDefaultErrorDungeon()
	{
		return defaultError;
	}
	
	public DungeonPack getDungeonPack(String name)
	{
		//TODO: This function might be obsolete after the new save format is implemented.
		return dungeonPackMapping.get(name.toUpperCase());
	}
	
	private DungeonPack getDimDungeonPack(NewDimData dimension)
	{
		// TODO: Drop support for dim-based packs and switch to embedding the pack
		// in the link data itself. That would solve the dungeon pre-generation issue.
		// Gateways should dictate which packs are being used, not the dimensions.
		
		DungeonPack pack;
		DungeonData dungeon = dimension.dungeon();
		if (dungeon != null)
		{
			pack = dungeon.dungeonType().Owner;
			
			//Make sure the pack isn't null. This can happen for dungeons with the special UNKNOWN type.
			if (pack == null)
			{
				pack = RuinsPack;
			}
		}
		else
		{
			if (dimension.id() == NETHER_DIMENSION_ID)
			{
				pack = NetherPack;
			}
			else
			{
				pack = RuinsPack;
			}
		}
		return pack;
	}
	
	public DimLink createCustomDungeonDoor(World world, int x, int y, int z)
	{
		//Create a link above the specified position. Link to a new pocket dimension.
		NewDimData dimension = PocketManager.getDimensionData(world);
		DimLink link = dimension.createLink(x, y + 1, z, LinkType.POCKET, 3);

		//Place a Warp Door linked to that pocket
		ItemDimensionalDoor.placeDoorBlock(world, x, y, z, 3, mod_pocketDim.warpDoor);
		
		return link;
	}
	
	public boolean validateDungeonType(String type, DungeonPack pack)
	{
		return pack.isKnownType(type);
	}
	
	public boolean validateSchematicName(String name, DungeonPack pack)
	{
		String[] dungeonData;
		
		if (!name.endsWith(SCHEMATIC_FILE_EXTENSION))
			return false;
		
		dungeonData = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length()).split("_");

		//Check for a valid number of parts
		if (dungeonData.length < 3 || dungeonData.length > 4)
			return false;

		//Check if the dungeon type is valid
		if (!validateDungeonType(dungeonData[0], pack))
			return false;
		
		//Check if the name is valid
		if (!SCHEMATIC_NAME_PATTERN.matcher(dungeonData[1]).matches())
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
				if (weight < MIN_DUNGEON_WEIGHT || weight > MAX_DUNGEON_WEIGHT)
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
	
	public void registerDungeon(String schematicPath, DungeonPack pack, boolean isInternal, boolean verbose)
	{
		//We use schematicPath as the real path for internal files (inside our JAR) because it seems
		//that File tries to interpret it as a local drive path and mangles it.
		File schematicFile = new File(schematicPath);
		String name = schematicFile.getName();
		String path = isInternal ? schematicPath : schematicFile.getAbsolutePath();
		try
		{
			if (validateSchematicName(name, pack))
			{
				//Strip off the file extension while splitting the file name
				String[] dungeonData = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length()).split("_");
				
				DungeonType dungeonType = pack.getType(dungeonData[0]);
				boolean isOpen = dungeonData[2].equalsIgnoreCase("open");
				int weight = (dungeonData.length == 4) ? Integer.parseInt(dungeonData[3]) : DEFAULT_DUNGEON_WEIGHT;
				
				//Add this custom dungeon to the list corresponding to its type
				DungeonData dungeon = new DungeonData(path, isInternal, dungeonType, isOpen, weight);

				pack.addDungeon(dungeon);
				registeredDungeons.add(dungeon);
				if (verbose)
				{
					System.out.println("Registered dungeon: " + name);
				}
			}
			else
			{
				if (verbose)
				{
					System.out.println("The following dungeon name is invalid for its given pack. It will not be generated naturally: " + schematicPath);
				}
				untaggedDungeons.add(new DungeonData(path, isInternal, DungeonType.UNKNOWN_TYPE, true, DEFAULT_DUNGEON_WEIGHT));
				System.out.println("Registered untagged dungeon: " + name);
			}
		}
		catch (Exception e)
		{
			System.err.println("Failed to register dungeon: " + name);
			e.printStackTrace();
		}
	}

	private void registerCustomDungeons(String path, DungeonPackConfigReader reader)
	{
		File[] schematics;
		File[] packDirectories;
		File[] packFiles;
		ArrayList<String> packFilePaths;
		File directory = new File(path);
		FileFilter schematicFileFilter = new FileFilters.FileExtensionFilter(SCHEMATIC_FILE_EXTENSION);

		//Check that the Ruins pack has been loaded
		if (RuinsPack == null)
		{
			throw new IllegalStateException("Cannot register custom dungeons without first loading the Ruins dungeon pack.");
		}
		
		//Load stray dungeons directly in the custom dungeons folder
		schematics = directory.listFiles(schematicFileFilter);
		if (schematics != null)
		{
			for (File schematicFile : schematics)
			{
				registerDungeon(schematicFile.getPath(), RuinsPack, false, true);
			}
		}
		else
		{
			System.err.println("Could not retrieve the list of schematics stored in the custom dungeons directory!");
		}
		schematics = null; //Release memory
		
		//Load the custom dungeon packs
		packDirectories = directory.listFiles(new FileFilters.DirectoryFilter());
		if (packDirectories != null)
		{
			//Loop through each directory, which is assumed to be a dungeon pack
			for (File packDirectory : packDirectories)
			{
				//List the schematics within the dungeon pack directory
				packFiles = packDirectory.listFiles(schematicFileFilter);
				if (packFiles != null)
				{
					//Copy the pack files' paths into an ArrayList for use with registerDungeonPack()
					packFilePaths = new ArrayList<String>(packFiles.length);
					for (File packFile : packFiles)
					{
						packFilePaths.add(packFile.getPath());
					}
					
					registerDungeonPack(packDirectory.getAbsolutePath(), packFilePaths, false, true, reader);
				}
				else
				{
					System.err.println("Could not retrieve the list of schematics in a dungeon pack: " + packDirectory.getPath());
				}
			}
		}
		else
		{
			System.err.println("Could not retrieve the list of dungeon pack directories in the custom dungeons directory!");
		}
	}

	private void registerBundledDungeons(DungeonPackConfigReader reader)
	{
		//Register the core schematics
		//These are used for debugging and in case of unusual errors while loading dungeons
		defaultError = new DungeonData(DEFAULT_ERROR_SCHEMATIC_PATH, true, DungeonType.UNKNOWN_TYPE, true, DEFAULT_DUNGEON_WEIGHT);
		
		//Open the list of dungeons packaged with our mod and register their schematics
		RuinsPack = registerBundledPack("Ruins", reader);
		NetherPack = registerBundledPack("Nether", reader);
		
		System.out.println("Finished registering bundled dungeon packs");
	}
	
	private DungeonPack registerBundledPack(String name, DungeonPackConfigReader reader)
	{
		System.out.println("Registering bundled dungeon pack: " + name);
		
		String packPath = BUNDLED_PACK_BASE_PATH + name.toLowerCase();
		String listPath = packPath + ".txt";
		InputStream listStream = this.getClass().getResourceAsStream(listPath);
		
		if (listStream == null)
		{
			throw new IllegalStateException("Failed to open the list of bundled dungeon schematics for " + name);
		}
		
		ArrayList<String> schematics = new ArrayList<String>();
		try
		{
			// Read the list of schematics that come with a bundled pack
			BufferedReader listReader = new BufferedReader(new InputStreamReader(listStream));
			String schematicPath = listReader.readLine();
			while (schematicPath != null)
			{
				schematicPath = schematicPath.trim();
				if (!schematicPath.isEmpty())
				{
					schematics.add(schematicPath);
				}
				schematicPath = listReader.readLine();
			}
			listReader.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException("An unexpected error occured while trying to read the list of schematics for the " + name + " bundled dungeon pack. This would inevitably cause Dimensional Doors to crash during runtime.", e);
		}
		
		// Register the pack
		DungeonPack pack = registerDungeonPack(packPath, schematics, true, false, reader);
		if (pack == null)
		{
			throw new RuntimeException("Failed to load the " + name + " bundled dungeon pack. This would inevitably cause Dimensional Doors to crash during runtime.");
		}
		return pack;
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

	public DungeonData selectNextDungeon(NewDimData parent, Random random)
	{
		DungeonPack pack = getDimDungeonPack(parent);
		DungeonData selection;
		DungeonPackConfig config;
		DungeonPack selectedPack;
		
		try
		{
			config = pack.getConfig();
			selectedPack = pack;
			
			// Are we allowed to switch to another dungeon pack?
			if (config.allowPackChangeOut())
			{
				// Calculate the chance of switching to a different pack type
				int packSwitchChance;
				if (parent.isPocketDimension())
				{
					packSwitchChance = MIN_PACK_SWITCH_CHANCE + parent.packDepth() * PACK_SWITCH_CHANCE_PER_LEVEL;
				}
				else
				{
					packSwitchChance = START_PACK_SWITCH_CHANCE;					
				}

				// Decide randomly whether to switch packs or not
				if (random.nextInt(MAX_PACK_SWITCH_CHANCE) < packSwitchChance)
				{
					// Find another pack
					selectedPack = getRandomDungeonPack(pack, random);
				}
			}
			
			//Pick the next dungeon
			selection = selectedPack.getNextDungeon(parent, random);
		}
		catch (Exception e)
		{
			System.err.println("An exception occurred while selecting a dungeon:");
			e.printStackTrace();
			
			if (!pack.isEmpty())
			{
				selection = pack.getRandomDungeon(random);
			}
			else
			{
				selection = defaultError;
			}
		}
		return selection;
	}

	@SuppressWarnings("unchecked")
	private DungeonPack getRandomDungeonPack(DungeonPack current, Random random)
	{
		DungeonPack selection = current;
		ArrayList<WeightedContainer<DungeonPack>> packs = new ArrayList<WeightedContainer<DungeonPack>>(dungeonPackList.size());

		//Load up a list of weighted items with any usable dungeon packs that is not the current pack
		for (DungeonPack pack : dungeonPackList)
		{
			DungeonPackConfig config = pack.getConfig();
			if (pack != current && config.allowPackChangeIn() && !pack.isEmpty())
			{
				packs.add(new WeightedContainer<DungeonPack>(pack, config.getPackWeight()));
			}
		}
		if (!packs.isEmpty())
		{
			//Pick a random dungeon pack
			selection = ((WeightedContainer<DungeonPack>) WeightedRandom.getRandomItem(random, packs)).getData();
		}
		return selection;
	}

	public ArrayList<String> getDungeonNames()
	{
		// Use a HashSet to guarantee that all dungeon names will be distinct.
		// This shouldn't be necessary if we keep proper lists without repetitions,
		// but it's a fool-proof workaround.
		HashSet<String> dungeonNames = new HashSet<String>();
		dungeonNames.addAll( parseDungeonNames(registeredDungeons) );
		dungeonNames.addAll( parseDungeonNames(untaggedDungeons) );
		
		//Sort dungeon names alphabetically
		ArrayList<String> sortedNames = new ArrayList<String>(dungeonNames);
		Collections.sort(sortedNames, String.CASE_INSENSITIVE_ORDER);
		return sortedNames;
	}
	
	private static ArrayList<String> parseDungeonNames(ArrayList<DungeonData> dungeons)
	{
		String name;
		File schematic;
		ArrayList<String> names = new ArrayList<String>(dungeons.size());
		
		for (DungeonData dungeon : dungeons)
		{
			//Retrieve the file name and strip off the file extension
			schematic = new File(dungeon.schematicPath());
			name = schematic.getName();
			name = name.substring(0, name.length() - SCHEMATIC_FILE_EXTENSION.length());
			names.add(name);
		}
		return names;
	}
	
	/**
	 * Lists all of the dungeons found by iterating through a dimension's ancestors. The search stops when a non-dungeon dimension is found or when the pack of a dungeon differs from the specified pack.
	 * @param start - the first dimension to include in the history
	 * @param pack - the pack to which any dungeons must belong in order to be listed
	 * @param maxSize - the maximum number of dungeons that can be listed
	 * @return a list of dungeons used in a given chain
	 */
	public static ArrayList<DungeonData> getDungeonChainHistory(NewDimData start, DungeonPack pack, int maxSize)
	{
		if (start == null)
		{
			throw new IllegalArgumentException("dimension cannot be null.");
		}
		int count = 0;
		NewDimData current = start;
		DungeonData dungeon = current.dungeon();
		ArrayList<DungeonData> history = new ArrayList<DungeonData>();
		
		while (count < maxSize && dungeon != null && dungeon.dungeonType().Owner == pack)
		{
			history.add(dungeon);
			current = current.parent();
			dungeon = current.dungeon();
			count++;
		}
		return history;
	}
	
	/**
	 * Performs a breadth-first listing of all dungeons rooted at a specified dimension. Only dungeons from the specified pack will be included.
	 * @param root - the pocket dimension that serves as the root for the dungeon tree
	 * @param pack - the pack to which any dungeons must belong in order to be listed
	 * @param maxSize - the maximum number of dungeons that can be listed
	 * @return a list of the dungeons used in a given dungeon tree
	 */
	public static ArrayList<DungeonData> listDungeonsInTree(NewDimData root, DungeonPack pack, int maxSize)
	{
		int count = 0;
		NewDimData current;
		DungeonData dungeon;
		ArrayList<DungeonData> dungeons = new ArrayList<DungeonData>();
		Queue<NewDimData> pendingDimensions = new LinkedList<NewDimData>();	
		pendingDimensions.add(root);
		
		// Perform a breadth-first search through the dungeon graph
		while (dungeons.size() < maxSize && !pendingDimensions.isEmpty())
		{
			current = pendingDimensions.remove();
			dungeon = current.dungeon();
			// Check that this is a dungeon, and if so, that it belongs to the pack that we want
			if (dungeon != null && dungeon.dungeonType().Owner == pack)
			{
				dungeons.add(dungeon);
				// Add all child dungeons for checking later
				for (NewDimData child : current.children())
				{
					pendingDimensions.add(child);
				}
			}
		}
		return dungeons;
	}
	
	/**
	 * Gets the highest ancestor of a dimension with a dungeon that belongs to the specified pack.
	 * @param dimension - the first dimension to include in the search
	 * @param pack - the pack to which the ancestors must belong
	 * @param maxLevels - the maximum number of ancestors to check
	 * @return the highest ancestor that belongs to the specified pack within the specified levels, or <code>null</code> if none exists
	 */
	public static NewDimData getAncestor(NewDimData dimension, DungeonPack pack, int maxLevels)
	{
		// Find the ancestor of a dimension located a specified number of levels up.
		NewDimData parent = dimension;
		NewDimData current = null;
		
		// We solve this inductively. We begin with null as the first valid ancestor,
		// like a kind of virtual child dimension. Then "current" references the
		// highest valid ancestor found so far and "parent" references its parent
		for (int levels = 0; levels <= maxLevels; levels++)
		{
			if (parent == null || parent.dungeon() == null ||
				parent.dungeon().dungeonType().Owner != pack)
			{
				break;
			}
			current = parent;
			parent = parent.parent();
		}
		return current;
	}
}