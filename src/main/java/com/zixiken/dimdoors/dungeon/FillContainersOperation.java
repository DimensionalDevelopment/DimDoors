package com.zixiken.dimdoors.dungeon;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import com.zixiken.dimdoors.DDLoot;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.schematic.WorldOperation;

public class FillContainersOperation extends WorldOperation {
	private Random random;
	private DDProperties properties;
	
	private static final int GRAVE_CHEST_CHANCE = 1;
	private static final int MAX_GRAVE_CHEST_CHANCE = 6;
	
	public FillContainersOperation(Random random, DDProperties properties) {
		super("FillContainersOperation");
		this.random = random;
		this.properties = properties;
	}

	@Override
	protected boolean applyToBlock(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		// Fill empty chests and dispensers
		if (state.getBlock() instanceof BlockContainer) {
			TileEntity tileEntity = world.getTileEntity(pos);

			// Fill chests
			if (tileEntity instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest) tileEntity;
				if (isInventoryEmpty(chest)) {
					// Randomly choose whether this will be a regular dungeon chest or a grave chest
					if (random.nextInt(MAX_GRAVE_CHEST_CHANCE) < GRAVE_CHEST_CHANCE) {
						DDLoot.fillGraveChest(chest, random, properties);
					} else {
						DDLoot.generateChestContents(DDLoot.dungeonChestInfo, chest, random);
					}
				}
			}
		}
		return true;
	}
	
	private static boolean isInventoryEmpty(IInventory inventory) {
		int size = inventory.getSizeInventory();
		for (int index = 0; index < size; index++) {
			if (inventory.getStackInSlot(index) != null) {
				return false;
			}
		}
		return true;
	}
}
