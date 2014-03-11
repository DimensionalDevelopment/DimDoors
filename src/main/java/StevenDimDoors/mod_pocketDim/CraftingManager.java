package StevenDimDoors.mod_pocketDim;

import StevenDimDoors.mod_pocketDim.config.DDProperties;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.*;

public class CraftingManager
{

	public static void registerRecipes(DDProperties properties)
	{	
		if (properties.CraftingDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemDimensionalDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', mod_pocketDim.itemStableFabric,  'y', Item.doorIron 
					});
		}
		if(properties.CraftingUnstableDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemUnstableDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', Item.eyeOfEnder,  'y', mod_pocketDim.itemDimensionalDoor 
					});
		}
		if(properties.CraftingWarpDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemWarpDoor, 1), new Object[]
					{
				"   ", "yxy", "   ", 'x', mod_pocketDim.itemStableFabric,  'y', Item.doorWood 
					});
		}
		if(properties.CraftingTransTrapdoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(transTrapdoor, 1), new Object[]
					{
				" y ", " x ", " y ", 'x', mod_pocketDim.itemStableFabric, 'y', Block.trapdoor
					});
		}
		if(properties.CraftingRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftSignature, 1), new Object[]
					{
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemStableFabric,  'y', Item.ingotIron
					});
		}

		if(properties.CraftingRiftRemoverAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftRemover, 1), new Object[]
					{
				"yyy", "yxy", "yyy", 'x', mod_pocketDim.itemStableFabric, 'y', Item.ingotGold
					});
		}

		if (properties.CraftingRiftBladeAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftBlade, 1), new Object[]
					{
				" x ", " x ", " y ", 'x', mod_pocketDim.itemStableFabric, 'y', Item.blazeRod
					});
		}

		if (properties.CraftingStableFabricAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemStableFabric, 1), new Object[]
					{
				"yyy", "yxy", "yyy", 'x', Item.enderPearl,  'y', mod_pocketDim.itemWorldThread					
				});
		}
		
		if (properties.CraftingStabilizedRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStabilizedLinkSignature,1), new Object[]
					{
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemRiftSignature,  'y', mod_pocketDim.itemStableFabric
					});
		}
		if (properties.CraftingGoldenDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDimensionalDoor,1), new Object[]
					{
				"   ", "xyx", "   ", 'x', mod_pocketDim.itemGoldenDoor, 'y', Item.eyeOfEnder
					});
		}
		if (properties.CraftingGoldenDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDoor, 1), new Object[]
					{
				"yy ", "yy ", "yy ", 'y', Item.ingotGold
					});
		}
	}
	
}
