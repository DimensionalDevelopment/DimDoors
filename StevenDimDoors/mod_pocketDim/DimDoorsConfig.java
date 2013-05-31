package StevenDimDoors.mod_pocketDim;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class DimDoorsConfig
{
	/**
	 * BlockIDs
	 */
	
	public static Property chaosDoor;
	public static Property dimDoor;
	public static Property exitDoor;
	public static Property dimHatch;
	public static Property transientDoor;
	
	public static Property blockFabric;
	public static Property blockRift;

	
	
	/**
	 * WorldGenBlockIDs
	 */
	
	public static Property blockLimbo;
	public static Property blockFabricPerm;
	

	
	/**
	 * ItemIDs
	 */
	
	public static Property itemRiftBlade;
	public static Property itemRiftSignature;
	public static Property itemRiftRemover;
	public static Property itemStableFabric;
	public static Property itemStabilizedRiftSignature;
	
	public static Property itemDimDoor;
	public static Property itemChaosDoor;
	public static Property itemExitDoor;
	
	
	/**
	 * Other IDs
	 */
	
	public static Property limboBiomeID;
	public static Property pocketBiomeID;
	public static Property limboDimID;
	public static Property limboProviderID;
	public static Property pocketProviderID;
	public static Property doorRenderID;
	public static Property monolithID;

	
	/**
	 * CraftingFlags
	 */
	public static Property bCraftDimDoor;
	public static Property bCraftExitDoor;
	public static Property bCraftRiftSig;
	public static Property bCraftRiftRemover;
	public static Property bCraftUnstableDoor;
	public static Property bCraftRiftBlade;
	public static Property bCraftDimHatch;
	public static Property bCraftChaosDoor;

	/**
	 * OtherFlags
	 */
	public static Property bWorldGenRifts;
	public static Property bRiftSpread;
	public static Property bRiftGreif;
	public static Property bRiftsSpawnEndermen;
	public static Property bLimboActive;
	public static Property bHardcoreLimbo;
	public static Property bLimboReturnInventory;
	public static Property bEnableDoorRender;
	public static Property bTNFREAKINGT;
	
	
	/**
	 * Other
	 */
	
	public static Property HOWMUCHTNT;
	public static Property riftSpreadModifier;
	public static Property limboReturnRange;
	

	
	public static void loadConfig(File configFile)
	{
		Configuration config = new Configuration(configFile);

	

		
	     config.load();
	     
	     bCraftDimHatch = config.get("Crafting control", "bCraftDimHatch", true);
         
         bCraftDimDoor = config.get("Crafting control", "bCraftDimDoor", true);
         
         bCraftRiftBlade = config.get("Crafting control", "bCraftRiftBlade", true);
         
         bCraftRiftRemover = config.get("Crafting control", "bCraftRiftRemover", true);
         
         bCraftRiftSig = config.get("Crafting control", "bCraftRiftSig", true);
         
         bCraftChaosDoor = config.get("Crafting control", "bCraftChaosDoor", true);
         
         bCraftExitDoor = config.get("Crafting control", "bCraftExitDoor", true);
         
         
         
	         bHardcoreLimbo = config.get(Configuration.CATEGORY_GENERAL, "bHardcoreLimbo", false);
	         bHardcoreLimbo.comment = "True causes the player to respawn in limbo if they die in limbo";
	         
	       
	         bTNFREAKINGT = config.get("Configuration.CATEGORY_GENERAL", "EXPLOSIONS!!???!!!?!?!!", false);
	         
	         bRiftGreif = config.get(Configuration.CATEGORY_GENERAL, "bRiftGreif", true);
	         bRiftGreif.comment = "toggles whether rifts eat blocks around them or not";
	         
	         bEnableDoorRender = config.get(Configuration.CATEGORY_GENERAL, "bEnableDoorRender", true);
	         
	         bLimboReturnInventory = config.get(Configuration.CATEGORY_GENERAL, "bLimboReturnInventory", true);
	         bLimboReturnInventory.comment="Toggles whether or not your inventory is returned upon dying and respawning in limbo";
	         
	         HOWMUCHTNT=config.get(Configuration.CATEGORY_GENERAL, "HOWMUCHTNT", 25);
	         HOWMUCHTNT.comment="Chance that a block will not be TNT. must be greater than 1. Explosions!?!?? must be set to true, and you figure out what it does. ";
	         
	         monolithID=config.get(Configuration.CATEGORY_GENERAL, "monolithID", 125);

	         
	     //    dimRailID = config.getBlock("Dimensional Rail", 1980).getInt();

	         chaosDoor = config.getBlock("Chaos Door", 1978);
	         dimDoor = config.getBlock("Dimensional Door", 1970);
	         dimHatch = config.getBlock("Transdimensional Trapdoor", 1971);
	         blockFabric=config.getBlock("Fabric of Reality", 1973);
	         exitDoor = config.getBlock("Warp Door", 1975);
	         blockRift = config.getBlock("Rift", 1977);
	         transientDoor = config.getBlock("transientDoorID", 1979);

	         itemStabilizedRiftSignature=config.getItem("Stabilized Rift Signature", 5677);
	         itemRiftBlade=config.getItem("Rift Blade", 5676);
	         itemChaosDoor=config.getItem("Chaos Door", 5673);
	         itemRiftRemover=config.getItem("Rift Remover", 5671);
	         itemStableFabric=config.getItem("Stable Fabric", 5672);
	         itemExitDoor=config.getItem("Warp Door Item", 5673);
	         itemDimDoor=config.getItem("Dimensional Door Item", 5674);
	         itemRiftSignature=config.getItem("Rift Signature Item", 5675);
	         
	         bLimboActive=config.get(Configuration.CATEGORY_GENERAL, "bLimboActive", true);
	      
	       

	         blockLimbo=config.get("Worldgen Block IDs - must be less than 256", "blockLimbo", 217);
	         blockFabricPerm=config.get("Worldgen Block IDs - must be less than 256", "blockFabricPerm", 220);
	       
	         
	         limboDimID=config.get(Configuration.CATEGORY_GENERAL, "limboDimID", -23);
	         doorRenderID=config.get(Configuration.CATEGORY_GENERAL, "doorRenderID", 89);

	         limboReturnRange=config.get(Configuration.CATEGORY_GENERAL, "limboReturnRange", 500);
	         limboReturnRange.comment = "The farthest possible distance that limbo can send you upon return to the overworld.";
	         
	         pocketProviderID=config.get(Configuration.CATEGORY_GENERAL, "pocketProviderID", 12);
	         
	         limboProviderID=config.get(Configuration.CATEGORY_GENERAL, "limboProvider ID", 13);

	      
	         
	         bWorldGenRifts = config.get(Configuration.CATEGORY_GENERAL, "bWorldGenRifts", true);
	         bWorldGenRifts.comment = "Toggles the natrual generation of dungeon rifts other dimensions";
	         
	         bLimboActive = config.get(Configuration.CATEGORY_GENERAL, "bLimboActive", true);
	         bLimboActive.comment="Toggles if dying in a pocket dim respawns the player in limbo";

	         riftSpreadModifier =  config.get(Configuration.CATEGORY_GENERAL, "riftSpreadModifier", 3);
	         riftSpreadModifier.comment = "How many times a rift can spread- 0 prevents rifts from spreading at all. I dont recommend putting it highter than 5, because its rather exponential. ";
	         
	         limboBiomeID=config.get(Configuration.CATEGORY_GENERAL, "limboBiomeID", 217);
	         pocketBiomeID=config.get(Configuration.CATEGORY_GENERAL, "pocketBiomeID", 218);
	         
	         
	         config.save();
	         
	         
		config.save();
		
		mod_pocketDim.blockDimWallID=blockFabric.getInt();
		mod_pocketDim.blockDimWallPermID=blockFabricPerm.getInt();
		mod_pocketDim.blockLimboID=blockLimbo.getInt();
		mod_pocketDim.blockRiftID=blockLimbo.getInt();
		mod_pocketDim.dimDoorID=dimDoor.getInt();
		mod_pocketDim.chaosDoorID=chaosDoor.getInt();
		mod_pocketDim.transientDoorID=transientDoor.getInt();
		mod_pocketDim.dimHatchID=dimHatch.getInt();
		mod_pocketDim.ExitDoorID=exitDoor.getInt();
		mod_pocketDim.blockRiftID=blockRift.getInt();
		mod_pocketDim.DoorRenderID=doorRenderID.getInt();
		mod_pocketDim.hardcoreLimbo=bHardcoreLimbo.getBoolean(false);
		mod_pocketDim.enableDimTrapDoor=bCraftDimHatch.getBoolean(true);
		mod_pocketDim.enableDoorOpenGL=bEnableDoorRender.getBoolean(true);
		mod_pocketDim.enableIronDimDoor=bCraftDimDoor.getBoolean(true);
		mod_pocketDim.enableRiftBlade=bCraftRiftBlade.getBoolean(true);
		mod_pocketDim.enableRiftRemover=bCraftRiftBlade.getBoolean(true);
		mod_pocketDim.enableRiftSignature=bCraftRiftSig.getBoolean(true);
		mod_pocketDim.enableUnstableDoor=bCraftRiftSig.getBoolean(true);
		mod_pocketDim.enableWoodenDimDoor=bCraftExitDoor.getBoolean(true);
		mod_pocketDim.itemChaosDoorID=itemChaosDoor.getInt();
		mod_pocketDim.itemDimDoorID=itemDimDoor.getInt();
		mod_pocketDim.itemExitDoorID=itemExitDoor.getInt();
		mod_pocketDim.itemLinkSignatureID=itemRiftSignature.getInt();
		mod_pocketDim.itemRiftBladeID=itemRiftBlade.getInt();
		mod_pocketDim.itemRiftRemoverID=itemRiftRemover.getInt();
		mod_pocketDim.itemStabilizedLinkSignatureID=itemStabilizedRiftSignature.getInt();
		mod_pocketDim.itemStableFabricID=itemStableFabric.getInt();
		mod_pocketDim.obeliskID=monolithID.getInt();
		mod_pocketDim.limboBiomeID=limboBiomeID.getInt();
		mod_pocketDim.pocketBiomeID=pocketBiomeID.getInt();
		mod_pocketDim.providerID=pocketProviderID.getInt();
		mod_pocketDim.limboProviderID=limboProviderID.getInt();
		mod_pocketDim.limboExitRange=limboReturnRange.getInt();
		mod_pocketDim.TNFREAKINGT=bTNFREAKINGT.getBoolean(false);
		mod_pocketDim.riftsInWorldGen=bWorldGenRifts.getBoolean(true);
		mod_pocketDim.riftSpreadFactor=riftSpreadModifier.getInt();
		mod_pocketDim.returnInventory=bLimboReturnInventory.getBoolean(true);
		mod_pocketDim.HOW_MUCH_TNT=HOWMUCHTNT.getInt();
		mod_pocketDim.limboDimID = limboDimID.getInt();
		
		
}
	
	
	
}