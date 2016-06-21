package com.zixiken.dimdoors;


import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DDLock;
import com.zixiken.dimdoors.items.behaviors.DispenserBehaviorStabilizedRS;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import com.zixiken.dimdoors.items.ItemDDKey;

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
					GameRegistry.addShapelessRecipe(new ItemStack(DimDoors.itemStableFabric, 1),
						Items.ender_pearl, DimDoors.itemWorldThread);
					break;
				case 2:
					GameRegistry.addRecipe(new ItemStack(DimDoors.itemStableFabric, 1),
						"yxy", 'x', Items.ender_pearl, 'y', DimDoors.itemWorldThread);
					break;
				case 3:
					GameRegistry.addRecipe(new ItemStack(DimDoors.itemStableFabric, 1),
						" y ", "yxy", " y ", 'x', Items.ender_pearl, 'y', DimDoors.itemWorldThread);
					break;
				default:
					GameRegistry.addRecipe(new ItemStack(DimDoors.itemStableFabric, 1),
						"yyy", "yxy", "yyy", 'x', Items.ender_pearl, 'y', DimDoors.itemWorldThread);
					break;
			}
		}
		
		if (properties.CraftingDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemDimensionalDoor, 1),
				"yxy", 'x', DimDoors.itemStableFabric, 'y', Items.iron_door);
		}
		if (properties.CraftingUnstableDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemUnstableDoor, 1),
				"yxy", 'x', Items.ender_eye, 'y', DimDoors.itemDimensionalDoor);
		}
		if (properties.CraftingWarpDoorAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(DimDoors.itemWarpDoor, 1),
				"yxy", 'x', Items.ender_pearl, 'y', Items.oak_door);
		}
		if (properties.CraftingTransTrapdoorAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(DimDoors.transTrapdoor, 1),
				"y", "x", "y", 'x', Items.ender_pearl, 'y', Blocks.trapdoor);
		}
		if (properties.CraftingRiftSignatureAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(DimDoors.itemRiftSignature, 1),
				" y ", "yxy", " y ", 'x', Items.ender_pearl, 'y', Items.iron_ingot);
		}
		if (properties.CraftingRiftRemoverAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(DimDoors.itemRiftRemover, 1),
				"yyy", "yxy", "yyy", 'x', Items.ender_pearl, 'y', Items.gold_ingot);
		}
		if (properties.CraftingRiftBladeAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemRiftBlade, 1),
				"x", "x", "y", 'x', DimDoors.itemStableFabric, 'y', Items.blaze_rod);
		}
		if (properties.CraftingStabilizedRiftSignatureAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(DimDoors.itemStabilizedRiftSignature, 1),
				" y ", "yxy", " y ", 'x', DimDoors.itemStableFabric, 'y', Items.iron_ingot);
		}
		if (properties.CraftingGoldenDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemGoldenDimensionalDoor, 1),
				"yxy", 'x', DimDoors.itemStableFabric, 'y', DimDoors.itemGoldenDoor);
		}
		if (properties.CraftingGoldenDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemGoldenDoor, 1),
				"yy", "yy", "yy", 'y', Items.gold_ingot);
		}
		if (properties.CraftingPersonalDimDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemPersonalDoor,1),
				"yxy", 'y', DimDoors.itemQuartzDoor, 'x', DimDoors.itemStableFabric);
		}
		if (properties.CraftingQuartzDoorAllowed)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(DimDoors.itemQuartzDoor, new Object[]{
				"yy", "yy", "yy", Character.valueOf('y'), "oreQuartz"}));
			
			
		}
		if (properties.CraftingDDKeysAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemDDKey, 1),
				"  z", " y ", "y  ", 'y', Items.gold_ingot, 'z', Items.ender_pearl);
			GameRegistry.addRecipe(new ItemStack(DimDoors.itemDDKey, 1),
					"z", "z", 'z', DimDoors.itemDDKey);
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
		BlockDispenser.dispenseBehaviorRegistry.putObject(DimDoors.itemStabilizedRiftSignature, new DispenserBehaviorStabilizedRS());
	}
}
