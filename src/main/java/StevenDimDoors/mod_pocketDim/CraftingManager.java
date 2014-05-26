package StevenDimDoors.mod_pocketDim;

import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemDimensionalDoor;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemRiftBlade;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemRiftRemover;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemRiftSignature;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemStableFabric;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemUnstableDoor;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.itemWarpDoor;
import static StevenDimDoors.mod_pocketDim.mod_pocketDim.transTrapdoor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.items.ItemDDKey;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingManager implements ICraftingHandler
{
	CraftingManager() { }
	
	public static void registerRecipes(DDProperties properties)
	{
		if (properties.CraftingStableFabricAllowed)
		{
			switch (properties.WorldThreadRequirementLevel)
			{
				case 1:
					GameRegistry.addShapelessRecipe(new ItemStack(itemStableFabric, 1),
						Item.enderPearl, mod_pocketDim.itemWorldThread);
					break;
				case 2:
					GameRegistry.addRecipe(new ItemStack(itemStableFabric, 1),
						"yxy", 'x', Item.enderPearl, 'y', mod_pocketDim.itemWorldThread);
					break;
				case 3:
					GameRegistry.addRecipe(new ItemStack(itemStableFabric, 1),
						" y ", "yxy", " y ", 'x', Item.enderPearl, 'y', mod_pocketDim.itemWorldThread);
					break;
				default:
					GameRegistry.addRecipe(new ItemStack(itemStableFabric, 1),
						"yyy", "yxy", "yyy", 'x', Item.enderPearl, 'y', mod_pocketDim.itemWorldThread);
					break;
			}
		}
		
		if (properties.CraftingDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemDimensionalDoor, 1),
				"yxy", 'x', mod_pocketDim.itemStableFabric, 'y', Item.doorIron);
		}
		if (properties.CraftingUnstableDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemUnstableDoor, 1),
				"yxy", 'x', Item.eyeOfEnder, 'y', mod_pocketDim.itemDimensionalDoor);
		}
		if (properties.CraftingWarpDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemWarpDoor, 1),
				"yxy", 'x', Item.enderPearl, 'y', Item.doorWood);
		}
		if (properties.CraftingTransTrapdoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(transTrapdoor, 1),
				"y", "x", "y", 'x', Item.enderPearl, 'y', Block.trapdoor);
		}
		if (properties.CraftingRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftSignature, 1),
				" y ", "yxy", " y ", 'x', Item.enderPearl, 'y', Item.ingotIron);
		}
		if (properties.CraftingRiftRemoverAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftRemover, 1),
				"yyy", "yxy", "yyy", 'x', Item.enderPearl, 'y', Item.ingotGold);
		}
		if (properties.CraftingRiftBladeAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(itemRiftBlade, 1),
				"x", "x", "y", 'x', mod_pocketDim.itemStableFabric, 'y', Item.blazeRod);
		}
		if (properties.CraftingStabilizedRiftSignatureAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemStabilizedLinkSignature,1),
				" y ", "yxy", " y ", 'x', mod_pocketDim.itemRiftSignature, 'y', mod_pocketDim.itemWorldThread);
		}
		if (properties.CraftingGoldenDimensionalDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDimensionalDoor,1),
				"yxy", 'y', mod_pocketDim.itemGoldenDoor, 'x', mod_pocketDim.itemStableFabric);
		}
		if (properties.CraftingGoldenDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemGoldenDoor, 1),
				"yy", "yy", "yy", 'y', Item.ingotGold);
		}
		if (properties.CraftingPersonalDimDoorAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemPersonalDoor,1),
				"yxy", 'y', mod_pocketDim.itemGoldenDoor, 'x', mod_pocketDim.itemStableFabric);
		}
		if (properties.CraftingQuartzDoorAllowed)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(mod_pocketDim.itemQuartzDoor, new Object[]{
				"yy", "yy", "yy", Character.valueOf('y'), "oreQuartz"}));
			
			
		}
		if (properties.CraftingDDKeysAllowed)
		{
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemDDKey, 1),
				"  z", " y ", "y  ", 'y', Item.ingotGold, 'z', Item.enderPearl);
			GameRegistry.addRecipe(new ItemStack(mod_pocketDim.itemDDKey, 1),
					"z", "z", 'z', mod_pocketDim.itemDDKey);
		}
		
	}

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix)
	{
		if(item.getItem() instanceof ItemDDKey)
		{
			ItemDDKey keyItem = (ItemDDKey) item.getItem();
			ItemStack topKey = null;
			ItemStack bottomKey = null;
			int topKeySlot = 0;
			
			for(int i = 0; i<craftMatrix.getSizeInventory();i++)
			{
				ItemStack slot = craftMatrix.getStackInSlot(i);
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
			item.setTagCompound(bottomKey.getTagCompound());
			player.inventory.addItemStackToInventory(topKey);
		}
		
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item)
	{
		// TODO Auto-generated method stub
		
	}
	
}
