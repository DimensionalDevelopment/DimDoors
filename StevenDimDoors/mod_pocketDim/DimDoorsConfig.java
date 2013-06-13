package StevenDimDoors.mod_pocketDim;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class DimDoorsConfig
{
	/**
	 * BlockIDs
	 */

	public static Property UnstableDoorID;
	public static Property DimensionalDoorID;
	public static Property WarpDoorID;
	public static Property TransTrapdoorID;
	public static Property TransientDoorID;

	public static Property FabricBlockID;
	public static Property RiftBlockID;

	/**
	 * WorldGenBlockIDs
	 */

	public static Property LimboBlockID;
	public static Property PermaFabricBlockID;


	/**
	 * ItemIDs
	 */

	public static Property RiftBladeItemID;
	public static Property RiftSignatureItemID;
	public static Property RiftRemoverItemID;
	public static Property StableFabricItemID;
	public static Property StabilizedRiftSignatureItemID;

	public static Property DimensionalDoorItemID;
	public static Property UnstableDoorItemID;
	public static Property WarpDoorItemID;

	/**
	 * Other IDs
	 */

	public static Property LimboBiomeID;
	public static Property PocketBiomeID;
	public static Property LimboDimensionID;
	public static Property limboProviderID;
	public static Property PocketProviderID;
	public static Property DoorRenderEntityID;
	public static Property MonolithEntityID;

	/**
	 * CraftingFlags
	 */
	public static Property CraftingDimensionaDoorAllowed;
	public static Property CraftingWarpDoorAllowed;
	public static Property CraftingRiftSignatureAllowed;
	public static Property CraftingRiftRemoverAllowed;
	public static Property CraftingUnstableDoorAllowed;
	public static Property CraftingRiftBladeAllowed;
	public static Property CraftingTransTrapdoorAllowed;
	public static Property CraftingStabilizedRiftSignatureAllowed;

	/**
	 * OtherFlags
	 */

	public static Property WorldRiftGenerationEnabled;
	public static Property RiftSpreadEnabled;
	public static Property RiftGriefingEnabled;
	public static Property RiftsSpawnEndermenEnabled;
	public static Property LimboEnabled;
	public static Property LimboRespawningEnabled;
	public static Property LimboReturnsInventoryEnabled;
	public static Property DoorRenderingEnabled;
	public static Property TNFREAKINGT_Enabled;

	/**
	 * Other
	 */

	public static Property NonTntWeight;
	public static Property RiftSpreadModifier;
	public static Property LimboReturnRange;

	public static void loadConfig(File configFile)
	{
		Configuration config = new Configuration(configFile);

		config.load();

		CraftingDimensionaDoorAllowed = config.get("Crafting control", "bCraftDimDoor", true);
		CraftingWarpDoorAllowed = config.get("Crafting control", "bCraftExitDoor", true);
		CraftingUnstableDoorAllowed = config.get("Crafting control", "bCraftChaosDoor", true);
		CraftingTransTrapdoorAllowed = config.get("Crafting control", "bCraftDimHatch", true);
		CraftingRiftSignatureAllowed = config.get("Crafting control", "bCraftRiftSig", true);
		CraftingRiftRemoverAllowed = config.get("Crafting control", "bCraftRiftRemover", true);
		CraftingStabilizedRiftSignatureAllowed = config.get("Crafting control", "bCraftStabilizedRiftSig", true);
		CraftingRiftBladeAllowed = config.get("Crafting control", "bCraftRiftBlade", true);

		LimboRespawningEnabled = config.get(Configuration.CATEGORY_GENERAL, "bHardcoreLimbo", false);
		LimboRespawningEnabled.comment = "True causes the player to respawn in limbo if they die in limbo";

		TNFREAKINGT_Enabled = config.get("Configuration.CATEGORY_GENERAL", "EXPLOSIONS!!???!!!?!?!!", false);

		RiftGriefingEnabled = config.get(Configuration.CATEGORY_GENERAL, "bRiftGreif", true);
		RiftGriefingEnabled.comment = "toggles whether rifts eat blocks around them or not";

		DoorRenderingEnabled = config.get(Configuration.CATEGORY_GENERAL, "bEnableDoorRender", true);

		LimboReturnsInventoryEnabled = config.get(Configuration.CATEGORY_GENERAL, "bLimboReturnInventory", true);
		LimboReturnsInventoryEnabled.comment="Toggles whether or not your inventory is returned upon dying and respawning in limbo";

		NonTntWeight=config.get(Configuration.CATEGORY_GENERAL, "HOWMUCHTNT", 25);
		NonTntWeight.comment="Chance that a block will not be TNT. must be greater than or equal to 0. Explosions!?!?? must be set to true, and you figure out what it does. ";

		MonolithEntityID=config.get(Configuration.CATEGORY_GENERAL, "monolithID", 125);

		DimensionalDoorID = config.getBlock("DimensionalDoorID", 1970);
		WarpDoorID = config.getBlock("WarpDoorID", 1975);
		UnstableDoorID = config.getBlock("UnstableDoorID", 1978);
		TransTrapdoorID = config.getBlock("TransdimensionalTrapdoorID", 1971);
		TransientDoorID = config.getBlock("TransientDoorID", 1979);
		FabricBlockID =config.getBlock("FabricOfRealityBlockID", 1973);
		RiftBlockID = config.getBlock("RiftBlockID", 1977);

		StabilizedRiftSignatureItemID=config.getItem("Stabilized Rift Signature", 5677);
		RiftBladeItemID=config.getItem("Rift Blade", 5676);
		UnstableDoorItemID=config.getItem("Chaos Door", 5673);
		RiftRemoverItemID=config.getItem("Rift Remover", 5671);
		StableFabricItemID=config.getItem("Stable Fabric", 5672);
		WarpDoorItemID=config.getItem("Warp Door Item", 5673);
		DimensionalDoorItemID=config.getItem("Dimensional Door Item", 5674);
		RiftSignatureItemID=config.getItem("Rift Signature Item", 5675);

		LimboEnabled=config.get(Configuration.CATEGORY_GENERAL, "bLimboActive", true);



		LimboBlockID=config.get("Worldgen Block IDs - must be less than 256", "blockLimbo", 217);
		PermaFabricBlockID=config.get("Worldgen Block IDs - must be less than 256", "blockFabricPerm", 220);


		LimboDimensionID=config.get(Configuration.CATEGORY_GENERAL, "limboDimID", -23);
		DoorRenderEntityID=config.get(Configuration.CATEGORY_GENERAL, "doorRenderID", 89);

		LimboReturnRange=config.get(Configuration.CATEGORY_GENERAL, "limboReturnRange", 500);
		LimboReturnRange.comment = "The farthest possible distance that limbo can send you upon return to the overworld.";

		PocketProviderID=config.get(Configuration.CATEGORY_GENERAL, "pocketProviderID", 24);

		limboProviderID=config.get(Configuration.CATEGORY_GENERAL, "limboProvider ID", 13);



		WorldRiftGenerationEnabled = config.get(Configuration.CATEGORY_GENERAL, "bWorldGenRifts", true);
		WorldRiftGenerationEnabled.comment = "Toggles the natrual generation of dungeon rifts in other dimensions";

		LimboEnabled = config.get(Configuration.CATEGORY_GENERAL, "bLimboActive", true);
		LimboEnabled.comment="Toggles if dying in a pocket dim respawns the player in limbo";

		RiftSpreadModifier =  config.get(Configuration.CATEGORY_GENERAL, "riftSpreadModifier", 3);
		RiftSpreadModifier.comment = "How many times a rift can spread- 0 prevents rifts from spreading at all. I dont recommend putting it highter than 5, because its rather exponential. ";

		LimboBiomeID=config.get(Configuration.CATEGORY_GENERAL, "limboBiomeID", 251);
		PocketBiomeID=config.get(Configuration.CATEGORY_GENERAL, "pocketBiomeID", 250);


		config.save();

		mod_pocketDim.blockDimWallID=FabricBlockID.getInt();
		mod_pocketDim.blockDimWallPermID=PermaFabricBlockID.getInt();
		mod_pocketDim.blockLimboID=LimboBlockID.getInt();
		mod_pocketDim.blockRiftID=LimboBlockID.getInt();
		mod_pocketDim.dimDoorID=DimensionalDoorID.getInt();
		mod_pocketDim.chaosDoorID=UnstableDoorID.getInt();
		mod_pocketDim.transientDoorID=TransientDoorID.getInt();
		mod_pocketDim.dimHatchID=TransTrapdoorID.getInt();
		mod_pocketDim.ExitDoorID=WarpDoorID.getInt();
		mod_pocketDim.blockRiftID=RiftBlockID.getInt();
		mod_pocketDim.DoorRenderID=DoorRenderEntityID.getInt();
		mod_pocketDim.hardcoreLimbo=LimboRespawningEnabled.getBoolean(false);
		mod_pocketDim.enableDimTrapDoor=CraftingTransTrapdoorAllowed.getBoolean(true);
		mod_pocketDim.enableDoorOpenGL=DoorRenderingEnabled.getBoolean(true);
		mod_pocketDim.enableIronDimDoor=CraftingDimensionaDoorAllowed.getBoolean(true);
		mod_pocketDim.enableRiftBlade=CraftingRiftBladeAllowed.getBoolean(true);
		mod_pocketDim.enableRiftRemover=CraftingRiftBladeAllowed.getBoolean(true);
		mod_pocketDim.enableRiftSignature=CraftingRiftSignatureAllowed.getBoolean(true);
		mod_pocketDim.enableUnstableDoor=CraftingUnstableDoorAllowed.getBoolean(true);
		mod_pocketDim.enableWoodenDimDoor=CraftingWarpDoorAllowed.getBoolean(true);
		mod_pocketDim.enableStabilizedRiftSignature=CraftingStabilizedRiftSignatureAllowed.getBoolean(true);
		mod_pocketDim.itemChaosDoorID=UnstableDoorItemID.getInt();
		mod_pocketDim.itemDimDoorID=DimensionalDoorItemID.getInt();
		mod_pocketDim.itemExitDoorID=WarpDoorItemID.getInt();
		mod_pocketDim.itemLinkSignatureID=RiftSignatureItemID.getInt();
		mod_pocketDim.itemRiftBladeID=RiftBladeItemID.getInt();
		mod_pocketDim.itemRiftRemoverID=RiftRemoverItemID.getInt();
		mod_pocketDim.itemStabilizedLinkSignatureID=StabilizedRiftSignatureItemID.getInt();
		mod_pocketDim.itemStableFabricID=StableFabricItemID.getInt();
		mod_pocketDim.obeliskID=MonolithEntityID.getInt();
		mod_pocketDim.limboBiomeID=LimboBiomeID.getInt();
		mod_pocketDim.pocketBiomeID=PocketBiomeID.getInt();
		mod_pocketDim.providerID=PocketProviderID.getInt();
		mod_pocketDim.limboProviderID=limboProviderID.getInt();
		mod_pocketDim.limboExitRange=LimboReturnRange.getInt();
		mod_pocketDim.TNFREAKINGT=TNFREAKINGT_Enabled.getBoolean(false);
		mod_pocketDim.riftsInWorldGen=WorldRiftGenerationEnabled.getBoolean(true);
		mod_pocketDim.riftSpreadFactor=RiftSpreadModifier.getInt();
		mod_pocketDim.returnInventory=LimboReturnsInventoryEnabled.getBoolean(true);
		mod_pocketDim.HOW_MUCH_TNT=NonTntWeight.getInt() + 1; //workaround so the generator code doesn't have to be changed
		mod_pocketDim.limboDimID = LimboDimensionID.getInt();
		mod_pocketDim.isLimboActive= LimboEnabled.getBoolean(true);


	}



}