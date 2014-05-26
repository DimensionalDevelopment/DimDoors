package StevenDimDoors.mod_pocketDim;

import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWall;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWallPerm;
import StevenDimDoors.mod_pocketDim.blocks.BlockDoorGold;
import StevenDimDoors.mod_pocketDim.blocks.BlockDoorQuartz;
import StevenDimDoors.mod_pocketDim.blocks.BlockGoldDimDoor;
import StevenDimDoors.mod_pocketDim.blocks.BlockLimbo;
import StevenDimDoors.mod_pocketDim.blocks.BlockRift;
import StevenDimDoors.mod_pocketDim.blocks.DimensionalDoor;
import StevenDimDoors.mod_pocketDim.blocks.PersonalDimDoor;
import StevenDimDoors.mod_pocketDim.blocks.TransTrapdoor;
import StevenDimDoors.mod_pocketDim.blocks.TransientDoor;
import StevenDimDoors.mod_pocketDim.blocks.UnstableDoor;
import StevenDimDoors.mod_pocketDim.blocks.WarpDoor;
import StevenDimDoors.mod_pocketDim.commands.CommandCreateDungeonRift;
import StevenDimDoors.mod_pocketDim.commands.CommandCreatePocket;
import StevenDimDoors.mod_pocketDim.commands.CommandCreateRandomRift;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteAllLinks;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteRifts;
import StevenDimDoors.mod_pocketDim.commands.CommandExportDungeon;
import StevenDimDoors.mod_pocketDim.commands.CommandListDungeons;
import StevenDimDoors.mod_pocketDim.commands.CommandResetDungeons;
import StevenDimDoors.mod_pocketDim.commands.CommandTeleportPlayer;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.config.DDWorldProperties;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.helpers.ChunkLoaderHelper;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.items.ItemBlockDimWall;
import StevenDimDoors.mod_pocketDim.items.ItemDDKey;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;
import StevenDimDoors.mod_pocketDim.items.ItemGoldDimDoor;
import StevenDimDoors.mod_pocketDim.items.ItemGoldDoor;
import StevenDimDoors.mod_pocketDim.items.ItemPersonalDoor;
import StevenDimDoors.mod_pocketDim.items.ItemQuartzDoor;
import StevenDimDoors.mod_pocketDim.items.ItemRiftBlade;
import StevenDimDoors.mod_pocketDim.items.ItemRiftSignature;
import StevenDimDoors.mod_pocketDim.items.ItemStabilizedRiftSignature;
import StevenDimDoors.mod_pocketDim.items.ItemStableFabric;
import StevenDimDoors.mod_pocketDim.items.ItemUnstableDoor;
import StevenDimDoors.mod_pocketDim.items.ItemWarpDoor;
import StevenDimDoors.mod_pocketDim.items.ItemWorldThread;
import StevenDimDoors.mod_pocketDim.items.itemRiftRemover;
import StevenDimDoors.mod_pocketDim.ticking.CommonTickHandler;
import StevenDimDoors.mod_pocketDim.ticking.CustomLimboPopulator;
import StevenDimDoors.mod_pocketDim.ticking.FastRiftRegenerator;
import StevenDimDoors.mod_pocketDim.ticking.LimboDecay;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.ticking.RiftRegenerator;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;
import StevenDimDoors.mod_pocketDim.world.BiomeGenLimbo;
import StevenDimDoors.mod_pocketDim.world.BiomeGenPocket;
import StevenDimDoors.mod_pocketDim.world.DDBiomeGenBase;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PersonalPocketProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;
import StevenDimDoors.mod_pocketDim.world.gateways.GatewayGenerator;
import StevenDimDoors.mod_pocketDimClient.ClientPacketHandler;
import StevenDimDoors.mod_pocketDimClient.ClientTickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = mod_pocketDim.modid, name = "Dimensional Doors", version = mod_pocketDim.version)

@NetworkMod(clientSideRequired = true, serverSideRequired = false, connectionHandler=ConnectionHandler.class,
clientPacketHandlerSpec =
@SidedPacketHandler(channels = {PacketConstants.CHANNEL_NAME}, packetHandler = ClientPacketHandler.class),
serverPacketHandlerSpec =
@SidedPacketHandler(channels = {PacketConstants.CHANNEL_NAME}, packetHandler = ServerPacketHandler.class))
public class mod_pocketDim
{
	public static final String version = "1.6.4-R2.2.3";
	public static final String modid = "dimdoors";
	
	//TODO need a place to stick all these constants
	public static final int NETHER_DIMENSION_ID = -1;

	//need to clean up 
	@SidedProxy(clientSide = "StevenDimDoors.mod_pocketDimClient.ClientProxy", serverSide = "StevenDimDoors.mod_pocketDim.CommonProxy")
	public static CommonProxy proxy;

	@Instance("PocketDimensions")
	public static mod_pocketDim instance = new mod_pocketDim();

	public static Block quartzDoor;
	public static Block personalDimDoor;
	public static Block transientDoor;
	public static Block warpDoor;
	public static Block goldenDoor;
	public static Block goldenDimensionalDoor;
	public static Block unstableDoor;
	public static Block blockLimbo;
	public static DimensionalDoor dimensionalDoor;    
	public static Block blockDimWall;   
	public static TransTrapdoor transTrapdoor;
	public static Block blockDimWallPerm;
	public static BlockRift blockRift;

	public static Item itemGoldenDimensionalDoor;
	public static Item itemGoldenDoor;
	public static Item itemWorldThread;

	public static Item itemRiftBlade;
	public static ItemDimensionalDoor itemDimensionalDoor;
	public static Item itemWarpDoor;
	public static Item itemRiftRemover;
	public static Item itemRiftSignature;
	public static Item itemStableFabric;
	public static Item itemUnstableDoor;
	public static Item itemStabilizedLinkSignature;
	public static Item itemDDKey;
	public static Item itemQuartzDoor;
	public static Item itemPersonalDoor;

	public static BiomeGenBase limboBiome;
	public static BiomeGenBase pocketBiome;

	public static boolean isPlayerWearingGoogles = false;

	public static DDProperties properties;
	public static DDWorldProperties worldProperties;
	public static CustomLimboPopulator spawner; //Added this field temporarily. Will be refactored out later.
	public static FastRiftRegenerator fastRiftRegenerator;
	public static GatewayGenerator gatewayGenerator;
	public static DeathTracker deathTracker;
	
	//TODO this is a temporary workaround for saving data
	private String currrentSaveRootDirectory;
	
	public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimDoorsCreativeTab") 
	{
		@Override
		public ItemStack getIconItemStack() 
		{
			return new ItemStack(mod_pocketDim.itemDimensionalDoor, 1, 0);
		}

		@Override
		public String getTranslatedTabLabel()
		{
			return "Dimensional Doors";
		}
	};

	@EventHandler
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
		instance = this;
		//This should be the FIRST thing that gets done.
		String path = event.getSuggestedConfigurationFile().getAbsolutePath().replace(modid, "DimDoors");

		properties = DDProperties.initialize(new File(path));

		//Now do other stuff
		EventHookContainer hooks = new EventHookContainer(properties);
		MinecraftForge.EVENT_BUS.register(hooks);
		MinecraftForge.TERRAIN_GEN_BUS.register(hooks);
	}

	@EventHandler
	public void onInitialization(FMLInitializationEvent event)
	{
		CommonTickHandler commonTickHandler = new CommonTickHandler();
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(commonTickHandler, Side.SERVER);

		//MonolithSpawner should be initialized before any provider instances are created
		//Register the other regular tick receivers as well
		spawner = new CustomLimboPopulator(commonTickHandler, properties);
		new RiftRegenerator(commonTickHandler); //No need to store the reference
		LimboDecay decay = new LimboDecay(commonTickHandler, properties);
		fastRiftRegenerator = new FastRiftRegenerator(commonTickHandler);

		transientDoor = new TransientDoor(properties.TransientDoorID, Material.iron, properties).setHardness(1.0F) .setUnlocalizedName("transientDoor");
		goldenDimensionalDoor = new BlockGoldDimDoor(properties.GoldenDimensionalDoorID, Material.iron, properties).setHardness(1.0F) .setUnlocalizedName("dimDoorGold");

		quartzDoor = new BlockDoorQuartz(properties.QuartzDoorID, Material.rock).setHardness(0.1F).setUnlocalizedName("doorQuartz");
		personalDimDoor = new PersonalDimDoor(properties.PersonalDimDoorID, Material.rock,properties).setHardness(0.1F).setUnlocalizedName("dimDoorPersonal");

		goldenDoor = new BlockDoorGold(properties.GoldenDoorID, Material.iron).setHardness(0.1F).setUnlocalizedName("doorGold");
		blockDimWall = new BlockDimWall(properties.FabricBlockID, 0, Material.iron).setLightValue(1.0F).setHardness(0.1F).setUnlocalizedName("blockDimWall");
		blockDimWallPerm = (new BlockDimWallPerm(properties.PermaFabricBlockID, 0, Material.iron)).setLightValue(1.0F).setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("blockDimWallPerm");
		warpDoor = new WarpDoor(properties.WarpDoorID, Material.wood, properties).setHardness(1.0F) .setUnlocalizedName("dimDoorWarp");
		blockRift = (BlockRift) (new BlockRift(properties.RiftBlockID, 0, Material.air, properties).setHardness(1.0F) .setUnlocalizedName("rift"));
		blockLimbo = new BlockLimbo(properties.LimboBlockID, 15, Material.iron, properties.LimboDimensionID, decay).setHardness(.2F).setUnlocalizedName("BlockLimbo").setLightValue(.0F);
		unstableDoor = (new UnstableDoor(properties.UnstableDoorID, Material.iron, properties).setHardness(.2F).setUnlocalizedName("chaosDoor").setLightValue(.0F) );
		dimensionalDoor = (DimensionalDoor) (new DimensionalDoor(properties.DimensionalDoorID, Material.iron, properties).setHardness(1.0F).setResistance(2000.0F) .setUnlocalizedName("dimDoor"));
		transTrapdoor = (TransTrapdoor) (new TransTrapdoor(properties.TransTrapdoorID, Material.wood).setHardness(1.0F) .setUnlocalizedName("dimHatch"));

		itemDDKey = (new ItemDDKey(properties.DDKeyItemID)).setUnlocalizedName("itemDDKey");
		itemQuartzDoor = (new ItemQuartzDoor(properties.QuartzDoorID, Material.rock)).setUnlocalizedName("itemQuartzDoor");
		itemPersonalDoor = (new ItemPersonalDoor(properties.PersonalDimDoorID, Material.rock, (ItemDoor)this.itemQuartzDoor)).setUnlocalizedName("itemQuartzDimDoor");
		itemGoldenDoor = (new ItemGoldDoor(properties.GoldenDoorItemID, Material.wood)).setUnlocalizedName("itemGoldDoor");
		itemGoldenDimensionalDoor = (new ItemGoldDimDoor(properties.GoldenDimensionalDoorItemID, Material.iron, (ItemDoor)this.itemGoldenDoor)).setUnlocalizedName("itemGoldDimDoor");
		itemDimensionalDoor = (ItemDimensionalDoor) (new ItemDimensionalDoor(properties.DimensionalDoorItemID, Material.iron, (ItemDoor)Item.doorIron)).setUnlocalizedName("itemDimDoor");
		itemWarpDoor = (new ItemWarpDoor(properties.WarpDoorItemID, Material.wood,(ItemDoor)Item.doorWood)).setUnlocalizedName("itemDimDoorWarp");
		itemRiftSignature = (new ItemRiftSignature(properties.RiftSignatureItemID)).setUnlocalizedName("itemLinkSignature");
		itemRiftRemover = (new itemRiftRemover(properties.RiftRemoverItemID, Material.wood)).setUnlocalizedName("itemRiftRemover");
		itemStableFabric = (new ItemStableFabric(properties.StableFabricItemID, 0)).setUnlocalizedName("itemStableFabric");
		itemUnstableDoor = (new ItemUnstableDoor(properties.UnstableDoorItemID, Material.iron, null)).setUnlocalizedName("itemChaosDoor");
		itemRiftBlade = (new ItemRiftBlade(properties.RiftBladeItemID, properties)).setUnlocalizedName("ItemRiftBlade");
		itemStabilizedLinkSignature = (new ItemStabilizedRiftSignature(properties.StabilizedRiftSignatureItemID)).setUnlocalizedName("itemStabilizedRiftSig");
		itemWorldThread = (new ItemWorldThread(properties.WorldThreadItemID)).setUnlocalizedName("itemWorldThread");
		
		// Check if other biomes have been registered with the same IDs we want. If so, crash Minecraft
		// to notify the user instead of letting it pass and conflicting with Biomes o' Plenty.
		DDBiomeGenBase.checkBiomes( new int[] { properties.LimboBiomeID, properties.PocketBiomeID } );

		// Initialize our biomes
		mod_pocketDim.limboBiome = (new BiomeGenLimbo(properties.LimboBiomeID));
		mod_pocketDim.pocketBiome = (new BiomeGenPocket(properties.PocketBiomeID));

		GameRegistry.registerBlock(quartzDoor, "Quartz Door");
		GameRegistry.registerBlock(personalDimDoor, "Personal Dimensional Door");
		GameRegistry.registerBlock(goldenDoor, "Golden Door");
		GameRegistry.registerBlock(goldenDimensionalDoor, "Golden Dimensional Door");
		GameRegistry.registerBlock(unstableDoor, "Unstable Door");
		GameRegistry.registerBlock(warpDoor, "Warp Door");
		GameRegistry.registerBlock(blockRift, "Rift");
		GameRegistry.registerBlock(blockLimbo, "Unraveled Fabric");
		GameRegistry.registerBlock(dimensionalDoor, "Dimensional Door");
		GameRegistry.registerBlock(transTrapdoor,"Transdimensional Trapdoor");
		GameRegistry.registerBlock(blockDimWallPerm, "Fabric of RealityPerm");
		GameRegistry.registerBlock(transientDoor, "transientDoor");

		GameRegistry.registerBlock(blockDimWall, ItemBlockDimWall.class, "Fabric of Reality");

		if (!DimensionManager.registerProviderType(properties.PocketProviderID, PocketProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between PocketProvider from Dimensional Doors and another provider type. Fix your configuration!");
		if (!DimensionManager.registerProviderType(properties.LimboProviderID, LimboProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between LimboProvider from Dimensional Doors and another provider type. Fix your configuration!");
		if (!DimensionManager.registerProviderType(properties.PersonalPocketProviderID, PersonalPocketProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between LimboProvider from Dimensional Doors and another provider type. Fix your configuration!");
			
		DimensionManager.registerDimension(properties.LimboDimensionID, properties.LimboProviderID);
		
		LanguageRegistry.addName(goldenDoor, "Golden Door");
		LanguageRegistry.addName(goldenDimensionalDoor, "Golden Dimensional Door");
		LanguageRegistry.addName(transientDoor	, "Transient Door");
		LanguageRegistry.addName(blockRift	, "Rift");
		LanguageRegistry.addName(blockLimbo	, "Unraveled Fabric");
		LanguageRegistry.addName(warpDoor	, "Warp Door");
		LanguageRegistry.addName(unstableDoor	, "Unstable Door");
		LanguageRegistry.addName(blockDimWall	, "Fabric of Reality");
		LanguageRegistry.addName(blockDimWallPerm	, "Eternal Fabric");
		LanguageRegistry.addName(dimensionalDoor, "Dimensional Door");
		LanguageRegistry.addName(transTrapdoor, "Transdimensional Trapdoor");

		LanguageRegistry.addName(itemWarpDoor, "Warp Door");
		LanguageRegistry.addName(itemRiftSignature, "Rift Signature");
		LanguageRegistry.addName(itemGoldenDoor, "Golden Door");
		LanguageRegistry.addName(itemGoldenDimensionalDoor, "Golden Dimensional Door");
		LanguageRegistry.addName(itemStabilizedLinkSignature, "Stabilized Rift Signature");
		LanguageRegistry.addName(itemRiftRemover, "Rift Remover");
		LanguageRegistry.addName(itemStableFabric, "Stable Fabric");
		LanguageRegistry.addName(itemUnstableDoor, "Unstable Door");
		LanguageRegistry.addName(itemDimensionalDoor, "Dimensional Door");
		LanguageRegistry.addName(itemRiftBlade, "Rift Blade");
		LanguageRegistry.addName(itemWorldThread, "World Thread");
		LanguageRegistry.addName(itemDDKey, "Rift Key");

		/**
		 * Add names for multiblock inventory item
		 */
		LanguageRegistry.addName(new ItemStack(blockDimWall, 1, 0), "Fabric of Reality");
		LanguageRegistry.addName(new ItemStack(blockDimWall, 1, 1), "Ancient Fabric");

		LanguageRegistry.instance().addStringLocalization("itemGroup.dimDoorsCustomTab", "en_US", "Dimensional Doors Items");

		GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
		GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
		GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
		GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");

		EntityRegistry.registerModEntity(MobMonolith.class, "Monolith", properties.MonolithEntityID, this, 70, 1, true);
		EntityList.IDtoClassMapping.put(properties.MonolithEntityID, MobMonolith.class);
		EntityList.entityEggs.put(properties.MonolithEntityID, new EntityEggInfo(properties.MonolithEntityID, 0, 0xffffff));
		LanguageRegistry.instance().addStringLocalization("entity.DimDoors.Obelisk.name", "Monolith");

		CraftingManager.registerRecipes(properties);
		GameRegistry.registerCraftingHandler(new CraftingManager());
		DungeonHelper.initialize();
		gatewayGenerator = new GatewayGenerator(properties);
		GameRegistry.registerWorldGenerator(mod_pocketDim.gatewayGenerator);

		// Register loot chests
		DDLoot.registerInfo(properties);
		proxy.loadTextures();
		proxy.registerRenderers();
	}

	@EventHandler
	public void onPostInitialization(FMLPostInitializationEvent event)
	{
		// Check in case other mods have registered over our biome IDs
		DDBiomeGenBase.checkBiomes( new int[] { properties.LimboBiomeID, properties.PocketBiomeID } );
		
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoaderHelper());
	}
	
	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent event)
	{
		try
		{
			PocketManager.unload();
			deathTracker.writeToFile();
			deathTracker = null;
			worldProperties = null;
			this.currrentSaveRootDirectory=null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		currrentSaveRootDirectory = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath();
		
		// Load the config file that's specific to this world
		worldProperties = new DDWorldProperties(new File(currrentSaveRootDirectory + "/DimensionalDoors/DimDoorsWorld.cfg"));

		// Initialize a new DeathTracker
		deathTracker = new DeathTracker(currrentSaveRootDirectory + "/DimensionalDoors/data/deaths.txt");
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		// Register commands with the server
		event.registerServerCommand( CommandResetDungeons.instance() );
		event.registerServerCommand( CommandCreateDungeonRift.instance() );
		event.registerServerCommand( CommandListDungeons.instance() );
		event.registerServerCommand( CommandCreateRandomRift.instance() );
		event.registerServerCommand( CommandDeleteAllLinks.instance() );
		//CommandDeleteDimensionData.instance().register(event);
		event.registerServerCommand( CommandDeleteRifts.instance() );
		event.registerServerCommand( CommandExportDungeon.instance() );
		//CommandPrintDimensionData.instance().register(event);
		//CommandPruneDimensions.instance().register(event);
		event.registerServerCommand( CommandCreatePocket.instance() );
		event.registerServerCommand( CommandTeleportPlayer.instance() );
		
		
		try
		{
			ChunkLoaderHelper.loadChunkForcedWorlds(event);
		}
		catch (Exception e)
		{
			System.err.println("Failed to load chunk loaders for Dimensional Doors. The following error occurred:");
			System.err.println(e.toString());
		}
	}
	
	public String getCurrentSavePath()
	{
		return this.currrentSaveRootDirectory;
	}
	
	public static void sendChat(EntityPlayer player, String message)
	{
		ChatMessageComponent cmp = new ChatMessageComponent();
		cmp.addText(message);
		player.sendChatToPlayer(cmp);
	}
}
