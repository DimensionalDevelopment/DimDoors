package org.dimdev.dimdoors.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class CounterItem extends Item {
	public CounterItem(Properties p) {
		super(p);
	}

	protected int getCount(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		return tag.contains("item_counter") ? tag.getInt("item_counter") : 0;
	}

	protected int incrementCount(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		int count = (tag.contains("item_counter") ? tag.getInt("item_counter") : -1)+1;
		tag.putInt("item_counter",count);
		return count;
	}

	protected int decrementCount(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		int count = (tag.contains("item_counter") ? tag.getInt("item_counter") : 1)-1;
		tag.putInt("item_counter",count);
		return count;
	}

	protected void resetCount(ItemStack stack) {
		stack.getOrCreateTag().putInt("item_counter",0);
	}
}
