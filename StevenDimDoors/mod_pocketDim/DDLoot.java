package StevenDimDoors.mod_pocketDim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
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
	
	private static final int COMMON_LOOT_WEIGHT = 9; //1 less than weight of iron ingots
	private static final int UNCOMMON_LOOT_WEIGHT = 4; //1 less than weight of iron armor
	private static final int RARE_LOOT_WEIGHT = 1; //Same weight as music discs, golden apple
	private static final int DUNGEON_CHEST_WEIGHT_INFLATION = 10; // (weight of iron ingots in dungeon) / (weight of iron ingots in other chests)
	
	private DDLoot() { }
	
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
		
		Random random = new Random();
		HashMap<Integer, WeightedRandomChestContent> container = new HashMap<Integer, WeightedRandomChestContent>();
		
		for (String category : categories)
		{
			WeightedRandomChestContent[] items = ChestGenHooks.getItems(category, random);
			for (WeightedRandomChestContent item : items)
			{
				ItemStack stack = item.theItemId;
				int id = stack.itemID;
				int subtype = stack.getItem().getHasSubtypes() ? stack.getItemDamage() : 0;

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
				
				//Generate an identifier for this item using its item ID and damage value,
				//if it has subtypes. This solves the issue of matching items that have
				//the same item ID but different subtypes (e.g. wood planks, dyes).
				int key = ((subtype & 0xFFFF) << 16) + ((id & 0xFFFF) << 16);
				WeightedRandomChestContent other = container.get(key);
				if (other == null)
				{
					//This item has not been seen before. Simply add it to the container.
					container.put(key, item);
				}
				else
				{
					//This item conflicts with an existing entry. Replace that entry
					//if our current item has a lower weight.
					if (item.itemWeight < other.itemWeight)
					{
						container.put(key, item);
					}
				}
			}
		}
		
		//I've added a minor hack here to make enchanted books more common
		//If this is necessary for more items, create an override table and use that
		//rather than hardcoding the changes below
		final int enchantedBookID = Item.enchantedBook.itemID;
		for (WeightedRandomChestContent item : container.values())
		{
			if (item.theItemId.itemID == enchantedBookID)
			{
				item.itemWeight = 4;
				break;
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
	
	public static void generateChestContents(ChestGenHooks chestInfo, IInventory inventory, Random random)
    {
		//This is a custom version of net.minecraft.util.WeightedRandomChestContent.generateChestContents()
		//It's designed to avoid the following bugs in MC 1.5:
		//1. The randomized filling algorithm will sometimes overwrite item stacks with other stacks
		//2. If multiple enchanted books appear, then they will have the same enchantment
		
		//The prime number below is used for choosing chest slots in a seemingly-random pattern. Its value
		//was selected specifically to achieve a spread-out distribution for chests with up to 104 slots.
		//Choosing a prime number ensures that our increments are relatively-prime to the chest size, which
		//means we'll cover all the slots before repeating any. This is mathematically guaranteed.
		final int primeOffset = 239333;
		
		int count = chestInfo.getCount(random);
		int size = inventory.getSizeInventory();
		WeightedRandomChestContent[] content = chestInfo.getItems(random);
		
        for (int k = 0; k < count; k++)
        {
            WeightedRandomChestContent selection = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, content);
            
            //Call getChestGenBase() to make sure we generate a different enchantment for books.
            //Don't just use a condition to check if the item is an instance of ItemEnchantedBook because
            //we don't know if other mods might add items that also need to be regenerated.
            selection = selection.theItemId.getItem().getChestGenBase(chestInfo, random, selection);
            
            ItemStack[] stacks = ChestGenHooks.generateStacks(random, selection.theItemId, selection.theMinimumChanceToGenerateItem, selection.theMaximumChanceToGenerateItem);

            for (ItemStack item : stacks)
            {
            	int limit = size;
            	int index = random.nextInt(size);

            	while (limit > 0 && inventory.getStackInSlot(index) != null)
            	{
            		limit--;
            		index = (index + primeOffset) % size;
            	}
            	
                inventory.setInventorySlotContents(index, item);
            }
        }
    }
}
