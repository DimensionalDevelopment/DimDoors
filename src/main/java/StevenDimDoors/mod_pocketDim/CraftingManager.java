package StevenDimDoors.mod_pocketDim;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.items.ItemDDKey;
import StevenDimDoors.mod_pocketDim.items.behaviors.DispenserBehaviorStabilizedRS;
import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingManager
{
	CraftingManager() { }
	
	public static void registerRecipes(DDProperties properties)
	{
		if (properties.CraftingStableFabricAllowed)
		{
			switch (properties.WorldThreadRequirementLevel)
			{
				case 1:
					GameRegistry.addShapelessRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						Items.ender_pearl, mod_pocketDim.itemWorldThread);
					break;
				case 2:
					GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						"yxy", 'x', Items.ender_pearl, 'y', mod_pocketDim.itemWorldThread);
					break;
				case 3:
					GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						" y ", "yxy", " y ", 'x', Items.ender_pearl, 'y', mod_pocketDim.itemWorldThread);
					break;
				default:
					GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						"yyy", "yxy", "yyy", 'x', Items.ender_pearl, 'y', mod_pocketDim.itemWorldThread);
					break;
			}
		}
		
		if (properties.CraftingDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemDimensionalDoor, 1),
				"yxy", 'x', mod_pocketDim.itemStableFabric, 'y', Items.iron_door);
		}
		if (properties.CraftingUnstableDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemUnstableDoor, 1),
				"yxy", 'x', Items.ender_eye, 'y', mod_pocketDim.itemDimensionalDoor);
		}
		if (properties.CraftingWarpDoorAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemWarpDoor, 1),
				"yxy", 'x', Items.ender_pearl, 'y', Items.wooden_door);
		}
		if (properties.CraftingTransTrapdoorAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.transTrapdoor, 1),
				"y", "x", "y", 'x', Items.ender_pearl, 'y', Blocks.trapdoor);
		}
		if (properties.CraftingRiftSignatureAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemRiftSignature, 1),
				" y ", "yxy", " y ", 'x', Items.ender_pearl, 'y', Items.iron_ingot);
		}
		if (properties.CraftingRiftRemoverAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemRiftRemover, 1),
				"yyy", "yxy", "yyy", 'x', Items.ender_pearl, 'y', Items.gold_ingot);
		}
		if (properties.CraftingRiftBladeAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemRiftBlade, 1),
				"x", "x", "y", 'x', mod_pocketDim.itemStableFabric, 'y', Items.blaze_rod);
		}
		if (properties.CraftingStabilizedRiftSignatureAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStabilizedRiftSignature, 1),
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemStableFabric, 'y', Items.iron_ingot);
		}
		if (properties.CraftingGoldenDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDimensionalDoor, 1),
				"yxy", 'x', mod_pocketDim.itemStableFabric, 'y', mod_pocketDim.itemGoldenDoor);
		}
		if (properties.CraftingGoldenDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDoor, 1),
				"yy", "yy", "yy", 'y', Items.gold_ingot);
		}
		if (properties.CraftingPersonalDimDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemPersonalDoor,1),
				"yxy", 'y', mod_pocketDim.itemQuartzDoor, 'x', mod_pocketDim.itemStableFabric);
		}
		if (properties.CraftingQuartzDoorAllowed)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(mod_pocketDim.itemQuartzDoor, new Object[]{
				"yy", "yy", "yy", Character.valueOf('y'), "oreQuartz"}));
			
			
		}
		if (properties.CraftingDDKeysAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemDDKey, 1),
				"  z", " y ", "y  ", 'y', Items.gold_ingot, 'z', Items.ender_pearl);
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemDDKey, 1),
					"z", "z", 'z', mod_pocketDim.itemDDKey);
		}
		
	}

	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event)
	{
		if(event.crafting.getItem() instanceof ItemDDKey)
		{
			ItemDDKey keyItem = (ItemDDKey) event.crafting.getItem();
			ItemStack topKey = null;
			ItemStack bottomKey = null;
			int topKeySlot = 0;
			
			for(int i = 0; i<event.craftMatrix.getSizeInventory();i++)
			{
				ItemStack slot = event.craftMatrix.getStackInSlot(i);
				if(slot!=null)
				{
					if(topKey==null)
					{
						topKey = slot;
						topKeySlot = i;
					}
					else 
					{
						bottomKey = slot;
						break;
					}
				}
			}
			DDLock.addKeys(bottomKey, DDLock.getKeys(topKey));
			event.crafting.setTagCompound(bottomKey.getTagCompound());
			event.player.inventory.addItemStackToInventory(topKey);
		}
		
	}
	
	public static void registerDispenserBehaviors()
	{
		// Register the dispenser behaviors for certain DD items
		BlockDispenser.dispenseBehaviorRegistry.putObject(mod_pocketDim.itemStabilizedRiftSignature, new DispenserBehaviorStabilizedRS());
	}
}
