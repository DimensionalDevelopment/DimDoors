package StevenDimDoors.mod_pocketDim;

import StevenDimDoors.mod_pocketDim.config.DDProperties;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingManager
{
	private CraftingManager() { }
	
	public static void registerRecipes(DDProperties properties)
	{
		if (properties.CraftingStableFabricAllowed)
		{
			switch (properties.WorldThreadRequirementLevel)
			{
				case 1:
					GameRegistry.addShapelessRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						Item.enderPearl, mod_pocketDim.itemWorldThread);
					break;
				case 2:
					GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						"yxy", 'x', Item.enderPearl, 'y', mod_pocketDim.itemWorldThread);
					break;
				case 3:
					GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						" y ", "yxy", " y ", 'x', Item.enderPearl, 'y', mod_pocketDim.itemWorldThread);
					break;
				default:
					GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStableFabric, 1),
						"yyy", "yxy", "yyy", 'x', Item.enderPearl, 'y', mod_pocketDim.itemWorldThread);
					break;
			}
		}
		
		if (properties.CraftingDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemDimensionalDoor, 1),
				"yxy", 'x', mod_pocketDim.itemStableFabric, 'y', Item.doorIron);
		}
		if (properties.CraftingUnstableDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemUnstableDoor, 1),
				"yxy", 'x', Item.eyeOfEnder, 'y', mod_pocketDim.itemDimensionalDoor);
		}
		if (properties.CraftingWarpDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemWarpDoor, 1),
				"yxy", 'x', Item.enderPearl, 'y', Item.doorWood);
		}
		if (properties.CraftingTransTrapdoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.transTrapdoor, 1),
				"y", "x", "y", 'x', Item.enderPearl, 'y', Block.trapdoor);
		}
		if (properties.CraftingRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemRiftSignature, 1),
				" y ", "yxy", " y ", 'x', Item.enderPearl, 'y', Item.ingotIron);
		}
		if (properties.CraftingRiftRemoverAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemRiftRemover, 1),
				"yyy", "yxy", "yyy", 'x', Item.enderPearl, 'y', Item.ingotGold);
		}
		if (properties.CraftingRiftBladeAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemRiftBlade, 1),
				"x", "x", "y", 'x', mod_pocketDim.itemStableFabric, 'y', Item.blazeRod);
		}
		if (properties.CraftingStabilizedRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStabilizedRiftSignature, 1),
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemStableFabric, 'y', Item.ingotIron);
		}
		if (properties.CraftingGoldenDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDimensionalDoor, 1),
				"yxy", 'x', mod_pocketDim.itemStableFabric, 'y', mod_pocketDim.itemGoldenDoor);
		}
		if (properties.CraftingGoldenDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDoor, 1),
				"yy", "yy", "yy", 'y', Item.ingotGold);
		}
	}
	
}
