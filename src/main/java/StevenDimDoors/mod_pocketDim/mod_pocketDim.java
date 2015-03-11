package StevenDimDoors.mod_pocketDim;

import java.io.File;
import java.util.List;

import StevenDimDoors.mod_pocketDim.network.DimDoorsNetwork;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
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
import StevenDimDoors.mod_pocketDim.ticking.CustomLimboPopulator;
import StevenDimDoors.mod_pocketDim.ticking.LimboDecayScheduler;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import StevenDimDoors.mod_pocketDim.ticking.RiftRegenerator;
import StevenDimDoors.mod_pocketDim.ticking.ServerTickHandler;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoorGold;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityRift;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;
import StevenDimDoors.mod_pocketDim.world.BiomeGenLimbo;
import StevenDimDoors.mod_pocketDim.world.BiomeGenPocket;
import StevenDimDoors.mod_pocketDim.world.DDBiomeGenBase;
import StevenDimDoors.mod_pocketDim.world.LimboDecay;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PersonalPocketProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;
import StevenDimDoors.mod_pocketDim.world.gateways.GatewayGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = mod_pocketDim.modid, name = "Dimensional Doors", version = mod_pocketDim.version)
public class mod_pocketDim
{
	public static final String version = "2.2.5-test";
	public static final String modid = "dimdoors";
	
	//TODO need a place to stick all these constants
	public static final int NETHER_DIMENSION_ID = -1;

	//need to clean up 
	@SidedProxy(clientSide = "StevenDimDoors.mod_pocketDimClient.ClientProxy", serverSide = "StevenDimDoors.mod_pocketDim.CommonProxy")
	public static CommonProxy proxy;

	@Instance(mod_pocketDim.modid)
	public static mod_pocketDim instance;

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
	public static Item itemDDKey;
	public static Item itemQuartzDoor;
	public static Item itemPersonalDoor;
	public static Item itemStabilizedRiftSignature;

	public static BiomeGenBase limboBiome;
	public static BiomeGenBase pocketBiome;

	public static DDProperties properties;
	public static DDWorldProperties worldProperties;
	public static CustomLimboPopulator spawner; //Added this field temporarily. Will be refactored out later.
	public static RiftRegenerator riftRegenerator;
	public static GatewayGenerator gatewayGenerator;
	public static DeathTracker deathTracker;
	private static ServerTickHandler serverTickHandler;
	private static LimboDecayScheduler limboDecayScheduler;
	private static LimboDecay limboDecay;
	private static EventHookContainer hooks;
	
	//TODO this is a temporary workaround for saving data
	private String currrentSaveRootDirectory;
	
	public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimDoorsCreativeTab") 
	{
		@Override
		public Item getTabIconItem()
		{
			return mod_pocketDim.itemDimensionalDoor;
		}
	};

	@EventHandler
	public void onPreInitialization(FMLPreInitializationEvent event)
	{
		//This should be the FIRST thing that gets done.
		String path = event.getSuggestedConfigurationFile().getAbsolutePath().replace(modid, "DimDoors");

		properties = DDProperties.initialize(new File(path));

		//Now do other stuff
		hooks = new EventHookContainer(properties);
		MinecraftForge.EVENT_BUS.register(hooks);
		MinecraftForge.TERRAIN_GEN_BUS.register(hooks);

        proxy.registerSidedHooks(properties);

        DimDoorsNetwork.init();
	}

	@EventHandler
	public void onInitialization(FMLInitializationEvent event)
	{
		// Initialize ServerTickHandler instance
		serverTickHandler = new ServerTickHandler();
        FMLCommonHandler.instance().bus().register(serverTickHandler);
		
		// Initialize LimboDecay instance: required for BlockLimbo
		limboDecay = new LimboDecay(properties);

		// Initialize blocks and items
		transientDoor = new TransientDoor(Material.iron, properties).setHardness(1.0F) .setBlockName("transientDoor");
		goldenDimensionalDoor = new BlockGoldDimDoor(Material.iron, properties).setHardness(1.0F).setBlockName("dimDoorGold").setBlockTextureName("itemGoldDimDoor");

		quartzDoor = new BlockDoorQuartz(Material.rock).setHardness(0.1F).setBlockName("doorQuartz").setBlockTextureName("itemQuartzDoor");
		personalDimDoor = new PersonalDimDoor(Material.rock,properties).setHardness(0.1F).setBlockName("dimDoorPersonal").setBlockTextureName("itemQuartzDimDoor");

		goldenDoor = new BlockDoorGold(Material.iron).setHardness(0.1F).setBlockName("doorGold").setBlockTextureName("itemGoldDoor");
		blockDimWall = new BlockDimWall(0, Material.iron).setLightLevel(1.0F).setHardness(0.1F).setBlockName("blockDimWall");
		blockDimWallPerm = (new BlockDimWallPerm(0, Material.iron)).setLightLevel(1.0F).setBlockUnbreakable().setResistance(6000000.0F).setBlockName("blockDimWallPerm");
		warpDoor = new WarpDoor(Material.wood, properties).setHardness(1.0F) .setBlockName("dimDoorWarp").setBlockTextureName("itemDimDoorWarp");
		blockLimbo = new BlockLimbo(15, Material.iron, properties.LimboDimensionID, limboDecay).setHardness(.2F).setBlockName("BlockLimbo").setLightLevel(.0F);
		unstableDoor = (new UnstableDoor(Material.iron, properties).setHardness(.2F).setBlockName("chaosDoor").setLightLevel(.0F).setBlockTextureName("itemChaosDoor") );
		dimensionalDoor = (DimensionalDoor) (new DimensionalDoor(Material.iron, properties).setHardness(1.0F).setResistance(2000.0F) .setBlockName("dimDoor").setBlockTextureName("itemDimDoor"));
		transTrapdoor = (TransTrapdoor) (new TransTrapdoor(Material.wood).setHardness(1.0F) .setBlockName("dimHatch"));
        blockRift = (BlockRift) (new BlockRift(Material.fire, properties).setHardness(1.0F) .setBlockName("rift"));

		itemDDKey = (new ItemDDKey()).setUnlocalizedName("itemDDKey");
		itemQuartzDoor = (new ItemQuartzDoor(Material.rock)).setUnlocalizedName("itemQuartzDoor");
		itemPersonalDoor = (new ItemPersonalDoor(Material.rock, (ItemDoor)this.itemQuartzDoor)).setUnlocalizedName("itemQuartzDimDoor");
		itemGoldenDoor = (new ItemGoldDoor(Material.wood)).setUnlocalizedName("itemGoldDoor");
		itemGoldenDimensionalDoor = (new ItemGoldDimDoor(Material.iron, (ItemDoor)this.itemGoldenDoor)).setUnlocalizedName("itemGoldDimDoor");
		itemDimensionalDoor = (ItemDimensionalDoor) (new ItemDimensionalDoor(Material.iron, (ItemDoor) Items.iron_door)).setUnlocalizedName("itemDimDoor");
		itemWarpDoor = (new ItemWarpDoor(Material.wood,(ItemDoor)Items.iron_door)).setUnlocalizedName("itemDimDoorWarp");
		itemRiftSignature = (new ItemRiftSignature()).setUnlocalizedName("itemLinkSignature");
		itemRiftRemover = (new itemRiftRemover(Material.wood)).setUnlocalizedName("itemRiftRemover");
		itemStableFabric = (new ItemStableFabric(0)).setUnlocalizedName("itemStableFabric");
		itemUnstableDoor = (new ItemUnstableDoor(Material.iron, null)).setUnlocalizedName("itemChaosDoor");
		itemRiftBlade = (new ItemRiftBlade(properties)).setUnlocalizedName("ItemRiftBlade");
		itemStabilizedRiftSignature = (new ItemStabilizedRiftSignature()).setUnlocalizedName("itemStabilizedRiftSig");
		itemWorldThread = (new ItemWorldThread()).setUnlocalizedName("itemWorldThread");
		
		// Check if other biomes have been registered with the same IDs we want. If so, crash Minecraft
		// to notify the user instead of letting it pass and conflicting with Biomes o' Plenty.
		DDBiomeGenBase.checkBiomes( new int[] { properties.LimboBiomeID, properties.PocketBiomeID } );

		// Initialize our biomes
		mod_pocketDim.limboBiome = (new BiomeGenLimbo(properties.LimboBiomeID));
		mod_pocketDim.pocketBiome = (new BiomeGenPocket(properties.PocketBiomeID));

		GameRegistry.registerBlock(quartzDoor, null, "Quartz Door");
		GameRegistry.registerBlock(personalDimDoor, null, "Personal Dimensional Door");
		GameRegistry.registerBlock(goldenDoor, null, "Golden Door");
		GameRegistry.registerBlock(goldenDimensionalDoor, null, "Golden Dimensional Door");
		GameRegistry.registerBlock(unstableDoor, null, "Unstable Door");
		GameRegistry.registerBlock(warpDoor, null, "Warp Door");
		GameRegistry.registerBlock(blockRift, "Rift");
		GameRegistry.registerBlock(blockLimbo, "Unraveled Fabric");
		GameRegistry.registerBlock(dimensionalDoor, null, "Dimensional Door");
		GameRegistry.registerBlock(transTrapdoor,"Transdimensional Trapdoor");
		GameRegistry.registerBlock(blockDimWallPerm, "Fabric of RealityPerm");
		GameRegistry.registerBlock(transientDoor, "transientDoor");
        GameRegistry.registerItem(itemDDKey, "Rift Key");
        GameRegistry.registerItem(itemQuartzDoor, "Quartz Door Item");
        GameRegistry.registerItem(itemPersonalDoor, "Personal Dimensional Door Item");
        GameRegistry.registerItem(itemGoldenDoor, "Golden Door Item");
        GameRegistry.registerItem(itemGoldenDimensionalDoor, "Golden Dimensional Door Item");
        GameRegistry.registerItem(itemDimensionalDoor, "Dimensional Door Item");
        GameRegistry.registerItem(itemWarpDoor, "Warp Door Item");
        GameRegistry.registerItem(itemRiftSignature, "Rift Signature");
        GameRegistry.registerItem(itemRiftRemover, "Rift Remover");
        GameRegistry.registerItem(itemStableFabric, "Stable Fabric Item");
        GameRegistry.registerItem(itemUnstableDoor, "Unstable Door Item");
        GameRegistry.registerItem(itemRiftBlade, "Rift Blade");
        GameRegistry.registerItem(itemStabilizedRiftSignature, "Stabilized Rift Signature");
        GameRegistry.registerItem(itemWorldThread, "World Thread");

		GameRegistry.registerBlock(blockDimWall, ItemBlockDimWall.class, "Fabric of Reality");

        BlockRotator.setupOrientations();

		if (!DimensionManager.registerProviderType(properties.PocketProviderID, PocketProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between PocketProvider from Dimensional Doors and another provider type. Fix your configuration!");
		if (!DimensionManager.registerProviderType(properties.LimboProviderID, LimboProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between LimboProvider from Dimensional Doors and another provider type. Fix your configuration!");
		if (!DimensionManager.registerProviderType(properties.PersonalPocketProviderID, PersonalPocketProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between PersonalPocketProvider from Dimensional Doors and another provider type. Fix your configuration!");
			
		DimensionManager.registerDimension(properties.LimboDimensionID, properties.LimboProviderID);

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
        GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
        GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");

		EntityRegistry.registerModEntity(MobMonolith.class, "Monolith", properties.MonolithEntityID, this, 70, 1, true);
		EntityList.IDtoClassMapping.put(properties.MonolithEntityID, MobMonolith.class);
		EntityList.entityEggs.put(properties.MonolithEntityID, new EntityList.EntityEggInfo(properties.MonolithEntityID, 0, 0xffffff));

		CraftingManager.registerRecipes(properties);
		CraftingManager.registerDispenserBehaviors();
        FMLCommonHandler.instance().bus().register(new CraftingManager());

		DungeonHelper.initialize();
		gatewayGenerator = new GatewayGenerator(properties);
		GameRegistry.registerWorldGenerator(mod_pocketDim.gatewayGenerator, 0);

		// Register loot chests
		DDLoot.registerInfo(properties);
		proxy.loadTextures();
		proxy.registerRenderers();
        FMLCommonHandler.instance().bus().register(new ConnectionHandler());
	}

    public static void translateAndAdd(String key, List list) {
        for (int i=0;i<10;i++) {
            if (StatCollector.canTranslate(key+Integer.toString(i))) {
                String line = StatCollector.translateToLocal(key + Integer.toString(i));
                list.add(line);
            } else
                break;
        }
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
			currrentSaveRootDirectory = null;
			
			// Unregister all tick receivers from serverTickHandler to avoid leaking
			// scheduled tasks between single-player game sessions
			serverTickHandler.unregisterReceivers();
			spawner = null;
			riftRegenerator = null;
			limboDecayScheduler = null;
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
		
		// Register regular tick receivers
		// CustomLimboPopulator should be initialized before any provider instances are created
		spawner = new CustomLimboPopulator(serverTickHandler, properties);
		riftRegenerator = new RiftRegenerator(serverTickHandler, blockRift);
		limboDecayScheduler = new LimboDecayScheduler(serverTickHandler, limboDecay);
		
		hooks.setSessionFields(worldProperties, riftRegenerator);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		// Register commands with the server
		event.registerServerCommand( CommandResetDungeons.instance() );
		event.registerServerCommand( CommandCreateDungeonRift.instance() );
		event.registerServerCommand( CommandListDungeons.instance() );
		event.registerServerCommand( CommandCreateRandomRift.instance() );
		event.registerServerCommand( CommandDeleteRifts.instance() );
		event.registerServerCommand( CommandExportDungeon.instance() );
		event.registerServerCommand( CommandCreatePocket.instance() );
		event.registerServerCommand( CommandTeleportPlayer.instance() );
		
		try
		{
			ChunkLoaderHelper.loadForcedChunkWorlds(event);
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
        ChatComponentText text = new ChatComponentText(message);
        player.addChatComponentMessage(text);
	}
}
