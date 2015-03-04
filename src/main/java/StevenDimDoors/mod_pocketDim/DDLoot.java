package StevenDimDoors.mod_pocketDim;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.util.WeightedContainer;

/*
 * Registers a category of loot chests for Dimensional Doors in Forge.
 */
public class DDLoot {
	
	private static final String[] SPECIAL_SKULL_OWNERS = new String[] { "stevenrs11", "kamikazekiwi3", "fbt", "Jaitsu", "XCompWiz", "skyboy026", "Wylker" };
	
	private static final double MIN_ITEM_DAMAGE = 0.3;
	private static final double MAX_ITEM_DAMAGE = 0.9;
	private static final int ITEM_ENCHANTMENT_CHANCE = 50;
	private static final int MAX_ITEM_ENCHANTMENT_CHANCE = 100;
	private static final int SPECIAL_SKULL_CHANCE = 20;
	private static final int MAX_SPECIAL_SKULL_CHANCE = 100;
	
	public static final String DIMENSIONAL_DUNGEON_CHEST = "dimensionalDungeonChest";
	public static ChestGenHooks DungeonChestInfo = null;
	private static final int CHEST_SIZE = 6;
	
	private DDLoot() { }
	
	public static void registerInfo(DDProperties properties)
	{
		// Register the dimensional dungeon chest with ChestGenHooks. This isn't necessary, but allows
		// other mods to add their own loot to our chests if they know our loot category, without having
		// to interface with our code.
		DungeonChestInfo = ChestGenHooks.getInfo(DIMENSIONAL_DUNGEON_CHEST);
		DungeonChestInfo.setMin(CHEST_SIZE);
		DungeonChestInfo.setMax(CHEST_SIZE);
		
		ArrayList<WeightedRandomChestContent> items = new ArrayList<WeightedRandomChestContent>();
		
		addContent(true, items, Items.iron_ingot, 160, 1, 3);
		addContent(true, items, Items.coal, 120, 1, 3);
		addContent(true, items, Items.quartz, 120, 1, 3);
		addContent(true, items, Items.book, 100);
		addContent(true, items, Items.gold_ingot, 80, 1, 3);
		addContent(true, items, Items.diamond, 40, 1, 2);
		addContent(true, items, Items.emerald, 20, 1, 2);
		addContent(true, items, Items.golden_apple, 10);

		addContent(properties.FabricOfRealityLootEnabled, items, Item.getItemFromBlock(mod_pocketDim.blockDimWall), 20, 16, 64);
		addContent(properties.WorldThreadLootEnabled, items, mod_pocketDim.itemWorldThread, 80, 2, 12);

		// Add all the items to our dungeon chest
		addItemsToContainer(DungeonChestInfo, items);
	}
		
	private static void addContent(boolean include, ArrayList<WeightedRandomChestContent> items,
			Item item, int weight)
	{
		if (include)
			items.add(new WeightedRandomChestContent(item, 0, 1, 1, weight));
	}
	
	private static void addContent(boolean include, ArrayList<WeightedRandomChestContent> items,
			Item item, int weight, int minAmount, int maxAmount)
	{
		if (include)
			items.add(new WeightedRandomChestContent(item, 0, minAmount, maxAmount, weight));
	}
	
	private static void addItemsToContainer(ChestGenHooks container, ArrayList<WeightedRandomChestContent> items)
	{
		for (WeightedRandomChestContent item : items)
		{
			container.addItem(item);
		}
	}
	
	private static void fillChest(ArrayList<ItemStack> stacks, IInventory inventory, Random random)
	{
		// This custom chest-filling function avoids overwriting item stacks
		
		// The prime number below is used for choosing chest slots in a seemingly-random pattern. Its value
		// was selected specifically to achieve a spread-out distribution for chests with up to 104 slots.
		// Choosing a prime number ensures that our increments are relatively-prime to the chest size, which
		// means we'll cover all the slots before repeating any. This is mathematically guaranteed.
		final int primeOffset = 239333;

		int size = inventory.getSizeInventory();
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
	
	public static void generateChestContents(ChestGenHooks chestInfo, IInventory inventory, Random random)
    {
		// This is a custom version of net.minecraft.util.WeightedRandomChestContent.generateChestContents()
		// It's designed to avoid the following bugs in MC 1.5:
		// 1. If multiple enchanted books appear, then they will have the same enchantment
		// 2. The randomized filling algorithm will sometimes overwrite item stacks with other stacks
		
		int count = chestInfo.getCount(random);
		WeightedRandomChestContent[] content = chestInfo.getItems(random);
		ArrayList<ItemStack> allStacks = new ArrayList<ItemStack>();
		
        for (int k = 0; k < count; k++)
        {
            WeightedRandomChestContent selection = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, content);
            
            // Call getChestGenBase() to make sure we generate a different enchantment for books.
            // Don't just use a condition to check if the item is an instance of ItemEnchantedBook because
            // we don't know if other mods might add items that also need to be regenerated.
            selection = selection.theItemId.getItem().getChestGenBase(chestInfo, random, selection);
            
            ItemStack[] stacks = ChestGenHooks.generateStacks(random, selection.theItemId, selection.theMinimumChanceToGenerateItem, selection.theMaximumChanceToGenerateItem);
            for (int h = 0; h < stacks.length; h++)
            {
            	allStacks.add(stacks[h]);
            }
        }
        
        fillChest(allStacks, inventory, random);
    }
	
	public static void fillGraveChest(IInventory inventory, Random random, DDProperties properties)
	{
		// This function fills "grave chests", which are chests for dungeons that
		// look like a player died in the area and his remains were gathered in
		// a chest. Doing this properly requires fine control of loot generation,
		// so we use our own function rather than Minecraft's functions.
		int k;
		int count;
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		ArrayList<WeightedContainer<Item>> selection = new ArrayList<WeightedContainer<Item>>();
		
		// Insert bones and rotten flesh
		// Make stacks of single items to spread them out
		count = MathHelper.getRandomIntegerInRange(random, 2, 5);
		for (k = 0; k < count; k++)
		{
			stacks.add( new ItemStack(Items.bone, 1) );
		}
		count = MathHelper.getRandomIntegerInRange(random, 2, 4);
		for (k = 0; k < count; k++)
		{
			stacks.add( new ItemStack(Items.rotten_flesh, 1) );
		}
		
		// Insert tools
		// 30% chance of adding a pickaxe
		if (random.nextInt(100) < 30)
		{
			addModifiedTool(Items.iron_pickaxe, stacks, random);
		}
		// 30% chance of adding a bow and some arrows
		if (random.nextInt(100) < 30)
		{
			addModifiedBow(stacks, random);
			stacks.add( new ItemStack(Items.arrow, MathHelper.getRandomIntegerInRange(random, 8, 32)) );
		}
		// 10% chance of adding a Rift Blade (no enchants)
		if (properties.RiftBladeLootEnabled && random.nextInt(100) < 10)
		{
			stacks.add( new ItemStack(mod_pocketDim.itemRiftBlade, 1) );
		}
		else
		{
			// 20% of adding an iron sword, 10% of adding a stone sword
			addModifiedSword( getRandomItem(Items.iron_sword, Items.stone_sword, null, 20, 10, random) , stacks, random);
		}
		
		// Insert equipment
		// For each piece, 25% of an iron piece, 10% of a chainmail piece
		addModifiedEquipment( getRandomItem(Items.iron_helmet, Items.chainmail_helmet, null, 25, 10, random) , stacks, random);
		addModifiedEquipment( getRandomItem(Items.iron_chestplate, Items.chainmail_chestplate, null, 25, 10, random) , stacks, random);
		addModifiedEquipment( getRandomItem(Items.iron_leggings, Items.chainmail_leggings, null, 25, 10, random) , stacks, random);
		addModifiedEquipment( getRandomItem(Items.iron_boots, Items.iron_boots, null, 25, 10, random) , stacks, random);
		
		// Insert other random stuff
		// 40% chance for a name tag, 35% chance for a glass bottle
		// 30% chance for an ender pearl, 5% chance for record 11
		// 30% chance for a ghast tear
		addItemWithChance(stacks, random, 40, Items.name_tag, 1);
		addItemWithChance(stacks, random, 35, Items.glass_bottle, 1);
		addItemWithChance(stacks, random, 30, Items.ender_pearl, 1);
		addItemWithChance(stacks, random, 30, Items.ghast_tear, 1);
		addItemWithChance(stacks, random, 5, Items.record_11, 1);
		
		// Finally, there is a 5% chance of adding a player head
		if (random.nextInt(100) < 5)
		{
			addGraveSkull(stacks, random);
		}
		
		fillChest(stacks, inventory, random);
	}

	private static void addModifiedEquipment(Item item, ArrayList<ItemStack> stacks, Random random)
	{
		if (item == null)
			return;
		
		stacks.add( getModifiedItem(item, random, new Enchantment[] { Enchantment.blastProtection, Enchantment.fireProtection, Enchantment.protection, Enchantment.projectileProtection }) );
	}

	private static void addModifiedSword(Item item, ArrayList<ItemStack> stacks, Random random)
	{
		if (item == null)
			return;
		
		stacks.add( getModifiedItem(item, random, new Enchantment[] { Enchantment.fireAspect, Enchantment.knockback, Enchantment.sharpness }) );
	}

	private static void addModifiedTool(Item tool, ArrayList<ItemStack> stacks, Random random)
	{
		if (tool == null)
			return;
		
		stacks.add( getModifiedItem(tool, random, new Enchantment[] { Enchantment.efficiency, Enchantment.unbreaking }) );
	}
	
	private static void addModifiedBow(ArrayList<ItemStack> stacks, Random random)
	{
		stacks.add( getModifiedItem(Items.bow, random, new Enchantment[] { Enchantment.flame, Enchantment.power, Enchantment.punch }) );
	}
	
	private static ItemStack getModifiedItem(Item item, Random random, Enchantment[] enchantments)
	{
		ItemStack result = applyRandomDamage(item, random);
		if (enchantments.length > 0 && random.nextInt(MAX_ITEM_ENCHANTMENT_CHANCE) < ITEM_ENCHANTMENT_CHANCE)
		{
			result.addEnchantment(enchantments[ random.nextInt(enchantments.length) ], 1);
		}
		return result;
	}
	
	private static Item getRandomItem(Item a, Item b, Item c, int weightA, int weightB, Random random)
	{
		int roll = random.nextInt(100);
		if (roll < weightA)
			return a;
		if (roll < weightA + weightB)
			return b;
		return c;
	}

	private static void addItemWithChance(ArrayList<ItemStack> stacks, Random random, int chance, Item item, int count)
	{
		if (random.nextInt(100) < chance)
		{
			stacks.add(new ItemStack(item, count));
		}
	}
	
	private static ItemStack applyRandomDamage(Item item, Random random)
	{
		int damage = (int) (item.getMaxDamage() * MathHelper.getRandomDoubleInRange(random, MIN_ITEM_DAMAGE, MAX_ITEM_DAMAGE));
		return new ItemStack(item, 1, damage);
	}
	
	private static void addGraveSkull(ArrayList<ItemStack> stacks, Random random)
	{
		final int PLAYER_SKULL_METADATA = 3;
		DeathTracker deathTracker = mod_pocketDim.deathTracker;
		String skullOwner;
		if (deathTracker.isEmpty() || (random.nextInt(MAX_SPECIAL_SKULL_CHANCE) < SPECIAL_SKULL_CHANCE))
		{
			skullOwner = SPECIAL_SKULL_OWNERS[ random.nextInt(SPECIAL_SKULL_OWNERS.length) ];
		}
		else
		{
			skullOwner = deathTracker.getRandomUsername(random);
		}
		ItemStack skull = new ItemStack(Items.skull, 1, PLAYER_SKULL_METADATA);
		skull.stackTagCompound = new NBTTagCompound();
		skull.stackTagCompound.setString("SkullOwner", skullOwner);
		stacks.add(skull);
	}
}
