package com.zixiken.dimdoors;

import java.io.File;
import java.util.List;

import com.zixiken.dimdoors.items.*;
import com.zixiken.dimdoors.network.DimDoorsNetwork;
import com.zixiken.dimdoors.render.ItemRenderManager;
import com.zixiken.dimdoors.schematic.BlockRotator;
import com.zixiken.dimdoors.blocks.TransientDoor;
import com.zixiken.dimdoors.commands.CommandListDungeons;
import com.zixiken.dimdoors.ticking.MobMonolith;
import com.zixiken.dimdoors.world.LimboDecay;
import com.zixiken.dimdoors.world.LimboProvider;
import com.zixiken.dimdoors.blocks.BlockDimWall;
import com.zixiken.dimdoors.blocks.BlockDimWallPerm;
import com.zixiken.dimdoors.blocks.BlockDoorGold;
import com.zixiken.dimdoors.blocks.BlockDoorQuartz;
import com.zixiken.dimdoors.blocks.BlockGoldDimDoor;
import com.zixiken.dimdoors.blocks.BlockLimbo;
import com.zixiken.dimdoors.blocks.BlockRift;
import com.zixiken.dimdoors.blocks.DimensionalDoor;
import com.zixiken.dimdoors.blocks.PersonalDimDoor;
import com.zixiken.dimdoors.blocks.TransTrapdoor;
import com.zixiken.dimdoors.blocks.UnstableDoor;
import com.zixiken.dimdoors.blocks.WarpDoor;
import com.zixiken.dimdoors.commands.CommandCreateDungeonRift;
import com.zixiken.dimdoors.commands.CommandCreatePocket;
import com.zixiken.dimdoors.commands.CommandCreateRandomRift;
import com.zixiken.dimdoors.commands.CommandDeleteRifts;
import com.zixiken.dimdoors.commands.CommandExportDungeon;
import com.zixiken.dimdoors.commands.CommandResetDungeons;
import com.zixiken.dimdoors.commands.CommandTeleportPlayer;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.config.DDWorldProperties;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.helpers.ChunkLoaderHelper;
import com.zixiken.dimdoors.helpers.DungeonHelper;
import com.zixiken.dimdoors.items.ItemRiftRemover;
import com.zixiken.dimdoors.ticking.CustomLimboPopulator;
import com.zixiken.dimdoors.ticking.LimboDecayScheduler;
import com.zixiken.dimdoors.ticking.RiftRegenerator;
import com.zixiken.dimdoors.ticking.ServerTickHandler;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoorGold;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;
import com.zixiken.dimdoors.world.BiomeGenLimbo;
import com.zixiken.dimdoors.world.BiomeGenPocket;
import com.zixiken.dimdoors.world.DDBiomeGenBase;
import com.zixiken.dimdoors.world.PersonalPocketProvider;
import com.zixiken.dimdoors.world.PocketProvider;
import com.zixiken.dimdoors.world.gateways.GatewayGenerator;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = DimDoors.MODID, name = "Dimensional Doors", version = DimDoors.VERSION)
public class DimDoors {
	public static final String VERSION = "2.3.0-a1";
	public static final String MODID = "dimdoors";

	public static final int NETHER_DIMENSION_ID = -1;

	@SidedProxy(clientSide = "com.zixiken.dimdoors.client.ClientProxy",
            serverSide = "com.zixiken.dimdoors.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance(DimDoors.MODID)
	public static DimDoors instance;

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

	public static ItemDoor itemGoldenDimensionalDoor;
	public static ItemDoor itemGoldenDoor;
	public static Item itemWorldThread;

	public static Item itemRiftBlade;
	public static ItemDimensionalDoor itemDimensionalDoor;
	public static Item itemWarpDoor;
	public static Item itemRiftRemover;
	public static Item itemRiftSignature;
	public static Item itemStableFabric;
	public static Item itemUnstableDoor;
	public static Item itemDDKey;
	public static ItemDoor itemQuartzDoor;
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

    private static LimboDecayScheduler limboDecayScheduler;
	private static ServerTickHandler serverTickHandler;
	private static LimboDecay limboDecay;
	private static EventHookContainer hooks;
	
	//TODO this is a temporary workaround for saving data
	private String currrentSaveRootDirectory;
	
	public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimDoorsCreativeTab") {
		@Override
		public Item getTabIconItem()
		{
			return DimDoors.itemDimensionalDoor;
		}
	};

	@Mod.EventHandler
	public void onPreInitialization(FMLPreInitializationEvent event) {
		//This should be the FIRST thing that gets done.
		String path = event.getSuggestedConfigurationFile().getAbsolutePath().replace(MODID, "DimDoors");

		properties = DDProperties.initialize(new File(path));

		//Now do other stuff
		hooks = new EventHookContainer(properties);
		MinecraftForge.EVENT_BUS.register(hooks);
		MinecraftForge.TERRAIN_GEN_BUS.register(hooks);

        // Initialize and register blocks and items
        //TODO: Move this all to their own classes
        transientDoor = new TransientDoor();
        goldenDimensionalDoor = new BlockGoldDimDoor();

        quartzDoor = new BlockDoorQuartz();
        personalDimDoor = new PersonalDimDoor();

        goldenDoor = new BlockDoorGold();
        blockDimWall = new BlockDimWall();
        blockDimWallPerm = new BlockDimWallPerm();
        warpDoor = new WarpDoor();
        blockLimbo = new BlockLimbo(limboDecay);
        unstableDoor = new UnstableDoor();
        dimensionalDoor = new DimensionalDoor();
        transTrapdoor = new TransTrapdoor();
        blockRift = new BlockRift();

        itemDDKey = new ItemDDKey();
        itemQuartzDoor = new ItemQuartzDoor();
        itemPersonalDoor = new ItemPersonalDoor();
        itemGoldenDoor = new ItemGoldDoor();
        itemGoldenDimensionalDoor = new ItemGoldDimDoor();
        itemDimensionalDoor = new ItemDimensionalDoor();
        itemWarpDoor = new ItemWarpDoor();
        itemRiftSignature = new ItemRiftSignature();
        itemRiftRemover = new ItemRiftRemover();
        itemStableFabric = new ItemStableFabric();
        itemUnstableDoor = new ItemUnstableDoor();
        itemRiftBlade = new ItemRiftBlade();
        itemStabilizedRiftSignature = new ItemStabilizedRiftSignature();
        itemWorldThread = new ItemWorldThread();

        GameRegistry.registerBlock(quartzDoor, null, BlockDoorQuartz.ID);
        GameRegistry.registerBlock(personalDimDoor, null, PersonalDimDoor.ID);
        GameRegistry.registerBlock(goldenDoor, null, BlockDoorGold.ID);
        GameRegistry.registerBlock(goldenDimensionalDoor, null, BlockGoldDimDoor.ID);
        GameRegistry.registerBlock(unstableDoor, null, UnstableDoor.ID);
        GameRegistry.registerBlock(warpDoor, null, WarpDoor.ID);
        GameRegistry.registerBlock(blockRift, BlockRift.ID);
        GameRegistry.registerBlock(blockLimbo, BlockLimbo.ID);
        GameRegistry.registerBlock(dimensionalDoor, null, DimensionalDoor.ID);
        GameRegistry.registerBlock(transTrapdoor, TransTrapdoor.ID);
        GameRegistry.registerBlock(blockDimWall, ItemBlockDimWall.class, BlockDimWall.ID);
        GameRegistry.registerBlock(blockDimWallPerm, BlockDimWallPerm.ID);
        GameRegistry.registerBlock(transientDoor, TransientDoor.ID);
        GameRegistry.registerItem(itemDDKey, ItemDDKey.ID);
        GameRegistry.registerItem(itemQuartzDoor, ItemQuartzDoor.ID);
        GameRegistry.registerItem(itemPersonalDoor, ItemPersonalDoor.ID);
        GameRegistry.registerItem(itemGoldenDoor, ItemGoldDoor.ID);
        GameRegistry.registerItem(itemGoldenDimensionalDoor, ItemGoldDimDoor.ID);
        GameRegistry.registerItem(itemDimensionalDoor, ItemDimensionalDoor.ID);
        GameRegistry.registerItem(itemWarpDoor, ItemWarpDoor.ID);
        GameRegistry.registerItem(itemRiftSignature, ItemRiftSignature.ID);
        GameRegistry.registerItem(itemRiftRemover, ItemRiftRemover.ID);
        GameRegistry.registerItem(itemStableFabric, ItemStableFabric.ID);
        GameRegistry.registerItem(itemUnstableDoor, ItemUnstableDoor.ID);
        GameRegistry.registerItem(itemRiftBlade, ItemRiftBlade.ID);
        GameRegistry.registerItem(itemStabilizedRiftSignature, ItemStabilizedRiftSignature.ID);
        GameRegistry.registerItem(itemWorldThread, ItemWorldThread.ID);

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
        GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
        GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");

        EntityRegistry.registerModEntity(MobMonolith.class, "Monolith", properties.MonolithEntityID, this, 70, 1, true);
        EntityList.idToClassMapping.put(properties.MonolithEntityID, MobMonolith.class);
        EntityList.entityEggs.put(properties.MonolithEntityID, new EntityList.EntityEggInfo(properties.MonolithEntityID, 0, 0xffffff));

		proxy.registerRenderers();

        proxy.registerSidedHooks();

        DimDoorsNetwork.init();

        ItemRenderManager.addModelVariants();
	}

	@Mod.EventHandler
	public void onInitialization(FMLInitializationEvent event) {
		// Initialize ServerTickHandler instance
		serverTickHandler = new ServerTickHandler();
        MinecraftForge.EVENT_BUS.register(serverTickHandler);
		
		// Initialize LimboDecay instance: required for BlockLimbo
		limboDecay = new LimboDecay(properties);
		
		// Check if other biomes have been registered with the same IDs we want. If so, crash Minecraft
		// to notify the user instead of letting it pass and conflicting with Biomes o' Plenty.
		DDBiomeGenBase.checkBiomes(properties.LimboBiomeID, properties.PocketBiomeID);

		// Initialize our biomes
		DimDoors.limboBiome = (new BiomeGenLimbo(properties.LimboBiomeID));
		DimDoors.pocketBiome = (new BiomeGenPocket(properties.PocketBiomeID));

        BlockRotator.setupOrientations();

		if(!DimensionManager.registerProviderType(properties.PocketProviderID, PocketProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between PocketProvider from Dimensional Doors and another provider type. Fix your configuration!");
		if(!DimensionManager.registerProviderType(properties.LimboProviderID, LimboProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between LimboProvider from Dimensional Doors and another provider type. Fix your configuration!");
		if(!DimensionManager.registerProviderType(properties.PersonalPocketProviderID, PersonalPocketProvider.class, false))
			throw new IllegalStateException("There is a provider ID conflict between PersonalPocketProvider from Dimensional Doors and another provider type. Fix your configuration!");
			
		DimensionManager.registerDimension(properties.LimboDimensionID, properties.LimboProviderID);

		CraftingManager.registerRecipes(properties);
		CraftingManager.registerDispenserBehaviors();
        MinecraftForge.EVENT_BUS.register(new CraftingManager());

		DungeonHelper.initialize();
		gatewayGenerator = new GatewayGenerator(properties);
		GameRegistry.registerWorldGenerator(gatewayGenerator, 0);

		// Register loot chests
		DDLoot.registerInfo(properties);
        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());

        ItemRenderManager.registerItemRenderers();
	}

	@Mod.EventHandler
	public void onPostInitialization(FMLPostInitializationEvent event) {
		// Check in case other mods have registered over our biome IDs
		DDBiomeGenBase.checkBiomes(properties.LimboBiomeID, properties.PocketBiomeID);
		
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoaderHelper());
	}
	
	@Mod.EventHandler
	public void onServerStopped(FMLServerStoppedEvent event) {
        PocketManager.tryUnload();
        if(deathTracker != null) {
            deathTracker.writeToFile();
            deathTracker = null;
        }
        worldProperties = null;
        currrentSaveRootDirectory = null;

        // Unregister all tick receivers from serverTickHandler to avoid leaking
        // scheduled tasks between single-player game sessions
        if(serverTickHandler != null)
            serverTickHandler.unregisterReceivers();
        spawner = null;
        riftRegenerator = null;
        limboDecayScheduler = null;
	}
	
	@Mod.EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
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

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		// Register commands with the server
		event.registerServerCommand( CommandResetDungeons.instance() );
		event.registerServerCommand( CommandCreateDungeonRift.instance() );
		event.registerServerCommand( CommandListDungeons.instance() );
		event.registerServerCommand( CommandCreateRandomRift.instance() );
		event.registerServerCommand( CommandDeleteRifts.instance() );
		event.registerServerCommand( CommandExportDungeon.instance() );
		event.registerServerCommand( CommandCreatePocket.instance() );
		event.registerServerCommand( CommandTeleportPlayer.instance() );

        ChunkLoaderHelper.loadForcedChunkWorlds(event);
	}
	
	public String getCurrentSavePath()
	{
		return this.currrentSaveRootDirectory;
	}

	public static void sendChat(EntityPlayer player, String message) {
        ChatComponentText text = new ChatComponentText(message);
        player.addChatComponentMessage(text);
	}

    public static void translateAndAdd(String key, List list) {
        for(int i=0;i<10;i++) {
            if(StatCollector.canTranslate(key+Integer.toString(i))) {
                String line = StatCollector.translateToLocal(key + Integer.toString(i));
                list.add(line);
            } else break;
        }
    }
}
