package StevenDimDoors.mod_pocketDim;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.*;

public class CraftingManager
{

	public static void registerRecipies()
	{
		Item coreCraftingItem = Item.enderPearl;
		
		if(properties.enableServerMode)
		{
			coreCraftingItem = itemWorldThread;
		}
		if (properties.CraftingDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemDimDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', coreCraftingItem,  'y', Item.doorIron 
					});

			GameRegistry.addRecipe(new ItemStack(itemDimDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', mod_pocketDim.itemStableFabric,  'y', Item.doorIron 
					});
		}
		if(properties.CraftingUnstableDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemChaosDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', Item.eyeOfEnder,  'y', mod_pocketDim.itemDimDoor 
					});
		}
		if(properties.CraftingWarpDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemExitDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', coreCraftingItem,  'y', Item.doorWood 
					});

			GameRegistry.addRecipe(new ItemStack(itemExitDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', mod_pocketDim.itemStableFabric,  'y', Item.doorWood 
					});
		}
		if(properties.CraftingTransTrapdoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(transTrapdoor, 1), new Object[]
					{
				" y ", " x ", " y ", 'x', coreCraftingItem,  'y', Block.trapdoor
					});

			GameRegistry.addRecipe(new ItemStack(transTrapdoor, 1), new Object[]
					{
				" y ", " x ", " y ", 'x', mod_pocketDim.itemStableFabric,  'y', Block.trapdoor
					});
		}
		if(properties.CraftingRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemLinkSignature, 1), new Object[]
					{
				" y ", "yxy", " y ", 'x', coreCraftingItem,  'y', Item.ingotIron
					});

			GameRegistry.addRecipe(new ItemStack(itemLinkSignature, 1), new Object[]
					{
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemStableFabric,  'y', Item.ingotIron
					});
		}

		if(properties.CraftingRiftRemoverAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftRemover, 1), new Object[]
					{
				" y ", "yxy", " y ", 'x', coreCraftingItem,  'y', Item.ingotGold
					});
			GameRegistry.addRecipe(new ItemStack(itemRiftRemover, 1), new Object[]
					{
				"yyy", "yxy", "yyy", 'x', mod_pocketDim.itemStableFabric,  'y', Item.ingotGold
					});
		}

		if (properties.CraftingRiftBladeAllowed)
		{

			GameRegistry.addRecipe(new ItemStack(itemRiftBlade, 1), new Object[]
					{
				" x ", " x ", " y ", 'x', coreCraftingItem,  'y',mod_pocketDim.itemRiftRemover
					});
		}

		if (properties.CraftingStableFabricAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemStableFabric, 1), new Object[]
					{
				"yyy", "yxy", "yyy", 'x', coreCraftingItem,  'y', mod_pocketDim.itemWorldThread
					});
		}
		
		if (properties.CraftingStabilizedRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStabilizedLinkSignature,1), new Object[]
					{
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemLinkSignature,  'y', mod_pocketDim.itemStableFabric
					});
		}
		if (properties.CraftingGoldDimDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldDimDoor,1), new Object[]
					{
				" x ", " y ", " x ", 'x', mod_pocketDim.itemGoldDoor,  'y', Item.eyeOfEnder
					});
		}
		if (properties.CraftingGoldDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldDoor,1), new Object[]
					{
				"yy ", "yy ", "yy ", 'y', Item.ingotGold
					});
			
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldDoor,1), new Object[]
					{
				" yy", " yy", " yy", 'y', Item.ingotGold
					});
		}
		
	}

	
}
