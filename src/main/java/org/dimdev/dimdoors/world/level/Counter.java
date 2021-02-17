package org.dimdev.dimdoors.world.level;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;

public class Counter extends ItemComponent {

	public Counter(ItemStack stack) {
		super(stack);
		if (!this.hasTag("counter"))
			this.putInt("counter", 0);
	}

	public int increment() {
		int counter = count();
		putInt("counter", counter + 1);
		return counter;
	}

	public int count() {
		return getInt("counter");
	}

	public void reset() {
		putInt("counter", 0);
	}

	public static <T> Counter get(T provider) {
		return DimensionalDoorsComponents.COUNTER_COMPONENT_KEY.get(provider);
	}
}
