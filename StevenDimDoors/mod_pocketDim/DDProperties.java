package StevenDimDoors.mod_pocketDim;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class DDProperties
{
	/**
	 * Block IDs
	 */

	public final int UnstableDoorID;
	public final int DimensionalDoorID;
	public final int WarpDoorID;
	public final int TransTrapdoorID;
	public final int TransientDoorID;
	public final int FabricBlockID;
	public final int RiftBlockID;

	/**
	 * World Generation Block IDs
	 */

	public final int LimboBlockID;
	public final int PermaFabricBlockID;

	/**
	 * Item IDs
	 */

	public final int RiftBladeItemID;
	public final int RiftSignatureItemID;
	public final int RiftRemoverItemID;
	public final int StableFabricItemID;
	public final int StabilizedRiftSignatureItemID;
	public final int DimensionalDoorItemID;
	public final int UnstableDoorItemID;
	public final int WarpDoorItemID;

	/**
	 * Other IDs
	 */

	public final int LimboBiomeID;
	public final int PocketBiomeID;
	public final int LimboDimensionID;
	public final int LimboProviderID;
	public final int PocketProviderID;
	public final int DoorRenderEntityID;
	public final int MonolithEntityID;

	/**
	 * Crafting Flags
	 */
	
	public final boolean CraftingDimensionaDoorAllowed;
	public final boolean CraftingWarpDoorAllowed;
	public final boolean CraftingRiftSignatureAllowed;
	public final boolean CraftingRiftRemoverAllowed;
	public final boolean CraftingUnstableDoorAllowed;
	public final boolean CraftingRiftBladeAllowed;
	public final boolean CraftingTransTrapdoorAllowed;
	public final boolean CraftingStabilizedRiftSignatureAllowed;
	public final boolean CraftingStableFabricAllowed;
	
	/**
	 * Loot Flags
	 */
	
	public final boolean DimensionalDoorLootEnabled;
	public final boolean WarpDoorLootEnabled;
	public final boolean UnstableDoorLootEnabled;
	public final boolean TransTrapdoorLootEnabled;
	public final boolean RiftSignatureLootEnabled;
	public final boolean RiftRemoverLootEnabled;
	public final boolean StabilizedRiftSignatureLootEnabled;
	public final boolean RiftBladeLootEnabled;
	public final boolean StableFabricLootEnabled;
	public final boolean FabricOfRealityLootEnabled;

	/**
	 * Other Flags
	 */

	public final boolean WorldRiftGenerationEnabled;
	public final boolean RiftSpreadEnabled;
	public final boolean RiftGriefingEnabled;
	public final boolean RiftsSpawnEndermenEnabled;
	public final boolean LimboEnabled;
	public final boolean HardcoreLimboEnabled;
	public final boolean LimboReturnsInventoryEnabled;
	public final boolean DoorRenderingEnabled;
	public final boolean TNFREAKINGT_Enabled;

	/**
	 * Other
	 */

	public final int NonTntWeight;
	public final int RiftSpreadModifier;
	public final int LimboReturnRange;
	public final String CustomSchematicDirectory;
	
	//Singleton instance
	private static DDProperties instance = null;
	//Path for custom dungeons within configuration directory
	private final String CUSTOM_SCHEMATIC_SUBDIRECTORY = "/DimDoors_Custom_schematics";
	//Names of categories
	private final String CATEGORY_CRAFTING = "crafting";
	private final String CATEGORY_ENTITY = "entity";
	private final String CATEGORY_DIMENSION = "dimension";
	private final String CATEGORY_PROVIDER = "provider";
	private final String CATEGORY_BIOME = "biome";
	
	private DDProperties(File configFile)
	{
		//Load the configuration. This must be done in the constructor, even though I'd rather have a separate
		//function, because "blank final" variables must be initialized within the constructor.
		
		CustomSchematicDirectory = configFile.getParent() + CUSTOM_SCHEMATIC_SUBDIRECTORY;
		Configuration config = new Configuration(configFile);
		config.load();

		CraftingDimensionaDoorAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Dimensional Door", true).getBoolean(true);
		CraftingWarpDoorAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Warp Door", true).getBoolean(true);
		CraftingUnstableDoorAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Unstable Door", true).getBoolean(true);
		CraftingTransTrapdoorAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Transdimensional Trapdoor", true).getBoolean(true);
		CraftingRiftSignatureAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Rift Signature", true).getBoolean(true);
		CraftingRiftRemoverAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Rift Remover", true).getBoolean(true);
		CraftingStabilizedRiftSignatureAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Stabilized Rift Signature", true).getBoolean(true);
		CraftingRiftBladeAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Rift Blade", true).getBoolean(true);
		CraftingStableFabricAllowed = config.get(CATEGORY_CRAFTING, "Allow Crafting Stable Fabric", true).getBoolean(true);
		
		DimensionalDoorLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Dimensional Door Loot", true).getBoolean(true);
		WarpDoorLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Warp Door Loot", false).getBoolean(false);
		UnstableDoorLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Unstable Door Loot", false).getBoolean(false);
		TransTrapdoorLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Transdimensional Trapdoor Loot", false).getBoolean(false);
		RiftSignatureLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Rift Signature Loot", true).getBoolean(true);
		RiftRemoverLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Rift Remover Loot", true).getBoolean(true);
		StabilizedRiftSignatureLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Stabilized Rift Signature Loot", false).getBoolean(false);
		RiftBladeLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Rift Blade Loot", true).getBoolean(true);
		StableFabricLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Stable Fabric Loot", false).getBoolean(false);
		FabricOfRealityLootEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Fabric of Reality Loot", true).getBoolean(true);

		RiftGriefingEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Rift Griefing", true,
				"Sets whether rifts destroy blocks around them or not").getBoolean(true);
		RiftSpreadEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Rift Spread", true,
				"Sets whether rifts create more rifts when they are near other rifts").getBoolean(true);
		RiftsSpawnEndermenEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Endermen Spawning from Rifts", true,
				"Sets whether groups of connected rifts will spawn Endermen").getBoolean(true);
		
		LimboEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Limbo", true,
				"Sets whether the Limbo dimension is activated").getBoolean(true);
		LimboReturnsInventoryEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Limbo Returns Inventory", true,
				"Sets whether players keep their inventories upon dying and respawning in Limbo").getBoolean(true);
		HardcoreLimboEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Hardcore Limbo", false,
				"Sets whether players that die in Limbo will respawn there").getBoolean(false);
		LimboReturnRange = config.get(Configuration.CATEGORY_GENERAL, "Limbo Return Range", 500,
				"Sets the farthest distance that Limbo can send you upon returning to the Overworld").getInt();
		DoorRenderingEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Door Rendering", true).getBoolean(true);

		TNFREAKINGT_Enabled = config.get(Configuration.CATEGORY_GENERAL, "EXPLOSIONS!!???!!!?!?!!", false).getBoolean(false);
		NonTntWeight = config.get(Configuration.CATEGORY_GENERAL, "HOWMUCHTNT", 25, 
				"Weighs the chance that a block will not be TNT. Must be greater than or equal to 0. " +
				"EXPLOSIONS must be set to true for this to have any effect.").getInt();

		DoorRenderEntityID=config.get(CATEGORY_ENTITY, "Door Render Entity ID", 89).getInt();
		MonolithEntityID = config.get(CATEGORY_ENTITY, "Monolith Entity ID", 125).getInt();

		DimensionalDoorID = config.getBlock("Dimensional Door Block ID", 1970).getInt();
		TransTrapdoorID = config.getBlock("Transdimensional Trapdoor Block ID", 1971).getInt();
		FabricBlockID =config.getBlock("Fabric Of Reality Block ID", 1973).getInt();
		WarpDoorID = config.getBlock("Warp Door Block ID", 1975).getInt();
		RiftBlockID = config.getBlock("Rift Block ID", 1977).getInt();
		UnstableDoorID = config.getBlock("Unstable Door Block ID", 1978).getInt();
		TransientDoorID = config.getBlock("Transient Door Block ID", 1979).getInt();

		WarpDoorItemID = config.getItem("Warp Door Item ID", 5670).getInt();
		RiftRemoverItemID = config.getItem("Rift Remover Item ID", 5671).getInt();
		StableFabricItemID = config.getItem("Stable Fabric Item ID", 5672).getInt();
		UnstableDoorItemID = config.getItem("Unstable Door Item ID", 5673).getInt();
		DimensionalDoorItemID = config.getItem("Dimensional Door Item ID", 5674).getInt();
		RiftSignatureItemID = config.getItem("Rift Signature Item ID", 5675).getInt();
		RiftBladeItemID = config.getItem("Rift Blade Item ID", 5676).getInt();
		StabilizedRiftSignatureItemID = config.getItem("Stabilized Rift Signature Item ID", 5677).getInt();

		LimboBlockID = config.getTerrainBlock("World Generation Block IDs - must be less than 256", "Limbo Block ID", 217,
				"Blocks used for the terrain in Limbo").getInt();
		PermaFabricBlockID = config.getTerrainBlock("World Generation Block IDs - must be less than 256",
				"Perma Fabric Block ID", 220, "Blocks used for enclosing pocket dimensions").getInt();

		LimboDimensionID = config.get(CATEGORY_DIMENSION, "Limbo Dimension ID", -23).getInt();
		PocketProviderID = config.get(CATEGORY_PROVIDER, "Pocket Provider ID", 24).getInt();
		LimboProviderID = config.get(CATEGORY_PROVIDER, "Limbo Provider ID", 13).getInt();

		WorldRiftGenerationEnabled = config.get(Configuration.CATEGORY_GENERAL, "Enable Rift World Generation", true,
				"Sets whether dungeon rifts generate in dimensions other than Limbo").getBoolean(true);

		RiftSpreadModifier = config.get(Configuration.CATEGORY_GENERAL, "Rift Spread Modifier", 3,
				"Sets the number of times a rift can spread. 0 prevents rifts from spreading at all. " +
				"A value greater than 5 is not recommended as the growth is exponential.").getInt();

		LimboBiomeID = config.get(CATEGORY_BIOME, "Limbo Biome ID", 251).getInt();
		PocketBiomeID = config.get(CATEGORY_BIOME, "Pocket Biome ID", 250).getInt();

		config.save();
	}
	
	public static DDProperties create(File configFile)
	{
		if (instance == null)
			instance = new DDProperties(configFile);
		else
			throw new IllegalStateException("Cannot create DDProperties twice");
		
		return instance;
	}
	
	public static DDProperties instance()
	{
		if (instance == null)
		{
			//This is to prevent some frustrating bugs that could arise when classes
			//are loaded in the wrong order. Trust me, I had to squash a few...
			throw new IllegalStateException("Instance of DDProperties requested before properties have been loaded");
		}
		return instance;
	}
}