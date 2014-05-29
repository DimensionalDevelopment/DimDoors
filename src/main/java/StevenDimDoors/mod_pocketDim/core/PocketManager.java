package StevenDimDoors.mod_pocketDim.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
import StevenDimDoors.mod_pocketDim.watcher.IUpdateSource;
import StevenDimDoors.mod_pocketDim.watcher.IUpdateWatcher;
import StevenDimDoors.mod_pocketDim.watcher.UpdateWatcherProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class regulates all the operations involving the storage and manipulation of dimensions.
 * It handles saving dim data, teleporting the player, and creating/registering new dimensions as
 * well as loading old dimensions on startup
 */
public class PocketManager
{
	private static class InnerDimData extends NewDimData
	{
		// This class allows us to instantiate NewDimData indirectly without exposing
		// a public constructor from NewDimData. It's meant to stop us from constructing
		// instances of NewDimData going through PocketManager. In turn, that enforces
		// that any link destinations must be real dimensions controlled by PocketManager.

		public InnerDimData(int id, InnerDimData parent, boolean isPocket, boolean isDungeon,
			IUpdateWatcher<ClientLinkData> linkWatcher)
		{
			super(id, parent, isPocket, isDungeon, linkWatcher);
		}
		
		public InnerDimData(int id, InnerDimData root)
		{
			// This constructor is meant for client-side code only
			super(id, root);
		}

	}
	
	  private static class ClientLinkWatcher implements IUpdateWatcher<ClientLinkData>
      {
		  @Override
		  public void onCreated(ClientLinkData link)
		  {
			  Point4D source = link.point;
			  NewDimData dimension = getDimensionData(source.getDimension());
			  dimension.createLink(source, LinkTypes.CLIENT_SIDE, 0, link.lock);
		  }

		  @Override
		  public void onDeleted(ClientLinkData link)
		  {
			  Point4D source = link.point;
			  NewDimData dimension = getDimensionData(source.getDimension());
			  dimension.deleteLink(source.getX(), source.getY(), source.getZ());
		  }

		  @Override
		  public void update(ClientLinkData link)
		  {
			  Point4D source = link.point;
			  NewDimData dimension = getDimensionData(source.getDimension());	
			  DimLink dLink = dimension.getLink(source);
			  dLink.lock=link.lock;
			  
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

		@Override
		public void update(ClientDimData message)
		{
			// TODO Auto-generated method stub
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
	/**
	 * Set as true if we are a client that has connected to a dedicated server
	 */
	public static volatile boolean isConnected = false;
	private static final UpdateWatcherProxy<ClientLinkData> linkWatcher = new UpdateWatcherProxy<ClientLinkData>();
	private static final UpdateWatcherProxy<ClientDimData> dimWatcher = new UpdateWatcherProxy<ClientDimData>();
	private static ArrayList<NewDimData> rootDimensions = null;

	//HashMap that maps all the dimension IDs registered with DimDoors to their DD data.
	private static HashMap<Integer, InnerDimData> dimensionData = null;
	//ArrayList that stores the dimension IDs of any dimension that has been deleted.
	private static ArrayList<Integer> dimensionIDBlackList = null;

	//Stores all the personal pocket mappings
	private static HashMap<String, NewDimData> personalPocketsMapping = null; 

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
		dimensionIDBlackList = new ArrayList<Integer>();
		personalPocketsMapping = new HashMap<String, NewDimData>();
		
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			//Shouldnt try to load everything if we are a client
			//This was preventing onPacket from loading properly
			isLoading=false;
			isLoaded=true;
			return;
		}
		//Register Limbo
		DDProperties properties = DDProperties.instance();
		registerDimension(properties.LimboDimensionID, null, false, false);
		
		loadInternal();
		
		//Register pocket dimensions
		registerPockets(properties);
		
		isLoaded = true;
		isLoading = false;
	}
	
	public static boolean registerPackedDimData(PackedDimData packedData)
	{
		
		InnerDimData dimData;
		//register roots
		if(packedData.ID==packedData.ParentID)
		{
			dimData =  new InnerDimData(packedData.ID, null, false, false, linkWatcher);
			dimData.root=dimData;
			dimData.parent=dimData;
			dimData.depth=packedData.Depth;
			dimData.isFilled=packedData.IsFilled;
			dimData.origin = new Point4D(packedData.Origin.getX(),packedData.Origin.getY(),packedData.Origin.getZ(),packedData.ID);

			PocketManager.rootDimensions.add(dimData);
		}
		else //register children
		{
			InnerDimData test = PocketManager.dimensionData.get(packedData.ParentID);
			dimData =  new InnerDimData(packedData.ID, test,true, packedData.IsDungeon, linkWatcher);
			dimData.isFilled=packedData.IsFilled;
			dimData.origin = new Point4D(packedData.Origin.getX(),packedData.Origin.getY(),packedData.Origin.getZ(),packedData.ID);
			dimData.root=PocketManager.getDimensionData(packedData.RootID);
			
			if(packedData.DungeonData!=null)
			{
				dimData.dungeon=DDSaveHandler.unpackDungeonData(packedData.DungeonData);
			}

		}
		PocketManager.dimensionData.put(dimData.id, dimData);
		getDimwatcher().onCreated(new ClientDimData(dimData));

		return true;
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
				deleteDimensionFiles(target);
			}
			dimensionIDBlackList.add(dimension.id);
			deleteDimensionData(dimension.id);
			return true;
		}
		return false;
	}
	
	private static boolean deleteDimensionFiles(NewDimData target)
	{
		InnerDimData dimension = (InnerDimData) target;
		if (dimension.isPocketDimension() && DimensionManager.getWorld(dimension.id()) == null)
		{
			String saveRootPath = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath();
			File saveDirectory = new File(saveRootPath + "/DimensionalDoors/pocketDimID" + dimension.id());
			DeleteFolder.deleteFolder(saveDirectory);
			File dataFile = new File(saveRootPath + "/DimensionalDoors/data/dim_" + dimension.id() + ".txt");
			dataFile.delete();
			return true;
		}
		return false;
	}
	
	private static boolean deleteDimensionData(int dimensionID)
	{
		if (dimensionData.containsKey(dimensionID) && DimensionManager.getWorld(dimensionID) == null)
		{
			NewDimData target = PocketManager.getDimensionData(dimensionID);
			InnerDimData dimension = (InnerDimData) target;

			dimensionData.remove(dimensionID);
			// Raise the dim deleted event
			getDimwatcher().onDeleted(new ClientDimData(dimension));
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
					if(personalPocketsMapping.containsValue(dimension))
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
		for(Integer dimID : dimensionIDBlackList)
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
	 * loads the dim data from the saved hashMap. Also handles compatibility with old saves, see OldSaveHandler
	 */
	private static void loadInternal()
	{	
		//System.out.println(!FMLCommonHandler.instance().getSide().isClient());

		File saveDir = DimensionManager.getCurrentSaveRootDirectory();
		if (saveDir != null)
		{
			//Try to import data from old DD versions
			//TODO - remove this code in a few versions
			File oldSaveData = new File(saveDir+"/DimensionalDoorsData");
			if(oldSaveData.exists())
			{
				try
				{
					System.out.println("Importing old DD save data...");
					OldSaveImporter.importOldSave(oldSaveData);
					
					oldSaveData.renameTo(new File(oldSaveData.getAbsolutePath()+"_IMPORTED"));

					System.out.println("Import Succesful!");
				}
				catch (Exception e)
				{
					//TODO handle fail cases
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
		//Check this last to make sure we set the flag shortly after.
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

	public static NewDimData registerPersonalPocket(NewDimData parent, String playerName)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("parent cannot be null. A pocket dimension must always have a parent dimension.");
		}
		
		DDProperties properties = DDProperties.instance();
		int dimensionID = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimensionID, properties.PersonalPocketProviderID);
		NewDimData data = registerDimension(dimensionID, (InnerDimData) parent, true, false);
		personalPocketsMapping.put(playerName, data);
		return data;
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
	/**
	 * Registers a dimension with DD but NOT with forge.
	 * @param dimensionID
	 * @param parent
	 * @param isPocket
	 * @param isDungeon
	 * @return
	 */
	private static NewDimData registerDimension(int dimensionID, InnerDimData parent, boolean isPocket, boolean isDungeon)
	{	
		if (dimensionData.containsKey(dimensionID))
		{
			if(PocketManager.dimensionIDBlackList.contains(dimensionID))
			{
				throw new IllegalArgumentException("Cannot register a dimension with ID = " + dimensionID + " because it has been blacklisted.");
			}
			throw new IllegalArgumentException("Cannot register a dimension with ID = " + dimensionID + " because it has already been registered.");
		}
		InnerDimData dimension = new InnerDimData(dimensionID, parent, isPocket, isDungeon, linkWatcher);
		dimensionData.put(dimensionID, dimension);
		if (!dimension.isPocketDimension())
		{
			rootDimensions.add(dimension);
		}
		getDimwatcher().onCreated(new ClientDimData(dimension));
		
		return dimension;
	}
	
	@SideOnly(Side.CLIENT)
	private static NewDimData registerClientDimension(int dimensionID, int rootID)
	{
		System.out.println("Registered dim "+dimensionID+" on the client.");
		// No need to raise events heres since this code should only run on the client side
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
		if(dimension.isPocketDimension()&&!DimensionManager.isDimensionRegistered(dimension.id()))
		{
			//Im registering pocket dims here. I *think* we can assume that if its a pocket and we are 
			//registering its dim data, we also need to register it with forge. 
			
			//New packet stuff prevents this from always being true, unfortuantly. I send the dimdata to the client when they teleport.
			//Steven
			DimensionManager.registerDimension(dimensionID, mod_pocketDim.properties.PocketProviderID);
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
		
		//FIXME: What's the point of this condition? Most calls to this function will crash anyway! ~SenseiKiwi
		if(PocketManager.dimensionData == null)
		{
			System.out.println("Something odd happend during shutdown");
			return null;
		}
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
		else
		{
			return null;
		}
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

	public static void getWatchers(IUpdateSource updateSource)
	{
		updateSource.registerWatchers(new ClientDimWatcher(), new ClientLinkWatcher());
	}
	
	public static void writePacket(DataOutputStream output) throws IOException
	{
		// Write a very compact description of our dimensions and links to be sent to a client
		Compactor.write(dimensionData.values(), output);
	}
	
	public static boolean isRegisteredInternally(int dimensionID)
	{
		return dimensionData.containsKey(dimensionID);
	}
	
	public static void createAndRegisterBlacklist(List<Integer> blacklist)
	{
		//TODO - create a special blacklist provider
		for(Integer dimID : blacklist)
		{
			PocketManager.dimensionIDBlackList.add(dimID);
			DimensionManager.registerDimension(dimID, DDProperties.instance().PocketProviderID);
		}
	}	
	public static void readPacket(DataInputStream input) throws IOException
	{
		//TODO- figure out why this is getting called so frequently
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
		
		// Register pocket dimensions
		DDProperties properties = DDProperties.instance();
				
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
		if(personalPocketsMapping.containsKey(name))
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
