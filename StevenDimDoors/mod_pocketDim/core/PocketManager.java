package StevenDimDoors.mod_pocketDim.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.helpers.Compactor;
import StevenDimDoors.mod_pocketDim.helpers.DeleteFolder;
import StevenDimDoors.mod_pocketDim.saving.DDSaveHandler;
import StevenDimDoors.mod_pocketDim.saving.IPackable;
import StevenDimDoors.mod_pocketDim.saving.PackedDimData;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateSource;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;
import StevenDimDoors.mod_pocketDim.watcher.UpdateWatcherProxy;

/**
 * This class regulates all the operations involving the storage and manipulation of dimensions. It handles saving dim data, teleporting the player, and 
 * creating/registering new dimensions as well as loading old dimensions on startup
 */
public class PocketManager
{	
	private static class InnerDimData extends NewDimData implements IPackable<PackedDimData>
	{
		// This class allows us to instantiate NewDimData indirectly without exposing
		// a public constructor from NewDimData. It's meant to stop us from constructing
		// instances of NewDimData going through PocketManager. In turn, that enforces
		// that any link destinations must be real dimensions controlled by PocketManager.

		public InnerDimData(int id, InnerDimData parent, boolean isPocket, boolean isDungeon,
			IUpdateWatcher<Point4D> linkWatcher)
		{
			super(id, parent, isPocket, isDungeon, linkWatcher);
		}
		
		public InnerDimData(int id, InnerDimData root)
		{
			// This constructor is meant for client-side code only
			super(id, root);
		}
		
		public void clear()
		{
			// If this dimension has a parent, remove it from its parent's list of children
			if (parent != null)
			{
				parent.children.remove(this);
			}
			// Remove this dimension as the parent of its children
			for (NewDimData child : children)
			{
				child.parent = null;
			}
			// Clear all fields
			id = Integer.MIN_VALUE;
			linkMapping.clear();
			linkMapping = null;
			linkList.clear();
			linkList = null;
			children.clear();
			children = null;
			isDungeon = false;
			isFilled = false;
			depth = Integer.MIN_VALUE;
			packDepth = Integer.MIN_VALUE;
			origin = null;
			orientation = Integer.MIN_VALUE;
			dungeon = null;
			linkWatcher = null;
		}

		@Override
		public String name()
		{
			return String.valueOf(id);
		}

		@Override
		public PackedDimData pack()
		{
			// FIXME: IMPLEMENTATION PLZTHX
			return null;
		}
	}
	
	private static class ClientLinkWatcher implements IUpdateWatcher<Point4D>
	{
		@Override
		public void onCreated(Point4D source)
		{
			NewDimData dimension = getDimensionData(source.getDimension());
			dimension.createLink(source.getX(), source.getY(), source.getZ(), LinkTypes.CLIENT_SIDE);
		}

		@Override
		public void onDeleted(Point4D source)
		{
			NewDimData dimension = getDimensionData(source.getDimension());
			dimension.deleteLink(source.getX(), source.getY(), source.getZ());
		}
	}
	
	private static class ClientDimWatcher implements IUpdateWatcher<ClientDimData>
	{
		@Override
		public void onCreated(ClientDimData data)
		{
			registerClientDimension(data.ID, data.RootID);
		}

		@Override
		public void onDeleted(ClientDimData data)
		{
			deletePocket(getDimensionData(data.ID), false);
		}
	}

	private static class DimRegistrationCallback implements IDimRegistrationCallback
	{
		// We use this class to provide Compactor with the ability to send us dim data without
		// having to instantiate a bunch of data containers and without exposing an "unsafe"
		// creation method for anyone to call. Integrity protection for the win! It's like
		// exposing a private constructor ONLY to a very specific trusted class.
		
		@Override
		public NewDimData registerDimension(int dimensionID, int rootID)
		{
			return registerClientDimension(dimensionID, rootID);
		}
	}
	
	private static int OVERWORLD_DIMENSION_ID = 0;

	private static volatile boolean isLoading = false;
	private static volatile boolean isLoaded = false;
	private static volatile boolean isSaving = false;
	private static final UpdateWatcherProxy<Point4D> linkWatcher = new UpdateWatcherProxy<Point4D>();
	private static final UpdateWatcherProxy<ClientDimData> dimWatcher = new UpdateWatcherProxy<ClientDimData>();
	private static ArrayList<NewDimData> rootDimensions = null;

	//HashMap that maps all the dimension IDs registered with DimDoors to their DD data.
	private static HashMap<Integer, InnerDimData> dimensionData = null;

	public static boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * simple method called on startup to register all dims saved in the dim list. Only tries to register pocket dims, though. Also calls load()
	 * @return
	 */
	public static void load()
	{
		if (isLoaded)
		{
			throw new IllegalStateException("Pocket dimensions have already been loaded!");
		}
		if (isLoading)
		{
			return;
		}

		isLoading = true;
		dimensionData = new HashMap<Integer, InnerDimData>();
		rootDimensions = new ArrayList<NewDimData>();
		
		//Register Limbo
		DDProperties properties = DDProperties.instance();
		registerDimension(properties.LimboDimensionID, null, false, false);
		
		loadInternal();
		
		//Register pocket dimensions
		registerPockets(properties);
		
		isLoaded = true;
		isLoading = false;
	}

	public boolean resetDungeon(NewDimData target)
	{
		// We can't reset the dimension if it's currently loaded or if it's not a dungeon.
		// We cast to InnerDimData so that if anyone tries to be a smartass and create their
		// own version of NewDimData, this will throw an exception.
		InnerDimData dimension = (InnerDimData) target;
		if (dimension.isDungeon() && DimensionManager.getWorld(dimension.id()) == null)
		{
			File saveDirectory = new File(DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/pocketDimID" + dimension.id());
			if (DeleteFolder.deleteFolder(saveDirectory))
			{
				dimension.setFilled(false);
				return true;
			}
		}
		return false;		
	}

	public static boolean deletePocket(NewDimData target, boolean deleteFolder)
	{
		// We can't delete the dimension if it's currently loaded or if it's not actually a pocket.
		// We cast to InnerDimData so that if anyone tries to be a smartass and create their
		// own version of NewDimData, this will throw an exception.
		InnerDimData dimension = (InnerDimData) target;
		if (dimension.isPocketDimension() && DimensionManager.getWorld(dimension.id()) == null)
		{
			if (deleteFolder)
			{
				File saveDirectory = new File(DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/pocketDimID" + dimension.id());
				DeleteFolder.deleteFolder(saveDirectory);
			}
			dimensionData.remove(dimension.id());
			// Raise the dim deleted event
			dimWatcher.onDeleted(new ClientDimData(dimension));
			dimension.clear();
			return true;
		}
		return false;
	}
	
	private static void registerPockets(DDProperties properties)
	{
		for (NewDimData dimension : dimensionData.values())
		{
			if (dimension.isPocketDimension())
			{
				try
				{
					DimensionManager.registerDimension(dimension.id(), properties.PocketProviderID);
				}
				catch (Exception e)
				{
					System.err.println("Could not register pocket dimension #" + dimension.id() + ". Probably caused by a version update/save data corruption/other mods.");
					e.printStackTrace();
				}
			}
		}
	}

	private static void unregisterPockets()
	{
		for (NewDimData dimension : dimensionData.values())
		{
			if (dimension.isPocketDimension())
			{
				try
				{
					DimensionManager.unregisterDimension(dimension.id());
				}
				catch (Exception e)
				{
					System.err.println("An unexpected error occurred while unregistering pocket dimension #" + dimension.id() + ":");
					e.printStackTrace();
				}
			}	
		}
	}

	/**
	 * loads the dim data from the saved hashMap. Also handles compatibility with old saves, see OldSaveHandler
	 */
	private static void loadInternal()
	{	
		if (!DimensionManager.getWorld(OVERWORLD_DIMENSION_ID).isRemote &&
			DimensionManager.getCurrentSaveRootDirectory() != null)
		{
			// Load and register blacklisted dimension IDs
			
			// Load save data
			System.out.println("Loading Dimensional Doors save data...");
			if (DDSaveHandler.loadAll())
			{
				System.out.println("Loaded successfully!");
			}
		}
	}
	
	public static void save()
	{
		if (!isLoaded)
		{
			return;
		}
		World world = DimensionManager.getWorld(OVERWORLD_DIMENSION_ID);
		if (world == null || world.isRemote || DimensionManager.getCurrentSaveRootDirectory() == null)
		{
			return;
		}
		//Check this last to make sure we set the flag shortly after.
		if (isSaving)
		{
			return;
		}
		isSaving = true;
		
		try
		{
			System.out.println("Writing Dimensional Doors save data...");
			if ( DDSaveHandler.saveAll(dimensionData.values()) )
			{
				System.out.println("Saved successfully!");				
			}
		}
		catch (Exception e)
		{
			// Wrap the exception in a RuntimeException so functions that call
			// PocketManager.save() don't need to catch it. We want MC to
			// crash if something really bad happens rather than ignoring it!
			throw new RuntimeException(e);
		}
		finally
		{
			isSaving = false;
		}
	}
	
	public static WorldServer loadDimension(int id)
	{
		WorldServer world = DimensionManager.getWorld(id);
		if (world == null)
		{
			DimensionManager.initDimension(id);
			world = DimensionManager.getWorld(id);
		}
		else if (world.provider == null)
		{
			DimensionManager.initDimension(id);
			world = DimensionManager.getWorld(id);
		}
		return world;
	}

	public static NewDimData registerDimension(World world)
	{
		return registerDimension(world.provider.dimensionId, null, false, false);
	}

	public static NewDimData registerPocket(NewDimData parent, boolean isDungeon)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("parent cannot be null. A pocket dimension must always have a parent dimension.");
		}
		
		DDProperties properties = DDProperties.instance();
		int dimensionID = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimensionID, properties.PocketProviderID);
		return registerDimension(dimensionID, (InnerDimData) parent, true, isDungeon);
	}

	private static NewDimData registerDimension(int dimensionID, InnerDimData parent, boolean isPocket, boolean isDungeon)
	{
		if (dimensionData.containsKey(dimensionID))
		{
			throw new IllegalArgumentException("Cannot register a dimension with ID = " + dimensionID + " because it has already been registered.");
		}

		InnerDimData dimension = new InnerDimData(dimensionID, parent, isPocket, isDungeon, linkWatcher);
		dimensionData.put(dimensionID, dimension);
		if (!dimension.isPocketDimension())
		{
			rootDimensions.add(dimension);
		}
		dimWatcher.onCreated(new ClientDimData(dimension));
		
		return dimension;
	}
	
	private static NewDimData registerClientDimension(int dimensionID, int rootID)
	{
		// No need to raise events here since this code should only run on the client side
		// getDimensionData() always handles root dimensions properly, even if the weren't defined before

		// SenseiKiwi: I'm a little worried about how getDimensionData will raise
		// an event when it creates any root dimensions... Needs checking later.
		
		InnerDimData root = (InnerDimData) getDimensionData(rootID);
		InnerDimData dimension;

		if (rootID != dimensionID)
		{
			dimension = dimensionData.get(dimensionID);
			if (dimension == null)
			{
				dimension = new InnerDimData(dimensionID, root);
				dimensionData.put(dimension.id(), dimension);
			}
		}
		else
		{
			dimension = root;
		}
		return dimension;
	}

	public static NewDimData getDimensionData(World world)
	{	
		return getDimensionData(world.provider.dimensionId);
	}

	public static NewDimData getDimensionData(int dimensionID)
	{
		//Retrieve the data for a dimension. If we don't have a record for that dimension,
		//assume it's a non-pocket dimension that hasn't been initialized with us before
		//and create a NewDimData instance for it.
		//Any pocket dimension must be listed with PocketManager to have a dimension ID
		//assigned, so it's safe to assume that any unknown dimensions don't belong to us.
		
		NewDimData dimension = PocketManager.dimensionData.get(dimensionID);
		if (dimension == null)
		{
			dimension = registerDimension(dimensionID, null, false, false);
		}
		return dimension;
	}

	public static Iterable<? extends NewDimData> getDimensions()
	{
		return dimensionData.values();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<NewDimData> getRootDimensions()
	{
		return (ArrayList<NewDimData>) rootDimensions.clone();
	}

	public static void unload()
	{
		if (!isLoaded)
		{
			throw new IllegalStateException("Pocket dimensions have already been unloaded!");
		}
		
		save();
		unregisterPockets();
		dimensionData = null;
		rootDimensions = null;
		isLoaded = false;
	}
	
	public static DimLink getLink(int x, int y, int z, World world)
	{
		return getLink(x, y, z, world.provider.dimensionId);
	}

	public static DimLink getLink(int x, int y, int z, int dimensionID)
	{
		NewDimData dimension = dimensionData.get(dimensionID);
		if (dimension != null)
		{
			return dimension.getLink(x, y, z);
		}
		else
		{
			return null;
		}
	}
	
	public static void registerDimWatcher(IUpdateWatcher<ClientDimData> watcher)
	{
		dimWatcher.registerReceiver(watcher);
	}
	
	public static boolean unregisterDimWatcher(IUpdateWatcher<ClientDimData> watcher)
	{
		return dimWatcher.unregisterReceiver(watcher);
	}
	
	public static void registerLinkWatcher(IUpdateWatcher<Point4D> watcher)
	{
		linkWatcher.registerReceiver(watcher);
	}
	
	public static boolean unregisterLinkWatcher(IUpdateWatcher<Point4D> watcher)
	{
		return linkWatcher.unregisterReceiver(watcher);
	}

	public static void getWatchers(IUpdateSource updateSource)
	{
		updateSource.registerWatchers(new ClientDimWatcher(), new ClientLinkWatcher());
	}
	
	public static void writePacket(DataOutputStream output) throws IOException
	{
		// Write a very compact description of our dimensions and links to be sent to a client
		Compactor.write(dimensionData.values(), output);
	}
	
	public static void readPacket(DataInputStream input) throws IOException
	{
		if (isLoaded)
		{
			throw new IllegalStateException("Pocket dimensions have already been loaded!");
		}
		if (isLoading)
		{
			throw new IllegalStateException("Pocket dimensions are already loading!");
		}

		isLoading = true;
		dimensionData = new HashMap<Integer, InnerDimData>();

		// Load compacted client-side dimension data
		Compactor.readDimensions(input, new DimRegistrationCallback());
		
		// Register pocket dimensions
		DDProperties properties = DDProperties.instance();
		registerPockets(properties);
		
		isLoaded = true;
		isLoading = false;
	}
}
