package StevenDimDoors.mod_pocketDim.core;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.helpers.Compactor;
import StevenDimDoors.mod_pocketDim.helpers.DeleteFolder;
import StevenDimDoors.mod_pocketDim.saving.DDSaveHandler;
import StevenDimDoors.mod_pocketDim.saving.OldSaveImporter;
import StevenDimDoors.mod_pocketDim.saving.PackedDimData;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.watcher.ClientDimData;
import StevenDimDoors.mod_pocketDim.watcher.ClientLinkData;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;
import StevenDimDoors.mod_pocketDim.watcher.UpdateWatcherProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class regulates all the operations involving the storage and
 * manipulation of dimensions. It handles saving dim data, teleporting the
 * player, and creating/registering new dimensions as well as loading old
 * dimensions on startup
 */
public class PocketManager
{
	private static class InnerDimData extends NewDimData
	{
		// This class allows us to instantiate NewDimData indirectly without
		// exposing
		// a public constructor from NewDimData. It's meant to stop us from
		// constructing
		// instances of NewDimData going through PocketManager. In turn, that
		// enforces
		// that any link destinations must be real dimensions controlled by
		// PocketManager.

		public InnerDimData(int id, InnerDimData parent, DimensionType type, IUpdateWatcher<ClientLinkData> linkWatcher)
		{
			super(id, parent, type, linkWatcher);
		}

		public InnerDimData(int id, NewDimData root, DimensionType type)
		{
			// This constructor is meant for client-side code only
			super(id, root, type);
		}

	}

	public static class ClientLinkWatcher implements IUpdateWatcher<ClientLinkData>
	{
		@Override
		public void onCreated(ClientLinkData link)
		{
            Point4D source = link.point;
            NewDimData dimension = getDimensionData(source.getDimension());
            if (dimension != null && dimension.getLink(source.getX(), source.getY(), source.getZ()) == null)
			    dimension.createLink(source, LinkType.CLIENT, 0, link.lock);
		}

		@Override
		public void onDeleted(ClientLinkData link)
		{
			Point4D source = link.point;
			NewDimData dimension = getDimensionData(source.getDimension());
            if (dimension != null && dimension.getLink(source.getX(),source.getY(),source.getZ()) != null)
			    dimension.deleteLink(source.getX(), source.getY(), source.getZ());
		}

		@Override
		public void update(ClientLinkData link)
		{
			Point4D source = link.point;
			NewDimData dimension = getDimensionData(source.getDimension());
            if (dimension != null) {
                DimLink dLink = dimension.getLink(source);
                dLink.lock = link.lock;
            }
		}
	}

	public static class ClientDimWatcher implements IUpdateWatcher<ClientDimData>
	{
		@Override
		public void onCreated(ClientDimData data)
		{
			registerClientDimension(data.ID, data.rootID, data.type);
		}

		@Override
		public void onDeleted(ClientDimData data)
		{
			deletePocket(getDimensionData(data.ID), false);
		}

		@Override
		public void update(ClientDimData message)
		{
			// TODO Auto-generated method stub
		}
	}

	private static class DimRegistrationCallback implements IDimRegistrationCallback
	{
		// We use this class to provide Compactor with the ability to send us
		// dim data without
		// having to instantiate a bunch of data containers and without exposing
		// an "unsafe"
		// creation method for anyone to call. Integrity protection for the win!
		// It's like
		// exposing a private constructor ONLY to a very specific trusted class.

		@Override
		public NewDimData registerDimension(int dimensionID, int rootID, DimensionType type)
		{
			return registerClientDimension(dimensionID, rootID, type);
		}
	}

	private static int OVERWORLD_DIMENSION_ID = 0;

	private static volatile boolean isLoading = false;
	private static volatile boolean isLoaded = false;
	private static volatile boolean isSaving = false;
	/**
	 * Set as true if we are a client that has connected to a dedicated server
	 */
	public static volatile boolean isConnected = false;
	private static final UpdateWatcherProxy<ClientLinkData> linkWatcher = new UpdateWatcherProxy<ClientLinkData>();
	private static final UpdateWatcherProxy<ClientDimData> dimWatcher = new UpdateWatcherProxy<ClientDimData>();
	private static ArrayList<NewDimData> rootDimensions = null;

	// HashMap that maps all the dimension IDs registered with DimDoors to their
	// DD data.
	private static HashMap<Integer, InnerDimData> dimensionData = null;
	// ArrayList that stores the dimension IDs of any dimension that has been
	// deleted.
	private static ArrayList<Integer> dimensionIDBlackList = null;

	// Stores all the personal pocket mappings
	private static HashMap<String, NewDimData> personalPocketsMapping = null;

	public static boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * simple method called on startup to register all dims saved in the dim
	 * list. Only tries to register pocket dims, though. Also calls load()
	 * 
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
		dimensionIDBlackList = new ArrayList<Integer>();
		personalPocketsMapping = new HashMap<String, NewDimData>();

		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			// Shouldnt try to load everything if we are a client
			// This was preventing onPacket from loading properly
			isLoading = false;
			isLoaded = true;
			return;
		}
		// Register Limbo
		DDProperties properties = DDProperties.instance();
		registerDimension(properties.LimboDimensionID, null, DimensionType.ROOT);

		loadInternal();

		// Register pocket dimensions
		registerPockets(properties);

		isLoaded = true;
		isLoading = false;
	}

	public static boolean registerPackedDimData(PackedDimData packedData)
	{
		InnerDimData dimData;
		DimensionType type = DimensionType.getTypeFromIndex(packedData.DimensionType);
		if (type == null)
		{
			throw new IllegalArgumentException("Invalid dimension type");
		}
		// register roots
		if (packedData.ID == packedData.ParentID)
		{
			dimData = new InnerDimData(packedData.ID, null, type, linkWatcher);
			dimData.root = dimData;
			dimData.parent = dimData;
			dimData.depth = packedData.Depth;
			dimData.isFilled = packedData.IsFilled;
			dimData.origin = new Point4D(packedData.Origin.getX(), packedData.Origin.getY(), packedData.Origin.getZ(), packedData.ID);

			PocketManager.rootDimensions.add(dimData);
		}
		else
		// register children
		{
			InnerDimData test = PocketManager.dimensionData.get(packedData.ParentID);
			dimData = new InnerDimData(packedData.ID, test, type, linkWatcher);
			dimData.isFilled = packedData.IsFilled;
			dimData.origin = new Point4D(packedData.Origin.getX(), packedData.Origin.getY(), packedData.Origin.getZ(), packedData.ID);
			dimData.root = PocketManager.createDimensionData(packedData.RootID);

			if (packedData.DungeonData != null)
			{
				dimData.dungeon = DDSaveHandler.unpackDungeonData(packedData.DungeonData);
			}

		}
		PocketManager.dimensionData.put(dimData.id, dimData);
		getDimwatcher().onCreated(new ClientDimData(dimData));

		return true;
	}

	public static boolean deletePocket(NewDimData target, boolean deleteFolder)
	{
		// We can't delete the dimension if it's currently loaded or if it's not
		// actually a pocket.
		// We cast to InnerDimData so that if anyone tries to be a smartass and
		// create their
		// own version of NewDimData, this will throw an exception.
		InnerDimData dimension = (InnerDimData) target;
		if (dimension.isPocketDimension() && DimensionManager.getWorld(dimension.id()) == null)
		{
			if (deleteFolder)
			{
				deleteDimensionFiles(dimension);
			}
			// Note: We INTENTIONALLY don't unregister the dimensions that we
			// delete with Forge. Instead, we keep them registered to stop Forge
			// from reallocating those IDs to other mods such as Mystcraft. This
			// is to prevent bugs. Blacklisted dimensions are still properly
			// unregistered when the server shuts down.
			dimensionIDBlackList.add(dimension.id);
			deleteDimensionData(dimension);
			return true;
		}
		return false;
	}

	private static void deleteDimensionFiles(InnerDimData dimension)
	{
		// We assume that the caller checks if the dimension is loaded, for the
		// sake of efficiency. Don't call this on a loaded dimension or bad
		// things will happen!
		String saveRootPath = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath();
		File saveDirectory = new File(saveRootPath + "/DimensionalDoors/pocketDimID" + dimension.id());
		DeleteFolder.deleteFolder(saveDirectory);
		File dataFile = new File(saveRootPath + "/DimensionalDoors/data/dim_" + dimension.id() + ".txt");
		dataFile.delete();
	}

	private static void deleteDimensionData(InnerDimData dimension)
	{
		// We assume that the caller checks if the dimension is loaded, for the
		// sake of efficiency. Don't call this on a loaded dimension or bad
		// things will happen!
		if (dimensionData.remove(dimension.id()) != null)
		{
			// Raise the dim deleted event
			getDimwatcher().onDeleted(new ClientDimData(dimension));
			dimension.clear();
		}
		else
		{
			// This should never happen. A simple sanity check.
			throw new IllegalArgumentException("The specified dimension is not listed with PocketManager.");
		}
	}

	private static void registerPockets(DDProperties properties)
	{
		for (NewDimData dimension : dimensionData.values())
		{
			if (dimension.isPocketDimension())
			{
				try
				{
					if (personalPocketsMapping.containsValue(dimension))
					{
						DimensionManager.registerDimension(dimension.id(), properties.PersonalPocketProviderID);
					}
					else
					{
						DimensionManager.registerDimension(dimension.id(), properties.PocketProviderID);
					}
				}
				catch (Exception e)
				{
					System.err.println("Could not register pocket dimension #" + dimension.id()
							+ ". Probably caused by a version update/save data corruption/other mods.");
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
		for (Integer dimID : dimensionIDBlackList)
		{
			try
			{
				DimensionManager.unregisterDimension(dimID);
			}
			catch (Exception e)
			{
				System.err.println("An unexpected error occurred while unregistering blacklisted dim #" + dimID + ":");
				e.printStackTrace();
			}
		}
	}

	/**
	 * loads the dim data from the saved hashMap. Also handles compatibility
	 * with old saves, see OldSaveHandler
	 */
	private static void loadInternal()
	{
		File saveDir = DimensionManager.getCurrentSaveRootDirectory();
		if (saveDir != null)
		{
			// Try to import data from old DD versions
			// TODO - remove this code in a few versions
			File oldSaveData = new File(saveDir + "/DimensionalDoorsData");
			if (oldSaveData.exists())
			{
				try
				{
					System.out.println("Importing old DD save data...");
					OldSaveImporter.importOldSave(oldSaveData);

					oldSaveData.renameTo(new File(oldSaveData.getAbsolutePath() + "_IMPORTED"));

					System.out.println("Import Succesful!");
				}
				catch (Exception e)
				{
					// TODO handle fail cases
					System.out.println("Import failed!");
					e.printStackTrace();
				}
				return;
			}

			// Load save data
			System.out.println("Loading Dimensional Doors save data...");
			if (DDSaveHandler.loadAll())
			{
				System.out.println("Loaded successfully!");
			}
		}
	}

	public static void save(boolean checkModified)
	{
		if (!isLoaded)
		{
			return;
		}
		// Check this last to make sure we set the flag shortly after.
		if (isSaving)
		{
			return;
		}
		isSaving = true;

		try
		{
			DDSaveHandler.saveAll(dimensionData.values(), dimensionIDBlackList, checkModified);
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
		if (!DimensionManager.isDimensionRegistered(id))
		{
			return null;
		}

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
		return registerDimension(world.provider.dimensionId, null, DimensionType.ROOT);
	}

	/**
	 * method to register a new pocket with DD and with forge.
	 * 
	 * @param parent
	 * @param type
	 * @param playername
	 * @return
	 */
	public static NewDimData registerPocket(NewDimData parent, DimensionType type, String playername)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("parent cannot be null. A pocket dimension must always have a parent dimension.");
		}

		DDProperties properties = DDProperties.instance();
		int dimensionID = DimensionManager.getNextFreeDimId();

		// register a personal pocket
		if (type == DimensionType.PERSONAL)
		{
			if (playername == null)
			{
				throw new IllegalArgumentException("A personal pocket must be attached to a playername");
			}
			DimensionManager.registerDimension(dimensionID, properties.PersonalPocketProviderID);
			NewDimData data = registerDimension(dimensionID, (InnerDimData) parent, type);
			personalPocketsMapping.put(playername, data);
			return data;
		}
		else
		{ // register a pocket as personal if its parents are personal, but
			// without a mapping.
			if (parent.type == DimensionType.PERSONAL)
			{
				DimensionManager.registerDimension(dimensionID, properties.PersonalPocketProviderID);
				NewDimData data = registerDimension(dimensionID, (InnerDimData) parent, DimensionType.PERSONAL);
				return data;
			}

			// register a standard pocket
			DimensionManager.registerDimension(dimensionID, properties.PocketProviderID);
			return registerDimension(dimensionID, (InnerDimData) parent, type);
		}

	}

	public static NewDimData registerPocket(NewDimData parent, DimensionType type)
	{
		return registerPocket(parent, type, null);
	}

	/**
	 * Registers a dimension with DD but NOT with forge.
	 * 
	 * @param dimensionID
	 * @param parent
	 * @param type
	 * @return
	 */
	private static NewDimData registerDimension(int dimensionID, InnerDimData parent, DimensionType type)
	{
		if (dimensionData.containsKey(dimensionID))
		{
			if (PocketManager.dimensionIDBlackList.contains(dimensionID))
			{
				throw new IllegalArgumentException("Cannot register a dimension with ID = " + dimensionID + " because it has been blacklisted.");
			}
			throw new IllegalArgumentException("Cannot register a dimension with ID = " + dimensionID + " because it has already been registered.");
		}
		InnerDimData dimension = new InnerDimData(dimensionID, parent, type, linkWatcher);
		dimensionData.put(dimensionID, dimension);
		if (!dimension.isPocketDimension())
		{
			rootDimensions.add(dimension);
		}
		getDimwatcher().onCreated(new ClientDimData(dimension));

		return dimension;
	}

	@SideOnly(Side.CLIENT)
	private static NewDimData registerClientDimension(int dimensionID, int rootID, DimensionType type)

	{
		// No need to raise events heres since this code should only run on the
		// client side. createDimensionData() always handles root dimensions
		// properly, even if they weren't defined before.

		// SenseiKiwi: I'm a little worried about how createDimensionData will
		// raise
		// an event when it creates any root dimensions... Needs checking later.

		InnerDimData root = (InnerDimData) createDimensionData(rootID);
		InnerDimData dimension;

		if (rootID != dimensionID)
		{
			dimension = dimensionData.get(dimensionID);
			if (dimension == null)
			{
				dimension = new InnerDimData(dimensionID, root, type);
				dimensionData.put(dimension.id(), dimension);
			}
		}
		else
		{
			dimension = root;
		}
		if (dimension.isPocketDimension() && !DimensionManager.isDimensionRegistered(dimension.id()))
		{
			// Im registering pocket dims here. I *think* we can assume that if
			// its a pocket and we are
			// registering its dim data, we also need to register it with forge.

			// New packet stuff prevents this from always being true,
			// unfortuantly. I send the dimdata to the client when they
			// teleport.
			// Steven
            int providerID = mod_pocketDim.properties.PocketProviderID;
            if (type == DimensionType.PERSONAL)
                providerID = mod_pocketDim.properties.PersonalPocketProviderID;
			DimensionManager.registerDimension(dimensionID,providerID);
		}
		return dimension;
	}

	public static NewDimData getDimensionData(int dimensionID)
	{
		return PocketManager.dimensionData.get(dimensionID);
	}

	public static NewDimData getDimensionData(World dimension)
	{
		return PocketManager.dimensionData.get(dimension.provider.dimensionId);
	}

	public static NewDimData createDimensionData(World world)
	{
		return createDimensionData(world.provider.dimensionId);
	}

	public static NewDimData createDimensionDataDangerously(int dimensionID)
	{
		// Same as createDimensionData(int), but public. Meant to discourage
		// anyone from
		// using it unless absolutely needed! We'll probably phase this out
		// eventually.
		return createDimensionData(dimensionID);
	}

	protected static NewDimData createDimensionData(int dimensionID)
	{
		// Retrieve the data for a dimension. If we don't have a record for that
		// dimension,
		// assume it's a non-pocket dimension that hasn't been initialized with
		// us before
		// and create a NewDimData instance for it.
		NewDimData dimension = PocketManager.dimensionData.get(dimensionID);

		// if we do not have a record of it, then it must be a root
		if (dimension == null)
		{
			dimension = registerDimension(dimensionID, null, DimensionType.ROOT);
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

    public static void tryUnload() {
        if (isConnected)
            unload();
        isLoading = false;
        isLoaded = false;
    }

	public static void unload()
	{
		System.out.println("Unloading Pocket Dimensions...");
		if (!isLoaded)
		{
			throw new IllegalStateException("Pocket dimensions have already been unloaded!");
		}

		unregisterPockets();
		dimensionData = null;
		personalPocketsMapping = null;
		rootDimensions = null;
		isLoaded = false;
		isConnected = false;
	}

	public static DimLink getLink(int x, int y, int z, World world)
	{
		return getLink(x, y, z, world.provider.dimensionId);
	}

	public static DimLink getLink(Point4D point)
	{
		return getLink(point.getX(), point.getY(), point.getZ(), point.getDimension());
	}

	public static DimLink getLink(int x, int y, int z, int dimensionID)
	{
		NewDimData dimension = dimensionData.get(dimensionID);
		if (dimension != null)
		{
			return dimension.getLink(x, y, z);
		}
		return null;
	}

	public static boolean isBlackListed(int dimensionID)
	{
		return PocketManager.dimensionIDBlackList.contains(dimensionID);
	}

	public static void registerDimWatcher(IUpdateWatcher<ClientDimData> watcher)
	{
		getDimwatcher().registerReceiver(watcher);
	}

	public static boolean unregisterDimWatcher(IUpdateWatcher<ClientDimData> watcher)
	{
		return getDimwatcher().unregisterReceiver(watcher);
	}

	public static void registerLinkWatcher(IUpdateWatcher<ClientLinkData> watcher)
	{
		linkWatcher.registerReceiver(watcher);
	}

	public static boolean unregisterLinkWatcher(IUpdateWatcher<ClientLinkData> watcher)
	{
		return linkWatcher.unregisterReceiver(watcher);
	}

	public static void writePacket(DataOutput output) throws IOException
	{
		// Write a very compact description of our dimensions and links to be
		// sent to a client
		Compactor.write(dimensionData.values(), output);
	}

	public static boolean isRegisteredInternally(int dimensionID)
	{
		return dimensionData.containsKey(dimensionID);
	}

	public static void createAndRegisterBlacklist(List<Integer> blacklist)
	{
		// TODO - create a special blacklist provider
		for (Integer dimID : blacklist)
		{
			PocketManager.dimensionIDBlackList.add(dimID);
			DimensionManager.registerDimension(dimID, DDProperties.instance().PocketProviderID);
		}
	}

	public static void readPacket(DataInput input) throws IOException
	{
		// TODO- figure out why this is getting called so frequently
		if (isLoaded)
		{
			return;
		}
		if (isLoading)
		{
			throw new IllegalStateException("Pocket dimensions are already loading!");
		}
		// Load compacted client-side dimension data
		load();
		Compactor.readDimensions(input, new DimRegistrationCallback());

		isLoaded = true;
		isLoading = false;
		isConnected = true;
	}

	public static UpdateWatcherProxy<ClientDimData> getDimwatcher()
	{
		return dimWatcher;
	}

	public static UpdateWatcherProxy<ClientLinkData> getLinkWatcher()
	{
		return linkWatcher;
	}

	public static NewDimData getPersonalDimensionForPlayer(String name)
	{
		if (personalPocketsMapping.containsKey(name))
		{
			return personalPocketsMapping.get(name);
		}
		return null;
	}

	public static void setPersonalPocketsMapping(HashMap<String, NewDimData> ppMap)
	{
		personalPocketsMapping = ppMap;
	}

	public static HashMap<String, NewDimData> getPersonalPocketMapping()
	{
		// TODO Auto-generated method stub
		return personalPocketsMapping;
	}
}
