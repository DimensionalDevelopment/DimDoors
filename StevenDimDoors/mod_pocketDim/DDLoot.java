package StevenDimDoors.mod_pocketDim;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

/*
 * Registers a category of loot chests for Dimensional Doors in Forge.
 */
public class DDLoot {
	
	//These are the categories of loot to be merged into our chests
	static final String[] chestSources = new String[] {
			ChestGenHooks.MINESHAFT_CORRIDOR, 
		    ChestGenHooks.PYRAMID_DESERT_CHEST,
		    ChestGenHooks.PYRAMID_JUNGLE_CHEST,
		    ChestGenHooks.STRONGHOLD_CORRIDOR,
		    ChestGenHooks.STRONGHOLD_CROSSING,
		    ChestGenHooks.VILLAGE_BLACKSMITH,
		    ChestGenHooks.DUNGEON_CHEST
		};
	
	public static final String DIMENSIONAL_DUNGEON_CHEST = "dimensionalDungeonChest";
	public static ChestGenHooks DungeonChestInfo = null;
	private static final int CHEST_SIZE = 5;
	
	private static final int COMMON_LOOT_WEIGHT = 10; //As common as iron ingots
	private static final int UNCOMMON_LOOT_WEIGHT = 5; //As common as iron armor loot
	private static final int RARE_LOOT_WEIGHT = 3; //As common as diamonds
	private static final int DUNGEON_CHEST_WEIGHT_INFLATION = 10; // (weight of iron ingots in dungeon) / (weight of iron ingots in other chests)
	
	public static void registerInfo()
	{
		DDProperties properties = DDProperties.instance();
		
		//Register the dimensional dungeon chest with ChestGenHooks. This isn't necessary, but allows
		//other mods to add their own loot to our chests if they know our loot category, without having
		//to interface with our code.
		DungeonChestInfo = ChestGenHooks.getInfo(DIMENSIONAL_DUNGEON_CHEST);
		DungeonChestInfo.setMin(CHEST_SIZE);
		DungeonChestInfo.setMax(CHEST_SIZE);

		//Merge the item lists from source chests
		//This means chests will include future loot as Minecraft updates! ^_^
		ArrayList<WeightedRandomChestContent> items = mergeCategories(chestSources);
		
		//Add any enabled DD loot to the list of items
		addContent(properties.FabricOfRealityLootEnabled, items, mod_pocketDim.blockDimWall.blockID, 8, 32, COMMON_LOOT_WEIGHT);

		addContent(properties.DimensionalDoorLootEnabled, items, mod_pocketDim.itemDimDoor.itemID, UNCOMMON_LOOT_WEIGHT);
		addContent(properties.WarpDoorLootEnabled, items, mod_pocketDim.itemExitDoor.itemID, UNCOMMON_LOOT_WEIGHT);
		addContent(properties.TransTrapdoorLootEnabled, items, mod_pocketDim.dimHatch.blockID, UNCOMMON_LOOT_WEIGHT);
		addContent(properties.RiftSignatureLootEnabled, items, mod_pocketDim.itemLinkSignature.itemID, UNCOMMON_LOOT_WEIGHT);
		addContent(properties.StableFabricLootEnabled, items, mod_pocketDim.itemStableFabric.itemID, UNCOMMON_LOOT_WEIGHT);
		addContent(properties.RiftRemoverLootEnabled, items, mod_pocketDim.itemRiftRemover.itemID, UNCOMMON_LOOT_WEIGHT);

		addContent(properties.UnstableDoorLootEnabled, items, mod_pocketDim.itemChaosDoor.itemID, RARE_LOOT_WEIGHT);
		addContent(properties.StabilizedRiftSignatureLootEnabled, items, mod_pocketDim.itemStabilizedLinkSignature.itemID, RARE_LOOT_WEIGHT);
		addContent(properties.RiftBladeLootEnabled, items, mod_pocketDim.itemRiftBlade.itemID, RARE_LOOT_WEIGHT);

		//Add all the items to our dungeon chest
		addItemsToContainer(DungeonChestInfo, items);
	}
	
	private static ArrayList<WeightedRandomChestContent> mergeCategories(String[] categories)
	{
		//Retrieve the items of each container category and merge the lists together. If two matching items
		//are found, choose the item with the minimum weight. Special checks are included for DUNGEON_CHEST
		//because the items in that category have strange weights that are incompatible with all other
		//chest categories.
		
		//This function has a flaw. It treats items with the same item ID but different damage values as
		//the same item. For instance, it cannot distinguish between different types of wood. That shouldn't
		//matter for most chest loot, though. This could be fixed if we cared enough.
		Random random = new Random();
		Hashtable<Integer, WeightedRandomChestContent> container = new Hashtable<Integer, WeightedRandomChestContent>();
		
		for (String category : categories)
		{
			WeightedRandomChestContent[] items = ChestGenHooks.getItems(category, random);
			for (WeightedRandomChestContent item : items)
			{
				int id = item.theItemId.itemID;

				//Correct the weights of Vanilla dungeon chests (DUNGEON_CHEST)
				//Comparing by String references is valid here since they should match!
				if (category == ChestGenHooks.DUNGEON_CHEST)
				{
					//It's okay to modify the weights directly. These are copies of instances,
					//not direct references. It won't affect Vanilla chests.
					item.itemWeight /= DUNGEON_CHEST_WEIGHT_INFLATION;
					if (item.itemWeight == 0)
						item.itemWeight = 1;
				}				
				if (!container.containsKey(id))
				{
					//This item has not been seen before. Simply add it to the container.
					container.put(id, item);
				}
				else
				{
					//This item conflicts with an existing entry. Replace that entry
					//if our current item has a lower weight.
					WeightedRandomChestContent other = container.get(id);
					if (item.itemWeight < other.itemWeight)
					{
						container.put(id, item);
					}
				}
			}
		}
		
		//Return merged list
		return new ArrayList<WeightedRandomChestContent>( container.values() );
	}
	
	private static void addContent(boolean include, ArrayList<WeightedRandomChestContent> items,
			int itemID, int weight)
	{
		if (include)
			items.add(new WeightedRandomChestContent(itemID, 0, 1, 1, weight));
	}
	
	private static void addContent(boolean include, ArrayList<WeightedRandomChestContent> items,
			int itemID, int minAmount, int maxAmount, int weight)
	{
		if (include)
			items.add(new WeightedRandomChestContent(itemID, 0, minAmount, maxAmount, weight));
	}
	
	private static void addItemsToContainer(ChestGenHooks container, ArrayList<WeightedRandomChestContent> items)
	{
		//System.out.println("Preparing Chest Stuff");
		
		for (WeightedRandomChestContent item : items)
		{
			container.addItem(item);
			//Uncomment this code to print out loot and weight pairs
			//System.out.println(item.theItemId.getDisplayName() + "\t" + item.itemWeight);
		}
	}
}
