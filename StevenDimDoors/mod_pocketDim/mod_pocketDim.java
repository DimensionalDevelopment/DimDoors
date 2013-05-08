package StevenDimDoors.mod_pocketDim;


import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWall;
import StevenDimDoors.mod_pocketDim.blocks.BlockDimWallPerm;
import StevenDimDoors.mod_pocketDim.blocks.BlockLimbo;
import StevenDimDoors.mod_pocketDim.blocks.BlockRift;
import StevenDimDoors.mod_pocketDim.blocks.ExitDoor;
import StevenDimDoors.mod_pocketDim.blocks.dimDoor;
import StevenDimDoors.mod_pocketDim.blocks.dimHatch;
import StevenDimDoors.mod_pocketDim.blocks.linkDimDoor;
import StevenDimDoors.mod_pocketDim.blocks.linkExitDoor;
import StevenDimDoors.mod_pocketDim.commands.CommandAddDungeonRift;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteAllLinks;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteDimData;
import StevenDimDoors.mod_pocketDim.commands.CommandDeleteRifts;
import StevenDimDoors.mod_pocketDim.commands.CommandPruneDims;
import StevenDimDoors.mod_pocketDim.items.ItemChaosDoor;
import StevenDimDoors.mod_pocketDim.items.ItemRiftBlade;
import StevenDimDoors.mod_pocketDim.items.ItemStabilizedRiftSignature;
import StevenDimDoors.mod_pocketDim.items.ItemStableFabric;
import StevenDimDoors.mod_pocketDim.items.itemDimDoor;
import StevenDimDoors.mod_pocketDim.items.itemExitDoor;
import StevenDimDoors.mod_pocketDim.items.itemLinkSignature;
import StevenDimDoors.mod_pocketDim.items.itemRiftRemover;


@Mod(modid = mod_pocketDim.modid, name = "Dimensional Doors", version = mod_pocketDim.version)



@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        clientPacketHandlerSpec =
                @SidedPacketHandler(channels = {"pocketDim" }, packetHandler = ClientPacketHandler.class),
        serverPacketHandlerSpec =
                @SidedPacketHandler(channels = {"pocketDim" }, packetHandler = ServerPacketHandler.class),
                channels={"DimDoorPackets"}, packetHandler = PacketHandler.class, connectionHandler=ConnectionHandler.class)

public class mod_pocketDim
{
	

	public static final String version = "1.5.1R1.3.5RC3";
	public static final String modid = "DimensionalDoors";

	//need to clean up 
    @SidedProxy(clientSide = "StevenDimDoors.mod_pocketDimClient.ClientProxy", serverSide = "StevenDimDoors.mod_pocketDim.CommonProxy")
    public static CommonProxy proxy;

    @Instance("PocketDimensions")
    public static mod_pocketDim instance = new mod_pocketDim();
    public static SchematicLoader loader = new SchematicLoader();
    public static pocketTeleporter teleporter = new pocketTeleporter();
 
    public static final ICommand removeRiftsCommand = new CommandDeleteRifts();
    public static final ICommand pruneDimsCommand = new CommandPruneDims();
    public static final ICommand removeAllLinksCommand = new CommandDeleteAllLinks();
    public static final ICommand deleteDimDataCommand = new CommandDeleteDimData();
    public static final ICommand addDungeonRift = new CommandAddDungeonRift();

    
    public static int providerID;
    public static int dimDoorID;
    public static int ExitDoorID;
    public static int linkExitDoorID;
    public static int itemLinkSignatureID;
    public static int blockRiftID;
    public static int transientDoorID;
    public static int itemRiftBladeID;
    public static int limboExitRange;
  //  public static int railRenderID;
    
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
    public static int linkDimDoorID;
    public static int blockDimWallID;
    public static int itemRiftRemoverID;
    public static int blockDimWallPermID;
    public static Block linkDimDoor;
    public static Block transientDoor;
    public static Block ExitDoor;
    public static Block chaosDoor;
    public static Block linkExitDoor;
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

    
    
    public static PlayerRespawnTracker tracker= new PlayerRespawnTracker();
    
    public static HashMap<String,ArrayList<EntityItem>> limboSpawnInventory=new HashMap<String,ArrayList<EntityItem>>();
    
    public static ArrayList blocksImmuneToRift= new ArrayList();
	public static ArrayList<DungeonGenerator> registeredDungeons = new ArrayList<DungeonGenerator>();
	
	public static ArrayList<DungeonGenerator> simpleHalls = new ArrayList<DungeonGenerator>();

	
	public static ArrayList<DungeonGenerator> complexHalls = new ArrayList<DungeonGenerator>();

	
	public static ArrayList<DungeonGenerator> deadEnds = new ArrayList<DungeonGenerator>();

	
	public static ArrayList<DungeonGenerator> hubs = new ArrayList<DungeonGenerator>();

	
	public static ArrayList<DungeonGenerator> mazes = new ArrayList<DungeonGenerator>();

	
	public static ArrayList<DungeonGenerator> pistonTraps = new ArrayList<DungeonGenerator>();

	
	public static ArrayList<DungeonGenerator> exits = new ArrayList<DungeonGenerator>();

	public static ArrayList metadataFlipList = new ArrayList();
	public static ArrayList metadataNextList = new ArrayList();
	
	public static DungeonGenerator defaultUp = new DungeonGenerator(0, "simpleStairsUp.schematic", null);



    

    
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

    





    @PreInit
    public void PreInit(FMLPreInitializationEvent event)
    {
    	
        MinecraftForge.EVENT_BUS.register(new EventHookContainer());

        

        

         Configuration config = new Configuration(event.getSuggestedConfigurationFile());
         
         
         

         
         
         config.load();
       //  this.enableDimRail = config.get("BOOLEAN", "true to enable dim rail crafting", true).getBoolean(true);
         this.hardcoreLimbo = config.get("BOOLEAN", "true to cause player to respawn in Limbo", false).getBoolean(false);
         this.enableDimTrapDoor = config.get("BOOLEAN", "true to enable trap door crafting", true).getBoolean(true);
         this.enableIronDimDoor = config.get("BOOLEAN", "true to enable iron dim door crafting", true).getBoolean(true);
         this.enableRiftBlade = config.get("BOOLEAN", "true to enable rift blade crafting", true).getBoolean(true);
         this.enableRiftRemover = config.get("BOOLEAN", "true to enable rift remover crafting", true).getBoolean(true);
         this.enableRiftSignature = config.get("BOOLEAN", "true to enable rift signature crafting", true).getBoolean(true);
         this.enableUnstableDoor = config.get("BOOLEAN", "true to enable unstable door crafting", true).getBoolean(true);
         this.enableWoodenDimDoor = config.get("BOOLEAN", "true to enable wooden door crafting", true).getBoolean(true);
         this.enableDoorOpenGL = config.get("BOOLEAN", "Toggles the door render effect", true).getBoolean(true);
         this.returnInventory = config.get("BOOLEAN", "Toggles whether or not your inventory is returned upon dying and respawning in limbo", true).getBoolean(true);

         
     //    dimRailID = config.getBlock("Dimensional Rail", 1980).getInt();

         chaosDoorID = config.getBlock("Chaos Door", 1978).getInt();
         dimDoorID = config.getBlock("Dimensional Door", 1970).getInt();
         dimHatchID = config.getBlock("Transdimensional Trapdoor", 1971).getInt();
         linkDimDoorID= config.getBlock("Dimensional Door Link", 1972).getInt();
         blockDimWallID=config.getBlock("Fabric of Reality", 1973).getInt();
         ExitDoorID = config.getBlock("Warp Door", 1975).getInt();
         linkExitDoorID = config.getBlock("Warp Door Link", 1976).getInt();
         blockRiftID = config.getBlock("Rift", 1977).getInt();
         transientDoorID = config.getBlock("transientDoorID", 1979).getInt();

         itemStabilizedLinkSignatureID=config.getItem("Stabilized Rift Signature", 5677).getInt();
         itemRiftBladeID=config.getItem("Rift Blade", 5676).getInt();
         itemChaosDoorID=config.getItem("Chaos Door", 5673).getInt();
         itemRiftRemoverID=config.getItem("Rift Remover", 5671).getInt();
         itemStableFabricID=config.getItem("Stable Fabric", 5672).getInt();
         itemExitDoorID=config.getItem("Warp Door Item", 5673).getInt();
         itemDimDoorID=config.getItem("Dimensional Door Item", 5674).getInt();
         itemLinkSignatureID=config.getItem("Rift Signature Item", 5675).getInt();
         
       
         TNFREAKINGT = config.get("BOOLEAN", "EXPLOSIONS!!???!!!?!?!!", false).getBoolean(false);
         this.enableRiftGrief = config.get("BOOLEAN", "toggles whether rifts eat blocks around them or not", true).getBoolean(true);
         HOW_MUCH_TNT=config.get("Int", "Chance that a block will not be TNT. must be greater than 1. Explosions!?!?? must be set to true, and you figure out what it does. ", 25).getInt(25);

   
         blockLimboID=config.get("Int", "Block ID for Limbo- must be below 256", 217).getInt();
         blockDimWallPermID=config.get("Int", "Block ID for blockDimWallPermID- must be below 256", 220).getInt();
         this.limboDimID=config.get("Int", "Limbo Dimension ID", -23).getInt();
         this.limboExitRange=config.get("Int", "The farthest possible distance that limbo can send you upon return to the overworld.", 500).getInt();

         providerID=config.get("Int", "ProviderID", 12).getInt();
         this.limboProviderID=config.get("Int", "Limbo Provider ID", 13).getInt();

      
         
         this.riftsInWorldGen = config.get("BOOLEAN", "Should rifts generate natrually in the world? ", true).getBoolean(true);
         this.isLimboActive = config.get("BOOLEAN", "Toggles limbo", true).getBoolean(true);

         this.riftSpreadFactor =  config.get("Int", "How many times a rift can spread- 0 prevents rifts from spreading at all. I dont recommend putting it highter than 5, because its rather exponential. ", 3).getInt();

         
         config.save();
	
    }
    
    @Init
    public void Init(FMLInitializationEvent event)
    {
    	
    	
    	
    
        transientDoor = (new TransientDoor(transientDoorID, Material.iron)).setHardness(1.0F) .setUnlocalizedName("transientDoor");

        linkDimDoor = (new linkDimDoor(linkDimDoorID, Material.iron)).setHardness(1.0F) .setUnlocalizedName("dimDoorLink");
        blockDimWall = (new BlockDimWall(blockDimWallID, 0, Material.iron)).setLightValue(1.0F).setHardness(0.1F).setUnlocalizedName("blockDimWall");
        blockDimWallPerm = (new BlockDimWallPerm(blockDimWallPermID, 0, Material.iron)).setLightValue(1.0F).setBlockUnbreakable().setHardness(100000.0F).setUnlocalizedName("blockDimWallPerm");
        ExitDoor = (new ExitDoor(ExitDoorID, Material.wood)).setHardness(1.0F) .setUnlocalizedName("dimDoorWarp");
        linkExitDoor = (new linkExitDoor(linkExitDoorID, Material.wood)).setHardness(1.0F) .setUnlocalizedName("dimDoorexitlink");
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

        
        proxy.loadTextures();
    	proxy.registerRenderers();
    	GameRegistry.registerWorldGenerator(this.riftGen);
    	
        //GameRegistry.registerBlock(dimRail, "Dimensional Rail");
        GameRegistry.registerBlock(chaosDoor, "Unstable Door");
        GameRegistry.registerBlock(ExitDoor, "Warp Door");
        GameRegistry.registerBlock(linkExitDoor, "Warp Door link");
        GameRegistry.registerBlock(blockRift, "Rift");
        GameRegistry.registerBlock(blockLimbo, "Unraveled Fabric");
        GameRegistry.registerBlock(linkDimDoor, "Dimensional Door link");
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
        LanguageRegistry.addName(linkDimDoor, "Dimensional Door");
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

      //  GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimRail");

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");

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
    	 this.blocksImmuneToRift.add(this.linkDimDoorID);
    	 this.blocksImmuneToRift.add(this.linkExitDoorID);
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

    
	 		this.hubs.add(new DungeonGenerator(0, "4WayBasicHall.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "4WayBasicHall.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "doorTotemRuins.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "hallwayTrapRooms1.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "longDoorHallway.schematic", null));
    	 	this.hubs.add(new DungeonGenerator(0, "smallRotundaWithExit.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "fortRuins.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "4WayHallExit.schematic", null));
 	 		this.hubs.add(new DungeonGenerator(0, "4WayHallExit.schematic", null));


 	 		this.simpleHalls.add(new DungeonGenerator(0, "collapsedSingleTunnel1.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "singleStraightHall1.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "smallBranchWithExit.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "smallSimpleLeft.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "smallSimpleRight.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "simpleStairsUp.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "simpleStairsDown.schematic", null));
 	 		this.simpleHalls.add(new DungeonGenerator(0, "simpleSmallT1.schematic", null));


 	 		this.complexHalls.add(new DungeonGenerator(0, "brokenPillarsO.schematic", null));
 	 		this.complexHalls.add(new DungeonGenerator(0, "buggyTopEntry1.schematic", null));
 	 		this.complexHalls.add(new DungeonGenerator(0, "exitRuinsWithHiddenDoor.schematic", null));
 	 		this.complexHalls.add(new DungeonGenerator(0, "hallwayHiddenTreasure.schematic", null));
 	 		this.complexHalls.add(new DungeonGenerator(0, "mediumPillarStairs.schematic", null));
 	 		this.complexHalls.add(new DungeonGenerator(0, "ruinsO.schematic", null));
 	 		this.complexHalls.add(new DungeonGenerator(0, "pitStairs.schematic", null));

 	 		
 	 		this.deadEnds.add(new DungeonGenerator(0, "azersDungeonO.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "diamondTowerTemple1.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "fallingTrapO.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "hiddenStaircaseO.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "lavaTrapO.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "randomTree.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "smallHiddenTowerO.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "smallSilverfishRoom.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "tntTrapO.schematic", null));
 			this.deadEnds.add(new DungeonGenerator(0, "smallDesert.schematic", null));
 	 		this.deadEnds.add(new DungeonGenerator(0, "smallPond.schematic", null));
 	 		
 	 		
 	 		this.pistonTraps.add(new DungeonGenerator(0, "fakeTNTTrap.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "hallwayPitFallTrap.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "hallwayPitFallTrap.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "pistonFallRuins.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "pistonFloorHall.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "pistonFloorHall.schematic", null));
 	 //		this.pistonTraps.add(new DungeonGenerator(0, "pistonHallway.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "pistonSmasherHall.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "raceTheTNTHall.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "simpleDropHall.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "wallFallcomboPistonHall.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "wallFallcomboPistonHall.schematic", null));
 	 		this.pistonTraps.add(new DungeonGenerator(0, "lavaPyramid.schematic", null));

 	 	
	
 	 		this.mazes.add(new DungeonGenerator(0, "smallMaze1.schematic", null));
 	 		this.mazes.add(new DungeonGenerator(0, "smallMultilevelMaze.schematic", null));
 	 	

 	 		this.exits.add(new DungeonGenerator(0, "exitCube.schematic", null));
 	 		this.exits.add(new DungeonGenerator(0, "lockingExitHall.schematic", null));
 	 		this.exits.add(new DungeonGenerator(0, "smallExitPrison.schematic", null));
 	 		this.exits.add(new DungeonGenerator(0, "lockingExitHall.schematic", null));

 	 		this.registeredDungeons.addAll(this.simpleHalls);
 	 	 	this.registeredDungeons.addAll(this.exits);
 	 		this.registeredDungeons.addAll(this.pistonTraps);
 	 		this.registeredDungeons.addAll(this.mazes);
 	 		this.registeredDungeons.addAll(this.deadEnds);
 	 		this.registeredDungeons.addAll(this.complexHalls);
 	 		this.registeredDungeons.addAll(this.hubs);



 	 		
    	 	

    		this.metadataFlipList.add(Block.dispenser.blockID);
    		this.metadataFlipList.add(Block.stairsStoneBrick.blockID);
    		this.metadataFlipList.add(Block.lever.blockID);
    		this.metadataFlipList.add(Block.stoneButton.blockID);
    		this.metadataFlipList.add(Block.redstoneRepeaterIdle.blockID);
    		this.metadataFlipList.add(Block.redstoneRepeaterActive.blockID);
    		this.metadataFlipList.add(Block.tripWireSource.blockID);
    		this.metadataFlipList.add(Block.torchWood.blockID);
    		this.metadataFlipList.add(Block.torchRedstoneIdle.blockID);
    		this.metadataFlipList.add(Block.torchRedstoneActive.blockID);
    		this.metadataFlipList.add(Block.doorIron.blockID);
    		this.metadataFlipList.add(Block.doorWood.blockID);
    		this.metadataFlipList.add(Block.pistonBase.blockID);
    		this.metadataFlipList.add(Block.pistonStickyBase.blockID);
    		
    		this.metadataNextList.add(Block.redstoneRepeaterIdle.blockID);
    		this.metadataNextList.add(Block.redstoneRepeaterActive.blockID);


    }
    
    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
    	//dimHelper.instance.dimList.put(this.limboDimID, new DimData( this.limboDimID,  false,  0,  new LinkData()));
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

    	dimHelper.instance.load();
   
    
    }
    
    
    public static int teleTimer=0;
    
}