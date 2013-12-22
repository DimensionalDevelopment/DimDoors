package StevenDimDoors.mod_pocketDim.dungeon;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDLoot;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.schematic.WorldOperation;

public class FillContainersOperation extends WorldOperation
{
	private Random random;
	private DDProperties properties;
	
	private static final int GRAVE_CHEST_CHANCE = 100;
	private static final int MAX_GRAVE_CHEST_CHANCE = 700;
	
	public FillContainersOperation(Random random, DDProperties properties)
	{
		super("FillContainersOperation");
		this.random = random;
		this.properties = properties;
	}

	@Override
	protected boolean applyToBlock(World world, int x, int y, int z)
	{
		int blockID = world.getBlockId(x, y, z);

		// Fill empty chests and dispensers
		if (Block.blocksList[blockID] instanceof BlockContainer)
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

			// Fill chests
			if (tileEntity instanceof TileEntityChest)
			{
				TileEntityChest chest = (TileEntityChest) tileEntity;
				if (isInventoryEmpty(chest))
				{
					// Randomly choose whether this will be a regular dungeon chest or a grave chest
					if (random.nextInt(MAX_GRAVE_CHEST_CHANCE) < GRAVE_CHEST_CHANCE)
					{
						DDLoot.fillGraveChest(chest, random, properties);
					}
					else
					{
						DDLoot.generateChestContents(DDLoot.DungeonChestInfo, chest, random);						
					}
				}
			}
			
			// Fill dispensers
			if (tileEntity instanceof TileEntityDispenser)
			{
				TileEntityDispenser dispenser = (TileEntityDispenser) tileEntity;
				if (isInventoryEmpty(dispenser))
				{
					dispenser.addItem(new ItemStack(Item.arrow, 64));
				}
			}
		}
		return true;
	}
	
	private static boolean isInventoryEmpty(IInventory inventory)
	{
		int size = inventory.getSizeInventory();
		for (int index = 0; index < size; index++)
		{
			if (inventory.getStackInSlot(index) != null)
			{
				return false;
			}
		}
		return true;
	}
}
