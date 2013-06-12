package StevenDimDoors.mod_pocketDim;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWall;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWallPerm;
import StevenDimDoors.mod_pocketDim.blocks.BlockLimbo;
import StevenDimDoors.mod_pocketDim.blocks.BlockRift;
import StevenDimDoors.mod_pocketDim.blocks.ChaosDoor;
import StevenDimDoors.mod_pocketDim.blocks.ExitDoor;
import StevenDimDoors.mod_pocketDim.blocks.dimDoor;
import StevenDimDoors.mod_pocketDim.blocks.dimHatch;
import StevenDimDoors.mod_pocketDim.commands.CommandAddDungeonRift;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteAllLinks;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteDimData;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteRifts;
import StevenDimDoors.mod_pocketDim.commands.CommandEndDungeonCreation;
import StevenDimDoors.mod_pocketDim.commands.CommandPrintDimData;
import StevenDimDoors.mod_pocketDim.commands.CommandPruneDims;
import StevenDimDoors.mod_pocketDim.commands.CommandStartDungeonCreation;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.helpers.copyfile;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.items.ItemChaosDoor;
import StevenDimDoors.mod_pocketDim.items.ItemRiftBlade;
import StevenDimDoors.mod_pocketDim.items.ItemStabilizedRiftSignature;
import StevenDimDoors.mod_pocketDim.items.ItemStableFabric;
import StevenDimDoors.mod_pocketDim.items.itemDimDoor;
import StevenDimDoors.mod_pocketDim.items.itemExitDoor;
import StevenDimDoors.mod_pocketDim.items.itemLinkSignature;
import StevenDimDoors.mod_pocketDim.items.itemRiftRemover;
import StevenDimDoors.mod_pocketDim.ticking.MobObelisk;
import StevenDimDoors.mod_pocketDim.world.BiomeGenLimbo;
import StevenDimDoors.mod_pocketDim.world.BiomeGenPocket;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.pocketProvider;
import StevenDimDoors.mod_pocketDimClient.ClientPacketHandler;
import StevenDimDoors.mod_pocketDimClient.ClientTickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;


@Mod(modid = mod_pocketDim.modid, name = "Dimensional Doors", version = mod_pocketDim.version)



@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        clientPacketHandlerSpec =
                @SidedPacketHandler(channels = {"pocketDim" }, packetHandler = ClientPacketHandler.class),
        serverPacketHandlerSpec =
                @SidedPacketHandler(channels = {"pocketDim" }, packetHandler = ServerPacketHandler.class),
                channels={"DimDoorPackets"}, packetHandler = PacketHandler.class, connectionHandler=ConnectionHandler.class)

public class mod_pocketDim
{
	

	public static final String version = "1.5.2R1.3.6RC1";
	public static final String modid = "DimDoors";

	//need to clean up 
    @SidedProxy(clientSide = "StevenDimDoors.mod_pocketDimClient.ClientProxy", serverSide = "StevenDimDoors.mod_pocketDim.CommonProxy")
    public static CommonProxy proxy;

    @Instance("PocketDimensions")
    public static mod_pocketDim instance = new mod_pocketDim();
    public static SchematicLoader loader = new SchematicLoader();
    public static pocketTeleporter teleporter = new pocketTeleporter();
    public static DungeonHelper dungeonHelper= new DungeonHelper();
    
 
 
    public static final ICommand printDimData = new CommandPrintDimData();
    public static final ICommand removeRiftsCommand = new CommandDeleteRifts();
    public static final ICommand pruneDimsCommand = new CommandPruneDims();
    public static final ICommand removeAllLinksCommand = new CommandDeleteAllLinks();
    public static final ICommand deleteDimDataCommand = new CommandDeleteDimData();
    public static final ICommand addDungeonRift = new CommandAddDungeonRift();
    public static final ICommand endDungeonCreation = new CommandEndDungeonCreation();
    public static final ICommand startDungeonCreation = new CommandStartDungeonCreation();


    
    public static int providerID;
    public static int dimDoorID;
    public static int ExitDoorID;
  //  public static int linkExitDoorID;
    public static int itemLinkSignatureID;
    public static int blockRiftID;
    public static int transientDoorID;
    public static int itemRiftBladeID;
    public static int limboExitRange;
  //  public static int railRenderID;
    
    public static String schematicContainer;
    
    public static int itemStableFabricID;

    public static int itemStabilizedLinkSignatureID;
    public static int itemExitDoorID;
    public static int limboDimID;
    public static int limboProviderID;
    public static int itemChaosDoorID;
    public static int chaosDoorID;
	public static int blockLimboID;
    public static int dimHatchID;
 //   public static int dimRailID;

	public static int riftSpreadFactor;
	public static int DoorRenderID=55;
	public static int HOW_MUCH_TNT;

	
    public static int itemDimDoorID;
    ///public static int linkDimDoorID;
    public static int blockDimWallID;
    public static int itemRiftRemoverID;
    public static int blockDimWallPermID;
    public static int obeliskID;
    //public static Block linkDimDoor;
    public static Block transientDoor;
    public static Block ExitDoor;
    public static Block chaosDoor;
   // public static Block linkExitDoor;
    public static Block blockRift;
    public static Block blockLimbo;
    public static  Block dimDoor;    
//    public static  Block dimRail;    

    public static  Block blockDimWall;   
    public static  Block dimHatch;
    public static Block blockDimWallPerm;
    public static  Item itemRiftBlade;

    public static  Item itemDimDoor;
    public static  Item itemExitDoor;
    public static  Item itemRiftRemover;
    public static  Item itemLinkSignature;
    public static  Item itemStableFabric;
    public static  Item itemChaosDoor;
    public static  Item itemStabilizedLinkSignature;

    
    public static BiomeGenBase limboBiome;
    public static BiomeGenBase pocketBiome;

    public static int limboBiomeID;
    public static int pocketBiomeID;
    
    public static PlayerRespawnTracker tracker= new PlayerRespawnTracker();
    
    public static HashMap<String,ArrayList<EntityItem>> limboSpawnInventory=new HashMap<String,ArrayList<EntityItem>>();
    
    public static ArrayList blocksImmuneToRift= new ArrayList();
	



    

    
    public static boolean riftsInWorldGen;
 
	public static boolean isLimboActive;
	
	  public static boolean enableIronDimDoor;
	  
		public static boolean enableWoodenDimDoor;
		
		  public static boolean enableRiftSignature;
		  
			public static boolean enableRiftRemover;
			
			public static boolean enableUnstableDoor;

			public static boolean enableRiftBlade;
			
		//	public static boolean enableDimRail;

			public static boolean enableDimTrapDoor;

			public static boolean enableDoorOpenGL;

			public static boolean hardcoreLimbo;

			public static boolean returnInventory;

	public static boolean hasInitDims=false;

    public static boolean TNFREAKINGT;

	public static boolean isPlayerWearingGoogles=false;
	
    


	

    public static RiftGenerator riftGen = new RiftGenerator();
    
    
   // public static World limbo= null;

    public static long genTime;
	public static boolean enableRiftGrief;

    


	
	//public Spells spells = null;



    @PreInit
    public void PreInit(FMLPreInitializationEvent event)
    {
    	
    	
        MinecraftForge.EVENT_BUS.register(new EventHookContainer());
        File configFile = event.getSuggestedConfigurationFile();
        
        Configuration config = new Configuration(configFile);

        
        DimDoorsConfig.loadConfig(configFile);
        
        
        String schematicDir = configFile.getParent()+"/DimDoors_Custom_schematics";
        this.schematicContainer=schematicDir;
        File file= new File(schematicDir);
    	file.mkdir();

    	String helpFile = "/mods/DimDoors/How_to_add_dungeons.txt";
    	if(new File(helpFile).exists())
    	{
    		copyfile.copyFile(helpFile, file+"/How_to_add_dungeons.txt");
    	}
    	
    	dungeonHelper.importCustomDungeons(schematicDir);
    	dungeonHelper.registerBaseDungeons();

    
        
        
        
        


         
         
         

         
         
    
	
    }
    
    @Init
    public void Init(FMLInitializationEvent event)
    {
    	
    	
    	
    
        transientDoor = (new TransientDoor(transientDoorID, Material.iron)).setHardness(1.0F) .setUnlocalizedName("transientDoor");

     //   linkDimDoor = (new linkDimDoor(linkDimDoorID, Material.iron)).setHardness(1.0F) .setUnlocalizedName("dimDoorLink");
        blockDimWall = (new BlockDimWall(blockDimWallID, 0, Material.iron)).setLightValue(1.0F).setHardness(0.1F).setUnlocalizedName("blockDimWall");
        blockDimWallPerm = (new BlockDimWallPerm(blockDimWallPermID, 0, Material.iron)).setLightValue(1.0F).setBlockUnbreakable().setHardness(100000.0F).setUnlocalizedName("blockDimWallPerm");
        ExitDoor = (new ExitDoor(ExitDoorID, Material.wood)).setHardness(1.0F) .setUnlocalizedName("dimDoorWarp");
     //   linkExitDoor = (new linkExitDoor(linkExitDoorID, Material.wood)).setHardness(1.0F) .setUnlocalizedName("dimDoorexitlink");
        blockRift = (new BlockRift(blockRiftID, 0, Material.air).setHardness(1.0F) .setUnlocalizedName("rift"));
        blockLimbo = (new BlockLimbo(blockLimboID, 15, Material.iron).setHardness(.2F).setUnlocalizedName("BlockLimbo").setLightValue(.0F));
        chaosDoor = (new ChaosDoor(chaosDoorID, Material.iron).setHardness(.2F).setUnlocalizedName("chaosDoor").setLightValue(.0F) );
        dimDoor = (new dimDoor(dimDoorID, Material.iron)).setHardness(1.0F).setResistance(2000.0F) .setUnlocalizedName("dimDoor");
        dimHatch = (new dimHatch(dimHatchID,   84, Material.iron)).setHardness(1.0F) .setUnlocalizedName("dimHatch");
      //  dimRail = (new DimRail(dimRailID, 88, false)).setHardness(.5F) .setUnlocalizedName("dimRail");
 
        itemDimDoor = (new itemDimDoor(itemDimDoorID, Material.iron)).setUnlocalizedName("itemDimDoor");
        itemExitDoor = (new itemExitDoor(itemExitDoorID, Material.wood)).setUnlocalizedName("itemDimDoorWarp");
        itemLinkSignature = (new itemLinkSignature(itemLinkSignatureID )).setUnlocalizedName("itemLinkSignature");
        itemRiftRemover = (new itemRiftRemover(itemRiftRemoverID, Material.wood)).setUnlocalizedName("itemRiftRemover");
        itemStableFabric = (new ItemStableFabric(itemStableFabricID, 0)).setUnlocalizedName("itemStableFabric");
        itemChaosDoor = (new ItemChaosDoor(itemChaosDoorID, Material.iron)).setUnlocalizedName("itemChaosDoor");
        itemRiftBlade = (new ItemRiftBlade(itemRiftBladeID, Material.iron)).setUnlocalizedName("ItemRiftBlade");
        itemStabilizedLinkSignature = (new ItemStabilizedRiftSignature(itemStabilizedLinkSignatureID)).setUnlocalizedName("itemStabilizedRiftSig");

        this.limboBiome= (new BiomeGenLimbo(this.limboBiomeID) );
        this.pocketBiome= (new BiomeGenPocket(this.pocketBiomeID));
        
     
    	GameRegistry.registerWorldGenerator(this.riftGen);
    	
        //GameRegistry.registerBlock(dimRail, "Dimensional Rail");
        GameRegistry.registerBlock(chaosDoor, "Unstable Door");
        GameRegistry.registerBlock(ExitDoor, "Warp Door");
        //GameRegistry.registerBlock(linkExitDoor, "Warp Door link");
        GameRegistry.registerBlock(blockRift, "Rift");
        GameRegistry.registerBlock(blockLimbo, "Unraveled Fabric");
        //GameRegistry.registerBlock(linkDimDoor, "Dimensional Door link");
        GameRegistry.registerBlock(dimDoor, "Dimensional Door");
        GameRegistry.registerBlock(dimHatch,"Transdimensional Trapdoor");
        GameRegistry.registerBlock(blockDimWall, "Fabric of Reality");
        GameRegistry.registerBlock(blockDimWallPerm, "Fabric of RealityPerm");
        GameRegistry.registerBlock(transientDoor, "transientDoor");

        GameRegistry.registerPlayerTracker(tracker);
        
        
        
        DimensionManager.registerProviderType(this.providerID, pocketProvider.class, false);
        DimensionManager.registerProviderType(this.limboProviderID, LimboProvider.class, false);
        
        
      
        DimensionManager.registerDimension(this.limboDimID	, this.limboProviderID);
        
        LanguageRegistry.addName(transientDoor	, "transientDoor");

        LanguageRegistry.addName(blockRift	, "Rift");
        LanguageRegistry.addName(blockLimbo	, "Unraveled Fabric");
        LanguageRegistry.addName(ExitDoor	, "Warp Door");
        LanguageRegistry.addName(chaosDoor	, "Unstable Door");
        //LanguageRegistry.addName(linkDimDoor, "Dimensional Door");
        LanguageRegistry.addName(blockDimWall	, "Fabric of Reality");
        LanguageRegistry.addName(blockDimWallPerm	, "Fabric of Reality");
        LanguageRegistry.addName(dimDoor, "Dimensional Door");
        LanguageRegistry.addName(dimHatch, "Transdimensional Trapdoor");
        
        LanguageRegistry.addName(itemExitDoor	, "Warp Door");
        LanguageRegistry.addName(itemLinkSignature	, "Rift Signature");
        LanguageRegistry.addName(itemStabilizedLinkSignature, "Stabilized Rift Signature");
        LanguageRegistry.addName(itemRiftRemover	, "Rift Remover");
        LanguageRegistry.addName(itemStableFabric	, "Stable Fabric");
        LanguageRegistry.addName(itemChaosDoor	, "Unstable Door");
        LanguageRegistry.addName(itemDimDoor, "Dimensional Door");
        LanguageRegistry.addName(itemRiftBlade	, "Rift Blade");


        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);

      //GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimRail");

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
        
        EntityRegistry.registerModEntity(MobObelisk.class, "Obelisk", this.obeliskID, this,70, 1, true);
        EntityList.IDtoClassMapping.put(this.obeliskID, MobObelisk.class);
     	EntityList.entityEggs.put(this.obeliskID, new EntityEggInfo(this.obeliskID, 0, 0xffffff));
    	LanguageRegistry.instance().addStringLocalization("entity.DimDoors.Obelisk.name", "Monolith");


        
        //GameRegistry.addBiome(this.limboBiome);
        //GameRegistry.addBiome(this.pocketBiome);

        if(this.enableIronDimDoor)
        {
    	 GameRegistry.addRecipe(new ItemStack(itemDimDoor, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', Item.enderPearl,  'y', Item.doorIron 
                 });
    	 
    	 GameRegistry.addRecipe(new ItemStack(itemDimDoor, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', this.itemStableFabric,  'y', Item.doorIron 
                 });
        }
        
        /**
       if(this.enableDimRail)
        {
    	 GameRegistry.addRecipe(new ItemStack(dimRail, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', this.itemDimDoor,  'y', Block.rail 
                 });
    	 
    	 GameRegistry.addRecipe(new ItemStack(dimRail, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', this.itemExitDoor,  'y', Block.rail 
                 });
        }
        **/
    	 
        if(this.enableUnstableDoor)
        {
    	 GameRegistry.addRecipe(new ItemStack(itemChaosDoor, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', Item.eyeOfEnder,  'y', this.itemDimDoor 
                 });
        }
        if(this.enableWoodenDimDoor)
        {
    	 GameRegistry.addRecipe(new ItemStack(itemExitDoor, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', Item.enderPearl,  'y', Item.doorWood 
                 });
    	 
    	 GameRegistry.addRecipe(new ItemStack(itemExitDoor, 1), new Object[]
                 {
                     "   ", "yxy", "   ", 'x', this.itemStableFabric,  'y', Item.doorWood 
                 });
        }
        if(this.enableDimTrapDoor)
        {
    	 GameRegistry.addRecipe(new ItemStack(dimHatch, 1), new Object[]
                 {
                     " y ", " x ", " y ", 'x', Item.enderPearl,  'y', Block.trapdoor
                 });
    	 
    	 GameRegistry.addRecipe(new ItemStack(dimHatch, 1), new Object[]
                 {
                     " y ", " x ", " y ", 'x', this.itemStableFabric,  'y', Block.trapdoor
                 });
        }
        if(this.enableRiftSignature)
        {
    	 GameRegistry.addRecipe(new ItemStack(itemLinkSignature, 1), new Object[]
                 {
                     " y ", "yxy", " y ", 'x', Item.enderPearl,  'y', Item.ingotIron
                 });

    	 GameRegistry.addRecipe(new ItemStack(itemLinkSignature, 1), new Object[]
                 {
                     " y ", "yxy", " y ", 'x', this.itemStableFabric,  'y', Item.ingotIron
                 });
        }
        if(this.enableRiftRemover)
        {
    	 GameRegistry.addRecipe(new ItemStack(itemRiftRemover, 1), new Object[]
                 {
                     " y ", "yxy", " y ", 'x', Item.enderPearl,  'y', Item.ingotGold
                 });
    	 GameRegistry.addRecipe(new ItemStack(itemRiftRemover, 1), new Object[]
                 {
                     "yyy", "yxy", "yyy", 'x', this.itemStableFabric,  'y', Item.ingotGold
                 });
        }
     
    	
    	
    	
        if(this.enableRiftBlade)
        {
    	
    	 GameRegistry.addRecipe(new ItemStack(itemRiftBlade, 1), new Object[]
                 {
                     " x ", " x ", " y ", 'x', Item.enderPearl,  'y',this.itemRiftRemover
                 });
        }
        
        GameRegistry.addRecipe(new ItemStack(itemStableFabric, 4), new Object[]
                {
                    " y ", "yxy", " y ", 'x', Item.enderPearl,  'y', this.blockDimWall
                });
    	 
       
    	 GameRegistry.addRecipe(new ItemStack(itemStableFabric, 4), new Object[]
                 {
                     " y ", "yxy", " y ", 'x', Item.enderPearl,  'y', this.blockLimbo
                 });
                 
    	 GameRegistry.addRecipe(new ItemStack(this.itemStabilizedLinkSignature,1), new Object[]
                 {
                     " y ", "yxy", " y ", 'x', this.itemLinkSignature,  'y', this.itemStableFabric
                 });
    	 
    	 this.blocksImmuneToRift.add(this.blockDimWallID);
    	 this.blocksImmuneToRift.add(this.blockDimWallPermID);
    	 this.blocksImmuneToRift.add(this.dimDoorID);
    	 this.blocksImmuneToRift.add(this.ExitDoorID);
  //  	 this.blocksImmuneToRift.add(this.linkDimDoorID);
   // 	 this.blocksImmuneToRift.add(this.linkExitDoorID);
    	 this.blocksImmuneToRift.add(this.dimHatchID);
    	 this.blocksImmuneToRift.add(this.chaosDoorID);
    	 this.blocksImmuneToRift.add(this.blockRiftID);
    	 this.blocksImmuneToRift.add(this.transientDoorID);
    	 this.blocksImmuneToRift.add(Block.blockIron.blockID);
    	 this.blocksImmuneToRift.add(Block.blockDiamond.blockID);
    	 this.blocksImmuneToRift.add(Block.blockEmerald.blockID);
    	 this.blocksImmuneToRift.add(Block.blockGold.blockID);
    	 this.blocksImmuneToRift.add(Block.blockLapis.blockID);
    	 this.blocksImmuneToRift.add(Block.bedrock.blockID);
    	 
    	 dungeonHelper.registerFlipBlocks();

/**
    




 	 		**/
    	 	

    		
    	
    		
    		
    		
    		   proxy.loadTextures();
    		   proxy.registerRenderers();


    }
    
    
    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
    }
    
    @ServerStopping
    public void serverStopping(FMLServerStoppingEvent event)
    {
    	try
    	{
    	
    	dimHelper.instance.save();
    	dimHelper.instance.unregsisterDims();
    	dimHelper.dimList.clear();
    	dimHelper.blocksToDecay.clear();
    	dimHelper.instance.interDimLinkList.clear();
    	this.hasInitDims=false;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    @ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
    
    	event.registerServerCommand(removeRiftsCommand);
    	event.registerServerCommand(pruneDimsCommand);
    	event.registerServerCommand(removeAllLinksCommand);
    	event.registerServerCommand(deleteDimDataCommand);
    	event.registerServerCommand(addDungeonRift);
    	event.registerServerCommand(this.startDungeonCreation);
    	event.registerServerCommand(this.printDimData);
    	event.registerServerCommand(this.endDungeonCreation);

    	dimHelper.instance.load();
    	if(!dimHelper.dimList.containsKey(this.limboDimID))
		{
    		dimHelper.instance.dimList.put(mod_pocketDim.limboDimID, new DimData( mod_pocketDim.limboDimID,  false,  0,  new LinkData()));

		}
   
    
    }
    
    
    public static int teleTimer=0;
    
}