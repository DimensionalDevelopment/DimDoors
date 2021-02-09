package org.dimdev.dimdoors.world.level;

import net.minecraft.item.ItemStack;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;

public class Counter extends ItemComponent {
	public Counter(ItemStack stack) {
		super(stack, DimensionalDoorsComponents.COUNTER_COMPONENT_KEY);

		if(!this.hasTag("counter")) {
			this.putInt("counter", -1);
		}
	}

	public int count() {
		return getInt("counter");
	}

	public void clear() {
		this.putInt("counter", -1);
	}

	public int increment() {
		int count = count() + 1;
		this.putInt("counter", count);
		return count;
	}

	public static <T> Counter get(T provider) {
		return DimensionalDoorsComponents.COUNTER_COMPONENT_KEY.get(provider);
	}
}
