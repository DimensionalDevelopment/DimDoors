package org.dimdev.dimdoors.item.component;

import net.minecraft.world.item.ItemStack;

// TODO: Fix the Fabric Implementation of idCounter
public class IdCounter {
	public static int get(ItemStack provider) {
		return provider.hasTag() ? provider.getTag().getInt("count") : 0;
	}

	public static void set(ItemStack provider, Integer value) {
		provider.getOrCreateTag().putInt("count", value);
	}


	public static int increment(ItemStack provider) {
		var value = get(provider) + 1;

		set(provider, value);

		return value;
	}

	public static int count(ItemStack stack) {
		return get(stack);
	}
}
