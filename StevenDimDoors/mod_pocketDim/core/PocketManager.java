package StevenDimDoors.mod_pocketDim.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.DDTeleporter;
import StevenDimDoors.mod_pocketDim.ObjectSaveInputStream;
import StevenDimDoors.mod_pocketDim.PacketHandler;
import StevenDimDoors.mod_pocketDim.TileEntityRift;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.DeleteFolder;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

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

	public ILinkData createLink(ILinkData link)
	{
		DDProperties properties = DDProperties.instance();

		if(!PocketManager.dimList.containsKey(link.locDimID))
		{
			NewDimData locationDimData= new NewDimData(link.locDimID, false, 0, link.locDimID,link.locXCoord,link.locYCoord,link.locZCoord);
			PocketManager.dimList.put(link.locDimID, locationDimData);
			link.isLocPocket=false;
		}
		if(!dimList.containsKey(link.destDimID))
		{
			PocketManager.dimList.put(link.destDimID, new NewDimData(link.destDimID, false, 0, link.locDimID,link.locXCoord,link.locYCoord,link.locZCoord));
		}
		NewDimData locationDimData=	PocketManager.instance.getDimData(link.locDimID);
		link.isLocPocket=locationDimData.isPocket;
		locationDimData.addLinkToDim(link);

		World world = PocketManager.getWorld(link.locDimID);
		if (world != null)
		{
			if (!mod_pocketDim.blockRift.isBlockImmune(world, link.locXCoord, link.locYCoord, link.locZCoord))
			{
				world.setBlock(link.locXCoord, link.locYCoord, link.locZCoord, properties.RiftBlockID);	
			}
		}
		//Notifies other players that a link has been created.
		//TODO: Couldn't we use the serverside/clientside annotations to achieve this instead? Seems safer. ~SenseiKiwi
		if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER)
		{		
			PacketHandler.onLinkCreatedPacket(link);
		}	
		return link;
	}

	/**
	 * properly deletes a link at the given coordinates. used by the rift remover. Also notifies clients of change. 
	 * @param locationDimID
	 * @param locationXCoord
	 * @param locationYCoord
	 * @param locationZCoord
	 */
	public void removeLink( int locationDimID, int locationXCoord, int locationYCoord, int locationZCoord)
	{
		if(!PocketManager.dimList.containsKey(locationDimID))
		{
			NewDimData locationDimData= new NewDimData(locationDimID, false, 0, locationDimID,locationXCoord,locationYCoord,locationZCoord);
			PocketManager.dimList.put(locationDimID, locationDimData);
		}
		ILinkData link = this.getLinkDataFromCoords(locationXCoord, locationYCoord, locationZCoord, locationDimID);
		PocketManager.instance.getDimData(locationDimID).removeLinkAtCoords(link);
		//updates clients that a rift has been removed
		if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER)
		{
			PacketHandler.onLinkRemovedPacket(link);
			this.save();
		}	
	}
	
	/**
	 * generates a door based on what door was used to teleport. Only functions once per linking. 
	 * @param world- door 
	 * @param incLink
	 */
	public void generateDoor(World world, ILinkData incLink)
	{
		int locX = incLink.locXCoord;
		int locY = incLink.locYCoord;
		int locZ = incLink.locZCoord;

		int destX = incLink.destXCoord;
		int destY = incLink.destYCoord;
		int destZ = incLink.destZCoord;

		DDProperties properties = DDProperties.instance();

		if(!incLink.hasGennedDoor)
		{
			int destinationID = incLink.destDimID;

			int id =world.getBlockId(locX, locY, locZ);
			if(id==properties.WarpDoorID||id==properties.DimensionalDoorID||id==properties.TransientDoorID)
			{
				int doorTypeToPlace=id;
				if(DimensionManager.getWorld(destinationID)==null)
				{
					DimensionManager.initDimension(destinationID);
				}
				ILinkData destLink =  this.getLinkDataFromCoords(destX, destY, destZ, destinationID);
				int destOrientation = 0;
				if(destLink!=null)
				{
					destOrientation = destLink.linkOrientation;
					destLink.hasGennedDoor=true;
				}
				int blockToReplace= DimensionManager.getWorld(destinationID).getBlockId(destX, destY, destZ);
				if(blockToReplace!=properties.DimensionalDoorID&&blockToReplace!=properties.WarpDoorID&&blockToReplace != properties.TransientDoorID)
				{
					DimensionManager.getWorld(destinationID).setBlock(destX, destY-1, destZ, doorTypeToPlace,destOrientation,2);
					DimensionManager.getWorld(destinationID).setBlock(destX, destY, destZ, doorTypeToPlace,8,2);
				}
				incLink.hasGennedDoor=true;
			}
		}
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
	 * Method used to create and register a new pocket dimension. Creates a reverse link if necessary.
	 * Populates the destination as well.
	 */
	private NewDimData createDestinationPocket(IDimLink link)
	{
		//First check the destination type
		if (link.linkType() != IDimLink.TYPE_DUNGEON && link.linkType() != IDimLink.TYPE_POCKET)
		{
			throw new IllegalArgumentException("The link must lead to a dimensional dungeon or a pocket dimension.");
		}
		
		DDProperties properties = DDProperties.instance();

		//FIXME: This code had a check for whether dimension 0 was null. Why? Removed it for the time being. ~SenseiKiwi
		
		
		if (PocketManager.getWorld(link.locDimID) == null)
		{
			PocketManager.initDimension(link.locDimID);
		}
		
		int dimensionID;
		int depth = this.getDimDepth(link.locDimID);
		dimensionID = getNextFreeDimId();
		registerDimension(dimensionID, properties.PocketProviderID);
		NewDimData locationDimData;
		NewDimData destDimData;



		if(PocketManager.dimList.containsKey(link.locDimID)&&!DimensionManager.getWorld(link.locDimID).isRemote) //checks to see if dim is already registered. If not, it creates a DimData entry for it later
		{
			//randomizes exit if deep enough
			locationDimData= dimList.get(DimensionManager.getWorld(link.locDimID).provider.dimensionId);

			if(depth>5)
			{
				if(depth>=12)
				{
					depth=11;
				}
				if(rand.nextInt(13-depth)==0)
				{
					ILinkData link1=getRandomLinkData(false);		
				}
			}
			if(locationDimData.isPocket) //determines the qualites of the pocket dim being created, based on parent dim. 
			{
				if(isGoingDown)
				{
					destDimData= new NewDimData(dimensionID, true, locationDimData.depth+1, locationDimData.exitDimLink);
				}
				else
				{
					destDimData= new NewDimData(dimensionID, true, locationDimData.depth-1, locationDimData.exitDimLink);
				}
			}
			else
			{
				destDimData= new NewDimData(dimensionID, true, 1, link.locDimID,link.locXCoord,link.locYCoord,link.locZCoord);
			}

		}
		else
		{
			locationDimData= new NewDimData(link.locDimID, false, 0, link.locDimID,link.locXCoord,link.locYCoord,link.locZCoord);
			destDimData= new NewDimData(dimensionID, true, 1, link.locDimID,link.locXCoord,link.locYCoord,link.locZCoord);
		}
		destDimData.isDimRandomRift=isRandomRift;
		PocketManager.dimList.put(DimensionManager.getWorld(link.locDimID).provider.dimensionId, locationDimData);
		PocketManager.dimList.put(dimensionID, destDimData);

		if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER)//sends packet to clients notifying them that a new dim has been created. 
		{
			PacketHandler.onDimCreatedPacket(destDimData);
		}
		link = this.createLink(DimensionManager.getWorld(link.locDimID).provider.dimensionId,dimensionID,link.locXCoord,link.locYCoord,link.locZCoord, link.destXCoord,constrainPocketY(link.destYCoord),link.destZCoord,link.linkOrientation); //creates and registers the two rifts that link the parent and pocket dim. 
		this.createLink(dimensionID,DimensionManager.getWorld(link.locDimID).provider.dimensionId, link.destXCoord,constrainPocketY(link.destYCoord),link.destZCoord, link.locXCoord,link.locYCoord,link.locZCoord, BlockRotator.transformMetadata(link.linkOrientation, 2, Block.doorWood.blockID));
		return link;
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
			HashMap comboSave = new HashMap();
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				HashMap comboSave = ((HashMap) save.readObject());

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
					HashMap comboSave =((HashMap)save.readObject());

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
