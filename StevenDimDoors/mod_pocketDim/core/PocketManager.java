package StevenDimDoors.mod_pocketDim.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.ObjectSaveInputStream;
import StevenDimDoors.mod_pocketDim.helpers.DeleteFolder;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * This class regulates all the operations involving the storage and manipulation of dimensions. It handles saving dim data, teleporting the player, and 
 * creating/registering new dimensions as well as loading old dimensions on startup
 */
public class PocketManager
{
	private static class InnerDimData extends NewDimData
	{
		//This inner class allows us to instantiate NewDimData indirectly without exposing
		//a public constructor from NewDimData. It's meant to stop us from constructing instances
		//of NewDimData without using PocketManager's functions. In turn, that enforces that any
		//link destinations must be real dimensions controlled by PocketManager.
		
		private static final long serialVersionUID = -3497038894870586232L;

		public InnerDimData(int id, NewDimData parent, boolean isPocket, boolean isDungeon)
		{
			super(id, parent, isPocket, isDungeon);
		}
	}
	
	private static int OVERWORLD_DIMENSION_ID = 0;
	
	private static boolean isInitialized = false;
	private static boolean isSaving = false;
	private static Random random = new Random();

	//HashMap containing all the dims registered with DimDoors, sorted by dim ID. loaded on startup
	private static HashMap<Integer, NewDimData> dimensionData = new HashMap<Integer, NewDimData>();

	//HashMap for temporary storage of Link Signature damage hash values. See itemLinkSignature for more details
	private static HashMap<Integer, IDimLink> keyLinkMapping = new HashMap<Integer, IDimLink>();

	public static boolean isInitialized()
	{
		return isInitialized;
	}

	/**
	 * simple method called on startup to register all dims saved in the dim list. Only tries to register pocket dims, though. Also calls load()
	 * @return
	 */
	public static void initPockets()
	{
		if (isInitialized)
		{
			throw new IllegalStateException("Pocket dimensions have already been initialized!");
		}
		
		DDProperties properties = DDProperties.instance();
		
		isInitialized = true;
		load();
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

	public boolean resetPocket(NewDimData dimension)
	{
		if (!dimension.isPocketDimension() || DimensionManager.getWorld(dimension.id()) != null)
		{
			return false;
		}
		
		File save = new File(DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/pocketDimID" + dimension.id());
		DeleteFolder.deleteFolder(save);
		dimension.setFilled(false);
		//FIXME: Reset door information?
		return true;
	}

	public static boolean pruneDimension(NewDimData dimension, boolean deleteFolder)
	{
		//FIXME: Shouldn't the links in and out of this dimension be altered somehow? Otherwise we have links pointing
		//into a deleted dimension!
		
		//Checks to see if the pocket is loaded or isn't actually a pocket.
		if (dimension.isPocketDimension() && DimensionManager.getWorld(dimension.id()) == null)
		{
			dimensionData.remove(dimension.id());
			//FIXME: I added the following line. Seems like a good idea. Is it?
			DimensionManager.unregisterDimension(dimension.id());
			if (deleteFolder)
			{
				File save = new File(DimensionManager.getCurrentSaveRootDirectory() + "/DimensionalDoors/pocketDimID" + dimension.id());
				DeleteFolder.deleteFolder(save);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	private static void unregisterDimensions()
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
	 * Used to associate a damage value on a Rift Signature with a link pair. See LinkSignature for details. 
	 * @return
	 */
	public static int createUniqueLinkKey()
	{
		int linkKey;
		do
		{
			linkKey = random.nextInt(30000);
		}
		while (keyLinkMapping.containsKey(linkKey));
		return linkKey;
	}

	/**
	 * Function that saves all dim data in a hashMap. Calling too often can cause Concurrent modification exceptions, so be careful.
	 * @return
	 */
	public static void save()
	{
		//TODO change from saving serialized objects to just saving data for compatabilies sake. 
		//TODO If saving is multithreaded as the concurrent modification exception implies, you should be synchronizing access. ~SenseiKiwi

		if (isSaving)
		{
			return;
		}
		World world = DimensionManager.getWorld(OVERWORLD_DIMENSION_ID);
		if (world == null || world.isRemote)
		{
			return;
		}	
		if (DimensionManager.getCurrentSaveRootDirectory() != null)
		{
			isSaving = true;
			HashMap<String, Object> comboSave = new HashMap<String, Object>();
			comboSave.put("dimensionData", dimensionData);
			comboSave.put("keyLinkMapping", keyLinkMapping);

			FileOutputStream saveFile = null;
			try
			{
				String saveFileName=DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsDataTEMP";
				saveFile = new FileOutputStream(saveFileName);

				ObjectOutputStream save = new ObjectOutputStream(saveFile);
				save.writeObject(comboSave);
				save.close();
				saveFile.close();

				if (new File(DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsDataOLD").exists())
				{
					new File(DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsDataOLD").delete(); 
				}
				new File(DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsData").renameTo(new File(DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsDataOLD"));

				new File(saveFileName).renameTo( new File(DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsData"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.err.println("Could not save data-- SEVERE");
			}
			isSaving = false;
		}
	}

	/**
	 * loads the dim data from the saved hashMap. Also handles compatibility with old saves, see OldSaveHandler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void load()
	{
		//FIXME: There are a lot of things to fix here... First, we shouldn't be created so many File instances
		//when we could just hold references and reuse them. Second, duplicate code is BAD. Loading stuff should
		//be a function so that you can apply it to the save file first, then the "backup", instead of duplicating
		//so much code. >_<
		
		boolean firstRun = false;
		System.out.println("Loading DimDoors data");
		FileInputStream saveFile = null;

		if (!DimensionManager.getWorld(OVERWORLD_DIMENSION_ID).isRemote && DimensionManager.getCurrentSaveRootDirectory()!=null)
		{
			try
			{
				File dataStore = new File( DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsData");
				if (!dataStore.exists())
				{
					if (!new File( DimensionManager.getCurrentSaveRootDirectory()+"/DimensionalDoorsDataOLD").exists())
					{
						firstRun=true;
					}
				}
				saveFile = new FileInputStream(dataStore);
				ObjectSaveInputStream save = new ObjectSaveInputStream(saveFile);
				HashMap<String, Object> comboSave = (HashMap<String, Object>) save.readObject();

				try
				{
					keyLinkMapping = (HashMap<Integer, IDimLink>) comboSave.get("keyLinkMapping");
				}
				catch (Exception e)
				{
					System.out.println("Could not load Link Signature list. Link Sig items will lose their stored locations.");
				}

				try
				{
					dimensionData = (HashMap<Integer, NewDimData>) comboSave.get("dimensionData");
				}
				catch(Exception e)
				{
					System.out.println("Could not load pocket dimension list. Saves are probably lost, but repairable. Move the files from individual pocket dim files to active ones. See MC thread for details.");
				}

				save.close();
				saveFile.close();
			}
			catch (Exception e)
			{
				try
				{
					if (!firstRun)
					{
						System.out.println("Save data damaged, trying backup...");
					}
					World world=FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0];
					File dataStore =new File( world.getSaveHandler().getMapFileFromName("idcounts").getParentFile().getParent()+"/DimensionalDoorsDataOLD");


					saveFile = new FileInputStream(dataStore);
					ObjectSaveInputStream save = new ObjectSaveInputStream(saveFile);
					HashMap<String, Object> comboSave = (HashMap<String, Object>) save.readObject();

					try
					{
						keyLinkMapping = (HashMap<Integer, IDimLink>) comboSave.get("keyLinkMapping");
					}
					catch (Exception e2)
					{
						System.out.println("Could not load Link Signature list. Link Sig items will loose restored locations.");
					}

					try
					{
						dimensionData = (HashMap<Integer, NewDimData>) comboSave.get("dimensionData");
					}
					catch (Exception e2)
					{
						System.out.println("Could not load pocket dim list. Saves probably lost, but repairable. Move the files from indivual pocket dim files to active ones. See MC thread for details.");
					}

					save.close();
					saveFile.close();
				}
				catch (Exception e2)			
				{
					if (!firstRun)
					{
						System.err.println("Could not read data-- SEVERE");
						e2.printStackTrace();
					}
				}
			}
		}	
	}

	public static boolean removeRift(World world, int x, int y, int z, int range, EntityPlayer player, ItemStack item)
	{
		//Function called by rift tile entities and the rift remover to find and spread between rifts.
		//Does not actually unregister the rift data, see deleteRift for that.
		
		NewDimData dimension = getDimensionData(world);
		IDimLink nearest = dimension.findNearestRift(world, range, x, y, z);

		if (nearest != null)
		{
			Point4D location = nearest.source();
			TileEntity tileEntity = world.getBlockTileEntity(location.getX(), location.getY(), location.getZ());
			if (tileEntity != null)
			{
				TileEntityRift riftEntity = (TileEntityRift) tileEntity;
				riftEntity.shouldClose = true;
				item.damageItem(1, player);
				return true;
			}
		}
		return false;
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
		return registerDimension(dimensionID, parent, true, isDungeon);
	}

	private static NewDimData registerDimension(int dimensionID, NewDimData parent, boolean isPocket, boolean isDungeon)
	{
		if (dimensionData.containsKey(dimensionID))
		{
			throw new IllegalArgumentException("Cannot register a dimension with ID = " + dimensionID + " because it has already been registered.");
		}

		NewDimData dimension = new InnerDimData(dimensionID, parent, isPocket, isDungeon);
		dimensionData.put(dimensionID, dimension);
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

	public static void unload()
	{
		save();
		unregisterDimensions();
		dimensionData.clear();
		keyLinkMapping.clear();
	}

	public static Iterable<NewDimData> getDimensions()
	{
		return dimensionData.values();
	}
	
	public static IDimLink getLink(int x, int y, int z, World world)
	{
		return getLink(x, y, z, world.provider.dimensionId);
	}

	public static IDimLink getLink(int x, int y, int z, int dimensionID)
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
}
